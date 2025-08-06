package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.src.MathHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;

public class BlockOre extends Block {
	public BlockOre(int i1, int i2) {
		super(i1, i2, Material.rock);
		
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}

	public int idDropped(int i1, Random random2, int i3) {
		return this.blockID == Block.oreCoal.blockID ? Item.coal.shiftedIndex :
			(this.blockID == Block.oreDiamond.blockID ? Item.diamond.shiftedIndex : 
				(this.blockID == Block.oreLapis.blockID ? Item.dyePowder.shiftedIndex : 
					this.blockID
				)
			);
	}

	public int quantityDropped(Random random1) {
		return this.blockID == Block.oreLapis.blockID ? 4 + random1.nextInt(5) : 1;
	}

	public int quantityDroppedWithBonus(int i1, Random random2) {
		if(i1 > 0 && this.blockID != this.idDropped(0, random2, i1)) {
			int i3 = random2.nextInt(i1 + 2) - 1;
			if(i3 < 0) {
				i3 = 0;
			}

			return this.quantityDropped(random2) * (i3 + 1);
		} else {
			return this.quantityDropped(random2);
		}
	}

	public int damageDropped(int i1) {
		return this.blockID == Block.oreLapis.blockID ? 4 : 0;
	}
	
	@Override
	public void dropBlockAsItemWithChance(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
		super.dropBlockAsItemWithChance(var1, var2, var3, var4, var5, var6, var7);
		if(this.idDropped(var5, var1.rand, var7) != this.blockID) {
			int var8 = 0;
			if(this.blockID == Block.oreCoal.blockID) {
				var8 = MathHelper.getRandomIntegerInRange(var1.rand, 0, 2);
			} else if(this.blockID == Block.oreDiamond.blockID ) {
				var8 = MathHelper.getRandomIntegerInRange(var1.rand, 3, 7);
			} else if(this.blockID == Block.oreLapis.blockID) {
				var8 = MathHelper.getRandomIntegerInRange(var1.rand, 2, 5);
			} 

			this.dropXpOnBlockBreak(var1, var2, var3, var4, var8);
		}

	}
}
