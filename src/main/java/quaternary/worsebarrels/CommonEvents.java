package quaternary.worsebarrels;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import quaternary.worsebarrels.block.BlockWorseBarrel;
import quaternary.worsebarrels.net.*;
import quaternary.worsebarrels.tile.TileWorseBarrel;

@Mod.EventBusSubscriber(modid = WorseBarrels.MODID)
public class CommonEvents {
	@SubscribeEvent
	public static void leftClick(PlayerInteractEvent.LeftClickBlock e) {
		if(FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
		
		handleClick(e.getWorld(), e.getPos(), e.getEntityPlayer(), e.getFace(), () -> {
			WorseBarrelsPacketHandler.sendTo(new MessageLeftClickBarrel(e.getPos(), e.getEntityPlayer().isSneaking()), (EntityPlayerMP) e.getEntityPlayer());
			e.setUseItem(Event.Result.DENY);
		});
	}
	
	public static void handleClick(World world, BlockPos pos, EntityPlayer player, EnumFacing clickedSide, Runnable ifBarrel) {
		if(world.getTileEntity(pos) instanceof TileWorseBarrel) {
			EnumFacing barrelFacing = world.getBlockState(pos).getValue(BlockWorseBarrel.ORIENTATION).facing;
			
			if(barrelFacing == clickedSide) {
				ifBarrel.run();
			}
		}
	}
}
