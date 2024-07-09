package net.modderg.thedigimod.events;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.packet.CToSEntityJoinedClientPacket;
import net.modderg.thedigimod.packet.PacketInit;
import net.modderg.thedigimod.packet.SToCLoadJsonDataPacket;

@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeBusEvents {

    @SubscribeEvent
    public static void onEntityJoinServerWorld(EntityJoinLevelEvent event) {
        if(event.getEntity() instanceof CustomDigimon cd){
            if(cd.level().isClientSide()){
                PacketInit.sendToServer(new CToSEntityJoinedClientPacket(cd.getId()));
            }
            cd.initInventory();
        }
    }

    public static RelaodListener THE_DIGIMON_RELOAD_LISTENER = new RelaodListener((new GsonBuilder()).create(),"digimon");

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(THE_DIGIMON_RELOAD_LISTENER);
    }
}