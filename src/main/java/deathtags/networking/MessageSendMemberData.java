package deathtags.networking;

import java.util.function.Supplier;

import com.google.common.base.Charsets;

import deathtags.core.MMOParties;
import deathtags.stats.Party;
import deathtags.stats.PartyMemberData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSendMemberData {

  private PartyPacketDataBuilder builder;
  
  public MessageSendMemberData() {

  }

  public MessageSendMemberData(PartyPacketDataBuilder data) 
  {
	  this.builder = data;
  }

  public static MessageSendMemberData decode(PacketBuffer buf) 
  {
	  return new MessageSendMemberData( new PartyPacketDataBuilder()
			  .SetPlayer(buf.readCharSequence(buf.readInt(), Charsets.UTF_8).toString())
			  .SetHealth(buf.readFloat())
			  .SetMaxHealth(buf.readFloat())
			  .SetArmor(buf.readFloat())
			  .SetLeader(buf.readBoolean())
			  .SetAbsorption(buf.readFloat())
			  .SetShields(buf.readFloat())
			  .SetMaxShields(buf.readFloat())
			  .SetHunger(buf.readFloat()));
			 
  }

  public static void encode(MessageSendMemberData msg, PacketBuffer buf) 
  {
	  buf.writeInt(msg.builder.nameLength);
	  buf.writeCharSequence(msg.builder.playerId, Charsets.UTF_8);
	  buf.writeFloat(msg.builder.health);
	  buf.writeFloat(msg.builder.maxHealth);
	  buf.writeFloat(msg.builder.armor);
	  buf.writeBoolean(msg.builder.leader);
	  buf.writeFloat(msg.builder.absorption);
	  buf.writeFloat(msg.builder.shields);
	  buf.writeFloat(msg.builder.maxShields);
	  buf.writeFloat(msg.builder.hunger);
  }

  public static class Handler {

    public static void handle(MessageSendMemberData message, Supplier<NetworkEvent.Context> ctx) {
    	System.out.println("Recieved party update message");
    	
    		PartyMemberData player = new PartyMemberData(message.builder);
    		System.out.println(player.name);
    		
    		if (MMOParties.localParty == null)
    			MMOParties.localParty = new Party();
    		
    		MMOParties.localParty.data.put(player.name, player);
    	}
    	
  	}
}