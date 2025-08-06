package net.minecraft.world.item;

import java.util.List;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockJukeBox;

public class ItemRecord extends Item {
	public String recordName;

	protected ItemRecord(int i1, String string2) {
		super(i1);
		this.recordName = "C418 - " + string2;
		this.maxStackSize = 1;
		
		this.displayOnCreativeTab = CreativeTabs.tabMisc;
	}

	public boolean onItemUse(ItemStack itemStack1, EntityPlayer entityPlayer2, World world3, int i4, int i5, int i6, int i7) {
		if(world3.getBlockId(i4, i5, i6) == Block.jukebox.blockID && world3.getBlockMetadata(i4, i5, i6) == 0) {
			if(world3.isRemote) {
				return true;
			} else {
				((BlockJukeBox)Block.jukebox).insertRecord(world3, i4, i5, i6, this.shiftedIndex);
				world3.playAuxSFXAtEntity((EntityPlayer)null, 1005, i4, i5, i6, this.shiftedIndex);
				--itemStack1.stackSize;
				return true;
			}
		} else {
			return false;
		}
	}

	public void addInformation(ItemStack itemStack1, List<String> list2) {
		list2.add("C418 - " + this.recordName);
	}

	public EnumRarity getRarity(ItemStack itemStack1) {
		return EnumRarity.rare;
	}
}
