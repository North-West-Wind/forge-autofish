package ml.northwestwind.forgeautofish.config.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

public class SettingsScreen extends Screen {
    public SettingsScreen() {
        super(new TranslatableComponent("gui.forgeautofish"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void init() {
        Button recastDelay = new Button(this.width / 2 - 75, this.height / 2 - 50, 150, 20, new TranslatableComponent("gui.forgeautofish.recastdelay"), button -> Minecraft.getInstance().setScreen(new RecastDelayScreen(this)));
        addRenderableWidget(recastDelay);
        Button reelInDelay = new Button(this.width / 2 - 75, this.height / 2 - 25, 150, 20, new TranslatableComponent("gui.forgeautofish.reelindelay"), button -> Minecraft.getInstance().setScreen(new ReelInDelayScreen(this)));
        addRenderableWidget(reelInDelay);
        Button throwDelay = new Button(this.width / 2 - 75, this.height / 2, 150, 20, new TranslatableComponent("gui.forgeautofish.throwdelay"), button -> Minecraft.getInstance().setScreen(new ThrowDelayScreen(this)));
        addRenderableWidget(throwDelay);
        Button checkInterval = new Button(this.width / 2 - 75, this.height / 2 + 25, 150, 20, new TranslatableComponent("gui.forgeautofish.checkinterval"), button -> Minecraft.getInstance().setScreen(new CheckIntervalScreen(this)));
        addRenderableWidget(checkInterval);
        Button filter = new Button(this.width / 2 - 75, this.height / 2 + 50, 150, 20, new TranslatableComponent("gui.forgeautofish.filter"), button -> Minecraft.getInstance().setScreen(new SuperFilterScreen(this)));
        addRenderableWidget(filter);
        Button done = new Button(this.width / 2 - 75, this.height - 25, 150, 20, new TranslatableComponent("gui.forgeautofish.done"), button -> onClose());
        addRenderableWidget(done);
    }

    @Override
    public void render(PoseStack PoseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(PoseStack);
        drawCenteredString(PoseStack, this.font, this.title, this.width / 2, 20, -1);
        super.render(PoseStack, mouseX, mouseY, partialTicks);
    }
}
