package net.minecraft.world.level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.mojang.nbt.NBTTagCompound;

import ca.spottedleaf.starlight.StarlightEngine;
import net.minecraft.src.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLightningBolt;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.map.MapStorage;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkCache;
import net.minecraft.world.level.chunk.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.ChunkCoordinates;
import net.minecraft.world.level.chunk.ChunkProvider;
import net.minecraft.world.level.chunk.ExtendedBlockStorage;
import net.minecraft.world.level.chunk.IChunkLoader;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.chunk.storage.IProgressUpdate;
import net.minecraft.world.level.chunk.storage.ISaveHandler;
import net.minecraft.world.level.chunk.storage.ThreadedFileIOBase;
import net.minecraft.world.level.dimension.WorldProvider;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockFluid;
import net.minecraft.world.level.tile.BlockStairs;
import net.minecraft.world.level.tile.BlockStep;
import net.minecraft.world.level.tile.IPlant;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

public class World implements IBlockAccess {
	public boolean scheduledUpdatesAreImmediate = false;
	public List<Entity> loadedEntityList = new ArrayList<Entity>();
	private List<Entity> unloadedEntityList = new ArrayList<Entity>();
	private TreeSet<NextTickListEntry> scheduledTickTreeSet = new TreeSet<NextTickListEntry>();
	private Set<NextTickListEntry> scheduledTickSet = new HashSet<NextTickListEntry>();
	public List<TileEntity> loadedTileEntityList = new ArrayList<TileEntity>();
	private List<TileEntity> addedTileEntityList = new ArrayList<TileEntity>();
	private List<TileEntity> entityRemoval = new ArrayList<TileEntity>();
	public List<EntityPlayer> playerEntities = new ArrayList<EntityPlayer>();
	public List<Entity> weatherEffects = new ArrayList<Entity>();
	private long cloudColour = 16777215L;
	public int skylightSubtracted = 0;
	protected int updateLCG = (new Random()).nextInt();
	protected final int DIST_HASH_MAGIC;
	
	// Weather
	
	protected float prevRainingStrength;
	protected float rainingStrength;
	protected float prevSnowingStrength; 
	protected float snowingStrength; 	
	protected float prevThunderingStrength;
	protected float thunderingStrength;
	protected float prevSandstormingStrength;
	protected float sandstormingStrength;
	protected int lastLightningBolt = 0;
	public int lightningFlash = 0;
	public int lightningChance = 50000;
	
	public boolean editingBlocks = false;
	protected int autosavePeriod = 40;
	public int difficultySetting;
	public Random rand = new Random();
	public boolean isNewWorld = false;
	public final WorldProvider worldProvider;
	public List<IWorldAccess> worldAccesses = new ArrayList<IWorldAccess>();
	protected IChunkProvider chunkProvider;
	protected final ISaveHandler saveHandler;
	public WorldInfo worldInfo;
	public boolean findingSpawnPoint;
	private boolean allPlayersSleeping;
	public MapStorage mapStorage;
	private ArrayList<AxisAlignedBB> collidingBoundingBoxes = new ArrayList<AxisAlignedBB>();
	private boolean scanningTileEntities;
	protected boolean spawnHostileMobs = true;
	protected boolean spawnPeacefulMobs = true;
	protected Set<ChunkCoordIntPair> activeChunkSet = new HashSet<ChunkCoordIntPair>();
	private int ambientTickCountdown = this.rand.nextInt(12000);
	private List<Entity> entitiesWithinAABBExcludingEntity = new ArrayList<Entity>();
	public boolean isRemote = false;
	
	// Starlight
	public final StarlightEngine blockLight = new StarlightEngine(false, this);
	public final StarlightEngine skyLight = new StarlightEngine(true, this);
	
	public static int notifys = 0;

	public BiomeGenBase getBiomeGenForCoords(int x, int z) {
		if(this.blockExists(x, 0, z)) {
			Chunk chunk = this.getChunkFromBlockCoords(x, z);
			if(chunk != null) {
				return chunk.getBiomeForCoords(x & 15, z & 15, this.worldProvider.worldChunkMgr);
			}
		}

		return this.worldProvider.worldChunkMgr.getBiomeGenAt(x, z);
	}

	public WorldChunkManager getWorldChunkManager() {
		return this.worldProvider.worldChunkMgr;
	}

	public World(ISaveHandler iSaveHandler1, String string2, WorldProvider worldProvider3, WorldSettings worldSettings4) {
		this.scheduledUpdatesAreImmediate = false;
		this.loadedEntityList = new ArrayList<Entity>();
		this.unloadedEntityList = new ArrayList<Entity>();
		this.scheduledTickTreeSet = new TreeSet<NextTickListEntry>();
		this.scheduledTickSet = new HashSet<NextTickListEntry>();
		this.loadedTileEntityList = new ArrayList<TileEntity>();
		this.addedTileEntityList = new ArrayList<TileEntity>();
		this.entityRemoval = new ArrayList<TileEntity>();
		this.playerEntities = new ArrayList<EntityPlayer>();
		this.weatherEffects = new ArrayList<Entity>();
		this.cloudColour = 16777215L;
		this.skylightSubtracted = 0;
		this.updateLCG = (new Random()).nextInt();
		this.DIST_HASH_MAGIC = 1013904223;
		this.lastLightningBolt = 0;
		this.lightningFlash = 0;
		this.editingBlocks = false;
		System.currentTimeMillis();
		this.autosavePeriod = 40;
		this.rand = new Random();
		this.isNewWorld = false;
		this.worldAccesses = new ArrayList<IWorldAccess>();
		this.collidingBoundingBoxes = new ArrayList<AxisAlignedBB>();
		this.spawnHostileMobs = true;
		this.spawnPeacefulMobs = true;
		this.activeChunkSet = new HashSet<ChunkCoordIntPair>();
		this.ambientTickCountdown = this.rand.nextInt(12000);
		this.entitiesWithinAABBExcludingEntity = new ArrayList<Entity>();
		this.isRemote = false;
		this.saveHandler = iSaveHandler1;
		this.worldInfo = new WorldInfo(worldSettings4, string2);
		this.worldProvider = worldProvider3;
		this.mapStorage = new MapStorage(iSaveHandler1);
		worldProvider3.registerWorld(this);
		this.chunkProvider = this.createChunkProvider();
		this.calculateInitialSkylight();
		this.calculateInitialWeather();
		Seasons.seasonsAreOn(this.worldInfo.isEnableSeasons());
	}
	
	// This is the constructor that gets called when changing dimensions.
	// It should have the correct world provider.
	public World(World world1, WorldProvider worldProvider2) {
		this.scheduledUpdatesAreImmediate = false;
		this.loadedEntityList = new ArrayList<Entity>();
		this.unloadedEntityList = new ArrayList<Entity>();
		this.scheduledTickTreeSet = new TreeSet<NextTickListEntry>();
		this.scheduledTickSet = new HashSet<NextTickListEntry>();
		this.loadedTileEntityList = new ArrayList<TileEntity>();
		this.addedTileEntityList = new ArrayList<TileEntity>();
		this.entityRemoval = new ArrayList<TileEntity>();
		this.playerEntities = new ArrayList<EntityPlayer>();
		this.weatherEffects = new ArrayList<Entity>();
		this.cloudColour = 16777215L;
		this.skylightSubtracted = 0;
		this.updateLCG = (new Random()).nextInt();
		this.DIST_HASH_MAGIC = 1013904223;
		this.lastLightningBolt = 0;
		this.lightningFlash = 0;
		this.editingBlocks = false;
		this.autosavePeriod = 40;
		this.rand = new Random();
		this.isNewWorld = false;
		this.worldAccesses = new ArrayList<IWorldAccess>();
		this.collidingBoundingBoxes = new ArrayList<AxisAlignedBB>();
		this.spawnHostileMobs = true;
		this.spawnPeacefulMobs = true;
		this.activeChunkSet = new HashSet<ChunkCoordIntPair>();
		this.ambientTickCountdown = this.rand.nextInt(12000);
		this.entitiesWithinAABBExcludingEntity = new ArrayList<Entity>();
		this.isRemote = false;
		this.saveHandler = world1.saveHandler;
		this.worldInfo = new WorldInfo(world1.worldInfo);
		this.mapStorage = new MapStorage(this.saveHandler);
		this.worldProvider = worldProvider2;
		worldProvider2.registerWorld(this);
		this.chunkProvider = this.createChunkProvider();
		
		this.calculateInitialSkylight();
		this.calculateInitialWeather();
		Seasons.seasonsAreOn(this.worldInfo.isEnableSeasons());
	}

	public World(ISaveHandler saveHandler, String displayName, WorldSettings worldSettings) {
		this(saveHandler, displayName, (WorldSettings)worldSettings, (WorldProvider)null);
	}

	public World(ISaveHandler saveHandler, String displayName, WorldSettings worldSettings, WorldProvider worldProvider) {
		this.scheduledUpdatesAreImmediate = false;
		this.loadedEntityList = new ArrayList<Entity>();
		this.unloadedEntityList = new ArrayList<Entity>();
		this.scheduledTickTreeSet = new TreeSet<NextTickListEntry>();
		this.scheduledTickSet = new HashSet<NextTickListEntry>();
		this.loadedTileEntityList = new ArrayList<TileEntity>();
		this.addedTileEntityList = new ArrayList<TileEntity>();
		this.entityRemoval = new ArrayList<TileEntity>();
		this.playerEntities = new ArrayList<EntityPlayer>();
		this.weatherEffects = new ArrayList<Entity>();
		this.cloudColour = 16777215L;
		this.skylightSubtracted = 0;
		this.updateLCG = (new Random()).nextInt();
		this.DIST_HASH_MAGIC = 1013904223;
		this.lastLightningBolt = 0;
		this.lightningFlash = 0;
		this.editingBlocks = false;
		System.currentTimeMillis();
		this.autosavePeriod = 40;
		this.rand = new Random();
		this.isNewWorld = false;
		this.worldAccesses = new ArrayList<IWorldAccess>();
		this.collidingBoundingBoxes = new ArrayList<AxisAlignedBB>();
		this.spawnHostileMobs = true;
		this.spawnPeacefulMobs = true;
		this.activeChunkSet = new HashSet<ChunkCoordIntPair>();
		this.ambientTickCountdown = this.rand.nextInt(12000);
		this.entitiesWithinAABBExcludingEntity = new ArrayList<Entity>();
		this.isRemote = false;
		
		this.saveHandler = saveHandler;
		this.mapStorage = new MapStorage(saveHandler);
		
		Seasons.dayOfTheYear = -1;
		
		this.worldInfo = saveHandler.loadWorldInfo();

		//System.out.println ("WorldInfo: " + this.worldInfo);	
		this.isNewWorld = this.worldInfo == null;
		
		boolean z5 = false;
		
		if(worldProvider != null) {
			this.worldProvider = worldProvider;
			
			if(this.worldInfo == null) {
				this.worldInfo = new WorldInfo(worldSettings, displayName);
				z5 = true;
			} 
		} else if(this.worldInfo != null && this.worldInfo.getDimension() != 0) {
			// When loading a new world, worldSettings & worldProvider should me null
			// But worldInfo SHOULDN'T be null! Hence when loading a level from disk this is executed:
			System.out.println ("Loading with worldInfo " + this.worldInfo);
			this.worldProvider = WorldProvider.getProviderForDimension(this.worldInfo.getDimension(), worldInfo.getTerrainType());
		} else {
			if(this.worldInfo == null) {
				this.worldInfo = new WorldInfo(worldSettings, displayName);
				z5 = true;
			} 
			
			this.worldProvider = WorldProvider.getProviderForDimension(0, worldInfo.getTerrainType());
		}

		this.worldInfo.setWorldName(displayName);

		this.worldProvider.registerWorld(this);
		this.chunkProvider = this.createChunkProvider();
		if(z5) {
			this.initializeWeather();
			this.generateSpawnPoint();
		}


		if(Seasons.dayOfTheYear < 0) {
			Seasons.dayOfTheYear = this.rand.nextInt(Seasons.SEASON_DURATION) + Seasons.SEASON_DURATION + (Seasons.SEASON_DURATION >> 1);
		}
		Seasons.updateSeasonCounters();
		Seasons.seasonsAreOn(this.worldInfo.isEnableSeasons());
		
		this.calculateInitialSkylight();
		this.calculateInitialWeather();
	}

	protected IChunkProvider createChunkProvider() {
		IChunkLoader iChunkLoader1 = this.saveHandler.getChunkLoader(this.worldProvider);
		return new ChunkProvider(this, iChunkLoader1, this.worldProvider.getChunkProvider());
	}

	protected void generateSpawnPoint() {
		Random random3 = new Random(this.getSeed());
		
		if(!this.worldProvider.canRespawnHere()) {
			this.worldInfo.setSpawnPosition(0, this.worldProvider.getAverageGroundLevel(), 0);
		} else {
			this.worldProvider.generateSpawnPoint(this, random3);
			this.findingSpawnPoint = false;
		}
	}

	public ChunkCoordinates getEntrancePortalLocation() {
		return this.worldProvider.getEntrancePortalLocation();
	}

	public void setSpawnLocation() {
		if(this.worldInfo.getSpawnY() <= 0) {
			this.worldInfo.setSpawnY(64);
		}

		int i1 = this.worldInfo.getSpawnX();
		int i2 = this.worldInfo.getSpawnZ();
		int i3 = 0;

		while(this.getFirstUncoveredBlock(i1, i2) == 0) {
			i1 += this.rand.nextInt(8) - this.rand.nextInt(8);
			i2 += this.rand.nextInt(8) - this.rand.nextInt(8);
			++i3;
			if(i3 == 10000) {
				break;
			}
		}

		this.worldInfo.setSpawnX(i1);
		this.worldInfo.setSpawnZ(i2);
	}

	public int getFirstUncoveredBlock(int i1, int i2) {
		int i3;
		for(i3 = 63; !this.isAirBlock(i1, i3 + 1, i2); ++i3) {
		}

		return this.getBlockId(i1, i3, i2);
	}

	public void func_6464_c() {
	}

	public void spawnPlayerWithLoadedChunks(EntityPlayer entityPlayer1) {
		try {
			NBTTagCompound nBTTagCompound2 = this.worldInfo.getPlayerNBTTagCompound();
			if(nBTTagCompound2 != null) {
				entityPlayer1.readFromNBT(nBTTagCompound2);
				this.worldInfo.setPlayerNBTTagCompound((NBTTagCompound)null);
			}

			this.spawnEntityInWorld(entityPlayer1);
		} catch (Exception exception6) {
			exception6.printStackTrace();
		}

	}

	public void saveWorld(boolean z1, IProgressUpdate iProgressUpdate2) {
		if(this.chunkProvider.canSave()) {
			if(iProgressUpdate2 != null) {
				iProgressUpdate2.displaySavingString("Saving level");
			}

			this.saveLevel();
			if(iProgressUpdate2 != null) {
				iProgressUpdate2.displayLoadingString("Saving chunks");
			}

			this.chunkProvider.saveChunks(z1, iProgressUpdate2);
		}
	}

	private void saveLevel() {
		this.checkSessionLock();
		this.saveHandler.saveWorldInfoAndPlayer(this.worldInfo, this.playerEntities);
		this.mapStorage.saveAllData();
	}

	public boolean quickSaveWorld(int i1) {
		if(!this.chunkProvider.canSave()) {
			return true;
		} else {
			if(i1 == 0) {
				this.saveLevel();
			}

			return this.chunkProvider.saveChunks(false, (IProgressUpdate)null);
		}
	}
	
	public int getLandSurfaceHeight(int x, int z) {
		Chunk chunk = null;
		if(this.chunkExists(x >> 4, z >> 4)) {
			chunk = this.getChunkFromChunkCoords(x >> 4, z >> 4);
		} else {
			chunk = this.generateChunk(x >> 4, z >> 4);
		}
		return chunk.getLandSurfaceHeightValue(x & 15, z & 15);
	}

