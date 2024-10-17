package dev.matthe815.mmoparties.common.gui.rendering;

import dev.matthe815.mmoparties.common.gui.UISpec;
import net.minecraft.client.Minecraft;

public class Renderer {
    public static void drawString(UISpec ui, String text)
    {
        ui.renderer.drawString(Minecraft.getInstance().font, text, ui.x, ui.y, 0xFFFFFF);
    }

    public static void drawSprite (UISpec UI) {
        UI.renderer.blit(UI.texture, UI.x, UI.y, 0, 0, 0, 0);
    }

    public static void drawLayeredSprite (UISpec UI) {
        UI.renderer.blit(UI.textureBack, UI.x, UI.y, 0, 0, 0, 0);
        UI.renderer.blit(UI.texture, UI.x, UI.y, 0, 0, 0 ,0);
    }

    public static void drawHalvedLayeredSprite (UISpec UI, float current, int half) {
        UI.renderer.blit(UI.textureBack, UI.x, UI.y, UI.width, UI.height, 0, 0);

        // Draw half or full depending on health amount.
        if ((int) current > half) {
            UI.renderer.blit(UI.texture, UI.x, UI.y, UI.width, UI.height, 0, 0);
        }
        else if ((int) current == half)
            UI.renderer.blit(UI.textureHalf, UI.x, UI.y, UI.width, UI.height, 0, 0);
    }
}
