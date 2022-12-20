package deathtags.stats;

import deathtags.networking.BuilderData;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerPing {
	public PlayerEntity player;
	
	public BuilderData[] additionalData;
	
	public PlayerPing(PlayerEntity player, BuilderData[] data)
	{
		this.player = player;
		this.additionalData = data;
	}

	public PlayerPing Update(BuilderData[] data)
	{
		this.additionalData = data;
		return this;
	}

	public boolean IsDifferent(PlayerEntity player) {
		// Check all of the registered values for differences.
		for (BuilderData data : additionalData) {
			if (data.IsDifferent(player)) return true;
		}
		
		return false;
	}
}
