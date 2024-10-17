package dev.matthe815.mmoparties.common.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.matthe815.mmoparties.common.core.MMOPartiesCommon;
import dev.matthe815.mmoparties.forge.api.compatibility.CompatibilityHelper;
import java.util.Objects;
import java.util.Random;

import dev.matthe815.mmoparties.common.gui.rendering.Renderer;
import dev.matthe815.mmoparties.common.networking.builders.BuilderData;
import dev.matthe815.mmoparties.common.stats.PartyMemberData;

import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;

/**
 * The UI handler that controls the creation and rendering of all party elements
 * Each bar to be rendered is created on mod initialization.
 * @see CompatibilityHelper
 */
public class PartyList {

    // public static final ResourceLocation TEXTURE_ICON = ResourceLocation.fromNamespaceAndPath(MMOPartiesCommon.MODID,
    //     "textures/icons.png");

    public static final ResourceLocation TEXTURE_ICON = new ResourceLocation(MMOPartiesCommon.MODID, "textures/icons.png");

    private static Minecraft mc;
    private static int updateCounter = 0;
    private static Random random;

    private static boolean renderAscending = false;
    private static boolean renderOpposite = false;

    public static NuggetBar[] nuggetBars = new NuggetBar[] {};

    public PartyList() {
        super();
        random = new Random();
        mc = Minecraft.getInstance();
    }

    public static void onRenderGameOverlay(GuiGraphics gui) {
        updateCounter++;
        if (MMOPartiesCommon.localParty == null || MMOPartiesCommon.localParty.local_players.isEmpty()) return;

        int lastOffset = 0;
        int playerNumber = 0;

        for (PartyMemberData data : MMOPartiesCommon.localParty.data.values()) {
            // Hide yourself if the option is enabled.
            if (ConfigHolder.CLIENT.hideSelf.get() && data.name.equals(mc.player.getName().getString())) continue;

            // Render a new player and track the additional offset for the next player.
            lastOffset += RenderMember(new UISpec(gui, 0, lastOffset), data, playerNumber, (MMOPartiesCommon.localParty.local_players.size() > 4 || ConfigHolder.CLIENT.useSimpleUI.get()));
            playerNumber++;
        }
    }

    /**
     * Get an X-Y offset based off of the config's UI Anchor value.
     * @return X and Y
     */
    public static UISpec GetAnchorOffset(GuiGraphics graphics)
    {
        switch (ConfigHolder.CLIENT.anchorPoint.get()) {
            case "top-left":
                return new UISpec(graphics, 8, 0);

            case "bottom-left":
                renderAscending = true;
                return new UISpec(graphics, 8, mc.getWindow().getGuiScaledHeight());

            case "top-right":
                return new UISpec(graphics, mc.getWindow().getGuiScaledWidth() - 5, 0);

            case "bottom-right":
                renderAscending = true;
                return new UISpec(graphics, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight() - 20);

            default:
                System.out.println("Invalid anchor position selected.");
                break;
        }

        return new UISpec(graphics, 0,0);
    }

    /**
     * Draw a nugget-bar with consideration to the compact settings and render restrictions
     * @param current The current value for the nugget-bar in halves.
     * @param max The maximum amount for the nugget bar in halves.
     * @param UI The UI spec to apply to the draw-call.
     * @param compact Whether to render in compact-mode.
     * @param render Flag to set to render at all.
     * @return The relative y-offset for the next row.
     */
    public static int Draw(UISpec UI, float current, float max, boolean compact, boolean render) {
        if (!render) return -1; // Don't render. Used for config values and what not.

        if (!compact) return DrawNuggetBar(UI, current, max);
        else return DrawNuggetBarCompact(UI, current);
    }

    /**
     * Draw a sectioned "nugget bar" in a compacted setting (icon + number) at a specified position.
     * Positioning of the element is automatically handled and supplied in the UI argument.
     * @param UI The UI spec to render.
     * @param current The current number of nuggets to be shown as a number.
     * @return The relative-Y of the next row.
     */
    public static int DrawNuggetBarCompact(UISpec UI, float current) {
        Renderer.drawLayeredSprite(UI);
        Renderer.drawString(new UISpec(UI.renderer, UI.x + 12, UI.y), String.format("%s", Math.floor(current)));
        return 6;
    }

    /**
     * An interface for rendering nugget bars.
     * The Render method returns an offset for the next bar.
     * @since 2.3.0
     */
    public interface NuggetBar {
        int Render(GuiGraphics gui, BuilderData data, int xOffset, int yOffset, boolean compact);
    }

