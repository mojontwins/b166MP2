package net.minecraft.src;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.SimpleButtonModel;

import java.util.Map;

public class mod_mocreatures extends BaseMod {
	public static String modName = "DrZhark\'s Mo\'Creatures Mod";
	public static Item horsesaddle = (new HorseSaddle(3772)).setIconIndex(ModLoader.addOverride("/gui/items.png", "/mob/horsesaddle.png")).setItemName("HorseSaddle");
	public static Item haystack = (new HayStack(3775)).setIconIndex(ModLoader.addOverride("/gui/items.png", "/mob/haystack.png")).setItemName("HayStack");
	public static Item sugarlump = (new SugarLump(3776)).setIconIndex(ModLoader.addOverride("/gui/items.png", "/mob/sugarlump.png")).setItemName("SugarLump");
	public static Item sharkteeth = (new Item(3774)).setIconIndex(ModLoader.addOverride("/gui/items.png", "/mob/sharkteeth.png")).setItemName("sharkteeth");
	public static Item sharkegg = (new ItemSharkEgg(3773)).setIconIndex(ModLoader.addOverride("/gui/items.png", "/mob/sharkegg.png")).setItemName("sharkegg");
	public static Item fishyegg = (new ItemFishyEgg(3777)).setIconIndex(ModLoader.addOverride("/gui/items.png", "/mob/fishyegg.png")).setItemName("fishyegg");
	public static Item bigcatclaw = (new Item(3778)).setIconIndex(ModLoader.addOverride("/gui/items.png", "/mob/bigcatclaw.png")).setItemName("bigcatclaw");
	public ModSettings MoCSettings;
	public ModSettingScreen MoCScreen;
	public static SettingInt maxMobsS;
	public static WidgetInt maxMobsW;
	public static SettingInt maxAnimalsS;
	public static WidgetInt maxAnimalsW;
	public static SettingInt maxWaterMobsS;
	public static WidgetInt maxWaterMobsW;
	public static SettingBoolean spawnpiranha;
	public static SettingInt horsefreq;
	public static SettingInt birdfreq;
	public static SettingInt lionfreq;
	public static SettingInt bearfreq;
	public static SettingInt pbearfreq;
	public static SettingInt wwolffreq;
	public static SettingInt duckfreq;
	public static SettingInt boarfreq;
	public static SettingInt bunnyfreq;
	public static SettingInt wraithfreq;
	public static SettingInt fwraithfreq;
	public static SettingInt ogrefreq;
	public static SettingInt cogrefreq;
	public static SettingInt fogrefreq;
	public static SettingInt werewolffreq;
	public static SettingInt foxfreq;
	public static SettingInt sharkfreq;
	public static SettingInt squidfreq;
	public static SettingInt dolphinfreq;
	public static SettingInt fishfreq;
	public static SettingInt deerfreq;
	public static WidgetInt horsefreqW;
	public static WidgetInt birdfreqW;
	public static WidgetInt lionfreqW;
	public static WidgetInt bearfreqW;
	public static WidgetInt pbearfreqW;
	public static WidgetInt wwolffreqW;
	public static WidgetInt duckfreqW;
	public static WidgetInt boarfreqW;
	public static WidgetInt bunnyfreqW;
	public static WidgetInt wraithfreqW;
	public static WidgetInt fwraithfreqW;
	public static WidgetInt ogrefreqW;
	public static WidgetInt cogrefreqW;
	public static WidgetInt fogrefreqW;
	public static WidgetInt werewolffreqW;
	public static WidgetInt foxfreqW;
	public static WidgetInt sharkfreqW;
	public static WidgetInt squidfreqW;
	public static WidgetInt dolphinfreqW;
	public static WidgetInt fishfreqW;
	public static WidgetInt deerfreqW;
	public static SettingBoolean attackdolphins;
	public static WidgetBoolean attackdolphinsW;
	public static SettingBoolean attackhorses;
	public static WidgetBoolean attackhorsesW;
	public static SettingBoolean attackwolves;
	public static WidgetBoolean attackwolvesW;
	public static SettingBoolean destroyitems;
	public static WidgetBoolean destroyitemsW;
	public static WidgetBoolean spawnpiranhaW;
	public static SettingInt pegasusChanceS;
	public static WidgetInt pegasusChanceW;
	public static SettingBoolean easybreeding;
	public static WidgetBoolean easybreedingW;
	public static SettingFloat ogreStrength;
	public static SettingFloat fogreStrength;
	public static SettingFloat cogreStrength;
	public static WidgetFloat ogreStrengthW;
	public static WidgetFloat fogreStrengthW;
	public static WidgetFloat cogreStrengthW;
	public static SettingMulti ogreSpawnDifficulty;
	public static SettingMulti cogreSpawnDifficulty;
	public static SettingMulti fogreSpawnDifficulty;
	public static WidgetMulti ogreSpawnDifficultyW;
	public static WidgetMulti cogreSpawnDifficultyW;
	public static WidgetMulti fogreSpawnDifficultyW;
	public static SettingMulti wereSpawnDifficulty;
	public static SettingMulti wraithSpawnDifficulty;
	public static SettingMulti fwraithSpawnDifficulty;
	public static WidgetMulti wereSpawnDifficultyW;
	public static WidgetMulti wraithSpawnDifficultyW;
	public static WidgetMulti fwraithSpawnDifficultyW;
	public static SettingMulti sharkSpawnDifficulty;
	public static WidgetMulti sharkSpawnDifficultyW;
	public WidgetSimplewindow spawnwindow;
	public WidgetSimplewindow animalwindow;
	public WidgetSimplewindow hunterwindow;
	public WidgetSimplewindow mobwindow;
	public WidgetSimplewindow watermobwindow;

