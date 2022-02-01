package deathtags.stats;

import java.util.ArrayList;
import java.util.List;
import deathtags.core.MMOParties;
import deathtags.networking.MessageAllianceSendMemberData;
import deathtags.networking.MessageAllianceUpdateParty;
import epicsquid.superiorshields.capability.shield.IShieldCapability;
import epicsquid.superiorshields.capability.shield.SuperiorShieldsCapabilityManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.Loader;

public class Alliance extends PlayerGroup {
	public List<Party> parties = new ArrayList<Party>();

	@Override
	public void SendUpdate() {
		for(Party party : parties) {
			List<EntityPlayerMP> players = party.players;
			
			String[] playerNames = new String[players.size()];
			int i = 0;
			
			for (EntityPlayerMP party_player : players) {
				playerNames[i] = party_player.getName();
				i++;
			}
			
			for (EntityPlayerMP party_player : players) {
				MMOParties.network.sendTo(new MessageAllianceUpdateParty(String.join(",", playerNames), this.parties.indexOf(party)), party_player);
			}	
		}
	}

	@Override
	public void SendPartyMemberData(EntityPlayerMP member, boolean bypassLimit) {
		if (IsDataDifferent(member) || bypassLimit)
		{
			if (!this.pings.containsKey(member.getName()))
				this.pings.put(member.getName(), new PlayerPing(member, 0, 0, 0, bypassLimit, 0, 0, 0, 0));
			
			if (Loader.isModLoaded("superiorshields")) {
				IShieldCapability shields = member.getCapability(SuperiorShieldsCapabilityManager.shieldCapability, null);
					
				this.pings.get(member.getName()).Update(member.getHealth(), member.getMaxHealth(), member.getTotalArmorValue(), 
					this.leader==member, member.getAbsorptionAmount(), shields.getCurrentHp(), shields.getMaxHp());
			} else
				this.pings.get(member.getName()).Update(member.getHealth(), member.getMaxHealth(), member.getTotalArmorValue(), 
					this.leader==member, member.getAbsorptionAmount(), 0, 0);
			
			for(Party party : parties) {
				List<EntityPlayerMP> players = party.players;
				
				for (EntityPlayerMP party_player : players) {
					if (Loader.isModLoaded("superiorshields")) {
						IShieldCapability shields = member.getCapability(SuperiorShieldsCapabilityManager.shieldCapability, null);
					MMOParties.network.sendTo(new MessageAllianceSendMemberData(String.join(",", new String[] {
							member.getName(), 
							Integer.toString((int)member.getHealth()), 
							Integer.toString((int)member.getMaxHealth()),
							Integer.toString((int)member.getTotalArmorValue()), 
							Boolean.toString(this.leader==member),
							Integer.toString((int)member.getAbsorptionAmount()),
							Integer.toString((int)shields.getCurrentHp()),
							Integer.toString((int)shields.getMaxHp())
						}
					), this.parties.indexOf(party)), party_player);
					} else {
						MMOParties.network.sendTo(new MessageAllianceSendMemberData(String.join(",", new String[] {
								member.getName(), 
								Integer.toString((int)member.getHealth()), 
								Integer.toString((int)member.getMaxHealth()),
								Integer.toString((int)member.getTotalArmorValue()), 
								Boolean.toString(this.leader==member),
								Integer.toString((int)member.getAbsorptionAmount()),
								Integer.toString(0),
								Integer.toString(0)
							}
						), this.parties.indexOf(party)), party_player);
					}
				}
			}
		}
		
	}

	@Override
	public boolean IsDataDifferent(EntityPlayerMP member) {
		for(Party party : parties)
			if (party.IsDataDifferent(member))
				return true;
		
		return false;
	}

	@Override
	public boolean IsAllDead() {
		int numDead = 0;
		int allianceSize = 0;
		
		for(Party party : parties) {
			List<EntityPlayerMP> players = party.players;
			
			for (EntityPlayerMP player : players) {
				allianceSize++;
				if (player.isSpectator())
					numDead++;
			}
		}
		
		return numDead == allianceSize;
		
	}

	@Override
	public void ReviveAll() {
		for(Party party : parties) {
			List<EntityPlayerMP> players = party.players;
		
			for (EntityPlayerMP player : players) {
				if (player.isSpectator()) {
					player.setPosition(player.bedLocation.getX(), player.bedLocation.getY(), player.bedLocation.getZ());
					player.setGameType(GameType.SURVIVAL);
				}
			}
		}
	}

	@Override
	public void Broadcast(String message) {
		for(Party party : parties) {
			List<EntityPlayerMP> players = party.players;
		
			for (EntityPlayerMP player : players) {
				player.sendMessage(new TextComponentString(message));
			}
		}
	}

	@Override
	public EntityPlayerMP[] GetOnlinePlayers() {
		List<EntityPlayerMP> all_players = new ArrayList<EntityPlayerMP>();
		
		for(Party party : parties) {
			List<EntityPlayerMP> players = party.players;
		
			for (EntityPlayerMP player : players)
				all_players.add(player);
		}
		
		return all_players.toArray(new EntityPlayerMP[] {});
	}

	@Override
	public boolean IsMember(EntityPlayerMP player) {
		for(Party party : parties) {
			List<EntityPlayerMP> players = party.players;
			
			for (EntityPlayerMP member : players) {
				if (member.getName().equals(player.getName()))
					return true;
			}
		}
		
		return false;
	}

	@Override
	public String GetGroupAlias() {
		return "alliance";
	}

	public void Join(Party party) {
		party.alliance = this;
		this.parties.add(party);
		
		this.Broadcast(String.format("%s's party has joined your alliance!", party.leader.getName()));
		
		for(Party pparty : parties) {
			for (EntityPlayerMP party_player : pparty.players) {
				SendPartyMemberData(party_player, true);
			}	
		}
		
		SendUpdate();
	}
}
