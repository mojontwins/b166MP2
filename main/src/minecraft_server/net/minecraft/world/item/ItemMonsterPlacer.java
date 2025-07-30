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

	public boolean onItemUse(ItemStack itemStack1, EntityPlayer entityPlayer2, World world3, int i4, int i5, int i6, int i7) {
		if(world3.isRemote) {
			return true;
		} else {
			int i8 = world3.getBlockId(i4, i5, i6);
			i4 += Facing.offsetsXForSide[i7];
			i5 += Facing.offsetsYForSide[i7];
			i6 += Facing.offsetsZForSide[i7];
			double d9 = 0.0D;
			if(i7 == 1 && i8 == Block.fence.blockID /*|| i8 == Block.netherFence.blockID*/) {
				d9 = 0.5D;
			}

			if(func_48440_a(world3, itemStack1.getItemDamage(), (double)i4 + 0.5D, (double)i5 + d9, (double)i6 + 0.5D) && !entityPlayer2.capabilities.isCreativeMode) {
				--itemStack1.stackSize;
			}

			return true;
		}
	}

	public static boolean func_48440_a(World world0, int i1, double d2, double d4, double d6) {
		if(!EntityList.entityEggs.containsKey(i1)) {
			return false;
		} else {
			Entity entity8 = EntityList.createEntityByID(i1, world0);
			if(entity8 != null) {
				entity8.setLocationAndAngles(d2, d4, d6, world0.rand.nextFloat() * 360.0F, 0.0F);
				world0.spawnEntityInWorld(entity8);
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
