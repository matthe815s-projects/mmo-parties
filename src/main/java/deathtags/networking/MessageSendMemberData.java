package deathtags.networking;

import java.nio.charset.Charset;

import org.apache.commons.lang3.CharSet;

import com.google.common.base.Charsets;

import deathtags.core.MMOParties;
import deathtags.stats.Party;
import deathtags.stats.PartyMemberData;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSendMemberData implements IMessage {

  private PartyPacketDataBuilder builder;
  
  public MessageSendMemberData() {

  }

  public MessageSendMemberData(PartyPacketDataBuilder data) 
  {
	  this.builder = data;
  }

  @Override
  public void fromBytes(ByteBuf buf) 
  {
	  this.builder = new PartyPacketDataBuilder()
			  .SetPlayer(buf.readCharSequence(buf.readInt(), Charsets.UTF_8).toString())
			  .SetHealth(buf.readFloat())
			  .SetMaxHealth(buf.readFloat())
			  .SetArmor(buf.readFloat())
			  .SetLeader(buf.readBoolean())
			  .SetAbsorption(buf.readFloat())
			  .SetShields(buf.readFloat())
			  .SetMaxShields(buf.readFloat())
			  .SetHunger(buf.readFloat());
			 
  }

  @Override
  public void toBytes(ByteBuf buf) 
  {
	  buf.writeInt(builder.nameLength);
	  buf.writeBytes(builder.playerId.getBytes());
	  buf.writeFloat(builder.health);
	  buf.writeFloat(builder.maxHealth);
	  buf.writeFloat(builder.armor);
	  buf.writeBoolean(builder.leader);
	  buf.writeFloat(builder.absorption);
	  buf.writeFloat(builder.shields);
	  buf.writeFloat(builder.maxShields);
	  buf.writeFloat(builder.hunger);
  }

  public static class Handler implements IMessageHandler<MessageSendMemberData, IMessage> {

    @Override
    public IMessage onMessage(MessageSendMemberData message, MessageContext ctx) {
    	if (ctx.side == Side.CLIENT) {
    		PartyMemberData player = new PartyMemberData(message.builder);

    		if (MMOParties.localParty == null)
    			MMOParties.localParty = new Party();
    		
    		MMOParties.localParty.data.put(player.name, player);
    	}
    	
    	return null;
    }
  	}
}