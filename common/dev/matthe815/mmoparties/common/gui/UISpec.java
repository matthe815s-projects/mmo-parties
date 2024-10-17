package dev.matthe815.mmoparties.common.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class UISpec {
    public UISpec texture;
    public UISpec textureHalf;
    public UISpec textureBack;

    public GuiGraphics renderer;
    public int x;
    public int y;
    public int width;
    public int height;

    public UISpec(GuiGraphics renderer, int x, int y)
    {
        this.renderer = renderer;
        this.x = x;
        this.y = y;
    }

    public UISpec(GuiGraphics renderer, UISpec texture, UISpec textureHalf, UISpec textureBackground, int x, int y, int width, int height)
    {
        this.renderer = renderer;
        this.texture = texture;
        this.textureHalf = textureHalf;
        this.textureBack = textureBackground;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
