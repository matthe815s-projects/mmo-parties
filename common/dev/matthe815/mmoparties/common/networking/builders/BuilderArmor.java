package dev.matthe815.mmoparties.common.networking.builders;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.gui.UISpec;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;

public class BuilderArmor implements BuilderData {
    float armor;

    @Override
    public void OnWrite(ByteBuf buffer, PlayerEntity player) {
        armor = player.getArmorValue();
        buffer.writeFloat(armor);
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        armor = buffer.readFloat();
    }

    @Override
    public boolean IsDifferent(PlayerEntity player) {
        return armor != player.getArmorValue();
    }

    public static class NuggetBar implements PartyList.NuggetBar {
        @Override
        public int Render(MatrixStack stack, BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderArmor builder = (BuilderArmor) data;
            return PartyList.Draw(new UISpec(stack, new UISpec(stack, 34, 9), new UISpec(stack, 25, 9), new UISpec(stack, 16, 9), xOffset, yOffset, 9, 9), builder.armor, builder.armor, compact,
                    ConfigHolder.CLIENT.showArmor.get() && builder.armor > 0);
        }
    }
}
