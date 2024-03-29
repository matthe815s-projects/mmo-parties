package deathtags.networking;

import com.google.common.base.Charsets;
import com.mojang.brigadier.Message;
import deathtags.core.MMOParties;

import deathtags.gui.screens.InvitedScreen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

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
			InvitedScreen.ShowToast();
			ctx.get().setPacketHandled(true);
		}
	}
}