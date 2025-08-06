package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;

public class BlockCryingObsidian extends Block {

	// Adapted from NSSS
	
	public BlockCryingObsidian(int var1, int var2) {
		super(var1, var2, Material.rock);
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}

	public boolean blockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer) {
		if (entityPlayer.getCurrentEquippedItem() != null && entityPlayer.getCurrentEquippedItem().itemID == Item.compass.shiftedIndex) {
			if(world.isAirBlock(x, y + 1, z) && world.isAirBlock(x, y + 2, z)) {
				entityPlayer.setSpawnChunk(new ChunkCoordinates(x, y + 1, z));
				entityPlayer.setDontCheckSpawnCoordinates(true);
		 		
				if (!world.isRemote) {
					entityPlayer.addChatMessage("Spawn point set.");
				}
	
				for(int i = 0; i < 7; ++i) {
					world.spawnParticle("largesmoke", 
						(double)x + world.rand.nextDouble (), 
						(double)y + world.rand.nextDouble (),
						(double)z + world.rand.nextDouble (),
						0.0, 0.0, 0.0);
				}
	
				world.playSoundEffect((double)x, (double)y, (double)z, "random.drr", 1.0F, 0.25F);
				world.playSoundEffect((double)x, (double)y, (double)z, "random.breath", 0.5F, 0.15F);
			} else {
				if (!world.isRemote) {
					entityPlayer.addChatMessage("Can't set spawn! Top is blocked!");
				}
			}
		}
		return true;
	}
	
	public int idDropped(int metadata, Random rand) {
		return Block.cryingObsidian.blockID;
	}
}
