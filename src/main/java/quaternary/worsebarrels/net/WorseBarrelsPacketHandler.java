package quaternary.worsebarrels.net;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import quaternary.worsebarrels.WorseBarrels;

public class WorseBarrelsPacketHandler {
	private static SimpleNetworkWrapper NET;
	
	public static void preinit() {
		NET = new SimpleNetworkWrapper(WorseBarrels.MODID);
		
		NET.registerMessage(MessageRequestBarrelItem.Handler.class, MessageRequestBarrelItem.class, 0, Side.SERVER);
		NET.registerMessage(MessageInsertBarrelItem.Handler.class, MessageInsertBarrelItem.class, 1, Side.SERVER);
		NET.registerMessage(MessageLeftClickBarrel.Handler.class, MessageLeftClickBarrel.class, 2, Side.CLIENT);
	}
	
	public static void sendToServer(IMessage message) {
		NET.sendToServer(message);
	}
	
	public static void sendTo(IMessage message, EntityPlayerMP playerMP) {
		NET.sendTo(message, playerMP);
	}
}
