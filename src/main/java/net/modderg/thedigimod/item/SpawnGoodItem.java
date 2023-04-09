package net.modderg.thedigimod.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class SpawnGoodItem extends Item {
    private static final Map<EntityType<? extends Animal>, SpawnEggItem> BY_ID = Maps.newIdentityHashMap();
    private final int backgroundColor;
    private final int highlightColor;
    private final RegistryObject<? extends EntityType<?>> defaultType;

    @Deprecated
    public SpawnGoodItem(RegistryObject<? extends EntityType<?>> p_43207_, int p_43208_, int p_43209_, Item.Properties p_43210_) {
        super(p_43210_);
        this.defaultType = p_43207_;
        this.backgroundColor = p_43208_;
        this.highlightColor = p_43209_;

    }

    public InteractionResult useOn(UseOnContext p_43223_) {
        Level level = p_43223_.getLevel();
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack itemstack = p_43223_.getItemInHand();
            BlockPos blockpos = p_43223_.getClickedPos();
            Direction direction = p_43223_.getClickedFace();
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(Blocks.SPAWNER)) {
                BlockEntity blockentity = level.getBlockEntity(blockpos);
                if (blockentity instanceof SpawnerBlockEntity) {
                    SpawnerBlockEntity spawnerblockentity = (SpawnerBlockEntity)blockentity;
                    EntityType<?> entitytype1 = this.getType(itemstack.getTag());
                    spawnerblockentity.m_252803_(entitytype1, level.getRandom());
                    blockentity.setChanged();
                    level.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
                    level.gameEvent(p_43223_.getPlayer(), GameEvent.BLOCK_CHANGE, blockpos);
                    itemstack.shrink(1);
                    return InteractionResult.CONSUME;
                }
            }

            BlockPos blockpos1;
            if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
                blockpos1 = blockpos;
            } else {
                blockpos1 = blockpos.relative(direction);
            }

            EntityType<?> entitytype = this.getType(itemstack.getTag());
            if (entitytype.spawn((ServerLevel) level, itemstack, p_43223_.getPlayer(), blockpos1, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP) != null) {
                itemstack.shrink(1);
                level.gameEvent(p_43223_.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
            }

            return InteractionResult.CONSUME;
        }
    }

    protected static BlockHitResult getPlayerPOVHitResult(Level p_41436_, Player p_41437_, ClipContext.Fluid p_41438_) {
        float f = p_41437_.getXRot();
        float f1 = p_41437_.getYRot();
        Vec3 vec3 = p_41437_.getEyePosition();
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = p_41437_.getReachDistance();
        Vec3 vec31 = vec3.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        return p_41436_.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, p_41438_, p_41437_));
    }

    public InteractionResultHolder<ItemStack> use(Level p_43225_, Player p_43226_, InteractionHand p_43227_) {
        ItemStack itemstack = p_43226_.getItemInHand(p_43227_);
        HitResult hitresult = getPlayerPOVHitResult(p_43225_, p_43226_, ClipContext.Fluid.SOURCE_ONLY);
        if (hitresult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemstack);
        } else if (!(p_43225_ instanceof ServerLevel)) {
            return InteractionResultHolder.success(itemstack);
        } else {
            BlockHitResult blockhitresult = (BlockHitResult) hitresult;
            BlockPos blockpos = blockhitresult.getBlockPos();
            if (!(p_43225_.getBlockState(blockpos).getBlock() instanceof LiquidBlock)) {
                return InteractionResultHolder.pass(itemstack);
            } else if (p_43225_.mayInteract(p_43226_, blockpos) && p_43226_.mayUseItemAt(blockpos, blockhitresult.getDirection(), itemstack)) {
                EntityType<?> entitytype = this.getType(itemstack.getTag());
                Entity entity = entitytype.spawn((ServerLevel) p_43225_, itemstack, p_43226_, blockpos, MobSpawnType.SPAWN_EGG, false, false);
                if (entity == null) {
                    return InteractionResultHolder.pass(itemstack);
                } else {
                    if (!p_43226_.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    p_43226_.awardStat(Stats.ITEM_USED.get(this));
                    p_43225_.gameEvent(p_43226_, GameEvent.ENTITY_PLACE, entity.position());
                    return InteractionResultHolder.consume(itemstack);
                }
            } else {
                return InteractionResultHolder.fail(itemstack);
            }
        }
    }

    @Deprecated
    @Nullable
    public static SpawnEggItem byId(@Nullable EntityType<?> p_43214_) {
        return BY_ID.get(p_43214_);
    }

    public EntityType<?> getType(@Nullable CompoundTag p_43229_) {
        if (p_43229_ != null && p_43229_.contains("EntityTag", 10)) {
            CompoundTag compoundtag = p_43229_.getCompound("EntityTag");
            if (compoundtag.contains("id", 8)) {
                return EntityType.byString(compoundtag.getString("id")).orElse(this.defaultType.get());
            }
        }

        return this.defaultType.get();
    }
}

