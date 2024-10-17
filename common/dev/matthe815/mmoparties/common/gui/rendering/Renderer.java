package dev.matthe815.mmoparties.common.gui.rendering;

import dev.matthe815.mmoparties.common.gui.UISpec;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class Renderer {
    public static ResourceLocation MC_ICONS = new ResourceLocation("textures/gui/icons.png");

    public static void drawString(UISpec ui, String text)
    {
        ui.renderer.drawString(Minecraft.getInstance().font, text, ui.x, ui.y, 0xFFFFFF);
    }

    public static void drawSprite (UISpec UI) {
        UI.renderer.blit(MC_ICONS, UI.x, UI.y, 0, 0, 0, 0);
    }

    public static void drawLayeredSprite (UISpec UI) {
        UI.renderer.blit(MC_ICONS, UI.x, UI.y, 0, 0, 9, 9);
        UI.renderer.blit(MC_ICONS, UI.x, UI.y, 0, 0, 9, 9);
    }

    public static void drawHalvedLayeredSprite (UISpec UI, float current, int half) {
        UI.renderer.blit(MC_ICONS, UI.x, UI.y, UI.textureBack.x, UI.textureBack.y, UI.width, UI.height);

        // Draw half or full depending on health amount.
        if ((int) current > half) {
            UI.renderer.blit(MC_ICONS, UI.x, UI.y, UI.texture.x, UI.texture.y, UI.width, UI.height);
        }
        else if ((int) current == half)
            UI.renderer.blit(MC_ICONS, UI.x, UI.y, UI.textureHalf.x, UI.textureHalf.y, UI.width, UI.height);
    }
}
