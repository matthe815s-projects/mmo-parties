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
import epicsquid.superiorshields.capability.shield.IShieldCapability;
import epicsquid.superiorshields.capability.shield.SuperiorShieldsCapabilityManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Loader;

public class Party extends PlayerGroup
{
	public List<PlayerEntity> players = new ArrayList<PlayerEntity>();
	public List<String> playersOffline = new ArrayList<>();
	public List<String> local_players = new ArrayList<String>();
	public Map<String, PartyMemberData> data = new HashMap<String, PartyMemberData>();

	public Party(PlayerEntity player)
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
	public static Party Create ( PlayerEntity leader ) {
		PlayerStats stats = MMOParties.GetStatsByName( leader.getName().getContents() );

		if (stats.InParty()) { CommandMessageHelper.SendError( leader, "rpgparties.message.party.exists" ); return stats.party; }
		stats.party = new Party (leader); // Set the leaders' party.

		CommandMessageHelper.SendInfo( leader ,  "rpgparties.message.party.create" );
		return stats.party;
	}

	/**
	 * Create a new party without a leader. Can error and do nothing.
	 * @param leader The player to attempt to make leader.
	 */
	public static Party CreateGlobalParty ( PlayerEntity player ) {
		PlayerStats stats = MMOParties.GetStatsByName( player.getName().getContents() );

		if (stats.InParty()) { CommandMessageHelper.SendError( player, "rpgparties.message.party.exists" ); return stats.party; }
		stats.party = new Party (player); // Set the leaders' party.
		stats.party.leader = null;

		CommandMessageHelper.SendInfo( player ,  "rpgparties.message.party.create" );
		return stats.party;
	}

	/**
	 * Invite a player to the party.
	 * @param player Target player.
	 */
	public void Invite ( PlayerEntity invoker, PlayerEntity player ) {
		PlayerStats targetPlayer = MMOParties.GetStats( player );
		PlayerStats invokerPlayer = MMOParties.GetStats( invoker );

		if ( invokerPlayer.party.leader != invoker ) // Only the leader may invite.
		{ CommandMessageHelper.SendError( invoker , "rpgparties.message.party.privilege" ); return; }

		//if ( targetPlayer.InParty () || targetPlayer.partyInvite != null ) // Players already in a party may not be invited.
		//	{ CommandMessageHelper.SendError( invoker, "rpgparties.message.party.player.exists", player.getName().getContents() ); return; }

		targetPlayer.partyInvite = this;

		CommandMessageHelper.SendInfo( invoker, "rpgparties.message.party.invited" , player.getName().getContents() );
		CommandMessageHelper.SendInfoWithButton(player, "rpgparties.message.party.invite.from", invoker.getName().getContents());
	}

	/**
	 * Join a player to this party.
	 * @param player The target.
	 */
	public void Join ( PlayerEntity player, boolean displayMessage )
	{
		if (this.players.size() >= 4)
		{ CommandMessageHelper.SendError(player, "rpgparties.message.party.full"); return; }

		this.players.add(player);
		this.playersOffline.add(player.getName().getString());

		PlayerStats stats = MMOParties.GetStatsByName( player.getName().getContents() );

		stats.party = this;
		stats.partyInvite = null; // Clear the party invite to prevent potential double joining.

		if (displayMessage) Broadcast( new TranslationTextComponent( "rpgparties.message.party.joined", player.getName().getContents() ) );

		for ( PlayerEntity member : players ) SendPartyMemberData( member, true ); // Update all of the party members.

		SendUpdate(); // Send a player stat update.
	}

	public void Leave (PlayerEntity player)
	{
		this.players.remove(player);

		Broadcast( new TranslationTextComponent( "rpgparties.message.party.player.left", player.getName().getContents() ) );

		for ( PlayerEntity member : players ) SendPartyMemberData ( member, true );
		SendPartyMemberData(player, true); // Send one last update.

		if (player == this.leader && players.size() > 0) this.leader = players.get(0); // If the player was the leader, then assign a new leader.

		SendUpdate();

		MMOParties.GetStats(player).party = null; // No party.
		MMOParties.network.sendTo(new MessageUpdateParty(""), ((ServerPlayerEntity)player).connection.connection, NetworkDirection.PLAY_TO_CLIENT); // Clear the player's party.

		// Disband the party of 1 player. Don't disband if auto-parties is enabled.
		if (players.size() == 1 && !ConfigHolder.COMMON.autoAssignParties.get()) Disband();

		CommandMessageHelper.SendInfo(player, "rpgparties.message.party.leave");
	}

	/**
	 * Disband the party.
	 */
	public void Disband ()
	{
		Broadcast(new TranslationTextComponent("rpgparties.message.party.disbanded"));

		leader = null;

		for (PlayerEntity member : players) {
			PlayerStats stats = MMOParties.GetStatsByName ( member.getName().getContents() );
			stats.party = null;
			MMOParties.network.sendTo(new MessageUpdateParty(""), ((ServerPlayerEntity)member).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}

		players.clear();
	}

	/**
	 * Broadcast a message to every member within the group.
	 * @param message The message to send.
	 */
	@Override
	public void Broadcast ( TranslationTextComponent message )
	{
		for (PlayerEntity member : players) member.displayClientMessage(message, false);
	}

	@Override
	public PlayerEntity[] GetOnlinePlayers()
	{
		return players.toArray(new PlayerEntity[] {});
	}

	@Override
	public void SendUpdate()
	{
		String[] playerNames = new String[players.size()];
		int i = 0;

		for (PlayerEntity party_player : players) {
			playerNames[i] = party_player.getName().getContents();
			i++;
		}

		for (PlayerEntity party_player : players) {
			if (!(party_player instanceof ServerPlayerEntity)) return;

			MMOParties.network.sendTo(new MessageUpdateParty(String.join(",", playerNames)), ((ServerPlayerEntity)party_player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	@Override
	public void SendPartyMemberData(PlayerEntity member, boolean bypassLimit)
	{
		if (IsDataDifferent(member) || bypassLimit)
		{
			if (!this.pings.containsKey( member.getName().getContents() ))
				this.pings.put(member.getName().getContents(), new PlayerPing(member, 0, 0, 0, bypassLimit, 0, 0, 0, 0));

			this.pings.get( member.getName().getContents() ).Update(member.getHealth(), member.getMaxHealth(), member.getArmorValue(),
					this.leader==member, member.getAbsorptionAmount(), 0, 0);

			for (PlayerEntity party_player : players) {
				if (!(party_player instanceof ServerPlayerEntity)) return;

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
						), ((ServerPlayerEntity)party_player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
			}
		}
	}

	@Override
	public boolean IsDataDifferent(PlayerEntity player)
	{
		if (!this.pings.containsKey( player.getName().getContents() ) || this.pings.get( player.getName().getContents() ).IsDifferent(player))
			return true;

		return false;
	}

	@Override
	public boolean IsMember(PlayerEntity player)
	{
		for (PlayerEntity member : players) {
			if (member.getName().equals(player.getName()))
				return true;
		}

		return false;
	}

	public boolean IsMemberOffline(PlayerEntity player)
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
	public void Teleport(PlayerEntity player, PlayerEntity target) {
		if ( ! IsMember ( target ) )
		{ CommandMessageHelper.SendError(player, "rpgparties.message.error.party"); return; }

		MMOParties.GetStatsByName( player.getName().getContents() ).StartTeleport (target);
	}
}
