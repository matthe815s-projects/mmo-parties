package deathtags.gui;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;

import deathtags.config.Color;
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
	private static final String[] barColors = new String[] {"bf0000", "e66000", "e69900", "e6d300", "99e600", "4ce600", "00e699", "00e6e6", "0099e6", "0000e6", "9900e6", "d580ff", "8c8c8c", "e6e6e6"};
	
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

    
    void RenderOwnPartyMember(PartyMemberData data, int posX, int pN, String p_player) {
        if (data == null)
            return;

    	System.out.println("Render player");
    	
        float currentHealth = data.health;
        float maxHealth = data.maxHealth;
        float currentArmor = data.armor;
        float currentAbsorption = data.absorption;
        int iconRows = 0;

        Minecraft.getInstance().getTextureManager().bind(HEART_TEXTURE);
        
        /*
         * HP Bar.
         */
        drawHealth(posX, 50 + (30 * (pN + 1)), currentHealth, maxHealth);
        iconRows++;
        
        /*
         * Hunger Bar.
         */
        drawHunger(posX, (50 + (10 * iconRows)) + (30 * (pN + 1)), data.hunger, 20);
        iconRows++;
        
        /*
         * Absorption Bar.
         */
        if (currentAbsorption > 0) {
            drawAbsorption(posX, (50 + (10 * iconRows)) + (30 * (pN + 1)), currentAbsorption);
            iconRows++;	
        }
        
        /*
         * Armor Bar.
         */
        if (currentArmor > 0) {
            drawArmor(posX, (50 + (10 * iconRows)) + (30 * (pN + 1)), currentArmor);
            iconRows++;
        }
        
        Minecraft.getInstance().getTextureManager().bind(TEXTURE_ICON);

        FontRenderer fontRender = mc.font;

        String format3 = "%s";
        String str3 = String.format(format3, p_player);

        GL11.glColor4f(255, 255, 255, 1f);
        
        if (data.leader) 
        	Minecraft.getInstance().gui.blit(stack, 10, (31 + (30 * (pN + 1))), 0, 18, 9, 9);
        
        fontRender.draw(stack, str3, 10, 40 + (30 * (pN + 1)), 0xFFFFFF);
    }
    
    private void drawHealth(int width, int height, float health, float maxHealth) {
        int left = width / 2;
        int top = height;
        int barLength = 0;
        
        for (int i = 0; i < maxHealth / 2; i++) {
            int dropletHalf = i * 2 + 1;
            int startX = left + barLength * 8 + 9;
            int startY = top;

            if (health <= 6.0f && updateCounter % (health * 3 + 1) == 0) startY = top + (random.nextInt(3) - 1);
            
            Minecraft.getInstance().gui.blit(stack, startX, startY, 16, 0, 9, 9);
            
            if ((int) health > dropletHalf) 
                Minecraft.getInstance().gui.blit(stack, startX, startY, 52, 0, 9, 9);
            else if ((int) health == dropletHalf)
                Minecraft.getInstance().gui.blit(stack, startX, startY, 61, 0, 9, 9);

            barLength ++;
        }
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