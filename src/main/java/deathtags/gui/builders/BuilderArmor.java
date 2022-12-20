package deathtags.gui.builders;

import deathtags.config.ConfigHolder;
import deathtags.gui.HealthBar;
import deathtags.gui.UISpec;
import deathtags.networking.BuilderData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

public class BuilderArmor implements BuilderData {
    float armor;

    @Override
    public void OnWrite(PacketBuffer buffer, PlayerEntity player) {
        buffer.writeFloat(player.getArmorValue());
    }

    @Override
    public void OnRead(PacketBuffer buffer) {
        armor = buffer.readFloat();
    }

    public static class NuggetBar implements HealthBar.NuggetBar {

        @Override
        public int Render(BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderArmor builder = (BuilderArmor) data;
            return HealthBar.Draw(builder.armor, builder.armor, new UISpec(HealthBar.HEART_TEXTURE, xOffset, yOffset, 34, 9, 9, 9), 16, -9, compact,
                    ConfigHolder.CLIENT.showArmor.get() && builder.armor > 0);
        }
    }
}
