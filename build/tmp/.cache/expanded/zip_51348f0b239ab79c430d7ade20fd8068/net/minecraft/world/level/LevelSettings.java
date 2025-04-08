package net.minecraft.world.level;

import com.mojang.serialization.Dynamic;
import net.minecraft.world.Difficulty;

public final class LevelSettings {
   private final String levelName;
   private final GameType gameType;
   private final boolean hardcore;
   private final Difficulty difficulty;
   private final boolean allowCommands;
   private final GameRules gameRules;
   private final WorldDataConfiguration dataConfiguration;
   private final com.mojang.serialization.Lifecycle lifecycle;

   public LevelSettings(String p_250485_, GameType p_250207_, boolean p_251631_, Difficulty p_252122_, boolean p_248961_, GameRules p_248536_, WorldDataConfiguration p_249797_) {
      this(p_250485_, p_250207_, p_251631_, p_252122_, p_248961_, p_248536_, p_249797_, com.mojang.serialization.Lifecycle.stable());
   }
   public LevelSettings(String p_250485_, GameType p_250207_, boolean p_251631_, Difficulty p_252122_, boolean p_248961_, GameRules p_248536_, WorldDataConfiguration p_249797_, com.mojang.serialization.Lifecycle lifecycle) {
      this.levelName = p_250485_;
      this.gameType = p_250207_;
      this.hardcore = p_251631_;
      this.difficulty = p_252122_;
      this.allowCommands = p_248961_;
      this.gameRules = p_248536_;
      this.dataConfiguration = p_249797_;
      this.lifecycle = lifecycle;
   }

   public static LevelSettings parse(Dynamic<?> p_46925_, WorldDataConfiguration p_251697_) {
      GameType gametype = GameType.byId(p_46925_.get("GameType").asInt(0));
      return new LevelSettings(p_46925_.get("LevelName").asString(""), gametype, p_46925_.get("hardcore").asBoolean(false), p_46925_.get("Difficulty").asNumber().map((p_46928_) -> {
         return Difficulty.byId(p_46928_.byteValue());
      }).result().orElse(Difficulty.NORMAL), p_46925_.get("allowCommands").asBoolean(gametype == GameType.CREATIVE), new GameRules(p_46925_.get("GameRules")), p_251697_, net.minecraftforge.common.ForgeHooks.parseLifecycle(p_46925_.get("forgeLifecycle").asString("stable")));
   }

   public String levelName() {
      return this.levelName;
   }

   public GameType gameType() {
      return this.gameType;
   }

   public boolean hardcore() {
      return this.hardcore;
   }

   public Difficulty difficulty() {
      return this.difficulty;
   }

   public boolean allowCommands() {
      return this.allowCommands;
   }

   public GameRules gameRules() {
      return this.gameRules;
   }

   public WorldDataConfiguration getDataConfiguration() {
      return this.dataConfiguration;
   }

   public LevelSettings withGameType(GameType p_46923_) {
      return new LevelSettings(this.levelName, p_46923_, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, this.dataConfiguration, this.lifecycle);
   }

   public LevelSettings withDifficulty(Difficulty p_46919_) {
      net.minecraftforge.common.ForgeHooks.onDifficultyChange(p_46919_, this.difficulty);
      return new LevelSettings(this.levelName, this.gameType, this.hardcore, p_46919_, this.allowCommands, this.gameRules, this.dataConfiguration, this.lifecycle);
   }

   public LevelSettings withDataConfiguration(WorldDataConfiguration p_250867_) {
      return new LevelSettings(this.levelName, this.gameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, dataConfiguration, this.lifecycle);
   }

   public LevelSettings copy() {
      return new LevelSettings(this.levelName, this.gameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules.copy(), this.dataConfiguration, this.lifecycle);
   }
   public LevelSettings withLifecycle(com.mojang.serialization.Lifecycle lifecycle) {
      return new LevelSettings(this.levelName, this.gameType, this.hardcore, this.difficulty, this.allowCommands, this.gameRules, this.dataConfiguration, lifecycle);
   }
   public com.mojang.serialization.Lifecycle getLifecycle() {
      return this.lifecycle;
   }
}
