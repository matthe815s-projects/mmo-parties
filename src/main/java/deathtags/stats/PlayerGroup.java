package deathtags.stats;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;

public abstract class PlayerGroup {
	/**
	 * The group leader.
	 */
	public EntityPlayer leader = null;

	/**
	 * A cache of the last sent packet.
	 */
	public Map<String, PlayerPing> pings = new HashMap<String, PlayerPing>();

	/**
	 * Send a member-list update to the entire group.
	 */
	public abstract void SendUpdate();

	/**
	 * Send a stat update to the entire group.
	 * @param member The member in question.
	 * @param bypassLimit Whether or not it should account for the last ping.
	 */
	public abstract void SendPartyMemberData(EntityPlayer member, boolean bypassLimit);

	/**
	 * If the cached data, and the current data are identical.
	 * @param member The member in question.
	 * @return
	 */
	public abstract boolean IsDataDifferent(EntityPlayer member);

	/**
	 * If the player is a member of the player.
	 * @param member The member in question.
	 * @return
	 */
	public abstract boolean IsMember(EntityPlayer member);

	/**
	 * Broadcast a message to the entire group.
	 * @param message
	 */
	public abstract void Broadcast(TextComponentTranslation message);

	/**
	 * Get all of the players alive in the group.
	 * @return
	 */
	public abstract EntityPlayer[] GetOnlinePlayers();

	/**
	 * The name used to represent this kind of group in system messages.
	 * @return
	 */
	public abstract String GetGroupAlias();

	/**
	 * Set a specific player to the group leader.
	 * @param member
	 */
	public void MakeLeader(EntityPlayer member)
	{
		this.leader = member;
		this.Broadcast(new TextComponentTranslation("rpgparties.message.leader.make", member.getName(), this.GetGroupAlias()));

		for ( EntityPlayer player : this.GetOnlinePlayers() ) SendPartyMemberData ( player, true );
		SendUpdate();
	}
}
