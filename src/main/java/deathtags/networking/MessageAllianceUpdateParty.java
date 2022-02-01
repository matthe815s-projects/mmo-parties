package deathtags.networking;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import deathtags.core.MMOParties;
import deathtags.stats.Alliance;
import deathtags.stats.Party;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageAllianceUpdateParty implements IMessage {

  private String members;
  public int party;
  
  public MessageAllianceUpdateParty() {

  }

  public MessageAllianceUpdateParty(String members, int party) 
  {
	  this.members = members;
	  this.party = party;
  }

  @Override
  public void fromBytes(ByteBuf buf) 
  {
	  this.members = new PacketBuffer(buf).readString(1000);
	  this.party = new PacketBuffer(buf).readInt();
  }

  @Override
  public void toBytes(ByteBuf buf) 
  {
	  buf = new PacketBuffer(buf).writeString(members);
	  buf = new PacketBuffer(buf).writeInt(party);
  }

  public static class Handler implements IMessageHandler<MessageAllianceUpdateParty, IMessage> {

    @Override
    public IMessage onMessage(MessageAllianceUpdateParty message, MessageContext ctx) {
    	System.out.println("Alliance update message");
    	
    	if (ctx.side == Side.CLIENT) {
    		List<String> players = new ArrayList<String>(Arrays.asList(message.members.split(",")));
    		
    		if (MMOParties.localParty == null)
    			MMOParties.localParty = new Party();
    		
    		if (MMOParties.localParty.alliance == null)
    			MMOParties.localParty.alliance = new Alliance();
    		
    		if (!MMOParties.localParty.local_parties.containsKey(message.party))
    			MMOParties.localParty.local_parties.put(message.party, new Party());
    		
    		MMOParties.localParty.local_parties.get(message.party).local_players = players;
    	}
    	
    	return null;
    }
  	}
}