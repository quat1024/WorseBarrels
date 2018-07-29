package quaternary.worsebarrels.block;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;

import java.util.Locale;

public enum EnumWorseBarrelOrientation implements IStringSerializable{
	UPRIGHT_NORTH(EnumFacing.NORTH), UPRIGHT_SOUTH(EnumFacing.SOUTH),
	UPRIGHT_EAST(EnumFacing.EAST),   UPRIGHT_WEST(EnumFacing.WEST),
	
	CEILING_NORTH(EnumFacing.UP),    CEILING_SOUTH(EnumFacing.UP),
	CEILING_EAST(EnumFacing.UP),     CEILING_WEST(EnumFacing.UP),
	
	FLOOR_NORTH(EnumFacing.DOWN),    FLOOR_SOUTH(EnumFacing.DOWN),
	FLOOR_EAST(EnumFacing.DOWN),     FLOOR_WEST(EnumFacing.DOWN);
	
	public EnumFacing facing;
	EnumWorseBarrelOrientation(EnumFacing facing) {
		this.facing = facing;
	}
	
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
		switch (facing) {
			case NORTH: return UPRIGHT_NORTH;
			case EAST:  return UPRIGHT_EAST;
			case SOUTH: return UPRIGHT_SOUTH;
			case WEST:  return UPRIGHT_WEST;
			default: throw new IllegalArgumentException("Not a horizontal facing: " + facing);
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
