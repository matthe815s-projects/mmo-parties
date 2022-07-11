package deathtags.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import deathtags.config.ConfigHolder;
import deathtags.core.MMOParties;
import deathtags.helpers.CommandMessageHelper;
import deathtags.stats.Party;
import deathtags.stats.PlayerStats;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

public class PartyCommand {
	
	public static LiteralArgumentBuilder<CommandSource> register () {
		return Commands.literal("party")
				.requires(cs -> cs.hasPermission(0))
				.then(Commands.argument("sub", StringArgumentType.string()).executes(ctx -> run(ctx, StringArgumentType.getString(ctx, "sub"), null))
						.suggests(
								(sourceCommandContext, suggestionsBuilder) -> {
									suggestionsBuilder
											.suggest("invite")
											.suggest("accept")
											.suggest("deny")
											.suggest("leader")
											.suggest("disband"); // Build suggestions

									if (ConfigHolder.COMMON.allowPartyTP.get()) // If you're allowed to party teleport, display the option
										suggestionsBuilder.suggest("tp");

									return suggestionsBuilder.buildFuture();
								}
						).then(
								Commands.argument("player", StringArgumentType.string())
									.executes(ctx -> run(ctx, StringArgumentType.getString(ctx, "sub"), StringArgumentType.getString(ctx, "player")))
									.suggests((sourceCommandContext, suggestionsBuilder) -> {
										for (ServerPlayerEntity playerName : sourceCommandContext.getSource().getServer().getPlayerList().getPlayers())
										{
											if (sourceCommandContext.getSource().getPlayerOrException().getName().getContents() != playerName.getName().getContents())
												suggestionsBuilder.suggest(playerName.getName().getContents());
										}

										return suggestionsBuilder.buildFuture();
									})
								));
	}
	
	private static int run(CommandContext<CommandSource> context, String sub, String targetStr) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayerOrException();
		ServerPlayerEntity target = context.getSource().getServer().getPlayerList().getPlayerByName(targetStr);

		if (targetStr != null && target == null) { CommandMessageHelper.SendError( player, String.format("The player %s is not online.", targetStr) ); return 0; }
		if (player.getCommandSenderWorld().isClientSide) return 0; // Only perform operations on the server side.
			
		PlayerStats stats = MMOParties.GetStats( player );
		
		switch (sub) {
			case "tp":
				if (!ConfigHolder.COMMON.allowPartyTP.get())
				{ CommandMessageHelper.SendError(player, "This server disallows party teleportation."); return 0; }
				if (!stats.InParty())
				{ CommandMessageHelper.SendError( player, "You must be in a party to teleport someone." ); return 0; }
				
				stats.party.Teleport ( player, target );
				break;
			
			case "create":
				Party.Create( player ); // Create a new party for the player.
				break;
			
			case "invite":
				if (targetStr == null) { CommandMessageHelper.SendError(player, "You must specify a player with this command"); return 0; }
				if (!stats.InParty()) Party.Create ( player ); // Create a party to invite with if not existent.
				
				stats.party.Invite ( player, target ); // Send an invite to the target player.
				break;
			
			case "accept":				
				if (stats.partyInvite == null)
				{ CommandMessageHelper.SendError( player , "You do not currently have an invite." ); return 0; }
				
				stats.partyInvite.Join(player); // Accept an invite to a player.	
				break;
				
			case "deny":
				if (stats.partyInvite == null)
				{ CommandMessageHelper.SendError( player , "You do not currently have an invite." ); return 0; }

				stats.partyInvite = null; // Deny the invite.
				CommandMessageHelper.SendInfo(player, "You have denied the invite");
				break;
			
			case "leave":
				if (!stats.InParty())
				{ CommandMessageHelper.SendError( player, "You are not currently in a party." ); return 0; }
				
				stats.party.Leave(player); // Perform the leave actions.
				stats.party = null;
				
				CommandMessageHelper.SendInfo( player, "You have left your party." );
				break;
				
			case "leader":
				if (!stats.InParty())
				{ CommandMessageHelper.SendError( player, "You are not currently in a party." ); return 0; }
				
				if (stats.party.leader != player) // Only the leader can promote
				{ CommandMessageHelper.SendError( player, "Only the leader may promote members." ); return 0; }

				if (targetStr == null) { CommandMessageHelper.SendError(player, "You must specify a player with this command"); return 0; }
				
				stats.party.leader = target; // Assign leadership.
				stats.party.Broadcast(String.format( "%s has been given leadership of the party. ", target.getName() ) );
				break;
				
			case "disband":
				if (!stats.InParty())
				{ CommandMessageHelper.SendError( player, "You are not currently in a party." ); return 0; }
			
				if (stats.party.leader != player) // Only the leader can promote.
				{ CommandMessageHelper.SendError( player, "Only the leader may disband." ); return 0; }
			
				stats.party.Disband();
				break;
			default:
				break;
		}
		
		return 0;
	}
}
