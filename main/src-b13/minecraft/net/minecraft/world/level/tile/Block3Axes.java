package net.minecraft.world.level.tile;

import net.minecraft.world.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;

public abstract class Block3Axes extends Block {

	protected Block3Axes(int blockID, Material material) {
		super(blockID, material);
	}

	public void onBlockPlaced(World world, int x, int y, int z, int face, float xWithinFace, float yWithinFace, float zWithinFace, boolean keyPressed) {
		// Make this compatible with beta wood types:
		int meta = world.getBlockMetadata(x, y, z);
		
		// Make horizontal & oriented?
		if (face > 1 && !keyPressed) {
			if(face == 2 || face == 3) meta |= 8;
			if(face == 4 || face == 5) meta |= 4;
		}
		
		world.setBlockMetadata(x, y, z, meta);
	}
	
	public int getBlockTextureFromSideAndMetadata(int face, int metadata) {
		int orientation = GameRules.boolRule("renderAllBlocksStraight") ? 0 : (metadata & 12); 	
		
		if(
			(orientation == 0 && (face == 1 || face == 0)) ||
			(orientation == 4 && (face == 4 || face == 5)) ||
			(orientation == 8 && (face == 2 || face == 3))
		) {
			return this.getTextureEnds(metadata);
		} else {
			return this.getTextureSides(metadata);
		}
	}
	
	public int getRenderType() {
		return 31;
	}
	
	public int damageDropped(int meta) {
		return meta & 0xf3;
	}
	
	public abstract int getTextureSides(int metadata);
	public abstract int getTextureEnds(int metadata);
}
