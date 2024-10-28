package dev.matthe815.mmoparties.common.core;

import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.networking.PartyPacketDataBuilder;
import dev.matthe815.mmoparties.common.networking.builders.*;
import dev.matthe815.mmoparties.common.stats.Party;
import dev.matthe815.mmoparties.common.stats.PlayerStats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

import java.util.*;
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

	public static Map<EntityPlayer, PlayerStats> PlayerStats = new HashMap<>();

	public static KeyBinding OPEN_GUI_KEY;
	public static Side runningSide;

	public MMOPartiesCommon() {}

	public void initialize()
	{
		// Registers all standard UI elements for the base mod barring any compatibility mods.
		// Includes: Leader crown, name, status effects, health, absorption, hunger, and armor.
		// Rendering occurs in the order of registration.
		RegisterCompatibility(new BuilderLeader(), new BuilderLeader.Renderer(), runningSide == Side.SERVER);
		RegisterCompatibility(new BuilderName(), new BuilderName.Renderer(), runningSide == Side.SERVER);
		RegisterCompatibility(new BuilderHealth(), new BuilderHealth.NuggetBar(), runningSide == Side.SERVER);
		RegisterCompatibility(new BuilderAbsorption(), new BuilderAbsorption.NuggetBar(), runningSide == Side.SERVER);
		RegisterCompatibility(new BuilderHunger(), new BuilderHunger.NuggetBar(), runningSide == Side.SERVER);
		RegisterCompatibility(new BuilderArmor(), new BuilderArmor.NuggetBar(), runningSide == Side.SERVER);
		RegisterCompatibility(new BuilderWaypoint(), new BuilderWaypoint.NuggetBar(), runningSide == Side.SERVER);
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
		for (Entry<EntityPlayer, dev.matthe815.mmoparties.common.stats.PlayerStats> plr : PlayerStats.entrySet()) {
			if ( plr.getKey().getName().equals(username) )
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
	public static PlayerStats GetStats(EntityPlayer player)
	{
		return GetStatsByName(player.getName());
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
        List<PartyList.NuggetBar> bars = new ArrayList<>(Arrays.asList(PartyList.nuggetBars));
		bars.add(bar);
		PartyList.nuggetBars = bars.toArray(new PartyList.NuggetBar[0]); // Convert the list to an array.
	}
}
