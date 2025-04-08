package net.minecraft.server;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ServicesKeySet;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.SignatureValidator;

public record Services(MinecraftSessionService sessionService, ServicesKeySet servicesKeySet, GameProfileRepository profileRepository, GameProfileCache profileCache) {
   private static final String USERID_CACHE_FILE = "usercache.json";

   public static Services create(YggdrasilAuthenticationService p_214345_, File p_214346_) {
      MinecraftSessionService minecraftsessionservice = p_214345_.createMinecraftSessionService();
      GameProfileRepository gameprofilerepository = p_214345_.createProfileRepository();
      GameProfileCache gameprofilecache = new GameProfileCache(gameprofilerepository, new File(p_214346_, "usercache.json"));
      return new Services(minecraftsessionservice, p_214345_.getServicesKeySet(), gameprofilerepository, gameprofilecache);
   }

   @Nullable
   public SignatureValidator profileKeySignatureValidator() {
      return SignatureValidator.from(this.servicesKeySet, ServicesKeyType.PROFILE_KEY);
   }
}