package ml.northwestwind.forgeautofish.config.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import ml.northwestwind.forgeautofish.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.IReverseTag;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilterSelectionScreen extends Screen {
    private final Screen parent;
    private EditBox search;
    private final Collection<Item> original = ForgeRegistries.ITEMS.getValues();
    private Collection<Item> searching;
    private final Set<Item> selected = new HashSet<>(Config.FILTER.get().stream().map(string -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(string))).collect(Collectors.toList()));
    private int page, maxPage = (int) Math.ceil(original.size() / 300.0), max = 300;
    private boolean clickProcessed = true;
    private double clickX, clickY;
    private Button previous, next;
    int reducedHeight;
    int reducedWidth;

    public FilterSelectionScreen(Screen parent) {
        super(new TranslatableComponent("gui.filterselection"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        reducedHeight = this.height - 90;
        reducedWidth = this.width - 30;
        max = /* (int) Math.round(300 * (reducedWidth / 550.0 + reducedHeight / 330.0) / 2.0) */ 300;
        maxPage = (int) Math.ceil(original.size() / (double) max);
        searching = original;
        search = new EditBox(this.font, this.width / 2 - 75, 35, 150, 20, new TranslatableComponent("gui.superfilterscreen.search")) {
            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_2) this.setValue("");
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
                Optional<IReverseTag<Item>> reverseTagsOptional = ForgeRegistries.ITEMS.tags().getReverseTag(item);
                if (reverseTagsOptional.isPresent())
                    for (String tag : tags) {
                        String finalTag = tag.toLowerCase().substring(1);
                        matchtag = reverseTagsOptional.get().getTagKeys().anyMatch(tagKey -> tagKey.location().getPath().contains(finalTag));
                    }
                for (String arg : finalArgs) {
                    arg = arg.toLowerCase();
                    matcharg = item.getRegistryName().getPath().contains(arg) || item.getDescription().getString().contains(arg);
                }
                return matchmod && matchtag && matcharg;
            }).collect(Collectors.toList());
            maxPage = (int) Math.ceil(searching.size() / (double) max);
            if (page > maxPage - 1) page = maxPage - 1;
        });
        addRenderableWidget(search);
        Button add = new Button(this.width / 2 - 75, 60, 72, 20, new TranslatableComponent("gui.filterselection.save"), button -> {
            List<String> items = selected.stream().map(item -> item.getRegistryName().toString()).collect(Collectors.toList());
            Config.setFILTER(items);
            Minecraft.getInstance().setScreen(parent);
        });
        addRenderableWidget(add);
        Button done = new Button(this.width / 2 + 3, 60, 72, 20, new TranslatableComponent("gui.filterselection.cancel"), button -> Minecraft.getInstance().setScreen(parent));
        addRenderableWidget(done);
        previous = new Button(this.width / 2 - 100, 60, 20, 20, new TextComponent("<"), button -> {
            if (page > 0) page--;
        });
        previous.visible = false;
        addRenderableWidget(previous);
        next = new Button(this.width / 2 + 80, 60, 20, 20, new TextComponent(">"), button -> {
            if (page < maxPage - 1) page++;
        });
        next.visible = false;
        addRenderableWidget(next);
    }

    @Override
    public void render(PoseStack PoseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(PoseStack);
        drawCenteredString(PoseStack, this.font, this.title, this.width / 2, 20, -1);Collection<Item> searchingCopy = Lists.newArrayList();
        Collection<Item> prioritized = searching.stream().filter(item -> {
            boolean pri = Config.PRIORITIZE.get().contains(item.getRegistryName().toString());
            if (!pri) searchingCopy.add(item);
            return pri;
        }).collect(Collectors.toList());
        Item[] items = Stream.concat(prioritized.stream(), searchingCopy.stream()).toArray(Item[]::new);
        if (items.length > 0 && page >= 0) {
            for (int i = page * max; i < Math.min((page + 1) * max, searching.size()); i++) {
                Item item = items[i];
                int h = (i % max) / (max / 30);
                int k = (i % max) % (max / 30);
                int x = getXPos(h, reducedWidth);
                int y = getYPos(k, reducedHeight);
                ItemStack stack = new ItemStack(item);
                if (!stack.isEmpty()) {
                    itemRenderer.renderGuiItem(stack, x, y);
                    if (!clickProcessed && isMouseInRange(clickX, clickY, x, y, x+16, y+16)) {
                        if (selected.contains(item)) selected.remove(item);
                        else selected.add(item);
                        clickProcessed = true;
                    }
                    if (selected.contains(item)) fillGradient(PoseStack, x - 2, y - 2, x + 18, y + 18, Color.GREEN.getRGB(), Color.GREEN.getRGB());
                    else if (isMouseInRange(mouseX, mouseY, x, y,x + 16, y + 16)) fillGradient(PoseStack, x - 2, y - 2, x + 18, y + 18, Color.LIGHT_GRAY.getRGB(), Color.LIGHT_GRAY.getRGB());
                    if (isMouseInRange(mouseX, mouseY, x, y,x + 16, y + 16)) renderTooltip(PoseStack, stack, mouseX, mouseY);
                }
            }
        }
        search.render(PoseStack, mouseX, mouseY, partialTicks);
        super.render(PoseStack, mouseX, mouseY, partialTicks);
    }

    private boolean isMouseInRange(double mouseX, double mouseY, int x1, int y1, int x2, int y2) {
        return mouseX > x1 && mouseX < x2 && mouseY > y1 && mouseY < y2;
    }

    private int getXPos(int h, int width) {
        return (width * h / 30) + 15;
    }

    private int getYPos(int k, int height) {
        return ((height * k / (max / 30)) + 90);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (!search.isFocused()) Minecraft.getInstance().setScreen(parent);
            else search.setFocus(false);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        clickX = mouseX;
        clickY = mouseY;
        clickProcessed = false;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void tick() {
        search.tick();
        super.tick();
        previous.visible = page >= 1;
        next.visible = page < maxPage - 1;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
