package deathtags.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import deathtags.commands.*;
import deathtags.core.events.EventClient;
import deathtags.core.events.EventCommon;
import deathtags.gui.PartyList;
import deathtags.gui.builders.*;
import deathtags.networking.*;
import deathtags.stats.Party;
import deathtags.stats.PlayerStats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
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

@Mod(modid = MMOParties.MODID, version = MMOParties.VERSION, name = MMOParties.NAME)
public class MMOParties {

	public static final String MODID = "mmoparties";
	public static final String VERSION = "2.3.0";
	public static final String NAME = "RPG Parties";
	
	public static SimpleNetworkWrapper network;

	public static Party localParty;
	public static String partyInviter;
	public static Map<EntityPlayer, PlayerStats> PlayerStats = new HashMap<>();
	public static KeyBinding OPEN_GUI_KEY;

	public static Side runningSide;

	public static boolean DEBUGGING_ENABLED = false;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) 
	{
		System.out.println(MODID + " is pre-loading!");

		runningSide = event.getSide();

		RegisterCompatibility(new BuilderLeader(), new BuilderLeader.Renderer());
		RegisterCompatibility(new BuilderName(), new BuilderName.Renderer());
		RegisterCompatibility(new BuilderHealth(), new BuilderHealth.NuggetBar());
		RegisterCompatibility(new BuilderAbsorption(), new BuilderAbsorption.NuggetBar());
		RegisterCompatibility(new BuilderHunger(), new BuilderHunger.NuggetBar());
		RegisterCompatibility(new BuilderArmor(), new BuilderArmor.NuggetBar());
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) 
	{
		System.out.println(MODID + " is loading!");
		
	    network.registerMessage(MessageUpdateParty.Handler.class, MessageUpdateParty.class, 0, Side.CLIENT);
	    network.registerMessage(MessageSendMemberData.Handler.class, MessageSendMemberData.class, 1, Side.CLIENT);
		network.registerMessage(MessageHandleMenuAction.Handler.class, MessageHandleMenuAction.class, 2, Side.CLIENT);
		network.registerMessage(MessagePartyInvite.Handler.class, MessagePartyInvite.class, 3, Side.CLIENT);
	    
	    network.registerMessage(MessageUpdateParty.Handler.class, MessageUpdateParty.class, 0, Side.SERVER);
	    network.registerMessage(MessageSendMemberData.Handler.class, MessageSendMemberData.class, 1, Side.SERVER);
		network.registerMessage(MessageHandleMenuAction.Handler.class, MessageHandleMenuAction.class, 2, Side.SERVER);

		if (event.getSide() == Side.CLIENT) {
			MinecraftForge.EVENT_BUS.register(new EventClient());
			clientSetup();
		}

		MinecraftForge.EVENT_BUS.register(new EventCommon());

	}

	public void clientSetup() {
		OPEN_GUI_KEY = new KeyBinding("key.opengui.desc", KeyConflictContext.UNIVERSAL, 25, "key.mmoparties.category"); // Open GUI on G.
		ClientRegistry.registerKeyBinding(OPEN_GUI_KEY);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) 
	{
		System.out.println(MODID + " is post-loading!");
		
	    if (event.getSide() == Side.CLIENT)
	        PartyList.init();
	}
	
	@Mod.EventHandler
	public static void serverStarting(FMLServerStartingEvent event)
	{
		System.out.println("Registering commands");
		
		event.registerServerCommand(new PartyCommand());
	}
	
	/**
	 * Get the stat value of a player by their name.
	 * @param name
	 * @return
	 */
	public static PlayerStats GetStatsByName(String name)
	{
		for (Entry<EntityPlayer, deathtags.stats.PlayerStats> plr : PlayerStats.entrySet()) {
			if (plr.getKey().getName() == name)
				return plr.getValue();
		}
		
		return null;
	}

	/**
	 * Get the stat value of a player.
	 * @param player
	 * @return
	 */
	public static PlayerStats GetStats(EntityPlayer player)
	{
		return GetStatsByName(player.getName());
	}

	/**
	 * Register a new mod compatibility and nugget bar.
	 * @param bar
	 */
	public static void RegisterCompatibility(BuilderData builder, PartyList.NuggetBar bar)
	{
		PartyPacketDataBuilder.builderData.add(builder);

		if (runningSide.isServer()) return;

		// Make a bigger array and clone it.
		List<PartyList.NuggetBar> bars = new ArrayList<>();

		for (int i = 0; i< PartyList.nuggetBars.length; i++) {
			bars.add(PartyList.nuggetBars[i]);
		}

		bars.add(bar);
		PartyList.nuggetBars = bars.toArray(new PartyList.NuggetBar[0]); // Convert the list to an array.
	}
}
