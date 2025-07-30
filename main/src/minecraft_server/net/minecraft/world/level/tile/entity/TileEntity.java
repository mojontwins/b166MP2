package net.minecraft.world.level.tile.entity;

import java.util.HashMap;
import java.util.Map;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.network.packet.Packet;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class TileEntity {
	private static Map<String, Class<?>> nameToClassMap = new HashMap<String, Class<?>>();
	private static Map<Class<?>, String> classToNameMap = new HashMap<Class<?>, String>();
	public World worldObj;
	public int xCoord;
	public int yCoord;
	public int zCoord;
	protected boolean tileEntityInvalid;
	public int blockMetadata = -1;
	public Block blockType;

	@SuppressWarnings("unlikely-arg-type")
	private static void addMapping(Class<?> class0, String string1) {
		if(classToNameMap.containsKey(string1)) {
			throw new IllegalArgumentException("Duplicate id: " + string1);
		} else {
			nameToClassMap.put(string1, class0);
			classToNameMap.put(class0, string1);
		}
	}

	public void readFromNBT(NBTTagCompound compoundTag) {
		this.xCoord = compoundTag.getInteger("x");
		this.yCoord = compoundTag.getInteger("y");
		this.zCoord = compoundTag.getInteger("z");
	}

	public void writeToNBT(NBTTagCompound compoundTag) {
		String string2 = (String)classToNameMap.get(this.getClass());
		if(string2 == null) {
			throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
		} else {
			compoundTag.setString("id", string2);
			compoundTag.setInteger("x", this.xCoord);
			compoundTag.setInteger("y", this.yCoord);
			compoundTag.setInteger("z", this.zCoord);
		}
	}

	public void updateEntity() {
	}

	public static TileEntity createAndLoadEntity(NBTTagCompound nBTTagCompound0) {
		TileEntity tileEntity1 = null;

		try {
			Class<?> class2 = (Class<?>)nameToClassMap.get(nBTTagCompound0.getString("id"));
			if(class2 != null) {
				tileEntity1 = (TileEntity)class2.newInstance();
			}
		} catch (Exception exception3) {
			exception3.printStackTrace();
		}

		if(tileEntity1 != null) {
			tileEntity1.readFromNBT(nBTTagCompound0);
		} else {
			System.out.println("Skipping TileEntity with id " + nBTTagCompound0.getString("id"));
		}

		return tileEntity1;
	}

	public int getBlockMetadata() {
		if(this.blockMetadata == -1) {
			this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
		}

		return this.blockMetadata;
	}

	public void onInventoryChanged() {
		if(this.worldObj != null) {
			this.blockMetadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
			this.worldObj.updateTileEntityChunkAndDoNothing(this.xCoord, this.yCoord, this.zCoord, this);
		}

	}

	public double getDistanceFrom(double d1, double d3, double d5) {
		double d7 = (double)this.xCoord + 0.5D - d1;
		double d9 = (double)this.yCoord + 0.5D - d3;
		double d11 = (double)this.zCoord + 0.5D - d5;
		return d7 * d7 + d9 * d9 + d11 * d11;
	}

	public Block getBlockType() {
		if(this.blockType == null) {
			this.blockType = Block.blocksList[this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord)];
		}

		return this.blockType;
	}

	public Packet getDescriptionPacket() {
		return null;
	}

	public boolean isInvalid() {
		return this.tileEntityInvalid;
	}

	public void invalidate() {
		this.tileEntityInvalid = true;
	}

	public void validate() {
		this.tileEntityInvalid = false;
	}

	public void onTileEntityPowered(int i1, int i2) {
	}

	public void updateContainingBlockInfo() {
		this.blockType = null;
		this.blockMetadata = -1;
	}

	static {
		addMapping(TileEntityFurnace.class, "Furnace");
		addMapping(TileEntityChest.class, "Chest");
		addMapping(TileEntityRecordPlayer.class, "RecordPlayer");
		addMapping(TileEntityDispenser.class, "Trap");
		addMapping(TileEntitySign.class, "Sign");
		addMapping(TileEntityMobSpawner.class, "MobSpawner");
		addMapping(TileEntityMobSpawnerOneshot.class, "MobSpawnerOneshot");		
		addMapping(TileEntityNote.class, "Music");
		
	}
}
