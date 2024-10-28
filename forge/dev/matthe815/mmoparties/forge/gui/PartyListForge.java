package dev.matthe815.mmoparties.forge.gui;

import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.forge.api.compatibility.CompatibilityHelper;
import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * The UI handler that controls the creation and rendering of all party elements
 * Each bar to be rendered is created on mod initialization.
 * @see CompatibilityHelper
 */
public class PartyListForge {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (!event.getType().equals(RenderGameOverlayEvent.ElementType.TEXT)) return;
        PartyList.onRenderGameOverlay(Minecraft.getMinecraft().ingameGUI);
    }
}