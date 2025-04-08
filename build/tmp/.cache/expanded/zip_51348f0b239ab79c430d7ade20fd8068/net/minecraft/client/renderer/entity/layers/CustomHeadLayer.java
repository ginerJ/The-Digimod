package net.minecraft.client.renderer.entity.layers;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Map;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomHeadLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {
   private final float scaleX;
   private final float scaleY;
   private final float scaleZ;
   private final Map<SkullBlock.Type, SkullModelBase> skullModels;
   private final ItemInHandRenderer itemInHandRenderer;

   public CustomHeadLayer(RenderLayerParent<T, M> p_234829_, EntityModelSet p_234830_, ItemInHandRenderer p_234831_) {
      this(p_234829_, p_234830_, 1.0F, 1.0F, 1.0F, p_234831_);
   }

   public CustomHeadLayer(RenderLayerParent<T, M> p_234822_, EntityModelSet p_234823_, float p_234824_, float p_234825_, float p_234826_, ItemInHandRenderer p_234827_) {
      super(p_234822_);
      this.scaleX = p_234824_;
      this.scaleY = p_234825_;
      this.scaleZ = p_234826_;
      this.skullModels = SkullBlockRenderer.createSkullRenderers(p_234823_);
      this.itemInHandRenderer = p_234827_;
   }

   public void render(PoseStack p_116731_, MultiBufferSource p_116732_, int p_116733_, T p_116734_, float p_116735_, float p_116736_, float p_116737_, float p_116738_, float p_116739_, float p_116740_) {
      ItemStack itemstack = p_116734_.getItemBySlot(EquipmentSlot.HEAD);
      if (!itemstack.isEmpty()) {
         Item item = itemstack.getItem();
         p_116731_.pushPose();
         p_116731_.scale(this.scaleX, this.scaleY, this.scaleZ);
         boolean flag = p_116734_ instanceof Villager || p_116734_ instanceof ZombieVillager;
         if (p_116734_.isBaby() && !(p_116734_ instanceof Villager)) {
            float f = 2.0F;
            float f1 = 1.4F;
            p_116731_.translate(0.0F, 0.03125F, 0.0F);
            p_116731_.scale(0.7F, 0.7F, 0.7F);
            p_116731_.translate(0.0F, 1.0F, 0.0F);
         }

         this.getParentModel().getHead().translateAndRotate(p_116731_);
         if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
            float f2 = 1.1875F;
            p_116731_.scale(1.1875F, -1.1875F, -1.1875F);
            if (flag) {
               p_116731_.translate(0.0F, 0.0625F, 0.0F);
            }

            GameProfile gameprofile = null;
            if (itemstack.hasTag()) {
               CompoundTag compoundtag = itemstack.getTag();
               if (compoundtag.contains("SkullOwner", 10)) {
                  gameprofile = NbtUtils.readGameProfile(compoundtag.getCompound("SkullOwner"));
               }
            }

            p_116731_.translate(-0.5D, 0.0D, -0.5D);
            SkullBlock.Type skullblock$type = ((AbstractSkullBlock)((BlockItem)item).getBlock()).getType();
            SkullModelBase skullmodelbase = this.skullModels.get(skullblock$type);
            RenderType rendertype = SkullBlockRenderer.getRenderType(skullblock$type, gameprofile);
            Entity entity = p_116734_.getVehicle();
            WalkAnimationState walkanimationstate;
            if (entity instanceof LivingEntity) {
               LivingEntity livingentity = (LivingEntity)entity;
               walkanimationstate = livingentity.walkAnimation;
            } else {
               walkanimationstate = p_116734_.walkAnimation;
            }

            float f3 = walkanimationstate.position(p_116737_);
            SkullBlockRenderer.renderSkull((Direction)null, 180.0F, f3, p_116731_, p_116732_, p_116733_, skullmodelbase, rendertype);
         } else {
            label60: {
               if (item instanceof ArmorItem) {
                  ArmorItem armoritem = (ArmorItem)item;
                  if (armoritem.getEquipmentSlot() == EquipmentSlot.HEAD) {
                     break label60;
                  }
               }

               translateToHead(p_116731_, flag);
               this.itemInHandRenderer.renderItem(p_116734_, itemstack, ItemDisplayContext.HEAD, false, p_116731_, p_116732_, p_116733_);
            }
         }

         p_116731_.popPose();
      }
   }

   public static void translateToHead(PoseStack p_174484_, boolean p_174485_) {
      float f = 0.625F;
      p_174484_.translate(0.0F, -0.25F, 0.0F);
      p_174484_.mulPose(Axis.YP.rotationDegrees(180.0F));
      p_174484_.scale(0.625F, -0.625F, -0.625F);
      if (p_174485_) {
         p_174484_.translate(0.0F, 0.1875F, 0.0F);
      }

   }
}