package deathtags.core.events;

import deathtags.core.MMOParties;
import deathtags.gui.screens.InvitedScreen;
import deathtags.gui.screens.PartyScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class EventClient {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void OnServerDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        MMOParties.localParty = null;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void OnKeyInput(InputEvent.KeyInputEvent event) {
        if (MMOParties.OPEN_GUI_KEY.isKeyDown()) { // Detect party GUI keybind.
            // Open the party invitation menu if you have an invite.
            if (MMOParties.partyInviter != null)
                EventClient.OpenInvitationScreen();
            else
                EventClient.OpenPartyScreen();
        }
    }


    @SideOnly(Side.CLIENT)
    public static void OpenPartyScreen() {
        Minecraft.getMinecraft().displayGuiScreen(new PartyScreen());
    }

    /**
     * Handles opening of the invitation screen.
     * Opens automatically when an invite is received.
     * @link deathtags.networking.MessagePartyInvite
     */
    @SideOnly(Side.CLIENT)
    public static void OpenInvitationScreen() { Minecraft.getMinecraft().displayGuiScreen(new InvitedScreen()); }

}