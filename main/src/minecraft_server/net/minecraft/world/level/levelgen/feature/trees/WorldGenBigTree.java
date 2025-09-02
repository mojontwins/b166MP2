package net.minecraft.world.level.levelgen.feature.trees;

import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.world.level.World;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;

public class WorldGenBigTree extends WorldGenerator {
		
	private final int leavesID;
	private final int leavesMeta;
	private final int trunkID;
	private final int trunkMeta;
	
	static final byte[] otherCoordPairs = new byte[]{(byte)2, (byte)0, (byte)0, (byte)1, (byte)2, (byte)1};
	Random rand = new Random();
	World world;
	int[] basePos = new int[]{0, 0, 0};
	int heightLimit = 0;
	int height;
	double heightAttenuation = 0.618D;
	double branchDensity = 1.0D;
	double branchSlope = 0.381D;
	double scaleWidth = 1.0D;
	double leafDensity = 1.0D;
	int trunkSize = 1;
	int heightLimitLimit = 12;
	int leafDistanceLimit = 4;
	int[][] leafNodes;
	int trunkRadius = 0;

	boolean checkIfItFits = true;
	
	public WorldGenBigTree(boolean z1, EnumTreeType tree) {
		super(z1);
		this.leavesID = tree.leaves.getBlock().blockID;
		this.leavesMeta = tree.leaves.getMetadata();
		this.trunkID = tree.wood.getBlock().blockID;
		this.trunkMeta = tree.wood.getMetadata();
	}
	
	public WorldGenBigTree(boolean z1) {
		this(z1, EnumTreeType.OAK);
	}

	void generateLeafNodeList() {
		this.height = (int)((double)this.heightLimit * this.heightAttenuation);
		if(this.height >= this.heightLimit) {
			this.height = this.heightLimit - 1;
		}

		int i1 = (int)(1.382D + Math.pow(this.leafDensity * (double)this.heightLimit / 13.0D, 2.0D));
		if(i1 < 1) {
			i1 = 1;
		}

		int[][] i2 = new int[i1 * this.heightLimit][4];
		int i3 = this.basePos[1] + this.heightLimit - this.leafDistanceLimit;
		int i4 = 1;
		int i5 = this.basePos[1] + this.height;
		int i6 = i3 - this.basePos[1];
		i2[0][0] = this.basePos[0];
		i2[0][1] = i3;
		i2[0][2] = this.basePos[2];
		i2[0][3] = i5;
		--i3;

		while(true) {
			while(i6 >= 0) {
				int i7 = 0;
				float f8 = this.layerSize(i6);
				if(f8 < 0.0F) {
					--i3;
					--i6;
				} else {
					for(double d9 = 0.5D; i7 < i1; ++i7) {
						double d11 = this.scaleWidth * (double)f8 * ((double)this.rand.nextFloat() + 0.328D);
						double d13 = (double)this.rand.nextFloat() * 2.0D * 3.14159D;
						int i15 = MathHelper.floor_double(d11 * Math.sin(d13) + (double)this.basePos[0] + d9);
						int i16 = MathHelper.floor_double(d11 * Math.cos(d13) + (double)this.basePos[2] + d9);
						int[] i17 = new int[]{i15, i3, i16};
						int[] i18 = new int[]{i15, i3 + this.leafDistanceLimit, i16};
						if(this.checkBlockLine(i17, i18) == -1) {
							int[] i19 = new int[]{this.basePos[0], this.basePos[1], this.basePos[2]};
							double d20 = Math.sqrt(Math.pow((double)Math.abs(this.basePos[0] - i17[0]), 2.0D) + Math.pow((double)Math.abs(this.basePos[2] - i17[2]), 2.0D));
							double d22 = d20 * this.branchSlope;
							if((double)i17[1] - d22 > (double)i5) {
								i19[1] = i5;
							} else {
								i19[1] = (int)((double)i17[1] - d22);
							}

							if(this.checkBlockLine(i19, i17) == -1) {
								i2[i4][0] = i15;
								i2[i4][1] = i3;
								i2[i4][2] = i16;
								i2[i4][3] = i19[1];
								++i4;
							}
						}
					}

					--i3;
					--i6;
				}
			}

			this.leafNodes = new int[i4][4];
			System.arraycopy(i2, 0, this.leafNodes, 0, i4);
			return;
		}
	}

