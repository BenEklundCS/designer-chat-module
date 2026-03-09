// Designer Chat Module - Ben Eklund 2026
package org.designerchat.designer.components;

import com.inductiveautomation.ignition.common.util.LoggerEx;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

// top bar with health indicator, model selector, and clear button
public class Topbar extends JPanel {
    private final JComboBox<String> modelSelector;
    private final JLabel connectedIndicator;
    private static final LoggerEx logger = LoggerEx.newBuilder().build("designerchat.topbar");

    public Topbar(Runnable onClear) {
        setLayout(new BorderLayout());

        this.connectedIndicator = new JLabel("•");
        this.connectedIndicator.setFont(connectedIndicator.getFont().deriveFont(24f));
        this.modelSelector = new JComboBox<>();

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> onClear.run());

        add(connectedIndicator, BorderLayout.WEST);
        add(modelSelector, BorderLayout.CENTER);
        add(clearButton, BorderLayout.EAST);
    }

    public void setHealth(boolean health) {
        connectedIndicator.setForeground(health ? Color.GREEN : Color.RED);
    }

    public void setModels(ArrayList<String> models) {
        for (String model : models) {
            this.modelSelector.addItem(model);
        }
    }

    public String getSelectedModel() {
        return (String) modelSelector.getSelectedItem();
    }
}
