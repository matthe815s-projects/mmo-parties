package deathtags.stats;

import deathtags.networking.PartyPacketDataBuilder;

public class PartyMemberData {
	public String name;
	public float health;
	public float maxHealth;
	public float armor;
	public boolean leader = false;
	public float absorption;
	public float shields;
	public float maxShields;
	public float hunger;
	
	public PartyMemberData(String health, String maxHealth, String armor, String leader, String absorption, String shields, String maxShields) {
		this.health = Float.parseFloat(health);
		this.maxHealth = Float.parseFloat(maxHealth);
		this.armor = Float.parseFloat(armor);
		this.leader = Boolean.parseBoolean(leader);
		this.absorption = Float.parseFloat(absorption);
		this.shields = Float.parseFloat(shields);
		this.maxShields = Float.parseFloat(maxShields);
	}

	public PartyMemberData(PartyPacketDataBuilder builder) {
		this.name = builder.playerId;
		this.health = builder.health;
		this.maxHealth = builder.maxHealth;
		this.armor = builder.armor;
		this.leader = builder.leader;
		this.absorption = builder.absorption;
		this.shields = builder.shields;
		this.maxShields = builder.maxShields;
		this.hunger = builder.hunger;
	}
}
