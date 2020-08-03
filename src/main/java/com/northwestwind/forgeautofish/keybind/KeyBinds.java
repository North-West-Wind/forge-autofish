package com.northwestwind.forgeautofish.keybind;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBinds {

    public static KeyBinding autofish;

    public static void register() {
        autofish = new KeyBinding(new TextComponentTranslation("key.forgeautofish.autofish").getString(), 43, "key.categories.forgeautofish");

        ClientRegistry.registerKeyBinding(autofish);
    }
}
