package deathtags.gui.builders;

import com.google.common.base.Charsets;
import deathtags.core.MMOParties;
import deathtags.gui.HealthBar;
import deathtags.gui.UISpec;
import deathtags.networking.BuilderData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

public class BuilderLeader implements BuilderData {
    boolean isLeader;
    @Override
    public void OnWrite(PacketBuffer buffer, PlayerEntity player) {
        // Handle an edge case that can cause crashing (writing a packet while closing the server)
        if (!(MMOParties.GetStats(player) != null & MMOParties.GetStats(player).InParty())) return;

        buffer.writeBoolean(MMOParties.GetStats(player).party.leader == player);
    }

    @Override
    public void OnRead(PacketBuffer buffer) {
        isLeader = buffer.readBoolean();
    }

    @Override
    public boolean IsDifferent(PlayerEntity player) {
        return isLeader != (MMOParties.GetStats(player).party.leader == player);
    }

    public static class Renderer implements HealthBar.NuggetBar {
        @Override
        public int Render(BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderLeader builder = (BuilderLeader) data;
            if (builder.isLeader) HealthBar.DrawResource(new UISpec(HealthBar.TEXTURE_ICON, xOffset, yOffset, 0, 18, 9, 9));
            return builder.isLeader ? 9 : 0;
        }
    }
}
