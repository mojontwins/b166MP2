package net.minecraft.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.mojang.nbt.NBTBase;
import com.mojang.nbt.NBTTagByte;
import com.mojang.nbt.NBTTagCompound;
import com.mojang.nbt.NBTTagInt;

import net.minecraft.world.level.WorldType;
import net.minecraft.world.level.levelgen.genlayer.GenLayer;
import net.minecraft.world.level.levelgen.genlayer.GenLayerAlpha;

public class GameRules {
	// You should be able to change all this from a special menu in options.
	// This menu should be accessible when creating a new world or ingame.

	protected static File optionsFile;
	
	// Fixed (soft locked)
	public static boolean oldSelectWorldScreen = true;
	public static boolean genlayerWorldChunkManager = false;

	public static final boolean debug = false;
	
	public static HashMap<String, GameRule> gameRules = new HashMap<String, GameRule> ();
	
	public static final String catAesthetics = "Aesthetics";
	public static final String catMobs = "Mobs";
	public static final String catGameplay = "Gameplay";

	public static final String[] categories = {
			catAesthetics,
			catGameplay,
			catMobs
	};
	
	static {
		// Create all rules and give the default value		
		gameRules.put("connectFences", new GameRule().withCaption("connectFences").withDescription("Fences connect to all cube blocks.").category(catAesthetics).withValue(false));
		gameRules.put("generateLapislazuli", new GameRule().withCaption("generateLapislazuli").withDescription("Generate lapislazuli ore.").category(catGameplay).withValue(true));
		gameRules.put("noiseTreeDensity", new GameRule().withCaption("noiseTreeDensity").withDescription("Tree density based on noise.").category(catAesthetics).withValue(true));
		gameRules.put("smarterMobs", new GameRule().withCaption("smarterMobs").withDescription("Use new AI based zombies & skeletons.").category(catMobs).withValue(true));
		gameRules.put("enableSquids", new GameRule().withCaption("enableSquids").withDescription("Spawn squids.").category(catMobs).withValue(true));
		gameRules.put("oldFences", new GameRule().withCaption("oldFences").withDescription("Fences have full block collision.").category(catGameplay).withValue(false));
		gameRules.put("colouredWater", new GameRule().withCaption("colouredWater").withDescription("Use T/H from the biome gen to get color from ramp.").category(catAesthetics).withValue(false));
		gameRules.put("edibleChicken", new GameRule().withCaption("edibleChicken").withDescription("Chicken sometimes drop chicken meat.").category(catMobs).withValue(true));
		gameRules.put("edibleCows", new GameRule().withCaption("edibleCows").withDescription("Cows sometimes drop cow meat.").category(catMobs).withValue(true));
		gameRules.put("canBreedAnimals", new GameRule().withCaption("canBreedAnimals").withDescription("You can make animals mate & reproduce").category(catMobs).withValue(true));
		gameRules.put("skeletonsWithBows", new GameRule().withCaption("skeletonsWithBows").withDescription("Skeletons have visible bows.").category(catAesthetics).withValue(true));
		gameRules.put("classicHurtSound", new GameRule().withCaption("classicHurtSound").withDescription("Use the classic HUH when hurt.").category(catAesthetics).withValue(true));
		gameRules.put("enableHunger", new GameRule().withCaption("enableHunger").withDescription("Enable sprinting mechanic.").category(catGameplay).withValue(false));
		gameRules.put("enableSprinting", new GameRule().withCaption("enableSprinting").withDescription("Enable hunger mechanic.").category(catGameplay).withValue(false));
		gameRules.put("hasSunriseSunset", new GameRule().withCaption("hasSunriseSunset").withDescription("Enable sunrise / sunset colors.").category(catAesthetics).withValue(false));
		gameRules.put("classicBow", new GameRule().withCaption("classicBow").withDescription("Classic shotgun bow").category(catGameplay).withValue(true));
		gameRules.put("stackableFood", new GameRuleStackableFood().withCaption("stackableFood").withDescription("Food is stackable").category(catGameplay).withValue(false));
		gameRules.put("colouredFog", new GameRule().withCaption("colouredFog").withDescription("Coloured fog").category(catAesthetics).withValue(true));
		gameRules.put("renderAllBlocksStraight", new GameRule().withCaption("renderAllBlocksStraight").withDescription("Render al 3axis (i.e. logs) vertical").category(catAesthetics).withValue(false));
		gameRules.put("snowPilesUp", new GameRule().withCaption("snowPilesUp").withDescription("Snow will build up as it keeps snowing").category(catAesthetics).withValue(true));
	}
	
	public static boolean boolRule(String rule) {
		return gameRules.get(rule).getValue();
	}
	
	public static void withMcDataDir(File mcDataDir) {
		optionsFile = new File(mcDataDir, "rules.txt");
		System.out.println ("Gamerules file " + optionsFile.toString());
		
		// Create from default if it does not exist or add missing options
		loadRulesFromOptions();
		saveRulesAsOptions();
	}
	
