package net.minecraft.client.renderer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.Bidi;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Config;
import net.minecraft.client.GameSettingsValues;
import net.minecraft.client.gui.GameSettings;
import net.minecraft.client.skins.TexturePackBase;
import net.minecraft.util.ChatAllowedCharacters;

public class FontRenderer {
	private static final Pattern colorCodePattern = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");
	private float[] d = new float[256];
	public int fontTextureName = 0;
	public int FONT_HEIGHT = 8;
	public Random fontRandom = new Random();
	private byte[] glyphWidth = new byte[65536];
	private final int[] glyphTextureName = new int[256];
	private int[] colorCode = new int[32];
	private int boundTextureName;
	private final RenderEngine renderEngine;
	private float posX;
	private float posY;
	private boolean unicodeFlag;
	private boolean bidiFlag;
	private float colorR;
	private float colorG;
	private float colorB;
	private float colorA;
	private String textureFile;
	private long lastUpdateTime = 0L;
	private GameSettings gameSettings;

	FontRenderer() {
		this.renderEngine = null;
	}

	public FontRenderer(GameSettings par1GameSettings, String par2Str, RenderEngine par3RenderEngine, boolean par4) {
		this.renderEngine = par3RenderEngine;
		this.unicodeFlag = par4;
		this.textureFile = par2Str;
		this.gameSettings = par1GameSettings;
		this.init();
	}

	private void init() {
		this.d = new float[256];
		this.fontTextureName = 0;
		this.glyphWidth = new byte[65536];

		BufferedImage bufferedimage;
		try {
			bufferedimage = ImageIO.read(this.getFontTexturePack().getResourceAsStream(this.textureFile));
			InputStream imgWidth = this.getFontTexturePack().getResourceAsStream("/font/glyph_sizes.bin");
			imgWidth.read(this.glyphWidth);
		} catch (IOException iOException22) {
			throw new RuntimeException(iOException22);
		}

		int imgWidth = bufferedimage.getWidth();
		int imgHeight = bufferedimage.getHeight();
		int charW = imgWidth / 16;
		int charH = imgHeight / 16;
		float kx = (float)imgWidth / 128.0F;
		int[] ai = new int[imgWidth * imgHeight];
		bufferedimage.getRGB(0, 0, imgWidth, imgHeight, ai, 0, imgWidth);

		int l;
		int j1;
		int j2;
		int i3;
		int k3;
		int i4;
		int i25;
		for(int oldUseMipmaps = 0; oldUseMipmaps < 256; ++oldUseMipmaps) {
			l = oldUseMipmaps % 16;
			j1 = oldUseMipmaps / 16;
			for(i25 = charW - 1; i25 >= 0; --i25) {
				j2 = l * charW + i25;
				boolean l2 = true;

				for(i3 = 0; i3 < charH && l2; ++i3) {
					k3 = (j1 * charH + i3) * imgWidth;
					i4 = ai[j2 + k3];
					int alpha = i4 >> 24 & 255;
					if(alpha > 16) {
						l2 = false;
					}
				}

				if(!l2) {
					break;
				}
			}

			if(oldUseMipmaps == 32) {
				i25 = (int)(1.5D * (double)kx);
			}

			this.d[oldUseMipmaps] = (float)(i25 + 1) / kx + 1.0F;
		}

		this.readCustomCharWidths();
		boolean z24 = RenderEngine.useMipmaps;

		try {
			RenderEngine.useMipmaps = false;
			if(this.fontTextureName <= 0) {
				this.fontTextureName = this.renderEngine.allocateAndSetupTexture(bufferedimage);
			} else {
				this.renderEngine.setupTexture(bufferedimage, this.fontTextureName);
			}
		} finally {
			RenderEngine.useMipmaps = z24;
		}

		for(l = 0; l < 32; ++l) {
			j1 = (l >> 3 & 1) * 85;
			i25 = (l >> 2 & 1) * 170 + j1;
			j2 = (l >> 1 & 1) * 170 + j1;
			int i26 = (l >> 0 & 1) * 170 + j1;
			if(l == 6) {
				i25 += 85;
			}

			if(l >= 16) {
				i25 /= 4;
				j2 /= 4;
				i26 /= 4;
			}

			this.colorCode[l] = (i25 & 255) << 16 | (j2 & 255) << 8 | i26 & 255;
		}

	}

