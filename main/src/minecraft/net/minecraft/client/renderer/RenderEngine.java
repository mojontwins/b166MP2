package net.minecraft.client.renderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;

import com.mojontwins.utils.TextureAtlasSize;

import net.minecraft.client.GameSettingsValues;
import net.minecraft.client.renderer.ptexture.TextureFX;
import net.minecraft.client.skins.TexturePackBase;
import net.minecraft.client.skins.TexturePackList;
import net.minecraft.world.IntHashMap;

public class RenderEngine {
	public static boolean useMipmaps = false;
	private HashMap<String, Integer> textureMap = new HashMap<String, Integer>();
	private HashMap<String, int[]> textureContentsMap = new HashMap<String, int[]>();
	private IntHashMap textureNameToImageMap = new IntHashMap();
	private IntBuffer singleIntBuffer = GLAllocation.createDirectIntBuffer(1);
	private ByteBuffer imageData = GLAllocation.createDirectByteBuffer(16777216);
	private List<TextureFX> textureList = new ArrayList<TextureFX>();
	private Map<String, ThreadDownloadImageData> urlToImageDataMap = new HashMap<String, ThreadDownloadImageData>();
	public boolean clampTexture = false;
	public boolean blurTexture = false;
	private TexturePackList texturePack;
	private BufferedImage missingTextureImage = new BufferedImage(64, 64, 2);
	
	// Optifine 
	private int terrainTextureId = -1;
	private int guiItemsTextureId = -1;
	private Map<Integer, Dimension> textureDimensionsMap = new HashMap<Integer, Dimension>();
	private Map<String, byte[]> textureDataMap = new HashMap<String, byte[]>();

	private ByteBuffer[] mipImageDatas;
	
	public RenderEngine(TexturePackList texturePackList1) {
		this.allocateImageData(TextureAtlasSize.wi, TextureAtlasSize.hi);
	
		this.texturePack = texturePackList1;
	
		Graphics graphics3 = this.missingTextureImage.getGraphics();
		graphics3.setColor(Color.WHITE);
		graphics3.fillRect(0, 0, 64, 64);
		graphics3.setColor(Color.BLACK);
		graphics3.drawString("missingtex", 1, 10);
		graphics3.dispose();
	}

	public int[] getTextureContents(String string1) {
		TexturePackBase texturePackBase2 = this.texturePack.selectedTexturePack;
		int[] i3 = (int[])this.textureContentsMap.get(string1);
		if(i3 != null) {
			return i3;
		} else {
			try {
				if(string1.startsWith("##")) {
					i3 = this.getImageContentsAndAllocate(this.unwrapImageByColumns(this.readTextureImage(texturePackBase2.getResourceAsStream(string1.substring(2)))));
				} else if(string1.startsWith("%clamp%")) {
					this.clampTexture = true;
					i3 = this.getImageContentsAndAllocate(this.readTextureImage(texturePackBase2.getResourceAsStream(string1.substring(7))));
					this.clampTexture = false;
				} else if(string1.startsWith("%blur%")) {
					this.blurTexture = true;
					this.clampTexture = true;
					i3 = this.getImageContentsAndAllocate(this.readTextureImage(texturePackBase2.getResourceAsStream(string1.substring(6))));
					this.clampTexture = false;
					this.blurTexture = false;
				} else {
					InputStream inputStream7 = texturePackBase2.getResourceAsStream(string1);
					if(inputStream7 == null) {
						System.out.println ("Texture not found: " + string1);
						i3 = this.getImageContentsAndAllocate(this.missingTextureImage);
					} else {
						i3 = this.getImageContentsAndAllocate(this.readTextureImage(inputStream7));
					}
				}

				this.textureContentsMap.put(string1, i3);
				return i3;
			} catch (IOException iOException5) {
				iOException5.printStackTrace();
				int[] i4 = this.getImageContentsAndAllocate(this.missingTextureImage);
				this.textureContentsMap.put(string1, i4);
				return i4;
			}
		}
	}

	private int[] getImageContentsAndAllocate(BufferedImage bufferedImage1) {
		int i2 = bufferedImage1.getWidth();
		int i3 = bufferedImage1.getHeight();
		int[] i4 = new int[i2 * i3];
		bufferedImage1.getRGB(0, 0, i2, i3, i4, 0, i2);
		return i4;
	}

	private int[] getImageContents(BufferedImage bufferedImage1, int[] i2) {
		int i3 = bufferedImage1.getWidth();
		int i4 = bufferedImage1.getHeight();
		bufferedImage1.getRGB(0, 0, i3, i4, i2, 0, i3);
		return i2;
	}

