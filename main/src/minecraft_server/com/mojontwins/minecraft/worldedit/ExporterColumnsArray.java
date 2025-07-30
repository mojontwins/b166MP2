package com.mojontwins.minecraft.worldedit;

import java.io.File;
import java.io.FileWriter;

import net.minecraft.world.phys.Vec3i;

public class ExporterColumnsArray extends ExporterBase {
	public static final boolean is4096IDs = false;

	@Override
	public String getName() {
		return "columnsArray";
	}

	@Override
	public boolean export(int[][][] buffer, Vec3i dims, String fileName, String arg) {
		boolean doRotations = "rotations".equals(arg);
		FileWriter fileWriter = null;
		int [] columnBuffer = new int [dims.y];
		
		try {	
			File fileOut = new File(fileName);
			fileWriter = new FileWriter(fileOut);
			
			// Main rotation (direct copy)
			if(doRotations) fileWriter.write("// Rotation 1 (x, z)\n");
			fileWriter.write("public static final short[][][] array" + (doRotations ? "1" : "") + " = new short [][][] {\n");	
			
			for(int x = 0; x < dims.x; x ++) {
				fileWriter.write("\t{\n");
							
				for(int z = 0; z < dims.z; z ++) {
					for(int y = 0; y < dims.y; y ++) {
						columnBuffer[y] = buffer[x][z][y];
					}
					
					// TODO: Add RLE parameter and compress <here>
					
					this.writeFullArrayColumn(columnBuffer, fileWriter);
				}
				
				fileWriter.write("\t},\n");
			}
			fileWriter.write("};\n");
			
			if(doRotations) {
				fileWriter.write("// Rotation 2 (z, -x)\n");
				fileWriter.write("public static final short[][][] array2 = new short [][][] {\n");	
				
				for(int z = 0; z < dims.z; z ++) {
					fileWriter.write("\t{\n");
								
					for(int x = dims.x - 1; x >= 0; x --) {
						for(int y = 0; y < dims.y; y ++) {
							columnBuffer[y] = buffer[x][z][y];
						}
						
						// TODO: Add RLE parameter and compress <here>
						
						this.writeFullArrayColumn(columnBuffer, fileWriter);
					}
					
					fileWriter.write("\t},\n");
				}
				fileWriter.write("};\n");
				
				fileWriter.write("// Rotation 3 (-x, -z)\n");
				fileWriter.write("public static final short[][][] array3 = new short [][][] {\n");	
				
				for(int x = dims.x - 1; x >= 0; x --) {
					fileWriter.write("\t{\n");
								
					for(int z = dims.z - 1; z >= 0; z --) {
						for(int y = 0; y < dims.y; y ++) {
							columnBuffer[y] = buffer[x][z][y];
						}
						
						// TODO: Add RLE parameter and compress <here>
						
						this.writeFullArrayColumn(columnBuffer, fileWriter);
					}
					
					fileWriter.write("\t},\n");
				}
				fileWriter.write("};\n");
				
				fileWriter.write("// Rotation 4 (-z, x)\n");
				fileWriter.write("public static final short[][][] array4 = new short [][][] {\n");	
				
				for(int z = dims.z - 1; z >= 0; z --) {
					fileWriter.write("\t{\n");
								
					for(int x = 0; x < dims.x; x ++) {
						for(int y = 0; y < dims.y; y ++) {
							columnBuffer[y] = buffer[x][z][y];
						}
						
						// TODO: Add RLE parameter and compress <here>
						
						this.writeFullArrayColumn(columnBuffer, fileWriter);
					}
					
					fileWriter.write("\t},\n");
				}
				fileWriter.write("};\n");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				fileWriter.close();
			} catch (Exception e) {}
		}
		return true;
	}

	@Override
	public String getHelp() {
		return "Exports a column-first ordered based raw array\n"
				+ "compatible with Dynamic Schematics or features\n"
				+ "directly based on Schematics. \n"
				+ "Adding arg \"rotations\" generates 4 rotated arrays\n"
				+ "nothing generates direct copy.\nEach array item is " + 
				(is4096IDs ? 
						"id | (meta << 12)"
					:
						"id | (meta << 8)"
				);
	}
	
	static String toIdPlusMeta(int a) {
		if(is4096IDs) {
			if(a < 4096) return "" + a;
			return (a & 4095) + " | (" + (a >> 16) + " << 12)";
		} else {
			if(a < 256) return "" + a;
			return (a & 255) + " | (" + (a >> 16) + " << 8)";
		}
	}
	
	public void writeFullArrayColumn(int [] buffer, FileWriter fileWriter) throws Exception {
		fileWriter.write("\t\t{ ");
		for(int i = 0; i < buffer.length; i ++) {
			fileWriter.write(toIdPlusMeta(buffer[i]));
			if(i < buffer.length - 1) fileWriter.write(", ");
		}
		fileWriter.write("}, \n");
	}

}
