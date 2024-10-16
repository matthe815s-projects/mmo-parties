package dev.matthe815.mmoparties.forge.gui;

import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.forge.api.compatibility.CompatibilityHelper;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * The UI handler that controls the creation and rendering of all party elements
 * Each bar to be rendered is created on mod initialization.
 * @see CompatibilityHelper
 */
@Mod.EventBusSubscriber
public class PartyListForge {
    @SubscribeEvent
    public void onRenderGameOverlay(CustomizeGuiOverlayEvent gui) {
        PartyList.onRenderGameOverlay(gui.getGuiGraphics());
    }
}