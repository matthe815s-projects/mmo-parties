package deathtags.gui.builders;

import deathtags.core.ConfigHandler;
import deathtags.gui.PartyList;
import deathtags.gui.UISpec;
import deathtags.networking.BuilderData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class BuilderAbsorption implements BuilderData {
    float absorption;

    @Override
    public void OnWrite(ByteBuf buffer, EntityPlayer player) {
        buffer.writeFloat(player.getAbsorptionAmount());
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        absorption = buffer.readFloat();
    }

    @Override
    public boolean IsDifferent(EntityPlayer player) {
        return absorption != player.getAbsorptionAmount();
    }

    public static class NuggetBar implements PartyList.NuggetBar {

        @Override
        public int Render(BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderAbsorption builder = (BuilderAbsorption) data;
            return PartyList.Draw(builder.absorption, builder.absorption, new UISpec(PartyList.HEART_TEXTURE, xOffset, yOffset, 160, 0, 9, 9), 16, 9, compact,
                    ConfigHandler.Client_Options.showAbsorption && builder.absorption > 0);
        }
    }
}
