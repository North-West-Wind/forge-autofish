package ml.northwestwind.forgeautofish;

import ml.northwestwind.forgeautofish.config.Config;
import ml.northwestwind.forgeautofish.keybind.KeyBinds;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("forgeautofish")
public class AutoFish
{
    public static final String MODID = "forgeautofish";
    public static final Logger LOGGER = LogManager.getLogger();

    public AutoFish() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        Config.loadConfig(FMLPaths.CONFIGDIR.get().resolve("forgeautofish-client.toml").toString());
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, ()->new IExtensionPoint.DisplayTest(()->"ANY", (remote, isServer)-> true));
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        KeyBinds.register();
    }
}
