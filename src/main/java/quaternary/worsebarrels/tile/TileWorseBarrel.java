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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import quaternary.worsebarrels.Util;
import quaternary.worsebarrels.block.BlockWorseBarrel;

import javax.annotation.Nullable;

public class TileWorseBarrel extends TileEntity {
	public TileWorseBarrel() {
		handler = new BarrelItemHandler(this);
	}
	
	private final BarrelItemHandler handler;
	
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
		super.readFromNBT(cmp);
		readItemsOnlyFromNBT(cmp);
	}
	
	public void readItemsOnlyFromNBT(NBTTagCompound cmp) {
		handler.readNBT(cmp.getCompoundTag("Contents"));
	}
	
	@Override
	public void onLoad() {
		markDirty();
	}
	
	@Override
	public void markDirty() {
		super.markDirty();
		
		if(world != null && !world.isRemote) {
			IBlockState barrelState = world.getBlockState(pos);
			world.notifyBlockUpdate(pos, barrelState, barrelState, 2);
		}
	}
	
	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, 69, getUpdateTag());
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
	
	//Mfw
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}
	
	private static final Capability<IItemHandler> ITEM_HANDLER = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		if(capability == ITEM_HANDLER) {
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() instanceof BlockWorseBarrel && facing != state.getValue(BlockWorseBarrel.ORIENTATION).facing) {
				return true;
			}
		}
		
		return super.hasCapability(capability, facing);
	}
	
	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if(capability == ITEM_HANDLER) {
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() instanceof BlockWorseBarrel && facing != state.getValue(BlockWorseBarrel.ORIENTATION).facing) {
				return (T) handler;
			}
		}
		
		return super.getCapability(capability, facing);
	}
}
