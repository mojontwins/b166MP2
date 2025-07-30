package com.mojontwins.minecraft.commands;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.BlockPos;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkCoordinates;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;

public class CommandTree extends CommandBase {
	
	// Will only work as single player command (for debugging)
	public boolean shouldList() {
		return false;
	}

	public CommandTree() {
	}

	@Override
	public String getString() {
		return "tree";
	}

	@Override
	public int getMinParams() {
		return 1;
	}

	@SuppressWarnings("unused")
	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		int x = 0, y = 0, z = 0;
		String treeShape = "";
		String treeVariant = "";
		
		BlockPos soilBlockPos = this.theCommandSender.getMouseOverCoordinates();
		if(soilBlockPos != null) {
			x = soilBlockPos.x;
			y = soilBlockPos.y + 1;
			z = soilBlockPos.z;
		}
		
		if (idx >= 4) {
			x = Integer.parseInt(tokens[1]);
			z = Integer.parseInt(tokens[2]);
			y = theWorld.getLandSurfaceHeightValue(x, z);
			
			treeShape = tokens[3];
			if(idx > 4) treeVariant = tokens[4];
		} else {
			treeShape = tokens[1];
			if(idx > 2) treeVariant = tokens[2];
		}
		
		// This may look cheesy but may improve in the future (NOT)
		WorldGenerator treeGen = null;
		
		/*
		if("fir".equals(treeShape)) {
			if("big".equals(treeVariant)) {
				treeGen = new WorldGenFir(8 + theWorld.rand.nextInt(6), true);
			} else {
				treeGen = new WorldGenFir(4 + theWorld.rand.nextInt(4), false);
			}
		} else if("pine".equals(treeShape)) {
			treeGen = new WorldGenPineTree(8 + theWorld.rand.nextInt(6), true);
		} else if("mangrove".equals(treeShape)) {
			treeGen = new WorldGenMangrove(true);
			y = 63;
		} else if("nylium".equals(treeShape)) {
			treeGen = new WorldGenNylium(true);
		} else if("acacia".equals(treeShape)) {
			treeGen = new WorldGenAcacia(true);
		} else if("willow".equals(treeShape)) {
				treeGen = new WorldGenWillow(4 + theWorld.rand.nextInt(4), true);
		} else if("cypress".equals(treeShape)) {
			treeGen = new WorldGenCypress(5 + theWorld.rand.nextInt(5), true);
		} else if("palm".equals(treeShape)) {
				treeGen = new WorldGenPalmTree(true);
		}  else if("compare".equals(treeShape)) {
		}
		*/
		
		if(treeGen != null) {
			treeGen.generate(theWorld, theWorld.rand, x, y, z);
		}
		
		return 0;
	}

	@Override
	public String getHelp() {
		return "[debug command]";
	}

}
