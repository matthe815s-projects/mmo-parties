package deathtags.stats;

import deathtags.helpers.CommandMessageHelper;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerStats 
{
	public EntityPlayer player;
	public Party party = null;
	public Party partyInvite = null;

	public EntityPlayer target;
	public int teleportTicks = 0;

	public PlayerStats (EntityPlayer player ) {
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

	// A quick leave method for players.
	public void Leave ()
	{
		if (party == null) return;
		party.Leave(player);
	}

	public void StartTeleport(EntityPlayer target) {
		this.target = target;
		this.teleportTicks = 100;

		CommandMessageHelper.SendInfo ( player, "Commencing teleport in 5 seconds." );
	}

	public void CommenceTeleport() {
		player.setPositionAndUpdate( target.posX, target.posY, target.posZ );
		target = null;
	}

	public void TickTeleport() {
		// Process teleporting.
		if (teleportTicks > 0) {
			teleportTicks --;
			if (teleportTicks <= 0) CommenceTeleport(); // Teleport the player.
		}
	}
}
