package net.minecraft.client.multiplayer;

import com.mojang.authlib.minecraft.UserApiService;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.User;
import net.minecraft.world.entity.player.ProfileKeyPair;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ProfileKeyPairManager {
   ProfileKeyPairManager EMPTY_KEY_MANAGER = new ProfileKeyPairManager() {
      public CompletableFuture<Optional<ProfileKeyPair>> prepareKeyPair() {
         return CompletableFuture.completedFuture(Optional.empty());
      }

      public boolean shouldRefreshKeyPair() {
         return false;
      }
   };

   static ProfileKeyPairManager create(UserApiService p_253925_, User p_254501_, Path p_254206_) {
      return (ProfileKeyPairManager)(p_254501_.getType() == User.Type.MSA ? new AccountProfileKeyPairManager(p_253925_, p_254501_.getGameProfile().getId(), p_254206_) : EMPTY_KEY_MANAGER);
   }

   CompletableFuture<Optional<ProfileKeyPair>> prepareKeyPair();

   boolean shouldRefreshKeyPair();
}