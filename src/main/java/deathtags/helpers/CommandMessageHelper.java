package deathtags.helpers;

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
				new TranslatableComponent( message, arguments )
				, false
		);
	}

	public static void SendInfoWithButton ( Player player, String message, String... arguments )
	{
		TranslatableComponent component = new TranslatableComponent( message, arguments );

		TextComponent button = (TextComponent) new TextComponent(" [ACCEPT]").setStyle(
				Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept")).withColor(TextColor.parseColor("#FFAA00"))
		);

		TextComponent button2 = (TextComponent) new TextComponent(" [DENY]").setStyle(
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
	public static void SendError ( Player player, String message, String... arguments )
	{
		player.displayClientMessage(
				new TranslatableComponent( message, arguments )
						.setStyle(
								Style.EMPTY.withColor(TextColor.parseColor("#FF0000"))
						), false
		);
	}

}