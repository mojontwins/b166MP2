package net.minecraft.world.item;

import java.util.List;
import java.util.Random;

import com.mojontwins.minecraft.worldedit.ItemMagicWand;
import com.risugami.recipebook.ItemRecipeBook;

import net.minecraft.src.MathHelper;
import net.minecraft.util.StringTranslate;
import net.minecraft.util.Translator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumAction;
import net.minecraft.world.entity.item.EnumToolMaterial;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

public class Item implements ITextureProvider {
	protected static Random itemRand = new Random();
	public static Item[] itemsList = new Item[32000];
	public static Item shovelSteel = (new ItemSpade(0, EnumToolMaterial.IRON)).setIconCoord(2, 5).setItemName("shovelIron");
	public static Item pickaxeSteel = (new ItemPickaxe(1, EnumToolMaterial.IRON)).setIconCoord(2, 6).setItemName("pickaxeIron");
	public static Item axeSteel = (new ItemAxe(2, EnumToolMaterial.IRON)).setIconCoord(2, 7).setItemName("hatchetIron");
	public static Item flintAndSteel = (new ItemFlintAndSteel(3)).setIconCoord(5, 0).setItemName("flintAndSteel");
	public static Item appleRed = (new ItemFood(4, 4, 0.3F, false)).setIconCoord(10, 0).setItemName("apple");
	public static Item bow = (new ItemBow(5)).setIconCoord(5, 1).setItemName("bow");
	public static Item arrow = (new Item(6)).setIconCoord(5, 2).setItemName("arrow").setCreativeTab(CreativeTabs.tabCombat);
	public static Item coal = (new ItemCoal(7)).setIconCoord(7, 0).setItemName("coal");
	public static Item diamond = (new Item(8)).setIconCoord(7, 3).setItemName("emerald").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item ingotIron = (new Item(9)).setIconCoord(7, 1).setItemName("ingotIron").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item ingotGold = (new ItemGolden(10)).setIconCoord(7, 2).setItemName("ingotGold").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item swordSteel = (new ItemSword(11, EnumToolMaterial.IRON)).setIconCoord(2, 4).setItemName("swordIron");
	public static Item swordWood = (new ItemSword(12, EnumToolMaterial.WOOD)).setIconCoord(0, 4).setItemName("swordWood");
	public static Item shovelWood = (new ItemSpade(13, EnumToolMaterial.WOOD)).setIconCoord(0, 5).setItemName("shovelWood");
	public static Item pickaxeWood = (new ItemPickaxe(14, EnumToolMaterial.WOOD)).setIconCoord(0, 6).setItemName("pickaxeWood");
	public static Item axeWood = (new ItemAxe(15, EnumToolMaterial.WOOD)).setIconCoord(0, 7).setItemName("hatchetWood");
	public static Item swordStone = (new ItemSword(16, EnumToolMaterial.STONE)).setIconCoord(1, 4).setItemName("swordStone");
	public static Item shovelStone = (new ItemSpade(17, EnumToolMaterial.STONE)).setIconCoord(1, 5).setItemName("shovelStone");
	public static Item pickaxeStone = (new ItemPickaxe(18, EnumToolMaterial.STONE)).setIconCoord(1, 6).setItemName("pickaxeStone");
	public static Item axeStone = (new ItemAxe(19, EnumToolMaterial.STONE)).setIconCoord(1, 7).setItemName("hatchetStone");
	public static Item swordDiamond = (new ItemSword(20, EnumToolMaterial.EMERALD)).setIconCoord(3, 4).setItemName("swordDiamond");
	public static Item shovelDiamond = (new ItemSpade(21, EnumToolMaterial.EMERALD)).setIconCoord(3, 5).setItemName("shovelDiamond");
	public static Item pickaxeDiamond = (new ItemPickaxe(22, EnumToolMaterial.EMERALD)).setIconCoord(3, 6).setItemName("pickaxeDiamond");
	public static Item axeDiamond = (new ItemAxe(23, EnumToolMaterial.EMERALD)).setIconCoord(3, 7).setItemName("hatchetDiamond");
	public static Item stick = (new Item(24)).setIconCoord(5, 3).setFull3D().setItemName("stick").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item bowlEmpty = (new Item(25)).setIconCoord(7, 4).setItemName("bowl").setCreativeTab(CreativeTabs.tabFood);
	public static Item bowlSoup = (new ItemSoup(26, 8)).setIconCoord(8, 4).setItemName("mushroomStew");
	public static Item swordGold = (new ItemSword(27, EnumToolMaterial.GOLD)).setIconCoord(4, 4).setItemName("swordGold");
	public static Item shovelGold = (new ItemSpade(28, EnumToolMaterial.GOLD)).setIconCoord(4, 5).setItemName("shovelGold");
	public static Item pickaxeGold = (new ItemPickaxe(29, EnumToolMaterial.GOLD)).setIconCoord(4, 6).setItemName("pickaxeGold");
	public static Item axeGold = (new ItemAxe(30, EnumToolMaterial.GOLD)).setIconCoord(4, 7).setItemName("hatchetGold");
	public static Item silk = (new Item(31)).setIconCoord(8, 0).setItemName("string").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item feather = (new Item(32)).setIconCoord(8, 1).setItemName("feather").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item gunpowder = (new Item(33)).setIconCoord(8, 2).setItemName("sulphur").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item hoeWood = (new ItemHoe(34, EnumToolMaterial.WOOD)).setIconCoord(0, 8).setItemName("hoeWood");
	public static Item hoeStone = (new ItemHoe(35, EnumToolMaterial.STONE)).setIconCoord(1, 8).setItemName("hoeStone");
	public static Item hoeSteel = (new ItemHoe(36, EnumToolMaterial.IRON)).setIconCoord(2, 8).setItemName("hoeIron");
	public static Item hoeDiamond = (new ItemHoe(37, EnumToolMaterial.EMERALD)).setIconCoord(3, 8).setItemName("hoeDiamond");
	public static Item hoeGold = (new ItemHoe(38, EnumToolMaterial.GOLD)).setIconCoord(4, 8).setItemName("hoeGold");
	public static Item seeds = (new ItemSeeds(39, Block.crops.blockID, Block.tilledField.blockID)).setIconCoord(9, 0).setItemName("seeds");
	public static Item wheat = (new Item(40)).setIconCoord(9, 1).setItemName("wheat").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item bread = (new ItemFood(41, 5, 0.6F, false)).setIconCoord(9, 2).setItemName("bread");
	public static Item helmetLeather = (new ItemArmor(42, EnumArmorMaterial.CLOTH, 0, 0)).setIconCoord(0, 0).setItemName("helmetCloth");
	public static Item plateLeather = (new ItemArmor(43, EnumArmorMaterial.CLOTH, 0, 1)).setIconCoord(0, 1).setItemName("chestplateCloth");
	public static Item legsLeather = (new ItemArmor(44, EnumArmorMaterial.CLOTH, 0, 2)).setIconCoord(0, 2).setItemName("leggingsCloth");
	public static Item bootsLeather = (new ItemArmor(45, EnumArmorMaterial.CLOTH, 0, 3)).setIconCoord(0, 3).setItemName("bootsCloth");
	public static Item helmetChain = (new ItemArmor(46, EnumArmorMaterial.CHAIN, 1, 0)).setIconCoord(1, 0).setItemName("helmetChain");
	public static Item plateChain = (new ItemArmor(47, EnumArmorMaterial.CHAIN, 1, 1)).setIconCoord(1, 1).setItemName("chestplateChain");
	public static Item legsChain = (new ItemArmor(48, EnumArmorMaterial.CHAIN, 1, 2)).setIconCoord(1, 2).setItemName("leggingsChain");
	public static Item bootsChain = (new ItemArmor(49, EnumArmorMaterial.CHAIN, 1, 3)).setIconCoord(1, 3).setItemName("bootsChain");
	public static Item helmetSteel = (new ItemArmor(50, EnumArmorMaterial.IRON, 2, 0)).setIconCoord(2, 0).setItemName("helmetIron");
	public static Item plateSteel = (new ItemArmor(51, EnumArmorMaterial.IRON, 2, 1)).setIconCoord(2, 1).setItemName("chestplateIron");
	public static Item legsSteel = (new ItemArmor(52, EnumArmorMaterial.IRON, 2, 2)).setIconCoord(2, 2).setItemName("leggingsIron");
	public static Item bootsSteel = (new ItemArmor(53, EnumArmorMaterial.IRON, 2, 3)).setIconCoord(2, 3).setItemName("bootsIron");
	public static Item helmetDiamond = (new ItemArmor(54, EnumArmorMaterial.DIAMOND, 3, 0)).setIconCoord(3, 0).setItemName("helmetDiamond");
	public static Item plateDiamond = (new ItemArmor(55, EnumArmorMaterial.DIAMOND, 3, 1)).setIconCoord(3, 1).setItemName("chestplateDiamond");
	public static Item legsDiamond = (new ItemArmor(56, EnumArmorMaterial.DIAMOND, 3, 2)).setIconCoord(3, 2).setItemName("leggingsDiamond");
	public static Item bootsDiamond = (new ItemArmor(57, EnumArmorMaterial.DIAMOND, 3, 3)).setIconCoord(3, 3).setItemName("bootsDiamond");
	public static Item helmetGold = (new ItemArmor(58, EnumArmorMaterial.GOLD, 4, 0)).setIconCoord(4, 0).setItemName("helmetGold");
	public static Item plateGold = (new ItemArmor(59, EnumArmorMaterial.GOLD, 4, 1)).setIconCoord(4, 1).setItemName("chestplateGold");
	public static Item legsGold = (new ItemArmor(60, EnumArmorMaterial.GOLD, 4, 2)).setIconCoord(4, 2).setItemName("leggingsGold");
	public static Item bootsGold = (new ItemArmor(61, EnumArmorMaterial.GOLD, 4, 3)).setIconCoord(4, 3).setItemName("bootsGold");
	public static Item flint = (new Item(62)).setIconCoord(6, 0).setItemName("flint").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item porkRaw = (new ItemFood(63, 3, 0.3F, true)).setIconCoord(7, 5).setItemName("porkchopRaw");
	public static Item porkCooked = (new ItemPorkCooked(64, 8, 0.8F, true)).setIconCoord(8, 5).fixForAlphaBeta().setItemName("porkchopCooked");
	public static Item painting = (new ItemPainting(65)).setIconCoord(10, 1).setItemName("painting");
	public static Item appleGold = (new ItemAppleGold(66, 4, 1.2F, false)).setAlwaysEdible().setIconCoord(11, 0).setItemName("appleGold");
	public static Item sign = (new ItemSign(67)).setIconCoord(10, 2).setItemName("sign");
	public static Item doorWood = (new ItemDoor(68, Material.wood)).setIconCoord(11, 2).setItemName("doorWood");
	public static Item bucketEmpty = (new ItemBucket(69, 0)).setIconCoord(10, 4).setItemName("bucket");
	public static Item bucketWater = (new ItemBucket(70, Block.waterMoving.blockID)).setIconCoord(11, 4).setItemName("bucketWater").setContainerItem(bucketEmpty);
	public static Item bucketLava = (new ItemBucket(71, Block.lavaMoving.blockID)).setIconCoord(12, 4).setItemName("bucketLava").setContainerItem(bucketEmpty);
	public static Item minecartEmpty = (new ItemMinecart(72, 0)).setIconCoord(7, 8).setItemName("minecart");
	public static Item saddle = (new ItemSaddle(73)).setIconCoord(8, 6).setItemName("saddle");
	public static Item doorSteel = (new ItemDoor(74, Material.iron)).setIconCoord(12, 2).setItemName("doorIron");
	public static Item redstone = (new ItemRedstone(75)).setIconCoord(8, 3).setItemName("redstone");
	public static Item snowball = (new ItemSnowball(76)).setIconCoord(14, 0).setItemName("snowball");
	public static Item boat = (new ItemBoat(77)).setIconCoord(8, 8).setItemName("boat");
	public static Item leather = (new Item(78)).setIconCoord(7, 6).setItemName("leather").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item bucketMilk = (new ItemBucketMilk(79)).setIconCoord(13, 4).setItemName("milk").setContainerItem(bucketEmpty);
	public static Item brick = (new Item(80)).setIconCoord(6, 1).setItemName("brick").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item clay = (new Item(81)).setIconCoord(9, 3).setItemName("clay").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item reed = (new ItemPlacesBlock(82, Block.reed)).setIconCoord(11, 1).setItemName("reeds").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item paper = (new Item(83)).setIconCoord(10, 3).setItemName("paper").setCreativeTab(CreativeTabs.tabMisc);
	public static Item book = (new Item(84)).setIconCoord(11, 3).setItemName("book").setCreativeTab(CreativeTabs.tabMisc);
	public static Item slimeBall = (new Item(85)).setIconCoord(14, 1).setItemName("slimeball").setCreativeTab(CreativeTabs.tabMisc);
	public static Item minecartCrate = (new ItemMinecart(86, 1)).setIconCoord(7, 9).setItemName("minecartChest");
	public static Item minecartPowered = (new ItemMinecart(87, 2)).setIconCoord(7, 10).setItemName("minecartFurnace");
	public static Item egg = (new ItemEgg(88)).setIconCoord(12, 0).setItemName("egg");
	public static Item compass = (new Item(89)).setIconCoord(6, 3).setItemName("compass").setCreativeTab(CreativeTabs.tabTools);
	public static Item fishingRod = (new ItemFishingRod(90)).setIconCoord(5, 4).setItemName("fishingRod");
	
