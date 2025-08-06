package net.minecraft.client.renderer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkCache;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.phys.AxisAlignedBB;

public class WorldRenderer {
	public World worldObj;
	private int glRenderList = -1;
	private static Tessellator tessellator = Tessellator.instance;
	public static int chunksUpdated = 0;
	public int posX;
	public int posY;
	public int posZ;
	public int posXMinus;
	public int posYMinus;
	public int posZMinus;
	public int posXClip;
	public int posYClip;
	public int posZClip;
	public boolean isInFrustum = false;
	public boolean[] skipRenderPass = new boolean[2];
	public int posXPlus;
	public int posYPlus;
	public int posZPlus;
	public boolean needsUpdate;
	public AxisAlignedBB rendererBoundingBox;
	public int chunkIndex;
	public boolean isVisible = true;
	public boolean isWaitingOnOcclusionQuery;
	public int glOcclusionQuery;
	public boolean isChunkLit;
	private boolean isInitialized = false;
	public List<TileEntity> tileEntityRenderers = new ArrayList<TileEntity>();
	private List<TileEntity> tileEntities;
	
	public WorldRenderer(World world1, List<TileEntity> list2, int i3, int i4, int i5, int i6) {
		this.worldObj = world1;
		this.tileEntities = list2;
		this.glRenderList = i6;
		this.posX = -999;
		this.setPosition(i3, i4, i5);
		this.needsUpdate = false;
	}

	public void setPosition(int i1, int i2, int i3) {
		if(i1 != this.posX || i2 != this.posY || i3 != this.posZ) {
			this.setDontDraw();
			this.posX = i1;
			this.posY = i2;
			this.posZ = i3;
			this.posXPlus = i1 + 8;
			this.posYPlus = i2 + 8;
			this.posZPlus = i3 + 8;
			this.posXClip = i1 & 1023;
			this.posYClip = i2;
			this.posZClip = i3 & 1023;
			this.posXMinus = i1 - this.posXClip;
			this.posYMinus = i2 - this.posYClip;
			this.posZMinus = i3 - this.posZClip;
			float f4 = 6.0F;
			this.rendererBoundingBox = AxisAlignedBB.getBoundingBox((double)((float)i1 - f4), (double)((float)i2 - f4), (double)((float)i3 - f4), (double)((float)(i1 + 16) + f4), (double)((float)(i2 + 16) + f4), (double)((float)(i3 + 16) + f4));
			GL11.glNewList(this.glRenderList + 2, GL11.GL_COMPILE);
			RenderItem.renderAABB(AxisAlignedBB.getBoundingBoxFromPool((double)((float)this.posXClip - f4), (double)((float)this.posYClip - f4), (double)((float)this.posZClip - f4), (double)((float)(this.posXClip + 16) + f4), (double)((float)(this.posYClip + 16) + f4), (double)((float)(this.posZClip + 16) + f4)));
			GL11.glEndList();
			this.markDirty();
		}
	}

	private void setupGLTranslation() {
		GL11.glTranslatef((float)this.posXClip, (float)this.posYClip, (float)this.posZClip);
	}

