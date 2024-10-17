package dev.matthe815.mmoparties.forge.networking;

import java.util.function.Supplier;

import dev.matthe815.mmoparties.common.gui.screens.PartyScreen;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

/**
 * A packet to be sent from the server to a client with a request to open the party UI.
 * Cannot be sent to server from client.
 * @since 2.2.0
 */
public class MessageOpenUI {
	public MessageOpenUI() {}
	public static MessageOpenUI decode(ByteBuf buf) { return new MessageOpenUI(); }
	public static void encode(MessageOpenUI msg, ByteBuf buf) {}

	public static class Handler
	{
		@OnlyIn(Dist.CLIENT)
		public static void handle(final MessageOpenUI pkt, Supplier<NetworkEvent.Context> supplier)
		{
			NetworkEvent.Context ctx = supplier.get();
			// if (!ctx.isClientSide()) return; // Only allow from server.
			// TODO: Limit to server-side only. Somehow...
			Minecraft.getInstance().setScreen(new PartyScreen());
			ctx.setPacketHandled(true);
		}

		/**
		 * Required for the packet to function, but performs no operation.
		 */
		public static void handleServer(final MessageOpenUI pkt, Supplier<NetworkEvent.Context> ctx) {}
	}
}