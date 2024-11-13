package dev.matthe815.mmoparties.fabric.networking;

import com.google.common.base.Charsets;
import dev.matthe815.mmoparties.common.core.MMOPartiesCommon;
import dev.matthe815.mmoparties.fabric.core.MMOParties;

import dev.matthe815.mmoparties.common.gui.screens.InvitedScreenCommon;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Handles receiving an invite.
 * Allows the user to view it when they open their party menu.
 * @since 2.4.0
 */
public class MessagePartyInvite {
	public static ResourceLocation ID = new ResourceLocation(MMOPartiesCommon.MODID, "message_party_invite");
	public final String inviter;

	public MessagePartyInvite(String inviter)
	{
		this.inviter = inviter;
	}

	public static MessagePartyInvite decode(ByteBuf buf) {
		return new MessagePartyInvite((String)buf.readCharSequence(buf.readInt(), Charsets.UTF_8));
	}

	public static FriendlyByteBuf encode(String inviter) {
		FriendlyByteBuf packet = PacketByteBufs.create();
		packet.writeInt(inviter.length());
		packet.writeCharSequence(inviter, Charsets.UTF_8);
		return packet;
	}

	public static void registerHandler() {
		// Register the message handler for the client-to-server
		ClientPlayNetworking.registerGlobalReceiver(ID,
				(client, handler, buf, responseSender) -> {
					MessagePartyInvite message = decode(buf);
					client.execute(() -> {
						MMOParties.partyInviter = message.inviter;
						InvitedScreenCommon.ShowToast();
					});
				});
	}
}