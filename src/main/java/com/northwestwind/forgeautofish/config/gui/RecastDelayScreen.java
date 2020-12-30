package com.northwestwind.forgeautofish.config.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.northwestwind.forgeautofish.config.Config;
import com.northwestwind.forgeautofish.handler.AutoFishHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.regex.Pattern;

public class RecastDelayScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget recastDelay;

    protected RecastDelayScreen(Screen parent) {
        super(new TranslationTextComponent("gui.setrecastdelay"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        recastDelay = new TextFieldWidget(this.font, this.width / 2 - 75, this.height / 2 - 25, 150, 20, new TranslationTextComponent("gui.setrecastdelay.recastdelay")) {
            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_2) this.setText("");
                return super.mouseClicked(mouseX, mouseY, button);
            }
        };
        recastDelay.setText(Long.toString(AutoFishHandler.recastDelay));
        this.children.add(recastDelay);
        Button save = new Button(this.width / 2 - 75, this.height / 2, 150, 20, new TranslationTextComponent("gui.setrecastdelay.save"), button -> {
            if (!isNumeric(recastDelay.getText())) recastDelay.setText(Long.toString(AutoFishHandler.recastDelay));
            long delay = Long.parseLong(recastDelay.getText());
            Config.setRecastDelay(delay);
            Minecraft.getInstance().displayGuiScreen(parent);
        });
        addButton(save);
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 20, -1);
        this.recastDelay.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
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
