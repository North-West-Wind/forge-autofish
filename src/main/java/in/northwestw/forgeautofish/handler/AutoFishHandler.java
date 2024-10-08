package in.northwestw.forgeautofish.handler;

import com.google.common.collect.Lists;
import in.northwestw.forgeautofish.AutoFish;
import in.northwestw.forgeautofish.config.Config;
import in.northwestw.forgeautofish.config.gui.SettingsScreen;
import in.northwestw.forgeautofish.keybind.KeyBinds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = AutoFish.MODID, value = Dist.CLIENT)
public class AutoFishHandler {
    public static boolean autofish, rodprotect, autoreplace, itemfilter;
    public static long recastDelay, reelInDelay, throwDelay, checkInterval;
    private static final List<Item> shouldDrop = Lists.newArrayList();
    private static boolean processingDrop, pendingReelIn, pendingRecast, lastTickFishing, afterDrop;
    private static int dropCd;
    private static long tick, checkTick;
    private static List<ItemStack> itemsBeforeFished;
    private static ItemStack rodStack;

    public static void loadSettingsFromConfig() {
        autofish = Config.AUTO_FISH.get();
        rodprotect = Config.ROD_PROTECT.get();
        autoreplace = Config.AUTO_REPLACE.get();
        itemfilter = Config.ALL_FILTERS.get();

        recastDelay = Config.RECAST_DELAY.get();
        reelInDelay = Config.REEL_IN_DELAY.get();
        throwDelay = Config.THROW_DELAY.get();
        checkInterval = Config.CHECK_INTERVAL.get();
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key e) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (KeyBinds.autofish.consumeClick()) {
            Config.setAutoFish(!autofish);
            if (player != null) player.displayClientMessage(getText("forgeautofish", autofish), true);
        } else if (KeyBinds.rodprotect.consumeClick()) {
            Config.setRodProtect(!rodprotect);
            if (player != null) player.displayClientMessage(getText("rodprotect", rodprotect), true);
        } else if (KeyBinds.autoreplace.consumeClick()) {
            Config.setAutoReplace(!autoreplace);
            if (player != null) player.displayClientMessage(getText("autoreplace", autoreplace), true);
        } else if (KeyBinds.itemfilter.consumeClick()) {
            Config.enableFilter(!itemfilter);
            if (player != null)
                player.displayClientMessage(getText("itemfilter", itemfilter), true);
        } else if (KeyBinds.settings.consumeClick())
            minecraft.setScreen(new SettingsScreen());
    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent.Pre e) {
        if (!e.getEntity().level().isClientSide) return;
        Player player = e.getEntity();
        if (!player.getUUID().equals(Minecraft.getInstance().player.getUUID())) return;
        if (checkTick > 0) checkTick--;
        else {
            checkTick = checkInterval;
            if (!pendingRecast) {
                if (player.fishing == null) recast(player);
                else if (player.fishing.getDeltaMovement().lengthSqr() == 0) pendingReelIn = true;
            }
        }
        if (lastTickFishing && player.fishing == null)
            itemsBeforeFished = Lists.newArrayList(player.getInventory().items);
        lastTickFishing = player.fishing != null;
        if (afterDrop) {
            if (tick == 0 && rodStack != null) {
                player.getInventory().setPickedItem(rodStack);
                rodStack = null;
            }
            tick++;
            if (tick > 2) {
                afterDrop = false;
                tick = 0;
            }
            return;
        }
        if (pendingReelIn) {
            tick++;
            if (tick >= reelInDelay) {
                reelIn(player);
                tick = 0;
                pendingReelIn = false;
            }
            return;
        }
        if (processingDrop) {
            if (dropCd > 0) dropCd--;
            dropItem(player);
            if (shouldDrop.size() <= 0) {
                processingDrop = false;
                afterDrop = true;
            }
            return;
        }
        if (pendingRecast) {
            tick++;
            if (tick >= recastDelay) {
                checkItem(player);
                if (processingDrop) {
                    tick = 0;
                    return;
                }
                recast(player);
                tick = 0;
                pendingRecast = false;
            }
            return;
        }
        if (!autofish || player.fishing == null) return;
        Vec3 vector = player.fishing.getDeltaMovement();
        double x = vector.x();
        double y = vector.y();
        double z = vector.z();
        if (y < -0.075 && !player.level().getFluidState(player.fishing.blockPosition()).isEmpty() && x == 0 && z == 0)
            pendingReelIn = true;
    }

    private static void reelIn(Player player) {
        if (!autofish) return;
        InteractionHand hand = findHandOfRod(player);
        if (hand == null) return;
        click(player, hand, Minecraft.getInstance().gameMode);
        ItemStack fishingRod = player.getItemInHand(hand);
        boolean needReplace = false;
        if (fishingRod.getMaxDamage() - fishingRod.getDamageValue() < 2)
            if (autoreplace) needReplace = true;
            else return;
        else if (fishingRod.getMaxDamage() - fishingRod.getDamageValue() < 3 && !player.isCreative() && rodprotect)
            if (autoreplace) needReplace = true;
            else {
                autofish = false;
                player.displayClientMessage(getText("forgeautofish", autofish), true);
                return;
            }
        if (needReplace) {
            AutoFish.LOGGER.info("Fishing rod broke. Finding replacement...");
            boolean found = false;
            for (int i = 0; i < 9; i++) {
                if (i == player.getInventory().selected) continue;
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.getItem() instanceof FishingRodItem) {
                    if (rodprotect && stack.getMaxDamage() - stack.getDamageValue() < 2) continue;
                    AutoFish.LOGGER.info("Found fishing rod for replacement");
                    player.getInventory().selected = i;
                    found = true;
                    break;
                }
            }
            if (!found) return;
        }
        pendingRecast = true;
    }

    private static void recast(Player player) {
        if (!autofish) return;
        InteractionHand hand = findHandOfRod(player);
        if (hand == null) return;
        ItemStack fishingRod = player.getItemInHand(hand);
        if (fishingRod.isEmpty()) return;
        click(player, hand, Minecraft.getInstance().gameMode);
    }

    private static void checkItem(Player player) {
        if (itemsBeforeFished != null) {
            List<ItemStack> items = player.getInventory().items;
            for (String name : Config.FILTER.get()) {
                ResourceLocation rl = ResourceLocation.parse(name);
                Optional<Item> item = BuiltInRegistries.ITEM.getOptional(rl);
                if (item.isEmpty()) continue;
                int newCount = items.stream().filter(stack -> stack.getItem().equals(item.get())).mapToInt(ItemStack::getCount).reduce(Integer::sum).orElse(0);
                int oldCount = itemsBeforeFished.stream().filter(stack -> stack.getItem().equals(item.get())).mapToInt(ItemStack::getCount).reduce(Integer::sum).orElse(0);
                int diff = newCount - oldCount;
                for (int ii = 0; ii < diff; ii++) shouldDrop.add(item.get());
            }
            itemsBeforeFished = null;
            if (shouldDrop.size() > 0) {
                processingDrop = true;
                rodStack = player.getMainHandItem();
            }
        }
    }

    private static void dropItem(Player player) {
        if (dropCd == 4 || dropCd == 2 || dropCd == 1) return;
        Item item = shouldDrop.get(0);
        if (dropCd == 3) {
            ((LocalPlayer) player).drop(false);
            shouldDrop.remove(item);
            return;
        }
        for (int ii = 0; ii < 9; ii++) {
            final ItemStack stack = player.getInventory().items.get(ii);
            if (!stack.getItem().equals(item)) continue;
            player.getInventory().setPickedItem(stack);
            dropCd = 5;
            return;
        }
        // if item cannot be found in hotbar, just ignore it
        shouldDrop.remove(item);
    }

    private static void click(Player player, InteractionHand hand, @Nullable MultiPlayerGameMode controller) {
        if (controller == null) return;
        controller.useItem(player, hand);
    }

    @Nullable
    private static InteractionHand findHandOfRod(Player player) {
        if (player.getMainHandItem().getItem() instanceof FishingRodItem) return InteractionHand.MAIN_HAND;
        else if (player.getOffhandItem().getItem() instanceof FishingRodItem) return InteractionHand.OFF_HAND;
        else return null;
    }

    private static Component getText(String key, boolean bool) {
        return AutoFish.getTranslatableComponent("toggle." + key, AutoFish.getTranslatableComponent("toggle.enable." + bool).withStyle(bool ? ChatFormatting.GREEN : ChatFormatting.RED));
    }
}