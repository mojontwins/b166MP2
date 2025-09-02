package net.minecraft.world.level.levelgen.feature.trees;

import java.util.Random;

import net.minecraft.world.Direction;
import net.minecraft.world.level.BlockPos;
import net.minecraft.world.level.BlockState;
import net.minecraft.world.level.World;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockLeaves;

public abstract class WorldGenMojon extends WorldGenerator {
	public static final int logMetaHorzX = 12; 	// Vanilla is 4
	public static final int logMetaHorzZ = 4;	// Vanilla is 8
	
	boolean withNotify = false;
	
	public WorldGenMojon(boolean withNotify) {
		super();
		this.withNotify = withNotify;
	}
	
	public void setBlockAndMetadata(World world, int x, int y, int z, int blockID, int meta) {
		if (this.withNotify) {
			world.setBlockAndMetadataWithNotify(x, y, z, blockID, meta);
		} else {
			world.setBlockAndMetadata(x, y, z, blockID, meta);
		}
	}

	public void setBlockAndMetadata(World world, BlockPos blockPos, int blockID, int meta) {
		this.setBlockAndMetadata(world, blockPos.x, blockPos.y, blockPos.z, blockID, meta);
	}
	
	private void setBlockAndMetadata(World world, int x, int y, int z, BlockState bs) {
		this.setBlockAndMetadata(world, x, y, z, bs.getBlockID(), bs.getMetadata());
		
	}
	
	public void setBlockIfEmpty(World world, BlockPos blockPos, int blockID, int meta) {
		this.setBlockIfEmpty(world, blockPos.x, blockPos.y, blockPos.z, blockID, meta);
	}
	
	public void setBlockIfEmpty(World world, int x, int y, int z, int blockID, int meta) {
		if (0 == world.getBlockId(x, y, z)) this.setBlockAndMetadata(world, x, y, z, blockID, meta);
	}
	
	public void setBlockIfAirOrLeaves(World world, int x, int y, int z, int blockID, int meta) {
		Material material = world.getBlockMaterial(x, y, z);
		if (material == Material.air || material == Material.leaves) this.setBlockAndMetadata(world, x, y, z, blockID, meta);
	}
	
	public void roundedShape(World world, int x0, int y0, int z0, int r, int blockID, int meta, boolean withBottomHole) {
		int x1 = x0 - r, x2 = x0 + r;
		int y1 = y0 - r, y2 = y0 + r;
		int z1 = z0 - r, z2 = z0 + r;
		
		int bottom = y1; if (withBottomHole) bottom ++;
		
		for(int x = x1; x <= x2; x ++) {
			for(int y = bottom; y <= y2; y ++) {
				for(int z = z1; z <= z2; z ++) {
					if(
						(x == x1 || x == x2 || z == z1 || z == z2 || y == y1 || y == y2) &&
						!(x == x1 && z == z1 && y == y1) &&
						!(x == x2 && z == z1 && y == y1) &&
						!(x == x1 && z == z2 && y == y1) &&
						!(x == x2 && z == z2 && y == y1) &&
						!(x == x1 && z == z1 && y == y2) &&
						!(x == x2 && z == z1 && y == y2) &&
						!(x == x1 && z == z2 && y == y2) &&
						!(x == x2 && z == z2 && y == y2)
					) {
						this.setBlockAndMetadata(world, x, y, z, blockID, meta);
					}
				}
			}
		}
	}
	
	public void bresenham(World world, BlockPos src, BlockPos dst, int blockID, int meta) {
		this.bresenham(world, src.x, src.y, src.z, dst.x, dst.y, dst.z, blockID, meta);
	}
	