	void genTreeLayer(int x, int y, int z, float radiusD, byte lci) {
		int radiusI = (int)((double)radiusD + 0.618D);
		byte b8 = otherCoordPairs[lci];
		byte b9 = otherCoordPairs[lci + 3];
		int[] pos0 = new int[]{x, y, z};
		int[] pos = new int[]{0, 0, 0};
		int i12 = -radiusI;
		int i13 = -radiusI;

		label32:
		for(pos[lci] = pos0[lci]; i12 <= radiusI; ++i12) {
			pos[b8] = pos0[b8] + i12;
			i13 = -radiusI;

			while(true) {
				while(true) {
					if(i13 > radiusI) {
						continue label32;
					}

					double d15 = Math.sqrt(Math.pow((double)Math.abs(i12) + 0.5D, 2.0D) + Math.pow((double)Math.abs(i13) + 0.5D, 2.0D));
					if(d15 > (double)radiusD) {
						++i13;
					} else {
						pos[b9] = pos0[b9] + i13;
						Material m = world.getBlockMaterial(pos[0], pos[1], pos[2]);
						if(m != Material.air && m != Material.leaves) {
							++i13;
						} else {
							this.setBlockAndMetadata(world, pos[0], pos[1], pos[2], this.leavesID, this.leavesMeta);
							++i13;
						}
					}
				}
			}
		}

	}

	float layerSize(int i1) {
		if((double)i1 < (double)((float)this.heightLimit) * 0.3D) {
			return -1.618F;
		} else {
			float f2 = (float)this.heightLimit / 2.0F;
			float f3 = (float)this.heightLimit / 2.0F - (float)i1;
			float f4;
			if(f3 == 0.0F) {
				f4 = f2;
			} else if(Math.abs(f3) >= f2) {
				f4 = 0.0F;
			} else {
				f4 = (float)Math.sqrt(Math.pow((double)Math.abs(f2), 2.0D) - Math.pow((double)Math.abs(f3), 2.0D));
			}

			f4 *= 0.5F;
			return f4;
		}
	}

	float leafSize(int i1) {
		return i1 >= 0 && i1 < this.leafDistanceLimit ? (i1 != 0 && i1 != this.leafDistanceLimit - 1 ? 3.0F : 2.0F) : -1.0F;
	}

	void generateLeafNode(int x, int y, int z) {
		int i4 = y;

		for(int i5 = y + this.leafDistanceLimit; i4 < i5; ++i4) {
			float f6 = this.leafSize(i4 - y);
			this.genTreeLayer(x, i4, z, f6, (byte)1);
		}

	}

	void placeBlockLine(int[] from, int[] to) {
		// This is a 3d line algorithm
		int[] coord = new int[]{0, 0, 0};

		// Calculate dx, dy, dz, and find which is the biggest
		byte mainCmp = 0;
		for(byte i = 0; i < 3; ++i) {
			coord[i] = to[i] - from[i];
			if(Math.abs(coord[i]) > Math.abs(coord[mainCmp])) {
				mainCmp = i;
			}
		}

		// My work: knowing which component displaces the most
		// (x, y or z) I can select metadata
		int metadata = mainCmp == 1 ? 0 : (mainCmp == 0 ? 4 : 8);
		
		// Tree type meta
		metadata |= this.trunkMeta;

		if(coord[mainCmp] != 0) {
			// Pick up the other component (ie not mainCmp)
			byte cmp1 = otherCoordPairs[mainCmp];
			byte cmp2 = otherCoordPairs[mainCmp + 3];
			byte direction;
			
			if(coord[mainCmp] > 0) {
				direction = 1;
			} else {
				direction = -1;
			}

			// How much increase cmp1 and cmp2 in each iteration
			double dCmp1 = (double)coord[cmp1] / (double)coord[mainCmp];
			double dCmp2 = (double)coord[cmp2] / (double)coord[mainCmp];

			int[] curPos = new int[]{0, 0, 0};
			int dst = coord[mainCmp] + direction;
			
			for(int i = 0; i != dst; i += direction) {
				curPos[mainCmp] = MathHelper.floor_double((double)(from[mainCmp] + i) + 0.5D);
				curPos[cmp1] = MathHelper.floor_double((double)from[cmp1] + (double)i * dCmp1 + 0.5D);
				curPos[cmp2] = MathHelper.floor_double((double)from[cmp2] + (double)i * dCmp2 + 0.5D);
				this.setBlockAndMetadata(this.world, curPos[0], curPos[1], curPos[2], this.trunkID, metadata);
			}

		}
	}

	void generateLeaves() {
		int l = this.leafNodes.length;

		for(int i = 0; i < l; i ++) {
			int x = this.leafNodes[i][0];
			int y = this.leafNodes[i][1];
			int z = this.leafNodes[i][2];
			this.generateLeafNode(x, y, z);
		}

	}

	boolean leafNodeNeedsBase(int i1) {
		return (double)i1 >= (double)this.heightLimit * 0.2D;
	}

