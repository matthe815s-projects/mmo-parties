package deathtags.gui.builders;

import deathtags.config.ConfigHolder;
import deathtags.gui.PartyList;
import deathtags.gui.UISpec;
import deathtags.networking.BuilderData;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.player.Player;

public class BuilderAbsorption implements BuilderData {
    float absorption;

    @Override
    public void OnWrite(ByteBuf buffer, Player player) {
        buffer.writeFloat(player.getAbsorptionAmount());
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        absorption = buffer.readFloat();
    }

    @Override
    public boolean IsDifferent(Player player) {
        return absorption != player.getAbsorptionAmount();
    }

    public static class NuggetBar implements PartyList.NuggetBar {

        @Override
        public int Render(BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderAbsorption builder = (BuilderAbsorption) data;
            return PartyList.Draw(builder.absorption, builder.absorption, new UISpec(PartyList.HEART_TEXTURE, xOffset, yOffset, 160, 0, 9, 9), 16, 9, compact,
                    ConfigHolder.CLIENT.showAbsorption.get() && builder.absorption > 0);
        }
    }
}
