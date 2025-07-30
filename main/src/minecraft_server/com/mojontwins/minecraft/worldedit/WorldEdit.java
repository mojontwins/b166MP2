package com.mojontwins.minecraft.worldedit;

import com.mojontwins.minecraft.commands.CommandProcessor;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.BlockPos;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3i;

public class WorldEdit {
	public static BlockPos corner1 = new BlockPos();
	public static BlockPos corner2 = new BlockPos();
	public static int clipboard[][][];
	public static Vec3i clipboardDims = Vec3i.NULL_VECTOR.copy();
	
	public static int undo[][][];
	public static BlockPos undoOrigin = new BlockPos();
	public static Vec3i undoDims = Vec3i.NULL_VECTOR.copy();
	public static boolean hasUndo;
	
	public static boolean corner1Set = false;
	public static boolean corner2Set = false;
	
	public static Vec3i relativeOffset = Vec3i.NULL_VECTOR.copy();
	
	public static void init() {
		System.out.println ("Initializing WorldEdit");
		
		corner1Set = false;
		corner2Set = false;
		
		clipboard = null;
		clipboardDims = Vec3i.NULL_VECTOR.copy();
		
		undo = null;
		undoDims = Vec3i.NULL_VECTOR.copy();
		hasUndo = false;
		
		relativeOffset = Vec3i.NULL_VECTOR.copy();
	}
	
	public static boolean checkCorners() {
		return corner1Set && corner2Set;
	}
	
	public static void setCorner1(int x, int y, int z) {
		corner1.x = x;
		corner1.y = y;
		corner1.z = z;
		corner1Set = true;
	}
	
	public static void setCorner2(int x, int y, int z) {
		corner2.x = x;
		corner2.y = y;
		corner2.z = z;
		corner2Set = true;
	}
	
	public static BlockPos getFrom() {
		// Sorts out corner1 and corner2 to get the top-left-frontmost coordinate (min x, y, z)
		return new BlockPos().set(Math.min(corner1.x, corner2.x), Math.min(corner1.y, corner2.y), Math.min(corner1.z, corner2.z));
	}
	
	public static BlockPos getTo() {
		// Sorts out corner1 and corner2 to get the bottom-right-behindmost coordinate (max x, y, z)
		return new BlockPos().set(Math.max(corner1.x, corner2.x), Math.max(corner1.y, corner2.y), Math.max(corner1.z, corner2.z));
	}

	public static int[][][] initBuffer(Vec3i dims, BlockPos from, BlockPos to) {
		dims.x = to.x - from.x + 1;
		dims.y = to.y - from.y + 1;
		dims.z = to.z - from.z + 1;
		
		return new int[dims.x][dims.z][dims.y];
	}
	
	public static void copyToBuffer(int[][][] buffer, World world, BlockPos from, BlockPos to) {
		for(int x = from.x; x <= to.x; x ++) {
			for(int z = from.z; z <= to.z; z ++) {
				for(int y = from.y; y <= to.y; y ++) {
					buffer[x-from.x][z-from.z][y-from.y] = world.getBlockId(x, y, z) | world.getBlockMetadata(x, y, z) << 16;
				}
			}
		}
	}
	
	public static void pasteToWorld(int[][][] buffer, Vec3i dims, World world, BlockPos origin) {
		int x0 = origin.x; 
		int y0 = origin.y;
		int z0 = origin.z;
		
		if(clipboard != null) {
			for(int x = 0; x < dims.x; x ++) {
				for(int z = 0; z < dims.z; z ++) {
					for(int y = 0; y < dims.y; y ++) {
						int clip = buffer[x][z][y];
						world.setBlockAndMetadataWithNotify(x0 + x, y0 + y, z0 + z, clip & 0xFFFF, (clip >> 16) & 0xFF);
					}
				}
			}
		}
	}
	
	public static void copy(World world, EntityPlayer entityPlayer) {
		int px = (int) Math.floor(entityPlayer.posX);
		int py = (int) Math.floor(entityPlayer.posY);
		int pz = (int) Math.floor(entityPlayer.posZ);
		
		BlockPos from = getFrom();
		BlockPos to = getTo();
		
		clipboard = initBuffer(clipboardDims, from, to);
		copyToBuffer(clipboard, world, from, to);
		
		// Calculate offset
		relativeOffset.x = px - from.x;
		relativeOffset.y = py - from.y;
		relativeOffset.z = pz - from.z;
	}
	
	public static int clear(World world) {
		return fill(world, 0, 0);
	}
	
	public static int fill(World world, int blockID, int meta) {
		BlockPos from = getFrom();
		BlockPos to = getTo();
		int cleared = 0;
		
		// Save undo
		undoOrigin = from.copy();
		undo = initBuffer(undoDims, undoOrigin, to);
		copyToBuffer(undo, world, undoOrigin, to);
		hasUndo = true;
		
		for(int x = from.x; x <= to.x; x ++) {
			for(int z = from.z; z <= to.z; z ++) {
				for(int y = from.y; y <= to.y; y ++) {
					world.setBlockAndMetadataWithNotify(x, y, z, blockID, meta);
					cleared ++;
				}
			}
		}
		
		return cleared;
	}
	
