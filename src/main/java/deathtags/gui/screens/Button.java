package deathtags.gui.screens;

import net.minecraft.client.gui.GuiButton;

public class Button extends GuiButton implements IPressable {

    IPressable pressable;

    public Button(int id, int x, int y, int width, int height, String p_i1020_4_, IPressable pressable) {
        super(id, x, y, width, height, p_i1020_4_);
        this.pressable = pressable;
    }

    @Override
    public void OnPress() {
        this.pressable.OnPress();
    }
}
