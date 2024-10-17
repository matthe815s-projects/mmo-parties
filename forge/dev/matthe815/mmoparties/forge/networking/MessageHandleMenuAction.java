package dev.matthe815.mmoparties.forge.networking;

import com.google.common.base.Charsets;
import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import dev.matthe815.mmoparties.forge.core.MMOParties;
import dev.matthe815.mmoparties.common.stats.Party;
import dev.matthe815.mmoparties.common.stats.PlayerStats;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Handles menu actions for the popup GUI.
 * Does not skip permission checks normally performed within commands.
 * @since 2.2.0
 */
public class MessageHandleMenuAction {
	public final String name;
	public final EnumPartyGUIAction action;

	public MessageHandleMenuAction(CharSequence charSequence, EnumPartyGUIAction action)
	{
		this.name = charSequence.toString();
		this.action = action;
	}

	public static MessageHandleMenuAction decode(ByteBuf buf)
	{
		return new MessageHandleMenuAction(buf.readCharSequence(buf.readInt(), Charsets.UTF_8), EnumPartyGUIAction.values()[buf.readInt()]);
	}

	public static void encode(MessageHandleMenuAction msg, ByteBuf buf)
	{
		buf.writeInt(msg.name.length());
		buf.writeCharSequence(msg.name, Charsets.UTF_8);
		buf.writeInt(msg.action.ordinal());
	}

	public static class Handler
	{
		public static void handle(final MessageHandleMenuAction pkt, Supplier<NetworkEvent.Context> supplier)
		{
			NetworkEvent.Context ctx = supplier.get();
			ctx.setPacketHandled(true);
			PlayerStats stats = MMOParties.GetStatsByName(Objects.requireNonNull(ctx.getSender()).getName().getString());

			switch (pkt.action) {
				case INVITE:
					// Start a new party if one doesn't already exist.
                    assert stats != null;
                    if (!stats.InParty()) stats.party = new Party(ctx.getSender());
					if (stats.player != stats.party.leader) return;

					// If invite all is allowed and used.
					if (Objects.equals(pkt.name, "") && ConfigHolder.COMMON.allowInviteAll.get()) {
						ctx.getSender().server.getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
							stats.party.Invite(ctx.getSender(), serverPlayerEntity); // Invite player.
						});
					}
					else {
						// Invite the player supplied in the charsequence to your party.
						stats.party.Invite(ctx.getSender(), ctx.getSender().server.getPlayerList().getPlayerByName(pkt.name));
					}
					break;

				case KICK:
                    assert stats != null;
                    if (!stats.InParty()) return;
					if (stats.player != stats.party.leader) return;

					stats.party.Leave(ctx.getSender().server.getPlayerList().getPlayerByName(pkt.name));
					break;

				case LEADER:
                    assert stats != null;
                    if (!stats.InParty()) return;
					if (stats.player != stats.party.leader) return;

					stats.party.MakeLeader(Objects.requireNonNull(ctx.getSender().server.getPlayerList().getPlayerByName(pkt.name))); // Set a new leader.
					break;

				case DISBAND:
                    assert stats != null;
                    if (!stats.InParty()) return;
					if (stats.player != stats.party.leader) return;

					stats.party.Disband();
					break;

				case LEAVE:
                    assert stats != null;
                    if (!stats.InParty()) return;
					stats.Leave();
					break;

				case ACCEPT:
                    assert stats != null;
                    if (stats.partyInvite == null) return;
					stats.partyInvite.Join(stats.player, true);
					break;

				case DENY:
                    assert stats != null;
                    if (stats.partyInvite == null) return;
					stats.partyInvite = null;
					break;
			}
		}
	}
}