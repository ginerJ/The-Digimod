package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;

public class TreeConfiguration implements FeatureConfiguration {
   public static final Codec<TreeConfiguration> CODEC = RecordCodecBuilder.create((p_225468_) -> {
      return p_225468_.group(BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter((p_161248_) -> {
         return p_161248_.trunkProvider;
      }), TrunkPlacer.CODEC.fieldOf("trunk_placer").forGetter((p_161246_) -> {
         return p_161246_.trunkPlacer;
      }), BlockStateProvider.CODEC.fieldOf("foliage_provider").forGetter((p_161244_) -> {
         return p_161244_.foliageProvider;
      }), FoliagePlacer.CODEC.fieldOf("foliage_placer").forGetter((p_191357_) -> {
         return p_191357_.foliagePlacer;
      }), RootPlacer.CODEC.optionalFieldOf("root_placer").forGetter((p_225478_) -> {
         return p_225478_.rootPlacer;
      }), BlockStateProvider.CODEC.fieldOf("dirt_provider").forGetter((p_225476_) -> {
         return p_225476_.dirtProvider;
      }), FeatureSize.CODEC.fieldOf("minimum_size").forGetter((p_225474_) -> {
         return p_225474_.minimumSize;
      }), TreeDecorator.CODEC.listOf().fieldOf("decorators").forGetter((p_225472_) -> {
         return p_225472_.decorators;
      }), Codec.BOOL.fieldOf("ignore_vines").orElse(false).forGetter((p_161232_) -> {
         return p_161232_.ignoreVines;
      }), Codec.BOOL.fieldOf("force_dirt").orElse(false).forGetter((p_225470_) -> {
         return p_225470_.forceDirt;
      })).apply(p_225468_, TreeConfiguration::new);
   });
   //TODO: Review this, see if we can hook in the sapling into the Codec
   public final BlockStateProvider trunkProvider;
   public final BlockStateProvider dirtProvider;
   public final TrunkPlacer trunkPlacer;
   public final BlockStateProvider foliageProvider;
   public final FoliagePlacer foliagePlacer;
   public final Optional<RootPlacer> rootPlacer;
   public final FeatureSize minimumSize;
   public final List<TreeDecorator> decorators;
   public final boolean ignoreVines;
   public final boolean forceDirt;

   protected TreeConfiguration(BlockStateProvider p_225457_, TrunkPlacer p_225458_, BlockStateProvider p_225459_, FoliagePlacer p_225460_, Optional<RootPlacer> p_225461_, BlockStateProvider p_225462_, FeatureSize p_225463_, List<TreeDecorator> p_225464_, boolean p_225465_, boolean p_225466_) {
      this.trunkProvider = p_225457_;
      this.trunkPlacer = p_225458_;
      this.foliageProvider = p_225459_;
      this.foliagePlacer = p_225460_;
      this.rootPlacer = p_225461_;
      this.dirtProvider = p_225462_;
      this.minimumSize = p_225463_;
      this.decorators = p_225464_;
      this.ignoreVines = p_225465_;
      this.forceDirt = p_225466_;
   }

   public static class TreeConfigurationBuilder {
      public final BlockStateProvider trunkProvider;
      private final TrunkPlacer trunkPlacer;
      public final BlockStateProvider foliageProvider;
      private final FoliagePlacer foliagePlacer;
      private final Optional<RootPlacer> rootPlacer;
      private BlockStateProvider dirtProvider;
      private final FeatureSize minimumSize;
      private List<TreeDecorator> decorators = ImmutableList.of();
      private boolean ignoreVines;
      private boolean forceDirt;

      public TreeConfigurationBuilder(BlockStateProvider p_225481_, TrunkPlacer p_225482_, BlockStateProvider p_225483_, FoliagePlacer p_225484_, Optional<RootPlacer> p_225485_, FeatureSize p_225486_) {
         this.trunkProvider = p_225481_;
         this.trunkPlacer = p_225482_;
         this.foliageProvider = p_225483_;
         this.dirtProvider = BlockStateProvider.simple(Blocks.DIRT);
         this.foliagePlacer = p_225484_;
         this.rootPlacer = p_225485_;
         this.minimumSize = p_225486_;
      }

      public TreeConfigurationBuilder(BlockStateProvider p_191359_, TrunkPlacer p_191360_, BlockStateProvider p_191361_, FoliagePlacer p_191362_, FeatureSize p_191363_) {
         this(p_191359_, p_191360_, p_191361_, p_191362_, Optional.empty(), p_191363_);
      }

      public TreeConfiguration.TreeConfigurationBuilder dirt(BlockStateProvider p_161261_) {
         this.dirtProvider = p_161261_;
         return this;
      }

      public TreeConfiguration.TreeConfigurationBuilder decorators(List<TreeDecorator> p_68250_) {
         this.decorators = p_68250_;
         return this;
      }

      public TreeConfiguration.TreeConfigurationBuilder ignoreVines() {
         this.ignoreVines = true;
         return this;
      }

      public TreeConfiguration.TreeConfigurationBuilder forceDirt() {
         this.forceDirt = true;
         return this;
      }

      public TreeConfiguration build() {
         return new TreeConfiguration(this.trunkProvider, this.trunkPlacer, this.foliageProvider, this.foliagePlacer, this.rootPlacer, this.dirtProvider, this.minimumSize, this.decorators, this.ignoreVines, this.forceDirt);
      }
   }
}
