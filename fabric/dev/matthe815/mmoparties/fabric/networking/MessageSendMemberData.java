package dev.matthe815.mmoparties.fabric.networking;

import java.lang.reflect.InvocationTargetException;

import com.google.common.base.Charsets;

import dev.matthe815.mmoparties.common.core.MMOPartiesCommon;
import dev.matthe815.mmoparties.common.networking.PartyPacketDataBuilder;
import dev.matthe815.mmoparties.common.networking.builders.BuilderData;
import dev.matthe815.mmoparties.fabric.core.MMOParties;
import dev.matthe815.mmoparties.common.stats.Party;
import dev.matthe815.mmoparties.common.stats.PartyMemberData;
import dev.matthe815.mmoparties.common.stats.PlayerStats;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class MessageSendMemberData {
	public static ResourceLocation ID = new ResourceLocation(MMOPartiesCommon.MODID, "message_send_data");
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
		    } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
			    throw new RuntimeException(e);
		    }
        }

	    return data;
    }

    public static FriendlyByteBuf encode(PartyPacketDataBuilder builder, boolean remove)
    {
		FriendlyByteBuf packet = PacketByteBufs.create();
		packet.writeInt(builder.nameLength);
		packet.writeCharSequence(builder.playerId, Charsets.UTF_8);
		packet.writeBoolean(remove);

	    PlayerStats stats = MMOParties.GetStatsByName(builder.player.getName().getString());

	    stats.party.data.get(builder.player.getName().getString())
			    .additionalData = new BuilderData[PartyPacketDataBuilder.builderData.size()];

	    for (int index = 0; index < PartyPacketDataBuilder.builderData.size(); index++) {
		    BuilderData builderData = PartyPacketDataBuilder.builderData.get(index);
		    MMOParties.GetStats(builder.player).party.data.get(builder.player.getName().getString())
				  .additionalData[index] = builderData;

		    builderData.OnWrite(packet, builder.player);
	    }
		return packet;
    }

	public static void registerHandler() {
		// Register the message handler for the client-to-server
		ClientPlayNetworking.registerGlobalReceiver(ID,
				(client, handler, buf, responseSender) -> {
					MessageSendMemberData message = decode(buf);
					PartyMemberData player = new PartyMemberData(message.builder);

					if (MMOParties.localParty == null) // Create a new party if one doesn't exist already.
						MMOParties.localParty = new Party();

					// Remove this data and clear it out.
					if (message.remove) {
						MMOParties.localParty.data.remove(player.name);
						return;
					}

					MMOParties.localParty.data.put(player.name, player);
				});
	}
}