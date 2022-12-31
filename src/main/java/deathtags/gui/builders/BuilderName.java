package deathtags.gui.builders;

import com.google.common.base.Charsets;
import deathtags.gui.PartyList;
import deathtags.gui.UISpec;
import deathtags.networking.BuilderData;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.player.Player;

public class BuilderName implements BuilderData {
    String name;
    @Override
    public void OnWrite(ByteBuf buffer, Player player) {
        buffer.writeInt(player.getName().getString().length());
        buffer.writeCharSequence(player.getName().getString(), Charsets.UTF_8);
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        name = (String)buffer.readCharSequence(buffer.readInt(), Charsets.UTF_8);
    }

    @Override
    public boolean IsDifferent(Player player) {
        return false;
    }

    public static class Renderer implements PartyList.NuggetBar {
        @Override
        public int Render(BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderName builder = (BuilderName) data;
            return PartyList.DrawText(builder.name, new UISpec(xOffset, yOffset));
        }
    }
}