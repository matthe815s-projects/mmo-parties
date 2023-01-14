package deathtags.stats;

import deathtags.core.MMOParties;
import deathtags.networking.BuilderData;
import deathtags.networking.PartyPacketDataBuilder;
import net.minecraft.entity.player.PlayerEntity;

public class PartyMemberData {
	public String name;
	public boolean leader = false;
	public BuilderData[] additionalData;

	public PartyMemberData(PartyPacketDataBuilder builder) {
		this.additionalData = builder.data;
		this.name = builder.playerId;
	}

	public boolean IsDifferent(PlayerEntity player) {
		// Check all of the registered values for differences.
		for (BuilderData data : additionalData) {
			if (data.IsDifferent(player)) return true;
		}

		return false;
	}
}
