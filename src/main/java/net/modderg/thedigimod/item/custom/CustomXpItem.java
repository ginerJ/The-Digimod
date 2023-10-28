package net.modderg.thedigimod.item.custom;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.modderg.thedigimod.entity.CustomDigimon;
import net.modderg.thedigimod.item.DigiItems;

public class CustomXpItem extends Item {
    public int xpId;
    public int charges;

    public int getXpId(){
        return xpId;
    }

    public CustomXpItem(Properties p_41383_, int id, int charges) {
        super(p_41383_);
        xpId = id;
        this.charges = charges;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack item, Player player, LivingEntity entity, InteractionHand hand) {
        if(entity instanceof CustomDigimon){
            CustomDigimon digimon = (CustomDigimon) entity;
            if(!digimon.isEvolving()){
                for(int i = 0; i < charges; i++){
                    digimon.useXpItem(xpId);
                }
                digimon.eatItem(item, 0);
                return InteractionResult.CONSUME;
            }
        }
        return super.interactLivingEntity(item, player, entity, hand);
    }

    public static String getXpItem(int id) {
        switch (id) {
            case 0: return "dragon_data";
            case 1: return "beast_data";
            case 2: return "plantinsect_data";
            case 3: return "aquan_data";
            case 4: return "wind_data";
            case 5: return "machine_data";
            case 6: return "earth_data";
            case 7: return "nightmare_data";
        }
        return "holy_data";
    }
}
