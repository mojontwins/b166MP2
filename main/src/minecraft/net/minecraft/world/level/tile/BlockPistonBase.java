package net.minecraft.world.level.tile;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.item.EntityMovingPiston;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;

public class BlockPistonBase extends Block {
	private boolean sticky;

	public BlockPistonBase(int id, int blockIndexInTexture, boolean sticky) {
		super(id, Material.iron);
		this.sticky = sticky;
		this.blockIndexInTexture = blockIndexInTexture;
	}

	private void toggleBlock(World world, int x, int y, int z, boolean powered) {
		int meta = world.getBlockMetadata(x, y, z);

		// Failsafe. meta & 8 means the piston is already extended.

		if((meta & 8) == 0) {
			if(!powered) {
				return;
			}
		} else if(powered) {
			return;
		}

		meta &= 7;

		int xH = x;
		int yH = y;
		int zH = z;

		// meta & 7 tells us in which direction it extended.

		if(meta == 0) {
			yH = y - 1;
		}

		if(meta == 1) {
			++yH;
		}

		if(meta == 2) {
			zH = z - 1;
		}

		if(meta == 3) {
			++zH;
		}

		if(meta == 4) {
			xH = x - 1;
		}

		if(meta == 5) {
			++xH;
		}

		int blockID = world.getBlockId(xH, yH, zH);

		if((blockID == Block.classicPiston.blockID || blockID == Block.classicStickyPiston.blockID) && !powered) {
			// If the head position has a piston head and we are here means that such piston is extended.
			// And that it should retract.

			BlockPiston.resetBase = false;
			
			// retract head from (xH, yH, zH) to base (x, y, z).
			EntityMovingPiston.buildRetractingPistons(world, xH, yH, zH, x, y, z, meta, this.sticky);
			world.setBlockWithNotify(xH, yH, zH, 0);

			BlockPiston.resetBase = true;
		} else if(powered) {
			// in this case, extend the piston

			EntityMovingPiston piston = new EntityMovingPiston(world, x, y, z, this.sticky);
			if(!EntityMovingPiston.buildPistons(piston, xH, yH, zH, meta)) {
				return;
			}

			// meta | 8 means extended.
			world.setBlockMetadataWithNotify(x, y, z, meta | 8);
			world.markBlockAsNeedsUpdate(x, y, z);
		}

	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID) {
		if(blockID > 0 && Block.blocksList[blockID].canProvidePower()) {
			boolean powered = world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y + 1, z);
			this.toggleBlock(world, x, y, z, powered);
		}
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
		return 109;
	}

	private int getData(World world, int x, int y, int z, EntityLiving entityLiving) {
		// TODO: This sets meta depending only on angle. I have to change this, so if 
		// the player clicks the ground or a celing the orientations are correctly set to 0/1.
		
		if(
				MathHelper.abs((float)entityLiving.posX - (float)x) < 2.0F && 
				MathHelper.abs((float)entityLiving.posZ - (float)z) < 2.0F
		) {
			if(entityLiving.posY - (double)y > 2.0D) {
				return 1;
			}

			if((double)y - entityLiving.posY > 0.0D) {
				return 0;
			}
		}

		int angle = MathHelper.floor_double((double)(entityLiving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		if(angle == 0) {
			return 2;
		} else if(angle == 1) {
			return 5;
		} else if(angle == 2) {
			return 3;
		} else if(angle == 3) {
			return 4;
		} else {
			throw new IllegalStateException("Impossible values!");
		}
	}

	/*
	@Override
	public boolean blockActivated(World world, int x, int y, int z, EntityPlayer thePlayer) {
		if(world.isRemote) {
			return true;
		} else {
			int meta = world.getBlockMetadata(x, y, z);
			if((meta & 8) != 0) {
				return false;
			} else {
				int data = this.getData(world, x, y, z, thePlayer);
				if(data == meta) {
					return false;
				} else {
					world.setBlockMetadataWithNotify(x, y, z, data);
					world.markBlockAsNeedsUpdate(x, y, z);
					return true;
				}
			}
		}
	}
	*/
	
	@Override
	public void onBlockPlaced(World world, int x, int y, int z, int face, float xWithinFace, float yWithinFace, float zWithinFace, boolean keyPressed) {
		world.setBlockMetadata(x, y, z, face);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entityLiving) {
		//world.setBlockMetadata(x, y, z, this.getData(world, x, y, z, entityLiving));
		this.onNeighborBlockChange(world, x, y, z, Block.redstoneWire.blockID);
	}

	public void onPistonPushed(World world, int x, int y, int z) {
		this.onNeighborBlockChange(world, x, y, z, Block.redstoneWire.blockID);
	}

}
