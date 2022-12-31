package deathtags.gui.builders;

import deathtags.core.ConfigHandler;
import deathtags.gui.PartyList;
import deathtags.gui.UISpec;
import deathtags.networking.BuilderData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class BuilderHunger implements BuilderData {
    float hunger;

    @Override
    public void OnWrite(ByteBuf buffer, EntityPlayer player) {
        buffer.writeFloat(player.getFoodStats().getFoodLevel());
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
            return PartyList.Draw(builder.hunger, 20, new UISpec(PartyList.HEART_TEXTURE, xOffset, yOffset, 52, 27, 9, 9), 16, 9, compact, ConfigHandler.Client_Options.showHunger);
        }
    }
}
