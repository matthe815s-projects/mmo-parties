package dev.matthe815.mmoparties.forge.gui;

import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.forge.api.compatibility.CompatibilityHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * The UI handler that controls the creation and rendering of all party elements
 * Each bar to be rendered is created on mod initialization.
 * @see CompatibilityHelper
 */
public class PartyListForge {

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (!event.getType().equals(RenderGameOverlayEvent.ElementType.TEXT)) return;
        PartyList.onRenderGameOverlay(event.getMatrixStack());
    }
}