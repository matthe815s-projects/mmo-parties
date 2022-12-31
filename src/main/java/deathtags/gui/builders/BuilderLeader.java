package deathtags.gui.builders;

import deathtags.core.MMOParties;
import deathtags.gui.PartyList;
import deathtags.gui.UISpec;
import deathtags.networking.BuilderData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class BuilderLeader implements BuilderData {
    boolean isLeader;
    @Override
    public void OnWrite(ByteBuf buffer, EntityPlayer player) {
//        if (player == null) return; // Nothing here.
//
//        // Handle an edge case that can cause crashing (writing a packet while closing the server)
//        if (!(MMOParties.GetStats(player) != null & MMOParties.GetStats(player).InParty())) return;

        buffer.writeBoolean(false);
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        isLeader = buffer.readBoolean();
    }

    @Override
    public boolean IsDifferent(EntityPlayer player) {
        return isLeader != (MMOParties.GetStats(player).party.leader == player);
    }

    public static class Renderer implements PartyList.NuggetBar {
        @Override
        public int Render(BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderLeader builder = (BuilderLeader) data;
            if (builder.isLeader) PartyList.DrawResource(new UISpec(PartyList.TEXTURE_ICON, xOffset, yOffset, 0, 18, 9, 9));
            return builder.isLeader ? 9 : 0;
        }
    }
}
