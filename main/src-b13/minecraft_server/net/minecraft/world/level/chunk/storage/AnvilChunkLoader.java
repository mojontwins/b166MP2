package net.minecraft.world.level.chunk.storage;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.mojang.nbt.CompressedStreamTools;
import com.mojang.nbt.NBTTagCompound;
import com.mojang.nbt.NBTTagList;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityList;
import net.minecraft.world.level.NextTickListEntry;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.ExtendedBlockStorage;
import net.minecraft.world.level.chunk.IChunkLoader;
import net.minecraft.world.level.chunk.NibbleArray;
import net.minecraft.world.level.tile.entity.TileEntity;

public class AnvilChunkLoader implements IThreadedFileIO, IChunkLoader {
	private List<AnvilChunkLoaderPending> anvilChunkLoaderPending = new ArrayList<AnvilChunkLoaderPending>();
	private Set<ChunkCoordIntPair> chunkCoordIntPairList = new HashSet<ChunkCoordIntPair>();
	private Object fileLock = new Object();
	private final File chunkSaveLocation;

	public AnvilChunkLoader(File saveFile) {
		this.chunkSaveLocation = saveFile;
	}

	public Chunk loadChunk(World world, int chunkX, int chunkZ) throws IOException {
		NBTTagCompound nbt = null;

		ChunkCoordIntPair chunkCoords = new ChunkCoordIntPair(chunkX, chunkZ);
		
		synchronized(this.fileLock) {
			if(this.chunkCoordIntPairList.contains(chunkCoords)) {
				for(int i7 = 0; i7 < this.anvilChunkLoaderPending.size(); ++i7) {
					if(((AnvilChunkLoaderPending)this.anvilChunkLoaderPending.get(i7)).field_48427_a.equals(chunkCoords)) {
						nbt = ((AnvilChunkLoaderPending)this.anvilChunkLoaderPending.get(i7)).field_48426_b;
						break;
					}
				}
			}
		}

		if(nbt == null) {
			DataInputStream dataInputStream10 = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, chunkX, chunkZ);
			if(dataInputStream10 == null) {
				return null;
			}

			nbt = CompressedStreamTools.read((DataInput)dataInputStream10);
		}

