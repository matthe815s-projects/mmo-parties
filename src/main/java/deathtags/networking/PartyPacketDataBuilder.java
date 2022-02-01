package deathtags.networking;

public class PartyPacketDataBuilder {

	public int nameLength;
	public String playerId;
	public float health = 0;
	public float maxHealth = 0;
	public float armor = 0;
	public boolean leader = false;
	public float absorption = 0;
	public float shields = 0;
	public float maxShields = 0;
	public float hunger = 0;

	public PartyPacketDataBuilder SetPlayer (String name) {
		this.nameLength = name.length();
		this.playerId = name;
		return this;
	}

	public PartyPacketDataBuilder SetHealth (float health) {
		this.health = health;
		return this;
	}

	public PartyPacketDataBuilder SetMaxHealth (float health) {
		this.maxHealth = health;
		return this;
	}

	public PartyPacketDataBuilder SetArmor (float health) {
		this.armor = health;
		return this;
	}

	public PartyPacketDataBuilder SetLeader (boolean leader) {
		this.leader = leader;
		return this;
	}

	public PartyPacketDataBuilder SetAbsorption (float absorption) {
		this.absorption = absorption;
		return this;
	}

	public PartyPacketDataBuilder SetShields (float shields) {
		this.shields = shields;
		return this;
	}

	public PartyPacketDataBuilder SetMaxShields (float health) {
		this.maxShields = health;
		return this;
	}
	
	public PartyPacketDataBuilder SetHunger (float hunger) {
		this.hunger = hunger;
		return this;
	}

}
