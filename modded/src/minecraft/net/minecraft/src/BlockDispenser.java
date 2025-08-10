package net.minecraft.src;

import java.util.Random;

public class BlockDispenser extends BlockContainer {
	private Random field_28035_a = new Random();

	protected BlockDispenser(int paramInt) {
		super(paramInt, Material.rock);
		this.blockIndexInTexture = 45;
	}

	public int tickRate() {
		return 4;
	}

	public int idDropped(int paramInt, Random paramRandom) {
		return Block.dispenser.blockID;
	}

	public void onBlockAdded(World paramfb, int paramInt1, int paramInt2, int paramInt3) {
		super.onBlockAdded(paramfb, paramInt1, paramInt2, paramInt3);
		this.setDispenserDefaultDirection(paramfb, paramInt1, paramInt2, paramInt3);
	}

	private void setDispenserDefaultDirection(World paramfb, int paramInt1, int paramInt2, int paramInt3) {
		if(!paramfb.multiplayerWorld) {
			int i = paramfb.getBlockId(paramInt1, paramInt2, paramInt3 - 1);
			int j = paramfb.getBlockId(paramInt1, paramInt2, paramInt3 + 1);
			int k = paramfb.getBlockId(paramInt1 - 1, paramInt2, paramInt3);
			int m = paramfb.getBlockId(paramInt1 + 1, paramInt2, paramInt3);
			byte n = 3;
			if(Block.opaqueCubeLookup[i] && !Block.opaqueCubeLookup[j]) {
				n = 3;
			}

			if(Block.opaqueCubeLookup[j] && !Block.opaqueCubeLookup[i]) {
				n = 2;
			}

			if(Block.opaqueCubeLookup[k] && !Block.opaqueCubeLookup[m]) {
				n = 5;
			}

			if(Block.opaqueCubeLookup[m] && !Block.opaqueCubeLookup[k]) {
				n = 4;
			}

			paramfb.setBlockMetadataWithNotify(paramInt1, paramInt2, paramInt3, n);
		}
	}

	public int getBlockTexture(IBlockAccess paramxg, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		if(paramInt4 == 1) {
			return this.blockIndexInTexture + 17;
		} else if(paramInt4 == 0) {
			return this.blockIndexInTexture + 17;
		} else {
			int i = paramxg.getBlockMetadata(paramInt1, paramInt2, paramInt3);
			return paramInt4 != i ? this.blockIndexInTexture : this.blockIndexInTexture + 1;
		}
	}

	public int getBlockTextureFromSide(int paramInt) {
		return paramInt == 1 ? this.blockIndexInTexture + 17 : (paramInt == 0 ? this.blockIndexInTexture + 17 : (paramInt == 3 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture));
	}

	public boolean blockActivated(World paramfb, int paramInt1, int paramInt2, int paramInt3, EntityPlayer paramgq) {
		if(paramfb.multiplayerWorld) {
			return true;
		} else {
			TileEntityDispenser localay = (TileEntityDispenser)paramfb.getBlockTileEntity(paramInt1, paramInt2, paramInt3);
			paramgq.displayGUIDispenser(localay);
			return true;
		}
	}

	private void dispenseItem(World paramfb, int paramInt1, int paramInt2, int paramInt3, Random paramRandom) {
		int i = paramfb.getBlockMetadata(paramInt1, paramInt2, paramInt3);
		byte j = 0;
		byte k = 0;
		if(i == 3) {
			k = 1;
		} else if(i == 2) {
			k = -1;
		} else if(i == 5) {
			j = 1;
		} else {
			j = -1;
		}

		TileEntityDispenser localay = (TileEntityDispenser)paramfb.getBlockTileEntity(paramInt1, paramInt2, paramInt3);
		ItemStack localiw = localay.getRandomStackFromInventory();
		double d1 = (double)paramInt1 + (double)j * 0.6D + 0.5D;
		double d2 = (double)paramInt2 + 0.5D;
		double d3 = (double)paramInt3 + (double)k * 0.6D + 0.5D;
		if(localiw == null) {
			paramfb.func_28106_e(1001, paramInt1, paramInt2, paramInt3, 0);
		} else {
			boolean handled = ModLoader.DispenseEntity(paramfb, d1, d2, d3, j, k, localiw);
			if(!handled) {
				if(localiw.itemID == Item.arrow.shiftedIndex) {
					EntityArrow localObject = new EntityArrow(paramfb, d1, d2, d3);
					((EntityArrow)localObject).setArrowHeading((double)j, 0.1000000014901161D, (double)k, 1.1F, 6.0F);
					((EntityArrow)localObject).field_28020_a = true;
					paramfb.entityJoinedWorld(localObject);
					paramfb.func_28106_e(1002, paramInt1, paramInt2, paramInt3, 0);
				} else if(localiw.itemID == Item.egg.shiftedIndex) {
					EntityEgg localObject1 = new EntityEgg(paramfb, d1, d2, d3);
					((EntityEgg)localObject1).setEggHeading((double)j, 0.1000000014901161D, (double)k, 1.1F, 6.0F);
					paramfb.entityJoinedWorld(localObject1);
					paramfb.func_28106_e(1002, paramInt1, paramInt2, paramInt3, 0);
				} else if(localiw.itemID == Item.snowball.shiftedIndex) {
					EntitySnowball localObject2 = new EntitySnowball(paramfb, d1, d2, d3);
					((EntitySnowball)localObject2).setSnowballHeading((double)j, 0.1000000014901161D, (double)k, 1.1F, 6.0F);
					paramfb.entityJoinedWorld(localObject2);
					paramfb.func_28106_e(1002, paramInt1, paramInt2, paramInt3, 0);
				} else {
					EntityItem localObject3 = new EntityItem(paramfb, d1, d2 - 0.3D, d3, localiw);
					double d4 = paramRandom.nextDouble() * 0.1D + 0.2D;
					((EntityItem)localObject3).motionX = (double)j * d4;
					((EntityItem)localObject3).motionY = 0.2000000029802322D;
					((EntityItem)localObject3).motionZ = (double)k * d4;
					localObject3.motionX += paramRandom.nextGaussian() * (double)0.0075F * 6.0D;
					localObject3.motionY += paramRandom.nextGaussian() * (double)0.0075F * 6.0D;
					localObject3.motionZ += paramRandom.nextGaussian() * (double)0.0075F * 6.0D;
					paramfb.entityJoinedWorld(localObject3);
					paramfb.func_28106_e(1000, paramInt1, paramInt2, paramInt3, 0);
				}
			}

			paramfb.func_28106_e(2000, paramInt1, paramInt2, paramInt3, j + 1 + (k + 1) * 3);
		}

	}

