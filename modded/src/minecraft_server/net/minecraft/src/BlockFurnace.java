package net.minecraft.src;

import java.util.Random;

public class BlockFurnace extends BlockContainer {
	private Random field_28033_a = new Random();
	private final boolean isActive;
	private static boolean field_28034_c = false;

	protected BlockFurnace(int i1, boolean z2) {
		super(i1, Material.rock);
		this.isActive = z2;
		this.blockIndexInTexture = 45;
	}

	public int idDropped(int i1, Random random2) {
		return Block.stoneOvenIdle.blockID;
	}

	public void onBlockAdded(World world1, int i2, int i3, int i4) {
		super.onBlockAdded(world1, i2, i3, i4);
		this.setDefaultDirection(world1, i2, i3, i4);
	}

	private void setDefaultDirection(World world1, int i2, int i3, int i4) {
		if(!world1.singleplayerWorld) {
			int i5 = world1.getBlockId(i2, i3, i4 - 1);
			int i6 = world1.getBlockId(i2, i3, i4 + 1);
			int i7 = world1.getBlockId(i2 - 1, i3, i4);
			int i8 = world1.getBlockId(i2 + 1, i3, i4);
			byte b9 = 3;
			if(Block.opaqueCubeLookup[i5] && !Block.opaqueCubeLookup[i6]) {
				b9 = 3;
			}

			if(Block.opaqueCubeLookup[i6] && !Block.opaqueCubeLookup[i5]) {
				b9 = 2;
			}

			if(Block.opaqueCubeLookup[i7] && !Block.opaqueCubeLookup[i8]) {
				b9 = 5;
			}

			if(Block.opaqueCubeLookup[i8] && !Block.opaqueCubeLookup[i7]) {
				b9 = 4;
			}

			world1.setBlockMetadataWithNotify(i2, i3, i4, b9);
		}
	}

	public int getBlockTextureFromSide(int i1) {
		return i1 == 1 ? this.blockIndexInTexture + 17 : (i1 == 0 ? this.blockIndexInTexture + 17 : (i1 == 3 ? this.blockIndexInTexture - 1 : this.blockIndexInTexture));
	}

	public boolean blockActivated(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		if(world1.singleplayerWorld) {
			return true;
		} else {
			TileEntityFurnace tileEntityFurnace6 = (TileEntityFurnace)world1.getBlockTileEntity(i2, i3, i4);
			entityPlayer5.displayGUIFurnace(tileEntityFurnace6);
			return true;
		}
	}

	public static void updateFurnaceBlockState(boolean z0, World world1, int i2, int i3, int i4) {
		int i5 = world1.getBlockMetadata(i2, i3, i4);
		TileEntity tileEntity6 = world1.getBlockTileEntity(i2, i3, i4);
		field_28034_c = true;
		if(z0) {
			world1.setBlockWithNotify(i2, i3, i4, Block.stoneOvenActive.blockID);
		} else {
			world1.setBlockWithNotify(i2, i3, i4, Block.stoneOvenIdle.blockID);
		}

		field_28034_c = false;
		world1.setBlockMetadataWithNotify(i2, i3, i4, i5);
		world1.setBlockTileEntity(i2, i3, i4, tileEntity6);
	}

	protected TileEntity getBlockEntity() {
		return new TileEntityFurnace();
	}

	public void onBlockPlacedBy(World world1, int i2, int i3, int i4, EntityLiving entityLiving5) {
		int i6 = MathHelper.floor_double((double)(entityLiving5.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		if(i6 == 0) {
			world1.setBlockMetadataWithNotify(i2, i3, i4, 2);
		}

		if(i6 == 1) {
			world1.setBlockMetadataWithNotify(i2, i3, i4, 5);
		}

		if(i6 == 2) {
			world1.setBlockMetadataWithNotify(i2, i3, i4, 3);
		}

		if(i6 == 3) {
			world1.setBlockMetadataWithNotify(i2, i3, i4, 4);
		}

	}

	public void onBlockRemoval(World world1, int i2, int i3, int i4) {
		if(!field_28034_c) {
			TileEntityFurnace tileEntityFurnace5 = (TileEntityFurnace)world1.getBlockTileEntity(i2, i3, i4);

			for(int i6 = 0; i6 < tileEntityFurnace5.getSizeInventory(); ++i6) {
				ItemStack itemStack7 = tileEntityFurnace5.getStackInSlot(i6);
				if(itemStack7 != null) {
					float f8 = this.field_28033_a.nextFloat() * 0.8F + 0.1F;
					float f9 = this.field_28033_a.nextFloat() * 0.8F + 0.1F;
					float f10 = this.field_28033_a.nextFloat() * 0.8F + 0.1F;

					while(itemStack7.stackSize > 0) {
						int i11 = this.field_28033_a.nextInt(21) + 10;
						if(i11 > itemStack7.stackSize) {
							i11 = itemStack7.stackSize;
						}

						itemStack7.stackSize -= i11;
						EntityItem entityItem12 = new EntityItem(world1, (double)((float)i2 + f8), (double)((float)i3 + f9), (double)((float)i4 + f10), new ItemStack(itemStack7.itemID, i11, itemStack7.getItemDamage()));
						float f13 = 0.05F;
						entityItem12.motionX = (double)((float)this.field_28033_a.nextGaussian() * f13);
						entityItem12.motionY = (double)((float)this.field_28033_a.nextGaussian() * f13 + 0.2F);
						entityItem12.motionZ = (double)((float)this.field_28033_a.nextGaussian() * f13);
						world1.entityJoinedWorld(entityItem12);
					}
				}
			}
		}

		super.onBlockRemoval(world1, i2, i3, i4);
	}
}
