package deathtags.gui;

import java.util.Random;

import deathtags.config.ConfigHolder;
import deathtags.networking.BuilderData;
import deathtags.networking.PartyPacketDataBuilder;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import deathtags.core.MMOParties;
import deathtags.stats.PartyMemberData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber
public class HealthBar {

    public static final ResourceLocation TEXTURE_ICON = new ResourceLocation(MMOParties.MODID,
        "textures/icons.png");

    public static final ResourceLocation HEART_TEXTURE = new ResourceLocation("minecraft", "textures/gui/icons.png");

    private static Minecraft mc;
    private static int updateCounter = 0;
    private static Random random;
    private static MatrixStack stack = new MatrixStack();

    private static boolean renderAscending = false;
    private static boolean renderOpposite = false;

    public static NuggetBar[] nuggetBars = new NuggetBar[] {
//        (data, xOffset, yOffset, compact) -> Draw(data.health, data.maxHealth, new UISpec(HEART_TEXTURE, xOffset, yOffset, 52, 0), 16, 9, compact, true),
//        (data, xOffset, yOffset, compact) -> Draw(data.hunger, 20, new UISpec(HEART_TEXTURE, xOffset, yOffset, 52, 27), 16, 9, compact, ConfigHolder.CLIENT.showHunger.get()),
//        (data, xOffset, yOffset, compact) -> Draw(data.armor, data.armor, new UISpec(HEART_TEXTURE, xOffset, yOffset, 34, 9), 16, -9, compact, ConfigHolder.CLIENT.showArmor.get()),
//        (data, xOffset, yOffset, compact) -> Draw(data.absorption, data.absorption, new UISpec(HEART_TEXTURE, xOffset, yOffset, 160, 0), 16, 9, compact, ConfigHolder.CLIENT.showAbsorption.get()),
    };

    public HealthBar() {
        super();
        random = new Random();
    }

    public static UISpec GetAnchorOffset()
    {
        switch (ConfigHolder.CLIENT.anchorPoint.get()) {
            case "top-left":
                return new UISpec(8, 0);

            case "bottom-left":
                renderAscending = true;
                return new UISpec(8, mc.getWindow().getGuiScaledHeight());

            case "top-right":
                return new UISpec(mc.getWindow().getGuiScaledWidth() - 5, 0);

            case "bottom-right":
                renderAscending = true;
                return new UISpec(mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight() - 20);

            default:
                System.out.println("Invalid anchor position selected.");
                break;
        }

        return new UISpec(0,0);
    }

    public static int Draw(float current, float max, UISpec UI, int backgroundOffset, int halfOffset, boolean compact, boolean render) {
        if (render == false) return -1; // Don't render. Used for config values and what not.

        if (!compact) return DrawNuggetBar(current, max, UI, backgroundOffset, halfOffset);
        else return DrawNuggetBarCompact(current, max, UI, backgroundOffset);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != ElementType.TEXT)
            return;

        int lastOffset = 0;

        GL11.glScalef(1, 1, 1);

        mc = Minecraft.getInstance();
        
        /**
         * Party display.
         */
        if (MMOParties.localParty != null && MMOParties.localParty.local_players.size() >= 1) {
            int pN = 0;

            for (PartyMemberData data : MMOParties.localParty.data.values()) {
                // Hide yourself if the option is enabled.
                if (ConfigHolder.CLIENT.hideSelf.get() && data.name.equals(mc.player.getName().getString())) continue;

                // Render a new player and track the additional offset for the next player.
                lastOffset += RenderMember(data, lastOffset, pN, MMOParties.localParty.local_players.size() > 4
                        || ConfigHolder.CLIENT.useSimpleUI.get() == true);
                pN++;
            }
        }
        
