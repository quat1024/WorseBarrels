package quaternary.worsebarrels.proxy;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import quaternary.worsebarrels.WorseBarrelsConfig;
import quaternary.worsebarrels.net.WorseBarrelsPacketHandler;

@SuppressWarnings("unused")
public class ClientProxy extends ServerProxy {
	@Override
	public void handleRightClickBarrel(PlayerInteractEvent.RightClickBlock e) {
		if(!e.getWorld().isRemote) return;
		
		IMessage message;
		
		if(GuiScreen.isCtrlKeyDown()) {
			message = WorseBarrelsConfig.CTRL_RIGHT_CLICK_ACTION.getPacket(e.getPos());
		} else if(e.getEntityPlayer().isSneaking()) {
			message = WorseBarrelsConfig.SNEAK_RIGHT_CLICK_ACTION.getPacket(e.getPos());
		} else {
			message = WorseBarrelsConfig.RIGHT_CLICK_ACTION.getPacket(e.getPos());
		}
		
		if(message != null) WorseBarrelsPacketHandler.sendToServer(message);
		
		super.handleRightClickBarrel(e);
	}
	
	@Override
	public void handleLeftClickBarrel(World world, BlockPos pos, EntityPlayer player) {
		if(!world.isRemote) return;
		
		IMessage message;
		
		if(GuiScreen.isCtrlKeyDown()) {
			message = WorseBarrelsConfig.CTRL_LEFT_CLICK_ACTION.getPacket(pos);
		} else if(player.isSneaking()) {
			message = WorseBarrelsConfig.SNEAK_LEFT_CLICK_ACTION.getPacket(pos);
		} else {
			message = WorseBarrelsConfig.LEFT_CLICK_ACTION.getPacket(pos);
		}
		
		if(message != null)	WorseBarrelsPacketHandler.sendToServer(message);
		
		super.handleLeftClickBarrel(world, pos, player);
	}
}
