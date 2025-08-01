package net.minecraft.client.sound;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SoundPool {
	private Random rand = new Random();
	private Map<String, ArrayList<SoundPoolEntry>> nameToSoundPoolEntriesMapping = new HashMap<String, ArrayList<SoundPoolEntry>>();
	private List<SoundPoolEntry> allSoundPoolEntries = new ArrayList<SoundPoolEntry>();
	public int numberOfSoundPoolEntries = 0;
	public boolean isGetRandomSound = true;

	public SoundPoolEntry addSound(String string1, File file2) {
		try {
			String string3 = string1;
			string1 = string1.substring(0, string1.indexOf("."));
			if(this.isGetRandomSound) {
				while(Character.isDigit(string1.charAt(string1.length() - 1))) {
					string1 = string1.substring(0, string1.length() - 1);
				}
			}

			string1 = string1.replaceAll("/", ".");
			if(!this.nameToSoundPoolEntriesMapping.containsKey(string1)) {
				this.nameToSoundPoolEntriesMapping.put(string1, new ArrayList<SoundPoolEntry>());
			}

			SoundPoolEntry soundPoolEntry4 = new SoundPoolEntry(string3, file2.toURI().toURL());
			this.nameToSoundPoolEntriesMapping.get(string1).add(soundPoolEntry4);
			this.allSoundPoolEntries.add(soundPoolEntry4);
			++this.numberOfSoundPoolEntries;
			return soundPoolEntry4;
		} catch (MalformedURLException malformedURLException5) {
			malformedURLException5.printStackTrace();
			throw new RuntimeException(malformedURLException5);
		}
	}

	public SoundPoolEntry addSoundURL(String string1, URL url) {
		String string3 = string1;
		string1 = string1.substring(0, string1.indexOf("."));
		if(this.isGetRandomSound) {
			while(Character.isDigit(string1.charAt(string1.length() - 1))) {
				string1 = string1.substring(0, string1.length() - 1);
			}
		}

		string1 = string1.replaceAll("/", ".");
		if(!this.nameToSoundPoolEntriesMapping.containsKey(string1)) {
			this.nameToSoundPoolEntriesMapping.put(string1, new ArrayList<SoundPoolEntry>());
		}

		SoundPoolEntry soundPoolEntry4 = new SoundPoolEntry(string3, url);
		// System.out.println("Adding custom sound for " + string3 + " @ " + url);

		this.nameToSoundPoolEntriesMapping.get(string1).add(soundPoolEntry4);
		this.allSoundPoolEntries.add(soundPoolEntry4);
		++this.numberOfSoundPoolEntries;
		return soundPoolEntry4;
	}	

	public SoundPoolEntry getRandomSoundFromSoundPool(String string1) {
		List<SoundPoolEntry> list2 = this.nameToSoundPoolEntriesMapping.get(string1);
		return list2 == null ? null : (SoundPoolEntry)list2.get(this.rand.nextInt(list2.size()));
	}

	public SoundPoolEntry getRandomSound() {
		return this.allSoundPoolEntries.size() == 0 ? null : (SoundPoolEntry)this.allSoundPoolEntries.get(this.rand.nextInt(this.allSoundPoolEntries.size()));
	}
}
