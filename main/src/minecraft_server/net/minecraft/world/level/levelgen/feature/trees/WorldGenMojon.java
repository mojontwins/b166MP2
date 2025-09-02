package net.minecraft.world.level.levelgen.feature.trees;

import java.util.Random;

import net.minecraft.world.Direction;
import net.minecraft.world.level.BlockPos;
import net.minecraft.world.level.World;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;

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
}