	public mod_mocreatures() {
		ModLoader.AddName(horsesaddle, "Horse Saddle");
		ModLoader.AddName(sharkteeth, "Shark Teeth");
		ModLoader.AddName(sharkegg, "Shark Egg");
		ModLoader.AddName(haystack, "Hay Stack");
		ModLoader.AddName(sugarlump, "Sugar Lump");
		ModLoader.AddName(fishyegg, "Fish Egg");
		ModLoader.AddName(bigcatclaw, "BigCat Claw");
		ModLoader.RegisterEntityID(EntityHorse.class, "Horse", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityOgre.class, "Ogre", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityFireOgre.class, "FireOgre", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityCaveOgre.class, "CaveOgre", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityBoar.class, "Boar", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityBear.class, "Bear", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityDuck.class, "Duck", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityBigCat.class, "BigCat", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityDeer.class, "Deer", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityWWolf.class, "WildWolf", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityPolarBear.class, "PolarBear", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityWraith.class, "Wraith", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityFlameWraith.class, "FlameWraith", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityBunny.class, "Bunny", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityBird.class, "Bird", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityFox.class, "Fox", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityWerewolf.class, "Werewolf", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityShark.class, "Shark", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntitySharkEgg.class, "SharkEgg", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityDolphin.class, "Dolphin", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityFishyEgg.class, "FishyEgg", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityFishy.class, "Fishy", ModLoader.getUniqueEntityId());
		ModLoader.AddRecipe(new ItemStack(horsesaddle, 1), new Object[]{"XXX", "X#X", "# #", '#', Item.ingotIron, 'X', Item.leather});
		ModLoader.AddRecipe(new ItemStack(haystack, 1), new Object[]{"XXX", "XXX", 'X', Item.wheat});
		ModLoader.AddRecipe(new ItemStack(Item.wheat, 6), new Object[]{"X", 'X', haystack});
		ModLoader.AddRecipe(new ItemStack(sugarlump, 1), new Object[]{"XX", "##", 'X', Item.sugar, '#', Item.sugar});
		ModLoader.AddRecipe(new ItemStack(horsesaddle, 1), new Object[]{"X", "#", 'X', Item.saddle, '#', Item.ingotIron});
		ModLoader.AddRecipe(new ItemStack(Item.plateChain, 1), new Object[]{"X X", "XXX", "XXX", 'X', sharkteeth});
		ModLoader.AddRecipe(new ItemStack(Item.helmetChain, 1), new Object[]{"XXX", "X X", 'X', sharkteeth});
		ModLoader.AddRecipe(new ItemStack(Item.legsChain, 1), new Object[]{"XXX", "X X", "X X", 'X', sharkteeth});
		ModLoader.AddRecipe(new ItemStack(Item.bootsChain, 1), new Object[]{"X X", "X X", 'X', sharkteeth});
		this.MoCSettings = new ModSettings("mocreatures");
		this.MoCScreen = new ModSettingScreen("DrZhark\'s Mo\'Creatures");
		SimpleButtonModel simpleButtonModel1 = new SimpleButtonModel();
		simpleButtonModel1.addActionCallback(new ModAction(this, "spawnlimits", new Class[0]));
		Button button2 = new Button(simpleButtonModel1);
		button2.setText("Spawn Limits");
		this.MoCScreen.append(button2);
		this.MoCSettings.append(maxMobsS = new SettingInt("MobsSpawnLimit", 70, 1, 1, 1000));
		maxMobsW = new WidgetInt(maxMobsS, "Hostiles");
		this.MoCSettings.append(maxAnimalsS = new SettingInt("AnimalsSpawnLimit", 30, 1, 1, 1000));
		maxAnimalsW = new WidgetInt(maxAnimalsS, "Animals");
		this.MoCSettings.append(maxWaterMobsS = new SettingInt("WaterMobSpawnLimit", 30, 1, 1, 1000));
		maxWaterMobsW = new WidgetInt(maxWaterMobsS, "Water Mobs");
		SimpleButtonModel simpleButtonModel3 = new SimpleButtonModel();
		simpleButtonModel3.addActionCallback(new ModAction(this, "animalsettings", new Class[0]));
		Button button4 = new Button(simpleButtonModel3);
		button4.setText("Animals");
		this.MoCScreen.append(button4);
		this.MoCSettings.append(horsefreq = new SettingInt("FreqHorse", 8, 0, 1, 10));
		horsefreqW = new WidgetInt(horsefreq, "Horse Freq");
		this.MoCSettings.append(easybreeding = new SettingBoolean("EasyHorseBreeding", false));
		easybreedingW = new WidgetBoolean(easybreeding, "Easy Horse Breed", "Yes", "No");
		this.MoCSettings.append(pegasusChanceS = new SettingInt("PegasusSpawningP", 1, 1, 1, 3));
		pegasusChanceW = new WidgetInt(pegasusChanceS, "Pegasus chance");
		this.MoCSettings.append(birdfreq = new SettingInt("FreqBird", 6, 0, 1, 10));
		birdfreqW = new WidgetInt(birdfreq, "Bird Freq");
		this.MoCSettings.append(bunnyfreq = new SettingInt("FreqBunny", 10, 0, 1, 10));
		bunnyfreqW = new WidgetInt(bunnyfreq, "Bunny Freq");
		this.MoCSettings.append(duckfreq = new SettingInt("FreqDuck", 7, 0, 1, 10));
		duckfreqW = new WidgetInt(duckfreq, "Duck Freq");
		this.MoCSettings.append(deerfreq = new SettingInt("FreqDeer", 8, 0, 1, 10));
		deerfreqW = new WidgetInt(deerfreq, "Deer Freq");
		SimpleButtonModel simpleButtonModel5 = new SimpleButtonModel();
		simpleButtonModel5.addActionCallback(new ModAction(this, "huntersettings", new Class[0]));
		Button button6 = new Button(simpleButtonModel5);
		button6.setText("Hunter Creatures");
		this.MoCScreen.append(button6);
		this.MoCSettings.append(attackhorses = new SettingBoolean("HuntersAttackHorses", false));
		attackhorsesW = new WidgetBoolean(attackhorses, "Target horses?", "Yes", "No");
		this.MoCSettings.append(attackwolves = new SettingBoolean("HuntersAttackWolves", false));
		attackwolvesW = new WidgetBoolean(attackwolves, "Target dogs?", "Yes", "No");
		this.MoCSettings.append(destroyitems = new SettingBoolean("HuntersDestroyDrops", true));
		destroyitemsW = new WidgetBoolean(destroyitems, "Destroy drops?", "Yes", "No");
		this.MoCSettings.append(lionfreq = new SettingInt("FreqLion", 6, 0, 1, 10));
		lionfreqW = new WidgetInt(lionfreq, "BigCat Freq");
		this.MoCSettings.append(bearfreq = new SettingInt("FreqBear", 2, 0, 1, 10));
		bearfreqW = new WidgetInt(bearfreq, "Bear Freq");
		this.MoCSettings.append(pbearfreq = new SettingInt("FreqPBear", 2, 0, 1, 10));
		pbearfreqW = new WidgetInt(pbearfreq, "PBear Freq");
		this.MoCSettings.append(boarfreq = new SettingInt("FreqBoar", 3, 0, 1, 10));
		boarfreqW = new WidgetInt(boarfreq, "Boar Freq");
		this.MoCSettings.append(foxfreq = new SettingInt("FreqFox", 5, 0, 1, 10));
		foxfreqW = new WidgetInt(foxfreq, "Fox Freq");
		SimpleButtonModel simpleButtonModel7 = new SimpleButtonModel();
		simpleButtonModel7.addActionCallback(new ModAction(this, "mobsettings", new Class[0]));
		Button button8 = new Button(simpleButtonModel7);
		button8.setText("Hostile Mobs");
		this.MoCScreen.append(button8);
		this.MoCSettings.append(ogrefreq = new SettingInt("FreqOgre", 6, 0, 1, 10));
		ogrefreqW = new WidgetInt(ogrefreq, "Ogre Freq");
		this.MoCSettings.append(ogreSpawnDifficulty = new SettingMulti("ogreSpawnDifficulty", 1, new String[]{"Easy", "Normal", "Hard"}));
		ogreSpawnDifficultyW = new WidgetMulti(ogreSpawnDifficulty, "Spawn Ogres in");
		this.MoCSettings.append(ogreStrength = new SettingFloat("OgreStrength", 2.5F, 0.1F, 0.1F, 5.0F));
		ogreStrengthW = new WidgetFloat(ogreStrength, "Ogre Strength");
		this.MoCSettings.append(fogrefreq = new SettingInt("FreqFOgre", 2, 0, 1, 10));
		fogrefreqW = new WidgetInt(fogrefreq, "F.Ogre Freq");
		this.MoCSettings.append(fogreSpawnDifficulty = new SettingMulti("FireOgreSpawnDifficulty", 2, new String[]{"Easy", "Normal", "Hard"}));
		fogreSpawnDifficultyW = new WidgetMulti(fogreSpawnDifficulty, "Spawn Fire O. in");
		this.MoCSettings.append(fogreStrength = new SettingFloat("FireOgreStrength", 2.0F, 0.1F, 0.1F, 5.0F));
		fogreStrengthW = new WidgetFloat(fogreStrength, "Fire O. Strength");
		this.MoCSettings.append(cogrefreq = new SettingInt("FreqCOgre", 3, 0, 1, 10));
		cogrefreqW = new WidgetInt(cogrefreq, "C.Ogre Freq");
		this.MoCSettings.append(cogreSpawnDifficulty = new SettingMulti("CaveOgreSpawnDifficulty", 1, new String[]{"Easy", "Normal", "Hard"}));
		cogreSpawnDifficultyW = new WidgetMulti(cogreSpawnDifficulty, "Spawn Cave O. in");
		this.MoCSettings.append(cogreStrength = new SettingFloat("CaveOgreStrength", 3.0F, 0.1F, 0.1F, 5.0F));
		cogreStrengthW = new WidgetFloat(cogreStrength, "Cave O. Strength");
		this.MoCSettings.append(werewolffreq = new SettingInt("FreqWereWolf", 6, 0, 1, 10));
		werewolffreqW = new WidgetInt(werewolffreq, "WereWolf Freq");
		this.MoCSettings.append(wereSpawnDifficulty = new SettingMulti("wereSpawnDifficulty", 1, new String[]{"Easy", "Normal", "Hard"}));
		wereSpawnDifficultyW = new WidgetMulti(wereSpawnDifficulty, "Spawn Werew. in");
		this.MoCSettings.append(wraithfreq = new SettingInt("FreqWraith", 5, 0, 1, 10));
		wraithfreqW = new WidgetInt(wraithfreq, "Wraith Freq");
		this.MoCSettings.append(wraithSpawnDifficulty = new SettingMulti("wraithSpawnDifficulty", 1, new String[]{"Easy", "Normal", "Hard"}));
		wraithSpawnDifficultyW = new WidgetMulti(wraithSpawnDifficulty, "Spawn Wraiths in");
		this.MoCSettings.append(fwraithfreq = new SettingInt("FreqFWraith", 2, 0, 1, 10));
		fwraithfreqW = new WidgetInt(fwraithfreq, "F.Wraith Freq");
		this.MoCSettings.append(fwraithSpawnDifficulty = new SettingMulti("flameWraithSpawnDifficulty", 2, new String[]{"Easy", "Normal", "Hard"}));
		fwraithSpawnDifficultyW = new WidgetMulti(fwraithSpawnDifficulty, "Spawn Flame W. in");
		this.MoCSettings.append(wwolffreq = new SettingInt("FreqWildWolf", 8, 0, 1, 10));
		wwolffreqW = new WidgetInt(wwolffreq, "WildWolf Freq");
		SimpleButtonModel simpleButtonModel9 = new SimpleButtonModel();
		simpleButtonModel9.addActionCallback(new ModAction(this, "watermobsettings", new Class[0]));
		Button button10 = new Button(simpleButtonModel9);
		button10.setText("Water Mobs");
		this.MoCScreen.append(button10);
		this.MoCSettings.append(sharkfreq = new SettingInt("FreqShark", 3, 0, 1, 10));
		sharkfreqW = new WidgetInt(sharkfreq, "Shark Freq");
		this.MoCSettings.append(sharkSpawnDifficulty = new SettingMulti("sharkSpawnDifficulty", 1, new String[]{"Easy", "Normal", "Hard"}));
		sharkSpawnDifficultyW = new WidgetMulti(sharkSpawnDifficulty, "Spawn Sharks");
		this.MoCSettings.append(squidfreq = new SettingInt("FreqSquid", 0, 0, 1, 3));
		squidfreqW = new WidgetInt(squidfreq, "Squid Freq");
		this.MoCSettings.append(dolphinfreq = new SettingInt("FreqDolphin", 6, 0, 1, 10));
		dolphinfreqW = new WidgetInt(dolphinfreq, "Dolphin Freq");
		this.MoCSettings.append(attackdolphins = new SettingBoolean("DolphinsAttackSharks", true));
		attackdolphinsW = new WidgetBoolean(attackdolphins, "Aggresive Dolphins?", "Yes", "No");
		this.MoCSettings.append(fishfreq = new SettingInt("FreqFishy", 10, 0, 1, 20));
		fishfreqW = new WidgetInt(fishfreq, "Fishy Freq");
		this.MoCSettings.append(spawnpiranha = new SettingBoolean("SpawnPiranhas", true));
		spawnpiranhaW = new WidgetBoolean(spawnpiranha, "Spawn Piranhas?", "Yes", "No");
		SimpleButtonModel simpleButtonModel11 = new SimpleButtonModel();
		simpleButtonModel11.addActionCallback(new ModAction(this.MoCSettings, "resetAll", new Class[0]));
		Button button12 = new Button(simpleButtonModel11);
		button12.setText("Reset to defaults");
		this.MoCScreen.append(button12);
		this.MoCSettings.load();

		try {
			ModLoader.setPrivateValue(EnumCreatureType.class, EnumCreatureType.monster, "e", maxMobsS.get());
			ModLoader.setPrivateValue(EnumCreatureType.class, EnumCreatureType.creature, "e", maxAnimalsS.get());
			ModLoader.setPrivateValue(EnumCreatureType.class, EnumCreatureType.waterCreature, "e", maxWaterMobsS.get());
		} catch (Exception exception14) {
		}

		if(((Integer)horsefreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityHorse.class, ((Integer)horsefreq.get()).intValue(), EnumCreatureType.creature);
		}

		if(((Integer)ogrefreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityOgre.class, ((Integer)ogrefreq.get()).intValue(), EnumCreatureType.monster);
		}

		if(((Integer)fogrefreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityFireOgre.class, ((Integer)fogrefreq.get()).intValue(), EnumCreatureType.monster, new BiomeGenBase[]{BiomeGenBase.hell});
			ModLoader.AddSpawn(EntityFireOgre.class, ((Integer)fogrefreq.get()).intValue(), EnumCreatureType.monster);
		}

		if(((Integer)cogrefreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityCaveOgre.class, ((Integer)cogrefreq.get()).intValue(), EnumCreatureType.monster);
		}

		if(((Integer)boarfreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityBoar.class, ((Integer)boarfreq.get()).intValue(), EnumCreatureType.creature);
		}

		if(((Integer)bearfreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityBear.class, ((Integer)bearfreq.get()).intValue(), EnumCreatureType.creature);
		}

		if(((Integer)duckfreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityDuck.class, ((Integer)duckfreq.get()).intValue(), EnumCreatureType.creature);
		}

		if(((Integer)lionfreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityBigCat.class, ((Integer)lionfreq.get()).intValue(), EnumCreatureType.creature);
		}

		if(((Integer)wwolffreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityWWolf.class, ((Integer)wwolffreq.get()).intValue(), EnumCreatureType.monster);
		}

		if(((Integer)pbearfreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityPolarBear.class, ((Integer)pbearfreq.get()).intValue(), EnumCreatureType.creature);
		}

		if(((Integer)wraithfreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityWraith.class, ((Integer)wraithfreq.get()).intValue(), EnumCreatureType.monster);
		}

		if(((Integer)fwraithfreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityFlameWraith.class, ((Integer)fwraithfreq.get()).intValue(), EnumCreatureType.monster, new BiomeGenBase[]{BiomeGenBase.hell});
			ModLoader.AddSpawn(EntityFlameWraith.class, ((Integer)fwraithfreq.get()).intValue(), EnumCreatureType.monster);
		}

		if(((Integer)bunnyfreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityBunny.class, ((Integer)bunnyfreq.get()).intValue(), EnumCreatureType.creature);
		}

		if(((Integer)birdfreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityBird.class, ((Integer)birdfreq.get()).intValue(), EnumCreatureType.creature);
		}

		if(((Integer)deerfreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityDeer.class, ((Integer)deerfreq.get()).intValue(), EnumCreatureType.creature);
		}

		if(((Integer)foxfreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityFox.class, ((Integer)foxfreq.get()).intValue(), EnumCreatureType.creature);
		}

		if(((Integer)werewolffreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityWerewolf.class, ((Integer)werewolffreq.get()).intValue(), EnumCreatureType.monster);
		}

		if(((Integer)sharkfreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityShark.class, ((Integer)sharkfreq.get()).intValue(), EnumCreatureType.waterCreature);
		}

		if(((Integer)dolphinfreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityDolphin.class, ((Integer)dolphinfreq.get()).intValue(), EnumCreatureType.waterCreature);
		}

		if(((Integer)fishfreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntityFishy.class, ((Integer)fishfreq.get()).intValue(), EnumCreatureType.waterCreature);
		}

		if(((Integer)squidfreq.get()).intValue() == 0) {
			ModLoader.RemoveSpawn(EntitySquid.class, EnumCreatureType.waterCreature);
		}

		if(((Integer)squidfreq.get()).intValue() > 0) {
			ModLoader.AddSpawn(EntitySquid.class, ((Integer)squidfreq.get()).intValue(), EnumCreatureType.waterCreature);
		}

	}

