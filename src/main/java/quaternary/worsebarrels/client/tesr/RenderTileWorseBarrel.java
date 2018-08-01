package quaternary.worsebarrels.client.tesr;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import quaternary.worsebarrels.Util;
import quaternary.worsebarrels.block.BlockWorseBarrel;
import quaternary.worsebarrels.block.EnumWorseBarrelOrientation;
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
			EnumWorseBarrelOrientation orientation = barrelState.getValue(BlockWorseBarrel.ORIENTATION);
			EnumFacing barrelFacing = orientation.facing;
			
			Minecraft mc = Minecraft.getMinecraft();
			RenderItem ri = mc.getRenderItem();
			
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + .5, y + .5, z + .5);
			
			//TODO actual up/down rotations
			if(barrelFacing.getHorizontalIndex() != -1) {
				GlStateManager.rotate(-barrelFacing.getHorizontalAngle() - 90, 0, 1, 0);
			} else {
				GlStateManager.rotate(-orientation.secondaryFacing.getHorizontalAngle() - 90, 0, 1, 0);
				GlStateManager.rotate(barrelFacing == EnumFacing.UP ? 90 : -90, 0, 0, 1);
			}
			
			GlStateManager.pushMatrix();
			
			GlStateManager.translate(6 / 16d + .001, 0, 0); //<-- additional .001 to avoid Z fighting
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.scale(.75, .75, .001); //<-- Flatten the item out
			
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
			
			try {
				ri.renderItem(first, ItemCameraTransforms.TransformType.GUI);
			} catch (Exception oof) {}
			
			RenderHelper.disableStandardItemLighting();
			
			
			GlStateManager.popMatrix();
			
			RayTraceResult res = mc.getRenderViewEntity().rayTrace(7, 0);
			if(res != null && res.getBlockPos().equals(te.getPos())) {
				String txt;
				int itemCount = Util.countItemsInHandler(handler);
				if(mc.player.isSneaking()) {
					int maxStackSize = first.getMaxStackSize();
					int stacks = itemCount / maxStackSize;
					int leftover = itemCount % maxStackSize;
					txt = String.format("%sx%s + %s", stacks, maxStackSize, leftover);
				} else {
					txt = String.valueOf(itemCount);
				}
				
				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.translate(6 / 16d + .005, 0, 0);
				GlStateManager.scale(-0.025F, -0.025F, 0.025F);
				if(mc.player.isSneaking()) {
					GlStateManager.scale(.4, .4, .4);
				}
				GlStateManager.translate(0, -4, 0);
				GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(90, 0, 1, 0);
				
				GlStateManager.disableLighting();
				mc.fontRenderer.drawString(txt, -mc.fontRenderer.getStringWidth(txt) / 2, 0, 553648127);
				GlStateManager.enableLighting();
			}
			
			GlStateManager.popMatrix();
		}
	}
}