	public static Item pocketSundial = (new Item(91)).setIconCoord(6, 4).setItemName("clock").setCreativeTab(CreativeTabs.tabTools);
	public static Item lightStoneDust = (new Item(92)).setIconCoord(9, 4).setItemName("yellowDust").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item fishRaw = (new ItemFood(93, 2, 0.3F, false)).setIconCoord(9, 5).setItemName("fishRaw");
	public static Item fishCooked = (new ItemFood(94, 5, 0.6F, false)).setIconCoord(10, 5).setItemName("fishCooked");
	
	public static Item dyePowder = (new ItemDye(95)).setIconCoord(14, 4).setItemName("dyePowder");
	public static Item bone = (new Item(96)).setIconCoord(12, 1).setItemName("bone").setFull3D().setCreativeTab(CreativeTabs.tabMisc);
	public static Item sugar = (new Item(97)).setIconCoord(13, 0).setItemName("sugar").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item cake = (new ItemPlacesBlock(98, Block.cake)).setMaxStackSize(1).setIconCoord(13, 1).setItemName("cake").setCreativeTab(CreativeTabs.tabFood);
	
	public static Item redstoneRepeater = (new ItemPlacesBlock(100, Block.redstoneRepeaterIdle)).setIconCoord(6, 5).setItemName("diode").setCreativeTab(CreativeTabs.tabRedstone);
	public static Item cookie = (new ItemFood(101, 1, 0.1F, false)).setIconCoord(12, 5).setItemName("cookie");
	public static ItemShears shears = (ItemShears)(new ItemShears(103)).setIconCoord(13, 5).setItemName("shears");
	public static Item melon = (new ItemFood(104, 2, 0.3F, false)).setIconCoord(13, 6).setItemName("melon");
	public static Item beefRaw = (new ItemFood(107, 3, 0.3F, true)).setIconCoord(9, 6).setItemName("beefRaw");
	public static Item beefCooked = (new ItemFood(108, 8, 0.8F, true)).setIconCoord(10, 6).setItemName("beefCooked");
	public static Item chickenRaw = (new ItemFood(109, 2, 0.3F, true)).setIconCoord(9, 7).setItemName("chickenRaw");
	public static Item chickenCooked = (new ItemFood(110, 6, 0.6F, true)).setIconCoord(10, 7).setItemName("chickenCooked");
	public static Item rottenFlesh = (new ItemFood(111, 4, 0.1F, true)).setIconCoord(11, 5).setItemName("rottenFlesh");
	public static Item blazeRod = (new Item(113)).setIconCoord(12, 6).setItemName("blazeRod").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item ghastTear = (new Item(114)).setIconCoord(11, 7).setItemName("ghastTear").setCreativeTab(CreativeTabs.tabBrewing);
	public static Item goldNugget = (new ItemGolden(115)).setIconCoord(12, 7).setItemName("goldNugget").setCreativeTab(CreativeTabs.tabMaterials);
	public static Item netherStalkSeeds = (new ItemSeeds(116, Block.netherStalk.blockID, Block.slowSand.blockID)).setIconCoord(13, 7).setItemName("netherStalkSeeds").setPotionEffect("+4");
	public static Item glassBottle = (new Item(118)).setIconCoord(12, 8).setItemName("glassBottle");
	public static Item monsterPlacer = (new ItemMonsterPlacer(127)).setIconCoord(9, 9).setItemName("monsterPlacer");

