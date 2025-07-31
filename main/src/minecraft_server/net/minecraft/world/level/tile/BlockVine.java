package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.Direction;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.colorizer.ColorizerFoliage;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AxisAlignedBB;

public class BlockVine extends Block {
	public BlockVine(int i1) {
		super(i1, 143, Material.vine);
		this.setTickRandomly(true);
		this.displayOnCreativeTab = CreativeTabs.tabDeco;
	}

	public void setBlockBoundsForItemRender() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	public int getRenderType() {
		return 20;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		int i6 = iBlockAccess1.getBlockMetadata(i2, i3, i4);
		float f7 = 1.0F;
		float f8 = 1.0F;
		float f9 = 1.0F;
		float f10 = 0.0F;
		float f11 = 0.0F;
		float f12 = 0.0F;
		boolean z13 = i6 > 0;
		if((i6 & 2) != 0) {
			f10 = Math.max(f10, 0.0625F);
			f7 = 0.0F;
			f8 = 0.0F;
			f11 = 1.0F;
			f9 = 0.0F;
			f12 = 1.0F;
			z13 = true;
		}

		if((i6 & 8) != 0) {
			f7 = Math.min(f7, 0.9375F);
			f10 = 1.0F;
			f8 = 0.0F;
			f11 = 1.0F;
			f9 = 0.0F;
			f12 = 1.0F;
			z13 = true;
		}

		if((i6 & 4) != 0) {
			f12 = Math.max(f12, 0.0625F);
			f9 = 0.0F;
			f7 = 0.0F;
			f10 = 1.0F;
			f8 = 0.0F;
			f11 = 1.0F;
			z13 = true;
		}

		if((i6 & 1) != 0) {
			f9 = Math.min(f9, 0.9375F);
			f12 = 1.0F;
			f7 = 0.0F;
			f10 = 1.0F;
			f8 = 0.0F;
			f11 = 1.0F;
			z13 = true;
		}

		if(!z13 && this.canBePlacedOn(iBlockAccess1.getBlockId(i2, i3 + 1, i4))) {
			f8 = Math.min(f8, 0.9375F);
			f11 = 1.0F;
			f7 = 0.0F;
			f10 = 1.0F;
			f9 = 0.0F;
			f12 = 1.0F;
		}

		this.setBlockBounds(f7, f8, f9, f10, f11, f12);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world1, int i2, int i3, int i4) {
		return null;
	}

	public boolean canPlaceBlockOnSide(World world1, int i2, int i3, int i4, int i5) {
		switch(i5) {
		case 1:
			return this.canBePlacedOn(world1.getBlockId(i2, i3 + 1, i4));
		case 2:
			return this.canBePlacedOn(world1.getBlockId(i2, i3, i4 + 1));
		case 3:
			return this.canBePlacedOn(world1.getBlockId(i2, i3, i4 - 1));
		case 4:
			return this.canBePlacedOn(world1.getBlockId(i2 + 1, i3, i4));
		case 5:
			return this.canBePlacedOn(world1.getBlockId(i2 - 1, i3, i4));
		default:
			return false;
		}
	}

	private boolean canBePlacedOn(int i1) {
		if(i1 == 0) {
			return false;
		} else {
			Block block2 = Block.blocksList[i1];
			return block2.renderAsNormalBlock() && block2.blockMaterial.blocksMovement();
		}
	}

	private boolean canVineStay(World world1, int i2, int i3, int i4) {
		int i5 = world1.getBlockMetadata(i2, i3, i4);
		int i6 = i5;
		if(i5 > 0) {
			for(int i7 = 0; i7 <= 3; ++i7) {
				int i8 = 1 << i7;
				if((i5 & i8) != 0 && !this.canBePlacedOn(world1.getBlockId(i2 + Direction.offsetX[i7], i3, i4 + Direction.offsetZ[i7])) && (world1.getBlockId(i2, i3 + 1, i4) != this.blockID || (world1.getBlockMetadata(i2, i3 + 1, i4) & i8) == 0)) {
					i6 &= ~i8;
				}
			}
		}

		if(i6 == 0 && !this.canBePlacedOn(world1.getBlockId(i2, i3 + 1, i4))) {
			return false;
		} else {
			if(i6 != i5) {
				world1.setBlockMetadataWithNotify(i2, i3, i4, i6);
			}

			return true;
		}
	}

