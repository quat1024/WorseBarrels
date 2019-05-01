package quaternary.worsebarrels.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import quaternary.worsebarrels.WorseBarrelsConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemBlockWorseBarrel extends ItemBlock {
	public ItemBlockWorseBarrel(Block block) {
		super(block);
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag")) {
			return WorseBarrelsConfig.FILLED_STACK_SIZE;
		} else return WorseBarrelsConfig.EMPTY_STACK_SIZE;
	}
}
