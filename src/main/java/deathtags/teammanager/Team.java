package deathtags.teammanager;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;

public class Team {
	public List<EntityPlayerMP> players = new ArrayList<EntityPlayerMP>();
	
	/**
	 * If the team includes a certain player.
	 * @param player
	 * @return
	 */
	public boolean HasPlayer(EntityPlayerMP player)
	{
		return players.contains(player);
	}
	
	/**
	 * Returns if two players are of the same team.
	 * @param player1
	 * @param player2
	 * @return
	 */
	public boolean AreSameTeam(EntityPlayerMP player1, EntityPlayerMP player2)
	{
		return players.contains(player1) && players.contains(player2);
	}
}
