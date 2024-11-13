package dev.matthe815.mmoparties.fabric.screens;

import dev.matthe815.mmoparties.common.gui.screens.InvitedScreenCommon;
import dev.matthe815.mmoparties.fabric.core.MMOParties;
import dev.matthe815.mmoparties.fabric.networking.EnumPartyGUIAction;
import dev.matthe815.mmoparties.fabric.networking.MessageHandleMenuAction;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class InvitedScreenForge extends InvitedScreenCommon {
    @Override
    protected void init() {
        this.addRenderableWidget(this.CreateButton("rpgparties.gui.accept", 2, p_onPress_1_ -> {
            ClientPlayNetworking.send(MessageHandleMenuAction.ID, MessageHandleMenuAction.encode("", EnumPartyGUIAction.ACCEPT));
            MMOParties.partyInviter = null;
        }));

        this.addRenderableWidget(this.CreateButton("rpgparties.gui.deny", 3, p_onPress_1_ -> {
            ClientPlayNetworking.send(MessageHandleMenuAction.ID, MessageHandleMenuAction.encode("", EnumPartyGUIAction.DENY));
            MMOParties.partyInviter = null;
        }));
    }
}
