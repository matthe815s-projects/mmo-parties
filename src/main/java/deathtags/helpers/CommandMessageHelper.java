package deathtags.helpers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

public class CommandMessageHelper {

	/**
	 * Send a gray information chat to a player.
	 * @param player A server player.
	 * @param message The message to send.
	 */
	public static void SendInfo (Player player, String message )
	{
		player.displayClientMessage( 
			new TextComponent( message )
			, false
		);
	}
	
	/**
	 * Send a gray error chat to a player.
	 * @param player A server player.
	 * @param message The message to send.
	 */
	public static void SendError ( Player player, String message )
	{
		player.displayClientMessage( 
			new TextComponent( message )
			.setStyle( 
					Style.EMPTY.withColor(ChatFormatting.RED)
			), false
		);
	}
	
}