	public static Item fireballCharge = (new ItemFireball(129)).setIconCoord(14, 2).setItemName("fireball");
	public static Item emerald = (new Item(132)).setIconCoord(10, 11).setItemName("emerald").setCreativeTab(CreativeTabs.tabMaterials);
	
	public static Item record13 = (new ItemRecord(2000, "13")).setIconCoord(0, 15).setItemName("record");
	public static Item recordCat = (new ItemRecord(2001, "cat")).setIconCoord(1, 15).setItemName("record");
	public static Item recordBlocks = (new ItemRecord(2002, "blocks")).setIconCoord(2, 15).setItemName("record");
	public static Item recordChirp = (new ItemRecord(2003, "chirp")).setIconCoord(3, 15).setItemName("record");
	public static Item recordFar = (new ItemRecord(2004, "far")).setIconCoord(4, 15).setItemName("record");
	public static Item recordMall = (new ItemRecord(2005, "mall")).setIconCoord(5, 15).setItemName("record");
	public static Item recordMellohi = (new ItemRecord(2006, "mellohi")).setIconCoord(6, 15).setItemName("record");
	public static Item recordStal = (new ItemRecord(2007, "stal")).setIconCoord(7, 15).setItemName("record");
	public static Item recordStrad = (new ItemRecord(2008, "strad")).setIconCoord(8, 15).setItemName("record");
	public static Item recordWard = (new ItemRecord(2009, "ward")).setIconCoord(9, 15).setItemName("record");
	public static Item record11 = (new ItemRecord(2010, "11")).setIconCoord(10, 15).setItemName("record");
	
	
	// Misc
	public static Item recipeBook = (new ItemRecipeBook(400)).setIconIndex(237).setItemName("RecipeBook").setMaxStackSize(1).setCreativeTab(CreativeTabs.tabMisc);

