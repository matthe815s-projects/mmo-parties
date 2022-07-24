package deathtags.gui;

import java.util.Random;

import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import deathtags.config.Color;
import deathtags.config.Config;
import deathtags.core.ConfigHandler;
import deathtags.core.MMOParties;
import deathtags.stats.PartyMemberData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GUIHandler extends Gui {
	
	private static final String[] barColors = new String[] {"bf0000", "e66000", "e69900", "e6d300", "99e600", "4ce600", "00e699", "00e6e6", "0099e6", "0000e6", "9900e6", "d580ff", "8c8c8c", "e6e6e6"};
	
    private Minecraft mc;
    private int updateCounter = 0;
    private Random random;

    public GUIHandler(Minecraft mc) {

        super();
        this.mc = mc;
        random = new Random();
        
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        
    	if ( event.getType() != ElementType.TEXT ) return; // Only render on the text.

        int posX = 4;
        int lastOffset = 0;
        
        /**
         * Party display.
         */
        if (MMOParties.localParty != null && MMOParties.localParty.local_players.size() >= 2) {
            int pN = 0;

            for (String party_member : MMOParties.localParty.local_players) {
                if (! party_member.equals( Minecraft.getMinecraft().player.getName() ) ) {
                    PartyMemberData data = MMOParties.localParty.data.get(party_member);                   
                    lastOffset = renderMember ( data, posX, lastOffset, pN );
                    pN++;
                }
            }
        }
        
        Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
    }

    
    int renderMember ( PartyMemberData data, int posX, int lastOffset, int pN )
    {
        int iconRows = 0;

        int defaultOffset = 0;
        int yOffset = (30 * (pN + 1)) + lastOffset;
        int healthbarOffset = 0;
        
        Minecraft.getMinecraft().getTextureManager().bindTexture( Gui.ICONS );
        
        /*
         * HP Bar.
         */
        healthbarOffset = drawHealth(posX, defaultOffset + yOffset, data.health, data.maxHealth);
        iconRows++;
        
        /*
         * Hunger Bar.
         */
        if (ConfigHandler.Client_Options.showHunger) {
            drawHunger(posX, (defaultOffset + (10 * iconRows)) + yOffset, data.hunger, 20);
            iconRows++;
        }
        
        /*
         * Absorption Bar.
         */
        if (data.absorption > 0 && ConfigHandler.Client_Options.showAbsorption) {
            drawAbsorption(posX, (defaultOffset + (10 * iconRows)) + yOffset, data.absorption);
            iconRows++;	
        }
        
        /*
         * Armor Bar.
         */
        if (data.armor > 0 && ConfigHandler.Client_Options.showArmor) {
            drawArmor(posX, (defaultOffset + (10 * iconRows)) + yOffset, data.armor);
            iconRows++;
        }
        
        /*
         * The shield gauge.
         */
        if (Loader.isModLoaded("superiorshields") && ConfigHandler.Client_Options.showShields) {
            float shields = data.shields;
            float maxShields = data.maxShields;
            
            if (maxShields > 0) {
                drawShields(posX, (defaultOffset + (10 * iconRows)) + yOffset, shields, maxShields);
                iconRows++;	
            }
        }

        FontRenderer fontRender = mc.fontRenderer;
        Minecraft.getMinecraft().getTextureManager().bindTexture( new ResourceLocation(MMOParties.MODID, "textures/icons.png") );
        
        if (data.leader) 
        	Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(10, (defaultOffset - 20) + yOffset, 0, 18, 9, 9);
        
        fontRender.drawStringWithShadow(data.name, 10, (defaultOffset - 10) + yOffset - GuiIngameForge.right_height, 0xFFFFFF);
        
        Minecraft.getMinecraft().getTextureManager().bindTexture( Gui.ICONS );

        return healthbarOffset;
    }
    
    private int drawHealth(int width, int height, float health, float maxHealth) {
        int left = width / 2;
        int top = height - GuiIngameForge.right_height;
        int barLength = 0;
        int bars = 0;
        
        for (int i = 0; i < maxHealth / 2; i++) {
            int dropletHalf = i * 2 + 1;
            int startX = left + barLength * 8 + 9;
            int startY = top;

            if (health <= 6.0f && updateCounter % (health * 3 + 1) == 0) startY = top + (random.nextInt(3) - 1);
            
            Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY + (4 * bars), 16, 0, 9, 9);
            
            if ((int) health > dropletHalf) 
                Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY  + (4 * bars), 52, 0, 9, 9);
            else if ((int) health == dropletHalf)
                Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY  + (4 * bars), 61, 0, 9, 9);

            barLength ++;

            // Increase the number of bars
            if (barLength > 9) {
                bars++;
                barLength = 0;
            }
        }

        return (4 * (bars - 1)) + 2;
    }
    
    private void drawHunger(int width, int height, float health, float maxHealth) {
        int left = width / 2;
        int top = height - GuiIngameForge.right_height;
        int barLength = 0;
        
        for (int i = 0; i < maxHealth / 2; i++) {
            int dropletHalf = i * 2 + 1;
            int startX = left + barLength * 8 + 9;
            int startY = top;

            if (health <= 6.0f && updateCounter % (health * 3 + 1) == 0) startY = top + (random.nextInt(3) - 1);
            
            Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY, 16, 27, 9, 9);
            
            if ((int) health > dropletHalf) 
                Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY, 52, 27, 9, 9);
            else if ((int) health == dropletHalf)
                Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY, 61, 27, 9, 9);

            barLength ++;
        }
    }
    
    private void drawArmor(int width, int height, float armor) {
        int left = width / 2;
        int top = height - GuiIngameForge.right_height;
        int barLength = 0;
        
        for (int i = 0; i < armor / 2; i++) {
            int dropletHalf = i * 2 + 1;
            int startX = left + barLength * 8 + 9;
            int startY = top;
            
           Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY, 16, 9, 9, 9);
            
            if ((int) armor > dropletHalf) 
                Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY, 34, 9, 9, 9);
            else if ((int) armor == dropletHalf)
                Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY, 25, 9, 9, 9);
            
            barLength++;
            
            GL11.glColor4f(255, 255, 255, 1f);
        }
    }
    
    private void drawAbsorption(int width, int height, float armor) {
        int left = width / 2;
        int top = height - GuiIngameForge.right_height;
        int bars = 1;
        int barLength = 0;
        
        for (int i = 0; i < armor / 2; i++) {
            int dropletHalf = i * 2 + 1;
            int startX = left + barLength * 8 + 9;
            int startY = top;
            
            if (bars == 1)
            	Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY, 16, 0, 9, 9);
            
            if ((int) armor > dropletHalf) 
                Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY, 160, 0, 9, 9);
            else if ((int) armor == dropletHalf)
                Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY, 169, 0, 9, 9);
            
            barLength++;
            
            if (barLength >= 10) {
            	barLength = 0;
            	bars++;
            }
            
            GL11.glColor4f(255, 255, 255, 1f);
        }
    }
    
    private void drawShields(int width, int height, float shields, float maxShields) {
        int left = width / 2;
        int top = height - GuiIngameForge.right_height;
        int bars = 1;
        int barLength = 0;
        
        for (int i = 0; i < maxShields / 2; i++) {
            int dropletHalf = i * 2 + 1;
            int startX = left + barLength * 8 + 9;
            int startY = top;
            
            if (bars == 1)
            	Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY, 0, 36, 9, 9);
            
            Color barColor = new Color(barColors[bars-1]);
            GL11.glColor4f(barColor.red*255f, barColor.green*255f, barColor.blue*255f, 1f);
            
            if ((int) shields > dropletHalf) 
                Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY, 9, 36, 9, 9);
            else if ((int) shields == dropletHalf)
                Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(startX, startY, 18, 36, 9, 9);
            
            barLength++;
            
            if (barLength >= 10) {
            	barLength = 0;
            	bars++;
            }
            
            GL11.glColor4f(255, 255, 255, 1f);
        }
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new GUIHandler(Minecraft.getMinecraft()));
    }
}