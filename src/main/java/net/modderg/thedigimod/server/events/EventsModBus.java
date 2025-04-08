package net.modderg.thedigimod.server.events;

import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import net.modderg.thedigimod.TheDigiMod;
import net.modderg.thedigimod.server.TDConfig;
import net.modderg.thedigimod.server.advancements.TDAdvancements;
import net.modderg.thedigimod.server.block.TDBlocks;
import net.modderg.thedigimod.server.entity.DigimonEntity;
import net.modderg.thedigimod.server.entity.TDEntities;
import net.modderg.thedigimod.server.goods.AbstractTrainingGood;
import net.modderg.thedigimod.server.goods.InitGoods;
import net.modderg.thedigimod.server.events.EventsBus.*;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid=TheDigiMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventsModBus {

    @SubscribeEvent
    public static void onSpawnPlacementRegister(SpawnPlacementRegisterEvent event) {

        for (RegistryObject<EntityType<?>> dig : TDEntities.DIGIMONS.getEntries()) {

            EntityType<DigimonEntity> digimon = (EntityType<DigimonEntity>) dig.get();

            boolean wuter = TDEntities.AQUATIC_DIGIMON.stream().anyMatch(r -> r.get().equals(digimon));

            SpawnPlacements.Type placement = wuter? SpawnPlacements.Type.NO_RESTRICTIONS : SpawnPlacements.Type.ON_GROUND;

            event.register(digimon, placement, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    DigimonEntity::checkDigimonSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        }

        InitGoods.GOODS.getEntries().forEach(good -> {
            EntityType<AbstractTrainingGood> goodType = (EntityType<AbstractTrainingGood>) good.get();

            SpawnPlacements.register(goodType, SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (a,b,c,d,e) -> true);
        });
    }

    @SubscribeEvent
    public static void OnRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerVariables.class);
    }
}