	public static int substitute(World world, int existingBlockID, int existingMeta, int blockID, int meta) {
		BlockPos from = getFrom();
		BlockPos to = getTo();
		int cleared = 0;
		
		// Save undo
		undoOrigin = from.copy();
		undo = initBuffer(undoDims, undoOrigin, to);
		copyToBuffer(undo, world, undoOrigin, to);
		hasUndo = true;
		
		for(int x = from.x; x <= to.x; x ++) {
			for(int z = from.z; z <= to.z; z ++) {
				for(int y = from.y; y <= to.y; y ++) {
					int worldBlockID = world.getBlockId(x, y, z);
					int worldMeta = world.getBlockMetadata(x, y, z);
					if(worldBlockID == existingBlockID && (existingMeta == -1 || worldMeta == existingMeta)) world.setBlockAndMetadataWithNotify(x, y, z, blockID, meta);
					cleared ++;
				}
			}
		}
		
		return cleared;
	}
	
	public static void cut(World world, EntityPlayer entityPlayer) {
		copy(world, entityPlayer);
		clear(world);
	}
	
	public static void paste(World world, EntityPlayer entityPlayer) {
		int px = (int) Math.floor(entityPlayer.posX);
		int py = (int) Math.floor(entityPlayer.posY);
		int pz = (int) Math.floor(entityPlayer.posZ);
		
		int x0 = px - relativeOffset.x;
		int y0 = py - relativeOffset.y;
		int z0 = pz - relativeOffset.z;
		
		// Save undo
		undoOrigin = new BlockPos().set(x0, y0, z0);
		BlockPos to = new BlockPos().set(x0 + clipboardDims.x - 1, y0 + clipboardDims.y - 1, z0 + clipboardDims.z - 1);
		undo = initBuffer(undoDims, undoOrigin, to);
		copyToBuffer(undo, world, undoOrigin, to);
		hasUndo = true;
		
		pasteToWorld(clipboard, clipboardDims, world, new BlockPos().set(x0, y0, z0));
	}
	
	public static void undo(World world) {
		pasteToWorld(undo, undoDims, world, undoOrigin);
		hasUndo = false;
	}
	
	public static void rotate_ccw() {
		if(clipboard != null) {
			int aux[][][] = new int[clipboardDims.z][clipboardDims.x][clipboardDims.y];
			for(int x = 0; x < clipboardDims.x; x ++) {
				for(int z = 0; z < clipboardDims.z; z ++) {
					aux[clipboardDims.z - 1 - z][x] = clipboard[x][z];
				}
			}
			
			int t = clipboardDims.x;
			clipboardDims.x = clipboardDims.z;
			clipboardDims.z = t;
			
			clipboard = aux;
		}
	}
	
	public static void rotate_cw() {
		if(clipboard != null) {
			int aux[][][] = new int[clipboardDims.z][clipboardDims.x][clipboardDims.y];
			for(int x = 0; x < clipboardDims.x; x ++) {
				for(int z = 0; z < clipboardDims.z; z ++) {
					aux[z][clipboardDims.x - 1 - x] = clipboard[x][z];
				}
			}
			
			int t = clipboardDims.x;
			clipboardDims.x = clipboardDims.z;
			clipboardDims.z = t;
			
			clipboard = aux;
		}
	}
	
	public static boolean export(World world, int args, boolean flag, EntityPlayer entityPlayer, String filename, ExporterBase exporter, String arg) {
		// Make sure it's in the clipboard
		copy(world, entityPlayer);
		return exporter.export(clipboard, clipboardDims, filename, arg);
	}
	
	public static int clipboardSize() {
		return clipboardDims.x * clipboardDims.y * clipboardDims.z;
	}
	
	public static void registerCommands() {
		System.out.println ("Registering WorldEdit commands");
		CommandProcessor.registerCommand(CommandClear.class);
		CommandProcessor.registerCommand(CommandCopy.class);
		CommandProcessor.registerCommand(CommandCorner1.class);
		CommandProcessor.registerCommand(CommandCorner2.class);
		CommandProcessor.registerCommand(CommandCut.class);
		CommandProcessor.registerCommand(CommandExport.class);
		CommandProcessor.registerCommand(CommandFill.class);
		CommandProcessor.registerCommand(CommandPaste.class);
		CommandProcessor.registerCommand(CommandReplace.class);
		CommandProcessor.registerCommand(CommandRotateYCW.class);
		CommandProcessor.registerCommand(CommandRotateYCCW.class);
		CommandProcessor.registerCommand(CommandUndo.class);
		CommandProcessor.registerCommand(CommandWand.class);
	}
	
}
