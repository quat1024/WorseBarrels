package quaternary.worsebarrels;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class WorseBarrelsConfig {
	public static int MAX_NESTING_DEPTH;
	
	public static Configuration config;
	
	static void preinit(FMLPreInitializationEvent e) {
		config = new Configuration(e.getSuggestedConfigurationFile(), "1");
		config.load();
		
		readConfig();
	}
	
	private static void readConfig() {
		MAX_NESTING_DEPTH = config.getInt("maxNestingDepth", "balance", 5, 0, 8, "How many layers of nested barrels-inside-barrels are allowed? Set to 0 to disable nesting.");
		
		if(config.hasChanged()) config.save();
	}
	
	@SubscribeEvent
	public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
		if(e.getModID().equals(WorseBarrels.MODID)) {
			readConfig();
		}
	}
}
