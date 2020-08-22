package com.northwestwind.forgeautofish.keybind;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBinds {

    public static KeyBinding autofish;
    public static KeyBinding rodprotect;

    public static void register() {
        autofish = new KeyBinding(new TranslationTextComponent("key.forgeautofish.autofish").getString(), 43, "key.categories.forgeautofish");
        rodprotect = new KeyBinding(new TranslationTextComponent("key.forgeautofish.rodprotect").getString(), 27, "key.categories.forgeautofish");

        ClientRegistry.registerKeyBinding(autofish);
        ClientRegistry.registerKeyBinding(rodprotect);
    }
}
