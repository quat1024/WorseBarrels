package quaternary.worsebarrels;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.*;

public class Util {
	public static ItemStack withStackSize(ItemStack stack, int stackSize) {
		ItemStack stack2 = stack.copy();
		stack2.setCount(stackSize);
		return stack2;
	}
	
	public static void clearHandler(IItemHandlerModifiable handlerModifiable) {
		for(int i=0; i < handlerModifiable.getSlots(); i++) {
			handlerModifiable.setStackInSlot(i, ItemStack.EMPTY);
		}
	}
	
	public static boolean isHandlerEmpty(IItemHandler handler) {
		for(int i = 0; i < handler.getSlots(); i++) {
			if(!handler.getStackInSlot(i).isEmpty()) return false;
		}
		
		return true;
	}
	
	public static ItemStack getFirstStackInHandler(IItemHandler handler) {
		for(int i = 0; i < handler.getSlots(); i++) {
			ItemStack stack = handler.getStackInSlot(i);
			if(!stack.isEmpty()) return stack;
		}
		
		return ItemStack.EMPTY;
	}
	
	public static int countItemsInHandler(IItemHandler handler) {
		int runningTotal = 0;
		for(int i = 0; i < handler.getSlots(); i++) {
			ItemStack stack = handler.getStackInSlot(i);
			if(!stack.isEmpty()) runningTotal += stack.getCount();
		}
		return runningTotal;
	}
	
	public static ItemStack extractItem(IItemHandler handler, int count, boolean fake) {
		ItemStack runningStack = ItemStack.EMPTY;
		
		for(int i = 0, slotCount = handler.getSlots(); i < slotCount; i++) {
			ItemStack fakeStack = handler.extractItem(i, count, true);
			
			if(!fakeStack.isEmpty() && fakeStack.getCount() <= count) {
				ItemStack realStack = handler.extractItem(i, count, fake);
				
				if(runningStack.isEmpty()) {
					runningStack = realStack.copy();
					count -= realStack.getCount();
				} else if(ItemHandlerHelper.canItemStacksStack(runningStack, realStack)) {
					runningStack.grow(realStack.getCount());
					count -= realStack.getCount();
				}
			}
		}
		
		return runningStack;
	}
	
	//Range remap function. Useful!
	public static float map(float value, float low1, float high1, float low2, float high2) {
		return low2 + (value - low1) * ((high2 - low2) / (high1 - low1));
	}
}