	public int getBlockColor() {
		return ColorizerFoliage.getFoliageColorBasic();
	}

	public int getRenderColor(int i1) {
		return ColorizerFoliage.getFoliageColorBasic();
	}

	public int colorMultiplier(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		return iBlockAccess1.getBiomeGenForCoords(i2, i4).getBiomeFoliageColor();
	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		if(!world1.isRemote && !this.canVineStay(world1, i2, i3, i4)) {
			this.dropBlockAsItem(world1, i2, i3, i4, world1.getBlockMetadata(i2, i3, i4), 0);
			world1.setBlockWithNotify(i2, i3, i4, 0);
		}

	}

	public void updateTick(World world1, int i2, int i3, int i4, Random random5) {
		if(!world1.isRemote && world1.rand.nextInt(4) == 0) {
			byte b6 = 4;
			int i7 = 5;
			boolean z8 = false;

			int i9;
			int i10;
			int i11;
			label141:
			for(i9 = i2 - b6; i9 <= i2 + b6; ++i9) {
				for(i10 = i4 - b6; i10 <= i4 + b6; ++i10) {
					for(i11 = i3 - 1; i11 <= i3 + 1; ++i11) {
						if(world1.getBlockId(i9, i11, i10) == this.blockID) {
							--i7;
							if(i7 <= 0) {
								z8 = true;
								break label141;
							}
						}
					}
				}
			}

			i9 = world1.getBlockMetadata(i2, i3, i4);
			i10 = world1.rand.nextInt(6);
			i11 = Direction.vineGrowth[i10];
			int i12;
			int i13;
			if(i10 == 1 && i3 < 255 && world1.isAirBlock(i2, i3 + 1, i4)) {
				if(z8) {
					return;
				}

				i12 = world1.rand.nextInt(16) & i9;
				if(i12 > 0) {
					for(i13 = 0; i13 <= 3; ++i13) {
						if(!this.canBePlacedOn(world1.getBlockId(i2 + Direction.offsetX[i13], i3 + 1, i4 + Direction.offsetZ[i13]))) {
							i12 &= ~(1 << i13);
						}
					}

					if(i12 > 0) {
						world1.setBlockAndMetadataWithNotify(i2, i3 + 1, i4, this.blockID, i12);
					}
				}
			} else {
				int i14;
				if(i10 >= 2 && i10 <= 5 && (i9 & 1 << i11) == 0) {
					if(z8) {
						return;
					}

					i12 = world1.getBlockId(i2 + Direction.offsetX[i11], i3, i4 + Direction.offsetZ[i11]);
					if(i12 != 0 && Block.blocksList[i12] != null) {
						if(Block.blocksList[i12].blockMaterial.isOpaque() && Block.blocksList[i12].renderAsNormalBlock()) {
							world1.setBlockMetadataWithNotify(i2, i3, i4, i9 | 1 << i11);
						}
					} else {
						i13 = i11 + 1 & 3;
						i14 = i11 + 3 & 3;
						if((i9 & 1 << i13) != 0 && this.canBePlacedOn(world1.getBlockId(i2 + Direction.offsetX[i11] + Direction.offsetX[i13], i3, i4 + Direction.offsetZ[i11] + Direction.offsetZ[i13]))) {
							world1.setBlockAndMetadataWithNotify(i2 + Direction.offsetX[i11], i3, i4 + Direction.offsetZ[i11], this.blockID, 1 << i13);
						} else if((i9 & 1 << i14) != 0 && this.canBePlacedOn(world1.getBlockId(i2 + Direction.offsetX[i11] + Direction.offsetX[i14], i3, i4 + Direction.offsetZ[i11] + Direction.offsetZ[i14]))) {
							world1.setBlockAndMetadataWithNotify(i2 + Direction.offsetX[i11], i3, i4 + Direction.offsetZ[i11], this.blockID, 1 << i14);
						} else if((i9 & 1 << i13) != 0 && world1.isAirBlock(i2 + Direction.offsetX[i11] + Direction.offsetX[i13], i3, i4 + Direction.offsetZ[i11] + Direction.offsetZ[i13]) && this.canBePlacedOn(world1.getBlockId(i2 + Direction.offsetX[i13], i3, i4 + Direction.offsetZ[i13]))) {
							world1.setBlockAndMetadataWithNotify(i2 + Direction.offsetX[i11] + Direction.offsetX[i13], i3, i4 + Direction.offsetZ[i11] + Direction.offsetZ[i13], this.blockID, 1 << (i11 + 2 & 3));
						} else if((i9 & 1 << i14) != 0 && world1.isAirBlock(i2 + Direction.offsetX[i11] + Direction.offsetX[i14], i3, i4 + Direction.offsetZ[i11] + Direction.offsetZ[i14]) && this.canBePlacedOn(world1.getBlockId(i2 + Direction.offsetX[i14], i3, i4 + Direction.offsetZ[i14]))) {
							world1.setBlockAndMetadataWithNotify(i2 + Direction.offsetX[i11] + Direction.offsetX[i14], i3, i4 + Direction.offsetZ[i11] + Direction.offsetZ[i14], this.blockID, 1 << (i11 + 2 & 3));
						} else if(this.canBePlacedOn(world1.getBlockId(i2 + Direction.offsetX[i11], i3 + 1, i4 + Direction.offsetZ[i11]))) {
							world1.setBlockAndMetadataWithNotify(i2 + Direction.offsetX[i11], i3, i4 + Direction.offsetZ[i11], this.blockID, 0);
						}
					}
				} else if(i3 > 1) {
					i12 = world1.getBlockId(i2, i3 - 1, i4);
					if(i12 == 0) {
						i13 = world1.rand.nextInt(16) & i9;
						if(i13 > 0) {
							world1.setBlockAndMetadataWithNotify(i2, i3 - 1, i4, this.blockID, i13);
						}
					} else if(i12 == this.blockID) {
						i13 = world1.rand.nextInt(16) & i9;
						i14 = world1.getBlockMetadata(i2, i3 - 1, i4);
						if(i14 != (i14 | i13)) {
							world1.setBlockMetadataWithNotify(i2, i3 - 1, i4, i14 | i13);
						}
					}
				}
			}
		}

	}

	public void onBlockPlaced(World world1, int i2, int i3, int i4, int i5) {
		byte b6 = 0;
		switch(i5) {
		case 2:
			b6 = 1;
			break;
		case 3:
			b6 = 4;
			break;
		case 4:
			b6 = 8;
			break;
		case 5:
			b6 = 2;
		}

		if(b6 != 0) {
			world1.setBlockMetadataWithNotify(i2, i3, i4, b6);
		}

	}

	public int idDropped(int i1, Random random2, int i3) {
		return 0;
	}

	public int quantityDropped(Random random1) {
		return 0;
	}

	public void harvestBlock(World world1, EntityPlayer entityPlayer2, int i3, int i4, int i5, int i6) {
		if(!world1.isRemote && entityPlayer2.getCurrentEquippedItem() != null && entityPlayer2.getCurrentEquippedItem().itemID == Item.shears.shiftedIndex) {
			this.dropBlockAsItem_do(world1, i3, i4, i5, new ItemStack(Block.vine, 1, 0));
		} else {
			super.harvestBlock(world1, entityPlayer2, i3, i4, i5, i6);
		}

	}
	
	@Override
	public boolean isLadder() {
		return true;
	}
}
