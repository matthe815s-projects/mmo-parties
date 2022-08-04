package deathtags.networking;

import com.google.common.base.Charsets;
import deathtags.config.ConfigHolder;
import deathtags.core.MMOParties;
import deathtags.stats.Party;
import deathtags.stats.PlayerStats;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class MessageGUIInvitePlayer {

	public final String name;
	public final EnumPartyGUIAction action;

	public MessageGUIInvitePlayer(CharSequence charSequence, EnumPartyGUIAction action)
	{
		this.name = charSequence.toString();
		this.action = action;
	}

	public static MessageGUIInvitePlayer decode(PacketBuffer buf)
	{
		return new MessageGUIInvitePlayer(buf.readCharSequence(buf.readInt(), Charsets.UTF_8), EnumPartyGUIAction.values()[buf.readInt()]);
	}

	public static void encode(MessageGUIInvitePlayer msg, PacketBuffer buf)
	{
		buf.writeInt(msg.name.length());
		buf.writeCharSequence(msg.name, Charsets.UTF_8);
		buf.writeInt(msg.action.ordinal());
	}

	public static class Handler
	{
		public static void handle(final MessageGUIInvitePlayer pkt, Supplier<NetworkEvent.Context> ctx)
		{
			System.out.println("Party invite GUI interaction received");
			System.out.println(pkt);

			PlayerStats stats = MMOParties.GetStatsByName(ctx.get().getSender().getName().getString());

			switch (pkt.action) {
				case INVITE:
					if (!stats.InParty()) stats.party = new Party(ctx.get().getSender()); // Start new party with leader.

					// If invite all is allowed and used.
					if (pkt.name == "" && ConfigHolder.COMMON.allowInviteAll.get()) {
						ctx.get().getSender().server.getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
							stats.party.Invite(ctx.get().getSender(), serverPlayerEntity); // Invite player.
						});
					}else {
						stats.party.Invite(ctx.get().getSender(), ctx.get().getSender().server.getPlayerList().getPlayerByName(pkt.name)); // Invite player.
					}
					break;
				case KICK:
					if (!stats.InParty()) return; // No-op
					stats.party.Leave(ctx.get().getSender().server.getPlayerList().getPlayerByName(pkt.name)); // Remove a player.
					break;
				case LEADER:
					if (!stats.InParty()) return; // No-op
					stats.party.MakeLeader(ctx.get().getSender().server.getPlayerList().getPlayerByName(pkt.name)); // Set a new leader.
					break;
				case DISBAND:
					if (!stats.InParty()) return; // No-op.
					stats.party.Disband();
					break;
				case LEAVE:
					if (!stats.InParty()) return;
					stats.Leave(); // Leave the party
					break;
			}

		}
	}
}