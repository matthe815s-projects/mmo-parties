package dev.matthe815.mmoparties.forge.networking;

import com.google.common.base.Charsets;
import dev.matthe815.mmoparties.forge.core.MMOParties;
import dev.matthe815.mmoparties.forge.screens.InvitedScreenForge;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

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
			InvitedScreenForge.ShowToast();
			ctx.get().setPacketHandled(true);
		}
	}
}