	public void bresenham(World world, int x1, int y1, int z1, int x2, int y2, int z2, int blockID, int meta) {
		
		BlockPos blockPos = new BlockPos().set(x1, y1, z1);
		
		int dx = x2 - x1;
		int dxAbs = Math.abs(dx);
		
		int dy = y2 - y1;
		int dyAbs = Math.abs(dy);
		
		int dz = z2 - z1;
		int dzAbs = Math.abs(dz);
		
		// Directions of propagation
		int xIncr = dx < 0 ? -1 : 1;
		int yIncr = dy < 0 ? -1 : 1;
		int zIncr = dz < 0 ? -1 : 1;
		
		int dx2 = dxAbs << 1;
		int dy2 = dyAbs << 1;
		int dz2 = dzAbs << 1;
		
		if(dxAbs >= dyAbs && dxAbs >= dzAbs) {
			// Moves the most in the X axis...
			
			int errorY = dy2 - dxAbs;
			int errorZ = dz2 - dxAbs;

			for(int i = 0; i < dxAbs; ++i) {
				this.setBlockAndMetadata(world, blockPos, blockID, meta | logMetaHorzX);
				
				if(errorY > 0) {
					blockPos.move(Direction.UP, yIncr);
					errorY -= dx2;
				}

				if(errorZ > 0) {
					blockPos.move(Direction.SOUTH, zIncr);
					errorZ -= dx2;
				}

				errorY += dy2;
				errorZ += dz2;
				blockPos.move(Direction.EAST, xIncr);
			}
		} else if(dyAbs >= dxAbs && dyAbs >= dzAbs) {
			// Moves the most in the Y axis...

			int errorX = dx2 - dyAbs;
			int errorZ = dz2 - dyAbs;

			for(int i = 0; i < dyAbs; ++i) {
				this.setBlockAndMetadata(world, blockPos, blockID, meta);
				
				if(errorX > 0) {
					blockPos.move(Direction.EAST, xIncr);
					errorX -= dy2;
				}

				if(errorZ > 0) {
					blockPos.move(Direction.SOUTH, zIncr);
					errorZ -= dy2;
				}

				errorX += dx2;
				errorZ += dz2;
				blockPos.move(Direction.UP, yIncr);
			}
		} else {
			// Moves the most in the Z axis...

			int errorX = dx2 - dzAbs;
			int errorY = dy2 - dzAbs;
			
			for(int i = 0; i < dzAbs; ++i) {
				this.setBlockAndMetadata(world, blockPos, blockID, meta | logMetaHorzZ);
				
				if(errorX > 0) {
					blockPos.move(Direction.EAST, xIncr);
					errorX -= dz2;
				}

				if(errorY > 0) {
					blockPos.move(Direction.UP, yIncr);
					errorY -= dz2;
				}

				errorX += dx2;
				errorY += dy2;
				blockPos.move(Direction.SOUTH, zIncr);
			}
		}

		this.setBlockAndMetadata(world, blockPos, blockID, meta);
	}
	
	public void circle(World world, BlockPos src, int rad, int blockID, int meta) {
		this.circle(world, src.x, src.y, src.z, rad, blockID, meta);
	}
	
	public void circle(World world, int x0, int y0, int z0, int rad, int blockID, int meta) {
		for(byte dx = 0; dx <= rad; ++dx) {
			for(byte dz = 0; dz <= rad; ++dz) {
				int dist = (int)((double)Math.max(dx, dz) + (double)Math.min(dx, dz) * 0.5D);
				
				if(dx == 3 && dz == 3) {
					dist = 6;
				}

				if(dist <= rad) {
					this.setBlockIfEmpty(world, x0 + dx, y0, z0 + dz, blockID, meta);
					this.setBlockIfEmpty(world, x0 + dx, y0, z0 - dz, blockID, meta);
					this.setBlockIfEmpty(world, x0 - dx, y0, z0 + dz, blockID, meta);
					this.setBlockIfEmpty(world, x0 - dx, y0, z0 - dz, blockID, meta);
				}
			}
		}

	}

	public void drawDiameterCircle(World world, int x0, int y0, int z0, byte diam, int blockID, int meta) {
		byte rad = (byte) ((diam - 1) / 2);
		if(diam % 2 == 1) {
			this.circle(world, x0, y0, z0, rad, blockID, meta);
		} else {
			this.circle(world, x0, y0, z0, rad, blockID, meta);
			this.circle(world, x0 + 1, y0, z0, rad, blockID, meta);
			this.circle(world, x0, y0, z0 + 1, rad, blockID, meta);
			this.circle(world, x0 + 1, y0, z0 + 1, rad, blockID, meta);
		}

	}

