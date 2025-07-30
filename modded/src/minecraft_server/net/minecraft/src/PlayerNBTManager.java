package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class PlayerNBTManager implements IPlayerFileData, ISaveHandler {
	private static final Logger logger = Logger.getLogger("Minecraft");
	private final File worldDir;
	private final File worldFile;
	private final File field_28112_d;
	private final long field_22100_d = System.currentTimeMillis();

	public PlayerNBTManager(File file1, String string2, boolean z3) {
		this.worldDir = new File(file1, string2);
		this.worldDir.mkdirs();
		this.worldFile = new File(this.worldDir, "players");
		this.field_28112_d = new File(this.worldDir, "data");
		this.field_28112_d.mkdirs();
		if(z3) {
			this.worldFile.mkdirs();
		}

		this.func_22098_f();
	}

	private void func_22098_f() {
		try {
			File file1 = new File(this.worldDir, "session.lock");
			DataOutputStream dataOutputStream2 = new DataOutputStream(new FileOutputStream(file1));

			try {
				dataOutputStream2.writeLong(this.field_22100_d);
			} finally {
				dataOutputStream2.close();
			}

		} catch (IOException iOException7) {
			iOException7.printStackTrace();
			throw new RuntimeException("Failed to check session lock, aborting");
		}
	}

	protected File getWorldDir() {
		return this.worldDir;
	}

	public void func_22091_b() {
		try {
			File file1 = new File(this.worldDir, "session.lock");
			DataInputStream dataInputStream2 = new DataInputStream(new FileInputStream(file1));

			try {
				if(dataInputStream2.readLong() != this.field_22100_d) {
					throw new MinecraftException("The save is being accessed from another location, aborting");
				}
			} finally {
				dataInputStream2.close();
			}

		} catch (IOException iOException7) {
			throw new MinecraftException("Failed to check session lock, aborting");
		}
	}

	public IChunkLoader func_22092_a(WorldProvider worldProvider1) {
		if(worldProvider1 instanceof WorldProviderHell) {
			File file2 = new File(this.worldDir, "DIM-1");
			file2.mkdirs();
			return new ChunkLoader(file2, true);
		} else {
			return new ChunkLoader(this.worldDir, true);
		}
	}

	public WorldInfo func_22096_c() {
		File file1 = new File(this.worldDir, "level.dat");
		NBTTagCompound nBTTagCompound2;
		NBTTagCompound nBTTagCompound3;
		if(file1.exists()) {
			try {
				nBTTagCompound2 = CompressedStreamTools.func_770_a(new FileInputStream(file1));
				nBTTagCompound3 = nBTTagCompound2.getCompoundTag("Data");
				return new WorldInfo(nBTTagCompound3);
			} catch (Exception exception5) {
				exception5.printStackTrace();
			}
		}

		file1 = new File(this.worldDir, "level.dat_old");
		if(file1.exists()) {
			try {
				nBTTagCompound2 = CompressedStreamTools.func_770_a(new FileInputStream(file1));
				nBTTagCompound3 = nBTTagCompound2.getCompoundTag("Data");
				return new WorldInfo(nBTTagCompound3);
			} catch (Exception exception4) {
				exception4.printStackTrace();
			}
		}

		return null;
	}

	public void func_22095_a(WorldInfo worldInfo1, List list2) {
		NBTTagCompound nBTTagCompound3 = worldInfo1.func_22183_a(list2);
		NBTTagCompound nBTTagCompound4 = new NBTTagCompound();
		nBTTagCompound4.setTag("Data", nBTTagCompound3);

		try {
			File file5 = new File(this.worldDir, "level.dat_new");
			File file6 = new File(this.worldDir, "level.dat_old");
			File file7 = new File(this.worldDir, "level.dat");
			CompressedStreamTools.writeGzippedCompoundToOutputStream(nBTTagCompound4, new FileOutputStream(file5));
			if(file6.exists()) {
				file6.delete();
			}

			file7.renameTo(file6);
			if(file7.exists()) {
				file7.delete();
			}

			file5.renameTo(file7);
			if(file5.exists()) {
				file5.delete();
			}
		} catch (Exception exception8) {
			exception8.printStackTrace();
		}

	}

	public void func_22094_a(WorldInfo worldInfo1) {
		NBTTagCompound nBTTagCompound2 = worldInfo1.func_22185_a();
		NBTTagCompound nBTTagCompound3 = new NBTTagCompound();
		nBTTagCompound3.setTag("Data", nBTTagCompound2);

		try {
			File file4 = new File(this.worldDir, "level.dat_new");
			File file5 = new File(this.worldDir, "level.dat_old");
			File file6 = new File(this.worldDir, "level.dat");
			CompressedStreamTools.writeGzippedCompoundToOutputStream(nBTTagCompound3, new FileOutputStream(file4));
			if(file5.exists()) {
				file5.delete();
			}

			file6.renameTo(file5);
			if(file6.exists()) {
				file6.delete();
			}

			file4.renameTo(file6);
			if(file4.exists()) {
				file4.delete();
			}
		} catch (Exception exception7) {
			exception7.printStackTrace();
		}

	}

	public void writePlayerData(EntityPlayer entityPlayer1) {
		try {
			NBTTagCompound nBTTagCompound2 = new NBTTagCompound();
			entityPlayer1.writeToNBT(nBTTagCompound2);
			File file3 = new File(this.worldFile, "_tmp_.dat");
			File file4 = new File(this.worldFile, entityPlayer1.username + ".dat");
			CompressedStreamTools.writeGzippedCompoundToOutputStream(nBTTagCompound2, new FileOutputStream(file3));
			if(file4.exists()) {
				file4.delete();
			}

			file3.renameTo(file4);
		} catch (Exception exception5) {
			logger.warning("Failed to save player data for " + entityPlayer1.username);
		}

	}

	public void readPlayerData(EntityPlayer entityPlayer1) {
		NBTTagCompound nBTTagCompound2 = this.getPlayerData(entityPlayer1.username);
		if(nBTTagCompound2 != null) {
			entityPlayer1.readFromNBT(nBTTagCompound2);
		}

	}

	public NBTTagCompound getPlayerData(String string1) {
		try {
			File file2 = new File(this.worldFile, string1 + ".dat");
			if(file2.exists()) {
				return CompressedStreamTools.func_770_a(new FileInputStream(file2));
			}
		} catch (Exception exception3) {
			logger.warning("Failed to load player data for " + string1);
		}

		return null;
	}

	public IPlayerFileData func_22090_d() {
		return this;
	}

	public void func_22093_e() {
	}

	public File func_28111_b(String string1) {
		return new File(this.field_28112_d, string1 + ".dat");
	}
}
