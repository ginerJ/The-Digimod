package net.minecraft.client.multiplayer.chat.report;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum BanReason {
   GENERIC_VIOLATION("generic_violation"),
   FALSE_REPORTING("false_reporting"),
   HATE_SPEECH("hate_speech"),
   HATE_TERRORISM_NOTORIOUS_FIGURE("hate_terrorism_notorious_figure"),
   HARASSMENT_OR_BULLYING("harassment_or_bullying"),
   DEFAMATION_IMPERSONATION_FALSE_INFORMATION("defamation_impersonation_false_information"),
   DRUGS("drugs"),
   FRAUD("fraud"),
   SPAM_OR_ADVERTISING("spam_or_advertising"),
   NUDITY_OR_PORNOGRAPHY("nudity_or_pornography"),
   SEXUALLY_INAPPROPRIATE("sexually_inappropriate"),
   EXTREME_VIOLENCE_OR_GORE("extreme_violence_or_gore"),
   IMMINENT_HARM_TO_PERSON_OR_PROPERTY("imminent_harm_to_person_or_property");

   private final Component title;

   private BanReason(String p_273623_) {
      this.title = Component.translatable("gui.banned.reason." + p_273623_);
   }

   public Component title() {
      return this.title;
   }

   @Nullable
   public static BanReason byId(int p_272793_) {
      BanReason banreason;
      switch (p_272793_) {
         case 2:
            banreason = FALSE_REPORTING;
            break;
         case 3:
         case 4:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 18:
         case 20:
         case 22:
         case 24:
         case 26:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         default:
            banreason = null;
            break;
         case 5:
            banreason = HATE_SPEECH;
            break;
         case 16:
         case 25:
            banreason = HATE_TERRORISM_NOTORIOUS_FIGURE;
            break;
         case 17:
         case 19:
         case 23:
         case 31:
            banreason = GENERIC_VIOLATION;
            break;
         case 21:
            banreason = HARASSMENT_OR_BULLYING;
            break;
         case 27:
            banreason = DEFAMATION_IMPERSONATION_FALSE_INFORMATION;
            break;
         case 28:
            banreason = DRUGS;
            break;
         case 29:
            banreason = FRAUD;
            break;
         case 30:
            banreason = SPAM_OR_ADVERTISING;
            break;
         case 32:
            banreason = NUDITY_OR_PORNOGRAPHY;
            break;
         case 33:
            banreason = SEXUALLY_INAPPROPRIATE;
            break;
         case 34:
            banreason = EXTREME_VIOLENCE_OR_GORE;
            break;
         case 53:
            banreason = IMMINENT_HARM_TO_PERSON_OR_PROPERTY;
      }

      return banreason;
   }
}