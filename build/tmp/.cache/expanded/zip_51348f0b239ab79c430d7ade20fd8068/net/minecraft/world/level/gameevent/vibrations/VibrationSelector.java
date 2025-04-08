package net.minecraft.world.level.gameevent.vibrations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;

public class VibrationSelector {
   public static final Codec<VibrationSelector> CODEC = RecordCodecBuilder.create((p_249445_) -> {
      return p_249445_.group(VibrationInfo.CODEC.optionalFieldOf("event").forGetter((p_251862_) -> {
         return p_251862_.currentVibrationData.map(Pair::getLeft);
      }), Codec.LONG.fieldOf("tick").forGetter((p_251458_) -> {
         return p_251458_.currentVibrationData.map(Pair::getRight).orElse(-1L);
      })).apply(p_249445_, VibrationSelector::new);
   });
   private Optional<Pair<VibrationInfo, Long>> currentVibrationData;

   public VibrationSelector(Optional<VibrationInfo> p_251736_, long p_251649_) {
      this.currentVibrationData = p_251736_.map((p_251571_) -> {
         return Pair.of(p_251571_, p_251649_);
      });
   }

   public VibrationSelector() {
      this.currentVibrationData = Optional.empty();
   }

   public void addCandidate(VibrationInfo p_250149_, long p_249749_) {
      if (this.shouldReplaceVibration(p_250149_, p_249749_)) {
         this.currentVibrationData = Optional.of(Pair.of(p_250149_, p_249749_));
      }

   }

   private boolean shouldReplaceVibration(VibrationInfo p_248697_, long p_249040_) {
      if (this.currentVibrationData.isEmpty()) {
         return true;
      } else {
         Pair<VibrationInfo, Long> pair = this.currentVibrationData.get();
         long i = pair.getRight();
         if (p_249040_ != i) {
            return false;
         } else {
            VibrationInfo vibrationinfo = pair.getLeft();
            if (p_248697_.distance() < vibrationinfo.distance()) {
               return true;
            } else if (p_248697_.distance() > vibrationinfo.distance()) {
               return false;
            } else {
               return VibrationSystem.getGameEventFrequency(p_248697_.gameEvent()) > VibrationSystem.getGameEventFrequency(vibrationinfo.gameEvent());
            }
         }
      }
   }

   public Optional<VibrationInfo> chosenCandidate(long p_250251_) {
      if (this.currentVibrationData.isEmpty()) {
         return Optional.empty();
      } else {
         return this.currentVibrationData.get().getRight() < p_250251_ ? Optional.of(this.currentVibrationData.get().getLeft()) : Optional.empty();
      }
   }

   public void startOver() {
      this.currentVibrationData = Optional.empty();
   }
}