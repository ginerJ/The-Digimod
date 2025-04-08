package net.minecraft.client.multiplayer;

import com.google.common.base.Strings;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.response.KeyPairResponse;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PublicKey;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.world.entity.player.ProfileKeyPair;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class AccountProfileKeyPairManager implements ProfileKeyPairManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Duration MINIMUM_PROFILE_KEY_REFRESH_INTERVAL = Duration.ofHours(1L);
   private static final Path PROFILE_KEY_PAIR_DIR = Path.of("profilekeys");
   private final UserApiService userApiService;
   private final Path profileKeyPairPath;
   private CompletableFuture<Optional<ProfileKeyPair>> keyPair;
   private Instant nextProfileKeyRefreshTime = Instant.EPOCH;

   public AccountProfileKeyPairManager(UserApiService p_253640_, UUID p_254415_, Path p_253813_) {
      this.userApiService = p_253640_;
      this.profileKeyPairPath = p_253813_.resolve(PROFILE_KEY_PAIR_DIR).resolve(p_254415_ + ".json");
      this.keyPair = CompletableFuture.supplyAsync(() -> {
         return this.readProfileKeyPair().filter((p_254127_) -> {
            return !p_254127_.publicKey().data().hasExpired();
         });
      }, Util.backgroundExecutor()).thenCompose(this::readOrFetchProfileKeyPair);
   }

   public CompletableFuture<Optional<ProfileKeyPair>> prepareKeyPair() {
      this.nextProfileKeyRefreshTime = Instant.now().plus(MINIMUM_PROFILE_KEY_REFRESH_INTERVAL);
      this.keyPair = this.keyPair.thenCompose(this::readOrFetchProfileKeyPair);
      return this.keyPair;
   }

   public boolean shouldRefreshKeyPair() {
      return this.keyPair.isDone() && Instant.now().isAfter(this.nextProfileKeyRefreshTime) ? this.keyPair.join().map(ProfileKeyPair::dueRefresh).orElse(true) : false;
   }

   private CompletableFuture<Optional<ProfileKeyPair>> readOrFetchProfileKeyPair(Optional<ProfileKeyPair> p_254074_) {
      return CompletableFuture.supplyAsync(() -> {
         if (p_254074_.isPresent() && !p_254074_.get().dueRefresh()) {
            if (!SharedConstants.IS_RUNNING_IN_IDE) {
               this.writeProfileKeyPair((ProfileKeyPair)null);
            }

            return p_254074_;
         } else {
            try {
               ProfileKeyPair profilekeypair = this.fetchProfileKeyPair(this.userApiService);
               this.writeProfileKeyPair(profilekeypair);
               return Optional.of(profilekeypair);
            } catch (CryptException | MinecraftClientException | IOException ioexception) {
               // Forge: The offline user api service always returns a null profile key pair, so let's hide this useless exception if in dev
               if (net.minecraftforge.fml.loading.FMLLoader.isProduction() || this.userApiService != UserApiService.OFFLINE)
               LOGGER.error("Failed to retrieve profile key pair", (Throwable)ioexception);
               this.writeProfileKeyPair((ProfileKeyPair)null);
               return p_254074_;
            }
         }
      }, Util.backgroundExecutor());
   }

   private Optional<ProfileKeyPair> readProfileKeyPair() {
      if (Files.notExists(this.profileKeyPairPath)) {
         return Optional.empty();
      } else {
         try (BufferedReader bufferedreader = Files.newBufferedReader(this.profileKeyPairPath)) {
            return ProfileKeyPair.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(bufferedreader)).result();
         } catch (Exception exception) {
            LOGGER.error("Failed to read profile key pair file {}", this.profileKeyPairPath, exception);
            return Optional.empty();
         }
      }
   }

   private void writeProfileKeyPair(@Nullable ProfileKeyPair p_254227_) {
      try {
         Files.deleteIfExists(this.profileKeyPairPath);
      } catch (IOException ioexception) {
         LOGGER.error("Failed to delete profile key pair file {}", this.profileKeyPairPath, ioexception);
      }

      if (p_254227_ != null) {
         if (SharedConstants.IS_RUNNING_IN_IDE) {
            ProfileKeyPair.CODEC.encodeStart(JsonOps.INSTANCE, p_254227_).result().ifPresent((p_254406_) -> {
               try {
                  Files.createDirectories(this.profileKeyPairPath.getParent());
                  Files.writeString(this.profileKeyPairPath, p_254406_.toString());
               } catch (Exception exception) {
                  LOGGER.error("Failed to write profile key pair file {}", this.profileKeyPairPath, exception);
               }

            });
         }
      }
   }

   private ProfileKeyPair fetchProfileKeyPair(UserApiService p_253844_) throws CryptException, IOException {
      KeyPairResponse keypairresponse = p_253844_.getKeyPair();
      if (keypairresponse != null) {
         ProfilePublicKey.Data profilepublickey$data = parsePublicKey(keypairresponse);
         return new ProfileKeyPair(Crypt.stringToPemRsaPrivateKey(keypairresponse.getPrivateKey()), new ProfilePublicKey(profilepublickey$data), Instant.parse(keypairresponse.getRefreshedAfter()));
      } else {
         throw new IOException("Could not retrieve profile key pair");
      }
   }

   private static ProfilePublicKey.Data parsePublicKey(KeyPairResponse p_253834_) throws CryptException {
      if (!Strings.isNullOrEmpty(p_253834_.getPublicKey()) && p_253834_.getPublicKeySignature() != null && p_253834_.getPublicKeySignature().array().length != 0) {
         try {
            Instant instant = Instant.parse(p_253834_.getExpiresAt());
            PublicKey publickey = Crypt.stringToRsaPublicKey(p_253834_.getPublicKey());
            ByteBuffer bytebuffer = p_253834_.getPublicKeySignature();
            return new ProfilePublicKey.Data(instant, publickey, bytebuffer.array());
         } catch (IllegalArgumentException | DateTimeException datetimeexception) {
            throw new CryptException(datetimeexception);
         }
      } else {
         throw new CryptException(new InsecurePublicKeyException.MissingException());
      }
   }
}
