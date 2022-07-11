package ml.northwestwind.forgeautofish.keybind;

import ml.northwestwind.forgeautofish.AutoFish;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {

    public static KeyMapping autofish, rodprotect, autoreplace, settings, itemfilter;

    public static void register(final RegisterKeyMappingsEvent event) {
        autofish = new KeyMapping(AutoFish.getTranslatableComponent("key.forgeautofish.autofish").getString(), GLFW.GLFW_KEY_MINUS, "key.categories.forgeautofish");
        rodprotect = new KeyMapping(AutoFish.getTranslatableComponent("key.forgeautofish.rodprotect").getString(), GLFW.GLFW_KEY_BACKSLASH, "key.categories.forgeautofish");
        autoreplace = new KeyMapping(AutoFish.getTranslatableComponent("key.forgeautofish.autoreplace").getString(), GLFW.GLFW_KEY_RIGHT_BRACKET, "key.categories.forgeautofish");
        settings = new KeyMapping(AutoFish.getTranslatableComponent("key.forgeautofish.settings").getString(), GLFW.GLFW_KEY_K, "key.categories.forgeautofish");
        itemfilter = new KeyMapping(AutoFish.getTranslatableComponent("key.forgeautofish.itemfilter").getString(), GLFW.GLFW_KEY_APOSTROPHE, "key.categories.forgeautofish");

        event.register(autofish);
        event.register(rodprotect);
        event.register(autoreplace);
        event.register(settings);
        event.register(itemfilter);
    }
}
