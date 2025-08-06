package net.minecraft.world.item;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.tile.Block;

public class ItemFlintAndSteel extends Item {
	public ItemFlintAndSteel(int i1) {
		super(i1);
		this.maxStackSize = 1;
		this.setMaxDamage(64);
		
		this.displayOnCreativeTab = CreativeTabs.tabTools;
	}

	public boolean onItemUse(ItemStack itemStack1, EntityPlayer entityPlayer2, World world3, int i4, int i5, int i6, int i7) {
		if(i7 == 0) {
			--i5;
		}

		if(i7 == 1) {
			++i5;
		}

		if(i7 == 2) {
			--i6;
		}

		if(i7 == 3) {
			++i6;
		}

		if(i7 == 4) {
			--i4;
		}

		if(i7 == 5) {
			++i4;
		}

		if(!entityPlayer2.canPlayerEdit(i4, i5, i6)) {
			return false;
		} else {
			int i8 = world3.getBlockId(i4, i5, i6);
			if(i8 == 0) {
				world3.playSoundEffect((double)i4 + 0.5D, (double)i5 + 0.5D, (double)i6 + 0.5D, "fire.ignite", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
				world3.setBlockWithNotify(i4, i5, i6, Block.fire.blockID);
			}

			itemStack1.damageItem(1, entityPlayer2);
			return true;
		}
	}
}
