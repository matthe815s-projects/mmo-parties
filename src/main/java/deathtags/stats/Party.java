package deathtags.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deathtags.core.MMOParties;
import deathtags.helpers.CommandMessageHelper;
import deathtags.networking.MessageSendMemberData;
import deathtags.networking.MessageUpdateParty;
import deathtags.networking.PartyPacketDataBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;

public class Party extends PlayerGroup
{	
	public List<Player> players = new ArrayList<Player>();
	public List<String> local_players = new ArrayList<String>();
	public Map<String, PartyMemberData> data = new HashMap<String, PartyMemberData>();

	public Party(Player player)
	{
		leader = player;
		players.add(player);
		SendUpdate();
	}
	
	public Party() {}
	
	/**
	 * Create a new party and set the leader to a provided leader. Can error and do nothing.
	 * @param leader The player to attempt to make leader.
	 */
	public static void Create ( Player leader ) {
		PlayerStats stats = MMOParties.GetStatsByName( leader.getName().getContents() );
		
		if (stats.InParty()) { CommandMessageHelper.SendError( leader, "You are already in a party." ); return; }
		stats.party = new Party (leader); // Set the leaders' party.

		CommandMessageHelper.SendInfo( leader ,  "You have created a new party." );
	}
	
	/**
	 * Invite a player to the party.
	 * @param player Target player.
	 */
	public void Invite ( Player invoker, Player player ) {
		PlayerStats targetPlayer = MMOParties.GetStats( player );
		PlayerStats invokerPlayer = MMOParties.GetStats( invoker );
		
		if ( invokerPlayer.party.leader != invoker ) // Only the leader may invite.
			{ CommandMessageHelper.SendError( invoker , "You must be the leader of a party to invite others." ); return; }
		
		//if ( targetPlayer.InParty () || targetPlayer.partyInvite != null ) // Players already in a party may not be invited.
	//		{ CommandMessageHelper.SendError( invoker, String.format( "%s is already in a party.", player.getName().getContents() ) ); return; }
		
		targetPlayer.partyInvite = this;
		
		CommandMessageHelper.SendInfo( invoker, String.format( "You have invited %s to the party." , player.getName().getContents() ) );
		CommandMessageHelper.SendInfoWithButton(player, String.format("You have been invited to %s's party.", invoker.getName().getContents()));
	}
	
	/**
	 * Join a player to this party.
	 * @param player The target.
	 */
	public void Join ( Player player )
	{
		if (this.players.size() >= 4)
		 { CommandMessageHelper.SendError(player, "This party is currently full."); return; }
			
		this.players.add(player);
		
		PlayerStats stats = MMOParties.GetStatsByName( player.getName().getContents() );
		
		stats.party = this;
		stats.partyInvite = null; // Clear the party invite to prevent potential double joining.
		
		Broadcast( String.format( "%s has joined the party!", player.getName().getContents() ) );
		
		for ( Player member : players ) SendPartyMemberData( member, true ); // Update all of the party members.
		
		SendUpdate(); // Send a player stat update.
	}
	
	public void Leave (Player player)
	{
		this.players.remove(player);
		
		Broadcast( String.format( "%s has left the party..", player.getName().getContents() ) );
		
		for ( Player member : players ) SendPartyMemberData ( member, true );
		SendPartyMemberData(player, true); // Send one last update.
		
		if (player == this.leader && players.size() > 0) this.leader = players.get(0); // If the player was the leader, then assign a new leader.

		SendUpdate();
		
		// Disband the party of 1 player.
		if (players.size() == 1) Disband();
	}
	
	/**
	 * Disband the party.
	 */
	public void Disband ()
	{
		Broadcast("The party has been disbanded.");
		
		leader = null;
		
		for (Player member : players) {
			PlayerStats stats = MMOParties.GetStatsByName ( member.getName().getContents() );
			stats.party = null;
		}
	}
	
	/**
	 * Broadcast a message to every member within the group.
	 * @param message The message to send.
	 */
	@Override
	public void Broadcast ( String message )
	{
		for (Player member : players) CommandMessageHelper.SendInfo( member, message );
	}
	
	@Override
	public Player[] GetOnlinePlayers()
	{
		return players.toArray(new Player[] {});
	}
	
	@Override
	public void SendUpdate()
	{
		String[] playerNames = new String[players.size()];
		int i = 0;
		
		for (Player party_player : players) {
			playerNames[i] = party_player.getName().getContents();
			i++;
		}
		
		for (Player party_player : players) {
			if (!(party_player instanceof ServerPlayer)) return;
			
			System.out.println("Update:" + String.join(",", playerNames));
			MMOParties.network.sendTo(new MessageUpdateParty(String.join(",", playerNames)), ((ServerPlayer)party_player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}
	}
	
	@Override
	public void SendPartyMemberData(Player member, boolean bypassLimit)
	{
		if (IsDataDifferent(member) || bypassLimit)
		{	
			if (!this.pings.containsKey( member.getName().getContents() ))
				this.pings.put(member.getName().getContents(), new PlayerPing(member, 0, 0, 0, bypassLimit, 0, 0, 0, 0));
			
			this.pings.get( member.getName().getContents() ).Update(member.getHealth(), member.getMaxHealth(), member.getArmorValue(), 
					this.leader==member, member.getAbsorptionAmount(), 0, 0);	
			
			for (Player party_player : players) {
				if (!(party_player instanceof ServerPlayer)) return;
						
				MMOParties.network.sendTo(						
					new MessageSendMemberData(
						new PartyPacketDataBuilder ()
						.SetPlayer(member.getName().getContents())
						.SetHealth(member.getHealth())
						.SetMaxHealth(member.getMaxHealth())
						.SetArmor(member.getArmorValue())
						.SetLeader(this.leader==member)
						.SetAbsorption(member.getAbsorptionAmount())
						.SetHunger(member.getFoodData().getFoodLevel())
				), ((ServerPlayer)party_player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
			}
		}
	}
	
	@Override
	public boolean IsDataDifferent(Player player)
	{
		if (!this.pings.containsKey( player.getName().getContents() ) || this.pings.get( player.getName().getContents() ).IsDifferent(player))
			return true;
		
		return false;
	}

	@Override
	public boolean IsMember(Player player) 
	{
		for (Player member : players) {
			if (member.getName().equals(player.getName()))
				return true;
		}
		
		return false;
	}

	@Override
	public String GetGroupAlias() {
		return "party";
	}

	/**
	 * Teleport to a player within your party.
	 * @param player Player to teleport.
	 * @param target Player to teleport to.
	 */
	public void Teleport(Player player, Player target) {
		if ( ! IsMember ( target ) ) 
			{ CommandMessageHelper.SendError(player, "You may only teleport to players within your party."); return; }
		
		MMOParties.GetStatsByName( player.getName().getContents() ).StartTeleport (target);
	}
}
