package quaternary.worsebarrels.client.tesr;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import quaternary.worsebarrels.Util;
import quaternary.worsebarrels.block.BlockWorseBarrel;
import quaternary.worsebarrels.block.EnumWorseBarrelOrientation;
import quaternary.worsebarrels.tile.BarrelItemHandler;
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
			GlStateManager.color(1, 1, 1); //maybe this fixes the stateleak??
			
			RenderHelper.enableStandardItemLighting();
			
			//Following 9 lines lifted from Storage Drawers. Spent ages trying to figure out lighting...
			int ambLight = getWorld().getCombinedLight(te.getPos().offset(barrelFacing), 0);
			int lu = ambLight % 65536;
			int lv = ambLight / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lu / 1f, lv / 1f);
			
			GlStateManager.enableRescaleNormal();  //This hack Storage Drawers uses is crazy!!!
			GlStateManager.disableRescaleNormal(); //I guess the purpose is to make the lighting
			GlStateManager.pushAttrib();           //still work when the item is flattened
			GlStateManager.enableRescaleNormal();
			GlStateManager.popAttrib();
			
			try {
				ri.renderItem(first, ItemCameraTransforms.TransformType.GUI);
			} catch (Exception oof) {}
			
			RenderHelper.disableStandardItemLighting();
			
			GlStateManager.popMatrix();
			
			boolean showText = false;
			boolean showDetailedText = false;
			
			RayTraceResult res = mc.getRenderViewEntity().rayTrace(7, 0);
			if(res != null && res.getBlockPos().equals(te.getPos())) {
				showText = true;
			}
			
			if(mc.player.isSneaking()) {
				if(showText) showDetailedText = true;
				if(mc.player.getPositionVector().squareDistanceTo(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ()) < 5 * 5) showText = true;
			}
			
			if(showText) {
				String txt;
				int itemCount = Util.countItemsInHandler(handler);
				if(showDetailedText) {
					int maxStackSize = first.getMaxStackSize();
					int stacks = itemCount / maxStackSize;
					int leftover = itemCount % maxStackSize;
					txt = String.format("%sx%s + %s", stacks, maxStackSize, leftover);
				} else {
					txt = String.valueOf(itemCount);
				}
				
				boolean max = first.getMaxStackSize() * BarrelItemHandler.STACK_COUNT == itemCount;
				int col = max ? 0xFF6600 : 0xFFFFFF;
				float scale;
				if(showDetailedText) scale = 1/70f;
				else {
					if(itemCount < 10) {
						scale = 1/15f;
					} else if(itemCount < 100) {
						scale = 1/23f;
					} else scale = 1/30f;
				}
				
				GlStateManager.color(1, 1, 1, 1);
				GlStateManager.translate(6 / 16d + .005, 0, 0);
				GlStateManager.scale(-1, -scale, scale);
				GlStateManager.translate(0, -4, 0);
				GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(90, 0, 1, 0);
				
				GlStateManager.disableLighting();
				//Emulate drawStringWithShadow, since it z-fights when drawn with the depth buffer on
				//and it's not appropriate in this case to disable depth
				int xx = -mc.fontRenderer.getStringWidth(txt) / 2;
				mc.fontRenderer.drawString(txt, xx + 1, 1, (col & 0xFCFCFC) >> 2);
				GlStateManager.translate(0, 0, -0.001);
				mc.fontRenderer.drawString(txt, xx, 0, col);
				GlStateManager.enableLighting();
			}
			
			GlStateManager.popMatrix();
		}
	}
}
