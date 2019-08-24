package quaternary.worsebarrels;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import quaternary.worsebarrels.etc.EnumBarrelAction;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod.EventBusSubscriber
public class WorseBarrelsConfig {
	public static int MAX_NESTING_DEPTH;
	public static int EMPTY_STACK_SIZE;
	public static int FILLED_STACK_SIZE;
	
	public static boolean ALLOW_DISPENSE;
	
	public static Set<Item> ITEM_BLACKLIST;
	public static Set<Item> OFFHAND_SOFT_BLACKLIST;
	
	public static int DOUBLE_CLICK_TIME;
	public static boolean ALLOW_DOUBLE_CLICK_INSERTION;
	public static boolean ALLOW_DOUBLE_CLICK_REQUESTING;
	
	public static EnumBarrelAction LEFT_CLICK_ACTION;
	public static EnumBarrelAction SNEAK_LEFT_CLICK_ACTION;
	public static EnumBarrelAction CTRL_LEFT_CLICK_ACTION;
	
	public static EnumBarrelAction RIGHT_CLICK_ACTION;
	public static EnumBarrelAction SNEAK_RIGHT_CLICK_ACTION;
	public static EnumBarrelAction CTRL_RIGHT_CLICK_ACTION;
	
	public static Configuration config;
	
	static void preinit(FMLPreInitializationEvent e) {
		config = new Configuration(e.getSuggestedConfigurationFile(), "1");
		config.load();
		
		readConfigPreinit();
	}
	
	private static void readConfigPreinit() {
		config.setCategoryComment("balance", "Balancing features!");
		
		MAX_NESTING_DEPTH = config.getInt("maxNestingDepth", "balance", 2, 0, 8, "How many layers of nested barrels-inside-barrels are allowed? Set to 0 to disable nesting.");
		ALLOW_DISPENSE = config.get("balance", "allowDispense", true, "Can you dispense a barrel to place it?").setRequiresMcRestart(true).getBoolean();
		
		boolean bigboisOk = Loader.isModLoaded("stackup");
		
		EMPTY_STACK_SIZE = config.getInt("emptyStackSize", "balance", 64, 1, Integer.MAX_VALUE, "How many empty barrels fit in a single stack?");
		FILLED_STACK_SIZE = config.getInt("filledStackSize", "balance", 8, 1, Integer.MAX_VALUE, "How many non-empty barrels fit in a single stack?");
		
		if(!bigboisOk && (EMPTY_STACK_SIZE > 64 || FILLED_STACK_SIZE > 64)) {
			WorseBarrels.LOGGER.info("****************************************");
			WorseBarrels.LOGGER.info("Barrel stack sizes are set to over 64, but StackUp isn't installed!");
			WorseBarrels.LOGGER.info("This won't actually work ingame, and things will be terribly buggy!");
			WorseBarrels.LOGGER.info("Please consider installing StackUp by asie!");
			WorseBarrels.LOGGER.info("****************************************");
		}
		
		config.setCategoryComment("controls", "Interactions with the barrel. These options have no effect on a standalone server.");
		
		DOUBLE_CLICK_TIME = Math.round(20 * config.getFloat("doubleClickTime", "controls", 0.25f, 0, 2f, "A click counts as a 'double click' if it happens at most this many seconds after another click."));
		ALLOW_DOUBLE_CLICK_INSERTION = config.getBoolean("doubleClickInsert", "controls", true, "Double-clicking will 'upgrade' INSERT_STACK to INSERT_ALL.");
		ALLOW_DOUBLE_CLICK_REQUESTING = config.getBoolean("doubleClickRequest", "controls", false, "Double-clicking will 'upgrade' REQUEST_STACK to REQUEST_ALL.");
		
		LEFT_CLICK_ACTION = getControl(config, "leftClickAction", EnumBarrelAction.INSERT_ONE, "What happens when you left click on a barrel's face?");
		SNEAK_LEFT_CLICK_ACTION = getControl(config, "sneakLeftClickAction", EnumBarrelAction.INSERT_STACK, "What happens when you left click on a barrel's face while holding sneak?");
		CTRL_LEFT_CLICK_ACTION = getControl(config, "ctrlLeftClickAction", EnumBarrelAction.NOTHING, "What happens when you left click on a barrel's face while holding Control?");
		
		RIGHT_CLICK_ACTION = getControl(config, "rightClickAction", EnumBarrelAction.REQUEST_ONE, "What happens when you right click on a barrel's face?");
		SNEAK_RIGHT_CLICK_ACTION = getControl(config, "sneakRightClickAction", EnumBarrelAction.REQUEST_STACK, "What happens when you right click on a barrel's face while holding sneak?");
		CTRL_RIGHT_CLICK_ACTION = getControl(config, "ctrlRightClickAction", EnumBarrelAction.NOTHING, "What happens when you right click on a barrel's face while holding Control?");
		
		//don't save the config yet because readConfigInit will be called soon
	}
	