	public String Version() {
		return "v2.10 (MC 1.6_6)";
	}

	public void AddRenderer(Map map1) {
		map1.put(EntityHorse.class, new RenderHorse(new ModelHorse2(), new ModelHorse1()));
		map1.put(EntityFireOgre.class, new RenderFireOgre(new ModelOgre2(), new ModelOgre1(), 1.5F));
		map1.put(EntityCaveOgre.class, new RenderCaveOgre(new ModelOgre2(), new ModelOgre1(), 1.5F));
		map1.put(EntityOgre.class, new RenderOgre(new ModelOgre2(), new ModelOgre1(), 1.5F));
		map1.put(EntityBoar.class, new RenderPig(new ModelPig(), new ModelPig(0.5F), 0.7F));
		map1.put(EntityBear.class, new RenderBear(new ModelBear2(), new ModelBear1(), 0.7F));
		map1.put(EntityDuck.class, new RenderChicken(new ModelChicken(), 0.3F));
		map1.put(EntityBigCat.class, new RenderBigCat(new ModelBigCat2(), new ModelBigCat1(), 0.7F));
		map1.put(EntityDeer.class, new RenderDeer(new ModelDeer(), 0.5F));
		map1.put(EntityWWolf.class, new RenderWWolf(new ModelWolf2(), new ModelWolf1(), 0.7F));
		map1.put(EntityPolarBear.class, new RenderPolarBear(new ModelBear2(), new ModelBear1(), 0.7F));
		map1.put(EntityWraith.class, new RenderBiped(new ModelWraith(), 0.5F));
		map1.put(EntityFlameWraith.class, new RenderBiped(new ModelWraith(), 0.5F));
		map1.put(EntityBunny.class, new RenderBunny(new ModelBunny(), 0.3F));
		map1.put(EntityBird.class, new RenderBird(new ModelBird(), 0.3F));
		map1.put(EntityFox.class, new RenderFox(new ModelFox()));
		map1.put(EntityWerewolf.class, new RenderWerewolf(new ModelWereHuman(), new ModelWerewolf(), 0.7F));
		map1.put(EntityShark.class, new RenderShark(new ModelShark(), 0.6F));
		map1.put(EntitySharkEgg.class, new RenderSharkEgg(new ModelSharkEgg(), 0.1F));
		map1.put(EntityDolphin.class, new RenderDolphin(new ModelDolphin(), 0.6F));
		map1.put(EntityFishy.class, new RenderFishy(new ModelFishy(), 0.2F));
		map1.put(EntityFishyEgg.class, new RenderFishyEgg(new ModelSharkEgg(), 0.1F));
	}

