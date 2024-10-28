package dev.matthe815.mmoparties.common.networking.builders;

import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.gui.UISpec;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class BuilderArmor implements BuilderData {
    float armor;

    @Override
    public void OnWrite(ByteBuf buffer, EntityPlayer player) {
        armor = player.getTotalArmorValue();
        buffer.writeFloat(armor);
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
            return PartyList.Draw(new UISpec(new UISpec(34, 9), new UISpec(25, 9), new UISpec(16, 9), xOffset, yOffset, 9, 9), builder.armor, builder.armor, compact,
                    ConfigHolder.CLIENT.showArmor && builder.armor > 0);
        }
    }
}
