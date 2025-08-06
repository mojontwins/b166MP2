package net.minecraft.client.skins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.GameSettingsValues;
import net.minecraft.client.Minecraft;

public class TexturePackList {
	private List<TexturePackBase> availableTexturePacks = new ArrayList<TexturePackBase>();
	private TexturePackBase defaultTexturePack = new TexturePackDefault();
	public TexturePackBase selectedTexturePack;
	private Map<String, TexturePackBase> field_6538_d = new HashMap<String, TexturePackBase>();
	private Minecraft mc;
	private File texturePackDir;
	private String currentTexturePack;

	public TexturePackList(Minecraft mc, File file2) {
		this.mc = mc;
		this.texturePackDir = new File(file2, "texturepacks");
		if(this.texturePackDir.exists()) {
			if(!this.texturePackDir.isDirectory()) {
				this.texturePackDir.delete();
				this.texturePackDir.mkdirs();
			}
		} else {
			this.texturePackDir.mkdirs();
		}

		this.currentTexturePack = GameSettingsValues.skin;
		this.updateAvaliableTexturePacks();
		this.selectedTexturePack.func_6482_a();
	}

	public boolean setTexturePack(TexturePackBase texturePackBase1) {
		if(texturePackBase1 == this.selectedTexturePack) {
			return false;
		} else {
			this.selectedTexturePack.closeTexturePackFile();
			this.currentTexturePack = texturePackBase1.texturePackFileName;
			this.selectedTexturePack = texturePackBase1;
			GameSettingsValues.skin = this.currentTexturePack;
			this.mc.gameSettings.saveOptions();
			this.selectedTexturePack.func_6482_a();
			return true;
		}
	}

	public void updateAvaliableTexturePacks() {
		ArrayList<TexturePackBase> arrayList1 = new ArrayList<TexturePackBase>();
		this.selectedTexturePack = null;
		arrayList1.add(this.defaultTexturePack);
		if(this.texturePackDir.exists() && this.texturePackDir.isDirectory()) {
			File[] file2 = this.texturePackDir.listFiles();
			File[] file3 = file2;
			int i4 = file2.length;

			for(int i5 = 0; i5 < i4; ++i5) {
				File file6 = file3[i5];
				String string7;
				TexturePackBase texturePackBase13;
				if(file6.isFile() && file6.getName().toLowerCase().endsWith(".zip")) {
					string7 = file6.getName() + ":" + file6.length() + ":" + file6.lastModified();

					try {
						if(!this.field_6538_d.containsKey(string7)) {
							TexturePackCustom texturePackCustom14 = new TexturePackCustom(file6);
							texturePackCustom14.texturePackID = string7;
							this.field_6538_d.put(string7, texturePackCustom14);
							texturePackCustom14.func_6485_a(this.mc);
						}

						texturePackBase13 = (TexturePackBase)this.field_6538_d.get(string7);
						if(texturePackBase13.texturePackFileName.equals(this.currentTexturePack)) {
							this.selectedTexturePack = texturePackBase13;
						}

						arrayList1.add(texturePackBase13);
					} catch (IOException iOException10) {
						iOException10.printStackTrace();
					}
				} else if(file6.isDirectory() && (new File(file6, "pack.txt")).exists()) {
					string7 = file6.getName() + ":folder:" + file6.lastModified();

					try {
						if(!this.field_6538_d.containsKey(string7)) {
							TexturePackFolder texturePackFolder8 = new TexturePackFolder(file6);
							texturePackFolder8.texturePackID = string7;
							this.field_6538_d.put(string7, texturePackFolder8);
							texturePackFolder8.func_6485_a(this.mc);
						}

						texturePackBase13 = (TexturePackBase)this.field_6538_d.get(string7);
						if(texturePackBase13.texturePackFileName.equals(this.currentTexturePack)) {
							this.selectedTexturePack = texturePackBase13;
						}

						arrayList1.add(texturePackBase13);
					} catch (IOException iOException9) {
						iOException9.printStackTrace();
					}
				}
			}
		}

		if(this.selectedTexturePack == null) {
			this.selectedTexturePack = this.defaultTexturePack;
		}

		this.availableTexturePacks.removeAll(arrayList1);
		Iterator<TexturePackBase> iterator11 = this.availableTexturePacks.iterator();

		while(iterator11.hasNext()) {
			TexturePackBase texturePackBase12 = (TexturePackBase)iterator11.next();
			texturePackBase12.unbindThumbnailTexture(this.mc);
			this.field_6538_d.remove(texturePackBase12.texturePackID);
		}

		this.availableTexturePacks = arrayList1;
	}

	public List<TexturePackBase> availableTexturePacks() {
		return new ArrayList<TexturePackBase>(this.availableTexturePacks);
	}
}
