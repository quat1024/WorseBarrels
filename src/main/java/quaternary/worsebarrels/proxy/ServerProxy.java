package quaternary.worsebarrels.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

@SuppressWarnings("unused")
public class ServerProxy {
	public boolean handleRightClickBarrel(World world, BlockPos pos, EntityPlayer player) {
		if(!player.getHeldItemMainhand().isEmpty()) {
			player.swingArm(EnumHand.MAIN_HAND);
		} else if(!player.getHeldItemOffhand().isEmpty()) {
			player.swingArm(EnumHand.OFF_HAND);
		}
		
		return true;
	}
	
	public void handleLeftClickBarrel(World world, BlockPos pos, EntityPlayer player) {
		//No-op (nothing special needs to be done serverside here, I don't think)
	}
}
