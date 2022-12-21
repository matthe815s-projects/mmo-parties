package deathtags.networking;

import com.google.common.base.Charsets;
import deathtags.core.MMOParties;
import deathtags.gui.screens.InvitedScreen;
import deathtags.gui.screens.PartyScreen;
import deathtags.stats.Party;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Handles receiving an invite
 */
public class MessagePartyInvite {

	public final String inviter;


	public MessagePartyInvite(String inviter)
	{
		this.inviter = inviter;
	}

	public static MessagePartyInvite decode(PacketBuffer buf)
	{
		return new MessagePartyInvite((String)buf.readCharSequence(buf.readInt(), Charsets.UTF_8));
	}

	public static void encode(MessagePartyInvite msg, PacketBuffer buf)
	{
		buf.writeInt(msg.inviter.length());
		buf.writeCharSequence(msg.inviter, Charsets.UTF_8);
	}

	public static class Handler
	{
		public static void handle(final MessagePartyInvite pkt, Supplier<NetworkEvent.Context> ctx)
		{
			MMOParties.partyInviter = pkt.inviter;
			Minecraft.getInstance().setScreen(new InvitedScreen());
			ctx.get().setPacketHandled(true);
		}
	}
}