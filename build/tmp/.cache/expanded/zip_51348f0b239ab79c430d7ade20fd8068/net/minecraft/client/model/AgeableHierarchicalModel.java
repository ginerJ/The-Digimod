package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AgeableHierarchicalModel<E extends Entity> extends HierarchicalModel<E> {
   private final float youngScaleFactor;
   private final float bodyYOffset;

   public AgeableHierarchicalModel(float p_273694_, float p_273578_) {
      this(p_273694_, p_273578_, RenderType::entityCutoutNoCull);
   }

   public AgeableHierarchicalModel(float p_273130_, float p_273302_, Function<ResourceLocation, RenderType> p_273636_) {
      super(p_273636_);
      this.bodyYOffset = p_273302_;
      this.youngScaleFactor = p_273130_;
   }

   public void renderToBuffer(PoseStack p_273029_, VertexConsumer p_272763_, int p_273665_, int p_272602_, float p_273190_, float p_273731_, float p_272609_, float p_273331_) {
      if (this.young) {
         p_273029_.pushPose();
         p_273029_.scale(this.youngScaleFactor, this.youngScaleFactor, this.youngScaleFactor);
         p_273029_.translate(0.0F, this.bodyYOffset / 16.0F, 0.0F);
         this.root().render(p_273029_, p_272763_, p_273665_, p_272602_, p_273190_, p_273731_, p_272609_, p_273331_);
         p_273029_.popPose();
      } else {
         this.root().render(p_273029_, p_272763_, p_273665_, p_272602_, p_273190_, p_273731_, p_272609_, p_273331_);
      }

   }
}