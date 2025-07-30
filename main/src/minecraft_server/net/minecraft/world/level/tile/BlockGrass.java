package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.ISurface;
import net.minecraft.world.level.World;
import net.minecraft.world.level.colorizer.ColorizerGrass;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;

public class BlockGrass extends Block implements ISurface, IGround {
	private int texSide = 3;
	private int texTop = 0;
	private boolean needsColorizer = true;
	
	protected BlockGrass(int i1) {
		super(i1, Material.grass);
		
		this.blockIndexInTexture = 3;
		
		this.texSide = 14*16+7;
		this.texTop = 14*16+12;
		this.needsColorizer = false;
		this.setTickRandomly(true);	
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}

	public int getBlockTextureFromSideAndMetadata(int i1, int i2) {
		return i1 == 1 ? this.texTop : (i1 == 0 ? 2 : 3);
	}

	public int getBlockTexture(IBlockAccess iBlockAccess1, int i2, int i3, int i4, int i5) {
		if(i5 == 1) {
			return this.texTop;
		} else if(i5 == 0) {
			return 2;
		} else {
			Material material6 = iBlockAccess1.getBlockMaterial(i2, i3 + 1, i4);
			return material6 != Material.snow && material6 != Material.craftedSnow ? this.texSide : 68;
		}
	}

	public int getBlockColor() {
		if(!this.needsColorizer) return 0xFFFFFF;
		
		double d1 = 0.5D;
		double d3 = 1.0D;
		return ColorizerGrass.getGrassColor(d1, d3);
	}

	public int getRenderColor(int i1) {
		return this.getBlockColor();
	}

	public int colorMultiplier(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		if(!this.needsColorizer) return 0xFFFFFF;
		
		int i5 = 0;
		int i6 = 0;
		int i7 = 0;

		for(int i8 = -1; i8 <= 1; ++i8) {
			for(int i9 = -1; i9 <= 1; ++i9) {
				int i10 = iBlockAccess1.getBiomeGenForCoords(i2 + i9, i4 + i8).getBiomeGrassColor();
				i5 += (i10 & 16711680) >> 16;
				i6 += (i10 & 65280) >> 8;
				i7 += i10 & 255;
			}
		}

		return (i5 / 9 & 255) << 16 | (i6 / 9 & 255) << 8 | i7 / 9 & 255;
	}

	public void updateTick(World world1, int i2, int i3, int i4, Random random5) {
		if(!world1.isRemote) {
			if(world1.getBlockLightValue(i2, i3 + 1, i4) < 4 && Block.lightOpacity[world1.getBlockId(i2, i3 + 1, i4)] > 2) {
				world1.setBlockWithNotify(i2, i3, i4, Block.dirt.blockID);
			} else if(world1.getBlockLightValue(i2, i3 + 1, i4) >= 9) {
				for(int i6 = 0; i6 < 4; ++i6) {
					int i7 = i2 + random5.nextInt(3) - 1;
					int i8 = i3 + random5.nextInt(5) - 3;
					int i9 = i4 + random5.nextInt(3) - 1;
					int i10 = world1.getBlockId(i7, i8 + 1, i9);
					if(world1.getBlockId(i7, i8, i9) == Block.dirt.blockID && world1.getBlockLightValue(i7, i8 + 1, i9) >= 4 && Block.lightOpacity[i10] <= 2) {
						world1.setBlockWithNotify(i7, i8, i9, Block.grass.blockID);
					}
				}
			}

		}
	}

	public int idDropped(int i1, Random random2, int i3) {
		return Block.dirt.idDropped(0, random2, i3);
	}
	
	public boolean canGrowPlants() {
		return true;
	}
}
