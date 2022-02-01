package deathtags.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import deathtags.commands.PartyCommand;
import deathtags.gui.HealthBar;
import deathtags.networking.MessageSendMemberData;
import deathtags.networking.MessageUpdateParty;
import deathtags.stats.Party;
import deathtags.stats.PlayerStats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(value = MMOParties.MODID)
public class MMOParties {

	public static final String MODID = "mmoparties";

	public static Party localParty;
	public static Map<PlayerEntity, PlayerStats> PlayerStats = new HashMap<PlayerEntity, PlayerStats>();

	private static final String PROTOCOL_VERSION = "1";
	
	public static final SimpleChannel network = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(MODID, "sync"))
			.clientAcceptedVersions(s -> true)
			.serverAcceptedVersions(s -> true)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();
	
	public MMOParties ()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::preInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
		MinecraftForge.EVENT_BUS.addListener(this::serverInit);
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void preInit(FMLCommonSetupEvent event) 
	{
		System.out.println(MODID + " is pre-loading!");

		network.registerMessage(1, MessageUpdateParty.class, MessageUpdateParty::encode, MessageUpdateParty::decode, MessageUpdateParty.Handler::handle );
		network.registerMessage(2, MessageSendMemberData.class, MessageSendMemberData::encode, MessageSendMemberData::decode, MessageSendMemberData.Handler::handle);

		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}
	
	public void clientInit(FMLClientSetupEvent event)
	{
		HealthBar.init();
	}
	
	public void serverInit(RegisterCommandsEvent event)
	{	
		event.getDispatcher().register(PartyCommand.register());
	}

	/**
	 * Get the stat value of a player by their name.
	 * @param name
	 * @return
	 */
	public static PlayerStats GetStatsByName(String name)
	{
		for (Entry<PlayerEntity, deathtags.stats.PlayerStats> plr : PlayerStats.entrySet()) {
			if ( plr.getKey().getName().getContents().equals(name) )
				return plr.getValue();
		}

		return null;
	}

}
