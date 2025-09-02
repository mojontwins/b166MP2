package net.minecraft.world.level.levelgen.feature.trees;

import java.util.Random;

import net.minecraft.world.Direction;
import net.minecraft.world.level.BlockPos;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;

public class WorldGenAcacia extends WorldGenMojon {

	EnumTreeType tree = EnumTreeType.ACACIA;
	
	private final int leavesID = tree.leaves.getBlock().blockID;
	private final int leavesMeta = tree.leaves.getMetadata();
	private final int trunkID = tree.wood.getBlock().blockID;
	private final int trunkMeta = tree.wood.getMetadata();

	public WorldGenAcacia(boolean withNotify) {
		super(withNotify);
	}

	@Override
	public boolean generate(World world, Random rand, int x0, int y0, int z0) {
		int height = rand.nextInt(3);
		
		// Ground check / fits
		
		if(!this.validGround(world, x0, y0 - 1, z0) ||
				y0 + height > world.getHeight() - 1) {
			return false;
		}
		
		for(int y = y0; y <= y0 + 1 + height; y ++) {
			int radius = (y == y0) ? 0 : (y == y0 + 1 + height) ? 3 : 1;
			
			for(int x = x0 - radius; x <= x0 + radius; x ++) {
				for(int z = z0 - radius; z <= z0 + radius; z ++) {
					if(world.isBlockOpaqueCube(x, y, z) && 
							world.getBlockMaterial(x, y, z) != Material.leaves) {
						return false;
					}
				}
			}
		}
		
		// Main truck is tilt a random angle 0-PI/2 to any 
		// of the cardinal directions
		
		BlockPos src = new BlockPos().set(x0, y0, z0);
		double tilt = rand.nextDouble() * Math.PI / 5;
		double angle = rand.nextInt(4) * Math.PI / 2; 
		BlockPos dst = this.findEndPoint(src, height + 3, angle, tilt);

		this.bresenham(world, src, dst, trunkID, trunkMeta);
		
		// Use the same angle/tilt to find the starting point of the main branch
		
		BlockPos branchsrc = this.findEndPoint(src, height + 2, angle, tilt);
		
		// pick a different angle
		
		double branchAngle;
		do { branchAngle = rand.nextInt(4) * Math.PI / 2; } while (angle == branchAngle);
	
		BlockPos branchdst = this.findEndPoint(branchsrc, 3 + rand.nextInt(3), branchAngle, Math.PI / 3);
		
		this.bresenham(world, branchsrc, branchdst, trunkID, trunkMeta);
		this.circle(world, branchdst, 4 - rand.nextInt(2), leavesID, leavesMeta);
		this.circle(world, branchdst.move(Direction.UP), 2, leavesID, leavesMeta);
	
		// Secondary branch
		
		branchsrc = dst.copy();
		
		// pich a different angle
		
		angle = branchAngle;
		do { branchAngle = rand.nextInt(4) * Math.PI / 4; } while (angle == branchAngle);
		
		branchdst = this.findEndPoint(branchsrc, 2 + rand.nextInt(3), branchAngle, Math.PI / 4 + rand.nextDouble() * .3D);
		
		this.bresenham(world, branchsrc, branchdst, trunkID, trunkMeta);
		this.circle(world, branchdst, 4 - rand.nextInt(2), leavesID, leavesMeta);
		this.circle(world, branchdst.move(Direction.UP), 2, leavesID, leavesMeta);
		
		return true;
	}


}
