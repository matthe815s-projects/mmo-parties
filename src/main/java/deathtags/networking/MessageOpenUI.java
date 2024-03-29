package deathtags.networking;

import deathtags.gui.screens.PartyScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * A packet to be sent from the server to a client with a request to open the party UI.
 * Cannot be sent to server from client.
 * @since 2.2.0
 */
public class MessageOpenUI {
	public MessageOpenUI() {}
	public static MessageOpenUI decode(PacketBuffer buf) { return new MessageOpenUI(); }
	public static void encode(MessageOpenUI msg, PacketBuffer buf) {}

	public static class Handler
	{
		@OnlyIn(Dist.CLIENT)
		public static void handle(final MessageOpenUI pkt, Supplier<NetworkEvent.Context> ctx)
		{
			if (!ctx.get().getDirection().equals(NetworkDirection.PLAY_TO_CLIENT)) return; // Only allow from server.
			Minecraft.getInstance().setScreen(new PartyScreen());
			ctx.get().setPacketHandled(true);
		}

		/**
		 * Required for the packet to function, but performs no operation.
		 */
		public static void handleServer(final MessageOpenUI pkt, Supplier<NetworkEvent.Context> ctx) {}
	}
}