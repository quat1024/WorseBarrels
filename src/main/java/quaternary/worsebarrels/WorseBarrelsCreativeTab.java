package quaternary.worsebarrels;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class WorseBarrelsCreativeTab extends CreativeTabs {
	public static final WorseBarrelsCreativeTab INST = new WorseBarrelsCreativeTab();
	
	public WorseBarrelsCreativeTab() {
		super(WorseBarrels.MODID);
	}
	
	@GameRegistry.ItemStackHolder("worsebarrels:barrel_oak")
	public static final ItemStack icon = null;
	
	@Override
	public ItemStack getTabIconItem() {
		return icon;
	}
}
