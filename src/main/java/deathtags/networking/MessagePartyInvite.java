package deathtags.networking;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Charsets;
import deathtags.core.MMOParties;
import deathtags.gui.screens.InvitedScreen;
import deathtags.stats.Party;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
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
            InvitedScreen.ShowToast();
    	}
    	
    	return null;
    }
  	}
}