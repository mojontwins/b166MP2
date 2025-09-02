package net.minecraft.world.level.levelgen.feature.trees;

import java.util.Random;

import net.minecraft.world.level.World;

public class WorldGenBaobab extends WorldGenMojon {

	EnumTreeType tree = EnumTreeType.BAOBAB;
	
	private final int leavesID = tree.leaves.getBlock().blockID;
	private final int leavesMeta = tree.leaves.getMetadata();
	private final int trunkID = tree.wood.getBlock().blockID;
	private final int trunkMeta = tree.wood.getMetadata();
	
	public int height = 6;
	
	public WorldGenBaobab(int height) {
		this(height, false);
	}
	
	public WorldGenBaobab(int height, boolean withNotify) {
		super(withNotify);
		this.height = height;
	}

	public void leavesCluster(World world, Random rand, int x, int y, int z) {
		this.setBlockIfEmpty(world, x, y, z, leavesID, leavesMeta);
		
		if(rand.nextBoolean()) {
			this.setBlockIfEmpty(world, x - 1, y, z, leavesID, leavesMeta);
			this.setBlockIfEmpty(world, x + 1, y, z, leavesID, leavesMeta);
			this.setBlockIfEmpty(world, x, y, z - 1, leavesID, leavesMeta);
			this.setBlockIfEmpty(world, x, y, z + 1, leavesID, leavesMeta);
		} else {
			this.setBlockIfEmpty(world, x - 1, y, z - 1, leavesID, leavesMeta);
			this.setBlockIfEmpty(world, x + 1, y, z - 1, leavesID, leavesMeta);
			this.setBlockIfEmpty(world, x - 1, y, z + 1, leavesID, leavesMeta);
			this.setBlockIfEmpty(world, x + 1, y, z + 1, leavesID, leavesMeta);
		}
	}
	
	@Override
	public boolean generate(World world, Random rand, int x0, int y0, int z0) {
		if(y0 + this.height > world.getHeight() - 2) return false;
		
		int width = 2 + rand.nextInt(4);
		if(width > 3) width = 3;
		
		int x1 = x0 - 1;
		int x2 = x0 + width - 2;
		int z1 = z0 - 1;
		int z2 = z0 + width - 2;
		
		// Check if we can grow a baobab here
		
		for(int x = x1; x <= x2; x ++) {
			for(int z = z1; z <= z2; z ++) {
				if(!this.validGround(world, x, y0 - 1, z)) {
					return false;
				}
				for(int y = y0 + 1; y < y0 + this.height; y ++) {
					if(world.isBlockOpaqueCube(x, y, z)) {
						return false;
					}
				}
			}
		}
		
		// Make a thick trunk
		
		for(int x = x1; x <= x2; x ++) {
			for(int z = z1; z <= z2; z ++) {
				for(int y = y0; y < y0 + this.height; y ++) {
					this.setBlockAndMetadata(world, x, y, z, trunkID, trunkMeta);
				}
			}
		}
		
		// Make some roots
		
		if(rand.nextBoolean()) {
			this.setBlockAndMetadata(world, x1 - 1, y0, z1 + rand.nextInt(width), trunkID, trunkMeta);
		}
		
		if(rand.nextBoolean()) {
			this.setBlockAndMetadata(world, x2 + 1, y0, z1 + rand.nextInt(width), trunkID, trunkMeta);
		}
		
		if(rand.nextBoolean()) {
			this.setBlockAndMetadata(world, x1 + rand.nextInt(width), y0, z1 - 1, trunkID, trunkMeta);
		}
		
		if(rand.nextBoolean()) {
			this.setBlockAndMetadata(world, x1 + rand.nextInt(width), y0, z2 + 1, trunkID, trunkMeta);
		}
		
		// Make principal branches
		// X (West - East)
		
		int srcX = x1 - 1;
		// Remove the random part if you want only short branches
		int dstX = x1 - 3 - (rand.nextInt(6) == 0 ? 1 : 0);
		
		int srcY = y0 + this.height;
		int dstY = y0 + this.height + 1 + (rand.nextInt(3) == 0 ? 1 : 0);
		
		int srcZ = z1 + rand.nextInt(width);
		int dstZ = srcZ + rand.nextInt(3) - 1;
		
		this.bresenham(world, 
				srcX, srcY, srcZ,
				dstX, dstY, dstZ,
				trunkID, trunkMeta);
		this.leavesCluster(world, rand, dstX, dstY + 1, dstZ);
		
		srcX = x2 + 1;
		// Remove the random part if you want only short branches
		dstX = x2 + 3 + (rand.nextInt(4) == 0 ? 1 : 0);
		
		dstY = y0 + this.height + 1 + (rand.nextInt(3) == 0 ? 1 : 0);
		
		srcZ = z1 + rand.nextInt(width);
		dstZ = srcZ + rand.nextInt(3) - 1;
		
		this.bresenham(world, 
				srcX, srcY, srcZ,
				dstX, dstY, dstZ,
				trunkID, trunkMeta);
		this.leavesCluster(world, rand, dstX, dstY + 1, dstZ);
		
		// Z (South - North)
		
		srcZ = z1 - 1;
		// Remove the random part if you want only short branches
		dstZ = z1 - 3 - (rand.nextInt(4) == 0 ? 1 : 0);
		
		dstY = y0 + this.height + 1 + (rand.nextInt(3) == 0 ? 1 : 0);
		
		srcX = x1 + rand.nextInt(width);
		dstX = srcX + rand.nextInt(3) - 1;
		
		this.bresenham(world, 
				srcX, srcY, srcZ,
				dstX, dstY, dstZ,
				trunkID, trunkMeta);
		this.leavesCluster(world, rand, dstX, dstY + 1, dstZ);
		
		srcZ = z2 + 1;
		// Remove the random part if you want only short branches
		dstZ = z2 + 3 + (rand.nextInt(4) == 0 ? 1 : 0);
		
		dstY = y0 + this.height + 1 + (rand.nextInt(3) == 0 ? 1 : 0);
		
		srcX = x1 + rand.nextInt(width);
		dstX = srcX + rand.nextInt(3) - 1;
		
		this.bresenham(world, 
				srcX, srcY, srcZ,
				dstX, dstY, dstZ,
				trunkID, trunkMeta);
		this.leavesCluster(world, rand, dstX, dstY + 1, dstZ);
		
		// Secondary branches
		
		// X axis
		
		dstY = y0 + this.height + 2 + (rand.nextInt(4) == 0 ? 1 : 0);
		if(rand.nextBoolean()) {
			srcX = x1;
			dstX = x1 - 1;
		} else {
			srcX = x2;
			dstX = x2 + 1;
		}
		srcZ = dstZ = z1 + rand.nextInt(width);
		
		this.bresenham(world, 
				srcX, srcY, srcZ,
				dstX, dstY, dstZ,
				trunkID, trunkMeta);
		this.leavesCluster(world, rand, dstX, dstY + 1, dstZ);
		
		// Z axis
		
		dstY = y0 + this.height + 2 + (rand.nextInt(4) == 0 ? 1 : 0);
		if(rand.nextBoolean()) {
			srcZ = z1;
			dstZ = z1 - 1;
		} else {
			srcZ = z2;
			dstZ = z2 + 1;
		}
		srcX = dstX = x1 + rand.nextInt(width);
		
		this.bresenham(world, 
				srcX, srcY, srcZ,
				dstX, dstY, dstZ,
				trunkID, trunkMeta);
		this.leavesCluster(world, rand, dstX, dstY + 1, dstZ);
		
		return true;
	}

}
