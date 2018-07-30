package quaternary.worsebarrels;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import quaternary.worsebarrels.net.MessageInsertBarrelItem;
import quaternary.worsebarrels.net.MessageRequestBarrelItem;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class WorseBarrelsConfig {
	public static int MAX_NESTING_DEPTH;
	
	public static EnumBarrelAction LEFT_CLICK_ACTION;
	public static EnumBarrelAction SNEAK_LEFT_CLICK_ACTION;
	
	public static EnumBarrelAction RIGHT_CLICK_ACTION;
	public static EnumBarrelAction SNEAK_RIGHT_CLICK_ACTION;
	
	public static Configuration config;
	
	static void preinit(FMLPreInitializationEvent e) {
		config = new Configuration(e.getSuggestedConfigurationFile(), "1");
		config.load();
		
		readConfig();
	}
	
	private static void readConfig() {
		MAX_NESTING_DEPTH = config.getInt("maxNestingDepth", "balance", 5, 0, 8, "How many layers of nested barrels-inside-barrels are allowed? Set to 0 to disable nesting.");
		
		LEFT_CLICK_ACTION = getEnum(config, "leftClickAction", "controls", EnumBarrelAction.INSERT_STACK, "What happens when you left click on a barrel's face?", EnumBarrelAction::describe, EnumBarrelAction.class);
		
		SNEAK_LEFT_CLICK_ACTION = getEnum(config, "sneakLeftClickAction", "controls", EnumBarrelAction.INSERT_ONE, "What happens when you left click on a barrel's face while holding sneak?", EnumBarrelAction::describe, EnumBarrelAction.class);
		
		RIGHT_CLICK_ACTION = getEnum(config, "rightClickAction", "controls", EnumBarrelAction.REQUEST_STACK, "What happens when you right click on a barrel's face?", EnumBarrelAction::describe, EnumBarrelAction.class);
		
		SNEAK_RIGHT_CLICK_ACTION = getEnum(config, "sneakRightClickAction", "controls", EnumBarrelAction.REQUEST_ONE, "What happens when you right click on a barrel's face while holding sneak?", EnumBarrelAction::describe, EnumBarrelAction.class);
		
		config.setCategoryComment("controls", "Interactions with the barrel. These options have no effect on a standalone server.");
		
		if(config.hasChanged()) config.save();
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
			readConfig();
		}
	}
	
	public enum EnumBarrelAction {
		REQUEST_ONE,
		REQUEST_STACK,
		INSERT_ONE,
		INSERT_STACK;
		
		@Override
		public String toString() {
			return super.toString().toLowerCase(Locale.ROOT);
		}
		
		public String describe() {
			switch(this) {
				case REQUEST_ONE: return "Request one item from the barrel.";
				case REQUEST_STACK: return "Request a stack of items from the barrel.";
				case INSERT_ONE: return "Insert one item from your hand into the barrel.";
				case INSERT_STACK: return "Insert a whole stack of items from your hand into the barrel.";
				default: return "asjdlasjldsjldjlkasdjlksd";
			}
		}
		
		public Function<BlockPos, IMessage> getPacket() {
			switch(this) {
				case REQUEST_ONE: return (pos) -> new MessageRequestBarrelItem(pos, false);
				case REQUEST_STACK: return (pos) -> new MessageRequestBarrelItem(pos, true);
				case INSERT_ONE: return (pos) -> new MessageInsertBarrelItem(pos, false);
				case INSERT_STACK: return (pos) -> new MessageInsertBarrelItem(pos, true);
				default: return null;
			}
		}
	}
}
