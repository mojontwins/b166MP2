package net.minecraft.world.level.tile;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;

public class BlockSandStone extends Block implements IGround {
	public BlockSandStone(int i1) {
		super(i1, 192, Material.rock);
		
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}

	public int getBlockTextureFromSideAndMetadata(int i1, int i2) {
		return i1 != 1 && (i1 != 0 || i2 != 1 && i2 != 2) ? (i1 == 0 ? 208 : (i2 == 1 ? 229 : (i2 == 2 ? 230 : 192))) : 176;
	}

	public int getBlockTextureFromSide(int i1) {
		return i1 == 1 ? this.blockIndexInTexture - 16 : (i1 == 0 ? this.blockIndexInTexture + 16 : this.blockIndexInTexture);
	}

	public int damageDropped(int i1) {
		return i1;
	}
	
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
		par3List.add(new ItemStack(par1, 1, 0));
		//par3List.add(new ItemStack(par1, 1, 1));
		//par3List.add(new ItemStack(par1, 1, 2));
	}
}
