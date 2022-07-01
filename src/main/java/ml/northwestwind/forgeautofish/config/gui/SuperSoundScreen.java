package ml.northwestwind.forgeautofish.config.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import ml.northwestwind.forgeautofish.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.IReverseTag;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SuperSoundScreen extends Screen {
    private final Screen parent;
    private EditBox search;
    private List<SoundEvent> original;
    private List<SoundEvent> searching;
    private int page = 0, maxPage, max = 30;
    private Button previous, next;
    int reducedHeight;
    int reducedWidth;

    protected SuperSoundScreen(Screen parent) {
        super(new TranslatableComponent("gui.supersoundscreen"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        reducedHeight = this.height - 90;
        reducedWidth = this.width - 30;
        max = /* (int) Math.round(30 * (reducedWidth / 550.0 + reducedHeight / 330.0) / 2.0) */ 30;
        original = Config.SOUNDS.get().stream().map(string -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(string))).collect(Collectors.toList());
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
            String[] finalArgs = Arrays.stream(args).filter(s1 -> !s1.startsWith("@")).toArray(String[]::new);;
            searching = original.stream().filter(sound -> {
                boolean matchmod = mods.length < 1, matcharg = finalArgs.length < 1;
                for (String mod : mods) {
                    mod = mod.toLowerCase().substring(1);
                    matchmod = sound.getLocation().getNamespace().toLowerCase().contains(mod);
                }
                for (String arg : finalArgs) {
                    arg = arg.toLowerCase();
                    matcharg = sound.getLocation().getPath().contains(arg);
                }
                return matchmod && matcharg;
            }).collect(Collectors.toList());
            maxPage = (int) Math.ceil(original.size() / (double) max);
            if (page > maxPage - 1) page = maxPage - 1;
        });
        addRenderableWidget(search);
        Button add = new Button(this.width / 2 - 75, 60, 72, 20, new TranslatableComponent("gui.supersoundscreen.openfilter"), button -> Minecraft.getInstance().setScreen(new FilterSelectionScreen(this)));
        addRenderableWidget(add);
        Button done = new Button(this.width / 2 + 3, 60, 72, 20, new TranslatableComponent("gui.supersoundscreen.done"), button -> Minecraft.getInstance().setScreen(parent));
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
        drawCenteredString(PoseStack, this.font, this.title, this.width / 2, 20, -1);
        for (int i = page * max; i < Math.min((page + 1) * max, searching.size()); i++) {
            SoundEvent sound = searching.get(i);
            int h = (i % max) / (max / 2);
            int k = (i % max) % (max / 2);
            this.font.draw(PoseStack, sound.getLocation().toString(), (float) ((reducedWidth * h / 2) + 45), (float) ((reducedHeight * k / (max / 2)) + 95), Color.WHITE.getRGB());
        }
        search.render(PoseStack, mouseX, mouseY, partialTicks);
        super.render(PoseStack, mouseX, mouseY, partialTicks);
    }
}
