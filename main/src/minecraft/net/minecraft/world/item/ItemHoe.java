package net.minecraft.world.item;

import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.item.EnumToolMaterial;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.tile.Block;

public class ItemHoe extends Item {
	public ItemHoe(int id, EnumToolMaterial toolMaterial) {
		super(id);
		this.maxStackSize = 1;
		this.setMaxDamage(toolMaterial.getMaxUses());
		
		this.displayOnCreativeTab = CreativeTabs.tabTools;
	}

	public boolean onItemUse(ItemStack theStack, EntityPlayer thePlayer, World world, int x, int y, int z, int side) {
		if(!thePlayer.canPlayerEdit(x, y, z)) {
			return false;
		} else {
			int i8 = world.getBlockId(x, y, z);
			int i9 = world.getBlockId(x, y + 1, z);
			if((side == 0 || i9 != 0 || i8 != Block.grass.blockID) && i8 != Block.dirt.blockID) {
				return false;
			} else {
				Block block10 = Block.tilledField;
				world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), block10.stepSound.getStepSound(), (block10.stepSound.getVolume() + 1.0F) / 2.0F, block10.stepSound.getPitch() * 0.8F);
				if(world.isRemote) {
					return true;
				} else {
					world.setBlockWithNotify(x, y, z, block10.blockID);
					theStack.damageItem(1, thePlayer);
					
					if(world.rand.nextInt(8) == 0 && i8 == Block.grass.blockID) {
						byte b11 = 1;

						for(int i12 = 0; i12 < b11; ++i12) {
							float f13 = 0.7F;
							float f14 = world.rand.nextFloat() * f13 + (1.0F - f13) * 0.5F;
							float f15 = 1.2F;
							float f16 = world.rand.nextFloat() * f13 + (1.0F - f13) * 0.5F;
							EntityItem entityItem17 = new EntityItem(world, (double)((float)x + f14), (double)((float)y + f15), (double)((float)z + f16), new ItemStack(Item.seeds));
							entityItem17.delayBeforeCanPickup = 10;
							world.spawnEntityInWorld(entityItem17);
						}
					}

					return true;
				}
			}
		}
	}

	public boolean isFull3D() {
		return true;
	}
	
	@Override
	public boolean canHarvestBlock(Block block) {
		return block == Block.leaves;
	}
}
