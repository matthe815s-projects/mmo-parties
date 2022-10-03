package deathtags.core.events;

import deathtags.core.MMOParties;
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
    public void disconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        MMOParties.localParty = null;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void keyInput(InputEvent.KeyInputEvent event) {
        if (Minecraft.getMinecraft().isIntegratedServerRunning() && Minecraft.getMinecraft().getIntegratedServer().getCurrentPlayerCount() != 1) return;
        if ((Minecraft.getMinecraft().isIntegratedServerRunning() == false && (Minecraft.getMinecraft().getCurrentServerData() != null && !Minecraft.getMinecraft().getCurrentServerData().isOnLAN()))
                && Minecraft.getMinecraft().getConnection() == null) { return; } // No menu in singleplayer.

        if (MMOParties.OPEN_GUI_KEY.isKeyDown()) { // Detect party GUI keybind.
            Minecraft.getMinecraft().displayGuiScreen(new PartyScreen());
        }
    }


    @SideOnly(Side.CLIENT)
    public static void openScreen() {
        Minecraft.getMinecraft().displayGuiScreen(new PartyScreen());
    }

}