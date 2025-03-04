package net.modderg.thedigimod.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.modderg.thedigimod.server.item.TDItems;
import org.jetbrains.annotations.NotNull;

public class MeatCropBlock extends CropBlock {

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 6);

    public MeatCropBlock(Properties properties) {
        super(properties);
    }

    protected static float getGrowthSpeed(Block p_52273_, BlockGetter p_52274_, BlockPos p_52275_) {
        return CropBlock.getGrowthSpeed(p_52273_,p_52274_,p_52275_)*1.5f;
    }

    @Override
    public void randomTick(BlockState p_221050_, ServerLevel p_221051_, @NotNull BlockPos p_221052_, @NotNull RandomSource p_221053_) {
        if (!p_221051_.isAreaLoaded(p_221052_, 1)) return;
        if (p_221051_.getRawBrightness(p_221052_, 0) >= 9) {
            int i = this.getAge(p_221050_);
            if (i < this.getMaxAge()) {
                float f = getGrowthSpeed(this, p_221051_, p_221052_);
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_221051_, p_221052_, p_221050_, p_221053_.nextInt((int)(25.0F / f) + 1) == 0)) {
                    p_221051_.setBlock(p_221052_, this.getStateForAge(i + 1), 2);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_221051_, p_221052_, p_221050_);
                }
            }
        }

    }

    @Override
    protected ItemLike getBaseSeedId() {
        return TDItems.DIGI_MEAT.get();
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return 6;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}
