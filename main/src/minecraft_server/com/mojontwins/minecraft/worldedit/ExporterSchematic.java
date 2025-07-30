package com.mojontwins.minecraft.worldedit;

import java.io.DataOutputStream;
import java.io.FileOutputStream;

import com.mojang.nbt.NBTBase;
import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.phys.Vec3i;

public class ExporterSchematic extends ExporterBase {

	@Override
	public String getName() {
		return "schematic";
	}

	@Override
	public boolean export(int[][][] buffer, Vec3i dims, String fileName, String arg) {
		DataOutputStream out = null; 
		
		try {
			out = new DataOutputStream(new FileOutputStream(fileName));
			
			NBTTagCompound schematic = new NBTTagCompound();
			NBTTagCompound entities = new NBTTagCompound();
			NBTTagCompound tileEntities = new NBTTagCompound();
			
			schematic.setCompoundTag("Entities", entities);
			schematic.setCompoundTag("TileEntities", tileEntities);
			
			int w = dims.x;
			int h = dims.y;
			int l = dims.z;
			
			schematic.setShort("Width", (short)w);
			schematic.setShort("Height", (short)h);
			schematic.setShort("Length", (short)l);
			
			schematic.setString("Materials", "Alpha");
			schematic.setString("Client", "Infhell");
			
			byte[] data = new byte[w*h*l];
			byte[] meta = new byte[w*h*l];
			
			int idx = 0;
			for(int y = 0; y < h; y ++) {
				for(int z = 0; z < l; z ++) {
					for(int x = 0; x < w; x ++) {
	
						int encoded = buffer[x][z][y];
						int blockID = encoded & 0xFFFF;
						int metadata = encoded >> 16;
									
						data[idx] = (byte) (blockID & 0xff);
						meta[idx] = (byte) (metadata & 0xff);
						idx ++;
					}
				}
			}
			
			schematic.setByteArray("Blocks", data);
			schematic.setByteArray("Data", meta);
			
			NBTBase.writeNamedTag(schematic, out);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return true;
	}

	@Override
	public String getHelp() {
		return "Exports area in .schematic format. No extra parameters for this exporter.";
	}

}
