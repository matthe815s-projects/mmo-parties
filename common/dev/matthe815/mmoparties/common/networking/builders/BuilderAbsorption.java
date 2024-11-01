package dev.matthe815.mmoparties.common.networking.builders;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.gui.UISpec;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;

public class BuilderAbsorption implements BuilderData {
    float absorption;

    @Override
    public void OnWrite(ByteBuf buffer, PlayerEntity player) {
        absorption = player.getAbsorptionAmount();
        buffer.writeFloat(absorption);
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        absorption = buffer.readFloat();
    }

    @Override
    public boolean IsDifferent(PlayerEntity player) {
        return absorption != player.getAbsorptionAmount();
    }

    public static class NuggetBar implements PartyList.NuggetBar {
        @Override
        public int Render(MatrixStack stack, BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderAbsorption builder = (BuilderAbsorption) data;
            return PartyList.Draw(new UISpec(stack, new UISpec(stack, 160, 0), new UISpec(stack, 169, 0), new UISpec(stack, 16, 0), xOffset, yOffset, 9, 9), builder.absorption, builder.absorption, compact,
                    ConfigHolder.CLIENT.showAbsorption.get() && builder.absorption > 0);
        }
    }
}
