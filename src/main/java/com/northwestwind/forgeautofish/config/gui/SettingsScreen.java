package com.northwestwind.forgeautofish.config.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
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
        Button recastDelay = new Button(this.width / 2 - 75, this.height / 2 - 40, 150, 20, new TranslationTextComponent("gui.forgeautofish.recastdelay"), button -> Minecraft.getInstance().displayGuiScreen(new RecastDelayScreen(this)));
        addButton(recastDelay);
        Button reelInDelay = new Button(this.width / 2 - 75, this.height / 2 - 15, 150, 20, new TranslationTextComponent("gui.forgeautofish.reelindelay"), button -> Minecraft.getInstance().displayGuiScreen(new ReelInDelayScreen(this)));
        addButton(reelInDelay);
        Button filter = new Button(this.width / 2 - 75, this.height / 2 + 10, 150, 20, new TranslationTextComponent("gui.forgeautofish.filter"), button -> Minecraft.getInstance().displayGuiScreen(new SuperFilterScreen(this)));
        addButton(filter);
        Button done = new Button(this.width / 2 - 75, this.height - 25, 150, 20, new TranslationTextComponent("gui.forgeautofish.done"), button -> closeScreen());
        addButton(done);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 20, -1);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
