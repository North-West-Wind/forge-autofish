package com.northwestwind.forgeautofish.keybind;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {

    public static KeyBinding autofish;
    public static KeyBinding rodprotect;
    public static KeyBinding autoreplace;
    public static KeyBinding settings;
    public static KeyBinding itemfilter;

    public static void register() {
        autofish = new KeyBinding(new TranslationTextComponent("key.forgeautofish.autofish").getString(), GLFW.GLFW_KEY_MINUS, "key.categories.forgeautofish");
        rodprotect = new KeyBinding(new TranslationTextComponent("key.forgeautofish.rodprotect").getString(), GLFW.GLFW_KEY_BACKSLASH, "key.categories.forgeautofish");
        autoreplace = new KeyBinding(new TranslationTextComponent("key.forgeautofish.autoreplace").getString(), GLFW.GLFW_KEY_RIGHT_BRACKET, "key.categories.forgeautofish");
        settings = new KeyBinding(new TranslationTextComponent("key.forgeautofish.settings").getString(), GLFW.GLFW_KEY_K, "key.categories.forgeautofish");
        itemfilter = new KeyBinding(new TranslationTextComponent("key.forgeautofish.itemfilter").getString(), GLFW.GLFW_KEY_APOSTROPHE, "key.categories.forgeautofish");

        ClientRegistry.registerKeyBinding(autofish);
        ClientRegistry.registerKeyBinding(rodprotect);
        ClientRegistry.registerKeyBinding(autoreplace);
        ClientRegistry.registerKeyBinding(settings);
        ClientRegistry.registerKeyBinding(itemfilter);
    }
}
