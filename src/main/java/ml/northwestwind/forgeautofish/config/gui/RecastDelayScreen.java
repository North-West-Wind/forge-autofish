package ml.northwestwind.forgeautofish.config.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import ml.northwestwind.forgeautofish.config.Config;
import ml.northwestwind.forgeautofish.handler.AutoFishHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import org.lwjgl.glfw.GLFW;

import java.util.regex.Pattern;

public class RecastDelayScreen extends Screen {
    private final Screen parent;
    private EditBox recastDelay;

    protected RecastDelayScreen(Screen parent) {
        super(new TranslatableComponent("gui.setrecastdelay"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        recastDelay = new EditBox(this.font, this.width / 2 - 75, this.height / 2 - 25, 150, 20, new TranslatableComponent("gui.setrecastdelay.recastdelay")) {
            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_2) this.setValue("");
                return super.mouseClicked(mouseX, mouseY, button);
            }
        };
        recastDelay.setValue(Long.toString(AutoFishHandler.recastDelay));
        addRenderableWidget(recastDelay);
        Button save = new Button(this.width / 2 - 75, this.height / 2, 150, 20, new TranslatableComponent("gui.setrecastdelay.save"), button -> {
            if (!isNumeric(recastDelay.getValue())) recastDelay.setValue(Long.toString(AutoFishHandler.recastDelay));
            else {
                long delay = Long.parseLong(recastDelay.getValue());
                if (delay < Config.RECAST_DELAY_RANGE[1] || delay > Config.RECAST_DELAY_RANGE[2]) recastDelay.setValue(Long.toString(AutoFishHandler.recastDelay));
                else {
                    Config.setRecastDelay(delay);
                    Minecraft.getInstance().setScreen(parent);
                }
            }
        });
        addRenderableWidget(save);
    }

    @Override
    public void tick() {
        recastDelay.tick();
        super.tick();
    }

    private static final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    @Override
    public void render(PoseStack PoseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(PoseStack);
        drawCenteredString(PoseStack, this.font, this.title, this.width / 2, 20, -1);
        this.recastDelay.render(PoseStack, mouseX, mouseY, partialTicks);
        super.render(PoseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) Minecraft.getInstance().setScreen(parent);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