        Minecraft.getInstance().getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
    }

    // Draw the player's health bar at a define width and height.
    public static int DrawNuggetBarCompact(float current, float max, UISpec UI, int backgroundOffset) {
        int left = UI.x;
        int top = UI.y;
        int startX = left;
        int startY = top;

        Minecraft.getInstance().getTextureManager().bind(UI.texture); // Bind the appropriate texture

        Minecraft.getInstance().gui.blit(stack, startX, startY, backgroundOffset, UI.texture_y, 9, 9);
        Minecraft.getInstance().gui.blit(stack, startX, startY, UI.texture_x, UI.texture_y, 9, 9);

        mc.font.draw(stack, String.format("%s", Math.floor(current)), startX + 12, startY, 0xFFFFFF);

        return 6;
    }

    /**
     * An interface for rendering nugget bars.
     */
    public interface NuggetBar {
        int Render(BuilderData data, int xOffset, int yOffset, boolean compact);
    }

    // Render a party member in the party
    int RenderMember(PartyMemberData data, int lastOffset, int pN, boolean compact) {
        if (data == null) return 0; // There shouldn't be an instance where this is null, but..

        int iconRows = 0, additionalOffset = 0;
        UISpec defaultOffset = GetAnchorOffset();
        int yOffset = (15 * (pN + 1)) + lastOffset;
        int posX = defaultOffset.x;

        // Invert rendering of each player if in the bottom corners.
        if (renderAscending) yOffset = -yOffset;

        // Rendering compact or in verbose changes the amount of visible data as well as the yOffset.
        // The only bar visible within compact mode is hearts, and it's in a number form.
        if (compact) {
            yOffset = (int)(yOffset / 1.7) + 4;
            nuggetBars[1].Render(data.additionalData[1], posX, ((defaultOffset.y - 10) + yOffset), true);
            nuggetBars[2].Render(data.additionalData[2], posX + 30 + (4 * data.name.length()), ((defaultOffset.y - 10) + yOffset), true);
            return additionalOffset;
        }

        // Render each line of the list of UI elements
        // Each one is dynamically assigned at preInit by linked mods.
        for (int i=0; i < nuggetBars.length; i++) {
            if (data.additionalData[i] == null) continue;

            int rowOffset = (12 * iconRows);
            if (renderAscending) rowOffset = -rowOffset; // Reverse rendering.

            int offset = nuggetBars[i].Render(data.additionalData[i], posX, (defaultOffset.y + rowOffset) + yOffset, false);
            additionalOffset += offset;
            if (offset != -1) iconRows++;
        }

        return additionalOffset; // Return an offset calculated from number of wrapped bars to push future bars down.
    }

    /**
     * Draw a bar of nuggets based on a UI spec.
     * @param current
     * @param max
     * @param UI
     * @param backgroundOffset
     * @param halfOffset
     * @return
     */
    public static int DrawNuggetBar(float current, float max, UISpec UI, int backgroundOffset, int halfOffset) {
        UI.x = UI.x / 2;
        int length = 0, bars = 0;

        Minecraft.getInstance().getTextureManager().bind(UI.texture); // Bind the appropriate texture

        // Loop for each additional max nugget.
        for (int i = 0; i < max / 2; i++) {
            int dropletHalf = i * 2 + 1;
            int nuggetX = UI.x + (length * 8), offsetY = UI.y;

            int xOffset = 4 * bars;
            if (renderOpposite) xOffset = -xOffset;

            // Randomly jiggle the health droplets at low health
            if (max > 6.0f && current <= 6.0f && updateCounter % (current * 3 + 1) == 0)
                offsetY = UI.y + (random.nextInt(3) - 1);

            // Draw background
            Minecraft.getInstance().gui.blit(stack, nuggetX, offsetY+xOffset, backgroundOffset, UI.texture_y, 9, 9);

            // Draw half or full depending on health amount.
            if ((int) current > dropletHalf) {
                Minecraft.getInstance().gui.blit(stack, nuggetX, offsetY+xOffset, UI.texture_x, UI.texture_y, 9, 9);
            }
            else if ((int) current == dropletHalf)
                Minecraft.getInstance().gui.blit(stack, nuggetX, offsetY+xOffset, UI.texture_x + halfOffset, UI.texture_y, 9, 9);

            length ++; // increment length tracker.

            // Create a new bar when length reaches 10.
            if (length > 9) {
                bars++;
                length = 0;
            }
        }

        return (6 * (bars + 1));
    }

    public static int DrawText(String text, UISpec location)
    {
        mc.font.drawShadow(stack, text, location.x, location.y, 0xFFFFFF);
        return 8;
    }

    public static int DrawResource(UISpec ui)
    {
        Minecraft.getInstance().getTextureManager().bind(ui.texture);
        Minecraft.getInstance().gui.blit(stack, ui.x, ui.y, ui.texture_x, ui.texture_y, ui.width, ui.height);
        Minecraft.getInstance().getTextureManager().bind(ForgeIngameGui.GUI_ICONS_LOCATION);
        return ui.height;
    }

	public static void init() {
		System.out.println("Load GUI");
		MinecraftForge.EVENT_BUS.register(new HealthBar());
	}
}