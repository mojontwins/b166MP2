package net.minecraft.world.level.tile;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.world.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.entity.player.EnumStatus;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;
import net.minecraft.world.level.material.Material;

public class BlockBed extends BlockDirectional {
	public static final int[][] headBlockToFootBlockMap = new int[][]{{0, 1}, {-1, 0}, {0, -1}, {1, 0}};

	public BlockBed(int i1) {
		super(i1, 134, Material.cloth);
		this.setBounds();
	}

	public boolean blockActivated(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		if(world1.isRemote) {
			return true;
		} else {
			int i6 = world1.getBlockMetadata(i2, i3, i4);
			if(!isBlockFootOfBed(i6)) {
				int i7 = getDirection(i6);
				i2 += headBlockToFootBlockMap[i7][0];
				i4 += headBlockToFootBlockMap[i7][1];
				if(world1.getBlockId(i2, i3, i4) != this.blockID) {
					return true;
				}

				i6 = world1.getBlockMetadata(i2, i3, i4);
			}

			if(!world1.worldProvider.canRespawnHere()) {
				double d16 = (double)i2 + 0.5D;
				double d17 = (double)i3 + 0.5D;
				double d11 = (double)i4 + 0.5D;
				world1.setBlockWithNotify(i2, i3, i4, 0);
				int i13 = getDirection(i6);
				i2 += headBlockToFootBlockMap[i13][0];
				i4 += headBlockToFootBlockMap[i13][1];
				if(world1.getBlockId(i2, i3, i4) == this.blockID) {
					world1.setBlockWithNotify(i2, i3, i4, 0);
					d16 = (d16 + (double)i2 + 0.5D) / 2.0D;
					d17 = (d17 + (double)i3 + 0.5D) / 2.0D;
					d11 = (d11 + (double)i4 + 0.5D) / 2.0D;
				}

				world1.newExplosion((Entity)null, (double)((float)i2 + 0.5F), (double)((float)i3 + 0.5F), (double)((float)i4 + 0.5F), 5.0F, true);
				return true;
			} else {
				if(isBedOccupied(i6)) {
					EntityPlayer entityPlayer14 = null;
					Iterator<EntityPlayer> iterator8 = world1.playerEntities.iterator();

					while(iterator8.hasNext()) {
						EntityPlayer entityPlayer9 = (EntityPlayer)iterator8.next();
						if(entityPlayer9.isPlayerSleeping()) {
							ChunkCoordinates chunkCoordinates10 = entityPlayer9.playerLocation;
							if(chunkCoordinates10.posX == i2 && chunkCoordinates10.posY == i3 && chunkCoordinates10.posZ == i4) {
								entityPlayer14 = entityPlayer9;
							}
						}
					}

					if(entityPlayer14 != null) {
						entityPlayer5.addChatMessage("tile.bed.occupied");
						return true;
					}

					setBedOccupied(world1, i2, i3, i4, false);
				}

				EnumStatus enumStatus15 = entityPlayer5.sleepInBedAt(i2, i3, i4);
				if(enumStatus15 == EnumStatus.OK) {
					setBedOccupied(world1, i2, i3, i4, true);
					return true;
				} else {
					if(enumStatus15 == EnumStatus.NOT_POSSIBLE_NOW) {
						entityPlayer5.addChatMessage("tile.bed.noSleep");
					} else if(enumStatus15 == EnumStatus.NOT_SAFE) {
						entityPlayer5.addChatMessage("tile.bed.notSafe");
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
			int i3 = getDirection(i2);
			int i4 = Direction.bedDirection[i3][i1];
			return isBlockFootOfBed(i2) ? (i4 == 2 ? this.blockIndexInTexture + 2 + 16 : (i4 != 5 && i4 != 4 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture + 1 + 16)) : (i4 == 3 ? this.blockIndexInTexture - 1 + 16 : (i4 != 5 && i4 != 4 ? this.blockIndexInTexture : this.blockIndexInTexture + 16));
		}
	}

	public int getRenderType() {
		return 14;
	}

	public boolean renderAsNormalBlock() {
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
		int i7 = getDirection(i6);
		if(isBlockFootOfBed(i6)) {
			if(world1.getBlockId(i2 - headBlockToFootBlockMap[i7][0], i3, i4 - headBlockToFootBlockMap[i7][1]) != this.blockID) {
				world1.setBlockWithNotify(i2, i3, i4, 0);
			}
		} else if(world1.getBlockId(i2 + headBlockToFootBlockMap[i7][0], i3, i4 + headBlockToFootBlockMap[i7][1]) != this.blockID) {
			world1.setBlockWithNotify(i2, i3, i4, 0);
			if(!world1.isRemote) {
				this.dropBlockAsItem(world1, i2, i3, i4, i6, 0);
			}
		}

	}

	public int idDropped(int i1, Random random2, int i3) {
		return isBlockFootOfBed(i1) ? 0 : Item.bed.shiftedIndex;
	}

	private void setBounds() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5625F, 1.0F);
	}

	public static boolean isBlockFootOfBed(int i0) {
		return (i0 & 8) != 0;
	}

	public static boolean isBedOccupied(int i0) {
		return (i0 & 4) != 0;
	}

	public static void setBedOccupied(World world0, int i1, int i2, int i3, boolean z4) {
		int i5 = world0.getBlockMetadata(i1, i2, i3);
		if(z4) {
			i5 |= 4;
		} else {
			i5 &= -5;
		}

		world0.setBlockMetadataWithNotify(i1, i2, i3, i5);
	}

	public static ChunkCoordinates getNearestEmptyChunkCoordinates(World world, int x0, int y0, int z0, int attempts) {
		int meta = world.getBlockMetadata(x0, y0, z0);
		int direction = BlockDirectional.getDirection(meta);

		for(int d = 0; d <= 1; ++d) {
			int x1 = x0 - headBlockToFootBlockMap[direction][0] * d - 1;
			int z1 = z0 - headBlockToFootBlockMap[direction][1] * d - 1;
			int x2 = x1 + 2;
			int z2 = z1 + 2;

			for(int x = x1; x <= x2; ++x) {
				for(int z = z1; z <= z2; ++z) {
					if(world.isBlockNormalCube(x, y0 - 1, z) && world.isAirBlock(x, y0, z) && world.isAirBlock(x, y0 + 1, z)) {
						if(attempts <= 0) {
							return new ChunkCoordinates(x, y0, z);
						}

						--attempts;
					}
				}
			}
		}

		return null;
	}

	public void dropBlockAsItemWithChance(World world1, int i2, int i3, int i4, int i5, float f6, int i7) {
		if(!isBlockFootOfBed(i5)) {
			super.dropBlockAsItemWithChance(world1, i2, i3, i4, i5, f6, 0);
		}

	}

	public int getMobilityFlag() {
		return 1;
	}

	@Override
	public int getTextureTop() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTextureBottom() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTextureFront() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTextureSide() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTextureBack() {
		// TODO Auto-generated method stub
		return 0;
	}
}
