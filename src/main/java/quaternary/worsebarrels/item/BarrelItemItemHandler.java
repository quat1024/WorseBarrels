package quaternary.worsebarrels.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import quaternary.worsebarrels.Util;
import quaternary.worsebarrels.etc.BarrelItemHandler;

public class BarrelItemItemHandler extends BarrelItemHandler {
	public BarrelItemItemHandler(ItemStack stack) {
		this.stack = stack;
	}
	
	ItemStack stack;
	
	@Override
	protected void onContentsChanged(int slot) {
		if(Util.isHandlerEmpty(this)) {
			stack.removeSubCompound("BlockEntityTag");
			//TODO this is a piss poor way of marking the barrel as empty
			//however i do check for the existence of BlockEntityTag to determine if there are things inside
			//so idk
		} else {
			//it SUCKS DONKEY
			NBTTagCompound blockEntityTag = stack.getOrCreateSubCompound("BlockEntityTag");
			blockEntityTag.setTag("Contents", super.writeNBT());
		}
	}
}
