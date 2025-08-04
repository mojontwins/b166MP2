package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;

public class BlockSponge extends Block {
	public static final byte radius = 3;
	public boolean active;

	protected BlockSponge(int i, boolean isActive) {
		super(i, Material.sponge);
		this.blockIndexInTexture = 48;
		this.active = isActive;
		
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
		this.setTickRandomly(true);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		
		this.clearRadiusMinusOneSection(world, x, y, z);
		//world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate());
	}
	
	@Override
	public void onBlockRemoval(World world, int x, int y, int z) {
		this.tryAndRecoverRadiusSection(world, x, y, z);
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		super.updateTick(world, x, y, z, rand);
		if(!world.isRemote) {
		this.clearRadiusMinusOneSection(world, x, y, z);
			//world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate());
		}
	}
	
	private void clearRadiusMinusOneSection(World world, int x, int y, int z) {
		int mincoverage = radius - 1;
		
		for(int xx = x - mincoverage; xx <= x + mincoverage; ++xx) {
			for(int yy = y - mincoverage; yy <= y + mincoverage; ++yy) {
				for(int zz = z - mincoverage; zz <= z + mincoverage; ++zz) {
					if (world.getBlockMaterial(xx, yy, zz) == Material.water) {
						world.setBlockAndMetadata(xx, yy, zz, 0, 0xf);
					}
				}
			}
		}
	}
	
	private void tryAndRecoverRadiusSection(World world, int x, int y, int z) {
		for(int xx = x - radius; xx <= x + radius; ++xx) {
			for(int yy = y + radius; yy > y - radius; --yy) {
				for(int zz = z - radius; zz <= z  + radius; ++zz) {
					if (!this.spongeNear(world, xx, yy, zz)) {
						int meta = world.getBlockMetadata(xx, yy, zz);
						if(world.getBlockMaterial(xx, yy, zz) == Material.water) {
							world.setBlockWithNotify(xx, yy, zz, Block.waterStill.blockID);
						} else if(meta == 15 && world.getBlockId(xx, yy, zz) == 0) {
							world.setBlockWithNotify(xx, yy, zz, Block.waterStill.blockID);
						}
					}
				}
			}
		}
	}
	
	private boolean spongeNear(World world, int x, int y, int z) {
		int mincoverage = radius - 1;
		for(int x1 = x - mincoverage; x1 <= x + mincoverage; ++x1) {
			for(int y1 = y - mincoverage; y1 <= y + mincoverage; ++y1) {
				for(int z1 = z - mincoverage; z1 <= z + mincoverage; ++z1) {
					if (world.getBlockId(x1, y1, z1) == Block.sponge.blockID) {
						return true;
					}
				}
			}
		}

		return false;
	}

}
