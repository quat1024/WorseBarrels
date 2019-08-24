package quaternary.worsebarrels.net;

import com.google.common.collect.Iterators;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
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

import java.util.Iterator;

import static quaternary.worsebarrels.etc.EnumItemCount.ALL;
import static quaternary.worsebarrels.etc.EnumItemCount.ONE;

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
				boolean handlerEmpty = Util.isHandlerEmpty(handler);
				
				EnumHand activeHand = inserter.getHeldItemMainhand().isEmpty() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
				
				//Weird usability edge cases when the barrel is empty:
				if(handlerEmpty) {
					if(message.insertionType == ALL && inserter.getHeldItem(activeHand).isEmpty()) {
						//Avoid an ALL action dumping random things from your inventory into an empty barrel
						return;
					}
					
					ItemStack offhandStack = inserter.getHeldItemOffhand();
					if(activeHand == EnumHand.OFF_HAND && !offhandStack.isEmpty() && WorseBarrelsConfig.OFFHAND_SOFT_BLACKLIST.contains(offhandStack.getItem())) {
						//Avoid offhand-soft-blacklisted items going into an empty barrel if your main hand is empty
						return;
					}
				}
				
				Iterator<ItemStack> stackFinder = Iterators.singletonIterator(inserter.getHeldItem(activeHand));
				
				if(message.insertionType == ALL) {
					stackFinder = Iterators.concat(
						//Active hand,
						stackFinder,
						//inactive hand,
						Iterators.singletonIterator(inserter.getHeldItem(Util.otherHand(activeHand))),
						//the rest.
						inserter.inventory.mainInventory.iterator()
					);
				}
				
				int insertedCount = 0;
				
				while(stackFinder.hasNext()) {
					ItemStack toInsert = stackFinder.next();
					if(toInsert.isEmpty()) continue;
					
					if(WorseBarrelsConfig.ITEM_BLACKLIST.contains(toInsert.getItem())) {
						inserter.sendStatusMessage(new TextComponentTranslation("worsebarrels.cantInsertThis", toInsert.getDisplayName()).setStyle(new Style().setColor(TextFormatting.RED)), true);
						return;
					}
					
					ItemStack copy = toInsert.copy();
					if(message.insertionType == ONE) {
						copy.setCount(1);
					}
					
					int startingCount = copy.getCount();
					ItemStack leftover = ItemHandlerHelper.insertItem(handler, copy, false);
					int difference = startingCount - leftover.getCount();
					
					insertedCount += difference;
					toInsert.shrink(difference); //mutates
				}
				
				if(insertedCount > 150) insertedCount = 150;
				float volume = Util.map(insertedCount, 0, 150, 0.5f, 1f);
				float pitch = Util.map(insertedCount, 0, 150, 0.8f, 1.2f);
				inserter.playSound(SoundEvents.ENTITY_ITEM_PICKUP, volume, pitch);
			});
			
			return null;
		}
	}
}