	public boolean validGround(World world, int x, int y, int z) {
		Block b = Block.blocksList[world.getBlockId(x, y, z)];
		return b != null && b.canGrowPlants();
	}
	
	public void crossTreeLayer(World world, Random rand, int x0, int y0, int z0, int w, int leavesID, int leavesMeta) {
		int offset = w >> 1;
		if((w & 1) == 0) offset --;
		for(int i = 0; i < w; i ++) {
			this.setBlockIfEmpty(world, x0 + i - offset, y0, z0, leavesID, leavesMeta);
			this.setBlockIfEmpty(world, x0, y0, z0 + i - offset, leavesID, leavesMeta);
		}
	}
	
	public void squareTreeLayer(World world, Random rand, int x0, int y0, int z0, int w, int leavesID, int leavesMeta) {
		int offset = w >> 1;
		if((w & 1) == 0) offset --;
		for(int x = 0; x < w; x ++) {
			for(int z = 0; z < w; z ++) {
				this.setBlockIfEmpty(world, x0 + x - offset, y0, z0 + z - offset, leavesID, leavesMeta);
			}
		}
	}
	
	public void roundedSquareTreeLayer(World world, Random rand, int x0, int y0, int z0, int w, int leavesID, int leavesMeta) {
		int offset = w >> 1;
		if((w & 1) == 0) offset --;
		for(int x = 0; x < w; x ++) {
			for(int z = 0; z < w; z ++) {
				if(!((x == 0 || x == w - 1) && (z == 0 || z == w - 1))) {
					this.setBlockIfEmpty(world, x0 + x - offset, y0, z0 + z - offset, leavesID, leavesMeta);
				}
			}
		}
	}

	public void edgesPlusShape(World world, BlockPos blockPos, int w, int leavesID, int leavesMeta) {
		this.edgesPlusShape(world, blockPos.x, blockPos.y, blockPos.z, w, leavesID, leavesMeta);
	}
	
	public void edgesPlusShape(World world, int x, int y, int z, int w, int leavesID, int leavesMeta) {
		int offset = w >> 1;
		if((w & 1) == 0) offset --;
		this.setBlockIfEmpty(world, x - offset, y, z, leavesID, leavesMeta);
		this.setBlockIfEmpty(world, x + offset, y, z, leavesID, leavesMeta);
		this.setBlockIfEmpty(world, x, y, z - offset, leavesID, leavesMeta);
		this.setBlockIfEmpty(world, x, y, z + offset, leavesID, leavesMeta);
	}
	
	public void edgesSquareShape(World world, BlockPos blockPos, int w, int leavesID, int leavesMeta) {
		this.edgesSquareShape(world, blockPos.x, blockPos.y, blockPos.z, w, leavesID, leavesMeta);
	}
	
	public void edgesSquareShape(World world, int x, int y, int z, int w, int leavesID, int leavesMeta) {
		int offset = w >> 1;
		if((w & 1) == 0) offset --;
		this.setBlockIfEmpty(world, x - offset, y, z - offset, leavesID, leavesMeta);
		this.setBlockIfEmpty(world, x + offset, y, z - offset, leavesID, leavesMeta);
		this.setBlockIfEmpty(world, x - offset, y, z + offset, leavesID, leavesMeta);
		this.setBlockIfEmpty(world, x + offset, y, z + offset, leavesID, leavesMeta);
	}

	public void diamondTreeLayer(World world, Random rand, int x0, int y0, int z0, int w, int leavesID, int leavesMeta) {
		int radius = w >> 1;

		for(int i = 0; i < radius; i ++) {
			for(int j = 0; j <= i; j ++) {
				this.setBlockIfEmpty(world, x0 - radius + 1 + i, y0, z0 - j, leavesID, leavesMeta);
				this.setBlockIfEmpty(world, x0 - radius + 1 + i, y0, z0 + 1 + j, leavesID, leavesMeta);
				this.setBlockIfEmpty(world, x0 + radius - i, y0, z0 - j, leavesID, leavesMeta);
				this.setBlockIfEmpty(world, x0 + radius - i, y0, z0 + 1 + j, leavesID, leavesMeta);
			}
		}
	}
	
