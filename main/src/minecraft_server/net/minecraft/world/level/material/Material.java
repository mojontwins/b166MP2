package net.minecraft.world.level.material;

public class Material {
	
	public static final Material air = new MaterialTransparent().setName("air");
	public static final Material grass = new Material().setName("grass");
	public static final Material ground = new Material().setName("ground");
	public static final Material wood = (new Material()).setBurning().setName("wood");
	public static final Material rock = (new Material()).setNoHarvest().setName("rock");
	public static final Material iron = (new Material()).setNoHarvest().setName("iron");
	public static final Material water = (new MaterialLiquid()).setNoPushMobility().setName("water");
	public static final Material lava = (new MaterialLiquid()).setNoPushMobility().setName("lava");
	public static final Material leaves = (new Material()).setBurning().setTranslucent().setNoPushMobility().setName("leaves");
	public static final Material plants = (new MaterialLogic()).setNoPushMobility().setName("plants").setGroundCover();
	public static final Material vine = (new MaterialLogic()).setBurning().setNoPushMobility().setGroundCover().setName("vine");
	public static final Material sponge = new Material().setName("sponge");
	public static final Material cloth = (new Material()).setBurning().setName("cloth");
	public static final Material fire = (new MaterialTransparent()).setNoPushMobility().setName("fire");
	public static final Material sand = new Material().setName("sand");
	public static final Material circuits = (new MaterialLogic()).setNoPushMobility().setName("circuits");
	public static final Material glass = (new Material()).setTranslucent().setName("glass");
	public static final Material redstoneLight = new Material().setName("redstone");
	public static final Material tnt = (new Material()).setBurning().setTranslucent().setName("tnt");
	public static final Material unused = (new Material()).setNoPushMobility().setName("unused");
	public static final Material ice = (new Material()).setTranslucent().setName("ice");
	public static final Material snow = (new MaterialLogic()).setGroundCover().setTranslucent().setNoHarvest().setNoPushMobility().setName("snow");
	public static final Material craftedSnow = (new Material()).setNoHarvest().setName("craftedSnow");
	public static final Material cactus = (new Material()).setTranslucent().setNoPushMobility().setName("cactus");
	public static final Material clay = new Material().setName("clay");
	public static final Material pumpkin = (new Material()).setNoPushMobility().setName("pumpkin");
	public static final Material dragonEgg = (new Material()).setNoPushMobility().setName("dragon");
	public static final Material portal = (new MaterialPortal()).setImmovableMobility().setName("portal");
	public static final Material cake = (new Material()).setNoPushMobility().setName("cake");
	public static final Material web = (new MaterialWeb()).setNoHarvest().setNoPushMobility().setName("web");
	public static final Material piston = (new Material()).setImmovableMobility().setName("piston");
	private boolean canBurn;
	private boolean groundCover;
	private boolean isTranslucent;
	private boolean canHarvest = true;
	private int mobilityFlag;
	private String name = "default";

	public Material() {
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
