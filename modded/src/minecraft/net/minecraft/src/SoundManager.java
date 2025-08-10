package net.minecraft.src;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Random;

import net.minecraft.client.Minecraft;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecIBXM;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class SoundManager {
	private static SoundSystem sndSystem;
	private SoundPool soundPoolSounds = new SoundPool();
	private SoundPool soundPoolStreaming = new SoundPool();
	private SoundPool soundPoolMusic = new SoundPool();
	private SoundPool cave = new SoundPool();
	private int field_587_e = 0;
	private GameSettings options;
	private static boolean loaded = false;
	private Random rand = new Random();
	private Minecraft mc;
	private static final int MUSINTERVAL = 6000;
	private int ticksBeforeMusic = this.rand.nextInt(6000);

	public void loadSoundSettings(GameSettings paramkr) {
		this.soundPoolStreaming.field_1657_b = false;
		this.options = paramkr;
		if(!loaded && (paramkr == null || paramkr.soundVolume != 0.0F || paramkr.musicVolume != 0.0F)) {
			this.tryToSetLibraryAndCodecs();
		}

		loadModAudio("minecraft/resources/mod/sound", this.soundPoolSounds);
		loadModAudio("minecraft/resources/mod/streaming", this.soundPoolStreaming);
		loadModAudio("minecraft/resources/mod/music", this.soundPoolMusic);
		loadModAudio("minecraft/resources/mod/cavemusic", this.cave);

		try {
			Field minecraft = Minecraft.class.getDeclaredFields()[1];
			minecraft.setAccessible(true);
			this.mc = (Minecraft)minecraft.get((Object)null);
		} catch (Throwable throwable3) {
		}

	}

	private static void loadModAudio(String folder, SoundPool array) {
		File base = Minecraft.getAppDir(folder);

		try {
			walkFolder(base, base, array);
		} catch (IOException iOException4) {
			iOException4.printStackTrace();
		}

	}

	private static void walkFolder(File root, File folder, SoundPool array) throws IOException {
		if(folder.exists() || folder.mkdirs()) {
			File[] files = folder.listFiles();
			if(files != null && files.length > 0) {
				for(int i = 0; i < files.length; ++i) {
					if(!files[i].getName().startsWith(".")) {
						if(files[i].isDirectory()) {
							walkFolder(root, files[i], array);
						} else if(files[i].isFile()) {
							String path = files[i].getPath().substring(root.getPath().length() + 1).replace('\\', '/');
							array.addSound(path, files[i]);
						}
					}
				}
			}
		}

	}

	private void tryToSetLibraryAndCodecs() {
		try {
			float localThrowable = this.options.soundVolume;
			float f2 = this.options.musicVolume;
			this.options.soundVolume = 0.0F;
			this.options.musicVolume = 0.0F;
			this.options.saveOptions();
			SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
			SoundSystemConfig.setCodec("mus", CodecMus.class);
			SoundSystemConfig.setCodec("wav", CodecWav.class);
			if(Class.forName("paulscode.sound.codecs.CodecIBXM") != null) {
				SoundSystemConfig.setCodec("xm", CodecIBXM.class);
				SoundSystemConfig.setCodec("s3m", CodecIBXM.class);
				SoundSystemConfig.setCodec("mod", CodecIBXM.class);
			}

			sndSystem = new SoundSystem();
			this.options.soundVolume = localThrowable;
			this.options.musicVolume = f2;
			this.options.saveOptions();
		} catch (Throwable throwable3) {
			throwable3.printStackTrace();
			System.err.println("error linking with the LibraryJavaSound plug-in");
		}

		loaded = true;
	}

	public void onSoundOptionsChanged() {
		if(!loaded && (this.options.soundVolume != 0.0F || this.options.musicVolume != 0.0F)) {
			this.tryToSetLibraryAndCodecs();
		}

		if(sndSystem != null && loaded) {
			if(this.options.musicVolume == 0.0F) {
				sndSystem.stop("BgMusic");
			} else {
				sndSystem.setVolume("BgMusic", this.options.musicVolume);
			}
		}

	}

	public void closeMinecraft() {
		if(loaded) {
			sndSystem.cleanup();
		}

	}

	public void addSound(String paramString, File paramFile) {
		this.soundPoolSounds.addSound(paramString, paramFile);
	}

	public void addStreaming(String paramString, File paramFile) {
		this.soundPoolStreaming.addSound(paramString, paramFile);
	}

	public void addMusic(String paramString, File paramFile) {
		this.soundPoolMusic.addSound(paramString, paramFile);
	}

	public void playRandomMusicIfReady() {
		if(loaded && this.options.musicVolume != 0.0F && sndSystem != null) {
			if(!sndSystem.playing("BgMusic") && !sndSystem.playing("streaming")) {
				if(this.ticksBeforeMusic > 0) {
					--this.ticksBeforeMusic;
					return;
				}

				SoundPoolEntry localbg = null;
				if(this.mc != null && this.mc.thePlayer != null && !this.mc.thePlayer.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.mc.thePlayer.posX), MathHelper.floor_double(this.mc.thePlayer.posY), MathHelper.floor_double(this.mc.thePlayer.posZ))) {
					localbg = this.cave.getRandomSound();
				} else {
					localbg = this.soundPoolMusic.getRandomSound();
				}

				if(localbg != null) {
					this.ticksBeforeMusic = this.rand.nextInt(12000) + 12000;
					sndSystem.backgroundMusic("BgMusic", localbg.soundUrl, localbg.soundName, false);
					sndSystem.setVolume("BgMusic", this.options.musicVolume);
					sndSystem.play("BgMusic");
				}
			}

		}
	}

	public void func_338_a(EntityLiving paramlo, float paramFloat) {
		if(loaded && this.options.soundVolume != 0.0F && sndSystem != null) {
			if(paramlo != null) {
				float f1 = paramlo.prevRotationYaw + (paramlo.rotationYaw - paramlo.prevRotationYaw) * paramFloat;
				double d1 = paramlo.prevPosX + (paramlo.posX - paramlo.prevPosX) * (double)paramFloat;
				double d2 = paramlo.prevPosY + (paramlo.posY - paramlo.prevPosY) * (double)paramFloat;
				double d3 = paramlo.prevPosZ + (paramlo.posZ - paramlo.prevPosZ) * (double)paramFloat;
				float f2 = MathHelper.cos(-f1 * 0.01745329F - 3.141593F);
				float f3 = MathHelper.sin(-f1 * 0.01745329F - 3.141593F);
				float f4 = -f3;
				float f5 = 0.0F;
				float f6 = -f2;
				float f7 = 0.0F;
				float f8 = 1.0F;
				float f9 = 0.0F;
				sndSystem.setListenerPosition((float)d1, (float)d2, (float)d3);
				sndSystem.setListenerOrientation(f4, f5, f6, f7, f8, f9);
			}
		}
	}

	public void playStreaming(String paramString, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5) {
		if(loaded && this.options.soundVolume != 0.0F && sndSystem != null) {
			String str = "streaming";
			if(sndSystem.playing("streaming")) {
				sndSystem.stop("streaming");
			}

			if(paramString != null) {
				SoundPoolEntry localbg = this.soundPoolStreaming.getRandomSoundFromSoundPool(paramString);
				if(localbg != null && paramFloat4 > 0.0F) {
					if(sndSystem.playing("BgMusic")) {
						sndSystem.stop("BgMusic");
					}

					float f1 = 16.0F;
					sndSystem.newStreamingSource(true, str, localbg.soundUrl, localbg.soundName, false, paramFloat1, paramFloat2, paramFloat3, 2, f1 * 4.0F);
					sndSystem.setVolume(str, 0.5F * this.options.soundVolume);
					sndSystem.play(str);
				}

			}
		}
	}

	public void playSound(String paramString, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5) {
		if(loaded && this.options.soundVolume != 0.0F && sndSystem != null) {
			SoundPoolEntry localbg = this.soundPoolSounds.getRandomSoundFromSoundPool(paramString);
			if(localbg != null && paramFloat4 > 0.0F) {
				this.field_587_e = (this.field_587_e + 1) % 256;
				String str = "sound_" + this.field_587_e;
				float f1 = 16.0F;
				if(paramFloat4 > 1.0F) {
					f1 *= paramFloat4;
				}

				sndSystem.newSource(paramFloat4 > 1.0F, str, localbg.soundUrl, localbg.soundName, false, paramFloat1, paramFloat2, paramFloat3, 2, f1);
				sndSystem.setPitch(str, paramFloat5);
				if(paramFloat4 > 1.0F) {
					paramFloat4 = 1.0F;
				}

				sndSystem.setVolume(str, paramFloat4 * this.options.soundVolume);
				sndSystem.play(str);
			}

		}
	}

	public void playSoundFX(String paramString, float paramFloat1, float paramFloat2) {
		if(loaded && this.options.soundVolume != 0.0F && sndSystem != null) {
			SoundPoolEntry localbg = this.soundPoolSounds.getRandomSoundFromSoundPool(paramString);
			if(localbg != null) {
				this.field_587_e = (this.field_587_e + 1) % 256;
				String str = "sound_" + this.field_587_e;
				sndSystem.newSource(false, str, localbg.soundUrl, localbg.soundName, false, 0.0F, 0.0F, 0.0F, 0, 0.0F);
				if(paramFloat1 > 1.0F) {
					paramFloat1 = 1.0F;
				}

				paramFloat1 *= 0.25F;
				sndSystem.setPitch(str, paramFloat2);
				sndSystem.setVolume(str, paramFloat1 * this.options.soundVolume);
				sndSystem.play(str);
			}

		}
	}
}
