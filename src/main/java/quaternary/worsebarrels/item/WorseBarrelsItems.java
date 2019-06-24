package quaternary.worsebarrels.item;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.registries.IForgeRegistry;
import quaternary.worsebarrels.block.WorseBarrelsBlocks;

import java.util.Objects;
import java.util.stream.Collectors;

public final class WorseBarrelsItems {
	private WorseBarrelsItems() {}
	
	public static ImmutableList<ItemBlock> BARREL_ITEMS = null;
	
	public static void registerItems(IForgeRegistry<Item> reg) {
		BARREL_ITEMS = ImmutableList.copyOf(WorseBarrelsBlocks.BARREL_BLOCKS.stream().map(b -> createItemBlock(new ItemBlockWorseBarrel(b))).collect(Collectors.toList()));
		
		BARREL_ITEMS.forEach(reg::register);
	}
	
	private static <T extends ItemBlock> T createItemBlock(T itemBlock) {
		itemBlock.setRegistryName(Objects.requireNonNull(itemBlock.getBlock().getRegistryName()));
		itemBlock.setCreativeTab(itemBlock.getBlock().getCreativeTab());
		
		return itemBlock;
	}
}
