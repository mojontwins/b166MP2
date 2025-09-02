package net.minecraft.world.level.levelgen.feature.trees;

import java.util.Random;

import net.minecraft.world.level.World;

public class WorldGenAlder extends WorldGenMojon {
	
	// Original code by smez1234
	
	EnumTreeType tree = EnumTreeType.ALDER;
	
	private final int leavesID = tree.leaves.getBlock().blockID;
	private final int leavesMeta = tree.leaves.getMetadata();
	private final int trunkID = tree.wood.getBlock().blockID;
	private final int trunkMeta = tree.wood.getMetadata();
	
	int height;
	int span;
	int branches;

	public WorldGenAlder(int height, int span, int branches) {
		super(false);
		this.height = height;
		this.span = span;
		this.branches = branches;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		if(!this.validGround(world, x, y - 1, z)) return false;
		
		for(int i = 0; i < span; i++) {
			for(int k = -span; k < span + 1; k++) {
				for(int j = -span; j < span + 1; j++) {
					if(!world.isAirBlock(x + k, y + height + i, z + j)) {
						return false;
					}
				}
			}
		}

		for(int p = -2; p < 3; p++) {
			for(int r = -1; r < 2; r++) {
				for(int q = -1; q < 2; q++) {
					setBlockIfEmpty(x + p, y + height + span + 1 + q, z + r, this.leavesID, this.leavesMeta, 3, world);
					setBlockIfEmpty(x + r, y + height + span + 1 + p, z + q, this.leavesID, this.leavesMeta, 3, world);
					setBlockIfEmpty(x + q, y + height + span + 1 + r, z + p, this.leavesID, this.leavesMeta, 3, world);
				}
			}
		}

		//Layer 1
		for(int a = 0; a < branches; a++) {
			int disX = (rand.nextInt((span * 2) + 1)) - span;
			int disY = rand.nextInt(2);
			int disZ = (rand.nextInt((span * 2) + 1)) - span;

			int posX = x + disX;
			int posY = y + height - 2 + disY;
			int posZ = z + disZ;

			for(int p = -2; p < 3; p++) {
				for(int r = -1; r < 2; r++) {
					for(int q = -1; q < 2; q++) {
						setBlockIfEmpty(posX + p, posY + q, posZ + r, this.leavesID, this.leavesMeta, 3, world);
						setBlockIfEmpty(posX + r, posY + p, posZ + q, this.leavesID, this.leavesMeta, 3, world);
						setBlockIfEmpty(posX + q, posY + r, posZ + p, this.leavesID, this.leavesMeta, 3, world);
					}
				}
			}

			int meta = this.trunkMeta;
			if(disX > disZ) meta |= 4;
			else if(disZ > disX) meta |= 8;
			
			for(int b = 0; b < span; b++) {
				int xx = disX * (b + 1) / span;
				int yy = disY * (b + 1) / span;
				int zz = disZ * (b + 1) / span;
				
				this.setBlockAndMetadata(world, xx + x, y + height - 2 + yy, z + zz, this.trunkID, meta);
			}

			this.setBlockAndMetadata(world, posX, posY, posZ, this.trunkID, this.trunkMeta);
		}

		//Layer 2
		for(int a = 0; a < branches; a++) {
			int disX = (rand.nextInt(((span - 2) * 2) + 1)) - (span - 2);
			int disY = rand.nextInt(2);
			int disZ = (rand.nextInt(((span - 2) * 2) + 1)) - (span - 2);

			int posX = x + disX;
			int posY = y + height + 2 + disY;
			int posZ = z + disZ;

			for(int p = -2; p < 3; p++) {
				for(int r = -1; r < 2; r++) {
					for(int q = -1; q < 2; q++) {
						setBlockIfEmpty(posX + p, posY + q, posZ + r, this.leavesID, this.leavesMeta, 3, world);
						setBlockIfEmpty(posX + r, posY + p, posZ + q, this.leavesID, this.leavesMeta, 3, world);
						setBlockIfEmpty(posX + q, posY + r, posZ + p, this.leavesID, this.leavesMeta, 3, world);
					}
				}
			}

			int meta = this.trunkMeta;
			if(disX > disZ) meta |= 4;
			else if(disZ > disX) meta |= 8;
			
			for(int b = 0; b < (span - 2); b++) {
				int xx = disX * (b + 1) / (span - 2);
				int yy = disY * (b + 1) / (span - 2);
				int zz = disZ * (b + 1) / (span - 2);

				this.setBlockAndMetadata(world, x + xx, y + height + 2 + yy, z + zz, this.trunkID, meta);
			}

			this.setBlockAndMetadata(world, posX, posY, posZ, this.trunkID, this.trunkMeta);
		}

		for(int i = 0; i < height + span + 2; i++) {
			this.setBlockAndMetadata(world, x, y + i, z, this.trunkID, this.trunkMeta);
		}

		return true;
	}
}
