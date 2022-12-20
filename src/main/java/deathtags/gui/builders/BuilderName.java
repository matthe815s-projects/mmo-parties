package deathtags.gui.builders;

import com.google.common.base.Charsets;
import deathtags.gui.HealthBar;
import deathtags.gui.UISpec;
import deathtags.networking.BuilderData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

public class BuilderName implements BuilderData {
    String name;
    @Override
    public void OnWrite(PacketBuffer buffer, PlayerEntity player) {
        buffer.writeInt(player.getName().getString().length());
        buffer.writeCharSequence(player.getName().getString(), Charsets.UTF_8);
    }

    @Override
    public void OnRead(PacketBuffer buffer) {
        name = (String)buffer.readCharSequence(buffer.readInt(), Charsets.UTF_8);
    }

    public static class Renderer implements HealthBar.NuggetBar {
        @Override
        public int Render(BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderName builder = (BuilderName) data;
            return HealthBar.DrawText(builder.name, new UISpec(xOffset, yOffset));
        }
    }
}
