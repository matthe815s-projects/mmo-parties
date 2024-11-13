package dev.matthe815.mmoparties.fabric.core;

import com.mojang.blaze3d.platform.InputConstants;
import dev.matthe815.mmoparties.common.core.MMOPartiesCommon;
import dev.matthe815.mmoparties.fabric.events.EventClientFabric;
import dev.matthe815.mmoparties.fabric.events.EventCommonFabric;
import dev.matthe815.mmoparties.fabric.networking.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/**
 * The entry point for the MMO parties mod.
 * Contains some top-level cache management functionality.
 * @author Matthe815
 */
public class MMOParties extends MMOPartiesCommon {
	private static final String PROTOCOL_VERSION = "1";

	public MMOParties() {
		super(false);
	}

	public MMOParties(boolean isDedicatedServer) {
		super(isDedicatedServer);
	}

	@Override
	public void onInitialize() {
		this.OnSetup();

		// Register events for the client-side (if applicable)
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			// Register keybinding and client events
			this.KeyBinds();
		}
	}

	/**
	 * Runs when the mod is constructed and setups up the networking and bus events.
	 */
	public void OnSetup()
	{
		// Sets up all of the network packet handlers.
		SetupNetworking();

		// Register event handlers (Fabric equivalent of Forge's event bus).
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) {
			EventClientFabric.registerEvents();
		}

		EventCommonFabric.registerEvents();  // This will handle event registration for common side.
	}

	/**
	 * Handles setting up all common networking packet types; there are special types for Server and Client setup.
	 */
	public void SetupNetworking()
	{
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) {
			MessageUpdateParty.registerHandler();
			MessageSendMemberData.registerHandler();
			MessagePartyInvite.registerHandler();
		}

		MessageHandleMenuAction.registerHandler();
	}

	public void KeyBinds() {
		// Creates and registers the key-binding on a universal scale.
		// Register the keybinding
		OPEN_GUI_KEY = new KeyMapping(
				"key.opengui.desc",  // Key description
				InputConstants.Type.KEYSYM,  // Key type (KEYSYM means a physical key on the keyboard)
				GLFW.GLFW_KEY_P,  // Key to bind (P key)
				"key.mmoparties.category"  // Category the keybinding belongs to
		);

		// Register the keybinding with the game
		KeyBindingHelper.registerKeyBinding(OPEN_GUI_KEY);
	}
}
