package quaternary.worsebarrels.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class MessageInsertBarrelItem implements IMessage {
	public MessageInsertBarrelItem() {}
	
	@Override
	public void toBytes(ByteBuf buf) {
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		
	}
	
	public static class Handler implements IMessageHandler<MessageInsertBarrelItem, IMessage> {
		@Override
		public IMessage onMessage(MessageInsertBarrelItem message, MessageContext ctx) {
			return null;
		}
	}
}
