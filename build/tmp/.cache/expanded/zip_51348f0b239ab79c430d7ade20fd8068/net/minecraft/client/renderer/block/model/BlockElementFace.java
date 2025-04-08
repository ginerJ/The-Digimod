package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockElementFace {
   public static final int NO_TINT = -1;
   public final Direction cullForDirection;
   public final int tintIndex;
   public final String texture;
   public final BlockFaceUV uv;
   @Nullable
   private final net.minecraftforge.client.model.ForgeFaceData faceData; // If null, we defer to the parent BlockElement's ForgeFaceData, which is not nullable.
   @Nullable
   BlockElement parent; // Parent canot be set by the constructor due to instantiation ordering. This shouldn't really ever be null, but it could theoretically be.

   public BlockElementFace(@Nullable Direction p_111359_, int p_111360_, String p_111361_, BlockFaceUV p_111362_) {
      this(p_111359_, p_111360_, p_111361_, p_111362_, null);
   }

   public BlockElementFace(@Nullable Direction p_111359_, int p_111360_, String p_111361_, BlockFaceUV p_111362_, @Nullable net.minecraftforge.client.model.ForgeFaceData faceData) {
      this.cullForDirection = p_111359_;
      this.tintIndex = p_111360_;
      this.texture = p_111361_;
      this.uv = p_111362_;
      this.faceData = faceData;
   }

   public net.minecraftforge.client.model.ForgeFaceData getFaceData() {
      if(this.faceData != null) {
         return this.faceData;
      } else if(this.parent != null) {
         return this.parent.getFaceData();
      }
      return net.minecraftforge.client.model.ForgeFaceData.DEFAULT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<BlockElementFace> {
      private static final int DEFAULT_TINT_INDEX = -1;

      public BlockElementFace deserialize(JsonElement p_111365_, Type p_111366_, JsonDeserializationContext p_111367_) throws JsonParseException {
         JsonObject jsonobject = p_111365_.getAsJsonObject();
         Direction direction = this.getCullFacing(jsonobject);
         int i = this.getTintIndex(jsonobject);
         String s = this.getTexture(jsonobject);
         BlockFaceUV blockfaceuv = p_111367_.deserialize(jsonobject, BlockFaceUV.class);
         var faceData = net.minecraftforge.client.model.ForgeFaceData.read(jsonobject.get("forge_data"), null);
         return new BlockElementFace(direction, i, s, blockfaceuv, faceData);
      }

      protected int getTintIndex(JsonObject p_111369_) {
         return GsonHelper.getAsInt(p_111369_, "tintindex", -1);
      }

      private String getTexture(JsonObject p_111371_) {
         return GsonHelper.getAsString(p_111371_, "texture");
      }

      @Nullable
      private Direction getCullFacing(JsonObject p_111373_) {
         String s = GsonHelper.getAsString(p_111373_, "cullface", "");
         return Direction.byName(s);
      }
   }
}
