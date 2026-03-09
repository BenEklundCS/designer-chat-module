// Designer Chat Module - Ben Eklund 2026
package org.designerchat.gateway;

import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

public class ChatGatewayHook extends AbstractGatewayModuleHook {

    private static final LoggerEx logger = LoggerEx.newBuilder().build("designerchat.gateway");

    private GatewayContext gatewayContext;

    @Override
    public void setup(GatewayContext context) {
        this.gatewayContext = context;
        logger.info("Setting up Designer Chat Module.");
    }

    @Override
    public void startup(LicenseState activationState) {
        logger.info("Starting up Designer Chat Module.");
    }

    @Override
    public void shutdown() {
        logger.info("Shutting down Designer Chat Module.");
    }

    @Override
    public boolean isFreeModule() {
        return true;
    }
}
