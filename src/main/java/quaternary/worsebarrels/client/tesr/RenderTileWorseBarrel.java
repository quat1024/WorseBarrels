package quaternary.worsebarrels.client.tesr;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import quaternary.worsebarrels.Util;
import quaternary.worsebarrels.block.BlockWorseBarrel;
import quaternary.worsebarrels.tile.TileWorseBarrel;

public class RenderTileWorseBarrel extends TileEntitySpecialRenderer<TileWorseBarrel> {
	@Override
	public void render(TileWorseBarrel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if(te != null) {
			IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			if(handler == null) return;
			ItemStack first = Util.getFirstStackInHandler(handler);
			if(first.isEmpty()) return;
			IBlockState barrelState = te.getWorld().getBlockState(te.getPos());
			if(!(barrelState.getBlock() instanceof BlockWorseBarrel)) return;
			EnumFacing barrelFacing = barrelState.getValue(BlockWorseBarrel.ORIENTATION).facing;
			
			Minecraft mc = Minecraft.getMinecraft();
			RenderItem ri = mc.getRenderItem();
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + .5, y + .5, z + .5);
			
			//TODO actual up/down rotations
			GlStateManager.rotate(-barrelFacing.getHorizontalAngle() - 90, 0, 1, 0);
			GlStateManager.translate(6/16d + .001, 0, 0); //<-- additional .001 to avoid Z fighting
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.scale(.6, .6, .001); //<-- Flatten the item out
			
			RenderHelper.enableStandardItemLighting();
			
			//Following 9 lines lifted from Storage Drawers. Spent ages trying to figure out lighting...
			int ambLight = getWorld().getCombinedLight(te.getPos().offset(barrelFacing), 0);
			int lu = ambLight % 65536;
			int lv = ambLight / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lu / 1f, lv / 1f);
			
			GlStateManager.enableRescaleNormal(); //This hack Storage Drawers uses is crazy!!!
			GlStateManager.disableRescaleNormal();//I guess the purpose is to make the lighting
			GlStateManager.pushAttrib();          //still work when the item is flattened
			GlStateManager.enableRescaleNormal();
			GlStateManager.popAttrib();
			
			ri.renderItem(first, ItemCameraTransforms.TransformType.GUI);
			
			RenderHelper.disableStandardItemLighting();
			GlStateManager.enableLighting();
			
			GlStateManager.popMatrix();
		}
	}
}
