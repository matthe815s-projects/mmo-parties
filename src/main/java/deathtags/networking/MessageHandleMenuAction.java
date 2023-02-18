package deathtags.networking;

import com.google.common.base.Charsets;
import deathtags.core.MMOParties;
import deathtags.stats.Party;
import deathtags.stats.PlayerStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageHandleMenuAction implements IMessage {

    public String name;
    public EnumPartyGUIAction action;

    public MessageHandleMenuAction() {
    }

    public MessageHandleMenuAction(CharSequence charSequence, EnumPartyGUIAction action)
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

    public static class Handler implements IMessageHandler<MessageHandleMenuAction, IMessage>
    {
        @Override
        public IMessage onMessage(MessageHandleMenuAction pkt, MessageContext messageContext) {
            EntityPlayer player = messageContext.getServerHandler().player;
            PlayerStats stats = MMOParties.GetStatsByName(messageContext.getServerHandler().player.getName());

            switch (pkt.action) {
                case INVITE:
                    // Start a new party if one doesn't already exist.
                    if (!stats.InParty()) stats.party = new Party(player);
                    if (stats.player != stats.party.leader) return null;

                    // Invite the player supplied in the charsequence to your party.
                    stats.party.Invite(player, player.getServer().getPlayerList().getPlayerByUsername(pkt.name));
                    break;

                case KICK:
                    if (!stats.InParty()) return null;
                    if (stats.player != stats.party.leader) return null;

                    stats.party.Leave(player.getServer().getPlayerList().getPlayerByUsername(pkt.name));
                    break;

                case LEADER:
                    if (!stats.InParty()) return null;
                    if (stats.player != stats.party.leader) return null;

                    stats.party.MakeLeader(player.getServer().getPlayerList().getPlayerByUsername(pkt.name)); // Set a new leader.
                    break;

                case DISBAND:
                    if (!stats.InParty()) return null;
                    if (stats.player != stats.party.leader) return null;

                    stats.party.Disband();
                    break;

                case LEAVE:
                    if (!stats.InParty()) return null;
                    stats.Leave();
                    break;

                case ACCEPT:
                    if (stats.partyInvite == null) return null;
                    stats.partyInvite.Join(stats.player, true);
                    break;

                case DENY:
                    if (stats.partyInvite == null) return null;
                    stats.partyInvite = null;
                    break;
            }

            return null;
        }
    }
}