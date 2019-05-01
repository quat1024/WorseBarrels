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
import quaternary.worsebarrels.WorseBarrels;
import quaternary.worsebarrels.etc.EnumItemCount;
import quaternary.worsebarrels.tile.TileWorseBarrel;

public class MessageRequestBarrelItem implements IMessage {
	@SuppressWarnings("unused")
	public MessageRequestBarrelItem() {}
	
	public MessageRequestBarrelItem(BlockPos barrelPos, EnumItemCount requestType) {
		this.barrelPos = barrelPos;
		this.requestType = requestType;
	}
	
	private BlockPos barrelPos;
	private EnumItemCount requestType;
	
	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils2.writeBlockPos(buf, barrelPos);
		ByteBufUtils2.writeEnum(buf, requestType);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		barrelPos = ByteBufUtils2.readBlockPos(buf);
		requestType = ByteBufUtils2.readEnum(buf, EnumItemCount.class);
	}
	
	public static class Handler implements IMessageHandler<MessageRequestBarrelItem, IMessage> {
		@Override
		public IMessage onMessage(MessageRequestBarrelItem message, MessageContext ctx) {
			EntityPlayerMP requester = ctx.getServerHandler().player;
			WorldServer ws = requester.getServerWorld();
			ws.addScheduledTask(() -> {
				if(message.requestType == null) {
					Util.naughtyPlayer(ctx, "Tried to request with invalid type");
					return;
				}
				
				//No cheating!
				if(!ws.isBlockLoaded(message.barrelPos)) {
					Util.naughtyPlayer(ctx, "Tried to request from unloaded position");
					return;
				}
				
				IAttributeInstance reachAttr = requester.getAttributeMap().getAttributeInstance(EntityPlayerMP.REACH_DISTANCE);
				double reachDistanceSq = reachAttr.getAttributeValue() * reachAttr.getAttributeValue();
				double barrelDistance = message.barrelPos.distanceSq(requester.getPosition());
				if(reachDistanceSq < barrelDistance + 1) {
					WorseBarrels.LOGGER.info("Received out-of-range barrel request packet from " + ctx.getServerHandler().player.getName(), ", this could be evidence of some sort of cheat mod, a bug on my end... or just lag x)");
					return; //Too far away to actually click the barrel...
				}
				
				TileEntity tile = ws.getTileEntity(message.barrelPos);
				if(!(tile instanceof TileWorseBarrel)) return;
				TileWorseBarrel barrel = (TileWorseBarrel) tile;
				
				IItemHandler handler = barrel.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				if(handler == null) return;
				
				ItemStack extracted;
				
				switch(message.requestType) {
					case ONE:
						extracted = Util.extractItem(handler, 1, false);
						break;
					case STACK:
						ItemStack sampleStack = Util.extractItem(handler, 1, true);
						extracted = Util.extractItem(handler, sampleStack.getMaxStackSize(), false);
						break;
					case ALL:
						extracted = Util.extractItem(handler, Integer.MAX_VALUE, false);
						break;
					default:
						extracted = null; //Impossible
				}
				
				if(!extracted.isEmpty()) {
					ItemHandlerHelper.giveItemToPlayer(requester, extracted, requester.inventory.currentItem);
				}
			});
			
			return null;
		}
	}
}
