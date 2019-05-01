package quaternary.worsebarrels;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import quaternary.worsebarrels.block.BlockWorseBarrel;

@Mod.EventBusSubscriber(modid = WorseBarrels.MODID)
public class CommonEvents {
	@SubscribeEvent
	public static void breakSpeed(PlayerEvent.BreakSpeed e) {
		EntityPlayer player = e.getEntityPlayer();
		BlockPos pos = e.getPos();
		if(player == null || pos == null) return;
		World world = player.world;
		
		IBlockState state = e.getState();
		if(!(state.getBlock() instanceof BlockWorseBarrel)) return;
		
		RayTraceResult hit = world.rayTraceBlocks(player.getPositionVector().add(0, player.getEyeHeight(), 0), new Vec3d(pos).add(.5, .5, .5), false, true, false);
		if(hit == null || hit.sideHit == null) return;
		
		if(state.getValue(BlockWorseBarrel.ORIENTATION).facing == hit.sideHit) {
			e.setNewSpeed(0);
		}
	}
	
	@SubscribeEvent
	public static void useBlock(PlayerInteractEvent.RightClickBlock e) {
		//Goal: call onBlockActivated on the barrel, even if the player is sneaking.
		EntityPlayer player = e.getEntityPlayer();
		if(player == null || !player.isSneaking()) return;
		
		World world = e.getWorld();
		if(world == null) return;
		
		BlockPos pos = e.getPos();
		IBlockState state = world.getBlockState(pos);
		if(!(state.getBlock() instanceof BlockWorseBarrel)) return;
		
		if(state.getValue(BlockWorseBarrel.ORIENTATION).facing == e.getFace()) {
			e.setUseBlock(Event.Result.ALLOW);
		}
	}
}
