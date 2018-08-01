package quaternary.worsebarrels.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
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
import quaternary.worsebarrels.etc.BarrelItemHandler;

import javax.annotation.Nullable;

public class TileWorseBarrel extends TileEntity {
	//Mfw
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}
	
	BarrelItemHandler handler;
	
	public TileWorseBarrel() {
		handler = new BarrelTileItemHandler(this);
	}
	
	public int getComparatorOverride() {
		return ItemHandlerHelper.calcRedstoneFromInventory(handler);
	}
	
	public boolean isEmpty() {
		return Util.isHandlerEmpty(handler);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound cmp) {
		writeItemsOnlyToNBT(cmp);
		return super.writeToNBT(cmp);
	}
	
	public NBTTagCompound writeItemsOnlyToNBT(NBTTagCompound cmp) {
		cmp.setTag("Contents", handler.writeNBT());
		return cmp;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound cmp) {
		readItemsOnlyFromNBT(cmp);
		super.readFromNBT(cmp);
	}
	
	public void readItemsOnlyFromNBT(NBTTagCompound cmp) {
		handler.readNBT(cmp.getCompoundTag("Contents"));
	}
	
	@Override
	public void markDirty() {
		super.markDirty();
		
		IBlockState barrelState = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, barrelState, barrelState, 2);
	}
	
	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 69, writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
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
