package net.modderg.thedigimod.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.gui.DigiviceScreen;
import net.modderg.thedigimod.sound.DigiSounds;
import software.bernie.geckolib.util.ClientUtils;

import java.util.function.Supplier;

public class SToCDigiviceScreenHandle {
    public static void handle(Supplier<NetworkEvent.Context> context, int id){
        if (context.get().getDirection().getReceptionSide().isClient()) {
            Minecraft.getInstance().player.playNotifySound(DigiSounds.OPEN_GUI_SOUND.get(), SoundSource.PLAYERS, 0.25F, 1.0F);
            if(id != -1){
                Entity cd = ClientUtils.getLevel().getEntity(id);
                if(cd != null){
                    Minecraft.getInstance().setScreen(new DigiviceScreen(Component.empty(),cd));
                }
            } else {
                Minecraft.getInstance().setScreen(new DigiviceScreen(Component.empty(),null));
            }
        }
    }
}
