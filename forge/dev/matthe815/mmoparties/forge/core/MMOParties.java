package dev.matthe815.mmoparties.forge.core;

import com.mojang.blaze3d.platform.InputConstants;
import dev.matthe815.mmoparties.common.core.MMOPartiesCommon;
import dev.matthe815.mmoparties.forge.api.PartyHelper;
import dev.matthe815.mmoparties.forge.commands.PartyCommand;
import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import dev.matthe815.mmoparties.forge.events.EventClientForge;
import dev.matthe815.mmoparties.forge.events.EventCommonForge;
import dev.matthe815.mmoparties.forge.gui.PartyListForge;
import dev.matthe815.mmoparties.forge.networking.*;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
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
// import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.lwjgl.glfw.GLFW;

/**
 * The entry point for the MMO parties mod.
 * Contains some top-level cache management functionality.
 * @author Matthe815
 */
@Mod(value = MMOParties.MODID)
public class MMOParties extends MMOPartiesCommon {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel network = NetworkRegistry.newSimpleChannel(
	new ResourceLocation(MODID, "main"),
		() -> PROTOCOL_VERSION,
		PROTOCOL_VERSION::equals,
		PROTOCOL_VERSION::equals
	);
	
	public MMOParties ()
	{
		super(FMLEnvironment.dist == Dist.DEDICATED_SERVER);

		// Construct configuration
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);

		// Construct game events.
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::OnSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::OnClientInitialize);

		if (FMLEnvironment.dist == Dist.CLIENT) {
			FMLJavaModLoadingContext.get().getModEventBus().addListener(this::KeyBinds);
		}

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

		ConfigHolder.insertConfig();

		// Register event handlerse
		MinecraftForge.EVENT_BUS.register(new EventCommonForge());
		MinecraftForge.EVENT_BUS.register(new EventClientForge());
		MinecraftForge.EVENT_BUS.register(new PartyListForge());
	}

	/**
	 * Handles setting up all common networking packet types; there are special types for Server and Client setup.
	 */
	public void SetupNetworking()
	{
		network.messageBuilder(MessageUpdateParty.class, 0)
				.encoder(MessageUpdateParty::encode)
				.decoder(MessageUpdateParty::decode)
				.consumerMainThread(MessageUpdateParty.Handler::handle)
				.add();
		network.messageBuilder(MessageSendMemberData.class, 1)
				.encoder(MessageSendMemberData::encode)
				.decoder(MessageSendMemberData::decode)
				.consumerMainThread(MessageSendMemberData.Handler::handle)
				.add();
		network.messageBuilder(MessageHandleMenuAction.class, 2)
				.encoder(MessageHandleMenuAction::encode)
				.decoder(MessageHandleMenuAction::decode)
				.consumerMainThread(MessageHandleMenuAction.Handler::handle)
				.add();
		network.messageBuilder(MessagePartyInvite.class, 3)
				.encoder(MessagePartyInvite::encode)
				.decoder(MessagePartyInvite::decode)
				.consumerMainThread(MessagePartyInvite.Handler::handle)
				.add();
		network.messageBuilder(MessageOpenUI.class,5)
				.encoder(MessageOpenUI::encode)
				.decoder(MessageOpenUI::decode)
				.consumerMainThread(MessageOpenUI.Handler::handleServer)
				.add();
	}

	public void OnServerInitialize(ServerStartingEvent event) {
		PartyHelper.Server.server = event.getServer(); // Set server instance
	}

	public void KeyBinds(RegisterKeyMappingsEvent event) {
		// Creates and registers the key-binding on a universal scale.
		OPEN_GUI_KEY = new KeyMapping("key.opengui.desc", KeyConflictContext.UNIVERSAL, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, "key.mmoparties.category"); // Open GUI on P.
		event.register(OPEN_GUI_KEY);
	}

	/**
	 * Fired when a client is being setup during mod initialization.
	 * Does not run on the server.
	 * @param event
	 */
	public void OnClientInitialize(FMLClientSetupEvent event)
	{}

	/**
	 * Handles registering the mod commands as well as permissions.
	 */
	public void OnCommandRegister(RegisterCommandsEvent event)
	{
		event.getDispatcher().register(PartyCommand.register());
	}
}
