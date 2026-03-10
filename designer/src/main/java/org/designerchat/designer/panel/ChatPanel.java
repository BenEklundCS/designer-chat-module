// Designer Chat Module - Ben Eklund 2026
package org.designerchat.designer.panel;

import com.inductiveautomation.ignition.common.util.LoggerEx;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import org.designerchat.common.ChatHistoryRecord;
import org.designerchat.common.ChatRole;
import org.designerchat.common.IChatAPI;
import org.designerchat.designer.components.ChatInput;
import org.designerchat.designer.components.ConversationArea;
import org.designerchat.designer.components.Topbar;

public class ChatPanel extends JPanel {
    private static final LoggerEx logger = LoggerEx.newBuilder().build("designerchat.chatpanel");
    private final IChatAPI chatAPI;

    private Topbar topbar;
    private ConversationArea conversationArea;
    private ChatInput chatInput;

    private final List<ChatHistoryRecord> chatHistory;

    private final ScheduledExecutorService checkHealthScheduler;

    public ChatPanel(IChatAPI chatAPI) {
        this.chatAPI = chatAPI;
        this.chatHistory = new ArrayList<>();

        this.checkHealthScheduler = Executors.newScheduledThreadPool(1);
        this.checkHealthScheduler.scheduleAtFixedRate(this::checkHealth, 0, 5, TimeUnit.SECONDS);

        setLayout(new BorderLayout());
        initTopbar();
        initChatInput();
        initConversationArea();
    }

    private void initTopbar() {
        this.topbar = new Topbar(this::clearChatHistory);
        add(this.topbar, BorderLayout.NORTH);
        loadModels();
    }

    private void initChatInput() {
        this.chatInput = new ChatInput(this::sendChat);
        add(this.chatInput, BorderLayout.SOUTH);
    }

    private void initConversationArea() {
        this.conversationArea = new ConversationArea();
        add(this.conversationArea, BorderLayout.CENTER);
    }

    private void sendChat(String message) {
        this.chatInput.setLoading(true);
        String selectedModel = this.topbar.getSelectedModel();

        addToChatHistory(new ChatHistoryRecord(ChatRole.USER, message));
        this.conversationArea.updateText(this.chatHistory);

        this.chatAPI
                .chatCompletion(selectedModel, this.chatHistory) // async add model message
                .thenAccept(newModelRecord -> SwingUtilities.invokeLater(() -> {
                    addToChatHistory(newModelRecord);
                    this.chatInput.setLoading(false);
                }))
                .exceptionally((error) -> {
                    logger.error("Chat completion failed: " + error);
                    SwingUtilities.invokeLater(() -> this.chatInput.setLoading(false));
                    return null;
                });
    }

    private void checkHealth() {
        this.chatAPI
                .isHealthy()
                .thenAccept(isHealthy -> SwingUtilities.invokeLater(() -> this.topbar.setHealth(isHealthy)))
                .exceptionally((error) -> {
                    logger.error("Health check failed: " + error);
                    SwingUtilities.invokeLater(() -> this.topbar.setHealth(false));
                    return null;
                });
    }

    private void loadModels() {
        this.chatAPI
                .listModels()
                .thenAccept(models -> SwingUtilities.invokeLater(() -> this.topbar.setModels(models)))
                .exceptionally(e -> {
                    logger.error("Failed to load models", e);
                    return null;
                });
    }

    private void addToChatHistory(ChatHistoryRecord historyRecord) {
        this.chatHistory.add(historyRecord);
        this.conversationArea.updateText(this.chatHistory);
    }

    private void clearChatHistory() {
        this.chatHistory.clear();
        this.conversationArea.updateText(this.chatHistory);
    }

    public void shutdown() {
        this.checkHealthScheduler.shutdown();
    }
}
