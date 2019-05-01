package quaternary.worsebarrels.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

@SuppressWarnings("unused")
public class ServerProxy {
	public void handleRightClickBarrel(PlayerInteractEvent.RightClickBlock e) {
		e.getEntityPlayer().swingArm(EnumHand.MAIN_HAND);
		e.setUseItem(Event.Result.DENY);
	}
	
	public void handleLeftClickBarrel(World world, BlockPos pos, EntityPlayer player) {
		//No-op (nothing special needs to be done serverside here, I don't think)
	}
}
