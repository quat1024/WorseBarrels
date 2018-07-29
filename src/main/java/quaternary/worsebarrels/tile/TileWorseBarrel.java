package quaternary.worsebarrels.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import quaternary.worsebarrels.Util;
import quaternary.worsebarrels.block.BlockWorseBarrel;

import javax.annotation.Nullable;

public class TileWorseBarrel extends TileEntity {
	//Mfw
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}
	
	BarrelItemHandler handler;
	
	public TileWorseBarrel() {
		handler = new BarrelItemHandler(this);
	}
	
	public int getComparatorOverride() {
		return ItemHandlerHelper.calcRedstoneFromInventory(handler);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound cmp) {
		cmp.setTag("Contents", handler.writeNBT());
		
		return super.writeToNBT(cmp);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound cmp) {
		if(world == null) { //for some reason it's like that on the serve
			handler.readNBT(cmp.getCompoundTag("Contents"));
		}
		super.readFromNBT(cmp);
	}
	
	public void drop() {
		for(ItemStack stack : handler.getAllStacks()) {
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
		}
		Util.clearHandler(handler);
	}
	
	@CapabilityInject(IItemHandler.class)
	public static Capability<IItemHandler> ITEM_HANDLER = null;
	
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		IBlockState barrelState = world.getBlockState(pos);
		if(capability == ITEM_HANDLER && facing != barrelState.getValue(BlockWorseBarrel.ORIENTATION).facing) {
			return true;
		} else return super.hasCapability(capability, facing);
	}
	
	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		IBlockState barrelState = world.getBlockState(pos);
		if(capability == ITEM_HANDLER && facing != barrelState.getValue(BlockWorseBarrel.ORIENTATION).facing) {
			return (T) handler;
		} else return super.getCapability(capability, facing);
	}
	
}