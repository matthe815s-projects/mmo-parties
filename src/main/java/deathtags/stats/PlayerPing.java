package deathtags.stats;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerPing {
	public PlayerEntity player;
	
	public float health;
	public float maxHealth;
	public float armor;
	public boolean leader = false;
	public float absorption;
	public float shields;
	public float maxShields;
	public float hunger;
	
	public PlayerPing(PlayerEntity player, float health, float maxHealth, float armor, boolean leader, float absorption, float shields, float maxShields, float hunger) 
	{
		this.player = player;
		
		this.health = health;
		this.maxHealth = maxHealth;
		this.armor = armor;
		this.leader = leader;
		this.absorption = absorption;
		this.shields = shields;
		this.maxShields = maxShields;
		this.hunger = hunger;
	}
	
	public PlayerPing Update(float health, float maxHealth, float armor, boolean leader, float absorption, float shields, float maxShields)
	{
		this.health = health;
		this.maxHealth = maxHealth;
		this.armor = armor;
		this.leader = leader;
		this.absorption = absorption;
		this.shields = shields;
		this.maxShields = maxShields;
		
		return this;
	}

	public boolean IsDifferent(PlayerEntity player) {
		if (player.getHealth() != this.health || player.getMaxHealth() != this.maxHealth 
			|| player.getArmorValue() != this.armor || player.getAbsorptionAmount() != this.absorption)
				return true;
		
		return false;
	}
}
