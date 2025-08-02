package net.minecraft.client.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mojontwins.utils.Idx2uvF;
import com.mojontwins.utils.Texels;
import com.mojontwins.utils.TextureAtlasSize;

import net.minecraft.client.Minecraft;
import net.minecraft.src.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockFence;
import net.minecraft.world.level.tile.BlockFlower;
import net.minecraft.world.level.tile.BlockFluid;
import net.minecraft.world.level.tile.BlockPane;
import net.minecraft.world.level.tile.BlockRail;
import net.minecraft.world.level.tile.BlockRedstoneRepeater;
import net.minecraft.world.level.tile.BlockRedstoneWire;
import net.minecraft.world.phys.Vec3D;

public class RenderBlocks {
	public IBlockAccess blockAccess;
	private int overrideBlockTexture = -1;
	private boolean flipTexture = false;
	private boolean renderAllFaces = false;
	public static boolean fancyGrass = true;
	public boolean useInventoryTint = true;
	private int uvRotateEast = 0;
	private int uvRotateWest = 0;
	private int uvRotateSouth = 0;
	private int uvRotateNorth = 0;
	private int uvRotateTop = 0;
	private int uvRotateBottom = 0;
	private boolean enableAO;
	private float lightValueOwn;
	private float aoLightValueXNeg;
	private float aoLightValueYNeg;
	private float aoLightValueZNeg;
	private float aoLightValueXPos;
	private float aoLightValueYPos;
	private float aoLightValueZPos;
	private float aoLightValueScratchXYZNNN;
	private float aoLightValueScratchXYNN;
	private float aoLightValueScratchXYZNNP;
	private float aoLightValueScratchYZNN;
	private float aoLightValueScratchYZNP;
	private float aoLightValueScratchXYZPNN;
	private float aoLightValueScratchXYPN;
	private float aoLightValueScratchXYZPNP;
	private float aoLightValueScratchXYZNPN;
	private float aoLightValueScratchXYNP;
	private float aoLightValueScratchXYZNPP;
	private float aoLightValueScratchYZPN;
	private float aoLightValueScratchXYZPPN;
	private float aoLightValueScratchXYPP;
	private float aoLightValueScratchYZPP;
	private float aoLightValueScratchXYZPPP;
	private float aoLightValueScratchXZNN;
	private float aoLightValueScratchXZPN;
	private float aoLightValueScratchXZNP;
	private float aoLightValueScratchXZPP;
	private int aoBrightnessXYZNNN;
	private int aoBrightnessXYNN;
	private int aoBrightnessXYZNNP;
	private int aoBrightnessYZNN;
	private int aoBrightnessYZNP;
	private int aoBrightnessXYZPNN;
	private int aoBrightnessXYPN;
	private int aoBrightnessXYZPNP;
	private int aoBrightnessXYZNPN;
	private int aoBrightnessXYNP;
	private int aoBrightnessXYZNPP;
	private int aoBrightnessYZPN;
	private int aoBrightnessXYZPPN;
	private int aoBrightnessXYPP;
	private int aoBrightnessYZPP;
	private int aoBrightnessXYZPPP;
	private int aoBrightnessXZNN;
	private int aoBrightnessXZPN;
	private int aoBrightnessXZNP;
	private int aoBrightnessXZPP;
	private int aoType = 1;
	private int brightnessTopLeft;
	private int brightnessBottomLeft;
	private int brightnessBottomRight;
	private int brightnessTopRight;
	private float colorRedTopLeft;
	private float colorRedBottomLeft;
	private float colorRedBottomRight;
	private float colorRedTopRight;
	private float colorGreenTopLeft;
	private float colorGreenBottomLeft;
	private float colorGreenBottomRight;
	private float colorGreenTopRight;
	private float colorBlueTopLeft;
	private float colorBlueBottomLeft;
	private float colorBlueBottomRight;
	private float colorBlueTopRight;
	private boolean aoGrassXYZCPN;
	private boolean aoGrassXYZPPC;
	private boolean aoGrassXYZNPC;
	private boolean aoGrassXYZCPP;
	private boolean aoGrassXYZNCN;
	private boolean aoGrassXYZPCP;
	private boolean aoGrassXYZNCP;
	private boolean aoGrassXYZPCN;
	private boolean aoGrassXYZCNN;
	private boolean aoGrassXYZPNC;
	private boolean aoGrassXYZNNC;
	private boolean aoGrassXYZCNP;

	public RenderBlocks(IBlockAccess iBlockAccess1) {
		this.blockAccess = iBlockAccess1;
	}

	public RenderBlocks() {
	}

	public void clearOverrideBlockTexture() {
		this.overrideBlockTexture = -1;
	}

	public void renderBlockUsingTexture(Block block1, int i2, int i3, int i4, int i5) {
		this.overrideBlockTexture = i5;
		this.renderBlockByRenderType(block1, i2, i3, i4);
		this.overrideBlockTexture = -1;
	}

	public void renderBlockAllFaces(Block block1, int i2, int i3, int i4) {
		this.renderAllFaces = true;
		this.renderBlockByRenderType(block1, i2, i3, i4);
		this.renderAllFaces = false;
	}

	public boolean renderBlockByRenderType(Block block, int x, int y, int z) {
		int i5 = block.getRenderType();
		block.setBlockBoundsBasedOnState(this.blockAccess, x, y, z);
		
		switch (i5) {
			case 0: return this.renderStandardBlock(block, x, y, z);
			case 1: return this.renderCrossedSquares(block, x, y, z);
			case 2: return this.renderBlockTorch(block, x, y, z);
			case 3: return this.renderBlockFire(block, x, y, z);
			case 4: return this.renderBlockFluids(block, x, y, z);
			case 5: return this.renderBlockRedstoneWire((BlockRedstoneWire) block, x, y, z);
			case 6: return this.renderBlockCrops(block, x, y, z);
			case 7: return this.renderBlockDoor(block, x, y, z);
			case 8: return this.renderBlockLadder(block, x, y, z);
			case 9: return this.renderBlockMinecartTrack((BlockRail)block, x, y, z);
			case 10: return this.renderBlockStairs(block, x, y, z);
			case 11: return this.renderBlockFence((BlockFence)block, x, y, z);
			case 12: return this.renderBlockLever(block, x, y, z);
			case 13: return this.renderBlockCactus(block, x, y, z); 
			case 15: return this.renderBlockRepeater(block, x, y, z);
			case 18: return this.renderBlockPane((BlockPane)block, x, y, z);
			case 20: return this.renderBlockVine(block, x, y, z);
			case 23: return this.renderBlockLilyPad(block, x, y, z);	
			case 31: return this.renderLog(block, x, y, z);
			default: return false;
		}
	}

	public boolean renderBlockTorch(Block block1, int i2, int i3, int i4) {
		int i5 = this.blockAccess.getBlockMetadata(i2, i3, i4);
		Tessellator tessellator6 = Tessellator.instance;
		tessellator6.setBrightness(block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4));
		tessellator6.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		double d7 = (double)0.4F;
		double d9 = 0.5D - d7;
		double d11 = (double)0.2F;
		if(i5 == 1) {
			this.renderTorchAtAngle(block1, (double)i2 - d9, (double)i3 + d11, (double)i4, -d7, 0.0D);
		} else if(i5 == 2) {
			this.renderTorchAtAngle(block1, (double)i2 + d9, (double)i3 + d11, (double)i4, d7, 0.0D);
		} else if(i5 == 3) {
			this.renderTorchAtAngle(block1, (double)i2, (double)i3 + d11, (double)i4 - d9, 0.0D, -d7);
		} else if(i5 == 4) {
			this.renderTorchAtAngle(block1, (double)i2, (double)i3 + d11, (double)i4 + d9, 0.0D, d7);
		} else {
			this.renderTorchAtAngle(block1, (double)i2, (double)i3, (double)i4, 0.0D, 0.0D);
		}

