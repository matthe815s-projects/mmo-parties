package deathtags.networking;

import javafx.util.Builder;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class PartyPacketDataBuilder {

	public PlayerEntity player;
	public int nameLength;
	public String playerId;
	public float health = 0;
	public float maxHealth = 20;
	public float armor = 0;
	public boolean leader = false;
	public float absorption = 0;
	public float shields = 0;
	public float maxShields = 0;
	public float hunger = 0;
	public BuilderData[] data;

	public static List<BuilderData> builderData = new ArrayList<>();

	public PartyPacketDataBuilder() {
		this.data = new BuilderData[builderData.size()];
	}

	public PartyPacketDataBuilder SetPlayer (PlayerEntity player) {
		this.nameLength = player.getName().getContents().length();
		this.playerId = player.getName().getContents();
		this.player = player;
		return this;
	}

	public PartyPacketDataBuilder SetName (String name) {
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

	public PartyPacketDataBuilder AddData (int index, BuilderData data) {
		this.data[index] = data;
		return this;
	}

}
