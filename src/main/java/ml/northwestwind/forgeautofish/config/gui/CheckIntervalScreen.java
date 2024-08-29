package ml.northwestwind.forgeautofish.config.gui;

import ml.northwestwind.forgeautofish.AutoFish;
import ml.northwestwind.forgeautofish.config.Config;
import ml.northwestwind.forgeautofish.handler.AutoFishHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.util.regex.Pattern;

public class CheckIntervalScreen extends Screen {
    private final Screen parent;
    private EditBox checkInterval;

    protected CheckIntervalScreen(Screen parent) {
        super(AutoFish.getTranslatableComponent("gui.setcheckinterval"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        checkInterval = new EditBox(this.font, this.width / 2 - 75, this.height / 2 - 25, 150, 20, AutoFish.getTranslatableComponent("gui.setcheckinterval.checkinterval")) {
            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_2) this.setValue("");
                return super.mouseClicked(mouseX, mouseY, button);
            }
        };
        checkInterval.setValue(Long.toString(AutoFishHandler.checkInterval));
        addRenderableWidget(checkInterval);
        Button save = new Button.Builder(AutoFish.getTranslatableComponent("gui.setcheckinterval.save"), button -> {
            if (!isNumeric(checkInterval.getValue())) checkInterval.setValue(Long.toString(AutoFishHandler.checkInterval));
            else {
                long delay = Long.parseLong(checkInterval.getValue());
                if (delay < Config.CHECK_INTERVAL_RANGE[1] || delay > Config.CHECK_INTERVAL_RANGE[2]) checkInterval.setValue(Long.toString(AutoFishHandler.checkInterval));
                else {
                    Config.setCheckInterval(delay);
                    Minecraft.getInstance().setScreen(parent);
                }
            }
        }).pos(this.width / 2 - 75, this.height / 2).size(150, 20).build();
        addRenderableWidget(save);
    }

    @Override
    public void tick() {
        //checkInterval.tick();
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, -1);
        this.checkInterval.render(graphics, mouseX, mouseY, partialTicks);
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
