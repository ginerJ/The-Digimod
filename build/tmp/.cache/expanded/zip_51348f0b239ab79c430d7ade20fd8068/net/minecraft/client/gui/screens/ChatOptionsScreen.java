package net.minecraft.client.gui.screens;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChatOptionsScreen extends SimpleOptionsSubScreen {
   public ChatOptionsScreen(Screen p_95571_, Options p_95572_) {
      super(p_95571_, p_95572_, Component.translatable("options.chat.title"), new OptionInstance[]{p_95572_.chatVisibility(), p_95572_.chatColors(), p_95572_.chatLinks(), p_95572_.chatLinksPrompt(), p_95572_.chatOpacity(), p_95572_.textBackgroundOpacity(), p_95572_.chatScale(), p_95572_.chatLineSpacing(), p_95572_.chatDelay(), p_95572_.chatWidth(), p_95572_.chatHeightFocused(), p_95572_.chatHeightUnfocused(), p_95572_.narrator(), p_95572_.autoSuggestions(), p_95572_.hideMatchedNames(), p_95572_.reducedDebugInfo(), p_95572_.onlyShowSecureChat()});
   }
}