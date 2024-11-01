package dev.matthe815.mmoparties.forge.events;

import dev.matthe815.mmoparties.forge.core.MMOParties;
import dev.matthe815.mmoparties.common.gui.screens.InvitedScreenCommon;
import dev.matthe815.mmoparties.common.gui.screens.PartyScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EventClientForge {
    /**
     * Handles removing any temporary data when leaving a world/server
     */
    @SubscribeEvent
    public void OnServerDisconnect(ClientPlayerNetworkEvent.LoggingOut event)
    {
        MMOParties.localParty = null;
    }

    /**
     * Handles any mod specific key-inputs.
     */
    @SubscribeEvent
    public void OnKeyInput(InputEvent.Key event) {
        // Open the party menu when the GUI key is pressed.
        if (MMOParties.OPEN_GUI_KEY.isDown()) {
            // You can't be mid invitee if you've already accepted.
            if (MMOParties.localParty != null & MMOParties.partyInviter != null) MMOParties.partyInviter = null;

            // Open the party invitation menu if you have an invite.
            if (MMOParties.partyInviter != null)
                dev.matthe815.mmoparties.common.events.EventClient.OpenInvitationScreen();
            else
                Minecraft.getInstance().setScreen(new PartyScreen());
        }
    }
}
