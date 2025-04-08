package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum ChestType implements StringRepresentable {
   SINGLE("single"),
   LEFT("left"),
   RIGHT("right");

   private final String name;

   private ChestType(String p_263109_) {
      this.name = p_263109_;
   }

   public String getSerializedName() {
      return this.name;
   }

   public ChestType getOpposite() {
      ChestType chesttype;
      switch (this) {
         case SINGLE:
            chesttype = SINGLE;
            break;
         case LEFT:
            chesttype = RIGHT;
            break;
         case RIGHT:
            chesttype = LEFT;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return chesttype;
   }
}