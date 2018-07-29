package quaternary.worsebarrels.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.items.*;
import quaternary.worsebarrels.Util;
import quaternary.worsebarrels.tile.TileWorseBarrel;

public class MessageRequestBarrelItem implements IMessage {
	public MessageRequestBarrelItem() {}
	
	public MessageRequestBarrelItem(BlockPos barrelPos, boolean takeStack) {
		this.barrelPos = barrelPos;
		this.takeStack = takeStack;
	}
	
	BlockPos barrelPos;
	boolean takeStack;
	
	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils2.writeBlockPos(buf, barrelPos);
		buf.writeBoolean(takeStack);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		barrelPos = ByteBufUtils2.readBlockPos(buf);
		takeStack = buf.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<MessageRequestBarrelItem, IMessage> {
		@Override
		public IMessage onMessage(MessageRequestBarrelItem message, MessageContext ctx) {
			EntityPlayerMP requester = ctx.getServerHandler().player;
			WorldServer ws = requester.getServerWorld();
			ws.addScheduledTask(() -> {
				//No cheating!
				if(!ws.isBlockLoaded(message.barrelPos)) return;
				
				IAttributeInstance reachAttr = requester.getAttributeMap().getAttributeInstance(EntityPlayerMP.REACH_DISTANCE);
				double reachDistanceSq = reachAttr.getAttributeValue() * reachAttr.getAttributeValue();
				double barrelDistance = message.barrelPos.distanceSq(requester.getPosition());
				if(reachDistanceSq < barrelDistance + 1) {
					return; //They are too far away to actually click the barrel.
				}
				
				TileEntity tile = ws.getTileEntity(message.barrelPos);
				if(!(tile instanceof TileWorseBarrel)) return;
				TileWorseBarrel barrel = (TileWorseBarrel) tile;
				
				IItemHandler handler = barrel.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				if(handler == null) return;
				
				ItemStack extracted;
				if(message.takeStack) {
					ItemStack fakeStack = Util.extractItem(handler, 1, true);
					extracted = Util.extractItem(handler, fakeStack.getMaxStackSize(), false);
				} else {
					extracted = Util.extractItem(handler, 1, false);
				}
				
				ItemHandlerHelper.giveItemToPlayer(requester, extracted, requester.inventory.currentItem);
			});
			
			return null;
		}
	}
}
