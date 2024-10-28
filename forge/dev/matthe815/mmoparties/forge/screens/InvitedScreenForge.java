package dev.matthe815.mmoparties.forge.screens;

import dev.matthe815.mmoparties.common.gui.screens.InvitedScreenCommon;
import dev.matthe815.mmoparties.forge.core.MMOParties;
import dev.matthe815.mmoparties.forge.networking.EnumPartyGUIAction;
import dev.matthe815.mmoparties.forge.networking.MessageHandleMenuAction;

public class InvitedScreenForge extends InvitedScreenCommon {
    @Override
    public void initGui() {
        this.addButton(this.CreateButton("rpgparties.gui.accept", 2, () -> {
            MMOParties.network.sendToServer(new MessageHandleMenuAction("", EnumPartyGUIAction.ACCEPT));
            MMOParties.partyInviter = null;
        }));

        this.addButton(this.CreateButton("rpgparties.gui.deny", 3, () -> {
            MMOParties.network.sendToServer(new MessageHandleMenuAction("", EnumPartyGUIAction.DENY));
            MMOParties.partyInviter = null;
        }));
    }
}
