package net.minecraft.world.item;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;

public class ItemSeeds extends Item {
	private int blockType;
	private int soilBlockID;

	public ItemSeeds(int i1, int i2, int i3) {
		super(i1);
		this.blockType = i2;
		this.soilBlockID = i3;

		this.displayOnCreativeTab = CreativeTabs.tabMaterials;		
	}

	public boolean onItemUse(ItemStack itemStack1, EntityPlayer entityPlayer2, World world3, int i4, int i5, int i6, int i7) {
		if(i7 != 1) {
			return false;
		} else if(entityPlayer2.canPlayerEdit(i4, i5, i6) && entityPlayer2.canPlayerEdit(i4, i5 + 1, i6)) {
			int i8 = world3.getBlockId(i4, i5, i6);
			if(i8 == this.soilBlockID && world3.isAirBlock(i4, i5 + 1, i6)) {
				world3.setBlockWithNotify(i4, i5 + 1, i6, this.blockType);
				--itemStack1.stackSize;
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
