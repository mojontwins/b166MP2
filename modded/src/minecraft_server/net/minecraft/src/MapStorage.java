package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapStorage {
	private ISaveHandler field_28180_a;
	private Map field_28179_b = new HashMap();
	private List field_28182_c = new ArrayList();
	private Map field_28181_d = new HashMap();

	public MapStorage(ISaveHandler iSaveHandler1) {
		this.field_28180_a = iSaveHandler1;
		this.func_28174_b();
	}

	public MapDataBase func_28178_a(Class class1, String string2) {
		MapDataBase mapDataBase3 = (MapDataBase)this.field_28179_b.get(string2);
		if(mapDataBase3 != null) {
			return mapDataBase3;
		} else {
			if(this.field_28180_a != null) {
				try {
					File file4 = this.field_28180_a.func_28111_b(string2);
					if(file4 != null && file4.exists()) {
						try {
							mapDataBase3 = (MapDataBase)class1.getConstructor(new Class[]{String.class}).newInstance(new Object[]{string2});
						} catch (Exception exception7) {
							throw new RuntimeException("Failed to instantiate " + class1.toString(), exception7);
						}

						FileInputStream fileInputStream5 = new FileInputStream(file4);
						NBTTagCompound nBTTagCompound6 = CompressedStreamTools.func_770_a(fileInputStream5);
						fileInputStream5.close();
						mapDataBase3.func_28148_a(nBTTagCompound6.getCompoundTag("data"));
					}
				} catch (Exception exception8) {
					exception8.printStackTrace();
				}
			}

			if(mapDataBase3 != null) {
				this.field_28179_b.put(string2, mapDataBase3);
				this.field_28182_c.add(mapDataBase3);
			}

			return mapDataBase3;
		}
	}

	public void func_28177_a(String string1, MapDataBase mapDataBase2) {
		if(mapDataBase2 == null) {
			throw new RuntimeException("Can\'t set null data");
		} else {
			if(this.field_28179_b.containsKey(string1)) {
				this.field_28182_c.remove(this.field_28179_b.remove(string1));
			}

			this.field_28179_b.put(string1, mapDataBase2);
			this.field_28182_c.add(mapDataBase2);
		}
	}

	public void func_28176_a() {
		for(int i1 = 0; i1 < this.field_28182_c.size(); ++i1) {
			MapDataBase mapDataBase2 = (MapDataBase)this.field_28182_c.get(i1);
			if(mapDataBase2.func_28150_b()) {
				this.func_28175_a(mapDataBase2);
				mapDataBase2.func_28149_a(false);
			}
		}

	}

	private void func_28175_a(MapDataBase mapDataBase1) {
		if(this.field_28180_a != null) {
			try {
				File file2 = this.field_28180_a.func_28111_b(mapDataBase1.field_28152_a);
				if(file2 != null) {
					NBTTagCompound nBTTagCompound3 = new NBTTagCompound();
					mapDataBase1.func_28147_b(nBTTagCompound3);
					NBTTagCompound nBTTagCompound4 = new NBTTagCompound();
					nBTTagCompound4.setCompoundTag("data", nBTTagCompound3);
					FileOutputStream fileOutputStream5 = new FileOutputStream(file2);
					CompressedStreamTools.writeGzippedCompoundToOutputStream(nBTTagCompound4, fileOutputStream5);
					fileOutputStream5.close();
				}
			} catch (Exception exception6) {
				exception6.printStackTrace();
			}

		}
	}

	private void func_28174_b() {
		try {
			this.field_28181_d.clear();
			if(this.field_28180_a == null) {
				return;
			}

			File file1 = this.field_28180_a.func_28111_b("idcounts");
			if(file1 != null && file1.exists()) {
				DataInputStream dataInputStream2 = new DataInputStream(new FileInputStream(file1));
				NBTTagCompound nBTTagCompound3 = CompressedStreamTools.func_774_a(dataInputStream2);
				dataInputStream2.close();
				Iterator iterator4 = nBTTagCompound3.func_28107_c().iterator();

				while(iterator4.hasNext()) {
					NBTBase nBTBase5 = (NBTBase)iterator4.next();
					if(nBTBase5 instanceof NBTTagShort) {
						NBTTagShort nBTTagShort6 = (NBTTagShort)nBTBase5;
						String string7 = nBTTagShort6.getKey();
						short s8 = nBTTagShort6.shortValue;
						this.field_28181_d.put(string7, s8);
					}
				}
			}
		} catch (Exception exception9) {
			exception9.printStackTrace();
		}

	}

	public int func_28173_a(String string1) {
		Short short2 = (Short)this.field_28181_d.get(string1);
		if(short2 == null) {
			short2 = (short)0;
		} else {
			short2 = (short)(short2.shortValue() + 1);
		}

		this.field_28181_d.put(string1, short2);
		if(this.field_28180_a == null) {
			return short2.shortValue();
		} else {
			try {
				File file3 = this.field_28180_a.func_28111_b("idcounts");
				if(file3 != null) {
					NBTTagCompound nBTTagCompound4 = new NBTTagCompound();
					Iterator iterator5 = this.field_28181_d.keySet().iterator();

					while(iterator5.hasNext()) {
						String string6 = (String)iterator5.next();
						short s7 = ((Short)this.field_28181_d.get(string6)).shortValue();
						nBTTagCompound4.setShort(string6, s7);
					}

					DataOutputStream dataOutputStream9 = new DataOutputStream(new FileOutputStream(file3));
					CompressedStreamTools.func_771_a(nBTTagCompound4, dataOutputStream9);
					dataOutputStream9.close();
				}
			} catch (Exception exception8) {
				exception8.printStackTrace();
			}

			return short2.shortValue();
		}
	}
}
