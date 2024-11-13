package dev.matthe815.mmoparties.fabric.api.compatibility;

import dev.matthe815.mmoparties.fabric.core.MMOParties;
import dev.matthe815.mmoparties.common.gui.PartyList;
import dev.matthe815.mmoparties.common.networking.builders.BuilderData;

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
        MMOParties.RegisterCompatibility(builder, bar, false);
    }
}
