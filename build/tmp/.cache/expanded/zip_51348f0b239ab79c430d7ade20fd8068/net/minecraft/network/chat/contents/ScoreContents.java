package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class ScoreContents implements ComponentContents {
   private static final String SCORER_PLACEHOLDER = "*";
   private final String name;
   @Nullable
   private final EntitySelector selector;
   private final String objective;

   @Nullable
   private static EntitySelector parseSelector(String p_237448_) {
      try {
         return (new EntitySelectorParser(new StringReader(p_237448_))).parse();
      } catch (CommandSyntaxException commandsyntaxexception) {
         return null;
      }
   }

   public ScoreContents(String p_237438_, String p_237439_) {
      this.name = p_237438_;
      this.selector = parseSelector(p_237438_);
      this.objective = p_237439_;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public EntitySelector getSelector() {
      return this.selector;
   }

   public String getObjective() {
      return this.objective;
   }

   private String findTargetName(CommandSourceStack p_237442_) throws CommandSyntaxException {
      if (this.selector != null) {
         List<? extends Entity> list = this.selector.findEntities(p_237442_);
         if (!list.isEmpty()) {
            if (list.size() != 1) {
               throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
            }

            return list.get(0).getScoreboardName();
         }
      }

      return this.name;
   }

   private String getScore(String p_237450_, CommandSourceStack p_237451_) {
      MinecraftServer minecraftserver = p_237451_.getServer();
      if (minecraftserver != null) {
         Scoreboard scoreboard = minecraftserver.getScoreboard();
         Objective objective = scoreboard.getObjective(this.objective);
         if (scoreboard.hasPlayerScore(p_237450_, objective)) {
            Score score = scoreboard.getOrCreatePlayerScore(p_237450_, objective);
            return Integer.toString(score.getScore());
         }
      }

      return "";
   }

   public MutableComponent resolve(@Nullable CommandSourceStack p_237444_, @Nullable Entity p_237445_, int p_237446_) throws CommandSyntaxException {
      if (p_237444_ == null) {
         return Component.empty();
      } else {
         String s = this.findTargetName(p_237444_);
         String s1 = p_237445_ != null && s.equals("*") ? p_237445_.getScoreboardName() : s;
         return Component.literal(this.getScore(s1, p_237444_));
      }
   }

   public boolean equals(Object p_237455_) {
      if (this == p_237455_) {
         return true;
      } else {
         if (p_237455_ instanceof ScoreContents) {
            ScoreContents scorecontents = (ScoreContents)p_237455_;
            if (this.name.equals(scorecontents.name) && this.objective.equals(scorecontents.objective)) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashCode() {
      int i = this.name.hashCode();
      return 31 * i + this.objective.hashCode();
   }

   public String toString() {
      return "score{name='" + this.name + "', objective='" + this.objective + "'}";
   }
}