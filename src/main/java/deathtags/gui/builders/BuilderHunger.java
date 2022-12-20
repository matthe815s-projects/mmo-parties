package deathtags.gui.builders;

import deathtags.config.ConfigHolder;
import deathtags.gui.HealthBar;
import deathtags.gui.UISpec;
import deathtags.networking.BuilderData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

public class BuilderHunger implements BuilderData {
    float hunger;

    @Override
    public void OnWrite(PacketBuffer buffer, PlayerEntity player) {
        buffer.writeFloat(player.getFoodData().getFoodLevel());
    }

    @Override
    public void OnRead(PacketBuffer buffer) {
        hunger = buffer.readFloat();
    }

    @Override
    public void IsDifferent() {

    }

    public static class NuggetBar implements HealthBar.NuggetBar {
        @Override
        public int Render(BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderHunger builder = (BuilderHunger) data;
            return HealthBar.Draw(builder.hunger, 20, new UISpec(HealthBar.HEART_TEXTURE, xOffset, yOffset, 52, 27, 9, 9), 16, 9, compact, ConfigHolder.CLIENT.showHunger.get());
        }
    }
}