	public static void loadRules(NBTTagCompound nBTWorldInfo) {
		if(nBTWorldInfo.hasKey("gameRules")) {
			NBTTagCompound nBTGameRules = nBTWorldInfo.getCompoundTag("gameRules");
			
			for(String key : gameRules.keySet()) {
				GameRule gameRule = gameRules.get(key);
				if(gameRule.isIntRule()) {
					gameRule.setIntValue(nBTGameRules.getInteger(key));
				} else {
					gameRule.setValue(nBTGameRules.getBoolean(key));
				}
				
				if(debug) System.out.println ("LOAD " + key + ": " + gameRules.get(key).getValue());
			}
		}
	}

	public static void saveRules(NBTTagCompound nBTWorldInfo) {
		NBTTagCompound nBTGameRules = new NBTTagCompound();
		
		for(String key : gameRules.keySet()) {
			GameRule gameRule = gameRules.get(key);
			if(gameRule.isIntRule()) {
				nBTGameRules.setInteger(key, gameRule.getIntValue());
			} else {
				nBTGameRules.setBoolean(key, gameRule.getValue());
			}
		}
			
		nBTWorldInfo.setCompoundTag("gameRules", nBTGameRules);
	}
	
	public static void saveRulesAsOptions() {
		Properties props = new Properties();
		
		// Get options to an NBT
		NBTTagCompound compound = new NBTTagCompound();
		saveRules(compound);
		NBTTagCompound nBTGameRules = compound.getCompoundTag("gameRules");
		
		// Now convert NBT in Properties
		Collection<NBTBase> ruleNames = nBTGameRules.getTags();
		Iterator<NBTBase> it = ruleNames.iterator();
		while(it.hasNext()) {
			NBTBase tag = it.next();
			String tagName = tag.getName();
			
			if(tag instanceof NBTTagByte) {
				props.setProperty(tagName, nBTGameRules.getBoolean(tagName) ? "true" : "false");
			} else if(tag instanceof NBTTagInt) {
				props.setProperty(tagName, "" + nBTGameRules.getInteger(tagName));
			}
		}
		
		// Save to file
		try {
			props.store(new FileOutputStream(optionsFile), "Minecraft Game Rules");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadRulesFromOptions() {
		if(!optionsFile.isFile()) {
			System.out.println ("Generating default Gamerules file");
			return;
		}
		
		// Load from file
		Properties prop = new Properties();
		
		try {
			prop.load(new FileInputStream(optionsFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Generate a NBT from properties
		NBTTagCompound nBTGameRules = new NBTTagCompound();
		
		prop.forEach((k, v) -> {
			if("true".equals(v) || "false".equals(v)) {
				nBTGameRules.setBoolean((String)k, "true".equals(v));
			} else {
				try {
					nBTGameRules.setInteger((String)k, Integer.parseInt((String) v));
				} catch (NumberFormatException nfe) {
					nBTGameRules.setString((String)k, (String)v);
				}
			}
		});
		
		// Get options from NBT
		NBTTagCompound compound =(new NBTTagCompound());
		compound.setCompoundTag("gameRules", nBTGameRules);
		loadRules(compound);
	}
	
	// Change these at a later time
	public static GenLayer[] initializeAllBiomeGenerators(long seed, WorldType worldType) {
		if(genlayerWorldChunkManager) {
			return GenLayer.initializeAllBiomeGenerators(seed, worldType);
		} else {
			return GenLayerAlpha.initializeAllBiomeGenerators(seed, worldType);
		}
	}
	
	public static WorldType defaultWorldType() {
		if(genlayerWorldChunkManager) {
			return WorldType.DEFAULT;
		} else {
			return WorldType.ALPHA;
		}
	}
	
	public static WorldType defaultWorldType(boolean snowCovered) {
		if(genlayerWorldChunkManager) {
			return WorldType.DEFAULT;
		} else {
			return snowCovered ? WorldType.ALPHA_SNOW : WorldType.ALPHA;
		}
	}
	
	public static WorldType defaultWorldType(int worldTypeID) {
		switch (worldTypeID) {
		case 1: return WorldType.ALPHA_SNOW;
		case 2: return WorldType.INFDEV;
		case 3: return WorldType.SKY;
		case 4: return WorldType.OCEAN;
		default: return WorldType.ALPHA;
		}
	}

	public static List<GameRule> getRulesOfCategory(String category) {
		List<GameRule> rules = new ArrayList<>();
		
		gameRules.forEach((k, v) -> {
			GameRule rule = (GameRule)v;
			if (rule.getCategory().equals(category)) rules.add(rule);
		});
		
		return rules;
	}
	
	public static void refreshAllRules() {
		gameRules.forEach((k, v) -> {
			((GameRule)v).refresh();
		});
	}
}
