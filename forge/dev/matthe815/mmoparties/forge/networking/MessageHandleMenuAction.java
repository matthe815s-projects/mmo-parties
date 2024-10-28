package dev.matthe815.mmoparties.forge.networking;

import com.google.common.base.Charsets;
import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import dev.matthe815.mmoparties.forge.core.MMOParties;
import dev.matthe815.mmoparties.common.stats.Party;
import dev.matthe815.mmoparties.common.stats.PlayerStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Objects;

/**
 * Handles menu actions for the popup GUI.
 * Does not skip permission checks normally performed within commands.
 * @since 2.2.0
 */
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
	public void fromBytes(ByteBuf buf)
	{
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
		public IMessage onMessage(final MessageHandleMenuAction pkt, MessageContext messageContext)
		{
			EntityPlayer player = messageContext.getServerHandler().player;
			PlayerStats stats = MMOParties.GetStatsByName(messageContext.getServerHandler().player.getName());

			switch (pkt.action) {
				case INVITE:
					// Start a new party if one doesn't already exist.
                    assert stats != null;
					if (!stats.InParty()) stats.party = new Party(player);
					if (stats.player != stats.party.leader) return null;

					// If invite all is allowed and used.
					if (Objects.equals(pkt.name, "") && ConfigHolder.COMMON.allowInviteAll) {
						player.getServer().getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
							stats.party.Invite(player, serverPlayerEntity); // Invite player.
						});
					}
					else {
						stats.party.Invite(player, player.getServer().getPlayerList().getPlayerByUsername(pkt.name));
					}
					break;

				case KICK:
                    assert stats != null;
                    if (!stats.InParty()) return null;
					if (stats.player != stats.party.leader) return null;

					stats.party.Leave(player.getServer().getPlayerList().getPlayerByUsername(pkt.name));
					break;

				case LEADER:
                    assert stats != null;
                    if (!stats.InParty()) return null;
					if (stats.player != stats.party.leader) return null;

					stats.party.MakeLeader(Objects.requireNonNull(player.getServer().getPlayerList().getPlayerByUsername(pkt.name))); // Set a new leader.
					break;

				case DISBAND:
                    assert stats != null;
                    if (!stats.InParty()) return null;
					if (stats.player != stats.party.leader) return null;

					stats.party.Disband();
					break;

				case LEAVE:
                    assert stats != null;
                    if (!stats.InParty()) return null;
					stats.Leave();
					break;

				case ACCEPT:
                    assert stats != null;
                    if (stats.partyInvite == null) return null;
					stats.partyInvite.Join(stats.player, true);
					break;

				case DENY:
                    assert stats != null;
                    if (stats.partyInvite == null) return null;
					stats.partyInvite = null;
					break;
			}

			return null;
		}
	}
}