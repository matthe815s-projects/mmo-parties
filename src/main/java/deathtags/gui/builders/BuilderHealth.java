package deathtags.gui.builders;

import deathtags.gui.HealthBar;
import deathtags.gui.UISpec;
import deathtags.networking.BuilderData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

public class BuilderHealth implements BuilderData {
    float health;
    float maxHealth;

    @Override
    public void OnWrite(PacketBuffer buffer, PlayerEntity player) {
        buffer.writeFloat(player.getHealth());
        buffer.writeFloat(player.getMaxHealth());
    }

    @Override
    public void OnRead(PacketBuffer buffer) {
        health = buffer.readFloat();
        maxHealth = buffer.readFloat();
    }

    @Override
    public void IsDifferent() {

    }

    public static class NuggetBar implements HealthBar.NuggetBar {

        @Override
        public int Render(BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderHealth builder = (BuilderHealth) data;
            return HealthBar.Draw(builder.health, builder.maxHealth, new UISpec(HealthBar.HEART_TEXTURE, xOffset, yOffset, 52, 0, 9, 9), 16, 9, compact, true);
        }
    }
}
