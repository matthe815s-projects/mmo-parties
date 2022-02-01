package deathtags.stats;

import deathtags.helpers.CommandMessageHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;

public class PlayerStats 
{
	public EntityPlayerMP player = null;
	public Party party = null;
	public Party partyInvite = null;
	public BlockPos deathPosition;
	
	public EntityPlayerMP target;
	public int teleportTicks = 0;
	
	public boolean isMuted = false;
	
	public PlayerStats () {}
	
	public PlayerStats ( EntityPlayerMP player ) {
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

	public void StartTeleport(EntityPlayerMP target) {
		this.target = target;
		this.teleportTicks = 100;
		
		CommandMessageHelper.SendInfo ( player, "Commencing teleport in 5 seconds." );
	}
	
	public void CommenceTeleport() {
		player.setPositionAndUpdate ( target.posX, target.posY, target.posZ );
		target = null;
	}

}
