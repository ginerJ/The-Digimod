package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class Lighting {
   private static final Vector3f DIFFUSE_LIGHT_0 = (new Vector3f(0.2F, 1.0F, -0.7F)).normalize();
   private static final Vector3f DIFFUSE_LIGHT_1 = (new Vector3f(-0.2F, 1.0F, 0.7F)).normalize();
   private static final Vector3f NETHER_DIFFUSE_LIGHT_0 = (new Vector3f(0.2F, 1.0F, -0.7F)).normalize();
   private static final Vector3f NETHER_DIFFUSE_LIGHT_1 = (new Vector3f(-0.2F, -1.0F, 0.7F)).normalize();
   private static final Vector3f INVENTORY_DIFFUSE_LIGHT_0 = (new Vector3f(0.2F, -1.0F, -1.0F)).normalize();
   private static final Vector3f INVENTORY_DIFFUSE_LIGHT_1 = (new Vector3f(-0.2F, -1.0F, 0.0F)).normalize();

   public static void setupNetherLevel(Matrix4f p_254421_) {
      RenderSystem.setupLevelDiffuseLighting(NETHER_DIFFUSE_LIGHT_0, NETHER_DIFFUSE_LIGHT_1, p_254421_);
   }

   public static void setupLevel(Matrix4f p_254246_) {
      RenderSystem.setupLevelDiffuseLighting(DIFFUSE_LIGHT_0, DIFFUSE_LIGHT_1, p_254246_);
   }

   public static void setupForFlatItems() {
      RenderSystem.setupGuiFlatDiffuseLighting(DIFFUSE_LIGHT_0, DIFFUSE_LIGHT_1);
   }

   public static void setupFor3DItems() {
      RenderSystem.setupGui3DDiffuseLighting(DIFFUSE_LIGHT_0, DIFFUSE_LIGHT_1);
   }

   public static void setupForEntityInInventory() {
      RenderSystem.setShaderLights(INVENTORY_DIFFUSE_LIGHT_0, INVENTORY_DIFFUSE_LIGHT_1);
   }
}