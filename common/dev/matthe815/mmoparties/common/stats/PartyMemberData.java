package dev.matthe815.mmoparties.common.stats;

import dev.matthe815.mmoparties.common.networking.builders.BuilderData;
import dev.matthe815.mmoparties.common.networking.PartyPacketDataBuilder;
import net.minecraft.world.entity.player.Player;

public class PartyMemberData {
	public String name;
	public boolean leader = false;
	public BuilderData[] additionalData;

	public PartyMemberData(PartyPacketDataBuilder builder) {
		this.name = builder.playerId;
		this.additionalData = builder.data;
	}

	public boolean IsDifferent(Player player) {
		// No data bypass
		if (additionalData == null) return true;

		// Check all of the registered values for differences.
		for (BuilderData data : additionalData) {
			if (data == null) return true;
			if (data.IsDifferent(player)) return true;
		}

		return false;
	}
}
