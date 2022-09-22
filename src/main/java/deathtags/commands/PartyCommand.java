package deathtags.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import deathtags.core.ConfigHandler;
import deathtags.core.MMOParties;
import deathtags.core.events.EventClient;
import deathtags.helpers.ArrayHelpers;
import deathtags.helpers.CommandMessageHelper;
import deathtags.stats.Party;
import deathtags.stats.PlayerStats;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class PartyCommand extends CommandBase {
	public String[] corrections = new String[] {
			"invite",
			"accept",
			"deny",
			"kick",
			"leave",
			"leader",
			"tp",
			"disband",
			"gui"
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
		
		if ((args.length == 2) || (args.length == 1 && (args[0] == "invite" || args[0] == "kick" || args[0] == "leader"))) {
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
				{ CommandMessageHelper.SendError(player, "rpgparties.message.error.teleport.server"); return; }
				if (!stats.InParty())
				{ CommandMessageHelper.SendError( player, "rpgparties.message.error.teleport.party" ); return; }

				stats.party.Teleport ( player, target );
				break;

			case "kick":
				if (!stats.InParty())
				{ CommandMessageHelper.SendError( player, "rpgparties.message.error.party" ); return; }

				stats.party.Leave(target);
				break;

			case "create":
				Party.Create( player ); // Create a new party for the player.
				break;

			case "invite":
				if (target == null) { CommandMessageHelper.SendError(player, "rpgparties.message.error.argument", player.getName()); return; }
				if (!stats.InParty()) Party.Create ( player ); // Create a party to invite with if not existent.

				stats.party.Invite ( player, target ); // Send an invite to the target player.
				break;

			case "accept":
				if (stats.partyInvite == null)
				{ CommandMessageHelper.SendError( player , "rpgparties.message.error.invite" ); return; }

				stats.partyInvite.Join(player, true); // Accept an invite to a player.
				break;

			case "deny":
				if (stats.partyInvite == null)
				{ CommandMessageHelper.SendError( player , "rpgparties.message.error.invite" ); return; }

				stats.partyInvite = null; // Deny the invite.
				CommandMessageHelper.SendInfo(player, "rpgparties.message.party.deny");
				break;

			case "leave":
				if (!stats.InParty())
				{ CommandMessageHelper.SendError( player, "rpgparties.message.error.party" ); return; }

				stats.party.Leave(player); // Perform the leave actions.
				stats.party = null;
				break;

			case "leader":
				if (!stats.InParty())
				{ CommandMessageHelper.SendError( player, "You are not currently in a party." ); return; }

				if (stats.party.leader != player) // Only the leader can promote
				{ CommandMessageHelper.SendError( player, "Only the leader may promote members." ); return; }

				if (target == null) { CommandMessageHelper.SendError(player, "rpgparties.message.error.argument", player.getName().getContents()); return; }

				stats.party.MakeLeader(player);
				break;

			case "disband":
				if (!stats.InParty())
				{ CommandMessageHelper.SendError( player, "rpgparties.message.error.party" ); return; }

				if (stats.party.leader != player) // Only the leader can promote.
				{ CommandMessageHelper.SendError( player, "rpgparties.message.error.disband" ); return; }

				stats.party.Disband();
				break;

			case "gui":
				if (player.world.isRemote) MMOParties.network.sendTo(new MessageOpenUI(), player.connection); // Send open message
				else if (Minecraft.getMinecraft().isSingleplayer()) EventClient.openScreen();
				break;

			default:
				break;
		}

		return;
	}
}