		return this.loadChunkSections(world, chunkX, chunkZ, nbt);
	}

	protected Chunk loadChunkSections(World world, int chunkX, int chunkZ, NBTTagCompound nbt) {
		if(!nbt.hasKey("Level")) {
			System.out.println("Chunk file at " + chunkX + "," + chunkZ + " is missing level data, skipping");
			return null;
		} else if(!nbt.getCompoundTag("Level").hasKey("Sections")) {
			System.out.println("Chunk file at " + chunkX + "," + chunkZ + " is missing block data, skipping");
			return null;
		} else {
			Chunk chunk = this.doLoadChunk(world, nbt.getCompoundTag("Level"));
			if(!chunk.isAtLocation(chunkX, chunkZ)) {
				System.out.println("Chunk file at " + chunkX + "," + chunkZ + " is in the wrong location; relocating. (Expected " + chunkX + ", " + chunkZ + ", got " + chunk.xPosition + ", " + chunk.zPosition + ")");
				nbt.setInteger("xPos", chunkX);
				nbt.setInteger("zPos", chunkZ);
				chunk = this.doLoadChunk(world, nbt.getCompoundTag("Level"));
			}

			chunk.removeUnknownBlocks();
			return chunk;
		}
	}

	public void saveChunk(World world, Chunk chunk) {
		world.checkSessionLock();

		try {
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagCompound nbtLevel = new NBTTagCompound();
			nbt.setTag("Level", nbtLevel);
			this.doSaveChunk(chunk, world, nbtLevel);
			this.enqueueChunkSave(chunk.getChunkCoordIntPair(), nbt);
		} catch (Exception exception5) {
			exception5.printStackTrace();
		}

	}

	protected void enqueueChunkSave(ChunkCoordIntPair chunkCoordIntPair1, NBTTagCompound nBTTagCompound2) {
		synchronized(this.fileLock) {
			if(this.chunkCoordIntPairList.contains(chunkCoordIntPair1)) {
				for(int i4 = 0; i4 < this.anvilChunkLoaderPending.size(); ++i4) {
					if(((AnvilChunkLoaderPending)this.anvilChunkLoaderPending.get(i4)).field_48427_a.equals(chunkCoordIntPair1)) {
						this.anvilChunkLoaderPending.set(i4, new AnvilChunkLoaderPending(chunkCoordIntPair1, nBTTagCompound2));
						return;
					}
				}
			}

			this.anvilChunkLoaderPending.add(new AnvilChunkLoaderPending(chunkCoordIntPair1, nBTTagCompound2));
			this.chunkCoordIntPairList.add(chunkCoordIntPair1);
			ThreadedFileIOBase.threadedIOInstance.queueIO(this);
		}
	}

	public boolean writeNextIO() {
		AnvilChunkLoaderPending anvilChunkLoaderPending1 = null;
		synchronized(this.fileLock) {
			if(this.anvilChunkLoaderPending.size() <= 0) {
				return false;
			}

			anvilChunkLoaderPending1 = (AnvilChunkLoaderPending)this.anvilChunkLoaderPending.remove(0);
			this.chunkCoordIntPairList.remove(anvilChunkLoaderPending1.field_48427_a);
		}

		if(anvilChunkLoaderPending1 != null) {
			try {
				this.doWriteToDisk(anvilChunkLoaderPending1);
			} catch (Exception exception4) {
				exception4.printStackTrace();
			}
		}

		return true;
	}

	private void doWriteToDisk(AnvilChunkLoaderPending anvilChunkLoaderPending1) throws IOException {
		DataOutputStream dataOutputStream2 = RegionFileCache.getChunkOutputStream(this.chunkSaveLocation, anvilChunkLoaderPending1.field_48427_a.chunkXPos, anvilChunkLoaderPending1.field_48427_a.chunkZPos);
		CompressedStreamTools.write(anvilChunkLoaderPending1.field_48426_b, (DataOutput)dataOutputStream2);
		dataOutputStream2.close();
	}

	public void saveExtraChunkData(World world, Chunk chunk2) {
	}

	public void chunkTick() {
	}

	public void saveExtraData() {
	}

	private void doSaveChunk(Chunk chunk, World world, NBTTagCompound nbt) {
		world.checkSessionLock();

		// General data
		nbt.setInteger("xPos", chunk.xPosition);
		nbt.setInteger("zPos", chunk.zPosition);
		nbt.setLong("LastUpdate", world.getWorldTime());
		nbt.func_48183_a("HeightMap", chunk.heightMap);
		nbt.setBoolean("TerrainPopulated", chunk.isTerrainPopulated);
		
		ExtendedBlockStorage[] ebsArray = chunk.getBlockStorageArray();
		NBTTagList nbtSections = new NBTTagList("Sections");

		ExtendedBlockStorage[] ebsArrayS = ebsArray;
		int numSections = ebsArray.length;

		NBTTagCompound nbtSection;
		for(int i = 0; i < numSections; ++i) {
			ExtendedBlockStorage ebs = ebsArrayS[i];
			if(ebs != null && ebs.blockCount() != 0) {
				nbtSection = new NBTTagCompound();
				nbtSection.setByte("Y", (byte)(ebs.getYLocation() >> 4 & 255));
				
				// Save blocks
				nbtSection.setByteArray("Blocks", ebs.getBlockLSBArray());
				if(ebs.getBlockMSBArray() != null) {
					nbtSection.setByteArray("Add", ebs.getBlockMSBArray().data);
				}

				// Save metadata. TODO: expand to 8 bit
				nbtSection.setByteArray("Data", ebs.getMetadataArray().data);

				// Save light
				nbtSection.setByteArray("SkyLight", ebs.getSkylightArray().data);
				nbtSection.setByteArray("BlockLight", ebs.getBlocklightArray().data);

				nbtSections.appendTag(nbtSection);
			}
		}

		nbt.setTag("Sections", nbtSections);

		// Save biome cache
		nbt.setByteArray("Biomes", chunk.getBiomeArray());
		chunk.hasEntities = false;

		{
			NBTTagList nbtEntities = new NBTTagList();
			Iterator<Entity> it;
			for(int i = 0; i < chunk.entityLists.length; ++i) {
				it = chunk.entityLists[i].iterator();
	
				while(it.hasNext()) {
					Entity entity = (Entity)it.next();
					chunk.hasEntities = true;
					nbtSection = new NBTTagCompound();
					if(entity.addEntityID(nbtSection)) {
						nbtEntities.appendTag(nbtSection);
					}
				}
			}
	
			nbt.setTag("Entities", nbtEntities);
		}
		
		{
			NBTTagList nbtTileEntities = new NBTTagList();
			Iterator<TileEntity> it = chunk.chunkTileEntityMap.values().iterator();
	
			while(it.hasNext()) {
				TileEntity tileEntity20 = (TileEntity)it.next();
				nbtSection = new NBTTagCompound();
				tileEntity20.writeToNBT(nbtSection);
				nbtTileEntities.appendTag(nbtSection);
			}
	
			nbt.setTag("TileEntities", nbtTileEntities);
		}
		
		List<NextTickListEntry> pendingUpdatesList = world.getPendingBlockUpdates(chunk, false);
		if(pendingUpdatesList != null) {
			long worldTime = world.getWorldTime();
			NBTTagList nbtTileTicks = new NBTTagList();
			Iterator<NextTickListEntry> it = pendingUpdatesList.iterator();

			while(it.hasNext()) {
				NextTickListEntry ntle = (NextTickListEntry)it.next();
				NBTTagCompound nbtTileTick = new NBTTagCompound();
				nbtTileTick.setInteger("i", ntle.blockID);
				nbtTileTick.setInteger("x", ntle.xCoord);
				nbtTileTick.setInteger("y", ntle.yCoord);
				nbtTileTick.setInteger("z", ntle.zCoord);
				nbtTileTick.setInteger("t", (int)(ntle.scheduledTime - worldTime));
				nbtTileTicks.appendTag(nbtTileTick);
			}

			nbt.setTag("TileTicks", nbtTileTicks);
		}

	}

	private Chunk doLoadChunk(World world, NBTTagCompound nbt) {
		int chunkX = nbt.getInteger("xPos");
		int chunkZ = nbt.getInteger("zPos");

		Chunk chunk = new Chunk(world, chunkX, chunkZ);
		
		chunk.heightMap = nbt.getIntArray("HeightMap");
		chunk.isTerrainPopulated = nbt.getBoolean("TerrainPopulated");
		
		NBTTagList mbtSections = nbt.getTagList("Sections");
		byte maxSections = 16;
		ExtendedBlockStorage[] ebsArray = new ExtendedBlockStorage[maxSections];

		for(int i = 0; i < mbtSections.tagCount(); ++i) {
			NBTTagCompound nbtSection = (NBTTagCompound)mbtSections.tagAt(i);
			byte sectionY = nbtSection.getByte("Y");
			ExtendedBlockStorage ebs = new ExtendedBlockStorage(sectionY << 4);

			// Read blocks
			ebs.setBlockLSBArray(nbtSection.getByteArray("Blocks"));
			if(nbtSection.hasKey("Add")) {
				ebs.setBlockMSBArray(new NibbleArray(nbtSection.getByteArray("Add"), 4));
			}

			// Read metadata. TODO: Modify this to read 8 bits!
			ebs.setBlockMetadataArray(new NibbleArray(nbtSection.getByteArray("Data"), 4));

			// Read skylight and block light
			ebs.setSkylightArray(new NibbleArray(nbtSection.getByteArray("SkyLight"), 4));
			ebs.setBlocklightArray(new NibbleArray(nbtSection.getByteArray("BlockLight"), 4));
			
			ebs.cleanupAndUpdateCounters();
			ebsArray[sectionY] = ebs;
		}

		chunk.setStorageArrays(ebsArray);

		// Read biome cache
		if(nbt.hasKey("Biomes")) {
			chunk.setBiomeArray(nbt.getByteArray("Biomes"));
		}

		// Read entities
		NBTTagList nbtEntities = nbt.getTagList("Entities");
		if(nbtEntities != null) {
			for(int i = 0; i < nbtEntities.tagCount(); ++i) {
				NBTTagCompound nbtEntity = (NBTTagCompound)nbtEntities.tagAt(i);
				Entity entity = EntityList.createEntityFromNBT(nbtEntity, world);
				chunk.hasEntities = true;
				if(entity != null) {
					chunk.addEntity(entity);
				}
			}
		}

		// Read tile entities
		NBTTagList nbtTileEntities = nbt.getTagList("TileEntities");
		if(nbtTileEntities != null) {
			for(int i = 0; i < nbtTileEntities.tagCount(); ++i) {
				NBTTagCompound nbtTileEntity = (NBTTagCompound)nbtTileEntities.tagAt(i);
				TileEntity tileEntity13 = TileEntity.createAndLoadEntity(nbtTileEntity);
				if(tileEntity13 != null) {
					chunk.addTileEntity(tileEntity13);
				}
			}
		}

		// Read tile ticks
		if(nbt.hasKey("TileTicks")) {
			NBTTagList nbtTileTicks = nbt.getTagList("TileTicks");
			if(nbtTileTicks != null) {
				for(int i = 0; i < nbtTileTicks.tagCount(); ++i) {
					NBTTagCompound nbtTileTick = (NBTTagCompound)nbtTileTicks.tagAt(i);
					world.scheduleBlockUpdateFromLoad(nbtTileTick.getInteger("x"), nbtTileTick.getInteger("y"), nbtTileTick.getInteger("z"), nbtTileTick.getInteger("i"), nbtTileTick.getInteger("t"));
				}
			}
		}

		return chunk;
	}
}
