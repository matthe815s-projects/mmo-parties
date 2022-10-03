package deathtags.stats;

import epicsquid.superiorshields.capability.shield.IShieldCapability;
import epicsquid.superiorshields.capability.shield.SuperiorShieldsCapabilityManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;

public class PlayerPing {
	public EntityPlayer player;
	
	public float health;
	public float maxHealth;
	public float armor;
	public boolean leader = false;
	public float absorption;
	public float shields;
	public float maxShields;
	public float hunger;
	
	public PlayerPing(EntityPlayer player, float health, float maxHealth, float armor, boolean leader, float absorption, float shields, float maxShields, float hunger)
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

	public boolean IsDifferent(EntityPlayer player) {
		if (Loader.isModLoaded("superiorshields")) {
			IShieldCapability shields = player.getCapability(SuperiorShieldsCapabilityManager.shieldCapability, null);	
			
			if (player.getHealth() != this.health || player.getMaxHealth() != this.maxHealth 
				|| player.getTotalArmorValue() != this.armor || player.getAbsorptionAmount() != this.absorption 
				|| shields.getCurrentHp() != this.shields || shields.getMaxHp() != this.maxShields)
			return true;
		} else 
			if (player.getHealth() != this.health || player.getMaxHealth() != this.maxHealth 
				|| player.getTotalArmorValue() != this.armor || player.getAbsorptionAmount() != this.absorption)
			return true;
		
		return false;
	}
}
