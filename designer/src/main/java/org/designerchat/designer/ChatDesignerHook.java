// Designer Chat Module - Ben Eklund 2026
package org.designerchat.designer;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.designer.model.AbstractDesignerModuleHook;
import com.inductiveautomation.ignition.designer.model.DesignerContext;
import org.designerchat.designer.client.OpenRouterChatAPI;
import org.designerchat.designer.frame.ChatFrame;

public class ChatDesignerHook extends AbstractDesignerModuleHook {
    private static final LoggerEx logger = LoggerEx.newBuilder().build("designerchat.designer");

    private DesignerContext context;
    private ChatFrame chatFrame;

    @Override
    public void startup(DesignerContext context, LicenseState activationState) throws Exception {
        super.startup(context, activationState);
        this.context = context;

        logger.info("Designer Chat Module started.");

        // swap OpenRouterChatAPI for OllamaChatAPI to use local models
        this.chatFrame = new ChatFrame(new OpenRouterChatAPI());
        this.context.getDockingManager().addFrame(this.chatFrame);
        this.context.getDockingManager().showFrame("ChatFrame");
    }

    @Override
    public void shutdown() {
        this.chatFrame.shutdown();
        logger.info("Designer Chat Module shut down.");
    }
}
