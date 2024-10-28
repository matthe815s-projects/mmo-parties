package dev.matthe815.mmoparties.common.gui;

public class UISpec {
    public UISpec texture;
    public UISpec textureHalf;
    public UISpec textureBack;

    public int x;
    public int y;
    public int width;
    public int height;

    public UISpec(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public UISpec(UISpec texture, UISpec textureHalf, UISpec textureBackground, int x, int y, int width, int height)
    {
        this.texture = texture;
        this.textureHalf = textureHalf;
        this.textureBack = textureBackground;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
