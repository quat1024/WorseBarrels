package quaternary.worsebarrels.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import quaternary.worsebarrels.WorseBarrelsConfig;

public class ItemBlockWorseBarrel extends ItemBlock {
	public ItemBlockWorseBarrel(Block block) {
		super(block);
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		//TODO use an item capability
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag")) {
			return WorseBarrelsConfig.FILLED_STACK_SIZE;
		} else return WorseBarrelsConfig.EMPTY_STACK_SIZE;
	}
}
