package dev.matthe815.mmoparties.common.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class UISpec {
    public ResourceLocation texture;
    public ResourceLocation textureHalf;
    public ResourceLocation textureBack;

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

    public UISpec(GuiGraphics renderer, ResourceLocation texture, ResourceLocation textureHalf, ResourceLocation textureBackground, int x, int y, int width, int height)
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