	public int getTexture(String string1) {
		TexturePackBase texturePackBase2 = this.texturePack.selectedTexturePack;
		Integer integer3 = (Integer)this.textureMap.get(string1);
		if(integer3 != null) {
			return integer3.intValue();
		} else {
			try {
				this.singleIntBuffer.clear();
				GLAllocation.generateTextureNames(this.singleIntBuffer);
				int i6 = this.singleIntBuffer.get(0);
				if(string1.startsWith("##")) {
					this.setupTexture(this.unwrapImageByColumns(this.readTextureImage(texturePackBase2.getResourceAsStream(string1.substring(2)))), i6);
				} else if(string1.startsWith("%clamp%")) {
					this.clampTexture = true;
					this.setupTexture(this.readTextureImage(texturePackBase2.getResourceAsStream(string1.substring(7))), i6);
					this.clampTexture = false;
				} else if(string1.startsWith("%blur%")) {
					this.blurTexture = true;
					this.setupTexture(this.readTextureImage(texturePackBase2.getResourceAsStream(string1.substring(6))), i6);
					this.blurTexture = false;
				} else if(string1.startsWith("%blurclamp%")) {
					this.blurTexture = true;
					this.clampTexture = true;
					this.setupTexture(this.readTextureImage(texturePackBase2.getResourceAsStream(string1.substring(11))), i6);
					this.blurTexture = false;
					this.clampTexture = false;
				} else {
					if(string1.equals("/terrain.png")) {
						this.terrainTextureId = i6;
					}

					if(string1.equals("/gui/items.png")) {
						this.guiItemsTextureId = i6;
					}
					
					InputStream inputStream7 = texturePackBase2.getResourceAsStream(string1);
					if(inputStream7 == null) {
						System.out.println ("Texture not found: " + string1);
						this.setupTexture(this.missingTextureImage, i6);
					} else {
						this.setupTexture(this.readTextureImage(inputStream7), i6);
					}
				}

				this.textureMap.put(string1, i6);
				return i6;
			} catch (Exception exception5) {
				exception5.printStackTrace();
				GLAllocation.generateTextureNames(this.singleIntBuffer);
				int i4 = this.singleIntBuffer.get(0);
				this.setupTexture(this.missingTextureImage, i4);
				this.textureMap.put(string1, i4);
				return i4;
			}
		}
	}

	private BufferedImage unwrapImageByColumns(BufferedImage bufferedImage1) {
		int i2 = bufferedImage1.getWidth() / 16;
		BufferedImage bufferedImage3 = new BufferedImage(16, bufferedImage1.getHeight() * i2, 2);
		Graphics graphics4 = bufferedImage3.getGraphics();

		for(int i5 = 0; i5 < i2; ++i5) {
			graphics4.drawImage(bufferedImage1, -i5 * 16, i5 * bufferedImage1.getHeight(), (ImageObserver)null);
		}

		graphics4.dispose();
		return bufferedImage3;
	}

	public int allocateAndSetupTexture(BufferedImage bufferedImage1) {
		this.singleIntBuffer.clear();
		GLAllocation.generateTextureNames(this.singleIntBuffer);
		int i2 = this.singleIntBuffer.get(0);
		this.setupTexture(bufferedImage1, i2);
		this.textureNameToImageMap.addKey(i2, bufferedImage1);
		return i2;
	}

