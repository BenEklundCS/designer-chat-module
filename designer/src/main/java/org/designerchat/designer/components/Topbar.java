// Designer Chat Module - Ben Eklund 2026
package org.designerchat.designer.components;

import com.inductiveautomation.ignition.common.util.LoggerEx;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class Topbar extends JPanel {
    private final JComboBox<String> modelSelector;
    private final JLabel connectedIndicator;
    private static final LoggerEx logger = LoggerEx.newBuilder().build("designerchat.topbar");

    public Topbar(Runnable onClear) {
        setLayout(new BorderLayout());

        this.connectedIndicator = new JLabel("•");
        this.connectedIndicator.setFont(this.connectedIndicator.getFont().deriveFont(24f));
        this.connectedIndicator.setPreferredSize(new Dimension(16, 16));
        this.connectedIndicator.setHorizontalAlignment(SwingConstants.CENTER);

        this.modelSelector = new JComboBox<>();

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> onClear.run());

        add(this.connectedIndicator, BorderLayout.WEST);
        add(this.modelSelector, BorderLayout.CENTER);
        add(clearButton, BorderLayout.EAST);
    }

    public void setHealth(boolean health) {
        this.connectedIndicator.setForeground(health ? Color.GREEN : Color.RED);
    }

    public void setModels(List<String> models) {
        for (String model : models) {
            this.modelSelector.addItem(model);
        }
    }

    public String getSelectedModel() {
        return (String) this.modelSelector.getSelectedItem();
    }
}
