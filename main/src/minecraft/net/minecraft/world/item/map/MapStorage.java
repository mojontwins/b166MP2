package net.minecraft.world.item.map;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mojang.nbt.CompressedStreamTools;
import com.mojang.nbt.NBTBase;
import com.mojang.nbt.NBTTagCompound;
import com.mojang.nbt.NBTTagShort;

import net.minecraft.world.level.WorldSavedData;
import net.minecraft.world.level.chunk.storage.ISaveHandler;

public class MapStorage {
	private ISaveHandler saveHandler;
	private Map<String, WorldSavedData> loadedDataMap = new HashMap<String, WorldSavedData>();
	private List<WorldSavedData> loadedDataList = new ArrayList<WorldSavedData>();
	private Map<String, Short> idCounts = new HashMap<String, Short>();

	public MapStorage(ISaveHandler iSaveHandler1) {
		this.saveHandler = iSaveHandler1;
		this.loadIdCounts();
	}

	public WorldSavedData loadData(Class<?> class1, String string2) {
		WorldSavedData worldSavedData3 = (WorldSavedData)this.loadedDataMap.get(string2);
		if(worldSavedData3 != null) {
			return worldSavedData3;
		} else {
			if(this.saveHandler != null) {
				try {
					File file4 = this.saveHandler.getMapFileFromName(string2);
					if(file4 != null && file4.exists()) {
						try {
							worldSavedData3 = (WorldSavedData)class1.getConstructor(new Class[]{String.class}).newInstance(new Object[]{string2});
						} catch (Exception exception7) {
							throw new RuntimeException("Failed to instantiate " + class1.toString(), exception7);
						}

						FileInputStream fileInputStream5 = new FileInputStream(file4);
						NBTTagCompound nBTTagCompound6 = CompressedStreamTools.readCompressed(fileInputStream5);
						fileInputStream5.close();
						worldSavedData3.readFromNBT(nBTTagCompound6.getCompoundTag("data"));
					}
				} catch (Exception exception8) {
					exception8.printStackTrace();
				}
			}

			if(worldSavedData3 != null) {
				this.loadedDataMap.put(string2, worldSavedData3);
				this.loadedDataList.add(worldSavedData3);
			}

			return worldSavedData3;
		}
	}

	public void setData(String string1, WorldSavedData worldSavedData2) {
		if(worldSavedData2 == null) {
			throw new RuntimeException("Can\'t set null data");
		} else {
			if(this.loadedDataMap.containsKey(string1)) {
				this.loadedDataList.remove(this.loadedDataMap.remove(string1));
			}

			this.loadedDataMap.put(string1, worldSavedData2);
			this.loadedDataList.add(worldSavedData2);
		}
	}

	public void saveAllData() {
		for(int i1 = 0; i1 < this.loadedDataList.size(); ++i1) {
			WorldSavedData worldSavedData2 = (WorldSavedData)this.loadedDataList.get(i1);
			if(worldSavedData2.isDirty()) {
				this.saveData(worldSavedData2);
				worldSavedData2.setDirty(false);
			}
		}

	}

	private void saveData(WorldSavedData worldSavedData1) {
		if(this.saveHandler != null) {
			try {
				File file2 = this.saveHandler.getMapFileFromName(worldSavedData1.mapName);
				if(file2 != null) {
					NBTTagCompound nBTTagCompound3 = new NBTTagCompound();
					worldSavedData1.writeToNBT(nBTTagCompound3);
					NBTTagCompound nBTTagCompound4 = new NBTTagCompound();
					nBTTagCompound4.setCompoundTag("data", nBTTagCompound3);
					FileOutputStream fileOutputStream5 = new FileOutputStream(file2);
					CompressedStreamTools.writeCompressed(nBTTagCompound4, fileOutputStream5);
					fileOutputStream5.close();
				}
			} catch (Exception exception6) {
				exception6.printStackTrace();
			}

		}
	}

	private void loadIdCounts() {
		try {
			this.idCounts.clear();
			if(this.saveHandler == null) {
				return;
			}

			File file1 = this.saveHandler.getMapFileFromName("idcounts");
			if(file1 != null && file1.exists()) {
				DataInputStream dataInputStream2 = new DataInputStream(new FileInputStream(file1));
				NBTTagCompound nBTTagCompound3 = CompressedStreamTools.read((DataInput)dataInputStream2);
				dataInputStream2.close();
				Iterator<?> iterator4 = nBTTagCompound3.getTags().iterator();

				while(iterator4.hasNext()) {
					NBTBase nBTBase5 = (NBTBase)iterator4.next();
					if(nBTBase5 instanceof NBTTagShort) {
						NBTTagShort nBTTagShort6 = (NBTTagShort)nBTBase5;
						String string7 = nBTTagShort6.getName();
						short s8 = nBTTagShort6.data;
						this.idCounts.put(string7, s8);
					}
				}
			}
		} catch (Exception exception9) {
			exception9.printStackTrace();
		}

	}

	public int getUniqueDataId(String string1) {
		Short short2 = (Short)this.idCounts.get(string1);
		if(short2 == null) {
			short2 = (short)0;
		} else {
			short2 = (short)(short2.shortValue() + 1);
		}

		this.idCounts.put(string1, short2);
		if(this.saveHandler == null) {
			return short2.shortValue();
		} else {
			try {
				File file3 = this.saveHandler.getMapFileFromName("idcounts");
				if(file3 != null) {
					NBTTagCompound nBTTagCompound4 = new NBTTagCompound();
					Iterator<String> iterator5 = this.idCounts.keySet().iterator();

					while(iterator5.hasNext()) {
						String string6 = (String)iterator5.next();
						short s7 = ((Short)this.idCounts.get(string6)).shortValue();
						nBTTagCompound4.setShort(string6, s7);
					}

					DataOutputStream dataOutputStream9 = new DataOutputStream(new FileOutputStream(file3));
					CompressedStreamTools.write(nBTTagCompound4, (DataOutput)dataOutputStream9);
					dataOutputStream9.close();
				}
			} catch (Exception exception8) {
				exception8.printStackTrace();
			}

			return short2.shortValue();
		}
	}
}
