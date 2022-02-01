package deathtags.networking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import deathtags.core.MMOParties;
import deathtags.stats.Alliance;
import deathtags.stats.Party;
import deathtags.stats.PartyMemberData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageAllianceSendMemberData implements IMessage {

  private String data;
  private int party;
  
  public MessageAllianceSendMemberData() {

  }

  public MessageAllianceSendMemberData(String data, int party) 
  {
	  this.data = data;
	  this.party = party;
  }

  @Override
  public void fromBytes(ByteBuf buf) 
  {
	  this.data = new PacketBuffer(buf).readString(1000);
	  this.party = new PacketBuffer(buf).readInt();
  }

  @Override
  public void toBytes(ByteBuf buf) 
  {
	  buf = new PacketBuffer(buf).writeString(data);
	  buf = new PacketBuffer(buf).writeInt(party);
  }

  public static class Handler implements IMessageHandler<MessageAllianceSendMemberData, IMessage> {

    @Override
    public IMessage onMessage(MessageAllianceSendMemberData message, MessageContext ctx) {
    	System.out.println("Recieved alliance update message");
    	
    	if (ctx.side == Side.CLIENT) {
    		List<String> data = new ArrayList<String>(Arrays.asList(message.data.split(",")));
    		System.out.println("Health: " + data.get(1));
    		
    		
    		PartyMemberData player = new PartyMemberData(data.get(1), data.get(2), data.get(3), data.get(4), data.get(5), data.get(6) != null ? data.get(6) : "0", data.get(7) != null ? data.get(7) : "0");
    		
    		if (MMOParties.localParty == null)
    			MMOParties.localParty = new Party();
    		
    		if (MMOParties.localParty.alliance == null)
    			MMOParties.localParty.alliance = new Alliance();
    		
    		if (!MMOParties.localParty.local_parties.containsKey(message.party))
    			MMOParties.localParty.local_parties.put(message.party, new Party());
    		
    		MMOParties.localParty.local_parties.get(message.party).data.put(data.get(0), player);
    	}
    	
    	return null;
    }
  	}
}