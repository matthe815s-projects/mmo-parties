package deathtags.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deathtags.core.ConfigHandler;
import deathtags.core.MMOParties;
import deathtags.helpers.CommandMessageHelper;
import deathtags.networking.MessageSendMemberData;
import deathtags.networking.MessageUpdateParty;
import deathtags.networking.PartyPacketDataBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;

public class Party extends PlayerGroup
{
	public List<EntityPlayer> players = new ArrayList<EntityPlayer>();
	public List<String> playersOffline = new ArrayList<>();
	public List<String> local_players = new ArrayList<String>();
	public Map<String, PartyMemberData> data = new HashMap<String, PartyMemberData>();

	public Party(EntityPlayer player)
	{
		leader = player;
		players.add(player);
		playersOffline.add(player.getName());
		SendUpdate();
	}

	public Party() {}

	/**
	 * Create a new party and set the leader to a provided leader. Can error and do nothing.
	 * @param leader The player to attempt to make leader.
	 */
	public static Party Create ( EntityPlayer leader ) {
		PlayerStats stats = MMOParties.GetStatsByName( leader.getName() );

		if (stats.InParty()) { CommandMessageHelper.SendError( leader, "rpgparties.message.party.exists" ); return stats.party; }
		stats.party = new Party (leader); // Set the leaders' party.

		CommandMessageHelper.SendInfo( leader ,  "rpgparties.message.party.create" );
		return stats.party;
	}

	/**
	 * Create a new party without a leader. Can error and do nothing.
	 * @param player The player to attempt to make leader.
	 */
	public static Party CreateGlobalParty ( EntityPlayer player ) {
		PlayerStats stats = MMOParties.GetStatsByName( player.getName() );

		if (stats.InParty()) { CommandMessageHelper.SendError( player, "rpgparties.message.party.exists" ); return stats.party; }
		stats.party = new Party (player); // Set the leaders' party.
		stats.party.leader = null;

		return stats.party;
	}

	/**
	 * Invite a player to the party.
	 * @param player Target player.
	 */
	public void Invite ( EntityPlayer invoker, EntityPlayer player ) {
		if (invoker == player)
		{ CommandMessageHelper.SendError( invoker, "rpgparties.message.error.invite.self"); return; };

		PlayerStats targetPlayer = MMOParties.GetStats( player );
		PlayerStats invokerPlayer = MMOParties.GetStats( invoker );

		if ( invokerPlayer.party.leader != invoker ) // Only the leader may invite.
		{ CommandMessageHelper.SendError( invoker , "rpgparties.message.party.privilege" ); return; }

		if ( targetPlayer.InParty () || targetPlayer.partyInvite != null ) // Players already in a party may not be invited.
			{ CommandMessageHelper.SendError( invoker, "rpgparties.message.party.player.exists", player.getName() ); return; }

		targetPlayer.partyInvite = this;

		CommandMessageHelper.SendInfo( invoker, "rpgparties.message.party.invited" , player.getName() );
		CommandMessageHelper.SendInfoWithButton(player, "rpgparties.message.party.invite.from", invoker.getName() );
	}

	/**
	 * Join a player to this party.
	 * @param player The target.
	 */
	public void Join ( EntityPlayer player, boolean displayMessage )
	{
		if (this.players.size() >= 4)
		{ CommandMessageHelper.SendError(player, "rpgparties.message.party.full"); return; }

		this.players.add(player);
		this.playersOffline.add(player.getName());

		PlayerStats stats = MMOParties.GetStatsByName( player.getName() );

		stats.party = this;
		stats.partyInvite = null; // Clear the party invite to prevent potential double joining.

		if (displayMessage) Broadcast( new TextComponentTranslation( "rpgparties.message.party.joined", player.getName() ) );

		for ( EntityPlayer member : players ) SendPartyMemberData( member, true ); // Update all of the party members.

		SendUpdate(); // Send a player stat update.
	}

