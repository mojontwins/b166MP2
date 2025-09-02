package net.minecraft.world.level.levelgen.feature.trees;

import java.util.Random;

import net.minecraft.world.level.World;

public class WorldGenPineTree extends WorldGenMojon {

	EnumTreeType tree = EnumTreeType.PINE;
	
	private final int leavesID = tree.leaves.getBlock().blockID;
	private final int leavesMeta = tree.leaves.getMetadata();
	private final int trunkID = tree.wood.getBlock().blockID;
	private final int trunkMeta = tree.wood.getMetadata();
	
	public int height = 6;
	
	public WorldGenPineTree(int height) {
		this(height, false);
	}
	
	public WorldGenPineTree(int height, boolean withNotify) {
		super(withNotify);
		this.height = height;
	}
	
	public void makeBranch(World world, Random rand, int x0, int y0, int z0, int branch, int reduction) {
		// Center
		int reduction2 = reduction + reduction;
		
		this.squareTreeLayer(world, rand, x0, y0, z0, 6 - reduction2, leavesID, leavesMeta);
		
		if((branch & 1) != 0) {
			int offs = rand.nextInt(2); // 0 or 1
			
			// ····xX
			// ·····x
			// ··OO··
			// ··OO··
			// ······
			// ······
			
			this.setBlockIfAirOrLeaves(world, x0 + 2 - reduction, y0 - offs, z0 - 2 + reduction, leavesID, leavesMeta);
			this.setBlockIfAirOrLeaves(world, x0 + 3 - reduction, y0 - offs, z0 - 1 + reduction, leavesID, leavesMeta);
			this.setBlockIfAirOrLeaves(world, x0 + 3 - reduction, y0 - (offs + offs), z0 - 2 + reduction, leavesID, leavesMeta);
			
			// ······
			// ······			
			// ··OO··
			// ··OO··
			// x·····
			// Xx····
			
			offs = rand.nextInt(2); // 0 or 1
			
			this.setBlockIfAirOrLeaves(world, x0 - 2 + reduction, y0 - offs, z0 + 2 - reduction, leavesID, leavesMeta);
			this.setBlockIfAirOrLeaves(world, x0 - 1 + reduction, y0 - offs, z0 + 3 - reduction, leavesID, leavesMeta);
			this.setBlockIfAirOrLeaves(world, x0 - 2 + reduction, y0 - (offs + offs), z0 + 3 - reduction, leavesID, leavesMeta);	
			
		} else {
			int offs = rand.nextInt(2); // 0 or 1
			
			// Xx····
			// x·····
			// ··OO··
			// ··OO··
			// ······
			// ······
			
			this.setBlockIfAirOrLeaves(world, x0 - 1 + reduction, y0 - offs, z0 - 2 + reduction, leavesID, leavesMeta);
			this.setBlockIfAirOrLeaves(world, x0 - 2 + reduction, y0 - offs, z0 - 1 + reduction, leavesID, leavesMeta);
			this.setBlockIfAirOrLeaves(world, x0 - 2 + reduction, y0 - (offs + offs), z0 - 2 + reduction, leavesID, leavesMeta);
			
			// ······
			// ······			
			// ··OO··
			// ··OO··
			// ·····x
			// ····xX
			
			offs = rand.nextInt(2); // 0 or 1
			
			this.setBlockIfAirOrLeaves(world, x0 + 3 - reduction, y0 - offs, z0 + 2 - reduction, leavesID, leavesMeta);
			this.setBlockIfAirOrLeaves(world, x0 + 2 - reduction, y0 - offs, z0 + 3 - reduction, leavesID, leavesMeta);
			this.setBlockIfAirOrLeaves(world, x0 + 3 - reduction, y0 - (offs + offs), z0 + 3 - reduction, leavesID, leavesMeta);
		}
		
		// Deco
		
		for(int i = 0; i < 2; i ++) {
			this.setBlockIfAirOrLeaves(world, x0 - 3 + reduction, y0, z0 - 2 + rand.nextInt(6 - reduction2), leavesID, leavesMeta);
			this.setBlockIfAirOrLeaves(world, x0 + 4 - reduction, y0, z0 - 2 + rand.nextInt(6 - reduction2), leavesID, leavesMeta);
			this.setBlockIfAirOrLeaves(world, x0 - 2 + rand.nextInt(6 - reduction2), y0, z0 - 3 + reduction, leavesID, leavesMeta);
			this.setBlockIfAirOrLeaves(world, x0 - 2 + rand.nextInt(6 - reduction2), y0, z0 + 4 - reduction , leavesID, leavesMeta);
		}
		
		for(int y = y0; y <= y0 + 1; y ++) {
			this.setBlockAndMetadata(world, x0, y, z0, trunkID, trunkMeta);
			this.setBlockAndMetadata(world, x0 + 1, y, z0, trunkID, trunkMeta);
			this.setBlockAndMetadata(world, x0, y, z0 + 1, trunkID, trunkMeta);
			this.setBlockAndMetadata(world, x0 + 1, y, z0 + 1, trunkID, trunkMeta);
		}
		
	}

	@Override
	public boolean generate(World world, Random rand, int x0, int y0, int z0) {
		int branches = 1 + this.height / 2 + this.height / 4;
		this.height -= 4;
		
		// Huge fir version
		if(y0 + this.height > world.getHeight() - 2) return false;
		
		// Check if tree fits / valid soil
		if(!this.validGround(world, x0, y0 - 1, z0)) return false;
		if(!this.validGround(world, x0 + 1, y0 - 1, z0)) return false;
		if(!this.validGround(world, x0, y0 - 1, z0 + 1)) return false;
		if(!this.validGround(world, x0 + 1, y0 - 1, z0 + 1)) return false;
		
		// Trunk 
		
		for(int y = y0; y < this.height + y0; y ++) {
			this.setBlockAndMetadata(world, x0, y, z0, trunkID, trunkMeta);
			this.setBlockAndMetadata(world, x0 + 1, y, z0, trunkID, trunkMeta);
			this.setBlockAndMetadata(world, x0, y, z0 + 1, trunkID, trunkMeta);
			this.setBlockAndMetadata(world, x0 + 1, y, z0 + 1, trunkID, trunkMeta);
		}
		
		// Branches
		
		int y = y0 + this.height;
		for(int branch = 0; branch < branches; branch ++) {
			this.makeBranch(world, rand, x0, y, z0, branch, branch < branches - 1 ? 0 : 1);
			y += 2;
		}
		
		// Top
		this.squareTreeLayer(world, rand, x0, y, z0, 4, leavesID, leavesMeta);
		this.setBlockAndMetadata(world, x0, y, z0, trunkID, trunkMeta);
		this.crossTreeLayer(world, rand, x0, y + 1, z0, 4, leavesID, leavesMeta);
		this.setBlockAndMetadata(world, x0, y + 2, z0, leavesID, leavesMeta);
		this.setBlockAndMetadata(world, x0 + 1, y + 2, z0, leavesID, leavesMeta);
		this.setBlockAndMetadata(world, x0, y + 2, z0 + 1, leavesID, leavesMeta);
		this.setBlockAndMetadata(world, x0 + 1, y + 2, z0 + 1, leavesID, leavesMeta);
		
		return true;

	}

}