	private void readCustomCharWidths() {
		String suffix = ".png";
		if(this.textureFile.endsWith(suffix)) {
			String fileName = this.textureFile.substring(0, this.textureFile.length() - suffix.length()) + ".properties";
			InputStream in = this.getFontTexturePack().getResourceAsStream(fileName);
			if(in != null) {
				try {
					System.out.println("Loading " + fileName);
					Properties e = new Properties();
					e.load(in);
					Set<Object> keySet = e.keySet();
					Iterator<Object> iter = keySet.iterator();

					while(iter.hasNext()) {
						String key = (String)iter.next();
						String prefix = "width.";
						if(key.startsWith(prefix)) {
							String numStr = key.substring(prefix.length());
							int num = -1;
							try {
								num = Integer.parseInt(numStr, -1);
							} catch (Exception ex) {}
							if(num >= 0 && num < this.d.length) {
								String value = e.getProperty(key);
								float width = -1.0F;
								try {
									width = Float.parseFloat(value);
								} catch (Exception ex) {}
								if(width >= 0.0F) {
									this.d[num] = width;
								}
							}
						}
					}
				} catch (IOException iOException13) {
					iOException13.printStackTrace();
			}

			}
		}
			}

	private TexturePackBase getFontTexturePack() {
		return GameSettingsValues.ofCustomFonts ? this.gameSettings.mc.texturePackList.selectedTexturePack : (TexturePackBase)this.gameSettings.mc.texturePackList.availableTexturePacks().get(0);
		}

	private void checkUpdated() {
		if(Config.getTextureUpdateTime() != this.lastUpdateTime) {
			this.lastUpdateTime = Config.getTextureUpdateTime();
			this.init();
		}
	}

	private float func_50112_a(int par1, char par2, boolean par3) {
		return par2 == 32 ? 4.0F : (par1 > 0 && !this.unicodeFlag ? this.func_50106_a(par1 + 32, par3) : this.func_50111_a(par2, par3));
	}

