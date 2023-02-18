package deathtags.gui.builders;

import deathtags.core.ConfigHandler;
import deathtags.gui.PartyList;
import deathtags.gui.UISpec;
import deathtags.networking.BuilderData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class BuilderArmor implements BuilderData {
    float armor;

    @Override
    public void OnWrite(ByteBuf buffer, EntityPlayer player) {
        buffer.writeFloat(player.getTotalArmorValue());
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        armor = buffer.readFloat();
    }

    @Override
    public boolean IsDifferent(EntityPlayer player) {
        return armor != player.getTotalArmorValue();
    }

    public static class NuggetBar implements PartyList.NuggetBar {

        @Override
        public int Render(BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderArmor builder = (BuilderArmor) data;
            return PartyList.Draw(builder.armor, builder.armor, new UISpec(PartyList.HEART_TEXTURE, xOffset, yOffset, 34, 9, 9, 9), 16, -9, compact,
                    ConfigHandler.Client_Options.showArmor && builder.armor > 0);
        }
    }
}
