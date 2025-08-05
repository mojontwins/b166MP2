package net.minecraft.world.level.material;

import net.minecraft.world.item.map.MapColor;

public class Material {
	
	public static final Material air = new MaterialTransparent(MapColor.airColor).setName("air");
	public static final Material grass = new Material(MapColor.grassColor).setName("grass");
	public static final Material ground = new Material(MapColor.dirtColor).setName("ground");
	public static final Material wood = (new Material(MapColor.woodColor)).setBurning().setName("wood");
	public static final Material rock = (new Material(MapColor.stoneColor)).setNoHarvest().setName("rock");
	public static final Material iron = (new Material(MapColor.ironColor)).setNoHarvest().setName("iron");
	public static final Material water = (new MaterialLiquid(MapColor.waterColor)).setNoPushMobility().setName("water");
	public static final Material lava = (new MaterialLiquid(MapColor.tntColor)).setNoPushMobility().setName("lava");
	public static final Material leaves = (new Material(MapColor.foliageColor)).setBurning().setTranslucent().setNoPushMobility().setName("leaves");
	public static final Material plants = (new MaterialLogic(MapColor.foliageColor)).setNoPushMobility().setName("plants").setGroundCover();
	public static final Material vine = (new MaterialLogic(MapColor.foliageColor)).setBurning().setNoPushMobility().setGroundCover().setName("vine");
	public static final Material sponge = new Material(MapColor.clothColor).setName("sponge");
	public static final Material cloth = (new Material(MapColor.clothColor)).setBurning().setName("cloth");
	public static final Material fire = (new MaterialTransparent(MapColor.airColor)).setNoPushMobility().setName("fire");
	public static final Material sand = new Material(MapColor.sandColor).setName("sand");
	public static final Material circuits = (new MaterialLogic(MapColor.airColor)).setNoPushMobility().setName("circuits");
	public static final Material glass = (new Material(MapColor.airColor)).setTranslucent().setName("glass");
	public static final Material redstoneLight = new Material(MapColor.airColor).setName("redstone");
	public static final Material tnt = (new Material(MapColor.tntColor)).setBurning().setTranslucent().setName("tnt");
	public static final Material unused = (new Material(MapColor.foliageColor)).setNoPushMobility().setName("unused");
	public static final Material ice = (new Material(MapColor.iceColor)).setTranslucent().setName("ice");
	public static final Material snow = (new MaterialLogic(MapColor.snowColor)).setGroundCover().setTranslucent().setNoHarvest().setNoPushMobility().setName("snow");
	public static final Material craftedSnow = (new Material(MapColor.snowColor)).setNoHarvest().setName("craftedSnow");
	public static final Material cactus = (new Material(MapColor.foliageColor)).setTranslucent().setNoPushMobility().setName("cactus");
	public static final Material clay = new Material(MapColor.clayColor).setName("clay");
	public static final Material pumpkin = (new Material(MapColor.foliageColor)).setNoPushMobility().setName("pumpkin");
	public static final Material dragonEgg = (new Material(MapColor.foliageColor)).setNoPushMobility().setName("dragon");
	public static final Material portal = (new MaterialPortal(MapColor.airColor)).setImmovableMobility().setName("portal");
	public static final Material cake = (new Material(MapColor.airColor)).setNoPushMobility().setName("cake");
	public static final Material web = (new MaterialWeb(MapColor.clothColor)).setNoHarvest().setNoPushMobility().setName("web");
	public static final Material piston = (new Material(MapColor.stoneColor)).setImmovableMobility().setName("piston");
	private boolean canBurn;
	private boolean groundCover;
	private boolean isTranslucent;
	public final MapColor materialMapColor;
	private boolean canHarvest = true;
	private int mobilityFlag;
	private String name = "default";

	public Material(MapColor mapColor) {
		this.materialMapColor = mapColor;
	}

	public boolean isLiquid() {
		return false;
	}

	public boolean isSolid() {
		return true;
	}

	public boolean getCanBlockGrass() {
		return true;
	}

	public boolean blocksMovement() {
		return true;
	}

	private Material setTranslucent() {
		this.isTranslucent = true;
		return this;
	}

	protected Material setNoHarvest() {
		this.canHarvest = false;
		return this;
	}

	protected Material setBurning() {
		this.canBurn = true;
		return this;
	}

	public boolean getCanBurn() {
		return this.canBurn;
	}

	public Material setGroundCover() {
		this.groundCover = true;
		return this;
	}

	public boolean isGroundCover() {
		return this.groundCover;
	}

	public boolean isOpaque() {
		return this.isTranslucent ? false : this.blocksMovement();
	}

	public boolean isHarvestable() {
		return this.canHarvest;
	}

	public int getMaterialMobility() {
		return this.mobilityFlag;
	}

	protected Material setNoPushMobility() {
		this.mobilityFlag = 1;
		return this;
	}

	protected Material setImmovableMobility() {
		this.mobilityFlag = 2;
		return this;
	}

	public String getName() {
		return name;
	}

	public Material setName(String name) {
		this.name = name;
		return this;
	}
}
