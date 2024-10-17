package dev.matthe815.mmoparties.forge.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import dev.matthe815.mmoparties.forge.core.MMOParties;
import dev.matthe815.mmoparties.common.events.EventClient;
import dev.matthe815.mmoparties.forge.helpers.CommandMessageHelper;
import dev.matthe815.mmoparties.forge.networking.MessageOpenUI;
import dev.matthe815.mmoparties.forge.networking.MessageUpdateParty;
import dev.matthe815.mmoparties.common.stats.Party;
import dev.matthe815.mmoparties.common.stats.PlayerStats;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.PacketTarget;

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
											.suggest("gui")
											.suggest("pvp");

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

		// When using invite, kick, or leader, it should suggest players to use for these commands.
		// In the case of kick and leader, it'll only suggest players within your party.
		switch (argument) {
			case "invite":
				ctx.getSource().getServer().getPlayerList().getPlayers().forEach(player -> {
					builder.suggest(player.getName().getString());
				});
				break;
			case "kick":
			case "leader":
				Player player = null;
				try {
					player = ctx.getSource().getPlayerOrException();
				} catch (CommandSyntaxException e) {
					throw new RuntimeException(e);
				}

				MMOParties.GetStats(player).party.players.forEach(playerEntity -> {
					builder.suggest(playerEntity.getName().getString());
				});
				break;
		}

		return builder.buildFuture();
	}
	
	private static int run(CommandContext<CommandSourceStack> context, String sub, String targetStr) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		ServerPlayer target = null; // This is null until the next statement. It can remain null.

		if (targetStr != null) target = (context.getSource().getPlayerOrException()).getServer().getPlayerList().getPlayerByName(targetStr);

		if (targetStr != null && target == null) { CommandMessageHelper.SendError( player, String.format("The player %s is not online.", targetStr) ); return 0; }
		if (player.getCommandSenderWorld().isClientSide) return 0; // Only perform operations on the server side.
			
		PlayerStats stats = MMOParties.GetStats( player );
		System.out.println(stats);
		
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

			case "invite":
				if (targetStr == null) { CommandMessageHelper.SendError(player, "rpgparties.message.error.argument", player.getName().getString()); return 0; }
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

				if (targetStr == null) { CommandMessageHelper.SendError(player, "rpgparties.message.error.argument", player.getName().getString()); return 0; }
				
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
				Level playerSenderWorld = player.getCommandSenderWorld();
				if (!playerSenderWorld.isClientSide) MMOParties.network.send(PacketDistributor.PLAYER.with(() -> player), new MessageOpenUI()); // Send open message
				else if (Minecraft.getInstance().hasSingleplayerServer()) EventClient.OpenPartyScreen();
 				break;

			case "pvp":
				stats.pvpEnabled = !stats.pvpEnabled;
				CommandMessageHelper.SendInfo( player,  stats.pvpEnabled ? "rpgparties.message.pvp.enabled" : "rpgparties.message.pvp.disabled" );
				break;

			case "add":
				MMOParties.network.send(PacketDistributor.PLAYER.with(() -> player), new MessageUpdateParty("dev,devtestman2,dev3"));
				break;
			default:
				break;
		}
		
		return 0;
	}
}
