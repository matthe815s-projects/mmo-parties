package deathtags.core.events;

import deathtags.core.MMOParties;
import deathtags.gui.screens.InvitedScreen;
import deathtags.gui.screens.PartyScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EventClient {
    /**
     * Handles removing any temporary data when leaving a world/server
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void OnServerDisconnect(ClientPlayerNetworkEvent.LoggedOutEvent event)
    {
        MMOParties.localParty = null;
    }

    /**
     * Handles any mod specific key-inputs.
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void OnKeyInput(InputEvent.KeyInputEvent event) {
        // Open the party menu when the GUI key is pressed.
        if (MMOParties.OPEN_GUI_KEY.isDown()) {
            // Open the party invitation menu if you have an invite.
            if (MMOParties.partyInviter != null)
                EventClient.OpenInvitationScreen();
            else
                Minecraft.getInstance().setScreen(new PartyScreen());
        }
    }

    /**
     * Handles opening of the party screens
     */
    @OnlyIn(Dist.CLIENT)
    public static void OpenPartyScreen() {
        Minecraft.getInstance().setScreen(new PartyScreen());
    }

    /**
     * Handles opening of the invitation screen.
     * Opens automatically when an invite is received.
     * @link deathtags.networking.MessagePartyInvite
     */
    @OnlyIn(Dist.CLIENT)
    public static void OpenInvitationScreen() { Minecraft.getInstance().setScreen(new InvitedScreen()); }
}
