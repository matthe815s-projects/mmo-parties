package deathtags.stats;

import deathtags.helpers.CommandMessageHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class PlayerStats 
{
	public Player player = null;
	public Party party = null;
	public Party partyInvite = null;
	public BlockPos deathPosition;
	
	public Player target;
	public int teleportTicks = 0;
	
	public boolean isMuted = false;
	
	public PlayerStats () {}
	
	public PlayerStats ( Player player ) {
		this.player = player;
	}
	
	/**
	 * Returns if the player is in a party.
	 * @return The players' party status
	 */
	public boolean InParty () {
		return party != null;
	}
	
	/**
	 * Returns if the player is a leader within a party.
	 * @return The player's leader status.
	 */
	public boolean IsLeader () {
		return party.leader == player;
	}

	public void StartTeleport(Player target) {
		this.target = target;
		this.teleportTicks = 100;
		
		CommandMessageHelper.SendInfo ( player, "Commencing teleport in 5 seconds." );
	}
	
	public void CommenceTeleport() {
		player.setPos( target.xo, target.yo, target.zo );
		target = null;
	}

}
