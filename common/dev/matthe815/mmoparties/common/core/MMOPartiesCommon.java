package dev.matthe815.mmoparties.common.core;

import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.networking.PartyPacketDataBuilder;
import dev.matthe815.mmoparties.common.networking.builders.*;
import dev.matthe815.mmoparties.common.stats.Party;
import dev.matthe815.mmoparties.common.stats.PlayerStats;
import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;

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

	public static Map<PlayerEntity, PlayerStats> PlayerStats = new HashMap<>();

	public static KeyBinding OPEN_GUI_KEY;

	public MMOPartiesCommon()
	{
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);
	}

	public void initialize()
	{
		// Registers all standard UI elements for the base mod barring any compatibility mods.
		// Includes: Leader crown, name, status effects, health, absorption, hunger, and armor.
		// Rendering occurs in the order of registration.
		RegisterCompatibility(new BuilderLeader(), new BuilderLeader.Renderer());
		RegisterCompatibility(new BuilderName(), new BuilderName.Renderer());
		RegisterCompatibility(new BuilderHealth(), new BuilderHealth.NuggetBar());
		RegisterCompatibility(new BuilderAbsorption(), new BuilderAbsorption.NuggetBar());
		RegisterCompatibility(new BuilderHunger(), new BuilderHunger.NuggetBar());
		RegisterCompatibility(new BuilderArmor(), new BuilderArmor.NuggetBar());
		RegisterCompatibility(new BuilderWaypoint(), new BuilderWaypoint.NuggetBar());
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
		for (Entry<PlayerEntity, dev.matthe815.mmoparties.common.stats.PlayerStats> plr : PlayerStats.entrySet()) {
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
	public static PlayerStats GetStats(PlayerEntity player)
	{
		return GetStatsByName(player.getName().getString());
	}

	/**
	 * Register a new mod compatibility and nugget bar.
	 * @param bar
	 */
	public static void RegisterCompatibility(BuilderData builder, PartyList.NuggetBar bar)
	{
		PartyPacketDataBuilder.builderData.add(builder);

		if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) return;

		// Make a bigger array and clone it.
        List<PartyList.NuggetBar> bars = new ArrayList<>(Arrays.asList(PartyList.nuggetBars));
		bars.add(bar);
		PartyList.nuggetBars = bars.toArray(new PartyList.NuggetBar[0]); // Convert the list to an array.
	}
}
