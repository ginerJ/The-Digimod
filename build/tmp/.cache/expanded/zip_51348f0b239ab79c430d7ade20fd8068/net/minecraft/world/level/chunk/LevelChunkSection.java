package net.minecraft.world.level.chunk;

import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class LevelChunkSection {
   public static final int SECTION_WIDTH = 16;
   public static final int SECTION_HEIGHT = 16;
   public static final int SECTION_SIZE = 4096;
   public static final int BIOME_CONTAINER_BITS = 2;
   private short nonEmptyBlockCount;
   private short tickingBlockCount;
   private short tickingFluidCount;
   private final PalettedContainer<BlockState> states;
   private PalettedContainerRO<Holder<Biome>> biomes;

   public LevelChunkSection(PalettedContainer<BlockState> p_282846_, PalettedContainerRO<Holder<Biome>> p_281695_) {
      this.states = p_282846_;
      this.biomes = p_281695_;
      this.recalcBlockCounts();
   }

   public LevelChunkSection(Registry<Biome> p_282873_) {
      this.states = new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
      this.biomes = new PalettedContainer<>(p_282873_.asHolderIdMap(), p_282873_.getHolderOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES);
   }

   public BlockState getBlockState(int p_62983_, int p_62984_, int p_62985_) {
      return this.states.get(p_62983_, p_62984_, p_62985_);
   }

   public FluidState getFluidState(int p_63008_, int p_63009_, int p_63010_) {
      return this.states.get(p_63008_, p_63009_, p_63010_).getFluidState();
   }

   public void acquire() {
      this.states.acquire();
   }

   public void release() {
      this.states.release();
   }

   public BlockState setBlockState(int p_62987_, int p_62988_, int p_62989_, BlockState p_62990_) {
      return this.setBlockState(p_62987_, p_62988_, p_62989_, p_62990_, true);
   }

   public BlockState setBlockState(int p_62992_, int p_62993_, int p_62994_, BlockState p_62995_, boolean p_62996_) {
      BlockState blockstate;
      if (p_62996_) {
         blockstate = this.states.getAndSet(p_62992_, p_62993_, p_62994_, p_62995_);
      } else {
         blockstate = this.states.getAndSetUnchecked(p_62992_, p_62993_, p_62994_, p_62995_);
      }

      FluidState fluidstate = blockstate.getFluidState();
      FluidState fluidstate1 = p_62995_.getFluidState();
      if (!blockstate.isAir()) {
         --this.nonEmptyBlockCount;
         if (blockstate.isRandomlyTicking()) {
            --this.tickingBlockCount;
         }
      }

      if (!fluidstate.isEmpty()) {
         --this.tickingFluidCount;
      }

      if (!p_62995_.isAir()) {
         ++this.nonEmptyBlockCount;
         if (p_62995_.isRandomlyTicking()) {
            ++this.tickingBlockCount;
         }
      }

      if (!fluidstate1.isEmpty()) {
         ++this.tickingFluidCount;
      }

      return blockstate;
   }

   public boolean hasOnlyAir() {
      return this.nonEmptyBlockCount == 0;
   }

   public boolean isRandomlyTicking() {
      return this.isRandomlyTickingBlocks() || this.isRandomlyTickingFluids();
   }

   public boolean isRandomlyTickingBlocks() {
      return this.tickingBlockCount > 0;
   }

   public boolean isRandomlyTickingFluids() {
      return this.tickingFluidCount > 0;
   }

   public void recalcBlockCounts() {
      class BlockCounter implements PalettedContainer.CountConsumer<BlockState> {
         public int nonEmptyBlockCount;
         public int tickingBlockCount;
         public int tickingFluidCount;

         public void accept(BlockState p_204444_, int p_204445_) {
            FluidState fluidstate = p_204444_.getFluidState();
            if (!p_204444_.isAir()) {
               this.nonEmptyBlockCount += p_204445_;
               if (p_204444_.isRandomlyTicking()) {
                  this.tickingBlockCount += p_204445_;
               }
            }

            if (!fluidstate.isEmpty()) {
               this.nonEmptyBlockCount += p_204445_;
               if (fluidstate.isRandomlyTicking()) {
                  this.tickingFluidCount += p_204445_;
               }
            }

         }
      }

      BlockCounter levelchunksection$1blockcounter = new BlockCounter();
      this.states.count(levelchunksection$1blockcounter);
      this.nonEmptyBlockCount = (short)levelchunksection$1blockcounter.nonEmptyBlockCount;
      this.tickingBlockCount = (short)levelchunksection$1blockcounter.tickingBlockCount;
      this.tickingFluidCount = (short)levelchunksection$1blockcounter.tickingFluidCount;
   }

   public PalettedContainer<BlockState> getStates() {
      return this.states;
   }

   public PalettedContainerRO<Holder<Biome>> getBiomes() {
      return this.biomes;
   }

   public void read(FriendlyByteBuf p_63005_) {
      this.nonEmptyBlockCount = p_63005_.readShort();
      this.states.read(p_63005_);
      PalettedContainer<Holder<Biome>> palettedcontainer = this.biomes.recreate();
      palettedcontainer.read(p_63005_);
      this.biomes = palettedcontainer;
   }

   public void readBiomes(FriendlyByteBuf p_275669_) {
      PalettedContainer<Holder<Biome>> palettedcontainer = this.biomes.recreate();
      palettedcontainer.read(p_275669_);
      this.biomes = palettedcontainer;
   }

   public void write(FriendlyByteBuf p_63012_) {
      p_63012_.writeShort(this.nonEmptyBlockCount);
      this.states.write(p_63012_);
      this.biomes.write(p_63012_);
   }

   public int getSerializedSize() {
      return 2 + this.states.getSerializedSize() + this.biomes.getSerializedSize();
   }

   public boolean maybeHas(Predicate<BlockState> p_63003_) {
      return this.states.maybeHas(p_63003_);
   }

   public Holder<Biome> getNoiseBiome(int p_204434_, int p_204435_, int p_204436_) {
      return this.biomes.get(p_204434_, p_204435_, p_204436_);
   }

   public void fillBiomesFromNoise(BiomeResolver p_282075_, Climate.Sampler p_283084_, int p_282310_, int p_281510_, int p_283057_) {
      PalettedContainer<Holder<Biome>> palettedcontainer = this.biomes.recreate();
      int i = 4;

      for(int j = 0; j < 4; ++j) {
         for(int k = 0; k < 4; ++k) {
            for(int l = 0; l < 4; ++l) {
               palettedcontainer.getAndSetUnchecked(j, k, l, p_282075_.getNoiseBiome(p_282310_ + j, p_281510_ + k, p_283057_ + l, p_283084_));
            }
         }
      }

      this.biomes = palettedcontainer;
   }
}