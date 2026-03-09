// Designer Chat Module - Ben Eklund 2026
package org.designerchat.designer.components;

import org.designerchat.common.ChatHistoryRecord;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

// read-only text area that displays conversation history
public class ConversationArea extends JPanel {
    private final JTextArea conversationText;

    public ConversationArea() {
        setLayout(new BorderLayout());
        this.conversationText = new JTextArea();
        this.conversationText.setEditable(false);
        this.conversationText.setLineWrap(true);
        // suppress beep on keystrokes while keeping focus for copy-paste
        this.conversationText.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                e.consume();
            }
        });
        add(new JScrollPane(this.conversationText), BorderLayout.CENTER);
    }

    public void updateText(ArrayList<ChatHistoryRecord> chatHistory) {
        StringBuilder conversation = new StringBuilder();
        for (ChatHistoryRecord record : chatHistory) {
            conversation.append(record.role()).append(": ").append(record.content()).append("\n\n");
        }
        this.conversationText.setText(conversation.toString());
    }
}
