package deathtags.gui;

import net.minecraft.util.ResourceLocation;

public class UISpec {
    public ResourceLocation texture;
    public int x;
    public int y;
    public int texture_x;
    public int texture_y;
    public int width;
    public int height;

    public UISpec(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public UISpec(ResourceLocation texture, int x, int y, int texture_x, int texture_y, int width, int height)
    {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.texture_x = texture_x;
        this.texture_y = texture_y;
        this.width = width;
        this.height = height;
    }
}
