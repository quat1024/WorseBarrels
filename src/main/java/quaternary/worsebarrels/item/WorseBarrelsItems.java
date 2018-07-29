package quaternary.worsebarrels.item;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.registries.IForgeRegistry;
import quaternary.worsebarrels.block.WorseBarrelsBlocks;

import java.util.stream.Collectors;

public final class WorseBarrelsItems {
	private WorseBarrelsItems() {}
	
	public static ImmutableList<ItemBlock> WOOD_BARREL_ITEMS = null;
	
	public static void registerItems(IForgeRegistry<Item> reg) {
		WOOD_BARREL_ITEMS = ImmutableList.copyOf(WorseBarrelsBlocks.WOOD_BARRELS.stream().map(b -> createItemBlock(new ItemBlock(b))).collect(Collectors.toList()));
		
		WOOD_BARREL_ITEMS.forEach(reg::register);
	}
	
	private static <T extends ItemBlock> T createItemBlock(T itemBlock) {
		itemBlock.setRegistryName(itemBlock.getBlock().getRegistryName());
		itemBlock.setCreativeTab(itemBlock.getBlock().getCreativeTab());
		
		return itemBlock;
	}
}
