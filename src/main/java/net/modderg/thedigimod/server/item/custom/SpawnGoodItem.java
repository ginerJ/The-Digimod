package net.modderg.thedigimod.server.item.custom;

import com.google.common.collect.Maps;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
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
import net.modderg.thedigimod.server.advancements.TDAdvancements;
import net.modderg.thedigimod.server.goods.AbstractTrainingGood;
import net.modderg.thedigimod.server.sound.DigiSounds;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class SpawnGoodItem extends DigimonItem {
    private static final Map<EntityType<? extends Animal>, SpawnEggItem> BY_ID = Maps.newIdentityHashMap();
    private final RegistryObject<? extends EntityType<?>> defaultType;

    private String stat;

    private static final int DEFAULT_INTEGER_VALUE = 500;

    public SpawnGoodItem(RegistryObject<? extends EntityType<?>> p_43207_, Item.Properties p_43210_, String stat) {
        super(p_43210_);
        this.stat = stat;
        this.defaultType = p_43207_;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level p_41405_, Entity p_41406_, int p_41407_, boolean p_41408_) {
        super.inventoryTick(stack, p_41405_, p_41406_, p_41407_, p_41408_);
        CompoundTag compoundNBT = stack.getOrCreateTag();

        if (!compoundNBT.contains("USES"))
            compoundNBT.putInt("USES", DEFAULT_INTEGER_VALUE);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @org.jetbrains.annotations.Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        p_41423_.add(Component.literal(""));
        p_41423_.add(Component.literal("Trains " + stat).withStyle(ChatFormatting.BLUE));
        p_41423_.add(Component.translatable("Uses left: " + (p_41421_.getOrCreateTag().getInt("USES"))).withStyle(ChatFormatting.GREEN));
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        Level level = context.getLevel();
        if (!(level instanceof ServerLevel))
            return InteractionResult.SUCCESS;
        else {
            ItemStack itemstack = context.getItemInHand();
            BlockPos blockpos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(Blocks.SPAWNER)) {
                BlockEntity blockentity = level.getBlockEntity(blockpos);
                if (blockentity instanceof SpawnerBlockEntity) {
                    SpawnerBlockEntity spawnerblockentity = (SpawnerBlockEntity)blockentity;
                    EntityType<?> entitytype1 = this.getType(itemstack.getTag());
                    spawnerblockentity.setEntityId(entitytype1, level.getRandom());
                    blockentity.setChanged();
                    level.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
                    level.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, blockpos);
                    itemstack.shrink(1);
                    return InteractionResult.CONSUME;
                }
            }

            BlockPos blockpos1;
            if (blockstate.getCollisionShape(level, blockpos).isEmpty())
                blockpos1 = blockpos;
            else
                blockpos1 = blockpos.relative(direction);


            EntityType<?> entitytype = this.getType(itemstack.getTag());

            Entity entity = entitytype.spawn((ServerLevel) level, itemstack, context.getPlayer(), blockpos1, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockpos, blockpos1) && direction == Direction.UP);
            if (entity instanceof AbstractTrainingGood good) {
                good.setHealth(context.getItemInHand().getOrCreateTag().getInt("USES"));
                itemstack.shrink(1);
                level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);

                if(context.getPlayer() instanceof ServerPlayer sPlayer){
                    TDAdvancements.grantAdvancement(sPlayer, TDAdvancements.TRAINING);
                    if(good.getStatMultiplier() > 1)
                        TDAdvancements.grantAdvancement(sPlayer, TDAdvancements.HIGH_TRAINING);
                }
            }
            context.getPlayer().playNotifySound(DigiSounds.PLACE_GOOD_SOUND.get(), SoundSource.PLAYERS, 1F, 1.0F);

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
        double d0 = p_41437_.getBlockReach();
        Vec3 vec31 = vec3.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        return p_41436_.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, p_41438_, p_41437_));
    }

    public InteractionResultHolder<ItemStack> use(@NotNull Level p_43225_, Player p_43226_, @NotNull InteractionHand p_43227_) {
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
            if (compoundtag.contains("id", 8))
                return EntityType.byString(compoundtag.getString("id")).orElse(this.defaultType.get());
        }

        return this.defaultType.get();
    }
}