	public BlockPos findEndPoint(BlockPos origin, double distance, double angle, double tilt) {
		// angles are in radians
		BlockPos dest = new BlockPos();
		
		dest.set(
				origin.x + (int) Math.round(Math.sin(angle) * Math.sin(tilt) * distance),
				origin.y + (int) Math.round(Math.cos(tilt) * distance),
				origin.z + (int) Math.round(Math.cos(angle) * Math.sin(tilt) * distance)
		);
		
		return dest;
	}
	
	public boolean ellipsoid(World world, Random par2Random, int x, int y, int z, int radius, float variation, BlockState fill) {
		int baseX = x;
		int baseY = y;
		int baseZ = z;

		int radiusSq = radius * radius;

		for(int posX = -radius; posX <= radius; posX++) {
			for(int posY = -radius; posY <= radius * variation; posY++) {
				for(int posZ = -radius; posZ <= radius; posZ++) {
					int distance = (int) (posX * posX + posY * posY / variation / variation + posZ * posZ);

					if(distance <= radiusSq) {
						if(fill.getBlock() instanceof BlockLeaves) {
							this.setBlockIfEmpty(world, posX + baseX, posY + baseY, posZ + baseZ, fill.getBlockID(), fill.getMetadata());
						} else {
							this.setBlockAndMetadata(world, posX + baseX, posY + baseY, posZ + baseZ, fill.getBlockID(), fill.getMetadata());
						}
					}
				}
			}
		}

		return true;
	}
	
	// Helpers from extraBiomesXXL

	public boolean check2x2Trunk(int x, int y, int z, int height, BlockState logs, World world, boolean inWater) {
		if (inWater) {
			for (int y1 = y + 1; y1 < y + height; y1++) {
				Block b00 = world.getBlock(x, y1, z);
				Block b10 = world.getBlock(x + 1, y1, z);
				Block b01 = world.getBlock(x, y1, z + 1);
				Block b11 = world.getBlock(x + 1, y1, z + 1);
				if (b00 != null && !(b00.blockMaterial == Material.water) && !this.isReplaceable(b00))
					return false;
				if (b01 != null && !(b01.blockMaterial == Material.water) && !this.isReplaceable(b01))
					return false;
				if (b10 != null && !(b10.blockMaterial == Material.water) && !this.isReplaceable(b10))
					return false;
				if (b11 != null && !(b11.blockMaterial == Material.water) && !this.isReplaceable(b11))
					return false;
			}
		} else {
			for (int y1 = y + 1; y1 < y + height; y1++) {
				if (!world.isAirBlock(x, y1, z))
					return false;
				if (!world.isAirBlock(x + 1, y1, z))
					return false;
				if (!world.isAirBlock(x, y1, z + 1))
					return false;
				if (!world.isAirBlock(x + 1, y1, z + 1))
					return false;
			}
		}

		return true;
	}

	public boolean place2x2Trunk(int x, int y, int z, int height, BlockState logs, World world) {
		for (int y1 = y; y1 < y + height; y1++) {
			this.setBlockAndMetadata(world, x, y1, z, logs);
			this.setBlockAndMetadata(world, x + 1, y1, z, logs);
			this.setBlockAndMetadata(world, x, y1, z + 1, logs);
			this.setBlockAndMetadata(world, x + 1, y1, z + 1, logs);
		}

		return true;
	}

	private boolean isReplaceable(Block block) {
		return block == null || !block.isOpaqueCube() || block.blockID == Block.leaves.blockID;
	}

	public boolean checkLeafCluster(World world, int x, int y, int z, int height, int radius) {
		for (int layer = -height; layer <= height; layer++) {
			if (!this.checkLeavesCircle(x, y + layer, z, radius * Math.cos(layer / (height / 1.3)), world))
				return false;
		}

		return true;
	}

	public void generateLeafCluster(World world, int x, int y, int z, int height, int radius, BlockState leaves) {
		for (int layer = -height; layer <= height; layer++) {
			this.placeLeavesCircle(x, y + layer, z, radius * Math.cos(layer / (height / 1.3)), leaves, world);
		}
	}

