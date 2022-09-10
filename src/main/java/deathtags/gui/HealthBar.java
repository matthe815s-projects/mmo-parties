package deathtags.gui;

import java.util.Random;

import deathtags.config.ConfigHolder;
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

public class HealthBar {

    public static final ResourceLocation TEXTURE_ICON = new ResourceLocation(MMOParties.MODID,
        "textures/icons.png");

    public static final ResourceLocation HEART_TEXTURE = new ResourceLocation("minecraft", "textures/gui/icons.png");

    private static Minecraft mc;
    private static int updateCounter = 0;
    private static Random random;
    private static MatrixStack stack = new MatrixStack();

    public static NuggetBar[] nuggetBars = new NuggetBar[] {
        (data, xOffset, yOffset, compact) -> Draw(data.health, data.maxHealth, new UISpec(HEART_TEXTURE, xOffset, yOffset, 52, 0), 16, 9, compact, true),
        (data, xOffset, yOffset, compact) -> Draw(data.hunger, 20, new UISpec(HEART_TEXTURE, xOffset, yOffset, 52, 27), 16, 9, compact, ConfigHolder.CLIENT.showHunger.get()),
        (data, xOffset, yOffset, compact) -> Draw(data.armor, data.armor, new UISpec(HEART_TEXTURE, xOffset, yOffset, 34, 9), 16, -9, compact, ConfigHolder.CLIENT.showArmor.get()),
        (data, xOffset, yOffset, compact) -> Draw(data.absorption, data.absorption, new UISpec(HEART_TEXTURE, xOffset, yOffset, 160, 0), 16, 9, compact, ConfigHolder.CLIENT.showAbsorption.get()),
    };

    public HealthBar(Minecraft mc) {
        super();
        this.mc = mc;
        random = new Random();
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
        
        /**
         * Party display.
         */
        if (MMOParties.localParty != null && MMOParties.localParty.local_players.size() >= 1) {
            int pN = 0;

            for (int i=0;i<8;i++) {
                for (String p_player : MMOParties.localParty.local_players) {
                    if (p_player.equals(Minecraft.getInstance().player.getName().getContents())) {
                        PartyMemberData data = MMOParties.localParty.data.get(p_player);
                        lastOffset += RenderMember(data, lastOffset, pN, MMOParties.localParty.local_players.size() > 4
                                || ConfigHolder.CLIENT.useSimpleUI.get() == true);
                        pN++;
                    }
                }
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
        int Render(PartyMemberData data, int xOffset, int yOffset, boolean compact);
    }

    // Render a party member in the party
    int RenderMember(PartyMemberData data, int lastOffset, int pN, boolean compact) {
        if (data == null) return 0; // There shouldn't be an instance where this is null, but..

        int iconRows = 0, additionalOffset = 0, posX = 4;
        int defaultOffset = ConfigHolder.CLIENT.uiYOffset.get();
        int yOffset = (15 * (pN + 1)) + lastOffset;

        // Rendering compact or in verbose changes the amount of visible data as well as the yOffset.
        // The only bar visible within compact mode is hearts, and it's in a number form.
        if (compact) {
            yOffset = (int)(yOffset / 1.7) + 4;
            nuggetBars[0].Render(data, posX + 30, ((defaultOffset - 10) + yOffset), true);
        } else {
            for (NuggetBar bar : nuggetBars) {
                int offset = bar.Render(data, posX, (defaultOffset + (12 * iconRows)) + yOffset, false);
                additionalOffset += offset;
                if (offset != -1) iconRows++;
            }
        }

        Minecraft.getInstance().getTextureManager().bind(TEXTURE_ICON);
        
        if (data.leader) // If the player is the party leader, draw a crown next to their name
        	Minecraft.getInstance().gui.blit(stack, 10, ((defaultOffset - 20) + yOffset), 0, 18, 9, 9);

        // Render the name.
        mc.font.draw(stack, data.name, 10, (defaultOffset - 10) + yOffset, 0xFFFFFF);

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
        int length = 0;
        int bars = 0;

        Minecraft.getInstance().getTextureManager().bind(UI.texture); // Bind the appropriate texture

        // Loop for each additional max nugget.
        for (int i = 0; i < max / 2; i++) {
            int dropletHalf = i * 2 + 1;
            int nuggetX = UI.x + length * 8 + 9;
            int offsetY = UI.y;

            // Randomly jiggle the health droplets at low health
            if (max > 6.0f && current <= 6.0f && updateCounter % (current * 3 + 1) == 0)
                offsetY = UI.y + (random.nextInt(3) - 1);

            // Draw background
            Minecraft.getInstance().gui.blit(stack, nuggetX, offsetY+ (4 * bars), backgroundOffset, UI.texture_y, 9, 9);

            // Draw half or full depending on health amount.
            if ((int) current > dropletHalf) {
                Minecraft.getInstance().gui.blit(stack, nuggetX, offsetY+ (4 * bars), UI.texture_x, UI.texture_y, 9, 9);
            }
            else if ((int) current == dropletHalf)
                Minecraft.getInstance().gui.blit(stack, nuggetX, offsetY+ (4 * bars), UI.texture_x + halfOffset, UI.texture_y, 9, 9);

            length ++; // increment length tracker.

            // Create a new bar when length reaches 10.
            if (length > 9) {
                bars++;
                length = 0;
            }
        }

        return (6 * (bars + 1));
    }

	public static void init() {
		System.out.println("Load GUI");
		MinecraftForge.EVENT_BUS.register(new HealthBar(Minecraft.getInstance()));
	}
}