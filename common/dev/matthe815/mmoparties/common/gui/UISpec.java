package dev.matthe815.mmoparties.common.gui;

import com.mojang.blaze3d.vertex.PoseStack;

public class UISpec {
    public UISpec texture;
    public UISpec textureHalf;
    public UISpec textureBack;
    public PoseStack stack;
    public int x;
    public int y;
    public int width;
    public int height;

    public UISpec(PoseStack renderer, int x, int y)
    {
        this.stack = renderer;
        this.x = x;
        this.y = y;
    }

    public UISpec(PoseStack renderer, UISpec texture, UISpec textureHalf, UISpec textureBackground, int x, int y, int width, int height)
    {
        this.stack = renderer;
        this.texture = texture;
        this.textureHalf = textureHalf;
        this.textureBack = textureBackground;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
