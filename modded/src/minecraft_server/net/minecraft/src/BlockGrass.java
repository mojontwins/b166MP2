package net.minecraft.src;

import java.util.Random;

public class BlockGrass extends Block {
	protected BlockGrass(int i1) {
		super(i1, Material.field_28132_b);
		this.blockIndexInTexture = 3;
		this.setTickOnLoad(true);
	}

	public void updateTick(World world1, int i2, int i3, int i4, Random random5) {
		if(!world1.singleplayerWorld) {
			if(world1.getBlockLightValue(i2, i3 + 1, i4) < 4 && Block.lightOpacity[world1.getBlockId(i2, i3 + 1, i4)] > 2) {
				if(random5.nextInt(4) != 0) {
					return;
				}

				world1.setBlockWithNotify(i2, i3, i4, Block.dirt.blockID);
			} else if(world1.getBlockLightValue(i2, i3 + 1, i4) >= 9) {
				int i6 = i2 + random5.nextInt(3) - 1;
				int i7 = i3 + random5.nextInt(5) - 3;
				int i8 = i4 + random5.nextInt(3) - 1;
				int i9 = world1.getBlockId(i6, i7 + 1, i8);
				if(world1.getBlockId(i6, i7, i8) == Block.dirt.blockID && world1.getBlockLightValue(i6, i7 + 1, i8) >= 4 && Block.lightOpacity[i9] <= 2) {
					world1.setBlockWithNotify(i6, i7, i8, Block.grass.blockID);
				}
			}

		}
	}

	public int idDropped(int i1, Random random2) {
		return Block.dirt.idDropped(0, random2);
	}
}
