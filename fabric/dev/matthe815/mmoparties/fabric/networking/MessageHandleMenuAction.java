package dev.matthe815.mmoparties.fabric.networking;

import com.google.common.base.Charsets;
import dev.matthe815.mmoparties.common.core.MMOPartiesCommon;
import dev.matthe815.mmoparties.fabric.core.MMOParties;
import dev.matthe815.mmoparties.common.stats.Party;
import dev.matthe815.mmoparties.common.stats.PlayerStats;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Handles menu actions for the popup GUI.
 * Does not skip permission checks normally performed within commands.
 * @since 2.2.0
 */
public class MessageHandleMenuAction {
	public static ResourceLocation ID = new ResourceLocation(MMOPartiesCommon.MODID, "message_handle_menu");

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

	public static FriendlyByteBuf encode(String name, EnumPartyGUIAction action)
	{
		FriendlyByteBuf buffer = PacketByteBufs.create();
		buffer.writeInt(name.length());
		buffer.writeCharSequence(name, Charsets.UTF_8);
		buffer.writeInt(action.ordinal());
		return buffer;
	}

	public static void registerHandler() {
		ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, responseSender) -> {
			PlayerStats stats = MMOParties.GetStatsByName(player.getName().getString());
			MessageHandleMenuAction message = decode(buf);
			switch (message.action) {
				case INVITE:
					// Start a new party if one doesn't already exist.
                    assert stats != null;
                    if (!stats.InParty()) stats.party = new Party(player);
					if (stats.player != stats.party.leader) return;

					stats.party.Invite(player, server.getPlayerList().getPlayerByName(message.name));
					break;

				case KICK:
					assert stats != null;
					if (!stats.InParty()) return;
					if (stats.player != stats.party.leader) return;

					stats.party.Leave(server.getPlayerList().getPlayerByName(message.name));
					break;

				case LEADER:
					assert stats != null;
					if (!stats.InParty()) return;
					if (stats.player != stats.party.leader) return;

					stats.party.MakeLeader(Objects.requireNonNull(server.getPlayerList().getPlayerByName(message.name))); // Set a new leader.
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
					stats.partyInvite.Join(player, true);
					break;

				case DENY:
					assert stats != null;
					if (stats.partyInvite == null) return;
					stats.partyInvite = null;
					break;
			}
		});
	}
}