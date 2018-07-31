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
		//TODO use itme capability to determine filledness
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag")) {
			return WorseBarrelsConfig.FILLED_STACK_SIZE;
		} else return WorseBarrelsConfig.EMPTY_STACK_SIZE;
	}
	
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return null;
		//TODO: Implement this
		//return new WorseBarrelItemCapProvider(stack);
	}
	
	public static class WorseBarrelItemCapProvider implements ICapabilityProvider {
		public WorseBarrelItemCapProvider(ItemStack barrelStack) {
			this.barrelStack = barrelStack;
		}
		
		ItemStack barrelStack;
		
		//Note: Only barrel stacks with a stack size of 1 can provide an item handler capability.
		//The reason is quite simple. Barrels with the same amounts of items inside can stack.
		//If there's a stack of 8 barrels with 1 stone each in them, what happens when only 1
		//stone is requested?
		
		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
			return barrelStack.getCount() == 1 && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
		}
		
		@Nullable
		@Override
		@SuppressWarnings("unchecked")
		public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
			if(barrelStack.getCount() != 1) return null;
			return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? (T) new BarrelItemItemHandler(barrelStack) : null;
		}
	}
}
