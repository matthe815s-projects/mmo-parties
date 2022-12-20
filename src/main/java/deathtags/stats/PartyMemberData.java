package deathtags.stats;

import deathtags.core.MMOParties;
import deathtags.networking.BuilderData;
import deathtags.networking.PartyPacketDataBuilder;

public class PartyMemberData {
	public String name;
	public boolean leader = false;
	public BuilderData[] additionalData;

	public PartyMemberData(PartyPacketDataBuilder builder) {
		this.additionalData = builder.data;
		this.name = builder.playerId;
	}
}
