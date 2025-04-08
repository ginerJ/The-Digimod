package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;

public class References {
   public static final DSL.TypeReference LEVEL = () -> {
      return "level";
   };
   public static final DSL.TypeReference PLAYER = () -> {
      return "player";
   };
   public static final DSL.TypeReference CHUNK = () -> {
      return "chunk";
   };
   public static final DSL.TypeReference HOTBAR = () -> {
      return "hotbar";
   };
   public static final DSL.TypeReference OPTIONS = () -> {
      return "options";
   };
   public static final DSL.TypeReference STRUCTURE = () -> {
      return "structure";
   };
   public static final DSL.TypeReference STATS = () -> {
      return "stats";
   };
   public static final DSL.TypeReference SAVED_DATA = () -> {
      return "saved_data";
   };
   public static final DSL.TypeReference ADVANCEMENTS = () -> {
      return "advancements";
   };
   public static final DSL.TypeReference POI_CHUNK = () -> {
      return "poi_chunk";
   };
   public static final DSL.TypeReference ENTITY_CHUNK = () -> {
      return "entity_chunk";
   };
   public static final DSL.TypeReference BLOCK_ENTITY = () -> {
      return "block_entity";
   };
   public static final DSL.TypeReference ITEM_STACK = () -> {
      return "item_stack";
   };
   public static final DSL.TypeReference BLOCK_STATE = () -> {
      return "block_state";
   };
   public static final DSL.TypeReference ENTITY_NAME = () -> {
      return "entity_name";
   };
   public static final DSL.TypeReference ENTITY_TREE = () -> {
      return "entity_tree";
   };
   public static final DSL.TypeReference ENTITY = () -> {
      return "entity";
   };
   public static final DSL.TypeReference BLOCK_NAME = () -> {
      return "block_name";
   };
   public static final DSL.TypeReference ITEM_NAME = () -> {
      return "item_name";
   };
   public static final DSL.TypeReference GAME_EVENT_NAME = () -> {
      return "game_event_name";
   };
   public static final DSL.TypeReference UNTAGGED_SPAWNER = () -> {
      return "untagged_spawner";
   };
   public static final DSL.TypeReference STRUCTURE_FEATURE = () -> {
      return "structure_feature";
   };
   public static final DSL.TypeReference OBJECTIVE = () -> {
      return "objective";
   };
   public static final DSL.TypeReference TEAM = () -> {
      return "team";
   };
   public static final DSL.TypeReference RECIPE = () -> {
      return "recipe";
   };
   public static final DSL.TypeReference BIOME = () -> {
      return "biome";
   };
   public static final DSL.TypeReference MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST = () -> {
      return "multi_noise_biome_source_parameter_list";
   };
   public static final DSL.TypeReference WORLD_GEN_SETTINGS = () -> {
      return "world_gen_settings";
   };
}