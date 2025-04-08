package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import com.mojang.realmsclient.dto.RealmsServer;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record ReportEnvironment(String clientVersion, @Nullable ReportEnvironment.Server server) {
   public static ReportEnvironment local() {
      return create((ReportEnvironment.Server)null);
   }

   public static ReportEnvironment thirdParty(String p_238999_) {
      return create(new ReportEnvironment.Server.ThirdParty(p_238999_));
   }

   public static ReportEnvironment realm(RealmsServer p_239765_) {
      return create(new ReportEnvironment.Server.Realm(p_239765_));
   }

   public static ReportEnvironment create(@Nullable ReportEnvironment.Server p_239956_) {
      return new ReportEnvironment(getClientVersion(), p_239956_);
   }

   public AbuseReportRequest.ClientInfo clientInfo() {
      return new AbuseReportRequest.ClientInfo(this.clientVersion, Locale.getDefault().toLanguageTag());
   }

   @Nullable
   public AbuseReportRequest.ThirdPartyServerInfo thirdPartyServerInfo() {
      ReportEnvironment.Server reportenvironment$server = this.server;
      if (reportenvironment$server instanceof ReportEnvironment.Server.ThirdParty reportenvironment$server$thirdparty) {
         return new AbuseReportRequest.ThirdPartyServerInfo(reportenvironment$server$thirdparty.ip);
      } else {
         return null;
      }
   }

   @Nullable
   public AbuseReportRequest.RealmInfo realmInfo() {
      ReportEnvironment.Server reportenvironment$server = this.server;
      if (reportenvironment$server instanceof ReportEnvironment.Server.Realm reportenvironment$server$realm) {
         return new AbuseReportRequest.RealmInfo(String.valueOf(reportenvironment$server$realm.realmId()), reportenvironment$server$realm.slotId());
      } else {
         return null;
      }
   }

   private static String getClientVersion() {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("1.20.1");
      if (Minecraft.checkModStatus().shouldReportAsModified()) {
         stringbuilder.append(" (modded)");
      }

      return stringbuilder.toString();
   }

   @OnlyIn(Dist.CLIENT)
   public interface Server {
      @OnlyIn(Dist.CLIENT)
      public static record Realm(long realmId, int slotId) implements ReportEnvironment.Server {
         public Realm(RealmsServer p_239068_) {
            this(p_239068_.id, p_239068_.activeSlot);
         }
      }

      @OnlyIn(Dist.CLIENT)
      public static record ThirdParty(String ip) implements ReportEnvironment.Server {
      }
   }
}