	public void spawnlimits() {
		if(this.spawnwindow == null) {
			WidgetClassicTwocolumn widgetClassicTwocolumn1 = new WidgetClassicTwocolumn(new Widget[0]);
			widgetClassicTwocolumn1.add(maxMobsW);
			widgetClassicTwocolumn1.add(maxAnimalsW);
			widgetClassicTwocolumn1.add(maxWaterMobsW);
			this.spawnwindow = new WidgetSimplewindow(widgetClassicTwocolumn1, "Creature Spawn Limits");
		}

		GuiModScreen.show((Widget)this.spawnwindow);
	}

	public void animalsettings() {
		if(this.animalwindow == null) {
			WidgetClassicTwocolumn widgetClassicTwocolumn1 = new WidgetClassicTwocolumn(new Widget[0]);
			widgetClassicTwocolumn1.add(birdfreqW);
			widgetClassicTwocolumn1.add(bunnyfreqW);
			widgetClassicTwocolumn1.add(duckfreqW);
			widgetClassicTwocolumn1.add(horsefreqW);
			widgetClassicTwocolumn1.add(easybreedingW);
			widgetClassicTwocolumn1.add(pegasusChanceW);
			widgetClassicTwocolumn1.add(deerfreqW);
			this.animalwindow = new WidgetSimplewindow(widgetClassicTwocolumn1, "Pacific Creatures Settings");
		}

		GuiModScreen.show((Widget)this.animalwindow);
	}

