package net.minecraft.world.item;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockStep;

public class ItemSlab extends ItemBlock {
	public ItemSlab(int i1) {
		super(i1);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	public int getIconFromDamage(int i1) {
		return this.blockSingle().getBlockTextureFromSideAndMetadata(2, i1);
	}

	public int getMetadata(int i1) {
		return i1;
	}

	public String getItemNameIS(ItemStack itemStack1) {
		int i2 = itemStack1.getItemDamage();
		if(i2 < 0 || i2 >= this.blockSingle().blockStepTypes.length) {
			i2 = 0;
		}

		return super.getItemName() + "." + this.blockSingle().blockStepTypes[i2];
	}

	public BlockStep blockSingle() {
		return (BlockStep) Block.stairSingle;
	}
	
	public BlockStep blockDouble() {
		return (BlockStep) Block.stairDouble;
	}
	
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int face, float xWithinFace, float yWithinFace, float zWithinFace, boolean keyPressed)  {
		if(itemStack.stackSize == 0) {
			return false;
		} else {
			int blockID = world.getBlockId(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			int type = meta & 7;
			boolean isUpper = (meta & 8) != 0;

			if(
				((face == 1 && !isUpper) || (face == 0 && isUpper)) && 
				blockID == this.blockSingle().blockID && 
				type == itemStack.getItemDamage()
			) {
				if (world.checkIfAABBIsClear(this.blockDouble().getCollisionBoundingBoxFromPool(world, x, y, z)) && 
					world.setBlockAndMetadataWithNotify(x, y, z, this.blockDouble().blockID, type)
				) {
					world.playSoundEffect(
						(double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), 
						this.blockDouble().stepSound.getStepSound(), 
						(this.blockDouble().stepSound.getVolume() + 1.0F) / 2.0F, 
						this.blockDouble().stepSound.getPitch() * 0.8F
					);
					--itemStack.stackSize;
				}

				return true;
			} else if (onItemUseAux(itemStack, entityPlayer, world, x, y, z, face, xWithinFace, yWithinFace, zWithinFace, keyPressed)) {
				return true;
			} else {
				return super.onItemUse(itemStack, entityPlayer, world, x, y, z, face, xWithinFace, yWithinFace, zWithinFace, keyPressed);
			}
		}
	}

	private boolean onItemUseAux (ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float xWithinFace, float yWithinFace, float zWithinFace, boolean keyPressed) {
		switch (side) {
			case 0:	y --; break;
			case 1: y ++; break;
			case 2: z --; break;
			case 3: z ++; break;
			case 4: x --; break;
			case 5: x ++; break;
		}

		int blockID = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		int type = meta & 7;

		if (blockID == this.blockSingle().blockID && type == itemStack.getItemDamage ()) {
			if (world.checkIfAABBIsClear(this.blockDouble().getCollisionBoundingBoxFromPool(world, x, y, z)) &&
				world.setBlockAndMetadataWithNotify(x, y, z, this.blockDouble().blockID, type)
			) {
				world.playSoundEffect(
					(double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), 
					this.blockDouble().stepSound.getStepSound(), 
					(this.blockDouble().stepSound.getVolume() + 1.0F) / 2.0F, 
					this.blockDouble().stepSound.getPitch() * 0.8F
				);
				--itemStack.stackSize;
			}

			return true;
		} else {
			return false;
		}
	}
}
