package deathtags.gui.builders;

import deathtags.config.ConfigHolder;
import deathtags.gui.PartyList;
import deathtags.gui.UISpec;
import deathtags.networking.BuilderData;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.player.Player;

public class BuilderHunger implements BuilderData {
    float hunger;

    @Override
    public void OnWrite(ByteBuf buffer, Player player) {
        hunger = player.getFoodData().getFoodLevel();
        buffer.writeFloat(hunger);
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        hunger = buffer.readFloat();
    }

    @Override
    public boolean IsDifferent(Player player) {
        return hunger != player.getFoodData().getFoodLevel();
    }

    public static class NuggetBar implements PartyList.NuggetBar {
        @Override
        public int Render(BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderHunger builder = (BuilderHunger) data;
            return PartyList.Draw(builder.hunger, 20, new UISpec(PartyList.HEART_TEXTURE, xOffset, yOffset, 52, 27, 9, 9), 16, 9, compact, ConfigHolder.CLIENT.showHunger.get());
        }
    }
}
