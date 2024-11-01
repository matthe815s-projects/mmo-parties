package dev.matthe815.mmoparties.forge.core;

import dev.matthe815.mmoparties.common.core.MMOPartiesCommon;
import dev.matthe815.mmoparties.forge.commands.PartyCommand;
import dev.matthe815.mmoparties.forge.events.EventClientForge;
import dev.matthe815.mmoparties.forge.events.EventCommonForge;
import dev.matthe815.mmoparties.forge.gui.PartyListForge;
import dev.matthe815.mmoparties.forge.networking.*;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import org.lwjgl.glfw.GLFW;

/**
 * The entry point for the MMO parties mod.
 * Contains some top-level cache management functionality.
 * @author Matthe815
 */
@Mod(value = MMOPartiesCommon.MODID)
public class MMOParties extends MMOPartiesCommon {
	private static final String PROTOCOL_VERSION = "2";
	public static final SimpleChannel network = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(MODID, "sync"))
			.clientAcceptedVersions(s -> true)
			.serverAcceptedVersions(s -> true)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();

	public MMOParties() {
		super();

		// Construct game events.
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::OnSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::OnClientInitialize);

		MinecraftForge.EVENT_BUS.addListener(this::OnCommandRegister);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void OnSetup(FMLCommonSetupEvent event)
	{
		System.out.println(MODID + " is pre-loading!");
		this.initialize();
		this.init();
	}

	public void OnClientInitialize(FMLClientSetupEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new EventClientForge());
		MinecraftForge.EVENT_BUS.register(new PartyListForge());
		clientSetup();
	}

	public void init()
	{
		System.out.println(MODID + " is loading!");
		this.SetupNetworking();
		MinecraftForge.EVENT_BUS.register(new EventCommonForge());
	}

	public void clientSetup() {
		OPEN_GUI_KEY = new KeyBinding("key.opengui.desc", KeyConflictContext.UNIVERSAL, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_P, "key.mmoparties.category"); // Open GUI on G.
		ClientRegistry.registerKeyBinding(OPEN_GUI_KEY);
	}

	public void OnCommandRegister(RegisterCommandsEvent event)
	{
		event.getDispatcher().register(PartyCommand.register());
		PermissionAPI.registerNode("rpgparties.*", DefaultPermissionLevel.ALL, "The base permission");
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
}
