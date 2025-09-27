package net.minecraft.world.level.tile;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.tile.entity.TileEntityGobDrum;

public class BlockGobDrum extends BlockContainer {
	public BlockGobDrum(int par1) {
		super(par1, 0, Material.wood);
	}

	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
		if(par5 > 0) {
			boolean flag = par1World.isBlockIndirectlyGettingPowered(par2, par3, par4);
			TileEntityGobDrum tileentitynote = (TileEntityGobDrum)par1World.getBlockTileEntity(par2, par3, par4);
			if(tileentitynote != null && tileentitynote.previousRedstoneState != flag) {
				if(flag) {
					tileentitynote.triggerNote(par1World, par2, par3, par4);
				}

				tileentitynote.previousRedstoneState = flag;
			}
		}

	}

	public boolean blockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
		if(par1World.isRemote) {
			return true;
		} else {
			TileEntityGobDrum tileentitynote = (TileEntityGobDrum)par1World.getBlockTileEntity(par2, par3, par4);
			if(tileentitynote != null) {
				tileentitynote.changePitch();
				tileentitynote.triggerNote(par1World, par2, par3, par4);
			}

			return true;
		}
	}

	public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
		if(!par1World.isRemote) {
			TileEntityGobDrum tileentitynote = (TileEntityGobDrum)par1World.getBlockTileEntity(par2, par3, par4);
			if(tileentitynote != null) {
				tileentitynote.triggerNote(par1World, par2, par3, par4);
			}

		}
	}

	public TileEntity getBlockEntity() {
		return new TileEntityGobDrum();
	}

	public void powerBlock(World par1World, int par2, int par3, int par4, int par5, int par6) {
		float f = (float)Math.pow(2.0D, (double)(par6 - 12) / 12.0D);
		String s = "harp";
		if(par5 == 1) {
			s = "bd";
		}

		if(par5 == 2) {
			s = "snare";
		}

		if(par5 == 3) {
			s = "hat";
		}

		if(par5 == 4) {
			s = "bassattack";
		}

		par1World.playSoundEffect((double)par2 + 0.5D, (double)par3 + 0.5D, (double)par4 + 0.5D, "note." + s, 3.0F, f);
		par1World.spawnParticle("note", (double)par2 + 0.5D, (double)par3 + 1.2D, (double)par4 + 0.5D, (double)par6 / 24.0D, 0.0D, 0.0D);
	}
}
