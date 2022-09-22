package deathtags.gui;

import net.minecraft.util.ResourceLocation;

public class UISpec {
    public ResourceLocation texture;
    public int x;
    public int y;
    public int texture_x;
    public int texture_y;

    public UISpec(ResourceLocation texture, int x, int y, int texture_x, int texture_y)
    {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.texture_x = texture_x;
        this.texture_y = texture_y;
    }
}