package ml.northwestwind.forgeautofish.config.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import ml.northwestwind.forgeautofish.AutoFish;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;

public class SettingsScreen extends Screen {
    public SettingsScreen() {
        super(AutoFish.getTranslatableComponent("gui.forgeautofish"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        Button recastDelay = new Button.Builder(AutoFish.getTranslatableComponent("gui.forgeautofish.recastdelay"), button -> Minecraft.getInstance().setScreen(new RecastDelayScreen(this))).pos(this.width / 2 - 75, this.height / 2 - 50).size(150, 20).build();
        addRenderableWidget(recastDelay);
        Button reelInDelay = new Button.Builder(AutoFish.getTranslatableComponent("gui.forgeautofish.reelindelay"), button -> Minecraft.getInstance().setScreen(new ReelInDelayScreen(this))).pos(this.width / 2 - 75, this.height / 2 - 25).size(150, 20).build();
        addRenderableWidget(reelInDelay);
        Button throwDelay = new Button.Builder(AutoFish.getTranslatableComponent("gui.forgeautofish.throwdelay"), button -> Minecraft.getInstance().setScreen(new ThrowDelayScreen(this))).pos(this.width / 2 - 75, this.height / 2).size(150, 20).build();
        addRenderableWidget(throwDelay);
        Button checkInterval = new Button.Builder(AutoFish.getTranslatableComponent("gui.forgeautofish.checkinterval"), button -> Minecraft.getInstance().setScreen(new CheckIntervalScreen(this))).pos(this.width / 2 - 75, this.height / 2 + 25).size(150, 20).build();
        addRenderableWidget(checkInterval);
        Button filter = new Button.Builder(AutoFish.getTranslatableComponent("gui.forgeautofish.filter"), button -> Minecraft.getInstance().setScreen(new SuperFilterScreen(this))).pos(this.width / 2 - 75, this.height / 2 + 50).size(150, 20).build();
        addRenderableWidget(filter);
        Button done = new Button.Builder(AutoFish.getTranslatableComponent("gui.forgeautofish.done"), button -> onClose()).pos(this.width / 2 - 75, this.height - 25).size(150, 20).build();
        addRenderableWidget(done);
    }

    @Override
    public void render(PoseStack PoseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(PoseStack);
        drawCenteredString(PoseStack, this.font, this.title, this.width / 2, 20, -1);
        super.render(PoseStack, mouseX, mouseY, partialTicks);
    }
}
