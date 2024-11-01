package dev.matthe815.mmoparties.common.networking.builders;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.gui.UISpec;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;

public class BuilderHunger implements BuilderData {
    float hunger;

    @Override
    public void OnWrite(ByteBuf buffer, PlayerEntity player) {
        hunger = player.getFoodData().getFoodLevel();
        buffer.writeFloat(hunger);
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        hunger = buffer.readFloat();
    }

    @Override
    public boolean IsDifferent(PlayerEntity player) {
        return hunger != player.getFoodData().getFoodLevel();
    }

    public static class NuggetBar implements PartyList.NuggetBar {
        @Override
        public int Render(MatrixStack stack, BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderHunger builder = (BuilderHunger) data;
            return PartyList.Draw(new UISpec(stack, new UISpec(stack, 52, 27), new UISpec(stack, 61, 27), new UISpec(stack, 16, 27), xOffset, yOffset, 9, 9), builder.hunger, 20, compact, ConfigHolder.CLIENT.showHunger.get());
        }
    }
}
