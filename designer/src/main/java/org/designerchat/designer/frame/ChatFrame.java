// Designer Chat Module - Ben Eklund 2026
package org.designerchat.designer.frame;
import com.jidesoft.docking.DockContext;
import com.jidesoft.docking.DockableFrame;
import org.designerchat.common.IChatAPI;
import org.designerchat.designer.panel.ChatPanel;

import java.awt.*;

// dockable frame that hosts the chat panel in the designer
public class ChatFrame extends DockableFrame {
    private ChatPanel chatPanel;

    public ChatFrame(IChatAPI chatAPI) {
        setTitle("Chat");
        setKey("ChatFrame");
        setInitSide(DockContext.DOCK_SIDE_EAST);
        setInitMode(DockContext.STATE_FRAMEDOCKED);
        setPreferredSize(new Dimension(350, 600));
        setVisible(true);
        this.chatPanel = new ChatPanel(chatAPI);
        getContentPane().add(this.chatPanel, BorderLayout.CENTER);
    }

    public void shutdown() {
        this.chatPanel.shutdown();
    }
}
