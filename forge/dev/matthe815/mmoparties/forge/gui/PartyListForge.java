package dev.matthe815.mmoparties.forge.gui;

import dev.matthe815.mmoparties.common.core.MMOPartiesCommon;
import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.gui.UISpec;
import dev.matthe815.mmoparties.common.gui.rendering.Renderer;
import dev.matthe815.mmoparties.common.networking.builders.BuilderData;
import dev.matthe815.mmoparties.common.stats.PartyMemberData;
import dev.matthe815.mmoparties.forge.api.compatibility.CompatibilityHelper;
import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;
import java.util.Random;

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