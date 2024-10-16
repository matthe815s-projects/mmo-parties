package dev.matthe815.mmoparties.common.networking.builders;

import dev.matthe815.mmoparties.forge.core.MMOParties;
import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.gui.UISpec;
import dev.matthe815.mmoparties.common.stats.PlayerStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

public class BuilderLeader implements BuilderData {
    public boolean isLeader = false;
    @Override
    public void OnWrite(ByteBuf buffer, Player player) {
        PlayerStats stats = MMOParties.GetStats(player);
        if (player == null || stats == null || !stats.InParty()) {
            buffer.writeBoolean(stats.party.leader.getUUID().equals(player.getUUID()));
            return; // Nothing here.
        }

        buffer.writeBoolean(isLeader);
    }

    @Override
    public void OnRead(ByteBuf buffer) {
        isLeader = buffer.readBoolean();
    }

    @Override
    public boolean IsDifferent(Player player) {
        if (MMOParties.GetStats(player) == null) return false; // Integrity check
        return !(isLeader == (MMOParties.GetStats(player).party.leader.getUUID().equals(player.getUUID())));
    }

    public static class Renderer implements PartyList.NuggetBar {
        @Override
        public int Render(GuiGraphics gui, BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderLeader builder = (BuilderLeader) data;
            if (builder.isLeader) PartyList.DrawResource(new UISpec(gui, PartyList.TEXTURE_ICON, null, null, 0, 18, 9, 9));
            return builder.isLeader ? 9 : 0;
        }
    }
}
