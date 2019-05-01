package quaternary.worsebarrels.etc;

import net.minecraftforge.fml.client.config.GuiConfig;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;
import quaternary.worsebarrels.WorseBarrels;
import quaternary.worsebarrels.WorseBarrelsConfig;

import java.util.List;
import java.util.stream.Collectors;

public class WorseBarrelsGuiConfig extends GuiConfig {
	public WorseBarrelsGuiConfig(GuiScreen parent) {
		super(parent, getConfigElements(), WorseBarrels.MODID, false, false, WorseBarrels.NAME + " Config!");
	}
	
	//Adapted from Choonster's TestMod3. They say they adapted it from EnderIO "a while back".
	//http://www.minecraftforge.net/forum/topic/39880-110solved-make-config-options-show-up-in-gui/
	private static List<IConfigElement> getConfigElements() {
		Configuration c = WorseBarrelsConfig.config;
		//Don't look!
		return c.getCategoryNames().stream().filter(name -> !c.getCategory(name).isChild()).map(name -> new ConfigElement(c.getCategory(name).setLanguageKey(WorseBarrels.MODID + ".config." + name))).collect(Collectors.toList());
	}
}
