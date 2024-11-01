package dev.matthe815.mmoparties.common.core;

import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.networking.PartyPacketDataBuilder;
import dev.matthe815.mmoparties.common.networking.builders.*;
import dev.matthe815.mmoparties.common.stats.Party;
import dev.matthe815.mmoparties.common.stats.PlayerStats;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The entry point for the MMO parties mod.
 * Contains some top-level cache management functionality.
 * @author Matthe815
 */
public class MMOPartiesCommon {
	public static final String MODID = "mmoparties";
	public static Party localParty;
	public static String partyInviter;

	public static Map<Player, PlayerStats> PlayerStats = new HashMap<>();

	public static KeyMapping OPEN_GUI_KEY;

	public MMOPartiesCommon(boolean isDedicatedServer)
	{
		// Registers all standard UI elements for the base mod barring any compatibility mods.
		// Includes: Leader crown, name, status effects, health, absorption, hunger, and armor.
		// Rendering occurs in the order of registration.
		RegisterCompatibility(new BuilderLeader(), new BuilderLeader.Renderer(), isDedicatedServer);
		RegisterCompatibility(new BuilderName(), new BuilderName.Renderer(), isDedicatedServer);
		RegisterCompatibility(new BuilderHealth(), new BuilderHealth.NuggetBar(), isDedicatedServer);
		RegisterCompatibility(new BuilderAbsorption(), new BuilderAbsorption.NuggetBar(), isDedicatedServer);
		RegisterCompatibility(new BuilderHunger(), new BuilderHunger.NuggetBar(), isDedicatedServer);
		RegisterCompatibility(new BuilderArmor(), new BuilderArmor.NuggetBar(), isDedicatedServer);
	}

	/**
	 * Get the player datastore from their username.
	 * This datastore is temporary and removed upon disconnecting.
	 * @param username
	 * @return Player Datastore
	 * @apiNote Can use MMOParties#GetStats as well.
	 */
	public static PlayerStats GetStatsByName(String username)
	{
		for (Entry<Player, dev.matthe815.mmoparties.common.stats.PlayerStats> plr : PlayerStats.entrySet()) {
			if ( plr.getKey().getName().getString().equals(username) )
				return plr.getValue();
		}

		return null;
	}

	/**
	 * Get the player database from their PlayerEntity
	 * This datastore is temporary and removed upon disconnecting.
	 * @param player
	 * @return Player Datastore
	 */
	public static PlayerStats GetStats(Player player)
	{
		return GetStatsByName(player.getName().getString());
	}

	/**
	 * Register a new mod compatibility and nugget bar.
	 * @param bar
	 */
	public static void RegisterCompatibility(BuilderData builder, PartyList.NuggetBar bar, boolean isDedicatedServer)
	{
		PartyPacketDataBuilder.builderData.add(builder);

		if (isDedicatedServer) return;

		// Make a bigger array and clone it.
		List<PartyList.NuggetBar> bars = new ArrayList<>();

		for (int i = 0; i< PartyList.nuggetBars.length; i++) {
			bars.add(PartyList.nuggetBars[i]);
		}

		bars.add(bar);
		PartyList.nuggetBars = bars.toArray(new PartyList.NuggetBar[0]); // Convert the list to an array.
	}
}