	public void updateRenderer() {
		if(this.needsUpdate) {
			this.needsUpdate = false;
			int i1 = this.posX;
			int i2 = this.posY;
			int i3 = this.posZ;
			int i4 = this.posX + 16;
			int i5 = this.posY + 16;
			int i6 = this.posZ + 16;

			for(int i7 = 0; i7 < 2; ++i7) {
				this.skipRenderPass[i7] = true;
			}

			Chunk.isLit = false;
			HashSet<TileEntity> hashSet21 = new HashSet<TileEntity>();
			hashSet21.addAll(this.tileEntityRenderers);
			this.tileEntityRenderers.clear();
			byte b8 = 1;
			ChunkCache chunkCache9 = new ChunkCache(this.worldObj, i1 - b8, i2 - b8, i3 - b8, i4 + b8, i5 + b8, i6 + b8);
			if(!chunkCache9.getAreChunksEmpty()) {
				++chunksUpdated;
				RenderBlocks renderBlocks10 = new RenderBlocks(chunkCache9);
				for(int i11 = 0; i11 < 2; ++i11) {
					boolean renderNextLayer = false;
					boolean rendered = false;
					boolean started = false;

					for(int i15 = i2; i15 < i5; ++i15) {
						for(int i16 = i3; i16 < i6; ++i16) {
							for(int i17 = i1; i17 < i4; ++i17) {
								if (!this.worldObj.checkChunksExist(i17, i15, i16, i17, i15, i16)) {
									continue;
								}
								
								int i18 = chunkCache9.getBlockId(i17, i15, i16);
								if(i18 > 0) {
									if(!started) {
										started = true;
										GL11.glNewList(this.glRenderList + i11, GL11.GL_COMPILE);
										GL11.glPushMatrix();
										this.setupGLTranslation();
										float f19 = 1.000001F;
										GL11.glTranslatef(-8.0F, -8.0F, -8.0F);
										GL11.glScalef(f19, f19, f19);
										GL11.glTranslatef(8.0F, 8.0F, 8.0F);
										tessellator.startDrawingQuads();
										tessellator.setTranslation((double)(-this.posX), (double)(-this.posY), (double)(-this.posZ));
									}

									if(i11 == 0 && Block.blocksList[i18].hasTileEntity()) {
										TileEntity tileEntity23 = chunkCache9.getBlockTileEntity(i17, i15, i16);
										if(TileEntityRenderer.instance.hasSpecialRenderer(tileEntity23)) {
											this.tileEntityRenderers.add(tileEntity23);
										}
									}

									Block block24 = Block.blocksList[i18];
									int i20 = block24.getRenderBlockPass();
									if(i20 != i11) {
										renderNextLayer = true;
									} else if(i20 == i11) {
										rendered |= renderBlocks10.renderBlockByRenderType(block24, i17, i15, i16);
									}
								}
							}
						}
					}

					if(started) {
						tessellator.draw();
						GL11.glPopMatrix();
						GL11.glEndList();
						tessellator.setTranslation(0.0D, 0.0D, 0.0D);
					} else {
						rendered = false;
					}

					if(rendered) {
						this.skipRenderPass[i11] = false;
					}

					if(!renderNextLayer) {
						break;
					}
				}
			}

			HashSet<TileEntity> hashSet22 = new HashSet<TileEntity>();
			hashSet22.addAll(this.tileEntityRenderers);
			hashSet22.removeAll(hashSet21);
			this.tileEntities.addAll(hashSet22);
			hashSet21.removeAll(this.tileEntityRenderers);
			this.tileEntities.removeAll(hashSet21);
			this.isChunkLit = Chunk.isLit;
			this.isInitialized = true;
		}
	}

	public float distanceToEntitySquared(Entity entity1) {
		float f2 = (float)(entity1.posX - (double)this.posXPlus);
		float f3 = (float)(entity1.posY - (double)this.posYPlus);
		float f4 = (float)(entity1.posZ - (double)this.posZPlus);
		return f2 * f2 + f3 * f3 + f4 * f4;
	}

	public void setDontDraw() {
		for(int i1 = 0; i1 < 2; ++i1) {
			this.skipRenderPass[i1] = true;
		}

		this.isInFrustum = false;
		this.isInitialized = false;
	}

	public void stopRendering() {
		this.setDontDraw();
		this.worldObj = null;
	}

	public int getGLCallListForPass(int i1) {
		return !this.isInFrustum ? -1 : (!this.skipRenderPass[i1] ? this.glRenderList + i1 : -1);
	}

	public void updateInFrustum(ICamera iCamera1) {
		this.isInFrustum = iCamera1.isBoundingBoxInFrustum(this.rendererBoundingBox);
	}

	public void callOcclusionQueryList() {
		GL11.glCallList(this.glRenderList + 2);
	}

	public boolean skipAllRenderPasses() {
		return !this.isInitialized ? false : this.skipRenderPass[0] && this.skipRenderPass[1];
	}

	public void markDirty() {
		this.needsUpdate = true;
	}
}
