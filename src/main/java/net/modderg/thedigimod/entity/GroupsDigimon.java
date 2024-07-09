package net.modderg.thedigimod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

import static net.modderg.thedigimod.entity.InitDigimons.*;


public class GroupsDigimon {

    public static final List<RegistryObject<EntityType<CustomDigimon>>> riderDigimon = List.of(
        AGUMON, AGUMONBLACK, BEARMON, GABUMON,
        BLACKGABUMON, JELLYMON, DRACOMON, PULSEMON, PIYOMON, LOPMON,
        IMPMON, SHOUTMON, KAMEMON
    );

    public static final List<RegistryObject<EntityType<CustomDigimon>>> baby2Digimon = List.of(
            KOROMON, MOCHIMON, TSUNOMON, GIGIMON, BABYDMON, PUYOYOMON, BIBIMON,
            YOKOMON, YARMON, TOKOMON, GOROMON, CHOCOMON, CHAPMON
    );
}
