// Designer Chat Module - Ben Eklund 2026
package org.designerchat.designer.components;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.*;
import org.designerchat.common.ChatHistoryRecord;

public class ConversationArea extends JPanel {
    private final JTextArea conversationText;

    public ConversationArea() {
        setLayout(new BorderLayout());
        this.conversationText = new JTextArea();
        this.conversationText.setEditable(false);
        this.conversationText.setLineWrap(true);
        this.conversationText.setWrapStyleWord(true);
        // suppress beep on keystrokes while keeping focus for copy-paste
        this.conversationText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                e.consume();
            }
        });
        JScrollPane scrollPane = new JScrollPane(this.conversationText);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateText(List<ChatHistoryRecord> chatHistory) {
        StringBuilder conversation = new StringBuilder();
        for (ChatHistoryRecord record : chatHistory) {
            conversation.append(record);
        }
        this.conversationText.setText(conversation.toString());
    }
}
