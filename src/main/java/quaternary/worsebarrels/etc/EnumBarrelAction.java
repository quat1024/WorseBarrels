package quaternary.worsebarrels.etc;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import quaternary.worsebarrels.WorseBarrelsConfig;
import quaternary.worsebarrels.net.MessageInsertBarrelItem;
import quaternary.worsebarrels.net.MessageRequestBarrelItem;

import javax.annotation.Nullable;
import java.util.Locale;

public enum EnumBarrelAction {
	REQUEST_ONE,
	REQUEST_STACK,
	REQUEST_ALL,
	INSERT_ONE,
	INSERT_STACK,
	INSERT_ALL,
	NOTHING;
	
	@Override
	public String toString() {
		return super.toString().toLowerCase(Locale.ROOT);
	}
	
	public String describe() {
		switch(this) {
			case REQUEST_ONE: return "Request one item from the barrel.";
			case REQUEST_STACK: return "Request a stack of items from the barrel.";
			case REQUEST_ALL: return "Request all of the items from the barrel.";
			case INSERT_ONE: return "Insert one item from your hand into the barrel.";
			case INSERT_STACK: return "Insert a stack of items from your hand into the barrel.";
			case INSERT_ALL: return "Insert all matching items from your inventory into the barrel.";
			case NOTHING: return "Does nothing.";
			default: throw new IllegalStateException(this.toString());
		}
	}
	
	@Nullable
	public IMessage getPacket(BlockPos pos) {
		switch(this) {
			case REQUEST_ONE: return new MessageRequestBarrelItem(pos, EnumItemCount.ONE);
			case REQUEST_STACK: return new MessageRequestBarrelItem(pos, EnumItemCount.STACK);
			case REQUEST_ALL: return new MessageRequestBarrelItem(pos, EnumItemCount.ALL);
			case INSERT_ONE: return new MessageInsertBarrelItem(pos, EnumItemCount.ONE);
			case INSERT_STACK: return new MessageInsertBarrelItem(pos, EnumItemCount.STACK);
			case INSERT_ALL: return new MessageInsertBarrelItem(pos, EnumItemCount.ALL);
			case NOTHING: return null;
			default: throw new IllegalStateException(this.toString());
		}
	}
	
	public EnumBarrelAction doubleClickUpgrade() {
		if(this == REQUEST_STACK && WorseBarrelsConfig.ALLOW_DOUBLE_CLICK_REQUESTING) {
			return REQUEST_ALL;
		} else if(this == INSERT_STACK && WorseBarrelsConfig.ALLOW_DOUBLE_CLICK_INSERTION) {
			return INSERT_ALL;
		} else return this;
	}
}
