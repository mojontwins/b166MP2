package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.colorizer.ColorizerFoliage;

public class BlockLeavesEx2 extends BlockLeaves implements IBlockWithSubtypes {
	
	public static final int AlderMetadata = 0;
	public static final int AspenMetadata = 1;
	public static final int EucalyptusMetadata = 2;
	public static final int CherryTreeMetadata = 3;
	
	public String[] names = new String[] {
			"Alder", "Aspen", "Eucalyptus", "CherryTree"
	};

	public BlockLeavesEx2(int blockID) {
		super(blockID, 0);
	}

	@Override
	public String getNameFromMeta(int meta) {
		return this.names[meta & 3] + "Leaves";
	}

	@Override
	public int getIndexInTextureFromMeta(int meta) {
		return this.getBlockTextureFromSideAndMetadata(2, meta);
	}

	@Override
	public int getRenderColor(int meta) {
		if ((meta & 3) == 3) return 0xFFFFFF;
		return ColorizerFoliage.getFoliageColor(.5f, .5f);
	}
	
	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		if((world.getBlockMetadata(x, y, z) & 3) == 3) return 0xFFFFFF;
		float t = world.getWorldChunkManager().getTemperatureAt(x, z);
		float h = world.getWorldChunkManager().getRainfallAt(x, z);
		return ColorizerFoliage.getFoliageColor(t, h);
	}

	@Override
	public int idDropped(int i1, Random random2, int i3) {
		return Block.saplingEx2.blockID;
	}
	
	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		return (BlockLeavesBase.graphicsLevel ? 272 + 4 : 288 + 4) + (meta & 3);
	}
}
