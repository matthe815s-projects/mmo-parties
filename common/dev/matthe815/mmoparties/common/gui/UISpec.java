package dev.matthe815.mmoparties.common.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

public class UISpec {

    public MatrixStack stack;

    public UISpec texture;
    public UISpec textureHalf;
    public UISpec textureBack;

    public int x;
    public int y;
    public int width;
    public int height;

    public UISpec(MatrixStack stack, int x, int y)
    {
        this.stack = stack;
        this.x = x;
        this.y = y;
    }

    public UISpec(MatrixStack stack, UISpec texture, UISpec textureHalf, UISpec textureBackground, int x, int y, int width, int height)
    {
        this.stack = stack;
        this.texture = texture;
        this.textureHalf = textureHalf;
        this.textureBack = textureBackground;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