	public int getBlockId(int x, int y, int z) {
		return (y < 0 || y >= 256) ? 0 : this.getChunkFromChunkCoords(x >> 4, z >> 4).getBlockID(x & 15, y, z & 15);
	}

	public Block getBlock(int x, int y, int z) {
		return (y < 0 || y >= 256) ? null : Block.blocksList[this.getChunkFromChunkCoords(x >> 4, z >> 4).getBlockID(x & 15, y, z & 15)];
	}

	public int getBlockLightOpacity(int i1, int i2, int i3) {
		return i2 < 0 ? 0 : (i2 >= 256 ? 0 : this.getChunkFromChunkCoords(i1 >> 4, i3 >> 4).getBlockLightOpacity(i1 & 15, i2, i3 & 15));
	}

	public boolean isAirBlock(int i1, int i2, int i3) {
		return this.getBlockId(i1, i2, i3) == 0;
	}
	

	public boolean isAirBlock(ChunkPosition pos) {
		return this.isAirBlock(pos.x, pos.y, pos.z);
	}

	public boolean hasTileEntity(int i1, int i2, int i3) {
		int i4 = this.getBlockId(i1, i2, i3);
		return Block.blocksList[i4] != null && Block.blocksList[i4].hasTileEntity();
	}

	public boolean blockExists(int x, int y, int z) {
		return y >= 0 && y < 256 ? this.chunkExists(x >> 4, z >> 4) : false;
	}

	public boolean doChunksNearChunkExist(int x, int y, int z, int r) {
		return this.checkChunksExist(x - r, y - r, z - r, x + r, y + r, z + r);
	}

