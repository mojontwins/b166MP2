package net.minecraft.src;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

public class RenderBlocks {
	public IBlockAccess blockAccess = null;
	public int overrideBlockTexture = 0;
	public boolean flipTexture = false;
	public boolean renderAllFaces = false;
	public static boolean fancyGrass = true;
	public static boolean cfgGrassFix = true;
	public boolean enableAO = false;
	public float field_22384_f = 0.0F;
	public float aoLightValueXNeg = 0.0F;
	public float aoLightValueYNeg = 0.0F;
	public float aoLightValueZNeg = 0.0F;
	public float aoLightValueXPos = 0.0F;
	public float aoLightValueYPos = 0.0F;
	public float aoLightValueZPos = 0.0F;
	public float field_22377_m = 0.0F;
	public float field_22376_n = 0.0F;
	public float field_22375_o = 0.0F;
	public float field_22374_p = 0.0F;
	public float field_22373_q = 0.0F;
	public float field_22372_r = 0.0F;
	public float field_22371_s = 0.0F;
	public float field_22370_t = 0.0F;
	public float field_22369_u = 0.0F;
	public float field_22368_v = 0.0F;
	public float field_22367_w = 0.0F;
	public float field_22366_x = 0.0F;
	public float field_22365_y = 0.0F;
	public float field_22364_z = 0.0F;
	public float field_22362_A = 0.0F;
	public float field_22360_B = 0.0F;
	public float field_22358_C = 0.0F;
	public float field_22356_D = 0.0F;
	public float field_22354_E = 0.0F;
	public float field_22353_F = 0.0F;
	public int field_22352_G = 0;
	public float colorRedTopLeft = 0.0F;
	public float colorRedBottomLeft = 0.0F;
	public float colorRedBottomRight = 0.0F;
	public float colorRedTopRight = 0.0F;
	public float colorGreenTopLeft = 0.0F;
	public float colorGreenBottomLeft = 0.0F;
	public float colorGreenBottomRight = 0.0F;
	public float colorGreenTopRight = 0.0F;
	public float colorBlueTopLeft = 0.0F;
	public float colorBlueBottomLeft = 0.0F;
	public float colorBlueBottomRight = 0.0F;
	public float colorBlueTopRight = 0.0F;
	public boolean field_22339_T = false;
	public boolean field_22338_U = false;
	public boolean field_22337_V = false;
	public boolean field_22336_W = false;
	public boolean field_22335_X = false;
	public boolean field_22334_Y = false;
	public boolean field_22333_Z = false;
	public boolean field_22363_aa = false;
	public boolean field_22361_ab = false;
	public boolean field_22359_ac = false;
	public boolean field_22357_ad = false;
	public boolean field_22355_ae = false;
	public static float[][] redstoneColors = new float[16][];

	static {
		for(int i = 0; i < redstoneColors.length; ++i) {
			float j = (float)i / 15.0F;
			float red = j * 0.6F + 0.4F;
			if(i == 0) {
				j = 0.0F;
			}

			float green = j * j * 0.7F - 0.5F;
			float blue = j * j * 0.6F - 0.7F;
			if(green < 0.0F) {
				green = 0.0F;
			}

			if(blue < 0.0F) {
				blue = 0.0F;
			}

			redstoneColors[i] = new float[]{red, green, blue};
		}

	}

	public RenderBlocks(IBlockAccess xg1) {
		this.overrideBlockTexture = -1;
		this.flipTexture = false;
		this.renderAllFaces = false;
		this.field_22352_G = 1;
		this.blockAccess = xg1;
	}

	public RenderBlocks() {
		this.overrideBlockTexture = -1;
		this.flipTexture = false;
		this.renderAllFaces = false;
		this.field_22352_G = 1;
	}

	public void renderBlockUsingTexture(Block un1, int i1, int j1, int k1, int l1) {
		this.overrideBlockTexture = l1;
		this.renderBlockByRenderType(un1, i1, j1, k1);
		this.overrideBlockTexture = -1;
	}

	public boolean renderBlockByRenderType(Block un1, int i1, int j1, int k1) {
		int l1 = un1.getRenderType();
		un1.setBlockBoundsBasedOnState(this.blockAccess, i1, j1, k1);
		return l1 == 0 ? this.renderStandardBlock(un1, i1, j1, k1) : (l1 == 4 ? this.renderBlockFluids(un1, i1, j1, k1) : (l1 == 13 ? this.renderBlockCactus(un1, i1, j1, k1) : (l1 == 1 ? this.renderBlockReed(un1, i1, j1, k1) : (l1 == 6 ? this.renderBlockCrops(un1, i1, j1, k1) : (l1 == 2 ? this.renderBlockTorch(un1, i1, j1, k1) : (l1 == 3 ? this.renderBlockFire(un1, i1, j1, k1) : (l1 == 5 ? this.renderBlockRedstoneWire(un1, i1, j1, k1) : (l1 == 8 ? this.renderBlockLadder(un1, i1, j1, k1) : (l1 == 7 ? this.renderBlockDoor(un1, i1, j1, k1) : (l1 == 9 ? this.renderBlockMinecartTrack((BlockRail)un1, i1, j1, k1) : (l1 == 10 ? this.renderBlockStairs(un1, i1, j1, k1) : (l1 == 11 ? this.renderBlockFence(un1, i1, j1, k1) : (l1 == 12 ? this.renderBlockLever(un1, i1, j1, k1) : (l1 == 14 ? this.renderBlockBed(un1, i1, j1, k1) : (l1 == 15 ? this.renderBlockRepeater(un1, i1, j1, k1) : ModLoader.RenderWorldBlock(this, this.blockAccess, i1, j1, k1, un1, l1))))))))))))))));
	}

	public boolean renderBlockBed(Block un1, int i1, int j1, int k1) {
		Tessellator ns1 = Tessellator.instance;
		int l1 = this.blockAccess.getBlockMetadata(i1, j1, k1);
		int i2 = BlockBed.getDirectionFromMetadata(l1);
		boolean flag = BlockBed.isBlockFootOfBed(l1);
		float f1 = 0.5F;
		float f2 = 1.0F;
		float f3 = 0.8F;
		float f4 = 0.6F;
		float f17 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1);
		ns1.setColorOpaque_F(f1 * f17, f1 * f17, f1 * f17);
		int i18 = un1.getBlockTexture(this.blockAccess, i1, j1, k1, 0);
		int j2 = (i18 & 15) << 4;
		int k2 = i18 & 240;
		double d1 = (double)((float)j2 / 256.0F);
		double d3 = ((double)(j2 + 16) - 0.01D) / 256.0D;
		double d5 = (double)((float)k2 / 256.0F);
		double d7 = ((double)(k2 + 16) - 0.01D) / 256.0D;
		double d9 = (double)i1 + un1.minX;
		double d11 = (double)i1 + un1.maxX;
		double d13 = (double)j1 + un1.minY + 0.1875D;
		double d15 = (double)k1 + un1.minZ;
		double d17 = (double)k1 + un1.maxZ;
		ns1.addVertexWithUV(d9, d13, d17, d1, d7);
		ns1.addVertexWithUV(d9, d13, d15, d1, d5);
		ns1.addVertexWithUV(d11, d13, d15, d3, d5);
		ns1.addVertexWithUV(d11, d13, d17, d3, d7);
		float f18 = un1.getBlockBrightness(this.blockAccess, i1, j1 + 1, k1);
		ns1.setColorOpaque_F(f2 * f18, f2 * f18, f2 * f18);
		j2 = un1.getBlockTexture(this.blockAccess, i1, j1, k1, 1);
		k2 = (j2 & 15) << 4;
		d1 = (double)(j2 & 240);
		double d2 = (double)((float)k2 / 256.0F);
		double d4 = ((double)(k2 + 16) - 0.01D) / 256.0D;
		double d6 = (double)((float)d1 / 256.0F);
		double d8 = (d1 + 16.0D - 0.01D) / 256.0D;
		double d10 = d2;
		double d12 = d4;
		double d14 = d6;
		double d16 = d6;
		double d18 = d2;
		double d19 = d4;
		double d20 = d8;
		double d21 = d8;
		if(i2 == 0) {
			d12 = d2;
			d14 = d8;
			d18 = d4;
			d21 = d6;
		} else if(i2 == 2) {
			d10 = d4;
			d16 = d8;
			d19 = d2;
			d20 = d6;
		} else if(i2 == 3) {
			d10 = d4;
			d16 = d8;
			d19 = d2;
			d20 = d6;
			d12 = d2;
			d14 = d8;
			d18 = d4;
			d21 = d6;
		}

		double d22 = (double)i1 + un1.minX;
		double d23 = (double)i1 + un1.maxX;
		double d24 = (double)j1 + un1.maxY;
		double d25 = (double)k1 + un1.minZ;
		double d26 = (double)k1 + un1.maxZ;
		ns1.addVertexWithUV(d23, d24, d26, d18, d20);
		ns1.addVertexWithUV(d23, d24, d25, d10, d14);
		ns1.addVertexWithUV(d22, d24, d25, d12, d16);
		ns1.addVertexWithUV(d22, d24, d26, d19, d21);
		f18 = (float)ModelBed.field_22280_a[i2];
		if(flag) {
			f18 = (float)ModelBed.field_22280_a[ModelBed.field_22279_b[i2]];
		}

		byte j21 = 4;
		switch(i2) {
		case 0:
			j21 = 5;
			break;
		case 1:
			j21 = 3;
		case 2:
		default:
			break;
		case 3:
			j21 = 2;
		}

