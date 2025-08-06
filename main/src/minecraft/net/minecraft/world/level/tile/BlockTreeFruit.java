package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;

public class BlockTreeFruit extends Block implements IGetNameBasedOnMeta {
	private static final int[] fruitTextures = new int[] {
		256	
	};
	
	private static final String[] fruitNames = new String[] {
		"cocoapod"	
	};
	
	private static final ItemStack[] dropStacks = new ItemStack[] {
		new ItemStack(Item.dyePowder, 1, 3), 		// Brown dye = cocoa beans
	};
	
	public BlockTreeFruit(int id) {
		super(id, Material.pumpkin);
		
		float size= .25F;
		this.setBlockBounds(0.5F - size, 1F - 3 * size, 0.5F - size, 0.5F + size, 1F - .01F, 0.5F + size);
	}
	
	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y + 1, z);
		return block != null && (block instanceof iLeaves);
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return super.canPlaceBlockAt(world, x, y, z) && this.canBlockStay(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
		if(!this.canBlockStay(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlockWithNotify(x, y, z, 0);
		}

	}

	@Override
	public int idDropped(int meta, Random rand, int fortune) {
		if(meta < BlockTreeFruit.dropStacks.length) {
			return BlockTreeFruit.dropStacks[meta].itemID; 
		}
		return -1;
	}
	
	@Override
	public int damageDropped(int meta) {
		if(meta < BlockTreeFruit.dropStacks.length) {
			return BlockTreeFruit.dropStacks[meta].itemDamage; 
		}
		return 0;
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
		return 1;
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		return BlockTreeFruit.fruitTextures[meta];
	}

	@Override
	public String getName(int meta) {
		return BlockTreeFruit.fruitNames[meta];
	}

}
