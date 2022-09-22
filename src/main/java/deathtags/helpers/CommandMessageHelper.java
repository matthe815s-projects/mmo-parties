package deathtags.helpers;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandMessageHelper {

package deathtags.helpers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;

	public class CommandMessageHelper {

		/**
		 * Send a gray information chat to a player.
		 * @param player A server player.
		 * @param message The message to send.
		 */
		public static void SendInfo ( PlayerEntity player, String message, String... arguments )
		{
			player.displayClientMessage(
					new TranslationTextComponent( message, arguments )
					, false
			);
		}

		public static void SendInfoWithButton ( PlayerEntity player, String message, String... arguments )
		{
			TranslationTextComponent component = new TranslationTextComponent( message, arguments );

			IFormattableTextComponent button = new StringTextComponent(" [ACCEPT]").setStyle(
					Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept")).withColor(Color.parseColor("#FFAA00"))
			);

			IFormattableTextComponent button2 = new StringTextComponent(" [DENY]").setStyle(
					Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny")).withColor(Color.parseColor("#FF5555"))
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
		public static void SendError ( PlayerEntity player, String message, String... arguments )
		{
			player.displayClientMessage(
					new TranslationTextComponent( message, arguments )
							.setStyle(
									Style.EMPTY.withColor(TextFormatting.RED)
							), false
			);
		}
	
}
