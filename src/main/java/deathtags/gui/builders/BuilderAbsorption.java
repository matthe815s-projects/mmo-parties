package deathtags.gui.builders;

import deathtags.config.ConfigHolder;
import deathtags.gui.HealthBar;
import deathtags.gui.UISpec;
import deathtags.networking.BuilderData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

public class BuilderAbsorption implements BuilderData {
    float absorption;

    @Override
    public void OnWrite(PacketBuffer buffer, PlayerEntity player) {
        buffer.writeFloat(player.getAbsorptionAmount());
    }

    @Override
    public void OnRead(PacketBuffer buffer) {
        absorption = buffer.readFloat();
    }

    @Override
    public boolean IsDifferent(PlayerEntity player) {
        return absorption != player.getAbsorptionAmount();
    }

    public static class NuggetBar implements HealthBar.NuggetBar {

        @Override
        public int Render(BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderAbsorption builder = (BuilderAbsorption) data;
            return HealthBar.Draw(builder.absorption, builder.absorption, new UISpec(HealthBar.HEART_TEXTURE, xOffset, yOffset, 160, 0, 9, 9), 16, 9, compact,
                    ConfigHolder.CLIENT.showAbsorption.get() && builder.absorption > 0);
        }
    }
}
