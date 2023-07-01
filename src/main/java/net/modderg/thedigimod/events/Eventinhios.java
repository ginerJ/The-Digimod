package net.modderg.thedigimod.events;

import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.modderg.thedigimod.TheDigiMod;

import java.util.List;

@Mod.EventBusSubscriber(modid = TheDigiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Eventinhios {
    @SubscribeEvent
    public static void onPotentialSpawns(LevelEvent.PotentialSpawns event) {
        List<MobSpawnSettings.SpawnerData> spawnerList = event.getSpawnerDataList();
        for (MobSpawnSettings.SpawnerData spawner : spawnerList) {
            spawner = new MobSpawnSettings.SpawnerData(spawner.type, spawner.getWeight(), spawner.minCount, spawner.maxCount * 10);
        }
    }
}
