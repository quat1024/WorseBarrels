package quaternary.worsebarrels;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

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
	
	//Range remap function. Useful!
	public static float map(float value, float low1, float high1, float low2, float high2) {
		return low2 + (value - low1) * ((high2 - low2) / (high1 - low1));
	}
}
