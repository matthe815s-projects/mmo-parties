package dev.matthe815.mmoparties.common.networking.builders;

import dev.matthe815.mmoparties.forge.core.MMOParties;
import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.stats.PlayerStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class BuilderLeader implements BuilderData {
    public boolean isLeader = false;
    @Override
    public void OnWrite(ByteBuf buffer, EntityPlayer player) {
        PlayerStats stats = MMOParties.GetStats(player);
        if (player == null || stats == null || !stats.InParty()) {
            buffer.writeBoolean(stats.party.leader.getUniqueID().equals(player.getUniqueID()));
            return; // Nothing here.
        }

        buffer.writeBoolean(isLeader);
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        isLeader = buffer.readBoolean();
    }

    @Override
    public boolean IsDifferent(EntityPlayer player) {
        if (MMOParties.GetStats(player) == null) return false; // Integrity check
        return !(isLeader == (MMOParties.GetStats(player).party.leader.getUniqueID().equals(player.getUniqueID())));
    }

    public static class Renderer implements PartyList.NuggetBar {
        @Override
        public int Render(BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderLeader builder = (BuilderLeader) data;
            //if (builder.isLeader) PartyList.DrawResource(new UISpec(gui, PartyList.TEXTURE_ICON, null, null, 0, 18, 9, 9));
            return builder.isLeader ? 9 : 0;
        }
    }
}
