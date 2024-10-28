package dev.matthe815.mmoparties.forge.events;

import dev.matthe815.mmoparties.forge.core.MMOParties;
import dev.matthe815.mmoparties.common.gui.screens.PartyScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventClientForge {
    /**
     * Handles removing any temporary data when leaving a world/server
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void OnServerDisconnect(PlayerEvent.PlayerLoggedOutEvent event)
    {
        MMOParties.localParty = null;
    }

    /**
     * Handles any mod specific key-inputs.
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void OnKeyInput(InputEvent.KeyInputEvent event) {
        // Open the party menu when the GUI key is pressed.
        if (MMOParties.OPEN_GUI_KEY.isPressed()) {
            // You can't be mid invitee if you've already accepted.
            if (MMOParties.localParty != null & MMOParties.partyInviter != null) MMOParties.partyInviter = null;

            // Open the party invitation menu if you have an invite.
            if (MMOParties.partyInviter != null)
                dev.matthe815.mmoparties.common.events.EventClient.OpenInvitationScreen();
            else
                Minecraft.getMinecraft().displayGuiScreen(new PartyScreen());
        }
    }
}
