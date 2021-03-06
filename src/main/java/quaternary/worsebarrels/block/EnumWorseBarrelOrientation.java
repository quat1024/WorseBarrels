package quaternary.worsebarrels.block;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;

import java.util.Locale;

public enum EnumWorseBarrelOrientation implements IStringSerializable{
	UPRIGHT_NORTH(EnumFacing.NORTH, EnumFacing.UP), UPRIGHT_SOUTH(EnumFacing.SOUTH, EnumFacing.UP),
	UPRIGHT_EAST(EnumFacing.EAST, EnumFacing.UP),   UPRIGHT_WEST(EnumFacing.WEST, EnumFacing.UP),
	
	CEILING_NORTH(EnumFacing.UP, EnumFacing.NORTH),   CEILING_SOUTH(EnumFacing.UP, EnumFacing.SOUTH),
	CEILING_EAST(EnumFacing.UP, EnumFacing.EAST),     CEILING_WEST(EnumFacing.UP, EnumFacing.WEST),
	
	FLOOR_NORTH(EnumFacing.DOWN, EnumFacing.NORTH),   FLOOR_SOUTH(EnumFacing.DOWN, EnumFacing.SOUTH),
	FLOOR_EAST(EnumFacing.DOWN, EnumFacing.EAST),     FLOOR_WEST(EnumFacing.DOWN, EnumFacing.WEST);
	
	EnumWorseBarrelOrientation(EnumFacing facing, EnumFacing secondaryFacing) {
		this.facing = facing;
		this.secondaryFacing = secondaryFacing;
	}
	
	public final EnumFacing facing;
	public final EnumFacing secondaryFacing; //TODO Name this better, I'm tired.
	
	//Blehhhhhhhhh
	EnumWorseBarrelOrientation rotateUp() {
		switch(this) {
			case UPRIGHT_NORTH: return CEILING_NORTH;
			case UPRIGHT_EAST:  return CEILING_EAST;
			case UPRIGHT_SOUTH: return CEILING_SOUTH;
			case UPRIGHT_WEST:  return CEILING_WEST;
			default: throw new IllegalArgumentException("Not a horizontal facing: " + facing);
		}
	}
	
	EnumWorseBarrelOrientation rotateDown() {
		switch(this) {
			case UPRIGHT_NORTH: return FLOOR_NORTH;
			case UPRIGHT_EAST:  return FLOOR_EAST;
			case UPRIGHT_SOUTH: return FLOOR_SOUTH;
			case UPRIGHT_WEST:  return FLOOR_WEST;
			default: throw new IllegalArgumentException("Not a horizontal facing: " + facing);
		}
	}
	
	public static EnumWorseBarrelOrientation fromHorizontalFacing(EnumFacing facing) {
		switch(facing) {
			case NORTH: return UPRIGHT_NORTH;
			case EAST:  return UPRIGHT_EAST;
			case SOUTH: return UPRIGHT_SOUTH;
			case WEST:  return UPRIGHT_WEST;
			default: throw new IllegalArgumentException("Not a horizontal facing: " + facing);
		}
	}
	
	public static EnumWorseBarrelOrientation fromFacingDirectional(EnumFacing facing) {
		switch(facing) {
			case UP: return CEILING_NORTH;
			case DOWN: return FLOOR_NORTH;
			default: return fromHorizontalFacing(facing);
		}
	}
	
	public static EnumWorseBarrelOrientation fromEntityLiving(BlockPos p, EntityLivingBase e) {
		EnumFacing livingFacing = EnumFacing.getDirectionFromEntityLiving(p, e);
		
		if(livingFacing.getHorizontalIndex() != -1) {
			return fromHorizontalFacing(livingFacing);
		}
		
		EnumWorseBarrelOrientation flatRotation = fromHorizontalFacing(e.getHorizontalFacing().getOpposite());
		if(livingFacing == EnumFacing.UP) return flatRotation.rotateUp();
		else return flatRotation.rotateDown();		
	}
	
	@Override
	public String getName() {
		return name().toLowerCase(Locale.ROOT);
	}
}
