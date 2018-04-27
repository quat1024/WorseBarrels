package quaternary.worsebarrels;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = WorseBarrels.MODID, name = WorseBarrels.NAME, version = WorseBarrels.VER)
public class WorseBarrels {
	public static final String MODID = "worsebarrels";
	public static final String NAME = "Worse Barrels";
	public static final String VER = "0.0.0";
	
	public static final List<Block> BLOCKS;
	public static final List<Item> ITEMS;
	
	public static final Logger LOGGER = LogManager.getLogger(NAME);
	
	static String[] woodVariants = new String[]{"oak", };// "spruce", "jungle", "acacia", "birch", "darkoak"};
	
	static {
		BLOCKS = new ArrayList<>();
		
		for(String woodVariant : woodVariants) {
			BLOCKS.add(new BlockWorseBarrel(woodVariant, Material.WOOD));
		}
		
		ITEMS = new ArrayList<>();
		for(Block b : BLOCKS) {
			//meh all the blocks happen to have itemblocks
			ItemBlock ib = new ItemBlock(b);
			ib.setRegistryName(b.getRegistryName());
			ITEMS.add(ib);
		}
	}
	
	@Mod.EventBusSubscriber(modid = MODID)
	public static class CommonEvents {
		@SubscribeEvent
		public static void blocks(RegistryEvent.Register<Block> e) {
			IForgeRegistry<Block> reg = e.getRegistry();
			
			for(Block b : BLOCKS) {
				reg.register(b);
			}
			
			GameRegistry.registerTileEntity(TileWorseBarrel.class, MODID + ":barrel");
		}
		
		@SubscribeEvent
		public static void items(RegistryEvent.Register<Item> e) {
			IForgeRegistry<Item> reg = e.getRegistry();
			reg.registerAll(ITEMS.toArray(new Item[0]));
		}
	}
	
	@Mod.EventBusSubscriber(modid = MODID, value = Side.CLIENT)
	public static class ClientEvents {
		@SubscribeEvent
		public static void models(ModelRegistryEvent e) {
			for(Item i : ITEMS) {
				ModelResourceLocation mrl = new ModelResourceLocation(i.getRegistryName(), "inventory");
				ModelLoader.setCustomModelResourceLocation(i, 0, mrl);
			}
		}
	}
}
