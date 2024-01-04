package deathtags.networking;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

import com.google.common.base.Charsets;

import deathtags.core.MMOParties;
import deathtags.stats.Party;
import deathtags.stats.PartyMemberData;
import deathtags.stats.PlayerStats;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

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

  public static MessageSendMemberData decode(ByteBuf buf)
  {
	  MessageSendMemberData data = new MessageSendMemberData( new PartyPacketDataBuilder()
			  .SetName(buf.readCharSequence(buf.readInt(), Charsets.UTF_8).toString()));

	  data.remove = buf.readBoolean();

	  // Instantiate builders
	  // Creates a new instance of the builder for each party member.
	  for (int i = 0; i < PartyPacketDataBuilder.builderData.size(); i++) {
		  Class<? extends BuilderData> aClass = (PartyPacketDataBuilder.builderData.get(i)).getClass();
		  try {
			  BuilderData builder = aClass.getDeclaredConstructor().newInstance();
			  builder.OnRead(buf);
			  data.builder.AddData(i, builder);
		  } catch (InstantiationException e) {
			  throw new RuntimeException(e);
		  } catch (IllegalAccessException e) {
			  throw new RuntimeException(e);
		  } catch (InvocationTargetException e) {
              throw new RuntimeException(e);
          } catch (NoSuchMethodException e) {
              throw new RuntimeException(e);
          }
      }

	  return data;
  }

  public static void encode(MessageSendMemberData msg, ByteBuf buf)
  {
	  buf.writeInt(msg.builder.nameLength);
	  buf.writeCharSequence(msg.builder.playerId, Charsets.UTF_8);
	  buf.writeBoolean(msg.remove);

	  PlayerStats stats = MMOParties.GetStats(msg.builder.player);

	  stats.party.data.get(msg.builder.player.getName().getString())
			  .additionalData = new BuilderData[PartyPacketDataBuilder.builderData.size()];

	  for (int index = 0; index < PartyPacketDataBuilder.builderData.size(); index++) {
		  BuilderData builderData = PartyPacketDataBuilder.builderData.get(index);
		  MMOParties.GetStats(msg.builder.player).party.data.get(msg.builder.player.getName().getString())
				  .additionalData[index] = builderData;

		  builderData.OnWrite(buf, msg.builder.player);
		  System.out.println("Wrote packet");
	  }

	  System.out.println(buf.array().toString());
  }

  public static class Handler {
    public static void handle(MessageSendMemberData message, CustomPayloadEvent.Context ctx) {
		System.out.println("Packet");
		PartyMemberData player = new PartyMemberData(message.builder);

		if (MMOParties.localParty == null) // Create a new party if one doesn't exist already.
			MMOParties.localParty = new Party();

		// Remove this data and clear it out.
		if (message.remove) {
			MMOParties.localParty.data.remove(player.name);
			System.out.println(MMOParties.localParty.data.size());
			ctx.setPacketHandled(true);
			return;
		}

		MMOParties.localParty.data.put(player.name, player);
		ctx.setPacketHandled(true);
	}
  }
}