	public void Leave (EntityPlayer player)
	{
		this.players.remove(player);

		Broadcast( new TextComponentTranslation( "rpgparties.message.party.player.left", player.getName() ) );

		for ( EntityPlayer member : players ) SendPartyMemberData ( member, true );
		SendPartyMemberData(player, true); // Send one last update.

		if (player == this.leader && players.size() > 0) this.leader = players.get(0); // If the player was the leader, then assign a new leader.

		SendUpdate();

		MMOParties.GetStats(player).party = null; // No party.
		SendMemberUpdate(player);

		// Disband the party of 1 player. Don't disband if auto-parties is enabled.
		if (players.size() <= 1 && !ConfigHandler.Server_Options.autoAssignParties) Disband();

		if (this.players.size() >= 1) CommandMessageHelper.SendInfo(player, "rpgparties.message.party.leave");
		else CommandMessageHelper.SendInfo(player, "rpgparties.message.party.disbanded");
	}

	/**
	 * Disband the party.
	 */
	public void Disband ()
	{
		Broadcast(new TextComponentTranslation("rpgparties.message.party.disbanded"));

		leader = null;

		for (EntityPlayer member : players) {
			PlayerStats stats = MMOParties.GetStatsByName ( member.getName() );
			stats.party = null;
			SendMemberUpdate(member);
		}

		players.clear();
	}

	public void SendMemberUpdate(EntityPlayer member)
	{
		if (!(member instanceof EntityPlayerMP)) return;
		MMOParties.network.sendTo(new MessageUpdateParty(""), ((EntityPlayerMP)member));
	}

	/**
	 * Broadcast a message to every member within the group.
	 * @param message The message to send.
	 */
	@Override
	public void Broadcast ( TextComponentTranslation message )
	{
		for (EntityPlayer member : players) member.sendMessage(message);
	}

	@Override
	public EntityPlayer[] GetOnlinePlayers()
	{
		return players.toArray(new EntityPlayer[] {});
	}

	@Override
	public void SendUpdate()
	{
		String[] playerNames = new String[players.size()];
		int i = 0;

		for (EntityPlayer party_player : players) {
			playerNames[i] = party_player.getName();
			i++;
		}

		for (EntityPlayer party_player : players) {
			if (!(party_player instanceof EntityPlayerMP)) return;

			MMOParties.network.sendTo(new MessageUpdateParty(String.join(",", playerNames)), ((EntityPlayerMP)party_player));
		}
	}

	@Override
	public void SendPartyMemberData(EntityPlayer member, boolean bypassLimit)
	{
		if (IsDataDifferent(member) || bypassLimit)
		{
			if (!this.pings.containsKey( member.getName() ))
				this.pings.put(member.getName(), new PlayerPing(member, 0, 0, 0, this.leader==member, 0, 0, 0, 0));

			this.pings.get( member.getName() ).Update(member.getHealth(), member.getMaxHealth(), member.getTotalArmorValue(),
					this.leader.getName()==member.getName(), member.getAbsorptionAmount(), 0, 0);

			for (EntityPlayer party_player : players) {
				if (!(party_player instanceof EntityPlayerMP)) return;

				MMOParties.network.sendTo(
						new MessageSendMemberData(
								new PartyPacketDataBuilder ()
										.SetPlayer(member.getName())
										.SetHealth(member.getHealth())
										.SetMaxHealth(member.getMaxHealth())
										.SetArmor(member.getTotalArmorValue())
										.SetLeader(this.leader.getName()==member.getName())
										.SetAbsorption(member.getAbsorptionAmount())
										.SetHunger(member.getFoodStats().getFoodLevel())
						), ((EntityPlayerMP)party_player));
			}
		}
	}

	@Override
	public boolean IsDataDifferent(EntityPlayer player)
	{
		if (!this.pings.containsKey( player.getName() ) || this.pings.get( player.getName() ).IsDifferent(player))
			return true;

		return false;
	}

	@Override
	public boolean IsMember(EntityPlayer player)
	{
		for (EntityPlayer member : players) {
			if (member.getName().equals(player.getName()))
				return true;
		}

		return false;
	}

	public boolean IsMemberOffline(EntityPlayer player)
	{
		return playersOffline.contains(player.getName());
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
	public void Teleport(EntityPlayer player, EntityPlayer target) {
		if ( ! IsMember ( target ) )
		{ CommandMessageHelper.SendError(player, "rpgparties.message.error.party"); return; }

		MMOParties.GetStatsByName( player.getName() ).StartTeleport (target);
	}
}
