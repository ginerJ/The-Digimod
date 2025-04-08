package net.minecraft.client.renderer.debug;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VillageSectionsDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final int MAX_RENDER_DIST_FOR_VILLAGE_SECTIONS = 60;
   private final Set<SectionPos> villageSections = Sets.newHashSet();

   VillageSectionsDebugRenderer() {
   }

   public void clear() {
      this.villageSections.clear();
   }

   public void setVillageSection(SectionPos p_113710_) {
      this.villageSections.add(p_113710_);
   }

   public void setNotVillageSection(SectionPos p_113712_) {
      this.villageSections.remove(p_113712_);
   }

   public void render(PoseStack p_113701_, MultiBufferSource p_113702_, double p_113703_, double p_113704_, double p_113705_) {
      BlockPos blockpos = BlockPos.containing(p_113703_, p_113704_, p_113705_);
      this.villageSections.forEach((p_269747_) -> {
         if (blockpos.closerThan(p_269747_.center(), 60.0D)) {
            highlightVillageSection(p_113701_, p_113702_, p_269747_);
         }

      });
   }

   private static void highlightVillageSection(PoseStack p_270832_, MultiBufferSource p_270443_, SectionPos p_271021_) {
      int i = 1;
      BlockPos blockpos = p_271021_.center();
      BlockPos blockpos1 = blockpos.offset(-1, -1, -1);
      BlockPos blockpos2 = blockpos.offset(1, 1, 1);
      DebugRenderer.renderFilledBox(p_270832_, p_270443_, blockpos1, blockpos2, 0.2F, 1.0F, 0.2F, 0.15F);
   }
}