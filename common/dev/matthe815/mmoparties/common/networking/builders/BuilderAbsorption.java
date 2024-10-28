package dev.matthe815.mmoparties.common.networking.builders;

import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.gui.UISpec;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class BuilderAbsorption implements BuilderData {
    float absorption;

    @Override
    public void OnWrite(ByteBuf buffer, EntityPlayer player) {
        absorption = player.getAbsorptionAmount();
        buffer.writeFloat(absorption);
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
            return PartyList.Draw(new UISpec(new UISpec(160, 0), new UISpec(169, 0), new UISpec(16, 0), xOffset, yOffset, 9, 9), builder.absorption, builder.absorption, compact,
                    ConfigHolder.CLIENT.showAbsorption && builder.absorption > 0);
        }
    }
}