	public static Item maceDiamond = (new ItemLongSword(420, EnumToolMaterial.EMERALD)).setIconCoord(3, 9).setItemName("maceDiamond");
	public static Item hammerDiamond = (new ItemHammer(421, EnumToolMaterial.EMERALD)).setIconCoord(3, 10).setItemName("hammerDiamond");
	public static Item battleDiamond = (new ItemBattleAxe(422, EnumToolMaterial.EMERALD)).setIconCoord(3, 11).setItemName("battleAxeDiamond");
	public static Item knifeDiamond = (new ItemKnife(423, EnumToolMaterial.EMERALD)).setIconCoord(3, 12).setItemName("knifeDiamond");
	
	public static Item maceGold = (new ItemLongSword(424, EnumToolMaterial.GOLD)).setIconCoord(4, 9).setItemName("maceGold");
	public static Item hammerGold = (new ItemHammer(425, EnumToolMaterial.GOLD)).setIconCoord(4, 10).setItemName("hammerGold");
	public static Item battleGold = (new ItemBattleAxe(426, EnumToolMaterial.GOLD)).setIconCoord(4, 11).setItemName("battleAxeGold");
	public static Item knifeGold = (new ItemKnife(427, EnumToolMaterial.GOLD)).setIconCoord(4, 12).setItemName("knifeGold");
	
