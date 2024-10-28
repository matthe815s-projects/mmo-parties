package dev.matthe815.mmoparties.forge.networking;

import com.google.common.base.Charsets;
import dev.matthe815.mmoparties.forge.core.MMOParties;
import dev.matthe815.mmoparties.forge.screens.InvitedScreenForge;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessagePartyInvite implements IMessage {

	String inviter;

	public MessagePartyInvite() {}

	public MessagePartyInvite(String inviter)
	{
		this.inviter = inviter;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.inviter = (String) buf.readCharSequence(buf.readInt(), Charsets.UTF_8);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(inviter.length());
		buf.writeCharSequence(inviter, Charsets.UTF_8);
	}

	public static class Handler implements IMessageHandler<MessagePartyInvite, IMessage> {

		@Override
		public IMessage onMessage(MessagePartyInvite message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				MMOParties.partyInviter = message.inviter;
				InvitedScreenForge.ShowToast();
			}

			return null;
		}
	}
}