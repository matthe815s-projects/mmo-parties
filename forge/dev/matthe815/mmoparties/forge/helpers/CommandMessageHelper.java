package dev.matthe815.mmoparties.forge.helpers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;

public class CommandMessageHelper {

	/**
	 * Send a gray information chat to a player.
	 * @param player A server player.
	 * @param message The message to send.
	 */
	public static void SendInfo (PlayerEntity player, String message, String... arguments )
	{
		player.sendMessage(
			new TranslationTextComponent( message, arguments ), player.getUUID()
		);
	}

	public static void SendInfoWithButton ( PlayerEntity player, String message, String... arguments )
	{
		TranslationTextComponent component = new TranslationTextComponent( message, arguments );

		IFormattableTextComponent button = new StringTextComponent(" [ACCEPT]").setStyle(
				Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept")).withColor(TextFormatting.GREEN)
		);

		IFormattableTextComponent button2 = new StringTextComponent(" [DENY]").setStyle(
				Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny")).withColor(TextFormatting.RED)
		);

		component.append(button);
		component.append(button2);

		player.sendMessage(
				component,
				player.getUUID()
		);

	}
	
	/**
	 * Send a gray error chat to a player.
	 * @param player A server player.
	 * @param message The message to send.
	 */
	public static void SendError ( PlayerEntity player, String message, String... args )
	{
		player.sendMessage(
				new TranslationTextComponent( message, args )
						.setStyle(
								Style.EMPTY.withColor(TextFormatting.RED)
						),
				player.getUUID()
		);
	}
	
}