	public static Item maceSteel = (new ItemLongSword(428, EnumToolMaterial.IRON)).setIconCoord(2, 9).setItemName("maceSteel");
	public static Item hammerSteel = (new ItemHammer(429, EnumToolMaterial.IRON)).setIconCoord(2, 10).setItemName("hammerSteel");
	public static Item battleSteel = (new ItemBattleAxe(430, EnumToolMaterial.IRON)).setIconCoord(2, 11).setItemName("battleAxeSteel");
	public static Item knifeSteel = (new ItemKnife(431, EnumToolMaterial.IRON)).setIconCoord(2, 12).setItemName("knifeSteel");
	
	public static Item maceStone = (new ItemLongSword(432, EnumToolMaterial.STONE)).setIconCoord(1, 9).setItemName("maceStone");
	public static Item hammerStone = (new ItemHammer(433, EnumToolMaterial.STONE)).setIconCoord(1, 10).setItemName("hammerStone");
	public static Item battleStone = (new ItemBattleAxe(434, EnumToolMaterial.STONE)).setIconCoord(1, 11).setItemName("battleAxeStone");
	public static Item knifeStone = (new ItemKnife(435, EnumToolMaterial.STONE)).setIconCoord(1, 12).setItemName("knifeStone");
	
	public static Item battleWood = (new ItemBattleAxe(436, EnumToolMaterial.WOOD)).setIconCoord(0, 11).setItemName("battleAxeWood");
	public static Item knifeWood = (new ItemKnife(437, EnumToolMaterial.WOOD)).setIconCoord(0, 12).setItemName("knifeWood");
	
