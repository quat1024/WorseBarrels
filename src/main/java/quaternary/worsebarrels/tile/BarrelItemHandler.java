package quaternary.worsebarrels.tile;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import quaternary.worsebarrels.Util;
import quaternary.worsebarrels.WorseBarrels;
import quaternary.worsebarrels.WorseBarrelsConfig;
import quaternary.worsebarrels.block.BlockWorseBarrel;
import quaternary.worsebarrels.tile.TileWorseBarrel;

import javax.annotation.Nonnull;

public class BarrelItemHandler extends ItemStackHandler {
	public BarrelItemHandler(TileWorseBarrel tile) {
		super(STACK_COUNT);
		this.tile = tile;
	}
	
	private final TileWorseBarrel tile;
	
	public static final int STACK_COUNT = 8;
	
	public static final String STACK_KEY = "BarrelItem";
	public static final String COUNT_KEY = "BarrelCount";
	
	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack insertionStack, boolean simulate) {
		if(insertionStack.isEmpty()) return ItemStack.EMPTY;
		if(WorseBarrelsConfig.ITEM_BLACKLIST.contains(insertionStack.getItem())) return insertionStack; //no u
		
		ItemStack firstStack = getFirstNonemptyStack();
		
		boolean ok = firstStack.isEmpty() || ItemHandlerHelper.canItemStacksStack(firstStack, insertionStack);
		ok &= getNestedBarrelDepth(insertionStack) <= WorseBarrelsConfig.MAX_NESTING_DEPTH;
		
		if(ok) {
			return super.insertItem(slot, insertionStack, simulate);
		} else return insertionStack;
	}
	
	@Override
	protected void onContentsChanged(int slot) {
		tile.markDirty();
	}
	
	private int getNestedBarrelDepth(ItemStack stack_) {
		//please excuse the spaghetti
		ItemStack stack = stack_;
		int depth = 0;
		
		while(Block.getBlockFromItem(stack.getItem()) instanceof BlockWorseBarrel) {
			depth++;
			
			if(stack.hasTagCompound()) {
				NBTTagCompound innerNBT;
				if(stack.getTagCompound().hasKey("BlockEntityTag")) {
					innerNBT = stack.getTagCompound().getCompoundTag("BlockEntityTag");
				} else break;
				
				NBTTagCompound innerContents;
				if(innerNBT.hasKey("Contents")) {
					innerContents = innerNBT.getCompoundTag("Contents");
				} else break;
				
				NBTTagCompound innerItemNBT;
				if(innerContents.hasKey(STACK_KEY)) {
					innerItemNBT = innerContents.getCompoundTag(STACK_KEY);
				} else break;
				
				stack = new ItemStack(innerItemNBT);
			} else break;
		}
		
		return depth;
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
		
		int count = cmp.getInteger(COUNT_KEY);
		
		if(count != 0) {
			ItemStack stack = new ItemStack(cmp.getCompoundTag(STACK_KEY));
			stack.setCount(count);
			ItemHandlerHelper.insertItem(this, stack, false);
			
			if(getItemCount() == 0) {
				WorseBarrels.LOGGER.fatal("Tried to load a barrel that said it was non-empty, but no items were loaded??");
				WorseBarrels.LOGGER.fatal("Tag: " + cmp.toString());
			}
		}
	}
}
