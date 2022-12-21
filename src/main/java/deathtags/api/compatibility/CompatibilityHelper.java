package deathtags.api.compatibility;

import deathtags.core.MMOParties;
import deathtags.gui.HealthBar;
import deathtags.networking.BuilderData;

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
    public static void Register(BuilderData builder, HealthBar.NuggetBar bar)
    {
        MMOParties.RegisterCompatibility(builder, bar);
    }
}
