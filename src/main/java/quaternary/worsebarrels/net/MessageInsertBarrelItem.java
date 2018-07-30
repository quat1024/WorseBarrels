package quaternary.worsebarrels.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.items.*;
import quaternary.worsebarrels.Util;
import quaternary.worsebarrels.tile.TileWorseBarrel;

public class MessageInsertBarrelItem implements IMessage {
	public MessageInsertBarrelItem() {}
	
	public MessageInsertBarrelItem(BlockPos barrelPos, boolean insertStack) {
		this.barrelPos = barrelPos;
		this.insertStack = insertStack;
	}
	
	BlockPos barrelPos;
	boolean insertStack;
	
	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils2.writeBlockPos(buf, barrelPos);
		buf.writeBoolean(insertStack);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		barrelPos = ByteBufUtils2.readBlockPos(buf);
		insertStack = buf.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<MessageInsertBarrelItem, IMessage> {
		@Override
		public IMessage onMessage(MessageInsertBarrelItem message, MessageContext ctx) {
			EntityPlayerMP inserter = ctx.getServerHandler().player;
			WorldServer ws = inserter.getServerWorld();
			ws.addScheduledTask(() -> {
				//No cheating!
				if(!ws.isBlockLoaded(message.barrelPos)) return;
				
				IAttributeInstance reachAttr = inserter.getAttributeMap().getAttributeInstance(EntityPlayerMP.REACH_DISTANCE);
				double reachDistanceSq = reachAttr.getAttributeValue() * reachAttr.getAttributeValue();
				double barrelDistance = message.barrelPos.distanceSq(inserter.getPosition());
				if(reachDistanceSq < barrelDistance + 1) {
					return; //Too far away to actually click the barrel...
				}
				
				TileEntity tile = ws.getTileEntity(message.barrelPos);
				if(!(tile instanceof TileWorseBarrel)) return;
				TileWorseBarrel barrel = (TileWorseBarrel) tile;
				
				IItemHandler handler = barrel.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				if(handler == null) return;
				
				//This feels sketch af
				ItemStack toInsert = inserter.getHeldItemMainhand().copy();
				if(!message.insertStack) {
					toInsert.setCount(1);
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
