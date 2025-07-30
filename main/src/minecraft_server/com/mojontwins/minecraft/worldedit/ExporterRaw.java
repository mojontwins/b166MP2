package com.mojontwins.minecraft.worldedit;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.minecraft.world.phys.Vec3i;

public class ExporterRaw extends ExporterBase {
	
	// Simple raw exporter. Format is:
	// int x, int y, int z: dimensions.
	// int [x][z][y] encoded (blockID | (metadata << 16))

	@Override
	public String getName() {
		return "raw";
	}

	@Override
	public boolean export(int[][][] buffer, Vec3i dims, String fileName, String arg) {
		DataOutputStream out = null;
		
		try {
			out = new DataOutputStream (new FileOutputStream(fileName));
			out.writeInt(dims.x);
			out.writeInt(dims.y);
			out.writeInt(dims.z);
			
			for(int x = 0; x < dims.x; x ++) {
				for(int z = 0; z < dims.z; z ++) {
					for(int y = 0; y < dims.y; y ++) {
						out.writeInt(buffer[x][z][y]);
					}
				}
			}
		    
		} catch (IOException e) {
			e.printStackTrace();
			return false;
			
		} finally {
			try {
				out.flush();
		        out.close();
			} catch(Exception e){
	            System.out.println(e.getMessage());
	        }
			
		}
		
		return true;
	}

	@Override
	public String getHelp() {
		return "No extra parameters for this exporter.";
	}

}
