package deathtags.networking;

import com.google.common.base.Charsets;
import deathtags.core.ConfigHandler;
import deathtags.core.MMOParties;
import deathtags.stats.Party;
import deathtags.stats.PlayerStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageGUIInvitePlayer implements IMessage {

    public String name;
    public EnumPartyGUIAction action;

    public MessageGUIInvitePlayer() {
    }

    public MessageGUIInvitePlayer(CharSequence charSequence, EnumPartyGUIAction action)
    {
        this.name = charSequence.toString();
        this.action = action;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.name = buf.readCharSequence(buf.readInt(), Charsets.UTF_8).toString();
        this.action = EnumPartyGUIAction.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.name.length());
        buf.writeCharSequence(this.name, Charsets.UTF_8);
        buf.writeInt(this.action.ordinal());
    }

    public static class Handler implements IMessageHandler<MessageGUIInvitePlayer, IMessage>
    {
        @Override
        public IMessage onMessage(MessageGUIInvitePlayer pkt, MessageContext messageContext) {
            PlayerStats stats = MMOParties.GetStatsByName(messageContext.getClientHandler().getGameProfile().getName());

            switch (pkt.action) {
                case INVITE:
                    if (!stats.InParty()) stats.party = new Party(messageContext.getServerHandler().player); // Start new party with leader.
                    stats.party.Invite(messageContext.getServerHandler().player, messageContext.getServerHandler().player.world.getPlayerEntityByName(pkt.name)); // Invite player.
                    break;
                case KICK:
                    if (!stats.InParty()) return null; // No-op
                    stats.party.Leave(messageContext.getServerHandler().player.world.getPlayerEntityByName(pkt.name)); // Remove a player.
                    break;
                case LEADER:
                    if (!stats.InParty()) return null; // No-op
                    stats.party.MakeLeader(messageContext.getServerHandler().player.world.getPlayerEntityByName(pkt.name)); // Set a new leader.
                    break;
                case DISBAND:
                    if (!stats.InParty()) return null; // No-op.
                    stats.party.Disband();
                    break;
                case LEAVE:
                    if (!stats.InParty()) return null;
                    stats.Leave(); // Leave the party
                    break;
            }
            return null;
        }
    }
}