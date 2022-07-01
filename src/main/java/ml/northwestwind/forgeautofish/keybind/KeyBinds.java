package ml.northwestwind.forgeautofish.keybind;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {

    public static KeyMapping autofish, rodprotect, autoreplace, settings, itemfilter, soundfishing;

    public static void register() {
        autofish = new KeyMapping(new TranslatableComponent("key.forgeautofish.autofish").getString(), GLFW.GLFW_KEY_MINUS, "key.categories.forgeautofish");
        rodprotect = new KeyMapping(new TranslatableComponent("key.forgeautofish.rodprotect").getString(), GLFW.GLFW_KEY_BACKSLASH, "key.categories.forgeautofish");
        autoreplace = new KeyMapping(new TranslatableComponent("key.forgeautofish.autoreplace").getString(), GLFW.GLFW_KEY_RIGHT_BRACKET, "key.categories.forgeautofish");
        settings = new KeyMapping(new TranslatableComponent("key.forgeautofish.settings").getString(), GLFW.GLFW_KEY_K, "key.categories.forgeautofish");
        itemfilter = new KeyMapping(new TranslatableComponent("key.forgeautofish.itemfilter").getString(), GLFW.GLFW_KEY_APOSTROPHE, "key.categories.forgeautofish");
        soundfishing = new KeyMapping(new TranslatableComponent("key.forgeautofish.soundfishing").getString(), GLFW.GLFW_KEY_LEFT_BRACKET, "key.categories.forgeautofish");

        ClientRegistry.registerKeyBinding(autofish);
        ClientRegistry.registerKeyBinding(rodprotect);
        ClientRegistry.registerKeyBinding(autoreplace);
        ClientRegistry.registerKeyBinding(settings);
        ClientRegistry.registerKeyBinding(itemfilter);
        ClientRegistry.registerKeyBinding(soundfishing);
    }
}
