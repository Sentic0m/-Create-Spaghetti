package net.senti.createspaghetti.event;

import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.item.TooltipHelper;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.senti.createspaghetti.Block.custom.SpaghettiKineticBlock;
import net.senti.createspaghetti.CreateSpaghetti;
import net.senti.createspaghetti.item.ModItems;

import java.util.List;

@Mod.EventBusSubscriber(modid = CreateSpaghetti.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.FARMER) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            trades.get(1).add((p_219693_, p_219694_) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 2),
                    new ItemStack(ModItems.TOMATOSEEDS.get(), 2),
                    9, 6, 0.02f));
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof BlockItem blockItem)) return;
        if (!(blockItem.getBlock() instanceof SpaghettiKineticBlock)) return;

        double impact = BlockStressValues.getImpact(blockItem.getBlock());

        if (impact <= 0) return;

        event.getToolTip().add(Component.literal(" "));

        event.getToolTip().add(
                Component.translatable("create_spaghetti.kinetic")
                        .withStyle(ChatFormatting.GRAY)
        );

        event.getToolTip().add(
                Component.literal(TooltipHelper.makeProgressBar(3, 2) + " ")
                        .withStyle(ChatFormatting.GOLD)
                        .append(Component.literal((int) impact + "x ")
                                .withStyle(ChatFormatting.GOLD))
                        .append(Component.translatable("create.generic.unit.rpm")
                                .withStyle(ChatFormatting.GOLD))
        );
    }
}