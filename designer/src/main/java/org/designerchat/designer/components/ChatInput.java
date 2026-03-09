// Designer Chat Module - Ben Eklund 2026
package org.designerchat.designer.components;

import java.awt.*;
import java.util.function.Consumer;
import javax.swing.*;

// chat input with text area and send button
public class ChatInput extends JPanel {
    private final JButton sendChatButton;

    public ChatInput(Consumer<String> sendChat) {
        setLayout(new BorderLayout());

        JTextArea chatInput = new JTextArea(3, 20);
        JButton sendChatButton = new JButton("Send");

        sendChatButton.addActionListener((e) -> {
            String message = chatInput.getText();
            chatInput.setText("");
            sendChat.accept(message);
        });

        add(new JScrollPane(chatInput), BorderLayout.CENTER);
        add(sendChatButton, BorderLayout.EAST);

        this.sendChatButton = sendChatButton;
    }

    public void setLoading(boolean loading) {
        this.sendChatButton.setEnabled(!loading);
        this.sendChatButton.setText(loading ? "Sending..." : "Send");
    }
}
