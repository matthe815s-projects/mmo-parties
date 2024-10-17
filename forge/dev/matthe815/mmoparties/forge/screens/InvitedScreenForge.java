package dev.matthe815.mmoparties.forge.screens;

import dev.matthe815.mmoparties.common.gui.screens.InvitedScreenCommon;
import dev.matthe815.mmoparties.forge.core.MMOParties;
import dev.matthe815.mmoparties.forge.networking.EnumPartyGUIAction;
import dev.matthe815.mmoparties.forge.networking.MessageHandleMenuAction;
import net.minecraftforge.network.PacketDistributor;

public class InvitedScreenForge extends InvitedScreenCommon {
    public InvitedScreenForge() {
        super();
    }

    @Override
    protected void init() {
        this.addRenderableWidget(this.CreateButton("rpgparties.gui.accept", 2, p_onPress_1_ -> {
            MMOParties.network.send(PacketDistributor.SERVER.with(null), new MessageHandleMenuAction("", EnumPartyGUIAction.ACCEPT));
            MMOParties.partyInviter = null;
        }));

        this.addRenderableWidget(this.CreateButton("rpgparties.gui.deny", 3, p_onPress_1_ -> {
            MMOParties.network.send(PacketDistributor.SERVER.with(null), new MessageHandleMenuAction("", EnumPartyGUIAction.DENY));
            MMOParties.partyInviter = null;
        }));
    }
}
