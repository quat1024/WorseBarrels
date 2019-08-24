package quaternary.worsebarrels.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import quaternary.worsebarrels.WorseBarrels;
import quaternary.worsebarrels.WorseBarrelsConfig;
import quaternary.worsebarrels.net.WorseBarrelsPacketHandler;

@SuppressWarnings("unused")
public class ClientProxy extends ServerProxy {
	@Override
	public boolean handleRightClickBarrel(World world, BlockPos pos, EntityPlayer player) {
		super.handleRightClickBarrel(world, pos, player);
		if(!world.isRemote) return true;
		
		IMessage message;
		
		if(player.isSneaking()) {
			message = WorseBarrelsConfig.SNEAK_RIGHT_CLICK_ACTION.getPacket(pos);
		} else if(GuiScreen.isCtrlKeyDown()) {
			message = WorseBarrelsConfig.CTRL_RIGHT_CLICK_ACTION.getPacket(pos);
		} else {
			message = WorseBarrelsConfig.RIGHT_CLICK_ACTION.getPacket(pos);
		}
		
		if(message == null) {
			return false;
		} else {
			WorseBarrelsPacketHandler.sendToServer(message);
			return true;
		}
	}
	
	@Override
	public void handleLeftClickBarrel(World world, BlockPos pos, EntityPlayer player) {
		super.handleLeftClickBarrel(world, pos, player);
		if(!world.isRemote) return;
		
		//Fixes double-hits...?
		//Basically playercontrollermp#clickBlock is called in 2 places.
		//Once when you actually click the block, and also from onPlayerDamageBlock when you start to break it.
		//This tells the calls apart.              : v o l d e t h o n k :
		if(Minecraft.getMinecraft().playerController.getIsHittingBlock()) return;
		
		IMessage message;
		
		if(player.isSneaking()) {
			message = WorseBarrelsConfig.SNEAK_LEFT_CLICK_ACTION.getPacket(pos);
		} else if(GuiScreen.isCtrlKeyDown()) {
			message = WorseBarrelsConfig.CTRL_LEFT_CLICK_ACTION.getPacket(pos);
		} else {
			message = WorseBarrelsConfig.LEFT_CLICK_ACTION.getPacket(pos);
		}
		
		if(message != null)	WorseBarrelsPacketHandler.sendToServer(message);
	}
}
