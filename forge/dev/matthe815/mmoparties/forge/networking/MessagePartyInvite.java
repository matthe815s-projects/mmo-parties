package dev.matthe815.mmoparties.forge.networking;

import java.util.function.Supplier;

import com.google.common.base.Charsets;
import dev.matthe815.mmoparties.forge.core.MMOParties;

import dev.matthe815.mmoparties.common.gui.screens.InvitedScreenCommon;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.network.NetworkEvent;

/**
 * Handles receiving an invite.
 * Allows the user to view it when they open their party menu.
 * @since 2.4.0
 */
public class MessagePartyInvite {

	public final String inviter;


	public MessagePartyInvite(String inviter)
	{
		this.inviter = inviter;
	}

	public static MessagePartyInvite decode(ByteBuf buf)
	{
		return new MessagePartyInvite((String)buf.readCharSequence(buf.readInt(), Charsets.UTF_8));
	}

	public static void encode(MessagePartyInvite msg, ByteBuf buf)
	{
		buf.writeInt(msg.inviter.length());
		buf.writeCharSequence(msg.inviter, Charsets.UTF_8);
	}

	public static class Handler
	{
		public static void handle(final MessagePartyInvite pkt, Supplier<NetworkEvent.Context> supplier)
		{
			NetworkEvent.Context ctx = supplier.get();
			MMOParties.partyInviter = pkt.inviter;
			InvitedScreenCommon.ShowToast();
			ctx.setPacketHandled(true);
		}
	}
}