	public void setupTexture(BufferedImage bufferedImage1, int i2) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, i2);
		
		useMipmaps = GameSettingsValues.ofMipmapLevel > 0;
		int width;
		int height;
		
		if(useMipmaps && i2 != this.guiItemsTextureId) {
			int mipmapType = (GameSettingsValues.ofMipmapLinear ? 9986 : 9984);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mipmapType);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			if(GLContext.getCapabilities().OpenGL12) {
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
				int mipmapLevel = GameSettingsValues.ofMipmapLevel;
				if(mipmapLevel >= 4) {
					int ai = Math.min(bufferedImage1.getWidth(), bufferedImage1.getHeight());
					mipmapLevel = this.getMaxMipmapLevel(ai) - 4;
					if(mipmapLevel < 0) {
						mipmapLevel = 0;
					}
				}

				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, mipmapLevel);
			}
		} else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		}

		if(this.blurTexture) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		}

		if(this.clampTexture) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		} else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		}

		width = bufferedImage1.getWidth();
		height = bufferedImage1.getHeight();
		this.setTextureDimension(i2, new Dimension(width, height));
		
		// Separate image pixels (in ARGB) to individual bytes A, R, G and B
		
		int[] imageInts = new int[width * height];
		byte[] imageByte = new byte[width * height * 4];
		bufferedImage1.getRGB(0, 0, width, height, imageInts, 0, width);

		int i, a, r, g, b;
		
		for(i = 0; i < imageInts.length; ++i) {
			a = imageInts[i] >> 24 & 255;
			r = imageInts[i] >> 16 & 255;
			g = imageInts[i] >> 8 & 255;
			b = imageInts[i] & 255;

			if(a == 0) { r = g = b = 255; }
			
			imageByte[i * 4 + 0] = (byte)r;
			imageByte[i * 4 + 1] = (byte)g;
			imageByte[i * 4 + 2] = (byte)b;
			imageByte[i * 4 + 3] = (byte)a;
		}

		this.checkImageDataSize(width, height);
		this.imageData.clear();
		this.imageData.put(imageByte);
		this.imageData.position(0).limit(imageByte.length);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.imageData);
		if(useMipmaps) {
			this.generateMipMaps(this.imageData, width, height);
		}

	}
	

	private void generateMipMaps(ByteBuffer data, int width, int height) {
		ByteBuffer parMipData = data;

		for(int level = 1; level <= 16; ++level) {
			int parWidth = width >> level - 1;
			int mipWidth = width >> level;
			int mipHeight = height >> level;
			if(mipWidth <= 0 || mipHeight <= 0) {
				break;
				}

			ByteBuffer mipData = this.mipImageDatas[level - 1];

			for(int mipX = 0; mipX < mipWidth; ++mipX) {
				for(int mipY = 0; mipY < mipHeight; ++mipY) {
					int p1 = parMipData.getInt((mipX * 2 + 0 + (mipY * 2 + 0) * parWidth) * 4);
					int p2 = parMipData.getInt((mipX * 2 + 1 + (mipY * 2 + 0) * parWidth) * 4);
					int p3 = parMipData.getInt((mipX * 2 + 1 + (mipY * 2 + 1) * parWidth) * 4);
					int p4 = parMipData.getInt((mipX * 2 + 0 + (mipY * 2 + 1) * parWidth) * 4);
					int pixel = this.weightedAverageColor(p1, p2, p3, p4);
					
					mipData.putInt((mipX + mipY * mipWidth) * 4, pixel);
			}
		}

			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, level, GL11.GL_RGBA, mipWidth, mipHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, mipData);
			parMipData = mipData;
		}

	}

	public void createTextureFromBytes(int[] i1, int i2, int i3, int i4) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, i4);
		if(useMipmaps) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		} else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		}

		if(this.blurTexture) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		}

		if(this.clampTexture) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		} else {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		}

		byte[] b5 = new byte[i2 * i3 * 4];

		for(int i6 = 0; i6 < i1.length; ++i6) {
			int i7 = i1[i6] >> 24 & 255;
			int i8 = i1[i6] >> 16 & 255;
			int i9 = i1[i6] >> 8 & 255;
			int i10 = i1[i6] & 255;

			b5[i6 * 4 + 0] = (byte)i8;
			b5[i6 * 4 + 1] = (byte)i9;
			b5[i6 * 4 + 2] = (byte)i10;
			b5[i6 * 4 + 3] = (byte)i7;
		}

		this.imageData.clear();
		this.imageData.put(b5);
		this.imageData.position(0).limit(b5.length);
		GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, i2, i3, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.imageData);
	}

	public void deleteTexture(int i1) {
		this.textureNameToImageMap.removeObject(i1);
		this.singleIntBuffer.clear();
		this.singleIntBuffer.put(i1);
		this.singleIntBuffer.flip();
		GL11.glDeleteTextures(this.singleIntBuffer);
	}

	public int getTextureForDownloadableImage(String string1, String string2) {
		ThreadDownloadImageData threadDownloadImageData3 = (ThreadDownloadImageData)this.urlToImageDataMap.get(string1);
		if(threadDownloadImageData3 != null && threadDownloadImageData3.image != null && !threadDownloadImageData3.textureSetupComplete) {
			if(threadDownloadImageData3.textureName < 0) {
				threadDownloadImageData3.textureName = this.allocateAndSetupTexture(threadDownloadImageData3.image);
			} else {
				this.setupTexture(threadDownloadImageData3.image, threadDownloadImageData3.textureName);
			}

			threadDownloadImageData3.textureSetupComplete = true;
		}

		return threadDownloadImageData3 != null && threadDownloadImageData3.textureName >= 0 ? threadDownloadImageData3.textureName : (string2 == null ? -1 : this.getTexture(string2));
	}

	public ThreadDownloadImageData obtainImageData(String string1, HttpTextureProcessor imageBuffer2) {
		ThreadDownloadImageData threadDownloadImageData3 = (ThreadDownloadImageData)this.urlToImageDataMap.get(string1);
		if(threadDownloadImageData3 == null) {
			this.urlToImageDataMap.put(string1, new ThreadDownloadImageData(string1, imageBuffer2));
		} else {
			++threadDownloadImageData3.referenceCount;
		}

		return threadDownloadImageData3;
	}

	public void releaseImageData(String string1) {
		ThreadDownloadImageData threadDownloadImageData2 = (ThreadDownloadImageData)this.urlToImageDataMap.get(string1);
		if(threadDownloadImageData2 != null) {
			--threadDownloadImageData2.referenceCount;
			if(threadDownloadImageData2.referenceCount == 0) {
				if(threadDownloadImageData2.textureName >= 0) {
					this.deleteTexture(threadDownloadImageData2.textureName);
				}

				this.urlToImageDataMap.remove(string1);
			}
		}

	}

	public void registerTextureFX(TextureFX texturefx) {
		for(int i = 0; i < this.textureList.size(); ++i) {
			TextureFX tex = (TextureFX)this.textureList.get(i);
			if(tex.tileImage == texturefx.tileImage && tex.iconIndex == texturefx.iconIndex) {
				this.textureList.remove(i);
				--i;
			}
		}

		this.textureList.add(texturefx);
		texturefx.onTick();
	}


	private void generateMipMapsSub(int xOffset, int yOffset, int width, int height, ByteBuffer data, int numTiles, boolean fastColor) {
		ByteBuffer parMipData = data;

		for(int level = 1; level <= 16; ++level) {
			int parWidth = width >> level - 1;
			int mipWidth = width >> level;
			int mipHeight = height >> level;
			int xMipOffset = xOffset >> level;
			int yMipOffset = yOffset >> level;
			if(mipWidth <= 0 || mipHeight <= 0) {
				break;
			}

			ByteBuffer mipData = this.mipImageDatas[level - 1];

			int ix;
			int iy;
			int dx;
			int dy;
			for(ix = 0; ix < mipWidth; ++ix) {
				for(iy = 0; iy < mipHeight; ++iy) {
					dx = parMipData.getInt((ix * 2 + 0 + (iy * 2 + 0) * parWidth) * 4);
					dy = parMipData.getInt((ix * 2 + 1 + (iy * 2 + 0) * parWidth) * 4);
					int p3 = parMipData.getInt((ix * 2 + 1 + (iy * 2 + 1) * parWidth) * 4);
					int p4 = parMipData.getInt((ix * 2 + 0 + (iy * 2 + 1) * parWidth) * 4);
					int pixel;
					if(fastColor) {
						pixel = this.averageColor(this.averageColor(dx, dy), this.averageColor(p3, p4));
					} else {
						pixel = this.weightedAverageColor(dx, dy, p3, p4);
					}

					mipData.putInt((ix + iy * mipWidth) * 4, pixel);
				}
			}

			for(ix = 0; ix < numTiles; ++ix) {
				for(iy = 0; iy < numTiles; ++iy) {
					dx = ix * mipWidth;
					dy = iy * mipHeight;
					GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, xMipOffset + dx, yMipOffset + dy, mipWidth, mipHeight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, mipData);
				}
			}

			parMipData = mipData;
		}

	}

	public void updateDynamicTextures() {

		this.terrainTextureId = this.getTexture("/terrain.png");
		this.guiItemsTextureId = this.getTexture("/gui/items.png");
		
		int i;
		TextureFX texturefx1;
		for(i = 0; i < this.textureList.size(); ++i) {
			texturefx1 = (TextureFX)this.textureList.get(i);
		
			int i13;
			if(texturefx1.tileImage == 0) {
				i13 = this.terrainTextureId;
			} else {
				i13 = this.guiItemsTextureId;
			}

			Dimension dim = this.getTextureDimensions(i13);
			if(dim == null) {
				throw new IllegalArgumentException("Unknown dimensions for texture id: " + i13);
			}

			int tileWidth = dim.width / TextureAtlasSize.widthInTiles;
			int tileHeight = dim.height / TextureAtlasSize.heightInTiles;
			
			this.checkImageDataSize(dim.width, dim.height);

			texturefx1.onTick();

			if(texturefx1.imageData == null) {
				continue;
			}

			this.imageData.clear();
			this.imageData.put(texturefx1.imageData);
			this.imageData.position(0).limit(texturefx1.imageData.length);

			texturefx1.bindImage(this);

			for(int ix = 0; ix < texturefx1.tileSize; ++ix) {
				for(int iy = 0; iy < texturefx1.tileSize; ++iy) {
					int xOffset = texturefx1.iconIndex % 16 * tileWidth + ix * tileWidth;
					int yOffset = texturefx1.iconIndex / 16 * tileHeight + iy * tileHeight;

					GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, xOffset, yOffset, tileWidth, tileHeight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.imageData);
					if(useMipmaps && ix == 0 && iy == 0) {
						this.generateMipMapsSub(xOffset, yOffset, tileWidth, tileHeight, this.imageData, texturefx1.tileSize, true);
					}
				}
			}
			}

		for(i = 0; i < this.textureList.size(); ++i) {
			texturefx1 = (TextureFX)this.textureList.get(i);
			if(texturefx1.textureId > 0) {
				this.imageData.clear();
				this.imageData.put(texturefx1.imageData);
				this.imageData.position(0).limit(texturefx1.imageData.length);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturefx1.textureId);
				GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 16, 16, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.imageData);
				if(useMipmaps) {
					this.generateMipMapsSub(0, 0, 16, 16, this.imageData, texturefx1.tileSize, false);
				}
			}
		}

	}

	private int averageColor(int i1, int i2) {
		int i3 = (i1 & 0xFF000000) >> 24 & 255;
		int i4 = (i2 & 0xFF000000) >> 24 & 255;
		return (i3 + i4 >> 1 << 24) + ((i1 & 16711422) + (i2 & 16711422) >> 1);
	}

	private int weightedAverageColor(int c1, int c2, int c3, int c4) {
		int cx1 = this.weightedAverageColor(c1, c2);
		int cx2 = this.weightedAverageColor(c3, c4);
		int cx = this.weightedAverageColor(cx1, cx2);
		return cx;
	}
	
	private int weightedAverageColor(int c1, int c2) {
		int a1 = (c1 & 0xFF000000) >> 24 & 255;
		int a2 = (c2 & 0xFF000000) >> 24 & 255;
			
		int ax = (a1 + a2) / 2;
		
		// a1 solid , a2 solid  -> ax = 255
		// a1 transp, a2 solid  -> ax = 127
		// a1 solid , a2 transp -> ax = 127
		// a1 transp, a2 transp -> ax = 0;
		
		if(a1 == 0 && a2 == 0) {
			a1 = 1;
			a2 = 1;
		} else {
			// If either pixel is transp (not both), it takes the same color as the other, and ax = 63 
			
			if(a1 == 0) {
				c1 = c2;
				ax /= 2;
			}

			if(a2 == 0) {
				c2 = c1;
				ax /= 2;
			}
		}

		int r1 = (c1 >> 16 & 255) * a1;
		int g1 = (c1 >> 8 & 255) * a1;
		int b1 = (c1 & 255) * a1;
		
		int r2 = (c2 >> 16 & 255) * a2;
		int g2 = (c2 >> 8 & 255) * a2;
		int b2 = (c2 & 255) * a2;
				
		int rx = (r1 + r2) / (a1 + a2);
		int gx = (g1 + g2) / (a1 + a2);
		int bx = (b1 + b2) / (a1 + a2);
		
		return ax << 24 | rx << 16 | gx << 8 | bx;
	}

	public void refreshTextures() {
		this.textureDataMap.clear();
		TexturePackBase texturePackBase1 = this.texturePack.selectedTexturePack;
		Iterator<Integer> iterator2 = this.textureNameToImageMap.getKeySet().iterator();

		BufferedImage bufferedImage4;
		while(iterator2.hasNext()) {
			int i3 = ((Integer)iterator2.next()).intValue();
			bufferedImage4 = (BufferedImage)this.textureNameToImageMap.lookup(i3);
			this.setupTexture(bufferedImage4, i3);
		}

		ThreadDownloadImageData threadDownloadImageData8;
		Iterator<ThreadDownloadImageData> iterator3;
		for(iterator3 = this.urlToImageDataMap.values().iterator(); iterator3.hasNext(); threadDownloadImageData8.textureSetupComplete = false) {
			threadDownloadImageData8 = (ThreadDownloadImageData)iterator3.next();
		}

		Iterator<String> iterator4 = this.textureMap.keySet().iterator();

		String string9;
		while(iterator4.hasNext()) {
			string9 = (String)iterator4.next();

			try {
				if(string9.startsWith("##")) {
					bufferedImage4 = this.unwrapImageByColumns(this.readTextureImage(texturePackBase1.getResourceAsStream(string9.substring(2))));
				} else if(string9.startsWith("%clamp%")) {
					this.clampTexture = true;
					bufferedImage4 = this.readTextureImage(texturePackBase1.getResourceAsStream(string9.substring(7)));
				} else if(string9.startsWith("%blur%")) {
					this.blurTexture = true;
					bufferedImage4 = this.readTextureImage(texturePackBase1.getResourceAsStream(string9.substring(6)));
				} else if(string9.startsWith("%blurclamp%")) {
					this.blurTexture = true;
					this.clampTexture = true;
					bufferedImage4 = this.readTextureImage(texturePackBase1.getResourceAsStream(string9.substring(11)));
				} else {
					bufferedImage4 = this.readTextureImage(texturePackBase1.getResourceAsStream(string9));
				}

				int i5 = ((Integer)this.textureMap.get(string9)).intValue();
				this.setupTexture(bufferedImage4, i5);
				this.blurTexture = false;
				this.clampTexture = false;
			} catch (IOException iOException7) {
				iOException7.printStackTrace();
			}
		}

		Iterator<String> iterator5 = this.textureContentsMap.keySet().iterator();

		while(iterator5.hasNext()) {
			string9 = (String)iterator5.next();

			try {
				if(string9.startsWith("##")) {
					bufferedImage4 = this.unwrapImageByColumns(this.readTextureImage(texturePackBase1.getResourceAsStream(string9.substring(2))));
				} else if(string9.startsWith("%clamp%")) {
					this.clampTexture = true;
					bufferedImage4 = this.readTextureImage(texturePackBase1.getResourceAsStream(string9.substring(7)));
				} else if(string9.startsWith("%blur%")) {
					this.blurTexture = true;
					bufferedImage4 = this.readTextureImage(texturePackBase1.getResourceAsStream(string9.substring(6)));
				} else {
					bufferedImage4 = this.readTextureImage(texturePackBase1.getResourceAsStream(string9));
				}

				this.getImageContents(bufferedImage4, (int[])this.textureContentsMap.get(string9));
				this.blurTexture = false;
				this.clampTexture = false;
			} catch (IOException iOException6) {
				iOException6.printStackTrace();
			}
		}

	}
	private void setTextureDimension(int id, Dimension dim) {
		this.textureDimensionsMap.put(new Integer(id), dim);
	}
	
	private Dimension getTextureDimensions(int id) {
		return (Dimension)this.textureDimensionsMap.get(new Integer(id));
	}
	
	private void checkImageDataSize(int width, int height) {
		if(this.imageData != null) {
			int len = width * height * 4;
			if(this.imageData.capacity() >= len) {
				return;
			}
		}

		this.allocateImageData(width, height);
	}

	private void allocateImageData(int width, int height) {
		int imgLen = width * height * 4;
		this.imageData = GLAllocation.createDirectByteBuffer(imgLen);
		ArrayList<ByteBuffer> list = new ArrayList<ByteBuffer>();

		int mipHeight = height / 2;
		for(int mipWidth = width / 2; mipWidth > 0; mipWidth /= 2) {
			
			int mipLen = mipWidth * mipHeight * 4;
			ByteBuffer buf = GLAllocation.createDirectByteBuffer(mipLen);
			list.add(buf);
			
			mipHeight /= 2;
		}

		this.mipImageDatas = (ByteBuffer[])((ByteBuffer[])list.toArray(new ByteBuffer[list.size()]));
	}
	
	private int getMaxMipmapLevel(int size) {
		int level;
		for(level = 0; size > 0; ++level) {
			size /= 2;
		}

		return level - 1;
	}

	private BufferedImage readTextureImage(InputStream inputStream1) throws IOException {
		BufferedImage bufferedImage2 = ImageIO.read(inputStream1);
		inputStream1.close();
		return bufferedImage2;
	}

	public void bindTexture(int i1) {
		if(i1 >= 0) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, i1);
		}
	}
}