	public void huntersettings() {
		if(this.hunterwindow == null) {
			WidgetClassicTwocolumn widgetClassicTwocolumn1 = new WidgetClassicTwocolumn(new Widget[0]);
			widgetClassicTwocolumn1.add(attackhorsesW);
			widgetClassicTwocolumn1.add(attackwolvesW);
			widgetClassicTwocolumn1.add(destroyitemsW);
			widgetClassicTwocolumn1.add(lionfreqW);
			widgetClassicTwocolumn1.add(bearfreqW);
			widgetClassicTwocolumn1.add(pbearfreqW);
			widgetClassicTwocolumn1.add(boarfreqW);
			widgetClassicTwocolumn1.add(foxfreqW);
			this.hunterwindow = new WidgetSimplewindow(widgetClassicTwocolumn1, "Hunter Creatures Settings");
		}

		GuiModScreen.show((Widget)this.hunterwindow);
	}

	public void mobsettings() {
		if(this.mobwindow == null) {
			WidgetClassicTwocolumn widgetClassicTwocolumn1 = new WidgetClassicTwocolumn(new Widget[0]);
			widgetClassicTwocolumn1.add(ogrefreqW);
			widgetClassicTwocolumn1.add(ogreSpawnDifficultyW);
			widgetClassicTwocolumn1.add(ogreStrengthW);
			widgetClassicTwocolumn1.add(fogrefreqW);
			widgetClassicTwocolumn1.add(fogreSpawnDifficultyW);
			widgetClassicTwocolumn1.add(fogreStrengthW);
			widgetClassicTwocolumn1.add(cogrefreqW);
			widgetClassicTwocolumn1.add(cogreSpawnDifficultyW);
			widgetClassicTwocolumn1.add(cogreStrengthW);
			widgetClassicTwocolumn1.add(wwolffreqW);
			widgetClassicTwocolumn1.add(wraithfreqW);
			widgetClassicTwocolumn1.add(wraithSpawnDifficultyW);
			widgetClassicTwocolumn1.add(fwraithfreqW);
			widgetClassicTwocolumn1.add(fwraithSpawnDifficultyW);
			widgetClassicTwocolumn1.add(werewolffreqW);
			widgetClassicTwocolumn1.add(wereSpawnDifficultyW);
			this.mobwindow = new WidgetSimplewindow(widgetClassicTwocolumn1, "Hostile Mob Settings");
		}

		GuiModScreen.show((Widget)this.mobwindow);
	}

	public void watermobsettings() {
		if(this.watermobwindow == null) {
			WidgetClassicTwocolumn widgetClassicTwocolumn1 = new WidgetClassicTwocolumn(new Widget[0]);
			widgetClassicTwocolumn1.add(sharkfreqW);
			widgetClassicTwocolumn1.add(sharkSpawnDifficultyW);
			widgetClassicTwocolumn1.add(squidfreqW);
			widgetClassicTwocolumn1.add(dolphinfreqW);
			widgetClassicTwocolumn1.add(attackdolphinsW);
			widgetClassicTwocolumn1.add(fishfreqW);
			widgetClassicTwocolumn1.add(spawnpiranhaW);
			this.watermobwindow = new WidgetSimplewindow(widgetClassicTwocolumn1, "Water Mobs Settings");
		}

		GuiModScreen.show((Widget)this.watermobwindow);
	}
}
