package com.northwestwind.forgeautofish.config.gui;

import com.northwestwind.forgeautofish.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class SuperFilterScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget search;
    private Collection<Item> original;
    private Collection<Item> searching;
    private int page = 0, maxPage, max = 30;
    private Button previous, next;
    int reducedHeight;
    int reducedWidth;

    protected SuperFilterScreen(Screen parent) {
        super(new TranslationTextComponent("gui.superfilterscreen"));
        this.parent = parent;
    }

    @Override
    public void tick() {
        search.tick();
        previous.visible = page >= 1;
        next.visible = page < maxPage - 1;
    }

    @Override
    protected void init() {
        reducedHeight = this.height - 90;
        reducedWidth = this.width - 30;
        max = /* (int) Math.round(30 * (reducedWidth / 550.0 + reducedHeight / 330.0) / 2.0) */ 30;
        original = Config.FILTER.get().stream().map(string -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(string))).collect(Collectors.toList());
        maxPage = (int) Math.ceil(original.size() / (double) max);
        searching = original;
        search = new TextFieldWidget(this.font, this.width / 2 - 75, 35, 150, 20, new TranslationTextComponent("gui.superfilterscreen.search").getString()) {
            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_2) this.setText("");
                return super.mouseClicked(mouseX, mouseY, button);
            }
        };
        search.setResponder(s -> {
            String[] args = s.split("/ +/");
            String[] mods = Arrays.stream(args).filter(s1 -> s1.startsWith("@")).toArray(String[]::new);
            String[] tags = Arrays.stream(args).filter(s1 -> s1.startsWith("#")).toArray(String[]::new);
            String[] finalArgs = Arrays.stream(args).filter(s1 -> !s1.startsWith("@") && !s1.startsWith("#")).toArray(String[]::new);;
            searching = original.stream().filter(item -> {
                boolean matchmod = mods.length < 1, matchtag = tags.length < 1, matcharg = finalArgs.length < 1;
                for (String mod : mods) {
                    mod = mod.toLowerCase().substring(1);
                    matchmod = item.getRegistryName().getNamespace().toLowerCase().contains(mod);
                }
                for (String tag : tags) {
                    String finalTag = tag.toLowerCase().substring(1);
                    matchtag = item.getTags().stream().anyMatch(r -> r.getPath().contains(finalTag));
                }
                for (String arg : finalArgs) {
                    arg = arg.toLowerCase();
                    matcharg = item.getRegistryName().getPath().contains(arg) || item.getName().getString().contains(arg);
                }
                return matchmod && matchtag && matcharg;
            }).collect(Collectors.toList());
            maxPage = (int) Math.ceil(original.size() / (double) max);
            if (page > maxPage - 1) page = maxPage - 1;
        });
        this.children.add(search);
        Button add = new Button(this.width / 2 - 75, 60, 72, 20, new TranslationTextComponent("gui.superfilterscreen.openfilter").getString(), button -> Minecraft.getInstance().displayGuiScreen(new FilterSelectionScreen(this)));
        addButton(add);
        Button done = new Button(this.width / 2 + 3, 60, 72, 20, new TranslationTextComponent("gui.superfilterscreen.done").getString(), button -> Minecraft.getInstance().displayGuiScreen(parent));
        addButton(done);
        previous = new Button(this.width / 2 - 100, 60, 20, 20, new StringTextComponent("<").getString(), button -> {
            if (page > 0) page--;
        });
        previous.visible = false;
        addButton(previous);
        next = new Button(this.width / 2 + 80, 60, 20, 20, new StringTextComponent(">").getString(), button -> {
            if (page < maxPage - 1) page++;
        });
        next.visible = false;
        addButton(next);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        drawCenteredString(this.font, this.title.getString(), this.width / 2, 20, -1);
        Item[] items = searching.toArray(new Item[0]);
        for (int i = page * max; i < Math.min((page + 1) * max, searching.size()); i++) {
            Item item = items[i];
            int h = (i % max) / (max / 3);
            int k = (i % max) % (max / 3);
            ItemStack stack = ItemStack.EMPTY;
            if (item != null) stack = new ItemStack(item);
            RenderHelper.enableStandardItemLighting();
            if (!stack.isEmpty()) itemRenderer.renderItemAndEffectIntoGUI(stack, (reducedWidth * h / 3) + 15, (reducedHeight * k / (max / 3)) + 90);
            RenderHelper.disableStandardItemLighting();
            this.font.drawString(stack.getDisplayName().getString(), (float) ((reducedWidth * h / 3) + 45), (float) ((reducedHeight * k / (max / 3)) + 95), Color.WHITE.getRGB());
        }
        search.render(mouseX, mouseY, partialTicks);
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