	public boolean checkLeavesCircle(double x, int y, double z, double r, World world) {
		double dist = r * r;

		for (double z1 = Math.floor(-r); z1 < r + 1; z1++) {
			for (double x1 = Math.floor(-r); x1 < r + 1; x1++) {
				int x2 = (int) (x1 + x);
				int z2 = (int) (z1 + z);

				final Block block = world.getBlock(x2, y, z2);

				if (((x1 * x1) + (z1 * z1)) <= dist) {
					if (block != null && block.isOpaqueCube())
						return false;
				}
			}
		}

		return true;
	}

	public void placeLeavesCircle(double x, int y, double z, double r, BlockState leaves, World world) {
		double dist = r * r;

		for (double z1 = Math.floor(-r); z1 < r + 1; z1++) {
			for (double x1 = Math.floor(-r); x1 < r + 1; x1++) {
				int x2 = (int) (x1 + x);
				int z2 = (int) (z1 + z);

				final Block block = world.getBlock(x2, y, z2);

				if ((((x1 * x1) + (z1 * z1)) <= dist) && this.isReplaceable(block)) {
					this.setBlockAndMetadata(world, x2, y, z2, leaves);
				}
			}
		}
	}

	public boolean checkBlockLine(int[] start, int[] end, BlockState logs, World world) {
		if (start.length != 3 || end.length != 3)
			return false;

		// Get the direction vector
		int[] direction = { start[0] - end[0], start[1] - end[1], start[2] - end[2] };
		if (Math.abs(direction[2]) > Math.abs(direction[1]) && Math.abs(direction[2]) > Math.abs(direction[0])) {
			// We are going to use the y axis as our major axis
			if (direction[2] >= 0) {
				for (int z = start[2]; z >= end[2]; z--) {
					double m = (z - start[2]) / (double) direction[2];
					int x = (int) (start[0] + (direction[0] * m));
					int y = (int) (start[1] + (direction[1] * m));
					if (!world.isAirBlock(x, y, z))
						return false;
				}
			} else {
				for (int z = start[2]; z <= end[2]; z++) {
					double m = (z - start[2]) / (double) direction[2];
					int x = (int) (start[0] + (direction[0] * m));
					int y = (int) (start[1] + (direction[1] * m));
					if (!world.isAirBlock(x, y, z))
						return false;
				}
			}
		} else if (Math.abs(direction[0]) > Math.abs(direction[1])) {
			// Treverse along the x axis
			if (direction[0] >= 0) {
				for (int x = start[0]; x >= end[0]; x--) {
					double m = (x - start[0]) / (double) direction[0];
					int z = (int) (start[2] + (direction[2] * m));
					int y = (int) (start[1] + (direction[1] * m));
					if (!world.isAirBlock(x, y, z))
						return false;
				}
			} else {
				for (int x = start[0]; x <= end[0]; x++) {
					double m = (x - start[0]) / (double) direction[0];
					int z = (int) (start[2] + (direction[2] * m));
					int y = (int) (start[1] + (direction[1] * m));
					if (!world.isAirBlock(x, y, z))
						return false;
				}
			}
		} else {
			// We will use the y axis as our major axis
			if (direction[1] >= 0) {
				for (int y = start[1]; y >= end[1]; y--) {
					double m = (y - start[1]) / (double) direction[1];
					int x = (int) (start[0] + (direction[0] * m));
					int z = (int) (start[2] + (direction[2] * m));
					if (!world.isAirBlock(x, y, z))
						return false;
				}
			} else {
				for (int y = start[1]; y <= end[1]; y++) {
					double m = (y - start[1]) / (double) direction[1];
					int x = (int) (start[0] + (direction[0] * m));
					int z = (int) (start[2] + (direction[2] * m));
					if (!world.isAirBlock(x, y, z))
						return false;
				}
			}
		}

		return true;
	}

