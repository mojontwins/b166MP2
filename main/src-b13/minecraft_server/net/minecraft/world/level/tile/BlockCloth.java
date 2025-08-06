package net.minecraft.world.level.tile;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;

public class BlockCloth extends Block implements IDyeableBlock {
	
	// Colors are reversed when compared to metadata. This order fits ItemDye's:
	public static final int clothTints[] = {
		0x252121, 0xBC342F, 0x40591C, 0x633B20, 0x2D3BB2, 0x944DD2, 0x2D86AC, 0xB8BDBD,
		0x4C4C4C, 0xE5A8B9, 0x4ACF3E, 0xDECF2A, 0x8EA8DF, 0xCB6BD4, 0xED8F4E, 0xFFFFFF
	};

	public BlockCloth() {
		super(35, 64, Material.cloth);
		
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}

	public int damageDropped(int meta) {
		return meta;
	}

	public int getMetadataFromDye(int dye) {
		return ~dye & 15;
	}

	public int getDyeFromMetadata(int meta) {
		return ~meta & 15;
	}
	
	public static int getFleeceColorFromDamage(int damage) {
		return ~damage & 15;
	}
	
	// Use this and save 15 textures in the texture atlas. Note how metadata-color is reversed
	public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
		return this.getRenderColor(blockAccess.getBlockMetadata(x, y, z));
	}
	
	public int getRenderColor(int meta) {
		return this.getTints() [15 - meta];
	}
	
	public int[] getTints() {
		return BlockCloth.clothTints;
	}

	public void getSubBlocks(int blockID, CreativeTabs creativeTab, List<ItemStack> itemStacks) {
		for(int i = 0; i < BlockCloth.clothTints.length; i ++) {
			itemStacks.add(new ItemStack(blockID, 1, i));
		}
	}
}
