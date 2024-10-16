package dev.matthe815.mmoparties.forge.helpers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.player.Player;

public class CommandMessageHelper {

	/**
	 * Send a gray information chat to a player.
	 * @param player A server player.
	 * @param message The message to send.
	 */
	public static void SendInfo (Player player, String message, String... arguments )
	{
		player.displayClientMessage( 
			Component.translatable( message, arguments )
			, false
		);
	}

	public static void SendInfoWithButton ( Player player, String message, String... arguments )
	{
		MutableComponent component = Component.translatable( message, arguments );

		MutableComponent button = Component.literal(" [ACCEPT]").setStyle(
				Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept")).withColor(ChatFormatting.GREEN)
		);

		MutableComponent button2 = Component.literal(" [DENY]").setStyle(
				Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny")).withColor(ChatFormatting.RED)
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
	public static void SendError ( Player player, String message, String... arguments )
	{
		player.displayClientMessage( 
			Component.translatable( message, arguments )
			.setStyle( 
					Style.EMPTY.withColor(ChatFormatting.RED)
			), false
		);
	}
	
}
