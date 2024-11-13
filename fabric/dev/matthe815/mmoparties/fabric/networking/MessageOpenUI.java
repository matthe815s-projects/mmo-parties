package dev.matthe815.mmoparties.fabric.networking;

import dev.matthe815.mmoparties.common.core.MMOPartiesCommon;
import dev.matthe815.mmoparties.common.gui.screens.PartyScreen;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

/**
 * A packet to be sent from the server to a client with a request to open the party UI.
 * Cannot be sent to server from client.
 * @since 2.2.0
 */
public class MessageOpenUI {
	public static ResourceLocation ID = new ResourceLocation(MMOPartiesCommon.MODID, "message_open_ui");
	public MessageOpenUI() {}
	public static MessageOpenUI decode(ByteBuf buf) { return new MessageOpenUI(); }
	public static void encode() {}

	public static void registerHandler() {
		// Register the message handler for the client-to-server
		ClientPlayNetworking.registerGlobalReceiver(ID,
				(client, handler, buf, responseSender) -> {
					MessageOpenUI message = decode(buf);
					client.execute(() -> {
						Minecraft.getInstance().setScreen(new PartyScreen());
					});
				});
	}
}