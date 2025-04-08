package net.minecraft.world.entity;

import java.util.EnumSet;
import java.util.Set;

public enum RelativeMovement {
   X(0),
   Y(1),
   Z(2),
   Y_ROT(3),
   X_ROT(4);

   public static final Set<RelativeMovement> ALL = Set.of(values());
   public static final Set<RelativeMovement> ROTATION = Set.of(X_ROT, Y_ROT);
   private final int bit;

   private RelativeMovement(int p_265098_) {
      this.bit = p_265098_;
   }

   private int getMask() {
      return 1 << this.bit;
   }

   private boolean isSet(int p_265420_) {
      return (p_265420_ & this.getMask()) == this.getMask();
   }

   public static Set<RelativeMovement> unpack(int p_265683_) {
      Set<RelativeMovement> set = EnumSet.noneOf(RelativeMovement.class);

      for(RelativeMovement relativemovement : values()) {
         if (relativemovement.isSet(p_265683_)) {
            set.add(relativemovement);
         }
      }

      return set;
   }

   public static int pack(Set<RelativeMovement> p_265525_) {
      int i = 0;

      for(RelativeMovement relativemovement : p_265525_) {
         i |= relativemovement.getMask();
      }

      return i;
   }
}