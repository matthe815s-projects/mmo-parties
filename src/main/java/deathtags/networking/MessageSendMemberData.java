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
  boolean remove = false;
  
  public MessageSendMemberData() {

  }

  public MessageSendMemberData(PartyPacketDataBuilder data)
  {
		this.builder = data;
  }

  public MessageSendMemberData(PartyPacketDataBuilder data, boolean remove)
  {
	  this.builder = data;
	  this.remove = remove;
  }

  @Override
  public void fromBytes(ByteBuf buf) 
  {
	  builder = new PartyPacketDataBuilder()
			  .SetName(buf.readCharSequence(buf.readInt(), Charsets.UTF_8).toString());

	  remove = buf.readBoolean();

	  // Instantiate builders
	  // Creates a new instance of the builder for each party member.
	  for (int i = 0; i < PartyPacketDataBuilder.builderData.size(); i++) {
		  Class<? extends BuilderData> aClass = (PartyPacketDataBuilder.builderData.get(i)).getClass();
		  try {
			  BuilderData builderData = aClass.newInstance();
			  builderData.OnRead(buf);
			  builder.AddData(i, builderData);
		  } catch (InstantiationException e) {
			  throw new RuntimeException(e);
		  } catch (IllegalAccessException e) {
			  throw new RuntimeException(e);
		  }
	  }
  }

  @Override
  public void toBytes(ByteBuf buf) 
  {
	  buf.writeInt(builder.nameLength);
	  buf.writeCharSequence(builder.playerId, Charsets.UTF_8);
	  buf.writeBoolean(remove);

	  PartyPacketDataBuilder.builderData.forEach(builderData -> {
		  builderData.OnWrite(buf, builder.player);
	  });
  }

  public static class Handler implements IMessageHandler<MessageSendMemberData, IMessage> {

    @Override
    public IMessage onMessage(MessageSendMemberData message, MessageContext ctx) {
    	if (ctx.side == Side.CLIENT) {
    		PartyMemberData player = new PartyMemberData(message.builder);

    		if (MMOParties.localParty == null)
    			MMOParties.localParty = new Party();

			// Remove this data and clear it out.
			if (message.remove) {
				MMOParties.localParty.data.remove(player.name);
				System.out.println("Remove player " + player.name);
				System.out.println(MMOParties.localParty.data.size());
				return null;
			}

			MMOParties.localParty.data.put(player.name, player);
    	}
    	
    	return null;
    }
  	}
}