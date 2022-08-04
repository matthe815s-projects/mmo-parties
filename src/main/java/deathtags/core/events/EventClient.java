package deathtags.core.events;

import deathtags.core.MMOParties;
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
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void disconnectFromServer(ClientPlayerNetworkEvent.LoggedOutEvent event)
    {
        MMOParties.localParty = null;
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void keyInput(InputEvent.KeyInputEvent event) {
        if (MMOParties.OPEN_GUI_KEY.isDown()) { // Detect party GUI keybind.
            Minecraft.getInstance().setScreen(new PartyScreen());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void openScreen() {
        Minecraft.getInstance().setScreen(new PartyScreen());
    }
}
