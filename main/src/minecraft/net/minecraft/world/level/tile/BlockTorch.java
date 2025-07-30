package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

public class BlockTorch extends Block {
	protected BlockTorch(int blockID, int blockIndexInTexture) {
		super(blockID, blockIndexInTexture, Material.circuits);
		this.setTickRandomly(true);
		this.displayOnCreativeTab = CreativeTabs.tabDeco;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return 2;
	}

	private boolean canPlaceTorchOn(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		return block != null && block.supportsTorch(world.getBlockMetadata(x, y, z));
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return 
				world.isBlockNormalCubeDefault(x - 1, y, z, true) ? 
						true 
					: 
						(world.isBlockNormalCubeDefault(x + 1, y, z, true) ? 
								true 
							: 
								(world.isBlockNormalCubeDefault(x, y, z - 1, true) ? 
										true 
									: 
										(world.isBlockNormalCubeDefault(x, y, z + 1, true) ? 
												true 
											: 
												this.canPlaceTorchOn(world, x, y - 1, z))));
	}

	public boolean canPlaceTorchBy(World world, int x, int y, int z, int side) {
		Block block = world.getBlock(x, y, z);
		return block != null && block.canPlaceTorchBy(world.getBlockMetadata(x, y, z), side);
	}
	
	@Override
	public void onBlockPlaced(World world, int x, int y, int z, int side) {
		int meta = world.getBlockMetadata(x, y, z);
		
		if(side == 1 && this.canPlaceTorchOn(world, x, y - 1, z)) {
			meta = 5;
		}

		if(side == 2 && this.canPlaceTorchBy(world, x, y, z + 1, 2)) {
			meta = 4;
		}

		if(side == 3 && this.canPlaceTorchBy(world, x, y, z - 1, 3)) {
			meta = 3;
		}

		if(side == 4 && this.canPlaceTorchBy(world, x + 1, y, z, 4)) {
			meta = 2;
		}

		if(side == 5 && this.canPlaceTorchBy(world, x - 1, y, z, 5)) {
			meta = 1;
		}

		world.setBlockMetadataWithNotify(x, y, z, meta);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		super.updateTick(world, x, y, z, rand);
		if(world.getBlockMetadata(x, y, z) == 0) {
			this.onBlockAdded(world, x, y, z);
		}

	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		if(world.isBlockNormalCubeDefault(x - 1, y, z, true)) {
			world.setBlockMetadataWithNotify(x, y, z, 1);
		} else if(world.isBlockNormalCubeDefault(x + 1, y, z, true)) {
			world.setBlockMetadataWithNotify(x, y, z, 2);
		} else if(world.isBlockNormalCubeDefault(x, y, z - 1, true)) {
			world.setBlockMetadataWithNotify(x, y, z, 3);
		} else if(world.isBlockNormalCubeDefault(x, y, z + 1, true)) {
			world.setBlockMetadataWithNotify(x, y, z, 4);
		} else if(this.canPlaceTorchOn(world, x, y - 1, z)) {
			world.setBlockMetadataWithNotify(x, y, z, 5);
		}

		this.dropTorchIfCantStay(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int side) {
		if(this.dropTorchIfCantStay(world, x, y, z)) {
			int i6 = world.getBlockMetadata(x, y, z);
			boolean z7 = false;
			if(!world.isBlockNormalCubeDefault(x - 1, y, z, true) && i6 == 1) {
				z7 = true;
			}

			if(!world.isBlockNormalCubeDefault(x + 1, y, z, true) && i6 == 2) {
				z7 = true;
			}

			if(!world.isBlockNormalCubeDefault(x, y, z - 1, true) && i6 == 3) {
				z7 = true;
			}

			if(!world.isBlockNormalCubeDefault(x, y, z + 1, true) && i6 == 4) {
				z7 = true;
			}

			if(!this.canPlaceTorchOn(world, x, y - 1, z) && i6 == 5) {
				z7 = true;
			}

			if(z7) {
				this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
				world.setBlockWithNotify(x, y, z, 0);
			}
		}

	}

	private boolean dropTorchIfCantStay(World world, int x, int y, int z) {
		if(!this.canPlaceBlockAt(world, x, y, z)) {
			if(world.getBlockId(x, y, z) == this.blockID) {
				this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
				world.setBlockWithNotify(x, y, z, 0);
			}

			return false;
		} else {
			return true;
		}
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3D vec3D5, Vec3D vec3D6) {
		int i7 = world.getBlockMetadata(x, y, z) & 7;
		float f8 = 0.15F;
		if(i7 == 1) {
			this.setBlockBounds(0.0F, 0.2F, 0.5F - f8, f8 * 2.0F, 0.8F, 0.5F + f8);
		} else if(i7 == 2) {
			this.setBlockBounds(1.0F - f8 * 2.0F, 0.2F, 0.5F - f8, 1.0F, 0.8F, 0.5F + f8);
		} else if(i7 == 3) {
			this.setBlockBounds(0.5F - f8, 0.2F, 0.0F, 0.5F + f8, 0.8F, f8 * 2.0F);
		} else if(i7 == 4) {
			this.setBlockBounds(0.5F - f8, 0.2F, 1.0F - f8 * 2.0F, 0.5F + f8, 0.8F, 1.0F);
		} else {
			f8 = 0.1F;
			this.setBlockBounds(0.5F - f8, 0.0F, 0.5F - f8, 0.5F + f8, 0.6F, 0.5F + f8);
		}

		return super.collisionRayTrace(world, x, y, z, vec3D5, vec3D6);
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		int i6 = world.getBlockMetadata(x, y, z);
		double d7 = (double)((float)x + 0.5F);
		double d9 = (double)((float)y + 0.7F);
		double d11 = (double)((float)z + 0.5F);
		double d13 = (double)0.22F;
		double d15 = (double)0.27F;
		if(i6 == 1) {
			world.spawnParticle("smoke", d7 - d15, d9 + d13, d11, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("flame", d7 - d15, d9 + d13, d11, 0.0D, 0.0D, 0.0D);
		} else if(i6 == 2) {
			world.spawnParticle("smoke", d7 + d15, d9 + d13, d11, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("flame", d7 + d15, d9 + d13, d11, 0.0D, 0.0D, 0.0D);
		} else if(i6 == 3) {
			world.spawnParticle("smoke", d7, d9 + d13, d11 - d15, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("flame", d7, d9 + d13, d11 - d15, 0.0D, 0.0D, 0.0D);
		} else if(i6 == 4) {
			world.spawnParticle("smoke", d7, d9 + d13, d11 + d15, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("flame", d7, d9 + d13, d11 + d15, 0.0D, 0.0D, 0.0D);
		} else {
			world.spawnParticle("smoke", d7, d9, d11, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("flame", d7, d9, d11, 0.0D, 0.0D, 0.0D);
		}

	}
}
