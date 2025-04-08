package net.minecraft.client.resources;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.VanillaPackResourcesBuilder;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPackSource extends BuiltInPackSource {
   private static final PackMetadataSection VERSION_METADATA_SECTION = new PackMetadataSection(Component.translatable("resourcePack.vanilla.description"), SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES));
   private static final BuiltInMetadata BUILT_IN_METADATA = BuiltInMetadata.of(PackMetadataSection.TYPE, VERSION_METADATA_SECTION);
   private static final Component VANILLA_NAME = Component.translatable("resourcePack.vanilla.name");
   public static final String HIGH_CONTRAST_PACK = "high_contrast";
   private static final Map<String, Component> SPECIAL_PACK_NAMES = Map.of("programmer_art", Component.translatable("resourcePack.programmer_art.name"), "high_contrast", Component.translatable("resourcePack.high_contrast.name"));
   private static final ResourceLocation PACKS_DIR = new ResourceLocation("minecraft", "resourcepacks");
   @Nullable
   private final Path externalAssetDir;

   public ClientPackSource(Path p_249324_) {
      super(PackType.CLIENT_RESOURCES, createVanillaPackSource(p_249324_), PACKS_DIR);
      this.externalAssetDir = this.findExplodedAssetPacks(p_249324_);
   }

   @Nullable
   private Path findExplodedAssetPacks(Path p_251339_) {
      if (SharedConstants.IS_RUNNING_IN_IDE && p_251339_.getFileSystem() == FileSystems.getDefault()) {
         Path path = p_251339_.getParent().resolve("resourcepacks");
         if (Files.isDirectory(path)) {
            return path;
         }
      }

      return null;
   }

   public static VanillaPackResources createVanillaPackSource(Path p_250749_) {
      VanillaPackResourcesBuilder vanillapackresourcesbuilder = (new VanillaPackResourcesBuilder()).setMetadata(BUILT_IN_METADATA).exposeNamespace("minecraft", "realms");
      return vanillapackresourcesbuilder.applyDevelopmentConfig().pushJarResources().pushAssetPath(PackType.CLIENT_RESOURCES, p_250749_).build();
   }

   protected Component getPackTitle(String p_250421_) {
      Component component = SPECIAL_PACK_NAMES.get(p_250421_);
      return (Component)(component != null ? component : Component.literal(p_250421_));
   }

   @Nullable
   protected Pack createVanillaPack(PackResources p_250048_) {
      return Pack.readMetaAndCreate("vanilla", VANILLA_NAME, true, (p_247953_) -> {
         return p_250048_;
      }, PackType.CLIENT_RESOURCES, Pack.Position.BOTTOM, PackSource.BUILT_IN);
   }

   @Nullable
   protected Pack createBuiltinPack(String p_250992_, Pack.ResourcesSupplier p_250814_, Component p_249835_) {
      return Pack.readMetaAndCreate(p_250992_, p_249835_, false, p_250814_, PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN);
   }

   protected void populatePackList(BiConsumer<String, Function<String, Pack>> p_249851_) {
      super.populatePackList(p_249851_);
      if (this.externalAssetDir != null) {
         this.discoverPacksInPath(this.externalAssetDir, p_249851_);
      }

   }
}