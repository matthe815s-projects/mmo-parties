package deathtags.core;

import java.util.*;
import java.util.Map.Entry;

import com.mojang.blaze3d.platform.InputConstants;
import deathtags.api.PartyHelper;
import deathtags.commands.PartyCommand;
import deathtags.config.ConfigHolder;
import deathtags.core.events.EventClient;
import deathtags.core.events.EventCommon;
import deathtags.gui.PartyList;
import deathtags.gui.builders.*;
import deathtags.networking.*;
import deathtags.stats.Party;
import deathtags.stats.PlayerStats;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.lwjgl.glfw.GLFW;

/**
 * The entry point for the MMO parties mod.
 * Contains some top-level cache management functionality.
 * @author Matthe815
 */
@Mod(value = MMOParties.MODID)
public class MMOParties {

	public static final String MODID = "mmoparties";

	public static Party localParty;
	public static String partyInviter;

	public static Map<Player, PlayerStats> PlayerStats = new HashMap<>();

	private static final String PROTOCOL_VERSION = "2";

	public static KeyMapping OPEN_GUI_KEY;
	
	public static final SimpleChannel network = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(MODID, "sync"))
			.clientAcceptedVersions(s -> true)
			.serverAcceptedVersions(s -> true)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();
	
	public MMOParties ()
	{
		// Construct configuration
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);

		// Registers all standard UI elements for the base mod barring any compatibility mods.
		// Includes: Leader crown, name, status effects, health, absorption, hunger, and armor.
		// Rendering occurs in the order of registration.
		RegisterCompatibility(new BuilderLeader(), new BuilderLeader.Renderer());
		RegisterCompatibility(new BuilderName(), new BuilderName.Renderer());
		RegisterCompatibility(new BuilderHealth(), new BuilderHealth.NuggetBar());
		RegisterCompatibility(new BuilderAbsorption(), new BuilderAbsorption.NuggetBar());
		RegisterCompatibility(new BuilderHunger(), new BuilderHunger.NuggetBar());
		RegisterCompatibility(new BuilderArmor(), new BuilderArmor.NuggetBar());

		// Construct game events.
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::OnSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::OnClientInitialize);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::KeyBinds);

		MinecraftForge.EVENT_BUS.addListener(this::OnCommandRegister);
		MinecraftForge.EVENT_BUS.addListener(this::OnServerInitialize);

		MinecraftForge.EVENT_BUS.register(this);
	}

	/**
	 * Runs when the mod is constructed and setups up the networking and bus events.
	 * @param event
	 */
	public void OnSetup(FMLCommonSetupEvent event)
	{
		// Sets up all of the network packet handlers.
		SetupNetworking();

		// Register event handlerse
		MinecraftForge.EVENT_BUS.register(new EventCommon());
		MinecraftForge.EVENT_BUS.register(new EventClient());
	}

	/**
	 * Handles setting up all common networking packet types; there are special types for Server and Client setup.
	 */
	public void SetupNetworking()
	{
		network.registerMessage(1, MessageUpdateParty.class, MessageUpdateParty::encode, MessageUpdateParty::decode, MessageUpdateParty.Handler::handle );
		network.registerMessage(2, MessageSendMemberData.class, MessageSendMemberData::encode, MessageSendMemberData::decode, MessageSendMemberData.Handler::handle);
		network.registerMessage(3, MessageHandleMenuAction.class, MessageHandleMenuAction::encode, MessageHandleMenuAction::decode, MessageHandleMenuAction.Handler::handle);
		network.registerMessage(4, MessagePartyInvite.class, MessagePartyInvite::encode, MessagePartyInvite::decode, MessagePartyInvite.Handler::handle);
	}

	public void OnServerInitialize(ServerStartingEvent event) {
		PartyHelper.Server.server = event.getServer(); // Set server instance
		network.registerMessage(5, MessageOpenUI.class, MessageOpenUI::encode, MessageOpenUI::decode, MessageOpenUI.Handler::handleServer);
	}

	public void KeyBinds(RegisterKeyMappingsEvent event) {
		event.register(OPEN_GUI_KEY);
	}

	/**
	 * Fired when a client is being setup during mod initialization.
	 * Does not run on the server.
	 * @param event
	 */
	public void OnClientInitialize(FMLClientSetupEvent event)
	{
		PartyList.init(); // Initializes the Party renderer.
		network.registerMessage(5, MessageOpenUI.class, MessageOpenUI::encode, MessageOpenUI::decode, MessageOpenUI.Handler::handle); // A special handler for single-player instances.

		// Creates and registers the key-binding on a universal scale.
		OPEN_GUI_KEY = new KeyMapping("key.opengui.desc", KeyConflictContext.UNIVERSAL, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, "key.mmoparties.category"); // Open GUI on G.
	}

	/**
	 * Handles registering the mod commands as well as permissions.
	 */
	public void OnCommandRegister(RegisterCommandsEvent event)
	{	
		event.getDispatcher().register(PartyCommand.register());
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
		for (Entry<Player, deathtags.stats.PlayerStats> plr : PlayerStats.entrySet()) {
			if ( plr.getKey().getName().getContents().equals(username) )
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
	public static void RegisterCompatibility(BuilderData builder, PartyList.NuggetBar bar)
	{
		PartyPacketDataBuilder.builderData.add(builder);

		if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) return;

		// Make a bigger array and clone it.
		List<PartyList.NuggetBar> bars = new ArrayList<>();

		for (int i = 0; i< PartyList.nuggetBars.length; i++) {
			bars.add(PartyList.nuggetBars[i]);
		}

		bars.add(bar);
		PartyList.nuggetBars = bars.toArray(new PartyList.NuggetBar[0]); // Convert the list to an array.
	}
}
