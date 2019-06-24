package quaternary.worsebarrels.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import quaternary.worsebarrels.*;
import quaternary.worsebarrels.client.tesr.RenderTileWorseBarrel;
import quaternary.worsebarrels.item.WorseBarrelsItems;
import quaternary.worsebarrels.tile.TileWorseBarrel;

import java.util.Objects;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = WorseBarrels.MODID)
public class ClientEvents {
	@SubscribeEvent
	public static void models(ModelRegistryEvent e) {
		WorseBarrelsItems.BARREL_ITEMS.forEach(i -> {
			ModelResourceLocation mrl = new ModelResourceLocation(Objects.requireNonNull(i.getRegistryName()), "inventory");
			ModelLoader.setCustomModelResourceLocation(i, 0, mrl);
		});
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileWorseBarrel.class, new RenderTileWorseBarrel());
	}
}
