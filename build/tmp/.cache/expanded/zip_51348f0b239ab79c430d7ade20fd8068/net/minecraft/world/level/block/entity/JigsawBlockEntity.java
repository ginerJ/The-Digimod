package net.minecraft.world.level.block.entity;

import java.util.Arrays;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class JigsawBlockEntity extends BlockEntity {
   public static final String TARGET = "target";
   public static final String POOL = "pool";
   public static final String JOINT = "joint";
   public static final String NAME = "name";
   public static final String FINAL_STATE = "final_state";
   private ResourceLocation name = new ResourceLocation("empty");
   private ResourceLocation target = new ResourceLocation("empty");
   private ResourceKey<StructureTemplatePool> pool = ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation("empty"));
   private JigsawBlockEntity.JointType joint = JigsawBlockEntity.JointType.ROLLABLE;
   private String finalState = "minecraft:air";

   public JigsawBlockEntity(BlockPos p_155605_, BlockState p_155606_) {
      super(BlockEntityType.JIGSAW, p_155605_, p_155606_);
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public ResourceLocation getTarget() {
      return this.target;
   }

   public ResourceKey<StructureTemplatePool> getPool() {
      return this.pool;
   }

   public String getFinalState() {
      return this.finalState;
   }

   public JigsawBlockEntity.JointType getJoint() {
      return this.joint;
   }

   public void setName(ResourceLocation p_59436_) {
      this.name = p_59436_;
   }

   public void setTarget(ResourceLocation p_59439_) {
      this.target = p_59439_;
   }

   public void setPool(ResourceKey<StructureTemplatePool> p_222764_) {
      this.pool = p_222764_;
   }

   public void setFinalState(String p_59432_) {
      this.finalState = p_59432_;
   }

   public void setJoint(JigsawBlockEntity.JointType p_59425_) {
      this.joint = p_59425_;
   }

   protected void saveAdditional(CompoundTag p_187504_) {
      super.saveAdditional(p_187504_);
      p_187504_.putString("name", this.name.toString());
      p_187504_.putString("target", this.target.toString());
      p_187504_.putString("pool", this.pool.location().toString());
      p_187504_.putString("final_state", this.finalState);
      p_187504_.putString("joint", this.joint.getSerializedName());
   }

   public void load(CompoundTag p_155608_) {
      super.load(p_155608_);
      this.name = new ResourceLocation(p_155608_.getString("name"));
      this.target = new ResourceLocation(p_155608_.getString("target"));
      this.pool = ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(p_155608_.getString("pool")));
      this.finalState = p_155608_.getString("final_state");
      this.joint = JigsawBlockEntity.JointType.byName(p_155608_.getString("joint")).orElseGet(() -> {
         return JigsawBlock.getFrontFacing(this.getBlockState()).getAxis().isHorizontal() ? JigsawBlockEntity.JointType.ALIGNED : JigsawBlockEntity.JointType.ROLLABLE;
      });
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public void generate(ServerLevel p_59421_, int p_59422_, boolean p_59423_) {
      BlockPos blockpos = this.getBlockPos().relative(this.getBlockState().getValue(JigsawBlock.ORIENTATION).front());
      Registry<StructureTemplatePool> registry = p_59421_.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
      Holder<StructureTemplatePool> holder = registry.getHolderOrThrow(this.pool);
      JigsawPlacement.generateJigsaw(p_59421_, holder, this.target, p_59422_, blockpos, p_59423_);
   }

   public static enum JointType implements StringRepresentable {
      ROLLABLE("rollable"),
      ALIGNED("aligned");

      private final String name;

      private JointType(String p_59455_) {
         this.name = p_59455_;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static Optional<JigsawBlockEntity.JointType> byName(String p_59458_) {
         return Arrays.stream(values()).filter((p_59461_) -> {
            return p_59461_.getSerializedName().equals(p_59458_);
         }).findFirst();
      }

      public Component getTranslatedName() {
         return Component.translatable("jigsaw_block.joint." + this.name);
      }
   }
}