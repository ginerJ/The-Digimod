package com.mojang.realmsclient.gui;

import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.util.RealmsPersistence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsNewsManager {
   private final RealmsPersistence newsLocalStorage;
   private boolean hasUnreadNews;
   private String newsLink;

   public RealmsNewsManager(RealmsPersistence p_239304_) {
      this.newsLocalStorage = p_239304_;
      RealmsPersistence.RealmsPersistenceData realmspersistence$realmspersistencedata = p_239304_.read();
      this.hasUnreadNews = realmspersistence$realmspersistencedata.hasUnreadNews;
      this.newsLink = realmspersistence$realmspersistencedata.newsLink;
   }

   public boolean hasUnreadNews() {
      return this.hasUnreadNews;
   }

   public String newsLink() {
      return this.newsLink;
   }

   public void updateUnreadNews(RealmsNews p_239191_) {
      RealmsPersistence.RealmsPersistenceData realmspersistence$realmspersistencedata = this.updateNewsStorage(p_239191_);
      this.hasUnreadNews = realmspersistence$realmspersistencedata.hasUnreadNews;
      this.newsLink = realmspersistence$realmspersistencedata.newsLink;
   }

   private RealmsPersistence.RealmsPersistenceData updateNewsStorage(RealmsNews p_240153_) {
      RealmsPersistence.RealmsPersistenceData realmspersistence$realmspersistencedata = new RealmsPersistence.RealmsPersistenceData();
      realmspersistence$realmspersistencedata.newsLink = p_240153_.newsLink;
      RealmsPersistence.RealmsPersistenceData realmspersistence$realmspersistencedata1 = this.newsLocalStorage.read();
      boolean flag = realmspersistence$realmspersistencedata.newsLink == null || realmspersistence$realmspersistencedata.newsLink.equals(realmspersistence$realmspersistencedata1.newsLink);
      if (flag) {
         return realmspersistence$realmspersistencedata1;
      } else {
         realmspersistence$realmspersistencedata.hasUnreadNews = true;
         this.newsLocalStorage.save(realmspersistence$realmspersistencedata);
         return realmspersistence$realmspersistencedata;
      }
   }
}