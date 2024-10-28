package dev.matthe815.mmoparties.forge.api.compatibility;

import dev.matthe815.mmoparties.forge.core.MMOParties;
import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.networking.builders.BuilderData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * A helper to assist with registering and applying compatibility.
 */
public class CompatibilityHelper {
    /**
     * Register a new party UI element handler and packet writer.
     * The element is rendered rows based on load order of the mods.
     * @param builder
     * @param bar
     */
    public static void Register(BuilderData builder, PartyList.NuggetBar bar)
    {
        MMOParties.RegisterCompatibility(builder, bar, FMLEnvironment.dist == Dist.DEDICATED_SERVER);
    }
}
