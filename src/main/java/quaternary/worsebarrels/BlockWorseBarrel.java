package quaternary.worsebarrels;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockWorseBarrel extends Block {
	public static final PropertyEnum<EnumWorseBarrelOrientation> ORIENTATION = PropertyEnum.create("orientation", EnumWorseBarrelOrientation.class);
	
	public BlockWorseBarrel(String name, Material mat) {
		super(mat, MapColor.BROWN);
		
		setHardness(1.2f);
		setResistance(1f);
		setSoundType(SoundType.WOOD);
		
		setRegistryName(new ResourceLocation(WorseBarrels.MODID, "barrel_" + name));
		setUnlocalizedName(WorseBarrels.MODID + "." + name);
		setCreativeTab(WorseBarrelsCreativeTab.INST);
		
		setDefaultState(getDefaultState().withProperty(ORIENTATION, EnumWorseBarrelOrientation.UPRIGHT_NORTH));
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileWorseBarrel();
	}
	
	@Override
	public boolean hasComparatorInputOverride(IBlockState state) {
		return true;
	}
	
	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileWorseBarrel) {
			return ((TileWorseBarrel)te).getComparatorOverride();
		} else return 0;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileWorseBarrel) {
			((TileWorseBarrel)te).drop();
		}
		
		super.breakBlock(world, pos, state);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, ORIENTATION);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(ORIENTATION).ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ORIENTATION, EnumWorseBarrelOrientation.values()[meta]);
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(ORIENTATION, EnumWorseBarrelOrientation.fromEntityLiving(pos, placer));
	}
}
