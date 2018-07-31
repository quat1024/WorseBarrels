package quaternary.worsebarrels.tile;

import quaternary.worsebarrels.etc.BarrelItemHandler;

public class BarrelTileItemHandler extends BarrelItemHandler {
	public BarrelTileItemHandler(TileWorseBarrel tile) {
		super();
		this.tile = tile;
	}
	
	TileWorseBarrel tile;
	
	@Override
	protected void onContentsChanged(int slot) {
		tile.markDirty();
	}
}
