package com.northwestwind.forgeautofish.handler;

import com.northwestwind.forgeautofish.AutoFish;
import com.northwestwind.forgeautofish.keybind.KeyBinds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AutoFish.MODID, value = Dist.CLIENT)
public class AutoFishHandler {

    private boolean autofishenabled;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if(KeyBinds.autofish.isPressed()) {
            autofishenabled = !autofishenabled;
            Minecraft.getInstance().player.sendStatusMessage(new TextComponentTranslation("toggle.forgeautofish", (autofishenabled ? "\u00A7aEnabled" : "\u00A7cDisabled")), true);
        }
    }

    private boolean fished = false;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onPlayerTick(TickEvent.PlayerTickEvent e) throws InterruptedException {
        if(!autofishenabled) return;
        if(!(e.player.getHeldItemMainhand().getItem() instanceof ItemFishingRod)) {
            return;
        }
        if(e.player.fishEntity == null) {
            return;
        }
        if(fished) return;
        EntityFishHook fishingHook = e.player.fishEntity;
        double x = fishingHook.motionX;
        double z = fishingHook.motionZ;
        double y =fishingHook.motionY;
        if(y < -0.1 && e.player.fishEntity.isInWater() && x == 0 && z == 0) {
            NetHandlerPlayClient nethandler = Minecraft.getInstance().getConnection();
            if(nethandler != null) {
                nethandler.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            } else {
                rightClick(e.player.world, e.player, EnumHand.MAIN_HAND);
            }
            if(!fished) {
                fished = true;
                startTimer();
            }
            if(e.player.getHeldItemMainhand().isEmpty()) return;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                        if(nethandler != null) {
                            nethandler.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                        } else {
                            rightClick(e.player.world, e.player, EnumHand.MAIN_HAND);
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void startTimer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    fished = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void rightClick(World world, EntityPlayer player, EnumHand hand) {
        player.getHeldItemMainhand().useItemRightClick(world, player, hand);
    }
}
