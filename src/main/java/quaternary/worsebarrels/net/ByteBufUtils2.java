package quaternary.worsebarrels.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

public final class ByteBufUtils2 {
	private ByteBufUtils2() {}
	
	public static void writeBlockPos(ByteBuf buf, BlockPos pos) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
	}
	
	public static BlockPos readBlockPos(ByteBuf buf) {
		return new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
	}
}
