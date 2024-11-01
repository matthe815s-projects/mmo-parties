package dev.matthe815.mmoparties.forge.networking;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import dev.matthe815.mmoparties.forge.core.MMOParties;
import dev.matthe815.mmoparties.common.stats.Party;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.commons.io.Charsets;

public class MessageUpdateParty {

	public final String members;


	public MessageUpdateParty(CharSequence charSequence)
	{
		this.members = charSequence.toString();
	}

	public static MessageUpdateParty decode(PacketBuffer buf)
	{
		return new MessageUpdateParty(buf.readCharSequence(buf.readInt(), Charsets.UTF_8));
	}

	public static void encode(MessageUpdateParty msg, PacketBuffer buf)
	{
		buf.writeInt(msg.members.length());
		buf.writeCharSequence(msg.members, Charsets.UTF_8);
	}

	public static class Handler
	{
		public static void handle(final MessageUpdateParty pkt, Supplier<NetworkEvent.Context> ctx)
		{
			List<String> players = new ArrayList<String>(Arrays.asList(pkt.members.split(",")));

			if (MMOParties.localParty == null)
				MMOParties.localParty = new Party();

			MMOParties.localParty.local_players = players;

			if (pkt.members.equals("")) MMOParties.localParty = null;
			ctx.get().setPacketHandled(true);
		}
	}
}