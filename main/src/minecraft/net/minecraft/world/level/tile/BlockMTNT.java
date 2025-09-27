package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.item.EntityMTNTPrimed;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;

public class BlockMTNT extends Block {
	public BlockMTNT(int i) {
		super(i, Material.ground);
	}

	public int getBlockTextureFromSideAndMetadata(int i, int j) {
		return i == 1 ? 359 : (i == 0 ? 358 : 357);
	}

	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		if(world.isBlockIndirectlyGettingPowered(i, j, k)) {
			this.onBlockDestroyedByPlayer(world, i, j, k, 1);
			world.setBlockWithNotify(i, j, k, 0);
		}

	}

	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
		if(l > 0 && Block.blocksList[l].canProvidePower() && world.isBlockIndirectlyGettingPowered(i, j, k)) {
			this.onBlockDestroyedByPlayer(world, i, j, k, 1);
			world.setBlockWithNotify(i, j, k, 0);
		}

	}

	public int quantityDropped(Random random) {
		return 0;
	}

	public void onBlockDestroyedByExplosion(World world, int i, int j, int k) {
		EntityMTNTPrimed entityetntprimed = new EntityMTNTPrimed(world, (double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F));
		entityetntprimed.fuse = world.rand.nextInt(entityetntprimed.fuse / 4) + entityetntprimed.fuse / 8;
		world.spawnEntityInWorld(entityetntprimed);
	}

	public void onBlockDestroyedByPlayer(World world, int i, int j, int k, int l) {
		if(!world.isRemote) {
			EntityMTNTPrimed entitytntprimed = new EntityMTNTPrimed(world, (double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F));
			world.spawnEntityInWorld(entitytntprimed);
			world.playSoundAtEntity(entitytntprimed, "random.fuse", 1.0F, 1.0F);
		}
	}

	public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer) {
		if(entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().itemID == Item.flintAndSteel.shiftedIndex) {
			world.setBlockMetadata(i, j, k, 1);
		}

		super.onBlockClicked(world, i, j, k, entityplayer);
	}

	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer) {
		return super.blockActivated(world, i, j, k, entityplayer);
	}

	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving) {
		int l = MathHelper.floor_double((double)(entityliving.rotationYaw * 4.0F / 360.0F) + 2.5D) & 3;
		world.setBlockMetadataWithNotify(i, j, k, l);
	}
}
