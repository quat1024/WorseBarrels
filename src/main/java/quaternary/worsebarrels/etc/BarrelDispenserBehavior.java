package quaternary.worsebarrels.etc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import quaternary.worsebarrels.block.BlockWorseBarrel;
import quaternary.worsebarrels.block.EnumWorseBarrelOrientation;
import quaternary.worsebarrels.tile.TileWorseBarrel;

public class BarrelDispenserBehavior extends Bootstrap.BehaviorDispenseOptional {
	public BarrelDispenserBehavior() {
		super();
	}
	
	@Override
	protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
		Block block = Block.getBlockFromItem(stack.getItem());
		World world = source.getWorld();
		EnumFacing dispenserFacing = source.getBlockState().getValue(BlockDispenser.FACING);
		BlockPos blockpos = source.getBlockPos().offset(dispenserFacing);
		this.successful = world.mayPlace(block, blockpos, false, EnumFacing.DOWN, null);
		
		if(this.successful) {
			EnumWorseBarrelOrientation barrelOrientation = EnumWorseBarrelOrientation.fromFacingDirectional(dispenserFacing);
			IBlockState barrelState = block.getDefaultState().withProperty(BlockWorseBarrel.ORIENTATION, barrelOrientation);
			world.setBlockState(blockpos, barrelState);
			
			TileEntity tile = world.getTileEntity(blockpos);
			
			if(tile instanceof TileWorseBarrel && stack.hasTagCompound()) {
				((TileWorseBarrel)tile).readItemsOnlyFromNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"));
			}
			
			world.updateComparatorOutputLevel(blockpos, barrelState.getBlock());
			stack.shrink(1);
		}
		
		return stack;
	}
}
