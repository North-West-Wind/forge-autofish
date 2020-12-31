package com.northwestwind.forgeautofish.config.gui;

import com.northwestwind.forgeautofish.config.Config;
import com.northwestwind.forgeautofish.handler.AutoFishHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.regex.Pattern;

public class ReelInDelayScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget reelInDelay;

    protected ReelInDelayScreen(Screen parent) {
        super(new TranslationTextComponent("gui.setreelindelay"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        reelInDelay = new TextFieldWidget(this.font, this.width / 2 - 75, this.height / 2 - 25, 150, 20, new TranslationTextComponent("gui.setreelindelay.reelindelay").getString()) {
            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_2) this.setText("");
                return super.mouseClicked(mouseX, mouseY, button);
            }
        };
        reelInDelay.setText(Long.toString(AutoFishHandler.reelInDelay));
        this.children.add(reelInDelay);
        Button save = new Button(this.width / 2 - 75, this.height / 2, 150, 20, new TranslationTextComponent("gui.setreelindelay.save").getString(), button -> {
            if (!isNumeric(reelInDelay.getText())) reelInDelay.setText(Long.toString(AutoFishHandler.reelInDelay));
            long delay = Long.parseLong(reelInDelay.getText());
            Config.setReelInDelay(delay);
            Minecraft.getInstance().displayGuiScreen(parent);
        });
        addButton(save);
    }

    @Override
    public void tick() {
        reelInDelay.tick();
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
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        drawCenteredString(this.font, this.title.getString(), this.width / 2, 20, -1);
        this.reelInDelay.render(mouseX, mouseY, partialTicks);
        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) Minecraft.getInstance().displayGuiScreen(parent);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
