package quaternary.worsebarrels.cap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.*;
import quaternary.worsebarrels.*;

import javax.annotation.Nonnull;

public class BarrelItemHandler extends ItemStackHandler {
	TileWorseBarrel tile;
	
	static final int STACK_COUNT = 8;
	
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
	
	public NonNullList<ItemStack> getAllStacks() {
		return stacks;
	}
	
	public NBTTagCompound writeNBT() {
		NBTTagCompound cmp = new NBTTagCompound();
		ItemStack firstStack = Util.withStackSize(getFirstNonemptyStack(), 1);
		int count = getItemCount();
		
		NBTTagCompound item = new NBTTagCompound();
		firstStack.writeToNBT(item);
		
		cmp.setTag("BarrelItem", item);
		cmp.setInteger("BarrelCount", count);
		return cmp;
	}
	
	public void readNBT(NBTTagCompound cmp) {
		Util.clearHandler(this);
		
		ItemStack stack = new ItemStack(cmp.getCompoundTag("BarrelItem"));
		stack.setCount(cmp.getInteger("BarrelCount"));
		ItemHandlerHelper.insertItem(this, stack, false);
	}
}
