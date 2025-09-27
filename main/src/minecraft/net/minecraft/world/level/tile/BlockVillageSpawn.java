package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.entity.TileEntityChest;

public class BlockVillageSpawn extends Block {
	private Random rand;
	int houseLoc1;
	int houseLoc2;

	protected BlockVillageSpawn(int i, int j) {
		super(i, j, Material.ground);
	}

	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer) {
		this.blockCreate(world, i, j, k);
		return super.blockActivated(world, i, j, k, entityplayer);
	}

	public void blockCreate(World world, int i, int j, int k) {
		this.rand = new Random();

		int boss;
		int goblinlord;
		int itemstack;
		for(boss = -1; boss < 23; ++boss) {
			for(goblinlord = 0; goblinlord < 31; ++goblinlord) {
				for(itemstack = 0; itemstack < 12; ++itemstack) {
					if(itemstack != 1 || boss >= 9 && boss <= 12 || goblinlord >= 9 && goblinlord <= 19) {
						world.setBlock(i + boss, j + itemstack, k + goblinlord, 0);
					} else {
						if(world.getBlockId(i + boss, j + itemstack, k + goblinlord) == Block.snow.blockID || world.getBlockId(i + boss, j + itemstack, k + goblinlord) == Block.tallGrass.blockID) {
							continue;
						}

						world.setBlock(i + boss, j + itemstack, k + goblinlord, 0);
					}

					if(itemstack == 0) {
						world.setBlock(i + boss, j - itemstack, k + goblinlord, Block.grass.blockID);
					} else if(itemstack > 0 && itemstack < 3) {
						world.setBlock(i + boss, j - itemstack, k + goblinlord, Block.dirt.blockID);
					} else {
						world.setBlock(i + boss, j - itemstack, k + goblinlord, Block.stone.blockID);
					}
				}
			}
		}

		world.setBlock(i + 11, j + 1, k + 16, Block.totemR.blockID);
		world.setBlock(i + 11, j + 2, k + 16, Block.totemG.blockID);
		world.setBlock(i + 11, j + 3, k + 16, Block.totemB.blockID);
		world.setBlock(i + 11, j + 4, k + 16, Block.totemY.blockID);

		for(boss = 0; boss < 5; ++boss) {
			for(goblinlord = 0; goblinlord < 10; ++goblinlord) {
				if(this.rand.nextInt(6) == 1) {
					world.setBlock(i + 9 + boss, j, k + 9 + goblinlord, Block.cobblestoneMossy.blockID);
				} else {
					world.setBlock(i + 9 + boss, j, k + 9 + goblinlord, Block.cobblestone.blockID);
				}
			}
		}

		world.setBlock(i + 11, j, k + 11, Block.netherrack.blockID);
		world.setBlock(i + 11, j + 1, k + 11, Block.fire.blockID);
		world.setBlock(i + 11, j + 1, k + 12, Block.stairDouble.blockID);
		world.setBlock(i + 11, j + 1, k + 10, Block.stairDouble.blockID);
		world.setBlock(i + 10, j + 1, k + 11, Block.stairDouble.blockID);
		world.setBlock(i + 12, j + 1, k + 11, Block.stairDouble.blockID);
		world.setBlock(i + 12, j + 1, k + 17, Block.fence.blockID);
		world.setBlock(i + 10, j + 1, k + 17, Block.fence.blockID);
		world.setBlock(i + 12, j + 1, k + 15, Block.fence.blockID);
		world.setBlock(i + 10, j + 1, k + 15, Block.fence.blockID);
		world.setBlock(i + 12, j + 2, k + 17, Block.torchWood.blockID);
		world.setBlock(i + 10, j + 2, k + 17, Block.torchWood.blockID);
		world.setBlock(i + 12, j + 2, k + 15, Block.torchWood.blockID);
		world.setBlock(i + 10, j + 2, k + 15, Block.torchWood.blockID);

		for(boss = 0; boss < 3; ++boss) {
			for(goblinlord = 0; goblinlord < 3; ++goblinlord) {
				world.setBlock(i + 10 + boss, j, k + 15 + goblinlord, Block.grass.blockID);
			}
		}

		for(boss = 3; boss <= 14; boss += 11) {
			for(goblinlord = 3; goblinlord <= 19; goblinlord += 16) {
				this.houseLoc1 = boss;
				this.houseLoc2 = goblinlord;

				for(itemstack = 1; itemstack <= 2; ++itemstack) {
					world.setBlock(i + this.houseLoc1 + 0, j + itemstack, k + this.houseLoc2 + 3, Block.planks.blockID);
					world.setBlock(i + this.houseLoc1 + 0, j + itemstack, k + this.houseLoc2 + 2, Block.planks.blockID);
					world.setBlock(i + this.houseLoc1 + 2, j + itemstack, k + this.houseLoc2 + 5, Block.planks.blockID);
					world.setBlock(i + this.houseLoc1 + 2, j + itemstack, k + this.houseLoc2, Block.planks.blockID);
					world.setBlock(i + this.houseLoc1 + 3, j + itemstack, k + this.houseLoc2 + 5, Block.planks.blockID);
					world.setBlock(i + this.houseLoc1 + 3, j + itemstack, k + this.houseLoc2, Block.planks.blockID);
					world.setBlock(i + this.houseLoc1 + 5, j + itemstack, k + this.houseLoc2 + 3, Block.planks.blockID);
					world.setBlock(i + this.houseLoc1 + 5, j + itemstack, k + this.houseLoc2 + 2, Block.planks.blockID);
					world.setBlock(i + this.houseLoc1 + 4, j + itemstack, k + this.houseLoc2 + 4, Block.planks.blockID);
					world.setBlock(i + this.houseLoc1 + 4, j + itemstack, k + this.houseLoc2 + 1, Block.planks.blockID);
					world.setBlock(i + this.houseLoc1 + 1, j + itemstack, k + this.houseLoc2 + 4, Block.planks.blockID);
					world.setBlock(i + this.houseLoc1 + 1, j + itemstack, k + this.houseLoc2 + 1, Block.planks.blockID);
				}

				world.setBlock(i + this.houseLoc1 + 1, j + 3, k + this.houseLoc2 + 3, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + 3, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + 3, k + this.houseLoc2 + 1, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + 3, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + 3, k + this.houseLoc2 + 1, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + 3, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + 3, k + this.houseLoc2 + 3, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + 3, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j, k + this.houseLoc2 + 1, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j, k + this.houseLoc2 + 1, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + 1, k + this.houseLoc2 + 3, 0);
				world.setBlock(i + this.houseLoc1 + 1, j + 1, k + this.houseLoc2 + 2, 0);
				world.setBlock(i + this.houseLoc1 + 2, j + 1, k + this.houseLoc2 + 1, 0);
				world.setBlock(i + this.houseLoc1 + 2, j + 1, k + this.houseLoc2 + 4, 0);
				world.setBlock(i + this.houseLoc1 + 3, j + 1, k + this.houseLoc2 + 1, 0);
				world.setBlock(i + this.houseLoc1 + 3, j + 1, k + this.houseLoc2 + 4, 0);
				world.setBlock(i + this.houseLoc1 + 4, j + 1, k + this.houseLoc2 + 3, 0);
				world.setBlock(i + this.houseLoc1 + 4, j + 1, k + this.houseLoc2 + 2, 0);
				world.setBlock(i + this.houseLoc1 + 3, j + 4, k + this.houseLoc2 + 3, Block.gobSlabWood.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + 4, k + this.houseLoc2 + 2, Block.gobSlabWood.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + 4, k + this.houseLoc2 + 3, Block.gobSlabWood.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + 4, k + this.houseLoc2 + 2, Block.gobSlabWood.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + 1, k + this.houseLoc2 + 3, 0);
				world.setBlock(i + this.houseLoc1 + 3, j + 1, k + this.houseLoc2 + 2, 0);
				world.setBlock(i + this.houseLoc1 + 2, j + 1, k + this.houseLoc2 + 3, 0);
				world.setBlock(i + this.houseLoc1 + 2, j + 1, k + this.houseLoc2 + 2, 0);
			}
		}

		this.houseLoc1 = 3;
		this.houseLoc2 = 3;
		if(this.rand.nextInt(4) == 1) {
			world.setBlock(i + this.houseLoc1 + 3, j + 3, k + this.houseLoc2 + 3, Block.web.blockID);
		}

		if(this.rand.nextInt(5) == 0) {
			world.setBlock(i + this.houseLoc1 + 3, j + 3, k + this.houseLoc2 + 2, Block.web.blockID);
		}

		if(this.rand.nextInt(4) == 0) {
			world.setBlock(i + this.houseLoc1 + 2, j + 3, k + this.houseLoc2 + 3, Block.web.blockID);
		}

		if(this.rand.nextInt(5) == 0) {
			world.setBlock(i + this.houseLoc1 + 2, j + 3, k + this.houseLoc2 + 2, Block.web.blockID);
		}

		world.setBlock(i + this.houseLoc1 + 4, j + 3, k + this.houseLoc2 + 4, Block.planks.blockID);
		world.setBlock(i + this.houseLoc1 + 4, j + 2, k + this.houseLoc2 + 4, 0);
		world.setBlock(i + this.houseLoc1 + 4, j + 1, k + this.houseLoc2 + 4, 0);
		world.setBlock(i + this.houseLoc1 + 5, j + 2, k + this.houseLoc2 + 4, Block.torchWood.blockID);
		world.setBlock(i + this.houseLoc1 + 4, j + 2, k + this.houseLoc2 + 5, Block.torchWood.blockID);
		world.setBlock(i + this.houseLoc1 + 2, j + 2, k + this.houseLoc2 + 1, Block.torchWood.blockID);
		world.setBlock(i + this.houseLoc1 + 1, j + 2, k + this.houseLoc2 + 2, Block.torchWood.blockID);
		world.setBlock(i + this.houseLoc1 + 2, j + 1, k + this.houseLoc2 + 2, Block.fence.blockID);
		world.setBlock(i + this.houseLoc1 + 2, j + 2, k + this.houseLoc2 + 2, Block.fence.blockID);
		world.setBlock(i + this.houseLoc1 + 2, j + 3, k + this.houseLoc2 + 2, Block.fence.blockID);
		TileEntityChest tileEntityChest10;
		ItemStack itemStack11;
		if(this.rand.nextInt(1) == 0) {
			world.setBlockWithNotify(i + this.houseLoc1 + 1, j + 1, k + this.houseLoc2 + 3, Block.chest.blockID);
			tileEntityChest10 = (TileEntityChest)world.getBlockTileEntity(i + this.houseLoc1 + 1, j + 1, k + this.houseLoc2 + 3);

			for(goblinlord = 0; goblinlord <= 4; ++goblinlord) {
				itemStack11 = this.pickCheckLootItem(this.rand);
				if(itemStack11 != null) {
					tileEntityChest10.setInventorySlotContents(this.rand.nextInt(tileEntityChest10.getSizeInventory()), itemStack11);
				}
			}
		}

		world.setBlockWithNotify(i + this.houseLoc1 + 4, j, k + this.houseLoc2 + 4, Block.MobGSpawner.blockID);
		this.houseLoc1 = 14;
		this.houseLoc2 = 3;
		if(this.rand.nextInt(4) == 1) {
			world.setBlock(i + this.houseLoc1 + 3, j + 3, k + this.houseLoc2 + 3, Block.web.blockID);
		}

		if(this.rand.nextInt(5) == 0) {
			world.setBlock(i + this.houseLoc1 + 3, j + 3, k + this.houseLoc2 + 2, Block.web.blockID);
		}

		if(this.rand.nextInt(4) == 0) {
			world.setBlock(i + this.houseLoc1 + 2, j + 3, k + this.houseLoc2 + 3, Block.web.blockID);
		}

		if(this.rand.nextInt(5) == 0) {
			world.setBlock(i + this.houseLoc1 + 2, j + 3, k + this.houseLoc2 + 2, Block.web.blockID);
		}

		world.setBlock(i + this.houseLoc1 + 1, j + 3, k + this.houseLoc2 + 4, Block.planks.blockID);
		world.setBlock(i + this.houseLoc1 + 1, j + 2, k + this.houseLoc2 + 4, 0);
		world.setBlock(i + this.houseLoc1 + 1, j + 1, k + this.houseLoc2 + 4, 0);
		world.setBlock(i + this.houseLoc1 + 1, j + 2, k + this.houseLoc2 + 5, Block.torchWood.blockID);
		world.setBlock(i + this.houseLoc1 + 0, j + 2, k + this.houseLoc2 + 4, Block.torchWood.blockID);
		world.setBlock(i + this.houseLoc1 + 3, j + 2, k + this.houseLoc2 + 1, Block.torchWood.blockID);
		world.setBlock(i + this.houseLoc1 + 4, j + 2, k + this.houseLoc2 + 2, Block.torchWood.blockID);
		world.setBlock(i + this.houseLoc1 + 3, j + 1, k + this.houseLoc2 + 2, Block.fence.blockID);
		world.setBlock(i + this.houseLoc1 + 3, j + 2, k + this.houseLoc2 + 2, Block.fence.blockID);
		world.setBlock(i + this.houseLoc1 + 3, j + 3, k + this.houseLoc2 + 2, Block.fence.blockID);
		if(this.rand.nextInt(2) == 0) {
			world.setBlockWithNotify(i + this.houseLoc1 + 4, j + 1, k + this.houseLoc2 + 2, Block.chest.blockID);
			tileEntityChest10 = (TileEntityChest)world.getBlockTileEntity(i + this.houseLoc1 + 4, j + 1, k + this.houseLoc2 + 2);

			for(goblinlord = 0; goblinlord <= 4; ++goblinlord) {
				itemStack11 = this.pickCheckLootItem(this.rand);
				if(itemStack11 != null) {
					tileEntityChest10.setInventorySlotContents(this.rand.nextInt(tileEntityChest10.getSizeInventory()), itemStack11);
				}
			}
		}

		world.setBlockWithNotify(i + this.houseLoc1 + 1, j, k + this.houseLoc2 + 4, Block.MobGSpawner.blockID);
		this.houseLoc1 = 3;
		this.houseLoc2 = 19;
		if(this.rand.nextInt(4) == 1) {
			world.setBlock(i + this.houseLoc1 + 3, j + 3, k + this.houseLoc2 + 3, Block.web.blockID);
		}

		if(this.rand.nextInt(5) == 0) {
			world.setBlock(i + this.houseLoc1 + 3, j + 3, k + this.houseLoc2 + 2, Block.web.blockID);
		}

		if(this.rand.nextInt(4) == 0) {
			world.setBlock(i + this.houseLoc1 + 2, j + 3, k + this.houseLoc2 + 3, Block.web.blockID);
		}

		if(this.rand.nextInt(5) == 0) {
			world.setBlock(i + this.houseLoc1 + 2, j + 3, k + this.houseLoc2 + 2, Block.web.blockID);
		}

		world.setBlock(i + this.houseLoc1 + 4, j + 3, k + this.houseLoc2 + 1, Block.planks.blockID);
		world.setBlock(i + this.houseLoc1 + 4, j + 2, k + this.houseLoc2 + 1, 0);
		world.setBlock(i + this.houseLoc1 + 4, j + 1, k + this.houseLoc2 + 1, 0);
		world.setBlock(i + this.houseLoc1 + 5, j + 2, k + this.houseLoc2 + 1, Block.torchWood.blockID);
		world.setBlock(i + this.houseLoc1 + 4, j + 2, k + this.houseLoc2 + 0, Block.torchWood.blockID);
		world.setBlock(i + this.houseLoc1 + 1, j + 2, k + this.houseLoc2 + 3, Block.torchWood.blockID);
		world.setBlock(i + this.houseLoc1 + 2, j + 2, k + this.houseLoc2 + 4, Block.torchWood.blockID);
		world.setBlock(i + this.houseLoc1 + 2, j + 1, k + this.houseLoc2 + 3, Block.fence.blockID);
		world.setBlock(i + this.houseLoc1 + 2, j + 2, k + this.houseLoc2 + 3, Block.fence.blockID);
		world.setBlock(i + this.houseLoc1 + 2, j + 3, k + this.houseLoc2 + 3, Block.fence.blockID);
		if(this.rand.nextInt(1) == 0) {
			world.setBlockWithNotify(i + this.houseLoc1 + 1, j + 1, k + this.houseLoc2 + 2, Block.chest.blockID);
			tileEntityChest10 = (TileEntityChest)world.getBlockTileEntity(i + this.houseLoc1 + 1, j + 1, k + this.houseLoc2 + 2);

			for(goblinlord = 0; goblinlord <= 4; ++goblinlord) {
				itemStack11 = this.pickCheckLootItem(this.rand);
				if(itemStack11 != null) {
					tileEntityChest10.setInventorySlotContents(this.rand.nextInt(tileEntityChest10.getSizeInventory()), itemStack11);
				}
			}
		}

		world.setBlockWithNotify(i + this.houseLoc1 + 4, j, k + this.houseLoc2 + 1, Block.MobGSpawner.blockID);
		this.houseLoc1 = 14;
		this.houseLoc2 = 19;
		if(this.rand.nextInt(4) == 1) {
			world.setBlock(i + this.houseLoc1 + 3, j + 3, k + this.houseLoc2 + 3, Block.web.blockID);
		}

		if(this.rand.nextInt(5) == 0) {
			world.setBlock(i + this.houseLoc1 + 3, j + 3, k + this.houseLoc2 + 2, Block.web.blockID);
		}

		if(this.rand.nextInt(4) == 0) {
			world.setBlock(i + this.houseLoc1 + 2, j + 3, k + this.houseLoc2 + 3, Block.web.blockID);
		}

		if(this.rand.nextInt(5) == 0) {
			world.setBlock(i + this.houseLoc1 + 2, j + 3, k + this.houseLoc2 + 2, Block.web.blockID);
		}

		world.setBlock(i + this.houseLoc1 + 1, j + 3, k + this.houseLoc2 + 1, Block.planks.blockID);
		world.setBlock(i + this.houseLoc1 + 1, j + 2, k + this.houseLoc2 + 1, 0);
		world.setBlock(i + this.houseLoc1 + 1, j + 1, k + this.houseLoc2 + 1, 0);
		world.setBlock(i + this.houseLoc1 + 0, j + 2, k + this.houseLoc2 + 1, Block.torchWood.blockID);
		world.setBlock(i + this.houseLoc1 + 1, j + 2, k + this.houseLoc2 + 0, Block.torchWood.blockID);
		world.setBlock(i + this.houseLoc1 + 4, j + 2, k + this.houseLoc2 + 3, Block.torchWood.blockID);
		world.setBlock(i + this.houseLoc1 + 3, j + 2, k + this.houseLoc2 + 4, Block.torchWood.blockID);
		world.setBlock(i + this.houseLoc1 + 3, j + 1, k + this.houseLoc2 + 3, Block.fence.blockID);
		world.setBlock(i + this.houseLoc1 + 3, j + 2, k + this.houseLoc2 + 3, Block.fence.blockID);
		world.setBlock(i + this.houseLoc1 + 3, j + 3, k + this.houseLoc2 + 3, Block.fence.blockID);
		if(this.rand.nextInt(3) == 0) {
			world.setBlockWithNotify(i + this.houseLoc1 + 4, j + 1, k + this.houseLoc2 + 2, Block.chest.blockID);
			tileEntityChest10 = (TileEntityChest)world.getBlockTileEntity(i + this.houseLoc1 + 4, j + 1, k + this.houseLoc2 + 2);

			for(goblinlord = 0; goblinlord <= 4; ++goblinlord) {
				itemStack11 = this.pickCheckLootItem(this.rand);
				if(itemStack11 != null) {
					tileEntityChest10.setInventorySlotContents(this.rand.nextInt(tileEntityChest10.getSizeInventory()), itemStack11);
				}
			}
		}

		world.setBlockWithNotify(i + this.houseLoc1 + 1, j, k + this.houseLoc2 + 1, Block.MobGSpawner.blockID);
		this.houseLoc1 = 16;
		this.houseLoc2 = 10;
		if(this.rand.nextInt(2) == 0) {
			for(boss = 1; boss <= 2; ++boss) {
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 0, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 0, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 6, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 6, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
			}

			world.setBlockWithNotify(i + this.houseLoc1 + 5, j + 1, k + this.houseLoc2 + 3, Block.fence.blockID);

			for(boss = 3; boss <= 3; ++boss) {
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss + 1, k + this.houseLoc2 + 3, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
			}

			for(boss = 0; boss <= 0; ++boss) {
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 1, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 1, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 5, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 5, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
			}

			for(boss = 4; boss <= 4; ++boss) {
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss + 1, k + this.houseLoc2 + 3, Block.gobSlabWood.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
			}

			for(boss = 0; boss <= 0; ++boss) {
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
			}

			world.setBlock(i + this.houseLoc1 + 2, j + 5, k + this.houseLoc2 + 3, Block.gobSlabWood.blockID);
			world.setBlock(i + this.houseLoc1 + 2, j, k + this.houseLoc2 + 3, Block.cobblestone.blockID);

			for(boss = 1; boss <= 4; ++boss) {
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 3, Block.fence.blockID);
			}

			world.setBlock(i + this.houseLoc1 - 1, j + 2, k + this.houseLoc2 + 4, Block.torchWood.blockID);
			world.setBlock(i + this.houseLoc1 - 1, j + 2, k + this.houseLoc2 + 2, Block.torchWood.blockID);
			world.setBlock(i + this.houseLoc1 + 4, j + 2, k + this.houseLoc2 + 3, Block.torchWood.blockID);
			world.setBlockWithNotify(i + this.houseLoc1 + 0, j, k + this.houseLoc2 + 3, Block.MobGRSpawner.blockID);
			if(this.rand.nextInt(2) == 0) {
				world.setBlockWithNotify(i + this.houseLoc1 + 3, j + 1, k + this.houseLoc2 + 5, Block.chest.blockID);
				tileEntityChest10 = (TileEntityChest)world.getBlockTileEntity(i + this.houseLoc1 + 3, j + 1, k + this.houseLoc2 + 5);

				for(goblinlord = 0; goblinlord <= 4; ++goblinlord) {
					itemStack11 = this.pickCheckLootItemRider(this.rand);
					if(itemStack11 != null) {
						tileEntityChest10.setInventorySlotContents(this.rand.nextInt(tileEntityChest10.getSizeInventory()), itemStack11);
					}
				}
			}

			for(boss = 0; boss <= 2; ++boss) {
				for(goblinlord = 0; goblinlord <= 4; ++goblinlord) {
					for(itemstack = 1; itemstack <= 3; ++itemstack) {
						world.setBlock(i + this.houseLoc1 + goblinlord + 6, j + itemstack, k + this.houseLoc2 + boss + 2, 0);
					}
				}
			}

			for(boss = 0; boss <= 4; ++boss) {
				for(goblinlord = 0; goblinlord <= 6; ++goblinlord) {
					for(itemstack = 0; itemstack <= 5; ++itemstack) {
						world.setBlock(i + this.houseLoc1 + goblinlord + 5, j - itemstack, k + this.houseLoc2 + boss + 1, Block.dirt.blockID);
					}
				}
			}

			for(boss = 0; boss <= 5; ++boss) {
				world.setBlock(i + this.houseLoc1 + boss + 5, j + 1, k + this.houseLoc2 + 5, Block.fence.blockID);
				world.setBlock(i + this.houseLoc1 + boss + 5, j, k + this.houseLoc2 + 5, Block.cobblestone.blockID);
			}

			for(boss = 0; boss <= 5; ++boss) {
				world.setBlock(i + this.houseLoc1 + boss + 5, j + 1, k + this.houseLoc2 + 1, Block.fence.blockID);
				world.setBlock(i + this.houseLoc1 + boss + 5, j, k + this.houseLoc2 + 1, Block.cobblestone.blockID);
			}

			for(boss = 0; boss <= 3; ++boss) {
				world.setBlock(i + this.houseLoc1 + 11, j + 1, k + this.houseLoc2 + boss + 1, Block.fence.blockID);
				world.setBlock(i + this.houseLoc1 + 11, j, k + this.houseLoc2 + boss + 1, Block.cobblestone.blockID);
			}

			world.setBlock(i + this.houseLoc1 + 11, j + 1, k + this.houseLoc2 + 5, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 11, j + 1, k + this.houseLoc2 + 1, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 11, j + 2, k + this.houseLoc2 + 5, Block.fence.blockID);
			world.setBlock(i + this.houseLoc1 + 10, j + 2, k + this.houseLoc2 + 1, Block.fence.blockID);
			world.setBlock(i + this.houseLoc1 + 10, j + 2, k + this.houseLoc2 + 5, Block.fence.blockID);
			world.setBlock(i + this.houseLoc1 + 11, j + 2, k + this.houseLoc2 + 2, Block.fence.blockID);
			world.setBlock(i + this.houseLoc1 + 11, j + 2, k + this.houseLoc2 + 4, Block.fence.blockID);
			world.setBlock(i + this.houseLoc1 + 11, j + 2, k + this.houseLoc2 + 1, Block.fence.blockID);
			world.setBlock(i + this.houseLoc1 + 11, j + 3, k + this.houseLoc2 + 5, Block.torchWood.blockID);
			world.setBlock(i + this.houseLoc1 + 11, j + 3, k + this.houseLoc2 + 1, Block.torchWood.blockID);

			for(boss = 0; boss <= 1; ++boss) {
				goblinlord = this.rand.nextInt(3);
				itemstack = this.rand.nextInt(2);
				EntityDirewolf i2 = new EntityDirewolf(world);
				i2.setLocationAndAngles((double)(i + this.houseLoc1 + 7 + goblinlord), (double)(j + 1), (double)(k + this.houseLoc2 + 2 + itemstack), world.rand.nextFloat() * 360.0F, 0.0F);
				i2.setPosition((double)(i + this.houseLoc1 + 7 + goblinlord), (double)(j + 1), (double)(k + this.houseLoc2 + 2 + itemstack));
				world.spawnEntityInWorld(i2);
			}
		} else {
			for(boss = 1; boss <= 2; ++boss) {
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 0, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 0, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 6, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 6, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
			}

			for(boss = 3; boss <= 3; ++boss) {
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
			}

			for(boss = 0; boss <= 0; ++boss) {
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 1, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 1, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 5, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 5, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
			}

			for(boss = 4; boss <= 4; ++boss) {
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
			}

			for(boss = 0; boss <= 0; ++boss) {
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
			}

			world.setBlock(i + this.houseLoc1 + 2, j + 5, k + this.houseLoc2 + 3, Block.gobSlabWood.blockID);
			world.setBlock(i + this.houseLoc1 + 2, j, k + this.houseLoc2 + 3, Block.cobblestone.blockID);

			for(boss = 1; boss <= 4; ++boss) {
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 3, Block.fence.blockID);
			}

			world.setBlock(i + this.houseLoc1 - 1, j + 2, k + this.houseLoc2 + 4, Block.torchWood.blockID);
			world.setBlock(i + this.houseLoc1 - 1, j + 2, k + this.houseLoc2 + 2, Block.torchWood.blockID);
			world.setBlock(i + this.houseLoc1 + 4, j + 2, k + this.houseLoc2 + 3, Block.torchWood.blockID);
			world.setBlockWithNotify(i + this.houseLoc1 + 0, j, k + this.houseLoc2 + 3, Block.MobGSpawner.blockID);
			if(this.rand.nextInt(2) == 0) {
				world.setBlockWithNotify(i + this.houseLoc1 + 4, j + 1, k + this.houseLoc2 + 3, Block.chest.blockID);
				tileEntityChest10 = (TileEntityChest)world.getBlockTileEntity(i + this.houseLoc1 + 4, j + 1, k + this.houseLoc2 + 3);

				for(goblinlord = 0; goblinlord <= 4; ++goblinlord) {
					itemStack11 = this.pickCheckLootItem(this.rand);
					if(itemStack11 != null) {
						tileEntityChest10.setInventorySlotContents(this.rand.nextInt(tileEntityChest10.getSizeInventory()), itemStack11);
					}
				}
			}
		}

		this.houseLoc1 = 0;
		this.houseLoc2 = 10;
		if(this.rand.nextInt(2) == 0) {
			for(boss = 1; boss <= 2; ++boss) {
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 0, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 0, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 6, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 6, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
			}

			for(boss = 3; boss <= 3; ++boss) {
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss + 1, k + this.houseLoc2 + 3, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
			}

			for(boss = 0; boss <= 0; ++boss) {
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 1, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 1, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 5, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 5, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
			}

			for(boss = 4; boss <= 4; ++boss) {
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss + 1, k + this.houseLoc2 + 3, Block.gobSlabWood.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
			}

			for(boss = 0; boss <= 0; ++boss) {
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
			}

			world.setBlock(i + this.houseLoc1 + 2, j + 5, k + this.houseLoc2 + 3, Block.gobSlabWood.blockID);
			world.setBlock(i + this.houseLoc1 + 2, j, k + this.houseLoc2 + 3, Block.cobblestone.blockID);

			for(boss = 1; boss <= 4; ++boss) {
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 3, Block.fence.blockID);
			}

			world.setBlock(i + this.houseLoc1 + 6, j + 2, k + this.houseLoc2 + 4, Block.torchWood.blockID);
			world.setBlock(i + this.houseLoc1 + 6, j + 2, k + this.houseLoc2 + 2, Block.torchWood.blockID);
			world.setBlock(i + this.houseLoc1 + 4, j + 2, k + this.houseLoc2 + 3, Block.torchWood.blockID);
			world.setBlockWithNotify(i + this.houseLoc1 + 5, j, k + this.houseLoc2 + 3, Block.MobGMSpawner.blockID);
			world.setBlockWithNotify(i + this.houseLoc1 + 2, j + 1, k + this.houseLoc2 + 5, Block.chest.blockID);
			tileEntityChest10 = (TileEntityChest)world.getBlockTileEntity(i + this.houseLoc1 + 2, j + 1, k + this.houseLoc2 + 5);

			for(goblinlord = 0; goblinlord <= 4; ++goblinlord) {
				itemStack11 = this.pickCheckLootItemMiner(this.rand);
				if(itemStack11 != null) {
					tileEntityChest10.setInventorySlotContents(this.rand.nextInt(tileEntityChest10.getSizeInventory()), itemStack11);
				}
			}

			int i12;
			for(goblinlord = -6; goblinlord <= -4; ++goblinlord) {
				for(itemstack = -3; itemstack <= 3; ++itemstack) {
					for(i12 = 1; i12 <= 5; ++i12) {
						world.setBlock(i + this.houseLoc1 + goblinlord, j + itemstack, k + this.houseLoc2 + i12, 0);
					}
				}
			}

			for(goblinlord = -3; goblinlord <= -1; ++goblinlord) {
				for(itemstack = -3; itemstack <= 3; ++itemstack) {
					for(i12 = 2; i12 <= 4; ++i12) {
						world.setBlock(i + this.houseLoc1 + goblinlord, j + itemstack, k + this.houseLoc2 + i12, 0);
					}
				}
			}

			for(goblinlord = 2; goblinlord <= 4; ++goblinlord) {
				world.setBlock(i + this.houseLoc1 - 1, j - 1, k + this.houseLoc2 + goblinlord, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 - 2, j - 2, k + this.houseLoc2 + goblinlord, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 - 3, j - 3, k + this.houseLoc2 + goblinlord, Block.cobblestone.blockID);
			}

			world.setBlock(i + this.houseLoc1 - 6, j - 3, k + this.houseLoc2 + 5, Block.stone.blockID);
			world.setBlock(i + this.houseLoc1 - 5, j - 3, k + this.houseLoc2 + 1, Block.stone.blockID);
			world.setBlock(i + this.houseLoc1 - 6, j - 3, k + this.houseLoc2 + 1, Block.stone.blockID);
			world.setBlock(i + this.houseLoc1 - 6, j - 2, k + this.houseLoc2 + 1, Block.stone.blockID);

			for(goblinlord = -3; goblinlord <= 0; ++goblinlord) {
				world.setBlock(i + this.houseLoc1 + goblinlord, j + 3, k + this.houseLoc2 + 1, 0);
				world.setBlock(i + this.houseLoc1 + goblinlord, j + 3, k + this.houseLoc2 + 5, 0);
				world.setBlock(i + this.houseLoc1 + goblinlord, j + 2, k + this.houseLoc2 + 1, 0);
				world.setBlock(i + this.houseLoc1 + goblinlord, j + 2, k + this.houseLoc2 + 5, 0);
				world.setBlock(i + this.houseLoc1 + goblinlord, j + 1, k + this.houseLoc2 + 1, Block.fence.blockID);
				world.setBlock(i + this.houseLoc1 + goblinlord, j + 1, k + this.houseLoc2 + 5, Block.fence.blockID);
				world.setBlock(i + this.houseLoc1 + goblinlord, j, k + this.houseLoc2 + 1, Block.grass.blockID);
				world.setBlock(i + this.houseLoc1 + goblinlord, j, k + this.houseLoc2 + 5, Block.grass.blockID);

				for(itemstack = -3; itemstack <= -1; ++itemstack) {
					world.setBlock(i + this.houseLoc1 + goblinlord, j + itemstack, k + this.houseLoc2 + 1, Block.dirt.blockID);
					world.setBlock(i + this.houseLoc1 + goblinlord, j + itemstack, k + this.houseLoc2 + 5, Block.dirt.blockID);
				}
			}

			for(goblinlord = -7; goblinlord <= -3; ++goblinlord) {
				world.setBlock(i + this.houseLoc1 + goblinlord, j + 3, k + this.houseLoc2 + 0, 0);
				world.setBlock(i + this.houseLoc1 + goblinlord, j + 3, k + this.houseLoc2 + 6, 0);
				world.setBlock(i + this.houseLoc1 + goblinlord, j + 2, k + this.houseLoc2 + 0, 0);
				world.setBlock(i + this.houseLoc1 + goblinlord, j + 2, k + this.houseLoc2 + 6, 0);
				world.setBlock(i + this.houseLoc1 + goblinlord, j + 1, k + this.houseLoc2 + 0, Block.fence.blockID);
				world.setBlock(i + this.houseLoc1 + goblinlord, j + 1, k + this.houseLoc2 + 6, Block.fence.blockID);
				world.setBlock(i + this.houseLoc1 + goblinlord, j, k + this.houseLoc2 + 0, Block.grass.blockID);
				world.setBlock(i + this.houseLoc1 + goblinlord, j, k + this.houseLoc2 + 6, Block.grass.blockID);

				for(itemstack = -3; itemstack <= -1; ++itemstack) {
					world.setBlock(i + this.houseLoc1 + goblinlord, j + itemstack, k + this.houseLoc2 + 0, Block.dirt.blockID);
					world.setBlock(i + this.houseLoc1 + goblinlord, j + itemstack, k + this.houseLoc2 + 6, Block.dirt.blockID);
				}
			}

			for(goblinlord = 1; goblinlord <= 5; ++goblinlord) {
				world.setBlock(i + this.houseLoc1 - 7, j + 2, k + this.houseLoc2 + goblinlord, 0);
				world.setBlock(i + this.houseLoc1 - 7, j + 3, k + this.houseLoc2 + goblinlord, 0);
				world.setBlock(i + this.houseLoc1 - 7, j + 1, k + this.houseLoc2 + goblinlord, Block.fence.blockID);
				world.setBlock(i + this.houseLoc1 - 7, j, k + this.houseLoc2 + goblinlord, Block.grass.blockID);
			}

			for(goblinlord = 0; goblinlord <= 50; ++goblinlord) {
				if(goblinlord == 50) {
					itemstack = 0;

					while(true) {
						if(itemstack > 2) {
							world.setBlock(i + this.houseLoc1 - 7 - goblinlord + 2, j - goblinlord - 1, k + this.houseLoc2 + 3, Block.MobGMSpawner.blockID);
							break;
						}

						for(i12 = 0; i12 <= 5; ++i12) {
							for(int i3 = 0; i3 <= 3; ++i3) {
								world.setBlock(i + this.houseLoc1 - 7 - goblinlord + i12, j - goblinlord + i3, k + this.houseLoc2 + 3 + itemstack, 0);
								world.setBlock(i + this.houseLoc1 - 7 - goblinlord + i12, j - goblinlord + i3, k + this.houseLoc2 + 3 - itemstack, 0);
							}
						}

						++itemstack;
					}
				}

				if(!world.isAirBlock(i + this.houseLoc1 - 7 - goblinlord, j - 2 - goblinlord, k + this.houseLoc2 + 3)) {
					world.setBlock(i + this.houseLoc1 - 7 - goblinlord, j - 2 - goblinlord, k + this.houseLoc2 + 3, 0);
					world.setBlock(i + this.houseLoc1 - 7 - goblinlord, j - 3 - goblinlord, k + this.houseLoc2 + 3, 0);
					world.setBlock(i + this.houseLoc1 - 7 - goblinlord, j - 4 - goblinlord, k + this.houseLoc2 + 3, 0);
					if(goblinlord % 6 == 0) {
						world.setBlockWithNotify(i + this.houseLoc1 - 7 - goblinlord + 1, j - 3 - goblinlord + 1, k + this.houseLoc2 + 3, Block.torchWood.blockID);
					}
				} else {
					for(itemstack = 0; itemstack <= 15; ++itemstack) {
						if(!world.isAirBlock(i + this.houseLoc1 - 7 - goblinlord, j - 2 - goblinlord - itemstack, k + this.houseLoc2 + 3)) {
							world.setBlockWithNotify(i + this.houseLoc1 - 7 - goblinlord, j - 2 - goblinlord - itemstack, k + this.houseLoc2 + 3, Block.MobGMSpawner.blockID);
							goblinlord = 51;
						}
					}
				}
			}
		} else {
			for(boss = 1; boss <= 2; ++boss) {
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 0, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 0, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 6, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 6, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
			}

			for(boss = 3; boss <= 3; ++boss) {
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
			}

			for(boss = 0; boss <= 0; ++boss) {
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 1, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 1, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 5, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 5, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
			}

			for(boss = 4; boss <= 4; ++boss) {
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
			}

			for(boss = 0; boss <= 0; ++boss) {
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
				world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
			}

			world.setBlock(i + this.houseLoc1 + 3, j + 5, k + this.houseLoc2 + 3, Block.gobSlabWood.blockID);
			world.setBlock(i + this.houseLoc1 + 2, j, k + this.houseLoc2 + 3, Block.cobblestone.blockID);

			for(boss = 1; boss <= 4; ++boss) {
				world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 3, Block.fence.blockID);
			}

			world.setBlock(i + this.houseLoc1 + 6, j + 2, k + this.houseLoc2 + 4, Block.torchWood.blockID);
			world.setBlock(i + this.houseLoc1 + 6, j + 2, k + this.houseLoc2 + 2, Block.torchWood.blockID);
			world.setBlock(i + this.houseLoc1 + 1, j + 2, k + this.houseLoc2 + 3, Block.torchWood.blockID);
			world.setBlockWithNotify(i + this.houseLoc1 + 5, j, k + this.houseLoc2 + 3, Block.MobGSpawner.blockID);
			if(this.rand.nextInt(2) == 0) {
				world.setBlockWithNotify(i + this.houseLoc1 + 1, j + 1, k + this.houseLoc2 + 3, Block.chest.blockID);
				tileEntityChest10 = (TileEntityChest)world.getBlockTileEntity(i + this.houseLoc1 + 1, j + 1, k + this.houseLoc2 + 3);

				for(goblinlord = 0; goblinlord <= 4; ++goblinlord) {
					itemStack11 = this.pickCheckLootItem(this.rand);
					if(itemStack11 != null) {
						tileEntityChest10.setInventorySlotContents(this.rand.nextInt(tileEntityChest10.getSizeInventory()), itemStack11);
					}
				}
			}
		}

		for(boss = 0; boss <= 4; ++boss) {
			world.setBlock(i + 11, j, k + 19 + boss, Block.cobblestone.blockID);
		}

		this.houseLoc1 = 8;
		this.houseLoc2 = 24;

		for(boss = 1; boss <= 2; ++boss) {
			world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 0, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 0, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 1, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 1, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 0, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 5, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 5, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 6, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 6, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 6, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 6, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 6, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 6, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
		}

		for(boss = 3; boss <= 3; ++boss) {
			world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 1, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 1, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 0, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 5, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 5, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 5, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
		}

		for(boss = 4; boss <= 4; ++boss) {
			world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 4, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 1, Block.cobblestone.blockID);
		}

		for(boss = 5; boss <= 5; ++boss) {
			world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 3, Block.cobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 2, Block.cobblestone.blockID);
		}

		for(boss = 0; boss <= 0; ++boss) {
			world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 0, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 5, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 4, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 1, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 3, Block.planks.blockID);
			world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 2, Block.planks.blockID);
		}

		for(boss = 1; boss <= 4; ++boss) {
			world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 3, Block.fence.blockID);
		}

		for(boss = 2; boss <= 2; ++boss) {
			world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 - 1, Block.torchWood.blockID);
			world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 - 1, Block.torchWood.blockID);
			world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 5, Block.torchWood.blockID);
		}

		for(boss = 1; boss <= 1; ++boss) {
			world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 2, Block.gobStone.blockID);
			world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 3, Block.stone.blockID);
			world.setBlock(i + this.houseLoc1 + 1, j + boss + 1, k + this.houseLoc2 + 3, Block.glass.blockID);
			world.setBlock(i + this.houseLoc1 + 1, j + boss, k + this.houseLoc2 + 4, Block.gobStone.blockID);
			world.setBlock(i + this.houseLoc1 + 2, j + boss, k + this.houseLoc2 + 5, Block.gobStone.blockID);
			world.setBlock(i + this.houseLoc1 + 3, j + boss, k + this.houseLoc2 + 5, Block.stairCompactCobblestone.blockID);
			world.setBlock(i + this.houseLoc1 + 4, j + boss, k + this.houseLoc2 + 5, Block.gobStone.blockID);
			world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 2, Block.gobStone.blockID);
			world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 3, Block.cauldron.blockID);
			world.setBlock(i + this.houseLoc1 + 5, j + boss, k + this.houseLoc2 + 4, Block.gobStone.blockID);
		}

		boss = this.rand.nextInt(2);
		if(boss == 0) {
			EntityGoblinMage entityGoblinMage13 = new EntityGoblinMage(world);
			entityGoblinMage13.setLocationAndAngles((double)(i + this.houseLoc1 + 3), (double)(j + 1), (double)(k + this.houseLoc2 - 1), world.rand.nextFloat() * 360.0F, 0.0F);
			entityGoblinMage13.setPosition((double)(i + this.houseLoc1 + 3), (double)(j + 1), (double)(k + this.houseLoc2 - 1));
			world.spawnEntityInWorld(entityGoblinMage13);
		} else {
			EntityGoblinLord entityGoblinLord14 = new EntityGoblinLord(world);
			entityGoblinLord14.setLocationAndAngles((double)(i + this.houseLoc1 + 3), (double)(j + 1), (double)(k + this.houseLoc2 - 1), world.rand.nextFloat() * 360.0F, 0.0F);
			entityGoblinLord14.setPosition((double)(i + this.houseLoc1 + 3), (double)(j + 1), (double)(k + this.houseLoc2 - 1));
			world.spawnEntityInWorld(entityGoblinLord14);
		}

	}

	private ItemStack pickCheckLootItem(Random random) {
		int i = random.nextInt(6);
		return i == 0 ? 
				new ItemStack(Item.appleRed) : (i == 1 ?
						new ItemStack(Item.goldNugget, random.nextInt(2) + 1) : (i == 2 ? 
								new ItemStack(Item.diamond) : (i == 3 ? 
										new ItemStack(Item.beefCooked, random.nextInt(2) + 1) : (i == 4 ? 
												new ItemStack(Item.ingotIron, 1) : (i == 5 && random.nextInt(20) == 1 ? 
														new ItemStack(mod_Goblins.powderR) : null)))));
	}

	private ItemStack pickCheckLootItemRider(Random random) {
		int i = random.nextInt(6);
		return i == 0 ? new ItemStack(Item.appleRed) : (i == 1 ?
				new ItemStack(Item.goldNugget, random.nextInt(2) + 1) : (i == 2 ? 
						new ItemStack(Item.saddle) : (i == 3 ? 
								new ItemStack(Item.beefCooked, random.nextInt(2) + 1) : (i == 4 ? 
										new ItemStack(Item.ingotIron, random.nextInt(2) + 1) : (i == 5 && 
										random.nextInt(20) == 1 ? new ItemStack(mod_Goblins.powderG) : null)))));
	}

	private ItemStack pickCheckLootItemMiner(Random random) {
		int i = random.nextInt(6);
		return i == 0 ? new ItemStack(Item.flint, random.nextInt(2) + 1) : (i == 1 ? 
				new ItemStack(Item.goldNugget, random.nextInt(3) + 1) : (i == 2 ? 
						new ItemStack(Item.coal, random.nextInt(4) + 1) : (i == 3 ? 
								new ItemStack(Item.beefCooked, random.nextInt(2) + 1) : (i == 4 ? 
										new ItemStack(Item.ingotIron, random.nextInt(3) + 1) : (i == 5 && random.nextInt(20) == 1 ? 
												new ItemStack(mod_Goblins.powderY) : null)))));
	}
}
