package net.minecraft.client.renderer;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import net.minecraft.client.Config;
import net.minecraft.client.GameSettingsValues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.client.particle.EntityCloudFX;
import net.minecraft.client.particle.EntityCritFX;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityDropParticleFX;
import net.minecraft.client.particle.EntityEnchantmentTableParticleFX;
import net.minecraft.client.particle.EntityExplodeFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityFootStepFX;
import net.minecraft.client.particle.EntityHeartFX;
import net.minecraft.client.particle.EntityHugeExplodeFX;
import net.minecraft.client.particle.EntityLargeExplodeFX;
import net.minecraft.client.particle.EntityLavaFX;
import net.minecraft.client.particle.EntityNoteFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.particle.EntitySnowShovelFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.particle.EntitySplashFX;
import net.minecraft.client.particle.EntitySuspendFX;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.EntitySorter;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.src.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemRecord;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IWorldAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.EnumMovingObjectType;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

public class LevelRenderer implements IWorldAccess {
	public List<TileEntity> tileEntities = new ArrayList<TileEntity>();
	private World worldObj;
	private RenderEngine renderEngine;
	private List<Object> worldRenderersToUpdate = new ArrayList<Object>();
	private WorldRenderer[] sortedWorldRenderers;
	private WorldRenderer[] worldRenderers;
	private int renderChunksWide;
	private int renderChunksTall;
	private int renderChunksDeep;
	private int glRenderListBase;
	private Minecraft mc;
	private RenderBlocks globalRenderBlocks;
	private IntBuffer glOcclusionQueryBase;
	private boolean occlusionEnabled = false;
	private int cloudOffsetX = 0;
	private int starGLCallList;
	private int glSkyList;
	private int glSkyList2;
	private int minBlockX;
	private int minBlockY;
	private int minBlockZ;
	private int maxBlockX;
	private int maxBlockY;
	private int maxBlockZ;
	private int renderDistance = -1;
	private int renderEntitiesStartupCounter = 2;
	private int countEntitiesTotal;
	private int countEntitiesRendered;
	private int countEntitiesHidden;
	int[] dummyBuf50k = new int[50000];
	IntBuffer occlusionResult = GLAllocation.createDirectIntBuffer(64);
	private int renderersLoaded;
	private int renderersBeingClipped;
	private int renderersBeingOccluded;
	private int renderersBeingRendered;
	private int renderersSkippingRenderPass;
	private int worldRenderersCheckIndex;
	private List<WorldRenderer> glRenderLists = new ArrayList<WorldRenderer>();
	private RenderList[] allRenderLists = new RenderList[]{new RenderList(), new RenderList(), new RenderList(), new RenderList()};
	double prevSortX = -9999.0D;
	double prevSortY = -9999.0D;
	double prevSortZ = -9999.0D;
	public float damagePartialTime;
	int frustumCheckOffset = 0;

	private long lastMovedTime = System.currentTimeMillis();
	private long lastActionTime;
	
	public LevelRenderer(Minecraft mc, RenderEngine renderEngine2) {
		this.mc = mc;
		this.renderEngine = renderEngine2;
		byte b3 = 34;
		byte b4 = 32;
		this.glRenderListBase = GLAllocation.generateDisplayLists(b3 * b3 * b4 * 3);
		this.occlusionEnabled = OpenGlCapsChecker.checkARBOcclusion();
		if(this.occlusionEnabled) {
			this.occlusionResult.clear();
			this.glOcclusionQueryBase = GLAllocation.createDirectIntBuffer(b3 * b3 * b4);
			this.glOcclusionQueryBase.clear();
			this.glOcclusionQueryBase.position(0);
			this.glOcclusionQueryBase.limit(b3 * b3 * b4);
			ARBOcclusionQuery.glGenQueriesARB(this.glOcclusionQueryBase);
		}

		this.starGLCallList = GLAllocation.generateDisplayLists(3);
		GL11.glPushMatrix();
		GL11.glNewList(this.starGLCallList, GL11.GL_COMPILE);
		this.renderStars();
		GL11.glEndList();
		GL11.glPopMatrix();
		Tessellator tessellator5 = Tessellator.instance;
		this.glSkyList = this.starGLCallList + 1;
		GL11.glNewList(this.glSkyList, GL11.GL_COMPILE);
		byte b7 = 64;
		int i8 = 256 / b7 + 2;
		float f6 = 16.0F;

		int i9;
		int i10;
		for(i9 = -b7 * i8; i9 <= b7 * i8; i9 += b7) {
			for(i10 = -b7 * i8; i10 <= b7 * i8; i10 += b7) {
				tessellator5.startDrawingQuads();
				tessellator5.addVertex((double)(i9 + 0), (double)f6, (double)(i10 + 0));
				tessellator5.addVertex((double)(i9 + b7), (double)f6, (double)(i10 + 0));
				tessellator5.addVertex((double)(i9 + b7), (double)f6, (double)(i10 + b7));
				tessellator5.addVertex((double)(i9 + 0), (double)f6, (double)(i10 + b7));
				tessellator5.draw();
			}
		}

		GL11.glEndList();
		this.glSkyList2 = this.starGLCallList + 2;
		GL11.glNewList(this.glSkyList2, GL11.GL_COMPILE);
		f6 = -16.0F;
		tessellator5.startDrawingQuads();

		for(i9 = -b7 * i8; i9 <= b7 * i8; i9 += b7) {
			for(i10 = -b7 * i8; i10 <= b7 * i8; i10 += b7) {
				tessellator5.addVertex((double)(i9 + b7), (double)f6, (double)(i10 + 0));
				tessellator5.addVertex((double)(i9 + 0), (double)f6, (double)(i10 + 0));
				tessellator5.addVertex((double)(i9 + 0), (double)f6, (double)(i10 + b7));
				tessellator5.addVertex((double)(i9 + b7), (double)f6, (double)(i10 + b7));
			}
		}

		tessellator5.draw();
		GL11.glEndList();
	}

	private void renderStars() {
		Random random1 = new Random(10842L);
		Tessellator tessellator2 = Tessellator.instance;
		tessellator2.startDrawingQuads();

		for(int i3 = 0; i3 < 1500; ++i3) {
			double d4 = (double)(random1.nextFloat() * 2.0F - 1.0F);
			double d6 = (double)(random1.nextFloat() * 2.0F - 1.0F);
			double d8 = (double)(random1.nextFloat() * 2.0F - 1.0F);
			double d10 = (double)(0.25F + random1.nextFloat() * 0.25F);
			double d12 = d4 * d4 + d6 * d6 + d8 * d8;
			if(d12 < 1.0D && d12 > 0.01D) {
				d12 = 1.0D / Math.sqrt(d12);
				d4 *= d12;
				d6 *= d12;
				d8 *= d12;
				double d14 = d4 * 100.0D;
				double d16 = d6 * 100.0D;
				double d18 = d8 * 100.0D;
				double d20 = Math.atan2(d4, d8);
				double d22 = Math.sin(d20);
				double d24 = Math.cos(d20);
				double d26 = Math.atan2(Math.sqrt(d4 * d4 + d8 * d8), d6);
				double d28 = Math.sin(d26);
				double d30 = Math.cos(d26);
				double d32 = random1.nextDouble() * Math.PI * 2.0D;
				double d34 = Math.sin(d32);
				double d36 = Math.cos(d32);

				for(int i38 = 0; i38 < 4; ++i38) {
					double d39 = 0.0D;
					double d41 = (double)((i38 & 2) - 1) * d10;
					double d43 = (double)((i38 + 1 & 2) - 1) * d10;
					double d47 = d41 * d36 - d43 * d34;
					double d49 = d43 * d36 + d41 * d34;
					double d53 = d47 * d28 + d39 * d30;
					double d55 = d39 * d28 - d47 * d30;
					double d57 = d55 * d22 - d49 * d24;
					double d61 = d49 * d22 + d55 * d24;
					tessellator2.addVertex(d14 + d57, d16 + d53, d18 + d61);
				}
			}
		}

		tessellator2.draw();
	}

	public void changeWorld(World world1) {
		if(this.worldObj != null) {
			this.worldObj.removeWorldAccess(this);
		}

		this.prevSortX = -9999.0D;
		this.prevSortY = -9999.0D;
		this.prevSortZ = -9999.0D;
		RenderManager.instance.set(world1);
		this.worldObj = world1;
		this.globalRenderBlocks = new RenderBlocks(world1);
		if(world1 != null) {
			world1.addWorldAccess(this);
			this.loadRenderers();
		}

	}