	void generateTrunk() {
		if(this.trunkRadius == 0) {
			int i1 = this.basePos[0];
			int i2 = this.basePos[1];
			int i3 = this.basePos[1] + this.height;
			int i4 = this.basePos[2];
			int[] i5 = new int[]{i1, i2, i4};
			int[] i6 = new int[]{i1, i3, i4};
			this.placeBlockLine(i5, i6);
			if(this.trunkSize == 2) {
				++i5[0];
				++i6[0];
				this.placeBlockLine(i5, i6);
				++i5[2];
				++i6[2];
				this.placeBlockLine(i5, i6);
				i5[0] += -1;
				i6[0] += -1;
				this.placeBlockLine(i5, i6);
			}
		} else {
			int radius = this.trunkRadius;
			
			for(int x = -radius; x <= radius; x ++) {
				for(int z = -radius; z <= radius; z ++) {
					if(x * x + z * z <= radius * radius) {
						int i1 = this.basePos[0] + x;
						int i2 = this.basePos[1];
						int i3 = this.basePos[1] + this.height;
						int i4 = this.basePos[2] + z;
						int[] i5 = new int[]{i1, i2, i4};
						int[] i6 = new int[]{i1, i3, i4};
						this.placeBlockLine(i5, i6);
					}
				}
			}
		}

	}

	void generateLeafNodeBases() {
		int i1 = 0;
		int i2 = this.leafNodes.length;

		for(int[] i3 = new int[]{this.basePos[0], this.basePos[1], this.basePos[2]}; i1 < i2; ++i1) {
			int[] i4 = this.leafNodes[i1];
			int[] i5 = new int[]{i4[0], i4[1], i4[2]};
			i3[1] = i4[3];
			int i6 = i3[1] - this.basePos[1];
			if(this.leafNodeNeedsBase(i6)) {
				this.placeBlockLine(i3, i5);
			}
		}

	}

	int checkBlockLine(int[] i1, int[] i2) {
		int[] i3 = new int[]{0, 0, 0};
		byte b4 = 0;

		byte b5;
		for(b5 = 0; b4 < 3; ++b4) {
			i3[b4] = i2[b4] - i1[b4];
			if(Math.abs(i3[b4]) > Math.abs(i3[b5])) {
				b5 = b4;
			}
		}

		if(i3[b5] == 0) {
			return -1;
		} else {
			byte b6 = otherCoordPairs[b5];
			byte b7 = otherCoordPairs[b5 + 3];
			byte b8;
			if(i3[b5] > 0) {
				b8 = 1;
			} else {
				b8 = -1;
			}

			double d9 = (double)i3[b6] / (double)i3[b5];
			double d11 = (double)i3[b7] / (double)i3[b5];
			int[] i13 = new int[]{0, 0, 0};
			int i14 = 0;

			int i15;
			for(i15 = i3[b5] + b8; i14 != i15; i14 += b8) {
				i13[b5] = i1[b5] + i14;
				i13[b6] = MathHelper.floor_double((double)i1[b6] + (double)i14 * d9);
				i13[b7] = MathHelper.floor_double((double)i1[b7] + (double)i14 * d11);
				Material m = this.world.getBlockMaterial(i13[0], i13[1], i13[2]);
				if(m != Material.air && m != Material.leaves) {
					break;
				}
			}

			return i14 == i15 ? -1 : Math.abs(i14);
		}
	}

	boolean validTreeLocation() {
		Block block = this.world.getBlock(this.basePos[0], this.basePos[1] - 1, this.basePos[2]);
		
		if(!(block != null && block.canGrowPlants())) return false;
		
		if(this.checkIfItFits) {
			int[] i1 = new int[]{this.basePos[0], this.basePos[1] + 1, this.basePos[2]};
			int[] i2 = new int[]{this.basePos[0], this.basePos[1] + this.heightLimit - 1, this.basePos[2]};
			int i4 = this.checkBlockLine(i1, i2);
			if(i4 == -1) {
				return true;
			} else if(i4 < 6) {
				return false;
			} else {
				this.heightLimit = i4;
				return true;
			}
		} else return true;
	}

	public void setScale(double scaleX, double scaleY, double scaleZ) {
		this.heightLimitLimit = (int)(scaleX * 12.0D);
		if(scaleX > 0.5D) {
			this.leafDistanceLimit = 5;
		}

		this.scaleWidth = scaleY;
		this.leafDensity = scaleZ;
		
		this.trunkRadius = (int)(scaleY - .1);
		if(this.trunkRadius < 0) this.trunkRadius = 0;
	}

	public boolean generate(World world, Random rand, int x, int y, int z) {
		this.world = world;
		long j6 = rand.nextLong();
		this.rand.setSeed(j6);
		this.basePos[0] = x;
		this.basePos[1] = y;
		this.basePos[2] = z;
		if(this.heightLimit == 0) {
			this.heightLimit = 5 + this.rand.nextInt(this.heightLimitLimit);
		}

		if(!this.validTreeLocation()) {
			return false;
		} else {
			this.generateLeafNodeList();
			this.generateLeaves();
			this.generateTrunk();
			this.generateLeafNodeBases();
			return true;
		}
	}
}
