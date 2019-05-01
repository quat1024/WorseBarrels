package quaternary.worsebarrels;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quaternary.worsebarrels.block.WorseBarrelsBlocks;
import quaternary.worsebarrels.etc.BarrelDispenserBehavior;
import quaternary.worsebarrels.item.WorseBarrelsItems;
import quaternary.worsebarrels.net.WorseBarrelsPacketHandler;
import quaternary.worsebarrels.proxy.ServerProxy;
import quaternary.worsebarrels.tile.TileWorseBarrel;

@Mod(modid = WorseBarrels.MODID, name = WorseBarrels.NAME, version = WorseBarrels.VER, guiFactory = "quaternary.worsebarrels.etc.WorseBarrelsGuiFactory")
@Mod.EventBusSubscriber(modid = WorseBarrels.MODID)
public class WorseBarrels {
	public static final String MODID = "worsebarrels";
	public static final String NAME = "Worse Barrels";
	public static final String VER = "1.1.0";
	
	public static final Logger LOGGER = LogManager.getLogger(NAME);
	
	@GameRegistry.ItemStackHolder("worsebarrels:barrel_oak")
	public static final ItemStack ICON = ItemStack.EMPTY;
	
	public static final CreativeTabs TAB = new CreativeTabs(MODID) {
		@SideOnly(Side.CLIENT)
		@Override
		public ItemStack createIcon() {
			return ICON;
		}
	};
	
	@SidedProxy(serverSide = "quaternary.worsebarrels.proxy.ServerProxy", clientSide = "quaternary.worsebarrels.proxy.ClientProxy")
	public static ServerProxy PROXY;
	
	@Mod.EventHandler
	public static void preinit(FMLPreInitializationEvent e) {
		WorseBarrelsConfig.preinit(e);
		WorseBarrelsPacketHandler.preinit();
	}
	
	@SubscribeEvent
	public static void blocks(RegistryEvent.Register<Block> e) {
		WorseBarrelsBlocks.registerBlocks(e.getRegistry());
		
		GameRegistry.registerTileEntity(TileWorseBarrel.class, new ResourceLocation(MODID, "barrel"));
	}
	
	@SubscribeEvent
	public static void items(RegistryEvent.Register<Item> e) {
		WorseBarrelsItems.registerItems(e.getRegistry());
	}
	
	@Mod.EventHandler
	public static void init(FMLInitializationEvent e) {
		WorseBarrelsConfig.readConfigInit();
		
		if(WorseBarrelsConfig.ALLOW_DISPENSE) {
			BarrelDispenserBehavior dispensey = new BarrelDispenserBehavior();
			
			WorseBarrelsItems.WOOD_BARREL_ITEMS.forEach(i -> {
				BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(i, dispensey);
			});
		}
	}
}
