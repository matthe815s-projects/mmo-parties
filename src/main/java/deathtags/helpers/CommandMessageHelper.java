package deathtags.helpers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
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

	public static void SendInfoWithButton ( Player player, String message )
	{
		TextComponent component = new TextComponent( message );

		MutableComponent button = new TextComponent(" [ACCEPT]").setStyle(
				Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept")).withColor(TextColor.parseColor("#FFAA00"))
		);

		MutableComponent button2 = new TextComponent(" [DENY]").setStyle(
				Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny")).withColor(TextColor.parseColor("#FF5555"))
		);

		component.append(button);
		component.append(button2);

		player.displayClientMessage(
				component
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
