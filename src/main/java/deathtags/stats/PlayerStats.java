package deathtags.stats;

import deathtags.helpers.CommandMessageHelper;
import net.minecraft.world.entity.player.Player;

public class PlayerStats 
{
	public Player player;
	public Party party = null;
	public Party partyInvite = null;

	public Player target;
	public int teleportTicks = 0;
	
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

	// A quick leave method for players.
	public void Leave ()
	{
		if (party == null) return;
		party.Leave(player);
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

	public void TickTeleport() {
		// Process teleporting.
		if (teleportTicks > 0) {
			teleportTicks --;
			if (teleportTicks <= 0) CommenceTeleport(); // Teleport the player.
		}
	}

}
