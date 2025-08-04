package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;

import net.minecraft.util.Translator;
import net.minecraft.world.Facing;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityList;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.entity.projectile.EntityEggInfo;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockMobSpawner;
import net.minecraft.world.level.tile.entity.TileEntityMobSpawner;
import net.minecraft.world.level.tile.entity.TileEntityMobSpawnerOneshot;

public class ItemMonsterPlacer extends Item {
	public ItemMonsterPlacer(int i1) {
		super(i1);
		this.setHasSubtypes(true);
		
		this.displayOnCreativeTab = CreativeTabs.tabMisc;
	}

	public String getItemDisplayName(ItemStack itemStack1) {
		String string2 = ("" + Translator.translateToLocal(this.getItemName() + ".name")).trim();
		String string3 = EntityList.getStringFromID(itemStack1.getItemDamage());
		if(string3 != null) {
			string2 = string2 + " " + Translator.translateToLocal("entity." + string3 + ".name");
		}

		return string2;
	}

	public int getColorFromDamage(int i1, int i2) {
		EntityEggInfo entityEggInfo3 = (EntityEggInfo)EntityList.entityEggs.get(i1);
		return entityEggInfo3 != null ? (i2 == 0 ? entityEggInfo3.primaryColor : entityEggInfo3.secondaryColor) : 0xFFFFFF;
	}

	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	public int getIconFromDamageAndRenderpass(int i1, int i2) {
		return i2 > 0 ? super.getIconFromDamageAndRenderpass(i1, i2) + 16 : super.getIconFromDamageAndRenderpass(i1, i2);
	}

	public boolean onItemUse(ItemStack theStack, EntityPlayer thePlayer, World world, int x, int y, int z, int side) {
		if(world.isRemote) {
			return true;
		} else {
			int blockID = world.getBlockId(x, y, z);

			// Hit a spawner?
			Block block = Block.blocksList[blockID];
			if(block instanceof BlockMobSpawner) {
				TileEntityMobSpawner spawner;
				if(((BlockMobSpawner)block).oneShot) {
					spawner = (TileEntityMobSpawnerOneshot)world.getBlockTileEntity(x, y, z);
				} else {
					spawner = (TileEntityMobSpawner)world.getBlockTileEntity(x, y, z);
				}
				
				if(spawner != null) {
					int damage = theStack.itemDamage;
					if(EntityList.entityEggs.containsKey(damage)) {
						spawner.setMobID(EntityList.entityEggs.get(damage).mobName);
						
						if(!thePlayer.capabilities.isCreativeMode) {
							--theStack.stackSize;
						}
			}

				}
			} else {

				x += Facing.offsetsXForSide[side];
				y += Facing.offsetsYForSide[side];
				z += Facing.offsetsZForSide[side];
				double yOffset = 0.0D;
				if(side == 1 && blockID == Block.fence.blockID /*|| blockID == Block.netherFence.blockID*/) {
					yOffset = 0.5D;
				}

				if(hatchEgg(world, theStack.getItemDamage(), (double)x + 0.5D, (double)y + yOffset, (double)z + 0.5D) && !thePlayer.capabilities.isCreativeMode) {
					--theStack.stackSize;
				}
			}

			return true;
		}
	}

	public static boolean hatchEgg(World world, int damage, double x, double y, double z) {
		if(!EntityList.entityEggs.containsKey(damage)) {
			return false;
		} else {
			Entity entity8 = EntityList.createEntityByID(damage, world);
			if(entity8 != null) {
				entity8.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0F, 0.0F);
				world.spawnEntityInWorld(entity8);
				((EntityLiving)entity8).playLivingSound();
			}

			return entity8 != null;
		}
	}
	
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
		Iterator<EntityEggInfo> iterator = EntityList.entityEggs.values().iterator();
		while(iterator.hasNext()) {
			par3List.add(new ItemStack(par1, 1, iterator.next().spawnedID));
		}
	}
}
