package deathtags.networking;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import deathtags.core.MMOParties;
import deathtags.stats.Party;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageUpdateParty implements IMessage {

  private String members;
  
  public MessageUpdateParty() {

  }

  public MessageUpdateParty(String members) 
  {
	  this.members = members;
  }

  @Override
  public void fromBytes(ByteBuf buf) 
  {
	  this.members = new PacketBuffer(buf).readString(1000);
  }

  @Override
  public void toBytes(ByteBuf buf) 
  {
	  buf = new PacketBuffer(buf).writeString(members);
  }

  public static class Handler implements IMessageHandler<MessageUpdateParty, IMessage> {

    @Override
    public IMessage onMessage(MessageUpdateParty message, MessageContext ctx) {
    	System.out.println(message.members);
    	
    	if (ctx.side == Side.CLIENT) {
    		List<String> players = new ArrayList<String>(Arrays.asList(message.members.split(",")));
    		
    		if (MMOParties.localParty == null)
    			MMOParties.localParty = new Party();

    		MMOParties.localParty.local_players = players;
            if (message.members == "") MMOParties.localParty = null;
    	}
    	
    	return null;
    }
  	}
}