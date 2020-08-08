package com.northwestwind.forgeautofish.keybind;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class KeyBinds {

    public static KeyBinding autofish;

    public static void register() {
        autofish = new KeyBinding(new TextComponentTranslation("key.forgeautofish.autofish").getFormattedText(), Keyboard.KEY_BACKSLASH, "key.categories.forgeautofish");

        ClientRegistry.registerKeyBinding(autofish);
    }
}
