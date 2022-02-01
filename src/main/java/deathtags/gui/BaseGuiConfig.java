package deathtags.gui;

import deathtags.core.*;
import deathtags.config.*;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;

public class BaseGuiConfig extends GuiConfig {

  public BaseGuiConfig(GuiScreen parent) {
    super(parent, new ConfigElement(Config.getCategory(Config.CAT_BAR)).getChildElements(),
        MMOParties.MODID, false, false, "Health Bar Config");
    
    titleLine2 = Config.configFile.getAbsolutePath();
  }

  @Override
  public void initGui() {

    // You can add buttons and initialize fields here
    super.initGui();
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {

    // You can do things like create animations, draw additional elements, etc. here
    super.drawScreen(mouseX, mouseY, partialTicks);
  }

  @Override
  protected void actionPerformed(GuiButton button) {

    // You can process any additional buttons you may have added here
    super.actionPerformed(button);
  }
}