package quaternary.worsebarrels.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class MessageLeftClickBarrel implements IMessage {
	public MessageLeftClickBarrel() {}
	
	public MessageLeftClickBarrel(BlockPos barrelPos, boolean sneaking) {
		this.barrelPos = barrelPos;
		this.sneaking = sneaking;
	}
	
	BlockPos barrelPos;
	boolean sneaking;
	
	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils2.writeBlockPos(buf, barrelPos);
		buf.writeBoolean(sneaking);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		barrelPos = ByteBufUtils2.readBlockPos(buf);
		sneaking = buf.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<MessageLeftClickBarrel, IMessage> {
		@Override
		public IMessage onMessage(MessageLeftClickBarrel message, MessageContext ctx) {
			//TODO handle the client-side config
			
			Minecraft.getMinecraft().addScheduledTask(() -> {
				WorseBarrelsPacketHandler.sendToServer(new MessageRequestBarrelItem(message.barrelPos, message.sneaking));
			});
			
			return null;
		}
	}
}
