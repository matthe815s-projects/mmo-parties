package dev.matthe815.mmoparties.common.gui.rendering;

import dev.matthe815.mmoparties.common.gui.UISpec;
import net.minecraft.client.Minecraft;

public class Renderer {
    public static void drawString(UISpec ui, String text)
    {
        ui.renderer.drawString(Minecraft.getInstance().font, text, ui.x, ui.y, 0xFFFFFF);
    }

    public static void drawSprite (UISpec UI) {
        UI.renderer.blitSprite(UI.texture, UI.x, UI.y, 0, 0);
    }

    public static void drawLayeredSprite (UISpec UI) {
        UI.renderer.blitSprite(UI.textureBack, UI.x, UI.y, 0, 0);
        UI.renderer.blitSprite(UI.texture, UI.x, UI.y, 0, 0);
    }
}
