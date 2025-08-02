package net.minecraft.world.level.tile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.util.Translator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumMobType;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.item.EnumToolMaterial;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ITextureProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemColored;
import net.minecraft.world.item.ItemDyeable;
import net.minecraft.world.item.ItemLeaves;
import net.minecraft.world.item.ItemMetadata;
import net.minecraft.world.item.ItemSapling;
import net.minecraft.world.item.ItemSlab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemTool;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.level.tile.entity.TileEntitySign;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

public class Block implements ITextureProvider {
	
	// Step sounds
	public static final StepSound soundPowderFootstep = new StepSound("stone", 1.0F, 1.0F);
	public static final StepSound soundWoodFootstep = new StepSound("wood", 1.0F, 1.0F);
	public static final StepSound soundGravelFootstep = new StepSound("gravel", 1.0F, 1.0F);
	public static final StepSound soundGrassFootstep = new StepSound("grass", 1.0F, 1.0F);
	public static final StepSound soundStoneFootstep = new StepSound("stone", 1.0F, 1.0F);
	public static final StepSound soundMetalFootstep = new StepSound("stone", 1.0F, 1.5F);
	public static final StepSound soundGlassFootstep = new StepSoundStone("stone", 1.0F, 1.0F);
	public static final StepSound soundClothFootstep = new StepSound("cloth", 1.0F, 1.0F);
	public static final StepSound soundSandFootstep = new StepSoundSand("sand", 1.0F, 1.0F);
	
	// Block arrays
	public static final Block[] blocksList = new Block[4096];
	public static final boolean[] opaqueCubeLookup = new boolean[4096];
	public static final int[] lightOpacity = new int[4096];
	public static final boolean[] canBlockGrass = new boolean[4096];
	public static final int[] lightValue = new int[4096];
	public static final boolean[] requiresSelfNotify = new boolean[4096];
	public static boolean[] useNeighborBrightness = new boolean[4096];
	
