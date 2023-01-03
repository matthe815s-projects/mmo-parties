package deathtags.gui.builders;

import com.mojang.blaze3d.matrix.MatrixStack;
import deathtags.core.MMOParties;
import deathtags.gui.PartyList;
import deathtags.gui.UISpec;
import deathtags.networking.BuilderData;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

public class BuilderLeader implements BuilderData {
    public boolean isLeader;
    @Override
    public void OnWrite(PacketBuffer buffer, PlayerEntity player) {
        if (player == null || MMOParties.GetStats(player) == null || !MMOParties.GetStats(player).InParty()) {
            buffer.writeBoolean(false);
            return; // Nothing here.
        }


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

    public static class Renderer implements PartyList.NuggetBar {
        @Override
        public int Render(BuilderData data, int xOffset, int yOffset, boolean compact) {
            BuilderLeader builder = (BuilderLeader) data;
            if (builder.isLeader) PartyList.DrawResource(new UISpec(PartyList.TEXTURE_ICON, xOffset, yOffset, 0, 18, 9, 9));
            return builder.isLeader ? 9 : 0;
        }
    }
}
