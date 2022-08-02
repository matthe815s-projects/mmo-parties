package deathtags.networking;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.base.Charsets;

import deathtags.core.MMOParties;
import deathtags.stats.Party;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

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
			System.out.println("Party update message");
			System.out.println(pkt.members);

			List<String> players = new ArrayList<String>(Arrays.asList(pkt.members.split(",")));
			System.out.println(players.size());

			if (MMOParties.localParty == null)
				MMOParties.localParty = new Party();

			MMOParties.localParty.local_players = players;

			if (pkt.members == "") MMOParties.localParty = null;
		}
	}
}