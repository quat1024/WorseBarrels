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
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import quaternary.worsebarrels.*;
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
		if(e.getHand() != EnumHand.MAIN_HAND) return; //TODO proper offhand support
		
		CommonEvents.handleClick(e.getWorld(), e.getPos(), e.getFace(), () -> {
			if(FMLCommonHandler.instance().getEffectiveSide().isClient()) {
				IMessage message;
				
				if(e.getEntityPlayer().isSneaking()) {
					message = WorseBarrelsConfig.SNEAK_RIGHT_CLICK_ACTION.getPacket().apply(e.getPos());
				} else {
					message = WorseBarrelsConfig.RIGHT_CLICK_ACTION.getPacket().apply(e.getPos());
				}
				
				WorseBarrelsPacketHandler.sendToServer(message);
			}
			
			e.getEntityPlayer().swingArm(EnumHand.MAIN_HAND);
			e.setUseItem(Event.Result.DENY);
		});
	}
}
