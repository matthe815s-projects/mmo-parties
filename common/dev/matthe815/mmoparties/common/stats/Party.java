package dev.matthe815.mmoparties.common.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.matthe815.mmoparties.fabric.core.MMOParties;
import dev.matthe815.mmoparties.fabric.helpers.CommandMessageHelper;
import dev.matthe815.mmoparties.fabric.networking.MessagePartyInvite;
import dev.matthe815.mmoparties.fabric.networking.MessageSendMemberData;
import dev.matthe815.mmoparties.fabric.networking.MessageUpdateParty;
import dev.matthe815.mmoparties.common.networking.PartyPacketDataBuilder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class Party extends PlayerGroup
{	
	public List<ServerPlayer> players = new ArrayList<>();
	public List<String> playersOffline = new ArrayList<>();
	public List<String> local_players = new ArrayList<String>();
	public Map<String, PartyMemberData> data = new HashMap<String, PartyMemberData>();

	public Party(ServerPlayer player)
	{
		leader = player;
		players.add(player);
		playersOffline.add(player.getName().getString());
		SendUpdate();
	}
	
	public Party() {}
	
	/**
	 * Create a new party and set the leader to a provided leader. Can error and do nothing.
	 * @param leader The player to attempt to make leader.
	 */
	public static Party Create ( ServerPlayer leader ) {
		PlayerStats stats = MMOParties.GetStatsByName( leader.getName().getString() );
		
		if (stats.InParty()) { CommandMessageHelper.SendError( leader, "rpgparties.message.party.exists" ); return stats.party; }
		stats.party = new Party (leader); // Set the leaders' party.

		CommandMessageHelper.SendInfo( leader ,  "rpgparties.message.party.create" );
		return stats.party;
	}

	/**
	 * Create a new party without a leader. Can error and do nothing.
	 */
	public static Party CreateGlobalParty ( ServerPlayer player ) {
		PlayerStats stats = MMOParties.GetStatsByName( player.getName().getString() );
		stats.party = new Party (player); // Set the leaders' party.
		stats.party.leader = null;
		return stats.party;
	}
	
	/**
	 * Invite a player to the party.
	 * @param player Target player.
	 */
	public void Invite ( Player invoker, ServerPlayer player ) {
		PlayerStats targetPlayer = MMOParties.GetStats( player );

		// Prevent you from inviting yourself.
		if ( invoker == player )
			{ CommandMessageHelper.SendInfo( invoker, "rpgparties.message.invite.self" ); return; }

		PlayerStats invokerPlayer = MMOParties.GetStats( invoker );
		
		if ( invokerPlayer.party.leader != invoker ) // Only the leader may invite.
			{ CommandMessageHelper.SendError( invoker , "rpgparties.message.party.privilege" ); return; }
		
		if ( ( targetPlayer.InParty () || targetPlayer.partyInvite != null )) // Players already in a party may not be invited.
			{ CommandMessageHelper.SendError( invoker, "rpgparties.message.party.player.exists", player.getName().getString() ); return; }
		
		targetPlayer.partyInvite = this;
		ServerPlayNetworking.send(player, MessagePartyInvite.ID, MessagePartyInvite.encode(invoker.getName().getString()));
		CommandMessageHelper.SendInfo( invoker, "rpgparties.message.party.invited" , player.getName().getString() );
	}
	
	/**
	 * Join a player to this party.
	 * @param player The target.
	 */
	public void Join ( ServerPlayer player, boolean displayMessage )
	{
		if (this.players.size() >= 10)
		 { CommandMessageHelper.SendError(player, "rpgparties.message.party.full"); return; }
			
		this.players.add(player);
		this.playersOffline.add(player.getName().getString());

		PlayerStats stats = MMOParties.GetStatsByName( player.getName().getString() );
		
		stats.party = this;
		stats.partyInvite = null; // Clear the party invite to prevent potential double joining.
		
		if (displayMessage) Broadcast( Component.translatable( "rpgparties.message.party.joined", player.getName().getString() ) );
		
		for ( ServerPlayer member : players ) SendPartyMemberData( member, true, false ); // Update all of the party members.
		
		SendUpdate(); // Send a player stat update.
	}
	
	public void Leave (ServerPlayer player)
	{
		this.players.remove(player);
		
		Broadcast( Component.translatable( "rpgparties.message.party.player.left", player.getName().toString() ) );
		SendPartyMemberData(player,true, true); // Send one last update.
		
		if (player == this.leader && players.size() > 0) this.leader = players.get(0); // If the player was the leader, then assign a new leader.

		SendUpdate();
		MMOParties.GetStats(player).party = null; // No party.
		ClientPlayNetworking.send(MessageUpdateParty.ID, MessageUpdateParty.encode(""));

		// Disband the party of 1 player. Don't disband if auto-parties is enabled.
		if (players.size() == 1) Disband();

		CommandMessageHelper.SendInfo(player, "rpgparties.message.party.leave");
	}
	
	/**
	 * Disband the party.
	 */
	public void Disband ()
	{
		Broadcast(Component.translatable("rpgparties.message.party.disbanded"));
		
		leader = null;
		
		for (Player member : players) {
			PlayerStats stats = MMOParties.GetStatsByName ( member.getName().getString() );
			stats.party = null;
			ClientPlayNetworking.send(MessageUpdateParty.ID, MessageUpdateParty.encode(""));
		}

		players.clear();
	}
	
	/**
	 * Broadcast a message to every member within the group.
	 * @param message The message to send.
	 */
	@Override
	public void Broadcast ( MutableComponent message )
	{
		for (Player member : this.players) {
			member.displayClientMessage(message, false);
		}
	}
	
	@Override
	public ServerPlayer[] GetOnlinePlayers()
	{
		return players.toArray(new ServerPlayer[] {});
	}
	
	@Override
	public void SendUpdate()
	{
		String[] playerNames = new String[players.size()];
		int i = 0;
		
		for (ServerPlayer partyPlayer : players) {
			playerNames[i] = partyPlayer.getName().getString();
			i++;
		}

		for (ServerPlayer partyPlayer : players) {
			if (!(partyPlayer instanceof ServerPlayer)) return;
			ServerPlayNetworking.send(partyPlayer, MessageUpdateParty.ID, MessageUpdateParty.encode(String.join(",", playerNames)));
		}
	}
	
	@Override
	public void SendPartyMemberData(ServerPlayer member, boolean bypassLimit, boolean remove)
	{
		if (IsDataDifferent(member) || bypassLimit)
		{
			PartyPacketDataBuilder builder = new PartyPacketDataBuilder ()
				.SetPlayer(member)
				.SetHealth(member.getHealth())
				.SetMaxHealth(member.getMaxHealth())
				.SetArmor(member.getArmorValue())
				.SetLeader(this.leader.getName()==member.getName())
				.SetAbsorption(member.getAbsorptionAmount())
				.SetHunger(member.getFoodData().getFoodLevel());

			this.data.put(member.getName().getString(), new PartyMemberData(builder));

			for (ServerPlayer partyPlayer : players) {
				ServerPlayNetworking.send(partyPlayer, MessageSendMemberData.ID, MessageSendMemberData.encode(builder, remove));
			}
		}
	}
	
	@Override
	public boolean IsDataDifferent(ServerPlayer player)
	{
        return !this.data.containsKey(player.getName().getString()) || this.data.get(player.getName().getString()).IsDifferent(player);
    }

	@Override
	public boolean IsMember(ServerPlayer player)
	{
		for (Player member : players) {
			if (member.getName().equals(player.getName()))
				return true;
		}
		
		return false;
	}

	public boolean IsMemberOffline(Player player)
	{
		return playersOffline.contains(player.getName().getString());
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
	public void Teleport(ServerPlayer player, ServerPlayer target) {
		if ( ! IsMember ( target ) ) 
			{ CommandMessageHelper.SendError(player, "rpgparties.message.error.party"); return; }
		
		MMOParties.GetStatsByName( player.getName().getString() ).StartTeleport (target);
	}
}
