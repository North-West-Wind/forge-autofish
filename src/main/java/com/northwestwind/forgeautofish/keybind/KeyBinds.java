package com.northwestwind.forgeautofish.keybind;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBinds {

    public static KeyBinding autofish;
    public static KeyBinding rodprotect;
    public static KeyBinding autoreplace;
    public static KeyBinding command;

    public static void register() {
        autofish = new KeyBinding(new TranslationTextComponent("key.forgeautofish.autofish").getString(), 43, "key.categories.forgeautofish");
        rodprotect = new KeyBinding(new TranslationTextComponent("key.forgeautofish.rodprotect").getString(), 27, "key.categories.forgeautofish");
        autoreplace = new KeyBinding(new TranslationTextComponent("key.forgeautofish.autoreplace").getString(), 26, "key.categories.forgeautofish");
        command = new KeyBinding(new TranslationTextComponent("key.forgeautofish.command").getString(), 25, "key.categories.forgeautofish");

        ClientRegistry.registerKeyBinding(autofish);
        ClientRegistry.registerKeyBinding(rodprotect);
        ClientRegistry.registerKeyBinding(autoreplace);
    }
}
