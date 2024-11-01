package dev.matthe815.mmoparties.common.networking.builders;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.matrix.MatrixStack;
import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.gui.UISpec;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;

public class BuilderName implements BuilderData {
    String name;
    @Override
    public void OnWrite(ByteBuf buffer, PlayerEntity player) {
        buffer.writeInt(player.getName().toString().length());
        buffer.writeCharSequence(player.getName().toString(), Charsets.UTF_8);
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        name = (String)buffer.readCharSequence(buffer.readInt(), Charsets.UTF_8);
    }

    @Override
    public boolean IsDifferent(PlayerEntity player) {
        return false;
    }

    public static class Renderer implements PartyList.NuggetBar {
        @Override
        public int Render(MatrixStack stack, BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderName builder = (BuilderName) data;
            return PartyList.DrawText(builder.name, new UISpec(stack, xOffset, yOffset));
        }
    }
}