	public void loadRenderers() {
		if(this.worldObj != null) {
			Block.leaves.setGraphicsLevel(GameSettingsValues.fancyGraphics);
			
			this.renderDistance = GameSettingsValues.renderDistance;
			int i1;
			if(this.worldRenderers != null) {
				for(i1 = 0; i1 < this.worldRenderers.length; ++i1) {
					this.worldRenderers[i1].stopRendering();
				}
			}

			i1 = 64 << 3 - this.renderDistance;
			if(i1 > 400) {
				i1 = 400;
			}

			this.renderChunksWide = i1 / 16 + 1;
			this.renderChunksTall = 16;
			this.renderChunksDeep = i1 / 16 + 1;
			this.worldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
			this.sortedWorldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
			int i2 = 0;
			int i3 = 0;
			this.minBlockX = 0;
			this.minBlockY = 0;
			this.minBlockZ = 0;
			this.maxBlockX = this.renderChunksWide;
			this.maxBlockY = this.renderChunksTall;
			this.maxBlockZ = this.renderChunksDeep;

			int i4;
			for(i4 = 0; i4 < this.worldRenderersToUpdate.size(); ++i4) {
				WorldRenderer wr = (WorldRenderer) this.worldRenderersToUpdate.get(i4);
				if (wr != null) wr.needsUpdate = false;
			}

			this.worldRenderersToUpdate.clear();
			this.tileEntities.clear();

			for(i4 = 0; i4 < this.renderChunksWide; ++i4) {
				for(int i5 = 0; i5 < this.renderChunksTall; ++i5) {
					for(int i6 = 0; i6 < this.renderChunksDeep; ++i6) {
						this.worldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4] = new WorldRenderer(this.worldObj, this.tileEntities, i4 * 16, i5 * 16, i6 * 16, this.glRenderListBase + i2);
						if(this.occlusionEnabled) {
							this.worldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4].glOcclusionQuery = this.glOcclusionQueryBase.get(i3);
						}

						this.worldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4].isWaitingOnOcclusionQuery = false;
						this.worldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4].isVisible = true;
						this.worldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4].isInFrustum = true;
						this.worldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4].chunkIndex = i3++;
						this.worldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4].markDirty();
						this.sortedWorldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4] = this.worldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4];
						this.worldRenderersToUpdate.add(this.worldRenderers[(i6 * this.renderChunksTall + i5) * this.renderChunksWide + i4]);
						i2 += 3;
					}
				}
			}

			if(this.worldObj != null) {
				EntityLiving entityLiving7 = this.mc.renderViewEntity;
				if(entityLiving7 != null) {
					this.markRenderersForNewPosition(MathHelper.floor_double(entityLiving7.posX), MathHelper.floor_double(entityLiving7.posY), MathHelper.floor_double(entityLiving7.posZ));
					Arrays.sort(this.sortedWorldRenderers, new EntitySorter(entityLiving7));
				}
			}

			this.renderEntitiesStartupCounter = 2;
		}
	}

	public void renderEntities(Vec3D vec3D1, ICamera iCamera2, float f3) {
		if(this.renderEntitiesStartupCounter > 0) {
			--this.renderEntitiesStartupCounter;
		} else {
			//Profiler.startSection("prepare");
			TileEntityRenderer.instance.cacheActiveRenderInfo(this.worldObj, this.renderEngine, this.mc.fontRenderer, this.mc.renderViewEntity, f3);
			RenderManager.instance.cacheActiveRenderInfo(this.worldObj, this.renderEngine, this.mc.fontRenderer, this.mc.renderViewEntity, this.mc.gameSettings, f3);
			TileEntityRenderer.instance.func_40742_a();
			this.countEntitiesTotal = 0;
			this.countEntitiesRendered = 0;
			this.countEntitiesHidden = 0;
			EntityLiving entityLiving4 = this.mc.renderViewEntity;
			RenderManager.renderPosX = entityLiving4.lastTickPosX + (entityLiving4.posX - entityLiving4.lastTickPosX) * (double)f3;
			RenderManager.renderPosY = entityLiving4.lastTickPosY + (entityLiving4.posY - entityLiving4.lastTickPosY) * (double)f3;
			RenderManager.renderPosZ = entityLiving4.lastTickPosZ + (entityLiving4.posZ - entityLiving4.lastTickPosZ) * (double)f3;
			TileEntityRenderer.staticPlayerX = entityLiving4.lastTickPosX + (entityLiving4.posX - entityLiving4.lastTickPosX) * (double)f3;
			TileEntityRenderer.staticPlayerY = entityLiving4.lastTickPosY + (entityLiving4.posY - entityLiving4.lastTickPosY) * (double)f3;
			TileEntityRenderer.staticPlayerZ = entityLiving4.lastTickPosZ + (entityLiving4.posZ - entityLiving4.lastTickPosZ) * (double)f3;
			this.mc.entityRenderer.enableLightmap((double)f3);
			//Profiler.endStartSection("global");
			List<?> list5 = this.worldObj.getLoadedEntityList();
			this.countEntitiesTotal = list5.size();

			int i6;
			Entity entity7;
			for(i6 = 0; i6 < this.worldObj.weatherEffects.size(); ++i6) {
				entity7 = (Entity)this.worldObj.weatherEffects.get(i6);
				++this.countEntitiesRendered;
				if(entity7.isInRangeToRenderVec3D(vec3D1)) {
					RenderManager.instance.renderEntity(entity7, f3);
				}
			}

			//Profiler.endStartSection("entities");

			for(i6 = 0; i6 < list5.size(); ++i6) {
				entity7 = (Entity)list5.get(i6);
				if(entity7.isInRangeToRenderVec3D(vec3D1) && (entity7.ignoreFrustumCheck || iCamera2.isBoundingBoxInFrustum(entity7.boundingBox)) && (entity7 != this.mc.renderViewEntity || GameSettingsValues.thirdPersonView != 0 || this.mc.renderViewEntity.isPlayerSleeping()) && this.worldObj.blockExists(MathHelper.floor_double(entity7.posX), 0, MathHelper.floor_double(entity7.posZ))) {
					++this.countEntitiesRendered;
					RenderManager.instance.renderEntity(entity7, f3);
				}
			}

			//Profiler.endStartSection("tileentities");
			RenderHelper.enableStandardItemLighting();

			for(i6 = 0; i6 < this.tileEntities.size(); ++i6) {
				TileEntityRenderer.instance.renderTileEntity((TileEntity)this.tileEntities.get(i6), f3);
			}

			this.mc.entityRenderer.disableLightmap((double)f3);
			//Profiler.endSection();
		}
	}

	public String getDebugInfoRenders() {
		return "C: " + this.renderersBeingRendered + "/" + this.renderersLoaded + ". F: " + this.renderersBeingClipped + ", O: " + this.renderersBeingOccluded + ", E: " + this.renderersSkippingRenderPass;
	}

	public String getDebugInfoEntities() {
		return "E: " + this.countEntitiesRendered + "/" + this.countEntitiesTotal + ". B: " + this.countEntitiesHidden + ", I: " + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered);
	}

	private void markRenderersForNewPosition(int i1, int i2, int i3) {
		i1 -= 8;
		i2 -= 8;
		i3 -= 8;
		this.minBlockX = Integer.MAX_VALUE;
		this.minBlockY = Integer.MAX_VALUE;
		this.minBlockZ = Integer.MAX_VALUE;
		this.maxBlockX = Integer.MIN_VALUE;
		this.maxBlockY = Integer.MIN_VALUE;
		this.maxBlockZ = Integer.MIN_VALUE;
		int i4 = this.renderChunksWide * 16;
		int i5 = i4 / 2;

		for(int i6 = 0; i6 < this.renderChunksWide; ++i6) {
			int i7 = i6 * 16;
			int i8 = i7 + i5 - i1;
			if(i8 < 0) {
				i8 -= i4 - 1;
			}

			i8 /= i4;
			i7 -= i8 * i4;
			if(i7 < this.minBlockX) {
				this.minBlockX = i7;
			}

			if(i7 > this.maxBlockX) {
				this.maxBlockX = i7;
			}

			for(int i9 = 0; i9 < this.renderChunksDeep; ++i9) {
				int i10 = i9 * 16;
				int i11 = i10 + i5 - i3;
				if(i11 < 0) {
					i11 -= i4 - 1;
				}

				i11 /= i4;
				i10 -= i11 * i4;
				if(i10 < this.minBlockZ) {
					this.minBlockZ = i10;
				}

				if(i10 > this.maxBlockZ) {
					this.maxBlockZ = i10;
				}

				for(int i12 = 0; i12 < this.renderChunksTall; ++i12) {
					int i13 = i12 * 16;
					if(i13 < this.minBlockY) {
						this.minBlockY = i13;
					}

					if(i13 > this.maxBlockY) {
						this.maxBlockY = i13;
					}

					WorldRenderer worldRenderer14 = this.worldRenderers[(i9 * this.renderChunksTall + i12) * this.renderChunksWide + i6];
					boolean z15 = worldRenderer14.needsUpdate;
					worldRenderer14.setPosition(i7, i13, i10);
					if(!z15 && worldRenderer14.needsUpdate) {
						this.worldRenderersToUpdate.add(worldRenderer14);
					}
				}
			}
		}

	}

	public int sortAndRender(EntityLiving entityLiving1, int i2, double d3) {
		//Profiler.startSection("sortchunks");

		for(int i5 = 0; i5 < 10; ++i5) {
			this.worldRenderersCheckIndex = (this.worldRenderersCheckIndex + 1) % this.worldRenderers.length;
			WorldRenderer worldRenderer6 = this.worldRenderers[this.worldRenderersCheckIndex];
			if(worldRenderer6.needsUpdate && !this.worldRenderersToUpdate.contains(worldRenderer6)) {
				this.worldRenderersToUpdate.add(worldRenderer6);
			}
		}

		if(GameSettingsValues.renderDistance != this.renderDistance) {
			this.loadRenderers();
		}

		if(i2 == 0) {
			this.renderersLoaded = 0;
			this.renderersBeingClipped = 0;
			this.renderersBeingOccluded = 0;
			this.renderersBeingRendered = 0;
			this.renderersSkippingRenderPass = 0;
		}

		double d33 = entityLiving1.lastTickPosX + (entityLiving1.posX - entityLiving1.lastTickPosX) * d3;
		double d7 = entityLiving1.lastTickPosY + (entityLiving1.posY - entityLiving1.lastTickPosY) * d3;
		double d9 = entityLiving1.lastTickPosZ + (entityLiving1.posZ - entityLiving1.lastTickPosZ) * d3;
		double d11 = entityLiving1.posX - this.prevSortX;
		double d13 = entityLiving1.posY - this.prevSortY;
		double d15 = entityLiving1.posZ - this.prevSortZ;
		if(d11 * d11 + d13 * d13 + d15 * d15 > 16.0D) {
			this.prevSortX = entityLiving1.posX;
			this.prevSortY = entityLiving1.posY;
			this.prevSortZ = entityLiving1.posZ;
			this.markRenderersForNewPosition(MathHelper.floor_double(entityLiving1.posX), MathHelper.floor_double(entityLiving1.posY), MathHelper.floor_double(entityLiving1.posZ));
			Arrays.sort(this.sortedWorldRenderers, new EntitySorter(entityLiving1));
		}

		RenderHelper.disableStandardItemLighting();
		byte b17 = 0;
		int i34;
		if(this.occlusionEnabled && GameSettingsValues.advancedOpengl && i2 == 0) {
			byte b18 = 0;
			int i19 = 16;
			this.checkOcclusionQueryResult(b18, i19);

			for(int i20 = b18; i20 < i19; ++i20) {
				this.sortedWorldRenderers[i20].isVisible = true;
			}

			//Profiler.endStartSection("render");
			i34 = b17 + this.renderSortedRenderers(b18, i19, i2, d3);

			do {
				//Profiler.endStartSection("occ");
				int i35 = i19;
				i19 *= 2;
				if(i19 > this.sortedWorldRenderers.length) {
					i19 = this.sortedWorldRenderers.length;
				}

				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glDisable(GL11.GL_FOG);
				GL11.glColorMask(false, false, false, false);
				GL11.glDepthMask(false);
				//Profiler.startSection("check");
				this.checkOcclusionQueryResult(i35, i19);
				//Profiler.endSection();
				GL11.glPushMatrix();
				float f36 = 0.0F;
				float f21 = 0.0F;
				float f22 = 0.0F;

				for(int armorValue = i35; armorValue < i19; ++armorValue) {
					if(this.sortedWorldRenderers[armorValue].skipAllRenderPasses()) {
						this.sortedWorldRenderers[armorValue].isInFrustum = false;
					} else {
						if(!this.sortedWorldRenderers[armorValue].isInFrustum) {
							this.sortedWorldRenderers[armorValue].isVisible = true;
						}

						if(this.sortedWorldRenderers[armorValue].isInFrustum && !this.sortedWorldRenderers[armorValue].isWaitingOnOcclusionQuery) {
							float f24 = MathHelper.sqrt_float(this.sortedWorldRenderers[armorValue].distanceToEntitySquared(entityLiving1));
							int i25 = (int)(1.0F + f24 / 128.0F);
							if(this.cloudOffsetX % i25 == armorValue % i25) {
								WorldRenderer worldRenderer26 = this.sortedWorldRenderers[armorValue];
								float f27 = (float)((double)worldRenderer26.posXMinus - d33);
								float f28 = (float)((double)worldRenderer26.posYMinus - d7);
								float f29 = (float)((double)worldRenderer26.posZMinus - d9);
								float f30 = f27 - f36;
								float f31 = f28 - f21;
								float f32 = f29 - f22;
								if(f30 != 0.0F || f31 != 0.0F || f32 != 0.0F) {
									GL11.glTranslatef(f30, f31, f32);
									f36 += f30;
									f21 += f31;
									f22 += f32;
								}

								//Profiler.startSection("bb");
								ARBOcclusionQuery.glBeginQueryARB(GL15.GL_SAMPLES_PASSED, this.sortedWorldRenderers[armorValue].glOcclusionQuery);
								this.sortedWorldRenderers[armorValue].callOcclusionQueryList();
								ARBOcclusionQuery.glEndQueryARB(GL15.GL_SAMPLES_PASSED);
								//Profiler.endSection();
								this.sortedWorldRenderers[armorValue].isWaitingOnOcclusionQuery = true;
							}
						}
					}
				}

				GL11.glPopMatrix();

				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glEnable(GL11.GL_FOG);
				//Profiler.endStartSection("render");
				i34 += this.renderSortedRenderers(i35, i19, i2, d3);
			} while(i19 < this.sortedWorldRenderers.length);
		} else {
			//Profiler.endStartSection("render");
			i34 = b17 + this.renderSortedRenderers(0, this.sortedWorldRenderers.length, i2, d3);
		}

		//Profiler.endSection();
		return i34;
	}

	private void checkOcclusionQueryResult(int i1, int i2) {
		for(int i3 = i1; i3 < i2; ++i3) {
			if(this.sortedWorldRenderers[i3].isWaitingOnOcclusionQuery) {
				this.occlusionResult.clear();
				ARBOcclusionQuery.glGetQueryObjectuARB(this.sortedWorldRenderers[i3].glOcclusionQuery, GL15.GL_QUERY_RESULT_AVAILABLE, this.occlusionResult);
				if(this.occlusionResult.get(0) != 0) {
					this.sortedWorldRenderers[i3].isWaitingOnOcclusionQuery = false;
					this.occlusionResult.clear();
					ARBOcclusionQuery.glGetQueryObjectuARB(this.sortedWorldRenderers[i3].glOcclusionQuery, GL15.GL_QUERY_RESULT, this.occlusionResult);
					this.sortedWorldRenderers[i3].isVisible = this.occlusionResult.get(0) != 0;
				}
			}
		}

	}

	private int renderSortedRenderers(int i1, int i2, int i3, double d4) {
		this.glRenderLists.clear();
		int i6 = 0;

		for(int i7 = i1; i7 < i2; ++i7) {
			if(i3 == 0) {
				++this.renderersLoaded;
				if(this.sortedWorldRenderers[i7].skipRenderPass[i3]) {
					++this.renderersSkippingRenderPass;
				} else if(!this.sortedWorldRenderers[i7].isInFrustum) {
					++this.renderersBeingClipped;
				} else if(this.occlusionEnabled && !this.sortedWorldRenderers[i7].isVisible) {
					++this.renderersBeingOccluded;
				} else {
					++this.renderersBeingRendered;
				}
			}

			if(!this.sortedWorldRenderers[i7].skipRenderPass[i3] && this.sortedWorldRenderers[i7].isInFrustum && (!this.occlusionEnabled || this.sortedWorldRenderers[i7].isVisible)) {
				int i8 = this.sortedWorldRenderers[i7].getGLCallListForPass(i3);
				if(i8 >= 0) {
					this.glRenderLists.add(this.sortedWorldRenderers[i7]);
					++i6;
				}
			}
		}

		EntityLiving entityLiving19 = this.mc.renderViewEntity;
		double d20 = entityLiving19.lastTickPosX + (entityLiving19.posX - entityLiving19.lastTickPosX) * d4;
		double d10 = entityLiving19.lastTickPosY + (entityLiving19.posY - entityLiving19.lastTickPosY) * d4;
		double d12 = entityLiving19.lastTickPosZ + (entityLiving19.posZ - entityLiving19.lastTickPosZ) * d4;
		int i14 = 0;

		int i15;
		for(i15 = 0; i15 < this.allRenderLists.length; ++i15) {
			this.allRenderLists[i15].func_859_b();
		}

		for(i15 = 0; i15 < this.glRenderLists.size(); ++i15) {
			WorldRenderer worldRenderer16 = (WorldRenderer)this.glRenderLists.get(i15);
			int i17 = -1;

			for(int i18 = 0; i18 < i14; ++i18) {
				if(this.allRenderLists[i18].func_862_a(worldRenderer16.posXMinus, worldRenderer16.posYMinus, worldRenderer16.posZMinus)) {
					i17 = i18;
				}
			}

			if(i17 < 0) {
				i17 = i14++;
				this.allRenderLists[i17].func_861_a(worldRenderer16.posXMinus, worldRenderer16.posYMinus, worldRenderer16.posZMinus, d20, d10, d12);
			}

			this.allRenderLists[i17].func_858_a(worldRenderer16.getGLCallListForPass(i3));
		}

		this.renderAllRenderLists(i3, d4);
		return i6;
	}

	public void renderAllRenderLists(int i1, double d2) {
		this.mc.entityRenderer.enableLightmap(d2);

		for(int i4 = 0; i4 < this.allRenderLists.length; ++i4) {
			this.allRenderLists[i4].func_860_a();
		}

		this.mc.entityRenderer.disableLightmap(d2);
	}

	public void updateClouds() {
		++this.cloudOffsetX;
	}

	public void renderSky(float f1) {
		if(this.mc.theWorld.worldProvider.worldType == 1) {
			GL11.glDisable(GL11.GL_FOG);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			RenderHelper.disableStandardItemLighting();
			GL11.glDepthMask(false);
			this.renderEngine.bindTexture(this.renderEngine.getTexture("/misc/tunnel.png"));
			Tessellator tessellator19 = Tessellator.instance;

			for(int i20 = 0; i20 < 6; ++i20) {
				GL11.glPushMatrix();
				if(i20 == 1) {
					GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				}

				if(i20 == 2) {
					GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				}

				if(i20 == 3) {
					GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
				}

				if(i20 == 4) {
					GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
				}

				if(i20 == 5) {
					GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
				}

				tessellator19.startDrawingQuads();
				tessellator19.setColorOpaque_I(1579032);
				tessellator19.addVertexWithUV(-100.0D, -100.0D, -100.0D, 0.0D, 0.0D);
				tessellator19.addVertexWithUV(-100.0D, -100.0D, 100.0D, 0.0D, 16.0D);
				tessellator19.addVertexWithUV(100.0D, -100.0D, 100.0D, 16.0D, 16.0D);
				tessellator19.addVertexWithUV(100.0D, -100.0D, -100.0D, 16.0D, 0.0D);
				tessellator19.draw();
				GL11.glPopMatrix();
			}

			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		} else if(this.mc.theWorld.worldProvider.canSleepHere() && !this.mc.theWorld.worldProvider.noCelestials()) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			Vec3D vec3D2 = this.worldObj.getSkyColor(this.mc.renderViewEntity, f1);
			float f3 = (float)vec3D2.xCoord;
			float f4 = (float)vec3D2.yCoord;
			float f5 = (float)vec3D2.zCoord;
			float f7;
			float f8;

			GL11.glColor3f(f3, f4, f5);
			Tessellator tessellator21 = Tessellator.instance;
			GL11.glDepthMask(false);
			GL11.glEnable(GL11.GL_FOG);
			GL11.glColor3f(f3, f4, f5);
			GL11.glCallList(this.glSkyList);
			GL11.glDisable(GL11.GL_FOG);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			RenderHelper.disableStandardItemLighting();
			
			
			float f9;
			float f10;
			float f11;
			float f12;
			float f15;
			int i25;
			
			if(GameRules.boolRule("hasSunriseSunset")) {
				float[] f22 = this.worldObj.worldProvider.calcSunriseSunsetColors(this.worldObj.getCelestialAngle(f1), f1);
				if(f22 != null) {
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glShadeModel(GL11.GL_SMOOTH);
					GL11.glPushMatrix();
					GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
					GL11.glRotatef(MathHelper.sin(this.worldObj.getCelestialAngleRadians(f1)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
					GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
					f8 = f22[0];
					f9 = f22[1];
					f10 = f22[2];
					float f13;
	
					tessellator21.startDrawing(6);
					tessellator21.setColorRGBA_F(f8, f9, f10, f22[3]);
					tessellator21.addVertex(0.0D, 100.0D, 0.0D);
					byte b24 = 16;
					tessellator21.setColorRGBA_F(f22[0], f22[1], f22[2], 0.0F);
	
					for(i25 = 0; i25 <= b24; ++i25) {
						f13 = (float)i25 * (float)Math.PI * 2.0F / (float)b24;
						float f14 = MathHelper.sin(f13);
						f15 = MathHelper.cos(f13);
						tessellator21.addVertex((double)(f14 * 120.0F), (double)(f15 * 120.0F), (double)(-f15 * 40.0F * f22[3]));
					}
	
					tessellator21.draw();
					GL11.glPopMatrix();
					GL11.glShadeModel(GL11.GL_FLAT);
				}
			}

			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GL11.glPushMatrix();
			f7 = 1.0F - this.worldObj.getRainStrength(f1) - this.worldObj.getSandstormingStrength(f1);
			f8 = 0.0F;
			f9 = 0.0F;
			f10 = 0.0F;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, f7);
			GL11.glTranslatef(f8, f9, f10);
			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(this.worldObj.getCelestialAngle(f1) * 360.0F, 1.0F, 0.0F, 0.0F);
			f11 = 30.0F;
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/terrain/sun.png"));
			tessellator21.startDrawingQuads();
			tessellator21.addVertexWithUV((double)(-f11), 100.0D, (double)(-f11), 0.0D, 0.0D);
			tessellator21.addVertexWithUV((double)f11, 100.0D, (double)(-f11), 1.0D, 0.0D);
			tessellator21.addVertexWithUV((double)f11, 100.0D, (double)f11, 1.0D, 1.0D);
			tessellator21.addVertexWithUV((double)(-f11), 100.0D, (double)f11, 0.0D, 1.0D);
			tessellator21.draw();
			f11 = 20.0F;
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/terrain/moon_phases.png"));
			i25 = this.worldObj.getMoonPhase(f1);
			int i26 = i25 % 4;
			int i27 = i25 / 4 % 2;
			f15 = (float)(i26 + 0) / 4.0F;
			float f16 = (float)(i27 + 0) / 2.0F;
			float f17 = (float)(i26 + 1) / 4.0F;
			float f18 = (float)(i27 + 1) / 2.0F;
			tessellator21.startDrawingQuads();
			tessellator21.addVertexWithUV((double)(-f11), -100.0D, (double)f11, (double)f17, (double)f18);
			tessellator21.addVertexWithUV((double)f11, -100.0D, (double)f11, (double)f15, (double)f18);
			tessellator21.addVertexWithUV((double)f11, -100.0D, (double)(-f11), (double)f15, (double)f16);
			tessellator21.addVertexWithUV((double)(-f11), -100.0D, (double)(-f11), (double)f17, (double)f16);
			tessellator21.draw();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			f12 = this.worldObj.getStarBrightness(f1) * f7;
			if(f12 > 0.0F) {
				GL11.glColor4f(f12, f12, f12, f12);
				GL11.glCallList(this.starGLCallList);
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_FOG);
			GL11.glPopMatrix();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			
			if (GameRules.boolRule("colouredWater")) {
				this.setBottomOfTheWorldColours(f3, f4, f5, f1);
			} else {
				GL11.glColor3f(0.0F, 0.0F, 0.0F);
			}
						
			double d23 = this.mc.thePlayer.getCurrentNodeVec3d(f1).yCoord - this.worldObj.getSeaLevelForRendering();
			if(d23 < 0.0D) {
				GL11.glPushMatrix();
				GL11.glTranslatef(0.0F, 12.0F, 0.0F);
				GL11.glCallList(this.glSkyList2);
				GL11.glPopMatrix();
				f9 = 1.0F;
				f10 = -((float)(d23 + 65.0D));
				f11 = -f9;
				tessellator21.startDrawingQuads();
				tessellator21.setColorRGBA_I(0, 255);
				tessellator21.addVertex((double)(-f9), (double)f10, (double)f9);
				tessellator21.addVertex((double)f9, (double)f10, (double)f9);
				tessellator21.addVertex((double)f9, (double)f11, (double)f9);
				tessellator21.addVertex((double)(-f9), (double)f11, (double)f9);
				tessellator21.addVertex((double)(-f9), (double)f11, (double)(-f9));
				tessellator21.addVertex((double)f9, (double)f11, (double)(-f9));
				tessellator21.addVertex((double)f9, (double)f10, (double)(-f9));
				tessellator21.addVertex((double)(-f9), (double)f10, (double)(-f9));
				tessellator21.addVertex((double)f9, (double)f11, (double)(-f9));
				tessellator21.addVertex((double)f9, (double)f11, (double)f9);
				tessellator21.addVertex((double)f9, (double)f10, (double)f9);
				tessellator21.addVertex((double)f9, (double)f10, (double)(-f9));
				tessellator21.addVertex((double)(-f9), (double)f10, (double)(-f9));
				tessellator21.addVertex((double)(-f9), (double)f10, (double)f9);
				tessellator21.addVertex((double)(-f9), (double)f11, (double)f9);
				tessellator21.addVertex((double)(-f9), (double)f11, (double)(-f9));
				tessellator21.addVertex((double)(-f9), (double)f11, (double)(-f9));
				tessellator21.addVertex((double)(-f9), (double)f11, (double)f9);
				tessellator21.addVertex((double)f9, (double)f11, (double)f9);
				tessellator21.addVertex((double)f9, (double)f11, (double)(-f9));
				tessellator21.draw();
			}

			this.setBottomOfTheWorldColours(f3, f4, f5, f1);

			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, -((float)(d23 - 16.0D)), 0.0F);
			GL11.glCallList(this.glSkyList2);
			GL11.glPopMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(true);
		}
	}
	
	public void setBottomOfTheWorldColours(float f3, float f4, float f5, float f1) {
		if(this.worldObj.worldProvider.isSkyColored()) {
			//GL11.glColor3f(f3 * 0.2F + 0.04F, f4 * 0.2F + 0.04F, f5 * 0.6F + 0.1F);

			Vec3D vec3D = this.worldObj.getSkyColorBottom(this.mc.renderViewEntity, f1);
			
			GL11.glColor3d(vec3D.xCoord, vec3D.yCoord, vec3D.zCoord);
		} else {
			GL11.glColor3f(f3, f4, f5);
		}
	}

	public void renderClouds(float f1) {
		if(this.mc.theWorld.worldProvider.canSleepHere()) {
			if(GameSettingsValues.fancyGraphics) {
				this.renderCloudsFancy(f1);
			} else {
				GL11.glDisable(GL11.GL_CULL_FACE);
				float f2 = (float)(this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double)f1);
				byte b3 = 32;
				int i4 = 256 / b3;
				Tessellator tessellator5 = Tessellator.instance;
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/environment/clouds.png"));
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				Vec3D vec3D6 = this.worldObj.getCloudColor(f1);
				float f7 = (float)vec3D6.xCoord;
				float f8 = (float)vec3D6.yCoord;
				float f9 = (float)vec3D6.zCoord;
				float f10;

				f10 = 4.8828125E-4F;
				double d24 = (double)((float)this.cloudOffsetX + f1);
				double d13 = this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double)f1 + d24 * (double)0.03F;
				double d15 = this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double)f1;
				int i17 = MathHelper.floor_double(d13 / 2048.0D);
				int i18 = MathHelper.floor_double(d15 / 2048.0D);
				d13 -= (double)(i17 * 2048);
				d15 -= (double)(i18 * 2048);
				float f19 = this.worldObj.worldProvider.getCloudHeight() - f2 + 0.33F;
				float f20 = (float)(d13 * (double)f10);
				float f21 = (float)(d15 * (double)f10);
				tessellator5.startDrawingQuads();
				tessellator5.setColorRGBA_F(f7, f8, f9, 0.8F);

				for(int i22 = -b3 * i4; i22 < b3 * i4; i22 += b3) {
					for(int armorValue = -b3 * i4; armorValue < b3 * i4; armorValue += b3) {
						tessellator5.addVertexWithUV((double)(i22 + 0), (double)f19, (double)(armorValue + b3), (double)((float)(i22 + 0) * f10 + f20), (double)((float)(armorValue + b3) * f10 + f21));
						tessellator5.addVertexWithUV((double)(i22 + b3), (double)f19, (double)(armorValue + b3), (double)((float)(i22 + b3) * f10 + f20), (double)((float)(armorValue + b3) * f10 + f21));
						tessellator5.addVertexWithUV((double)(i22 + b3), (double)f19, (double)(armorValue + 0), (double)((float)(i22 + b3) * f10 + f20), (double)((float)(armorValue + 0) * f10 + f21));
						tessellator5.addVertexWithUV((double)(i22 + 0), (double)f19, (double)(armorValue + 0), (double)((float)(i22 + 0) * f10 + f20), (double)((float)(armorValue + 0) * f10 + f21));
					}
				}

				tessellator5.draw();
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_CULL_FACE);
			}
		}
		GL11.glEnable(GL11.GL_FOG);
	}

	public boolean func_27307_a(double d1, double d3, double d5, float f7) {
		return false;
	}

	public void renderCloudsFancy(float f1) {
		GL11.glDisable(GL11.GL_CULL_FACE);
		float f2 = (float)(this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * (double)f1);
		Tessellator tessellator3 = Tessellator.instance;
		float f4 = 12.0F;
		float f5 = 4.0F;
		
		double d6 = (double)((float)this.cloudOffsetX + f1);
		double d8 = (this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * (double)f1 + d6 * (double)0.03F) / (double)f4;
		double d10 = (this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * (double)f1) / (double)f4 + (double)0.33F;
		float f12 = this.worldObj.worldProvider.getCloudHeight() - f2 + 0.33F;
		int i = MathHelper.floor_double(d8 / 2048.0D);
		int j = MathHelper.floor_double(d10 / 2048.0D);
		d8 -= (double)(i * 2048);
		d10 -= (double)(j * 2048);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/environment/clouds.png"));
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Vec3D vec3D15 = this.worldObj.getCloudColor(f1);
		float r = (float)vec3D15.xCoord;
		float g = (float)vec3D15.yCoord;
		float b = (float)vec3D15.zCoord; 
		float f19;
		float f20;
		float f21;

		f19 = (float)(d8 * 0.0D);
		f20 = (float)(d10 * 0.0D);
		f21 = 0.00390625F;
		f19 = (float)MathHelper.floor_double(d8) * f21;
		f20 = (float)MathHelper.floor_double(d10) * f21;
		float f22 = (float)(d8 - (double)MathHelper.floor_double(d8));
		float f23 = (float)(d10 - (double)MathHelper.floor_double(d10));
		byte b24 = 8;
		byte b25 = 4;
		float f26 = 9.765625E-4F;
		GL11.glScalef(f4, 1.0F, f4);

		for(int l = 0; l < 2; ++l) {
			if(l == 0) {
				GL11.glColorMask(false, false, false, false);
			} else {
				GL11.glColorMask(true, true, true, true);
			}

			for(int i28 = -b25 + 1; i28 <= b25; ++i28) {
				for(int i29 = -b25 + 1; i29 <= b25; ++i29) {
					tessellator3.startDrawingQuads();
					float f30 = (float)(i28 * b24);
					float f31 = (float)(i29 * b24);
					float f32 = f30 - f22;
					float f33 = f31 - f23;
					if(f12 > -f5 - 1.0F) {
						tessellator3.setColorRGBA_F(r * 0.7F, g * 0.7F, b * 0.7F, 0.8F);
						tessellator3.setNormal(0.0F, -1.0F, 0.0F);
						tessellator3.addVertexWithUV((double)(f32 + 0.0F), (double)(f12 + 0.0F), (double)(f33 + (float)b24), (double)((f30 + 0.0F) * f21 + f19), (double)((f31 + (float)b24) * f21 + f20));
						tessellator3.addVertexWithUV((double)(f32 + (float)b24), (double)(f12 + 0.0F), (double)(f33 + (float)b24), (double)((f30 + (float)b24) * f21 + f19), (double)((f31 + (float)b24) * f21 + f20));
						tessellator3.addVertexWithUV((double)(f32 + (float)b24), (double)(f12 + 0.0F), (double)(f33 + 0.0F), (double)((f30 + (float)b24) * f21 + f19), (double)((f31 + 0.0F) * f21 + f20));
						tessellator3.addVertexWithUV((double)(f32 + 0.0F), (double)(f12 + 0.0F), (double)(f33 + 0.0F), (double)((f30 + 0.0F) * f21 + f19), (double)((f31 + 0.0F) * f21 + f20));
					}

					if(f12 <= f5 + 1.0F) {
						tessellator3.setColorRGBA_F(r, g, b, 0.8F);
						tessellator3.setNormal(0.0F, 1.0F, 0.0F);
						tessellator3.addVertexWithUV((double)(f32 + 0.0F), (double)(f12 + f5 - f26), (double)(f33 + (float)b24), (double)((f30 + 0.0F) * f21 + f19), (double)((f31 + (float)b24) * f21 + f20));
						tessellator3.addVertexWithUV((double)(f32 + (float)b24), (double)(f12 + f5 - f26), (double)(f33 + (float)b24), (double)((f30 + (float)b24) * f21 + f19), (double)((f31 + (float)b24) * f21 + f20));
						tessellator3.addVertexWithUV((double)(f32 + (float)b24), (double)(f12 + f5 - f26), (double)(f33 + 0.0F), (double)((f30 + (float)b24) * f21 + f19), (double)((f31 + 0.0F) * f21 + f20));
						tessellator3.addVertexWithUV((double)(f32 + 0.0F), (double)(f12 + f5 - f26), (double)(f33 + 0.0F), (double)((f30 + 0.0F) * f21 + f19), (double)((f31 + 0.0F) * f21 + f20));
					}

					tessellator3.setColorRGBA_F(r * 0.9F, g * 0.9F, b * 0.9F, 0.8F);
					int i34;
					if(i28 > -1) {
						tessellator3.setNormal(-1.0F, 0.0F, 0.0F);

						for(i34 = 0; i34 < b24; ++i34) {
							tessellator3.addVertexWithUV((double)(f32 + (float)i34 + 0.0F), (double)(f12 + 0.0F), (double)(f33 + (float)b24), (double)((f30 + (float)i34 + 0.5F) * f21 + f19), (double)((f31 + (float)b24) * f21 + f20));
							tessellator3.addVertexWithUV((double)(f32 + (float)i34 + 0.0F), (double)(f12 + f5), (double)(f33 + (float)b24), (double)((f30 + (float)i34 + 0.5F) * f21 + f19), (double)((f31 + (float)b24) * f21 + f20));
							tessellator3.addVertexWithUV((double)(f32 + (float)i34 + 0.0F), (double)(f12 + f5), (double)(f33 + 0.0F), (double)((f30 + (float)i34 + 0.5F) * f21 + f19), (double)((f31 + 0.0F) * f21 + f20));
							tessellator3.addVertexWithUV((double)(f32 + (float)i34 + 0.0F), (double)(f12 + 0.0F), (double)(f33 + 0.0F), (double)((f30 + (float)i34 + 0.5F) * f21 + f19), (double)((f31 + 0.0F) * f21 + f20));
						}
					}

					if(i28 <= 1) {
						tessellator3.setNormal(1.0F, 0.0F, 0.0F);

						for(i34 = 0; i34 < b24; ++i34) {
							tessellator3.addVertexWithUV((double)(f32 + (float)i34 + 1.0F - f26), (double)(f12 + 0.0F), (double)(f33 + (float)b24), (double)((f30 + (float)i34 + 0.5F) * f21 + f19), (double)((f31 + (float)b24) * f21 + f20));
							tessellator3.addVertexWithUV((double)(f32 + (float)i34 + 1.0F - f26), (double)(f12 + f5), (double)(f33 + (float)b24), (double)((f30 + (float)i34 + 0.5F) * f21 + f19), (double)((f31 + (float)b24) * f21 + f20));
							tessellator3.addVertexWithUV((double)(f32 + (float)i34 + 1.0F - f26), (double)(f12 + f5), (double)(f33 + 0.0F), (double)((f30 + (float)i34 + 0.5F) * f21 + f19), (double)((f31 + 0.0F) * f21 + f20));
							tessellator3.addVertexWithUV((double)(f32 + (float)i34 + 1.0F - f26), (double)(f12 + 0.0F), (double)(f33 + 0.0F), (double)((f30 + (float)i34 + 0.5F) * f21 + f19), (double)((f31 + 0.0F) * f21 + f20));
						}
					}

					tessellator3.setColorRGBA_F(r * 0.8F, g * 0.8F, b * 0.8F, 0.8F);
					if(i29 > -1) {
						tessellator3.setNormal(0.0F, 0.0F, -1.0F);

						for(i34 = 0; i34 < b24; ++i34) {
							tessellator3.addVertexWithUV((double)(f32 + 0.0F), (double)(f12 + f5), (double)(f33 + (float)i34 + 0.0F), (double)((f30 + 0.0F) * f21 + f19), (double)((f31 + (float)i34 + 0.5F) * f21 + f20));
							tessellator3.addVertexWithUV((double)(f32 + (float)b24), (double)(f12 + f5), (double)(f33 + (float)i34 + 0.0F), (double)((f30 + (float)b24) * f21 + f19), (double)((f31 + (float)i34 + 0.5F) * f21 + f20));
							tessellator3.addVertexWithUV((double)(f32 + (float)b24), (double)(f12 + 0.0F), (double)(f33 + (float)i34 + 0.0F), (double)((f30 + (float)b24) * f21 + f19), (double)((f31 + (float)i34 + 0.5F) * f21 + f20));
							tessellator3.addVertexWithUV((double)(f32 + 0.0F), (double)(f12 + 0.0F), (double)(f33 + (float)i34 + 0.0F), (double)((f30 + 0.0F) * f21 + f19), (double)((f31 + (float)i34 + 0.5F) * f21 + f20));
						}
					}

					if(i29 <= 1) {
						tessellator3.setNormal(0.0F, 0.0F, 1.0F);

						for(i34 = 0; i34 < b24; ++i34) {
							tessellator3.addVertexWithUV((double)(f32 + 0.0F), (double)(f12 + f5), (double)(f33 + (float)i34 + 1.0F - f26), (double)((f30 + 0.0F) * f21 + f19), (double)((f31 + (float)i34 + 0.5F) * f21 + f20));
							tessellator3.addVertexWithUV((double)(f32 + (float)b24), (double)(f12 + f5), (double)(f33 + (float)i34 + 1.0F - f26), (double)((f30 + (float)b24) * f21 + f19), (double)((f31 + (float)i34 + 0.5F) * f21 + f20));
							tessellator3.addVertexWithUV((double)(f32 + (float)b24), (double)(f12 + 0.0F), (double)(f33 + (float)i34 + 1.0F - f26), (double)((f30 + (float)b24) * f21 + f19), (double)((f31 + (float)i34 + 0.5F) * f21 + f20));
							tessellator3.addVertexWithUV((double)(f32 + 0.0F), (double)(f12 + 0.0F), (double)(f33 + (float)i34 + 1.0F - f26), (double)((f30 + 0.0F) * f21 + f19), (double)((f31 + (float)i34 + 0.5F) * f21 + f20));
						}
					}

					tessellator3.draw();
				}
			}
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}
	
	public boolean isMoving(EntityLiving entityliving) {
		boolean moving = this.isMovingNow(entityliving);
		if(moving) {
			this.lastMovedTime = System.currentTimeMillis();
			return true;
		} else {
			return System.currentTimeMillis() - this.lastMovedTime < 2000L;
		}
	}

	private boolean isMovingNow(EntityLiving entityliving) {
		double maxDiff = 0.001D;
		return entityliving.isJumping ? true : (entityliving.isSneaking() ? true : ((double)entityliving.prevSwingProgress > maxDiff ? true : (this.mc.mouseHelper.deltaX != 0 ? true : (this.mc.mouseHelper.deltaY != 0 ? true : (Math.abs(entityliving.posX - entityliving.prevPosX) > maxDiff ? true : (Math.abs(entityliving.posY - entityliving.prevPosY) > maxDiff ? true : Math.abs(entityliving.posZ - entityliving.prevPosZ) > maxDiff))))));
	}
	
	public boolean isActing() {
		boolean acting = this.isActingNow();
		if(acting) {
			this.lastActionTime = System.currentTimeMillis();
			return true;
		} else {
			return System.currentTimeMillis() - this.lastActionTime < 500L;
		}
	}

	public boolean isActingNow() {
		return Mouse.isButtonDown(0) ? true : Mouse.isButtonDown(1);
	}
	
	public boolean updateRenderers(EntityLiving entityliving, boolean flag) {
		if(this.worldRenderersToUpdate.size() <= 0) {
			return false;
		} else {
			int num = 0;
			int maxNum = Config.getUpdatesPerFrame();
			if(Config.isDynamicUpdates() && !this.isMoving(entityliving)) {
				maxNum *= 3;
			}

			byte NOT_IN_FRUSTRUM_MUL = 4;
			int numValid = 0;
			WorldRenderer wrBest = null;
			float distSqBest = Float.MAX_VALUE;
			int indexBest = -1;

			int dstIndex;
			for(dstIndex = 0; dstIndex < this.worldRenderersToUpdate.size(); ++dstIndex) {
				WorldRenderer i = (WorldRenderer)this.worldRenderersToUpdate.get(dstIndex);
				if(i != null) {
					++numValid;
					if(!i.needsUpdate) {
						this.worldRenderersToUpdate.set(dstIndex, (Object)null);
					} else {
						float wr = i.distanceToEntitySquared(entityliving);
						if(wr <= 256.0F && this.isActingNow()) {
							i.updateRenderer();
							i.needsUpdate = false;
							this.worldRenderersToUpdate.set(dstIndex, (Object)null);
							++num;
						} else {
							if(wr > 256.0F && num >= maxNum) {
								break;
							}

							if(!i.isInFrustum) {
								wr *= (float)NOT_IN_FRUSTRUM_MUL;
							}

							if(wrBest == null) {
								wrBest = i;
								distSqBest = wr;
								indexBest = dstIndex;
							} else if(wr < distSqBest) {
								wrBest = i;
								distSqBest = wr;
								indexBest = dstIndex;
							}
						}
					}
				}
			}

			int i16;
			if(wrBest != null) {
				wrBest.updateRenderer();
				wrBest.needsUpdate = false;
				this.worldRenderersToUpdate.set(indexBest, (Object)null);
				++num;
				float f15 = distSqBest / 5.0F;

				for(i16 = 0; i16 < this.worldRenderersToUpdate.size() && num < maxNum; ++i16) {
					WorldRenderer worldRenderer17 = (WorldRenderer)this.worldRenderersToUpdate.get(i16);
					if(worldRenderer17 != null) {
						float distSq = worldRenderer17.distanceToEntitySquared(entityliving);
						if(!worldRenderer17.isInFrustum) {
							distSq *= (float)NOT_IN_FRUSTRUM_MUL;
						}

						float diffDistSq = Math.abs(distSq - distSqBest);
						if(diffDistSq < f15) {
							worldRenderer17.updateRenderer();
							worldRenderer17.needsUpdate = false;
							this.worldRenderersToUpdate.set(i16, (Object)null);
							++num;
						}
					}
				}
			}

			if(numValid == 0) {
				this.worldRenderersToUpdate.clear();
			}

			if(this.worldRenderersToUpdate.size() > 100 && numValid < this.worldRenderersToUpdate.size() * 4 / 5) {
				dstIndex = 0;

				for(i16 = 0; i16 < this.worldRenderersToUpdate.size(); ++i16) {
					Object object18 = this.worldRenderersToUpdate.get(i16);
					if(object18 != null) {
						if(i16 != dstIndex) {
							this.worldRenderersToUpdate.set(dstIndex, object18);
						}

						++dstIndex;
					}
				}

				for(i16 = this.worldRenderersToUpdate.size() - 1; i16 >= dstIndex; --i16) {
					this.worldRenderersToUpdate.remove(i16);
				}
			}

			return true;
		}
	}

	public void drawBlockBreaking(EntityPlayer entityPlayer1, MovingObjectPosition movingObjectPosition2, int i3, ItemStack itemStack4, float f5) {
		Tessellator tessellator6 = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, (MathHelper.sin((float)System.currentTimeMillis() / 100.0F) * 0.2F + 0.4F) * 0.5F);
		int i8;
		if(i3 == 0) {
			if(this.damagePartialTime > 0.0F) {
				GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_SRC_COLOR);
				int i7 = this.renderEngine.getTexture("/terrain.png");
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, i7);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
				GL11.glPushMatrix();
				i8 = this.worldObj.getBlockId(movingObjectPosition2.blockX, movingObjectPosition2.blockY, movingObjectPosition2.blockZ);
				Block block9 = i8 > 0 ? Block.blocksList[i8] : null;
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glPolygonOffset(-3.0F, -3.0F);
				GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
				double d10 = entityPlayer1.lastTickPosX + (entityPlayer1.posX - entityPlayer1.lastTickPosX) * (double)f5;
				double d12 = entityPlayer1.lastTickPosY + (entityPlayer1.posY - entityPlayer1.lastTickPosY) * (double)f5;
				double d14 = entityPlayer1.lastTickPosZ + (entityPlayer1.posZ - entityPlayer1.lastTickPosZ) * (double)f5;
				if(block9 == null) {
					block9 = Block.stone;
				}

				GL11.glEnable(GL11.GL_ALPHA_TEST);
				tessellator6.startDrawingQuads();
				tessellator6.setTranslation(-d10, -d12, -d14);
				tessellator6.disableColor();
				this.globalRenderBlocks.renderBlockUsingTexture(block9, movingObjectPosition2.blockX, movingObjectPosition2.blockY, movingObjectPosition2.blockZ, 240 + (int)(this.damagePartialTime * 10.0F));
				tessellator6.draw();
				tessellator6.setTranslation(0.0D, 0.0D, 0.0D);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glPolygonOffset(0.0F, 0.0F);
				GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
				GL11.glDepthMask(true);
				GL11.glPopMatrix();
			}
		} else if(itemStack4 != null) {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			float f16 = MathHelper.sin((float)System.currentTimeMillis() / 100.0F) * 0.2F + 0.8F;
			GL11.glColor4f(f16, f16, f16, MathHelper.sin((float)System.currentTimeMillis() / 200.0F) * 0.2F + 0.5F);
			i8 = this.renderEngine.getTexture("/terrain.png");
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, i8);
			if(movingObjectPosition2.sideHit == 0) {
			}

			if(movingObjectPosition2.sideHit == 1) {
			}

			if(movingObjectPosition2.sideHit == 2) {
			}

			if(movingObjectPosition2.sideHit == 3) {
			}

			if(movingObjectPosition2.sideHit == 4) {
			}

			if(movingObjectPosition2.sideHit == 5) {
			}
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
	}

	public void drawSelectionBox(EntityPlayer entityPlayer1, MovingObjectPosition movingObjectPosition2, int i3, ItemStack itemStack4, float f5) {
		if(i3 == 0 && movingObjectPosition2.typeOfHit == EnumMovingObjectType.TILE) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
			GL11.glLineWidth(2.0F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(false);
			float f6 = 0.002F;
			int i7 = this.worldObj.getBlockId(movingObjectPosition2.blockX, movingObjectPosition2.blockY, movingObjectPosition2.blockZ);
			if(i7 > 0) {
				Block.blocksList[i7].setBlockBoundsBasedOnState(this.worldObj, movingObjectPosition2.blockX, movingObjectPosition2.blockY, movingObjectPosition2.blockZ);
				double d8 = entityPlayer1.lastTickPosX + (entityPlayer1.posX - entityPlayer1.lastTickPosX) * (double)f5;
				double d10 = entityPlayer1.lastTickPosY + (entityPlayer1.posY - entityPlayer1.lastTickPosY) * (double)f5;
				double d12 = entityPlayer1.lastTickPosZ + (entityPlayer1.posZ - entityPlayer1.lastTickPosZ) * (double)f5;
				this.drawOutlinedBoundingBox(Block.blocksList[i7].getSelectedBoundingBoxFromPool(this.worldObj, movingObjectPosition2.blockX, movingObjectPosition2.blockY, movingObjectPosition2.blockZ).expand((double)f6, (double)f6, (double)f6).getOffsetBoundingBox(-d8, -d10, -d12));
			}

			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}

	}

	private void drawOutlinedBoundingBox(AxisAlignedBB axisAlignedBB1) {
		Tessellator tessellator2 = Tessellator.instance;
		tessellator2.startDrawing(3);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.minY, axisAlignedBB1.minZ);
		tessellator2.addVertex(axisAlignedBB1.maxX, axisAlignedBB1.minY, axisAlignedBB1.minZ);
		tessellator2.addVertex(axisAlignedBB1.maxX, axisAlignedBB1.minY, axisAlignedBB1.maxZ);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.minY, axisAlignedBB1.maxZ);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.minY, axisAlignedBB1.minZ);
		tessellator2.draw();
		tessellator2.startDrawing(3);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.maxY, axisAlignedBB1.minZ);
		tessellator2.addVertex(axisAlignedBB1.maxX, axisAlignedBB1.maxY, axisAlignedBB1.minZ);
		tessellator2.addVertex(axisAlignedBB1.maxX, axisAlignedBB1.maxY, axisAlignedBB1.maxZ);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.maxY, axisAlignedBB1.maxZ);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.maxY, axisAlignedBB1.minZ);
		tessellator2.draw();
		tessellator2.startDrawing(1);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.minY, axisAlignedBB1.minZ);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.maxY, axisAlignedBB1.minZ);
		tessellator2.addVertex(axisAlignedBB1.maxX, axisAlignedBB1.minY, axisAlignedBB1.minZ);
		tessellator2.addVertex(axisAlignedBB1.maxX, axisAlignedBB1.maxY, axisAlignedBB1.minZ);
		tessellator2.addVertex(axisAlignedBB1.maxX, axisAlignedBB1.minY, axisAlignedBB1.maxZ);
		tessellator2.addVertex(axisAlignedBB1.maxX, axisAlignedBB1.maxY, axisAlignedBB1.maxZ);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.minY, axisAlignedBB1.maxZ);
		tessellator2.addVertex(axisAlignedBB1.minX, axisAlignedBB1.maxY, axisAlignedBB1.maxZ);
		tessellator2.draw();
	}

	public void markBlocksForUpdate(int i1, int i2, int i3, int i4, int i5, int i6) {
		int i7 = MathHelper.bucketInt(i1, 16);
		int i8 = MathHelper.bucketInt(i2, 16);
		int i9 = MathHelper.bucketInt(i3, 16);
		int i10 = MathHelper.bucketInt(i4, 16);
		int i11 = MathHelper.bucketInt(i5, 16);
		int i12 = MathHelper.bucketInt(i6, 16);

		for(int i13 = i7; i13 <= i10; ++i13) {
			int i14 = i13 % this.renderChunksWide;
			if(i14 < 0) {
				i14 += this.renderChunksWide;
			}

			for(int i15 = i8; i15 <= i11; ++i15) {
				int i16 = i15 % this.renderChunksTall;
				if(i16 < 0) {
					i16 += this.renderChunksTall;
				}

				for(int i17 = i9; i17 <= i12; ++i17) {
					int i18 = i17 % this.renderChunksDeep;
					if(i18 < 0) {
						i18 += this.renderChunksDeep;
					}

					int i19 = (i18 * this.renderChunksTall + i16) * this.renderChunksWide + i14;
					WorldRenderer worldRenderer20 = this.worldRenderers[i19];
					if(!worldRenderer20.needsUpdate) {
						this.worldRenderersToUpdate.add(worldRenderer20);
						worldRenderer20.markDirty();
					}
				}
			}
		}

	}

	public void markBlockNeedsUpdate(int i1, int i2, int i3) {
		this.markBlocksForUpdate(i1 - 1, i2 - 1, i3 - 1, i1 + 1, i2 + 1, i3 + 1);
	}

	public void markBlockRangeNeedsUpdate(int i1, int i2, int i3, int i4, int i5, int i6) {
		this.markBlocksForUpdate(i1 - 1, i2 - 1, i3 - 1, i4 + 1, i5 + 1, i6 + 1);
	}

	public void clipRenderersByFrustum(ICamera iCamera1, float f2) {
		for(int i3 = 0; i3 < this.worldRenderers.length; ++i3) {
			if(!this.worldRenderers[i3].skipAllRenderPasses() && (!this.worldRenderers[i3].isInFrustum || (i3 + this.frustumCheckOffset & 15) == 0)) {
				this.worldRenderers[i3].updateInFrustum(iCamera1);
			}
		}

		++this.frustumCheckOffset;
	}

	public void playRecord(String string1, int i2, int i3, int i4) {
		if(string1 != null) {
			this.mc.ingameGUI.setRecordPlayingMessage(string1);
		}

		// TODO : Get freezer music here somehow!!
		
		this.mc.sndManager.playStreaming(string1, (float)i2, (float)i3, (float)i4, 1.0F, 1.0F);
	}
	
	public void showString(String s) {
		if (s != null) {
			this.mc.ingameGUI.showString(s);
		}
	}

	public void playSound(String string1, double d2, double d4, double d6, float f8, float f9) {
		float f10 = 16.0F;
		if(f8 > 1.0F) {
			f10 *= f8;
		}

		if(this.mc.renderViewEntity.getDistanceSq(d2, d4, d6) < (double)(f10 * f10)) {
			this.mc.sndManager.playSound(string1, (float)d2, (float)d4, (float)d6, f8, f9);
		}

	}

	public void spawnParticle(String string1, double d2, double d4, double d6, double d8, double d10, double d12) {
		this.spawnParticleDo(string1, d2, d4, d6, d8, d10, d12);
	}

	public EntityFX spawnParticleDo(String string1, double d2, double d4, double d6, double d8, double d10, double d12) {
		if(this.mc != null && this.mc.renderViewEntity != null && this.mc.effectRenderer != null) {
			int i14 = GameSettingsValues.particleSetting;
			if(i14 == 1 && this.worldObj.rand.nextInt(3) == 0) {
				i14 = 2;
			}

			double d15 = this.mc.renderViewEntity.posX - d2;
			double d17 = this.mc.renderViewEntity.posY - d4;
			double d19 = this.mc.renderViewEntity.posZ - d6;
			Object object21 = null;
			if(string1.equals("hugeexplosion")) {
				this.mc.effectRenderer.addEffect((EntityFX)(object21 = new EntityHugeExplodeFX(this.worldObj, d2, d4, d6, d8, d10, d12)));
			} else if(string1.equals("largeexplode")) {
				this.mc.effectRenderer.addEffect((EntityFX)(object21 = new EntityLargeExplodeFX(this.renderEngine, this.worldObj, d2, d4, d6, d8, d10, d12)));
			}

			if(object21 != null) {
				return (EntityFX)object21;
			} else {
				double d22 = 16.0D;
				if(d15 * d15 + d17 * d17 + d19 * d19 > d22 * d22) {
					return null;
				} else if(i14 > 1) {
					return null;
				} else {
					if(string1.equals("bubble")) {
						object21 = new EntityBubbleFX(this.worldObj, d2, d4, d6, d8, d10, d12);
					} else if(string1.equals("suspended")) {
						object21 = new EntitySuspendFX(this.worldObj, d2, d4, d6, d8, d10, d12);
					} else if(string1.equals("depthsuspend")) {
						object21 = new EntityAuraFX(this.worldObj, d2, d4, d6, d8, d10, d12);
					} else if(string1.equals("townaura")) {
						object21 = new EntityAuraFX(this.worldObj, d2, d4, d6, d8, d10, d12);
					} else if(string1.equals("crit")) {
						object21 = new EntityCritFX(this.worldObj, d2, d4, d6, d8, d10, d12);
					} else if(string1.equals("magicCrit")) {
						object21 = new EntityCritFX(this.worldObj, d2, d4, d6, d8, d10, d12);
						((EntityFX)object21).func_40097_b(((EntityFX)object21).func_40098_n() * 0.3F, ((EntityFX)object21).func_40101_o() * 0.8F, ((EntityFX)object21).func_40102_p());
						((EntityFX)object21).setParticleTextureIndex(((EntityFX)object21).getParticleTextureIndex() + 1);
					} else if(string1.equals("smoke")) {
						object21 = new EntitySmokeFX(this.worldObj, d2, d4, d6, d8, d10, d12);
					} else if(string1.equals("mobSpell")) {
						object21 = new EntitySpellParticleFX(this.worldObj, d2, d4, d6, 0.0D, 0.0D, 0.0D);
						((EntityFX)object21).func_40097_b((float)d8, (float)d10, (float)d12);
					} else if(string1.equals("spell")) {
						object21 = new EntitySpellParticleFX(this.worldObj, d2, d4, d6, d8, d10, d12);
					} else if(string1.equals("instantSpell")) {
						object21 = new EntitySpellParticleFX(this.worldObj, d2, d4, d6, d8, d10, d12);
						((EntitySpellParticleFX)object21).func_40110_b(144);
					} else if(string1.equals("note")) {
						object21 = new EntityNoteFX(this.worldObj, d2, d4, d6, d8, d10, d12);
					}else if(string1.equals("enchantmenttable")) {
						object21 = new EntityEnchantmentTableParticleFX(this.worldObj, d2, d4, d6, d8, d10, d12);
					} else if(string1.equals("explode")) {
						object21 = new EntityExplodeFX(this.worldObj, d2, d4, d6, d8, d10, d12);
					} else if(string1.equals("flame")) {
						object21 = new EntityFlameFX(this.worldObj, d2, d4, d6, d8, d10, d12);
					} else if(string1.equals("lava")) {
						object21 = new EntityLavaFX(this.worldObj, d2, d4, d6);
					} else if(string1.equals("footstep")) {
						object21 = new EntityFootStepFX(this.renderEngine, this.worldObj, d2, d4, d6);
					} else if(string1.equals("splash")) {
						object21 = new EntitySplashFX(this.worldObj, d2, d4, d6, d8, d10, d12);
					} else if(string1.equals("largesmoke")) {
						object21 = new EntitySmokeFX(this.worldObj, d2, d4, d6, d8, d10, d12, 2.5F);
					} else if(string1.equals("hugesmoke")) {
						object21 = new EntitySmokeFX(this.worldObj, d2, d4, d6, d8, d10, d12, 8.0F);
					} else if(string1.equals("cloud")) {
						object21 = new EntityCloudFX(this.worldObj, d2, d4, d6, d8, d10, d12);
					} else if(string1.equals("reddust")) {
						object21 = new EntityReddustFX(this.worldObj, d2, d4, d6, (float)d8, (float)d10, (float)d12);
					} else if(string1.equals("snowballpoof")) {
						object21 = new EntityBreakingFX(this.worldObj, d2, d4, d6, Item.snowball);
					} else if(string1.equals("dripWater")) {
						object21 = new EntityDropParticleFX(this.worldObj, d2, d4, d6, Material.water);
					} else if(string1.equals("dripLava")) {
						object21 = new EntityDropParticleFX(this.worldObj, d2, d4, d6, Material.lava);
					} else if(string1.equals("snowshovel")) {
						object21 = new EntitySnowShovelFX(this.worldObj, d2, d4, d6, d8, d10, d12);
					} else if(string1.equals("slime")) {
						object21 = new EntityBreakingFX(this.worldObj, d2, d4, d6, Item.slimeBall);
					} else if(string1.equals("heart")) {
						object21 = new EntityHeartFX(this.worldObj, d2, d4, d6, d8, d10, d12);
					} else {
						int i24;
						if(string1.startsWith("iconcrack_")) {
							i24 = Integer.parseInt(string1.substring(string1.indexOf("_") + 1));
							object21 = new EntityBreakingFX(this.worldObj, d2, d4, d6, d8, d10, d12, Item.itemsList[i24]);
						} else if(string1.startsWith("tilecrack_")) {
							i24 = Integer.parseInt(string1.substring(string1.indexOf("_") + 1));
							object21 = new EntityDiggingFX(this.worldObj, d2, d4, d6, d8, d10, d12, Block.blocksList[i24], 0, 0);
						}
					}

					if(object21 != null) {
						this.mc.effectRenderer.addEffect((EntityFX)object21);
					}

					return (EntityFX)object21;
				}
			}
		} else {
			return null;
		}
	}

	public void obtainEntitySkin(Entity entity1) {
		entity1.updateCloak();
		if(entity1.skinUrl != null) {
			this.renderEngine.obtainImageData(entity1.skinUrl, new MobSkinTextureProcessor());
		}

		if(entity1.cloakUrl != null) {
			this.renderEngine.obtainImageData(entity1.cloakUrl, new MobSkinTextureProcessor());
		}

	}

	public void releaseEntitySkin(Entity entity1) {
		if(entity1.skinUrl != null) {
			this.renderEngine.releaseImageData(entity1.skinUrl);
		}

		if(entity1.cloakUrl != null) {
			this.renderEngine.releaseImageData(entity1.cloakUrl);
		}

	}

	public void updateAllRenderers() {
		if(this.worldRenderers != null) {
			for(int i = 0; i < this.worldRenderers.length; ++i) {
				if(this.worldRenderers[i].isChunkLit && !this.worldRenderers[i].needsUpdate) {
					this.worldRenderersToUpdate.add(this.worldRenderers[i]);
					this.worldRenderers[i].markDirty();
				}
			}

		}
	}
	
	public void doNothingWithTileEntity(int i1, int i2, int i3, TileEntity tileEntity4) {
	}

	public void func_28137_f() {
		GLAllocation.deleteDisplayLists(this.glRenderListBase);
	}

	public void playAuxSFX(EntityPlayer entityPlayer1, int i2, int i3, int i4, int i5, int i6) {
		Random random7 = this.worldObj.rand;
		int i8;
		double d10;
		double d12;
		double d21;
		double d23;
		double d25;
		double d27;
		double d29;
		double d33;
		switch(i2) {
		case 1000:
			this.worldObj.playSoundEffect((double)i3, (double)i4, (double)i5, "random.click", 1.0F, 1.0F);
			break;
		case 1001:
			this.worldObj.playSoundEffect((double)i3, (double)i4, (double)i5, "random.click", 1.0F, 1.2F);
			break;
		case 1002:
			this.worldObj.playSoundEffect((double)i3, (double)i4, (double)i5, "random.bow", 1.0F, 1.2F);
			break;
		case 1003:
			if(Math.random() < 0.5D) {
				this.worldObj.playSoundEffect((double)i3 + 0.5D, (double)i4 + 0.5D, (double)i5 + 0.5D, "random.door_open", 1.0F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
			} else {
				this.worldObj.playSoundEffect((double)i3 + 0.5D, (double)i4 + 0.5D, (double)i5 + 0.5D, "random.door_close", 1.0F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}
			break;
		case 1004:
			this.worldObj.playSoundEffect((double)((float)i3 + 0.5F), (double)((float)i4 + 0.5F), (double)((float)i5 + 0.5F), "random.fizz", 0.5F, 2.6F + (random7.nextFloat() - random7.nextFloat()) * 0.8F);
			break;
		case 1005:
			if(Item.itemsList[i6] instanceof ItemRecord) {
				this.worldObj.playRecord(((ItemRecord)Item.itemsList[i6]).recordName, i3, i4, i5);
			} else {
				this.worldObj.playRecord((String)null, i3, i4, i5);
			}
			break;
		case 1007:
			this.worldObj.playSoundEffect((double)i3 + 0.5D, (double)i4 + 0.5D, (double)i5 + 0.5D, "mob.ghast.charge", 10.0F, (random7.nextFloat() - random7.nextFloat()) * 0.2F + 1.0F);
			break;
		case 1008:
			this.worldObj.playSoundEffect((double)i3 + 0.5D, (double)i4 + 0.5D, (double)i5 + 0.5D, "mob.ghast.fireball", 10.0F, (random7.nextFloat() - random7.nextFloat()) * 0.2F + 1.0F);
			break;
		case 1010:
			this.worldObj.playSoundEffect((double)i3 + 0.5D, (double)i4 + 0.5D, (double)i5 + 0.5D, "mob.zombie.wood", 2.0F, (random7.nextFloat() - random7.nextFloat()) * 0.2F + 1.0F);
			break;
		case 1011:
			this.worldObj.playSoundEffect((double)i3 + 0.5D, (double)i4 + 0.5D, (double)i5 + 0.5D, "mob.zombie.metal", 2.0F, (random7.nextFloat() - random7.nextFloat()) * 0.2F + 1.0F);
			break;
		case 1012:
			this.worldObj.playSoundEffect((double)i3 + 0.5D, (double)i4 + 0.5D, (double)i5 + 0.5D, "mob.zombie.woodbreak", 2.0F, (random7.nextFloat() - random7.nextFloat()) * 0.2F + 1.0F);
			break;
		case 2000:
			i8 = i6 % 3 - 1;
			int i35 = i6 / 3 % 3 - 1;
			d10 = (double)i3 + (double)i8 * 0.6D + 0.5D;
			d12 = (double)i4 + 0.5D;
			double d36 = (double)i5 + (double)i35 * 0.6D + 0.5D;

			for(int i38 = 0; i38 < 10; ++i38) {
				double d39 = random7.nextDouble() * 0.2D + 0.01D;
				double d40 = d10 + (double)i8 * 0.01D + (random7.nextDouble() - 0.5D) * (double)i35 * 0.5D;
				d21 = d12 + (random7.nextDouble() - 0.5D) * 0.5D;
				d23 = d36 + (double)i35 * 0.01D + (random7.nextDouble() - 0.5D) * (double)i8 * 0.5D;
				d25 = (double)i8 * d39 + random7.nextGaussian() * 0.01D;
				d27 = -0.03D + random7.nextGaussian() * 0.01D;
				d29 = (double)i35 * d39 + random7.nextGaussian() * 0.01D;
				this.spawnParticle("smoke", d40, d21, d23, d25, d27, d29);
			}

			return;
		case 2001:
			i8 = i6 & 4095;
			if(i8 > 0) {
				Block block34 = Block.blocksList[i8];
				this.mc.sndManager.playSound(block34.stepSound.getBreakSound(), (float)i3 + 0.5F, (float)i4 + 0.5F, (float)i5 + 0.5F, (block34.stepSound.getVolume() + 1.0F) / 2.0F, block34.stepSound.getPitch() * 0.8F);
			}

			this.mc.effectRenderer.addBlockDestroyEffects(i3, i4, i5, i6 & 4095, i6 >> 12 & 255);
			break;
		case 2002:
			d33 = (double)i3;
			d10 = (double)i4;
			d12 = (double)i5;
			
			/*
			string14 = "iconcrack_" + Item.potion.shiftedIndex;

			for(i15 = 0; i15 < 8; ++i15) {
				this.spawnParticle(string14, d33, d10, d12, random7.nextGaussian() * 0.15D, random7.nextDouble() * 0.2D, random7.nextGaussian() * 0.15D);
			}
			
			i15 = Item.potion.getColorFromDamage(i6, 0);
			float f16 = (float)(i15 >> 16 & 255) / 255.0F;
			float f17 = (float)(i15 >> 8 & 255) / 255.0F;
			float f18 = (float)(i15 >> 0 & 255) / 255.0F;
			String string19 = "spell";
			if(Item.potion.isEffectInstant(i6)) {
				string19 = "instantSpell";
			}

			for(int i20 = 0; i20 < 100; ++i20) {
				d21 = random7.nextDouble() * 4.0D;
				d23 = random7.nextDouble() * Math.PI * 2.0D;
				d25 = Math.cos(d23) * d21;
				d27 = 0.01D + random7.nextDouble() * 0.5D;
				d29 = Math.sin(d23) * d21;
				EntityFX entityFX31 = this.spawnParticleDo(string19, d33 + d25 * 0.1D, d10 + 0.3D, d12 + d29 * 0.1D, d25, d27, d29);
				if(entityFX31 != null) {
					float f32 = 0.75F + random7.nextFloat() * 0.25F;
					entityFX31.func_40097_b(f16 * f32, f17 * f32, f18 * f32);
					entityFX31.multiplyVelocity((float)d21);
				}
			}
			*/

			this.worldObj.playSoundEffect((double)i3 + 0.5D, (double)i4 + 0.5D, (double)i5 + 0.5D, "random.glass", 1.0F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
			break;
		case 2003:
			d33 = (double)i3 + 0.5D;
			d10 = (double)i4;
			d12 = (double)i5 + 0.5D;
			/*
			string14 = "iconcrack_" + Item.eyeOfEnder.shiftedIndex;

			for(i15 = 0; i15 < 8; ++i15) {
				this.spawnParticle(string14, d33, d10, d12, random7.nextGaussian() * 0.15D, random7.nextDouble() * 0.2D, random7.nextGaussian() * 0.15D);
			}
			*/

			for(double d37 = 0.0D; d37 < Math.PI * 2D; d37 += 0.15707963267948966D) {
				this.spawnParticle("portal", d33 + Math.cos(d37) * 5.0D, d10 - 0.4D, d12 + Math.sin(d37) * 5.0D, Math.cos(d37) * -5.0D, 0.0D, Math.sin(d37) * -5.0D);
				this.spawnParticle("portal", d33 + Math.cos(d37) * 5.0D, d10 - 0.4D, d12 + Math.sin(d37) * 5.0D, Math.cos(d37) * -7.0D, 0.0D, Math.sin(d37) * -7.0D);
			}

			return;
		case 2004:
			for(i8 = 0; i8 < 20; ++i8) {
				double d9 = (double)i3 + 0.5D + ((double)this.worldObj.rand.nextFloat() - 0.5D) * 2.0D;
				double d11 = (double)i4 + 0.5D + ((double)this.worldObj.rand.nextFloat() - 0.5D) * 2.0D;
				double d13 = (double)i5 + 0.5D + ((double)this.worldObj.rand.nextFloat() - 0.5D) * 2.0D;
				this.worldObj.spawnParticle("smoke", d9, d11, d13, 0.0D, 0.0D, 0.0D);
				this.worldObj.spawnParticle("flame", d9, d11, d13, 0.0D, 0.0D, 0.0D);
			}
		}

	}

}
