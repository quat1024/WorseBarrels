package quaternary.worsebarrels.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import quaternary.worsebarrels.CommonEvents;
import quaternary.worsebarrels.WorseBarrels;
import quaternary.worsebarrels.item.WorseBarrelsItems;
import quaternary.worsebarrels.net.*;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = WorseBarrels.MODID)
public class ClientEvents {
	@SubscribeEvent
	public static void models(ModelRegistryEvent e) {
		WorseBarrelsItems.WOOD_BARREL_ITEMS.forEach(i -> {
			ModelResourceLocation mrl = new ModelResourceLocation(i.getRegistryName(), "inventory");
			ModelLoader.setCustomModelResourceLocation(i, 0, mrl);
		});
	}
	
	@SubscribeEvent
	public static void rightClick(PlayerInteractEvent.RightClickBlock e) {
		if(FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		if(e.getHand() == EnumHand.OFF_HAND) return; //TODO Proper offhand support
		
		CommonEvents.handleClick(e.getWorld(), e.getPos(), e.getEntityPlayer(), e.getFace(), () -> {
			WorseBarrelsPacketHandler.sendToServer(new MessageInsertBarrelItem(e.getPos(), e.getEntityPlayer().isSneaking()));
			e.getEntityPlayer().swingArm(EnumHand.MAIN_HAND);
			e.setUseItem(Event.Result.DENY);
		});
	}
}
