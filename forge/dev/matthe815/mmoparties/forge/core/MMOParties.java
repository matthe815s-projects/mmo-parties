package dev.matthe815.mmoparties.forge.core;

import dev.matthe815.mmoparties.common.core.MMOPartiesCommon;
import dev.matthe815.mmoparties.common.events.EventClient;
import dev.matthe815.mmoparties.common.events.EventCommon;
import dev.matthe815.mmoparties.forge.commands.PartyCommand;
import dev.matthe815.mmoparties.forge.events.EventClientForge;
import dev.matthe815.mmoparties.forge.events.EventCommonForge;
import dev.matthe815.mmoparties.forge.gui.PartyListForge;
import dev.matthe815.mmoparties.forge.networking.*;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * The entry point for the MMO parties mod.
 * Contains some top-level cache management functionality.
 * @author Matthe815
 */
@Mod(modid = MMOParties.MODID)
public class MMOParties extends MMOPartiesCommon {
	public static SimpleNetworkWrapper network;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		System.out.println(MODID + " is pre-loading!");
		runningSide = event.getSide();
		this.initialize();
		network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		System.out.println(MODID + " is loading!");

		this.SetupNetworking();

		if (event.getSide() == Side.CLIENT) {
			MinecraftForge.EVENT_BUS.register(new EventClientForge());
			MinecraftForge.EVENT_BUS.register(new PartyListForge());
			clientSetup();
		}

		MinecraftForge.EVENT_BUS.register(new EventCommonForge());
	}

	public void clientSetup() {
		OPEN_GUI_KEY = new KeyBinding("key.opengui.desc", KeyConflictContext.UNIVERSAL, 25, "key.mmoparties.category"); // Open GUI on G.
		ClientRegistry.registerKeyBinding(OPEN_GUI_KEY);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		System.out.println(MODID + " is post-loading!");
	}

	@Mod.EventHandler
	public static void serverStarting(FMLServerStartingEvent event)
	{
		System.out.println("Registering commands");

		event.registerServerCommand(new PartyCommand());
	}

	/**
	 * Handles setting up all common networking packet types; there are special types for Server and Client setup.
	 */
	public void SetupNetworking()
	{
		network.registerMessage(MessageUpdateParty.Handler.class, MessageUpdateParty.class, 0, Side.CLIENT);
		network.registerMessage(MessageSendMemberData.Handler.class, MessageSendMemberData.class, 1, Side.CLIENT);
		network.registerMessage(MessageHandleMenuAction.Handler.class, MessageHandleMenuAction.class, 2, Side.CLIENT);
		network.registerMessage(MessagePartyInvite.Handler.class, MessagePartyInvite.class, 3, Side.CLIENT);

		network.registerMessage(MessageUpdateParty.Handler.class, MessageUpdateParty.class, 0, Side.SERVER);
		network.registerMessage(MessageSendMemberData.Handler.class, MessageSendMemberData.class, 1, Side.SERVER);
		network.registerMessage(MessageHandleMenuAction.Handler.class, MessageHandleMenuAction.class, 2, Side.SERVER);
	}
}
