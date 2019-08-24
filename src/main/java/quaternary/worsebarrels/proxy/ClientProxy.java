package quaternary.worsebarrels.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import quaternary.worsebarrels.WorseBarrels;
import quaternary.worsebarrels.etc.EnumBarrelAction;
import quaternary.worsebarrels.WorseBarrelsConfig;
import quaternary.worsebarrels.net.WorseBarrelsPacketHandler;

@SuppressWarnings("unused")
public class ClientProxy extends ServerProxy {
	private long lastClickTick = 0L;
	
	@Override
	public boolean handleRightClickBarrel(World world, BlockPos pos, EntityPlayer player) {
		super.handleRightClickBarrel(world, pos, player);
		if(!world.isRemote) return true;
		
		return handleClick(world, pos, player,
			WorseBarrelsConfig.SNEAK_RIGHT_CLICK_ACTION,
			WorseBarrelsConfig.CTRL_RIGHT_CLICK_ACTION,
			WorseBarrelsConfig.RIGHT_CLICK_ACTION
		);
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
		
		handleClick(world, pos, player,
			WorseBarrelsConfig.SNEAK_LEFT_CLICK_ACTION,
			WorseBarrelsConfig.CTRL_LEFT_CLICK_ACTION,
			WorseBarrelsConfig.LEFT_CLICK_ACTION
		);
	}
	
	private boolean handleClick(World world, BlockPos pos, EntityPlayer player, EnumBarrelAction sneak, EnumBarrelAction ctrl, EnumBarrelAction normal) {
		EnumBarrelAction action;
		
		if(player.isSneaking()) {
			action = sneak;
		} else if(GuiScreen.isCtrlKeyDown()) {
			action = ctrl;
		} else {
			action = normal;
		}
		
		if(world.getTotalWorldTime() - lastClickTick <= WorseBarrelsConfig.DOUBLE_CLICK_TIME) {
			action = action.doubleClickUpgrade();
		}
		
		if(action == EnumBarrelAction.NOTHING) {
			return false;
		} else {
			IMessage message = action.getPacket(pos);
			if(message != null) WorseBarrelsPacketHandler.sendToServer(message);
			lastClickTick = world.getTotalWorldTime();
			return true;
		}
	}
}
