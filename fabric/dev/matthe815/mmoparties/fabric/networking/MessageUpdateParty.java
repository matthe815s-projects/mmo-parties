package dev.matthe815.mmoparties.fabric.networking;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.matthe815.mmoparties.common.core.MMOPartiesCommon;
import dev.matthe815.mmoparties.fabric.core.MMOParties;
import dev.matthe815.mmoparties.common.stats.Party;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.include.com.google.common.base.Charsets;

public class MessageUpdateParty {
	public static ResourceLocation ID = new ResourceLocation(MMOPartiesCommon.MODID, "message_update_party");
	public final String members;

	// Constructor to store members as a comma-separated string
	public MessageUpdateParty(CharSequence charSequence) {
		this.members = charSequence.toString();
	}

	// Decode method to convert ByteBuf into a MessageUpdateParty object
	public static MessageUpdateParty decode(FriendlyByteBuf buf) {
		return new MessageUpdateParty(buf.readCharSequence(buf.readInt(), Charsets.UTF_8));  // Maximum length for UTF-8 string
	}

	// Encode method to convert the MessageUpdateParty object into a ByteBuf
	public static FriendlyByteBuf encode(String members) {
		FriendlyByteBuf packet = PacketByteBufs.create();
		packet.writeInt(members.length());
		packet.writeCharSequence(members, Charsets.UTF_8);  // Write string to the buffer
		return packet;
	}

	// Register this message handler for the network
	public static void registerHandler() {
		// Register the message handler for the client-to-server
		ClientPlayNetworking.registerGlobalReceiver(ID,
				(client, handler, buf, responseSender) -> {
					MessageUpdateParty message = decode(buf);
					// Handle the received message on the client thread
					List<String> players = new ArrayList<>(Arrays.asList(message.members.split(",")));

					if (MMOParties.localParty == null) {
						MMOParties.localParty = new Party();
					}

					MMOParties.localParty.local_players = players;

					// If no members, nullify the party
					if (message.members.isEmpty()) {
						MMOParties.localParty = null;
					}
				});
	}
}