package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.slf4j.Logger;

public class PoolElementStructurePiece extends StructurePiece {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected final StructurePoolElement element;
   protected BlockPos position;
   private final int groundLevelDelta;
   protected final Rotation rotation;
   private final List<JigsawJunction> junctions = Lists.newArrayList();
   private final StructureTemplateManager structureTemplateManager;

   public PoolElementStructurePiece(StructureTemplateManager p_226495_, StructurePoolElement p_226496_, BlockPos p_226497_, int p_226498_, Rotation p_226499_, BoundingBox p_226500_) {
      super(StructurePieceType.JIGSAW, 0, p_226500_);
      this.structureTemplateManager = p_226495_;
      this.element = p_226496_;
      this.position = p_226497_;
      this.groundLevelDelta = p_226498_;
      this.rotation = p_226499_;
   }

   public PoolElementStructurePiece(StructurePieceSerializationContext p_192406_, CompoundTag p_192407_) {
      super(StructurePieceType.JIGSAW, p_192407_);
      this.structureTemplateManager = p_192406_.structureTemplateManager();
      this.position = new BlockPos(p_192407_.getInt("PosX"), p_192407_.getInt("PosY"), p_192407_.getInt("PosZ"));
      this.groundLevelDelta = p_192407_.getInt("ground_level_delta");
      DynamicOps<Tag> dynamicops = RegistryOps.create(NbtOps.INSTANCE, p_192406_.registryAccess());
      this.element = StructurePoolElement.CODEC.parse(dynamicops, p_192407_.getCompound("pool_element")).resultOrPartial(LOGGER::error).orElseThrow(() -> {
         return new IllegalStateException("Invalid pool element found");
      });
      this.rotation = Rotation.valueOf(p_192407_.getString("rotation"));
      this.boundingBox = this.element.getBoundingBox(this.structureTemplateManager, this.position, this.rotation);
      ListTag listtag = p_192407_.getList("junctions", 10);
      this.junctions.clear();
      listtag.forEach((p_204943_) -> {
         this.junctions.add(JigsawJunction.deserialize(new Dynamic<>(dynamicops, p_204943_)));
      });
   }

   protected void addAdditionalSaveData(StructurePieceSerializationContext p_192425_, CompoundTag p_192426_) {
      p_192426_.putInt("PosX", this.position.getX());
      p_192426_.putInt("PosY", this.position.getY());
      p_192426_.putInt("PosZ", this.position.getZ());
      p_192426_.putInt("ground_level_delta", this.groundLevelDelta);
      DynamicOps<Tag> dynamicops = RegistryOps.create(NbtOps.INSTANCE, p_192425_.registryAccess());
      StructurePoolElement.CODEC.encodeStart(dynamicops, this.element).resultOrPartial(LOGGER::error).ifPresent((p_163125_) -> {
         p_192426_.put("pool_element", p_163125_);
      });
      p_192426_.putString("rotation", this.rotation.name());
      ListTag listtag = new ListTag();

      for(JigsawJunction jigsawjunction : this.junctions) {
         listtag.add(jigsawjunction.serialize(dynamicops).getValue());
      }

      p_192426_.put("junctions", listtag);
   }

   public void postProcess(WorldGenLevel p_226502_, StructureManager p_226503_, ChunkGenerator p_226504_, RandomSource p_226505_, BoundingBox p_226506_, ChunkPos p_226507_, BlockPos p_226508_) {
      this.place(p_226502_, p_226503_, p_226504_, p_226505_, p_226506_, p_226508_, false);
   }

   public void place(WorldGenLevel p_226510_, StructureManager p_226511_, ChunkGenerator p_226512_, RandomSource p_226513_, BoundingBox p_226514_, BlockPos p_226515_, boolean p_226516_) {
      this.element.place(this.structureTemplateManager, p_226510_, p_226511_, p_226512_, this.position, p_226515_, this.rotation, p_226514_, p_226513_, p_226516_);
   }

   public void move(int p_72616_, int p_72617_, int p_72618_) {
      super.move(p_72616_, p_72617_, p_72618_);
      this.position = this.position.offset(p_72616_, p_72617_, p_72618_);
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public String toString() {
      return String.format(Locale.ROOT, "<%s | %s | %s | %s>", this.getClass().getSimpleName(), this.position, this.rotation, this.element);
   }

   public StructurePoolElement getElement() {
      return this.element;
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public int getGroundLevelDelta() {
      return this.groundLevelDelta;
   }

   public void addJunction(JigsawJunction p_209917_) {
      this.junctions.add(p_209917_);
   }

   public List<JigsawJunction> getJunctions() {
      return this.junctions;
   }
}