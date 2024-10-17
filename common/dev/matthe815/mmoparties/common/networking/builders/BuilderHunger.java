package dev.matthe815.mmoparties.common.networking.builders;

import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.gui.UISpec;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class BuilderHunger implements BuilderData {
    float hunger;

    @Override
    public void OnWrite(ByteBuf buffer, Player player) {
        hunger = player.getFoodData().getFoodLevel();
        buffer.writeFloat(hunger);
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        hunger = buffer.readFloat();
    }

    @Override
    public boolean IsDifferent(Player player) {
        return hunger != player.getFoodData().getFoodLevel();
    }

    public static class NuggetBar implements PartyList.NuggetBar {
        @Override
        public int Render(GuiGraphics gui, BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderHunger builder = (BuilderHunger) data;
            return PartyList.Draw(new UISpec(gui, new UISpec(gui, 52, 27), new UISpec(gui, 61, 27), new UISpec(gui, 16, 27), xOffset, yOffset, 9, 9), builder.hunger, 20, compact, ConfigHolder.CLIENT.showHunger.get());
        }
    }
}