		return true;
	}

	public boolean renderBlockLever(Block block1, int i2, int i3, int i4) {
		int i5 = this.blockAccess.getBlockMetadata(i2, i3, i4);
		int i6 = i5 & 7;
		boolean z7 = (i5 & 8) > 0;
		Tessellator tessellator8 = Tessellator.instance;
		boolean z9 = this.overrideBlockTexture >= 0;
		if(!z9) {
			this.overrideBlockTexture = Block.cobblestone.blockIndexInTexture;
		}

		float f10 = 0.25F;
		float f11 = 0.1875F;
		float f12 = 0.1875F;
		if(i6 == 5) {
			block1.setBlockBounds(0.5F - f11, 0.0F, 0.5F - f10, 0.5F + f11, f12, 0.5F + f10);
		} else if(i6 == 6) {
			block1.setBlockBounds(0.5F - f10, 0.0F, 0.5F - f11, 0.5F + f10, f12, 0.5F + f11);
		} else if(i6 == 4) {
			block1.setBlockBounds(0.5F - f11, 0.5F - f10, 1.0F - f12, 0.5F + f11, 0.5F + f10, 1.0F);
		} else if(i6 == 3) {
			block1.setBlockBounds(0.5F - f11, 0.5F - f10, 0.0F, 0.5F + f11, 0.5F + f10, f12);
		} else if(i6 == 2) {
			block1.setBlockBounds(1.0F - f12, 0.5F - f10, 0.5F - f11, 1.0F, 0.5F + f10, 0.5F + f11);
		} else if(i6 == 1) {
			block1.setBlockBounds(0.0F, 0.5F - f10, 0.5F - f11, f12, 0.5F + f10, 0.5F + f11);
		}

		this.renderStandardBlock(block1, i2, i3, i4);
		if(!z9) {
			this.overrideBlockTexture = -1;
		}

		tessellator8.setBrightness(block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4));
		float f13 = 1.0F;
		
		//if(Block.lightValue[block1.blockID] > 0) {
		if(block1 != null && block1.getLightValue(i5) > 0) {
			f13 = 1.0F;
		}

		tessellator8.setColorOpaque_F(f13, f13, f13);
		int i14 = block1.getBlockTextureFromSide(0);
		if(this.overrideBlockTexture >= 0) {
			i14 = this.overrideBlockTexture;
		}

		/*
		int i15 = (i14 & 15) << 4;
		int i16 = i14 & 0xff0;
		float f17 = (float)i15 / 256F;
		float f18 = ((float)i15 + 15.99F) / 256F;
		float f19 = (float)i16 / 256F;
		float f20 = ((float)i16 + 15.99F) / 256F;
		*/
		
		Idx2uvF.calc(i14);

		double f17 = (double)Idx2uvF.u1;
		double f18 = (double)Idx2uvF.u2;
		double f19 = (double)Idx2uvF.v1;
		double f20 = (double)Idx2uvF.v2;
		
		Vec3D[] vec3D21 = new Vec3D[8];
		float f22 = 0.0625F;
		float f23 = 0.0625F;
		float f24 = 0.625F;
		vec3D21[0] = Vec3D.createVector((double)(-f22), 0.0D, (double)(-f23));
		vec3D21[1] = Vec3D.createVector((double)f22, 0.0D, (double)(-f23));
		vec3D21[2] = Vec3D.createVector((double)f22, 0.0D, (double)f23);
		vec3D21[3] = Vec3D.createVector((double)(-f22), 0.0D, (double)f23);
		vec3D21[4] = Vec3D.createVector((double)(-f22), (double)f24, (double)(-f23));
		vec3D21[5] = Vec3D.createVector((double)f22, (double)f24, (double)(-f23));
		vec3D21[6] = Vec3D.createVector((double)f22, (double)f24, (double)f23);
		vec3D21[7] = Vec3D.createVector((double)(-f22), (double)f24, (double)f23);

		for(int i25 = 0; i25 < 8; ++i25) {
			if(z7) {
				vec3D21[i25].zCoord -= 0.0625D;
				vec3D21[i25].rotateAroundX((float)Math.PI / 4.5F);
			} else {
				vec3D21[i25].zCoord += 0.0625D;
				vec3D21[i25].rotateAroundX(-0.69813174F);
			}

			if(i6 == 6) {
				vec3D21[i25].rotateAroundY((float)Math.PI / 2F);
			}

			if(i6 < 5) {
				vec3D21[i25].yCoord -= 0.375D;
				vec3D21[i25].rotateAroundX((float)Math.PI / 2F);
				if(i6 == 4) {
					vec3D21[i25].rotateAroundY(0.0F);
				}

				if(i6 == 3) {
					vec3D21[i25].rotateAroundY((float)Math.PI);
				}

				if(i6 == 2) {
					vec3D21[i25].rotateAroundY((float)Math.PI / 2F);
				}

				if(i6 == 1) {
					vec3D21[i25].rotateAroundY(-1.5707964F);
				}

				vec3D21[i25].xCoord += (double)i2 + 0.5D;
				vec3D21[i25].yCoord += (double)((float)i3 + 0.5F);
				vec3D21[i25].zCoord += (double)i4 + 0.5D;
			} else {
				vec3D21[i25].xCoord += (double)i2 + 0.5D;
				vec3D21[i25].yCoord += (double)((float)i3 + 0.125F);
				vec3D21[i25].zCoord += (double)i4 + 0.5D;
			}
		}

		Vec3D vec3D30 = null;
		Vec3D vec3D26 = null;
		Vec3D vec3D27 = null;
		Vec3D vec3D28 = null;

		for(int i29 = 0; i29 < 6; ++i29) {
			if(i29 == 0) {
				/*
				f17 = (float)(i15 + 7) / 256F;
				f18 = ((float)(i15 + 9) - 0.01F) / 256F;
				f19 = (float)(i16 + 6) / 256F;
				f20 = ((float)(i16 + 8) - 0.01F) / 256F;
				*/
				f17 = Idx2uvF.u1 + Texels.texelsU(7.0F);
				f18 = Idx2uvF.u1 + Texels.texelsU(9.0F);
				f19 = Idx2uvF.v1 + Texels.texelsV(6.0F);
				f20 = Idx2uvF.v1 + Texels.texelsV(8.0F);
			} else if(i29 == 2) {
				/*
				f17 = (float)(i15 + 7) / 256F;
				f18 = ((float)(i15 + 9) - 0.01F) / 256F;
				f19 = (float)(i16 + 6) / 256F;
				f20 = ((float)(i16 + 16) - 0.01F) / 256F;
				*/
				f17 = Idx2uvF.u1 + Texels.texelsU(7.0F);
				f18 = Idx2uvF.u1 + Texels.texelsU(9.0F);
				f19 = Idx2uvF.v1 + Texels.texelsV(6.0F);
				f20 = Idx2uvF.v1 + Texels.texelsV(16.0F);
			}

			if(i29 == 0) {
				vec3D30 = vec3D21[0];
				vec3D26 = vec3D21[1];
				vec3D27 = vec3D21[2];
				vec3D28 = vec3D21[3];
			} else if(i29 == 1) {
				vec3D30 = vec3D21[7];
				vec3D26 = vec3D21[6];
				vec3D27 = vec3D21[5];
				vec3D28 = vec3D21[4];
			} else if(i29 == 2) {
				vec3D30 = vec3D21[1];
				vec3D26 = vec3D21[0];
				vec3D27 = vec3D21[4];
				vec3D28 = vec3D21[5];
			} else if(i29 == 3) {
				vec3D30 = vec3D21[2];
				vec3D26 = vec3D21[1];
				vec3D27 = vec3D21[5];
				vec3D28 = vec3D21[6];
			} else if(i29 == 4) {
				vec3D30 = vec3D21[3];
				vec3D26 = vec3D21[2];
				vec3D27 = vec3D21[6];
				vec3D28 = vec3D21[7];
			} else if(i29 == 5) {
				vec3D30 = vec3D21[0];
				vec3D26 = vec3D21[3];
				vec3D27 = vec3D21[7];
				vec3D28 = vec3D21[4];
			}

			tessellator8.addVertexWithUV(vec3D30.xCoord, vec3D30.yCoord, vec3D30.zCoord, (double)f17, (double)f20);
			tessellator8.addVertexWithUV(vec3D26.xCoord, vec3D26.yCoord, vec3D26.zCoord, (double)f18, (double)f20);
			tessellator8.addVertexWithUV(vec3D27.xCoord, vec3D27.yCoord, vec3D27.zCoord, (double)f18, (double)f19);
			tessellator8.addVertexWithUV(vec3D28.xCoord, vec3D28.yCoord, vec3D28.zCoord, (double)f17, (double)f19);
		}

		return true;
	}
	
	public boolean renderBlockFire(Block block1, int i2, int i3, int i4) {
		Tessellator tessellator5 = Tessellator.instance;
		int i6 = block1.getBlockTextureFromSide(0);
		if(this.overrideBlockTexture >= 0) {
			i6 = this.overrideBlockTexture;
		}

		tessellator5.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		tessellator5.setBrightness(block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4));
		
		/*
		int i7 = (i6 & 15) << 4;
		int i8 = i6 & 0xff0;
		double d9 = (double)((float)i7 / 256F);
		double d11 = (double)(((float)i7 + 15.99F) / 256F);
		double d13 = (double)((float)i8 / 256F);
		double d15 = (double)(((float)i8 + 15.99F) / 256F);
		*/
		
		Idx2uvF.calc(i6);

		double d9 = (double)Idx2uvF.u1;
		double d11 = (double)Idx2uvF.u2;
		double d13 = (double)Idx2uvF.v1;
		double d15 = (double)Idx2uvF.v2;
		
		float f17 = 1.4F;
		double d20;
		double d22;
		double d24;
		double d26;
		double d28;
		double d30;
		double d32;
		if(!this.blockAccess.isBlockNormalCube(i2, i3 - 1, i4) && !Block.fire.canBlockCatchFire(this.blockAccess, i2, i3 - 1, i4)) {
			float f36 = 0.2F;
			float f19 = 0.0625F;
			if((i2 + i3 + i4 & 1) == 1) {
				/*
				d9 = (double)((float)i7 / 256F);
				d11 = (double)(((float)i7 + 15.99F) / 256F);
				d13 = (double)((float)(i8 + 16) / 256F);
				d15 = (double)(((float)i8 + 15.99F + 16.0F) / 256F);
				*/
				d13 = (double)Idx2uvF.v1 + Texels.texelsV(16.0F);
				d15 = (double)Idx2uvF.v2 + Texels.texelsV(16.0F);
			}

			if((i2 / 2 + i3 / 2 + i4 / 2 & 1) == 1) {
				d20 = d11;
				d11 = d9;
				d9 = d20;
			}

			if(Block.fire.canBlockCatchFire(this.blockAccess, i2 - 1, i3, i4)) {
				tessellator5.addVertexWithUV((double)((float)i2 + f36), (double)((float)i3 + f17 + f19), (double)(i4 + 1), d11, d13);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)(i3 + 0) + f19), (double)(i4 + 1), d11, d15);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)(i3 + 0) + f19), (double)(i4 + 0), d9, d15);
				tessellator5.addVertexWithUV((double)((float)i2 + f36), (double)((float)i3 + f17 + f19), (double)(i4 + 0), d9, d13);
				tessellator5.addVertexWithUV((double)((float)i2 + f36), (double)((float)i3 + f17 + f19), (double)(i4 + 0), d9, d13);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)(i3 + 0) + f19), (double)(i4 + 0), d9, d15);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)(i3 + 0) + f19), (double)(i4 + 1), d11, d15);
				tessellator5.addVertexWithUV((double)((float)i2 + f36), (double)((float)i3 + f17 + f19), (double)(i4 + 1), d11, d13);
			}

			if(Block.fire.canBlockCatchFire(this.blockAccess, i2 + 1, i3, i4)) {
				tessellator5.addVertexWithUV((double)((float)(i2 + 1) - f36), (double)((float)i3 + f17 + f19), (double)(i4 + 0), d9, d13);
				tessellator5.addVertexWithUV((double)(i2 + 1 - 0), (double)((float)(i3 + 0) + f19), (double)(i4 + 0), d9, d15);
				tessellator5.addVertexWithUV((double)(i2 + 1 - 0), (double)((float)(i3 + 0) + f19), (double)(i4 + 1), d11, d15);
				tessellator5.addVertexWithUV((double)((float)(i2 + 1) - f36), (double)((float)i3 + f17 + f19), (double)(i4 + 1), d11, d13);
				tessellator5.addVertexWithUV((double)((float)(i2 + 1) - f36), (double)((float)i3 + f17 + f19), (double)(i4 + 1), d11, d13);
				tessellator5.addVertexWithUV((double)(i2 + 1 - 0), (double)((float)(i3 + 0) + f19), (double)(i4 + 1), d11, d15);
				tessellator5.addVertexWithUV((double)(i2 + 1 - 0), (double)((float)(i3 + 0) + f19), (double)(i4 + 0), d9, d15);
				tessellator5.addVertexWithUV((double)((float)(i2 + 1) - f36), (double)((float)i3 + f17 + f19), (double)(i4 + 0), d9, d13);
			}

			if(Block.fire.canBlockCatchFire(this.blockAccess, i2, i3, i4 - 1)) {
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)i3 + f17 + f19), (double)((float)i4 + f36), d11, d13);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)(i3 + 0) + f19), (double)(i4 + 0), d11, d15);
				tessellator5.addVertexWithUV((double)(i2 + 1), (double)((float)(i3 + 0) + f19), (double)(i4 + 0), d9, d15);
				tessellator5.addVertexWithUV((double)(i2 + 1), (double)((float)i3 + f17 + f19), (double)((float)i4 + f36), d9, d13);
				tessellator5.addVertexWithUV((double)(i2 + 1), (double)((float)i3 + f17 + f19), (double)((float)i4 + f36), d9, d13);
				tessellator5.addVertexWithUV((double)(i2 + 1), (double)((float)(i3 + 0) + f19), (double)(i4 + 0), d9, d15);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)(i3 + 0) + f19), (double)(i4 + 0), d11, d15);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)i3 + f17 + f19), (double)((float)i4 + f36), d11, d13);
			}

			if(Block.fire.canBlockCatchFire(this.blockAccess, i2, i3, i4 + 1)) {
				tessellator5.addVertexWithUV((double)(i2 + 1), (double)((float)i3 + f17 + f19), (double)((float)(i4 + 1) - f36), d9, d13);
				tessellator5.addVertexWithUV((double)(i2 + 1), (double)((float)(i3 + 0) + f19), (double)(i4 + 1 - 0), d9, d15);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)(i3 + 0) + f19), (double)(i4 + 1 - 0), d11, d15);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)i3 + f17 + f19), (double)((float)(i4 + 1) - f36), d11, d13);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)i3 + f17 + f19), (double)((float)(i4 + 1) - f36), d11, d13);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)(i3 + 0) + f19), (double)(i4 + 1 - 0), d11, d15);
				tessellator5.addVertexWithUV((double)(i2 + 1), (double)((float)(i3 + 0) + f19), (double)(i4 + 1 - 0), d9, d15);
				tessellator5.addVertexWithUV((double)(i2 + 1), (double)((float)i3 + f17 + f19), (double)((float)(i4 + 1) - f36), d9, d13);
			}

			if(Block.fire.canBlockCatchFire(this.blockAccess, i2, i3 + 1, i4)) {
				d20 = (double)i2 + 0.5D + 0.5D;
				d22 = (double)i2 + 0.5D - 0.5D;
				d24 = (double)i4 + 0.5D + 0.5D;
				d26 = (double)i4 + 0.5D - 0.5D;
				d28 = (double)i2 + 0.5D - 0.5D;
				d30 = (double)i2 + 0.5D + 0.5D;
				d32 = (double)i4 + 0.5D - 0.5D;
				double d34 = (double)i4 + 0.5D + 0.5D;
				
				/*
				d9 = (double)((float)i7 / 256F);
				d11 = (double)(((float)i7 + 15.99F) / 256F);
				d13 = (double)((float)i8 / 256F);
				d15 = (double)(((float)i8 + 15.99F) / 256F);
				*/
				d9 = (double)Idx2uvF.u1;
				d11 = (double)Idx2uvF.u2;
				d13 = (double)Idx2uvF.v1;
				d15 = (double)Idx2uvF.v2;
				
				++i3;
				f17 = -0.2F;
				if((i2 + i3 + i4 & 1) == 0) {
					tessellator5.addVertexWithUV(d28, (double)((float)i3 + f17), (double)(i4 + 0), d11, d13);
					tessellator5.addVertexWithUV(d20, (double)(i3 + 0), (double)(i4 + 0), d11, d15);
					tessellator5.addVertexWithUV(d20, (double)(i3 + 0), (double)(i4 + 1), d9, d15);
					tessellator5.addVertexWithUV(d28, (double)((float)i3 + f17), (double)(i4 + 1), d9, d13);
					
					/*
					d9 = (double)((float)i7 / 256F);
					d11 = (double)(((float)i7 + 15.99F) / 256F);
					d13 = (double)((float)(i8 + 16) / 256F);
					d15 = (double)(((float)i8 + 15.99F + 16.0F) / 256F);
					*/
					d13 = (double)Idx2uvF.v1 + Texels.texelsV(16.0F);
					d15 = (double)Idx2uvF.v2 + Texels.texelsV(16.0F);
					
					tessellator5.addVertexWithUV(d30, (double)((float)i3 + f17), (double)(i4 + 1), d11, d13);
					tessellator5.addVertexWithUV(d22, (double)(i3 + 0), (double)(i4 + 1), d11, d15);
					tessellator5.addVertexWithUV(d22, (double)(i3 + 0), (double)(i4 + 0), d9, d15);
					tessellator5.addVertexWithUV(d30, (double)((float)i3 + f17), (double)(i4 + 0), d9, d13);
				} else {
					tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)i3 + f17), d34, d11, d13);
					tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 0), d26, d11, d15);
					tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 0), d26, d9, d15);
					tessellator5.addVertexWithUV((double)(i2 + 1), (double)((float)i3 + f17), d34, d9, d13);
					/*
					d9 = (double)((float)i7 / 256F);
					d11 = (double)(((float)i7 + 15.99F) / 256F);
					d13 = (double)((float)(i8 + 16) / 256F);
					d15 = (double)(((float)i8 + 15.99F + 16.0F) / 256F);
					*/
					d13 = (double)Idx2uvF.v1 + Texels.texelsV(16.0F);
					d15 = (double)Idx2uvF.v2 + Texels.texelsV(16.0F);
					tessellator5.addVertexWithUV((double)(i2 + 1), (double)((float)i3 + f17), d32, d11, d13);
					tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 0), d24, d11, d15);
					tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 0), d24, d9, d15);
					tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)i3 + f17), d32, d9, d13);
				}
			}
		} else {
			double d18 = (double)i2 + 0.5D + 0.2D;
			d20 = (double)i2 + 0.5D - 0.2D;
			d22 = (double)i4 + 0.5D + 0.2D;
			d24 = (double)i4 + 0.5D - 0.2D;
			d26 = (double)i2 + 0.5D - 0.3D;
			d28 = (double)i2 + 0.5D + 0.3D;
			d30 = (double)i4 + 0.5D - 0.3D;
			d32 = (double)i4 + 0.5D + 0.3D;
			tessellator5.addVertexWithUV(d26, (double)((float)i3 + f17), (double)(i4 + 1), d11, d13);
			tessellator5.addVertexWithUV(d18, (double)(i3 + 0), (double)(i4 + 1), d11, d15);
			tessellator5.addVertexWithUV(d18, (double)(i3 + 0), (double)(i4 + 0), d9, d15);
			tessellator5.addVertexWithUV(d26, (double)((float)i3 + f17), (double)(i4 + 0), d9, d13);
			tessellator5.addVertexWithUV(d28, (double)((float)i3 + f17), (double)(i4 + 0), d11, d13);
			tessellator5.addVertexWithUV(d20, (double)(i3 + 0), (double)(i4 + 0), d11, d15);
			tessellator5.addVertexWithUV(d20, (double)(i3 + 0), (double)(i4 + 1), d9, d15);
			tessellator5.addVertexWithUV(d28, (double)((float)i3 + f17), (double)(i4 + 1), d9, d13);
			
			/*
			d9 = (double)((float)i7 / 256F);
			d11 = (double)(((float)i7 + 15.99F) / 256F);
			d13 = (double)((float)(i8 + 16) / 256F);
			d15 = (double)(((float)i8 + 15.99F + 16.0F) / 256F);
			*/
			d13 = (double)Idx2uvF.v1 + Texels.texelsV(16.0F);
			d15 = (double)Idx2uvF.v2 + Texels.texelsV(16.0F);
			
			tessellator5.addVertexWithUV((double)(i2 + 1), (double)((float)i3 + f17), d32, d11, d13);
			tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 0), d24, d11, d15);
			tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 0), d24, d9, d15);
			tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)i3 + f17), d32, d9, d13);
			tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)i3 + f17), d30, d11, d13);
			tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 0), d22, d11, d15);
			tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 0), d22, d9, d15);
			tessellator5.addVertexWithUV((double)(i2 + 1), (double)((float)i3 + f17), d30, d9, d13);
			d18 = (double)i2 + 0.5D - 0.5D;
			d20 = (double)i2 + 0.5D + 0.5D;
			d22 = (double)i4 + 0.5D - 0.5D;
			d24 = (double)i4 + 0.5D + 0.5D;
			d26 = (double)i2 + 0.5D - 0.4D;
			d28 = (double)i2 + 0.5D + 0.4D;
			d30 = (double)i4 + 0.5D - 0.4D;
			d32 = (double)i4 + 0.5D + 0.4D;
			tessellator5.addVertexWithUV(d26, (double)((float)i3 + f17), (double)(i4 + 0), d9, d13);
			tessellator5.addVertexWithUV(d18, (double)(i3 + 0), (double)(i4 + 0), d9, d15);
			tessellator5.addVertexWithUV(d18, (double)(i3 + 0), (double)(i4 + 1), d11, d15);
			tessellator5.addVertexWithUV(d26, (double)((float)i3 + f17), (double)(i4 + 1), d11, d13);
			tessellator5.addVertexWithUV(d28, (double)((float)i3 + f17), (double)(i4 + 1), d9, d13);
			tessellator5.addVertexWithUV(d20, (double)(i3 + 0), (double)(i4 + 1), d9, d15);
			tessellator5.addVertexWithUV(d20, (double)(i3 + 0), (double)(i4 + 0), d11, d15);
			tessellator5.addVertexWithUV(d28, (double)((float)i3 + f17), (double)(i4 + 0), d11, d13);
			
			/*
			d9 = (double)((float)i7 / 256F);
			d11 = (double)(((float)i7 + 15.99F) / 256F);
			d13 = (double)((float)i8 / 256F);
			d15 = (double)(((float)i8 + 15.99F) / 256F);
			*/
			d13 = (double)Idx2uvF.v1;
			d15 = (double)Idx2uvF.v2;
			
			tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)i3 + f17), d32, d9, d13);
			tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 0), d24, d9, d15);
			tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 0), d24, d11, d15);
			tessellator5.addVertexWithUV((double)(i2 + 1), (double)((float)i3 + f17), d32, d11, d13);
			tessellator5.addVertexWithUV((double)(i2 + 1), (double)((float)i3 + f17), d30, d9, d13);
			tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 0), d22, d9, d15);
			tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 0), d22, d11, d15);
			tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)i3 + f17), d30, d11, d13);
		}

		return true;
	}

	public boolean renderBlockRedstoneWire(BlockRedstoneWire wire, int i2, int i3, int i4) {
		Tessellator tessellator5 = Tessellator.instance;
		int i6 = this.blockAccess.getBlockMetadata(i2, i3, i4);
		int i7 = wire.getBlockTextureFromSideAndMetadata(1, i6);
		if(this.overrideBlockTexture >= 0) {
			i7 = this.overrideBlockTexture;
		}

		tessellator5.setBrightness(wire.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4));
		float f8 = 1.0F;
		float f9 = (float)i6 / 15.0F;
		float f10 = f9 * 0.6F + 0.4F;
		if(i6 == 0) {
			f10 = 0.3F;
		}

		float f11 = f9 * f9 * 0.7F - 0.5F;
		float f12 = f9 * f9 * 0.6F - 0.7F;
		if(f11 < 0.0F) {
			f11 = 0.0F;
		}

		if(f12 < 0.0F) {
			f12 = 0.0F;
		}

		tessellator5.setColorOpaque_F(f10, f11, f12);
		int i13 = (i7 & 15) << 4;
		int i14 = i7 & 0xff0;
		double u1 = (double)((float)i13 / TextureAtlasSize.w);
		double u2 = (double)(((float)i13 + 15.99F) / TextureAtlasSize.w);
		double v1 = (double)((float)i14 / TextureAtlasSize.h);
		double v2 = (double)(((float)i14 + 15.99F) / TextureAtlasSize.h);
		
		boolean z29 = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i2 - 1, i3, i4, 1) || !wire.isBlockGood(this.blockAccess, i2 - 1, i3, i4) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i2 - 1, i3 - 1, i4, -1);
		boolean z30 = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i2 + 1, i3, i4, 3) || !wire.isBlockGood(this.blockAccess, i2 + 1, i3, i4) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i2 + 1, i3 - 1, i4, -1);
		boolean z31 = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i2, i3, i4 - 1, 2) || !wire.isBlockGood(this.blockAccess, i2, i3, i4 - 1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i2, i3 - 1, i4 - 1, -1);
		boolean z32 = BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i2, i3, i4 + 1, 0) || !wire.isBlockGood(this.blockAccess, i2, i3, i4 + 1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i2, i3 - 1, i4 + 1, -1);
		if(!wire.isBlockGood(this.blockAccess, i2, i3 + 1, i4)) {
			if(wire.isBlockGood(this.blockAccess, i2 - 1, i3, i4) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i2 - 1, i3 + 1, i4, -1)) {
				z29 = true;
			}

			if(wire.isBlockGood(this.blockAccess, i2 + 1, i3, i4) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i2 + 1, i3 + 1, i4, -1)) {
				z30 = true;
			}

			if(wire.isBlockGood(this.blockAccess, i2, i3, i4 - 1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i2, i3 + 1, i4 - 1, -1)) {
				z31 = true;
			}

			if(wire.isBlockGood(this.blockAccess, i2, i3, i4 + 1) && BlockRedstoneWire.isPowerProviderOrWire(this.blockAccess, i2, i3 + 1, i4 + 1, -1)) {
				z32 = true;
			}
		}

		float f34 = (float)(i2 + 0);
		float f35 = (float)(i2 + 1);
		float f36 = (float)(i4 + 0);
		float f37 = (float)(i4 + 1);
		byte b38 = 0;
		if((z29 || z30) && !z31 && !z32) {
			b38 = 1;
		}

		if((z31 || z32) && !z30 && !z29) {
			b38 = 2;
		}

		if(b38 != 0) {
			u1 = (double)((float)(i13 + 16) / TextureAtlasSize.w);
			u2 = (double)(((float)(i13 + 16) + 15.99F) / TextureAtlasSize.w);
			v1 = (double)((float)i14 / TextureAtlasSize.h);
			v2 = (double)(((float)i14 + 15.99F) / TextureAtlasSize.h);
		}

		if(b38 == 0) {
			if(!z29) {
				f34 += 0.3125F;
			}

			if(!z29) {
				u1 += Texels.texelsU(5); // 0.01953125D;
			}

			if(!z30) {
				f35 -= 0.3125F;
			}

			if(!z30) {
				u2 -= Texels.texelsU(5); // 0.01953125D;
			}

			if(!z31) {
				f36 += 0.3125F;
			}

			if(!z31) {
				v1 += Texels.texelsV(5); // 0.01953125D;
			}

			if(!z32) {
				f37 -= 0.3125F;
			}

			if(!z32) {
				v2 -= Texels.texelsV(5); // 0.01953125D;
			}

			tessellator5.addVertexWithUV((double)f35, (double)i3 + 0.015625D, (double)f37, u2, v2);
			tessellator5.addVertexWithUV((double)f35, (double)i3 + 0.015625D, (double)f36, u2, v1);
			tessellator5.addVertexWithUV((double)f34, (double)i3 + 0.015625D, (double)f36, u1, v1);
			tessellator5.addVertexWithUV((double)f34, (double)i3 + 0.015625D, (double)f37, u1, v2);
			tessellator5.setColorOpaque_F(f8, f8, f8);
			tessellator5.addVertexWithUV((double)f35, (double)i3 + 0.015625D, (double)f37, u2, v2 + Texels.texH);
			tessellator5.addVertexWithUV((double)f35, (double)i3 + 0.015625D, (double)f36, u2, v1 + Texels.texH);
			tessellator5.addVertexWithUV((double)f34, (double)i3 + 0.015625D, (double)f36, u1, v1 + Texels.texH);
			tessellator5.addVertexWithUV((double)f34, (double)i3 + 0.015625D, (double)f37, u1, v2 + Texels.texH);
		} else if(b38 == 1) {
			tessellator5.addVertexWithUV((double)f35, (double)i3 + 0.015625D, (double)f37, u2, v2);
			tessellator5.addVertexWithUV((double)f35, (double)i3 + 0.015625D, (double)f36, u2, v1);
			tessellator5.addVertexWithUV((double)f34, (double)i3 + 0.015625D, (double)f36, u1, v1);
			tessellator5.addVertexWithUV((double)f34, (double)i3 + 0.015625D, (double)f37, u1, v2);
			tessellator5.setColorOpaque_F(f8, f8, f8);
			tessellator5.addVertexWithUV((double)f35, (double)i3 + 0.015625D, (double)f37, u2, v2 + Texels.texH);
			tessellator5.addVertexWithUV((double)f35, (double)i3 + 0.015625D, (double)f36, u2, v1 + Texels.texH);
			tessellator5.addVertexWithUV((double)f34, (double)i3 + 0.015625D, (double)f36, u1, v1 + Texels.texH);
			tessellator5.addVertexWithUV((double)f34, (double)i3 + 0.015625D, (double)f37, u1, v2 + Texels.texH);
		} else if(b38 == 2) {
			tessellator5.addVertexWithUV((double)f35, (double)i3 + 0.015625D, (double)f37, u2, v2);
			tessellator5.addVertexWithUV((double)f35, (double)i3 + 0.015625D, (double)f36, u1, v2);
			tessellator5.addVertexWithUV((double)f34, (double)i3 + 0.015625D, (double)f36, u1, v1);
			tessellator5.addVertexWithUV((double)f34, (double)i3 + 0.015625D, (double)f37, u2, v1);
			tessellator5.setColorOpaque_F(f8, f8, f8);
			tessellator5.addVertexWithUV((double)f35, (double)i3 + 0.015625D, (double)f37, u2, v2 + Texels.texH);
			tessellator5.addVertexWithUV((double)f35, (double)i3 + 0.015625D, (double)f36, u1, v2 + Texels.texH);
			tessellator5.addVertexWithUV((double)f34, (double)i3 + 0.015625D, (double)f36, u1, v1 + Texels.texH);
			tessellator5.addVertexWithUV((double)f34, (double)i3 + 0.015625D, (double)f37, u2, v1 + Texels.texH);
		}

		if(!wire.isBlockGood(this.blockAccess, i2, i3 + 1, i4)) {
			u1 = (double)((float)(i13 + 16) / TextureAtlasSize.w);
			u2 = (double)(((float)(i13 + 16) + 15.99F) / TextureAtlasSize.w);
			v1 = (double)((float)i14 / TextureAtlasSize.h);
			v2 = (double)(((float)i14 + 15.99F) / TextureAtlasSize.h);
			if(wire.isBlockGood(this.blockAccess, i2 - 1, i3, i4) && this.blockAccess.getBlockId(i2 - 1, i3 + 1, i4) == Block.redstoneWire.blockID) {
				tessellator5.setColorOpaque_F(f8 * f10, f8 * f11, f8 * f12);
				tessellator5.addVertexWithUV((double)i2 + 0.015625D, (double)((float)(i3 + 1) + 0.021875F), (double)(i4 + 1), u2, v1);
				tessellator5.addVertexWithUV((double)i2 + 0.015625D, (double)(i3 + 0), (double)(i4 + 1), u1, v1);
				tessellator5.addVertexWithUV((double)i2 + 0.015625D, (double)(i3 + 0), (double)(i4 + 0), u1, v2);
				tessellator5.addVertexWithUV((double)i2 + 0.015625D, (double)((float)(i3 + 1) + 0.021875F), (double)(i4 + 0), u2, v2);
				tessellator5.setColorOpaque_F(f8, f8, f8);
				tessellator5.addVertexWithUV((double)i2 + 0.015625D, (double)((float)(i3 + 1) + 0.021875F), (double)(i4 + 1), u2, v1 + Texels.texH);
				tessellator5.addVertexWithUV((double)i2 + 0.015625D, (double)(i3 + 0), (double)(i4 + 1), u1, v1 + Texels.texH);
				tessellator5.addVertexWithUV((double)i2 + 0.015625D, (double)(i3 + 0), (double)(i4 + 0), u1, v2 + Texels.texH);
				tessellator5.addVertexWithUV((double)i2 + 0.015625D, (double)((float)(i3 + 1) + 0.021875F), (double)(i4 + 0), u2, v2 + Texels.texH);
			}

			if(wire.isBlockGood(this.blockAccess, i2 + 1, i3, i4) && this.blockAccess.getBlockId(i2 + 1, i3 + 1, i4) == Block.redstoneWire.blockID) {
				tessellator5.setColorOpaque_F(f8 * f10, f8 * f11, f8 * f12);
				tessellator5.addVertexWithUV((double)(i2 + 1) - 0.015625D, (double)(i3 + 0), (double)(i4 + 1), u1, v2);
				tessellator5.addVertexWithUV((double)(i2 + 1) - 0.015625D, (double)((float)(i3 + 1) + 0.021875F), (double)(i4 + 1), u2, v2);
				tessellator5.addVertexWithUV((double)(i2 + 1) - 0.015625D, (double)((float)(i3 + 1) + 0.021875F), (double)(i4 + 0), u2, v1);
				tessellator5.addVertexWithUV((double)(i2 + 1) - 0.015625D, (double)(i3 + 0), (double)(i4 + 0), u1, v1);
				tessellator5.setColorOpaque_F(f8, f8, f8);
				tessellator5.addVertexWithUV((double)(i2 + 1) - 0.015625D, (double)(i3 + 0), (double)(i4 + 1), u1, v2 + Texels.texH);
				tessellator5.addVertexWithUV((double)(i2 + 1) - 0.015625D, (double)((float)(i3 + 1) + 0.021875F), (double)(i4 + 1), u2, v2 + Texels.texH);
				tessellator5.addVertexWithUV((double)(i2 + 1) - 0.015625D, (double)((float)(i3 + 1) + 0.021875F), (double)(i4 + 0), u2, v1 + Texels.texH);
				tessellator5.addVertexWithUV((double)(i2 + 1) - 0.015625D, (double)(i3 + 0), (double)(i4 + 0), u1, v1 + Texels.texH);
			}

			if(wire.isBlockGood(this.blockAccess, i2, i3, i4 - 1) && this.blockAccess.getBlockId(i2, i3 + 1, i4 - 1) == Block.redstoneWire.blockID) {
				tessellator5.setColorOpaque_F(f8 * f10, f8 * f11, f8 * f12);
				tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 0), (double)i4 + 0.015625D, u1, v2);
				tessellator5.addVertexWithUV((double)(i2 + 1), (double)((float)(i3 + 1) + 0.021875F), (double)i4 + 0.015625D, u2, v2);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)(i3 + 1) + 0.021875F), (double)i4 + 0.015625D, u2, v1);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 0), (double)i4 + 0.015625D, u1, v1);
				tessellator5.setColorOpaque_F(f8, f8, f8);
				tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 0), (double)i4 + 0.015625D, u1, v2 + Texels.texH);
				tessellator5.addVertexWithUV((double)(i2 + 1), (double)((float)(i3 + 1) + 0.021875F), (double)i4 + 0.015625D, u2, v2 + Texels.texH);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)(i3 + 1) + 0.021875F), (double)i4 + 0.015625D, u2, v1 + Texels.texH);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 0), (double)i4 + 0.015625D, u1, v1 + Texels.texH);
			}

			if(wire.isBlockGood(this.blockAccess, i2, i3, i4 + 1) && this.blockAccess.getBlockId(i2, i3 + 1, i4 + 1) == Block.redstoneWire.blockID) {
				tessellator5.setColorOpaque_F(f8 * f10, f8 * f11, f8 * f12);
				tessellator5.addVertexWithUV((double)(i2 + 1), (double)((float)(i3 + 1) + 0.021875F), (double)(i4 + 1) - 0.015625D, u2, v1);
				tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 0), (double)(i4 + 1) - 0.015625D, u1, v1);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 0), (double)(i4 + 1) - 0.015625D, u1, v2);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)(i3 + 1) + 0.021875F), (double)(i4 + 1) - 0.015625D, u2, v2);
				tessellator5.setColorOpaque_F(f8, f8, f8);
				tessellator5.addVertexWithUV((double)(i2 + 1), (double)((float)(i3 + 1) + 0.021875F), (double)(i4 + 1) - 0.015625D, u2, v1 + Texels.texH);
				tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 0), (double)(i4 + 1) - 0.015625D, u1, v1 + Texels.texH);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 0), (double)(i4 + 1) - 0.015625D, u1, v2 + Texels.texH);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)((float)(i3 + 1) + 0.021875F), (double)(i4 + 1) - 0.015625D, u2, v2 + Texels.texH);
			}
		}

		return true;
	}

	public boolean renderBlockMinecartTrack(BlockRail blockRail1, int i2, int i3, int i4) {
		Tessellator tessellator5 = Tessellator.instance;
		int i6 = this.blockAccess.getBlockMetadata(i2, i3, i4);
		int i7 = blockRail1.getBlockTextureFromSideAndMetadata(0, i6);
		if(this.overrideBlockTexture >= 0) {
			i7 = this.overrideBlockTexture;
		}

		if(blockRail1.isPowered()) {
			i6 &= 7;
		}

		tessellator5.setBrightness(blockRail1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4));
		tessellator5.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		/*
		int i8 = (i7 & 15) << 4;
		int i9 = i7 & 0xff0;
		double d10 = (double)((float)i8 / 256F);
		double d12 = (double)(((float)i8 + 15.99F) / 256F);
		double d14 = (double)((float)i9 / 256F);
		double d16 = (double)(((float)i9 + 15.99F) / 256F);
		*/
		Idx2uvF.calc(i7);
		double d10 = Idx2uvF.u1;
		double d12 = Idx2uvF.u2;
		double d14 = Idx2uvF.v1;
		double d16 = Idx2uvF.v2;
		
		double d18 = 0.0625D;
		double d20 = (double)(i2 + 1);
		double d22 = (double)(i2 + 1);
		double d24 = (double)(i2 + 0);
		double d26 = (double)(i2 + 0);
		double d28 = (double)(i4 + 0);
		double d30 = (double)(i4 + 1);
		double d32 = (double)(i4 + 1);
		double d34 = (double)(i4 + 0);
		double d36 = (double)i3 + d18;
		double d38 = (double)i3 + d18;
		double d40 = (double)i3 + d18;
		double d42 = (double)i3 + d18;
		if(i6 != 1 && i6 != 2 && i6 != 3 && i6 != 7) {
			if(i6 == 8) {
				d20 = d22 = (double)(i2 + 0);
				d24 = d26 = (double)(i2 + 1);
				d28 = d34 = (double)(i4 + 1);
				d30 = d32 = (double)(i4 + 0);
			} else if(i6 == 9) {
				d20 = d26 = (double)(i2 + 0);
				d22 = d24 = (double)(i2 + 1);
				d28 = d30 = (double)(i4 + 0);
				d32 = d34 = (double)(i4 + 1);
			}
		} else {
			d20 = d26 = (double)(i2 + 1);
			d22 = d24 = (double)(i2 + 0);
			d28 = d30 = (double)(i4 + 1);
			d32 = d34 = (double)(i4 + 0);
		}

		if(i6 != 2 && i6 != 4) {
			if(i6 == 3 || i6 == 5) {
				++d38;
				++d40;
			}
		} else {
			++d36;
			++d42;
		}

		tessellator5.addVertexWithUV(d20, d36, d28, d12, d14);
		tessellator5.addVertexWithUV(d22, d38, d30, d12, d16);
		tessellator5.addVertexWithUV(d24, d40, d32, d10, d16);
		tessellator5.addVertexWithUV(d26, d42, d34, d10, d14);
		tessellator5.addVertexWithUV(d26, d42, d34, d10, d14);
		tessellator5.addVertexWithUV(d24, d40, d32, d10, d16);
		tessellator5.addVertexWithUV(d22, d38, d30, d12, d16);
		tessellator5.addVertexWithUV(d20, d36, d28, d12, d14);
		return true;
	}

	public boolean renderBlockLadder(Block block1, int i2, int i3, int i4) {
		Tessellator tessellator5 = Tessellator.instance;
		int i6 = block1.getBlockTextureFromSide(0);
		if(this.overrideBlockTexture >= 0) {
			i6 = this.overrideBlockTexture;
		}

		tessellator5.setBrightness(block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4));
		float f7 = 1.0F;
		tessellator5.setColorOpaque_F(f7, f7, f7);
		
		/*
		int i22 = (i6 & 15) << 4;
		int i8 = i6 & 0xff0;
		double d9 = (double)((float)i22 / 256F);
		double d11 = (double)(((float)i22 + 15.99F) / 256F);
		double d13 = (double)((float)i8 / 256F);
		double d15 = (double)(((float)i8 + 15.99F) / 256F);
		*/
		
		Idx2uvF.calc(i6);
		double d9 = Idx2uvF.u1;
		double d11 = Idx2uvF.u2;
		double d13 = Idx2uvF.v1;
		double d15 = Idx2uvF.v2;
		
		int i17 = this.blockAccess.getBlockMetadata(i2, i3, i4);
		double d18 = 0.0D;
		double d20 = (double)0.05F;
		if(i17 == 5) {
			tessellator5.addVertexWithUV((double)i2 + d20, (double)(i3 + 1) + d18, (double)(i4 + 1) + d18, d9, d13);
			tessellator5.addVertexWithUV((double)i2 + d20, (double)(i3 + 0) - d18, (double)(i4 + 1) + d18, d9, d15);
			tessellator5.addVertexWithUV((double)i2 + d20, (double)(i3 + 0) - d18, (double)(i4 + 0) - d18, d11, d15);
			tessellator5.addVertexWithUV((double)i2 + d20, (double)(i3 + 1) + d18, (double)(i4 + 0) - d18, d11, d13);
		}

		if(i17 == 4) {
			tessellator5.addVertexWithUV((double)(i2 + 1) - d20, (double)(i3 + 0) - d18, (double)(i4 + 1) + d18, d11, d15);
			tessellator5.addVertexWithUV((double)(i2 + 1) - d20, (double)(i3 + 1) + d18, (double)(i4 + 1) + d18, d11, d13);
			tessellator5.addVertexWithUV((double)(i2 + 1) - d20, (double)(i3 + 1) + d18, (double)(i4 + 0) - d18, d9, d13);
			tessellator5.addVertexWithUV((double)(i2 + 1) - d20, (double)(i3 + 0) - d18, (double)(i4 + 0) - d18, d9, d15);
		}

		if(i17 == 3) {
			tessellator5.addVertexWithUV((double)(i2 + 1) + d18, (double)(i3 + 0) - d18, (double)i4 + d20, d11, d15);
			tessellator5.addVertexWithUV((double)(i2 + 1) + d18, (double)(i3 + 1) + d18, (double)i4 + d20, d11, d13);
			tessellator5.addVertexWithUV((double)(i2 + 0) - d18, (double)(i3 + 1) + d18, (double)i4 + d20, d9, d13);
			tessellator5.addVertexWithUV((double)(i2 + 0) - d18, (double)(i3 + 0) - d18, (double)i4 + d20, d9, d15);
		}

		if(i17 == 2) {
			tessellator5.addVertexWithUV((double)(i2 + 1) + d18, (double)(i3 + 1) + d18, (double)(i4 + 1) - d20, d9, d13);
			tessellator5.addVertexWithUV((double)(i2 + 1) + d18, (double)(i3 + 0) - d18, (double)(i4 + 1) - d20, d9, d15);
			tessellator5.addVertexWithUV((double)(i2 + 0) - d18, (double)(i3 + 0) - d18, (double)(i4 + 1) - d20, d11, d15);
			tessellator5.addVertexWithUV((double)(i2 + 0) - d18, (double)(i3 + 1) + d18, (double)(i4 + 1) - d20, d11, d13);
		}

		return true;
	}

	public boolean renderBlockVine(Block block1, int i2, int i3, int i4) {
		Tessellator tessellator5 = Tessellator.instance;
		int i6 = block1.getBlockTextureFromSide(0);
		if(this.overrideBlockTexture >= 0) {
			i6 = this.overrideBlockTexture;
		}

		float f7 = 1.0F;
		tessellator5.setBrightness(block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4));
		int i8 = block1.colorMultiplier(this.blockAccess, i2, i3, i4);
		float f9 = (float)(i8 >> 16 & 255) / 255.0F;
		float f10 = (float)(i8 >> 8 & 255) / 255.0F;
		float f11 = (float)(i8 & 255) / 255.0F;
		tessellator5.setColorOpaque_F(f7 * f9, f7 * f10, f7 * f11);
		
		Idx2uvF.calc(i6);
		double d22 = Idx2uvF.u1;
		double d12 = Idx2uvF.u2;
		double d14 = Idx2uvF.v1;
		double d16 = Idx2uvF.v2;
		
		double d18 = (double)0.05F;
		int i20 = this.blockAccess.getBlockMetadata(i2, i3, i4);
		if((i20 & 2) != 0) {
			tessellator5.addVertexWithUV((double)i2 + d18, (double)(i3 + 1), (double)(i4 + 1), d22, d14);
			tessellator5.addVertexWithUV((double)i2 + d18, (double)(i3 + 0), (double)(i4 + 1), d22, d16);
			tessellator5.addVertexWithUV((double)i2 + d18, (double)(i3 + 0), (double)(i4 + 0), d12, d16);
			tessellator5.addVertexWithUV((double)i2 + d18, (double)(i3 + 1), (double)(i4 + 0), d12, d14);
			tessellator5.addVertexWithUV((double)i2 + d18, (double)(i3 + 1), (double)(i4 + 0), d12, d14);
			tessellator5.addVertexWithUV((double)i2 + d18, (double)(i3 + 0), (double)(i4 + 0), d12, d16);
			tessellator5.addVertexWithUV((double)i2 + d18, (double)(i3 + 0), (double)(i4 + 1), d22, d16);
			tessellator5.addVertexWithUV((double)i2 + d18, (double)(i3 + 1), (double)(i4 + 1), d22, d14);
		}

		if((i20 & 8) != 0) {
			tessellator5.addVertexWithUV((double)(i2 + 1) - d18, (double)(i3 + 0), (double)(i4 + 1), d12, d16);
			tessellator5.addVertexWithUV((double)(i2 + 1) - d18, (double)(i3 + 1), (double)(i4 + 1), d12, d14);
			tessellator5.addVertexWithUV((double)(i2 + 1) - d18, (double)(i3 + 1), (double)(i4 + 0), d22, d14);
			tessellator5.addVertexWithUV((double)(i2 + 1) - d18, (double)(i3 + 0), (double)(i4 + 0), d22, d16);
			tessellator5.addVertexWithUV((double)(i2 + 1) - d18, (double)(i3 + 0), (double)(i4 + 0), d22, d16);
			tessellator5.addVertexWithUV((double)(i2 + 1) - d18, (double)(i3 + 1), (double)(i4 + 0), d22, d14);
			tessellator5.addVertexWithUV((double)(i2 + 1) - d18, (double)(i3 + 1), (double)(i4 + 1), d12, d14);
			tessellator5.addVertexWithUV((double)(i2 + 1) - d18, (double)(i3 + 0), (double)(i4 + 1), d12, d16);
		}

		if((i20 & 4) != 0) {
			tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 0), (double)i4 + d18, d12, d16);
			tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 1), (double)i4 + d18, d12, d14);
			tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 1), (double)i4 + d18, d22, d14);
			tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 0), (double)i4 + d18, d22, d16);
			tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 0), (double)i4 + d18, d22, d16);
			tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 1), (double)i4 + d18, d22, d14);
			tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 1), (double)i4 + d18, d12, d14);
			tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 0), (double)i4 + d18, d12, d16);
		}

		if((i20 & 1) != 0) {
			tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 1), (double)(i4 + 1) - d18, d22, d14);
			tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 0), (double)(i4 + 1) - d18, d22, d16);
			tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 0), (double)(i4 + 1) - d18, d12, d16);
			tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 1), (double)(i4 + 1) - d18, d12, d14);
			tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 1), (double)(i4 + 1) - d18, d12, d14);
			tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 0), (double)(i4 + 1) - d18, d12, d16);
			tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 0), (double)(i4 + 1) - d18, d22, d16);
			tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 1), (double)(i4 + 1) - d18, d22, d14);
		}

		if(this.blockAccess.isBlockNormalCube(i2, i3 + 1, i4)) {
			tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 1) - d18, (double)(i4 + 0), d22, d14);
			tessellator5.addVertexWithUV((double)(i2 + 1), (double)(i3 + 1) - d18, (double)(i4 + 1), d22, d16);
			tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 1) - d18, (double)(i4 + 1), d12, d16);
			tessellator5.addVertexWithUV((double)(i2 + 0), (double)(i3 + 1) - d18, (double)(i4 + 0), d12, d14);
		}

		return true;
	}
	
	public boolean renderBlockPane(BlockPane block, int x, int y, int z) { 
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, x, y, z));
		
		int colorMultiplier = block.colorMultiplier(this.blockAccess, x, y, z);
		float r = (float)(colorMultiplier >> 16 & 255) / 255.0F;
		float g = (float)(colorMultiplier >> 8 & 255) / 255.0F;
		float b = (float)(colorMultiplier & 255) / 255.0F;
		
		tessellator.setColorOpaque_F(r, g, b);
		
		boolean connectN = block.canThisPaneConnectToThisBlockID(this.blockAccess.getBlockId(x, y, z - 1));
		boolean connectS = block.canThisPaneConnectToThisBlockID(this.blockAccess.getBlockId(x, y, z + 1));
		boolean connectW = block.canThisPaneConnectToThisBlockID(this.blockAccess.getBlockId(x - 1, y, z));
		boolean connectE = block.canThisPaneConnectToThisBlockID(this.blockAccess.getBlockId(x + 1, y, z));
		
		// New, simple renderer
		if (!connectN && !connectS && !connectW && !connectE) {
			block.setBlockBounds(.5F-0.0625F, 0.0F, .5F-0.0625F, .5F+0.0625F, 1.0F, .5F+0.0625F);
			this.renderStandardBlock(block, x, y, z);
		} else if (connectN && connectS && !connectW && !connectE) {
			block.setBlockBounds(.5F-0.0625F, 0.0F, 0.0F, .5F+0.0625F, 1.0F, 1.0F);
			this.renderStandardBlock(block, x, y, z);
		} else if (!connectN && !connectS && connectW && connectE) {
			block.setBlockBounds(0.0F, 0.0F, .5F-0.0625F, 1.0F, 1.0F, .5F+0.0625F);
			this.renderStandardBlock(block, x, y, z);
		} else {
			if (connectS) {
				block.setBlockBounds(.5F-0.0625F, 0.0F, .5F, .5F+0.0625F, 1.0F, 1F);
				this.renderStandardBlock(block, x, y, z);
			}
			
			if (connectN) {
				block.setBlockBounds(.5F-0.0625F, 0.0F, 0F, .5F+0.0625F, 1.0F, .5F);
				this.renderStandardBlock(block, x, y, z);
			}
			
			if (connectE) {
				block.setBlockBounds(0.5F, 0.0F, .5F-0.0625F, 1.0F, 1.0F, .5F+0.0625F);
				this.renderStandardBlock(block, x, y, z);
			}
			
			if (connectW) {
				block.setBlockBounds(0F, 0.0F, .5F-0.0625F, 0.5F, 1.0F, .5F+0.0625F);
				this.renderStandardBlock(block, x, y, z);
			}
		}
		
		return true;
	}
	
	public boolean renderCrossedSquares(Block block1, int i2, int i3, int i4) {
		return this.renderCrossedSquares(block1, i2, i3, i4, false);
	}
	
	public boolean renderCrossedSquares(Block block1, int i2, int i3, int i4, boolean fixedY) {
		Tessellator tessellator5 = Tessellator.instance;
		tessellator5.setBrightness(block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4));
		float f6 = 1.0F;
		int i7 = block1.colorMultiplier(this.blockAccess, i2, i3, i4);
		float f8 = (float)(i7 >> 16 & 255) / 255.0F;
		float f9 = (float)(i7 >> 8 & 255) / 255.0F;
		float f10 = (float)(i7 & 255) / 255.0F;

		tessellator5.setColorOpaque_F(f6 * f8, f6 * f9, f6 * f10);
		double d19 = (double)i2;
		double d20 = (double)i3;
		double d15 = (double)i4;
		if(block1 instanceof BlockFlower) {
			long j17 = (long)(i2 * 3129871) ^ (long)i4 * 116129781L ^ (long)i3;
			j17 = j17 * j17 * 42317861L + j17 * 11L;
			d19 += ((double)((float)(j17 >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
			if (!fixedY) d20 += ((double)((float)(j17 >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
			d15 += ((double)((float)(j17 >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;
		}

		this.drawCrossedSquares(block1, this.blockAccess.getBlockMetadata(i2, i3, i4), d19, d20, d15);
		return true;
	}

	public boolean renderBlockCrops(Block block1, int i2, int i3, int i4) {
		Tessellator tessellator5 = Tessellator.instance;
		tessellator5.setBrightness(block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4));
		tessellator5.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		this.renderBlockCropsImpl(block1, this.blockAccess.getBlockMetadata(i2, i3, i4), (double)i2, (double)((float)i3 - 0.0625F), (double)i4);
		return true;
	}

	public void renderTorchAtAngle(Block block1, double d2, double d4, double d6, double d8, double d10) {
		Tessellator tessellator12 = Tessellator.instance;
		int i13 = block1.getBlockTextureFromSide(0);
		if(this.overrideBlockTexture >= 0) {
			i13 = this.overrideBlockTexture;
		}

		/*
		int i14 = (i13 & 15) << 4;
		int i15 = i13 & 0xff0;
		
		float f16 = (float)i14 / 256F;
		float f17 = ((float)i14 + 15.99F) / 256F;
		float f18 = (float)i15 / 256F;
		float f19 = ((float)i15 + 15.99F) / 256F;
		
		double d20 = (double)f16 + 7.0D / 256D;
		double d22 = (double)f18 + 6.0D / 256D;
		double d24 = (double)f16 + 9.0D / 256D;
		double d26 = (double)f18 + 8.0D / 256D;
		*/
		Idx2uvF.calc(i13);
		
		double f16 = Idx2uvF.u1;
		double f17 = Idx2uvF.u2;
		double f18 = Idx2uvF.v1;
		double f19 = Idx2uvF.v2;
		
		double d20 = f16 + Texels.texelsU(7.0F);
		double d22 = f18 + Texels.texelsV(6.0F);
		double d24 = f16 + Texels.texelsU(9.0F);
		double d26 = f18 + Texels.texelsV(8.0F);
		
		d2 += 0.5D;
		d6 += 0.5D;
		double d28 = d2 - 0.5D;
		double d30 = d2 + 0.5D;
		double d32 = d6 - 0.5D;
		double d34 = d6 + 0.5D;
		double d36 = 0.0625D;
		double d38 = 0.625D;
		tessellator12.addVertexWithUV(d2 + d8 * (1.0D - d38) - d36, d4 + d38, d6 + d10 * (1.0D - d38) - d36, d20, d22);
		tessellator12.addVertexWithUV(d2 + d8 * (1.0D - d38) - d36, d4 + d38, d6 + d10 * (1.0D - d38) + d36, d20, d26);
		tessellator12.addVertexWithUV(d2 + d8 * (1.0D - d38) + d36, d4 + d38, d6 + d10 * (1.0D - d38) + d36, d24, d26);
		tessellator12.addVertexWithUV(d2 + d8 * (1.0D - d38) + d36, d4 + d38, d6 + d10 * (1.0D - d38) - d36, d24, d22);
		tessellator12.addVertexWithUV(d2 - d36, d4 + 1.0D, d32, (double)f16, (double)f18);
		tessellator12.addVertexWithUV(d2 - d36 + d8, d4 + 0.0D, d32 + d10, (double)f16, (double)f19);
		tessellator12.addVertexWithUV(d2 - d36 + d8, d4 + 0.0D, d34 + d10, (double)f17, (double)f19);
		tessellator12.addVertexWithUV(d2 - d36, d4 + 1.0D, d34, (double)f17, (double)f18);
		tessellator12.addVertexWithUV(d2 + d36, d4 + 1.0D, d34, (double)f16, (double)f18);
		tessellator12.addVertexWithUV(d2 + d8 + d36, d4 + 0.0D, d34 + d10, (double)f16, (double)f19);
		tessellator12.addVertexWithUV(d2 + d8 + d36, d4 + 0.0D, d32 + d10, (double)f17, (double)f19);
		tessellator12.addVertexWithUV(d2 + d36, d4 + 1.0D, d32, (double)f17, (double)f18);
		tessellator12.addVertexWithUV(d28, d4 + 1.0D, d6 + d36, (double)f16, (double)f18);
		tessellator12.addVertexWithUV(d28 + d8, d4 + 0.0D, d6 + d36 + d10, (double)f16, (double)f19);
		tessellator12.addVertexWithUV(d30 + d8, d4 + 0.0D, d6 + d36 + d10, (double)f17, (double)f19);
		tessellator12.addVertexWithUV(d30, d4 + 1.0D, d6 + d36, (double)f17, (double)f18);
		tessellator12.addVertexWithUV(d30, d4 + 1.0D, d6 - d36, (double)f16, (double)f18);
		tessellator12.addVertexWithUV(d30 + d8, d4 + 0.0D, d6 - d36 + d10, (double)f16, (double)f19);
		tessellator12.addVertexWithUV(d28 + d8, d4 + 0.0D, d6 - d36 + d10, (double)f17, (double)f19);
		tessellator12.addVertexWithUV(d28, d4 + 1.0D, d6 - d36, (double)f17, (double)f18);
	}

	public void drawCrossedSquares(Block block, int meta, double x, double y, double z) {
		Tessellator tes = Tessellator.instance;
		int texId = block.getBlockTextureFromSideAndMetadata(0, meta);
		if(this.overrideBlockTexture >= 0) {
			texId = this.overrideBlockTexture;
		}

		Idx2uvF.calc(texId);
		double u1 = Idx2uvF.u1;
		double u2 = Idx2uvF.u2;
		double v1 = Idx2uvF.v1;
		double v2 = Idx2uvF.v2;
		
		double x1 = x + 0.5D - 0.45D;
		double x2 = x + 0.5D + 0.45D;
		double z1 = z + 0.5D - 0.45D;
		double z2 = z + 0.5D + 0.45D;
		
		tes.addVertexWithUV(x1, y + 1.0D, z1, u1, v1);
		tes.addVertexWithUV(x1, y + 0.0D, z1, u1, v2);
		tes.addVertexWithUV(x2, y + 0.0D, z2, u2, v2);
		tes.addVertexWithUV(x2, y + 1.0D, z2, u2, v1);
		tes.addVertexWithUV(x2, y + 1.0D, z2, u1, v1);
		tes.addVertexWithUV(x2, y + 0.0D, z2, u1, v2);
		tes.addVertexWithUV(x1, y + 0.0D, z1, u2, v2);
		tes.addVertexWithUV(x1, y + 1.0D, z1, u2, v1);
		tes.addVertexWithUV(x1, y + 1.0D, z2, u1, v1);
		tes.addVertexWithUV(x1, y + 0.0D, z2, u1, v2);
		tes.addVertexWithUV(x2, y + 0.0D, z1, u2, v2);
		tes.addVertexWithUV(x2, y + 1.0D, z1, u2, v1);
		tes.addVertexWithUV(x2, y + 1.0D, z1, u1, v1);
		tes.addVertexWithUV(x2, y + 0.0D, z1, u1, v2);
		tes.addVertexWithUV(x1, y + 0.0D, z2, u2, v2);
		tes.addVertexWithUV(x1, y + 1.0D, z2, u2, v1);
	}

	public void renderBlockStemSmall(Block block1, int i2, double d3, double d5, double d7, double d9) {
		Tessellator tessellator11 = Tessellator.instance;
		int i12 = block1.getBlockTextureFromSideAndMetadata(0, i2);
		if(this.overrideBlockTexture >= 0) {
			i12 = this.overrideBlockTexture;
		}

		/*
		int i13 = (i12 & 15) << 4;
		int i14 = i12 & 0xff0;
		double d15 = (double)((float)i13 / 256F);
		double d17 = (double)(((float)i13 + 15.99F) / 256F);
		double d19 = (double)((float)i14 / 256F);
		double d21 = ((double)i14 + 15.989999771118164D * d3) / 256D;
		*/
		Idx2uvF.calc(i12);
		double d15 = Idx2uvF.u1;
		double d17 = Idx2uvF.u2;
		double d19 = Idx2uvF.v1;
		double d21 = Idx2uvF.v2;
		
		double d23 = d5 + 0.5D - (double)0.45F;
		double d25 = d5 + 0.5D + (double)0.45F;
		double d27 = d9 + 0.5D - (double)0.45F;
		double d29 = d9 + 0.5D + (double)0.45F;
		tessellator11.addVertexWithUV(d23, d7 + d3, d27, d15, d19);
		tessellator11.addVertexWithUV(d23, d7 + 0.0D, d27, d15, d21);
		tessellator11.addVertexWithUV(d25, d7 + 0.0D, d29, d17, d21);
		tessellator11.addVertexWithUV(d25, d7 + d3, d29, d17, d19);
		tessellator11.addVertexWithUV(d25, d7 + d3, d29, d15, d19);
		tessellator11.addVertexWithUV(d25, d7 + 0.0D, d29, d15, d21);
		tessellator11.addVertexWithUV(d23, d7 + 0.0D, d27, d17, d21);
		tessellator11.addVertexWithUV(d23, d7 + d3, d27, d17, d19);
		tessellator11.addVertexWithUV(d23, d7 + d3, d29, d15, d19);
		tessellator11.addVertexWithUV(d23, d7 + 0.0D, d29, d15, d21);
		tessellator11.addVertexWithUV(d25, d7 + 0.0D, d27, d17, d21);
		tessellator11.addVertexWithUV(d25, d7 + d3, d27, d17, d19);
		tessellator11.addVertexWithUV(d25, d7 + d3, d27, d15, d19);
		tessellator11.addVertexWithUV(d25, d7 + 0.0D, d27, d15, d21);
		tessellator11.addVertexWithUV(d23, d7 + 0.0D, d29, d17, d21);
		tessellator11.addVertexWithUV(d23, d7 + d3, d29, d17, d19);
	}

	public boolean renderBlockLilyPad(Block block, int x, int y, int z) {

		Tessellator tessellator = Tessellator.instance;
		int textureIndex = block.blockIndexInTexture;
		if(this.overrideBlockTexture >= 0) {
			textureIndex = this.overrideBlockTexture;
		}

		/*
		int i7 = (textureIndex & 15) << 4;
		int i8 = textureIndex & 0xff0;
		double u1 = (double)((float)i7 / 256F);
		double u2 = (double)(((float)i7 + 15.99F) / 256F);
		double v1 = (double)((float)i8 / 256F);
		double v2 = (double)(((float)i8 + 15.99F) / 256F);
		*/
		Idx2uvF.calc(textureIndex);
		double u1 = Idx2uvF.u1;
		double u2 = Idx2uvF.u2;
		double v1 = Idx2uvF.v1;
		double v2 = Idx2uvF.v2;
		
		long coordinateHash = (long)(x * 3129871) ^ (long)z * 116129781L ^ (long)y;
		coordinateHash = coordinateHash * coordinateHash * 42317861L + coordinateHash * 11L;
		int variation = (int)((coordinateHash >> 16) & 7L);
		tessellator.setBrightness(block.getMixedBrightnessForBlock(this.blockAccess, x, y, z));
		
		// Render a lily flower on top
		if(block == Block.waterlily && (variation & 4) != 0) {
			this.overrideBlockTexture = 12 * 16 + 12;
			this.renderCrossedSquares(Block.plantYellow, x, y, z, true);
			this.overrideBlockTexture = -1;
		}
		
		float fX = (float)x + 0.5F;
		float fZ = (float)z + 0.5F;
		float fR1 = (float)(variation & 1) * 0.5F * (float)(1 - variation / 2 % 2 * 2);
		float fR2 = (float)(variation + 1 & 1) * 0.5F * (float)(1 - (variation + 1) / 2 % 2 * 2);
		double fY = (double)y + 0.015625F;

		tessellator.setColorOpaque_I(block.getBlockColor());
		tessellator.addVertexWithUV((double)(fX + fR1 - fR2), fY, (double)(fZ + fR1 + fR2), u1, v1);
		tessellator.addVertexWithUV((double)(fX + fR1 + fR2), fY, (double)(fZ - fR1 + fR2), u2, v1);
		tessellator.addVertexWithUV((double)(fX - fR1 + fR2), fY, (double)(fZ - fR1 - fR2), u2, v2);
		tessellator.addVertexWithUV((double)(fX - fR1 - fR2), fY, (double)(fZ + fR1 - fR2), u1, v2);
		tessellator.setColorOpaque_I((block.getBlockColor() & 16711422) >> 1);
		tessellator.addVertexWithUV((double)(fX - fR1 - fR2), fY, (double)(fZ + fR1 - fR2), u1, v2);
		tessellator.addVertexWithUV((double)(fX - fR1 + fR2), fY, (double)(fZ - fR1 - fR2), u2, v2);
		tessellator.addVertexWithUV((double)(fX + fR1 + fR2), fY, (double)(fZ - fR1 + fR2), u2, v1);
		tessellator.addVertexWithUV((double)(fX + fR1 - fR2), fY, (double)(fZ + fR1 + fR2), u1, v1);
		
		return true;
	}

	public void renderBlockStemBig(Block block1, int i2, int i3, double d4, double d6, double d8, double d10) {
		Tessellator tessellator12 = Tessellator.instance;
		int i13 = block1.getBlockTextureFromSideAndMetadata(0, i2) + 16;
		if(this.overrideBlockTexture >= 0) {
			i13 = this.overrideBlockTexture;
		}

		/*
		int i14 = (i13 & 15) << 4;
		int i15 = i13 & 0xff0;
		double d16 = (double)((float)i14 / 256F);
		double d18 = (double)(((float)i14 + 15.99F) / 256F);
		double d20 = (double)((float)i15 / 256F);
		double d22 = ((double)i15 + 15.989999771118164D * d4) / 256D;
		*/
		Idx2uvF.calc(i13);
		double d16 = Idx2uvF.u1;
		double d18 = Idx2uvF.u2;
		double d20 = Idx2uvF.v1;
		double d22 = Idx2uvF.v2;
		
		double d24 = d6 + 0.5D - 0.5D;
		double d26 = d6 + 0.5D + 0.5D;
		double d28 = d10 + 0.5D - 0.5D;
		double d30 = d10 + 0.5D + 0.5D;
		double d32 = d6 + 0.5D;
		double d34 = d10 + 0.5D;
		if((i3 + 1) / 2 % 2 == 1) {
			double d36 = d18;
			d18 = d16;
			d16 = d36;
		}

		if(i3 < 2) {
			tessellator12.addVertexWithUV(d24, d8 + d4, d34, d16, d20);
			tessellator12.addVertexWithUV(d24, d8 + 0.0D, d34, d16, d22);
			tessellator12.addVertexWithUV(d26, d8 + 0.0D, d34, d18, d22);
			tessellator12.addVertexWithUV(d26, d8 + d4, d34, d18, d20);
			tessellator12.addVertexWithUV(d26, d8 + d4, d34, d18, d20);
			tessellator12.addVertexWithUV(d26, d8 + 0.0D, d34, d18, d22);
			tessellator12.addVertexWithUV(d24, d8 + 0.0D, d34, d16, d22);
			tessellator12.addVertexWithUV(d24, d8 + d4, d34, d16, d20);
		} else {
			tessellator12.addVertexWithUV(d32, d8 + d4, d30, d16, d20);
			tessellator12.addVertexWithUV(d32, d8 + 0.0D, d30, d16, d22);
			tessellator12.addVertexWithUV(d32, d8 + 0.0D, d28, d18, d22);
			tessellator12.addVertexWithUV(d32, d8 + d4, d28, d18, d20);
			tessellator12.addVertexWithUV(d32, d8 + d4, d28, d18, d20);
			tessellator12.addVertexWithUV(d32, d8 + 0.0D, d28, d18, d22);
			tessellator12.addVertexWithUV(d32, d8 + 0.0D, d30, d16, d22);
			tessellator12.addVertexWithUV(d32, d8 + d4, d30, d16, d20);
		}

	}

	public void renderBlockCropsImpl(Block block1, int i2, double d3, double d5, double d7) {
		Tessellator tessellator9 = Tessellator.instance;
		int i10 = block1.getBlockTextureFromSideAndMetadata(0, i2);
		if(this.overrideBlockTexture >= 0) {
			i10 = this.overrideBlockTexture;
		}

		/*
		int i11 = (i10 & 15) << 4;
		int i12 = i10 & 0xff0;
		double d13 = (double)((float)i11 / 256F);
		double d15 = (double)(((float)i11 + 15.99F) / 256F);
		double d17 = (double)((float)i12 / 256F);
		double d19 = (double)(((float)i12 + 15.99F) / 256F);
		*/
		Idx2uvF.calc(i10);
		double d13 = Idx2uvF.u1;
		double d15 = Idx2uvF.u2;
		double d17 = Idx2uvF.v1;
		double d19 = Idx2uvF.v2;
		
		double d21 = d3 + 0.5D - 0.25D;
		double d23 = d3 + 0.5D + 0.25D;
		double d25 = d7 + 0.5D - 0.5D;
		double d27 = d7 + 0.5D + 0.5D;
		tessellator9.addVertexWithUV(d21, d5 + 1.0D, d25, d13, d17);
		tessellator9.addVertexWithUV(d21, d5 + 0.0D, d25, d13, d19);
		tessellator9.addVertexWithUV(d21, d5 + 0.0D, d27, d15, d19);
		tessellator9.addVertexWithUV(d21, d5 + 1.0D, d27, d15, d17);
		tessellator9.addVertexWithUV(d21, d5 + 1.0D, d27, d13, d17);
		tessellator9.addVertexWithUV(d21, d5 + 0.0D, d27, d13, d19);
		tessellator9.addVertexWithUV(d21, d5 + 0.0D, d25, d15, d19);
		tessellator9.addVertexWithUV(d21, d5 + 1.0D, d25, d15, d17);
		tessellator9.addVertexWithUV(d23, d5 + 1.0D, d27, d13, d17);
		tessellator9.addVertexWithUV(d23, d5 + 0.0D, d27, d13, d19);
		tessellator9.addVertexWithUV(d23, d5 + 0.0D, d25, d15, d19);
		tessellator9.addVertexWithUV(d23, d5 + 1.0D, d25, d15, d17);
		tessellator9.addVertexWithUV(d23, d5 + 1.0D, d25, d13, d17);
		tessellator9.addVertexWithUV(d23, d5 + 0.0D, d25, d13, d19);
		tessellator9.addVertexWithUV(d23, d5 + 0.0D, d27, d15, d19);
		tessellator9.addVertexWithUV(d23, d5 + 1.0D, d27, d15, d17);
		d21 = d3 + 0.5D - 0.5D;
		d23 = d3 + 0.5D + 0.5D;
		d25 = d7 + 0.5D - 0.25D;
		d27 = d7 + 0.5D + 0.25D;
		tessellator9.addVertexWithUV(d21, d5 + 1.0D, d25, d13, d17);
		tessellator9.addVertexWithUV(d21, d5 + 0.0D, d25, d13, d19);
		tessellator9.addVertexWithUV(d23, d5 + 0.0D, d25, d15, d19);
		tessellator9.addVertexWithUV(d23, d5 + 1.0D, d25, d15, d17);
		tessellator9.addVertexWithUV(d23, d5 + 1.0D, d25, d13, d17);
		tessellator9.addVertexWithUV(d23, d5 + 0.0D, d25, d13, d19);
		tessellator9.addVertexWithUV(d21, d5 + 0.0D, d25, d15, d19);
		tessellator9.addVertexWithUV(d21, d5 + 1.0D, d25, d15, d17);
		tessellator9.addVertexWithUV(d23, d5 + 1.0D, d27, d13, d17);
		tessellator9.addVertexWithUV(d23, d5 + 0.0D, d27, d13, d19);
		tessellator9.addVertexWithUV(d21, d5 + 0.0D, d27, d15, d19);
		tessellator9.addVertexWithUV(d21, d5 + 1.0D, d27, d15, d17);
		tessellator9.addVertexWithUV(d21, d5 + 1.0D, d27, d13, d17);
		tessellator9.addVertexWithUV(d21, d5 + 0.0D, d27, d13, d19);
		tessellator9.addVertexWithUV(d23, d5 + 0.0D, d27, d15, d19);
		tessellator9.addVertexWithUV(d23, d5 + 1.0D, d27, d15, d17);
	}

	public boolean renderBlockFluids(Block block1, int i2, int i3, int i4) {
		Tessellator tessellator5 = Tessellator.instance;
		int i6 = block1.colorMultiplier(this.blockAccess, i2, i3, i4);
		float f7 = (float)(i6 >> 16 & 255) / 255.0F;
		float f8 = (float)(i6 >> 8 & 255) / 255.0F;
		float f9 = (float)(i6 & 255) / 255.0F;
		boolean z10 = block1.shouldSideBeRendered(this.blockAccess, i2, i3 + 1, i4, 1);
		boolean z11 = block1.shouldSideBeRendered(this.blockAccess, i2, i3 - 1, i4, 0);
		boolean[] z12 = new boolean[]{block1.shouldSideBeRendered(this.blockAccess, i2, i3, i4 - 1, 2), block1.shouldSideBeRendered(this.blockAccess, i2, i3, i4 + 1, 3), block1.shouldSideBeRendered(this.blockAccess, i2 - 1, i3, i4, 4), block1.shouldSideBeRendered(this.blockAccess, i2 + 1, i3, i4, 5)};
		if(!z10 && !z11 && !z12[0] && !z12[1] && !z12[2] && !z12[3]) {
			return false;
		} else {
			boolean z13 = false;
			float f14 = 0.5F;
			float f15 = 1.0F;
			float f16 = 0.8F;
			float f17 = 0.6F;
			double d18 = 0.0D;
			double d20 = 1.0D;
			Material material22 = block1.blockMaterial;
			int armorValue = this.blockAccess.getBlockMetadata(i2, i3, i4);
			
			double d24 = (double)this.getFluidHeight(i2, i3, i4, material22);
			double d26 = (double)this.getFluidHeight(i2, i3, i4 + 1, material22);
			double d28 = (double)this.getFluidHeight(i2 + 1, i3, i4 + 1, material22);
			double d30 = (double)this.getFluidHeight(i2 + 1, i3, i4, material22);

			double d32 = 0.0010000000474974513D;
			int i34;
			int i37;
			if(this.renderAllFaces || z10) {
				z13 = true;
				i34 = block1.getBlockTextureFromSideAndMetadata(1, armorValue);
				float f35 = (float)BlockFluid.func_293_a(this.blockAccess, i2, i3, i4, material22);
				if(f35 > -999.0F) {
					i34 = block1.getBlockTextureFromSideAndMetadata(2, armorValue);
				}

				d24 -= d32;
				d26 -= d32;
				d28 -= d32;
				d30 -= d32;

				int i36 = (i34 & 15) << 4;
				    i37 = i34 & 0xff0;

				double uCenter, vCenter;
				
				if(f35 < -999.0F) {
					f35 = 0.0F;
					uCenter = ((double)i36 + 8.0D) / TextureAtlasSize.w;
					vCenter = ((double)i37 + 8.0D) / TextureAtlasSize.h;
				} else {
					uCenter = (double)((float)(i36 + 16) / TextureAtlasSize.w);
					vCenter = (double)((float)(i37 + 16) / TextureAtlasSize.h);
				}

				double d42 = (double)(MathHelper.sin(f35) * 8.0F);
				double d44 = (double)(MathHelper.cos(f35) * 8.0F);

				double u1 = (d44 - d42) / TextureAtlasSize.w;
				double u2 = (d44 + d42) / TextureAtlasSize.w;
				double v1 = (d44 - d42) / TextureAtlasSize.h;
				double v2 = (d44 + d42) / TextureAtlasSize.h;

				tessellator5.setBrightness(block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4));
				float f46 = 1.0F;
				tessellator5.setColorOpaque_F(f15 * f46 * f7, f15 * f46 * f8, f15 * f46 * f9);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)i3 + d24, (double)(i4 + 0), uCenter - u2, vCenter - v1);
				tessellator5.addVertexWithUV((double)(i2 + 0), (double)i3 + d26, (double)(i4 + 1), uCenter - u1, vCenter + v2);
				tessellator5.addVertexWithUV((double)(i2 + 1), (double)i3 + d28, (double)(i4 + 1), uCenter + u2, vCenter + v1);
				tessellator5.addVertexWithUV((double)(i2 + 1), (double)i3 + d30, (double)(i4 + 0), uCenter + u1, vCenter - v2);
			}

			if(this.renderAllFaces || z11) {
				tessellator5.setBrightness(block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 - 1, i4));
				float f64 = 1.0F;
				tessellator5.setColorOpaque_F(f14 * f64 * f7, f14 * f64 * f8, f14 * f64 * f9);
				this.renderBottomFace(block1, (double)i2, (double)i3 + d32, (double)i4, block1.getBlockTextureFromSide(0));
				z13 = true;
			}

			for(i34 = 0; i34 < 4; ++i34) {
				int i65 = i2;
				i37 = i4;
				if(i34 == 0) {
					i37 = i4 - 1;
				}

				if(i34 == 1) {
					++i37;
				}

				if(i34 == 2) {
					i65 = i2 - 1;
				}

				if(i34 == 3) {
					++i65;
				}

				if(this.renderAllFaces || z12[i34]) {
					double d41;
					double d43;
					double d45;
					double d47;
					double d49;
					double d51;
					if(i34 == 0) {
						d41 = d24;
						d43 = d30;
						d45 = (double)i2;
						d49 = (double)(i2 + 1);
						d47 = (double)i4 + d32;
						d51 = (double)i4 + d32;
					} else if(i34 == 1) {
						d41 = d28;
						d43 = d26;
						d45 = (double)(i2 + 1);
						d49 = (double)i2;
						d47 = (double)(i4 + 1) - d32;
						d51 = (double)(i4 + 1) - d32;
					} else if(i34 == 2) {
						d41 = d26;
						d43 = d24;
						d45 = (double)i2 + d32;
						d49 = (double)i2 + d32;
						d47 = (double)(i4 + 1);
						d51 = (double)i4;
					} else {
						d41 = d30;
						d43 = d28;
						d45 = (double)(i2 + 1) - d32;
						d49 = (double)(i2 + 1) - d32;
						d47 = (double)i4;
						d51 = (double)(i4 + 1);
					}

					z13 = true;
					int i66 = block1.getBlockTextureFromSideAndMetadata(i34 + 2, armorValue);
					
					int i39 = (i66 & 15) << 4;
					int i67 = i66 & 0xff0;

					double d53 = (double)((float)(i39 + 0) / TextureAtlasSize.w);
					double d55 = ((double)(i39 + 16) - 0.01D) / TextureAtlasSize.w;
					double d57 = ((double)i67 + (1.0D - d41) * 16.0D) / TextureAtlasSize.h;
					double d59 = ((double)i67 + (1.0D - d43) * 16.0D) / TextureAtlasSize.h;
					double d61 = ((double)(i67 + 16) - 0.01D) / TextureAtlasSize.h;
					
					tessellator5.setBrightness(block1.getMixedBrightnessForBlock(this.blockAccess, i65, i3, i37));
					float f63 = 1.0F;
					if(i34 < 2) {
						f63 *= f16;
					} else {
						f63 *= f17;
					}

					tessellator5.setColorOpaque_F(f15 * f63 * f7, f15 * f63 * f8, f15 * f63 * f9);
					tessellator5.addVertexWithUV(d45, (double)i3 + d41, d47, d53, d57);
					tessellator5.addVertexWithUV(d49, (double)i3 + d43, d51, d55, d59);
					tessellator5.addVertexWithUV(d49, (double)(i3 + 0), d51, d55, d61);
					tessellator5.addVertexWithUV(d45, (double)(i3 + 0), d47, d53, d61);
				}
			}

			block1.minY = d18;
			block1.maxY = d20;
			return z13;
		}
	}

	private float getFluidHeight(int i1, int i2, int i3, Material material4) {
		int i5 = 0;
		float f6 = 0.0F;

		for(int i7 = 0; i7 < 4; ++i7) {
			int i8 = i1 - (i7 & 1);
			int i10 = i3 - (i7 >> 1 & 1);
			if(this.blockAccess.getBlockMaterial(i8, i2 + 1, i10) == material4) {
				return 1.0F;
			}

			Material material11 = this.blockAccess.getBlockMaterial(i8, i2, i10);
			if(material11 != material4) {
				if(!material11.isSolid()) {
					++f6;
					++i5;
				}
			} else {
				int i12 = this.blockAccess.getBlockMetadata(i8, i2, i10);
				if(i12 >= 8 || i12 == 0) {
					f6 += BlockFluid.getFluidHeightPercent(i12) * 10.0F;
					i5 += 10;
				}

				f6 += BlockFluid.getFluidHeightPercent(i12);
				++i5;
			}
		}

		return 1.0F - f6 / (float)i5;
	}

	public void renderBlockFallingSand(Block block1, World world2, int i3, int i4, int i5) {
		float f6 = 0.5F;
		float f7 = 1.0F;
		float f8 = 0.8F;
		float f9 = 0.6F;
		Tessellator tessellator10 = Tessellator.instance;
		tessellator10.startDrawingQuads();
		tessellator10.setBrightness(block1.getMixedBrightnessForBlock(world2, i3, i4, i5));
		float f11 = 1.0F;
		float f12 = 1.0F;
		if(f12 < f11) {
			f12 = f11;
		}

		tessellator10.setColorOpaque_F(f6 * f12, f6 * f12, f6 * f12);
		this.renderBottomFace(block1, -0.5D, -0.5D, -0.5D, block1.getBlockTextureFromSide(0));
		f12 = 1.0F;
		if(f12 < f11) {
			f12 = f11;
		}

		tessellator10.setColorOpaque_F(f7 * f12, f7 * f12, f7 * f12);
		this.renderTopFace(block1, -0.5D, -0.5D, -0.5D, block1.getBlockTextureFromSide(1));
		f12 = 1.0F;
		if(f12 < f11) {
			f12 = f11;
		}

		tessellator10.setColorOpaque_F(f8 * f12, f8 * f12, f8 * f12);
		this.renderEastFace(block1, -0.5D, -0.5D, -0.5D, block1.getBlockTextureFromSide(2));
		f12 = 1.0F;
		if(f12 < f11) {
			f12 = f11;
		}

		tessellator10.setColorOpaque_F(f8 * f12, f8 * f12, f8 * f12);
		this.renderWestFace(block1, -0.5D, -0.5D, -0.5D, block1.getBlockTextureFromSide(3));
		f12 = 1.0F;
		if(f12 < f11) {
			f12 = f11;
		}

		tessellator10.setColorOpaque_F(f9 * f12, f9 * f12, f9 * f12);
		this.renderNorthFace(block1, -0.5D, -0.5D, -0.5D, block1.getBlockTextureFromSide(4));
		f12 = 1.0F;
		if(f12 < f11) {
			f12 = f11;
		}

		tessellator10.setColorOpaque_F(f9 * f12, f9 * f12, f9 * f12);
		this.renderSouthFace(block1, -0.5D, -0.5D, -0.5D, block1.getBlockTextureFromSide(5));
		tessellator10.draw();
	}

	public boolean renderStandardBlock(Block block1, int i2, int i3, int i4) {
		int i5 = block1.colorMultiplier(this.blockAccess, i2, i3, i4);
		float f6 = (float)(i5 >> 16 & 255) / 255.0F;
		float f7 = (float)(i5 >> 8 & 255) / 255.0F;
		float f8 = (float)(i5 & 255) / 255.0F;

		return Minecraft.isAmbientOcclusionEnabled() && Block.lightValue[block1.blockID] == 0 ? 
				this.renderStandardBlockWithAmbientOcclusion(block1, i2, i3, i4, f6, f7, f8) 
			: 
				this.renderStandardBlockWithColorMultiplier(block1, i2, i3, i4, f6, f7, f8);
	}
	
	public boolean renderLog(Block block, int x, int y, int z) {
		int meta = this.blockAccess.getBlockMetadata(x, y, z);
		int orientation = GameRules.boolRule("renderAllBlocksStraight") ? 0 : (meta & 12);

		if (orientation == 4) {
			this.uvRotateEast = 1;
			this.uvRotateWest = 1;
			this.uvRotateTop = 1;
			this.uvRotateBottom = 1;
		} else if (orientation == 8) {
			this.uvRotateSouth = 1;
			this.uvRotateNorth = 1;
		}

		boolean result = this.renderStandardBlock(block, x, y, z);
		this.uvRotateSouth = 0;
		this.uvRotateEast = 0;
		this.uvRotateWest = 0;
		this.uvRotateNorth = 0;
		this.uvRotateTop = 0;
		this.uvRotateBottom = 0;
		return result;
	}

	public boolean renderStandardBlockWithAmbientOcclusion(Block block1, int i2, int i3, int i4, float f5, float f6, float f7) {
		this.enableAO = true;
		boolean z8 = false;
		float f9 = this.lightValueOwn;
		float f10 = this.lightValueOwn;
		float f11 = this.lightValueOwn;
		float f12 = this.lightValueOwn;
		boolean z13 = true;
		boolean z14 = true;
		boolean z15 = true;
		boolean z16 = true;
		boolean z17 = true;
		boolean z18 = true;
		this.lightValueOwn = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3, i4);
		this.aoLightValueXNeg = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 - 1, i3, i4);
		this.aoLightValueYNeg = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3 - 1, i4);
		this.aoLightValueZNeg = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3, i4 - 1);
		this.aoLightValueXPos = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 + 1, i3, i4);
		this.aoLightValueYPos = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3 + 1, i4);
		this.aoLightValueZPos = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3, i4 + 1);
		int i19 = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4);
		int i20 = i19;
		int i21 = i19;
		int i22 = i19;
		int armorValue = i19;
		int i24 = i19;
		int i25 = i19;
		if(block1.minY <= 0.0D) {
			i21 = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 - 1, i4);
		}

		if(block1.maxY >= 1.0D) {
			i24 = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 + 1, i4);
		}

		if(block1.minX <= 0.0D) {
			i20 = block1.getMixedBrightnessForBlock(this.blockAccess, i2 - 1, i3, i4);
		}

		if(block1.maxX >= 1.0D) {
			armorValue = block1.getMixedBrightnessForBlock(this.blockAccess, i2 + 1, i3, i4);
		}

		if(block1.minZ <= 0.0D) {
			i22 = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4 - 1);
		}

		if(block1.maxZ >= 1.0D) {
			i25 = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4 + 1);
		}

		Tessellator tessellator26 = Tessellator.instance;
		tessellator26.setBrightness(983055);
		this.aoGrassXYZPPC = Block.canBlockGrass[this.blockAccess.getBlockId(i2 + 1, i3 + 1, i4)];
		this.aoGrassXYZPNC = Block.canBlockGrass[this.blockAccess.getBlockId(i2 + 1, i3 - 1, i4)];
		this.aoGrassXYZPCP = Block.canBlockGrass[this.blockAccess.getBlockId(i2 + 1, i3, i4 + 1)];
		this.aoGrassXYZPCN = Block.canBlockGrass[this.blockAccess.getBlockId(i2 + 1, i3, i4 - 1)];
		this.aoGrassXYZNPC = Block.canBlockGrass[this.blockAccess.getBlockId(i2 - 1, i3 + 1, i4)];
		this.aoGrassXYZNNC = Block.canBlockGrass[this.blockAccess.getBlockId(i2 - 1, i3 - 1, i4)];
		this.aoGrassXYZNCN = Block.canBlockGrass[this.blockAccess.getBlockId(i2 - 1, i3, i4 - 1)];
		this.aoGrassXYZNCP = Block.canBlockGrass[this.blockAccess.getBlockId(i2 - 1, i3, i4 + 1)];
		this.aoGrassXYZCPP = Block.canBlockGrass[this.blockAccess.getBlockId(i2, i3 + 1, i4 + 1)];
		this.aoGrassXYZCPN = Block.canBlockGrass[this.blockAccess.getBlockId(i2, i3 + 1, i4 - 1)];
		this.aoGrassXYZCNP = Block.canBlockGrass[this.blockAccess.getBlockId(i2, i3 - 1, i4 + 1)];
		this.aoGrassXYZCNN = Block.canBlockGrass[this.blockAccess.getBlockId(i2, i3 - 1, i4 - 1)];
		if(block1.blockIndexInTexture == 3) {
			z18 = false;
			z17 = false;
			z16 = false;
			z15 = false;
			z13 = false;
		}

		if(this.overrideBlockTexture >= 0) {
			z18 = false;
			z17 = false;
			z16 = false;
			z15 = false;
			z13 = false;
		}

		if(this.renderAllFaces || block1.shouldSideBeRendered(this.blockAccess, i2, i3 - 1, i4, 0)) {
			if(this.aoType > 0) {
				if(block1.minY <= 0.0D) {
					--i3;
				}

				this.aoBrightnessXYNN = block1.getMixedBrightnessForBlock(this.blockAccess, i2 - 1, i3, i4);
				this.aoBrightnessYZNN = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4 - 1);
				this.aoBrightnessYZNP = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4 + 1);
				this.aoBrightnessXYPN = block1.getMixedBrightnessForBlock(this.blockAccess, i2 + 1, i3, i4);
				this.aoLightValueScratchXYNN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 - 1, i3, i4);
				this.aoLightValueScratchYZNN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3, i4 - 1);
				this.aoLightValueScratchYZNP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3, i4 + 1);
				this.aoLightValueScratchXYPN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 + 1, i3, i4);
				if(!this.aoGrassXYZCNN && !this.aoGrassXYZNNC) {
					this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXYNN;
					this.aoBrightnessXYZNNN = this.aoBrightnessXYNN;
				} else {
					this.aoLightValueScratchXYZNNN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 - 1, i3, i4 - 1);
					this.aoBrightnessXYZNNN = block1.getMixedBrightnessForBlock(this.blockAccess, i2 - 1, i3, i4 - 1);
				}

				if(!this.aoGrassXYZCNP && !this.aoGrassXYZNNC) {
					this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXYNN;
					this.aoBrightnessXYZNNP = this.aoBrightnessXYNN;
				} else {
					this.aoLightValueScratchXYZNNP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 - 1, i3, i4 + 1);
					this.aoBrightnessXYZNNP = block1.getMixedBrightnessForBlock(this.blockAccess, i2 - 1, i3, i4 + 1);
				}

				if(!this.aoGrassXYZCNN && !this.aoGrassXYZPNC) {
					this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXYPN;
					this.aoBrightnessXYZPNN = this.aoBrightnessXYPN;
				} else {
					this.aoLightValueScratchXYZPNN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 + 1, i3, i4 - 1);
					this.aoBrightnessXYZPNN = block1.getMixedBrightnessForBlock(this.blockAccess, i2 + 1, i3, i4 - 1);
				}

				if(!this.aoGrassXYZCNP && !this.aoGrassXYZPNC) {
					this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXYPN;
					this.aoBrightnessXYZPNP = this.aoBrightnessXYPN;
				} else {
					this.aoLightValueScratchXYZPNP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 + 1, i3, i4 + 1);
					this.aoBrightnessXYZPNP = block1.getMixedBrightnessForBlock(this.blockAccess, i2 + 1, i3, i4 + 1);
				}

				if(block1.minY <= 0.0D) {
					++i3;
				}

				f9 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXYNN + this.aoLightValueScratchYZNP + this.aoLightValueYNeg) / 4.0F;
				f12 = (this.aoLightValueScratchYZNP + this.aoLightValueYNeg + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXYPN) / 4.0F;
				f11 = (this.aoLightValueYNeg + this.aoLightValueScratchYZNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNN) / 4.0F;
				f10 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNN + this.aoLightValueYNeg + this.aoLightValueScratchYZNN) / 4.0F;
				this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXYNN, this.aoBrightnessYZNP, i21);
				this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXYPN, i21);
				this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYPN, this.aoBrightnessXYZPNN, i21);
				this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNN, this.aoBrightnessYZNN, i21);
			} else {
				f12 = this.aoLightValueYNeg;
				f11 = this.aoLightValueYNeg;
				f10 = this.aoLightValueYNeg;
				f9 = this.aoLightValueYNeg;
				this.brightnessTopLeft = this.brightnessBottomLeft = this.brightnessBottomRight = this.brightnessTopRight = this.aoBrightnessXYNN;
			}

			this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = (z13 ? f5 : 1.0F) * 0.5F;
			this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = (z13 ? f6 : 1.0F) * 0.5F;
			this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = (z13 ? f7 : 1.0F) * 0.5F;
			this.colorRedTopLeft *= f9;
			this.colorGreenTopLeft *= f9;
			this.colorBlueTopLeft *= f9;
			this.colorRedBottomLeft *= f10;
			this.colorGreenBottomLeft *= f10;
			this.colorBlueBottomLeft *= f10;
			this.colorRedBottomRight *= f11;
			this.colorGreenBottomRight *= f11;
			this.colorBlueBottomRight *= f11;
			this.colorRedTopRight *= f12;
			this.colorGreenTopRight *= f12;
			this.colorBlueTopRight *= f12;
			this.renderBottomFace(block1, (double)i2, (double)i3, (double)i4, block1.getBlockTexture(this.blockAccess, i2, i3, i4, 0));
			z8 = true;
		}

		if(this.renderAllFaces || block1.shouldSideBeRendered(this.blockAccess, i2, i3 + 1, i4, 1)) {
			if(this.aoType > 0) {
				if(block1.maxY >= 1.0D) {
					++i3;
				}

				this.aoBrightnessXYNP = block1.getMixedBrightnessForBlock(this.blockAccess, i2 - 1, i3, i4);
				this.aoBrightnessXYPP = block1.getMixedBrightnessForBlock(this.blockAccess, i2 + 1, i3, i4);
				this.aoBrightnessYZPN = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4 - 1);
				this.aoBrightnessYZPP = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4 + 1);
				this.aoLightValueScratchXYNP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 - 1, i3, i4);
				this.aoLightValueScratchXYPP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 + 1, i3, i4);
				this.aoLightValueScratchYZPN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3, i4 - 1);
				this.aoLightValueScratchYZPP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3, i4 + 1);
				if(!this.aoGrassXYZCPN && !this.aoGrassXYZNPC) {
					this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXYNP;
					this.aoBrightnessXYZNPN = this.aoBrightnessXYNP;
				} else {
					this.aoLightValueScratchXYZNPN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 - 1, i3, i4 - 1);
					this.aoBrightnessXYZNPN = block1.getMixedBrightnessForBlock(this.blockAccess, i2 - 1, i3, i4 - 1);
				}

				if(!this.aoGrassXYZCPN && !this.aoGrassXYZPPC) {
					this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXYPP;
					this.aoBrightnessXYZPPN = this.aoBrightnessXYPP;
				} else {
					this.aoLightValueScratchXYZPPN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 + 1, i3, i4 - 1);
					this.aoBrightnessXYZPPN = block1.getMixedBrightnessForBlock(this.blockAccess, i2 + 1, i3, i4 - 1);
				}

				if(!this.aoGrassXYZCPP && !this.aoGrassXYZNPC) {
					this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXYNP;
					this.aoBrightnessXYZNPP = this.aoBrightnessXYNP;
				} else {
					this.aoLightValueScratchXYZNPP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 - 1, i3, i4 + 1);
					this.aoBrightnessXYZNPP = block1.getMixedBrightnessForBlock(this.blockAccess, i2 - 1, i3, i4 + 1);
				}

				if(!this.aoGrassXYZCPP && !this.aoGrassXYZPPC) {
					this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXYPP;
					this.aoBrightnessXYZPPP = this.aoBrightnessXYPP;
				} else {
					this.aoLightValueScratchXYZPPP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 + 1, i3, i4 + 1);
					this.aoBrightnessXYZPPP = block1.getMixedBrightnessForBlock(this.blockAccess, i2 + 1, i3, i4 + 1);
				}

				if(block1.maxY >= 1.0D) {
					--i3;
				}

				f12 = (this.aoLightValueScratchXYZNPP + this.aoLightValueScratchXYNP + this.aoLightValueScratchYZPP + this.aoLightValueYPos) / 4.0F;
				f9 = (this.aoLightValueScratchYZPP + this.aoLightValueYPos + this.aoLightValueScratchXYZPPP + this.aoLightValueScratchXYPP) / 4.0F;
				f10 = (this.aoLightValueYPos + this.aoLightValueScratchYZPN + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPN) / 4.0F;
				f11 = (this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPN + this.aoLightValueYPos + this.aoLightValueScratchYZPN) / 4.0F;
				this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNPP, this.aoBrightnessXYNP, this.aoBrightnessYZPP, i24);
				this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXYZPPP, this.aoBrightnessXYPP, i24);
				this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXYPP, this.aoBrightnessXYZPPN, i24);
				this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYNP, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, i24);
			} else {
				f12 = this.aoLightValueYPos;
				f11 = this.aoLightValueYPos;
				f10 = this.aoLightValueYPos;
				f9 = this.aoLightValueYPos;
				this.brightnessTopLeft = this.brightnessBottomLeft = this.brightnessBottomRight = this.brightnessTopRight = i24;
			}

			this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = z14 ? f5 : 1.0F;
			this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = z14 ? f6 : 1.0F;
			this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = z14 ? f7 : 1.0F;
			this.colorRedTopLeft *= f9;
			this.colorGreenTopLeft *= f9;
			this.colorBlueTopLeft *= f9;
			this.colorRedBottomLeft *= f10;
			this.colorGreenBottomLeft *= f10;
			this.colorBlueBottomLeft *= f10;
			this.colorRedBottomRight *= f11;
			this.colorGreenBottomRight *= f11;
			this.colorBlueBottomRight *= f11;
			this.colorRedTopRight *= f12;
			this.colorGreenTopRight *= f12;
			this.colorBlueTopRight *= f12;
			this.renderTopFace(block1, (double)i2, (double)i3, (double)i4, block1.getBlockTexture(this.blockAccess, i2, i3, i4, 1));
			z8 = true;
		}

		int i27;
		if(this.renderAllFaces || block1.shouldSideBeRendered(this.blockAccess, i2, i3, i4 - 1, 2)) {
			if(this.aoType > 0) {
				if(block1.minZ <= 0.0D) {
					--i4;
				}

				this.aoLightValueScratchXZNN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 - 1, i3, i4);
				this.aoLightValueScratchYZNN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3 - 1, i4);
				this.aoLightValueScratchYZPN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3 + 1, i4);
				this.aoLightValueScratchXZPN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 + 1, i3, i4);
				this.aoBrightnessXZNN = block1.getMixedBrightnessForBlock(this.blockAccess, i2 - 1, i3, i4);
				this.aoBrightnessYZNN = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 - 1, i4);
				this.aoBrightnessYZPN = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 + 1, i4);
				this.aoBrightnessXZPN = block1.getMixedBrightnessForBlock(this.blockAccess, i2 + 1, i3, i4);
				if(!this.aoGrassXYZNCN && !this.aoGrassXYZCNN) {
					this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
					this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
				} else {
					this.aoLightValueScratchXYZNNN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 - 1, i3 - 1, i4);
					this.aoBrightnessXYZNNN = block1.getMixedBrightnessForBlock(this.blockAccess, i2 - 1, i3 - 1, i4);
				}

				if(!this.aoGrassXYZNCN && !this.aoGrassXYZCPN) {
					this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
					this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
				} else {
					this.aoLightValueScratchXYZNPN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 - 1, i3 + 1, i4);
					this.aoBrightnessXYZNPN = block1.getMixedBrightnessForBlock(this.blockAccess, i2 - 1, i3 + 1, i4);
				}

				if(!this.aoGrassXYZPCN && !this.aoGrassXYZCNN) {
					this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
					this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
				} else {
					this.aoLightValueScratchXYZPNN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 + 1, i3 - 1, i4);
					this.aoBrightnessXYZPNN = block1.getMixedBrightnessForBlock(this.blockAccess, i2 + 1, i3 - 1, i4);
				}

				if(!this.aoGrassXYZPCN && !this.aoGrassXYZCPN) {
					this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
					this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
				} else {
					this.aoLightValueScratchXYZPPN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 + 1, i3 + 1, i4);
					this.aoBrightnessXYZPPN = block1.getMixedBrightnessForBlock(this.blockAccess, i2 + 1, i3 + 1, i4);
				}

				if(block1.minZ <= 0.0D) {
					++i4;
				}

				f9 = (this.aoLightValueScratchXZNN + this.aoLightValueScratchXYZNPN + this.aoLightValueZNeg + this.aoLightValueScratchYZPN) / 4.0F;
				f10 = (this.aoLightValueZNeg + this.aoLightValueScratchYZPN + this.aoLightValueScratchXZPN + this.aoLightValueScratchXYZPPN) / 4.0F;
				f11 = (this.aoLightValueScratchYZNN + this.aoLightValueZNeg + this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXZPN) / 4.0F;
				f12 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXZNN + this.aoLightValueScratchYZNN + this.aoLightValueZNeg) / 4.0F;
				this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessYZPN, i22);
				this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessYZPN, this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, i22);
				this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNN, this.aoBrightnessXYZPNN, this.aoBrightnessXZPN, i22);
				this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXZNN, this.aoBrightnessYZNN, i22);
			} else {
				f12 = this.aoLightValueZNeg;
				f11 = this.aoLightValueZNeg;
				f10 = this.aoLightValueZNeg;
				f9 = this.aoLightValueZNeg;
				this.brightnessTopLeft = this.brightnessBottomLeft = this.brightnessBottomRight = this.brightnessTopRight = i22;
			}

			this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = (z15 ? f5 : 1.0F) * 0.8F;
			this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = (z15 ? f6 : 1.0F) * 0.8F;
			this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = (z15 ? f7 : 1.0F) * 0.8F;
			this.colorRedTopLeft *= f9;
			this.colorGreenTopLeft *= f9;
			this.colorBlueTopLeft *= f9;
			this.colorRedBottomLeft *= f10;
			this.colorGreenBottomLeft *= f10;
			this.colorBlueBottomLeft *= f10;
			this.colorRedBottomRight *= f11;
			this.colorGreenBottomRight *= f11;
			this.colorBlueBottomRight *= f11;
			this.colorRedTopRight *= f12;
			this.colorGreenTopRight *= f12;
			this.colorBlueTopRight *= f12;
			i27 = block1.getBlockTexture(this.blockAccess, i2, i3, i4, 2);
			this.renderEastFace(block1, (double)i2, (double)i3, (double)i4, i27);
			if(fancyGrass && i27 == 3 && this.overrideBlockTexture < 0) {
				this.colorRedTopLeft *= f5;
				this.colorRedBottomLeft *= f5;
				this.colorRedBottomRight *= f5;
				this.colorRedTopRight *= f5;
				this.colorGreenTopLeft *= f6;
				this.colorGreenBottomLeft *= f6;
				this.colorGreenBottomRight *= f6;
				this.colorGreenTopRight *= f6;
				this.colorBlueTopLeft *= f7;
				this.colorBlueBottomLeft *= f7;
				this.colorBlueBottomRight *= f7;
				this.colorBlueTopRight *= f7;
				this.renderEastFace(block1, (double)i2, (double)i3, (double)i4, 38);
			}

			z8 = true;
		}

		if(this.renderAllFaces || block1.shouldSideBeRendered(this.blockAccess, i2, i3, i4 + 1, 3)) {
			if(this.aoType > 0) {
				if(block1.maxZ >= 1.0D) {
					++i4;
				}

				this.aoLightValueScratchXZNP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 - 1, i3, i4);
				this.aoLightValueScratchXZPP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 + 1, i3, i4);
				this.aoLightValueScratchYZNP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3 - 1, i4);
				this.aoLightValueScratchYZPP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3 + 1, i4);
				this.aoBrightnessXZNP = block1.getMixedBrightnessForBlock(this.blockAccess, i2 - 1, i3, i4);
				this.aoBrightnessXZPP = block1.getMixedBrightnessForBlock(this.blockAccess, i2 + 1, i3, i4);
				this.aoBrightnessYZNP = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 - 1, i4);
				this.aoBrightnessYZPP = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 + 1, i4);
				if(!this.aoGrassXYZNCP && !this.aoGrassXYZCNP) {
					this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
					this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
				} else {
					this.aoLightValueScratchXYZNNP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 - 1, i3 - 1, i4);
					this.aoBrightnessXYZNNP = block1.getMixedBrightnessForBlock(this.blockAccess, i2 - 1, i3 - 1, i4);
				}

				if(!this.aoGrassXYZNCP && !this.aoGrassXYZCPP) {
					this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
					this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
				} else {
					this.aoLightValueScratchXYZNPP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 - 1, i3 + 1, i4);
					this.aoBrightnessXYZNPP = block1.getMixedBrightnessForBlock(this.blockAccess, i2 - 1, i3 + 1, i4);
				}

				if(!this.aoGrassXYZPCP && !this.aoGrassXYZCNP) {
					this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
					this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
				} else {
					this.aoLightValueScratchXYZPNP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 + 1, i3 - 1, i4);
					this.aoBrightnessXYZPNP = block1.getMixedBrightnessForBlock(this.blockAccess, i2 + 1, i3 - 1, i4);
				}

				if(!this.aoGrassXYZPCP && !this.aoGrassXYZCPP) {
					this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
					this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
				} else {
					this.aoLightValueScratchXYZPPP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2 + 1, i3 + 1, i4);
					this.aoBrightnessXYZPPP = block1.getMixedBrightnessForBlock(this.blockAccess, i2 + 1, i3 + 1, i4);
				}

				if(block1.maxZ >= 1.0D) {
					--i4;
				}

				f9 = (this.aoLightValueScratchXZNP + this.aoLightValueScratchXYZNPP + this.aoLightValueZPos + this.aoLightValueScratchYZPP) / 4.0F;
				f12 = (this.aoLightValueZPos + this.aoLightValueScratchYZPP + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYZPPP) / 4.0F;
				f11 = (this.aoLightValueScratchYZNP + this.aoLightValueZPos + this.aoLightValueScratchXYZPNP + this.aoLightValueScratchXZPP) / 4.0F;
				f10 = (this.aoLightValueScratchXYZNNP + this.aoLightValueScratchXZNP + this.aoLightValueScratchYZNP + this.aoLightValueZPos) / 4.0F;
				this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYZNPP, this.aoBrightnessYZPP, i25);
				this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessYZPP, this.aoBrightnessXZPP, this.aoBrightnessXYZPPP, i25);
				this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessYZNP, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, i25);
				this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, this.aoBrightnessYZNP, i25);
			} else {
				f12 = this.aoLightValueZPos;
				f11 = this.aoLightValueZPos;
				f10 = this.aoLightValueZPos;
				f9 = this.aoLightValueZPos;
				this.brightnessTopLeft = this.brightnessBottomLeft = this.brightnessBottomRight = this.brightnessTopRight = i25;
			}

			this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = (z16 ? f5 : 1.0F) * 0.8F;
			this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = (z16 ? f6 : 1.0F) * 0.8F;
			this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = (z16 ? f7 : 1.0F) * 0.8F;
			this.colorRedTopLeft *= f9;
			this.colorGreenTopLeft *= f9;
			this.colorBlueTopLeft *= f9;
			this.colorRedBottomLeft *= f10;
			this.colorGreenBottomLeft *= f10;
			this.colorBlueBottomLeft *= f10;
			this.colorRedBottomRight *= f11;
			this.colorGreenBottomRight *= f11;
			this.colorBlueBottomRight *= f11;
			this.colorRedTopRight *= f12;
			this.colorGreenTopRight *= f12;
			this.colorBlueTopRight *= f12;
			i27 = block1.getBlockTexture(this.blockAccess, i2, i3, i4, 3);
			this.renderWestFace(block1, (double)i2, (double)i3, (double)i4, block1.getBlockTexture(this.blockAccess, i2, i3, i4, 3));
			if(fancyGrass && i27 == 3 && this.overrideBlockTexture < 0) {
				this.colorRedTopLeft *= f5;
				this.colorRedBottomLeft *= f5;
				this.colorRedBottomRight *= f5;
				this.colorRedTopRight *= f5;
				this.colorGreenTopLeft *= f6;
				this.colorGreenBottomLeft *= f6;
				this.colorGreenBottomRight *= f6;
				this.colorGreenTopRight *= f6;
				this.colorBlueTopLeft *= f7;
				this.colorBlueBottomLeft *= f7;
				this.colorBlueBottomRight *= f7;
				this.colorBlueTopRight *= f7;
				this.renderWestFace(block1, (double)i2, (double)i3, (double)i4, 38);
			}

			z8 = true;
		}

		if(this.renderAllFaces || block1.shouldSideBeRendered(this.blockAccess, i2 - 1, i3, i4, 4)) {
			if(this.aoType > 0) {
				if(block1.minX <= 0.0D) {
					--i2;
				}

				this.aoLightValueScratchXYNN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3 - 1, i4);
				this.aoLightValueScratchXZNN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3, i4 - 1);
				this.aoLightValueScratchXZNP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3, i4 + 1);
				this.aoLightValueScratchXYNP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3 + 1, i4);
				this.aoBrightnessXYNN = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 - 1, i4);
				this.aoBrightnessXZNN = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4 - 1);
				this.aoBrightnessXZNP = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4 + 1);
				this.aoBrightnessXYNP = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 + 1, i4);
				if(!this.aoGrassXYZNCN && !this.aoGrassXYZNNC) {
					this.aoLightValueScratchXYZNNN = this.aoLightValueScratchXZNN;
					this.aoBrightnessXYZNNN = this.aoBrightnessXZNN;
				} else {
					this.aoLightValueScratchXYZNNN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3 - 1, i4 - 1);
					this.aoBrightnessXYZNNN = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 - 1, i4 - 1);
				}

				if(!this.aoGrassXYZNCP && !this.aoGrassXYZNNC) {
					this.aoLightValueScratchXYZNNP = this.aoLightValueScratchXZNP;
					this.aoBrightnessXYZNNP = this.aoBrightnessXZNP;
				} else {
					this.aoLightValueScratchXYZNNP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3 - 1, i4 + 1);
					this.aoBrightnessXYZNNP = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 - 1, i4 + 1);
				}

				if(!this.aoGrassXYZNCN && !this.aoGrassXYZNPC) {
					this.aoLightValueScratchXYZNPN = this.aoLightValueScratchXZNN;
					this.aoBrightnessXYZNPN = this.aoBrightnessXZNN;
				} else {
					this.aoLightValueScratchXYZNPN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3 + 1, i4 - 1);
					this.aoBrightnessXYZNPN = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 + 1, i4 - 1);
				}

				if(!this.aoGrassXYZNCP && !this.aoGrassXYZNPC) {
					this.aoLightValueScratchXYZNPP = this.aoLightValueScratchXZNP;
					this.aoBrightnessXYZNPP = this.aoBrightnessXZNP;
				} else {
					this.aoLightValueScratchXYZNPP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3 + 1, i4 + 1);
					this.aoBrightnessXYZNPP = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 + 1, i4 + 1);
				}

				if(block1.minX <= 0.0D) {
					++i2;
				}

				f12 = (this.aoLightValueScratchXYNN + this.aoLightValueScratchXYZNNP + this.aoLightValueXNeg + this.aoLightValueScratchXZNP) / 4.0F;
				f9 = (this.aoLightValueXNeg + this.aoLightValueScratchXZNP + this.aoLightValueScratchXYNP + this.aoLightValueScratchXYZNPP) / 4.0F;
				f10 = (this.aoLightValueScratchXZNN + this.aoLightValueXNeg + this.aoLightValueScratchXYZNPN + this.aoLightValueScratchXYNP) / 4.0F;
				f11 = (this.aoLightValueScratchXYZNNN + this.aoLightValueScratchXYNN + this.aoLightValueScratchXZNN + this.aoLightValueXNeg) / 4.0F;
				this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXYNN, this.aoBrightnessXYZNNP, this.aoBrightnessXZNP, i20);
				this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXZNP, this.aoBrightnessXYNP, this.aoBrightnessXYZNPP, i20);
				this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXZNN, this.aoBrightnessXYZNPN, this.aoBrightnessXYNP, i20);
				this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXYZNNN, this.aoBrightnessXYNN, this.aoBrightnessXZNN, i20);
			} else {
				f12 = this.aoLightValueXNeg;
				f11 = this.aoLightValueXNeg;
				f10 = this.aoLightValueXNeg;
				f9 = this.aoLightValueXNeg;
				this.brightnessTopLeft = this.brightnessBottomLeft = this.brightnessBottomRight = this.brightnessTopRight = i20;
			}

			this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = (z17 ? f5 : 1.0F) * 0.6F;
			this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = (z17 ? f6 : 1.0F) * 0.6F;
			this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = (z17 ? f7 : 1.0F) * 0.6F;
			this.colorRedTopLeft *= f9;
			this.colorGreenTopLeft *= f9;
			this.colorBlueTopLeft *= f9;
			this.colorRedBottomLeft *= f10;
			this.colorGreenBottomLeft *= f10;
			this.colorBlueBottomLeft *= f10;
			this.colorRedBottomRight *= f11;
			this.colorGreenBottomRight *= f11;
			this.colorBlueBottomRight *= f11;
			this.colorRedTopRight *= f12;
			this.colorGreenTopRight *= f12;
			this.colorBlueTopRight *= f12;
			i27 = block1.getBlockTexture(this.blockAccess, i2, i3, i4, 4);
			this.renderNorthFace(block1, (double)i2, (double)i3, (double)i4, i27);
			if(fancyGrass && i27 == 3 && this.overrideBlockTexture < 0) {
				this.colorRedTopLeft *= f5;
				this.colorRedBottomLeft *= f5;
				this.colorRedBottomRight *= f5;
				this.colorRedTopRight *= f5;
				this.colorGreenTopLeft *= f6;
				this.colorGreenBottomLeft *= f6;
				this.colorGreenBottomRight *= f6;
				this.colorGreenTopRight *= f6;
				this.colorBlueTopLeft *= f7;
				this.colorBlueBottomLeft *= f7;
				this.colorBlueBottomRight *= f7;
				this.colorBlueTopRight *= f7;
				this.renderNorthFace(block1, (double)i2, (double)i3, (double)i4, 38);
			}

			z8 = true;
		}

		if(this.renderAllFaces || block1.shouldSideBeRendered(this.blockAccess, i2 + 1, i3, i4, 5)) {
			if(this.aoType > 0) {
				if(block1.maxX >= 1.0D) {
					++i2;
				}

				this.aoLightValueScratchXYPN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3 - 1, i4);
				this.aoLightValueScratchXZPN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3, i4 - 1);
				this.aoLightValueScratchXZPP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3, i4 + 1);
				this.aoLightValueScratchXYPP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3 + 1, i4);
				this.aoBrightnessXYPN = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 - 1, i4);
				this.aoBrightnessXZPN = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4 - 1);
				this.aoBrightnessXZPP = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4 + 1);
				this.aoBrightnessXYPP = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 + 1, i4);
				if(!this.aoGrassXYZPNC && !this.aoGrassXYZPCN) {
					this.aoLightValueScratchXYZPNN = this.aoLightValueScratchXZPN;
					this.aoBrightnessXYZPNN = this.aoBrightnessXZPN;
				} else {
					this.aoLightValueScratchXYZPNN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3 - 1, i4 - 1);
					this.aoBrightnessXYZPNN = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 - 1, i4 - 1);
				}

				if(!this.aoGrassXYZPNC && !this.aoGrassXYZPCP) {
					this.aoLightValueScratchXYZPNP = this.aoLightValueScratchXZPP;
					this.aoBrightnessXYZPNP = this.aoBrightnessXZPP;
				} else {
					this.aoLightValueScratchXYZPNP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3 - 1, i4 + 1);
					this.aoBrightnessXYZPNP = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 - 1, i4 + 1);
				}

				if(!this.aoGrassXYZPPC && !this.aoGrassXYZPCN) {
					this.aoLightValueScratchXYZPPN = this.aoLightValueScratchXZPN;
					this.aoBrightnessXYZPPN = this.aoBrightnessXZPN;
				} else {
					this.aoLightValueScratchXYZPPN = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3 + 1, i4 - 1);
					this.aoBrightnessXYZPPN = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 + 1, i4 - 1);
				}

				if(!this.aoGrassXYZPPC && !this.aoGrassXYZPCP) {
					this.aoLightValueScratchXYZPPP = this.aoLightValueScratchXZPP;
					this.aoBrightnessXYZPPP = this.aoBrightnessXZPP;
				} else {
					this.aoLightValueScratchXYZPPP = block1.getAmbientOcclusionLightValue(this.blockAccess, i2, i3 + 1, i4 + 1);
					this.aoBrightnessXYZPPP = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 + 1, i4 + 1);
				}

				if(block1.maxX >= 1.0D) {
					--i2;
				}

				f9 = (this.aoLightValueScratchXYPN + this.aoLightValueScratchXYZPNP + this.aoLightValueXPos + this.aoLightValueScratchXZPP) / 4.0F;
				f12 = (this.aoLightValueXPos + this.aoLightValueScratchXZPP + this.aoLightValueScratchXYPP + this.aoLightValueScratchXYZPPP) / 4.0F;
				f11 = (this.aoLightValueScratchXZPN + this.aoLightValueXPos + this.aoLightValueScratchXYZPPN + this.aoLightValueScratchXYPP) / 4.0F;
				f10 = (this.aoLightValueScratchXYZPNN + this.aoLightValueScratchXYPN + this.aoLightValueScratchXZPN + this.aoLightValueXPos) / 4.0F;
				this.brightnessTopLeft = this.getAoBrightness(this.aoBrightnessXYPN, this.aoBrightnessXYZPNP, this.aoBrightnessXZPP, armorValue);
				this.brightnessTopRight = this.getAoBrightness(this.aoBrightnessXZPP, this.aoBrightnessXYPP, this.aoBrightnessXYZPPP, armorValue);
				this.brightnessBottomRight = this.getAoBrightness(this.aoBrightnessXZPN, this.aoBrightnessXYZPPN, this.aoBrightnessXYPP, armorValue);
				this.brightnessBottomLeft = this.getAoBrightness(this.aoBrightnessXYZPNN, this.aoBrightnessXYPN, this.aoBrightnessXZPN, armorValue);
			} else {
				f12 = this.aoLightValueXPos;
				f11 = this.aoLightValueXPos;
				f10 = this.aoLightValueXPos;
				f9 = this.aoLightValueXPos;
				this.brightnessTopLeft = this.brightnessBottomLeft = this.brightnessBottomRight = this.brightnessTopRight = armorValue;
			}

			this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = (z18 ? f5 : 1.0F) * 0.6F;
			this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = (z18 ? f6 : 1.0F) * 0.6F;
			this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = (z18 ? f7 : 1.0F) * 0.6F;
			this.colorRedTopLeft *= f9;
			this.colorGreenTopLeft *= f9;
			this.colorBlueTopLeft *= f9;
			this.colorRedBottomLeft *= f10;
			this.colorGreenBottomLeft *= f10;
			this.colorBlueBottomLeft *= f10;
			this.colorRedBottomRight *= f11;
			this.colorGreenBottomRight *= f11;
			this.colorBlueBottomRight *= f11;
			this.colorRedTopRight *= f12;
			this.colorGreenTopRight *= f12;
			this.colorBlueTopRight *= f12;
			i27 = block1.getBlockTexture(this.blockAccess, i2, i3, i4, 5);
			this.renderSouthFace(block1, (double)i2, (double)i3, (double)i4, i27);
			if(fancyGrass && i27 == 3 && this.overrideBlockTexture < 0) {
				this.colorRedTopLeft *= f5;
				this.colorRedBottomLeft *= f5;
				this.colorRedBottomRight *= f5;
				this.colorRedTopRight *= f5;
				this.colorGreenTopLeft *= f6;
				this.colorGreenBottomLeft *= f6;
				this.colorGreenBottomRight *= f6;
				this.colorGreenTopRight *= f6;
				this.colorBlueTopLeft *= f7;
				this.colorBlueBottomLeft *= f7;
				this.colorBlueBottomRight *= f7;
				this.colorBlueTopRight *= f7;
				this.renderSouthFace(block1, (double)i2, (double)i3, (double)i4, 38);
			}

			z8 = true;
		}

		this.enableAO = false;
		return z8;
	}

	private int getAoBrightness(int i1, int i2, int i3, int i4) {
		if(i1 == 0) {
			i1 = i4;
		}

		if(i2 == 0) {
			i2 = i4;
		}

		if(i3 == 0) {
			i3 = i4;
		}

		return i1 + i2 + i3 + i4 >> 2 & 16711935;
	}

	public boolean renderStandardBlockWithColorMultiplier(Block block1, int i2, int i3, int i4, float f5, float f6, float f7) {
		this.enableAO = false;
		Tessellator tessellator8 = Tessellator.instance;
		boolean z9 = false;
		float f10 = 0.5F;
		float f11 = 1.0F;
		float f12 = 0.8F;
		float f13 = 0.6F;
		float f14 = f11 * f5;
		float f15 = f11 * f6;
		float f16 = f11 * f7;
		float f17 = f10;
		float f18 = f12;
		float f19 = f13;
		float f20 = f10;
		float f21 = f12;
		float f22 = f13;
		float f23 = f10;
		float f24 = f12;
		float f25 = f13;
		if(block1 != Block.grass) {
			f17 = f10 * f5;
			f18 = f12 * f5;
			f19 = f13 * f5;
			f20 = f10 * f6;
			f21 = f12 * f6;
			f22 = f13 * f6;
			f23 = f10 * f7;
			f24 = f12 * f7;
			f25 = f13 * f7;
		}

		int i26 = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4);
		if(this.renderAllFaces || block1.shouldSideBeRendered(this.blockAccess, i2, i3 - 1, i4, 0)) {
			tessellator8.setBrightness(block1.minY > 0.0D ? i26 : block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 - 1, i4));
			tessellator8.setColorOpaque_F(f17, f20, f23);
			this.renderBottomFace(block1, (double)i2, (double)i3, (double)i4, block1.getBlockTexture(this.blockAccess, i2, i3, i4, 0));
			z9 = true;
		}

		if(this.renderAllFaces || block1.shouldSideBeRendered(this.blockAccess, i2, i3 + 1, i4, 1)) {
			tessellator8.setBrightness(block1.maxY < 1.0D ? i26 : block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 + 1, i4));
			tessellator8.setColorOpaque_F(f14, f15, f16);
			this.renderTopFace(block1, (double)i2, (double)i3, (double)i4, block1.getBlockTexture(this.blockAccess, i2, i3, i4, 1));
			z9 = true;
		}

		int i28;
		if(this.renderAllFaces || block1.shouldSideBeRendered(this.blockAccess, i2, i3, i4 - 1, 2)) {
			tessellator8.setBrightness(block1.minZ > 0.0D ? i26 : block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4 - 1));
			tessellator8.setColorOpaque_F(f18, f21, f24);
			i28 = block1.getBlockTexture(this.blockAccess, i2, i3, i4, 2);
			this.renderEastFace(block1, (double)i2, (double)i3, (double)i4, i28);
			if(fancyGrass && i28 == 3 && this.overrideBlockTexture < 0) {
				tessellator8.setColorOpaque_F(f18 * f5, f21 * f6, f24 * f7);
				this.renderEastFace(block1, (double)i2, (double)i3, (double)i4, 38);
			}

			z9 = true;
		}

		if(this.renderAllFaces || block1.shouldSideBeRendered(this.blockAccess, i2, i3, i4 + 1, 3)) {
			tessellator8.setBrightness(block1.maxZ < 1.0D ? i26 : block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4 + 1));
			tessellator8.setColorOpaque_F(f18, f21, f24);
			i28 = block1.getBlockTexture(this.blockAccess, i2, i3, i4, 3);
			this.renderWestFace(block1, (double)i2, (double)i3, (double)i4, i28);
			if(fancyGrass && i28 == 3 && this.overrideBlockTexture < 0) {
				tessellator8.setColorOpaque_F(f18 * f5, f21 * f6, f24 * f7);
				this.renderWestFace(block1, (double)i2, (double)i3, (double)i4, 38);
			}

			z9 = true;
		}

		if(this.renderAllFaces || block1.shouldSideBeRendered(this.blockAccess, i2 - 1, i3, i4, 4)) {
			tessellator8.setBrightness(block1.minX > 0.0D ? i26 : block1.getMixedBrightnessForBlock(this.blockAccess, i2 - 1, i3, i4));
			tessellator8.setColorOpaque_F(f19, f22, f25);
			i28 = block1.getBlockTexture(this.blockAccess, i2, i3, i4, 4);
			this.renderNorthFace(block1, (double)i2, (double)i3, (double)i4, i28);
			if(fancyGrass && i28 == 3 && this.overrideBlockTexture < 0) {
				tessellator8.setColorOpaque_F(f19 * f5, f22 * f6, f25 * f7);
				this.renderNorthFace(block1, (double)i2, (double)i3, (double)i4, 38);
			}

			z9 = true;
		}

		if(this.renderAllFaces || block1.shouldSideBeRendered(this.blockAccess, i2 + 1, i3, i4, 5)) {
			tessellator8.setBrightness(block1.maxX < 1.0D ? i26 : block1.getMixedBrightnessForBlock(this.blockAccess, i2 + 1, i3, i4));
			tessellator8.setColorOpaque_F(f19, f22, f25);
			i28 = block1.getBlockTexture(this.blockAccess, i2, i3, i4, 5);
			this.renderSouthFace(block1, (double)i2, (double)i3, (double)i4, i28);
			if(fancyGrass && i28 == 3 && this.overrideBlockTexture < 0) {
				tessellator8.setColorOpaque_F(f19 * f5, f22 * f6, f25 * f7);
				this.renderSouthFace(block1, (double)i2, (double)i3, (double)i4, 38);
			}

			z9 = true;
		}

		return z9;
	}

	private boolean renderBlockRepeater(Block block1, int i2, int i3, int i4) {
		int i5 = this.blockAccess.getBlockMetadata(i2, i3, i4);
		int i6 = i5 & 3;
		int i7 = (i5 & 12) >> 2;
		this.renderStandardBlock(block1, i2, i3, i4);
		Tessellator tessellator8 = Tessellator.instance;
		tessellator8.setBrightness(block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4));
		tessellator8.setColorOpaque_F(1.0F, 1.0F, 1.0F);
		double d9 = -0.1875D;
		double d11 = 0.0D;
		double d13 = 0.0D;
		double d15 = 0.0D;
		double d17 = 0.0D;
		switch(i6) {
		case 0:
			d17 = -0.3125D;
			d13 = BlockRedstoneRepeater.repeaterTorchOffset[i7];
			break;
		case 1:
			d15 = 0.3125D;
			d11 = -BlockRedstoneRepeater.repeaterTorchOffset[i7];
			break;
		case 2:
			d17 = 0.3125D;
			d13 = -BlockRedstoneRepeater.repeaterTorchOffset[i7];
			break;
		case 3:
			d15 = -0.3125D;
			d11 = BlockRedstoneRepeater.repeaterTorchOffset[i7];
		}

		this.renderTorchAtAngle(block1, (double)i2 + d11, (double)i3 + d9, (double)i4 + d13, 0.0D, 0.0D);
		this.renderTorchAtAngle(block1, (double)i2 + d15, (double)i3 + d9, (double)i4 + d17, 0.0D, 0.0D);
		int i19 = block1.getBlockTextureFromSide(1);
		
		Idx2uvF.calc(i19);
		double u1 = Idx2uvF.u1;
		double u2 = Idx2uvF.u2;
		double v1 = Idx2uvF.v1;
		double v2 = Idx2uvF.v2;

		double d30 = 0.125D;
		double d32 = (double)(i2 + 1);
		double d34 = (double)(i2 + 1);
		double d36 = (double)(i2 + 0);
		double d38 = (double)(i2 + 0);
		double d40 = (double)(i4 + 0);
		double d42 = (double)(i4 + 1);
		double d44 = (double)(i4 + 1);
		double d46 = (double)(i4 + 0);
		double d48 = (double)i3 + d30;
		if(i6 == 2) {
			d32 = d34 = (double)(i2 + 0);
			d36 = d38 = (double)(i2 + 1);
			d40 = d46 = (double)(i4 + 1);
			d42 = d44 = (double)(i4 + 0);
		} else if(i6 == 3) {
			d32 = d38 = (double)(i2 + 0);
			d34 = d36 = (double)(i2 + 1);
			d40 = d42 = (double)(i4 + 0);
			d44 = d46 = (double)(i4 + 1);
		} else if(i6 == 1) {
			d32 = d38 = (double)(i2 + 1);
			d34 = d36 = (double)(i2 + 0);
			d40 = d42 = (double)(i4 + 1);
			d44 = d46 = (double)(i4 + 0);
		}

		tessellator8.addVertexWithUV(d38, d48, d46, u1, v1);
		tessellator8.addVertexWithUV(d36, d48, d44, u1, v2);
		tessellator8.addVertexWithUV(d34, d48, d42, u2, v2);
		tessellator8.addVertexWithUV(d32, d48, d40, u2, v1);
		return true;
	}
	
	public boolean renderBlockCactus(Block block1, int i2, int i3, int i4) {
		int i5 = block1.colorMultiplier(this.blockAccess, i2, i3, i4);
		float f6 = (float)(i5 >> 16 & 255) / 255.0F;
		float f7 = (float)(i5 >> 8 & 255) / 255.0F;
		float f8 = (float)(i5 & 255) / 255.0F;
	
		return this.renderBlockCactusImpl(block1, i2, i3, i4, f6, f7, f8);
	}

	public boolean renderBlockCactusImpl(Block block1, int i2, int i3, int i4, float f5, float f6, float f7) {
		Tessellator tessellator8 = Tessellator.instance;
		boolean z9 = false;
		float f10 = 0.5F;
		float f11 = 1.0F;
		float f12 = 0.8F;
		float f13 = 0.6F;
		float f14 = f10 * f5;
		float f15 = f11 * f5;
		float f16 = f12 * f5;
		float f17 = f13 * f5;
		float f18 = f10 * f6;
		float f19 = f11 * f6;
		float f20 = f12 * f6;
		float f21 = f13 * f6;
		float f22 = f10 * f7;
		float f23 = f11 * f7;
		float f24 = f12 * f7;
		float f25 = f13 * f7;
		float f26 = 0.0625F;
		int i28 = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4);
		if(this.renderAllFaces || block1.shouldSideBeRendered(this.blockAccess, i2, i3 - 1, i4, 0)) {
			tessellator8.setBrightness(block1.minY > 0.0D ? i28 : block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 - 1, i4));
			tessellator8.setColorOpaque_F(f14, f18, f22);
			this.renderBottomFace(block1, (double)i2, (double)i3, (double)i4, block1.getBlockTexture(this.blockAccess, i2, i3, i4, 0));
			z9 = true;
		}

		if(this.renderAllFaces || block1.shouldSideBeRendered(this.blockAccess, i2, i3 + 1, i4, 1)) {
			tessellator8.setBrightness(block1.maxY < 1.0D ? i28 : block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 + 1, i4));
			tessellator8.setColorOpaque_F(f15, f19, f23);
			this.renderTopFace(block1, (double)i2, (double)i3, (double)i4, block1.getBlockTexture(this.blockAccess, i2, i3, i4, 1));
			z9 = true;
		}

		if(this.renderAllFaces || block1.shouldSideBeRendered(this.blockAccess, i2, i3, i4 - 1, 2)) {
			tessellator8.setBrightness(block1.minZ > 0.0D ? i28 : block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4 - 1));
			tessellator8.setColorOpaque_F(f16, f20, f24);
			tessellator8.addTranslation(0.0F, 0.0F, f26);
			this.renderEastFace(block1, (double)i2, (double)i3, (double)i4, block1.getBlockTexture(this.blockAccess, i2, i3, i4, 2));
			tessellator8.addTranslation(0.0F, 0.0F, -f26);
			z9 = true;
		}

		if(this.renderAllFaces || block1.shouldSideBeRendered(this.blockAccess, i2, i3, i4 + 1, 3)) {
			tessellator8.setBrightness(block1.maxZ < 1.0D ? i28 : block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4 + 1));
			tessellator8.setColorOpaque_F(f16, f20, f24);
			tessellator8.addTranslation(0.0F, 0.0F, -f26);
			this.renderWestFace(block1, (double)i2, (double)i3, (double)i4, block1.getBlockTexture(this.blockAccess, i2, i3, i4, 3));
			tessellator8.addTranslation(0.0F, 0.0F, f26);
			z9 = true;
		}

		if(this.renderAllFaces || block1.shouldSideBeRendered(this.blockAccess, i2 - 1, i3, i4, 4)) {
			tessellator8.setBrightness(block1.minX > 0.0D ? i28 : block1.getMixedBrightnessForBlock(this.blockAccess, i2 - 1, i3, i4));
			tessellator8.setColorOpaque_F(f17, f21, f25);
			tessellator8.addTranslation(f26, 0.0F, 0.0F);
			this.renderNorthFace(block1, (double)i2, (double)i3, (double)i4, block1.getBlockTexture(this.blockAccess, i2, i3, i4, 4));
			tessellator8.addTranslation(-f26, 0.0F, 0.0F);
			z9 = true;
		}

		if(this.renderAllFaces || block1.shouldSideBeRendered(this.blockAccess, i2 + 1, i3, i4, 5)) {
			tessellator8.setBrightness(block1.maxX < 1.0D ? i28 : block1.getMixedBrightnessForBlock(this.blockAccess, i2 + 1, i3, i4));
			tessellator8.setColorOpaque_F(f17, f21, f25);
			tessellator8.addTranslation(-f26, 0.0F, 0.0F);
			this.renderSouthFace(block1, (double)i2, (double)i3, (double)i4, block1.getBlockTexture(this.blockAccess, i2, i3, i4, 5));
			tessellator8.addTranslation(f26, 0.0F, 0.0F);
			z9 = true;
		}

		return z9;
	}

	public boolean renderBlockFence(BlockFence blockFence1, int i2, int i3, int i4) {
		boolean z5 = false;
		float f6 = 0.375F;
		float f7 = 0.625F;
		blockFence1.setBlockBounds(f6, 0.0F, f6, f7, 1.0F, f7);
		this.renderStandardBlock(blockFence1, i2, i3, i4);
		z5 = true;
		boolean z8 = false;
		boolean z9 = false;
		if(blockFence1.canConnectFenceTo(this.blockAccess, i2 - 1, i3, i4) || blockFence1.canConnectFenceTo(this.blockAccess, i2 + 1, i3, i4)) {
			z8 = true;
		}

		if(blockFence1.canConnectFenceTo(this.blockAccess, i2, i3, i4 - 1) || blockFence1.canConnectFenceTo(this.blockAccess, i2, i3, i4 + 1)) {
			z9 = true;
		}

		boolean z10 = blockFence1.canConnectFenceTo(this.blockAccess, i2 - 1, i3, i4);
		boolean z11 = blockFence1.canConnectFenceTo(this.blockAccess, i2 + 1, i3, i4);
		boolean z12 = blockFence1.canConnectFenceTo(this.blockAccess, i2, i3, i4 - 1);
		boolean z13 = blockFence1.canConnectFenceTo(this.blockAccess, i2, i3, i4 + 1);
		if(!z8 && !z9) {
			z8 = true;
		}

		f6 = 0.4375F;
		f7 = 0.5625F;
		float f14 = 0.75F;
		float f15 = 0.9375F;
		float f16 = z10 ? 0.0F : f6;
		float f17 = z11 ? 1.0F : f7;
		float f18 = z12 ? 0.0F : f6;
		float f19 = z13 ? 1.0F : f7;
		if(z8) {
			blockFence1.setBlockBounds(f16, f14, f6, f17, f15, f7);
			this.renderStandardBlock(blockFence1, i2, i3, i4);
			z5 = true;
		}

		if(z9) {
			blockFence1.setBlockBounds(f6, f14, f18, f7, f15, f19);
			this.renderStandardBlock(blockFence1, i2, i3, i4);
			z5 = true;
		}

		f14 = 0.375F;
		f15 = 0.5625F;
		if(z8) {
			blockFence1.setBlockBounds(f16, f14, f6, f17, f15, f7);
			this.renderStandardBlock(blockFence1, i2, i3, i4);
			z5 = true;
		}

		if(z9) {
			blockFence1.setBlockBounds(f6, f14, f18, f7, f15, f19);
			this.renderStandardBlock(blockFence1, i2, i3, i4);
			z5 = true;
		}

		blockFence1.setBlockBoundsBasedOnState(this.blockAccess, i2, i3, i4);
		return z5;
	}

	public boolean renderBlockStairs(Block block1, int i2, int i3, int i4) {
		int i5 = this.blockAccess.getBlockMetadata(i2, i3, i4);
		int i6 = i5 & 3;
		float f7 = 0.0F;
		float f8 = 0.5F;
		float f9 = 0.5F;
		float f10 = 1.0F;
		if((i5 & 4) != 0) {
			f7 = 0.5F;
			f8 = 1.0F;
			f9 = 0.0F;
			f10 = 0.5F;
		}

		block1.setBlockBounds(0.0F, f7, 0.0F, 1.0F, f8, 1.0F);
		this.renderStandardBlock(block1, i2, i3, i4);
		if(i6 == 0) {
			block1.setBlockBounds(0.5F, f9, 0.0F, 1.0F, f10, 1.0F);
			this.renderStandardBlock(block1, i2, i3, i4);
		} else if(i6 == 1) {
			block1.setBlockBounds(0.0F, f9, 0.0F, 0.5F, f10, 1.0F);
			this.renderStandardBlock(block1, i2, i3, i4);
		} else if(i6 == 2) {
			block1.setBlockBounds(0.0F, f9, 0.5F, 1.0F, f10, 1.0F);
			this.renderStandardBlock(block1, i2, i3, i4);
		} else if(i6 == 3) {
			block1.setBlockBounds(0.0F, f9, 0.0F, 1.0F, f10, 0.5F);
			this.renderStandardBlock(block1, i2, i3, i4);
		}

		block1.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		return true;
	}

	public boolean renderBlockDoor(Block block1, int i2, int i3, int i4) {
		Tessellator tessellator5 = Tessellator.instance;
		boolean z7 = false;
		float f8 = 0.5F;
		float f9 = 1.0F;
		float f10 = 0.8F;
		float f11 = 0.6F;
		int i12 = block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4);
		tessellator5.setBrightness(block1.minY > 0.0D ? i12 : block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 - 1, i4));
		tessellator5.setColorOpaque_F(f8, f8, f8);
		this.renderBottomFace(block1, (double)i2, (double)i3, (double)i4, block1.getBlockTexture(this.blockAccess, i2, i3, i4, 0));
		z7 = true;
		tessellator5.setBrightness(block1.maxY < 1.0D ? i12 : block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3 + 1, i4));
		tessellator5.setColorOpaque_F(f9, f9, f9);
		this.renderTopFace(block1, (double)i2, (double)i3, (double)i4, block1.getBlockTexture(this.blockAccess, i2, i3, i4, 1));
		z7 = true;
		tessellator5.setBrightness(block1.minZ > 0.0D ? i12 : block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4 - 1));
		tessellator5.setColorOpaque_F(f10, f10, f10);
		int i14 = block1.getBlockTexture(this.blockAccess, i2, i3, i4, 2);
		if(i14 < 0) {
			this.flipTexture = true;
			i14 = -i14;
		}

		this.renderEastFace(block1, (double)i2, (double)i3, (double)i4, i14);
		z7 = true;
		this.flipTexture = false;
		tessellator5.setBrightness(block1.maxZ < 1.0D ? i12 : block1.getMixedBrightnessForBlock(this.blockAccess, i2, i3, i4 + 1));
		tessellator5.setColorOpaque_F(f10, f10, f10);
		i14 = block1.getBlockTexture(this.blockAccess, i2, i3, i4, 3);
		if(i14 < 0) {
			this.flipTexture = true;
			i14 = -i14;
		}

		this.renderWestFace(block1, (double)i2, (double)i3, (double)i4, i14);
		z7 = true;
		this.flipTexture = false;
		tessellator5.setBrightness(block1.minX > 0.0D ? i12 : block1.getMixedBrightnessForBlock(this.blockAccess, i2 - 1, i3, i4));
		tessellator5.setColorOpaque_F(f11, f11, f11);
		i14 = block1.getBlockTexture(this.blockAccess, i2, i3, i4, 4);
		if(i14 < 0) {
			this.flipTexture = true;
			i14 = -i14;
		}

		this.renderNorthFace(block1, (double)i2, (double)i3, (double)i4, i14);
		z7 = true;
		this.flipTexture = false;
		tessellator5.setBrightness(block1.maxX < 1.0D ? i12 : block1.getMixedBrightnessForBlock(this.blockAccess, i2 + 1, i3, i4));
		tessellator5.setColorOpaque_F(f11, f11, f11);
		i14 = block1.getBlockTexture(this.blockAccess, i2, i3, i4, 5);
		if(i14 < 0) {
			this.flipTexture = true;
			i14 = -i14;
		}

		this.renderSouthFace(block1, (double)i2, (double)i3, (double)i4, i14);
		z7 = true;
		this.flipTexture = false;
		return z7;
	}

	public void renderBottomFace(Block block1, double d2, double d4, double d6, int i8) {
		Tessellator tessellator9 = Tessellator.instance;
		if(this.overrideBlockTexture >= 0) {
			i8 = this.overrideBlockTexture;
		}

		final double taw = TextureAtlasSize.w;
		final double tah = TextureAtlasSize.h;
		
		double u1, u2, v1, v2;
		double ru2, ru1, rv1, rv2;

		int i10 = (i8 & 15) << 4;
		int i11 = i8 & 0xff0;
				;
		if(this.uvRotateBottom == 2) {
			u1 = ((double)i10 + block1.minZ * 16.0D) / taw;
			v1 = ((double)(i11 + 16) - block1.maxX * 16.0D) / taw;
			u2 = ((double)i10 + block1.maxZ * 16.0D) / tah;
			v2 = ((double)(i11 + 16) - block1.minX * 16.0D) / tah;
			rv1 = v1;
			rv2 = v2;
			ru2 = u1;
			ru1 = u2;
			v1 = v2;
			v2 = rv1;
		} else if(this.uvRotateBottom == 1) {
			u1 = ((double)(i10 + 16) - block1.maxZ * 16.0D) / taw;
			v1 = ((double)i11 + block1.minX * 16.0D) / tah;
			u2 = ((double)(i10 + 16) - block1.minZ * 16.0D) / taw;
			v2 = ((double)i11 + block1.maxX * 16.0D) / tah;
			ru2 = u2;
			ru1 = u1;
			u1 = u2;
			u2 = ru1;
			rv1 = v2;
			rv2 = v1;
		} else if(this.uvRotateBottom == 3) {
			u1 = ((double)(i10 + 16) - block1.minX * 16.0D) / taw;
			u2 = ((double)(i10 + 16) - block1.maxX * 16.0D - 0.01D) / taw;
			v1 = ((double)(i11 + 16) - block1.minZ * 16.0D) / tah;
			v2 = ((double)(i11 + 16) - block1.maxZ * 16.0D - 0.01D) / tah;
			ru2 = u2;
			ru1 = u1;
			rv1 = v1;
			rv2 = v2;
		} else {
			u1 = ((double)i10 + block1.minX * 16.0D) / taw;
			u2 = ((double)i10 + block1.maxX * 16.0D - 0.01D) / taw;
			v1 = ((double)i11 + block1.minZ * 16.0D) / tah;
			v2 = ((double)i11 + block1.maxZ * 16.0D - 0.01D) / tah;
			
			if(block1.minX < 0.0D || block1.maxX > 1.0D) {
				u1 = (double)(((float)i10 + 0.0F) / taw);
				u2 = (double)(((float)i10 + 15.99F) / taw);
			}

			if(block1.minZ < 0.0D || block1.maxZ > 1.0D) {
				v1 = (double)(((float)i11 + 0.0F) / tah);
				v2 = (double)(((float)i11 + 15.99F) / tah);
			}

			ru2 = u2;
			ru1 = u1;
			rv1 = v1;
			rv2 = v2;
		}

		double d28 = d2 + block1.minX;
		double d30 = d2 + block1.maxX;
		double d32 = d4 + block1.minY;
		double d34 = d6 + block1.minZ;
		double d36 = d6 + block1.maxZ;
		if(this.enableAO) {
			tessellator9.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
			tessellator9.setBrightness(this.brightnessTopLeft);
			tessellator9.addVertexWithUV(d28, d32, d36, ru1, rv2);
			tessellator9.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			tessellator9.setBrightness(this.brightnessBottomLeft);
			tessellator9.addVertexWithUV(d28, d32, d34, u1, v1);
			tessellator9.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
			tessellator9.setBrightness(this.brightnessBottomLeft);
			tessellator9.addVertexWithUV(d30, d32, d34, ru2, rv1);
			tessellator9.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
			tessellator9.setBrightness(this.brightnessTopRight);
			tessellator9.addVertexWithUV(d30, d32, d36, u2, v2);
		} else {
			tessellator9.addVertexWithUV(d28, d32, d36, ru1, rv2);
			tessellator9.addVertexWithUV(d28, d32, d34, u1, v1);
			tessellator9.addVertexWithUV(d30, d32, d34, ru2, rv1);
			tessellator9.addVertexWithUV(d30, d32, d36, u2, v2);
		}

	}

	public void renderTopFace(Block block1, double d2, double d4, double d6, int i8) {
		Tessellator tessellator9 = Tessellator.instance;
		if(this.overrideBlockTexture >= 0) {
			i8 = this.overrideBlockTexture;
		}

		final double taw = TextureAtlasSize.w;
		final double tah = TextureAtlasSize.h;
		
		double u1, u2, v1, v2;
		double ru2, ru1, rv1, rv2;
		
		int i10 = (i8 & 15) << 4;
		int i11 = i8 & 0xff0;
	
		if(this.uvRotateTop == 1) {
			u1 = ((double)i10 + block1.minZ * 16.0D) / taw;
			v1 = ((double)(i11 + 16) - block1.maxX * 16.0D) / tah;
			u2 = ((double)i10 + block1.maxZ * 16.0D) / taw;
			v2 = ((double)(i11 + 16) - block1.minX * 16.0D) / tah;
			rv1 = v1;
			rv2 = v2;
			ru2 = u1;
			ru1 = u2;
			v1 = v2;
			v2 = rv1;
		} else if(this.uvRotateTop == 2) {
			u1 = ((double)(i10 + 16) - block1.maxZ * 16.0D) / taw;
			v1 = ((double)i11 + block1.minX * 16.0D) / tah;
			u2 = ((double)(i10 + 16) - block1.minZ * 16.0D) / taw;
			v2 = ((double)i11 + block1.maxX * 16.0D) / tah;
			ru2 = u2;
			ru1 = u1;
			u1 = u2;
			u2 = ru1;
			rv1 = v2;
			rv2 = v1;
		} else if(this.uvRotateTop == 3) {
			u1 = ((double)(i10 + 16) - block1.minX * 16.0D) / taw;
			u2 = ((double)(i10 + 16) - block1.maxX * 16.0D - 0.01D) / taw;
			v1 = ((double)(i11 + 16) - block1.minZ * 16.0D) / tah;
			v2 = ((double)(i11 + 16) - block1.maxZ * 16.0D - 0.01D) / tah;
			ru2 = u2;
			ru1 = u1;
			rv1 = v1;
			rv2 = v2;
		} else {
			u1 = ((double)i10 + block1.minX * 16.0D) / taw;
			u2 = ((double)i10 + block1.maxX * 16.0D - 0.01D) / taw;
			v1 = ((double)i11 + block1.minZ * 16.0D) / tah;
			v2 = ((double)i11 + block1.maxZ * 16.0D - 0.01D) / tah;
			if(block1.minX < 0.0D || block1.maxX > 1.0D) {
				u1 = (double)(((float)i10 + 0.0F) / taw);
				u2 = (double)(((float)i10 + 15.99F) / taw);
			}

			if(block1.minZ < 0.0D || block1.maxZ > 1.0D) {
				v1 = (double)(((float)i11 + 0.0F) / tah);
				v2 = (double)(((float)i11 + 15.99F) / tah);
			}

			ru2 = u2;
			ru1 = u1;
			rv1 = v1;
			rv2 = v2;
		}

		double d28 = d2 + block1.minX;
		double d30 = d2 + block1.maxX;
		double d32 = d4 + block1.maxY;
		double d34 = d6 + block1.minZ;
		double d36 = d6 + block1.maxZ;
		if(this.enableAO) {
			tessellator9.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
			tessellator9.setBrightness(this.brightnessTopLeft);
			tessellator9.addVertexWithUV(d30, d32, d36, u2, v2);
			tessellator9.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			tessellator9.setBrightness(this.brightnessBottomLeft);
			tessellator9.addVertexWithUV(d30, d32, d34, ru2, rv1);
			tessellator9.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
			tessellator9.setBrightness(this.brightnessBottomRight);
			tessellator9.addVertexWithUV(d28, d32, d34, u1, v1);
			tessellator9.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
			tessellator9.setBrightness(this.brightnessTopRight);
			tessellator9.addVertexWithUV(d28, d32, d36, ru1, rv2);
		} else {
			tessellator9.addVertexWithUV(d30, d32, d36, u2, v2);
			tessellator9.addVertexWithUV(d30, d32, d34, ru2, rv1);
			tessellator9.addVertexWithUV(d28, d32, d34, u1, v1);
			tessellator9.addVertexWithUV(d28, d32, d36, ru1, rv2);
		}

	}

	public void renderEastFace(Block block1, double d2, double d4, double d6, int i8) {
		Tessellator tessellator9 = Tessellator.instance;
		if(this.overrideBlockTexture >= 0) {
			i8 = this.overrideBlockTexture;
		}

		int i10 = (i8 & 15) << 4;
		int i11 = i8 & 0xff0;
		
		final double taw = TextureAtlasSize.w;
		final double tah = TextureAtlasSize.h;
		
		double u1, u2, v1, v2;
		double ru2, ru1, rv1, rv2;
		
		if(this.uvRotateEast == 2) {
			u1 = ((double)i10 + block1.minY * 16.0D) / taw;
			v1 = ((double)(i11 + 16) - block1.minX * 16.0D) / tah;
			u2 = ((double)i10 + block1.maxY * 16.0D) / taw;
			v2 = ((double)(i11 + 16) - block1.maxX * 16.0D) / tah;
			rv1 = v1;
			rv2 = v2;
			ru2 = u1;
			ru1 = u2;
			v1 = v2;
			v2 = rv1;
		} else if(this.uvRotateEast == 1) {
			u1 = ((double)(i10 + 16) - block1.maxY * 16.0D) / taw;
			v1 = ((double)i11 + block1.maxX * 16.0D) / tah;
			u2 = ((double)(i10 + 16) - block1.minY * 16.0D) / taw;
			v2 = ((double)i11 + block1.minX * 16.0D) / tah;
			ru2 = u2;
			ru1 = u1;
			u1 = u2;
			u2 = ru1;
			rv1 = v2;
			rv2 = v1;
		} else if(this.uvRotateEast == 3) {
			u1 = ((double)(i10 + 16) - block1.minX * 16.0D) / taw;
			u2 = ((double)(i10 + 16) - block1.maxX * 16.0D - 0.01D) / taw;
			v1 = ((double)i11 + block1.maxY * 16.0D) / tah;
			v2 = ((double)i11 + block1.minY * 16.0D - 0.01D) / tah;
			ru2 = u2;
			ru1 = u1;
			rv1 = v1;
			rv2 = v2;
		} else {
			u1 = ((double)i10 + block1.minX * 16.0D) / taw;
			u2 = ((double)i10 + block1.maxX * 16.0D - 0.01D) / taw;
			v1 = ((double)(i11 + 16) - block1.maxY * 16.0D) / tah;
			v2 = ((double)(i11 + 16) - block1.minY * 16.0D - 0.01D) / tah;

			if(this.flipTexture) {
				ru2 = u1;
				u1 = u2;
				u2 = ru2;
			}

			if(block1.minX < 0.0D || block1.maxX > 1.0D) {
				u1 = (double)(((float)i10 + 0.0F) / taw);
				u2 = (double)(((float)i10 + 15.99F) / taw);
			}

			if(block1.minY < 0.0D || block1.maxY > 1.0D) {
				v1 = (double)(((float)i11 + 0.0F) / tah);
				v2 = (double)(((float)i11 + 15.99F) / tah);
			}

			ru2 = u2;
			ru1 = u1;
			rv1 = v1;
			rv2 = v2;
		}

		double d28 = d2 + block1.minX;
		double d30 = d2 + block1.maxX;
		double d32 = d4 + block1.minY;
		double d34 = d4 + block1.maxY;
		double d36 = d6 + block1.minZ;
		if(this.enableAO) {
			tessellator9.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
			tessellator9.setBrightness(this.brightnessTopLeft);
			tessellator9.addVertexWithUV(d28, d34, d36, ru2, rv1);
			tessellator9.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			tessellator9.setBrightness(this.brightnessBottomLeft);
			tessellator9.addVertexWithUV(d30, d34, d36, u1, v1);
			tessellator9.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
			tessellator9.setBrightness(this.brightnessBottomRight);
			tessellator9.addVertexWithUV(d30, d32, d36, ru1, rv2);
			tessellator9.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
			tessellator9.setBrightness(this.brightnessTopRight);
			tessellator9.addVertexWithUV(d28, d32, d36, u2, v2);
		} else {
			tessellator9.addVertexWithUV(d28, d34, d36, ru2, rv1);
			tessellator9.addVertexWithUV(d30, d34, d36, u1, v1);
			tessellator9.addVertexWithUV(d30, d32, d36, ru1, rv2);
			tessellator9.addVertexWithUV(d28, d32, d36, u2, v2);
		}

	}

	public void renderWestFace(Block block1, double d2, double d4, double d6, int i8) {
		Tessellator tessellator9 = Tessellator.instance;
		if(this.overrideBlockTexture >= 0) {
			i8 = this.overrideBlockTexture;
		}

		int i10 = (i8 & 15) << 4;
		int i11 = i8 & 0xff0;
		
		final double taw = TextureAtlasSize.w;
		final double tah = TextureAtlasSize.h;
		
		double u1, u2, v1, v2;
		double ru2, ru1, rv1, rv2;
		
		if(this.uvRotateWest == 1) {
			u1 = ((double)i10 + block1.minY * 16.0D) / taw;
			v2 = ((double)(i11 + 16) - block1.minX * 16.0D) / tah;
			u2 = ((double)i10 + block1.maxY * 16.0D) / taw;
			v1 = ((double)(i11 + 16) - block1.maxX * 16.0D) / tah;
			rv1 = v1;
			rv2 = v2;
			ru2 = u1;
			ru1 = u2;
			v1 = v2;
			v2 = rv1;
		} else if(this.uvRotateWest == 2) {
			u1 = ((double)(i10 + 16) - block1.maxY * 16.0D) / taw;
			v1 = ((double)i11 + block1.minX * 16.0D) / tah;
			u2 = ((double)(i10 + 16) - block1.minY * 16.0D) / taw;
			v2 = ((double)i11 + block1.maxX * 16.0D) / tah;
			ru2 = u2;
			ru1 = u1;
			u1 = u2;
			u2 = ru1;
			rv1 = v2;
			rv2 = v1;
		} else if(this.uvRotateWest == 3) {
			u1 = ((double)(i10 + 16) - block1.minX * 16.0D) / taw;
			u2 = ((double)(i10 + 16) - block1.maxX * 16.0D - 0.01D) / taw;
			v1 = ((double)i11 + block1.maxY * 16.0D) / tah;
			v2 = ((double)i11 + block1.minY * 16.0D - 0.01D) / tah;
			ru2 = u2;
			ru1 = u1;
			rv1 = v1;
			rv2 = v2;
		} else {
			u1 = ((double)i10 + block1.minX * 16.0D) / taw;
			u2 = ((double)i10 + block1.maxX * 16.0D - 0.01D) / taw;
			v1 = ((double)(i11 + 16) - block1.maxY * 16.0D) / tah;
			v2 = ((double)(i11 + 16) - block1.minY * 16.0D - 0.01D) / tah;
			if(this.flipTexture) {
				ru2 = u1;
				u1 = u2;
				u2 = ru2;
			}

			if(block1.minX < 0.0D || block1.maxX > 1.0D) {
				u1 = (double)(((float)i10 + 0.0F) / taw);
				u2 = (double)(((float)i10 + 15.99F) / taw);
			}

			if(block1.minY < 0.0D || block1.maxY > 1.0D) {
				v1 = (double)(((float)i11 + 0.0F) / tah);
				v2 = (double)(((float)i11 + 15.99F) / tah);
			}

			ru2 = u2;
			ru1 = u1;
			rv1 = v1;
			rv2 = v2;
		}

		double d28 = d2 + block1.minX;
		double d30 = d2 + block1.maxX;
		double d32 = d4 + block1.minY;
		double d34 = d4 + block1.maxY;
		double d36 = d6 + block1.maxZ;
		if(this.enableAO) {
			tessellator9.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
			tessellator9.setBrightness(this.brightnessTopLeft);
			tessellator9.addVertexWithUV(d28, d34, d36, u1, v1);
			tessellator9.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			tessellator9.setBrightness(this.brightnessBottomLeft);
			tessellator9.addVertexWithUV(d28, d32, d36, ru1, rv2);
			tessellator9.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
			tessellator9.setBrightness(this.brightnessBottomRight);
			tessellator9.addVertexWithUV(d30, d32, d36, u2, v2);
			tessellator9.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
			tessellator9.setBrightness(this.brightnessTopRight);
			tessellator9.addVertexWithUV(d30, d34, d36, ru2, rv1);
		} else {
			tessellator9.addVertexWithUV(d28, d34, d36, u1, v1);
			tessellator9.addVertexWithUV(d28, d32, d36, ru1, rv2);
			tessellator9.addVertexWithUV(d30, d32, d36, u2, v2);
			tessellator9.addVertexWithUV(d30, d34, d36, ru2, rv1);
		}

	}

	public void renderNorthFace(Block block1, double d2, double d4, double d6, int i8) {
		Tessellator tessellator9 = Tessellator.instance;
		if(this.overrideBlockTexture >= 0) {
			i8 = this.overrideBlockTexture;
		}

		int i10 = (i8 & 15) << 4;
		int i11 = i8 & 0xff0;
		
		final double taw = TextureAtlasSize.w;
		final double tah = TextureAtlasSize.h;
		
		double u1, u2, v1, v2;
		double ru2, ru1, rv1, rv2;
		
		if(this.uvRotateNorth == 1) {
			u1 = ((double)i10 + block1.minY * 16.0D) / taw;
			v1 = ((double)(i11 + 16) - block1.maxZ * 16.0D) / tah;
			u2 = ((double)i10 + block1.maxY * 16.0D) / taw;
			v2 = ((double)(i11 + 16) - block1.minZ * 16.0D) / tah;
			rv1 = v1;
			rv2 = v2;
			ru2 = u1;
			ru1 = u2;
			v1 = v2;
			v2 = rv1;
		} else if(this.uvRotateNorth == 2) {
			u1 = ((double)(i10 + 16) - block1.maxY * 16.0D) / taw;
			v1 = ((double)i11 + block1.minZ * 16.0D) / tah;
			u2 = ((double)(i10 + 16) - block1.minY * 16.0D) / taw;
			v2 = ((double)i11 + block1.maxZ * 16.0D) / tah;
			ru2 = u2;
			ru1 = u1;
			u1 = u2;
			u2 = ru1;
			rv1 = v2;
			rv2 = v1;
		} else if(this.uvRotateNorth == 3) {
			u1 = ((double)(i10 + 16) - block1.minZ * 16.0D) / taw;
			u2 = ((double)(i10 + 16) - block1.maxZ * 16.0D - 0.01D) / taw;
			v1 = ((double)i11 + block1.maxY * 16.0D) / tah;
			v2 = ((double)i11 + block1.minY * 16.0D - 0.01D) / tah;
			ru2 = u2;
			ru1 = u1;
			rv1 = v1;
			rv2 = v2;
		} else {
			u1 = ((double)i10 + block1.minZ * 16.0D) / taw;
			u2 = ((double)i10 + block1.maxZ * 16.0D - 0.01D) / taw;
			v1 = ((double)(i11 + 16) - block1.maxY * 16.0D) / tah;
			v2 = ((double)(i11 + 16) - block1.minY * 16.0D - 0.01D) / tah;

			if(this.flipTexture) {
				ru2 = u1;
				u1 = u2;
				u2 = ru2;
			}

			if(block1.minZ < 0.0D || block1.maxZ > 1.0D) {
				u1 = (double)(((float)i10 + 0.0F) / taw);
				u2 = (double)(((float)i10 + 15.99F) / taw);
			}

			if(block1.minY < 0.0D || block1.maxY > 1.0D) {
				v1 = (double)(((float)i11 + 0.0F) / tah);
				v2 = (double)(((float)i11 + 15.99F) / tah);
			}

			ru2 = u2;
			ru1 = u1;
			rv1 = v1;
			rv2 = v2;
		}

		double d28 = d2 + block1.minX;
		double d30 = d4 + block1.minY;
		double d32 = d4 + block1.maxY;
		double d34 = d6 + block1.minZ;
		double d36 = d6 + block1.maxZ;
		if(this.enableAO) {
			tessellator9.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
			tessellator9.setBrightness(this.brightnessTopLeft);
			tessellator9.addVertexWithUV(d28, d32, d36, ru2, rv1);
			tessellator9.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			tessellator9.setBrightness(this.brightnessBottomLeft);
			tessellator9.addVertexWithUV(d28, d32, d34, u1, v1);
			tessellator9.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
			tessellator9.setBrightness(this.brightnessBottomRight);
			tessellator9.addVertexWithUV(d28, d30, d34, ru1, rv2);
			tessellator9.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
			tessellator9.setBrightness(this.brightnessTopRight);
			tessellator9.addVertexWithUV(d28, d30, d36, u2, v2);
		} else {
			tessellator9.addVertexWithUV(d28, d32, d36, ru2, rv1);
			tessellator9.addVertexWithUV(d28, d32, d34, u1, v1);
			tessellator9.addVertexWithUV(d28, d30, d34, ru1, rv2);
			tessellator9.addVertexWithUV(d28, d30, d36, u2, v2);
		}

	}

	public void renderSouthFace(Block block1, double d2, double d4, double d6, int i8) {
		Tessellator tessellator9 = Tessellator.instance;
		if(this.overrideBlockTexture >= 0) {
			i8 = this.overrideBlockTexture;
		}

		int i10 = (i8 & 15) << 4;
		int i11 = i8 & 0xff0;
		
		final double taw = TextureAtlasSize.w;
		final double tah = TextureAtlasSize.h;
		
		double u1, u2, v1, v2;
		double ru2, ru1, rv1, rv2;
		
		if(this.uvRotateSouth == 2) {
			u1 = ((double)i10 + block1.minY * 16.0D) / taw;
			v1 = ((double)(i11 + 16) - block1.minZ * 16.0D) / tah;
			u2 = ((double)i10 + block1.maxY * 16.0D) / taw;
			v2 = ((double)(i11 + 16) - block1.maxZ * 16.0D) / tah;
			rv1 = v1;
			rv2 = v2;
			ru2 = u1;
			ru1 = u2;
			v1 = v2;
			v2 = rv1;
		} else if(this.uvRotateSouth == 1) {
			u1 = ((double)(i10 + 16) - block1.maxY * 16.0D) / taw;
			v1 = ((double)i11 + block1.maxZ * 16.0D) / tah;
			u2 = ((double)(i10 + 16) - block1.minY * 16.0D) / taw;
			v2 = ((double)i11 + block1.minZ * 16.0D) / tah;
			ru2 = u2;
			ru1 = u1;
			u1 = u2;
			u2 = ru1;
			rv1 = v2;
			rv2 = v1;
		} else if(this.uvRotateSouth == 3) {
			u1 = ((double)(i10 + 16) - block1.minZ * 16.0D) / taw;
			u2 = ((double)(i10 + 16) - block1.maxZ * 16.0D - 0.01D) / taw;
			v1 = ((double)i11 + block1.maxY * 16.0D) / tah;
			v2 = ((double)i11 + block1.minY * 16.0D - 0.01D) / tah;
			ru2 = u2;
			ru1 = u1;
			rv1 = v1;
			rv2 = v2;
		} else {
			u1 = ((double)i10 + block1.minZ * 16.0D) / taw;
			u2 = ((double)i10 + block1.maxZ * 16.0D - 0.01D) / taw;
			v1 = ((double)(i11 + 16) - block1.maxY * 16.0D) / tah;
			v2 = ((double)(i11 + 16) - block1.minY * 16.0D - 0.01D) / tah;

			if(this.flipTexture) {
				ru2 = u1;
				u1 = u2;
				u2 = ru2;
			}

			if(block1.minZ < 0.0D || block1.maxZ > 1.0D) {
				u1 = (double)(((float)i10 + 0.0F) / taw);
				u2 = (double)(((float)i10 + 15.99F) / taw);
			}

			if(block1.minY < 0.0D || block1.maxY > 1.0D) {
				v1 = (double)(((float)i11 + 0.0F) / tah);
				v2 = (double)(((float)i11 + 15.99F) / tah);
			}

			ru2 = u2;
			ru1 = u1;
			rv1 = v1;
			rv2 = v2;
		}

		double d28 = d2 + block1.maxX;
		double d30 = d4 + block1.minY;
		double d32 = d4 + block1.maxY;
		double d34 = d6 + block1.minZ;
		double d36 = d6 + block1.maxZ;
		if(this.enableAO) {
			tessellator9.setColorOpaque_F(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft);
			tessellator9.setBrightness(this.brightnessTopLeft);
			tessellator9.addVertexWithUV(d28, d30, d36, ru1, rv2);
			tessellator9.setColorOpaque_F(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft);
			tessellator9.setBrightness(this.brightnessBottomLeft);
			tessellator9.addVertexWithUV(d28, d30, d34, u2, v2);
			tessellator9.setColorOpaque_F(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight);
			tessellator9.setBrightness(this.brightnessBottomRight);
			tessellator9.addVertexWithUV(d28, d32, d34, ru2, rv1);
			tessellator9.setColorOpaque_F(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight);
			tessellator9.setBrightness(this.brightnessTopRight);
			tessellator9.addVertexWithUV(d28, d32, d36, u1, v1);
		} else {
			tessellator9.addVertexWithUV(d28, d30, d36, ru1, rv2);
			tessellator9.addVertexWithUV(d28, d30, d34, u2, v2);
			tessellator9.addVertexWithUV(d28, d32, d34, ru2, rv1);
			tessellator9.addVertexWithUV(d28, d32, d36, u1, v1);
		}

	}
	
	public void renderBlockAsItem(Block block1, float f2) {
		int i3 = block1.getRenderType();
		Tessellator tessellator4 = Tessellator.instance;
		if(i3 == 0) {
			block1.setBlockBoundsForItemRender();
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			float f5 = 0.5F;
			float f6 = 1.0F;
			float f7 = 0.8F;
			float f8 = 0.6F;
			tessellator4.startDrawingQuads();
			tessellator4.setColorRGBA_F(f6, f6, f6, f2);
			this.renderBottomFace(block1, 0.0D, 0.0D, 0.0D, block1.getBlockTextureFromSide(0));
			tessellator4.setColorRGBA_F(f5, f5, f5, f2);
			this.renderTopFace(block1, 0.0D, 0.0D, 0.0D, block1.getBlockTextureFromSide(1));
			tessellator4.setColorRGBA_F(f7, f7, f7, f2);
			this.renderEastFace(block1, 0.0D, 0.0D, 0.0D, block1.getBlockTextureFromSide(2));
			this.renderWestFace(block1, 0.0D, 0.0D, 0.0D, block1.getBlockTextureFromSide(3));
			tessellator4.setColorRGBA_F(f8, f8, f8, f2);
			this.renderNorthFace(block1, 0.0D, 0.0D, 0.0D, block1.getBlockTextureFromSide(4));
			this.renderSouthFace(block1, 0.0D, 0.0D, 0.0D, block1.getBlockTextureFromSide(5));
			tessellator4.draw();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		} else {
			block1.setBlockBoundsForItemRender();
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			tessellator4.startDrawingQuads();
			tessellator4.setColorRGBA_F(1.0F, 1.0F, 1.0F, f2);
			this.renderEastFace(block1, 0.0D, 0.0D, 0.0D, block1.getBlockTextureFromSide(0));
			tessellator4.draw();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		}

	}
	
	public void renderBlockAsItem(Block block, int meta, float brightness) {
		if(meta < 0) meta = 0;
		
		Tessellator tes = Tessellator.instance;
		boolean isGrass = block.blockID == Block.grass.blockID;

		float r;
		float g;
		float b;

		if(this.useInventoryTint) {
			int renderColor = isGrass ? 0xFFFFFF : block.getRenderColor(meta);
			
			r = (float)(renderColor >> 16 & 255) / 255.0F;
			g = (float)(renderColor >> 8 & 255) / 255.0F;
			b = (float)(renderColor & 255) / 255.0F;
			GL11.glColor4f(r * brightness, g * brightness, b * brightness, 1.0F);
		}

		int renderType = block.getRenderType();
		
		int i14;
		if(renderType != 0 && renderType != 31 && renderType != 16) {
			if(renderType == 1) {
				tes.startDrawingQuads();
				tes.setNormal(0.0F, -1.0F, 0.0F);
				this.drawCrossedSquares(block, meta, -0.5D, -0.5D, -0.5D);
				tes.draw();
			} else if(renderType == 19) {
				tes.startDrawingQuads();
				tes.setNormal(0.0F, -1.0F, 0.0F);
				block.setBlockBoundsForItemRender();
				this.renderBlockStemSmall(block, meta, block.maxY, -0.5D, -0.5D, -0.5D);
				tes.draw();
			} else if(renderType == 23) {
				tes.startDrawingQuads();
				tes.setNormal(0.0F, -1.0F, 0.0F);
				block.setBlockBoundsForItemRender();
				tes.draw();
			} else if(renderType == 13) {
				block.setBlockBoundsForItemRender();
				GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
				r = 0.0625F;
				tes.startDrawingQuads();
				tes.setNormal(0.0F, -1.0F, 0.0F);
				this.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(0));
				tes.draw();
				tes.startDrawingQuads();
				tes.setNormal(0.0F, 1.0F, 0.0F);
				this.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(1));
				tes.draw();
				tes.startDrawingQuads();
				tes.setNormal(0.0F, 0.0F, -1.0F);
				tes.addTranslation(0.0F, 0.0F, r);
				this.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(2));
				tes.addTranslation(0.0F, 0.0F, -r);
				tes.draw();
				tes.startDrawingQuads();
				tes.setNormal(0.0F, 0.0F, 1.0F);
				tes.addTranslation(0.0F, 0.0F, -r);
				this.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(3));
				tes.addTranslation(0.0F, 0.0F, r);
				tes.draw();
				tes.startDrawingQuads();
				tes.setNormal(-1.0F, 0.0F, 0.0F);
				tes.addTranslation(r, 0.0F, 0.0F);
				this.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(4));
				tes.addTranslation(-r, 0.0F, 0.0F);
				tes.draw();
				tes.startDrawingQuads();
				tes.setNormal(1.0F, 0.0F, 0.0F);
				tes.addTranslation(-r, 0.0F, 0.0F);
				this.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(5));
				tes.addTranslation(r, 0.0F, 0.0F);
				tes.draw();
				GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			} else if(renderType == 22) {
				ChestItemRenderHelper.instance.render(block, meta, brightness);
				GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			} else if(renderType == 6) {
				tes.startDrawingQuads();
				tes.setNormal(0.0F, -1.0F, 0.0F);
				this.renderBlockCropsImpl(block, meta, -0.5D, -0.5D, -0.5D);
				tes.draw();
			} else if(renderType == 2) {
				tes.startDrawingQuads();
				tes.setNormal(0.0F, -1.0F, 0.0F);
				this.renderTorchAtAngle(block, -0.5D, -0.5D, -0.5D, 0.0D, 0.0D);
				tes.draw();
			} else if(renderType == 10) {
				for(i14 = 0; i14 < 2; ++i14) {
					if(i14 == 0) {
						block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.5F);
					}

					if(i14 == 1) {
						block.setBlockBounds(0.0F, 0.0F, 0.5F, 1.0F, 0.5F, 1.0F);
					}

					GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
					tes.startDrawingQuads();
					tes.setNormal(0.0F, -1.0F, 0.0F);
					this.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(0));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(0.0F, 1.0F, 0.0F);
					this.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(1));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(0.0F, 0.0F, -1.0F);
					this.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(2));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(0.0F, 0.0F, 1.0F);
					this.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(3));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(-1.0F, 0.0F, 0.0F);
					this.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(4));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(1.0F, 0.0F, 0.0F);
					this.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(5));
					tes.draw();
					GL11.glTranslatef(0.5F, 0.5F, 0.5F);
				}
			} else if(renderType == 27) {
				i14 = 0;
				GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
				tes.startDrawingQuads();

				for(int i15 = 0; i15 < 8; ++i15) {
					byte b16 = 0;
					byte b17 = 1;
					if(i15 == 0) {
						b16 = 2;
					}

					if(i15 == 1) {
						b16 = 3;
					}

					if(i15 == 2) {
						b16 = 4;
					}

					if(i15 == 3) {
						b16 = 5;
						b17 = 2;
					}

					if(i15 == 4) {
						b16 = 6;
						b17 = 3;
					}

					if(i15 == 5) {
						b16 = 7;
						b17 = 5;
					}

					if(i15 == 6) {
						b16 = 6;
						b17 = 2;
					}

					if(i15 == 7) {
						b16 = 3;
					}

					float f11 = (float)b16 / 16.0F;
					float f12 = 1.0F - (float)i14 / 16.0F;
					float f13 = 1.0F - (float)(i14 + b17) / 16.0F;
					i14 += b17;
					block.setBlockBounds(0.5F - f11, f13, 0.5F - f11, 0.5F + f11, f12, 0.5F + f11);
					tes.setNormal(0.0F, -1.0F, 0.0F);
					this.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(0));
					tes.setNormal(0.0F, 1.0F, 0.0F);
					this.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(1));
					tes.setNormal(0.0F, 0.0F, -1.0F);
					this.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(2));
					tes.setNormal(0.0F, 0.0F, 1.0F);
					this.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(3));
					tes.setNormal(-1.0F, 0.0F, 0.0F);
					this.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(4));
					tes.setNormal(1.0F, 0.0F, 0.0F);
					this.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(5));
				}

				tes.draw();
				GL11.glTranslatef(0.5F, 0.5F, 0.5F);
				block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			} else if(renderType == 11) {
				for(i14 = 0; i14 < 4; ++i14) {
					g = 0.125F;
					if(i14 == 0) {
						block.setBlockBounds(0.5F - g, 0.0F, 0.0F, 0.5F + g, 1.0F, g * 2.0F);
					}

					if(i14 == 1) {
						block.setBlockBounds(0.5F - g, 0.0F, 1.0F - g * 2.0F, 0.5F + g, 1.0F, 1.0F);
					}

					g = 0.0625F;
					if(i14 == 2) {
						block.setBlockBounds(0.5F - g, 1.0F - g * 3.0F, -g * 2.0F, 0.5F + g, 1.0F - g, 1.0F + g * 2.0F);
					}

					if(i14 == 3) {
						block.setBlockBounds(0.5F - g, 0.5F - g * 3.0F, -g * 2.0F, 0.5F + g, 0.5F - g, 1.0F + g * 2.0F);
					}

					GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
					tes.startDrawingQuads();
					tes.setNormal(0.0F, -1.0F, 0.0F);
					this.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(0));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(0.0F, 1.0F, 0.0F);
					this.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(1));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(0.0F, 0.0F, -1.0F);
					this.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(2));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(0.0F, 0.0F, 1.0F);
					this.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(3));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(-1.0F, 0.0F, 0.0F);
					this.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(4));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(1.0F, 0.0F, 0.0F);
					this.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(5));
					tes.draw();
					GL11.glTranslatef(0.5F, 0.5F, 0.5F);
				}

				block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			} else if(renderType == 21) {
				for(i14 = 0; i14 < 3; ++i14) {
					g = 0.0625F;
					if(i14 == 0) {
						block.setBlockBounds(0.5F - g, 0.3F, 0.0F, 0.5F + g, 1.0F, g * 2.0F);
					}

					if(i14 == 1) {
						block.setBlockBounds(0.5F - g, 0.3F, 1.0F - g * 2.0F, 0.5F + g, 1.0F, 1.0F);
					}

					g = 0.0625F;
					if(i14 == 2) {
						block.setBlockBounds(0.5F - g, 0.5F, 0.0F, 0.5F + g, 1.0F - g, 1.0F);
					}

					GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
					tes.startDrawingQuads();
					tes.setNormal(0.0F, -1.0F, 0.0F);
					this.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(0));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(0.0F, 1.0F, 0.0F);
					this.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(1));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(0.0F, 0.0F, -1.0F);
					this.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(2));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(0.0F, 0.0F, 1.0F);
					this.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(3));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(-1.0F, 0.0F, 0.0F);
					this.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(4));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(1.0F, 0.0F, 0.0F);
					this.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSide(5));
					tes.draw();
					GL11.glTranslatef(0.5F, 0.5F, 0.5F);
				}

				block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			} else if(renderType == 97) {
				if(meta == 1) {
					GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
					tes.startDrawingQuads();
					tes.setNormal(0.0F, -1.0F, 0.0F);
					this.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(0, meta));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(0.0F, 1.0F, 0.0F);
					this.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(1, meta));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(0.0F, 0.0F, -1.0F);
					this.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(2, meta));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(0.0F, 0.0F, 1.0F);
					this.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(3, meta));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(-1.0F, 0.0F, 0.0F);
					this.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(4, meta));
					tes.draw();
					tes.startDrawingQuads();
					tes.setNormal(1.0F, 0.0F, 0.0F);
					this.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(5, meta));
					tes.draw();
				} else {

				}
			}
		} else {
			if(renderType == 16) {
				meta = 1;
			}

			block.setBlockBoundsForItemRender();
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			tes.startDrawingQuads();
			tes.setNormal(0.0F, -1.0F, 0.0F);
			this.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(0, meta));
			tes.draw();
			if(isGrass && this.useInventoryTint) {
				i14 = block.getRenderColor(meta);
				g = (float)(i14 >> 16 & 255) / 255.0F;
				b = (float)(i14 >> 8 & 255) / 255.0F;
				float f10 = (float)(i14 & 255) / 255.0F;
				GL11.glColor4f(g * brightness, b * brightness, f10 * brightness, 1.0F);
			}

			tes.startDrawingQuads();
			tes.setNormal(0.0F, 1.0F, 0.0F);
			this.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(1, meta));
			tes.draw();
			if(isGrass && this.useInventoryTint) {
				GL11.glColor4f(brightness, brightness, brightness, 1.0F);
			}

			tes.startDrawingQuads();
			tes.setNormal(0.0F, 0.0F, -1.0F);
			this.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(2, meta));
			tes.draw();
			tes.startDrawingQuads();
			tes.setNormal(0.0F, 0.0F, 1.0F);
			this.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(3, meta));
			tes.draw();
			tes.startDrawingQuads();
			tes.setNormal(-1.0F, 0.0F, 0.0F);
			this.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(4, meta));
			tes.draw();
			tes.startDrawingQuads();
			tes.setNormal(1.0F, 0.0F, 0.0F);
			this.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(5, meta));
			tes.draw();
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		}

	}
	
	public static boolean renderItemIn3d(int i0) {
		//return i0 == 0 ? true : (i0 == 13 ? true : (i0 == 10 ? true : (i0 == 11 ? true : (i0 == 27 ? true : (i0 == 22 ? true : (i0 == 21 ? true : i0 == 16))))));
		return (i0 == 0 || i0 == 13 || i0 == 10 || i0 == 11 || i0 == 27 || i0 == 22 || i0 == 21 || i0 == 16 || i0 == 31 || i0 == 97);
	}
}
