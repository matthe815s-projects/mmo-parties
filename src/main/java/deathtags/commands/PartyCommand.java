package deathtags.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import deathtags.config.ConfigHolder;
import deathtags.core.MMOParties;
import deathtags.core.events.EventClient;
import deathtags.helpers.CommandMessageHelper;
import deathtags.networking.MessageOpenUI;
import deathtags.stats.Party;
import deathtags.stats.PlayerStats;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;

import java.util.concurrent.CompletableFuture;

public class PartyCommand {

	public static LiteralArgumentBuilder<CommandSourceStack> register () {
		return Commands.literal("party")
				.requires(commandSource -> true)
				.then(
						Commands.argument("sub", StringArgumentType.string())
								.executes(ctx -> run(ctx, StringArgumentType.getString(ctx, "sub"), null))
								.suggests((sourceCommandContext, suggestionsBuilder) -> {
									suggestionsBuilder
											.suggest("invite")
											.suggest("accept")
											.suggest("deny")
											.suggest("kick")
											.suggest("leader")
											.suggest("disband") // Build suggestions
											.suggest("gui");

									if (ConfigHolder.COMMON.allowPartyTP.get()) // If you're allowed to party teleport, display the option
										suggestionsBuilder.suggest("tp");

									return suggestionsBuilder.buildFuture();
								})
								.then(
										Commands.argument("player", StringArgumentType.string())
												.executes(ctx -> run(ctx, StringArgumentType.getString(ctx, "sub"), StringArgumentType.getString(ctx, "player")))
												.suggests((sourceCommandContext, suggestionsBuilder) -> getSuggestions(sourceCommandContext, suggestionsBuilder))
								));
	}

	private static CompletableFuture<Suggestions> getSuggestions (CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
		String argument = StringArgumentType.getString(ctx, "sub").trim();

		switch (argument) {
			case "invite":
			case "kick":
			case "leader":
				ctx.getSource().getServer().getPlayerList().getPlayers().forEach(player -> {
					builder.suggest(player.getName().getString());
				});
				break;
		}

		return builder.buildFuture();
	}

	private static int run(CommandContext<CommandSourceStack> context, String sub, String targetStr) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		ServerPlayer target = null;

		if (targetStr != null) context.getSource().getServer().getPlayerList().getPlayerByName(targetStr);

		if (targetStr != null && target == null) { CommandMessageHelper.SendError( player, String.format("The player %s is not online.", targetStr) ); return 0; }
		if (player.getCommandSenderWorld().isClientSide) return 0; // Only perform operations on the server side.

		PlayerStats stats = MMOParties.GetStats( player );

		switch (sub) {
			case "tp":
				if (!ConfigHolder.COMMON.allowPartyTP.get())
				{ CommandMessageHelper.SendError(player, "rpgparties.message.error.teleport.server"); return 0; }
				if (!stats.InParty())
				{ CommandMessageHelper.SendError( player, "rpgparties.message.error.teleport.party" ); return 0; }

				stats.party.Teleport ( player, target );
				break;

			case "kick":
				if (!stats.InParty())
				{ CommandMessageHelper.SendError( player, "rpgparties.message.error.party" ); return 0; }

				stats.party.Leave(target);
				break;

			case "create":
				Party.Create( player ); // Create a new party for the player.
				break;

			case "invite":
				if (targetStr == null) { CommandMessageHelper.SendError(player, "rpgparties.message.error.argument", player.getName().getContents()); return 0; }
				if (!stats.InParty()) Party.Create ( player ); // Create a party to invite with if not existent.

				stats.party.Invite ( player, target ); // Send an invite to the target player.
				break;

			case "accept":
				if (stats.partyInvite == null)
				{ CommandMessageHelper.SendError( player , "rpgparties.message.error.invite" ); return 0; }

				stats.partyInvite.Join(player, true); // Accept an invite to a player.
				break;

			case "deny":
				if (stats.partyInvite == null)
				{ CommandMessageHelper.SendError( player , "rpgparties.message.error.invite" ); return 0; }

				stats.partyInvite = null; // Deny the invite.
				CommandMessageHelper.SendInfo(player, "rpgparties.message.party.deny");
				break;

			case "leave":
				if (!stats.InParty())
				{ CommandMessageHelper.SendError( player, "rpgparties.message.error.party" ); return 0; }

				stats.party.Leave(player); // Perform the leave actions.
				stats.party = null;
				break;

			case "leader":
				if (!stats.InParty())
				{ CommandMessageHelper.SendError( player, "You are not currently in a party." ); return 0; }

				if (stats.party.leader != player) // Only the leader can promote
				{ CommandMessageHelper.SendError( player, "Only the leader may promote members." ); return 0; }

				if (targetStr == null) { CommandMessageHelper.SendError(player, "rpgparties.message.error.argument", player.getName().getContents()); return 0; }

				stats.party.MakeLeader(player);
				break;

			case "disband":
				if (!stats.InParty())
				{ CommandMessageHelper.SendError( player, "rpgparties.message.error.party" ); return 0; }

				if (stats.party.leader != player) // Only the leader can promote.
				{ CommandMessageHelper.SendError( player, "rpgparties.message.error.disband" ); return 0; }

				stats.party.Disband();
				break;

			case "gui":
				if (!player.getCommandSenderWorld().isClientSide) MMOParties.network.sendTo(new MessageOpenUI(), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT); // Send open message
				else if (Minecraft.getInstance().hasSingleplayerServer()) EventClient.openScreen();
				break;

			default:
				break;
		}

		return 0;
	}
}