package quaternary.worsebarrels.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import quaternary.worsebarrels.Util;

import javax.annotation.Nonnull;

public class BarrelItemHandler extends ItemStackHandler {
	TileWorseBarrel tile;
	
	public static final int STACK_COUNT = 8;
	public static final String STACK_KEY = "BarrelItem";
	public static final String COUNT_KEY = "BarrelCount";
	
	public BarrelItemHandler(TileWorseBarrel tile) {
		super(STACK_COUNT);
		this.tile = tile;
	}
	
	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack insertionStack, boolean simulate) {
		if(insertionStack.isEmpty()) return ItemStack.EMPTY;
		
		ItemStack firstStack = getFirstNonemptyStack();
		if(firstStack.isEmpty()) {
			//Barrel is empty? Anything goes
			return super.insertItem(slot, insertionStack, simulate);
		} else {
			//Barrel has something in it? Allow it in... if it matches
			if(ItemHandlerHelper.canItemStacksStack(firstStack, insertionStack)) {
				return super.insertItem(slot, insertionStack, simulate);
			} else return insertionStack; //Reject it
		}
	}
	
	@Override
	protected void onContentsChanged(int slot) {
		tile.markDirty();
	}
	
	private ItemStack getFirstNonemptyStack() {
		for(ItemStack stack : stacks) {
			if(stack.isEmpty()) continue;
			return stack;
		}
		return ItemStack.EMPTY;
	}
	
	private int getItemCount() {
		int runningCount = 0;
		for(ItemStack stack : stacks) {
			runningCount += stack.getCount();
		}
		return runningCount;
	}
	
	public NBTTagCompound writeNBT() {
		NBTTagCompound cmp = new NBTTagCompound();
		ItemStack firstStack = Util.withStackSize(getFirstNonemptyStack(), 1);
		int count = getItemCount();
		
		NBTTagCompound item = new NBTTagCompound();
		firstStack.writeToNBT(item);
		
		cmp.setTag(STACK_KEY, item);
		cmp.setInteger(COUNT_KEY, count);
		return cmp;
	}
	
	public void readNBT(NBTTagCompound cmp) {
		Util.clearHandler(this);
		
		ItemStack stack = new ItemStack(cmp.getCompoundTag(STACK_KEY));
		stack.setCount(cmp.getInteger(COUNT_KEY));
		ItemHandlerHelper.insertItem(this, stack, false);
	}
}
