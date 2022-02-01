package deathtags.stats;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;

public abstract class PlayerGroup {
	/**
	 * The group leader.
	 */
	public EntityPlayerMP leader = null;
	
	/**
	 * A cache of the last sent packet.
	 */
	public Map<String, PlayerPing> pings = new HashMap<String, PlayerPing>();
	
	/**
	 * The group attacking the player.
	 */
	public PlayerGroup opposer = null;
	
	/**
	 * Send a member-list update to the entire group.
	 */
	public abstract void SendUpdate();
	
	/**
	 * Send a stat update to the entire group.
	 * @param member The member in question.
	 * @param bypassLimit Whether or not it should account for the last ping.
	 */
	public abstract void SendPartyMemberData(EntityPlayerMP member, boolean bypassLimit);
	
	/**
	 * If the cached data, and the current data are identical.
	 * @param member The member in question.
	 * @return
	 */
	public abstract boolean IsDataDifferent(EntityPlayerMP member);
	
	/**
	 * If the player is a member of the player.
	 * @param member The member in question.
	 * @return
	 */
	public abstract boolean IsMember(EntityPlayerMP member);
	
	/**
	 * If the entire group is dead.
	 * @return
	 */
	public abstract boolean IsAllDead();
	
	/**
	 * Resurrect the entire group.
	 */
	public abstract void ReviveAll();
	
	/**
	 * Broadcast a message to the entire group.
	 * @param message
	 */
	public abstract void Broadcast(String message);
	
	/**
	 * Get all of the players alive in the group.
	 * @return
	 */
	public abstract EntityPlayerMP[] GetOnlinePlayers();
	
	/**
	 * The name used to represent this kind of group in system messages.
	 * @return
	 */
	public abstract String GetGroupAlias();
	
	/**
	 * Set a specific player to the group leader.
	 * @param member
	 */
	public void MakeLeader(EntityPlayerMP member)
	{
		this.leader = member;
		this.Broadcast(String.format("%s is now the %s leader.", member.getName(), this.GetGroupAlias()));
	}
}
