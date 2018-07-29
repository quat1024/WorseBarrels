package quaternary.worsebarrels.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import quaternary.worsebarrels.WorseBarrels;
import quaternary.worsebarrels.item.WorseBarrelsItems;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = WorseBarrels.MODID)
public class ClientEvents {
	@SubscribeEvent
	public static void models(ModelRegistryEvent e) {
		WorseBarrelsItems.WOOD_BARREL_ITEMS.forEach(i -> {
			ModelResourceLocation mrl = new ModelResourceLocation(i.getRegistryName(), "inventory");
			ModelLoader.setCustomModelResourceLocation(i, 0, mrl);
		});
	}
}
