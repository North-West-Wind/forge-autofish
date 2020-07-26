package com.northwestwind.forgeautofish;

import com.northwestwind.forgeautofish.handler.AutoFishHandler;
import com.northwestwind.forgeautofish.keybind.KeyBinds;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("forgeautofish")
public class AutoFish
{
    public static final String MODID = "forgeautofish";

    public AutoFish() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(new AutoFishHandler());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        KeyBinds.register();
    }
}
