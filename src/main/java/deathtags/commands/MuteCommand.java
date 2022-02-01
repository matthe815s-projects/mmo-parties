package deathtags.commands;

import java.util.ArrayList;
import java.util.List;

import deathtags.core.MMOParties;
import deathtags.stats.PlayerStats;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class MuteCommand extends CommandBase {
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "mute";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "mute <action> (player)";
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		List<String> completions = new ArrayList<String>();
		
		if (args.length == 0 || args.length == 1) {
			for (String player : server.getOnlinePlayerNames()) {
				completions.add(player);
			}
		}
		
		return completions;
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0)
			return;
		
		EntityPlayerMP player = (EntityPlayerMP) sender.getCommandSenderEntity();
		EntityPlayerMP target = server.getPlayerList().getPlayerByUsername(args[0]);
		
		if (player.world.isRemote)
			return;
		
		if (target == null) {
			player.sendMessage(new TextComponentString("No user with the name " + args[0] + " exists!"));
			return;
		}
		
		PlayerStats targetStats = MMOParties.GetStatsByName(target.getName());
		targetStats.isMuted = !targetStats.isMuted;
		
		if (targetStats.isMuted) {
			target.sendMessage(new TextComponentString("You have been muted!"));
			player.sendMessage(new TextComponentString("You have muted " + target.getName() + "!"));	
		} else {
			target.sendMessage(new TextComponentString("You have been unmuted!"));
			player.sendMessage(new TextComponentString("You have unmuted " + target.getName() + "!"));
		}
	}
}