	// Blocks
	public static final Block stone = (new BlockStone(1, 1)).setHardness(1.5F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("stone").setCreativeTab(CreativeTabs.tabBlock);
	public static final BlockGrass grass = (BlockGrass)(new BlockGrass(2)).setHardness(0.6F).setStepSound(soundGrassFootstep).setBlockName("grass");
	public static final Block dirt = (new BlockDirt(3, 2)).setHardness(0.5F).setStepSound(soundGravelFootstep).setBlockName("dirt");
	public static final Block cobblestone = (new BlockCobblestone(4, 16, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("stonebrick").setCreativeTab(CreativeTabs.tabBlock);
	public static final Block planks = (new BlockWood(5)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundWoodFootstep).setBlockName("wood").setRequiresSelfNotify();
	public static final Block sapling = (new BlockSapling(6, 15)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("sapling").setRequiresSelfNotify();
	public static final Block bedrock = (new Block(7, 17, Material.rock)).setBlockUnbreakable().setResistance(6000000.0F).setStepSound(soundStoneFootstep).setBlockName("bedrock").disableStats().setCreativeTab(CreativeTabs.tabBlock);
	public static final Block waterMoving = (new BlockFlowing(8, Material.water)).setHardness(100.0F).setLightOpacity(3).setBlockName("water").disableStats().setRequiresSelfNotify();
	public static final Block waterStill = (new BlockStationary(9, Material.water)).setHardness(100.0F).setLightOpacity(3).setBlockName("water").disableStats().setRequiresSelfNotify();
	public static final Block lavaMoving = (new BlockFlowing(10, Material.lava)).setHardness(0.0F).setLightValue(1.0F).setLightOpacity(255).setBlockName("lava").disableStats().setRequiresSelfNotify();
	public static final Block lavaStill = (new BlockStationary(11, Material.lava)).setHardness(100.0F).setLightValue(1.0F).setLightOpacity(255).setBlockName("lava").disableStats().setRequiresSelfNotify();
	public static final Block sand = (new BlockSand(12, 18)).setHardness(0.5F).setStepSound(soundSandFootstep).setBlockName("sand");
	public static final Block gravel = (new BlockGravel(13, 19)).setHardness(0.6F).setStepSound(soundGravelFootstep).setBlockName("gravel");
	public static final Block oreGold = (new BlockOre(14, 32)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreGold");
	public static final Block oreIron = (new BlockOre(15, 33)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreIron");
	public static final Block oreCoal = (new BlockOre(16, 34)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreCoal");
	public static final Block wood = (new BlockLog(17)).setHardness(2.0F).setStepSound(soundWoodFootstep).setBlockName("log").setRequiresSelfNotify();
	public static final BlockLeaves leaves = (BlockLeaves)(new BlockLeaves(18, 52)).setHardness(0.2F).setLightOpacity(1).setStepSound(soundGrassFootstep).setBlockName("leaves").setRequiresSelfNotify();
	public static final Block sponge = (new BlockSponge(19, true)).setHardness(0.6F).setStepSound(soundGrassFootstep).setBlockName("sponge");
	public static final Block glass = (new BlockGlass(20, 49, Material.glass, false)).setHardness(0.3F).setStepSound(soundGlassFootstep).setBlockName("glass");
	public static final Block oreLapis = (new BlockOre(21, 160)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreLapis");
	public static final Block blockLapis = (new Block(22, 144, Material.rock)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("blockLapis").setCreativeTab(CreativeTabs.tabBlock);
	public static final Block dispenser = (new BlockDispenser(23)).setHardness(3.5F).setStepSound(soundStoneFootstep).setBlockName("dispenser").setRequiresSelfNotify();
	public static final Block sandStone = (new BlockSandStone(24)).setStepSound(soundStoneFootstep).setHardness(0.8F).setBlockName("sandStone").setRequiresSelfNotify();
	// 25
	// 26
	public static final Block railPowered = (new BlockRail(27, 179, true)).setHardness(0.7F).setStepSound(soundMetalFootstep).setBlockName("goldenRail").setRequiresSelfNotify();
	// 28
	// 29
	public static final Block web = (new BlockWeb(30, 11)).setLightOpacity(1).setHardness(4.0F).setBlockName("web");
	public static final BlockTallGrass tallGrass = (BlockTallGrass)(new BlockTallGrass(31, 39)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("tallgrass");
	public static final BlockDeadBush deadBush = (BlockDeadBush)(new BlockDeadBush(32, 55)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("deadbush");
	// 33
	// 34
	public static final Block cloth = (new BlockCloth()).setHardness(0.8F).setStepSound(soundClothFootstep).setBlockName("cloth").setRequiresSelfNotify();
	// 36
	public static final BlockFlower plantYellow = (BlockFlower)(new BlockFlower(37, 13)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("flower");
	public static final BlockFlower plantRed = (BlockFlower)(new BlockFlower(38, 12)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("rose");
	public static final BlockFlower mushroomBrown = (BlockFlower)(new BlockMushroom(39, 29)).setHardness(0.0F).setStepSound(soundGrassFootstep).setLightValue(0.125F).setBlockName("mushroom");
	public static final BlockFlower mushroomRed = (BlockFlower)(new BlockMushroom(40, 28)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("mushroom");
	public static final Block blockGold = (new BlockGold(41, 23)).setHardness(3.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setBlockName("blockGold");
	public static final Block blockSteel = (new BlockIron(42, 22)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setBlockName("blockIron");
	public static final Block stairDouble = (new BlockStep(43, true)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("stoneSlab");
	public static final Block stairSingle = (new BlockStep(44, false)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("stoneSlab");
	public static final Block brick = (new BlockBrick(45, 7, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("brick").setCreativeTab(CreativeTabs.tabBlock);
	public static final Block tnt = (new BlockTNT(46, 8)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("tnt");
	public static final Block bookShelf = (new BlockBookshelf(47, 35)).setHardness(1.5F).setStepSound(soundWoodFootstep).setBlockName("bookshelf");
	public static final Block cobblestoneMossy = (new Block(48, 36, Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("stoneMoss").setCreativeTab(CreativeTabs.tabBlock);
	public static final Block obsidian = (new BlockObsidian(49, 37)).setHardness(50.0F).setResistance(2000.0F).setStepSound(soundStoneFootstep).setBlockName("obsidian");
	public static final Block torchWood = (new BlockTorch(50, 80)).setHardness(0.0F).setLightValue(0.9375F).setStepSound(soundWoodFootstep).setBlockName("torch").setRequiresSelfNotify();
	public static final BlockFire fire = (BlockFire)(new BlockFire(51, 31)).setHardness(0.0F).setLightValue(1.0F).setStepSound(soundWoodFootstep).setBlockName("fire").disableStats();
	public static final Block mobSpawner = (new BlockMobSpawner(52, 65, false)).setHardness(5.0F).setStepSound(soundMetalFootstep).setBlockName("mobSpawner").disableStats();
	public static final Block stairCompactPlanks = (new BlockStairs(53, planks)).setBlockName("stairsWood").setRequiresSelfNotify();
	public static final Block chest = (new BlockChest(54)).setHardness(2.5F).setStepSound(soundWoodFootstep).setBlockName("chest").setRequiresSelfNotify();
	public static final Block redstoneWire = (new BlockRedstoneWire(55, 164)).setHardness(0.0F).setStepSound(soundPowderFootstep).setBlockName("redstoneDust").disableStats().setRequiresSelfNotify();
	public static final Block oreDiamond = (new BlockOre(56, 50)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreDiamond");
	public static final Block blockDiamond = (new BlockDiamond(57, 24)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setBlockName("blockDiamond");
	public static final Block workbench = (new BlockWorkbench(58)).setHardness(2.5F).setStepSound(soundWoodFootstep).setBlockName("workbench");
	public static final Block crops = (new BlockCrops(59, 88)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("crops").disableStats().setRequiresSelfNotify();
	public static final Block tilledField = (new BlockFarmland(60)).setHardness(0.6F).setStepSound(soundGravelFootstep).setBlockName("farmland").setRequiresSelfNotify();
	public static final Block stoneOvenIdle = (new BlockFurnace(61, false)).setHardness(3.5F).setStepSound(soundStoneFootstep).setBlockName("furnace").setRequiresSelfNotify().setCreativeTab(CreativeTabs.tabDeco);
	public static final Block stoneOvenActive = (new BlockFurnace(62, true)).setHardness(3.5F).setStepSound(soundStoneFootstep).setLightValue(0.875F).setBlockName("furnace").setRequiresSelfNotify();
	public static final Block signPost = (new BlockSign(63, TileEntitySign.class, true)).setHardness(1.0F).setStepSound(soundWoodFootstep).setBlockName("sign").disableStats().setRequiresSelfNotify();
	public static final Block doorWood = (new BlockDoor(64, Material.wood)).setHardness(3.0F).setStepSound(soundWoodFootstep).setBlockName("doorWood").disableStats().setRequiresSelfNotify();
	public static final Block ladder = (new BlockLadder(65, 83)).setHardness(0.4F).setStepSound(soundWoodFootstep).setBlockName("ladder").setRequiresSelfNotify();
	public static final Block rail = (new BlockRail(66, 128, false)).setHardness(0.7F).setStepSound(soundMetalFootstep).setBlockName("rail").setRequiresSelfNotify();
	public static final Block stairCompactCobblestone = (new BlockStairs(67, cobblestone)).setBlockName("stairsStone").setRequiresSelfNotify();
	public static final Block signWall = (new BlockSign(68, TileEntitySign.class, false)).setHardness(1.0F).setStepSound(soundWoodFootstep).setBlockName("sign").disableStats().setRequiresSelfNotify();
	public static final Block lever = (new BlockLever(69, 96)).setHardness(0.5F).setStepSound(soundWoodFootstep).setBlockName("lever").setRequiresSelfNotify();
	public static final Block pressurePlateStone = (new BlockPressurePlate(70, stone.blockIndexInTexture, EnumMobType.mobs, Material.rock)).setHardness(0.5F).setStepSound(soundStoneFootstep).setBlockName("pressurePlate").setRequiresSelfNotify();
	public static final Block doorSteel = (new BlockDoor(71, Material.iron)).setHardness(5.0F).setStepSound(soundMetalFootstep).setBlockName("doorIron").disableStats().setRequiresSelfNotify();
	public static final Block pressurePlatePlanks = (new BlockPressurePlate(72, planks.blockIndexInTexture, EnumMobType.everything, Material.wood)).setHardness(0.5F).setStepSound(soundWoodFootstep).setBlockName("pressurePlate").setRequiresSelfNotify();
	public static final Block oreRedstone = (new BlockRedstoneOre(73, 51, false)).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreRedstone").setRequiresSelfNotify();
	public static final Block oreRedstoneGlowing = (new BlockRedstoneOre(74, 51, true)).setLightValue(0.625F).setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreRedstone").setRequiresSelfNotify();
	public static final Block torchRedstoneIdle = (new BlockRedstoneTorch(75, 115, false)).setHardness(0.0F).setStepSound(soundWoodFootstep).setBlockName("notGate").setRequiresSelfNotify();
	public static final Block torchRedstoneActive = (new BlockRedstoneTorch(76, 99, true)).setHardness(0.0F).setLightValue(0.5F).setStepSound(soundWoodFootstep).setBlockName("notGate").setRequiresSelfNotify().setCreativeTab(CreativeTabs.tabRedstone);
	public static final Block button = (new BlockButton(77, stone.blockIndexInTexture)).setHardness(0.5F).setStepSound(soundStoneFootstep).setBlockName("button").setRequiresSelfNotify();
	public static final Block snow = (new BlockSnow(78, 66)).setHardness(0.1F).setStepSound(soundClothFootstep).setBlockName("snow").setLightOpacity(0);
	public static final Block ice = (new BlockIce(79, 67)).setHardness(0.5F).setLightOpacity(3).setStepSound(soundGlassFootstep).setBlockName("ice");
	public static final Block blockSnow = (new BlockSnowBlock(80, 66)).setHardness(0.2F).setStepSound(soundClothFootstep).setBlockName("snow");
	public static final Block cactus = (new BlockCactus(81, 70)).setHardness(0.4F).setStepSound(soundClothFootstep).setBlockName("cactus");
	public static final Block blockClay = (new BlockClay(82, 72)).setHardness(0.6F).setStepSound(soundGravelFootstep).setBlockName("clay");
	public static final Block reed = (new BlockReed(83, 73)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("reeds").disableStats();
	public static final Block jukebox = (new BlockJukeBox(84, 74)).setHardness(2.0F).setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("jukebox").setRequiresSelfNotify();
	public static final Block fence = (new BlockFence(85, 4)).setHardness(2.0F).setResistance(5.0F).setStepSound(soundWoodFootstep).setBlockName("fence");
	public static final Block pumpkin = (new BlockPumpkin(86, 102, false)).setHardness(1.0F).setStepSound(soundWoodFootstep).setBlockName("pumpkin").setRequiresSelfNotify();
	public static final Block netherrack = (new BlockNetherrack(87, 103)).setHardness(0.4F).setStepSound(soundStoneFootstep).setBlockName("hellrock");
	public static final Block slowSand = (new BlockSoulSand(88, 104)).setHardness(0.5F).setStepSound(soundSandFootstep).setBlockName("hellsand");
	public static final Block glowStone = (new BlockGlowStone(89, 105, Material.glass)).setHardness(0.3F).setStepSound(soundGlassFootstep).setLightValue(1.0F).setBlockName("lightgem");
	public static final BlockPortal portal = (BlockPortal)(new BlockPortal(90, 14)).setHardness(-1.0F).setStepSound(soundGlassFootstep).setLightValue(0.75F).setBlockName("portal");
	public static final Block pumpkinLantern = (new BlockPumpkin(91, 102, true)).setHardness(1.0F).setStepSound(soundWoodFootstep).setLightValue(1.0F).setBlockName("litpumpkin").setRequiresSelfNotify();	
	public static final Block cake = (new BlockCake(92, 121)).setHardness(0.5F).setStepSound(soundClothFootstep).setBlockName("cake").disableStats().setRequiresSelfNotify();
	public static final Block redstoneRepeaterIdle = (new BlockRedstoneRepeater(93, false)).setHardness(0.0F).setStepSound(soundWoodFootstep).setBlockName("diode").disableStats().setRequiresSelfNotify();
	public static final Block redstoneRepeaterActive = (new BlockRedstoneRepeater(94, true)).setHardness(0.0F).setLightValue(0.625F).setStepSound(soundWoodFootstep).setBlockName("diode").disableStats().setRequiresSelfNotify();	
	// 95
	// 96
	// 97
	// 98
	// 99
	// 100
	public static final Block fenceIron = (new BlockPane(101, 85, 85, Material.iron, true)).setHardness(5.0F).setResistance(10.0F).setStepSound(soundMetalFootstep).setBlockName("fenceIron");
	public static final Block thinGlass = (new BlockPane(102, 49, 148, Material.glass, false)).setHardness(0.3F).setStepSound(soundGlassFootstep).setBlockName("thinGlass");
	// 103
	// 104
	// 105
	public static final Block vine = (new BlockVine(106)).setHardness(0.2F).setStepSound(soundGrassFootstep).setBlockName("vine").setRequiresSelfNotify();
	// 107
	public static final Block stairsBrick = (new BlockStairs(108, brick)).setBlockName("stairsBrick").setRequiresSelfNotify();
	// 109 
	// 110
	public static final Block waterlily = (new BlockLilypad(111, 60)).setHardness(0.0F).setStepSound(soundGrassFootstep).setBlockName("waterlily");
	// 112
	// 113
	// 114
	public static final Block netherStalk = (new BlockNetherStalk(115)).setBlockName("netherStalk").setRequiresSelfNotify();
	// 1166 - 135
	public static final Block mobSpawnerOneshot = (new BlockMobSpawner(136, 65, true)).setHardness(5.0F).setStepSound(soundMetalFootstep);
	public static final Block blockCoal = (new Block(137, 12 * 16 + 10, Material.rock)).setHardness(1.5F).setResistance(10.0F).setStepSound(soundStoneFootstep).setCreativeTab(CreativeTabs.tabBlock).setBlockName("blockCoal");	
	//
	// 138 - 167 
	public static final Block leafPile = (new BlockLeafPile(168)).setHardness(0.1F).setResistance(0.1F).setStepSound(soundGrassFootstep).setBlockName("leafPile");
	// 169 - 208
	public static final Block cryingObsidian = (new BlockCryingObsidian(209, 11*16+8)).setHardness(10.0F).setResistance(2000.0F).setStepSound(soundStoneFootstep).setBlockName("cryingObsidian");
	//public static final Block spongeOff = (new BlockSponge(210, false)).setHardness(0.6F).setStepSound(soundGrassFootstep).setBlockName("sponge");
	
	public int blockIndexInTexture;
	public final int blockID;
	public float blockHardness;
	protected float blockResistance;
	protected boolean blockConstructorCalled;
	protected boolean enableStats;
	protected boolean needsRandomTick;
	protected boolean isBlockContainer;
	public double minX;
	public double minY;
	public double minZ;
	public double maxX;
	public double maxY;
	public double maxZ;
	public StepSound stepSound;
	public float blockParticleGravity;
	public final Material blockMaterial;
	public float slipperiness;
	private String blockName;
	protected String currentTexture;
	public boolean isDefaultTexture;
	public CreativeTabs displayOnCreativeTab;

	protected Block(int i1, Material material2) {
		this.currentTexture = "/terrain.png";
		this.isDefaultTexture = true;
		this.blockConstructorCalled = true;
		this.enableStats = true;
		this.stepSound = soundPowderFootstep;
		this.blockParticleGravity = 1.0F;
		this.slipperiness = 0.6F;
		if(blocksList[i1] != null) {
			throw new IllegalArgumentException("Slot " + i1 + " is already occupied by " + blocksList[i1] + " when adding " + this);
		} else {
			this.blockMaterial = material2;
			blocksList[i1] = this;
			this.blockID = i1;
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			opaqueCubeLookup[i1] = this.isOpaqueCube();
			lightOpacity[i1] = this.isOpaqueCube() ? 255 : 0;
			canBlockGrass[i1] = !material2.getCanBlockGrass();
			this.isDefaultTexture = this.getTextureFile() != null && this.getTextureFile().equalsIgnoreCase("/terrain.png");
		}
	}
	
	public Block setCreativeTab(CreativeTabs creativeTab) {
		this.displayOnCreativeTab = creativeTab;
		return this;
	}
	
	public CreativeTabs getCreativeTab() {
		return this.displayOnCreativeTab;
	}
	
	public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
		par3List.add(new ItemStack(par1, 1, 0));
	}

	protected Block setRequiresSelfNotify() {
		requiresSelfNotify[this.blockID] = true;
		return this;
	}

	protected void initializeBlock() {
	}

	protected Block(int i1, int i2, Material material3) {
		this(i1, material3);
		this.blockIndexInTexture = i2;
	}

	protected Block setStepSound(StepSound stepSound1) {
		this.stepSound = stepSound1;
		return this;
	}

	protected Block setLightOpacity(int i1) {
		lightOpacity[this.blockID] = i1;
		return this;
	}

	protected Block setLightValue(float f1) {
		lightValue[this.blockID] = (int)(15.0F * f1);
		return this;
	}

	protected Block setResistance(float f1) {
		this.blockResistance = f1 * 3.0F;
		return this;
	}

	public static boolean isNormalCube(int i0) {
		Block block1 = blocksList[i0];
		return block1 == null ? false : block1.blockMaterial.isOpaque() && block1.renderAsNormalBlock();
	}
	
	public boolean isBlockNormalCube(int metadata) {
		return Block.isNormalCube(this.blockID);
	}

	public boolean renderAsNormalBlock() {
		return true;
	}

	public boolean getBlocksMovement(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		return !this.blockMaterial.blocksMovement();
	}

	public int getRenderType() {
		return 0;
	}

	protected Block setHardness(float f1) {
		this.blockHardness = f1;
		if(this.blockResistance < f1 * 5.0F) {
			this.blockResistance = f1 * 5.0F;
		}

		return this;
	}

	protected Block setBlockUnbreakable() {
		this.setHardness(-1.0F);
		return this;
	}

	public float getHardness() {
		return this.blockHardness;
	}
	
	public float getHardness(int meta) {
		return this.getHardness();
	}

	protected Block setTickRandomly(boolean z1) {
		this.needsRandomTick = z1;
		return this;
	}

	public boolean getTickRandomly() {
		return this.needsRandomTick;
	}

	public boolean hasTileEntity(int metadata) {
		return this.isBlockContainer;
	}
	
	public boolean hasTileEntity() {
		return this.hasTileEntity(0);
	}
	
	public TileEntity getTileEntity(int metadata) {
		return this instanceof BlockContainer ? ((BlockContainer)this).getBlockEntity(metadata) : null;
	}

	public void setBlockBounds(float f1, float f2, float f3, float f4, float f5, float f6) {
		this.minX = (double)f1;
		this.minY = (double)f2;
		this.minZ = (double)f3;
		this.maxX = (double)f4;
		this.maxY = (double)f5;
		this.maxZ = (double)f6;
	}

	public float getBlockBrightness(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		return iBlockAccess1.getBrightness(i2, i3, i4, lightValue[this.blockID]);
	}

	public int getMixedBrightnessForBlock(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		return iBlockAccess1.getLightBrightnessForSkyBlocks(i2, i3, i4, lightValue[this.blockID]);
	}

	/*
	public boolean shouldSideBeRendered(IBlockAccess iBlockAccess1, int i2, int i3, int i4, int i5) {
		return i5 == 0 && this.minY > 0.0D ? true : (i5 == 1 && this.maxY < 1.0D ? true : (i5 == 2 && this.minZ > 0.0D ? true : (i5 == 3 && this.maxZ < 1.0D ? true : (i5 == 4 && this.minX > 0.0D ? true : (i5 == 5 && this.maxX < 1.0D ? true : !iBlockAccess1.isBlockOpaqueCube(i2, i3, i4))))));
	}
	*/
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		// 0 is bottom
		// 1 is top
		// 2 is the side at min Z
		// 3 is the side at max Z
		// 4 is the side at min X 
		// 5 is the side at max X

		if (side == 0 && this.minY > 0.0D) {
			// Bottom side must always be renderd if bb bottom is not at 0
			return true;
		} else if (side == 1 && this.maxY < 1.0D) {
			// Top side must always be rendered if bb top is not at 1
			return true;
		} else if (side == 2 && this.minZ > 0.0D) {
			return true;
		} else if (side == 3 && this.maxZ < 1.0D) {
			return true;
		} else if (side == 4 && this.minX > 0.0D) {
			return true;
		} else if (side == 5 && this.maxX < 1.0D) {
			return true;
		} else {
			return !world.isBlockOpaqueCube(x, y, z);
		}
	}

	public boolean isBlockSolid(IBlockAccess iBlockAccess1, int i2, int i3, int i4, int i5) {
		return iBlockAccess1.getBlockMaterial(i2, i3, i4).isSolid();
	}

	public int getBlockTexture(IBlockAccess iBlockAccess1, int i2, int i3, int i4, int i5) {
		return this.getBlockTextureFromSideAndMetadata(i5, iBlockAccess1.getBlockMetadata(i2, i3, i4));
	}

	public int getBlockTextureFromSideAndMetadata(int i1, int i2) {
		return this.getBlockTextureFromSide(i1);
	}

	public int getBlockTextureFromSide(int i1) {
		return this.blockIndexInTexture;
	}

	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world1, int i2, int i3, int i4) {
		return AxisAlignedBB.getBoundingBoxFromPool((double)i2 + this.minX, (double)i3 + this.minY, (double)i4 + this.minZ, (double)i2 + this.maxX, (double)i3 + this.maxY, (double)i4 + this.maxZ);
	}

	public void getCollidingBoundingBoxes(World world, int x, int y, int z, AxisAlignedBB bb, ArrayList<AxisAlignedBB> bbArray, Entity entity) {
		this.getCollidingBoundingBoxes(world, x, y, z, bb, bbArray);
	}
	
	public void getCollidingBoundingBoxes(World world, int x, int y, int z, AxisAlignedBB bb, ArrayList<AxisAlignedBB> bbArray) {
		AxisAlignedBB axisAlignedBB7 = this.getCollisionBoundingBoxFromPool(world, x, y, z);
		if(axisAlignedBB7 != null && bb.intersectsWith(axisAlignedBB7)) {
			bbArray.add(axisAlignedBB7);
		}

	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world1, int i2, int i3, int i4) {
		return AxisAlignedBB.getBoundingBoxFromPool((double)i2 + this.minX, (double)i3 + this.minY, (double)i4 + this.minZ, (double)i2 + this.maxX, (double)i3 + this.maxY, (double)i4 + this.maxZ);
	}

	public boolean isOpaqueCube() {
		return true;
	}

	public boolean canCollideCheck(int i1, boolean z2) {
		return this.isCollidable();
	}

	public boolean isCollidable() {
		return true;
	}

	public void updateTick(World world1, int i2, int i3, int i4, Random random5) {
	}

	public void randomDisplayTick(World world1, int i2, int i3, int i4, Random random5) {
	}

	public void onBlockDestroyedByPlayer(World world1, int i2, int i3, int i4, int i5) {
	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
	}

	public int tickRate() {
		return 10;
	}

	public void onBlockAdded(World world1, int i2, int i3, int i4) {
	}

	public void onBlockRemoval(World world1, int i2, int i3, int i4) {
	}
	
	public void onBlockRemoval(World world1, int i2, int i3, int i4, int blockID, int metadata) {
		this.onBlockRemoval(world1, i2, i3, i4);
	}

	public int quantityDropped(Random random1) {
		return 1;
	}
	
	public int quantityDropped(int meta, int fortune, Random random) {
		return this.quantityDropped(random);
	}

	public int idDropped(int i1, Random random2, int i3) {
		return this.blockID;
	}

	public float blockStrength(EntityPlayer entityPlayer1, int metadata) {
		float hardness = this.getHardness(metadata);
		return hardness < 0.0F ? 0.0F : (!entityPlayer1.canHarvestBlock(this, metadata) ? 1.0F / hardness / 100.0F : entityPlayer1.getCurrentPlayerStrVsBlock(this, metadata) / hardness / 30.0F);
	}

	public final void dropBlockAsItem(World world1, int i2, int i3, int i4, int i5, int i6) {
		this.dropBlockAsItemWithChance(world1, i2, i3, i4, i5, 1.0F, i6);
	}

	/*
	public void dropBlockAsItemWithChance(World world1, int i2, int i3, int i4, int i5, float f6, int i7) {
		if(!world1.isRemote) {
			int i8 = this.quantityDroppedWithBonus(i7, world1.rand);

			for(int i9 = 0; i9 < i8; ++i9) {
				if(world1.rand.nextFloat() <= f6) {
					int i10 = this.idDropped(i5, world1.rand, i7);
					if(i10 > 0) {
						this.dropBlockAsItem_do(world1, i2, i3, i4, new ItemStack(i10, 1, this.damageDropped(i5)));
					} 
				}
			}

		}
	}
	*/
	
	// Reimplement this logic the forge / 1.3.2 way
	/**
	 * Drops the block items with a specified chance of dropping the specified items
	 */
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance,
			int fortune) {
		if (!world.isRemote) {
			ArrayList<ItemStack> blocksDropped = this.getBlockDropped(world, x, y, z, meta, fortune);
			Iterator<ItemStack> iterator = blocksDropped.iterator();

			while (iterator.hasNext()) {
				ItemStack itemStack = iterator.next();

				if (world.rand.nextFloat() <= chance) {
					this.dropBlockAsItem_do(world, x, y, z, itemStack);
				}
			}
		}
	}
	
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> itemsToDrop = new ArrayList<ItemStack>();
		int quantityDropped = this.quantityDropped(meta, fortune, world.rand);

		for (int i = 0; i < quantityDropped; ++i) {
			int idDropped = this.idDropped(meta, world.rand, 0);

			if (idDropped > 0) {
				itemsToDrop.add(new ItemStack(idDropped, 1, this.damageDropped(meta)));
			}
		}

		return itemsToDrop;
	}

	protected void dropBlockAsItem_do(World world1, int i2, int i3, int i4, ItemStack itemStack5) {
		if(!world1.isRemote) {
			float f6 = 0.7F;
			double d7 = (double)(world1.rand.nextFloat() * f6) + (double)(1.0F - f6) * 0.5D;
			double d9 = (double)(world1.rand.nextFloat() * f6) + (double)(1.0F - f6) * 0.5D;
			double d11 = (double)(world1.rand.nextFloat() * f6) + (double)(1.0F - f6) * 0.5D;
			EntityItem entityItem13 = new EntityItem(world1, (double)i2 + d7, (double)i3 + d9, (double)i4 + d11, itemStack5);
			entityItem13.delayBeforeCanPickup = 10;
			world1.spawnEntityInWorld(entityItem13);
		}
	}
	
	protected void dropXpOnBlockBreak(World var1, int var2, int var3, int var4, int var5) {

	}

	public int damageDropped(int i1) {
		return 0;
	}

	public float getExplosionResistance(Entity entity1) {
		return this.blockResistance / 5.0F;
	}

	public MovingObjectPosition collisionRayTrace(World world1, int i2, int i3, int i4, Vec3D vec3D5, Vec3D vec3D6) {
		this.setBlockBoundsBasedOnState(world1, i2, i3, i4);
		vec3D5 = vec3D5.addVector((double)(-i2), (double)(-i3), (double)(-i4));
		vec3D6 = vec3D6.addVector((double)(-i2), (double)(-i3), (double)(-i4));
		Vec3D vec3D7 = vec3D5.getIntermediateWithXValue(vec3D6, this.minX);
		Vec3D vec3D8 = vec3D5.getIntermediateWithXValue(vec3D6, this.maxX);
		Vec3D vec3D9 = vec3D5.getIntermediateWithYValue(vec3D6, this.minY);
		Vec3D vec3D10 = vec3D5.getIntermediateWithYValue(vec3D6, this.maxY);
		Vec3D vec3D11 = vec3D5.getIntermediateWithZValue(vec3D6, this.minZ);
		Vec3D vec3D12 = vec3D5.getIntermediateWithZValue(vec3D6, this.maxZ);
		if(!this.isVecInsideYZBounds(vec3D7)) {
			vec3D7 = null;
		}

		if(!this.isVecInsideYZBounds(vec3D8)) {
			vec3D8 = null;
		}

		if(!this.isVecInsideXZBounds(vec3D9)) {
			vec3D9 = null;
		}

		if(!this.isVecInsideXZBounds(vec3D10)) {
			vec3D10 = null;
		}

		if(!this.isVecInsideXYBounds(vec3D11)) {
			vec3D11 = null;
		}

		if(!this.isVecInsideXYBounds(vec3D12)) {
			vec3D12 = null;
		}

		Vec3D vec3D13 = null;
		if(vec3D7 != null && (vec3D13 == null || vec3D5.distanceTo(vec3D7) < vec3D5.distanceTo(vec3D13))) {
			vec3D13 = vec3D7;
		}

		if(vec3D8 != null && (vec3D13 == null || vec3D5.distanceTo(vec3D8) < vec3D5.distanceTo(vec3D13))) {
			vec3D13 = vec3D8;
		}

		if(vec3D9 != null && (vec3D13 == null || vec3D5.distanceTo(vec3D9) < vec3D5.distanceTo(vec3D13))) {
			vec3D13 = vec3D9;
		}

		if(vec3D10 != null && (vec3D13 == null || vec3D5.distanceTo(vec3D10) < vec3D5.distanceTo(vec3D13))) {
			vec3D13 = vec3D10;
		}

		if(vec3D11 != null && (vec3D13 == null || vec3D5.distanceTo(vec3D11) < vec3D5.distanceTo(vec3D13))) {
			vec3D13 = vec3D11;
		}

		if(vec3D12 != null && (vec3D13 == null || vec3D5.distanceTo(vec3D12) < vec3D5.distanceTo(vec3D13))) {
			vec3D13 = vec3D12;
		}

		if(vec3D13 == null) {
			return null;
		} else {
			byte b14 = -1;
			if(vec3D13 == vec3D7) {
				b14 = 4;
			}

			if(vec3D13 == vec3D8) {
				b14 = 5;
			}

			if(vec3D13 == vec3D9) {
				b14 = 0;
			}

			if(vec3D13 == vec3D10) {
				b14 = 1;
			}

			if(vec3D13 == vec3D11) {
				b14 = 2;
			}

			if(vec3D13 == vec3D12) {
				b14 = 3;
			}

			return new MovingObjectPosition(i2, i3, i4, b14, vec3D13.addVector((double)i2, (double)i3, (double)i4));
		}
	}

	private boolean isVecInsideYZBounds(Vec3D vec3D1) {
		return vec3D1 == null ? false : vec3D1.yCoord >= this.minY && vec3D1.yCoord <= this.maxY && vec3D1.zCoord >= this.minZ && vec3D1.zCoord <= this.maxZ;
	}

	private boolean isVecInsideXZBounds(Vec3D vec3D1) {
		return vec3D1 == null ? false : vec3D1.xCoord >= this.minX && vec3D1.xCoord <= this.maxX && vec3D1.zCoord >= this.minZ && vec3D1.zCoord <= this.maxZ;
	}

	private boolean isVecInsideXYBounds(Vec3D vec3D1) {
		return vec3D1 == null ? false : vec3D1.xCoord >= this.minX && vec3D1.xCoord <= this.maxX && vec3D1.yCoord >= this.minY && vec3D1.yCoord <= this.maxY;
	}

	public void onBlockDestroyedByExplosion(World world1, int i2, int i3, int i4) {
	}

	public int getRenderBlockPass() {
		return 0;
	}

	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side, ItemStack itemStack) {
		return this.canPlaceBlockOnSide(world, x, y, z, side);
	}
	
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
		return this.canPlaceBlockAt(world, x, y, z);
	}

	public boolean canPlaceBlockAt(World world1, int i2, int i3, int i4) {
		int i5 = world1.getBlockId(i2, i3, i4);
		return i5 == 0 || blocksList[i5].blockMaterial.isGroundCover();
	}

	public boolean blockActivated(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5, int side, float xWithinFace, float yWithinFace, float zWithinFace) {
		return this.blockActivated(world1, i2, i3, i4, entityPlayer5);
	}
	
	public boolean blockActivated(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
		return false;
	}

	public void onEntityWalking(World world1, int i2, int i3, int i4, Entity entity5) {
	}

	public void onBlockPlaced(World world, int x, int y, int z, int face) {
	}
	
	public void onBlockPlaced(World world, int x, int y, int z, int face, float xWithinFace, float yWithinFace, float zWithinFace, boolean keyPressed) {
		this.onBlockPlaced (world, x, y, z, face);
	}

	public void onBlockClicked(World world1, int i2, int i3, int i4, EntityPlayer entityPlayer5) {
	}

	public void velocityToAddToEntity(World world1, int i2, int i3, int i4, Entity entity5, Vec3D vec3D6) {
	}

	public void setBlockBoundsBasedOnState(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
	}

	public int getBlockColor() {
		return 0xFFFFFF;
	}

	public int getRenderColor(int i1) {
		return 0xFFFFFF;
	}

	public int colorMultiplier(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		return 0xFFFFFF;
	}

	public boolean isPoweringTo(IBlockAccess iBlockAccess1, int i2, int i3, int i4, int i5) {
		return false;
	}

	public boolean canProvidePower() {
		return false;
	}

	public void onEntityCollidedWithBlock(World world1, int i2, int i3, int i4, Entity entity5) {
	}

	public boolean isIndirectlyPoweringTo(World world1, int i2, int i3, int i4, int i5) {
		return false;
	}

	public void setBlockBoundsForItemRender() {
	}

	public void harvestBlock(World world, EntityPlayer entityPlayer, int x, int y, int z, int meta) {
		entityPlayer.addExhaustion(0.025F);
		Item inHand = null;
		ItemStack inHandIS = entityPlayer.inventory.getStackInSlot(entityPlayer.inventory.currentItem);
		if(inHandIS != null) inHand = inHandIS.getItem();
		
		if(
				this.canBeSilkTouched() && 
				inHand != null && (inHand instanceof ItemTool) && 
				inHand.canHarvestBlock(this) && 
				((ItemTool)inHand).toolMaterial == EnumToolMaterial.GOLD
		) {
			ItemStack newStack = this.createStackedBlock(meta);
			if(newStack != null) {
				this.dropBlockAsItem_do(world, x, y, z, newStack);
			}
		} else {
			int i7 = 0; // TODO: add fortune
			this.dropBlockAsItem(world, x, y, z, meta, i7);
		}

	}

	protected boolean canBeSilkTouched() {
		return this.renderAsNormalBlock() && !this.isBlockContainer;
	}

	protected ItemStack createStackedBlock(int i1) {
		int i2 = 0;
		if(this.blockID >= 0 && this.blockID < Item.itemsList.length && Item.itemsList[this.blockID].getHasSubtypes()) {
			i2 = i1;
		}

		return new ItemStack(this.blockID, 1, i2);
	}

	public int quantityDroppedWithBonus(int i1, Random random2) {
		return this.quantityDropped(random2);
	}

	public boolean canBlockStay(World world1, int i2, int i3, int i4) {
		return true;
	}

	public void onBlockPlacedBy(World world1, int i2, int i3, int i4, EntityLiving entityLiving5) {
	}

	public Block setBlockName(String string1) {
		this.blockName = "tile." + string1;
		return this;
	}

	public String translateBlockName() {
		return Translator.translateToLocal(this.getBlockName() + ".name");
	}

	public String getBlockName() {
		return this.blockName;
	}

	public void powerBlock(World world1, int i2, int i3, int i4, int i5, int i6) {
	}

	public boolean getEnableStats() {
		return this.enableStats;
	}

	protected Block disableStats() {
		this.enableStats = false;
		return this;
	}

	public int getMobilityFlag() {
		return this.blockMaterial.getMaterialMobility();
	}

	public float getAmbientOcclusionLightValue(IBlockAccess iBlockAccess1, int i2, int i3, int i4) {
		return iBlockAccess1.isBlockNormalCube(i2, i3, i4) ? 0.2F : 1.0F;
	}

	public void onFallenUpon(World world1, int i2, int i3, int i4, Entity entity5, float f6) {
	}
	
	public String getTextureFile() {
		return this.currentTexture;
	}

	public void setTextureFile(String texture) {
		this.currentTexture = texture;
		this.isDefaultTexture = false;
	}
	
	// New stuff
	
	// This is to fix wooden slabs being Material.rock!
	public Material getBlockMaterialBasedOnmetaData(int meta) {
		return this.blockMaterial;
	}

	static {
		Item.itemsList[cloth.blockID] = (new ItemDyeable(cloth.blockID - 256)).setItemName("cloth");
		Item.itemsList[wood.blockID] = (new ItemMetadata(wood.blockID - 256, wood)).setItemName("log");
		Item.itemsList[planks.blockID] = (new ItemMetadata(planks.blockID - 256, planks)).setItemName("wood");
		Item.itemsList[sandStone.blockID] = (new ItemMetadata(sandStone.blockID - 256, sandStone)).setItemName("sandStone");
		Item.itemsList[stairSingle.blockID] = (new ItemSlab(stairSingle.blockID - 256)).setItemName("stoneSlab");
		Item.itemsList[sapling.blockID] = (new ItemSapling(sapling.blockID - 256)).setItemName("sapling");
		Item.itemsList[leaves.blockID] = (new ItemLeaves(leaves.blockID - 256)).setItemName("leaves");
		Item.itemsList[vine.blockID] = new ItemColored(vine.blockID - 256, false);
		Item.itemsList[tallGrass.blockID] = (new ItemColored(tallGrass.blockID - 256, true)).setBlockNames(new String[]{"shrub", "grass", "fern"});
		
		for(int i0 = 0; i0 < 4096; ++i0) {
			if(blocksList[i0] != null) {
				if(Item.itemsList[i0] == null) {
					Item.itemsList[i0] = new ItemBlock(i0 - 256);
					blocksList[i0].initializeBlock();
				} else if(!(Item.itemsList[i0] instanceof ItemBlock)) {
					throw new Error("Item id " + (i0 - 256) + "(shifted index " + i0 + ") is overlapping with block id " + i0);
				}

				boolean z1 = false;
				if(i0 > 0 && blocksList[i0].getRenderType() == 10) {
					z1 = true;
				}

				if(i0 > 0 && blocksList[i0] instanceof BlockStep) {
					z1 = true;
				}

				if(i0 == tilledField.blockID) {
					z1 = true;
				}

				if(canBlockGrass[i0]) {
					z1 = true;
				}

				useNeighborBrightness[i0] = z1;
			}
		}

		canBlockGrass[0] = true;
	}

	public boolean canGrowCacti() {
		return false;
	}
	
	public boolean canGrowPlants() {
		return false;
	}
	
	public boolean supportsRedstone(int meta) {
		return this.isBlockNormalCube(meta);
	}
	
	public boolean supportsTorch(int meta) {
		return (this.blockMaterial.isOpaque() && this.renderAsNormalBlock());
	}
	
	public boolean canPlaceTorchBy(int meta, int side) {
		return (this.blockMaterial.isOpaque() && this.renderAsNormalBlock());
	}
	
	public boolean canGrowMushrooms() {
		return Block.opaqueCubeLookup[this.blockID];
	}
	
	public boolean isLadder() {
		return false;
	}
	
	public int getLightValue(int meta) {
		return Block.lightValue[this.blockID];
	}

	/**
	 * Called when the block is attempted to be harvested
	 */
	public void onBlockHarvested(World par1World, int par2, int par3, int par4, int par5,
			EntityPlayer par6EntityPlayer) {
		// TODO Auto-generated method stub
		
	}

}
