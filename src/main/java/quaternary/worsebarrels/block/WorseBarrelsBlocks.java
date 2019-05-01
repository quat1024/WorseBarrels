package quaternary.worsebarrels.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import quaternary.worsebarrels.WorseBarrels;

public final class WorseBarrelsBlocks {
	private WorseBarrelsBlocks() {}
	
	public static final ImmutableList<String> woodVariants = ImmutableList.of("oak", "spruce", "jungle", "acacia", "birch", "darkoak");
	
	public static ImmutableList<BlockWorseBarrel> WOOD_BARRELS = null;
	
	public static void registerBlocks(IForgeRegistry<Block> reg) {
		ImmutableList.Builder<BlockWorseBarrel> bob = new ImmutableList.Builder<>();
		woodVariants.forEach(var -> bob.add(createBlock(new BlockWorseBarrel(Material.WOOD), "barrel_" + var)));
		WOOD_BARRELS = bob.build();
		
		WOOD_BARRELS.forEach(reg::register);
	}
	
	private static <T extends Block> T createBlock(T block, String name) {
		block.setRegistryName(new ResourceLocation(WorseBarrels.MODID, name));
		block.setTranslationKey(WorseBarrels.MODID + '.' + name);
		block.setCreativeTab(WorseBarrels.TAB);
		
		return block;
	}
}