	public boolean checkChunksExist(int x1, int y1, int z1, int x2, int y2, int z2) {
		if(y2 >= 0 && y1 < 256) {
			x1 >>= 4;
			z1 >>= 4;
			x2 >>= 4;
			z2 >>= 4;

			for(int x = x1; x <= x2; ++x) {
				for(int z = z1; z <= z2; ++z) {
					if(!this.chunkExists(x, z)) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean chunkExists(int x, int z) {
		return this.chunkProvider.chunkExists(x, z);
	}

	public Chunk getChunkFromBlockCoords(int x, int z) {
		return this.getChunkFromChunkCoords(x >> 4, z >> 4);
	}

	public Chunk getChunkFromChunkCoords(int x, int z) {
		return this.chunkProvider.provideChunk(x, z);
	}
	
	/*
	 * Just generate the chunk, don't populate. Useful to get terrain height for instance
	 */
	public Chunk generateChunk(int posX, int posZ) {
		return this.chunkProvider.justGenerate(posX, posZ);
	}

	public boolean setBlockAndMetadata(int x, int y, int z, int blockID, int metadata) {
		if(y < 0 || y >= 256) {
			return false;
		} else {
			return this.getChunkFromChunkCoords(x >> 4, z >> 4).setBlockIDWithMetadata(x & 15, y, z & 15, blockID, metadata);
		}
	
	}
	
	public boolean setBlockAndMetadata(int x, int y, int z, Block block, int metadata) {	
		if(y < 0 || y >= 256) {
			return false;
		} else {
			return this.getChunkFromChunkCoords(x >> 4, z >> 4).setBlockIDWithMetadata(x & 15, y, z & 15, block == null ? 0 : block.blockID, metadata);
		}
	
	}

	public boolean setBlock(int x, int y, int z, int blockID) {
		if(y < 0 || y >= 256) {
			return false;
		} else {
			return this.getChunkFromChunkCoords(x >> 4, z >> 4).setBlockID(x & 15, y, z & 15, blockID);
		}

	}

	public boolean setBlock(int x, int y, int z, Block block) {
		if(y < 0 || y >= 256) {
			return false;
		} else {
			return this.getChunkFromChunkCoords(x >> 4, z >> 4).setBlockID(x & 15, y, z & 15, block == null ? 0 : block.blockID);
		}
		
	}

	public void clearBlockNoLights(int x, int y, int z) {
		if(y >= 0 && y < 256) {
			Chunk chunk5 = this.getChunkFromChunkCoords(x >> 4, z >> 4);
			chunk5.clearBlockNoLights(x & 15, y, z & 15);
		}
	}
	
	public Material getBlockMaterial(int x, int y, int z) {
		int blockID = this.getBlockId(x, y, z);
		return blockID == 0 ? Material.air : Block.blocksList[blockID].blockMaterial;
	}

	public int getBlockMetadata(int x, int y, int z) {
		if(y < 0 || y >= 256) {
			return 0;
		} else {
			return this.getChunkFromChunkCoords(x >> 4, z >> 4).getBlockMetadata(x & 15, y, z & 15);
		}

	}

	public void setBlockMetadataWithNotify(int x, int y, int z, int meta) {
		++notifys;
		if(this.setBlockMetadata(x, y, z, meta)) {
			int blockID = this.getBlockId(x, y, z);
			if(Block.requiresSelfNotify[blockID & 4095]) {
				this.notifyBlockChange(x, y, z, blockID);
			} else {
				this.notifyBlocksOfNeighborChange(x, y, z, blockID);
			}
		}

	}

	public boolean setBlockMetadata(int x, int y, int z, int meta) {
		if(y < 0 || y >= 256) {
			return false;
		} else {
			return this.getChunkFromChunkCoords(x >> 4, z >> 4).setBlockMetadata(x & 15, y, z & 15, meta);
		}
	}

	public boolean setBlockWithNotify(int x, int y, int z, int blockID) {
		++ notifys;
		if(this.setBlock(x, y, z, blockID)) {
			this.notifyBlockChange(x, y, z, blockID);
			return true;
		} else {
			return false;
		}
		
	}

	public boolean setBlockWithNotify(int x, int y, int z, Block block) {
		++ notifys;
		int blockID = block == null ? 0 : block.blockID;
		if(this.setBlock(x, y, z, blockID)) {
			this.notifyBlockChange(x, y, z, blockID);
			return true;
		} else {
			return false;
		}
		
	}

	public boolean setBlockAndMetadataWithNotify(int x, int y, int z, int blockID, int metadata) {
		++ notifys;
		if(this.setBlockAndMetadata(x, y, z, blockID, metadata)) {
			this.notifyBlockChange(x, y, z, blockID);
			return true;
		} else {
			return false;
		}
		
	}

	public boolean setBlockAndMetadataWithNotify(int x, int y, int z, Block block, int metadata) {
		++ notifys;
		int blockID = block == null ? 0 : block.blockID;
		if(this.setBlockAndMetadata(x, y, z, blockID, metadata)) {
			this.notifyBlockChange(x, y, z, blockID);
			return true;
		} else {
			return false;
		}
		
	}

	public void markBlockNeedsUpdate(int i1, int i2, int i3) {
		for(int i4 = 0; i4 < this.worldAccesses.size(); ++i4) {
			((IWorldAccess)this.worldAccesses.get(i4)).markBlockNeedsUpdate(i1, i2, i3);
		}

	}

	public void notifyBlockChange(int i1, int i2, int i3, int i4) {
		this.markBlockNeedsUpdate(i1, i2, i3);
		this.notifyBlocksOfNeighborChange(i1, i2, i3, i4);
	}

	public void markBlocksDirtyVertical(int i1, int i2, int i3, int i4) {
		int i5;
		if(i3 > i4) {
			i5 = i4;
			i4 = i3;
			i3 = i5;
		}

		this.markBlocksDirty(i1, i3, i2, i1, i4, i2);
	}

	public void markBlockAsNeedsUpdate(int i1, int i2, int i3) {
		for(int i4 = 0; i4 < this.worldAccesses.size(); ++i4) {
			((IWorldAccess)this.worldAccesses.get(i4)).markBlockRangeNeedsUpdate(i1, i2, i3, i1, i2, i3);
		}

	}

	public void markBlocksDirty(int i1, int i2, int i3, int i4, int i5, int i6) {
		for(int i7 = 0; i7 < this.worldAccesses.size(); ++i7) {
			((IWorldAccess)this.worldAccesses.get(i7)).markBlockRangeNeedsUpdate(i1, i2, i3, i4, i5, i6);
		}

	}

	public void notifyBlocksOfNeighborChange(int i1, int i2, int i3, int i4) {
		this.notifyBlockOfNeighborChange(i1 - 1, i2, i3, i4);
		this.notifyBlockOfNeighborChange(i1 + 1, i2, i3, i4);
		this.notifyBlockOfNeighborChange(i1, i2 - 1, i3, i4);
		this.notifyBlockOfNeighborChange(i1, i2 + 1, i3, i4);
		this.notifyBlockOfNeighborChange(i1, i2, i3 - 1, i4);
		this.notifyBlockOfNeighborChange(i1, i2, i3 + 1, i4);
	}

	public void notifyBlockOfNeighborChange(int i1, int i2, int i3, int i4) {
		if(!this.editingBlocks && !this.isRemote) {
			Block block5 = Block.blocksList[this.getBlockId(i1, i2, i3)];
			if(block5 != null) {
				block5.onNeighborBlockChange(this, i1, i2, i3, i4);
			}

		}
	}

	public boolean canBlockSeeTheSky(int i1, int i2, int i3) {
		return this.getChunkFromChunkCoords(i1 >> 4, i3 >> 4).canBlockSeeTheSky(i1 & 15, i2, i3 & 15);
	}

	public int getFullBlockLightValue(int i1, int i2, int i3) {
		if(i2 < 0) {
			return 0;
		} else {
			if(i2 >= 256) {
				i2 = 255;
			}

			return this.getChunkFromChunkCoords(i1 >> 4, i3 >> 4).getBlockLightValue(i1 & 15, i2, i3 & 15, 0);
		}
	}

	public int getBlockLightValue(int i1, int i2, int i3) {
		return this.getBlockLightValue_do(i1, i2, i3, true);
	}
	
	public boolean isBlockUnderground(int x, int y, int z) {
		if(y < 0 || y > 255) return false;
		return this.getChunkFromChunkCoords(x >> 4, z >> 4).isBlockUnderground(x & 15, y, z & 15);
	}

	public int getBlockLightValue_do(int i1, int i2, int i3, boolean z4) {
		if(i1 >= -30000000 && i3 >= -30000000 && i1 < 30000000 && i3 < 30000000) {
			if(z4) {
				int i5 = this.getBlockId(i1, i2, i3);
				if(i5 == Block.stairSingle.blockID || i5 == Block.tilledField.blockID || i5 == Block.stairCompactCobblestone.blockID || i5 == Block.stairCompactPlanks.blockID) {
					int i6 = this.getBlockLightValue_do(i1, i2 + 1, i3, false);
					int i7 = this.getBlockLightValue_do(i1 + 1, i2, i3, false);
					int i8 = this.getBlockLightValue_do(i1 - 1, i2, i3, false);
					int i9 = this.getBlockLightValue_do(i1, i2, i3 + 1, false);
					int i10 = this.getBlockLightValue_do(i1, i2, i3 - 1, false);
					if(i7 > i6) {
						i6 = i7;
					}

					if(i8 > i6) {
						i6 = i8;
					}

					if(i9 > i6) {
						i6 = i9;
					}

					if(i10 > i6) {
						i6 = i10;
					}

					return i6;
				}
			}

			if(i2 < 0) {
				return 0;
			} else {
				if(i2 >= 256) {
					i2 = 255;
				}

				Chunk chunk11 = this.getChunkFromChunkCoords(i1 >> 4, i3 >> 4);
				i1 &= 15;
				i3 &= 15;
				return chunk11.getBlockLightValue(i1, i2, i3, this.skylightSubtracted);
			}
		} else {
			return 15;
		}
	}

	public int getHeightValue(int i1, int i2) {
		if(!this.chunkExists(i1 >> 4, i2 >> 4)) {
			return 0;
		} else {
			Chunk chunk3 = this.getChunkFromChunkCoords(i1 >> 4, i2 >> 4);
			return chunk3.getHeightValue(i1 & 15, i2 & 15);
		}
	}
	
	public int getLandSurfaceHeightValue(int blockX, int blockZ) {
		Chunk chunk3 = null;
		if(this.chunkExists(blockX >> 4, blockZ >> 4)) { 
			chunk3 = this.getChunkFromChunkCoords(blockX >> 4, blockZ >> 4);
		} else { 
			chunk3 = this.chunkProvider.justGenerate(blockX >> 4, blockZ >> 4);
		}
		return chunk3.getLandSurfaceHeightValue(blockX & 15, blockZ & 15);
	}
	
	public int getHeightValueUnderWater (int x, int z) {
		// Start here
		int y = getHeightValue (x, z);
		
		while (y > 8) {
			y --; int blockID = getBlockId (x, y, z);
			if (blockID != 0 && blockID != Block.waterStill.blockID) break;
		}
		
		return y;
	}
	
	public int getHeightValueForce(int i1, int i2) {
		Chunk chunk3 = this.getChunkFromChunkCoords(i1 >> 4, i2 >> 4);
		return chunk3.getHeightValue(i1 & 15, i2 & 15);
	}

	public int getSkyBlockTypeBrightness(EnumSkyBlock enumSkyBlock1, int i2, int i3, int i4) {
		if(this.worldProvider.hasNoSky && !this.worldProvider.isCaveWorld && enumSkyBlock1 == EnumSkyBlock.Sky) {
			return 0;
		} else {
			if(i3 < 0) {
				i3 = 0;
			}

			if(i3 >= 256) {
				return enumSkyBlock1.defaultLightValue;
			} else if(i2 >= -30000000 && i4 >= -30000000 && i2 < 30000000 && i4 < 30000000) {
				int i5 = i2 >> 4;
				int i6 = i4 >> 4;
				if(!this.chunkExists(i5, i6)) {
					return enumSkyBlock1.defaultLightValue;
				} else if(Block.useNeighborBrightness[this.getBlockId(i2, i3, i4)]) {
					int i12 = this.getSavedLightValue(enumSkyBlock1, i2, i3 + 1, i4);
					int i8 = this.getSavedLightValue(enumSkyBlock1, i2 + 1, i3, i4);
					int i9 = this.getSavedLightValue(enumSkyBlock1, i2 - 1, i3, i4);
					int i10 = this.getSavedLightValue(enumSkyBlock1, i2, i3, i4 + 1);
					int i11 = this.getSavedLightValue(enumSkyBlock1, i2, i3, i4 - 1);
					if(i8 > i12) {
						i12 = i8;
					}

					if(i9 > i12) {
						i12 = i9;
					}

					if(i10 > i12) {
						i12 = i10;
					}

					if(i11 > i12) {
						i12 = i11;
					}

					return i12;
				} else {
					Chunk chunk7 = this.getChunkFromChunkCoords(i5, i6);
					return chunk7.getSavedLightValue(enumSkyBlock1, i2 & 15, i3, i4 & 15);
				}
			} else {
				return enumSkyBlock1.defaultLightValue;
			}
		}
	}

	public int getSavedLightValue(EnumSkyBlock enumSkyBlock1, int i2, int i3, int i4) {
		if(i3 < 0) {
			i3 = 0;
		}

		if(i3 >= 256) {
			i3 = 255;
		}

		if(i2 >= -30000000 && i4 >= -30000000 && i2 < 30000000 && i4 < 30000000) {
			int i5 = i2 >> 4;
			int i6 = i4 >> 4;
			if(!this.chunkExists(i5, i6)) {
				return enumSkyBlock1.defaultLightValue;
			} else {
				Chunk chunk7 = this.getChunkFromChunkCoords(i5, i6);
				return chunk7.getSavedLightValue(enumSkyBlock1, i2 & 15, i3, i4 & 15);
			}
		} else {
			return enumSkyBlock1.defaultLightValue;
		}
	}

	public void setLightValue(EnumSkyBlock enumSkyBlock1, int i2, int i3, int i4, int i5) {
		if(i2 >= -30000000 && i4 >= -30000000 && i2 < 30000000 && i4 < 30000000) {
			if(i3 >= 0) {
				if(i3 < 256) {
					if(this.chunkExists(i2 >> 4, i4 >> 4)) {
						Chunk chunk6 = this.getChunkFromChunkCoords(i2 >> 4, i4 >> 4);
						chunk6.setLightValue(enumSkyBlock1, i2 & 15, i3, i4 & 15, i5);

						for(int i7 = 0; i7 < this.worldAccesses.size(); ++i7) {
							((IWorldAccess)this.worldAccesses.get(i7)).markBlockNeedsUpdate(i2, i3, i4);
						}

					}
				}
			}
		}
	}

	public int getLightBrightnessForSkyBlocks(int i1, int i2, int i3, int i4) {
		int i5 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, i1, i2, i3);
		int i6 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Block, i1, i2, i3);
		if(i6 < i4) {
			i6 = i4;
		}

		return i5 << 20 | i6 << 4;
	}

	public float getBrightness(int i1, int i2, int i3, int i4) {
		int i5 = this.getBlockLightValue(i1, i2, i3);
		if(i5 < i4) {
			i5 = i4;
		}

		return this.worldProvider.lightBrightnessTable[i5];
	}

	public float getLightBrightness(int i1, int i2, int i3) {
		return this.worldProvider.lightBrightnessTable[this.getBlockLightValue(i1, i2, i3)];
	}

	public boolean isDaytime() {
		return this.skylightSubtracted < 4;
	}

	public MovingObjectPosition rayTraceBlocks(Vec3D vec3D1, Vec3D vec3D2) {
		return this.rayTraceBlocks_do_do(vec3D1, vec3D2, false, false);
	}

	public MovingObjectPosition rayTraceBlocks_do(Vec3D vec3D1, Vec3D vec3D2, boolean z3) {
		return this.rayTraceBlocks_do_do(vec3D1, vec3D2, z3, false);
	}

	public MovingObjectPosition rayTraceBlocks_do_do(Vec3D vec3D1, Vec3D vec3D2, boolean z3, boolean z4) {
		if(!Double.isNaN(vec3D1.xCoord) && !Double.isNaN(vec3D1.yCoord) && !Double.isNaN(vec3D1.zCoord)) {
			if(!Double.isNaN(vec3D2.xCoord) && !Double.isNaN(vec3D2.yCoord) && !Double.isNaN(vec3D2.zCoord)) {
				int i5 = MathHelper.floor_double(vec3D2.xCoord);
				int i6 = MathHelper.floor_double(vec3D2.yCoord);
				int i7 = MathHelper.floor_double(vec3D2.zCoord);
				int i8 = MathHelper.floor_double(vec3D1.xCoord);
				int i9 = MathHelper.floor_double(vec3D1.yCoord);
				int i10 = MathHelper.floor_double(vec3D1.zCoord);
				int i11 = this.getBlockId(i8, i9, i10);
				int i12 = this.getBlockMetadata(i8, i9, i10);
				Block block13 = Block.blocksList[i11];
				if((!z4 || block13 == null || block13.getCollisionBoundingBoxFromPool(this, i8, i9, i10) != null) && i11 > 0 && block13.canCollideCheck(i12, z3)) {
					MovingObjectPosition movingObjectPosition14 = block13.collisionRayTrace(this, i8, i9, i10, vec3D1, vec3D2);
					if(movingObjectPosition14 != null) {
						return movingObjectPosition14;
					}
				}

				i11 = 200;

				while(i11-- >= 0) {
					if(Double.isNaN(vec3D1.xCoord) || Double.isNaN(vec3D1.yCoord) || Double.isNaN(vec3D1.zCoord)) {
						return null;
					}

					if(i8 == i5 && i9 == i6 && i10 == i7) {
						return null;
					}

					boolean z39 = true;
					boolean z40 = true;
					boolean z41 = true;
					double d15 = 999.0D;
					double d17 = 999.0D;
					double d19 = 999.0D;
					if(i5 > i8) {
						d15 = (double)i8 + 1.0D;
					} else if(i5 < i8) {
						d15 = (double)i8 + 0.0D;
					} else {
						z39 = false;
					}

					if(i6 > i9) {
						d17 = (double)i9 + 1.0D;
					} else if(i6 < i9) {
						d17 = (double)i9 + 0.0D;
					} else {
						z40 = false;
					}

					if(i7 > i10) {
						d19 = (double)i10 + 1.0D;
					} else if(i7 < i10) {
						d19 = (double)i10 + 0.0D;
					} else {
						z41 = false;
					}

					double d21 = 999.0D;
					double d23 = 999.0D;
					double d25 = 999.0D;
					double d27 = vec3D2.xCoord - vec3D1.xCoord;
					double d29 = vec3D2.yCoord - vec3D1.yCoord;
					double d31 = vec3D2.zCoord - vec3D1.zCoord;
					if(z39) {
						d21 = (d15 - vec3D1.xCoord) / d27;
					}

					if(z40) {
						d23 = (d17 - vec3D1.yCoord) / d29;
					}

					if(z41) {
						d25 = (d19 - vec3D1.zCoord) / d31;
					}

					byte b42;
					if(d21 < d23 && d21 < d25) {
						if(i5 > i8) {
							b42 = 4;
						} else {
							b42 = 5;
						}

						vec3D1.xCoord = d15;
						vec3D1.yCoord += d29 * d21;
						vec3D1.zCoord += d31 * d21;
					} else if(d23 < d25) {
						if(i6 > i9) {
							b42 = 0;
						} else {
							b42 = 1;
						}

						vec3D1.xCoord += d27 * d23;
						vec3D1.yCoord = d17;
						vec3D1.zCoord += d31 * d23;
					} else {
						if(i7 > i10) {
							b42 = 2;
						} else {
							b42 = 3;
						}

						vec3D1.xCoord += d27 * d25;
						vec3D1.yCoord += d29 * d25;
						vec3D1.zCoord = d19;
					}

					Vec3D vec3D34 = Vec3D.createVector(vec3D1.xCoord, vec3D1.yCoord, vec3D1.zCoord);
					i8 = (int)(vec3D34.xCoord = (double)MathHelper.floor_double(vec3D1.xCoord));
					if(b42 == 5) {
						--i8;
						++vec3D34.xCoord;
					}

					i9 = (int)(vec3D34.yCoord = (double)MathHelper.floor_double(vec3D1.yCoord));
					if(b42 == 1) {
						--i9;
						++vec3D34.yCoord;
					}

					i10 = (int)(vec3D34.zCoord = (double)MathHelper.floor_double(vec3D1.zCoord));
					if(b42 == 3) {
						--i10;
						++vec3D34.zCoord;
					}

					int i35 = this.getBlockId(i8, i9, i10);
					int i36 = this.getBlockMetadata(i8, i9, i10);
					Block block37 = Block.blocksList[i35];
					if((!z4 || block37 == null || block37.getCollisionBoundingBoxFromPool(this, i8, i9, i10) != null) && i35 > 0 && block37.canCollideCheck(i36, z3)) {
						MovingObjectPosition movingObjectPosition38 = block37.collisionRayTrace(this, i8, i9, i10, vec3D1, vec3D2);
						if(movingObjectPosition38 != null) {
							return movingObjectPosition38;
						}
					}
				}

				return null;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public void playSoundAtEntity(Entity entity1, String string2, float f3, float f4) {
		for(int i5 = 0; i5 < this.worldAccesses.size(); ++i5) {
			((IWorldAccess)this.worldAccesses.get(i5)).playSound(string2, entity1.posX, entity1.posY - (double)entity1.yOffset, entity1.posZ, f3, f4);
		}

	}

	public void playSoundEffect(double d1, double d3, double d5, String string7, float f8, float f9) {
		for(int i10 = 0; i10 < this.worldAccesses.size(); ++i10) {
			((IWorldAccess)this.worldAccesses.get(i10)).playSound(string7, d1, d3, d5, f8, f9);
		}

	}

	public void playRecord(String string1, int i2, int i3, int i4) {
		for(int i5 = 0; i5 < this.worldAccesses.size(); ++i5) {
			((IWorldAccess)this.worldAccesses.get(i5)).playRecord(string1, i2, i3, i4);
		}

	}

	public void spawnParticle(String string1, double d2, double d4, double d6, double d8, double d10, double d12) {
		for(int i14 = 0; i14 < this.worldAccesses.size(); ++i14) {
			((IWorldAccess)this.worldAccesses.get(i14)).spawnParticle(string1, d2, d4, d6, d8, d10, d12);
		}

	}

	public boolean addWeatherEffect(Entity entity1) {
		this.weatherEffects.add(entity1);
		return true;
	}

	public boolean spawnEntityInWorld(Entity entity1) {
		
		int i2 = MathHelper.floor_double(entity1.posX / 16.0D);
		int i3 = MathHelper.floor_double(entity1.posZ / 16.0D);
		boolean z4 = false;
		if(entity1 instanceof EntityPlayer) {
			z4 = true;
		}

		if(!z4 && !this.chunkExists(i2, i3)) {
			return false;
		} else {
			if(entity1 instanceof EntityPlayer) {
				EntityPlayer entityPlayer5 = (EntityPlayer)entity1;
				this.playerEntities.add(entityPlayer5);
				this.updateAllPlayersSleepingFlag();
			}

			this.getChunkFromChunkCoords(i2, i3).addEntity(entity1);
			this.loadedEntityList.add(entity1);
			this.obtainEntitySkin(entity1);
			return true;
		}
	}

	public Entity getEntityById(int id) {
		Iterator<Entity>it = this.loadedEntityList.iterator();
		while(it.hasNext()) {
			Entity e = it.next();
			if(e.entityId == id) return e;
		}
		return null;
	}
	
	protected void obtainEntitySkin(Entity entity1) {
		for(int i2 = 0; i2 < this.worldAccesses.size(); ++i2) {
			((IWorldAccess)this.worldAccesses.get(i2)).obtainEntitySkin(entity1);
		}

	}

	protected void releaseEntitySkin(Entity entity1) {
		for(int i2 = 0; i2 < this.worldAccesses.size(); ++i2) {
			((IWorldAccess)this.worldAccesses.get(i2)).releaseEntitySkin(entity1);
		}

	}

	public void setEntityDead(Entity entity1) {
		if(entity1.riddenByEntity != null) {
			entity1.riddenByEntity.mountEntity((Entity)null);
		}

		if(entity1.ridingEntity != null) {
			entity1.mountEntity((Entity)null);
		}

		entity1.setDead();
		if(entity1 instanceof EntityPlayer) {
			this.playerEntities.remove((EntityPlayer)entity1);
			this.updateAllPlayersSleepingFlag();
		}

	}

	public void removePlayer(Entity entity1) {
		entity1.setDead();
		if(entity1 instanceof EntityPlayer) {
			this.playerEntities.remove((EntityPlayer)entity1);
			this.updateAllPlayersSleepingFlag();
		}

		int i2 = entity1.chunkCoordX;
		int i3 = entity1.chunkCoordZ;
		if(entity1.addedToChunk && this.chunkExists(i2, i3)) {
			this.getChunkFromChunkCoords(i2, i3).removeEntity(entity1);
		}

		this.loadedEntityList.remove(entity1);
		this.releaseEntitySkin(entity1);
	}

	public void addWorldAccess(IWorldAccess iWorldAccess1) {
		this.worldAccesses.add(iWorldAccess1);
	}

	public void removeWorldAccess(IWorldAccess iWorldAccess1) {
		this.worldAccesses.remove(iWorldAccess1);
	}
	
	public IWorldAccess getWorldAccess(int i) {
		return this.worldAccesses.get(i);
	}

	public List<AxisAlignedBB> getCollidingBoundingBoxes(Entity entity1, AxisAlignedBB axisAlignedBB2) {
		this.collidingBoundingBoxes.clear();
		int i3 = MathHelper.floor_double(axisAlignedBB2.minX);
		int i4 = MathHelper.floor_double(axisAlignedBB2.maxX + 1.0D);
		int i5 = MathHelper.floor_double(axisAlignedBB2.minY);
		int i6 = MathHelper.floor_double(axisAlignedBB2.maxY + 1.0D);
		int i7 = MathHelper.floor_double(axisAlignedBB2.minZ);
		int i8 = MathHelper.floor_double(axisAlignedBB2.maxZ + 1.0D);

		for(int i9 = i3; i9 < i4; ++i9) {
			for(int i10 = i7; i10 < i8; ++i10) {
				if(this.blockExists(i9, 64, i10)) {
					for(int i11 = i5 - 1; i11 < i6; ++i11) {
						Block block12 = Block.blocksList[this.getBlockId(i9, i11, i10)];
						if(block12 != null) {
							block12.getCollidingBoundingBoxes(this, i9, i11, i10, axisAlignedBB2, this.collidingBoundingBoxes, entity1);
						}
					}
				}
			}
		}

		double d14 = 0.25D;
		List<Entity> list15 = this.getEntitiesWithinAABBExcludingEntity(entity1, axisAlignedBB2.expand(d14, d14, d14));

		for(int i16 = 0; i16 < list15.size(); ++i16) {
			AxisAlignedBB axisAlignedBB13 = ((Entity)list15.get(i16)).getBoundingBox();
			if(axisAlignedBB13 != null && axisAlignedBB13.intersectsWith(axisAlignedBB2)) {
				this.collidingBoundingBoxes.add(axisAlignedBB13);
			}

			axisAlignedBB13 = entity1.getCollisionBox((Entity)list15.get(i16));
			if(axisAlignedBB13 != null && axisAlignedBB13.intersectsWith(axisAlignedBB2)) {
				this.collidingBoundingBoxes.add(axisAlignedBB13);
			}
		}

		return this.collidingBoundingBoxes;
	}

	public int calculateSkylightSubtracted(float f1) {
		return this.worldProvider.calculateSkylightSubtracted(f1);
	}

	public float getSunBrightness(float f1) {
		return this.worldProvider.getSunBrightness(f1);
	}

	public Vec3D getSkyColor(Entity entity1, float f2) {
		return this.worldProvider.getSkyColor(entity1, f2);
	}

	public Vec3D getSkyColorBottom(Entity entity1, float f2) {
		return this.worldProvider.getSkyColorBottom(entity1, f2);
	}	
	
	public float getCelestialAngle(float f1) {
		return this.worldProvider.calculateCelestialAngle(this.worldInfo.getWorldTime(), f1);
	}

	public int getMoonPhase(float f1) {
		return this.worldProvider.getMoonPhase(this.worldInfo.getWorldTime(), f1);
	}

	public float getCelestialAngleRadians(float f1) {
		float f2 = this.getCelestialAngle(f1);
		return f2 * (float)Math.PI * 2.0F;
	}

	public Vec3D getCloudColor(float f1) {
		float f2 = this.getCelestialAngle(f1);
		float f3 = MathHelper.cos(f2 * (float)Math.PI * 2.0F) * 2.0F + 0.5F;
		if(f3 < 0.0F) {
			f3 = 0.0F;
		}

		if(f3 > 1.0F) {
			f3 = 1.0F;
		}

		float f4 = (float)(this.cloudColour >> 16 & 255L) / 255.0F;
		float f5 = (float)(this.cloudColour >> 8 & 255L) / 255.0F;
		float f6 = (float)(this.cloudColour & 255L) / 255.0F;
		float f7 = this.getRainStrength(f1);
		float f8;
		float f9;
		if(f7 > 0.0F) {
			f8 = (f4 * 0.3F + f5 * 0.59F + f6 * 0.11F) * 0.6F;
			f9 = 1.0F - f7 * 0.95F;
			f4 = f4 * f9 + f8 * (1.0F - f9);
			f5 = f5 * f9 + f8 * (1.0F - f9);
			f6 = f6 * f9 + f8 * (1.0F - f9);
		}

		f4 *= f3 * 0.9F + 0.1F;
		f5 *= f3 * 0.9F + 0.1F;
		f6 *= f3 * 0.85F + 0.15F;
		f8 = this.getWeightedThunderStrength(f1);
		if(f8 > 0.0F) {
			f9 = (f4 * 0.3F + f5 * 0.59F + f6 * 0.11F) * 0.2F;
			float f10 = 1.0F - f8 * 0.95F;
			f4 = f4 * f10 + f9 * (1.0F - f10);
			f5 = f5 * f10 + f9 * (1.0F - f10);
			f6 = f6 * f10 + f9 * (1.0F - f10);
		}

		return Vec3D.createVector((double)f4, (double)f5, (double)f6);
	}

	public Vec3D getFogColor(Entity entityPlayer, float f1) {
		float f2 = this.getCelestialAngle(f1);
		return this.worldProvider.getFogColor(entityPlayer, f2, f1);
	}

	public int getPrecipitationHeight(int i1, int i2) {
		return this.getChunkFromBlockCoords(i1, i2).getPrecipitationHeight(i1 & 15, i2 & 15);
	}

	public int getTopSolidOrLiquidBlock(int i1, int i2) {
		Chunk chunk3 = this.getChunkFromBlockCoords(i1, i2);
		int i4 = chunk3.getTopFilledSegment() + 16;
		i1 &= 15;

		for(i2 &= 15; i4 > 0; --i4) {
			int i5 = chunk3.getBlockID(i1, i4, i2);
			if(i5 != 0 && Block.blocksList[i5].blockMaterial.blocksMovement() && Block.blocksList[i5].blockMaterial != Material.leaves) {
				return i4 + 1;
			}
		}

		return -1;
	}
	
	public int findFirstFloorFrom(int x, int y, int z) {
		Chunk chunk = this.getChunkFromBlockCoords(x, z);
		x &= 0xf; 
		z &= 0xf;
		
		// First find solid
		while(y < 128) {
			Block block = Block.blocksList[chunk.getBlockID(x, y, z)];
			++ y;
			if(block != null && block.isOpaqueCube()) break;
		}
		
		// Then air, again
		while(y < 128) {
			Block block = Block.blocksList[chunk.getBlockID(x, y, z)];
			if(block == null || !block.isOpaqueCube()) {
				return y;
			}
			++ y;
		}
		
		return -1;
	}

	public float getStarBrightness(float f1) {
		return this.worldProvider.getStarBrightness(f1);
	}

	public void scheduleBlockUpdate(int i1, int i2, int i3, int i4, int i5) {
		NextTickListEntry nextTickListEntry6 = new NextTickListEntry(i1, i2, i3, i4);
		byte b7 = 8;
		if(this.scheduledUpdatesAreImmediate) {
			if(this.checkChunksExist(nextTickListEntry6.xCoord - b7, nextTickListEntry6.yCoord - b7, nextTickListEntry6.zCoord - b7, nextTickListEntry6.xCoord + b7, nextTickListEntry6.yCoord + b7, nextTickListEntry6.zCoord + b7)) {
				int i8 = this.getBlockId(nextTickListEntry6.xCoord, nextTickListEntry6.yCoord, nextTickListEntry6.zCoord);
				if(i8 == nextTickListEntry6.blockID && i8 > 0) {
					Block.blocksList[i8].updateTick(this, nextTickListEntry6.xCoord, nextTickListEntry6.yCoord, nextTickListEntry6.zCoord, this.rand);
				}
			}

		} else {
			if(this.checkChunksExist(i1 - b7, i2 - b7, i3 - b7, i1 + b7, i2 + b7, i3 + b7)) {
				if(i4 > 0) {
					nextTickListEntry6.setScheduledTime((long)i5 + this.worldInfo.getWorldTime());
				}

				if(!this.scheduledTickSet.contains(nextTickListEntry6)) {
					this.scheduledTickSet.add(nextTickListEntry6);
					this.scheduledTickTreeSet.add(nextTickListEntry6);
				}
			}

		}
	}

	public void scheduleBlockUpdateFromLoad(int i1, int i2, int i3, int i4, int i5) {
		NextTickListEntry nextTickListEntry6 = new NextTickListEntry(i1, i2, i3, i4);
		if(i4 > 0) {
			nextTickListEntry6.setScheduledTime((long)i5 + this.worldInfo.getWorldTime());
		}

		if(!this.scheduledTickSet.contains(nextTickListEntry6)) {
			this.scheduledTickSet.add(nextTickListEntry6);
			this.scheduledTickTreeSet.add(nextTickListEntry6);
		}

	}

	public void updateEntities() {
		//Profiler.startSection("entities");
		//Profiler.startSection("global");

		int i1;
		Entity entity2;
		for(i1 = 0; i1 < this.weatherEffects.size(); ++i1) {
			entity2 = (Entity)this.weatherEffects.get(i1);
			entity2.onUpdate();
			if(entity2.isDead) {
				this.weatherEffects.remove(i1--);
			}
		}

		//Profiler.endStartSection("remove");
		this.loadedEntityList.removeAll(this.unloadedEntityList);

		int i3;
		int i4;
		for(i1 = 0; i1 < this.unloadedEntityList.size(); ++i1) {
			entity2 = (Entity)this.unloadedEntityList.get(i1);
			i3 = entity2.chunkCoordX;
			i4 = entity2.chunkCoordZ;
			if(entity2.addedToChunk && this.chunkExists(i3, i4)) {
				this.getChunkFromChunkCoords(i3, i4).removeEntity(entity2);
			}
		}

		for(i1 = 0; i1 < this.unloadedEntityList.size(); ++i1) {
			this.releaseEntitySkin((Entity)this.unloadedEntityList.get(i1));
		}

		this.unloadedEntityList.clear();
		//Profiler.endStartSection("regular");

		for(i1 = 0; i1 < this.loadedEntityList.size(); ++i1) {
			entity2 = (Entity)this.loadedEntityList.get(i1);
			if(entity2.ridingEntity != null) {
				if(!entity2.ridingEntity.isDead && entity2.ridingEntity.riddenByEntity == entity2) {
					continue;
				}

				entity2.ridingEntity.riddenByEntity = null;
				entity2.ridingEntity = null;
			}

			if(!entity2.isDead) {
				this.updateEntity(entity2);
			}

			//Profiler.startSection("remove");
			if(entity2.isDead) {
				i3 = entity2.chunkCoordX;
				i4 = entity2.chunkCoordZ;
				if(entity2.addedToChunk && this.chunkExists(i3, i4)) {
					this.getChunkFromChunkCoords(i3, i4).removeEntity(entity2);
				}

				this.loadedEntityList.remove(i1--);
				this.releaseEntitySkin(entity2);
			}

			//Profiler.endSection();
		}

		//Profiler.endStartSection("tileEntities");
		this.scanningTileEntities = true;
		Iterator<TileEntity> iterator10 = this.loadedTileEntityList.iterator();

		while(iterator10.hasNext()) {
			TileEntity tileEntity5 = (TileEntity)iterator10.next();
			if(!tileEntity5.isInvalid() && tileEntity5.worldObj != null && this.blockExists(tileEntity5.xCoord, tileEntity5.yCoord, tileEntity5.zCoord)) {
				tileEntity5.updateEntity();
			}

			if(tileEntity5.isInvalid()) {
				iterator10.remove();
				if(this.chunkExists(tileEntity5.xCoord >> 4, tileEntity5.zCoord >> 4)) {
					Chunk chunk7 = this.getChunkFromChunkCoords(tileEntity5.xCoord >> 4, tileEntity5.zCoord >> 4);
					if(chunk7 != null) {
						chunk7.removeChunkBlockTileEntity(tileEntity5.xCoord & 15, tileEntity5.yCoord, tileEntity5.zCoord & 15);
					}
				}
			}
		}

		this.scanningTileEntities = false;
		if(!this.entityRemoval.isEmpty()) {
			this.loadedTileEntityList.removeAll(this.entityRemoval);
			this.entityRemoval.clear();
		}

		//Profiler.endStartSection("pendingTileEntities");
		if(!this.addedTileEntityList.isEmpty()) {
			Iterator<TileEntity> iterator6 = this.addedTileEntityList.iterator();

			while(iterator6.hasNext()) {
				TileEntity tileEntity8 = (TileEntity)iterator6.next();
				if(!tileEntity8.isInvalid()) {
					if(!this.loadedTileEntityList.contains(tileEntity8)) {
						this.loadedTileEntityList.add(tileEntity8);
					}

					if(this.chunkExists(tileEntity8.xCoord >> 4, tileEntity8.zCoord >> 4)) {
						Chunk chunk9 = this.getChunkFromChunkCoords(tileEntity8.xCoord >> 4, tileEntity8.zCoord >> 4);
						if(chunk9 != null) {
							chunk9.setChunkBlockTileEntity(tileEntity8.xCoord & 15, tileEntity8.yCoord, tileEntity8.zCoord & 15, tileEntity8);
						}
					}

					this.markBlockNeedsUpdate(tileEntity8.xCoord, tileEntity8.yCoord, tileEntity8.zCoord);
				}
			}

			this.addedTileEntityList.clear();
		}

		//Profiler.endSection();
		//Profiler.endSection();
	}

	public void addTileEntity(Collection<TileEntity> collection1) {
		if(this.scanningTileEntities) {
			this.addedTileEntityList.addAll(collection1);
		} else {
			this.loadedTileEntityList.addAll(collection1);
		}

	}

	public void updateEntity(Entity entity1) {
		this.updateEntityWithOptionalForce(entity1, true);
	}

	public void updateEntityWithOptionalForce(Entity entity1, boolean z2) {
		int i3 = MathHelper.floor_double(entity1.posX);
		int i4 = MathHelper.floor_double(entity1.posZ);
		byte b5 = 32;
		if(!z2 || this.checkChunksExist(i3 - b5, 0, i4 - b5, i3 + b5, 0, i4 + b5)) {
			entity1.lastTickPosX = entity1.posX;
			entity1.lastTickPosY = entity1.posY;
			entity1.lastTickPosZ = entity1.posZ;
			entity1.prevRotationYaw = entity1.rotationYaw;
			entity1.prevRotationPitch = entity1.rotationPitch;
			if(z2 && entity1.addedToChunk) {
				if(entity1.ridingEntity != null) {
					entity1.updateRidden();
				} else {
					entity1.onUpdate();
				}
			}

			//Profiler.startSection("chunkCheck");
			if(Double.isNaN(entity1.posX) || Double.isInfinite(entity1.posX)) {
				entity1.posX = entity1.lastTickPosX;
			}

			if(Double.isNaN(entity1.posY) || Double.isInfinite(entity1.posY)) {
				entity1.posY = entity1.lastTickPosY;
			}

			if(Double.isNaN(entity1.posZ) || Double.isInfinite(entity1.posZ)) {
				entity1.posZ = entity1.lastTickPosZ;
			}

			if(Double.isNaN((double)entity1.rotationPitch) || Double.isInfinite((double)entity1.rotationPitch)) {
				entity1.rotationPitch = entity1.prevRotationPitch;
			}

			if(Double.isNaN((double)entity1.rotationYaw) || Double.isInfinite((double)entity1.rotationYaw)) {
				entity1.rotationYaw = entity1.prevRotationYaw;
			}

			int i6 = MathHelper.floor_double(entity1.posX / 16.0D);
			int i7 = MathHelper.floor_double(entity1.posY / 16.0D);
			int i8 = MathHelper.floor_double(entity1.posZ / 16.0D);
			if(!entity1.addedToChunk || entity1.chunkCoordX != i6 || entity1.chunkCoordY != i7 || entity1.chunkCoordZ != i8) {
				if(entity1.addedToChunk && this.chunkExists(entity1.chunkCoordX, entity1.chunkCoordZ)) {
					this.getChunkFromChunkCoords(entity1.chunkCoordX, entity1.chunkCoordZ).removeEntityAtIndex(entity1, entity1.chunkCoordY);
				}

				if(this.chunkExists(i6, i8)) {
					entity1.addedToChunk = true;
					this.getChunkFromChunkCoords(i6, i8).addEntity(entity1);
				} else {
					entity1.addedToChunk = false;
				}
			}

			//Profiler.endSection();
			if(z2 && entity1.addedToChunk && entity1.riddenByEntity != null) {
				if(!entity1.riddenByEntity.isDead && entity1.riddenByEntity.ridingEntity == entity1) {
					this.updateEntity(entity1.riddenByEntity);
				} else {
					entity1.riddenByEntity.ridingEntity = null;
					entity1.riddenByEntity = null;
				}
			}

		}
	}

	public boolean checkIfAABBIsClear(AxisAlignedBB axisAlignedBB1) {
		List<Entity> list2 = this.getEntitiesWithinAABBExcludingEntity((Entity)null, axisAlignedBB1);

		for(int i3 = 0; i3 < list2.size(); ++i3) {
			Entity entity4 = (Entity)list2.get(i3);
			if(!entity4.isDead && entity4.preventEntitySpawning) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns true if there are no solid, live entities in the specified
	 * AxisAlignedBB, excluding the given entity
	 */
	public boolean checkIfAABBIsClearExcludingEntity(AxisAlignedBB par1AxisAlignedBB, Entity par2Entity) {
		List<Entity> var3 = this.getEntitiesWithinAABBExcludingEntity((Entity) null, par1AxisAlignedBB);
		Iterator<Entity> var4 = var3.iterator();
		Entity var5;

		do {
			if (!var4.hasNext()) {
				return true;
			}

			var5 = (Entity) var4.next();
		} while (var5.isDead || !var5.preventEntitySpawning || var5 == par2Entity);

		return false;
	}
	
	public boolean isAABBEmpty(AxisAlignedBB axisAlignedBB1) {
		int i2 = MathHelper.floor_double(axisAlignedBB1.minX);
		int i3 = MathHelper.floor_double(axisAlignedBB1.maxX + 1.0D);
		int i4 = MathHelper.floor_double(axisAlignedBB1.minY);
		int i5 = MathHelper.floor_double(axisAlignedBB1.maxY + 1.0D);
		int i6 = MathHelper.floor_double(axisAlignedBB1.minZ);
		int i7 = MathHelper.floor_double(axisAlignedBB1.maxZ + 1.0D);
		if(axisAlignedBB1.minX < 0.0D) {
			--i2;
		}

		if(axisAlignedBB1.minY < 0.0D) {
			--i4;
		}

		if(axisAlignedBB1.minZ < 0.0D) {
			--i6;
		}

		for(int i8 = i2; i8 < i3; ++i8) {
			for(int i9 = i4; i9 < i5; ++i9) {
				for(int i10 = i6; i10 < i7; ++i10) {
					Block block11 = Block.blocksList[this.getBlockId(i8, i9, i10)];
					if(block11 != null) {
						return true;
					}
				}
			}
		}

		return false;
	}
	
	public boolean isAABBInBlockID(AxisAlignedBB axisalignedbb, int blockID) {
		int i = MathHelper.floor_double(axisalignedbb.minX);
		int j = MathHelper.floor_double(axisalignedbb.maxX + 1.0D);
		int k = MathHelper.floor_double(axisalignedbb.minY);
		int l = MathHelper.floor_double(axisalignedbb.maxY + 1.0D);
		int i1 = MathHelper.floor_double(axisalignedbb.minZ);
		int j1 = MathHelper.floor_double(axisalignedbb.maxZ + 1.0D);

		for(int k1 = i; k1 < j; ++k1) {
			for(int l1 = k; l1 < l; ++l1) {
				for(int i2 = i1; i2 < j1; ++i2) {
					if(this.getBlockId(k1, l1, i2) == blockID) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean isAnyLiquid(AxisAlignedBB axisAlignedBB1) {
		int i2 = MathHelper.floor_double(axisAlignedBB1.minX);
		int i3 = MathHelper.floor_double(axisAlignedBB1.maxX + 1.0D);
		int i4 = MathHelper.floor_double(axisAlignedBB1.minY);
		int i5 = MathHelper.floor_double(axisAlignedBB1.maxY + 1.0D);
		int i6 = MathHelper.floor_double(axisAlignedBB1.minZ);
		int i7 = MathHelper.floor_double(axisAlignedBB1.maxZ + 1.0D);
		if(axisAlignedBB1.minX < 0.0D) {
			--i2;
		}

		if(axisAlignedBB1.minY < 0.0D) {
			--i4;
		}

		if(axisAlignedBB1.minZ < 0.0D) {
			--i6;
		}

		for(int i8 = i2; i8 < i3; ++i8) {
			for(int i9 = i4; i9 < i5; ++i9) {
				for(int i10 = i6; i10 < i7; ++i10) {
					Block block11 = Block.blocksList[this.getBlockId(i8, i9, i10)];
					if(block11 != null && block11.blockMaterial.isLiquid()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean isBoundingBoxBurning(AxisAlignedBB axisAlignedBB1) {
		int i2 = MathHelper.floor_double(axisAlignedBB1.minX);
		int i3 = MathHelper.floor_double(axisAlignedBB1.maxX + 1.0D);
		int i4 = MathHelper.floor_double(axisAlignedBB1.minY);
		int i5 = MathHelper.floor_double(axisAlignedBB1.maxY + 1.0D);
		int i6 = MathHelper.floor_double(axisAlignedBB1.minZ);
		int i7 = MathHelper.floor_double(axisAlignedBB1.maxZ + 1.0D);
		if(this.checkChunksExist(i2, i4, i6, i3, i5, i7)) {
			for(int i8 = i2; i8 < i3; ++i8) {
				for(int i9 = i4; i9 < i5; ++i9) {
					for(int i10 = i6; i10 < i7; ++i10) {
						int i11 = this.getBlockId(i8, i9, i10);
						if(i11 == Block.fire.blockID || i11 == Block.lavaMoving.blockID || i11 == Block.lavaStill.blockID) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public boolean handleMaterialAcceleration(AxisAlignedBB axisAlignedBB1, Material material2, Entity entity3) {
		int i4 = MathHelper.floor_double(axisAlignedBB1.minX);
		int i5 = MathHelper.floor_double(axisAlignedBB1.maxX + 1.0D);
		int i6 = MathHelper.floor_double(axisAlignedBB1.minY);
		int i7 = MathHelper.floor_double(axisAlignedBB1.maxY + 1.0D);
		int i8 = MathHelper.floor_double(axisAlignedBB1.minZ);
		int i9 = MathHelper.floor_double(axisAlignedBB1.maxZ + 1.0D);
		if(!this.checkChunksExist(i4, i6, i8, i5, i7, i9)) {
			return false;
		} else {
			boolean z10 = false;
			Vec3D vec3D11 = Vec3D.createVector(0.0D, 0.0D, 0.0D);

			for(int i12 = i4; i12 < i5; ++i12) {
				for(int i13 = i6; i13 < i7; ++i13) {
					for(int i14 = i8; i14 < i9; ++i14) {
						Block block15 = Block.blocksList[this.getBlockId(i12, i13, i14)];
						if(block15 != null && block15.blockMaterial == material2) {
							double d16 = (double)((float)(i13 + 1) - BlockFluid.getFluidHeightPercent(this.getBlockMetadata(i12, i13, i14)));
							if((double)i7 >= d16) {
								z10 = true;
								block15.velocityToAddToEntity(this, i12, i13, i14, entity3, vec3D11);
							}
						}
					}
				}
			}

			if(vec3D11.lengthVector() > 0.0D) {
				vec3D11 = vec3D11.normalize();
				double d18 = 0.014D;
				entity3.motionX += vec3D11.xCoord * d18;
				entity3.motionY += vec3D11.yCoord * d18;
				entity3.motionZ += vec3D11.zCoord * d18;
			}

			return z10;
		}
	}

	public boolean isMaterialInBB(AxisAlignedBB axisAlignedBB1, Material material2) {
		int i3 = MathHelper.floor_double(axisAlignedBB1.minX);
		int i4 = MathHelper.floor_double(axisAlignedBB1.maxX + 1.0D);
		int i5 = MathHelper.floor_double(axisAlignedBB1.minY);
		int i6 = MathHelper.floor_double(axisAlignedBB1.maxY + 1.0D);
		int i7 = MathHelper.floor_double(axisAlignedBB1.minZ);
		int i8 = MathHelper.floor_double(axisAlignedBB1.maxZ + 1.0D);

		for(int i9 = i3; i9 < i4; ++i9) {
			for(int i10 = i5; i10 < i6; ++i10) {
				for(int i11 = i7; i11 < i8; ++i11) {
					Block block12 = Block.blocksList[this.getBlockId(i9, i10, i11)];
					if(block12 != null && block12.blockMaterial == material2) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean isAABBInMaterial(AxisAlignedBB axisAlignedBB1, Material material2) {
		int i3 = MathHelper.floor_double(axisAlignedBB1.minX);
		int i4 = MathHelper.floor_double(axisAlignedBB1.maxX + 1.0D);
		int i5 = MathHelper.floor_double(axisAlignedBB1.minY);
		int i6 = MathHelper.floor_double(axisAlignedBB1.maxY + 1.0D);
		int i7 = MathHelper.floor_double(axisAlignedBB1.minZ);
		int i8 = MathHelper.floor_double(axisAlignedBB1.maxZ + 1.0D);

		for(int i9 = i3; i9 < i4; ++i9) {
			for(int i10 = i5; i10 < i6; ++i10) {
				for(int i11 = i7; i11 < i8; ++i11) {
					Block block12 = Block.blocksList[this.getBlockId(i9, i10, i11)];
					if(block12 != null && block12.blockMaterial == material2) {
						int i13 = this.getBlockMetadata(i9, i10, i11);
						double d14 = (double)(i10 + 1);
						if(i13 < 8) {
							d14 = (double)(i10 + 1) - (double)i13 / 8.0D;
						}

						if(d14 >= axisAlignedBB1.minY) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public Explosion createExplosion(Entity entity1, double d2, double d4, double d6, float f8) {
		return this.newExplosion(entity1, d2, d4, d6, f8, false);
	}

	public Explosion newExplosion(Entity entity1, double d2, double d4, double d6, float f8, boolean z9) {
		Explosion explosion10 = new Explosion(this, entity1, d2, d4, d6, f8);
		explosion10.isFlaming = z9;
		explosion10.doExplosionA();
		explosion10.doExplosionB(true);
		return explosion10;
	}

	public float getBlockDensity(Vec3D vec3D1, AxisAlignedBB axisAlignedBB2) {
		double d3 = 1.0D / ((axisAlignedBB2.maxX - axisAlignedBB2.minX) * 2.0D + 1.0D);
		double d5 = 1.0D / ((axisAlignedBB2.maxY - axisAlignedBB2.minY) * 2.0D + 1.0D);
		double d7 = 1.0D / ((axisAlignedBB2.maxZ - axisAlignedBB2.minZ) * 2.0D + 1.0D);
		int i9 = 0;
		int i10 = 0;

		for(float f11 = 0.0F; f11 <= 1.0F; f11 = (float)((double)f11 + d3)) {
			for(float f12 = 0.0F; f12 <= 1.0F; f12 = (float)((double)f12 + d5)) {
				for(float f13 = 0.0F; f13 <= 1.0F; f13 = (float)((double)f13 + d7)) {
					double d14 = axisAlignedBB2.minX + (axisAlignedBB2.maxX - axisAlignedBB2.minX) * (double)f11;
					double d16 = axisAlignedBB2.minY + (axisAlignedBB2.maxY - axisAlignedBB2.minY) * (double)f12;
					double d18 = axisAlignedBB2.minZ + (axisAlignedBB2.maxZ - axisAlignedBB2.minZ) * (double)f13;
					if(this.rayTraceBlocks(Vec3D.createVector(d14, d16, d18), vec3D1) == null) {
						++i9;
					}

					++i10;
				}
			}
		}

		return (float)i9 / (float)i10;
	}

	public boolean onBlockHit(EntityPlayer entityPlayer1, int i2, int i3, int i4, int i5) {
		if(i5 == 0) {
			--i3;
		}

		if(i5 == 1) {
			++i3;
		}

		if(i5 == 2) {
			--i4;
		}

		if(i5 == 3) {
			++i4;
		}

		if(i5 == 4) {
			--i2;
		}

		if(i5 == 5) {
			++i2;
		}

		if(this.getBlockId(i2, i3, i4) == Block.fire.blockID) {
			this.playAuxSFXAtEntity(entityPlayer1, 1004, i2, i3, i4, 0);
			this.setBlockWithNotify(i2, i3, i4, 0);
			return true;
		} else {
			return false;
		}
	}
	
	public String getDebugLoadedEntities() {
		return "All: " + this.loadedEntityList.size();
	}

	public String getProviderName() {
		return this.chunkProvider.makeString();
	}

	public TileEntity getBlockTileEntity(int i1, int i2, int i3) {
		if(i2 >= 256) {
			return null;
		} else {
			Chunk chunk4 = this.getChunkFromChunkCoords(i1 >> 4, i3 >> 4);
			if(chunk4 == null) {
				return null;
			} else {
				TileEntity tileEntity5 = chunk4.getChunkBlockTileEntity(i1 & 15, i2, i3 & 15);
				if(tileEntity5 == null) {
					Iterator<TileEntity> iterator6 = this.addedTileEntityList.iterator();

					while(iterator6.hasNext()) {
						TileEntity tileEntity7 = (TileEntity)iterator6.next();
						if(!tileEntity7.isInvalid() && tileEntity7.xCoord == i1 && tileEntity7.yCoord == i2 && tileEntity7.zCoord == i3) {
							tileEntity5 = tileEntity7;
							break;
						}
					}
				}

				return tileEntity5;
			}
		}
	}

	public void setBlockTileEntity(int i1, int i2, int i3, TileEntity tileEntity4) {
		if(tileEntity4 != null && !tileEntity4.isInvalid()) {
			if(this.scanningTileEntities) {
				tileEntity4.xCoord = i1;
				tileEntity4.yCoord = i2;
				tileEntity4.zCoord = i3;
				this.addedTileEntityList.add(tileEntity4);
			} else {
				this.loadedTileEntityList.add(tileEntity4);
				Chunk chunk5 = this.getChunkFromChunkCoords(i1 >> 4, i3 >> 4);
				if(chunk5 != null) {
					chunk5.setChunkBlockTileEntity(i1 & 15, i2, i3 & 15, tileEntity4);
				}
			}
		}

	}

	public void removeBlockTileEntity(int i1, int i2, int i3) {
		TileEntity tileEntity4 = this.getBlockTileEntity(i1, i2, i3);
		
		if(tileEntity4 != null && this.scanningTileEntities) {
			tileEntity4.invalidate();
			this.addedTileEntityList.remove(tileEntity4);
		} else {
			if(tileEntity4 != null) {
				this.addedTileEntityList.remove(tileEntity4);
				this.loadedTileEntityList.remove(tileEntity4);
			}

			Chunk chunk5 = this.getChunkFromChunkCoords(i1 >> 4, i3 >> 4);
			if(chunk5 != null) {
				chunk5.removeChunkBlockTileEntity(i1 & 15, i2, i3 & 15);
			}
		}

	}

	public void markTileEntityForDespawn(TileEntity tileEntity1) {
		this.entityRemoval.add(tileEntity1);
	}

	public boolean isBlockOpaqueCube(int i1, int i2, int i3) {
		Block block4 = Block.blocksList[this.getBlockId(i1, i2, i3)];
		return block4 == null ? false : block4.isOpaqueCube();
	}

	public boolean isBlockNormalCube(int i1, int i2, int i3) {
		//return Block.isNormalCube(this.getBlockId(i1, i2, i3));
		Block block = Block.blocksList[this.getBlockId(i1, i2, i3)];
		if(block == null) return false;
		return block.isBlockNormalCube(this.getBlockMetadata(i1, i2, i3));
	}

	public boolean isBlockNormalCubeDefault(int x, int y, int z, boolean def) {
		Chunk chunk = this.chunkProvider.provideChunk(x >> 4, z >> 4);
		if(chunk != null && !chunk.isEmpty()) {
			Block block = Block.blocksList[this.getBlockId(x, y, z)];
			return block == null ? false : block.blockMaterial.isOpaque() && block.renderAsNormalBlock();
		} else {
			return def;
		}
	}

	public void saveWorldIndirectly(IProgressUpdate iProgressUpdate1) {
		this.saveWorld(true, iProgressUpdate1);

		try {
			ThreadedFileIOBase.threadedIOInstance.waitForFinish();
		} catch (InterruptedException interruptedException3) {
			interruptedException3.printStackTrace();
		}

	}

	public void calculateInitialSkylight() {
		int i1 = this.calculateSkylightSubtracted(1.0F);
		if(i1 != this.skylightSubtracted) {
			this.skylightSubtracted = i1;
		}

	}

	public void setAllowedSpawnTypes(boolean z1, boolean z2) {
		this.spawnHostileMobs = z1;
		this.spawnPeacefulMobs = z2;
	}

	public void tick() {
		if(this.getWorldInfo().isHardcoreModeEnabled() && this.difficultySetting < 3) {
			this.difficultySetting = 3;
		}

		this.worldProvider.worldChunkMgr.cleanupCache();
		this.updateWeather();

		long worldTime;
	
		if(this.isAllPlayersFullyAsleep()) {
			boolean nightmare = false;
			if(this.spawnHostileMobs && this.difficultySetting >= 1) {
				nightmare = SpawnerAnimals.performSleepSpawning(this, this.playerEntities);
			}

			if(!nightmare) {
				worldTime = this.worldInfo.getWorldTime() + 24000L;
				this.worldInfo.setWorldTime(worldTime - worldTime % 24000L);
				this.wakeUpAllPlayers();
			}
		}
		
		SpawnerAnimals.performSpawning(this, this.spawnHostileMobs, this.spawnPeacefulMobs && this.worldInfo.getWorldTime() % 400L == 0L);

		this.chunkProvider.unload100OldestChunks();

		int skylightSubtracted = this.calculateSkylightSubtracted(1.0F);
		if(skylightSubtracted != this.skylightSubtracted) {
			this.skylightSubtracted = skylightSubtracted;
		}

		worldTime = this.worldInfo.getWorldTime() + 1L;
		int hourOfTheDay = (int)(worldTime % 24000L);

		if(worldTime % (long)this.autosavePeriod == 0L) {
			this.saveWorld(false, (IProgressUpdate)null);
		}

		this.worldInfo.setWorldTime(worldTime);

		this.updateDailyTasks(worldTime, hourOfTheDay);
		
		this.tickUpdates(false);
		this.tickBlocksAndPlayCaveSounds();

		// TODO : Blizzard -> weather effects like desert storm	
	}
	
	protected void updateDailyTasks(long worldTime, int hourOfTheDay) {
	
		// Advance day
		if(hourOfTheDay == 18000) {
			int oldCurrentSeason = Seasons.currentSeason;
			
			Seasons.dayOfTheYear ++;
			Seasons.updateSeasonCounters();
			
			if (this.worldInfo.isEnableSeasons()) {
				// Leaves change colours so
				for(int i5 = 0; i5 < this.worldAccesses.size(); ++i5) {
					((IWorldAccess)this.worldAccesses.get(i5)).updateAllRenderers();
				}
				
				if(Seasons.currentSeason != oldCurrentSeason) {
					if(Seasons.currentSeason == Seasons.WINTER) {
						if(!this.worldInfo.isSnowing()) {
							int newSnowingTime = Weather.getTimeForNextSnow(this.rand);
							if(newSnowingTime < this.worldInfo.getSnowingTime()) {
								this.worldInfo.setSnowingTime(newSnowingTime);
							}
						}
						if(this.worldInfo.isRaining()) {
							int newRainingTime = 3000 + this.rand.nextInt(3000);
							if(this.worldInfo.getRainTime() > newRainingTime) this.worldInfo.setRainTime(newRainingTime);
						}
					}
					
					if(!this.worldInfo.isRaining() && (Seasons.currentSeason == Seasons.SPRING || Seasons.currentSeason == Seasons.AUTUMN)) {
						int newRainingTime = Weather.getTimeForNextRain(this.rand);
						if(newRainingTime < this.worldInfo.getRainTime()) {
							this.worldInfo.setRainTime(newRainingTime);
						}
					}
					
					this.getWorldAccess(0).showString(Seasons.seasonNames[Seasons.currentSeason]);
				}
			}
		}
	}
	
	private void initializeWeather() {
		this.worldInfo.setRainTime(Weather.getTimeForNextRain(this.rand));
		this.worldInfo.setSnowingTime(Weather.getTimeForNextSnow(this.rand));
		this.worldInfo.setThunderTime(Weather.getTimeForNextThunder(this.rand));
	}

	private void calculateInitialWeather() {
		if (this.worldProvider.hasNoSky) {
			this.rainingStrength = 0.0F;
			this.snowingStrength = 0.0F;
			this.thunderingStrength = 0.0F;
		} else {
			if(this.worldInfo.isRaining()) {
				this.rainingStrength = 1.0F;	
			}
			
			if(this.worldInfo.isThundering()) {
				this.thunderingStrength = 1.0F;
			}

			if(this.worldInfo.isSnowing()) {
				this.snowingStrength = 1.0F;
			}
		}

		this.sandstormingStrength = 0.0F;
		
	}

	protected void updateWeather() {
		if(!this.worldProvider.hasNoSky) {

			// Lightning bolts
			
			if(this.lastLightningBolt > 0) {
				--this.lastLightningBolt;
			}
			
			// Thunderstorm. In this version, it is independent of rainstorms.

			int i1 = this.worldInfo.getThunderTime();
			--i1;
			this.worldInfo.setThunderTime(i1);
			
			if(i1 <= 0) {
				if(this.worldInfo.isThundering()) {
					this.worldInfo.setThunderTime(Weather.getTimeForNextThunder(this.rand));
				} else {
					this.worldInfo.setThunderTime(Weather.getTimeForThunderingEnd(this.rand));
				}	
				this.worldInfo.setThundering(!this.worldInfo.isThundering());
			}

			this.prevThunderingStrength = this.thunderingStrength;
			if(this.worldInfo.isThundering()) {
				this.thunderingStrength = (float)((double)this.thunderingStrength + 0.01D);
			} else {
				this.thunderingStrength = (float)((double)this.thunderingStrength - 0.01D);
			}

			if(this.thunderingStrength < 0.0F) {
				this.thunderingStrength = 0.0F;
			}

			if(this.thunderingStrength > 1.0F) {
				this.thunderingStrength = 1.0F;
			}
			
			// Snowstorm
			
			int i3 = this.worldInfo.getSnowingTime();
			--i3;
			this.worldInfo.setSnowingTime(i3);
			
			if(i3 <= 0) {
				if(this.worldInfo.isSnowing()) {
					this.worldInfo.setSnowingTime(Weather.getTimeForNextSnow(this.rand));
					this.worldInfo.setSnowing(false);
				} else if(!this.worldInfo.isRaining()) {
					this.worldInfo.setSnowingTime(Weather.getTimeForSnowingEnd(this.rand));
					this.worldInfo.setSnowing(true);
				}
			} 

			this.prevSnowingStrength = this.snowingStrength;
			
			if(this.worldInfo.isSnowing()) {
				this.snowingStrength = (float)((double)this.snowingStrength + 0.01D);
			} else {
				this.snowingStrength = (float)((double)this.snowingStrength - 0.01D);
			}

			if(this.snowingStrength < 0.0F) {
				this.snowingStrength = 0.0F;
			}

			if(this.snowingStrength > 1.0F) {
				this.snowingStrength = 1.0F;
			}
				
			// Rains
			
			int i2 = this.worldInfo.getRainTime();
			--i2;
			this.worldInfo.setRainTime(i2);
			
			if(i2 <= 0) {
				if(this.worldInfo.isRaining()) {
					this.worldInfo.setRainTime(Weather.getTimeForNextRain(this.rand));
					this.lightningChance = 60000;
					this.worldInfo.setRaining(false);
				} else if(!this.worldInfo.isSnowing()) {
					this.worldInfo.setRainTime(Weather.getTimeForRainingEnd(this.rand));
					this.lightningChance = 50000;
					this.worldInfo.setRaining(true);
				}
			} 

			this.prevRainingStrength = this.rainingStrength;
			if(this.worldInfo.isRaining()) {
				this.rainingStrength = (float)((double)this.rainingStrength + 0.01D);
			} else {
				this.rainingStrength = (float)((double)this.rainingStrength - 0.01D);
			}

			if(this.rainingStrength < 0.0F) {
				this.rainingStrength = 0.0F;
			}

			if(this.rainingStrength > 1.0F) {
				this.rainingStrength = 1.0F;
			}
			
		}
	}

	private void clearWeather() {
		this.worldInfo.setRainTime(0);
		this.worldInfo.setRaining(false);
		this.worldInfo.setThunderTime(0);
		this.worldInfo.setThundering(false);
	}

	public void commandToggleDownfall() {
		this.worldInfo.setRainTime(1);
	}

	protected void buildPlayerListAndCheckLight() {
		this.activeChunkSet.clear();

		int i1;
		EntityPlayer entityPlayer2;
		int i3;
		int i4;
		for(i1 = 0; i1 < this.playerEntities.size(); ++i1) {
			entityPlayer2 = (EntityPlayer)this.playerEntities.get(i1);
			i3 = MathHelper.floor_double(entityPlayer2.posX / 16.0D);
			i4 = MathHelper.floor_double(entityPlayer2.posZ / 16.0D);
			byte b5 = 7;

			for(int i6 = -b5; i6 <= b5; ++i6) {
				for(int i7 = -b5; i7 <= b5; ++i7) {
					this.activeChunkSet.add(new ChunkCoordIntPair(i6 + i3, i7 + i4));
				}
			}
		}

		if(this.ambientTickCountdown > 0) {
			--this.ambientTickCountdown;
		}

		if(!this.playerEntities.isEmpty()) {
			i1 = this.rand.nextInt(this.playerEntities.size());
			entityPlayer2 = (EntityPlayer)this.playerEntities.get(i1);
			i3 = MathHelper.floor_double(entityPlayer2.posX) + this.rand.nextInt(11) - 5;
			i4 = MathHelper.floor_double(entityPlayer2.posY) + this.rand.nextInt(11) - 5;
		}

	}

	protected void tickChunks(int i1, int i2, Chunk chunk3) {

		if(this.ambientTickCountdown == 0) {
			this.updateLCG = this.updateLCG * 3 + 1013904223;
			int i4 = this.updateLCG >> 2;
			int i5 = i4 & 15;
			int i6 = i4 >> 8 & 15;
			int i7 = i4 >> 16 & 127;
			int i8 = chunk3.getBlockID(i5, i7, i6);
			i5 += i1;
			i6 += i2;
			if(i8 == 0 && this.getFullBlockLightValue(i5, i7, i6) <= this.rand.nextInt(8) && this.getSavedLightValue(EnumSkyBlock.Sky, i5, i7, i6) <= 0) {
				EntityPlayer entityPlayer9 = this.getClosestPlayer((double)i5 + 0.5D, (double)i7 + 0.5D, (double)i6 + 0.5D, 8.0D);
				if(entityPlayer9 != null && entityPlayer9.getDistanceSq((double)i5 + 0.5D, (double)i7 + 0.5D, (double)i6 + 0.5D) > 4.0D) {
					this.playSoundEffect((double)i5 + 0.5D, (double)i7 + 0.5D, (double)i6 + 0.5D, "ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
					this.ambientTickCountdown = this.rand.nextInt(12000) + 6000;
				}
			}
		}

	}

	protected void tickBlocksAndPlayCaveSounds() {
		this.buildPlayerListAndCheckLight();
		Iterator<ChunkCoordIntPair> chunksIt = this.activeChunkSet.iterator();

		while(chunksIt.hasNext()) {
			ChunkCoordIntPair chunkCoords = (ChunkCoordIntPair)chunksIt.next();

			// Block coordinates at the beginning of this chunk
			int x0 = chunkCoords.chunkXPos * 16;
			int z0 = chunkCoords.chunkZPos * 16;

			Chunk chunk = this.getChunkFromChunkCoords(chunkCoords.chunkXPos, chunkCoords.chunkZPos);
			
			// Tick this chunk
			this.tickChunks(x0, z0, chunk);

			int tIndex;
			int x;
			int z;
			int y;
			if(this.isThundering() && this.rand.nextInt(this.lightningChance) == 0) {
				this.updateLCG = this.updateLCG * 3 + 1013904223;
				tIndex = this.updateLCG >> 2;
				x = x0 + (tIndex & 15);
				z = z0 + (tIndex >> 8 & 15);
				y = this.getPrecipitationHeight(x, z);

				if(this.canLightningStrikeAt(x, y, z)) {
					this.addWeatherEffect(new EntityLightningBolt(this, (double)x, (double)y, (double)z));
					this.lastLightningBolt = 2;
				}
			}

			// Select a top block and cover / uncover with snow
			// TODO: Only when needed: cold biomes, or normal biomes in winter.
			if(this.rand.nextInt(16) == 0) {
				this.updateLCG = this.updateLCG * 3 + 1013904223;
				tIndex = this.updateLCG >> 2;
				x = tIndex & 15;
				z = tIndex >> 8 & 15;
				y = this.getPrecipitationHeight(x + x0, z + z0);
				BiomeGenBase biomeGen = this.getBiomeGenForCoords(x, z);
				
				if(this.canFreezeWaterIndirectly(x + x0, y - 1, z + z0, biomeGen)) {
					this.setBlockWithNotify(x + x0, y - 1, z + z0, Block.ice.blockID);
				}

				if(this.canSnowAt(x + x0, y, z + z0, false, biomeGen)) {
					if(GameRules.boolRule("snowPilesUp")) {
						
					} else {
						this.setBlockWithNotify(x + x0, y, z + z0, Block.snow.blockID);
					}
				}
				
				if(this.canMeltSnow(x + x0, y, z + z0, biomeGen)) {
					this.setBlockWithNotify(x + x0, y, z + z0, 0);
				}
				
				if(this.canMeltIce(x + x0, y - 1, z + z0, biomeGen)) {
					this.setBlockWithNotify(x + x0, y - 1, z + z0, Block.waterMoving.blockID);
				}
			}

			ExtendedBlockStorage[] subChunks = chunk.getBlockStorageArray();
			int maxSubchunks = subChunks.length;

			for(int i = 0; i < maxSubchunks; ++i) {
				ExtendedBlockStorage subChunk = subChunks[i];
				if(subChunk != null && subChunk.getNeedsRandomTick()) {
					for(int j = 0; j < 3; ++j) {
						this.updateLCG = this.updateLCG * 3 + 1013904223;
						tIndex = this.updateLCG >> 2;
						x = tIndex & 15;
						z = tIndex >> 8 & 15;
						y = tIndex >> 16 & 15;
						int blockID = subChunk.getExtBlockID(x, y, z);
						Block block = Block.blocksList[blockID];
						if(block != null && block.getTickRandomly()) {
							block.updateTick(this, x + x0, y + subChunk.getYLocation(), z + z0, this.rand);
						}
					}
				}
			}
			
		}

	}

	public boolean canFreezeWaterDirectly(int i1, int i2, int i3, BiomeGenBase biomeGen) {
		return this.canFreezeWater(i1, i2, i3, false, biomeGen);
	}

	public boolean canFreezeWaterIndirectly(int i1, int i2, int i3, BiomeGenBase biomeGen) {
		return this.canFreezeWater(i1, i2, i3, true, biomeGen);
	}

	public boolean ambienceIsHot(int x, int z, BiomeGenBase biomeGen) {
		float t = this.getWorldChunkManager().getTemperatureAt(x, z);
		
		if(GameRules.rampBasedTemperature) { 
			if(this.worldInfo.isEnableSeasons()) {
				return t > .6F ||
						t > .2 && Seasons.currentSeason != Seasons.WINTER ||
						t > .1 && Seasons.currentSeason == Seasons.SUMMER;
				
			} else return t >= .2F;
		} else {
			if(this.worldInfo.isEnableSeasons()) {
				return
						biomeGen.weather == Weather.desert || 
						biomeGen.weather == Weather.hot ||
						(biomeGen.weather == Weather.normal && Seasons.currentSeason != Seasons.WINTER) ||
						(biomeGen.weather == Weather.cold && Seasons.currentSeason == Seasons.SUMMER);

			} else return biomeGen.weather != Weather.cold;
		}
	}
	
	public boolean canFreezeWater(int x, int y, int z, boolean z4, BiomeGenBase biomeGen) {
		// Discard.
		if(this.ambienceIsHot(x, z, biomeGen)) return false;
		
		if(y >= 0 && y < 256 && this.getSavedLightValue(EnumSkyBlock.Block, x, y, z) < 10) {
			int blockID = this.getBlockId(x, y, z);
			if((blockID == Block.waterStill.blockID || blockID == Block.waterMoving.blockID) && this.getBlockMetadata(x, y, z) == 0) {
				if(!z4) {
					return true;
				}

				boolean z8 = true;
				if(z8 && this.getBlockMaterial(x - 1, y, z) != Material.water) {
					z8 = false;
				}

				if(z8 && this.getBlockMaterial(x + 1, y, z) != Material.water) {
					z8 = false;
				}

				if(z8 && this.getBlockMaterial(x, y, z - 1) != Material.water) {
					z8 = false;
				}

				if(z8 && this.getBlockMaterial(x, y, z + 1) != Material.water) {
					z8 = false;
				}

				if(!z8) {
					return true;
				}
			}
		}

		return false;
	
	}

	public boolean canSnowAt(int x, int y, int z, boolean force, BiomeGenBase biomeGen) {
		if(this.ambienceIsHot(x, z, biomeGen)) return false;
		if(!force && Weather.particleDecide(biomeGen, this) != Weather.SNOW) return false;

		if(y >= 0 && y < 256 && this.getSavedLightValue(EnumSkyBlock.Block, x, y, z) < 10) {
			int belowID = this.getBlockId(x, y - 1, z);
			if(Block.snow.canPlaceBlockAt(this, x, y, z) && belowID != 0 && belowID != Block.ice.blockID && Block.blocksList[belowID].blockMaterial.blocksMovement()) {
				return true;
			}
		}

		return false;
	}
	
	public boolean canMeltSnow(int x, int y, int z, BiomeGenBase biomeGen) {
		if(this.worldInfo.isEnableSeasons()) {
			if(biomeGen.weather != Weather.cold && (Seasons.currentSeason != Seasons.WINTER || biomeGen.weather == Weather.desert)) {
				return this.getBlockId(x, y, z) == Block.snow.blockID;
			}
		}
		return false;
	}
	
	public boolean canMeltIce(int x, int y, int z, BiomeGenBase biomeGen) {
		if(this.worldInfo.isEnableSeasons()) {
			if(biomeGen.weather != Weather.cold && (Seasons.currentSeason != Seasons.WINTER || biomeGen.weather == Weather.desert)) {
				return this.getBlockId(x, y, z) == Block.ice.blockID;
			}
		}
		return false;
	}

	public boolean tickUpdates(boolean z1) {
		int i2 = this.scheduledTickTreeSet.size();
		if(i2 != this.scheduledTickSet.size()) {
			throw new IllegalStateException("TickNextTick list out of synch");
		} else {
			if(i2 > 1000) {
				i2 = 1000;
			}

			for(int i3 = 0; i3 < i2; ++i3) {
				NextTickListEntry nextTickListEntry4 = (NextTickListEntry)this.scheduledTickTreeSet.first();
				if(!z1 && nextTickListEntry4.scheduledTime > this.worldInfo.getWorldTime()) {
					break;
				}

				this.scheduledTickTreeSet.remove(nextTickListEntry4);
				this.scheduledTickSet.remove(nextTickListEntry4);
				byte b5 = 8;
				if(this.checkChunksExist(nextTickListEntry4.xCoord - b5, nextTickListEntry4.yCoord - b5, nextTickListEntry4.zCoord - b5, nextTickListEntry4.xCoord + b5, nextTickListEntry4.yCoord + b5, nextTickListEntry4.zCoord + b5)) {
					int i6 = this.getBlockId(nextTickListEntry4.xCoord, nextTickListEntry4.yCoord, nextTickListEntry4.zCoord);
					if(i6 == nextTickListEntry4.blockID && i6 > 0) {
						Block.blocksList[i6].updateTick(this, nextTickListEntry4.xCoord, nextTickListEntry4.yCoord, nextTickListEntry4.zCoord, this.rand);
					}
				}
			}

			return this.scheduledTickTreeSet.size() != 0;
		}
	}

	public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunk1, boolean z2) {
		ArrayList<NextTickListEntry> arrayList3 = null;
		ChunkCoordIntPair chunkCoordIntPair4 = chunk1.getChunkCoordIntPair();
		int i5 = chunkCoordIntPair4.chunkXPos << 4;
		int i6 = i5 + 16;
		int i7 = chunkCoordIntPair4.chunkZPos << 4;
		int i8 = i7 + 16;
		Iterator<NextTickListEntry> iterator9 = this.scheduledTickSet.iterator();

		while(iterator9.hasNext()) {
			NextTickListEntry nextTickListEntry10 = (NextTickListEntry)iterator9.next();
			if(nextTickListEntry10.xCoord >= i5 && nextTickListEntry10.xCoord < i6 && nextTickListEntry10.zCoord >= i7 && nextTickListEntry10.zCoord < i8) {
				if(z2) {
					this.scheduledTickTreeSet.remove(nextTickListEntry10);
					iterator9.remove();
				}

				if(arrayList3 == null) {
					arrayList3 = new ArrayList<NextTickListEntry>();
				}

				arrayList3.add(nextTickListEntry10);
			}
		}

		return arrayList3;
	}

	public void randomDisplayUpdates(int i1, int i2, int i3) {
		byte b4 = 16;
		Random random5 = new Random();

		for(int i6 = 0; i6 < 1000; ++i6) {
			int i7 = i1 + this.rand.nextInt(b4) - this.rand.nextInt(b4);
			int i8 = i2 + this.rand.nextInt(b4) - this.rand.nextInt(b4);
			int i9 = i3 + this.rand.nextInt(b4) - this.rand.nextInt(b4);
			int i10 = this.getBlockId(i7, i8, i9);
			if(i10 == 0 && this.rand.nextInt(8) > i8 && this.worldProvider.getWorldHasNoSky()) {
				this.spawnParticle("depthsuspend", (double)((float)i7 + this.rand.nextFloat()), (double)((float)i8 + this.rand.nextFloat()), (double)((float)i9 + this.rand.nextFloat()), 0.0D, 0.0D, 0.0D);
			} else if(i10 > 0) {
				Block.blocksList[i10].randomDisplayTick(this, i7, i8, i9, random5);
			}
		}

	}

	public List<Entity> getEntitiesWithinAABBExcludingEntity(Entity entity1, AxisAlignedBB axisAlignedBB2) {
		this.entitiesWithinAABBExcludingEntity.clear();
		int i3 = MathHelper.floor_double((axisAlignedBB2.minX - 2.0D) / 16.0D);
		int i4 = MathHelper.floor_double((axisAlignedBB2.maxX + 2.0D) / 16.0D);
		int i5 = MathHelper.floor_double((axisAlignedBB2.minZ - 2.0D) / 16.0D);
		int i6 = MathHelper.floor_double((axisAlignedBB2.maxZ + 2.0D) / 16.0D);

		for(int i7 = i3; i7 <= i4; ++i7) {
			for(int i8 = i5; i8 <= i6; ++i8) {
				if(this.chunkExists(i7, i8)) {
					this.getChunkFromChunkCoords(i7, i8).getEntitiesWithinAABBForEntity(entity1, axisAlignedBB2, this.entitiesWithinAABBExcludingEntity);
				}
			}
		}

		return this.entitiesWithinAABBExcludingEntity;
	}

	public List<Entity> getEntitiesWithinAABB(Class<?> clazz, AxisAlignedBB aabb) {
		int x1 = MathHelper.floor_double((aabb.minX - 2.0D) / 16.0D);
		int x2 = MathHelper.floor_double((aabb.maxX + 2.0D) / 16.0D);
		int z1 = MathHelper.floor_double((aabb.minZ - 2.0D) / 16.0D);
		int z2 = MathHelper.floor_double((aabb.maxZ + 2.0D) / 16.0D);
		ArrayList<Entity> list = new ArrayList<Entity>();

		for(int x = x1; x <= x2; ++x) {
			for(int z = z1; z <= z2; ++z) {
				if(this.chunkExists(x, z)) {
					this.getChunkFromChunkCoords(x, z).getEntitiesOfTypeWithinAAAB(clazz, aabb, list);
				}
			}
		}

		return list;
	}
	
	public List<Entity> getEntitiesWithinAABBbutNotMe(Entity entity, Class<?> clazz, AxisAlignedBB aabb) {
		List<Entity> list = this.getEntitiesWithinAABB(clazz, aabb);
		ListIterator<Entity> li = list.listIterator();
		while(li.hasNext()) {
			if(li.next().entityId == entity.entityId) {
				li.remove();
			}
		}
		
		return list;
	}

	public Entity findNearestEntityWithinAABB(Class<?> class1, AxisAlignedBB axisAlignedBB2, Entity entity3) {
		List<Entity> list4 = this.getEntitiesWithinAABB(class1, axisAlignedBB2);
		Entity entity5 = null;
		double d6 = Double.MAX_VALUE;
		Iterator<Entity> iterator8 = list4.iterator();

		while(iterator8.hasNext()) {
			Entity entity9 = (Entity)iterator8.next();
			if(entity9 != entity3) {
				double d10 = entity3.getDistanceSqToEntity(entity9);
				if(d10 <= d6) {
					entity5 = entity9;
					d6 = d10;
				}
			}
		}

		return entity5;
	}

	public List<Entity> getLoadedEntityList() {
		return this.loadedEntityList;
	}

	public void updateTileEntityChunkAndDoNothing(int i1, int i2, int i3, TileEntity tileEntity4) {
		if(this.blockExists(i1, i2, i3)) {
			this.getChunkFromBlockCoords(i1, i3).setChunkModified();
		}

		for(int i5 = 0; i5 < this.worldAccesses.size(); ++i5) {
			((IWorldAccess)this.worldAccesses.get(i5)).doNothingWithTileEntity(i1, i2, i3, tileEntity4);
		}

	}

	public int countEntities(Class<?> class1) {
		int i2 = 0;

		for(int i3 = 0; i3 < this.loadedEntityList.size(); ++i3) {
			Entity entity4 = (Entity)this.loadedEntityList.get(i3);
			if(class1.isAssignableFrom(entity4.getClass())) {
				++i2;
			}
		}

		return i2;
	}

	public void addLoadedEntities(List<Entity> list1) {
		this.loadedEntityList.addAll(list1);

		for(int i2 = 0; i2 < list1.size(); ++i2) {
			this.obtainEntitySkin((Entity)list1.get(i2));
		}

	}

	public void unloadEntities(List<Entity> list1) {
		this.unloadedEntityList.addAll(list1);
	}

	/**
	 * Returns true if the given Entity can be placed on the given side of the given
	 * block position.
	 */
	public boolean canPlaceEntityOnSide(int par1, int par2, int par3, int par4, boolean par5, int par6,
			Entity par7Entity) {
		int var8 = this.getBlockId(par2, par3, par4);
		Block var9 = Block.blocksList[var8];
		Block var10 = Block.blocksList[par1];
		AxisAlignedBB var11 = var10.getCollisionBoundingBoxFromPool(this, par2, par3, par4);

		if (par5) {
			var11 = null;
		}

		if (var11 != null && !this.checkIfAABBIsClearExcludingEntity(var11, par7Entity)) {
			return false;
		} else {
			if (var9 != null && (var9 == Block.waterMoving || var9 == Block.waterStill || var9 == Block.lavaMoving
					|| var9 == Block.lavaStill || var9 == Block.fire || var9.blockMaterial.isGroundCover())) {
				var9 = null;
			}

			/*
			if (var9 != null && var9.isBlockReplaceable(this, par2, par3, par4)) {
				var9 = null;
			}
			*/

			return par1 > 0 && var9 == null && var10.canPlaceBlockOnSide(this, par2, par3, par4, par6);
		}
	}
	
	public void dropOldChunks() {
		while(this.chunkProvider.unload100OldestChunks()) {
		}

	}

	public boolean canBlockBePlacedAt(int blockID, int x, int y, int z, boolean z5, int side, ItemStack itemStack) {
		
		int i7 = this.getBlockId(x, y, z);
		Block existingBlock = Block.blocksList[i7];
		Block placingBlock = Block.blocksList[blockID];
		AxisAlignedBB axisAlignedBB10 = placingBlock.getCollisionBoundingBoxFromPool(this, x, y, z);
		if(z5) {
			axisAlignedBB10 = null;
		}

		if(axisAlignedBB10 != null && !this.checkIfAABBIsClear(axisAlignedBB10)) {
			return false;
		} else {
			if(existingBlock != null && (
					existingBlock == Block.waterMoving || 
					existingBlock == Block.waterStill || 
					existingBlock == Block.lavaMoving || 
					existingBlock == Block.lavaStill || 
					existingBlock == Block.fire || 
					existingBlock.blockMaterial.isGroundCover() ||
					(existingBlock instanceof IPlant)
			)) {
				existingBlock = null;
			}
			
			return blockID > 0 && existingBlock == null && placingBlock.canPlaceBlockOnSide(this, x, y, z, side, itemStack);
		}
	}
	
	public boolean canBlockBePlacedAt(int blockID, int x, int y, int z, boolean z5, int side) {
		int i7 = this.getBlockId(x, y, z);
		Block existingBlock = Block.blocksList[i7];
		Block placingBlock = Block.blocksList[blockID];
		AxisAlignedBB axisAlignedBB10 = placingBlock.getCollisionBoundingBoxFromPool(this, x, y, z);
		if(z5) {
			axisAlignedBB10 = null;
		}

		if(axisAlignedBB10 != null && !this.checkIfAABBIsClear(axisAlignedBB10)) {
			return false;
		} else {
			if(existingBlock != null && (
					existingBlock == Block.waterMoving || 
					existingBlock == Block.waterStill || 
					existingBlock == Block.lavaMoving || 
					existingBlock == Block.lavaStill || 
					existingBlock == Block.fire ||
					existingBlock.blockMaterial.isGroundCover() ||
					(existingBlock instanceof IPlant)
				)) {
				existingBlock = null;
			}

			return blockID > 0 && existingBlock == null && placingBlock.canPlaceBlockOnSide(this, x, y, z, side);
		}
	}

	public PathEntity getPathEntityToEntity(Entity entity1, Entity entity2, float f3, boolean z4, boolean z5, boolean z6, boolean z7) {
		//Profiler.startSection("pathfind");
		int i8 = MathHelper.floor_double(entity1.posX);
		int i9 = MathHelper.floor_double(entity1.posY + 1.0D);
		int i10 = MathHelper.floor_double(entity1.posZ);
		int i11 = (int)(f3 + 16.0F);
		int i12 = i8 - i11;
		int i13 = i9 - i11;
		int i14 = i10 - i11;
		int i15 = i8 + i11;
		int i16 = i9 + i11;
		int i17 = i10 + i11;
		ChunkCache chunkCache18 = new ChunkCache(this, i12, i13, i14, i15, i16, i17);
		PathEntity pathEntity19 = (new PathFinder(chunkCache18, z4, z5, z6, z7)).createEntityPathTo(entity1, entity2, f3);
		//Profiler.endSection();
		return pathEntity19;
	}

	public PathEntity getEntityPathToXYZ(Entity entity1, int i2, int i3, int i4, float f5, boolean z6, boolean z7, boolean z8, boolean z9) {
		//Profiler.startSection("pathfind");
		int i10 = MathHelper.floor_double(entity1.posX);
		int i11 = MathHelper.floor_double(entity1.posY);
		int i12 = MathHelper.floor_double(entity1.posZ);
		int i13 = (int)(f5 + 8.0F);
		int i14 = i10 - i13;
		int i15 = i11 - i13;
		int i16 = i12 - i13;
		int i17 = i10 + i13;
		int i18 = i11 + i13;
		int i19 = i12 + i13;
		ChunkCache chunkCache20 = new ChunkCache(this, i14, i15, i16, i17, i18, i19);
		PathEntity pathEntity21 = (new PathFinder(chunkCache20, z6, z7, z8, z9)).createEntityPathTo(entity1, i2, i3, i4, f5);
		//Profiler.endSection();
		return pathEntity21;
	}

	public boolean isBlockProvidingPowerTo(int i1, int i2, int i3, int i4) {
		int i5 = this.getBlockId(i1, i2, i3);
		return i5 == 0 ? false : Block.blocksList[i5].isIndirectlyPoweringTo(this, i1, i2, i3, i4);
	}

	public boolean isBlockGettingPowered(int i1, int i2, int i3) {
		return this.isBlockProvidingPowerTo(i1, i2 - 1, i3, 0) ? true : (this.isBlockProvidingPowerTo(i1, i2 + 1, i3, 1) ? true : (this.isBlockProvidingPowerTo(i1, i2, i3 - 1, 2) ? true : (this.isBlockProvidingPowerTo(i1, i2, i3 + 1, 3) ? true : (this.isBlockProvidingPowerTo(i1 - 1, i2, i3, 4) ? true : this.isBlockProvidingPowerTo(i1 + 1, i2, i3, 5)))));
	}

	public boolean isBlockIndirectlyProvidingPowerTo(int i1, int i2, int i3, int i4) {
		if(this.isBlockNormalCube(i1, i2, i3)) {
			return this.isBlockGettingPowered(i1, i2, i3);
		} else {
			int i5 = this.getBlockId(i1, i2, i3);
			return i5 == 0 ? false : Block.blocksList[i5].isPoweringTo(this, i1, i2, i3, i4);
		}
	}

	public boolean isBlockIndirectlyGettingPowered(int i1, int i2, int i3) {
		return this.isBlockIndirectlyProvidingPowerTo(i1, i2 - 1, i3, 0) ? true : (this.isBlockIndirectlyProvidingPowerTo(i1, i2 + 1, i3, 1) ? true : (this.isBlockIndirectlyProvidingPowerTo(i1, i2, i3 - 1, 2) ? true : (this.isBlockIndirectlyProvidingPowerTo(i1, i2, i3 + 1, 3) ? true : (this.isBlockIndirectlyProvidingPowerTo(i1 - 1, i2, i3, 4) ? true : this.isBlockIndirectlyProvidingPowerTo(i1 + 1, i2, i3, 5)))));
	}

	public EntityPlayer getClosestPlayerToEntity(Entity entity1, double d2) {
		return this.getClosestPlayer(entity1.posX, entity1.posY, entity1.posZ, d2);
	}

	public EntityPlayer getClosestPlayer(double d1, double d3, double d5, double d7) {
		double d9 = -1.0D;
		EntityPlayer entityPlayer11 = null;

		for(int i12 = 0; i12 < this.playerEntities.size(); ++i12) {
			EntityPlayer entityPlayer13 = (EntityPlayer)this.playerEntities.get(i12);
			double d14 = entityPlayer13.getDistanceSq(d1, d3, d5);
			if((d7 < 0.0D || d14 < d7 * d7) && (d9 == -1.0D || d14 < d9)) {
				d9 = d14;
				entityPlayer11 = entityPlayer13;
			}
		}

		return entityPlayer11;
	}

	public EntityPlayer func_48456_a(double d1, double d3, double d5) {
		double d7 = -1.0D;
		EntityPlayer entityPlayer9 = null;

		for(int i10 = 0; i10 < this.playerEntities.size(); ++i10) {
			EntityPlayer entityPlayer11 = (EntityPlayer)this.playerEntities.get(i10);
			double d12 = entityPlayer11.getDistanceSq(d1, entityPlayer11.posY, d3);
			if((d5 < 0.0D || d12 < d5 * d5) && (d7 == -1.0D || d12 < d7)) {
				d7 = d12;
				entityPlayer9 = entityPlayer11;
			}
		}

		return entityPlayer9;
	}

	public EntityPlayer getClosestVulnerablePlayerToEntity(Entity entity1, double d2) {
		return this.getClosestVulnerablePlayer(entity1.posX, entity1.posY, entity1.posZ, d2);
	}

	public EntityPlayer getClosestVulnerablePlayer(double d1, double d3, double d5, double d7) {
		double d9 = -1.0D;
		EntityPlayer entityPlayer11 = null;

		for(int i12 = 0; i12 < this.playerEntities.size(); ++i12) {
			EntityPlayer entityPlayer13 = (EntityPlayer)this.playerEntities.get(i12);
			if(!entityPlayer13.capabilities.disableDamage) {
				double d14 = entityPlayer13.getDistanceSq(d1, d3, d5);
				if((d7 < 0.0D || d14 < d7 * d7) && (d9 == -1.0D || d14 < d9)) {
					d9 = d14;
					entityPlayer11 = entityPlayer13;
				}
			}
		}

		return entityPlayer11;
	}

	public EntityPlayer getPlayerEntityByName(String string1) {
		for(int i2 = 0; i2 < this.playerEntities.size(); ++i2) {
			if(string1.equals(((EntityPlayer)this.playerEntities.get(i2)).username)) {
				return (EntityPlayer)this.playerEntities.get(i2);
			}
		}

		return null;
	}

	public void sendQuittingDisconnectingPacket() {
	}

	public void checkSessionLock() {
		this.saveHandler.checkSessionLock();
	}

	public void setWorldTime(long j1) {
		this.worldInfo.setWorldTime(j1);
	}

	public void advanceTime(long j1) {
		long j3 = j1 - this.worldInfo.getWorldTime();

		NextTickListEntry nextTickListEntry6;
		for(Iterator<NextTickListEntry> iterator5 = this.scheduledTickSet.iterator(); iterator5.hasNext(); nextTickListEntry6.scheduledTime += j3) {
			nextTickListEntry6 = (NextTickListEntry)iterator5.next();
		}

		this.setWorldTime(j1);
	}

	public long getSeed() {
		return this.worldInfo.getSeed();
	}

	public long getWorldTime() {
		return this.worldInfo.getWorldTime();
	}

	public ChunkCoordinates getSpawnPoint() {
		return new ChunkCoordinates(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());
	}

	public void setSpawnPoint(ChunkCoordinates chunkCoordinates1) {
		this.worldInfo.setSpawnPosition(chunkCoordinates1.posX, chunkCoordinates1.posY, chunkCoordinates1.posZ);
	}

	public void joinEntityInSurroundings(Entity entity1) {
		int i2 = MathHelper.floor_double(entity1.posX / 16.0D);
		int i3 = MathHelper.floor_double(entity1.posZ / 16.0D);
		byte b4 = 2;

		for(int i5 = i2 - b4; i5 <= i2 + b4; ++i5) {
			for(int i6 = i3 - b4; i6 <= i3 + b4; ++i6) {
				this.getChunkFromChunkCoords(i5, i6);
			}
		}

		if(!this.loadedEntityList.contains(entity1)) {
			this.loadedEntityList.add(entity1);
		}

	}

	public boolean canMineBlock(EntityPlayer entityPlayer1, int i2, int i3, int i4) {
		return true;
	}

	public void setEntityState(Entity entity1, byte b2) {
	}

	public void updateEntityList() {
		this.loadedEntityList.removeAll(this.unloadedEntityList);

		int i1;
		Entity entity2;
		int i3;
		int i4;
		for(i1 = 0; i1 < this.unloadedEntityList.size(); ++i1) {
			entity2 = (Entity)this.unloadedEntityList.get(i1);
			i3 = entity2.chunkCoordX;
			i4 = entity2.chunkCoordZ;
			if(entity2.addedToChunk && this.chunkExists(i3, i4)) {
				this.getChunkFromChunkCoords(i3, i4).removeEntity(entity2);
			}
		}

		for(i1 = 0; i1 < this.unloadedEntityList.size(); ++i1) {
			this.releaseEntitySkin((Entity)this.unloadedEntityList.get(i1));
		}

		this.unloadedEntityList.clear();

		for(i1 = 0; i1 < this.loadedEntityList.size(); ++i1) {
			entity2 = (Entity)this.loadedEntityList.get(i1);
			if(entity2.ridingEntity != null) {
				if(!entity2.ridingEntity.isDead && entity2.ridingEntity.riddenByEntity == entity2) {
					continue;
				}

				entity2.ridingEntity.riddenByEntity = null;
				entity2.ridingEntity = null;
			}

			if(entity2.isDead) {
				i3 = entity2.chunkCoordX;
				i4 = entity2.chunkCoordZ;
				if(entity2.addedToChunk && this.chunkExists(i3, i4)) {
					this.getChunkFromChunkCoords(i3, i4).removeEntity(entity2);
				}

				this.loadedEntityList.remove(i1--);
				this.releaseEntitySkin(entity2);
			}
		}

	}

	public IChunkProvider getChunkProvider() {
		return this.chunkProvider;
	}

	public void playNoteAt(int i1, int i2, int i3, int i4, int i5) {
		int i6 = this.getBlockId(i1, i2, i3);
		if(i6 > 0) {
			Block.blocksList[i6].powerBlock(this, i1, i2, i3, i4, i5);
		}

	}

	public ISaveHandler getSaveHandler() {
		return this.saveHandler;
	}

	public WorldInfo getWorldInfo() {
		return this.worldInfo;
	}

	public void updateAllPlayersSleepingFlag() {
		this.allPlayersSleeping = !this.playerEntities.isEmpty();
		Iterator<EntityPlayer> iterator1 = this.playerEntities.iterator();

		while(iterator1.hasNext()) {
			EntityPlayer entityPlayer2 = (EntityPlayer)iterator1.next();
			if(!entityPlayer2.isPlayerSleeping()) {
				this.allPlayersSleeping = false;
				break;
			}
		}

	}

	protected void wakeUpAllPlayers() {
		this.allPlayersSleeping = false;
		Iterator<EntityPlayer> iterator1 = this.playerEntities.iterator();

		while(iterator1.hasNext()) {
			EntityPlayer entityPlayer2 = (EntityPlayer)iterator1.next();
			if(entityPlayer2.isPlayerSleeping()) {
				entityPlayer2.wakeUpPlayer(false, false, true);
			}
		}

		this.clearWeather();
	}

	public boolean isAllPlayersFullyAsleep() {
		if(this.allPlayersSleeping && !this.isRemote) {
			Iterator<EntityPlayer> iterator1 = this.playerEntities.iterator();

			EntityPlayer entityPlayer2;
			do {
				if(!iterator1.hasNext()) {
					return true;
				}

				entityPlayer2 = (EntityPlayer)iterator1.next();
			} while(entityPlayer2.isPlayerFullyAsleep());

			return false;
		} else {
			return false;
		}
	}

	public float getWeightedThunderStrength(float f1) {
		return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * f1) * this.getRainStrength(f1);
	}

	public float getRainStrength(float f1) {
		return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * f1;
	}
	
	public float getSnowStrength(float par1) {
		return this.prevSnowingStrength + (this.snowingStrength - this.prevSnowingStrength) * par1;
	}
	
	public float getSandstormingStrength(float f1) {
		return this.prevSandstormingStrength + (this.sandstormingStrength - this.prevSandstormingStrength) * f1;
	}

	public void setRainStrength(float f1) {
		this.prevRainingStrength = f1;
		this.rainingStrength = f1;
	}
	
	public void setSnowingStrength(float f1) {
		this.prevSnowingStrength = f1;
		this.snowingStrength = f1;
	}
	
	public void setThunderingStrength(float f1) {
		this.prevThunderingStrength = f1;
		this.thunderingStrength = f1;
	}
	
	public boolean isThundering() {
		return this.getWeightedThunderStrength(1.0F) > 0.9F;
	}

	public boolean isRaining() {
		return this.getRainStrength(1.0F) > 0.2F;
	}
	
	public boolean isSandstorming() {
		return this.getSandstormingStrength(1.0F) > 0.2F;
	}
	
	public boolean isSnowing() {
		return this.getSnowStrength(1.0F) > 0.2F;
	}

	public float getFogIntensity(float renderPartialTicks) {
		// Morning fog
		if(this.worldInfo.isEnableSeasons()) {
			
			// Must be 0 from 0 to 0.625,
			// lerp to 1 from 0.625 to 0.75,
			// lerp to 0 from 0.75 to 1.
			
			float celestialAngle = this.getCelestialAngle(renderPartialTicks);
			
			if(celestialAngle < 0.625F) return 0F;
			if(celestialAngle < 0.75F) return MathHelper.lerp(0F, Seasons.getMaxMorningFogIntensityForToday(), (celestialAngle - 0.625F) / 0.125F);
			if(celestialAngle < 0.825F) return Seasons.getMaxMorningFogIntensityForToday();
			return MathHelper.lerp(Seasons.getMaxMorningFogIntensityForToday(), 0F, (celestialAngle - 0.825F) / 0.125F);
			
		} else {
			return 0F;
		}
	}
	
	public boolean canLightningStrikeAt(int i1, int i2, int i3) {
		if(!this.isRaining()) {
			return false;
		} else if(!this.canBlockSeeTheSky(i1, i2, i3)) {
			return false;
		} else if(this.getPrecipitationHeight(i1, i3) > i2) {
			return false;
		} else {
			BiomeGenBase biomeGenBase4 = this.getBiomeGenForCoords(i1, i3);
			return biomeGenBase4.getEnableSnow() ? false : biomeGenBase4.canSpawnLightningBolt();
		}
	}

	public boolean isBlockHighHumidity(int i1, int i2, int i3) {
		BiomeGenBase biomeGenBase4 = this.getBiomeGenForCoords(i1, i3);
		return biomeGenBase4.isHighHumidity();
	}
	
	public void setItemData(String string1, WorldSavedData worldSavedData2) {
		this.mapStorage.setData(string1, worldSavedData2);
	}

	public WorldSavedData loadItemData(Class<?> class1, String string2) {
		return this.mapStorage.loadData(class1, string2);
	}

	public int getUniqueDataId(String string1) {
		return this.mapStorage.getUniqueDataId(string1);
	}

	public void playAuxSFX(int i1, int i2, int i3, int i4, int i5) {
		this.playAuxSFXAtEntity((EntityPlayer)null, i1, i2, i3, i4, i5);
	}

	public void playAuxSFXAtEntity(EntityPlayer entityPlayer1, int i2, int i3, int i4, int i5, int i6) {
		for(int i7 = 0; i7 < this.worldAccesses.size(); ++i7) {
			((IWorldAccess)this.worldAccesses.get(i7)).playAuxSFX(entityPlayer1, i2, i3, i4, i5, i6);
		}

	}

	public int getHeight() {
		return 256;
	}

	public Random setRandomSeed(int i1, int i2, int i3) {
		long j4 = (long)i1 * 341873128712L + (long)i2 * 132897987541L + this.getWorldInfo().getSeed() + (long)i3;
		this.rand.setSeed(j4);
		return this.rand;
	}

	public SpawnListEntry getRandomMob(EnumCreatureType enumCreatureType1, int i2, int i3, int i4) {
		List<SpawnListEntry> list5 = this.getChunkProvider().getPossibleCreatures(enumCreatureType1, i2, i3, i4);
		return list5 != null && !list5.isEmpty() ? (SpawnListEntry)WeightedRandom.getRandomItem(this.rand, (Collection<?>)list5) : null;
	}

	public ChunkPosition findClosestStructure(String string1, int i2, int i3, int i4) {
		return this.getChunkProvider().findClosestStructure(this, string1, i2, i3, i4);
	}

	public boolean getAreChunksEmpty() {
		return false;
	}

	public double getSeaLevelForRendering() {
		return this.worldProvider.getSeaLevelForRendering();
	}

	public boolean doesBlockHaveSolidTopSurface(int par1, int par2, int par3) {
        Block var4 = Block.blocksList[this.getBlockId(par1, par2, par3)];
        return var4 == null ? false : (var4.blockMaterial.isOpaque() && var4.renderAsNormalBlock() ? true : (var4 instanceof BlockStairs ? (this.getBlockMetadata(par1, par2, par3) & 4) == 4 : (var4 instanceof BlockStep ? (this.getBlockMetadata(par1, par2, par3) & 8) == 8 : false)));
	}

	public void setBlockToAir(int x, int y, int z) {
		this.setBlock(x, y, z, 0);
	}

	public int getBlockOwnLightValue(int x, int y, int z) {
		Block block = Block.blocksList[this.getBlockId(x, y, z)];
		if(block == null) return 0;
		return block.getLightValue(this.getBlockMetadata(x, y, z));
	}

	public int getMaxNumberOfCreature(EnumCreatureType enumCreatureType) {
		return this.worldProvider.getMaxNumberOfCreature(enumCreatureType);
	}

	public boolean setBlockAndMetadataColumn(int x, int y, int z, int[] id) {
		if(y < 0) return false;
		return this.getChunkFromChunkCoords(x >> 4, z >> 4).setBlockIDAndMetadataColumn(x & 15, y, z & 15, id);
	}

	public List<EntityPlayer> getPlayersInRangeFrom(double x, double y, double z, double range) {
		List<EntityPlayer> playersInRange = new ArrayList<EntityPlayer>();
		double rangeSq = range * range;
		
		Iterator<EntityPlayer> iterator = this.playerEntities.iterator();
		while(iterator.hasNext()) {
			EntityPlayer entityPlayer = iterator.next();
			double distanceSq = entityPlayer.getDistanceSq(x, y, z);
			if(distanceSq < rangeSq) {
				playersInRange.add(entityPlayer);
			}
		}
		
		return playersInRange;
	}
	
	// Future: override those in WorldServer with the right thing?
	public List<EntityPlayer> findPlayers(ChunkCoordinates coords, int maxRange, String name) {
		if(this.playerEntities.isEmpty()) {
			return null;
		} else {
			ArrayList<EntityPlayer> result = new ArrayList<EntityPlayer>();
			int maxRangeSq = maxRange * maxRange;
			
			for(int i = 0; i < this.playerEntities.size(); ++i) {
				EntityPlayer curPlayer = (EntityPlayer)this.playerEntities.get(i);
				
				boolean negMatch;
				if(name != null) {
					negMatch = name.startsWith("!");
					if(negMatch) {
						name = name.substring(1);
					}

					if(negMatch == name.equalsIgnoreCase(curPlayer.username)) {
						continue;
					}
				}

				if(coords != null && maxRange > 0) {
					float distanceSq = coords.getDistanceSquaredToChunkCoordinates(curPlayer.getPlayerCoordinates());
					if(distanceSq > (float)maxRangeSq) {
						continue;
					}
				}

				result.add(curPlayer);
			}

			if(coords != null) {
				Collections.sort(result, new PlayerPositionComparator(coords));
			}

			return result;
		}
	}

	public EntityPlayer getPlayerForUsername(String var1) {
		if(var1 == null) return null;
		for(int i = 0; i < this.playerEntities.size(); i ++) {
			EntityPlayer player = playerEntities.get(i);
			if(var1.equals(player.username)) return player;
		}
		return null;
	}

	public String[] getAllUsernames() {
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0; i < this.playerEntities.size(); i ++) {
			list.add(this.playerEntities.get(i).username);
		}
		return (String[]) list.toArray();
	}
}
