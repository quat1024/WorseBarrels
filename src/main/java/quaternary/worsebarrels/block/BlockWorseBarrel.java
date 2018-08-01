package quaternary.worsebarrels.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.*;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import quaternary.worsebarrels.WorseBarrelsConfig;
import quaternary.worsebarrels.net.WorseBarrelsPacketHandler;
import quaternary.worsebarrels.etc.BarrelItemHandler;
import quaternary.worsebarrels.tile.TileWorseBarrel;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockWorseBarrel extends Block {
	public static final PropertyEnum<EnumWorseBarrelOrientation> ORIENTATION = PropertyEnum.create("orientation", EnumWorseBarrelOrientation.class);
	
	public BlockWorseBarrel(Material mat) {
		super(mat, MapColor.BROWN);
		
		setHardness(1.2f);
		setResistance(1f);
		setSoundType(SoundType.WOOD);
		
		setDefaultState(getDefaultState().withProperty(ORIENTATION, EnumWorseBarrelOrientation.UPRIGHT_NORTH));
	}
	
	//tile
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
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		if(world.isRemote) {
			//This is slightly janky, but works well enough unless the barrel is only slightly within view.
			RayTraceResult res = world.rayTraceBlocks(player.getPositionVector().add(0, player.getEyeHeight(), 0), new Vec3d(pos).add(.5, .5, .5), false, true, false);
			if(res != null && res.sideHit != null) {
				IBlockState barrelState = world.getBlockState(pos);
				if(barrelState.getValue(ORIENTATION).facing != res.sideHit) return;
				
				IMessage message;
				if(player.isSneaking()) {
					message = WorseBarrelsConfig.SNEAK_LEFT_CLICK_ACTION.getPacket().apply(pos);
				} else {
					message = WorseBarrelsConfig.LEFT_CLICK_ACTION.getPacket().apply(pos);
				}
				
				WorseBarrelsPacketHandler.sendToServer(message);
			}
		}
	}
	
	@Override
	public EnumPushReaction getPushReaction(IBlockState state) {
		return EnumPushReaction.DESTROY;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileWorseBarrel) {
			TileWorseBarrel barrelTile = (TileWorseBarrel) te;
			
			ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
			if(!barrelTile.isEmpty()) {
				NBTTagCompound stackNBT = new NBTTagCompound();
				//"BlockEntityTag" is the magic string that makes ItemBlocks fill tiles with NBT data when placed
				stackNBT.setTag("BlockEntityTag", barrelTile.writeItemsOnlyToNBT(new NBTTagCompound()));
				stack.setTagCompound(stackNBT);
			}
			
			spawnAsEntity(world, pos, stack);
		}
		
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
		//No-op (handled in breakBlock)
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag mistake) {
		super.addInformation(stack, world, tooltip, mistake);
		
		NBTTagCompound stackNBT = stack.getTagCompound();
		if(stackNBT != null && stackNBT.hasKey("BlockEntityTag", Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound barrelNBT = stackNBT.getCompoundTag("BlockEntityTag");
			
			//Hey look I'm being way, way too defensive.
			NBTTagCompound contentsNBT;
			if(barrelNBT.hasKey("Contents", Constants.NBT.TAG_COMPOUND)) {
				contentsNBT = barrelNBT.getCompoundTag("Contents");
			} else return;
			
			int barrelCount;
			if(contentsNBT.hasKey(BarrelItemHandler.COUNT_KEY, Constants.NBT.TAG_INT)) {
				barrelCount = contentsNBT.getInteger(BarrelItemHandler.COUNT_KEY);
			} else return;
			
			ItemStack barrelStack;
			if(contentsNBT.hasKey(BarrelItemHandler.STACK_KEY, Constants.NBT.TAG_COMPOUND)) {
				barrelStack = new ItemStack(contentsNBT.getCompoundTag(BarrelItemHandler.STACK_KEY));
			} else return;
			
			StringBuilder bob = new StringBuilder();
			bob.append(barrelCount);
			bob.append("x ");
			bob.append(barrelStack.getRarity().color);
			bob.append(barrelStack.getDisplayName());
			if(mistake.isAdvanced()) {
				bob.append(TextFormatting.WHITE);
				bob.append(" (#");
				bob.append(Item.getIdFromItem(barrelStack.getItem()));
				bob.append('/');
				bob.append(barrelStack.getItemDamage());
				bob.append(')');
			}
			
			tooltip.add(bob.toString());
			
			List<String> containedTooltip = new ArrayList<>();
			barrelStack.getItem().addInformation(barrelStack, world, containedTooltip, mistake);
			for(String contained : containedTooltip) {
				tooltip.add("   " + contained);
			}
			
			if(mistake.isAdvanced()) {
				tooltip.add("   " + TextFormatting.DARK_GRAY + barrelStack.getItem().getRegistryName());
			}
		}
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
		EnumWorseBarrelOrientation orient;
		
		//If they are looking up or down, or aiming at the top of a block while looking sideways, use player look
		//The second case is especially important since it allows for placing upright barrels
		//despite clicking against a floor or ceiling
		if(Math.abs(placer.rotationPitch) > 65 || facing.getAxis() == EnumFacing.Axis.Y) {
			orient = EnumWorseBarrelOrientation.fromEntityLiving(pos, placer);
		} else {
			//Face away from the wall they clicked
			orient = EnumWorseBarrelOrientation.fromHorizontalFacing(facing);
		}
		
		return getDefaultState().withProperty(ORIENTATION, orient);
	}
}
