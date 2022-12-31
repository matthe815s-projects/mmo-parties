package deathtags.stats;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

public abstract class PlayerGroup {
	/**
	 * The group leader.
	 */
	public Player leader = null;

	/**
	 * Send a member-list update to the entire group.
	 */
	public abstract void SendUpdate();
	
	/**
	 * Send a stat update to the entire group.
	 * @param member The member in question.
	 * @param bypassLimit Whether or not it should account for the last ping.
	 */
	public abstract void SendPartyMemberData(Player member, boolean bypassLimit, boolean remove);
	
	/**
	 * If the cached data, and the current data are identical.
	 * @param member The member in question.
	 * @return
	 */
	public abstract boolean IsDataDifferent(Player member);
	
	/**
	 * If the player is a member of the player.
	 * @param member The member in question.
	 * @return
	 */
	public abstract boolean IsMember(Player member);
	
	/**
	 * Broadcast a message to the entire group.
	 * @param message
	 */
	public abstract void Broadcast(TranslatableComponent message);
	
	/**
	 * Get all of the players alive in the group.
	 * @return
	 */
	public abstract Player[] GetOnlinePlayers();
	
	/**
	 * The name used to represent this kind of group in system messages.
	 * @return
	 */
	public abstract String GetGroupAlias();
	
	/**
	 * Set a specific player to the group leader.
	 * @param member
	 */
	public void MakeLeader(Player member)
	{
		this.leader = member;
		this.Broadcast(new TranslatableComponent("rpgparties.message.leader.make", member.getName().getString(), this.GetGroupAlias()));

		for ( Player player : this.GetOnlinePlayers() ) SendPartyMemberData ( player, true, false );
		SendUpdate();
	}
}
