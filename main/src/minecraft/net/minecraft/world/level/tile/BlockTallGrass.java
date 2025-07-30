package net.minecraft.world.level.tile;

import java.util.List;
import java.util.Random;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.colorizer.ColorizerFoliage;
import net.minecraft.world.level.colorizer.ColorizerGrass;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;

public class BlockTallGrass extends BlockFlower {
	protected BlockTallGrass(int i1, int i2) {
		super(i1, i2, Material.vine);
		float f3 = 0.4F;
		this.setBlockBounds(0.5F - f3, 0.0F, 0.5F - f3, 0.5F + f3, 0.8F, 0.5F + f3);
		
		this.displayOnCreativeTab = CreativeTabs.tabDeco;
	}

	public int getBlockTextureFromSideAndMetadata(int i1, int i2) {
		return i2 == 1 ? this.blockIndexInTexture : (i2 == 2 ? this.blockIndexInTexture + 16 + 1 : (i2 == 0 ? this.blockIndexInTexture + 16 : this.blockIndexInTexture));
	}

	public int getBlockColor() {
		double d1 = 0.5D;
		double d3 = 1.0D;
		return ColorizerGrass.getGrassColor(d1, d3);
	}

	public int getRenderColor(int i1) {
		return i1 == 0 ? 0xFFFFFF : ColorizerFoliage.getFoliageColorBasic();
	}

	public int colorMultiplier(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		int i5 = iBlockAccess1.getBlockMetadata(i2, i3, i4);
		return i5 == 0 ? 0xFFFFFF : iBlockAccess1.getBiomeGenForCoords(i2, i4).getBiomeGrassColor();
	}

	public int idDropped(int i1, Random random2, int i3) {
		return random2.nextInt(8) == 0 ? Item.seeds.shiftedIndex : -1;
	}

	public int quantityDroppedWithBonus(int i1, Random random2) {
		return 1 + random2.nextInt(i1 * 2 + 1);
	}

	public void harvestBlock(World world1, EntityPlayer entityPlayer2, int i3, int i4, int i5, int i6) {
		if(!world1.isRemote && entityPlayer2.getCurrentEquippedItem() != null && entityPlayer2.getCurrentEquippedItem().itemID == Item.shears.shiftedIndex) {
			this.dropBlockAsItem_do(world1, i3, i4, i5, new ItemStack(Block.tallGrass, 1, i6));
		} else {
			super.harvestBlock(world1, entityPlayer2, i3, i4, i5, i6);
		}

	}
	
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
		par3List.add(new ItemStack(par1, 1, 1));
		par3List.add(new ItemStack(par1, 1, 2));
		par3List.add(new ItemStack(par1, 1, 3));
	}
}
