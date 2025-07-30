package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.tile.entity.TileEntityMobSpawner;
import net.minecraft.world.level.tile.entity.TileEntityMobSpawnerOneshot;

public class BlockMobSpawner extends BlockContainer {
	public boolean oneShot = false; 
	
	protected BlockMobSpawner(int i1, int i2, boolean oneShot) {
		super(i1, i2, Material.rock);
		this.oneShot = oneShot;
	}

	public TileEntity getBlockEntity() {
		if(this.oneShot) {
			return new TileEntityMobSpawnerOneshot();
		} else {
			return new TileEntityMobSpawner();
		}
	}

	public int idDropped(int i1, Random random2, int i3) {
		return 0;
	}

	public int quantityDropped(Random random1) {
		return 0;
	}
	
	@Override
	public void dropBlockAsItemWithChance(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
		super.dropBlockAsItemWithChance(var1, var2, var3, var4, var5, var6, var7);
		int var8 = 15 + var1.rand.nextInt(15) + var1.rand.nextInt(15);
		this.dropXpOnBlockBreak(var1, var2, var3, var4, var8);
	}

	public boolean isOpaqueCube() {
		return false;
	}
}
