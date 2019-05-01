package quaternary.worsebarrels.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.items.*;
import quaternary.worsebarrels.Util;
import quaternary.worsebarrels.WorseBarrels;
import quaternary.worsebarrels.WorseBarrelsConfig;
import quaternary.worsebarrels.etc.EnumItemCount;
import quaternary.worsebarrels.tile.TileWorseBarrel;

public class MessageInsertBarrelItem implements IMessage {
	@SuppressWarnings("unused")
	public MessageInsertBarrelItem() {}
	
	public MessageInsertBarrelItem(BlockPos barrelPos, EnumItemCount insertionType) {
		this.barrelPos = barrelPos;
		this.insertionType = insertionType;
	}
	
	private BlockPos barrelPos;
	private EnumItemCount insertionType;
	
	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils2.writeBlockPos(buf, barrelPos);
		ByteBufUtils2.writeEnum(buf, insertionType);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		barrelPos = ByteBufUtils2.readBlockPos(buf);
		insertionType = ByteBufUtils2.readEnum(buf, EnumItemCount.class);
	}
	
	public static class Handler implements IMessageHandler<MessageInsertBarrelItem, IMessage> {
		@Override
		public IMessage onMessage(MessageInsertBarrelItem message, MessageContext ctx) {
			EntityPlayerMP inserter = ctx.getServerHandler().player;
			WorldServer ws = inserter.getServerWorld();
			ws.addScheduledTask(() -> {
				if(message.insertionType == null) {
					Util.naughtyPlayer(ctx, "Tried to insert with invalid type");
					return;
				}
				
				//No cheating!
				if(!ws.isBlockLoaded(message.barrelPos)) {
					Util.naughtyPlayer(ctx, "Tried to insert to unloaded barrel");
					return;
				}
				
				IAttributeInstance reachAttr = inserter.getAttributeMap().getAttributeInstance(EntityPlayerMP.REACH_DISTANCE);
				double reachDistanceSq = reachAttr.getAttributeValue() * reachAttr.getAttributeValue();
				double barrelDistance = message.barrelPos.distanceSq(inserter.getPosition());
				if(reachDistanceSq < barrelDistance + 1) {
					WorseBarrels.LOGGER.info("Received out-of-range barrel insertion packet from " + ctx.getServerHandler().player.getName(), ", this could be evidence of some sort of cheat mod, a bug on my end... or just lag x)");
					return; //Too far away to actually click the barrel...
				}
				
				TileEntity tile = ws.getTileEntity(message.barrelPos);
				if(!(tile instanceof TileWorseBarrel)) return;
				TileWorseBarrel barrel = (TileWorseBarrel) tile;
				
				IItemHandler handler = barrel.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				if(handler == null) return;
				
				//This feels sketch af
				
				if(message.insertionType == EnumItemCount.ALL) {
					//TODO No-op for now while I figure this out :/
					//This is tricky since I have to loop over the whole inventory + not dupe items all over the place
					return;
				}
				
				ItemStack toInsert = inserter.getHeldItemMainhand().copy();
				//(this covers the STACK case)
				if(message.insertionType == EnumItemCount.ONE) {
					toInsert.setCount(1);
				}
				
				if(WorseBarrelsConfig.ITEM_BLACKLIST.contains(toInsert.getItem())) {
					inserter.sendStatusMessage(new TextComponentTranslation("worsebarrels.cantInsertThis", toInsert.getDisplayName()).setStyle(new Style().setColor(TextFormatting.RED)), true);
					return;
				}
				
				int startingCount = toInsert.getCount();
				ItemStack leftoverAfterInsertion = ItemHandlerHelper.insertItem(handler, toInsert, false);
				int endingCount = leftoverAfterInsertion.getCount();
				
				inserter.getHeldItemMainhand().shrink(startingCount - endingCount);
			});
			
			return null;
		}
	}
}
