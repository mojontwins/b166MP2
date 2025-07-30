package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StatList {
	protected static Map field_25104_C = new HashMap();
	public static List field_25123_a = new ArrayList();
	public static List field_25122_b = new ArrayList();
	public static List field_25121_c = new ArrayList();
	public static List field_25120_d = new ArrayList();
	public static StatBase field_25119_e = (new StatBasic(1000, StatCollector.translateToLocal("stat.startGame"))).func_27052_e().func_27053_d();
	public static StatBase field_25118_f = (new StatBasic(1001, StatCollector.translateToLocal("stat.createWorld"))).func_27052_e().func_27053_d();
	public static StatBase field_25117_g = (new StatBasic(1002, StatCollector.translateToLocal("stat.loadWorld"))).func_27052_e().func_27053_d();
	public static StatBase field_25116_h = (new StatBasic(1003, StatCollector.translateToLocal("stat.joinMultiplayer"))).func_27052_e().func_27053_d();
	public static StatBase field_25115_i = (new StatBasic(1004, StatCollector.translateToLocal("stat.leaveGame"))).func_27052_e().func_27053_d();
	public static StatBase field_25114_j = (new StatBasic(1100, StatCollector.translateToLocal("stat.playOneMinute"), StatBase.field_27055_j)).func_27052_e().func_27053_d();
	public static StatBase field_25113_k = (new StatBasic(2000, StatCollector.translateToLocal("stat.walkOneCm"), StatBase.field_27054_k)).func_27052_e().func_27053_d();
	public static StatBase field_25112_l = (new StatBasic(2001, StatCollector.translateToLocal("stat.swimOneCm"), StatBase.field_27054_k)).func_27052_e().func_27053_d();
	public static StatBase field_25111_m = (new StatBasic(2002, StatCollector.translateToLocal("stat.fallOneCm"), StatBase.field_27054_k)).func_27052_e().func_27053_d();
	public static StatBase field_25110_n = (new StatBasic(2003, StatCollector.translateToLocal("stat.climbOneCm"), StatBase.field_27054_k)).func_27052_e().func_27053_d();
	public static StatBase field_25109_o = (new StatBasic(2004, StatCollector.translateToLocal("stat.flyOneCm"), StatBase.field_27054_k)).func_27052_e().func_27053_d();
	public static StatBase field_25108_p = (new StatBasic(2005, StatCollector.translateToLocal("stat.diveOneCm"), StatBase.field_27054_k)).func_27052_e().func_27053_d();
	public static StatBase field_27095_r = (new StatBasic(2006, StatCollector.translateToLocal("stat.minecartOneCm"), StatBase.field_27054_k)).func_27052_e().func_27053_d();
	public static StatBase field_27094_s = (new StatBasic(2007, StatCollector.translateToLocal("stat.boatOneCm"), StatBase.field_27054_k)).func_27052_e().func_27053_d();
	public static StatBase field_27093_t = (new StatBasic(2008, StatCollector.translateToLocal("stat.pigOneCm"), StatBase.field_27054_k)).func_27052_e().func_27053_d();
	public static StatBase field_25106_q = (new StatBasic(2010, StatCollector.translateToLocal("stat.jump"))).func_27052_e().func_27053_d();
	public static StatBase field_25103_r = (new StatBasic(2011, StatCollector.translateToLocal("stat.drop"))).func_27052_e().func_27053_d();
	public static StatBase field_25102_s = (new StatBasic(2020, StatCollector.translateToLocal("stat.damageDealt"))).func_27053_d();
	public static StatBase field_25100_t = (new StatBasic(2021, StatCollector.translateToLocal("stat.damageTaken"))).func_27053_d();
	public static StatBase field_25098_u = (new StatBasic(2022, StatCollector.translateToLocal("stat.deaths"))).func_27053_d();
	public static StatBase field_25097_v = (new StatBasic(2023, StatCollector.translateToLocal("stat.mobKills"))).func_27053_d();
	public static StatBase field_25096_w = (new StatBasic(2024, StatCollector.translateToLocal("stat.playerKills"))).func_27053_d();
	public static StatBase field_25095_x = (new StatBasic(2025, StatCollector.translateToLocal("stat.fishCaught"))).func_27053_d();
	public static StatBase[] mineBlockStatArray = func_25089_a("stat.mineBlock", 16777216);
	public static StatBase[] field_25093_z;
	public static StatBase[] field_25107_A;
	public static StatBase[] field_25105_B;
	private static boolean field_25101_D;
	private static boolean field_25099_E;

	public static void func_27092_a() {
	}

	public static void func_25088_a() {
		field_25107_A = func_25090_a(field_25107_A, "stat.useItem", 16908288, 0, Block.blocksList.length);
		field_25105_B = func_25087_b(field_25105_B, "stat.breakItem", 16973824, 0, Block.blocksList.length);
		field_25101_D = true;
		func_25091_c();
	}

	public static void func_25086_b() {
		field_25107_A = func_25090_a(field_25107_A, "stat.useItem", 16908288, Block.blocksList.length, 32000);
		field_25105_B = func_25087_b(field_25105_B, "stat.breakItem", 16973824, Block.blocksList.length, 32000);
		field_25099_E = true;
		func_25091_c();
	}

	public static void func_25091_c() {
		if(field_25101_D && field_25099_E) {
			HashSet hashSet0 = new HashSet();
			Iterator iterator1 = CraftingManager.getInstance().getRecipeList().iterator();

			while(iterator1.hasNext()) {
				IRecipe iRecipe2 = (IRecipe)iterator1.next();
				hashSet0.add(iRecipe2.func_25077_b().itemID);
			}

			iterator1 = FurnaceRecipes.smelting().getSmeltingList().values().iterator();

			while(iterator1.hasNext()) {
				ItemStack itemStack4 = (ItemStack)iterator1.next();
				hashSet0.add(itemStack4.itemID);
			}

			field_25093_z = new StatBase[32000];
			iterator1 = hashSet0.iterator();

			while(iterator1.hasNext()) {
				Integer integer5 = (Integer)iterator1.next();
				if(Item.itemsList[integer5.intValue()] != null) {
					String string3 = StatCollector.translateToLocalFormatted("stat.craftItem", new Object[]{Item.itemsList[integer5.intValue()].func_25006_i()});
					field_25093_z[integer5.intValue()] = (new StatCrafting(16842752 + integer5.intValue(), string3, integer5.intValue())).func_27053_d();
				}
			}

			replaceAllSimilarBlocks(field_25093_z);
		}
	}

	private static StatBase[] func_25089_a(String string0, int i1) {
		StatBase[] statBase2 = new StatBase[256];

		for(int i3 = 0; i3 < 256; ++i3) {
			if(Block.blocksList[i3] != null && Block.blocksList[i3].getEnableStats()) {
				String string4 = StatCollector.translateToLocalFormatted(string0, new Object[]{Block.blocksList[i3].func_25012_e()});
				statBase2[i3] = (new StatCrafting(i1 + i3, string4, i3)).func_27053_d();
				field_25120_d.add((StatCrafting)statBase2[i3]);
			}
		}

		replaceAllSimilarBlocks(statBase2);
		return statBase2;
	}

	private static StatBase[] func_25090_a(StatBase[] statBase0, String string1, int i2, int i3, int i4) {
		if(statBase0 == null) {
			statBase0 = new StatBase[32000];
		}

		for(int i5 = i3; i5 < i4; ++i5) {
			if(Item.itemsList[i5] != null) {
				String string6 = StatCollector.translateToLocalFormatted(string1, new Object[]{Item.itemsList[i5].func_25006_i()});
				statBase0[i5] = (new StatCrafting(i2 + i5, string6, i5)).func_27053_d();
				if(i5 >= Block.blocksList.length) {
					field_25121_c.add((StatCrafting)statBase0[i5]);
				}
			}
		}

		replaceAllSimilarBlocks(statBase0);
		return statBase0;
	}

	private static StatBase[] func_25087_b(StatBase[] statBase0, String string1, int i2, int i3, int i4) {
		if(statBase0 == null) {
			statBase0 = new StatBase[32000];
		}

		for(int i5 = i3; i5 < i4; ++i5) {
			if(Item.itemsList[i5] != null && Item.itemsList[i5].func_25005_e()) {
				String string6 = StatCollector.translateToLocalFormatted(string1, new Object[]{Item.itemsList[i5].func_25006_i()});
				statBase0[i5] = (new StatCrafting(i2 + i5, string6, i5)).func_27053_d();
			}
		}

		replaceAllSimilarBlocks(statBase0);
		return statBase0;
	}

	private static void replaceAllSimilarBlocks(StatBase[] statBase0) {
		replaceSimilarBlocks(statBase0, Block.waterStill.blockID, Block.waterMoving.blockID);
		replaceSimilarBlocks(statBase0, Block.lavaStill.blockID, Block.lavaStill.blockID);
		replaceSimilarBlocks(statBase0, Block.pumpkinLantern.blockID, Block.pumpkin.blockID);
		replaceSimilarBlocks(statBase0, Block.stoneOvenActive.blockID, Block.stoneOvenIdle.blockID);
		replaceSimilarBlocks(statBase0, Block.oreRedstoneGlowing.blockID, Block.oreRedstone.blockID);
		replaceSimilarBlocks(statBase0, Block.redstoneRepeaterActive.blockID, Block.redstoneRepeaterIdle.blockID);
		replaceSimilarBlocks(statBase0, Block.torchRedstoneActive.blockID, Block.torchRedstoneIdle.blockID);
		replaceSimilarBlocks(statBase0, Block.mushroomRed.blockID, Block.mushroomBrown.blockID);
		replaceSimilarBlocks(statBase0, Block.stairDouble.blockID, Block.stairSingle.blockID);
		replaceSimilarBlocks(statBase0, Block.grass.blockID, Block.dirt.blockID);
		replaceSimilarBlocks(statBase0, Block.tilledField.blockID, Block.dirt.blockID);
	}

	private static void replaceSimilarBlocks(StatBase[] statBase0, int i1, int i2) {
		if(statBase0[i1] != null && statBase0[i2] == null) {
			statBase0[i2] = statBase0[i1];
		} else {
			field_25123_a.remove(statBase0[i1]);
			field_25120_d.remove(statBase0[i1]);
			field_25122_b.remove(statBase0[i1]);
			statBase0[i1] = statBase0[i2];
		}
	}

	static {
		AchievementList.func_27097_a();
		field_25101_D = false;
		field_25099_E = false;
	}
}
