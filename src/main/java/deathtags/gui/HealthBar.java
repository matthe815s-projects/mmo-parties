package deathtags.gui;

import java.awt.*;
import java.util.Random;

import deathtags.config.ConfigHolder;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import deathtags.core.MMOParties;
import deathtags.stats.PartyMemberData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HealthBar {

    public static final ResourceLocation TEXTURE_FRAME = new ResourceLocation(
        MMOParties.MODID, "textures/frame.png");
    public static final ResourceLocation TEXTURE_ICON = new ResourceLocation(MMOParties.MODID,
        "textures/icons.png");
    public static final ResourceLocation TEXTURE_BAR = new ResourceLocation(MMOParties.MODID, 
    	"textures/bar.png");

    public static final ResourceLocation HEART_TEXTURE = new ResourceLocation("minecraft", "textures/gui/icons.png");
	private static final String[] barColors = new String[] {"#bf0000", "#e66000", "#e69900", "#e6d300", "#99e600", "#4ce600", "#00e699", "#00e6e6", "#0099e6", "#0000e6", "#9900e6", "#d580ff", "#8c8c8c", "#e6e6e6"};
	
    private Minecraft mc;
    public static float targetHP;
    public static float targetMaxHP;
    private int updateCounter = 0;
    private Random random;
    private MatrixStack stack = new MatrixStack();

    public HealthBar(Minecraft mc) {

        super();
        this.mc = mc;
        random = new Random();
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
    	
        if (event.getType() != ElementType.TEXT)
            return;
        
        int posX = 4;

        GL11.glScalef(1, 1, 1);
        
        /**
         * Party display.
         */
        if (MMOParties.localParty != null && MMOParties.localParty.local_players.size() >= 1) {
            int pN = 0;

            for (String p_player : MMOParties.localParty.local_players) {
                if (!p_player.equals(Minecraft.getInstance().player.getName().getContents())) {
                    PartyMemberData data = MMOParties.localParty.data.get(p_player);                   
                    RenderOwnPartyMember(data, posX, pN, p_player);
                    pN++;
                }
            }
        }
        
        Minecraft.getInstance().getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
    }


    // Render a party member in the party
    void RenderOwnPartyMember(PartyMemberData data, int posX, int pN, String p_player) {
        if (data == null)
            return;
    	
        float currentHealth = data.health;
        float maxHealth = data.maxHealth;
        float currentArmor = data.armor;
        float currentAbsorption = data.absorption;
        int iconRows = 0;

        int yOffset = (30 * (pN + 1));
        int healthBarOffset = 0;

        Minecraft.getInstance().getTextureManager().bind(HEART_TEXTURE);
        
        /*
         * HP Bar.
         */
        healthBarOffset += drawHealth(posX, 50 + yOffset, currentHealth, maxHealth);
        iconRows++;
        
        /*
         * Hunger Bar.
         */
        if (ConfigHolder.COMMON.showHunger.get()) {
            drawHunger(posX, (50 + (10 * iconRows)) + yOffset + healthBarOffset, data.hunger, 20);
            iconRows++;
        }
        
        /*
         * Absorption Bar.
         */
        if (currentAbsorption > 0 && ConfigHolder.COMMON.showAbsorption.get()) {
            drawAbsorption(posX, (50 + (10 * iconRows)) + yOffset + healthBarOffset, currentAbsorption);
            iconRows++;	
        }
        
        /*
         * Armor Bar.
         */
        if (currentArmor > 0 && ConfigHolder.COMMON.showArmor.get()) {
            drawArmor(posX, (50 + (10 * iconRows)) + yOffset + healthBarOffset, currentArmor);
            iconRows++;
        }
        
        Minecraft.getInstance().getTextureManager().bind(TEXTURE_ICON);
        GL11.glColor4f(255, 255, 255, 1f);
        
        if (data.leader) // If the player is the party leader, draw a crown next to their name
        	Minecraft.getInstance().gui.blit(stack, 10, (31 + yOffset), 0, 18, 9, 9);
        
        mc.font.draw(stack, String.format("%s", p_player), 10, 40 + yOffset, 0xFFFFFF);
    }

    // Draw the player's health bar at a define width and height.
    private int drawHealth(int width, int height, float health, float maxHealth) {
        int left = width / 2;
        int top = height;
        int barLength = 0;
        int bars = 0;
        
        for (int i = 0; i < maxHealth / 2; i++) {
            int dropletHalf = i * 2 + 1;
            int startX = left + barLength * 8 + 9;
            int startY = top;

            // Randomly jiggle the health droplets at low health
            if (health <= 6.0f && updateCounter % (health * 3 + 1) == 0) startY = top + (random.nextInt(3) - 1);

            Minecraft.getInstance().gui.blit(stack, startX, startY+ (4 * bars), 16, 0, 9, 9);

            if ((int) health > dropletHalf) {
                Minecraft.getInstance().gui.blit(stack, startX, startY + (4 * bars), 52, 0, 9, 9);
            }
            else if ((int) health == dropletHalf)
                Minecraft.getInstance().gui.blit(stack, startX, startY  + (4 * bars), 61, 0, 9, 9);

            barLength ++;

            if (barLength > 9) {
                bars++;
                barLength = 0;
            }
        }

        return (4 * (bars - 1)) + 2;
    }
    
    private void drawHunger(int width, int height, float health, float maxHealth) {
        int left = width / 2;
        int top = height;
        int barLength = 0;
        
        for (int i = 0; i < maxHealth / 2; i++) {
            int dropletHalf = i * 2 + 1;
            int startX = left + barLength * 8 + 9;
            int startY = top;

            if (health <= 6.0f && updateCounter % (health * 3 + 1) == 0) startY = top + (random.nextInt(3) - 1);
            
            Minecraft.getInstance().gui.blit(stack, startX, startY, 16, 27, 9, 9);
            
            if ((int) health > dropletHalf) 
                Minecraft.getInstance().gui.blit(stack, startX, startY, 52, 27, 9, 9);
            else if ((int) health == dropletHalf)
                Minecraft.getInstance().gui.blit(stack, startX, startY, 61, 27, 9, 9);

            barLength ++;
        }
    }
    
    private void drawArmor(int width, int height, float armor) {
        int left = width / 2;
        int top = height;
        int barLength = 0;
        
        for (int i = 0; i < armor / 2; i++) {
            int dropletHalf = i * 2 + 1;
            int startX = left + barLength * 8 + 9;
            int startY = top;
            
           Minecraft.getInstance().gui.blit(stack, startX, startY, 16, 9, 9, 9);
            
            if ((int) armor > dropletHalf) 
                Minecraft.getInstance().gui.blit(stack, startX, startY, 34, 9, 9, 9);
            else if ((int) armor == dropletHalf)
                Minecraft.getInstance().gui.blit(stack, startX, startY, 25, 9, 9, 9);
            
            barLength++;
            
            GL11.glColor4f(255, 255, 255, 1f);
        }
    }
    
    private void drawAbsorption(int width, int height, float armor) {
        int left = width / 2;
        int top = height;
        int bars = 1;
        int barLength = 0;
        
        for (int i = 0; i < armor / 2; i++) {
            int dropletHalf = i * 2 + 1;
            int startX = left + barLength * 8 + 9;
            int startY = top;
            
            if (bars == 1)
            	Minecraft.getInstance().gui.blit(stack, startX, startY, 16, 0, 9, 9);
            
            if ((int) armor > dropletHalf) 
                Minecraft.getInstance().gui.blit(stack, startX, startY, 160, 0, 9, 9);
            else if ((int) armor == dropletHalf)
                Minecraft.getInstance().gui.blit(stack, startX, startY, 169, 0, 9, 9);
            
            barLength++;
            
            if (barLength >= 10) {
            	barLength = 0;
            	bars++;
            }
            
            GL11.glColor4f(255, 255, 255, 1f);
        }
    }

	public static void init() {
		System.out.println("Load GUI");
		MinecraftForge.EVENT_BUS.register(new HealthBar(Minecraft.getInstance()));
	}
}