	public void onNeighborBlockChange(World paramfb, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		if(paramInt4 > 0 && Block.blocksList[paramInt4].canProvidePower()) {
			boolean i = paramfb.isBlockIndirectlyGettingPowered(paramInt1, paramInt2, paramInt3) || paramfb.isBlockIndirectlyGettingPowered(paramInt1, paramInt2 + 1, paramInt3);
			if(i) {
				paramfb.scheduleBlockUpdate(paramInt1, paramInt2, paramInt3, this.blockID, this.tickRate());
			}
		}

	}

	public void updateTick(World paramfb, int paramInt1, int paramInt2, int paramInt3, Random paramRandom) {
		if(paramfb.isBlockIndirectlyGettingPowered(paramInt1, paramInt2, paramInt3) || paramfb.isBlockIndirectlyGettingPowered(paramInt1, paramInt2 + 1, paramInt3)) {
			this.dispenseItem(paramfb, paramInt1, paramInt2, paramInt3, paramRandom);
		}

	}

	protected TileEntity getBlockEntity() {
		return new TileEntityDispenser();
	}

	public void onBlockPlacedBy(World paramfb, int paramInt1, int paramInt2, int paramInt3, EntityLiving paramlo) {
		int i = MathHelper.floor_double((double)(paramlo.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		if(i == 0) {
			paramfb.setBlockMetadataWithNotify(paramInt1, paramInt2, paramInt3, 2);
		}

		if(i == 1) {
			paramfb.setBlockMetadataWithNotify(paramInt1, paramInt2, paramInt3, 5);
		}

		if(i == 2) {
			paramfb.setBlockMetadataWithNotify(paramInt1, paramInt2, paramInt3, 3);
		}

		if(i == 3) {
			paramfb.setBlockMetadataWithNotify(paramInt1, paramInt2, paramInt3, 4);
		}

	}

	public void onBlockRemoval(World paramfb, int paramInt1, int paramInt2, int paramInt3) {
		TileEntityDispenser localay = (TileEntityDispenser)paramfb.getBlockTileEntity(paramInt1, paramInt2, paramInt3);

		for(int i = 0; i < localay.getSizeInventory(); ++i) {
			ItemStack localiw = localay.getStackInSlot(i);
			if(localiw != null) {
				float f1 = this.field_28035_a.nextFloat() * 0.8F + 0.1F;
				float f2 = this.field_28035_a.nextFloat() * 0.8F + 0.1F;
				float f3 = this.field_28035_a.nextFloat() * 0.8F + 0.1F;

				while(localiw.stackSize > 0) {
					int j = this.field_28035_a.nextInt(21) + 10;
					if(j > localiw.stackSize) {
						j = localiw.stackSize;
					}

					localiw.stackSize -= j;
					EntityItem localhj = new EntityItem(paramfb, (double)((float)paramInt1 + f1), (double)((float)paramInt2 + f2), (double)((float)paramInt3 + f3), new ItemStack(localiw.itemID, j, localiw.getItemDamage()));
					float f4 = 0.05F;
					localhj.motionX = (double)((float)this.field_28035_a.nextGaussian() * f4);
					localhj.motionY = (double)((float)this.field_28035_a.nextGaussian() * f4 + 0.2F);
					localhj.motionZ = (double)((float)this.field_28035_a.nextGaussian() * f4);
					paramfb.entityJoinedWorld(localhj);
				}
			}
		}

		super.onBlockRemoval(paramfb, paramInt1, paramInt2, paramInt3);
	}
}