	public static Item magicWand = (new ItemMagicWand(999-256)).setIconCoord(8, 12).setItemName("magic wand");
	
	protected static final Item armorPieceForTier [][] = new Item[][] {
		{ Item.helmetLeather, Item.plateLeather, Item.legsLeather, Item.bootsLeather }, 
		{ Item.helmetChain, Item.plateChain, Item.legsChain, Item.bootsChain },
		{ Item.helmetGold, Item.plateGold, Item.legsGold, Item.bootsGold },
		{ Item.helmetSteel, Item.plateSteel, Item.legsSteel, Item.bootsSteel },
		{ Item.helmetDiamond, Item.plateDiamond, Item.legsDiamond, Item.bootsDiamond }
	};
	
	public final int shiftedIndex;
	protected int maxStackSize = 64;
	private int maxDamage = 0;
	public int iconIndex;
	protected boolean bFull3D = false;
	protected boolean hasSubtypes = false;
	private Item containerItem = null;
	private String potionEffect = null;
	private String itemName;
	public boolean isDefaultTexture = true;
	private String currentTexture = "/gui/items.png";
	public CreativeTabs displayOnCreativeTab;

	protected Item(int i1) {
		this.shiftedIndex = 256 + i1;
		if(itemsList[256 + i1] != null) {
			System.out.println("CONFLICT @ " + i1);
		}

		itemsList[256 + i1] = this;
	}
	
	public Item fixForAlphaBeta() {
		return this;
	}

	public Item setCreativeTab(CreativeTabs creativeTab) {
		this.displayOnCreativeTab = creativeTab;
		return this;
	}
	
