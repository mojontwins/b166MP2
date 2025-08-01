package net.minecraft.world.level.tile;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.tile.entity.TileEntityNote;

public class BlockNote extends BlockContainer {
	public BlockNote(int i1) {
		super(i1, 74, Material.wood);
		
		this.displayOnCreativeTab = CreativeTabs.tabRedstone;
	}

	public int getBlockTextureFromSide(int i1) {
		return this.blockIndexInTexture;
	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		if(i5 > 0) {
			boolean z6 = world1.isBlockIndirectlyGettingPowered(i2, i3, i4);
			TileEntityNote tileEntityNote7 = (TileEntityNote)world1.getBlockTileEntity(i2, i3, i4);
			if(tileEntityNote7 != null && tileEntityNote7.previousRedstoneState != z6) {
				if(z6) {
					tileEntityNote7.triggerNote(world1, i2, i3, i4);
				}

				tileEntityNote7.previousRedstoneState = z6;
			}
		}

	}

	public boolean blockActivated(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		if(world1.isRemote) {
			return true;
		} else {
			TileEntityNote tileEntityNote6 = (TileEntityNote)world1.getBlockTileEntity(i2, i3, i4);
			if(tileEntityNote6 != null) {
				tileEntityNote6.changePitch();
				tileEntityNote6.triggerNote(world1, i2, i3, i4);
			}

			return true;
		}
	}

	public void onBlockClicked(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		if(!world1.isRemote) {
			TileEntityNote tileEntityNote6 = (TileEntityNote)world1.getBlockTileEntity(i2, i3, i4);
			if(tileEntityNote6 != null) {
				tileEntityNote6.triggerNote(world1, i2, i3, i4);
			}

		}
	}

	public TileEntity getBlockEntity() {
		return new TileEntityNote();
	}

	public void powerBlock(World world1, int i2, int i3, int i4, int i5, int i6) {
		float f7 = (float)Math.pow(2.0D, (double)(i6 - 12) / 12.0D);
		String string8 = "harp";
		if(i5 == 1) {
			string8 = "bd";
		}

		if(i5 == 2) {
			string8 = "snare";
		}

		if(i5 == 3) {
			string8 = "hat";
		}

		if(i5 == 4) {
			string8 = "bassattack";
		}

		world1.playSoundEffect((double)i2 + 0.5D, (double)i3 + 0.5D, (double)i4 + 0.5D, "note." + string8, 3.0F, f7);
		world1.spawnParticle("note", (double)i2 + 0.5D, (double)i3 + 1.2D, (double)i4 + 0.5D, (double)i6 / 24.0D, 0.0D, 0.0D);
	}
}
