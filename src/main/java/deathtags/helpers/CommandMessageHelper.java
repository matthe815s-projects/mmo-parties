package deathtags.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;

public class CommandMessageHelper {

	/**
	 * Send a gray information chat to a player.
	 * @param player A server player.
	 * @param message The message to send.
	 */
	public static void SendInfo (EntityPlayer player, String message, String... arguments )
	{
		player.sendMessage(
				new TextComponentTranslation( message, arguments )
		);
	}

	public static void SendInfoWithButton ( EntityPlayer player, String message, String... arguments )
	{
		TextComponentTranslation component = new TextComponentTranslation( message, arguments );

		ITextComponent button = new TextComponentString(" [ACCEPT]").setStyle(
				new Style().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept")).setColor(TextFormatting.GREEN)
		);

		ITextComponent button2 = new TextComponentString(" [DENY]").setStyle(
				new Style().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny")).setColor(TextFormatting.RED)
		);

		component.appendSibling(button);
		component.appendSibling(button2);

		player.sendMessage(
				component
		);
	}

	/**
	 * Send a gray error chat to a player.
	 * @param player A server player.
	 * @param message The message to send.
	 */
	public static void SendError ( EntityPlayer player, String message, String... arguments )
	{
		player.sendMessage(
				new TextComponentTranslation( message, arguments )
						.setStyle(
								new Style().setColor(TextFormatting.RED)
						)
		);
	}
	
}
