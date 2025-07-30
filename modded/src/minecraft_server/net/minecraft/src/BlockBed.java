package net.minecraft.src;

import java.util.Iterator;
import java.util.Random;

public class BlockBed extends Block {
	public static final int[][] field_22023_a = new int[][]{{0, 1}, {-1, 0}, {0, -1}, {1, 0}};

	public BlockBed(int i1) {
		super(i1, 134, Material.cloth);
		this.setBounds();
	}

	public boolean blockActivated(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		if(world1.singleplayerWorld) {
			return true;
		} else {
			int i6 = world1.getBlockMetadata(i2, i3, i4);
			if(!func_22020_d(i6)) {
				int i7 = func_22019_c(i6);
				i2 += field_22023_a[i7][0];
				i4 += field_22023_a[i7][1];
				if(world1.getBlockId(i2, i3, i4) != this.blockID) {
					return true;
				}

				i6 = world1.getBlockMetadata(i2, i3, i4);
			}

			if(!world1.worldProvider.func_28108_d()) {
				double d16 = (double)i2 + 0.5D;
				double d17 = (double)i3 + 0.5D;
				double d11 = (double)i4 + 0.5D;
				world1.setBlockWithNotify(i2, i3, i4, 0);
				int i13 = func_22019_c(i6);
				i2 += field_22023_a[i13][0];
				i4 += field_22023_a[i13][1];
				if(world1.getBlockId(i2, i3, i4) == this.blockID) {
					world1.setBlockWithNotify(i2, i3, i4, 0);
					d16 = (d16 + (double)i2 + 0.5D) / 2.0D;
					d17 = (d17 + (double)i3 + 0.5D) / 2.0D;
					d11 = (d11 + (double)i4 + 0.5D) / 2.0D;
				}

				world1.newExplosion((Entity)null, (double)((float)i2 + 0.5F), (double)((float)i3 + 0.5F), (double)((float)i4 + 0.5F), 5.0F, true);
				return true;
			} else {
				if(func_22018_f(i6)) {
					EntityPlayer entityPlayer14 = null;
					Iterator iterator8 = world1.playerEntities.iterator();

					while(iterator8.hasNext()) {
						EntityPlayer entityPlayer9 = (EntityPlayer)iterator8.next();
						if(entityPlayer9.func_30001_K()) {
							ChunkCoordinates chunkCoordinates10 = entityPlayer9.playerLocation;
							if(chunkCoordinates10.posX == i2 && chunkCoordinates10.posY == i3 && chunkCoordinates10.posZ == i4) {
								entityPlayer14 = entityPlayer9;
							}
						}
					}

					if(entityPlayer14 != null) {
						entityPlayer5.func_22061_a("tile.bed.occupied");
						return true;
					}

					func_22022_a(world1, i2, i3, i4, false);
				}

				EnumStatus enumStatus15 = entityPlayer5.goToSleep(i2, i3, i4);
				if(enumStatus15 == EnumStatus.OK) {
					func_22022_a(world1, i2, i3, i4, true);
					return true;
				} else {
					if(enumStatus15 == EnumStatus.NOT_POSSIBLE_NOW) {
						entityPlayer5.func_22061_a("tile.bed.noSleep");
					}

					return true;
				}
			}
		}
	}

	public int getBlockTextureFromSideAndMetadata(int i1, int i2) {
		if(i1 == 0) {
			return Block.planks.blockIndexInTexture;
		} else {
			int i3 = func_22019_c(i2);
			int i4 = ModelBed.field_22155_c[i3][i1];
			return func_22020_d(i2) ? (i4 == 2 ? this.blockIndexInTexture + 2 + 16 : (i4 != 5 && i4 != 4 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture + 1 + 16)) : (i4 == 3 ? this.blockIndexInTexture - 1 + 16 : (i4 != 5 && i4 != 4 ? this.blockIndexInTexture : this.blockIndexInTexture + 16));
		}
	}

	public boolean func_28025_b() {
		return false;
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		this.setBounds();
	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		int i6 = world1.getBlockMetadata(i2, i3, i4);
		int i7 = func_22019_c(i6);
		if(func_22020_d(i6)) {
			if(world1.getBlockId(i2 - field_22023_a[i7][0], i3, i4 - field_22023_a[i7][1]) != this.blockID) {
				world1.setBlockWithNotify(i2, i3, i4, 0);
			}
		} else if(world1.getBlockId(i2 + field_22023_a[i7][0], i3, i4 + field_22023_a[i7][1]) != this.blockID) {
			world1.setBlockWithNotify(i2, i3, i4, 0);
			if(!world1.singleplayerWorld) {
				this.dropBlockAsItem(world1, i2, i3, i4, i6);
			}
		}

	}

	public int idDropped(int i1, Random random2) {
		return func_22020_d(i1) ? 0 : Item.bed.shiftedIndex;
	}

	private void setBounds() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
	}

	public static int func_22019_c(int i0) {
		return i0 & 3;
	}

	public static boolean func_22020_d(int i0) {
		return (i0 & 8) != 0;
	}

	public static boolean func_22018_f(int i0) {
		return (i0 & 4) != 0;
	}

	public static void func_22022_a(World world0, int i1, int i2, int i3, boolean z4) {
		int i5 = world0.getBlockMetadata(i1, i2, i3);
		if(z4) {
			i5 |= 4;
		} else {
			i5 &= -5;
		}

		world0.setBlockMetadataWithNotify(i1, i2, i3, i5);
	}

	public static ChunkCoordinates func_22021_g(World world0, int i1, int i2, int i3, int i4) {
		int i5 = world0.getBlockMetadata(i1, i2, i3);
		int i6 = func_22019_c(i5);

		for(int i7 = 0; i7 <= 1; ++i7) {
			int i8 = i1 - field_22023_a[i6][0] * i7 - 1;
			int i9 = i3 - field_22023_a[i6][1] * i7 - 1;
			int i10 = i8 + 2;
			int i11 = i9 + 2;

			for(int i12 = i8; i12 <= i10; ++i12) {
				for(int i13 = i9; i13 <= i11; ++i13) {
					if(world0.isBlockOpaqueCube(i12, i2 - 1, i13) && world0.isAirBlock(i12, i2, i13) && world0.isAirBlock(i12, i2 + 1, i13)) {
						if(i4 <= 0) {
							return new ChunkCoordinates(i12, i2, i13);
						}

						--i4;
					}
				}
			}
		}

		return null;
	}
}
