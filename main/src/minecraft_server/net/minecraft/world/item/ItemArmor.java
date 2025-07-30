package net.minecraft.world.item;

import java.util.Arrays;

import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.tile.Block;

public class ItemArmor extends Item {
	private static final int[] maxDamageArray = new int[]{11, 16, 15, 13};
	public final int armorType;
	public final int damageReduceAmount;
	public final int renderIndex;
	private final EnumArmorMaterial material;
	
	public static final int CLOTH = 0;
	public static final int CHAIN = 1;
	public static final int IRON = 2;
	public static final int DIAMOND = 3;
	public static final int GOLD = 4;
	public static final int PIRATE = 5;
	public static final int RAGS = 6;
	
	public static final int HELMET = 0;
	public static final int PLATE = 1;
	public static final int LEGS = 2;
	public static final int BOOTS = 3;

	public static final String[] armorFilenamePrefix = new String[]{"cloth", "chain", "iron", "diamond", "gold", "pirate", "rags"};
	public static final String[] parts = new String[] {"helmet", "plate", "legs", "boots"};

	public ItemArmor(int i1, EnumArmorMaterial enumArmorMaterial2, int i3, int i4) {
		super(i1);
		this.material = enumArmorMaterial2;
		this.armorType = i4;
		this.renderIndex = i3;
		this.damageReduceAmount = enumArmorMaterial2.getDamageReductionAmount(i4);
		this.setMaxDamage(enumArmorMaterial2.getDurability(i4));
		this.maxStackSize = 1;
		
		this.displayOnCreativeTab = CreativeTabs.tabCombat;
	}

	public int getItemEnchantability() {
		return this.material.getEnchantability();
	}

	static int[] getMaxDamageArray() {
		return maxDamageArray;
	}

	/*
	 * tier: for armored mobs,  0: leather, 1: chain, 2: gold, 3: iron, 4: diamond
	 * type = 0: helmet, 1: plate, 2:legs, 3:boots
	 */
	public static ItemStack getArmorPieceForTier(int tier, int type) {
		System.out.println ("tier " + tier + " type " + type + " Item " + Item.armorPieceForTier[tier][type]);
		return new ItemStack (Item.armorPieceForTier[tier][type]);
	}
	
	public static String getArmorFilenamePrefix(int renderIndex) {
		return armorFilenamePrefix[renderIndex];
	}

	public static int toArmorType(String string) {
		// Understand number
		try {
			int res = Integer.parseInt(string);
			if(res >= 0 && res < parts.length) return res;
			return -1;
		} catch (Exception e) { }
		
		return Arrays.asList(parts).indexOf(string);
	}

	public static int toItemId(int type, String string) {
		if(type == -1) return -1;
		
		// Understand number, but must be either ItemArmor or a Block if type == 0
		
		try {
			int res = Integer.parseInt(string);
			if(res < 0) return -1;
			
			Item item = Item.itemsList[res];
			if(item == null) return -1;
			
			if(item instanceof ItemArmor) return res;
	
			if(type == HELMET && item instanceof ItemBlock) return res;
			
			return -1;
		} catch (Exception e) {
			if(type == HELMET) {
				for(Block block : Block.blocksList) {
					if(block != null) {
						String blockName = block.getBlockName();
						if(blockName != null && blockName.indexOf(string) >= 0)
							return block.blockID;
					}
				}
			}
			
		}
		
		int res = -1;
		
		int armorMatId = Arrays.asList(armorFilenamePrefix).indexOf(string);
		switch(armorMatId) {
		case CLOTH:
			switch(type) {
			case HELMET: res = Item.helmetLeather.shiftedIndex; break;
			case PLATE: res = Item.plateLeather.shiftedIndex; break;
			case LEGS: res = Item.legsLeather.shiftedIndex; break;
			case BOOTS: res = Item.bootsLeather.shiftedIndex; break;
			}
			break;
		case CHAIN:
			switch(type) {
			case HELMET: res = Item.helmetChain.shiftedIndex; break;
			case PLATE: res = Item.plateChain.shiftedIndex; break;
			case LEGS: res = Item.legsChain.shiftedIndex; break;
			case BOOTS: res = Item.bootsChain.shiftedIndex; break;
			}
			break;
		case IRON:
			switch(type) {
			case HELMET: res = Item.helmetSteel.shiftedIndex; break;
			case PLATE: res = Item.plateSteel.shiftedIndex; break;
			case LEGS: res = Item.legsSteel.shiftedIndex; break;
			case BOOTS: res = Item.bootsSteel.shiftedIndex; break;
			}
			break;
		case DIAMOND:
			switch(type) {
			case HELMET: res = Item.helmetDiamond.shiftedIndex; break;
			case PLATE: res = Item.plateDiamond.shiftedIndex; break;
			case LEGS: res = Item.legsDiamond.shiftedIndex; break;
			case BOOTS: res = Item.bootsDiamond.shiftedIndex; break;
			}
			break;
		case GOLD:
			switch(type) {
			case HELMET: res = Item.helmetGold.shiftedIndex; break;
			case PLATE: res = Item.plateGold.shiftedIndex; break;
			case LEGS: res = Item.legsGold.shiftedIndex; break;
			case BOOTS: res = Item.bootsGold.shiftedIndex; break;
			}
			break;
			/*
		case PIRATE:
			switch(type) {
			case HELMET: res = Item.helmetPirate.shiftedIndex; break;
			case PLATE: res = Item.platePirate.shiftedIndex; break;
			case LEGS: res = Item.legsPirate.shiftedIndex; break;
			case BOOTS: res = Item.bootsPirate.shiftedIndex; break;
			}
			break;
		case RAGS:
			switch(type) {
			case HELMET: res = Item.helmetRags.shiftedIndex; break;
			case PLATE: res = Item.plateRags.shiftedIndex; break;
			case LEGS: res = Item.legsRags.shiftedIndex; break;
			case BOOTS: res = Item.bootsLeather.shiftedIndex; break;
			}
			break;
			*/
		}
		
		return res;
	}
}