	public boolean placeBlockLine(int[] start, int[] end, BlockState log, World world) {
		int logBlock = log.getBlock().blockID;
		int logMeta = log.getMetadata();

		if (start.length != 3 || end.length != 3)
			return false;

		// Get the direction vector
		int[] direction = { start[0] - end[0], start[1] - end[1], start[2] - end[2] };
		if (Math.abs(direction[2]) > Math.abs(direction[1]) && Math.abs(direction[2]) > Math.abs(direction[0])) {
			// We are going to use the y axis as our major axis
			if (direction[2] >= 0) {
				for (int z = start[2]; z >= end[2]; z--) {
					double m = (z - start[2]) / (double) direction[2];
					int x = (int) (start[0] + (direction[0] * m));
					int y = (int) (start[1] + (direction[1] * m));
					if (world.isAirBlock(x, y, z))
						this.setBlockAndMetadata(world, x, y, z, logBlock, logMeta | 4);
				}
			} else {
				for (int z = start[2]; z <= end[2]; z++) {
					double m = (z - start[2]) / (double) direction[2];
					int x = (int) (start[0] + (direction[0] * m));
					int y = (int) (start[1] + (direction[1] * m));
					if (world.isAirBlock(x, y, z))
						this.setBlockAndMetadata(world, x, y, z, logBlock, logMeta | 4);
				}
			}
		} else if (Math.abs(direction[0]) > Math.abs(direction[1])) {
			// Treverse along the x axis
			if (direction[0] >= 0) {
				for (int x = start[0]; x >= end[0]; x--) {
					double m = (x - start[0]) / (double) direction[0];
					int z = (int) (start[2] + (direction[2] * m));
					int y = (int) (start[1] + (direction[1] * m));
					if (world.isAirBlock(x, y, z))
						this.setBlockAndMetadata(world, x, y, z, logBlock, logMeta | 8);
				}
			} else {
				for (int x = start[0]; x <= end[0]; x++) {
					double m = (x - start[0]) / (double) direction[0];
					int z = (int) (start[2] + (direction[2] * m));
					int y = (int) (start[1] + (direction[1] * m));
					if (world.isAirBlock(x, y, z))
						this.setBlockAndMetadata(world, x, y, z, logBlock, logMeta | 8);
				}
			}
		} else {
			// We will use the y axis as our major axis
			if (direction[1] >= 0) {
				for (int y = start[1]; y >= end[1]; y--) {
					double m = (y - start[1]) / (double) direction[1];
					int x = (int) (start[0] + (direction[0] * m));
					int z = (int) (start[2] + (direction[2] * m));
					if (world.isAirBlock(x, y, z))
						this.setBlockAndMetadata(world, x, y, z, logBlock, logMeta);
				}
			} else {
				for (int y = start[1]; y <= end[1]; y++) {
					double m = (y - start[1]) / (double) direction[1];
					int x = (int) (start[0] + (direction[0] * m));
					int z = (int) (start[2] + (direction[2] * m));
					if (world.isAirBlock(x, y, z))
						this.setBlockAndMetadata(world, x, y, z, logBlock, logMeta);
				}
			}
		}

		return true;
	}

	public boolean placeKnee(int x, int y, int z, int height, int direction, BlockState logs, BlockState knees,
			World world) {
		int logBlock = logs.getBlock().blockID;
		int logMeta = logs.getMetadata();

		if (direction > 3)
			return false;

		int orientation = 0;

		switch (direction) {
		case 0:
			orientation = 8;
			break;
		case 1:
			orientation = 4;
			break;
		case 2:
			orientation = 8;
			break;
		case 3:
			orientation = 4;
			break;
		default:
			break;
		}

		for (int y1 = y - 1; y1 > 1; y1--) {
			Block block = world.getBlock(x, y1, z);
			if (!this.isReplaceable(block))
				break;

			// If there is an air block here place a root log
			this.setBlockAndMetadata(world, x, y1, z, logBlock, logMeta);
		}

		for (int y1 = y; y1 < y + height - 1; y1++) {
			this.setBlockAndMetadata(world, x, y1, z, logBlock, logMeta);
		}

		// Place the knee on top
		this.setBlockAndMetadata(world, x, y + height - 1, z, knees.getBlock().blockID, knees.getMetadata() | orientation);

		return true;
	}
}