	private float func_50106_a(int par1, boolean par2) {
		float f = (float)(par1 % 16 * 8);
		float f1 = (float)(par1 / 16 * 8);
		float f2 = par2 ? 1.0F : 0.0F;
		if(this.boundTextureName != this.fontTextureName) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.fontTextureName);
			this.boundTextureName = this.fontTextureName;
		}

		float f3 = this.d[par1] - 0.01F;
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glTexCoord2f(f / 128.0F, f1 / 128.0F);
		GL11.glVertex3f(this.posX + f2, this.posY, 0.0F);
		GL11.glTexCoord2f(f / 128.0F, (f1 + 7.99F) / 128.0F);
		GL11.glVertex3f(this.posX - f2, this.posY + 7.99F, 0.0F);
		GL11.glTexCoord2f((f + f3) / 128.0F, f1 / 128.0F);
		GL11.glVertex3f(this.posX + f3 + f2, this.posY, 0.0F);
		GL11.glTexCoord2f((f + f3) / 128.0F, (f1 + 7.99F) / 128.0F);
		GL11.glVertex3f(this.posX + f3 - f2, this.posY + 7.99F, 0.0F);
		GL11.glEnd();
		return this.d[par1];
	}

	private void loadGlyphTexture(int par1) {
		String s = String.format("/font/glyph_%02X.png", new Object[]{par1});

		BufferedImage bufferedimage;
		try {
			bufferedimage = ImageIO.read(RenderEngine.class.getResourceAsStream(s));
		} catch (IOException iOException5) {
			throw new RuntimeException(iOException5);
		}

		this.glyphTextureName[par1] = this.renderEngine.allocateAndSetupTexture(bufferedimage);
		this.boundTextureName = this.glyphTextureName[par1];
	}

	private float func_50111_a(char par1, boolean par2) {
		if(this.glyphWidth[par1] == 0) {
			return 0.0F;
		} else {
			int i = par1 / 256;
			if(this.glyphTextureName[i] == 0) {
				this.loadGlyphTexture(i);
			}

			if(this.boundTextureName != this.glyphTextureName[i]) {
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.glyphTextureName[i]);
				this.boundTextureName = this.glyphTextureName[i];
			}

			int j = this.glyphWidth[par1] >>> 4;
			int k = this.glyphWidth[par1] & 15;
			float f = (float)j;
			float f1 = (float)(k + 1);
			float f2 = (float)(par1 % 16 * 16) + f;
			float f3 = (float)((par1 & 255) / 16 * 16);
			float f4 = f1 - f - 0.02F;
			float f5 = par2 ? 1.0F : 0.0F;
			GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
			GL11.glTexCoord2f(f2 / 256.0F, f3 / 256.0F);
			GL11.glVertex3f(this.posX + f5, this.posY, 0.0F);
			GL11.glTexCoord2f(f2 / 256.0F, (f3 + 15.98F) / 256.0F);
			GL11.glVertex3f(this.posX - f5, this.posY + 7.99F, 0.0F);
			GL11.glTexCoord2f((f2 + f4) / 256.0F, f3 / 256.0F);
			GL11.glVertex3f(this.posX + f4 / 2.0F + f5, this.posY, 0.0F);
			GL11.glTexCoord2f((f2 + f4) / 256.0F, (f3 + 15.98F) / 256.0F);
			GL11.glVertex3f(this.posX + f4 / 2.0F - f5, this.posY + 7.99F, 0.0F);
			GL11.glEnd();
			return (f1 - f) / 2.0F + 1.0F;
		}
	}

	public int drawStringWithShadow(String par1Str, int par2, int par3, int par4) {
		if(this.bidiFlag) {
			par1Str = this.bidiReorder(par1Str);
		}

		int i = this.func_50101_a(par1Str, par2 + 1, par3 + 1, par4, true);
		i = Math.max(i, this.func_50101_a(par1Str, par2, par3, par4, false));
		return i;
	}

	public void drawString(String par1Str, int par2, int par3, int par4) {
		if(this.bidiFlag) {
			par1Str = this.bidiReorder(par1Str);
		}

		this.func_50101_a(par1Str, par2, par3, par4, false);
	}

	private String bidiReorder(String par1Str) {
		if(par1Str != null && Bidi.requiresBidi(par1Str.toCharArray(), 0, par1Str.length())) {
			Bidi bidi = new Bidi(par1Str, -2);
			byte[] abyte0 = new byte[bidi.getRunCount()];
			String[] as = new String[abyte0.length];

			int l;
			for(int as1 = 0; as1 < abyte0.length; ++as1) {
				int stringbuilder = bidi.getRunStart(as1);
				l = bidi.getRunLimit(as1);
				int byte0 = bidi.getRunLevel(as1);
				String j1 = par1Str.substring(stringbuilder, l);
				abyte0[as1] = (byte)byte0;
				as[as1] = j1;
			}

			String[] string11 = (String[])((String[])as.clone());
			Bidi.reorderVisually(abyte0, 0, as, 0, abyte0.length);
			StringBuilder stringBuilder12 = new StringBuilder();

			for(l = 0; l < as.length; ++l) {
				byte b13 = abyte0[l];

				int i14;
				for(i14 = 0; i14 < string11.length; ++i14) {
					if(string11[i14].equals(as[l])) {
						b13 = abyte0[i14];
						break;
					}
				}

				if((b13 & 1) == 0) {
					stringBuilder12.append(as[l]);
				} else {
					for(i14 = as[l].length() - 1; i14 >= 0; --i14) {
						char c = as[l].charAt(i14);
						if(c == 40) {
							c = 41;
						} else if(c == 41) {
							c = 40;
						}

						stringBuilder12.append(c);
					}
				}
			}

			return stringBuilder12.toString();
		} else {
			return par1Str;
		}
	}

	private void renderStringAtPos(String par1Str, boolean par2) {
		boolean flag = false;
		boolean flag1 = false;
		boolean flag2 = false;
		boolean flag3 = false;
		boolean flag4 = false;

		for(int i = 0; i < par1Str.length(); ++i) {
			char c = par1Str.charAt(i);
			int k;
			int f;
			if(c == 167 && i + 1 < par1Str.length()) {
				k = "0123456789abcdefklmnor".indexOf(par1Str.toLowerCase().charAt(i + 1));
				if(k < 16) {
					flag = false;
					flag1 = false;
					flag4 = false;
					flag3 = false;
					flag2 = false;
					if(k < 0 || k > 15) {
						k = 15;
					}

					if(par2) {
						k += 16;
					}

					f = this.colorCode[k];
					GL11.glColor4f((float)(f >> 16) / 255.0F, (float)(f >> 8 & 255) / 255.0F, (float)(f & 255) / 255.0F, this.colorA);
				} else if(k == 16) {
					flag = true;
				} else if(k == 17) {
					flag1 = true;
				} else if(k == 18) {
					flag4 = true;
				} else if(k == 19) {
					flag3 = true;
				} else if(k == 20) {
					flag2 = true;
				} else if(k == 21) {
					flag = false;
					flag1 = false;
					flag4 = false;
					flag3 = false;
					flag2 = false;
					GL11.glColor4f(this.colorR, this.colorG, this.colorB, this.colorA);
				}

				++i;
			} else {
				k = ChatAllowedCharacters.allowedCharacters.indexOf(c);
				if(flag && k > 0) {
					do {
						f = this.fontRandom.nextInt(ChatAllowedCharacters.allowedCharacters.length());
					} while((int)this.d[k + 32] != (int)this.d[f + 32]);

					k = f;
				}

				float f14 = this.func_50112_a(k, c, flag2);
				if(flag1) {
					++this.posX;
					this.func_50112_a(k, c, flag2);
					--this.posX;
					++f14;
				}

				Tessellator tessellator1;
				if(flag4) {
					tessellator1 = Tessellator.instance;
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					tessellator1.startDrawingQuads();
					tessellator1.addVertex((double)this.posX, (double)(this.posY + (float)(this.FONT_HEIGHT / 2)), 0.0D);
					tessellator1.addVertex((double)(this.posX + f14), (double)(this.posY + (float)(this.FONT_HEIGHT / 2)), 0.0D);
					tessellator1.addVertex((double)(this.posX + f14), (double)(this.posY + (float)(this.FONT_HEIGHT / 2) - 1.0F), 0.0D);
					tessellator1.addVertex((double)this.posX, (double)(this.posY + (float)(this.FONT_HEIGHT / 2) - 1.0F), 0.0D);
					tessellator1.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
				}

				if(flag3) {
					tessellator1 = Tessellator.instance;
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					tessellator1.startDrawingQuads();
					int j1 = flag3 ? -1 : 0;
					tessellator1.addVertex((double)(this.posX + (float)j1), (double)(this.posY + (float)this.FONT_HEIGHT), 0.0D);
					tessellator1.addVertex((double)(this.posX + f14), (double)(this.posY + (float)this.FONT_HEIGHT), 0.0D);
					tessellator1.addVertex((double)(this.posX + f14), (double)(this.posY + (float)this.FONT_HEIGHT - 1.0F), 0.0D);
					tessellator1.addVertex((double)(this.posX + (float)j1), (double)(this.posY + (float)this.FONT_HEIGHT - 1.0F), 0.0D);
					tessellator1.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
				}

				this.posX += f14;
			}
		}

	}

	public int func_50101_a(String par1Str, int par2, int par3, int par4, boolean par5) {
		if(par1Str != null) {
			this.boundTextureName = 0;
			if((par4 & -67108864) == 0) {
				par4 |= 0xFF000000;
			}

			if(par5) {
				par4 = (par4 & 16579836) >> 2 | par4 & 0xFF000000;
			}

			this.colorR = (float)(par4 >> 16 & 255) / 255.0F;
			this.colorG = (float)(par4 >> 8 & 255) / 255.0F;
			this.colorB = (float)(par4 & 255) / 255.0F;
			this.colorA = (float)(par4 >> 24 & 255) / 255.0F;
			GL11.glColor4f(this.colorR, this.colorG, this.colorB, this.colorA);
			this.posX = (float)par2;
			this.posY = (float)par3;
			this.renderStringAtPos(par1Str, par5);
			return (int)this.posX;
		} else {
			return 0;
		}
	}

	public int getStringWidth(String par1Str) {
		this.checkUpdated();
		if(par1Str == null) {
			return 0;
		} else {
			float i = 0.0F;
			boolean flag = false;

			for(int j = 0; j < par1Str.length(); ++j) {
				char c = par1Str.charAt(j);
				float k = this.getCharWidthFloat(c);
				if(k < 0.0F && j < par1Str.length() - 1) {
					++j;
					char c1 = par1Str.charAt(j);
					if(c1 != 108 && c1 != 76) {
						if(c1 == 114 || c1 == 82) {
							flag = false;
						}
					} else {
						flag = true;
					}

					k = this.getCharWidthFloat(c1);
				}

				i += k;
				if(flag) {
					++i;
				}
			}

			return Math.round(i);
		}
	}

	public int func_50105_a(char par1) {
		return Math.round(this.getCharWidthFloat(par1));
	}

	private float getCharWidthFloat(char par1) {
		if(par1 == 167) {
			return -1.0F;
		} else {
			int i = ChatAllowedCharacters.allowedCharacters.indexOf(par1);
			if(i >= 0 && !this.unicodeFlag) {
				return this.d[i + 32];
			} else if(this.glyphWidth[par1] != 0) {
				int j = this.glyphWidth[par1] >> 4;
				int k = this.glyphWidth[par1] & 15;
				if(k > 7) {
					k = 15;
					j = 0;
				}

				++k;
				return (float)((k - j) / 2 + 1);
			} else {
				return 0.0F;
			}
		}
	}

	public String func_50107_a(String par1Str, int par2) {
		return this.func_50104_a(par1Str, par2, false);
	}

	public String func_50104_a(String par1Str, int par2, boolean par3) {
		StringBuilder stringbuilder = new StringBuilder();
		float i = 0.0F;
		int j = par3 ? par1Str.length() - 1 : 0;
		byte byte0 = (byte)(par3 ? -1 : 1);
		boolean flag = false;
		boolean flag1 = false;

		for(int k = j; k >= 0 && k < par1Str.length() && i < (float)par2; k += byte0) {
			char c = par1Str.charAt(k);
			float l = this.getCharWidthFloat(c);
			if(flag) {
				flag = false;
				if(c != 108 && c != 76) {
					if(c == 114 || c == 82) {
						flag1 = false;
					}
				} else {
					flag1 = true;
				}
			} else if(l < 0.0F) {
				flag = true;
			} else {
				i += l;
				if(flag1) {
					++i;
				}
			}

			if(i > (float)par2) {
				break;
			}

			if(par3) {
				stringbuilder.insert(0, c);
			} else {
				stringbuilder.append(c);
			}
		}

		return stringbuilder.toString();
	}

	public void drawSplitString(String par1Str, int par2, int par3, int par4, int par5) {
		this.checkUpdated();
		if(this.bidiFlag) {
			par1Str = this.bidiReorder(par1Str);
		}

		this.renderSplitStringNoShadow(par1Str, par2, par3, par4, par5);
	}

	private void renderSplitStringNoShadow(String par1Str, int par2, int par3, int par4, int par5) {
		this.renderSplitString(par1Str, par2, par3, par4, par5, false);
	}

	private void renderSplitString(String par1Str, int par2, int par3, int par4, int par5, boolean par6) {
		this.checkUpdated();
		String[] as = par1Str.split("\n");
		if(as.length > 1) {
			for(int i14 = 0; i14 < as.length; ++i14) {
				this.renderSplitStringNoShadow(as[i14], par2, par3, par4, par5);
				par3 += this.splitStringWidth(as[i14], par4);
			}

		} else {
			String[] as1 = par1Str.split(" ");
			int j = 0;
			String s = "";

			while(j < as1.length) {
				String s1;
				for(s1 = s + as1[j++] + " "; j < as1.length && this.getStringWidth(s1 + as1[j]) < par4; s1 = s1 + as1[j++] + " ") {
				}

				int k;
				for(; this.getStringWidth(s1) > par4; s1 = s + s1.substring(k)) {
					for(k = 0; this.getStringWidth(s1.substring(0, k + 1)) <= par4; ++k) {
					}

					if(s1.substring(0, k).trim().length() > 0) {
						String s2 = s1.substring(0, k);
						if(s2.lastIndexOf("\u00a7") >= 0) {
							s = "\u00a7" + s2.charAt(s2.lastIndexOf("\u00a7") + 1);
						}

						this.func_50101_a(s2, par2, par3, par5, par6);
						par3 += this.FONT_HEIGHT;
					}
				}

				if(this.getStringWidth(s1.trim()) > 0) {
					if(s1.lastIndexOf("\u00a7") >= 0) {
						s = "\u00a7" + s1.charAt(s1.lastIndexOf("\u00a7") + 1);
					}

					this.func_50101_a(s1, par2, par3, par5, par6);
					par3 += this.FONT_HEIGHT;
				}
			}

		}
	}

	public int splitStringWidth(String par1Str, int par2) {
		this.checkUpdated();
		String[] as = par1Str.split("\n");
		int k;
		if(as.length > 1) {
			int i9 = 0;

			for(k = 0; k < as.length; ++k) {
				i9 += this.splitStringWidth(as[k], par2);
			}

			return i9;
		} else {
			String[] as1 = par1Str.split(" ");
			k = 0;
			int l = 0;

			while(k < as1.length) {
				String s;
				for(s = as1[k++] + " "; k < as1.length && this.getStringWidth(s + as1[k]) < par2; s = s + as1[k++] + " ") {
				}

				int i1;
				for(; this.getStringWidth(s) > par2; s = s.substring(i1)) {
					for(i1 = 0; this.getStringWidth(s.substring(0, i1 + 1)) <= par2; ++i1) {
					}

					if(s.substring(0, i1).trim().length() > 0) {
						l += this.FONT_HEIGHT;
					}
				}

				if(s.trim().length() > 0) {
					l += this.FONT_HEIGHT;
				}
			}

			if(l < this.FONT_HEIGHT) {
				l += this.FONT_HEIGHT;
			}

			return l;
		}
	}

	public void setUnicodeFlag(boolean par1) {
		this.unicodeFlag = par1;
	}

	public void setBidiFlag(boolean par1) {
		this.bidiFlag = par1;
	}

	public List<String> func_50108_c(String par1Str, int par2) {
		return Arrays.asList(this.func_50113_d(par1Str, par2).split("\n"));
	}

	String func_50113_d(String par1Str, int par2) {
		int i = this.func_50102_e(par1Str, par2);
		if(par1Str.length() <= i) {
			return par1Str;
		} else {
			String s = par1Str.substring(0, i);
			String s1 = func_50114_c(s) + par1Str.substring(i + (par1Str.charAt(i) != 32 ? 0 : 1));
			return s + "\n" + this.func_50113_d(s1, par2);
		}
	}

	private int func_50102_e(String par1Str, int par2) {
		int i = par1Str.length();
		float j = 0.0F;
		int k = 0;
		int l = -1;

		for(boolean flag = false; k < i; ++k) {
			char c = par1Str.charAt(k);
			switch(c) {
			case ' ':
				l = k;
			default:
				j += this.getCharWidthFloat(c);
				if(flag) {
					++j;
				}
				break;
			case '\u00a7':
				if(k != i) {
					++k;
					char c1 = par1Str.charAt(k);
					if(c1 != 108 && c1 != 76) {
						if(c1 == 114 || c1 == 82) {
							flag = false;
						}
					} else {
						flag = true;
					}
				}
			}

			if(c == 10) {
				++k;
				l = k;
				break;
			}

			if(j > (float)par2) {
				break;
			}
		}

		return k != i && l != -1 && l < k ? l : k;
	}

	private static boolean func_50110_b(char par0) {
		return par0 >= 48 && par0 <= 57 || par0 >= 97 && par0 <= 102 || par0 >= 65 && par0 <= 70;
	}

	private static boolean func_50109_c(char par0) {
		return par0 >= 107 && par0 <= 111 || par0 >= 75 && par0 <= 79 || par0 == 114 || par0 == 82;
	}

	private static String func_50114_c(String par0Str) {
		String s = "";
		int i = -1;
		int j = par0Str.length();

		while((i = par0Str.indexOf(167, i + 1)) != -1) {
			if(i < j - 1) {
				char c = par0Str.charAt(i + 1);
				if(func_50110_b(c)) {
					s = "\u00a7" + c;
				} else if(func_50109_c(c)) {
					s = s + "\u00a7" + c;
				}
			}
		}

		return s;
	}

	public static String removeColorCodes(String par0Str) {
		return colorCodePattern.matcher(par0Str).replaceAll("");
	}
}
