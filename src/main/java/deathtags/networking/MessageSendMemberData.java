package deathtags.networking;

import java.util.function.Supplier;

import com.google.common.base.Charsets;

import com.mojang.brigadier.Message;
import deathtags.core.MMOParties;
import deathtags.stats.Party;
import deathtags.stats.PartyMemberData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSendMemberData {

  private PartyPacketDataBuilder builder;
  private boolean remove = false;
  
  public MessageSendMemberData() {}

  public MessageSendMemberData(PartyPacketDataBuilder data)
  {
	  this.builder = data;
  }

  public MessageSendMemberData(PartyPacketDataBuilder data, boolean remove)
  {
		this.builder = data;
		this.remove = remove;
  }

  public static MessageSendMemberData decode(PacketBuffer buf)
  {
	  MessageSendMemberData data = new MessageSendMemberData( new PartyPacketDataBuilder()
			  .SetName(buf.readCharSequence(buf.readInt(), Charsets.UTF_8).toString()));

	  data.remove = buf.readBoolean();

	  // Instantiate builders
	  // Creates a new instance of the builder for each party member.
	  for (int i = 0; i < PartyPacketDataBuilder.builderData.size(); i++) {
		  Class<? extends BuilderData> aClass = (PartyPacketDataBuilder.builderData.get(i)).getClass();
		  try {
			  BuilderData builder = aClass.newInstance();
			  builder.OnRead(buf);
			  data.builder.AddData(i, builder);
		  } catch (InstantiationException e) {
			  throw new RuntimeException(e);
		  } catch (IllegalAccessException e) {
			  throw new RuntimeException(e);
		  }
	  }

	  return data;
  }

  public static void encode(MessageSendMemberData msg, PacketBuffer buf) 
  {
	  buf.writeInt(msg.builder.nameLength);
	  buf.writeCharSequence(msg.builder.playerId, Charsets.UTF_8);
	  buf.writeBoolean(msg.remove);

	  PartyPacketDataBuilder.builderData.forEach(builderData -> {
		builderData.OnWrite(buf, msg.builder.player);
	  });
  }

  public static class Handler {
    public static void handle(MessageSendMemberData message, Supplier<NetworkEvent.Context> ctx) {
		PartyMemberData player = new PartyMemberData(message.builder);

		if (MMOParties.localParty == null) // Create a new party if one doesn't exist already.
			MMOParties.localParty = new Party();

		// Remove this data and clear it out.
		if (message.remove) {
			MMOParties.localParty.data.remove(player.name);
			System.out.println("Remove player " + player.name);
			System.out.println(MMOParties.localParty.data.size());
			ctx.get().setPacketHandled(true);
			return;
		}

		MMOParties.localParty.data.put(player.name, player);
		ctx.get().setPacketHandled(true);
	}
  }
}