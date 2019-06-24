package quaternary.worsebarrels.block;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import quaternary.worsebarrels.WorseBarrels;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class WorseBarrelsBlocks {
	private WorseBarrelsBlocks() {}
	
	private static final List<String> COLOR_PREFIXES = Arrays.stream(EnumDyeColor.values()).map(EnumDyeColor::getName).map(s -> s + "_").collect(Collectors.toList());
	
	public static ImmutableList<BlockWorseBarrel> BARREL_BLOCKS = null;
	
	public static void registerBlocks(IForgeRegistry<Block> reg) {
		List<String> woodVariants = ImmutableList.of("oak", "spruce", "jungle", "acacia", "birch", "darkoak");
		List<String> stoneVariants = multiplyColor(Stream.of("concrete", "stained_hardened_clay")).collect(Collectors.toList());
		
		ImmutableList.Builder<BlockWorseBarrel> bob = new ImmutableList.Builder<>();
		
		woodVariants.forEach(var -> bob.add(createBlock(new BlockWorseBarrel(Material.WOOD), "barrel_" + var)));
		stoneVariants.forEach(var -> bob.add(createBlock(new BlockWorseBarrel(Material.ROCK), "barrel_" + var)));
		
		BARREL_BLOCKS = bob.build();
		BARREL_BLOCKS.forEach(reg::register);
	}
	
	private static <T extends Block> T createBlock(T block, String name) {
		block.setRegistryName(new ResourceLocation(WorseBarrels.MODID, name));
		block.setTranslationKey(WorseBarrels.MODID + '.' + name);
		block.setCreativeTab(WorseBarrels.TAB);
		
		return block;
	}
	
	private static Stream<String> multiplyColor(Stream<String> in) {
		return in.flatMap(s -> COLOR_PREFIXES.stream().map(prefix -> prefix + s));
	}
}