    /**
     * Handles rendering an individual party member.
     * @param UI The UI Spec to render for.
     * @param data The PartyMemberData to render.
     * @param playerNumber The idx of the player to render.
     * @param compact Whether to draw in compact mode.
     * @return Relative-Y for the new row.
     */
    static int RenderMember(UISpec UI, PartyMemberData data, int playerNumber, boolean compact) {
        if (data == null) return 0; // There shouldn't be an instance where this is null.

        UISpec defaultOffset = GetAnchorOffset(UI.renderer);

        int iconRows = 0, additionalOffset = 0;
        int yOffset = (15 * (playerNumber + 1)) + UI.y;
        int posX = defaultOffset.x;
        int configOffsetY = ConfigHolder.CLIENT.uiYOffset.get();

        // Invert rendering of each player if in the bottom corners.
        if (renderAscending) yOffset = -yOffset;

        // Rendering compact or in verbose changes the amount of visible data as well as the yOffset.
        // The only bar visible within compact mode is hearts, and it's in a number form.
        if (compact) {
            yOffset = (int)((yOffset / 1.7) + 4) + configOffsetY;
            nuggetBars[1].Render(UI.renderer, data.additionalData[1], posX, ((defaultOffset.y - 10) + yOffset), true);
            nuggetBars[2].Render(UI.renderer, data.additionalData[2], posX + 30 + (4 * data.name.length()), ((defaultOffset.y - 10) + yOffset), true);
            return additionalOffset;
        }

        // Render each line of the list of UI elements
        // Each one is dynamically assigned at preInit by linked mods.
        for (int i=0; i < nuggetBars.length; i++) {
            if (data.additionalData[i] == null) continue;

            int rowOffset = (12 * iconRows);
            if (renderAscending) rowOffset = -rowOffset; // Reverse rendering.

            int offset = nuggetBars[i].Render(UI.renderer, data.additionalData[i], posX, (defaultOffset.y + rowOffset) + yOffset, false);
            additionalOffset += offset;
            if (offset != -1) iconRows++;
        }

        return additionalOffset; // Return an offset calculated from number of wrapped bars to push future bars down.
    }

    /**
     * Draw a bar of nuggets based on a UI spec.
     * Position information is supplied automatically in the UI argument.
     * @param UI The UI spec to render.
     * @param current The current nugget-bar value in halves.
     * @param max The maximum nugget-bar value in halves.
     * @return The relative-Y for the next row.
     */
    public static int DrawNuggetBar(UISpec UI, float current, float max) {
        UI.x = UI.x / 2;
        int length = 0, bars = 0;

        float maxLength = ConfigHolder.CLIENT.numbersAsPercentage.get() ? 20 : max;

        // Normalize hearts into a value of 20 if the config is enabled.
        if (ConfigHolder.CLIENT.numbersAsPercentage.get()) {
            int extraHearts = (int)((current - 20) / 2);

            // If the renderer displays as additional hearts, it shouldn't percentage the current.
            if (!Objects.equals(ConfigHolder.CLIENT.extraNumberType.get(), "additional")) current = (current / max) * 20;

            // Render any health that goes beyond the cap.
            RenderExtraHealth(UI, max, (extraHearts + 20) * 2);
        }

        // Loop for each additional max nugget.
        for (int i = 0; i < maxLength / 2; i++) {
            int dropletHalf = i * 2 + 1;
            int nuggetX = UI.x + (length * 8), offsetY = UI.y;
            int xOffset = 4 * bars;
            if (renderOpposite) xOffset = -xOffset;

            // Randomly jiggle the health droplets at low health
            if (max > 6.0f && current <= 6.0f && updateCounter % (current * 3 + 1) == 0)
                offsetY = UI.y + (random.nextInt(3) - 1);

            DrawNugget(UI, current, dropletHalf, nuggetX, xOffset+offsetY);

            length ++; // increment length tracker.

            // Create a new bar when length reaches 10.
            if (length > 9) {
                bars++;
                length = 0;
            }
        }

        return (6 * (bars + 1));
    }

    /**
     * Render the health beyond maximum.
     */
    static void RenderExtraHealth(UISpec UI, float max, int extraHearts)
    {
        // Render remainder health to the side
        int remainderX = UI.x + (10 * 8) + 4;
        UISpec remainderSpec = new UISpec(UI.renderer, remainderX, UI.y);

        switch (ConfigHolder.CLIENT.extraNumberType.get()) {
            case "percentage": // This will show the x% after bars.
                Renderer.drawString(remainderSpec, String.format("%s", Math.floor(extraHearts / max * 100)) + "%");
                break;
            case "compare": // This will show the x/x after the bars.
                Renderer.drawString(remainderSpec, String.format("%s/%s",extraHearts - 20, max));
                break;
            case "additional": // This will show +X after health.
                if (extraHearts <= 20) return;
                Renderer.drawString(remainderSpec, String.format("+%s", (extraHearts - 20) / 2));
                break;
            case "none": // This will not render anything beyond max and instead scale the health bar.
            default:
                break;
        }
    }

    /**
     * Draw an individual nugget onto the screen.
     */
    static void DrawNugget(UISpec UI, float current, int dropletHalf, int x, int y)
    {
        // Draw background
        Renderer.drawHalvedLayeredSprite(new UISpec(UI.renderer, UI.texture, UI.textureHalf, UI.textureBack, x, y,9, 9), current, dropletHalf);
    }

    public static int DrawText(String text, UISpec location)
    {
        location.renderer.drawString(Minecraft.getInstance().font, text, location.x, location.y, 0xFFFFFF);
        return 8;
    }

    public static int DrawResource(UISpec ui)
    {
        Renderer.drawSprite(ui.texture);
        return ui.height;
    }
}