package com.northwestwind.forgeautofish;

import com.northwestwind.forgeautofish.keybind.KeyBinds;
import com.northwestwind.forgeautofish.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = AutoFish.MODID, name = AutoFish.NAME, version = AutoFish.VERSION)
public class AutoFish
{
    public static final String MODID = "forgeautofish";
    public static final String NAME = "AutoFish for Forge";
    public static final String VERSION = "1.0.3";
    public static final String ACCEPTED = "[1.12.2]";
    public static final String CLIENT_PROXY = "com.northwestwind.forgeautofish.proxy.ClientProxy";
    public static final String COMMON_PROXY = "com.northwestwind.forgeautofish.proxy.CommonProxy";

    @Mod.Instance
    public static AutoFish instance;

    @SidedProxy(clientSide = AutoFish.CLIENT_PROXY, serverSide = AutoFish.COMMON_PROXY)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event) {}

    @Mod.EventHandler
    public static void init(final FMLInitializationEvent event) {
        KeyBinds.register();
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event) {}
}
