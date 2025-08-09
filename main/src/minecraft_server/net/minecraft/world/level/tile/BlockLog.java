package net.minecraft.world.level.tile;

import java.util.List;
import java.util.Random;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;

public class BlockLog extends Block3Axes implements IGetNameBasedOnMeta {
	
	public static final int OakMetadata = 0;
	public static final int SpruceMetadata = 1;
	public static final int BirchMetadata = 2;
	public static final int JungleMetadata = 3;
	
	// This strings will be used by ItemMetadata thru IGetNameBasedOnBeta
	// to produce tile.[name].name to give custom names to different metadata.
	public static final String[] logNames = new String[] {
			"oaklog", "sprucelog", "birchlog", "junglelog"
	};
	
	protected BlockLog(int i1) {
		super(i1, Material.wood);
		this.blockIndexInTexture = 20;
		
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}
	
	public int quantityDropped(Random random1) {
		return 1;
	}

	public int idDropped(int i1, Random random2, int i3) {
		return Block.wood.blockID;
	}

	public void harvestBlock(World world1, EntityPlayer entityPlayer2, int i3, int i4, int i5, int i6) {
		super.harvestBlock(world1, entityPlayer2, i3, i4, i5, i6);
	}

	public void onBlockRemoval(World world1, int i2, int i3, int i4) {
		byte b5 = 4;
		int i6 = b5 + 1;
		if(world1.checkChunksExist(i2 - i6, i3 - i6, i4 - i6, i2 + i6, i3 + i6, i4 + i6)) {
			for(int i7 = -b5; i7 <= b5; ++i7) {
				for(int i8 = -b5; i8 <= b5; ++i8) {
					for(int i9 = -b5; i9 <= b5; ++i9) {
						int i10 = world1.getBlockId(i2 + i7, i3 + i8, i4 + i9);
						if(i10 == Block.leaves.blockID) {
							int i11 = world1.getBlockMetadata(i2 + i7, i3 + i8, i4 + i9);
							if((i11 & 8) == 0) {
								world1.setBlockMetadata(i2 + i7, i3 + i8, i4 + i9, i11 | 8);
							}
						}
					}
				}
			}
		}

	}
	
	public int getTextureSides(int metadata) {
		switch(this.getWoodType(metadata)) {
			case 1: return 116;
			case 2: return 117;
			case 3: return 153;
			default: return 20;
		}
	}
	
	public int getTextureEnds(int metadata) {
		return 21;
	}
	
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
		par3List.add(new ItemStack(par1, 1, 0));
		par3List.add(new ItemStack(par1, 1, 1));
		par3List.add(new ItemStack(par1, 1, 2));
	}
	
	public int getWoodType(int meta) {
		return meta & 0xf3;
	}

	@Override
	public String getName(int meta) {
		return BlockLog.logNames[this.getWoodType(meta)];
	}
}
