package com.northwestwind.forgeautofish.config.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;

public class SettingsScreen extends Screen {
    public SettingsScreen() {
        super(new TranslationTextComponent("gui.forgeautofish"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        Button recastDelay = new Button(this.width / 2 - 75, this.height / 2 - 40, 150, 20, new TranslationTextComponent("gui.forgeautofish.recastdelay").getString(), button -> Minecraft.getInstance().displayGuiScreen(new RecastDelayScreen(this)));
        addButton(recastDelay);
        Button reelInDelay = new Button(this.width / 2 - 75, this.height / 2 - 15, 150, 20, new TranslationTextComponent("gui.forgeautofish.reelindelay").getString(), button -> Minecraft.getInstance().displayGuiScreen(new ReelInDelayScreen(this)));
        addButton(reelInDelay);
        Button filter = new Button(this.width / 2 - 75, this.height / 2 + 10, 150, 20, new TranslationTextComponent("gui.forgeautofish.filter").getString(), button -> Minecraft.getInstance().displayGuiScreen(new SuperFilterScreen(this)));
        addButton(filter);
        Button done = new Button(this.width / 2 - 75, this.height - 25, 150, 20, new TranslationTextComponent("gui.forgeautofish.done").getString(), button -> onClose());
        addButton(done);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        drawCenteredString(this.font, this.title.getString(), this.width / 2, 20, -1);
        super.render(mouseX, mouseY, partialTicks);
    }
}
