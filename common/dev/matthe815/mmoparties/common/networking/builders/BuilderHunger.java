package dev.matthe815.mmoparties.common.networking.builders;

import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.gui.UISpec;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class BuilderHunger implements BuilderData {
    float hunger;

    @Override
    public void OnWrite(ByteBuf buffer, EntityPlayer player) {
        hunger = player.getFoodStats().getFoodLevel();
        buffer.writeFloat(hunger);
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        hunger = buffer.readFloat();
    }

    @Override
    public boolean IsDifferent(EntityPlayer player) {
        return hunger != player.getFoodStats().getFoodLevel();
    }

    public static class NuggetBar implements PartyList.NuggetBar {
        @Override
        public int Render(BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderHunger builder = (BuilderHunger) data;
            return PartyList.Draw(new UISpec(new UISpec(52, 27), new UISpec(61, 27), new UISpec(16, 27), xOffset, yOffset, 9, 9), builder.hunger, 20, compact, ConfigHolder.CLIENT.showHunger);
        }
    }
}
