package net.minecraft.world.level.tile;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class BlockStairs extends Block {
	private Block modelBlock;

	protected BlockStairs(int i1, Block block2) {
		super(i1, block2.blockIndexInTexture, block2.blockMaterial);
		this.modelBlock = block2;
		this.setHardness(block2.getHardness());
		this.setResistance(block2.blockResistance / 3.0F);
		this.setStepSound(block2.stepSound);
		this.setLightOpacity(255);
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world1, int i2, int i3, int i4) {
		return super.getCollisionBoundingBoxFromPool(world1, i2, i3, i4);
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
		return 10;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iBlockAccess1, int i2, int i3, int i4, int i5) {
		return super.shouldSideBeRendered(iBlockAccess1, i2, i3, i4, i5);
	}

	@Override
	public void getCollidingBoundingBoxes(World world, int x, int y, int z, AxisAlignedBB aabb, ArrayList<AxisAlignedBB> collidingBoundingBoxes) {
		int i7 = world.getBlockMetadata(x, y, z);
		boolean upsideDown = BlockStairs.isUpsideDown(i7);
		i7 &= 3;
		
		if(upsideDown) {
			if(i7 == 0) {
				this.setBlockBounds(0.0F, 0.5F, 0.0F, 0.5F, 1.0F, 1.0F);
				super.getCollidingBoundingBoxes(world, x, y, z, aabb, collidingBoundingBoxes);
				this.setBlockBounds(0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				super.getCollidingBoundingBoxes(world, x, y, z, aabb, collidingBoundingBoxes);
			} else if(i7 == 1) {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.5F, 1.0F, 1.0F);
				super.getCollidingBoundingBoxes(world, x, y, z, aabb, collidingBoundingBoxes);
				this.setBlockBounds(0.5F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
				super.getCollidingBoundingBoxes(world, x, y, z, aabb, collidingBoundingBoxes);
			} else if(i7 == 2) {
				this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 0.5F);
				super.getCollidingBoundingBoxes(world, x, y, z, aabb, collidingBoundingBoxes);
				this.setBlockBounds(0.0F, 0.0F, 0.5F, 1.0F, 1.0F, 1.0F);
				super.getCollidingBoundingBoxes(world, x, y, z, aabb, collidingBoundingBoxes);
			} else if(i7 == 3) {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.5F);
				super.getCollidingBoundingBoxes(world, x, y, z, aabb, collidingBoundingBoxes);
				this.setBlockBounds(0.0F, 0.5F, 0.5F, 1.0F, 1.0F, 1.0F);
				super.getCollidingBoundingBoxes(world, x, y, z, aabb, collidingBoundingBoxes);
			}
		} else {
			if(i7 == 0) {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 1.0F);
				super.getCollidingBoundingBoxes(world, x, y, z, aabb, collidingBoundingBoxes);
				this.setBlockBounds(0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
				super.getCollidingBoundingBoxes(world, x, y, z, aabb, collidingBoundingBoxes);
			} else if(i7 == 1) {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.5F, 1.0F, 1.0F);
				super.getCollidingBoundingBoxes(world, x, y, z, aabb, collidingBoundingBoxes);
				this.setBlockBounds(0.5F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
				super.getCollidingBoundingBoxes(world, x, y, z, aabb, collidingBoundingBoxes);
			} else if(i7 == 2) {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 0.5F);
				super.getCollidingBoundingBoxes(world, x, y, z, aabb, collidingBoundingBoxes);
				this.setBlockBounds(0.0F, 0.0F, 0.5F, 1.0F, 1.0F, 1.0F);
				super.getCollidingBoundingBoxes(world, x, y, z, aabb, collidingBoundingBoxes);
			} else if(i7 == 3) {
				this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.5F);
				super.getCollidingBoundingBoxes(world, x, y, z, aabb, collidingBoundingBoxes);
				this.setBlockBounds(0.0F, 0.0F, 0.5F, 1.0F, 0.5F, 1.0F);
				super.getCollidingBoundingBoxes(world, x, y, z, aabb, collidingBoundingBoxes);
			}
		}
		
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void randomDisplayTick(World world1, int i2, int i3, int i4, Random random5) {
		this.modelBlock.randomDisplayTick(world1, i2, i3, i4, random5);
	}

	@Override
	public void onBlockClicked(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		this.modelBlock.onBlockClicked(world1, i2, i3, i4, entityPlayer5);
	}

	@Override
	public int getMixedBrightnessForBlock(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		return this.modelBlock.getMixedBrightnessForBlock(iBlockAccess1, i2, i3, i4);
	}

	@Override
	public float getBlockBrightness(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		return this.modelBlock.getBlockBrightness(iBlockAccess1, i2, i3, i4);
	}

	@Override
	public float getExplosionResistance(Entity entity1) {
		return this.modelBlock.getExplosionResistance(entity1);
	}

	@Override
	public int getRenderBlockPass() {
		return this.modelBlock.getRenderBlockPass();
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int i1, int i2) {
		return this.modelBlock.getBlockTextureFromSideAndMetadata(i1, 0);
	}

	@Override
	public int getBlockTextureFromSide(int i1) {
		return this.modelBlock.getBlockTextureFromSideAndMetadata(i1, 0);
	}

	@Override
	public int tickRate() {
		return this.modelBlock.tickRate();
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world1, int i2, int i3, int i4) {
		return this.modelBlock.getSelectedBoundingBoxFromPool(world1, i2, i3, i4);
	}

	@Override
	public void velocityToAddToEntity(World world1, int i2, int i3, int i4, Entity entity5, Vec3D vec3D6) {
		this.modelBlock.velocityToAddToEntity(world1, i2, i3, i4, entity5, vec3D6);
	}

	@Override
	public boolean isCollidable() {
		return this.modelBlock.isCollidable();
	}

	@Override
	public boolean canCollideCheck(int i1, boolean z2) {
		return this.modelBlock.canCollideCheck(i1, z2);
	}

	@Override
	public boolean canPlaceBlockAt(World world1, int i2, int i3, int i4) {
		return this.modelBlock.canPlaceBlockAt(world1, i2, i3, i4);
	}

	@Override
	public void onBlockAdded(World world1, int i2, int i3, int i4) {
		this.onNeighborBlockChange(world1, i2, i3, i4, 0);
		this.modelBlock.onBlockAdded(world1, i2, i3, i4);
	}

	@Override
	public void onEntityWalking(World world1, int i2, int i3, int i4, Entity entity5) {
		this.modelBlock.onEntityWalking(world1, i2, i3, i4, entity5);
	}

	@Override
	public void updateTick(World world1, int i2, int i3, int i4, Random random5) {
		this.modelBlock.updateTick(world1, i2, i3, i4, random5);
	}

	@Override
	public boolean blockActivated(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		return this.modelBlock.blockActivated(world1, i2, i3, i4, entityPlayer5);
	}

	@Override
	public void onBlockPlacedBy(World world1, int i2, int i3, int i4, EntityLiving entityLiving5) {
		int i6 = MathHelper.floor_double((double)(entityLiving5.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		int i7 = world1.getBlockMetadata(i2, i3, i4) & 4;
		if(i6 == 0) {
			world1.setBlockMetadataWithNotify(i2, i3, i4, 2 | i7);
		}

		if(i6 == 1) {
			world1.setBlockMetadataWithNotify(i2, i3, i4, 1 | i7);
		}

		if(i6 == 2) {
			world1.setBlockMetadataWithNotify(i2, i3, i4, 3 | i7);
		}

		if(i6 == 3) {
			world1.setBlockMetadataWithNotify(i2, i3, i4, 0 | i7);
		}

	}

	@Override
	public void onBlockPlaced(World world, int x, int y, int z, int face, float xWithinFace, float yWithinFace, float zWithinFace, boolean keyPressed) {
		if(face == 0 || (face > 1 && yWithinFace >= 0.5F && !keyPressed)) world.setBlockMetadata(x, y, z, 4);
	}
	
	public static boolean isUpsideDown(int meta) {
		return (meta & 4) != 0;
	}
	
	@Override
	public boolean hasSolidTop(int meta) {
		return BlockStairs.isUpsideDown(meta);
	}
	
	@Override
	public boolean supportsTorch(int meta) {
		return BlockStairs.isUpsideDown(meta);
	}
	
	@Override
	public boolean canPlaceTorchBy(int meta, int side) {	
		meta &= 3;
		return (meta == 0 && side == 5) || (meta == 1 && side == 4) || (meta == 2 && side == 3) || (meta == 3 && side == 2); 
	}
}
