package net.minecraft.world.level.tile;

import net.minecraft.world.entity.monster.EntityIronGolem;
import net.minecraft.world.entity.monster.EntitySnowman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;

public class BlockPumpkin extends BlockDirectional {
	private boolean blockType;

	protected BlockPumpkin(int blockID, int blockIndexInTexture, boolean isLit) {
		super(blockID, Material.pumpkin);
		this.blockIndexInTexture = blockIndexInTexture;
		this.setTickRandomly(true);
		this.blockType = isLit;
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}

	public int getTextureTop() {
		return this.blockIndexInTexture;
	}

	public int getTextureBottom() {
		return this.blockIndexInTexture;
	}

	public int getTextureFront() {
		return this.blockIndexInTexture + (this.blockType ? 2 : 1) + 16;
	}

	public int getTextureSide() {
		return this.blockIndexInTexture + 16;
	}

	public int getTextureBack() {
		return this.blockIndexInTexture + 16;
	}
	
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);

		// Attempt to make golems
		
		if(world.getBlockId(x, y - 1, z) == Block.blockSnow.blockID && world.getBlockId(x, y - 2, z) == Block.blockSnow.blockID) {
			if(!world.isRemote) {
				world.setBlock(x, y, z, 0);
				world.setBlock(x, y - 1, z, 0);
				world.setBlock(x, y - 2, z, 0);
				EntitySnowman entity = new EntitySnowman(world);
				entity.setLocationAndAngles((double)x + 0.5D, (double)y - 1.95D, (double)z + 0.5D, 0.0F, 0.0F);
				world.spawnEntityInWorld(entity);
				world.notifyBlockChange(x, y, z, 0);
				world.notifyBlockChange(x, y - 1, z, 0);
				world.notifyBlockChange(x, y - 2, z, 0);
			}

			for(int i = 0; i < 120; ++i) {
				world.spawnParticle("snowshovel", (double)x + world.rand.nextDouble(), (double)(y - 2) + world.rand.nextDouble() * 2.5D, (double)z + world.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
			}
		} else if(world.getBlockId(x, y - 1, z) == Block.blockSteel.blockID && world.getBlockId(x, y - 2, z) == Block.blockSteel.blockID) {
			boolean correctEW = world.getBlockId(x - 1, y - 1, z) == Block.blockSteel.blockID && world.getBlockId(x + 1, y - 1, z) == Block.blockSteel.blockID;
			boolean correctNS = world.getBlockId(x, y - 1, z - 1) == Block.blockSteel.blockID && world.getBlockId(x, y - 1, z + 1) == Block.blockSteel.blockID;
			if(correctEW || correctNS) {
				world.setBlock(x, y, z, 0);
				world.setBlock(x, y - 1, z, 0);
				world.setBlock(x, y - 2, z, 0);
				if(correctEW) {
					world.setBlock(x - 1, y - 1, z, 0);
					world.setBlock(x + 1, y - 1, z, 0);
				} else {
					world.setBlock(x, y - 1, z - 1, 0);
					world.setBlock(x, y - 1, z + 1, 0);
				}

				EntityIronGolem entity = new EntityIronGolem(world);
				entity.setCreatedByPlayer(true);
				entity.setLocationAndAngles((double)x + 0.5D, (double)y - 1.95D, (double)z + 0.5D, 0.0F, 0.0F);
				world.spawnEntityInWorld(entity);

				for(int i = 0; i < 120; ++i) {
					world.spawnParticle("snowballpoof", (double)x + world.rand.nextDouble(), (double)(y - 2) + world.rand.nextDouble() * 3.9D, (double)z + world.rand.nextDouble(), 0.0D, 0.0D, 0.0D);
				}

				world.notifyBlockChange(x, y, z, 0);
				world.notifyBlockChange(x, y - 1, z, 0);
				world.notifyBlockChange(x, y - 2, z, 0);
				if(correctEW) {
					world.notifyBlockChange(x - 1, y - 1, z, 0);
					world.notifyBlockChange(x + 1, y - 1, z, 0);
				} else {
					world.notifyBlockChange(x, y - 1, z - 1, 0);
					world.notifyBlockChange(x, y - 1, z + 1, 0);
				}
			}
		}

	}

	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		int i5 = world.getBlockId(x, y, z);
		return (i5 == 0 || Block.blocksList[i5].blockMaterial.isGroundCover()) && world.isBlockNormalCube(x, y - 1, z);
	}


}
