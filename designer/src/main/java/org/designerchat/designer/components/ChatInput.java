// Designer Chat Module - Ben Eklund 2026
package org.designerchat.designer.components;

import java.awt.*;
import java.util.function.Consumer;
import javax.swing.*;

public class ChatInput extends JPanel {
    private final JButton sendChatButton;
    private final JTextArea chatInput;

    public ChatInput(Consumer<String> sendChat) {
        setLayout(new BorderLayout());

        this.chatInput = new JTextArea(3, 20);
        JScrollPane scrollPane = new JScrollPane(this.chatInput);
        this.sendChatButton = new JButton("Send");

        this.sendChatButton.addActionListener((e) -> {
            String message = this.chatInput.getText();
            this.chatInput.setText("");
            sendChat.accept(message);
        });

        add(scrollPane, BorderLayout.CENTER);
        add(this.sendChatButton, BorderLayout.EAST);

    }

    public void setLoading(boolean loading) {
        this.sendChatButton.setEnabled(!loading);
        this.sendChatButton.setText(loading ? "Sending..." : "Send");
    }
}
