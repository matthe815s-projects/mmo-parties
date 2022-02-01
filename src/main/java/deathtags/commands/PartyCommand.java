package deathtags.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import deathtags.core.ConfigHandler;
import deathtags.core.MMOParties;
import deathtags.helpers.ArrayHelpers;
import deathtags.helpers.CommandMessageHelper;
import deathtags.stats.Party;
import deathtags.stats.PlayerStats;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class PartyCommand extends CommandBase {
	public String[] corrections = new String[] {
			"create",
			"invite",
			"accept",
			"deny",
			"leave",
			"leader",
			"tp",
			"disband"
	};
	
	@Override
	public String getName() {
		return "party";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "party <action> (player)";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		List<String> completions = new ArrayList<String>();
		
		if (args.length < 2) {
			if (args.length == 0)
				for (int i=0;i<corrections.length;i++) {
					completions.add(corrections[i]);
				}
			else
				completions = ArrayHelpers.FindClosestToValue(args[0], corrections);
		}
		
		if ((args.length == 2) || (args.length == 1 && (args[0] == "invite" || args[0] == "leader"))) {
			for (String player : server.getOnlinePlayerNames()) {
				if (sender.getName() != player)
					completions.add(player);
			}
		}
		
		return completions;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		EntityPlayerMP player = (EntityPlayerMP) sender.getCommandSenderEntity();
		EntityPlayerMP target = null;
		
		if (args.length == 0) {
			CommandMessageHelper.SendInfo( player, String.format( "Command must contain valid argument(s). Valid arguments are: %s", String.join(", ", Arrays.asList ( corrections ) ) ) );
			return;
		}
	
		if (args.length > 1) target = server.getPlayerList().getPlayerByUsername(args.length == 3 ? args[2] : args[1]); // Set the target from a supplied username.
		if (player.world.isRemote) return; // Only perform operations on the server side.
			
		PlayerStats stats = MMOParties.GetStatsByName(player.getName());
		
		switch (args[0]) {
			case "tp":
				if (!ConfigHandler.Server_Options.allowPartyTP) 
					{ CommandMessageHelper.SendError( player, "This server does not allow for party teleportation." ); return; } // Do not allow TP.
				
				if (!stats.InParty()) 
					{ CommandMessageHelper.SendError( player, "You must be in a party to teleport someone." ); return; }
				
				stats.party.Teleport ( player, target );
				break;
			
			case "create":
				Party.Create( player ); // Create a new party for the player.
				break;
			
			case "invite":
				if (!stats.InParty()) Party.Create ( player ); // Create a party to invite with if not existant.
				
				stats.party.Invite ( player, target ); // Send an invite to the target player.
				break;
			
			case "accept":				
				if (stats.partyInvite == null)
					{ CommandMessageHelper.SendError( player , "You do not currently have an invite." ); return; }
				
				stats.partyInvite.Join(player); // Accept an invite to a player.	
				break;
				
			case "deny":
				if (stats.partyInvite == null)
					{ CommandMessageHelper.SendError( player , "You do not currently have an invite." ); return; }

				stats.partyInvite = null; // Deny the invite.
				break;
			
			case "leave":
				if (!stats.InParty())
					{ CommandMessageHelper.SendError( player, "You are not currently in a party." ); return; }
				
				stats.party.Leave(player); // Perform the leave actions.
				stats.party = null;
				
				CommandMessageHelper.SendInfo( player, "You have left your party." );
				break;
				
			case "leader":
				if (!stats.InParty())
					{ CommandMessageHelper.SendError( player, "You are not currently in a party." ); return; }
				
				if (stats.party.leader != player) // Only the leader can promote.
					{ CommandMessageHelper.SendError( player, "Only the leader may promote members." ); return; }
				
				stats.party.leader = target; // Assign leadership.
				stats.party.Broadcast(String.format( "%s has been given leadership of the party. ", target.getName() ) );
				break;
				
			case "disband":
				if (!stats.InParty())
					{ CommandMessageHelper.SendError( player, "You are not currently in a party." ); return; }
			
				if (stats.party.leader != player) // Only the leader can promote.
					{ CommandMessageHelper.SendError( player, "Only the leader may disband." ); return; }
			
				stats.party.Disband();
				break;
			default:
				CommandMessageHelper.SendInfo( player, String.format( "Command must contain valid argument(s). Valid arguments are: %s", String.join(", ", Arrays.asList ( corrections ) ) ) );
				break;
		}
	}
}