	private static EnumBarrelAction getControl(Configuration config, String name, EnumBarrelAction defaultBehavior, String description) {
		return getEnum(config, name, "controls", defaultBehavior, description, EnumBarrelAction::describe, EnumBarrelAction.class);
	}
	
	//Depends on what items are registered, so has to come later.
	public static void readConfigInit() {
		ITEM_BLACKLIST = getRegistrySet(config, "itemBlacklist", "balance", "Item IDs that are not allowed to go in barrels. One per line, please, of the form 'modid:name'", Collections.emptySet(), ForgeRegistries.ITEMS);
		
		OFFHAND_SOFT_BLACKLIST = getRegistrySet(config, "offhandSoftBlacklist", "behavior", "Items that you cannot fill an empty barrel with from your offhand. (This prevents errant clicks from yoinking your shield, for example.)", Collections.singleton(Items.SHIELD), ForgeRegistries.ITEMS);
		
		if(config.hasChanged()) config.save();
	}
	
	private static <T extends IForgeRegistryEntry<T>> Set<T> getRegistrySet(Configuration config, String name, String category, String description, Collection<T> defaultValues, IForgeRegistry<T> registry) {
		return Arrays.stream(
			config.getStringList(name, category, defaultValues.stream().map(IForgeRegistryEntry::getRegistryName).map(ResourceLocation::toString).toArray(String[]::new), description)
		)
			.map(ResourceLocation::new)
			.flatMap(res -> {
				if(registry.containsKey(res)) return Stream.of(res);
				else {
					WorseBarrels.LOGGER.warn("Can't find any " + res + " when reading option " + name);
					return Stream.empty();
				}
			})
			.map(registry::getValue)
			.collect(Collectors.toSet());
	}
	
	private static <T extends Enum> T getEnum(Configuration config, String configKey, String configCategory, T defaultValue, String comment_, Function<T, String> describerFunction, Class<T> enumClass) {
		List<T> enumValues = Arrays.asList(enumClass.getEnumConstants());
		
		Map<String, T> stringToEnumMap = enumValues.stream().collect(Collectors.toMap(Enum::toString, t -> t));
		String[] enumValueStringArray = stringToEnumMap.keySet().toArray(new String[0]);
		
		List<String> enumComments = enumValues.stream().map(describerFunction).collect(Collectors.toList());
		
		StringBuilder commentBuilder = new StringBuilder();
		commentBuilder.append(comment_);
		commentBuilder.append('\n');
		for(int i = 0; i < enumComments.size(); i++) {
			commentBuilder.append(enumValues.get(i).toString());
			commentBuilder.append(": ");
			commentBuilder.append(enumComments.get(i));
			commentBuilder.append('\n');
		}
		
		String comment = commentBuilder.toString();
		
		String configString = config.getString(configKey, configCategory, defaultValue.toString(), comment, enumValueStringArray);
		
		if(stringToEnumMap.containsKey(configString)) {
			return stringToEnumMap.get(configString);
		} else {
			
			String message = String.format("\"%s\" is not a valid option for config value \"%s\"! Please choose one of %s.", configString, configCategory + "." + configKey, Arrays.toString(enumValueStringArray));
			throw new IllegalArgumentException(message);
		}
	}
	
	@SubscribeEvent
	public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
		if(e.getModID().equals(WorseBarrels.MODID)) {
			readConfigPreinit();
			readConfigInit();
		}
	}
	
}
