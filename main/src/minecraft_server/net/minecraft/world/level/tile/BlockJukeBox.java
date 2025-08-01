package net.minecraft.world.level.tile;

import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.tile.entity.TileEntityRecordPlayer;

public class BlockJukeBox extends BlockContainer {
	protected BlockJukeBox(int i1, int i2) {
		super(i1, i2, Material.wood);
		this.displayOnCreativeTab = CreativeTabs.tabDeco;
	}

	public int getBlockTextureFromSide(int i1) {
		return this.blockIndexInTexture + (i1 == 1 ? 1 : 0);
	}

	public boolean blockActivated(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		if(world1.getBlockMetadata(i2, i3, i4) == 0) {
			return false;
		} else {
			this.ejectRecord(world1, i2, i3, i4);
			return true;
		}
	}

	public void insertRecord(World world1, int i2, int i3, int i4, int i5) {
		if(!world1.isRemote) {
			TileEntityRecordPlayer tileEntityRecordPlayer6 = (TileEntityRecordPlayer)world1.getBlockTileEntity(i2, i3, i4);
			if(tileEntityRecordPlayer6 != null) {
				tileEntityRecordPlayer6.record = i5;
				tileEntityRecordPlayer6.onInventoryChanged();
				world1.setBlockMetadataWithNotify(i2, i3, i4, 1);
			}
		}
	}

	public void ejectRecord(World world1, int i2, int i3, int i4) {
		if(!world1.isRemote) {
			TileEntityRecordPlayer tileEntityRecordPlayer5 = (TileEntityRecordPlayer)world1.getBlockTileEntity(i2, i3, i4);
			if(tileEntityRecordPlayer5 != null) {
				int i6 = tileEntityRecordPlayer5.record;
				if(i6 != 0) {
					world1.playAuxSFX(1005, i2, i3, i4, 0);
					world1.playRecord((String)null, i2, i3, i4);
					tileEntityRecordPlayer5.record = 0;
					tileEntityRecordPlayer5.onInventoryChanged();
					world1.setBlockMetadataWithNotify(i2, i3, i4, 0);
					float f8 = 0.7F;
					double d9 = (double)(world1.rand.nextFloat() * f8) + (double)(1.0F - f8) * 0.5D;
					double d11 = (double)(world1.rand.nextFloat() * f8) + (double)(1.0F - f8) * 0.2D + 0.6D;
					double d13 = (double)(world1.rand.nextFloat() * f8) + (double)(1.0F - f8) * 0.5D;
					EntityItem entityItem15 = new EntityItem(world1, (double)i2 + d9, (double)i3 + d11, (double)i4 + d13, new ItemStack(i6, 1, 0));
					entityItem15.delayBeforeCanPickup = 10;
					world1.spawnEntityInWorld(entityItem15);
				}
			}
		}
	}

	public void onBlockRemoval(World world1, int i2, int i3, int i4) {
		this.ejectRecord(world1, i2, i3, i4);
		super.onBlockRemoval(world1, i2, i3, i4);
	}

	public void dropBlockAsItemWithChance(World world1, int i2, int i3, int i4, int i5, float f6, int i7) {
		if(!world1.isRemote) {
			super.dropBlockAsItemWithChance(world1, i2, i3, i4, i5, f6, 0);
		}
	}

	public TileEntity getBlockEntity() {
		return new TileEntityRecordPlayer();
	}
}