	public CreativeTabs getCreativeTab() {
		return this.displayOnCreativeTab;
	}
	
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
		par3List.add(new ItemStack(par1, 1, 0));
	}
	
	public Item setIconIndex(int i1) {
		this.iconIndex = i1;
		return this;
	}

	public Item setMaxStackSize(int i1) {
		this.maxStackSize = i1;
		return this;
	}

	public Item setIconCoord(int i1, int i2) {
		this.iconIndex = i1 + i2 * 16;
		return this;
	}

	public int getIconFromDamage(int i1) {
		return this.iconIndex;
	}

	public final int getIconIndex(ItemStack itemStack1) {
		return this.getIconFromDamage(itemStack1.getItemDamage());
	}

	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int face) {
		return false;
	}
	
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int face, float xWithinFace, float yWithinFace, float zWithinFace, boolean keyPressed) {
		return this.onItemUse(itemStack, entityPlayer, world, x, y, z, face);
	}

	public float getStrVsBlock(ItemStack itemStack1, Block block2) {
		return 1.0F;
	}

	public ItemStack onItemRightClick(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
		return itemStack1;
	}

	public ItemStack onFoodEaten(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
		return itemStack1;
	}

	public int getItemStackLimit() {
		return this.maxStackSize;
	}

	public int getMetadata(int i1) {
		return 0;
	}

	public boolean getHasSubtypes() {
		return this.hasSubtypes;
	}

	protected Item setHasSubtypes(boolean z1) {
		this.hasSubtypes = z1;
		return this;
	}

	public int getMaxDamage() {
		return this.maxDamage;
	}

	protected Item setMaxDamage(int i1) {
		this.maxDamage = i1;
		return this;
	}

	public boolean isDamageable() {
		return this.maxDamage > 0 && !this.hasSubtypes;
	}

	public boolean hitEntity(ItemStack itemStack1, EntityLiving entityLiving2, EntityLiving entityLiving3) {
		return false;
	}

	public boolean onBlockDestroyed(ItemStack itemStack1, int i2, int i3, int i4, int i5, EntityLiving entityLiving6) {
		return false;
	}
	
	public boolean onBlockDestroyed(ItemStack itemStack1, World world, int i2, int i3, int i4, int i5, EntityLiving entityLiving6) {
		return this.onBlockDestroyed(itemStack1, i2, i3, i4, i5, entityLiving6);
	}

	public int getDamageVsEntity(Entity entity1) {
		return 1;
	}
	
	/*
	 * Add knock back when hitting an entity.
	 */
	public float getExtraKnockbackVsEntity(Entity entity) {
		return 0;
	}

	/*
	 * Default swinging speed = 6, less is faster. 
	 */
	public int getSwingSpeed() {
		return 6;
	}
	
	public boolean canHarvestBlock(Block block1) {
		return false;
	}

	public void useItemOnEntity(ItemStack itemStack1, EntityLiving entityLiving2) {
	}

	public Item setFull3D() {
		this.bFull3D = true;
		return this;
	}

	public boolean isFull3D() {
		return this.bFull3D;
	}

	public boolean shouldRotateAroundWhenRendering() {
		return false;
	}

	public Item setItemName(String string1) {
		this.itemName = "item." + string1;
		return this;
	}

	public String getLocalItemName(ItemStack itemStack1) {
		String string2 = this.getItemNameIS(itemStack1);
		return string2 == null ? "" : Translator.translateToLocal(string2);
	}

	public String getItemName() {
		return this.itemName;
	}

	public String getItemNameIS(ItemStack itemStack1) {
		return this.itemName;
	}

	public Item setContainerItem(Item item1) {
		this.containerItem = item1;
		return this;
	}

	public boolean doesContainerItemLeaveCraftingGrid(ItemStack itemStack1) {
		return true;
	}

	public boolean hasMetadata() {
		return false;
	}

	public Item getContainerItem() {
		return this.containerItem;
	}

	public boolean hasContainerItem() {
		return this.containerItem != null;
	}

	public String getStatName() {
		return Translator.translateToLocal(this.getItemName() + ".name");
	}

	public int getColorFromDamage(int i1, int i2) {
		return 0xFFFFFF;
	}

	public void onUpdate(ItemStack itemStack1, World world2, Entity entity3, int i4, boolean z5) {
	}

	public void onCreated(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
	}

	public boolean s_func_28019_b() {
		return false;
	}

	public EnumAction getItemUseAction(ItemStack itemStack1) {
		return EnumAction.none;
	}

	public int getMaxItemUseDuration(ItemStack itemStack1) {
		return 0;
	}

	public void onPlayerStoppedUsing(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3, int i4) {
	}

	protected Item setPotionEffect(String string1) {
		this.potionEffect = string1;
		return this;
	}

	public String getPotionEffect() {
		return this.potionEffect;
	}

	public boolean isPotionIngredient() {
		return this.potionEffect != null;
	}

	public void addInformation(ItemStack itemStack1, List<String> list2) {
	}

	public String getItemDisplayName(ItemStack itemStack1) {
		String string2 = ("" + StringTranslate.getInstance().translateNamedKey(this.getLocalItemName(itemStack1))).trim();
		return string2;
	}

	public boolean hasEffect(ItemStack itemStack1) {
		return itemStack1.isItemEnchanted();
	}

	public EnumRarity getRarity(ItemStack itemStack1) {
		return itemStack1.isItemEnchanted() ? EnumRarity.rare : EnumRarity.common;
	}

	public boolean isItemTool(ItemStack itemStack1) {
		return this.getItemStackLimit() == 1 && this.isDamageable();
	}

	protected MovingObjectPosition getMovingObjectPositionFromPlayer(World world1, EntityPlayer entityPlayer2, boolean z3) {
		float f4 = 1.0F;
		float f5 = entityPlayer2.prevRotationPitch + (entityPlayer2.rotationPitch - entityPlayer2.prevRotationPitch) * f4;
		float f6 = entityPlayer2.prevRotationYaw + (entityPlayer2.rotationYaw - entityPlayer2.prevRotationYaw) * f4;
		double d7 = entityPlayer2.prevPosX + (entityPlayer2.posX - entityPlayer2.prevPosX) * (double)f4;
		double d9 = entityPlayer2.prevPosY + (entityPlayer2.posY - entityPlayer2.prevPosY) * (double)f4 + 1.62D - (double)entityPlayer2.yOffset;
		double d11 = entityPlayer2.prevPosZ + (entityPlayer2.posZ - entityPlayer2.prevPosZ) * (double)f4;
		Vec3D vec3D13 = Vec3D.createVector(d7, d9, d11);
		float f14 = MathHelper.cos(-f6 * 0.017453292F - (float)Math.PI);
		float f15 = MathHelper.sin(-f6 * 0.017453292F - (float)Math.PI);
		float f16 = -MathHelper.cos(-f5 * 0.017453292F);
		float f17 = MathHelper.sin(-f5 * 0.017453292F);
		float f18 = f15 * f16;
		float f20 = f14 * f16;
		double d21 = 5.0D;
		Vec3D vec3D23 = vec3D13.addVector((double)f18 * d21, (double)f17 * d21, (double)f20 * d21);
		MovingObjectPosition movingObjectPosition24 = world1.rayTraceBlocks_do_do(vec3D13, vec3D23, z3, !z3);
		return movingObjectPosition24;
	}

	public int getItemEnchantability() {
		return 0;
	}

	public boolean requiresMultipleRenderPasses() {
		return false;
	}

	public int getIconFromDamageAndRenderpass(int i1, int i2) {
		return this.getIconFromDamage(i1);
	}

	public String getTextureFile() {
		return this instanceof ItemBlock ? Block.blocksList[((ItemBlock)this).getBlockID()].getTextureFile() : this.currentTexture;
	}

	public void setTextureFile(String texture) {
		this.currentTexture = texture;
		this.isDefaultTexture = false;
	}

	public void onUsingItemTick(ItemStack stack, EntityPlayer player, int count) {
	}
	
}
