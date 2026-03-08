// Designer Chat Module - Ben Eklund 2026
package org.designerchat.designer.panel;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import org.designerchat.common.ChatHistoryRecord;
import org.designerchat.designer.components.ChatInput;
import org.designerchat.common.IChatAPI;
import org.designerchat.designer.components.ConversationArea;
import org.designerchat.designer.components.Topbar;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ChatPanel extends JPanel {
    private static final LoggerEx logger = LoggerEx.newBuilder().build("designerchat.chatpanel");
    private final IChatAPI chatAPI;

    private Topbar topbar;
    private ConversationArea conversationArea;
    private ChatInput chatInput;

    private final ArrayList<ChatHistoryRecord> chatHistory;

    public ChatPanel(IChatAPI chatAPI) {
        this.chatAPI = chatAPI;
        this.chatHistory = new ArrayList<>();
        setLayout(new BorderLayout());
        initTopbar();
        initChatInput();
        initConversationArea();
    }

    // initializers
    private void initTopbar() {
        Topbar topbar = new Topbar(this::checkHealth, this::clearConversation);
        this.topbar = topbar;
        add(topbar, BorderLayout.NORTH);
        loadModels();
    }

    private void initChatInput() {
        ChatInput chatInput = new ChatInput(this::sendChat);
        add(chatInput, BorderLayout.SOUTH);
        this.chatInput = chatInput;
    }

    private void initConversationArea() {
        ConversationArea conversationArea = new ConversationArea();
        add(new JScrollPane(conversationArea), BorderLayout.CENTER);
        this.conversationArea = conversationArea;
    }

    // main functions

    private void sendChat(String message) {
        this.chatInput.setLoading(true);
        String selectedModel = this.topbar.getSelectedModel();
        ChatHistoryRecord newUserRecord = new ChatHistoryRecord("user", message);

        this.chatHistory.add(newUserRecord);
        this.conversationArea.updateText(this.chatHistory);

        this.chatAPI.chatCompletion(selectedModel, this.chatHistory) // async add model message
                .thenAccept(this::addToChatHistory)
                .exceptionally((error) -> {
                    logger.error("Chat completion failed: " + error);
                    return null;
                });
    }

    private void checkHealth() {
        this.chatAPI.isHealthy().thenAccept((isHealthy) -> this.topbar.setHealth(isHealthy));
    }

    private void addToChatHistory(ChatHistoryRecord historyRecord) {
        SwingUtilities.invokeLater(
                () -> {
                    this.chatHistory.add(historyRecord);
                    this.conversationArea.updateText(this.chatHistory);
                    this.chatInput.setLoading(false);
                }
        );
    }

    private void loadModels() {
        chatAPI.listModels()
                .thenAccept(models -> SwingUtilities.invokeLater(() -> this.topbar.setModels(models)))
                .exceptionally(e -> {
                    logger.error("Failed to load models", e);
                    return null;
                });
    }

    private void clearConversation() {
        this.chatHistory.clear();
        this.conversationArea.updateText(chatHistory);
    }

    // class utils
    public void shutdown() {
        topbar.shutdown();
    }
}
