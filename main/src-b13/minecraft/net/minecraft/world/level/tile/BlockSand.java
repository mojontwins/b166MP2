package net.minecraft.world.level.tile;

import net.minecraft.world.entity.item.EntityFallingSand;
import net.minecraft.world.level.ISurface;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;

public class BlockSand extends Block implements ISurface, IGround {
	public static boolean fallInstantly = false;

	public BlockSand(int i1, int i2) {
		super(i1, i2, Material.sand);
		
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}
	
	/*
	public void onBlockAdded(World world1, int i2, int i3, int i4) {
		world1.scheduleBlockUpdate(i2, i3, i4, this.blockID, this.tickRate());
	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		world1.scheduleBlockUpdate(i2, i3, i4, this.blockID, this.tickRate());
	}

	public void updateTick(World world1, int i2, int i3, int i4, Random random5) {
		this.tryToFall(world1, i2, i3, i4);
	}

	public int tickRate() {
		return 3;
	}
	*/

	// Non-ticking version
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		this.tryToFall(world, x, y, z);
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
		this.tryToFall(world, x, y, z);
	}
	
	private void tryToFall(World world1, int i2, int i3, int i4) {
		if(canFallBelow(world1, i2, i3 - 1, i4) && i3 >= 0) {
			byte b8 = 32;
			if(!fallInstantly && world1.checkChunksExist(i2 - b8, i3 - b8, i4 - b8, i2 + b8, i3 + b8, i4 + b8)) {
				if(!world1.isRemote) {
					EntityFallingSand entityFallingSand9 = new EntityFallingSand(world1, (double)((float)i2 + 0.5F), (double)((float)i3 + 0.5F), (double)((float)i4 + 0.5F), this.blockID);
					world1.spawnEntityInWorld(entityFallingSand9);
				}
			} else {
				world1.setBlockWithNotify(i2, i3, i4, 0);

				while(canFallBelow(world1, i2, i3 - 1, i4) && i3 > 0) {
					--i3;
				}

				if(i3 > 0) {
					world1.setBlockWithNotify(i2, i3, i4, this.blockID);
				}
			}
		}

	}

	public static boolean canFallBelow(World world0, int i1, int i2, int i3) {
		int i4 = world0.getBlockId(i1, i2, i3);
		if(i4 == 0) {
			return true;
		} else if(i4 == Block.fire.blockID) {
			return true;
		} else {
			Material material5 = Block.blocksList[i4].blockMaterial;
			return material5 == Material.water ? true : material5 == Material.lava;
		}
	}
	
	public boolean canGrowCacti() {
		return true;
	}
}
