package deathtags.stats;

import deathtags.core.MMOParties;
import deathtags.networking.BuilderData;
import deathtags.networking.PartyPacketDataBuilder;

public class PartyMemberData {
	public String name = "";
	public float health = 0;
	public float maxHealth = 20;
	public float armor = 0;
	public boolean leader = false;
	public float absorption = 0;
	public float shields = 0;
	public float maxShields = 0;
	public float hunger = 0;
	public BuilderData[] additionalData = new BuilderData[0];

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
		this.additionalData = builder.data;
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