		float f22;
		if(f18 != 2.0F && (this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1, j1, k1 - 1, 2))) {
			f22 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1 - 1);
			if(un1.minZ > 0.0D) {
				f22 = f17;
			}

			ns1.setColorOpaque_F(f3 * f22, f3 * f22, f3 * f22);
			this.flipTexture = j21 == 2;
			this.renderEastFace(un1, (double)i1, (double)j1, (double)k1, un1.getBlockTexture(this.blockAccess, i1, j1, k1, 2));
		}

		if(f18 != 3.0F && (this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1, j1, k1 + 1, 3))) {
			f22 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1 + 1);
			if(un1.maxZ < 1.0D) {
				f22 = f17;
			}

			ns1.setColorOpaque_F(f3 * f22, f3 * f22, f3 * f22);
			this.flipTexture = j21 == 3;
			this.renderWestFace(un1, (double)i1, (double)j1, (double)k1, un1.getBlockTexture(this.blockAccess, i1, j1, k1, 3));
		}

		if(f18 != 4.0F && (this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1 - 1, j1, k1, 4))) {
			f22 = un1.getBlockBrightness(this.blockAccess, i1 - 1, j1, k1);
			if(un1.minX > 0.0D) {
				f22 = f17;
			}

			ns1.setColorOpaque_F(f4 * f22, f4 * f22, f4 * f22);
			this.flipTexture = j21 == 4;
			this.renderNorthFace(un1, (double)i1, (double)j1, (double)k1, un1.getBlockTexture(this.blockAccess, i1, j1, k1, 4));
		}

		if(f18 != 5.0F && (this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1 + 1, j1, k1, 5))) {
			f22 = un1.getBlockBrightness(this.blockAccess, i1 + 1, j1, k1);
			if(un1.maxX < 1.0D) {
				f22 = f17;
			}

			ns1.setColorOpaque_F(f4 * f22, f4 * f22, f4 * f22);
			this.flipTexture = j21 == 5;
			this.renderSouthFace(un1, (double)i1, (double)j1, (double)k1, un1.getBlockTexture(this.blockAccess, i1, j1, k1, 5));
		}

		this.flipTexture = false;
		return true;
	}

	public boolean renderBlockTorch(Block un1, int i1, int j1, int k1) {
		int l1 = this.blockAccess.getBlockMetadata(i1, j1, k1);
		Tessellator ns1 = Tessellator.instance;
		float f1 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1);
		if(Block.lightValue[un1.blockID] > 0) {
			f1 = 1.0F;
		}

		ns1.setColorOpaque_F(f1, f1, f1);
		double d1 = (double)0.4F;
		double d2 = 0.5D - d1;
		double d3 = (double)0.2F;
		if(l1 == 1) {
			this.renderTorchAtAngle(un1, (double)i1 - d2, (double)j1 + d3, (double)k1, -d1, 0.0D);
		} else if(l1 == 2) {
			this.renderTorchAtAngle(un1, (double)i1 + d2, (double)j1 + d3, (double)k1, d1, 0.0D);
		} else if(l1 == 3) {
			this.renderTorchAtAngle(un1, (double)i1, (double)j1 + d3, (double)k1 - d2, 0.0D, -d1);
		} else if(l1 == 4) {
			this.renderTorchAtAngle(un1, (double)i1, (double)j1 + d3, (double)k1 + d2, 0.0D, d1);
		} else {
			this.renderTorchAtAngle(un1, (double)i1, (double)j1, (double)k1, 0.0D, 0.0D);
		}

		return true;
	}

	public boolean renderBlockRepeater(Block un1, int i1, int j1, int k1) {
		int l1 = this.blockAccess.getBlockMetadata(i1, j1, k1);
		int i2 = l1 & 3;
		int j2 = (l1 & 12) >> 2;
		this.renderStandardBlock(un1, i1, j1, k1);
		Tessellator ns1 = Tessellator.instance;
		float f1 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1);
		if(Block.lightValue[un1.blockID] > 0) {
			f1 = (f1 + 1.0F) * 0.5F;
		}

		ns1.setColorOpaque_F(f1, f1, f1);
		double d1 = -0.1875D;
		double d2 = 0.0D;
		double d3 = 0.0D;
		double d4 = 0.0D;
		double d5 = 0.0D;
		switch(i2) {
		case 0:
			d5 = -0.3125D;
			d3 = BlockRedstoneRepeater.field_22024_a[j2];
			break;
		case 1:
			d4 = 0.3125D;
			d2 = -BlockRedstoneRepeater.field_22024_a[j2];
			break;
		case 2:
			d5 = 0.3125D;
			d3 = -BlockRedstoneRepeater.field_22024_a[j2];
			break;
		case 3:
			d4 = -0.3125D;
			d2 = BlockRedstoneRepeater.field_22024_a[j2];
		}

		this.renderTorchAtAngle(un1, (double)i1 + d2, (double)j1 + d1, (double)k1 + d3, 0.0D, 0.0D);
		this.renderTorchAtAngle(un1, (double)i1 + d4, (double)j1 + d1, (double)k1 + d5, 0.0D, 0.0D);
		int k2 = un1.getBlockTextureFromSide(1);
		int l2 = (k2 & 15) << 4;
		int i3 = k2 & 240;
		double d6 = (double)((float)l2 / 256.0F);
		double d7 = (double)(((float)l2 + 15.99F) / 256.0F);
		double d8 = (double)((float)i3 / 256.0F);
		double d9 = (double)(((float)i3 + 15.99F) / 256.0F);
		float f2 = 0.125F;
		float f3 = (float)(i1 + 1);
		float f4 = (float)(i1 + 1);
		float f5 = (float)(i1 + 0);
		float f6 = (float)(i1 + 0);
		float f7 = (float)(k1 + 0);
		float f8 = (float)(k1 + 1);
		float f9 = (float)(k1 + 1);
		float f10 = (float)(k1 + 0);
		float f11 = (float)j1 + f2;
		if(i2 == 2) {
			f3 = f4 = (float)(i1 + 0);
			f5 = f6 = (float)(i1 + 1);
			f7 = f10 = (float)(k1 + 1);
			f8 = f9 = (float)(k1 + 0);
		} else if(i2 == 3) {
			f3 = f6 = (float)(i1 + 0);
			f4 = f5 = (float)(i1 + 1);
			f7 = f8 = (float)(k1 + 0);
			f9 = f10 = (float)(k1 + 1);
		} else if(i2 == 1) {
			f3 = f6 = (float)(i1 + 1);
			f4 = f5 = (float)(i1 + 0);
			f7 = f8 = (float)(k1 + 1);
			f9 = f10 = (float)(k1 + 0);
		}

		ns1.addVertexWithUV((double)f6, (double)f11, (double)f10, d6, d8);
		ns1.addVertexWithUV((double)f5, (double)f11, (double)f9, d6, d9);
		ns1.addVertexWithUV((double)f4, (double)f11, (double)f8, d7, d9);
		ns1.addVertexWithUV((double)f3, (double)f11, (double)f7, d7, d8);
		return true;
	}

	public boolean renderBlockLever(Block un1, int i1, int j1, int k1) {
		int l1 = this.blockAccess.getBlockMetadata(i1, j1, k1);
		int i2 = l1 & 7;
		boolean flag = (l1 & 8) > 0;
		Tessellator ns1 = Tessellator.instance;
		boolean flag1 = this.overrideBlockTexture >= 0;
		if(!flag1) {
			this.overrideBlockTexture = Block.cobblestone.blockIndexInTexture;
		}

		float f1 = 0.25F;
		float f2 = 0.1875F;
		float f3 = 0.1875F;
		if(i2 == 5) {
			un1.setBlockBounds(0.5F - f2, 0.0F, 0.5F - f1, 0.5F + f2, f3, 0.5F + f1);
		} else if(i2 == 6) {
			un1.setBlockBounds(0.5F - f1, 0.0F, 0.5F - f2, 0.5F + f1, f3, 0.5F + f2);
		} else if(i2 == 4) {
			un1.setBlockBounds(0.5F - f2, 0.5F - f1, 1.0F - f3, 0.5F + f2, 0.5F + f1, 1.0F);
		} else if(i2 == 3) {
			un1.setBlockBounds(0.5F - f2, 0.5F - f1, 0.0F, 0.5F + f2, 0.5F + f1, f3);
		} else if(i2 == 2) {
			un1.setBlockBounds(1.0F - f3, 0.5F - f1, 0.5F - f2, 1.0F, 0.5F + f1, 0.5F + f2);
		} else if(i2 == 1) {
			un1.setBlockBounds(0.0F, 0.5F - f1, 0.5F - f2, f3, 0.5F + f1, 0.5F + f2);
		}

		this.renderStandardBlock(un1, i1, j1, k1);
		if(!flag1) {
			this.overrideBlockTexture = -1;
		}

		float f4 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1);
		if(Block.lightValue[un1.blockID] > 0) {
			f4 = 1.0F;
		}

		ns1.setColorOpaque_F(f4, f4, f4);
		int j2 = un1.getBlockTextureFromSide(0);
		if(this.overrideBlockTexture >= 0) {
			j2 = this.overrideBlockTexture;
		}

		int k2 = (j2 & 15) << 4;
		int l2 = j2 & 240;
		float f5 = (float)k2 / 256.0F;
		float f6 = ((float)k2 + 15.99F) / 256.0F;
		float f7 = (float)l2 / 256.0F;
		float f8 = ((float)l2 + 15.99F) / 256.0F;
		Vec3D[] abr = new Vec3D[8];
		float f9 = 0.0625F;
		float f10 = 0.0625F;
		float f11 = 0.625F;
		abr[0] = Vec3D.createVector((double)(-f9), 0.0D, (double)(-f10));
		abr[1] = Vec3D.createVector((double)f9, 0.0D, (double)(-f10));
		abr[2] = Vec3D.createVector((double)f9, 0.0D, (double)f10);
		abr[3] = Vec3D.createVector((double)(-f9), 0.0D, (double)f10);
		abr[4] = Vec3D.createVector((double)(-f9), (double)f11, (double)(-f10));
		abr[5] = Vec3D.createVector((double)f9, (double)f11, (double)(-f10));
		abr[6] = Vec3D.createVector((double)f9, (double)f11, (double)f10);
		abr[7] = Vec3D.createVector((double)(-f9), (double)f11, (double)f10);

		for(int br1 = 0; br1 < 8; ++br1) {
			if(flag) {
				abr[br1].zCoord -= 0.0625D;
				abr[br1].rotateAroundX(0.6981317F);
			} else {
				abr[br1].zCoord += 0.0625D;
				abr[br1].rotateAroundX(-0.6981317F);
			}

			if(i2 == 6) {
				abr[br1].rotateAroundY(1.570796F);
			}

			if(i2 < 5) {
				abr[br1].yCoord -= 0.375D;
				abr[br1].rotateAroundX(1.570796F);
				if(i2 == 4) {
					abr[br1].rotateAroundY(0.0F);
				}

				if(i2 == 3) {
					abr[br1].rotateAroundY(3.141593F);
				}

				if(i2 == 2) {
					abr[br1].rotateAroundY(1.570796F);
				}

				if(i2 == 1) {
					abr[br1].rotateAroundY(-1.570796F);
				}

				abr[br1].xCoord += (double)i1 + 0.5D;
				abr[br1].yCoord += (double)((float)j1 + 0.5F);
				abr[br1].zCoord += (double)k1 + 0.5D;
			} else {
				abr[br1].xCoord += (double)i1 + 0.5D;
				abr[br1].yCoord += (double)((float)j1 + 0.125F);
				abr[br1].zCoord += (double)k1 + 0.5D;
			}
		}

		Vec3D vec3D30 = null;
		Vec3D br2 = null;
		Vec3D br3 = null;
		Vec3D br4 = null;

		for(int j3 = 0; j3 < 6; ++j3) {
			if(j3 == 0) {
				f5 = (float)(k2 + 7) / 256.0F;
				f6 = ((float)(k2 + 9) - 0.01F) / 256.0F;
				f7 = (float)(l2 + 6) / 256.0F;
				f8 = ((float)(l2 + 8) - 0.01F) / 256.0F;
			} else if(j3 == 2) {
				f5 = (float)(k2 + 7) / 256.0F;
				f6 = ((float)(k2 + 9) - 0.01F) / 256.0F;
				f7 = (float)(l2 + 6) / 256.0F;
				f8 = ((float)(l2 + 16) - 0.01F) / 256.0F;
			}

			if(j3 == 0) {
				vec3D30 = abr[0];
				br2 = abr[1];
				br3 = abr[2];
				br4 = abr[3];
			} else if(j3 == 1) {
				vec3D30 = abr[7];
				br2 = abr[6];
				br3 = abr[5];
				br4 = abr[4];
			} else if(j3 == 2) {
				vec3D30 = abr[1];
				br2 = abr[0];
				br3 = abr[4];
				br4 = abr[5];
			} else if(j3 == 3) {
				vec3D30 = abr[2];
				br2 = abr[1];
				br3 = abr[5];
				br4 = abr[6];
			} else if(j3 == 4) {
				vec3D30 = abr[3];
				br2 = abr[2];
				br3 = abr[6];
				br4 = abr[7];
			} else if(j3 == 5) {
				vec3D30 = abr[0];
				br2 = abr[3];
				br3 = abr[7];
				br4 = abr[4];
			}

			ns1.addVertexWithUV(vec3D30.xCoord, vec3D30.yCoord, vec3D30.zCoord, (double)f5, (double)f8);
			ns1.addVertexWithUV(br2.xCoord, br2.yCoord, br2.zCoord, (double)f6, (double)f8);
			ns1.addVertexWithUV(br3.xCoord, br3.yCoord, br3.zCoord, (double)f6, (double)f7);
			ns1.addVertexWithUV(br4.xCoord, br4.yCoord, br4.zCoord, (double)f5, (double)f7);
		}

		return true;
	}

	public boolean renderBlockFire(Block un1, int i1, int j1, int k1) {
		Tessellator ns1 = Tessellator.instance;
		int l1 = un1.getBlockTextureFromSide(0);
		if(this.overrideBlockTexture >= 0) {
			l1 = this.overrideBlockTexture;
		}

		float f1 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1);
		ns1.setColorOpaque_F(f1, f1, f1);
		int i2 = (l1 & 15) << 4;
		int j2 = l1 & 240;
		double d1 = (double)((float)i2 / 256.0F);
		double d3 = (double)(((float)i2 + 15.99F) / 256.0F);
		double d5 = (double)((float)j2 / 256.0F);
		double d7 = (double)(((float)j2 + 15.99F) / 256.0F);
		float f2 = 1.4F;
		double d12;
		double d14;
		double d16;
		double d18;
		double d20;
		double d22;
		double d24;
		if(!this.blockAccess.func_28100_h(i1, j1 - 1, k1) && !Block.fire.canBlockCatchFire(this.blockAccess, i1, j1 - 1, k1)) {
			float f46 = 0.2F;
			float f5 = 0.0625F;
			if((i1 + j1 + k1 & 1) == 1) {
				d1 = (double)((float)i2 / 256.0F);
				d3 = (double)(((float)i2 + 15.99F) / 256.0F);
				d5 = (double)((float)(j2 + 16) / 256.0F);
				d7 = (double)(((float)j2 + 15.99F + 16.0F) / 256.0F);
			}

			if((i1 / 2 + j1 / 2 + k1 / 2 & 1) == 1) {
				d12 = d3;
				d3 = d1;
				d1 = d12;
			}

			if(Block.fire.canBlockCatchFire(this.blockAccess, i1 - 1, j1, k1)) {
				ns1.addVertexWithUV((double)((float)i1 + f46), (double)((float)j1 + f2 + f5), (double)(k1 + 1), d3, d5);
				ns1.addVertexWithUV((double)(i1 + 0), (double)((float)(j1 + 0) + f5), (double)(k1 + 1), d3, d7);
				ns1.addVertexWithUV((double)(i1 + 0), (double)((float)(j1 + 0) + f5), (double)(k1 + 0), d1, d7);
				ns1.addVertexWithUV((double)((float)i1 + f46), (double)((float)j1 + f2 + f5), (double)(k1 + 0), d1, d5);
				ns1.addVertexWithUV((double)((float)i1 + f46), (double)((float)j1 + f2 + f5), (double)(k1 + 0), d1, d5);
				ns1.addVertexWithUV((double)(i1 + 0), (double)((float)(j1 + 0) + f5), (double)(k1 + 0), d1, d7);
				ns1.addVertexWithUV((double)(i1 + 0), (double)((float)(j1 + 0) + f5), (double)(k1 + 1), d3, d7);
				ns1.addVertexWithUV((double)((float)i1 + f46), (double)((float)j1 + f2 + f5), (double)(k1 + 1), d3, d5);
			}

			if(Block.fire.canBlockCatchFire(this.blockAccess, i1 + 1, j1, k1)) {
				ns1.addVertexWithUV((double)((float)(i1 + 1) - f46), (double)((float)j1 + f2 + f5), (double)(k1 + 0), d1, d5);
				ns1.addVertexWithUV((double)(i1 + 1 - 0), (double)((float)(j1 + 0) + f5), (double)(k1 + 0), d1, d7);
				ns1.addVertexWithUV((double)(i1 + 1 - 0), (double)((float)(j1 + 0) + f5), (double)(k1 + 1), d3, d7);
				ns1.addVertexWithUV((double)((float)(i1 + 1) - f46), (double)((float)j1 + f2 + f5), (double)(k1 + 1), d3, d5);
				ns1.addVertexWithUV((double)((float)(i1 + 1) - f46), (double)((float)j1 + f2 + f5), (double)(k1 + 1), d3, d5);
				ns1.addVertexWithUV((double)(i1 + 1 - 0), (double)((float)(j1 + 0) + f5), (double)(k1 + 1), d3, d7);
				ns1.addVertexWithUV((double)(i1 + 1 - 0), (double)((float)(j1 + 0) + f5), (double)(k1 + 0), d1, d7);
				ns1.addVertexWithUV((double)((float)(i1 + 1) - f46), (double)((float)j1 + f2 + f5), (double)(k1 + 0), d1, d5);
			}

			if(Block.fire.canBlockCatchFire(this.blockAccess, i1, j1, k1 - 1)) {
				ns1.addVertexWithUV((double)(i1 + 0), (double)((float)j1 + f2 + f5), (double)((float)k1 + f46), d3, d5);
				ns1.addVertexWithUV((double)(i1 + 0), (double)((float)(j1 + 0) + f5), (double)(k1 + 0), d3, d7);
				ns1.addVertexWithUV((double)(i1 + 1), (double)((float)(j1 + 0) + f5), (double)(k1 + 0), d1, d7);
				ns1.addVertexWithUV((double)(i1 + 1), (double)((float)j1 + f2 + f5), (double)((float)k1 + f46), d1, d5);
				ns1.addVertexWithUV((double)(i1 + 1), (double)((float)j1 + f2 + f5), (double)((float)k1 + f46), d1, d5);
				ns1.addVertexWithUV((double)(i1 + 1), (double)((float)(j1 + 0) + f5), (double)(k1 + 0), d1, d7);
				ns1.addVertexWithUV((double)(i1 + 0), (double)((float)(j1 + 0) + f5), (double)(k1 + 0), d3, d7);
				ns1.addVertexWithUV((double)(i1 + 0), (double)((float)j1 + f2 + f5), (double)((float)k1 + f46), d3, d5);
			}

			if(Block.fire.canBlockCatchFire(this.blockAccess, i1, j1, k1 + 1)) {
				ns1.addVertexWithUV((double)(i1 + 1), (double)((float)j1 + f2 + f5), (double)((float)(k1 + 1) - f46), d1, d5);
				ns1.addVertexWithUV((double)(i1 + 1), (double)((float)(j1 + 0) + f5), (double)(k1 + 1 - 0), d1, d7);
				ns1.addVertexWithUV((double)(i1 + 0), (double)((float)(j1 + 0) + f5), (double)(k1 + 1 - 0), d3, d7);
				ns1.addVertexWithUV((double)(i1 + 0), (double)((float)j1 + f2 + f5), (double)((float)(k1 + 1) - f46), d3, d5);
				ns1.addVertexWithUV((double)(i1 + 0), (double)((float)j1 + f2 + f5), (double)((float)(k1 + 1) - f46), d3, d5);
				ns1.addVertexWithUV((double)(i1 + 0), (double)((float)(j1 + 0) + f5), (double)(k1 + 1 - 0), d3, d7);
				ns1.addVertexWithUV((double)(i1 + 1), (double)((float)(j1 + 0) + f5), (double)(k1 + 1 - 0), d1, d7);
				ns1.addVertexWithUV((double)(i1 + 1), (double)((float)j1 + f2 + f5), (double)((float)(k1 + 1) - f46), d1, d5);
			}

			if(Block.fire.canBlockCatchFire(this.blockAccess, i1, j1 + 1, k1)) {
				d12 = (double)i1 + 0.5D + 0.5D;
				d14 = (double)i1 + 0.5D - 0.5D;
				d16 = (double)k1 + 0.5D + 0.5D;
				d18 = (double)k1 + 0.5D - 0.5D;
				d20 = (double)i1 + 0.5D - 0.5D;
				d22 = (double)i1 + 0.5D + 0.5D;
				d24 = (double)k1 + 0.5D - 0.5D;
				double d25 = (double)k1 + 0.5D + 0.5D;
				double d2 = (double)((float)i2 / 256.0F);
				double d4 = (double)(((float)i2 + 15.99F) / 256.0F);
				double d6 = (double)((float)j2 / 256.0F);
				double d8 = (double)(((float)j2 + 15.99F) / 256.0F);
				++j1;
				float f3 = -0.2F;
				if((i1 + j1 + k1 & 1) == 0) {
					ns1.addVertexWithUV(d20, (double)((float)j1 + f3), (double)(k1 + 0), d4, d6);
					ns1.addVertexWithUV(d12, (double)(j1 + 0), (double)(k1 + 0), d4, d8);
					ns1.addVertexWithUV(d12, (double)(j1 + 0), (double)(k1 + 1), d2, d8);
					ns1.addVertexWithUV(d20, (double)((float)j1 + f3), (double)(k1 + 1), d2, d6);
					d2 = (double)((float)i2 / 256.0F);
					d4 = (double)(((float)i2 + 15.99F) / 256.0F);
					d6 = (double)((float)(j2 + 16) / 256.0F);
					d8 = (double)(((float)j2 + 15.99F + 16.0F) / 256.0F);
					ns1.addVertexWithUV(d22, (double)((float)j1 + f3), (double)(k1 + 1), d4, d6);
					ns1.addVertexWithUV(d14, (double)(j1 + 0), (double)(k1 + 1), d4, d8);
					ns1.addVertexWithUV(d14, (double)(j1 + 0), (double)(k1 + 0), d2, d8);
					ns1.addVertexWithUV(d22, (double)((float)j1 + f3), (double)(k1 + 0), d2, d6);
				} else {
					ns1.addVertexWithUV((double)(i1 + 0), (double)((float)j1 + f3), d25, d4, d6);
					ns1.addVertexWithUV((double)(i1 + 0), (double)(j1 + 0), d18, d4, d8);
					ns1.addVertexWithUV((double)(i1 + 1), (double)(j1 + 0), d18, d2, d8);
					ns1.addVertexWithUV((double)(i1 + 1), (double)((float)j1 + f3), d25, d2, d6);
					d2 = (double)((float)i2 / 256.0F);
					d4 = (double)(((float)i2 + 15.99F) / 256.0F);
					d6 = (double)((float)(j2 + 16) / 256.0F);
					d8 = (double)(((float)j2 + 15.99F + 16.0F) / 256.0F);
					ns1.addVertexWithUV((double)(i1 + 1), (double)((float)j1 + f3), d24, d4, d6);
					ns1.addVertexWithUV((double)(i1 + 1), (double)(j1 + 0), d16, d4, d8);
					ns1.addVertexWithUV((double)(i1 + 0), (double)(j1 + 0), d16, d2, d8);
					ns1.addVertexWithUV((double)(i1 + 0), (double)((float)j1 + f3), d24, d2, d6);
				}
			}
		} else {
			double f4 = (double)i1 + 0.5D + 0.2D;
			d12 = (double)i1 + 0.5D - 0.2D;
			d14 = (double)k1 + 0.5D + 0.2D;
			d16 = (double)k1 + 0.5D - 0.2D;
			d18 = (double)i1 + 0.5D - 0.3D;
			d20 = (double)i1 + 0.5D + 0.3D;
			d22 = (double)k1 + 0.5D - 0.3D;
			d24 = (double)k1 + 0.5D + 0.3D;
			ns1.addVertexWithUV(d18, (double)((float)j1 + f2), (double)(k1 + 1), d3, d5);
			ns1.addVertexWithUV(f4, (double)(j1 + 0), (double)(k1 + 1), d3, d7);
			ns1.addVertexWithUV(f4, (double)(j1 + 0), (double)(k1 + 0), d1, d7);
			ns1.addVertexWithUV(d18, (double)((float)j1 + f2), (double)(k1 + 0), d1, d5);
			ns1.addVertexWithUV(d20, (double)((float)j1 + f2), (double)(k1 + 0), d3, d5);
			ns1.addVertexWithUV(d12, (double)(j1 + 0), (double)(k1 + 0), d3, d7);
			ns1.addVertexWithUV(d12, (double)(j1 + 0), (double)(k1 + 1), d1, d7);
			ns1.addVertexWithUV(d20, (double)((float)j1 + f2), (double)(k1 + 1), d1, d5);
			d1 = (double)((float)i2 / 256.0F);
			d3 = (double)(((float)i2 + 15.99F) / 256.0F);
			d5 = (double)((float)(j2 + 16) / 256.0F);
			d7 = (double)(((float)j2 + 15.99F + 16.0F) / 256.0F);
			ns1.addVertexWithUV((double)(i1 + 1), (double)((float)j1 + f2), d24, d3, d5);
			ns1.addVertexWithUV((double)(i1 + 1), (double)(j1 + 0), d16, d3, d7);
			ns1.addVertexWithUV((double)(i1 + 0), (double)(j1 + 0), d16, d1, d7);
			ns1.addVertexWithUV((double)(i1 + 0), (double)((float)j1 + f2), d24, d1, d5);
			ns1.addVertexWithUV((double)(i1 + 0), (double)((float)j1 + f2), d22, d3, d5);
			ns1.addVertexWithUV((double)(i1 + 0), (double)(j1 + 0), d14, d3, d7);
			ns1.addVertexWithUV((double)(i1 + 1), (double)(j1 + 0), d14, d1, d7);
			ns1.addVertexWithUV((double)(i1 + 1), (double)((float)j1 + f2), d22, d1, d5);
			f4 = (double)i1 + 0.5D - 0.5D;
			d12 = (double)i1 + 0.5D + 0.5D;
			d14 = (double)k1 + 0.5D - 0.5D;
			d16 = (double)k1 + 0.5D + 0.5D;
			d18 = (double)i1 + 0.5D - 0.4D;
			d20 = (double)i1 + 0.5D + 0.4D;
			d22 = (double)k1 + 0.5D - 0.4D;
			d24 = (double)k1 + 0.5D + 0.4D;
			ns1.addVertexWithUV(d18, (double)((float)j1 + f2), (double)(k1 + 0), d1, d5);
			ns1.addVertexWithUV(f4, (double)(j1 + 0), (double)(k1 + 0), d1, d7);
			ns1.addVertexWithUV(f4, (double)(j1 + 0), (double)(k1 + 1), d3, d7);
			ns1.addVertexWithUV(d18, (double)((float)j1 + f2), (double)(k1 + 1), d3, d5);
			ns1.addVertexWithUV(d20, (double)((float)j1 + f2), (double)(k1 + 1), d1, d5);
			ns1.addVertexWithUV(d12, (double)(j1 + 0), (double)(k1 + 1), d1, d7);
			ns1.addVertexWithUV(d12, (double)(j1 + 0), (double)(k1 + 0), d3, d7);
			ns1.addVertexWithUV(d20, (double)((float)j1 + f2), (double)(k1 + 0), d3, d5);
			d1 = (double)((float)i2 / 256.0F);
			d3 = (double)(((float)i2 + 15.99F) / 256.0F);
			d5 = (double)((float)j2 / 256.0F);
			d7 = (double)(((float)j2 + 15.99F) / 256.0F);
			ns1.addVertexWithUV((double)(i1 + 0), (double)((float)j1 + f2), d24, d1, d5);
			ns1.addVertexWithUV((double)(i1 + 0), (double)(j1 + 0), d16, d1, d7);
			ns1.addVertexWithUV((double)(i1 + 1), (double)(j1 + 0), d16, d3, d7);
			ns1.addVertexWithUV((double)(i1 + 1), (double)((float)j1 + f2), d24, d3, d5);
			ns1.addVertexWithUV((double)(i1 + 1), (double)((float)j1 + f2), d22, d1, d5);
			ns1.addVertexWithUV((double)(i1 + 1), (double)(j1 + 0), d14, d1, d7);
			ns1.addVertexWithUV((double)(i1 + 0), (double)(j1 + 0), d14, d3, d7);
			ns1.addVertexWithUV((double)(i1 + 0), (double)((float)j1 + f2), d22, d3, d5);
		}

		return true;
	}

	public static void setRedstoneColors(float[][] colors) {
		if(colors.length != 16) {
			throw new IllegalArgumentException("Must be 16 colors.");
		} else {
			for(int i = 0; i < colors.length; ++i) {
				if(colors[i].length != 3) {
					throw new IllegalArgumentException("Must be 3 channels in a color.");
				}
			}

			redstoneColors = colors;
		}
	}

	public boolean renderBlockRedstoneWire(Block un1, int i1, int j1, int k1) {
		Tessellator ns1 = Tessellator.instance;
		int l1 = this.blockAccess.getBlockMetadata(i1, j1, k1);
		int i2 = un1.getBlockTextureFromSideAndMetadata(1, l1);
		if(this.overrideBlockTexture >= 0) {
			i2 = this.overrideBlockTexture;
		}

		float f1 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1);
		float[] color = redstoneColors[l1];
		float f3 = color[0];
		float f4 = color[1];
		float f5 = color[2];
		ns1.setColorOpaque_F(f1 * f3, f1 * f4, f1 * f5);
		int j2 = (i2 & 15) << 4;
		int k2 = i2 & 240;
		double d1 = (double)((float)j2 / 256.0F);
		double d3 = (double)(((float)j2 + 15.99F) / 256.0F);
		double d5 = (double)((float)k2 / 256.0F);
		double d7 = (double)(((float)k2 + 15.99F) / 256.0F);
		boolean flag = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i1 - 1, j1, k1) || !this.blockAccess.func_28100_h(i1 - 1, j1, k1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i1 - 1, j1 - 1, k1);
		boolean flag1 = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i1 + 1, j1, k1) || !this.blockAccess.func_28100_h(i1 + 1, j1, k1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i1 + 1, j1 - 1, k1);
		boolean flag2 = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i1, j1, k1 - 1) || !this.blockAccess.func_28100_h(i1, j1, k1 - 1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i1, j1 - 1, k1 - 1);
		boolean flag3 = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i1, j1, k1 + 1) || !this.blockAccess.func_28100_h(i1, j1, k1 + 1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i1, j1 - 1, k1 + 1);
		if(!this.blockAccess.func_28100_h(i1, j1 + 1, k1)) {
			if(this.blockAccess.func_28100_h(i1 - 1, j1, k1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i1 - 1, j1 + 1, k1)) {
				flag = true;
			}

			if(this.blockAccess.func_28100_h(i1 + 1, j1, k1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i1 + 1, j1 + 1, k1)) {
				flag1 = true;
			}

			if(this.blockAccess.func_28100_h(i1, j1, k1 - 1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i1, j1 + 1, k1 - 1)) {
				flag2 = true;
			}

			if(this.blockAccess.func_28100_h(i1, j1, k1 + 1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i1, j1 + 1, k1 + 1)) {
				flag3 = true;
			}
		}

		float f6 = (float)(i1 + 0);
		float f7 = (float)(i1 + 1);
		float f8 = (float)(k1 + 0);
		float f9 = (float)(k1 + 1);
		byte byte0 = 0;
		if((flag || flag1) && !flag2 && !flag3) {
			byte0 = 1;
		}

		if((flag2 || flag3) && !flag1 && !flag) {
			byte0 = 2;
		}

		if(byte0 != 0) {
			d1 = (double)((float)(j2 + 16) / 256.0F);
			d3 = (double)(((float)(j2 + 16) + 15.99F) / 256.0F);
			d5 = (double)((float)k2 / 256.0F);
			d7 = (double)(((float)k2 + 15.99F) / 256.0F);
		}

		if(byte0 == 0) {
			if(flag1 || flag2 || flag3 || flag) {
				if(!flag) {
					f6 += 0.3125F;
				}

				if(!flag) {
					d1 += 0.01953125D;
				}

				if(!flag1) {
					f7 -= 0.3125F;
				}

				if(!flag1) {
					d3 -= 0.01953125D;
				}

				if(!flag2) {
					f8 += 0.3125F;
				}

				if(!flag2) {
					d5 += 0.01953125D;
				}

				if(!flag3) {
					f9 -= 0.3125F;
				}

				if(!flag3) {
					d7 -= 0.01953125D;
				}
			}

			ns1.addVertexWithUV((double)f7, (double)((float)j1 + 0.015625F), (double)f9, d3, d7);
			ns1.addVertexWithUV((double)f7, (double)((float)j1 + 0.015625F), (double)f8, d3, d5);
			ns1.addVertexWithUV((double)f6, (double)((float)j1 + 0.015625F), (double)f8, d1, d5);
			ns1.addVertexWithUV((double)f6, (double)((float)j1 + 0.015625F), (double)f9, d1, d7);
			ns1.setColorOpaque_F(f1, f1, f1);
			ns1.addVertexWithUV((double)f7, (double)((float)j1 + 0.015625F), (double)f9, d3, d7 + 0.0625D);
			ns1.addVertexWithUV((double)f7, (double)((float)j1 + 0.015625F), (double)f8, d3, d5 + 0.0625D);
			ns1.addVertexWithUV((double)f6, (double)((float)j1 + 0.015625F), (double)f8, d1, d5 + 0.0625D);
			ns1.addVertexWithUV((double)f6, (double)((float)j1 + 0.015625F), (double)f9, d1, d7 + 0.0625D);
		} else if(byte0 == 1) {
			ns1.addVertexWithUV((double)f7, (double)((float)j1 + 0.015625F), (double)f9, d3, d7);
			ns1.addVertexWithUV((double)f7, (double)((float)j1 + 0.015625F), (double)f8, d3, d5);
			ns1.addVertexWithUV((double)f6, (double)((float)j1 + 0.015625F), (double)f8, d1, d5);
			ns1.addVertexWithUV((double)f6, (double)((float)j1 + 0.015625F), (double)f9, d1, d7);
			ns1.setColorOpaque_F(f1, f1, f1);
			ns1.addVertexWithUV((double)f7, (double)((float)j1 + 0.015625F), (double)f9, d3, d7 + 0.0625D);
			ns1.addVertexWithUV((double)f7, (double)((float)j1 + 0.015625F), (double)f8, d3, d5 + 0.0625D);
			ns1.addVertexWithUV((double)f6, (double)((float)j1 + 0.015625F), (double)f8, d1, d5 + 0.0625D);
			ns1.addVertexWithUV((double)f6, (double)((float)j1 + 0.015625F), (double)f9, d1, d7 + 0.0625D);
		} else if(byte0 == 2) {
			ns1.addVertexWithUV((double)f7, (double)((float)j1 + 0.015625F), (double)f9, d3, d7);
			ns1.addVertexWithUV((double)f7, (double)((float)j1 + 0.015625F), (double)f8, d1, d7);
			ns1.addVertexWithUV((double)f6, (double)((float)j1 + 0.015625F), (double)f8, d1, d5);
			ns1.addVertexWithUV((double)f6, (double)((float)j1 + 0.015625F), (double)f9, d3, d5);
			ns1.setColorOpaque_F(f1, f1, f1);
			ns1.addVertexWithUV((double)f7, (double)((float)j1 + 0.015625F), (double)f9, d3, d7 + 0.0625D);
			ns1.addVertexWithUV((double)f7, (double)((float)j1 + 0.015625F), (double)f8, d1, d7 + 0.0625D);
			ns1.addVertexWithUV((double)f6, (double)((float)j1 + 0.015625F), (double)f8, d1, d5 + 0.0625D);
			ns1.addVertexWithUV((double)f6, (double)((float)j1 + 0.015625F), (double)f9, d3, d5 + 0.0625D);
		}

		if(!this.blockAccess.func_28100_h(i1, j1 + 1, k1)) {
			double d2 = (double)((float)(j2 + 16) / 256.0F);
			double d4 = (double)(((float)(j2 + 16) + 15.99F) / 256.0F);
			double d6 = (double)((float)k2 / 256.0F);
			double d8 = (double)(((float)k2 + 15.99F) / 256.0F);
			if(this.blockAccess.func_28100_h(i1 - 1, j1, k1) && this.blockAccess.getBlockId(i1 - 1, j1 + 1, k1) == Block.redstoneWire.blockID) {
				ns1.setColorOpaque_F(f1 * f3, f1 * f4, f1 * f5);
				ns1.addVertexWithUV((double)((float)i1 + 0.015625F), (double)((float)(j1 + 1) + 0.021875F), (double)(k1 + 1), d4, d6);
				ns1.addVertexWithUV((double)((float)i1 + 0.015625F), (double)(j1 + 0), (double)(k1 + 1), d2, d6);
				ns1.addVertexWithUV((double)((float)i1 + 0.015625F), (double)(j1 + 0), (double)(k1 + 0), d2, d8);
				ns1.addVertexWithUV((double)((float)i1 + 0.015625F), (double)((float)(j1 + 1) + 0.021875F), (double)(k1 + 0), d4, d8);
				ns1.setColorOpaque_F(f1, f1, f1);
				ns1.addVertexWithUV((double)((float)i1 + 0.015625F), (double)((float)(j1 + 1) + 0.021875F), (double)(k1 + 1), d4, d6 + 0.0625D);
				ns1.addVertexWithUV((double)((float)i1 + 0.015625F), (double)(j1 + 0), (double)(k1 + 1), d2, d6 + 0.0625D);
				ns1.addVertexWithUV((double)((float)i1 + 0.015625F), (double)(j1 + 0), (double)(k1 + 0), d2, d8 + 0.0625D);
				ns1.addVertexWithUV((double)((float)i1 + 0.015625F), (double)((float)(j1 + 1) + 0.021875F), (double)(k1 + 0), d4, d8 + 0.0625D);
			}

			if(this.blockAccess.func_28100_h(i1 + 1, j1, k1) && this.blockAccess.getBlockId(i1 + 1, j1 + 1, k1) == Block.redstoneWire.blockID) {
				ns1.setColorOpaque_F(f1 * f3, f1 * f4, f1 * f5);
				ns1.addVertexWithUV((double)((float)(i1 + 1) - 0.015625F), (double)(j1 + 0), (double)(k1 + 1), d2, d8);
				ns1.addVertexWithUV((double)((float)(i1 + 1) - 0.015625F), (double)((float)(j1 + 1) + 0.021875F), (double)(k1 + 1), d4, d8);
				ns1.addVertexWithUV((double)((float)(i1 + 1) - 0.015625F), (double)((float)(j1 + 1) + 0.021875F), (double)(k1 + 0), d4, d6);
				ns1.addVertexWithUV((double)((float)(i1 + 1) - 0.015625F), (double)(j1 + 0), (double)(k1 + 0), d2, d6);
				ns1.setColorOpaque_F(f1, f1, f1);
				ns1.addVertexWithUV((double)((float)(i1 + 1) - 0.015625F), (double)(j1 + 0), (double)(k1 + 1), d2, d8 + 0.0625D);
				ns1.addVertexWithUV((double)((float)(i1 + 1) - 0.015625F), (double)((float)(j1 + 1) + 0.021875F), (double)(k1 + 1), d4, d8 + 0.0625D);
				ns1.addVertexWithUV((double)((float)(i1 + 1) - 0.015625F), (double)((float)(j1 + 1) + 0.021875F), (double)(k1 + 0), d4, d6 + 0.0625D);
				ns1.addVertexWithUV((double)((float)(i1 + 1) - 0.015625F), (double)(j1 + 0), (double)(k1 + 0), d2, d6 + 0.0625D);
			}

			if(this.blockAccess.func_28100_h(i1, j1, k1 - 1) && this.blockAccess.getBlockId(i1, j1 + 1, k1 - 1) == Block.redstoneWire.blockID) {
				ns1.setColorOpaque_F(f1 * f3, f1 * f4, f1 * f5);
				ns1.addVertexWithUV((double)(i1 + 1), (double)(j1 + 0), (double)((float)k1 + 0.015625F), d2, d8);
				ns1.addVertexWithUV((double)(i1 + 1), (double)((float)(j1 + 1) + 0.021875F), (double)((float)k1 + 0.015625F), d4, d8);
				ns1.addVertexWithUV((double)(i1 + 0), (double)((float)(j1 + 1) + 0.021875F), (double)((float)k1 + 0.015625F), d4, d6);
				ns1.addVertexWithUV((double)(i1 + 0), (double)(j1 + 0), (double)((float)k1 + 0.015625F), d2, d6);
				ns1.setColorOpaque_F(f1, f1, f1);
				ns1.addVertexWithUV((double)(i1 + 1), (double)(j1 + 0), (double)((float)k1 + 0.015625F), d2, d8 + 0.0625D);
				ns1.addVertexWithUV((double)(i1 + 1), (double)((float)(j1 + 1) + 0.021875F), (double)((float)k1 + 0.015625F), d4, d8 + 0.0625D);
				ns1.addVertexWithUV((double)(i1 + 0), (double)((float)(j1 + 1) + 0.021875F), (double)((float)k1 + 0.015625F), d4, d6 + 0.0625D);
				ns1.addVertexWithUV((double)(i1 + 0), (double)(j1 + 0), (double)((float)k1 + 0.015625F), d2, d6 + 0.0625D);
			}

			if(this.blockAccess.func_28100_h(i1, j1, k1 + 1) && this.blockAccess.getBlockId(i1, j1 + 1, k1 + 1) == Block.redstoneWire.blockID) {
				ns1.setColorOpaque_F(f1 * f3, f1 * f4, f1 * f5);
				ns1.addVertexWithUV((double)(i1 + 1), (double)((float)(j1 + 1) + 0.021875F), (double)((float)(k1 + 1) - 0.015625F), d4, d6);
				ns1.addVertexWithUV((double)(i1 + 1), (double)(j1 + 0), (double)((float)(k1 + 1) - 0.015625F), d2, d6);
				ns1.addVertexWithUV((double)(i1 + 0), (double)(j1 + 0), (double)((float)(k1 + 1) - 0.015625F), d2, d8);
				ns1.addVertexWithUV((double)(i1 + 0), (double)((float)(j1 + 1) + 0.021875F), (double)((float)(k1 + 1) - 0.015625F), d4, d8);
				ns1.setColorOpaque_F(f1, f1, f1);
				ns1.addVertexWithUV((double)(i1 + 1), (double)((float)(j1 + 1) + 0.021875F), (double)((float)(k1 + 1) - 0.015625F), d4, d6 + 0.0625D);
				ns1.addVertexWithUV((double)(i1 + 1), (double)(j1 + 0), (double)((float)(k1 + 1) - 0.015625F), d2, d6 + 0.0625D);
				ns1.addVertexWithUV((double)(i1 + 0), (double)(j1 + 0), (double)((float)(k1 + 1) - 0.015625F), d2, d8 + 0.0625D);
				ns1.addVertexWithUV((double)(i1 + 0), (double)((float)(j1 + 1) + 0.021875F), (double)((float)(k1 + 1) - 0.015625F), d4, d8 + 0.0625D);
			}
		}

		return true;
	}

	public boolean renderBlockMinecartTrack(BlockRail oy1, int i1, int j1, int k1) {
		Tessellator ns1 = Tessellator.instance;
		int l1 = this.blockAccess.getBlockMetadata(i1, j1, k1);
		int i2 = oy1.getBlockTextureFromSideAndMetadata(0, l1);
		if(this.overrideBlockTexture >= 0) {
			i2 = this.overrideBlockTexture;
		}

		if(oy1.getIsPowered()) {
			l1 &= 7;
		}

		float f1 = oy1.getBlockBrightness(this.blockAccess, i1, j1, k1);
		ns1.setColorOpaque_F(f1, f1, f1);
		int j2 = (i2 & 15) << 4;
		int k2 = i2 & 240;
		double d1 = (double)((float)j2 / 256.0F);
		double d2 = (double)(((float)j2 + 15.99F) / 256.0F);
		double d3 = (double)((float)k2 / 256.0F);
		double d4 = (double)(((float)k2 + 15.99F) / 256.0F);
		float f2 = 0.0625F;
		float f3 = (float)(i1 + 1);
		float f4 = (float)(i1 + 1);
		float f5 = (float)(i1 + 0);
		float f6 = (float)(i1 + 0);
		float f7 = (float)(k1 + 0);
		float f8 = (float)(k1 + 1);
		float f9 = (float)(k1 + 1);
		float f10 = (float)(k1 + 0);
		float f11 = (float)j1 + f2;
		float f12 = (float)j1 + f2;
		float f13 = (float)j1 + f2;
		float f14 = (float)j1 + f2;
		if(l1 != 1 && l1 != 2 && l1 != 3 && l1 != 7) {
			if(l1 == 8) {
				f3 = f4 = (float)(i1 + 0);
				f5 = f6 = (float)(i1 + 1);
				f7 = f10 = (float)(k1 + 1);
				f8 = f9 = (float)(k1 + 0);
			} else if(l1 == 9) {
				f3 = f6 = (float)(i1 + 0);
				f4 = f5 = (float)(i1 + 1);
				f7 = f8 = (float)(k1 + 0);
				f9 = f10 = (float)(k1 + 1);
			}
		} else {
			f3 = f6 = (float)(i1 + 1);
			f4 = f5 = (float)(i1 + 0);
			f7 = f8 = (float)(k1 + 1);
			f9 = f10 = (float)(k1 + 0);
		}

		if(l1 != 2 && l1 != 4) {
			if(l1 == 3 || l1 == 5) {
				++f12;
				++f13;
			}
		} else {
			++f11;
			++f14;
		}

		ns1.addVertexWithUV((double)f3, (double)f11, (double)f7, d2, d3);
		ns1.addVertexWithUV((double)f4, (double)f12, (double)f8, d2, d4);
		ns1.addVertexWithUV((double)f5, (double)f13, (double)f9, d1, d4);
		ns1.addVertexWithUV((double)f6, (double)f14, (double)f10, d1, d3);
		ns1.addVertexWithUV((double)f6, (double)f14, (double)f10, d1, d3);
		ns1.addVertexWithUV((double)f5, (double)f13, (double)f9, d1, d4);
		ns1.addVertexWithUV((double)f4, (double)f12, (double)f8, d2, d4);
		ns1.addVertexWithUV((double)f3, (double)f11, (double)f7, d2, d3);
		return true;
	}

	public boolean renderBlockLadder(Block un1, int i1, int j1, int k1) {
		Tessellator ns1 = Tessellator.instance;
		int l1 = un1.getBlockTextureFromSide(0);
		if(this.overrideBlockTexture >= 0) {
			l1 = this.overrideBlockTexture;
		}

		float f1 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1);
		ns1.setColorOpaque_F(f1, f1, f1);
		int i2 = (l1 & 15) << 4;
		int j2 = l1 & 240;
		double d1 = (double)((float)i2 / 256.0F);
		double d2 = (double)(((float)i2 + 15.99F) / 256.0F);
		double d3 = (double)((float)j2 / 256.0F);
		double d4 = (double)(((float)j2 + 15.99F) / 256.0F);
		int k2 = this.blockAccess.getBlockMetadata(i1, j1, k1);
		float f2 = 0.0F;
		float f3 = 0.05F;
		if(k2 == 5) {
			ns1.addVertexWithUV((double)((float)i1 + f3), (double)((float)(j1 + 1) + f2), (double)((float)(k1 + 1) + f2), d1, d3);
			ns1.addVertexWithUV((double)((float)i1 + f3), (double)((float)(j1 + 0) - f2), (double)((float)(k1 + 1) + f2), d1, d4);
			ns1.addVertexWithUV((double)((float)i1 + f3), (double)((float)(j1 + 0) - f2), (double)((float)(k1 + 0) - f2), d2, d4);
			ns1.addVertexWithUV((double)((float)i1 + f3), (double)((float)(j1 + 1) + f2), (double)((float)(k1 + 0) - f2), d2, d3);
		}

		if(k2 == 4) {
			ns1.addVertexWithUV((double)((float)(i1 + 1) - f3), (double)((float)(j1 + 0) - f2), (double)((float)(k1 + 1) + f2), d2, d4);
			ns1.addVertexWithUV((double)((float)(i1 + 1) - f3), (double)((float)(j1 + 1) + f2), (double)((float)(k1 + 1) + f2), d2, d3);
			ns1.addVertexWithUV((double)((float)(i1 + 1) - f3), (double)((float)(j1 + 1) + f2), (double)((float)(k1 + 0) - f2), d1, d3);
			ns1.addVertexWithUV((double)((float)(i1 + 1) - f3), (double)((float)(j1 + 0) - f2), (double)((float)(k1 + 0) - f2), d1, d4);
		}

		if(k2 == 3) {
			ns1.addVertexWithUV((double)((float)(i1 + 1) + f2), (double)((float)(j1 + 0) - f2), (double)((float)k1 + f3), d2, d4);
			ns1.addVertexWithUV((double)((float)(i1 + 1) + f2), (double)((float)(j1 + 1) + f2), (double)((float)k1 + f3), d2, d3);
			ns1.addVertexWithUV((double)((float)(i1 + 0) - f2), (double)((float)(j1 + 1) + f2), (double)((float)k1 + f3), d1, d3);
			ns1.addVertexWithUV((double)((float)(i1 + 0) - f2), (double)((float)(j1 + 0) - f2), (double)((float)k1 + f3), d1, d4);
		}

		if(k2 == 2) {
			ns1.addVertexWithUV((double)((float)(i1 + 1) + f2), (double)((float)(j1 + 1) + f2), (double)((float)(k1 + 1) - f3), d1, d3);
			ns1.addVertexWithUV((double)((float)(i1 + 1) + f2), (double)((float)(j1 + 0) - f2), (double)((float)(k1 + 1) - f3), d1, d4);
			ns1.addVertexWithUV((double)((float)(i1 + 0) - f2), (double)((float)(j1 + 0) - f2), (double)((float)(k1 + 1) - f3), d2, d4);
			ns1.addVertexWithUV((double)((float)(i1 + 0) - f2), (double)((float)(j1 + 1) + f2), (double)((float)(k1 + 1) - f3), d2, d3);
		}

		return true;
	}

	public boolean renderBlockReed(Block un1, int i1, int j1, int k1) {
		Tessellator ns1 = Tessellator.instance;
		float f1 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1);
		int l1 = un1.colorMultiplier(this.blockAccess, i1, j1, k1);
		float f2 = (float)(l1 >> 16 & 255) / 255.0F;
		float f3 = (float)(l1 >> 8 & 255) / 255.0F;
		float f4 = (float)(l1 & 255) / 255.0F;
		if(EntityRenderer.field_28135_a) {
			float d1 = (f2 * 30.0F + f3 * 59.0F + f4 * 11.0F) / 100.0F;
			float f6 = (f2 * 30.0F + f3 * 70.0F) / 100.0F;
			float d2 = (f2 * 30.0F + f4 * 70.0F) / 100.0F;
			f2 = d1;
			f3 = f6;
			f4 = d2;
		}

		ns1.setColorOpaque_F(f1 * f2, f1 * f3, f1 * f4);
		double d11 = (double)i1;
		double d21 = (double)j1;
		double d3 = (double)k1;
		if(un1 == Block.tallGrass) {
			long l2 = (long)(i1 * 3129871) ^ (long)k1 * 116129781L ^ (long)j1;
			l2 = l2 * l2 * 42317861L + l2 * 11L;
			d11 += ((double)((float)(l2 >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
			d21 += ((double)((float)(l2 >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
			d3 += ((double)((float)(l2 >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;
		}

		this.renderCrossedSquares(un1, this.blockAccess.getBlockMetadata(i1, j1, k1), d11, d21, d3);
		return true;
	}

	public boolean renderBlockCrops(Block un1, int i1, int j1, int k1) {
		Tessellator ns1 = Tessellator.instance;
		float f1 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1);
		ns1.setColorOpaque_F(f1, f1, f1);
		this.func_1245_b(un1, this.blockAccess.getBlockMetadata(i1, j1, k1), (double)i1, (double)j1 - 0.0625D, (double)k1);
		return true;
	}

	public void renderTorchAtAngle(Block un1, double d1, double d2, double d3, double d4, double d5) {
		Tessellator ns1 = Tessellator.instance;
		int i1 = un1.getBlockTextureFromSide(0);
		if(this.overrideBlockTexture >= 0) {
			i1 = this.overrideBlockTexture;
		}

		int j1 = (i1 & 15) << 4;
		int k1 = i1 & 240;
		float f1 = (float)j1 / 256.0F;
		float f2 = ((float)j1 + 15.99F) / 256.0F;
		float f3 = (float)k1 / 256.0F;
		float f4 = ((float)k1 + 15.99F) / 256.0F;
		double d6 = (double)f1 + 7.0D / 256D;
		double d7 = (double)f3 + 6.0D / 256D;
		double d8 = (double)f1 + 9.0D / 256D;
		double d9 = (double)f3 + 8.0D / 256D;
		d1 += 0.5D;
		d3 += 0.5D;
		double d10 = d1 - 0.5D;
		double d11 = d1 + 0.5D;
		double d12 = d3 - 0.5D;
		double d13 = d3 + 0.5D;
		double d14 = 0.0625D;
		double d15 = 0.625D;
		ns1.addVertexWithUV(d1 + d4 * (1.0D - d15) - d14, d2 + d15, d3 + d5 * (1.0D - d15) - d14, d6, d7);
		ns1.addVertexWithUV(d1 + d4 * (1.0D - d15) - d14, d2 + d15, d3 + d5 * (1.0D - d15) + d14, d6, d9);
		ns1.addVertexWithUV(d1 + d4 * (1.0D - d15) + d14, d2 + d15, d3 + d5 * (1.0D - d15) + d14, d8, d9);
		ns1.addVertexWithUV(d1 + d4 * (1.0D - d15) + d14, d2 + d15, d3 + d5 * (1.0D - d15) - d14, d8, d7);
		ns1.addVertexWithUV(d1 - d14, d2 + 1.0D, d12, (double)f1, (double)f3);
		ns1.addVertexWithUV(d1 - d14 + d4, d2 + 0.0D, d12 + d5, (double)f1, (double)f4);
		ns1.addVertexWithUV(d1 - d14 + d4, d2 + 0.0D, d13 + d5, (double)f2, (double)f4);
		ns1.addVertexWithUV(d1 - d14, d2 + 1.0D, d13, (double)f2, (double)f3);
		ns1.addVertexWithUV(d1 + d14, d2 + 1.0D, d13, (double)f1, (double)f3);
		ns1.addVertexWithUV(d1 + d4 + d14, d2 + 0.0D, d13 + d5, (double)f1, (double)f4);
		ns1.addVertexWithUV(d1 + d4 + d14, d2 + 0.0D, d12 + d5, (double)f2, (double)f4);
		ns1.addVertexWithUV(d1 + d14, d2 + 1.0D, d12, (double)f2, (double)f3);
		ns1.addVertexWithUV(d10, d2 + 1.0D, d3 + d14, (double)f1, (double)f3);
		ns1.addVertexWithUV(d10 + d4, d2 + 0.0D, d3 + d14 + d5, (double)f1, (double)f4);
		ns1.addVertexWithUV(d11 + d4, d2 + 0.0D, d3 + d14 + d5, (double)f2, (double)f4);
		ns1.addVertexWithUV(d11, d2 + 1.0D, d3 + d14, (double)f2, (double)f3);
		ns1.addVertexWithUV(d11, d2 + 1.0D, d3 - d14, (double)f1, (double)f3);
		ns1.addVertexWithUV(d11 + d4, d2 + 0.0D, d3 - d14 + d5, (double)f1, (double)f4);
		ns1.addVertexWithUV(d10 + d4, d2 + 0.0D, d3 - d14 + d5, (double)f2, (double)f4);
		ns1.addVertexWithUV(d10, d2 + 1.0D, d3 - d14, (double)f2, (double)f3);
	}

	public void renderCrossedSquares(Block un1, int i1, double d1, double d2, double d3) {
		Tessellator ns1 = Tessellator.instance;
		int j1 = un1.getBlockTextureFromSideAndMetadata(0, i1);
		if(this.overrideBlockTexture >= 0) {
			j1 = this.overrideBlockTexture;
		}

		int k1 = (j1 & 15) << 4;
		int l1 = j1 & 240;
		double d4 = (double)((float)k1 / 256.0F);
		double d5 = (double)(((float)k1 + 15.99F) / 256.0F);
		double d6 = (double)((float)l1 / 256.0F);
		double d7 = (double)(((float)l1 + 15.99F) / 256.0F);
		double d8 = d1 + 0.5D - (double)0.45F;
		double d9 = d1 + 0.5D + (double)0.45F;
		double d10 = d3 + 0.5D - (double)0.45F;
		double d11 = d3 + 0.5D + (double)0.45F;
		ns1.addVertexWithUV(d8, d2 + 1.0D, d10, d4, d6);
		ns1.addVertexWithUV(d8, d2 + 0.0D, d10, d4, d7);
		ns1.addVertexWithUV(d9, d2 + 0.0D, d11, d5, d7);
		ns1.addVertexWithUV(d9, d2 + 1.0D, d11, d5, d6);
		ns1.addVertexWithUV(d9, d2 + 1.0D, d11, d4, d6);
		ns1.addVertexWithUV(d9, d2 + 0.0D, d11, d4, d7);
		ns1.addVertexWithUV(d8, d2 + 0.0D, d10, d5, d7);
		ns1.addVertexWithUV(d8, d2 + 1.0D, d10, d5, d6);
		ns1.addVertexWithUV(d8, d2 + 1.0D, d11, d4, d6);
		ns1.addVertexWithUV(d8, d2 + 0.0D, d11, d4, d7);
		ns1.addVertexWithUV(d9, d2 + 0.0D, d10, d5, d7);
		ns1.addVertexWithUV(d9, d2 + 1.0D, d10, d5, d6);
		ns1.addVertexWithUV(d9, d2 + 1.0D, d10, d4, d6);
		ns1.addVertexWithUV(d9, d2 + 0.0D, d10, d4, d7);
		ns1.addVertexWithUV(d8, d2 + 0.0D, d11, d5, d7);
		ns1.addVertexWithUV(d8, d2 + 1.0D, d11, d5, d6);
	}

	public void func_1245_b(Block un1, int i1, double d1, double d2, double d3) {
		Tessellator ns1 = Tessellator.instance;
		int j1 = un1.getBlockTextureFromSideAndMetadata(0, i1);
		if(this.overrideBlockTexture >= 0) {
			j1 = this.overrideBlockTexture;
		}

		int k1 = (j1 & 15) << 4;
		int l1 = j1 & 240;
		double d4 = (double)((float)k1 / 256.0F);
		double d5 = (double)(((float)k1 + 15.99F) / 256.0F);
		double d6 = (double)((float)l1 / 256.0F);
		double d7 = (double)(((float)l1 + 15.99F) / 256.0F);
		double d8 = d1 + 0.5D - 0.25D;
		double d9 = d1 + 0.5D + 0.25D;
		double d10 = d3 + 0.5D - 0.5D;
		double d11 = d3 + 0.5D + 0.5D;
		ns1.addVertexWithUV(d8, d2 + 1.0D, d10, d4, d6);
		ns1.addVertexWithUV(d8, d2 + 0.0D, d10, d4, d7);
		ns1.addVertexWithUV(d8, d2 + 0.0D, d11, d5, d7);
		ns1.addVertexWithUV(d8, d2 + 1.0D, d11, d5, d6);
		ns1.addVertexWithUV(d8, d2 + 1.0D, d11, d4, d6);
		ns1.addVertexWithUV(d8, d2 + 0.0D, d11, d4, d7);
		ns1.addVertexWithUV(d8, d2 + 0.0D, d10, d5, d7);
		ns1.addVertexWithUV(d8, d2 + 1.0D, d10, d5, d6);
		ns1.addVertexWithUV(d9, d2 + 1.0D, d11, d4, d6);
		ns1.addVertexWithUV(d9, d2 + 0.0D, d11, d4, d7);
		ns1.addVertexWithUV(d9, d2 + 0.0D, d10, d5, d7);
		ns1.addVertexWithUV(d9, d2 + 1.0D, d10, d5, d6);
		ns1.addVertexWithUV(d9, d2 + 1.0D, d10, d4, d6);
		ns1.addVertexWithUV(d9, d2 + 0.0D, d10, d4, d7);
		ns1.addVertexWithUV(d9, d2 + 0.0D, d11, d5, d7);
		ns1.addVertexWithUV(d9, d2 + 1.0D, d11, d5, d6);
		d8 = d1 + 0.5D - 0.5D;
		d9 = d1 + 0.5D + 0.5D;
		d10 = d3 + 0.5D - 0.25D;
		d11 = d3 + 0.5D + 0.25D;
		ns1.addVertexWithUV(d8, d2 + 1.0D, d10, d4, d6);
		ns1.addVertexWithUV(d8, d2 + 0.0D, d10, d4, d7);
		ns1.addVertexWithUV(d9, d2 + 0.0D, d10, d5, d7);
		ns1.addVertexWithUV(d9, d2 + 1.0D, d10, d5, d6);
		ns1.addVertexWithUV(d9, d2 + 1.0D, d10, d4, d6);
		ns1.addVertexWithUV(d9, d2 + 0.0D, d10, d4, d7);
		ns1.addVertexWithUV(d8, d2 + 0.0D, d10, d5, d7);
		ns1.addVertexWithUV(d8, d2 + 1.0D, d10, d5, d6);
		ns1.addVertexWithUV(d9, d2 + 1.0D, d11, d4, d6);
		ns1.addVertexWithUV(d9, d2 + 0.0D, d11, d4, d7);
		ns1.addVertexWithUV(d8, d2 + 0.0D, d11, d5, d7);
		ns1.addVertexWithUV(d8, d2 + 1.0D, d11, d5, d6);
		ns1.addVertexWithUV(d8, d2 + 1.0D, d11, d4, d6);
		ns1.addVertexWithUV(d8, d2 + 0.0D, d11, d4, d7);
		ns1.addVertexWithUV(d9, d2 + 0.0D, d11, d5, d7);
		ns1.addVertexWithUV(d9, d2 + 1.0D, d11, d5, d6);
	}

	public boolean renderBlockFluids(Block un1, int i1, int j1, int k1) {
		Tessellator ns1 = Tessellator.instance;
		int l1 = un1.colorMultiplier(this.blockAccess, i1, j1, k1);
		float f1 = (float)(l1 >> 16 & 255) / 255.0F;
		float f2 = (float)(l1 >> 8 & 255) / 255.0F;
		float f3 = (float)(l1 & 255) / 255.0F;
		boolean flag = un1.shouldSideBeRendered(this.blockAccess, i1, j1 + 1, k1, 1);
		boolean flag1 = un1.shouldSideBeRendered(this.blockAccess, i1, j1 - 1, k1, 0);
		boolean[] aflag = new boolean[]{un1.shouldSideBeRendered(this.blockAccess, i1, j1, k1 - 1, 2), un1.shouldSideBeRendered(this.blockAccess, i1, j1, k1 + 1, 3), un1.shouldSideBeRendered(this.blockAccess, i1 - 1, j1, k1, 4), un1.shouldSideBeRendered(this.blockAccess, i1 + 1, j1, k1, 5)};
		if(!flag && !flag1 && !aflag[0] && !aflag[1] && !aflag[2] && !aflag[3]) {
			return false;
		} else {
			boolean flag2 = false;
			float f4 = 0.5F;
			float f5 = 1.0F;
			float f6 = 0.8F;
			float f7 = 0.6F;
			double d1 = 0.0D;
			double d2 = 1.0D;
			Material lj1 = un1.blockMaterial;
			int i2 = this.blockAccess.getBlockMetadata(i1, j1, k1);
			float f8 = this.func_1224_a(i1, j1, k1, lj1);
			float f9 = this.func_1224_a(i1, j1, k1 + 1, lj1);
			float f10 = this.func_1224_a(i1 + 1, j1, k1 + 1, lj1);
			float f11 = this.func_1224_a(i1 + 1, j1, k1, lj1);
			int k2;
			int l3;
			float f16;
			float f18;
			float f20;
			if(this.renderAllFaces || flag) {
				flag2 = true;
				k2 = un1.getBlockTextureFromSideAndMetadata(1, i2);
				float l2 = (float)BlockFluid.func_293_a(this.blockAccess, i1, j1, k1, lj1);
				if(l2 > -999.0F) {
					k2 = un1.getBlockTextureFromSideAndMetadata(2, i2);
				}

				int j3 = (k2 & 15) << 4;
				l3 = k2 & 240;
				double i4 = ((double)j3 + 8.0D) / 256.0D;
				double k4 = ((double)l3 + 8.0D) / 256.0D;
				if(l2 < -999.0F) {
					l2 = 0.0F;
				} else {
					i4 = (double)((float)(j3 + 16) / 256.0F);
					k4 = (double)((float)(l3 + 16) / 256.0F);
				}

				f16 = MathHelper.sin(l2) * 8.0F / 256.0F;
				f18 = MathHelper.cos(l2) * 8.0F / 256.0F;
				f20 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1);
				ns1.setColorOpaque_F(f5 * f20 * f1, f5 * f20 * f2, f5 * f20 * f3);
				ns1.addVertexWithUV((double)(i1 + 0), (double)((float)j1 + f8), (double)(k1 + 0), i4 - (double)f18 - (double)f16, k4 - (double)f18 + (double)f16);
				ns1.addVertexWithUV((double)(i1 + 0), (double)((float)j1 + f9), (double)(k1 + 1), i4 - (double)f18 + (double)f16, k4 + (double)f18 + (double)f16);
				ns1.addVertexWithUV((double)(i1 + 1), (double)((float)j1 + f10), (double)(k1 + 1), i4 + (double)f18 + (double)f16, k4 + (double)f18 - (double)f16);
				ns1.addVertexWithUV((double)(i1 + 1), (double)((float)j1 + f11), (double)(k1 + 0), i4 + (double)f18 - (double)f16, k4 - (double)f18 - (double)f16);
			}

			if(this.renderAllFaces || flag1) {
				float f52 = un1.getBlockBrightness(this.blockAccess, i1, j1 - 1, k1);
				ns1.setColorOpaque_F(f4 * f52, f4 * f52, f4 * f52);
				this.renderBottomFace(un1, (double)i1, (double)j1, (double)k1, un1.getBlockTextureFromSide(0));
				flag2 = true;
			}

			for(k2 = 0; k2 < 4; ++k2) {
				int i53 = i1;
				l3 = k1;
				if(k2 == 0) {
					l3 = k1 - 1;
				}

				if(k2 == 1) {
					++l3;
				}

				if(k2 == 2) {
					i53 = i1 - 1;
				}

				if(k2 == 3) {
					++i53;
				}

				int i54 = un1.getBlockTextureFromSideAndMetadata(k2 + 2, i2);
				int j4 = (i54 & 15) << 4;
				int i55 = i54 & 240;
				if(this.renderAllFaces || aflag[k2]) {
					float f14;
					float f21;
					float f22;
					if(k2 == 0) {
						f14 = f8;
						f16 = f11;
						f18 = (float)i1;
						f21 = (float)(i1 + 1);
						f20 = (float)k1;
						f22 = (float)k1;
					} else if(k2 == 1) {
						f14 = f10;
						f16 = f9;
						f18 = (float)(i1 + 1);
						f21 = (float)i1;
						f20 = (float)(k1 + 1);
						f22 = (float)(k1 + 1);
					} else if(k2 == 2) {
						f14 = f9;
						f16 = f8;
						f18 = (float)i1;
						f21 = (float)i1;
						f20 = (float)(k1 + 1);
						f22 = (float)k1;
					} else {
						f14 = f11;
						f16 = f10;
						f18 = (float)(i1 + 1);
						f21 = (float)(i1 + 1);
						f20 = (float)k1;
						f22 = (float)(k1 + 1);
					}

					flag2 = true;
					double d5 = (double)((float)(j4 + 0) / 256.0F);
					double d6 = ((double)(j4 + 16) - 0.01D) / 256.0D;
					double d7 = (double)(((float)i55 + (1.0F - f14) * 16.0F) / 256.0F);
					double d8 = (double)(((float)i55 + (1.0F - f16) * 16.0F) / 256.0F);
					double d9 = ((double)(i55 + 16) - 0.01D) / 256.0D;
					float f23 = un1.getBlockBrightness(this.blockAccess, i53, j1, l3);
					if(k2 < 2) {
						f23 *= f6;
					} else {
						f23 *= f7;
					}

					ns1.setColorOpaque_F(f5 * f23 * f1, f5 * f23 * f2, f5 * f23 * f3);
					ns1.addVertexWithUV((double)f18, (double)((float)j1 + f14), (double)f20, d5, d7);
					ns1.addVertexWithUV((double)f21, (double)((float)j1 + f16), (double)f22, d6, d8);
					ns1.addVertexWithUV((double)f21, (double)(j1 + 0), (double)f22, d6, d9);
					ns1.addVertexWithUV((double)f18, (double)(j1 + 0), (double)f20, d5, d9);
				}
			}

			un1.minY = d1;
			un1.maxY = d2;
			return flag2;
		}
	}

	public float func_1224_a(int i1, int j1, int k1, Material lj1) {
		int l1 = 0;
		float f1 = 0.0F;

		for(int i2 = 0; i2 < 4; ++i2) {
			int j2 = i1 - (i2 & 1);
			int l2 = k1 - (i2 >> 1 & 1);
			if(this.blockAccess.getBlockMaterial(j2, j1 + 1, l2) == lj1) {
				return 1.0F;
			}

			Material lj2 = this.blockAccess.getBlockMaterial(j2, j1, l2);
			if(lj2 == lj1) {
				int i3 = this.blockAccess.getBlockMetadata(j2, j1, l2);
				if(i3 >= 8 || i3 == 0) {
					f1 += BlockFluid.getPercentAir(i3) * 10.0F;
					l1 += 10;
				}

				f1 += BlockFluid.getPercentAir(i3);
				++l1;
			} else if(!lj2.isSolid()) {
				++f1;
				++l1;
			}
		}

		return 1.0F - f1 / (float)l1;
	}

	public void renderBlockFallingSand(Block un1, World fb, int i1, int j1, int k1) {
		float f1 = 0.5F;
		float f2 = 1.0F;
		float f3 = 0.8F;
		float f4 = 0.6F;
		Tessellator ns1 = Tessellator.instance;
		ns1.startDrawingQuads();
		float f5 = un1.getBlockBrightness(fb, i1, j1, k1);
		float f6 = un1.getBlockBrightness(fb, i1, j1 - 1, k1);
		if(f6 < f5) {
			f6 = f5;
		}

		ns1.setColorOpaque_F(f1 * f6, f1 * f6, f1 * f6);
		this.renderBottomFace(un1, -0.5D, -0.5D, -0.5D, un1.getBlockTextureFromSide(0));
		f6 = un1.getBlockBrightness(fb, i1, j1 + 1, k1);
		if(f6 < f5) {
			f6 = f5;
		}

		ns1.setColorOpaque_F(f2 * f6, f2 * f6, f2 * f6);
		this.renderTopFace(un1, -0.5D, -0.5D, -0.5D, un1.getBlockTextureFromSide(1));
		f6 = un1.getBlockBrightness(fb, i1, j1, k1 - 1);
		if(f6 < f5) {
			f6 = f5;
		}

		ns1.setColorOpaque_F(f3 * f6, f3 * f6, f3 * f6);
		this.renderEastFace(un1, -0.5D, -0.5D, -0.5D, un1.getBlockTextureFromSide(2));
		f6 = un1.getBlockBrightness(fb, i1, j1, k1 + 1);
		if(f6 < f5) {
			f6 = f5;
		}

		ns1.setColorOpaque_F(f3 * f6, f3 * f6, f3 * f6);
		this.renderWestFace(un1, -0.5D, -0.5D, -0.5D, un1.getBlockTextureFromSide(3));
		f6 = un1.getBlockBrightness(fb, i1 - 1, j1, k1);
		if(f6 < f5) {
			f6 = f5;
		}

		ns1.setColorOpaque_F(f4 * f6, f4 * f6, f4 * f6);
		this.renderNorthFace(un1, -0.5D, -0.5D, -0.5D, un1.getBlockTextureFromSide(4));
		f6 = un1.getBlockBrightness(fb, i1 + 1, j1, k1);
		if(f6 < f5) {
			f6 = f5;
		}

		ns1.setColorOpaque_F(f4 * f6, f4 * f6, f4 * f6);
		this.renderSouthFace(un1, -0.5D, -0.5D, -0.5D, un1.getBlockTextureFromSide(5));
		ns1.draw();
	}

	public boolean renderStandardBlock(Block un1, int i1, int j1, int k1) {
		int l1 = un1.colorMultiplier(this.blockAccess, i1, j1, k1);
		float f1 = (float)(l1 >> 16 & 255) / 255.0F;
		float f2 = (float)(l1 >> 8 & 255) / 255.0F;
		float f3 = (float)(l1 & 255) / 255.0F;
		if(EntityRenderer.field_28135_a) {
			float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
			float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
			float f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
			f1 = f4;
			f2 = f5;
			f3 = f6;
		}

		return Minecraft.isAmbientOcclusionEnabled() ? this.renderStandardBlockWithAmbientOcclusion(un1, i1, j1, k1, f1, f2, f3) : this.renderStandardBlockWithColorMultiplier(un1, i1, j1, k1, f1, f2, f3);
	}

	public boolean renderStandardBlockWithAmbientOcclusion(Block un1, int i1, int j1, int k1, float f1, float f2, float f3) {
		this.enableAO = true;
		boolean flag = false;
		float f4 = this.field_22384_f;
		float f11 = this.field_22384_f;
		float f18 = this.field_22384_f;
		float f25 = this.field_22384_f;
		boolean flag1 = true;
		boolean flag2 = true;
		boolean flag3 = true;
		boolean flag4 = true;
		boolean flag5 = true;
		boolean flag6 = true;
		this.field_22384_f = un1.getBlockBrightness(this.blockAccess, i1, j1, k1);
		this.aoLightValueXNeg = un1.getBlockBrightness(this.blockAccess, i1 - 1, j1, k1);
		this.aoLightValueYNeg = un1.getBlockBrightness(this.blockAccess, i1, j1 - 1, k1);
		this.aoLightValueZNeg = un1.getBlockBrightness(this.blockAccess, i1, j1, k1 - 1);
		this.aoLightValueXPos = un1.getBlockBrightness(this.blockAccess, i1 + 1, j1, k1);
		this.aoLightValueYPos = un1.getBlockBrightness(this.blockAccess, i1, j1 + 1, k1);
		this.aoLightValueZPos = un1.getBlockBrightness(this.blockAccess, i1, j1, k1 + 1);
		this.field_22338_U = Block.canBlockGrass[this.blockAccess.getBlockId(i1 + 1, j1 + 1, k1)];
		this.field_22359_ac = Block.canBlockGrass[this.blockAccess.getBlockId(i1 + 1, j1 - 1, k1)];
		this.field_22334_Y = Block.canBlockGrass[this.blockAccess.getBlockId(i1 + 1, j1, k1 + 1)];
		this.field_22363_aa = Block.canBlockGrass[this.blockAccess.getBlockId(i1 + 1, j1, k1 - 1)];
		this.field_22337_V = Block.canBlockGrass[this.blockAccess.getBlockId(i1 - 1, j1 + 1, k1)];
		this.field_22357_ad = Block.canBlockGrass[this.blockAccess.getBlockId(i1 - 1, j1 - 1, k1)];
		this.field_22335_X = Block.canBlockGrass[this.blockAccess.getBlockId(i1 - 1, j1, k1 - 1)];
		this.field_22333_Z = Block.canBlockGrass[this.blockAccess.getBlockId(i1 - 1, j1, k1 + 1)];
		this.field_22336_W = Block.canBlockGrass[this.blockAccess.getBlockId(i1, j1 + 1, k1 + 1)];
		this.field_22339_T = Block.canBlockGrass[this.blockAccess.getBlockId(i1, j1 + 1, k1 - 1)];
		this.field_22355_ae = Block.canBlockGrass[this.blockAccess.getBlockId(i1, j1 - 1, k1 + 1)];
		this.field_22361_ab = Block.canBlockGrass[this.blockAccess.getBlockId(i1, j1 - 1, k1 - 1)];
		if(un1.blockIndexInTexture == 3) {
			flag6 = false;
			flag5 = false;
			flag4 = false;
			flag3 = false;
			flag1 = false;
		}

		if(this.overrideBlockTexture >= 0) {
			flag6 = false;
			flag5 = false;
			flag4 = false;
			flag3 = false;
			flag1 = false;
		}

		float f10;
		float f17;
		float f24;
		float f31;
		if(this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1, j1 - 1, k1, 0)) {
			if(this.field_22352_G <= 0) {
				f31 = this.aoLightValueYNeg;
				f24 = this.aoLightValueYNeg;
				f17 = this.aoLightValueYNeg;
				f10 = this.aoLightValueYNeg;
			} else {
				--j1;
				this.field_22376_n = un1.getBlockBrightness(this.blockAccess, i1 - 1, j1, k1);
				this.field_22374_p = un1.getBlockBrightness(this.blockAccess, i1, j1, k1 - 1);
				this.field_22373_q = un1.getBlockBrightness(this.blockAccess, i1, j1, k1 + 1);
				this.field_22371_s = un1.getBlockBrightness(this.blockAccess, i1 + 1, j1, k1);
				if(!this.field_22361_ab && !this.field_22357_ad) {
					this.field_22377_m = this.field_22376_n;
				} else {
					this.field_22377_m = un1.getBlockBrightness(this.blockAccess, i1 - 1, j1, k1 - 1);
				}

				if(!this.field_22355_ae && !this.field_22357_ad) {
					this.field_22375_o = this.field_22376_n;
				} else {
					this.field_22375_o = un1.getBlockBrightness(this.blockAccess, i1 - 1, j1, k1 + 1);
				}

				if(!this.field_22361_ab && !this.field_22359_ac) {
					this.field_22372_r = this.field_22371_s;
				} else {
					this.field_22372_r = un1.getBlockBrightness(this.blockAccess, i1 + 1, j1, k1 - 1);
				}

				if(!this.field_22355_ae && !this.field_22359_ac) {
					this.field_22370_t = this.field_22371_s;
				} else {
					this.field_22370_t = un1.getBlockBrightness(this.blockAccess, i1 + 1, j1, k1 + 1);
				}

				++j1;
				f10 = (this.field_22375_o + this.field_22376_n + this.field_22373_q + this.aoLightValueYNeg) / 4.0F;
				f31 = (this.field_22373_q + this.aoLightValueYNeg + this.field_22370_t + this.field_22371_s) / 4.0F;
				f24 = (this.aoLightValueYNeg + this.field_22374_p + this.field_22371_s + this.field_22372_r) / 4.0F;
				f17 = (this.field_22376_n + this.field_22377_m + this.aoLightValueYNeg + this.field_22374_p) / 4.0F;
			}

			this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = (flag1 ? f1 : 1.0F) * 0.5F;
			this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = (flag1 ? f2 : 1.0F) * 0.5F;
			this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = (flag1 ? f3 : 1.0F) * 0.5F;
			this.colorRedTopLeft *= f10;
			this.colorGreenTopLeft *= f10;
			this.colorBlueTopLeft *= f10;
			this.colorRedBottomLeft *= f17;
			this.colorGreenBottomLeft *= f17;
			this.colorBlueBottomLeft *= f17;
			this.colorRedBottomRight *= f24;
			this.colorGreenBottomRight *= f24;
			this.colorBlueBottomRight *= f24;
			this.colorRedTopRight *= f31;
			this.colorGreenTopRight *= f31;
			this.colorBlueTopRight *= f31;
			this.renderBottomFace(un1, (double)i1, (double)j1, (double)k1, un1.getBlockTexture(this.blockAccess, i1, j1, k1, 0));
			flag = true;
		}

		if(this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1, j1 + 1, k1, 1)) {
			if(this.field_22352_G <= 0) {
				f31 = this.aoLightValueYPos;
				f24 = this.aoLightValueYPos;
				f17 = this.aoLightValueYPos;
				f10 = this.aoLightValueYPos;
			} else {
				++j1;
				this.field_22368_v = un1.getBlockBrightness(this.blockAccess, i1 - 1, j1, k1);
				this.field_22364_z = un1.getBlockBrightness(this.blockAccess, i1 + 1, j1, k1);
				this.field_22366_x = un1.getBlockBrightness(this.blockAccess, i1, j1, k1 - 1);
				this.field_22362_A = un1.getBlockBrightness(this.blockAccess, i1, j1, k1 + 1);
				if(!this.field_22339_T && !this.field_22337_V) {
					this.field_22369_u = this.field_22368_v;
				} else {
					this.field_22369_u = un1.getBlockBrightness(this.blockAccess, i1 - 1, j1, k1 - 1);
				}

				if(!this.field_22339_T && !this.field_22338_U) {
					this.field_22365_y = this.field_22364_z;
				} else {
					this.field_22365_y = un1.getBlockBrightness(this.blockAccess, i1 + 1, j1, k1 - 1);
				}

				if(!this.field_22336_W && !this.field_22337_V) {
					this.field_22367_w = this.field_22368_v;
				} else {
					this.field_22367_w = un1.getBlockBrightness(this.blockAccess, i1 - 1, j1, k1 + 1);
				}

				if(!this.field_22336_W && !this.field_22338_U) {
					this.field_22360_B = this.field_22364_z;
				} else {
					this.field_22360_B = un1.getBlockBrightness(this.blockAccess, i1 + 1, j1, k1 + 1);
				}

				--j1;
				f31 = (this.field_22367_w + this.field_22368_v + this.field_22362_A + this.aoLightValueYPos) / 4.0F;
				f10 = (this.field_22362_A + this.aoLightValueYPos + this.field_22360_B + this.field_22364_z) / 4.0F;
				f17 = (this.aoLightValueYPos + this.field_22366_x + this.field_22364_z + this.field_22365_y) / 4.0F;
				f24 = (this.field_22368_v + this.field_22369_u + this.aoLightValueYPos + this.field_22366_x) / 4.0F;
			}

			this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = flag2 ? f1 : 1.0F;
			this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = flag2 ? f2 : 1.0F;
			this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = flag2 ? f3 : 1.0F;
			this.colorRedTopLeft *= f10;
			this.colorGreenTopLeft *= f10;
			this.colorBlueTopLeft *= f10;
			this.colorRedBottomLeft *= f17;
			this.colorGreenBottomLeft *= f17;
			this.colorBlueBottomLeft *= f17;
			this.colorRedBottomRight *= f24;
			this.colorGreenBottomRight *= f24;
			this.colorBlueBottomRight *= f24;
			this.colorRedTopRight *= f31;
			this.colorGreenTopRight *= f31;
			this.colorBlueTopRight *= f31;
			this.renderTopFace(un1, (double)i1, (double)j1, (double)k1, un1.getBlockTexture(this.blockAccess, i1, j1, k1, 1));
			flag = true;
		}

		int k2;
		if(this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1, j1, k1 - 1, 2)) {
			if(this.field_22352_G <= 0) {
				f31 = this.aoLightValueZNeg;
				f24 = this.aoLightValueZNeg;
				f17 = this.aoLightValueZNeg;
				f10 = this.aoLightValueZNeg;
			} else {
				--k1;
				this.field_22358_C = un1.getBlockBrightness(this.blockAccess, i1 - 1, j1, k1);
				this.field_22374_p = un1.getBlockBrightness(this.blockAccess, i1, j1 - 1, k1);
				this.field_22366_x = un1.getBlockBrightness(this.blockAccess, i1, j1 + 1, k1);
				this.field_22356_D = un1.getBlockBrightness(this.blockAccess, i1 + 1, j1, k1);
				if(!this.field_22335_X && !this.field_22361_ab) {
					this.field_22377_m = this.field_22358_C;
				} else {
					this.field_22377_m = un1.getBlockBrightness(this.blockAccess, i1 - 1, j1 - 1, k1);
				}

				if(!this.field_22335_X && !this.field_22339_T) {
					this.field_22369_u = this.field_22358_C;
				} else {
					this.field_22369_u = un1.getBlockBrightness(this.blockAccess, i1 - 1, j1 + 1, k1);
				}

				if(!this.field_22363_aa && !this.field_22361_ab) {
					this.field_22372_r = this.field_22356_D;
				} else {
					this.field_22372_r = un1.getBlockBrightness(this.blockAccess, i1 + 1, j1 - 1, k1);
				}

				if(!this.field_22363_aa && !this.field_22339_T) {
					this.field_22365_y = this.field_22356_D;
				} else {
					this.field_22365_y = un1.getBlockBrightness(this.blockAccess, i1 + 1, j1 + 1, k1);
				}

				++k1;
				f10 = (this.field_22358_C + this.field_22369_u + this.aoLightValueZNeg + this.field_22366_x) / 4.0F;
				f17 = (this.aoLightValueZNeg + this.field_22366_x + this.field_22356_D + this.field_22365_y) / 4.0F;
				f24 = (this.field_22374_p + this.aoLightValueZNeg + this.field_22372_r + this.field_22356_D) / 4.0F;
				f31 = (this.field_22377_m + this.field_22358_C + this.field_22374_p + this.aoLightValueZNeg) / 4.0F;
			}

			this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = (flag3 ? f1 : 1.0F) * 0.8F;
			this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = (flag3 ? f2 : 1.0F) * 0.8F;
			this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = (flag3 ? f3 : 1.0F) * 0.8F;
			this.colorRedTopLeft *= f10;
			this.colorGreenTopLeft *= f10;
			this.colorBlueTopLeft *= f10;
			this.colorRedBottomLeft *= f17;
			this.colorGreenBottomLeft *= f17;
			this.colorBlueBottomLeft *= f17;
			this.colorRedBottomRight *= f24;
			this.colorGreenBottomRight *= f24;
			this.colorBlueBottomRight *= f24;
			this.colorRedTopRight *= f31;
			this.colorGreenTopRight *= f31;
			this.colorBlueTopRight *= f31;
			k2 = un1.getBlockTexture(this.blockAccess, i1, j1, k1, 2);
			this.renderEastFace(un1, (double)i1, (double)j1, (double)k1, k2);
			if(cfgGrassFix && k2 == 3 && this.overrideBlockTexture < 0) {
				this.colorRedTopLeft *= f1;
				this.colorRedBottomLeft *= f1;
				this.colorRedBottomRight *= f1;
				this.colorRedTopRight *= f1;
				this.colorGreenTopLeft *= f2;
				this.colorGreenBottomLeft *= f2;
				this.colorGreenBottomRight *= f2;
				this.colorGreenTopRight *= f2;
				this.colorBlueTopLeft *= f3;
				this.colorBlueBottomLeft *= f3;
				this.colorBlueBottomRight *= f3;
				this.colorBlueTopRight *= f3;
				this.renderEastFace(un1, (double)i1, (double)j1, (double)k1, 38);
			}

			flag = true;
		}

		if(this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1, j1, k1 + 1, 3)) {
			if(this.field_22352_G <= 0) {
				f31 = this.aoLightValueZPos;
				f24 = this.aoLightValueZPos;
				f17 = this.aoLightValueZPos;
				f10 = this.aoLightValueZPos;
			} else {
				++k1;
				this.field_22354_E = un1.getBlockBrightness(this.blockAccess, i1 - 1, j1, k1);
				this.field_22353_F = un1.getBlockBrightness(this.blockAccess, i1 + 1, j1, k1);
				this.field_22373_q = un1.getBlockBrightness(this.blockAccess, i1, j1 - 1, k1);
				this.field_22362_A = un1.getBlockBrightness(this.blockAccess, i1, j1 + 1, k1);
				if(!this.field_22333_Z && !this.field_22355_ae) {
					this.field_22375_o = this.field_22354_E;
				} else {
					this.field_22375_o = un1.getBlockBrightness(this.blockAccess, i1 - 1, j1 - 1, k1);
				}

				if(!this.field_22333_Z && !this.field_22336_W) {
					this.field_22367_w = this.field_22354_E;
				} else {
					this.field_22367_w = un1.getBlockBrightness(this.blockAccess, i1 - 1, j1 + 1, k1);
				}

				if(!this.field_22334_Y && !this.field_22355_ae) {
					this.field_22370_t = this.field_22353_F;
				} else {
					this.field_22370_t = un1.getBlockBrightness(this.blockAccess, i1 + 1, j1 - 1, k1);
				}

				if(!this.field_22334_Y && !this.field_22336_W) {
					this.field_22360_B = this.field_22353_F;
				} else {
					this.field_22360_B = un1.getBlockBrightness(this.blockAccess, i1 + 1, j1 + 1, k1);
				}

				--k1;
				f10 = (this.field_22354_E + this.field_22367_w + this.aoLightValueZPos + this.field_22362_A) / 4.0F;
				f31 = (this.aoLightValueZPos + this.field_22362_A + this.field_22353_F + this.field_22360_B) / 4.0F;
				f24 = (this.field_22373_q + this.aoLightValueZPos + this.field_22370_t + this.field_22353_F) / 4.0F;
				f17 = (this.field_22375_o + this.field_22354_E + this.field_22373_q + this.aoLightValueZPos) / 4.0F;
			}

			this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = (flag4 ? f1 : 1.0F) * 0.8F;
			this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = (flag4 ? f2 : 1.0F) * 0.8F;
			this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = (flag4 ? f3 : 1.0F) * 0.8F;
			this.colorRedTopLeft *= f10;
			this.colorGreenTopLeft *= f10;
			this.colorBlueTopLeft *= f10;
			this.colorRedBottomLeft *= f17;
			this.colorGreenBottomLeft *= f17;
			this.colorBlueBottomLeft *= f17;
			this.colorRedBottomRight *= f24;
			this.colorGreenBottomRight *= f24;
			this.colorBlueBottomRight *= f24;
			this.colorRedTopRight *= f31;
			this.colorGreenTopRight *= f31;
			this.colorBlueTopRight *= f31;
			k2 = un1.getBlockTexture(this.blockAccess, i1, j1, k1, 3);
			this.renderWestFace(un1, (double)i1, (double)j1, (double)k1, un1.getBlockTexture(this.blockAccess, i1, j1, k1, 3));
			if(cfgGrassFix && k2 == 3 && this.overrideBlockTexture < 0) {
				this.colorRedTopLeft *= f1;
				this.colorRedBottomLeft *= f1;
				this.colorRedBottomRight *= f1;
				this.colorRedTopRight *= f1;
				this.colorGreenTopLeft *= f2;
				this.colorGreenBottomLeft *= f2;
				this.colorGreenBottomRight *= f2;
				this.colorGreenTopRight *= f2;
				this.colorBlueTopLeft *= f3;
				this.colorBlueBottomLeft *= f3;
				this.colorBlueBottomRight *= f3;
				this.colorBlueTopRight *= f3;
				this.renderWestFace(un1, (double)i1, (double)j1, (double)k1, 38);
			}

			flag = true;
		}

		if(this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1 - 1, j1, k1, 4)) {
			if(this.field_22352_G <= 0) {
				f31 = this.aoLightValueXNeg;
				f24 = this.aoLightValueXNeg;
				f17 = this.aoLightValueXNeg;
				f10 = this.aoLightValueXNeg;
			} else {
				--i1;
				this.field_22376_n = un1.getBlockBrightness(this.blockAccess, i1, j1 - 1, k1);
				this.field_22358_C = un1.getBlockBrightness(this.blockAccess, i1, j1, k1 - 1);
				this.field_22354_E = un1.getBlockBrightness(this.blockAccess, i1, j1, k1 + 1);
				this.field_22368_v = un1.getBlockBrightness(this.blockAccess, i1, j1 + 1, k1);
				if(!this.field_22335_X && !this.field_22357_ad) {
					this.field_22377_m = this.field_22358_C;
				} else {
					this.field_22377_m = un1.getBlockBrightness(this.blockAccess, i1, j1 - 1, k1 - 1);
				}

				if(!this.field_22333_Z && !this.field_22357_ad) {
					this.field_22375_o = this.field_22354_E;
				} else {
					this.field_22375_o = un1.getBlockBrightness(this.blockAccess, i1, j1 - 1, k1 + 1);
				}

				if(!this.field_22335_X && !this.field_22337_V) {
					this.field_22369_u = this.field_22358_C;
				} else {
					this.field_22369_u = un1.getBlockBrightness(this.blockAccess, i1, j1 + 1, k1 - 1);
				}

				if(!this.field_22333_Z && !this.field_22337_V) {
					this.field_22367_w = this.field_22354_E;
				} else {
					this.field_22367_w = un1.getBlockBrightness(this.blockAccess, i1, j1 + 1, k1 + 1);
				}

				++i1;
				f31 = (this.field_22376_n + this.field_22375_o + this.aoLightValueXNeg + this.field_22354_E) / 4.0F;
				f10 = (this.aoLightValueXNeg + this.field_22354_E + this.field_22368_v + this.field_22367_w) / 4.0F;
				f17 = (this.field_22358_C + this.aoLightValueXNeg + this.field_22369_u + this.field_22368_v) / 4.0F;
				f24 = (this.field_22377_m + this.field_22376_n + this.field_22358_C + this.aoLightValueXNeg) / 4.0F;
			}

			this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = (flag5 ? f1 : 1.0F) * 0.6F;
			this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = (flag5 ? f2 : 1.0F) * 0.6F;
			this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = (flag5 ? f3 : 1.0F) * 0.6F;
			this.colorRedTopLeft *= f10;
			this.colorGreenTopLeft *= f10;
			this.colorBlueTopLeft *= f10;
			this.colorRedBottomLeft *= f17;
			this.colorGreenBottomLeft *= f17;
			this.colorBlueBottomLeft *= f17;
			this.colorRedBottomRight *= f24;
			this.colorGreenBottomRight *= f24;
			this.colorBlueBottomRight *= f24;
			this.colorRedTopRight *= f31;
			this.colorGreenTopRight *= f31;
			this.colorBlueTopRight *= f31;
			k2 = un1.getBlockTexture(this.blockAccess, i1, j1, k1, 4);
			this.renderNorthFace(un1, (double)i1, (double)j1, (double)k1, k2);
			if(cfgGrassFix && k2 == 3 && this.overrideBlockTexture < 0) {
				this.colorRedTopLeft *= f1;
				this.colorRedBottomLeft *= f1;
				this.colorRedBottomRight *= f1;
				this.colorRedTopRight *= f1;
				this.colorGreenTopLeft *= f2;
				this.colorGreenBottomLeft *= f2;
				this.colorGreenBottomRight *= f2;
				this.colorGreenTopRight *= f2;
				this.colorBlueTopLeft *= f3;
				this.colorBlueBottomLeft *= f3;
				this.colorBlueBottomRight *= f3;
				this.colorBlueTopRight *= f3;
				this.renderNorthFace(un1, (double)i1, (double)j1, (double)k1, 38);
			}

			flag = true;
		}

		if(this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1 + 1, j1, k1, 5)) {
			if(this.field_22352_G <= 0) {
				f31 = this.aoLightValueXPos;
				f24 = this.aoLightValueXPos;
				f17 = this.aoLightValueXPos;
				f10 = this.aoLightValueXPos;
			} else {
				++i1;
				this.field_22371_s = un1.getBlockBrightness(this.blockAccess, i1, j1 - 1, k1);
				this.field_22356_D = un1.getBlockBrightness(this.blockAccess, i1, j1, k1 - 1);
				this.field_22353_F = un1.getBlockBrightness(this.blockAccess, i1, j1, k1 + 1);
				this.field_22364_z = un1.getBlockBrightness(this.blockAccess, i1, j1 + 1, k1);
				if(!this.field_22359_ac && !this.field_22363_aa) {
					this.field_22372_r = this.field_22356_D;
				} else {
					this.field_22372_r = un1.getBlockBrightness(this.blockAccess, i1, j1 - 1, k1 - 1);
				}

				if(!this.field_22359_ac && !this.field_22334_Y) {
					this.field_22370_t = this.field_22353_F;
				} else {
					this.field_22370_t = un1.getBlockBrightness(this.blockAccess, i1, j1 - 1, k1 + 1);
				}

				if(!this.field_22338_U && !this.field_22363_aa) {
					this.field_22365_y = this.field_22356_D;
				} else {
					this.field_22365_y = un1.getBlockBrightness(this.blockAccess, i1, j1 + 1, k1 - 1);
				}

				if(!this.field_22338_U && !this.field_22334_Y) {
					this.field_22360_B = this.field_22353_F;
				} else {
					this.field_22360_B = un1.getBlockBrightness(this.blockAccess, i1, j1 + 1, k1 + 1);
				}

				--i1;
				f10 = (this.field_22371_s + this.field_22370_t + this.aoLightValueXPos + this.field_22353_F) / 4.0F;
				f31 = (this.aoLightValueXPos + this.field_22353_F + this.field_22364_z + this.field_22360_B) / 4.0F;
				f24 = (this.field_22356_D + this.aoLightValueXPos + this.field_22365_y + this.field_22364_z) / 4.0F;
				f17 = (this.field_22372_r + this.field_22371_s + this.field_22356_D + this.aoLightValueXPos) / 4.0F;
			}

			this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = (flag6 ? f1 : 1.0F) * 0.6F;
			this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = (flag6 ? f2 : 1.0F) * 0.6F;
			this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = (flag6 ? f3 : 1.0F) * 0.6F;
			this.colorRedTopLeft *= f10;
			this.colorGreenTopLeft *= f10;
			this.colorBlueTopLeft *= f10;
			this.colorRedBottomLeft *= f17;
			this.colorGreenBottomLeft *= f17;
			this.colorBlueBottomLeft *= f17;
			this.colorRedBottomRight *= f24;
			this.colorGreenBottomRight *= f24;
			this.colorBlueBottomRight *= f24;
			this.colorRedTopRight *= f31;
			this.colorGreenTopRight *= f31;
			this.colorBlueTopRight *= f31;
			k2 = un1.getBlockTexture(this.blockAccess, i1, j1, k1, 5);
			this.renderSouthFace(un1, (double)i1, (double)j1, (double)k1, k2);
			if(cfgGrassFix && k2 == 3 && this.overrideBlockTexture < 0) {
				this.colorRedTopLeft *= f1;
				this.colorRedBottomLeft *= f1;
				this.colorRedBottomRight *= f1;
				this.colorRedTopRight *= f1;
				this.colorGreenTopLeft *= f2;
				this.colorGreenBottomLeft *= f2;
				this.colorGreenBottomRight *= f2;
				this.colorGreenTopRight *= f2;
				this.colorBlueTopLeft *= f3;
				this.colorBlueBottomLeft *= f3;
				this.colorBlueBottomRight *= f3;
				this.colorBlueTopRight *= f3;
				this.renderSouthFace(un1, (double)i1, (double)j1, (double)k1, 38);
			}

			flag = true;
		}

		this.enableAO = false;
		return flag;
	}

	public boolean renderStandardBlockWithColorMultiplier(Block un1, int i1, int j1, int k1, float f1, float f2, float f3) {
		this.enableAO = false;
		Tessellator ns1 = Tessellator.instance;
		boolean flag = false;
		float f4 = 0.5F;
		float f5 = 1.0F;
		float f6 = 0.8F;
		float f7 = 0.6F;
		float f8 = f5 * f1;
		float f9 = f5 * f2;
		float f10 = f5 * f3;
		float f11 = f4;
		float f12 = f6;
		float f13 = f7;
		float f14 = f4;
		float f15 = f6;
		float f16 = f7;
		float f17 = f4;
		float f18 = f6;
		float f19 = f7;
		if(un1 != Block.grass) {
			f11 = f4 * f1;
			f12 = f6 * f1;
			f13 = f7 * f1;
			f14 = f4 * f2;
			f15 = f6 * f2;
			f16 = f7 * f2;
			f17 = f4 * f3;
			f18 = f6 * f3;
			f19 = f7 * f3;
		}

		float f20 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1);
		float f26;
		if(this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1, j1 - 1, k1, 0)) {
			f26 = un1.getBlockBrightness(this.blockAccess, i1, j1 - 1, k1);
			ns1.setColorOpaque_F(f11 * f26, f14 * f26, f17 * f26);
			this.renderBottomFace(un1, (double)i1, (double)j1, (double)k1, un1.getBlockTexture(this.blockAccess, i1, j1, k1, 0));
			flag = true;
		}

		if(this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1, j1 + 1, k1, 1)) {
			f26 = un1.getBlockBrightness(this.blockAccess, i1, j1 + 1, k1);
			if(un1.maxY != 1.0D && !un1.blockMaterial.getIsLiquid()) {
				f26 = f20;
			}

			ns1.setColorOpaque_F(f8 * f26, f9 * f26, f10 * f26);
			this.renderTopFace(un1, (double)i1, (double)j1, (double)k1, un1.getBlockTexture(this.blockAccess, i1, j1, k1, 1));
			flag = true;
		}

		int k2;
		if(this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1, j1, k1 - 1, 2)) {
			f26 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1 - 1);
			if(un1.minZ > 0.0D) {
				f26 = f20;
			}

			ns1.setColorOpaque_F(f12 * f26, f15 * f26, f18 * f26);
			k2 = un1.getBlockTexture(this.blockAccess, i1, j1, k1, 2);
			this.renderEastFace(un1, (double)i1, (double)j1, (double)k1, k2);
			if(cfgGrassFix && k2 == 3 && this.overrideBlockTexture < 0) {
				ns1.setColorOpaque_F(f12 * f26 * f1, f15 * f26 * f2, f18 * f26 * f3);
				this.renderEastFace(un1, (double)i1, (double)j1, (double)k1, 38);
			}

			flag = true;
		}

		if(this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1, j1, k1 + 1, 3)) {
			f26 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1 + 1);
			if(un1.maxZ < 1.0D) {
				f26 = f20;
			}

			ns1.setColorOpaque_F(f12 * f26, f15 * f26, f18 * f26);
			k2 = un1.getBlockTexture(this.blockAccess, i1, j1, k1, 3);
			this.renderWestFace(un1, (double)i1, (double)j1, (double)k1, k2);
			if(cfgGrassFix && k2 == 3 && this.overrideBlockTexture < 0) {
				ns1.setColorOpaque_F(f12 * f26 * f1, f15 * f26 * f2, f18 * f26 * f3);
				this.renderWestFace(un1, (double)i1, (double)j1, (double)k1, 38);
			}

			flag = true;
		}

		if(this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1 - 1, j1, k1, 4)) {
			f26 = un1.getBlockBrightness(this.blockAccess, i1 - 1, j1, k1);
			if(un1.minX > 0.0D) {
				f26 = f20;
			}

			ns1.setColorOpaque_F(f13 * f26, f16 * f26, f19 * f26);
			k2 = un1.getBlockTexture(this.blockAccess, i1, j1, k1, 4);
			this.renderNorthFace(un1, (double)i1, (double)j1, (double)k1, k2);
			if(cfgGrassFix && k2 == 3 && this.overrideBlockTexture < 0) {
				ns1.setColorOpaque_F(f13 * f26 * f1, f16 * f26 * f2, f19 * f26 * f3);
				this.renderNorthFace(un1, (double)i1, (double)j1, (double)k1, 38);
			}

			flag = true;
		}

		if(this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1 + 1, j1, k1, 5)) {
			f26 = un1.getBlockBrightness(this.blockAccess, i1 + 1, j1, k1);
			if(un1.maxX < 1.0D) {
				f26 = f20;
			}

			ns1.setColorOpaque_F(f13 * f26, f16 * f26, f19 * f26);
			k2 = un1.getBlockTexture(this.blockAccess, i1, j1, k1, 5);
			this.renderSouthFace(un1, (double)i1, (double)j1, (double)k1, k2);
			if(cfgGrassFix && k2 == 3 && this.overrideBlockTexture < 0) {
				ns1.setColorOpaque_F(f13 * f26 * f1, f16 * f26 * f2, f19 * f26 * f3);
				this.renderSouthFace(un1, (double)i1, (double)j1, (double)k1, 38);
			}

			flag = true;
		}

		return flag;
	}

	public boolean renderBlockCactus(Block un1, int i1, int j1, int k1) {
		int l1 = un1.colorMultiplier(this.blockAccess, i1, j1, k1);
		float f1 = (float)(l1 >> 16 & 255) / 255.0F;
		float f2 = (float)(l1 >> 8 & 255) / 255.0F;
		float f3 = (float)(l1 & 255) / 255.0F;
		if(EntityRenderer.field_28135_a) {
			float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
			float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
			float f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
			f1 = f4;
			f2 = f5;
			f3 = f6;
		}

		return this.func_1230_b(un1, i1, j1, k1, f1, f2, f3);
	}

	public boolean func_1230_b(Block un1, int i1, int j1, int k1, float f1, float f2, float f3) {
		Tessellator ns1 = Tessellator.instance;
		boolean flag = false;
		float f4 = 0.5F;
		float f5 = 1.0F;
		float f6 = 0.8F;
		float f7 = 0.6F;
		float f8 = f4 * f1;
		float f9 = f5 * f1;
		float f10 = f6 * f1;
		float f11 = f7 * f1;
		float f12 = f4 * f2;
		float f13 = f5 * f2;
		float f14 = f6 * f2;
		float f15 = f7 * f2;
		float f16 = f4 * f3;
		float f17 = f5 * f3;
		float f18 = f6 * f3;
		float f19 = f7 * f3;
		float f20 = 0.0625F;
		float f21 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1);
		float f27;
		if(this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1, j1 - 1, k1, 0)) {
			f27 = un1.getBlockBrightness(this.blockAccess, i1, j1 - 1, k1);
			ns1.setColorOpaque_F(f8 * f27, f12 * f27, f16 * f27);
			this.renderBottomFace(un1, (double)i1, (double)j1, (double)k1, un1.getBlockTexture(this.blockAccess, i1, j1, k1, 0));
			flag = true;
		}

		if(this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1, j1 + 1, k1, 1)) {
			f27 = un1.getBlockBrightness(this.blockAccess, i1, j1 + 1, k1);
			if(un1.maxY != 1.0D && !un1.blockMaterial.getIsLiquid()) {
				f27 = f21;
			}

			ns1.setColorOpaque_F(f9 * f27, f13 * f27, f17 * f27);
			this.renderTopFace(un1, (double)i1, (double)j1, (double)k1, un1.getBlockTexture(this.blockAccess, i1, j1, k1, 1));
			flag = true;
		}

		if(this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1, j1, k1 - 1, 2)) {
			f27 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1 - 1);
			if(un1.minZ > 0.0D) {
				f27 = f21;
			}

			ns1.setColorOpaque_F(f10 * f27, f14 * f27, f18 * f27);
			ns1.setTranslationF(0.0F, 0.0F, f20);
			this.renderEastFace(un1, (double)i1, (double)j1, (double)k1, un1.getBlockTexture(this.blockAccess, i1, j1, k1, 2));
			ns1.setTranslationF(0.0F, 0.0F, -f20);
			flag = true;
		}

		if(this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1, j1, k1 + 1, 3)) {
			f27 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1 + 1);
			if(un1.maxZ < 1.0D) {
				f27 = f21;
			}

			ns1.setColorOpaque_F(f10 * f27, f14 * f27, f18 * f27);
			ns1.setTranslationF(0.0F, 0.0F, -f20);
			this.renderWestFace(un1, (double)i1, (double)j1, (double)k1, un1.getBlockTexture(this.blockAccess, i1, j1, k1, 3));
			ns1.setTranslationF(0.0F, 0.0F, f20);
			flag = true;
		}

		if(this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1 - 1, j1, k1, 4)) {
			f27 = un1.getBlockBrightness(this.blockAccess, i1 - 1, j1, k1);
			if(un1.minX > 0.0D) {
				f27 = f21;
			}

			ns1.setColorOpaque_F(f11 * f27, f15 * f27, f19 * f27);
			ns1.setTranslationF(f20, 0.0F, 0.0F);
			this.renderNorthFace(un1, (double)i1, (double)j1, (double)k1, un1.getBlockTexture(this.blockAccess, i1, j1, k1, 4));
			ns1.setTranslationF(-f20, 0.0F, 0.0F);
			flag = true;
		}

		if(this.renderAllFaces || un1.shouldSideBeRendered(this.blockAccess, i1 + 1, j1, k1, 5)) {
			f27 = un1.getBlockBrightness(this.blockAccess, i1 + 1, j1, k1);
			if(un1.maxX < 1.0D) {
				f27 = f21;
			}

			ns1.setColorOpaque_F(f11 * f27, f15 * f27, f19 * f27);
			ns1.setTranslationF(-f20, 0.0F, 0.0F);
			this.renderSouthFace(un1, (double)i1, (double)j1, (double)k1, un1.getBlockTexture(this.blockAccess, i1, j1, k1, 5));
			ns1.setTranslationF(f20, 0.0F, 0.0F);
			flag = true;
		}

		return flag;
	}

	public boolean renderBlockFence(Block un1, int i1, int j1, int k1) {
		boolean flag = false;
		float f1 = 0.375F;
		float f2 = 0.625F;
		un1.setBlockBounds(f1, 0.0F, f1, f2, 1.0F, f2);
		this.renderStandardBlock(un1, i1, j1, k1);
		flag = true;
		boolean flag1 = false;
		boolean flag2 = false;
		if(this.blockAccess.getBlockId(i1 - 1, j1, k1) == un1.blockID || this.blockAccess.getBlockId(i1 + 1, j1, k1) == un1.blockID) {
			flag1 = true;
		}

		if(this.blockAccess.getBlockId(i1, j1, k1 - 1) == un1.blockID || this.blockAccess.getBlockId(i1, j1, k1 + 1) == un1.blockID) {
			flag2 = true;
		}

		boolean flag3 = this.blockAccess.getBlockId(i1 - 1, j1, k1) == un1.blockID;
		boolean flag4 = this.blockAccess.getBlockId(i1 + 1, j1, k1) == un1.blockID;
		boolean flag5 = this.blockAccess.getBlockId(i1, j1, k1 - 1) == un1.blockID;
		boolean flag6 = this.blockAccess.getBlockId(i1, j1, k1 + 1) == un1.blockID;
		if(!flag1 && !flag2) {
			flag1 = true;
		}

		f1 = 0.4375F;
		f2 = 0.5625F;
		float f3 = 0.75F;
		float f4 = 0.9375F;
		float f5 = flag3 ? 0.0F : f1;
		float f6 = flag4 ? 1.0F : f2;
		float f7 = flag5 ? 0.0F : f1;
		float f8 = flag6 ? 1.0F : f2;
		if(flag1) {
			un1.setBlockBounds(f5, f3, f1, f6, f4, f2);
			this.renderStandardBlock(un1, i1, j1, k1);
			flag = true;
		}

		if(flag2) {
			un1.setBlockBounds(f1, f3, f7, f2, f4, f8);
			this.renderStandardBlock(un1, i1, j1, k1);
			flag = true;
		}

		f3 = 0.375F;
		f4 = 0.5625F;
		if(flag1) {
			un1.setBlockBounds(f5, f3, f1, f6, f4, f2);
			this.renderStandardBlock(un1, i1, j1, k1);
			flag = true;
		}

		if(flag2) {
			un1.setBlockBounds(f1, f3, f7, f2, f4, f8);
			this.renderStandardBlock(un1, i1, j1, k1);
			flag = true;
		}

		un1.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		return flag;
	}

	public boolean renderBlockStairs(Block un1, int i1, int j1, int k1) {
		boolean flag = false;
		int l1 = this.blockAccess.getBlockMetadata(i1, j1, k1);
		if(l1 == 0) {
			un1.setBlockBounds(0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 1.0F);
			this.renderStandardBlock(un1, i1, j1, k1);
			un1.setBlockBounds(0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			this.renderStandardBlock(un1, i1, j1, k1);
			flag = true;
		} else if(l1 == 1) {
			un1.setBlockBounds(0.0F, 0.0F, 0.0F, 0.5F, 1.0F, 1.0F);
			this.renderStandardBlock(un1, i1, j1, k1);
			un1.setBlockBounds(0.5F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
			this.renderStandardBlock(un1, i1, j1, k1);
			flag = true;
		} else if(l1 == 2) {
			un1.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 0.5F);
			this.renderStandardBlock(un1, i1, j1, k1);
			un1.setBlockBounds(0.0F, 0.0F, 0.5F, 1.0F, 1.0F, 1.0F);
			this.renderStandardBlock(un1, i1, j1, k1);
			flag = true;
		} else if(l1 == 3) {
			un1.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.5F);
			this.renderStandardBlock(un1, i1, j1, k1);
			un1.setBlockBounds(0.0F, 0.0F, 0.5F, 1.0F, 0.5F, 1.0F);
			this.renderStandardBlock(un1, i1, j1, k1);
			flag = true;
		}

		un1.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		return flag;
	}

	public boolean renderBlockDoor(Block un1, int i1, int j1, int k1) {
		Tessellator ns1 = Tessellator.instance;
		BlockDoor la1 = (BlockDoor)un1;
		boolean flag = false;
		float f1 = 0.5F;
		float f2 = 1.0F;
		float f3 = 0.8F;
		float f4 = 0.6F;
		float f5 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1);
		float f6 = un1.getBlockBrightness(this.blockAccess, i1, j1 - 1, k1);
		if(la1.minY > 0.0D) {
			f6 = f5;
		}

		if(Block.lightValue[un1.blockID] > 0) {
			f6 = 1.0F;
		}

		ns1.setColorOpaque_F(f1 * f6, f1 * f6, f1 * f6);
		this.renderBottomFace(un1, (double)i1, (double)j1, (double)k1, un1.getBlockTexture(this.blockAccess, i1, j1, k1, 0));
		flag = true;
		f6 = un1.getBlockBrightness(this.blockAccess, i1, j1 + 1, k1);
		if(la1.maxY < 1.0D) {
			f6 = f5;
		}

		if(Block.lightValue[un1.blockID] > 0) {
			f6 = 1.0F;
		}

		ns1.setColorOpaque_F(f2 * f6, f2 * f6, f2 * f6);
		this.renderTopFace(un1, (double)i1, (double)j1, (double)k1, un1.getBlockTexture(this.blockAccess, i1, j1, k1, 1));
		flag = true;
		f6 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1 - 1);
		if(la1.minZ > 0.0D) {
			f6 = f5;
		}

		if(Block.lightValue[un1.blockID] > 0) {
			f6 = 1.0F;
		}

		ns1.setColorOpaque_F(f3 * f6, f3 * f6, f3 * f6);
		int l1 = un1.getBlockTexture(this.blockAccess, i1, j1, k1, 2);
		if(l1 < 0) {
			this.flipTexture = true;
			l1 = -l1;
		}

		this.renderEastFace(un1, (double)i1, (double)j1, (double)k1, l1);
		flag = true;
		this.flipTexture = false;
		f6 = un1.getBlockBrightness(this.blockAccess, i1, j1, k1 + 1);
		if(la1.maxZ < 1.0D) {
			f6 = f5;
		}

		if(Block.lightValue[un1.blockID] > 0) {
			f6 = 1.0F;
		}

		ns1.setColorOpaque_F(f3 * f6, f3 * f6, f3 * f6);
		l1 = un1.getBlockTexture(this.blockAccess, i1, j1, k1, 3);
		if(l1 < 0) {
			this.flipTexture = true;
			l1 = -l1;
		}

		this.renderWestFace(un1, (double)i1, (double)j1, (double)k1, l1);
		flag = true;
		this.flipTexture = false;
		f6 = un1.getBlockBrightness(this.blockAccess, i1 - 1, j1, k1);
		if(la1.minX > 0.0D) {
			f6 = f5;
		}

		if(Block.lightValue[un1.blockID] > 0) {
			f6 = 1.0F;
		}

		ns1.setColorOpaque_F(f4 * f6, f4 * f6, f4 * f6);
		l1 = un1.getBlockTexture(this.blockAccess, i1, j1, k1, 4);
		if(l1 < 0) {
			this.flipTexture = true;
			l1 = -l1;
		}

		this.renderNorthFace(un1, (double)i1, (double)j1, (double)k1, l1);
		flag = true;
		this.flipTexture = false;
		f6 = un1.getBlockBrightness(this.blockAccess, i1 + 1, j1, k1);
		if(la1.maxX < 1.0D) {
			f6 = f5;
		}

		if(Block.lightValue[un1.blockID] > 0) {
			f6 = 1.0F;
		}

		ns1.setColorOpaque_F(f4 * f6, f4 * f6, f4 * f6);
		l1 = un1.getBlockTexture(this.blockAccess, i1, j1, k1, 5);
		if(l1 < 0) {
			this.flipTexture = true;
			l1 = -l1;
		}

		this.renderSouthFace(un1, (double)i1, (double)j1, (double)k1, l1);
		flag = true;
		this.flipTexture = false;
		return flag;
	}

	public void renderBottomFace(Block un1, double d1, double d2, double d3, int i1) {
		Tessellator ns1 = Tessellator.instance;
		if(this.overrideBlockTexture >= 0) {
			i1 = this.overrideBlockTexture;
		}

		int j1 = (i1 & 15) << 4;
		int k1 = i1 & 240;
		double d4 = ((double)j1 + un1.minX * 16.0D) / 256.0D;
		double d5 = ((double)j1 + un1.maxX * 16.0D - 0.01D) / 256.0D;
		double d6 = ((double)k1 + un1.minZ * 16.0D) / 256.0D;
		double d7 = ((double)k1 + un1.maxZ * 16.0D - 0.01D) / 256.0D;
		if(un1.minX < 0.0D || un1.maxX > 1.0D) {
			d4 = (double)(((float)j1 + 0.0F) / 256.0F);
			d5 = (double)(((float)j1 + 15.99F) / 256.0F);
		}

		if(un1.minZ < 0.0D || un1.maxZ > 1.0D) {
			d6 = (double)(((float)k1 + 0.0F) / 256.0F);
			d7 = (double)(((float)k1 + 15.99F) / 256.0F);
		}

		double d8 = d1 + un1.minX;
		double d9 = d1 + un1.maxX;
		double d10 = d2 + un1.minY;
		double d11 = d3 + un1.minZ;
		double d12 = d3 + un1.maxZ;
		if(this.enableAO) {
			ns1.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
			ns1.addVertexWithUV(d8, d10, d12, d4, d7);
			ns1.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			ns1.addVertexWithUV(d8, d10, d11, d4, d6);
			ns1.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
			ns1.addVertexWithUV(d9, d10, d11, d5, d6);
			ns1.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
			ns1.addVertexWithUV(d9, d10, d12, d5, d7);
		} else {
			ns1.addVertexWithUV(d8, d10, d12, d4, d7);
			ns1.addVertexWithUV(d8, d10, d11, d4, d6);
			ns1.addVertexWithUV(d9, d10, d11, d5, d6);
			ns1.addVertexWithUV(d9, d10, d12, d5, d7);
		}

	}

	public void renderTopFace(Block un1, double d1, double d2, double d3, int i1) {
		Tessellator ns1 = Tessellator.instance;
		if(this.overrideBlockTexture >= 0) {
			i1 = this.overrideBlockTexture;
		}

		int j1 = (i1 & 15) << 4;
		int k1 = i1 & 240;
		double d4 = ((double)j1 + un1.minX * 16.0D) / 256.0D;
		double d5 = ((double)j1 + un1.maxX * 16.0D - 0.01D) / 256.0D;
		double d6 = ((double)k1 + un1.minZ * 16.0D) / 256.0D;
		double d7 = ((double)k1 + un1.maxZ * 16.0D - 0.01D) / 256.0D;
		if(un1.minX < 0.0D || un1.maxX > 1.0D) {
			d4 = (double)(((float)j1 + 0.0F) / 256.0F);
			d5 = (double)(((float)j1 + 15.99F) / 256.0F);
		}

		if(un1.minZ < 0.0D || un1.maxZ > 1.0D) {
			d6 = (double)(((float)k1 + 0.0F) / 256.0F);
			d7 = (double)(((float)k1 + 15.99F) / 256.0F);
		}

		double d8 = d1 + un1.minX;
		double d9 = d1 + un1.maxX;
		double d10 = d2 + un1.maxY;
		double d11 = d3 + un1.minZ;
		double d12 = d3 + un1.maxZ;
		if(this.enableAO) {
			ns1.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
			ns1.addVertexWithUV(d9, d10, d12, d5, d7);
			ns1.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			ns1.addVertexWithUV(d9, d10, d11, d5, d6);
			ns1.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
			ns1.addVertexWithUV(d8, d10, d11, d4, d6);
			ns1.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
			ns1.addVertexWithUV(d8, d10, d12, d4, d7);
		} else {
			ns1.addVertexWithUV(d9, d10, d12, d5, d7);
			ns1.addVertexWithUV(d9, d10, d11, d5, d6);
			ns1.addVertexWithUV(d8, d10, d11, d4, d6);
			ns1.addVertexWithUV(d8, d10, d12, d4, d7);
		}

	}

	public void renderEastFace(Block un1, double d1, double d2, double d3, int i1) {
		Tessellator ns1 = Tessellator.instance;
		if(this.overrideBlockTexture >= 0) {
			i1 = this.overrideBlockTexture;
		}

		int j1 = (i1 & 15) << 4;
		int k1 = i1 & 240;
		double d4 = ((double)j1 + un1.minX * 16.0D) / 256.0D;
		double d5 = ((double)j1 + un1.maxX * 16.0D - 0.01D) / 256.0D;
		double d6 = ((double)k1 + un1.minY * 16.0D) / 256.0D;
		double d7 = ((double)k1 + un1.maxY * 16.0D - 0.01D) / 256.0D;
		double d9;
		if(this.flipTexture) {
			d9 = d4;
			d4 = d5;
			d5 = d9;
		}

		if(un1.minX < 0.0D || un1.maxX > 1.0D) {
			d4 = (double)(((float)j1 + 0.0F) / 256.0F);
			d5 = (double)(((float)j1 + 15.99F) / 256.0F);
		}

		if(un1.minY < 0.0D || un1.maxY > 1.0D) {
			d6 = (double)(((float)k1 + 0.0F) / 256.0F);
			d7 = (double)(((float)k1 + 15.99F) / 256.0F);
		}

		d9 = d1 + un1.minX;
		double d10 = d1 + un1.maxX;
		double d11 = d2 + un1.minY;
		double d12 = d2 + un1.maxY;
		double d13 = d3 + un1.minZ;
		if(this.enableAO) {
			ns1.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
			ns1.addVertexWithUV(d9, d12, d13, d5, d6);
			ns1.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			ns1.addVertexWithUV(d10, d12, d13, d4, d6);
			ns1.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
			ns1.addVertexWithUV(d10, d11, d13, d4, d7);
			ns1.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
			ns1.addVertexWithUV(d9, d11, d13, d5, d7);
		} else {
			ns1.addVertexWithUV(d9, d12, d13, d5, d6);
			ns1.addVertexWithUV(d10, d12, d13, d4, d6);
			ns1.addVertexWithUV(d10, d11, d13, d4, d7);
			ns1.addVertexWithUV(d9, d11, d13, d5, d7);
		}

	}

	public void renderWestFace(Block un1, double d1, double d2, double d3, int i1) {
		Tessellator ns1 = Tessellator.instance;
		if(this.overrideBlockTexture >= 0) {
			i1 = this.overrideBlockTexture;
		}

		int j1 = (i1 & 15) << 4;
		int k1 = i1 & 240;
		double d4 = ((double)j1 + un1.minX * 16.0D) / 256.0D;
		double d5 = ((double)j1 + un1.maxX * 16.0D - 0.01D) / 256.0D;
		double d6 = ((double)k1 + un1.minY * 16.0D) / 256.0D;
		double d7 = ((double)k1 + un1.maxY * 16.0D - 0.01D) / 256.0D;
		double d9;
		if(this.flipTexture) {
			d9 = d4;
			d4 = d5;
			d5 = d9;
		}

		if(un1.minX < 0.0D || un1.maxX > 1.0D) {
			d4 = (double)(((float)j1 + 0.0F) / 256.0F);
			d5 = (double)(((float)j1 + 15.99F) / 256.0F);
		}

		if(un1.minY < 0.0D || un1.maxY > 1.0D) {
			d6 = (double)(((float)k1 + 0.0F) / 256.0F);
			d7 = (double)(((float)k1 + 15.99F) / 256.0F);
		}

		d9 = d1 + un1.minX;
		double d10 = d1 + un1.maxX;
		double d11 = d2 + un1.minY;
		double d12 = d2 + un1.maxY;
		double d13 = d3 + un1.maxZ;
		if(this.enableAO) {
			ns1.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
			ns1.addVertexWithUV(d9, d12, d13, d4, d6);
			ns1.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			ns1.addVertexWithUV(d9, d11, d13, d4, d7);
			ns1.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
			ns1.addVertexWithUV(d10, d11, d13, d5, d7);
			ns1.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
			ns1.addVertexWithUV(d10, d12, d13, d5, d6);
		} else {
			ns1.addVertexWithUV(d9, d12, d13, d4, d6);
			ns1.addVertexWithUV(d9, d11, d13, d4, d7);
			ns1.addVertexWithUV(d10, d11, d13, d5, d7);
			ns1.addVertexWithUV(d10, d12, d13, d5, d6);
		}

	}

	public void renderNorthFace(Block un1, double d1, double d2, double d3, int i1) {
		Tessellator ns1 = Tessellator.instance;
		if(this.overrideBlockTexture >= 0) {
			i1 = this.overrideBlockTexture;
		}

		int j1 = (i1 & 15) << 4;
		int k1 = i1 & 240;
		double d4 = ((double)j1 + un1.minZ * 16.0D) / 256.0D;
		double d5 = ((double)j1 + un1.maxZ * 16.0D - 0.01D) / 256.0D;
		double d6 = ((double)k1 + un1.minY * 16.0D) / 256.0D;
		double d7 = ((double)k1 + un1.maxY * 16.0D - 0.01D) / 256.0D;
		double d9;
		if(this.flipTexture) {
			d9 = d4;
			d4 = d5;
			d5 = d9;
		}

		if(un1.minZ < 0.0D || un1.maxZ > 1.0D) {
			d4 = (double)(((float)j1 + 0.0F) / 256.0F);
			d5 = (double)(((float)j1 + 15.99F) / 256.0F);
		}

		if(un1.minY < 0.0D || un1.maxY > 1.0D) {
			d6 = (double)(((float)k1 + 0.0F) / 256.0F);
			d7 = (double)(((float)k1 + 15.99F) / 256.0F);
		}

		d9 = d1 + un1.minX;
		double d10 = d2 + un1.minY;
		double d11 = d2 + un1.maxY;
		double d12 = d3 + un1.minZ;
		double d13 = d3 + un1.maxZ;
		if(this.enableAO) {
			ns1.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
			ns1.addVertexWithUV(d9, d11, d13, d5, d6);
			ns1.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			ns1.addVertexWithUV(d9, d11, d12, d4, d6);
			ns1.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
			ns1.addVertexWithUV(d9, d10, d12, d4, d7);
			ns1.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
			ns1.addVertexWithUV(d9, d10, d13, d5, d7);
		} else {
			ns1.addVertexWithUV(d9, d11, d13, d5, d6);
			ns1.addVertexWithUV(d9, d11, d12, d4, d6);
			ns1.addVertexWithUV(d9, d10, d12, d4, d7);
			ns1.addVertexWithUV(d9, d10, d13, d5, d7);
		}

	}

	public void renderSouthFace(Block un1, double d1, double d2, double d3, int i1) {
		Tessellator ns1 = Tessellator.instance;
		if(this.overrideBlockTexture >= 0) {
			i1 = this.overrideBlockTexture;
		}

		int j1 = (i1 & 15) << 4;
		int k1 = i1 & 240;
		double d4 = ((double)j1 + un1.minZ * 16.0D) / 256.0D;
		double d5 = ((double)j1 + un1.maxZ * 16.0D - 0.01D) / 256.0D;
		double d6 = ((double)k1 + un1.minY * 16.0D) / 256.0D;
		double d7 = ((double)k1 + un1.maxY * 16.0D - 0.01D) / 256.0D;
		double d9;
		if(this.flipTexture) {
			d9 = d4;
			d4 = d5;
			d5 = d9;
		}

		if(un1.minZ < 0.0D || un1.maxZ > 1.0D) {
			d4 = (double)(((float)j1 + 0.0F) / 256.0F);
			d5 = (double)(((float)j1 + 15.99F) / 256.0F);
		}

		if(un1.minY < 0.0D || un1.maxY > 1.0D) {
			d6 = (double)(((float)k1 + 0.0F) / 256.0F);
			d7 = (double)(((float)k1 + 15.99F) / 256.0F);
		}

		d9 = d1 + un1.maxX;
		double d10 = d2 + un1.minY;
		double d11 = d2 + un1.maxY;
		double d12 = d3 + un1.minZ;
		double d13 = d3 + un1.maxZ;
		if(this.enableAO) {
			ns1.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
			ns1.addVertexWithUV(d9, d10, d13, d4, d7);
			ns1.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			ns1.addVertexWithUV(d9, d10, d12, d5, d7);
			ns1.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
			ns1.addVertexWithUV(d9, d11, d12, d5, d6);
			ns1.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
			ns1.addVertexWithUV(d9, d11, d13, d4, d6);
		} else {
			ns1.addVertexWithUV(d9, d10, d13, d4, d7);
			ns1.addVertexWithUV(d9, d10, d12, d5, d7);
			ns1.addVertexWithUV(d9, d11, d12, d5, d6);
			ns1.addVertexWithUV(d9, d11, d13, d4, d6);
		}

	}

	public void renderBlockOnInventory(Block un1, int i1) {
		Tessellator ns1 = Tessellator.instance;
		int j1 = un1.getRenderType();
		if(j1 == 0) {
			un1.setBlockBoundsForItemRender();
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			ns1.startDrawingQuads();
			ns1.setNormal(0.0F, -1.0F, 0.0F);
			this.renderBottomFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSideAndMetadata(0, i1));
			ns1.draw();
			ns1.startDrawingQuads();
			ns1.setNormal(0.0F, 1.0F, 0.0F);
			this.renderTopFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSideAndMetadata(1, i1));
			ns1.draw();
			ns1.startDrawingQuads();
			ns1.setNormal(0.0F, 0.0F, -1.0F);
			this.renderEastFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSideAndMetadata(2, i1));
			ns1.draw();
			ns1.startDrawingQuads();
			ns1.setNormal(0.0F, 0.0F, 1.0F);
			this.renderWestFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSideAndMetadata(3, i1));
			ns1.draw();
			ns1.startDrawingQuads();
			ns1.setNormal(-1.0F, 0.0F, 0.0F);
			this.renderNorthFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSideAndMetadata(4, i1));
			ns1.draw();
			ns1.startDrawingQuads();
			ns1.setNormal(1.0F, 0.0F, 0.0F);
			this.renderSouthFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSideAndMetadata(5, i1));
			ns1.draw();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		} else if(j1 == 1) {
			ns1.startDrawingQuads();
			ns1.setNormal(0.0F, -1.0F, 0.0F);
			this.renderCrossedSquares(un1, i1, -0.5D, -0.5D, -0.5D);
			ns1.draw();
		} else if(j1 == 13) {
			un1.setBlockBoundsForItemRender();
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			float l1 = 0.0625F;
			ns1.startDrawingQuads();
			ns1.setNormal(0.0F, -1.0F, 0.0F);
			this.renderBottomFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSide(0));
			ns1.draw();
			ns1.startDrawingQuads();
			ns1.setNormal(0.0F, 1.0F, 0.0F);
			this.renderTopFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSide(1));
			ns1.draw();
			ns1.startDrawingQuads();
			ns1.setNormal(0.0F, 0.0F, -1.0F);
			ns1.setTranslationF(0.0F, 0.0F, l1);
			this.renderEastFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSide(2));
			ns1.setTranslationF(0.0F, 0.0F, -l1);
			ns1.draw();
			ns1.startDrawingQuads();
			ns1.setNormal(0.0F, 0.0F, 1.0F);
			ns1.setTranslationF(0.0F, 0.0F, -l1);
			this.renderWestFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSide(3));
			ns1.setTranslationF(0.0F, 0.0F, l1);
			ns1.draw();
			ns1.startDrawingQuads();
			ns1.setNormal(-1.0F, 0.0F, 0.0F);
			ns1.setTranslationF(l1, 0.0F, 0.0F);
			this.renderNorthFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSide(4));
			ns1.setTranslationF(-l1, 0.0F, 0.0F);
			ns1.draw();
			ns1.startDrawingQuads();
			ns1.setNormal(1.0F, 0.0F, 0.0F);
			ns1.setTranslationF(-l1, 0.0F, 0.0F);
			this.renderSouthFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSide(5));
			ns1.setTranslationF(l1, 0.0F, 0.0F);
			ns1.draw();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		} else if(j1 == 6) {
			ns1.startDrawingQuads();
			ns1.setNormal(0.0F, -1.0F, 0.0F);
			this.func_1245_b(un1, i1, -0.5D, -0.5D, -0.5D);
			ns1.draw();
		} else if(j1 == 2) {
			ns1.startDrawingQuads();
			ns1.setNormal(0.0F, -1.0F, 0.0F);
			this.renderTorchAtAngle(un1, -0.5D, -0.5D, -0.5D, 0.0D, 0.0D);
			ns1.draw();
		} else {
			int i7;
			if(j1 == 10) {
				for(i7 = 0; i7 < 2; ++i7) {
					if(i7 == 0) {
						un1.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.5F);
					}

					if(i7 == 1) {
						un1.setBlockBounds(0.0F, 0.0F, 0.5F, 1.0F, 0.5F, 1.0F);
					}

					GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
					ns1.startDrawingQuads();
					ns1.setNormal(0.0F, -1.0F, 0.0F);
					this.renderBottomFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSide(0));
					ns1.draw();
					ns1.startDrawingQuads();
					ns1.setNormal(0.0F, 1.0F, 0.0F);
					this.renderTopFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSide(1));
					ns1.draw();
					ns1.startDrawingQuads();
					ns1.setNormal(0.0F, 0.0F, -1.0F);
					this.renderEastFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSide(2));
					ns1.draw();
					ns1.startDrawingQuads();
					ns1.setNormal(0.0F, 0.0F, 1.0F);
					this.renderWestFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSide(3));
					ns1.draw();
					ns1.startDrawingQuads();
					ns1.setNormal(-1.0F, 0.0F, 0.0F);
					this.renderNorthFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSide(4));
					ns1.draw();
					ns1.startDrawingQuads();
					ns1.setNormal(1.0F, 0.0F, 0.0F);
					this.renderSouthFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSide(5));
					ns1.draw();
					GL11.glTranslatef(0.5F, 0.5F, 0.5F);
				}
			} else if(j1 == 11) {
				for(i7 = 0; i7 < 4; ++i7) {
					float f2 = 0.125F;
					if(i7 == 0) {
						un1.setBlockBounds(0.5F - f2, 0.0F, 0.0F, 0.5F + f2, 1.0F, f2 * 2.0F);
					}

					if(i7 == 1) {
						un1.setBlockBounds(0.5F - f2, 0.0F, 1.0F - f2 * 2.0F, 0.5F + f2, 1.0F, 1.0F);
					}

					f2 = 0.0625F;
					if(i7 == 2) {
						un1.setBlockBounds(0.5F - f2, 1.0F - f2 * 3.0F, -f2 * 2.0F, 0.5F + f2, 1.0F - f2, 1.0F + f2 * 2.0F);
					}

					if(i7 == 3) {
						un1.setBlockBounds(0.5F - f2, 0.5F - f2 * 3.0F, -f2 * 2.0F, 0.5F + f2, 0.5F - f2, 1.0F + f2 * 2.0F);
					}

					GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
					ns1.startDrawingQuads();
					ns1.setNormal(0.0F, -1.0F, 0.0F);
					this.renderBottomFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSide(0));
					ns1.draw();
					ns1.startDrawingQuads();
					ns1.setNormal(0.0F, 1.0F, 0.0F);
					this.renderTopFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSide(1));
					ns1.draw();
					ns1.startDrawingQuads();
					ns1.setNormal(0.0F, 0.0F, -1.0F);
					this.renderEastFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSide(2));
					ns1.draw();
					ns1.startDrawingQuads();
					ns1.setNormal(0.0F, 0.0F, 1.0F);
					this.renderWestFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSide(3));
					ns1.draw();
					ns1.startDrawingQuads();
					ns1.setNormal(-1.0F, 0.0F, 0.0F);
					this.renderNorthFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSide(4));
					ns1.draw();
					ns1.startDrawingQuads();
					ns1.setNormal(1.0F, 0.0F, 0.0F);
					this.renderSouthFace(un1, 0.0D, 0.0D, 0.0D, un1.getBlockTextureFromSide(5));
					ns1.draw();
					GL11.glTranslatef(0.5F, 0.5F, 0.5F);
				}

				un1.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			} else {
				ModLoader.RenderInvBlock(this, un1, i1, j1);
			}
		}

	}

	public static boolean renderItemIn3d(int i1) {
		return i1 == 0 ? true : (i1 == 13 ? true : (i1 == 10 ? true : ModLoader.RenderBlockIsItemFull3D(i1)));
	}
}
