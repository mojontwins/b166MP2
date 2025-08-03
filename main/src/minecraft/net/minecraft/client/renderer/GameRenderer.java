package net.minecraft.client.renderer;

import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;

import net.minecraft.client.GameSettingsValues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.EntityRainFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.player.EntityPlayerSP;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.src.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.Weather;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockFluid;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

public class GameRenderer {
	private Minecraft mc;
	private float farPlaneDistance = 0.0F;
	public ItemRenderer itemRenderer;
	private int rendererUpdateCount;
	private Entity pointedEntity = null;
	private MouseFilter mouseFilterXAxis = new MouseFilter();
	private MouseFilter mouseFilterYAxis = new MouseFilter();
	private float thirdPersonDistance = 4.0F;
	private float thirdPersonDistanceTemp = 4.0F;
	private float debugCamYaw = 0.0F;
	private float prevDebugCamYaw = 0.0F;
	private float debugCamPitch = 0.0F;
	private float prevDebugCamPitch = 0.0F;
	private float smoothCamYaw;
	private float smoothCamPitch;
	private float smoothCamFilterX;
	private float smoothCamFilterY;
	private float smoothCamPartialTicks;
	private float debugCamFOV = 0.0F;
	private float prevDebugCamFOV = 0.0F;
	private float camRoll = 0.0F;
	private float prevCamRoll = 0.0F;
	public int lightmapTexture;
	private float fovModifierHand;
	private float fovModifierHandPrev;
	private float fovMultiplierTemp;
	private boolean cloudFog = false;
	private double cameraZoom = 1.0D;
	private double cameraYaw = 0.0D;
	private double cameraPitch = 0.0D;
	private long prevFrameTime = System.currentTimeMillis();
	private long renderEndNanoTime = 0L;
	private boolean lightmapUpdateNeeded = false;
	float torchFlickerX = 0.0F;
	float torchFlickerDX = 0.0F;
	float torchFlickerY = 0.0F;
	float torchFlickerDY = 0.0F;
	private Random random = new Random();
	private int rainSoundCounter = 0;
	
	volatile int field_1394_b = 0;
	volatile int field_1393_c = 0;
	FloatBuffer fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
	float fogColorRed;
	float fogColorGreen;
	float fogColorBlue;
	private float fogColor2;
	private float fogColor1;
	public int debugViewDirection;

	float rainXCoords[] = new float[1024];
	float rainYCoords[] = new float[1024];
	
	public GameRenderer(Minecraft mc) {
		this.mc = mc;
		this.itemRenderer = new ItemRenderer(mc);
		this.lightmapTexture = mc.renderEngine.allocateAndSetupTexture(new BufferedImage(16, 16, 1));
		
		// LUT
		int idx = 0;
		for (int x = -16; x < 16; x ++) for (int z = -16; z < 16; z ++) {
			float distance = MathHelper.sqrt_float((float)(x * x) + (z * z));
			this.rainXCoords [idx] = (-((float)x) / distance) * 0.5F;
			this.rainYCoords [idx] = (((float)z) / distance) * 0.5F;
			idx ++;
		}
	}

	public void updateRenderer() {
		this.updateFovModifierHand();
		this.updateTorchFlicker();
		this.fogColor2 = this.fogColor1;
		this.thirdPersonDistanceTemp = this.thirdPersonDistance;
		this.prevDebugCamYaw = this.debugCamYaw;
		this.prevDebugCamPitch = this.debugCamPitch;
		this.prevDebugCamFOV = this.debugCamFOV;
		this.prevCamRoll = this.camRoll;
		float f1;
		float f2;
		if(GameSettingsValues.smoothCamera) {
			f1 = GameSettingsValues.mouseSensitivity * 0.6F + 0.2F;
			f2 = f1 * f1 * f1 * 8.0F;
			this.smoothCamFilterX = this.mouseFilterXAxis.func_22386_a(this.smoothCamYaw, 0.05F * f2);
			this.smoothCamFilterY = this.mouseFilterYAxis.func_22386_a(this.smoothCamPitch, 0.05F * f2);
			this.smoothCamPartialTicks = 0.0F;
			this.smoothCamYaw = 0.0F;
			this.smoothCamPitch = 0.0F;
		}

		if(this.mc.renderViewEntity == null) {
			this.mc.renderViewEntity = this.mc.thePlayer;
		}

		f1 = this.mc.theWorld.getLightBrightness(MathHelper.floor_double(this.mc.renderViewEntity.posX), MathHelper.floor_double(this.mc.renderViewEntity.posY), MathHelper.floor_double(this.mc.renderViewEntity.posZ));
		f2 = (float)(3 - GameSettingsValues.renderDistance) / 3.0F;
		float f3 = f1 * (1.0F - f2) + f2;
		this.fogColor1 += (f3 - this.fogColor1) * 0.1F;
		++this.rendererUpdateCount;
		this.itemRenderer.updateEquippedItem();
		this.addRainParticles();
	}

	public void getMouseOver(float f1) {
		if(this.mc.renderViewEntity != null) {
			if(this.mc.theWorld != null) {
				double d2 = (double)this.mc.playerController.getBlockReachDistance();
				this.mc.objectMouseOver = this.mc.renderViewEntity.rayTrace(d2, f1);
				double d4 = d2;
				Vec3D vec3D6 = this.mc.renderViewEntity.getCurrentNodeVec3d(f1);
				if(this.mc.playerController.extendedReach()) {
					d2 = 6.0D;
					d4 = 6.0D;
				} else {
					if(d2 > 3.0D) {
						d4 = 3.0D;
					}

					d2 = d4;
				}

				if(this.mc.objectMouseOver != null) {
					d4 = this.mc.objectMouseOver.hitVec.distanceTo(vec3D6);
				}

				Vec3D vec3D7 = this.mc.renderViewEntity.getLook(f1);
				Vec3D vec3D8 = vec3D6.addVector(vec3D7.xCoord * d2, vec3D7.yCoord * d2, vec3D7.zCoord * d2);
				this.pointedEntity = null;
				float f9 = 1.0F;
				List<Entity> list10 = this.mc.theWorld.getEntitiesWithinAABBExcludingEntity(this.mc.renderViewEntity, this.mc.renderViewEntity.boundingBox.addCoord(vec3D7.xCoord * d2, vec3D7.yCoord * d2, vec3D7.zCoord * d2).expand((double)f9, (double)f9, (double)f9));
				double d11 = d4;

				for(int i13 = 0; i13 < list10.size(); ++i13) {
					Entity entity14 = (Entity)list10.get(i13);
					if(entity14.canBeCollidedWith()) {
						float f15 = entity14.getCollisionBorderSize();
						AxisAlignedBB axisAlignedBB16 = entity14.boundingBox.expand((double)f15, (double)f15, (double)f15);
						MovingObjectPosition movingObjectPosition17 = axisAlignedBB16.calculateIntercept(vec3D6, vec3D8);
						if(axisAlignedBB16.isVecInside(vec3D6)) {
							if(0.0D < d11 || d11 == 0.0D) {
								this.pointedEntity = entity14;
								d11 = 0.0D;
							}
						} else if(movingObjectPosition17 != null) {
							double d18 = vec3D6.distanceTo(movingObjectPosition17.hitVec);
							if(d18 < d11 || d11 == 0.0D) {
								this.pointedEntity = entity14;
								d11 = d18;
							}
						}
					}
				}

				if(this.pointedEntity != null && (d11 < d4 || this.mc.objectMouseOver == null)) {
					this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity);
				}

			}
		}
	}

	private void updateFovModifierHand() {
		EntityPlayerSP entityPlayerSP1 = (EntityPlayerSP)this.mc.renderViewEntity;
		this.fovMultiplierTemp = entityPlayerSP1.getFOVMultiplier();
		this.fovModifierHandPrev = this.fovModifierHand;
		this.fovModifierHand += (this.fovMultiplierTemp - this.fovModifierHand) * 0.5F;
	}

	private float getFOVModifier(float f1, boolean z2) {
		if(this.debugViewDirection > 0) {
			return 90.0F;
		} else {
			EntityPlayer entityPlayer3 = (EntityPlayer)this.mc.renderViewEntity;
			float f4 = 70.0F;
			if(z2) {
				f4 += GameSettingsValues.fovSetting * 40.0F;
				f4 *= this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * f1;
			}

			if(entityPlayer3.getHealth() <= 0) {
				float f5 = (float)entityPlayer3.deathTime + f1;
				f4 /= (1.0F - 500.0F / (f5 + 500.0F)) * 2.0F + 1.0F;
			}

			int i6 = ActiveRenderInfo.getBlockIdAtEntityViewpoint(this.mc.theWorld, entityPlayer3, f1);
			if(i6 != 0 && Block.blocksList[i6].blockMaterial == Material.water) {
				/*if(this.mc.thePlayer.divingHelmetOn()) {
					f4 = f4 * 50.0F / 70.0F;
				} else */{
					f4 = f4 * 60.0F / 70.0F;
				}
			}

			return f4 + this.prevDebugCamFOV + (this.debugCamFOV - this.prevDebugCamFOV) * f1;
		}
	}

	private void hurtCameraEffect(float f1) {
		EntityLiving entityLiving2 = this.mc.renderViewEntity;
		float f3 = (float)entityLiving2.hurtTime - f1;
		float f4;
		if(entityLiving2.getHealth() <= 0) {
			f4 = (float)entityLiving2.deathTime + f1;
			GL11.glRotatef(40.0F - 8000.0F / (f4 + 200.0F), 0.0F, 0.0F, 1.0F);
		}

		if(f3 >= 0.0F) {
			f3 /= (float)entityLiving2.maxHurtTime;
			f3 = MathHelper.sin(f3 * f3 * f3 * f3 * (float)Math.PI);
			f4 = entityLiving2.attackedAtYaw;
			GL11.glRotatef(-f4, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-f3 * 14.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(f4, 0.0F, 1.0F, 0.0F);
		}
	}

	private void setupViewBobbing(float f1) {
		if(this.mc.renderViewEntity instanceof EntityPlayer) {
			EntityPlayer entityPlayer2 = (EntityPlayer)this.mc.renderViewEntity;
			float f3 = entityPlayer2.distanceWalkedModified - entityPlayer2.prevDistanceWalkedModified;
			float f4 = -(entityPlayer2.distanceWalkedModified + f3 * f1);
			float f5 = entityPlayer2.prevCameraYaw + (entityPlayer2.cameraYaw - entityPlayer2.prevCameraYaw) * f1;
			float f6 = entityPlayer2.prevCameraPitch + (entityPlayer2.cameraPitch - entityPlayer2.prevCameraPitch) * f1;
			GL11.glTranslatef(MathHelper.sin(f4 * (float)Math.PI) * f5 * 0.5F, -Math.abs(MathHelper.cos(f4 * (float)Math.PI) * f5), 0.0F);
			GL11.glRotatef(MathHelper.sin(f4 * (float)Math.PI) * f5 * 3.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(Math.abs(MathHelper.cos(f4 * (float)Math.PI - 0.2F) * f5) * 5.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(f6, 1.0F, 0.0F, 0.0F);
		}
	}

	private void orientCamera(float f1) {
		EntityLiving entityLiving2 = this.mc.renderViewEntity;
		float f3 = entityLiving2.yOffset - 1.62F;
		double d4 = entityLiving2.prevPosX + (entityLiving2.posX - entityLiving2.prevPosX) * (double)f1;
		double d6 = entityLiving2.prevPosY + (entityLiving2.posY - entityLiving2.prevPosY) * (double)f1 - (double)f3;
		double d8 = entityLiving2.prevPosZ + (entityLiving2.posZ - entityLiving2.prevPosZ) * (double)f1;
		GL11.glRotatef(this.prevCamRoll + (this.camRoll - this.prevCamRoll) * f1, 0.0F, 0.0F, 1.0F);
		if(entityLiving2.isPlayerSleeping()) {
			f3 = (float)((double)f3 + 1.0D);
			GL11.glTranslatef(0.0F, 0.3F, 0.0F);
			if(!GameSettingsValues.debugCamEnable) {
				int i10 = this.mc.theWorld.getBlockId(MathHelper.floor_double(entityLiving2.posX), MathHelper.floor_double(entityLiving2.posY), MathHelper.floor_double(entityLiving2.posZ));
				
				if(i10 == Block.bed.blockID) {
					int i11 = this.mc.theWorld.getBlockMetadata(MathHelper.floor_double(entityLiving2.posX), MathHelper.floor_double(entityLiving2.posY), MathHelper.floor_double(entityLiving2.posZ));
					int i12 = i11 & 3;
					GL11.glRotatef((float)(i12 * 90), 0.0F, 1.0F, 0.0F);
				}
				
				GL11.glRotatef(entityLiving2.prevRotationYaw + (entityLiving2.rotationYaw - entityLiving2.prevRotationYaw) * f1 + 180.0F, 0.0F, -1.0F, 0.0F);
				GL11.glRotatef(entityLiving2.prevRotationPitch + (entityLiving2.rotationPitch - entityLiving2.prevRotationPitch) * f1, -1.0F, 0.0F, 0.0F);
			}
		} else if(GameSettingsValues.thirdPersonView > 0) {
			double d27 = (double)(this.thirdPersonDistanceTemp + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * f1);
			float f13;
			float f28;
			if(GameSettingsValues.debugCamEnable) {
				f28 = this.prevDebugCamYaw + (this.debugCamYaw - this.prevDebugCamYaw) * f1;
				f13 = this.prevDebugCamPitch + (this.debugCamPitch - this.prevDebugCamPitch) * f1;
				GL11.glTranslatef(0.0F, 0.0F, (float)(-d27));
				GL11.glRotatef(f13, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(f28, 0.0F, 1.0F, 0.0F);
			} else {
				f28 = entityLiving2.rotationYaw;
				f13 = entityLiving2.rotationPitch;
				if(GameSettingsValues.thirdPersonView == 2) {
					f13 += 180.0F;
				}

				double d14 = (double)(-MathHelper.sin(f28 / 180.0F * (float)Math.PI) * MathHelper.cos(f13 / 180.0F * (float)Math.PI)) * d27;
				double d16 = (double)(MathHelper.cos(f28 / 180.0F * (float)Math.PI) * MathHelper.cos(f13 / 180.0F * (float)Math.PI)) * d27;
				double d18 = (double)(-MathHelper.sin(f13 / 180.0F * (float)Math.PI)) * d27;

				for(int i20 = 0; i20 < 8; ++i20) {
					float f21 = (float)((i20 & 1) * 2 - 1);
					float f22 = (float)((i20 >> 1 & 1) * 2 - 1);
					float f23 = (float)((i20 >> 2 & 1) * 2 - 1);
					f21 *= 0.1F;
					f22 *= 0.1F;
					f23 *= 0.1F;
					MovingObjectPosition movingObjectPosition24 = this.mc.theWorld.rayTraceBlocks(Vec3D.createVector(d4 + (double)f21, d6 + (double)f22, d8 + (double)f23), Vec3D.createVector(d4 - d14 + (double)f21 + (double)f23, d6 - d18 + (double)f22, d8 - d16 + (double)f23));
					if(movingObjectPosition24 != null) {
						double d25 = movingObjectPosition24.hitVec.distanceTo(Vec3D.createVector(d4, d6, d8));
						if(d25 < d27) {
							d27 = d25;
						}
					}
				}

				if(GameSettingsValues.thirdPersonView == 2) {
					GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				}

				GL11.glRotatef(entityLiving2.rotationPitch - f13, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(entityLiving2.rotationYaw - f28, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(0.0F, 0.0F, (float)(-d27));
				GL11.glRotatef(f28 - entityLiving2.rotationYaw, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(f13 - entityLiving2.rotationPitch, 1.0F, 0.0F, 0.0F);
			}
		} else {
			GL11.glTranslatef(0.0F, 0.0F, -0.1F);
		}

		if(!GameSettingsValues.debugCamEnable) {
			GL11.glRotatef(entityLiving2.prevRotationPitch + (entityLiving2.rotationPitch - entityLiving2.prevRotationPitch) * f1, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(entityLiving2.prevRotationYaw + (entityLiving2.rotationYaw - entityLiving2.prevRotationYaw) * f1 + 180.0F, 0.0F, 1.0F, 0.0F);
		}

		GL11.glTranslatef(0.0F, f3, 0.0F);
		d4 = entityLiving2.prevPosX + (entityLiving2.posX - entityLiving2.prevPosX) * (double)f1;
		d6 = entityLiving2.prevPosY + (entityLiving2.posY - entityLiving2.prevPosY) * (double)f1 - (double)f3;
		d8 = entityLiving2.prevPosZ + (entityLiving2.posZ - entityLiving2.prevPosZ) * (double)f1;
		this.cloudFog = this.mc.renderGlobal.func_27307_a(d4, d6, d8, f1);
	}

	private void setupCameraTransform(float f1, int i2) {
		this.farPlaneDistance = (float)(256 >> GameSettingsValues.renderDistance);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		if(this.cameraZoom != 1.0D) {
			GL11.glTranslatef((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
			GL11.glScaled(this.cameraZoom, this.cameraZoom, 1.0D);
		}

		GLU.gluPerspective(this.getFOVModifier(f1, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
		float f4;
		if(this.mc.playerController.func_35643_e()) {
			f4 = 0.6666667F;
			GL11.glScalef(1.0F, f4, 1.0F);
		}

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		this.hurtCameraEffect(f1);
		if(GameSettingsValues.viewBobbing) {
			this.setupViewBobbing(f1);
		}

		f4 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * f1;
		if(f4 > 0.0F) {
			byte b5 = 20;

			float f6 = 5.0F / (f4 * f4 + 5.0F) - f4 * 0.04F;
			f6 *= f6;
			GL11.glRotatef(((float)this.rendererUpdateCount + f1) * (float)b5, 0.0F, 1.0F, 1.0F);
			GL11.glScalef(1.0F / f6, 1.0F, 1.0F);
			GL11.glRotatef(-((float)this.rendererUpdateCount + f1) * (float)b5, 0.0F, 1.0F, 1.0F);
		}

		this.orientCamera(f1);
		if(this.debugViewDirection > 0) {
			int i7 = this.debugViewDirection - 1;
			if(i7 == 1) {
				GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
			}

			if(i7 == 2) {
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			}

			if(i7 == 3) {
				GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			}

			if(i7 == 4) {
				GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			}

			if(i7 == 5) {
				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
			}
		}

	}

	private void renderHand(float f1, int i2) {
		if(this.debugViewDirection <= 0) {
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			if(this.cameraZoom != 1.0D) {
				GL11.glTranslatef((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
				GL11.glScaled(this.cameraZoom, this.cameraZoom, 1.0D);
			}

			GLU.gluPerspective(this.getFOVModifier(f1, false), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
			if(this.mc.playerController.func_35643_e()) {
				float f4 = 0.6666667F;
				GL11.glScalef(1.0F, f4, 1.0F);
			}

			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();

			GL11.glPushMatrix();
			this.hurtCameraEffect(f1);
			if(GameSettingsValues.viewBobbing) {
				this.setupViewBobbing(f1);
			}

			if(GameSettingsValues.thirdPersonView == 0 && !this.mc.renderViewEntity.isPlayerSleeping() && !GameSettingsValues.hideGUI && !this.mc.playerController.func_35643_e()) {
				this.enableLightmap((double)f1);
				this.itemRenderer.renderItemInFirstPerson(f1);
				this.disableLightmap((double)f1);
			}

			GL11.glPopMatrix();
			if(GameSettingsValues.thirdPersonView == 0 && !this.mc.renderViewEntity.isPlayerSleeping()) {
				this.itemRenderer.renderOverlays(f1);
				this.hurtCameraEffect(f1);
			}

			if(GameSettingsValues.viewBobbing) {
				this.setupViewBobbing(f1);
			}

		}
	}

	public void disableLightmap(double d1) {
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public void enableLightmap(double d1) {
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		float f3 = 0.00390625F;
		GL11.glScalef(f3, f3, f3);
		GL11.glTranslatef(8.0F, 8.0F, 8.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		this.mc.renderEngine.bindTexture(this.lightmapTexture);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	private void updateTorchFlicker() {
		this.torchFlickerDX = (float)((double)this.torchFlickerDX + (Math.random() - Math.random()) * Math.random() * Math.random());
		this.torchFlickerDY = (float)((double)this.torchFlickerDY + (Math.random() - Math.random()) * Math.random() * Math.random());
		this.torchFlickerDX = (float)((double)this.torchFlickerDX * 0.9D);
		this.torchFlickerDY = (float)((double)this.torchFlickerDY * 0.9D);
		this.torchFlickerX += (this.torchFlickerDX - this.torchFlickerX) * 1.0F;
		this.torchFlickerY += (this.torchFlickerDY - this.torchFlickerY) * 1.0F;
		this.lightmapUpdateNeeded = true;
	}
	
	private void updateLightmap() {
		if(this.mc.theWorld != null) {
			this.mc.renderEngine.createTextureFromBytes(
					this.mc.theWorld.worldProvider.updateLightmap(
							this.torchFlickerX,
							GameSettingsValues.gammaSetting,
							this.mc.thePlayer
					),
					16, 16, this.lightmapTexture);
			this.lightmapUpdateNeeded = false;
		}
	}

	public void updateCameraAndRender(float f1) {
		//Profiler.startSection("lightTex");
		if(this.lightmapUpdateNeeded) {
			this.updateLightmap();
		}

		//Profiler.endSection();
		if(!Display.isActive()) {
			if(System.currentTimeMillis() - this.prevFrameTime > 500L) {
				this.mc.displayInGameMenu();
			}
		} else {
			this.prevFrameTime = System.currentTimeMillis();
		}

		//Profiler.startSection("mouse");
		if(this.mc.inGameHasFocus) {
			this.mc.mouseHelper.mouseXYChange();
			float f2 = GameSettingsValues.mouseSensitivity * 0.6F + 0.2F;
			float f3 = f2 * f2 * f2 * 8.0F;
			float f4 = (float)this.mc.mouseHelper.deltaX * f3;
			float f5 = (float)this.mc.mouseHelper.deltaY * f3;
			byte b6 = 1;
			if(GameSettingsValues.invertMouse) {
				b6 = -1;
			}

			if(GameSettingsValues.smoothCamera) {
				this.smoothCamYaw += f4;
				this.smoothCamPitch += f5;
				float f7 = f1 - this.smoothCamPartialTicks;
				this.smoothCamPartialTicks = f1;
				f4 = this.smoothCamFilterX * f7;
				f5 = this.smoothCamFilterY * f7;
				this.mc.thePlayer.setAngles(f4, f5 * (float)b6);
			} else {
				this.mc.thePlayer.setAngles(f4, f5 * (float)b6);
			}
		}

		//Profiler.endSection();
		if(!this.mc.skipRenderWorld) {
			ScaledResolution scaledResolution13 = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);
			int i14 = scaledResolution13.getScaledWidth();
			int i15 = scaledResolution13.getScaledHeight();
			int i16 = Mouse.getX() * i14 / this.mc.displayWidth;
			int i17 = i15 - Mouse.getY() * i15 / this.mc.displayHeight - 1;
			short s18 = 200;
			if(GameSettingsValues.limitFramerate == 1) {
				s18 = 120;
			}

			if(GameSettingsValues.limitFramerate == 2) {
				s18 = 40;
			}

			long j8;
			if(this.mc.theWorld != null) {

				if(GameSettingsValues.limitFramerate == 0) {
					this.renderWorld(f1, 0L);
				} else {
					this.renderWorld(f1, this.renderEndNanoTime + (long)(1000000000 / s18));
				}

				if(GameSettingsValues.limitFramerate == 2) {
					j8 = (this.renderEndNanoTime + (long)(1000000000 / s18) - System.nanoTime()) / 1000000L;
					if(j8 > 0L && j8 < 500L) {
						try {
							Thread.sleep(j8);
						} catch (InterruptedException interruptedException12) {
							interruptedException12.printStackTrace();
						}
					}
				}

				this.renderEndNanoTime = System.nanoTime();

				if(!GameSettingsValues.hideGUI || this.mc.currentScreen != null) {
					this.mc.ingameGUI.renderGameOverlay(f1, this.mc.currentScreen != null, i16, i17);
				}

			} else {
				GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glLoadIdentity();
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glLoadIdentity();
				this.setupOverlayRendering();
				j8 = (this.renderEndNanoTime + (long)(1000000000 / s18) - System.nanoTime()) / 1000000L;
				if(j8 < 0L) {
					j8 += 10L;
				}

				if(j8 > 0L && j8 < 500L) {
					try {
						Thread.sleep(j8);
					} catch (InterruptedException interruptedException11) {
						interruptedException11.printStackTrace();
					}
				}

				this.renderEndNanoTime = System.nanoTime();
			}

			if(this.mc.currentScreen != null) {
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				this.mc.currentScreen.drawScreen(i16, i17, f1);
				if(this.mc.currentScreen != null && this.mc.currentScreen.guiParticles != null) {
					this.mc.currentScreen.guiParticles.draw(f1);
				}
			}

		}
	}

	public void renderWorld(float f1, long j2) {

		// Update the lightmap 
		if(this.lightmapUpdateNeeded) {
			this.updateLightmap();
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		// Who's the viewer...
		if(this.mc.renderViewEntity == null) {
			this.mc.renderViewEntity = this.mc.thePlayer;
		}

		this.getMouseOver(f1);
		EntityLiving entityLiving4 = this.mc.renderViewEntity;
		LevelRenderer renderGlobal5 = this.mc.renderGlobal;
		EffectRenderer effectRenderer6 = this.mc.effectRenderer;
		
		double d7 = entityLiving4.lastTickPosX + (entityLiving4.posX - entityLiving4.lastTickPosX) * (double)f1;
		double d9 = entityLiving4.lastTickPosY + (entityLiving4.posY - entityLiving4.lastTickPosY) * (double)f1;
		double d11 = entityLiving4.lastTickPosZ + (entityLiving4.posZ - entityLiving4.lastTickPosZ) * (double)f1;
		int i16;

		GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		this.updateFogColor(f1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glEnable(GL11.GL_CULL_FACE);

		this.setupCameraTransform(f1, 0);
		ActiveRenderInfo.updateRenderInfo(this.mc.thePlayer, GameSettingsValues.thirdPersonView == 2);

		ClippingHelperImpl.getInstance();
		if(GameSettingsValues.renderDistance < 2) {
			this.setupFog(-1, f1);

			renderGlobal5.renderSky(f1);
		}

		GL11.glEnable(GL11.GL_FOG);
		this.setupFog(1, f1);
		if(GameSettingsValues.ambientOcclusion) {
			GL11.glShadeModel(GL11.GL_SMOOTH);
		}

		Frustrum frustrum19 = new Frustrum();
		frustrum19.setPosition(d7, d9, d11);
		this.mc.renderGlobal.clipRenderersByFrustum(frustrum19, f1);
		
		while(!this.mc.renderGlobal.updateRenderers(entityLiving4, false) && j2 != 0L) {
			long j20 = j2 - System.nanoTime();
			if(j20 < 0L || j20 > 1000000000L) {
				break;
			}
		}

		this.setupFog(0, f1);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/terrain.png"));
		RenderHelper.disableStandardItemLighting();

		renderGlobal5.sortAndRender(entityLiving4, 0, (double)f1);
		GL11.glShadeModel(GL11.GL_FLAT);
		EntityPlayer entityPlayer21;
		if(this.debugViewDirection == 0) {
			RenderHelper.enableStandardItemLighting();

			renderGlobal5.renderEntities(entityLiving4.getCurrentNodeVec3d(f1), frustrum19, f1);
			this.enableLightmap((double)f1);

			effectRenderer6.func_1187_b(entityLiving4, f1);
			RenderHelper.disableStandardItemLighting();
			this.setupFog(0, f1);

			effectRenderer6.renderParticles(entityLiving4, f1);
			this.disableLightmap((double)f1);
			if(this.mc.objectMouseOver != null && entityLiving4.isInsideOfMaterial(Material.water) && entityLiving4 instanceof EntityPlayer && !GameSettingsValues.hideGUI) {
				entityPlayer21 = (EntityPlayer)entityLiving4;
				GL11.glDisable(GL11.GL_ALPHA_TEST);

				renderGlobal5.drawBlockBreaking(entityPlayer21, this.mc.objectMouseOver, 0, entityPlayer21.inventory.getCurrentItem(), f1);
				renderGlobal5.drawSelectionBox(entityPlayer21, this.mc.objectMouseOver, 0, entityPlayer21.inventory.getCurrentItem(), f1);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
			}
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthMask(true);
		this.setupFog(0, f1);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/terrain.png"));
		if(GameSettingsValues.fancyGraphics) {
			if(GameSettingsValues.ambientOcclusion) {
				GL11.glShadeModel(GL11.GL_SMOOTH);
			}

			GL11.glColorMask(false, false, false, false);
			i16 = renderGlobal5.sortAndRender(entityLiving4, 1, (double)f1);
			GL11.glColorMask(true, true, true, true);

			if(i16 > 0) {
				renderGlobal5.renderAllRenderLists(1, (double)f1);
			}

			GL11.glShadeModel(GL11.GL_FLAT);
		} else {
			renderGlobal5.sortAndRender(entityLiving4, 1, (double)f1);
		}

		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		if(this.cameraZoom == 1.0D && entityLiving4 instanceof EntityPlayer && !GameSettingsValues.hideGUI && this.mc.objectMouseOver != null && !entityLiving4.isInsideOfMaterial(Material.water)) {
			entityPlayer21 = (EntityPlayer)entityLiving4;
			GL11.glDisable(GL11.GL_ALPHA_TEST);

			renderGlobal5.drawBlockBreaking(entityPlayer21, this.mc.objectMouseOver, 0, entityPlayer21.inventory.getCurrentItem(), f1);
			renderGlobal5.drawSelectionBox(entityPlayer21, this.mc.objectMouseOver, 0, entityPlayer21.inventory.getCurrentItem(), f1);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		}

		this.renderWeather(f1);
		GL11.glDisable(GL11.GL_FOG);
		if(this.pointedEntity != null) {
			;
		}

		if(GameSettingsValues.shouldRenderClouds() && !this.mc.theWorld.worldProvider.isCaveWorld) {
			GL11.glPushMatrix();
			this.setupFog(0, f1);
			GL11.glEnable(GL11.GL_FOG);
			renderGlobal5.renderClouds(f1);
			GL11.glDisable(GL11.GL_FOG);
			this.setupFog(1, f1);
			GL11.glPopMatrix();
		}

		if(this.cameraZoom == 1.0D) {
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			this.renderHand(f1, 0);
		}

		GL11.glColorMask(true, true, true, false);
	}

	private void addRainParticles() {
		float rainStrength = this.mc.theWorld.getRainStrength(1.0F);
		if(!GameSettingsValues.fancyGraphics) {
			rainStrength /= 2.0F;
		}

		if(rainStrength != 0.0F) {
			this.random.setSeed((long)this.rendererUpdateCount * 312987231L);
			EntityLiving thePlayer = this.mc.renderViewEntity;
			World world = this.mc.theWorld;

			int x = MathHelper.floor_double(thePlayer.posX);
			int y = MathHelper.floor_double(thePlayer.posY);
			int z = MathHelper.floor_double(thePlayer.posZ);
			
			byte radius = 10;

			double fxX = 0.0D;
			double fxY = 0.0D;
			double fxZ = 0.0D;
			
			int soundCounter = 0;
			int maxParticles = (int)(100.0F * rainStrength * rainStrength);

			if(GameSettingsValues.particleSetting == 1) {
				maxParticles >>= 1;
			} else if(GameSettingsValues.particleSetting == 2) {
				maxParticles = 0;
			}

			for(int i = 0; i < maxParticles; ++i) {
				int partX = x + this.random.nextInt(radius) - this.random.nextInt(radius);
				int partZ = z + this.random.nextInt(radius) - this.random.nextInt(radius);
				int partY = world.getPrecipitationHeight(partX, partZ);
				int blockID = world.getBlockId(partX, partY - 1, partZ);

				BiomeGenBase biomeGen = world.getBiomeGenForCoords(partX, partZ);
				int particleType = Weather.particleDecide(biomeGen, world);
				if(particleType != Weather.RAIN) continue;

				if(partY <= y + radius && partY >= y - radius && biomeGen.canSpawnLightningBolt() && biomeGen.getFloatTemperature() > 0.2F) {
					float fineDx = this.random.nextFloat();
					float fineDz = this.random.nextFloat();
					
					if(blockID > 0) {
						if(Block.blocksList[blockID].blockMaterial == Material.lava) {
							this.mc.effectRenderer.addEffect(new EntitySmokeFX(world, (double)((float)partX + fineDx), (double)((float)partY + 0.1F) - Block.blocksList[blockID].minY, (double)((float)partZ + fineDz), 0.0D, 0.0D, 0.0D));
						} else {
							++soundCounter;
							if(this.random.nextInt(soundCounter) == 0) {
								fxX = (double)((float)partX + fineDx);
								fxY = (double)((float)partY + 0.1F) - Block.blocksList[blockID].minY;
								fxZ = (double)((float)partZ + fineDz);
							}

							this.mc.effectRenderer.addEffect(new EntityRainFX(world, (double)((float)partX + fineDx), (double)((float)partY + 0.1F) - Block.blocksList[blockID].minY, (double)((float)partZ + fineDz)));
						}
					}
				}
			}

			if(soundCounter > 0 && this.random.nextInt(3) < this.rainSoundCounter++) {
				this.rainSoundCounter = 0;
				if(fxY > thePlayer.posY + 1.0D && world.getPrecipitationHeight(MathHelper.floor_double(thePlayer.posX), MathHelper.floor_double(thePlayer.posZ)) > MathHelper.floor_double(thePlayer.posY)) {
					this.mc.theWorld.playSoundEffect(fxX, fxY, fxZ, "ambient.weather.rain", 0.1F, 0.5F);
				} else {
					this.mc.theWorld.playSoundEffect(fxX, fxY, fxZ, "ambient.weather.rain", 0.2F, 1.0F);
				}
			}

		}
	}

	protected void renderWeather(float renderPartialTick) {
		EntityLiving entityPlayerSP = this.mc.renderViewEntity;
		World world = this.mc.theWorld;
		float fRain = world.getRainStrength(renderPartialTick);
		float fSnow = world.getSnowStrength(renderPartialTick);

		if(fRain <= 0.0F && fSnow <= 0.0F) return;
		this.enableLightmap((double)renderPartialTick);
		
		// player block coordinates
		int playerX = MathHelper.floor_double(entityPlayerSP.posX);
		int playerY = MathHelper.floor_double(entityPlayerSP.posY);
		int playerZ = MathHelper.floor_double(entityPlayerSP.posZ);
		
		// Prepare tessellator & texture
		Tessellator tessellator = Tessellator.instance;
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.01F);
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/snow.png"));

		double interpolatedX = entityPlayerSP.lastTickPosX + (entityPlayerSP.posX - entityPlayerSP.lastTickPosX) * (double)renderPartialTick;
		double interpolatedY = entityPlayerSP.lastTickPosY + (entityPlayerSP.posY - entityPlayerSP.lastTickPosY) * (double)renderPartialTick;
		double interpolatedZ = entityPlayerSP.lastTickPosZ + (entityPlayerSP.posZ - entityPlayerSP.lastTickPosZ) * (double)renderPartialTick;

		byte radius = 8;

		byte lastParticle = -1;
		float f4 = (float)this.rendererUpdateCount + renderPartialTick;
		float f10 = ((float)(rendererUpdateCount & 0x1ff) + renderPartialTick) / 512F;

		for(int x = playerX - radius; x <= playerX + radius; ++x) {
			for(int z = playerZ - radius; z <= playerZ + radius; ++z) {
			
				// TODO:  Optimization... Is x/z visible?
				
				int idx = ((z - playerZ) + 16) * 32 + ((x - playerX) + 16);
				float distanceX = rainXCoords[idx];
				float distanceZ = rainYCoords[idx];				
				
				BiomeGenBase biomegenbase = world.getBiomeGenForCoords(x, z);
				int particleType = Weather.particleDecide(biomegenbase, world);
				if(particleType == 0) continue;
				
				int y = world.getPrecipitationHeight(x, z);
				if(y < 0) {
					y = 0;
				}
				
				int y1 = playerY - radius;
				int y2 = playerY + radius;
				
				if(y1 < y) {
					y1 = y;
				}

				if(y2 < y) {
					y2 = y;
				}
				
				if(y1 != y2) {
					
					this.random.setSeed(x * x * 3121 + x * 0x2b24abb ^ z * z * 0x66397 + z * 13761);
					
					switch(particleType) {
						case Weather.RAIN:
							if(lastParticle != 0) {
								if(lastParticle > 0) {
									tessellator.draw();
								}
								
								lastParticle = 0;
								GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/environment/rain.png"));
								tessellator.startDrawingQuads();
							}
							
							float f9 = (((float)(this.rendererUpdateCount + x * x * 3121 + x * 0x2b24abb + z * z * 0x66397 + z * 13761 & 0x1f) + renderPartialTick) / 32F) * (3F + this.random.nextFloat());
							double ddX = (double)((float)x + 0.5F) - entityPlayerSP.posX;
							double ddZ = (double)((float)z + 0.5F) - entityPlayerSP.posZ;
							float hypotenuse = MathHelper.sqrt_double(ddX * ddX + ddZ * ddZ) / (float)radius;
							
							tessellator.setBrightness(world.getLightBrightnessForSkyBlocks(x, y, z, 0));
							tessellator.setColorRGBA_F(1, 1, 1, ((1.0F - hypotenuse * hypotenuse) * 0.5F + 0.5F) * fRain);
							
							tessellator.setTranslation(-interpolatedX * 1.0D, -interpolatedY * 1.0D, -interpolatedZ * 1.0D);
							tessellator.addVertexWithUV((double)((float)x - distanceX) + 0.5D, y1, (double)((float)z - distanceZ) + 0.5D, 0.0F, ((float)y1) / 4F + f9);
							tessellator.addVertexWithUV((double)((float)x + distanceX) + 0.5D, y1, (double)((float)z + distanceZ) + 0.5D, 1.0F, ((float)y1) / 4F + f9);
							tessellator.addVertexWithUV((double)((float)x + distanceX) + 0.5D, y2, (double)((float)z + distanceZ) + 0.5D, 1.0F, ((float)y2) / 4F + f9);
							tessellator.addVertexWithUV((double)((float)x - distanceX) + 0.5D, y2, (double)((float)z - distanceZ) + 0.5D, 0.0F, ((float)y2) / 4F + f9);
							tessellator.setTranslation(0.0D, 0.0D, 0.0D);
							break;
							
						case Weather.SNOW:
							if (lastParticle != 1)	{
								if (lastParticle >= 0)	{
									tessellator.draw();
								}

								lastParticle = 1;
								GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture("/environment/snow.png"));
								tessellator.startDrawingQuads();
							}
							
							// Sometimes (in cold biomes during winter) rain is substituted for snow
							if(fSnow < fRain) fSnow = fRain;
							
							
							float f11 = this.random.nextFloat() + f4 * 0.01F * (float)this.random.nextGaussian();
							float f12 = this.random.nextFloat() + f4 * (float)this.random.nextGaussian() * 0.001F;
							double dddX = (double)((float)x + 0.5F) - entityPlayerSP.posX;
							double dddZ = (double)((float)z + 0.5F) - entityPlayerSP.posZ;
							float hypotenuse2 = MathHelper.sqrt_double(dddX * dddX + dddZ * dddZ) / (float)radius;
							
							tessellator.setBrightness(world.getLightBrightnessForSkyBlocks(x, y, z, 0));
							tessellator.setColorRGBA_F(1, 1, 1, ((1.0F - hypotenuse2 * hypotenuse2) * 0.3F + 0.5F) * fSnow);
							
							tessellator.setTranslation(-interpolatedX * 1.0D, -interpolatedY * 1.0D, -interpolatedZ * 1.0D);
							tessellator.addVertexWithUV((double)((float)x - distanceX) + 0.5D, y1, (double)((float)z - distanceZ) + 0.5D, 0.0F + f11, ((float)y1) / 4F + f10 + f12);
							tessellator.addVertexWithUV((double)((float)x + distanceX) + 0.5D, y1, (double)((float)z + distanceZ) + 0.5D, 1.0F + f11, ((float)y1) / 4F + f10 + f12);
							tessellator.addVertexWithUV((double)((float)x + distanceX) + 0.5D, y2, (double)((float)z + distanceZ) + 0.5D, 1.0F + f11, ((float)y2) / 4F + f10 + f12);
							tessellator.addVertexWithUV((double)((float)x - distanceX) + 0.5D, y2, (double)((float)z - distanceZ) + 0.5D, 0.0F + f11, ((float)y2) / 4F + f10 + f12);
							tessellator.setTranslation(0.0D, 0.0D, 0.0D);
							break;

					}
				}
			}
		}
		
		if (lastParticle >= 0)	{
			tessellator.draw();
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		this.disableLightmap((double)renderPartialTick);
		
	}

	public void setupOverlayRendering() {
		ScaledResolution scaledResolution1 = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, scaledResolution1.scaledWidthD, scaledResolution1.scaledHeightD, 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
	}

	private void updateFogColor(float f1) {
		World world2 = this.mc.theWorld;
		EntityLiving entityLiving3 = this.mc.renderViewEntity;
		float f4 = 1.0F / (float)(4 - GameSettingsValues.renderDistance);
		f4 = 1.0F - (float)Math.pow((double)f4, 0.25D);
		Vec3D vec3D5 = world2.getSkyColor(this.mc.renderViewEntity, f1);
		float f6 = (float)vec3D5.xCoord;
		float f7 = (float)vec3D5.yCoord;
		float f8 = (float)vec3D5.zCoord;

		Vec3D vec3D9 = world2.getFogColor(entityLiving3, f1);
		this.fogColorRed = (float)vec3D9.xCoord;
		this.fogColorGreen = (float)vec3D9.yCoord;
		this.fogColorBlue = (float)vec3D9.zCoord;

		float f11;
		if(GameSettingsValues.renderDistance < 2 && !this.mc.theWorld.worldProvider.hasNoSky) {
			
			Vec3D vec3D10 = MathHelper.sin(world2.getCelestialAngleRadians(f1)) > 0.0F ? Vec3D.createVector(-1.0D, 0.0D, 0.0D) : Vec3D.createVector(1.0D, 0.0D, 0.0D);
			f11 = (float)entityLiving3.getLook(f1).dotProduct(vec3D10);
			if(f11 < 0.0F) {
				f11 = 0.0F;
			}

			if(f11 > 0.0F) {
				if(GameRules.boolRule("hasSunriseSunset")) {
					float[] f12 = world2.worldProvider.calcSunriseSunsetColors(world2.getCelestialAngle(f1), f1);
					if(f12 != null) {
						f11 *= f12[3];
						this.fogColorRed = this.fogColorRed * (1.0F - f11) + f12[0] * f11;
						this.fogColorGreen = this.fogColorGreen * (1.0F - f11) + f12[1] * f11;
						this.fogColorBlue = this.fogColorBlue * (1.0F - f11) + f12[2] * f11;
					}
				}
			}
		}

		this.fogColorRed += (f6 - this.fogColorRed) * f4;
		this.fogColorGreen += (f7 - this.fogColorGreen) * f4;
		this.fogColorBlue += (f8 - this.fogColorBlue) * f4;
		
		float f19 = world2.getRainStrength(f1);
		float f20;
		if(f19 > 0.0F) {
			f11 = 1.0F - f19 * 0.5F;
			f20 = 1.0F - f19 * 0.4F;
			this.fogColorRed *= f11;
			this.fogColorGreen *= f11;
			this.fogColorBlue *= f20;
		}

		f11 = world2.getWeightedThunderStrength(f1);
		if(f11 > 0.0F) {
			f20 = 1.0F - f11 * 0.5F;
			this.fogColorRed *= f20;
			this.fogColorGreen *= f20;
			this.fogColorBlue *= f20;
		}

		int i21 = ActiveRenderInfo.getBlockIdAtEntityViewpoint(this.mc.theWorld, entityLiving3, f1);
		if(this.cloudFog) {
			Vec3D vec3D13 = world2.getCloudColor(f1);
			this.fogColorRed = (float)vec3D13.xCoord;
			this.fogColorGreen = (float)vec3D13.yCoord;
			this.fogColorBlue = (float)vec3D13.zCoord;
		} else if(i21 != 0 && Block.blocksList[i21].blockMaterial == Material.water) {
			if (!GameRules.boolRule("colouredWater")) {
				this.fogColorRed = 0.02F;
				this.fogColorGreen = 0.02F;
				this.fogColorBlue = 0.2F;
			} else {
				Vec3D vec3D = ((BlockFluid)Block.waterStill).colorMultiplierAsVec3D(this.mc.theWorld, (int) entityLiving3.posX, (int) entityLiving3.posY, (int) entityLiving3.posZ);
				this.fogColorRed = (float)vec3D.xCoord * 0.05F;
				this.fogColorGreen = (float)vec3D.yCoord * 0.05F;
				this.fogColorBlue = (float)vec3D.zCoord * 0.05F;
			}
		} else if(i21 != 0 && Block.blocksList[i21].blockMaterial == Material.lava) {
			this.fogColorRed = 0.6F;
			this.fogColorGreen = 0.1F;
			this.fogColorBlue = 0.0F;
		}

		float f22 = this.fogColor2 + (this.fogColor1 - this.fogColor2) * f1;
		this.fogColorRed *= f22;
		this.fogColorGreen *= f22;
		this.fogColorBlue *= f22;
		double d14 = (entityLiving3.lastTickPosY + (entityLiving3.posY - entityLiving3.lastTickPosY) * (double)f1) * world2.worldProvider.getVoidFogYFactor();
		
		// TODO: Blindness
		/*
		if(entityLiving3.isPotionActive(Potion.blindness)) {
			int i16 = entityLiving3.getActivePotionEffect(Potion.blindness).getDuration();
			if(i16 < 20) {
				d14 *= (double)(1.0F - (float)i16 / 20.0F);
			} else {
				d14 = 0.0D;
			}
		}*/

		if(d14 < 1.0D) {
			if(d14 < 0.0D) {
				d14 = 0.0D;
			}

			d14 *= d14;
			this.fogColorRed = (float)((double)this.fogColorRed * d14);
			this.fogColorGreen = (float)((double)this.fogColorGreen * d14);
			this.fogColorBlue = (float)((double)this.fogColorBlue * d14);
		}
		
		// Night vision
		/*
		if(((EntityPlayer)entityLiving3).divingHelmetOn()) {
			float nVB = 0.5F;
			
			float fNV = 1.0F / this.fogColorRed;
			if(fNV > 1.0F / this.fogColorGreen) fNV = 1.0F / this.fogColorGreen;
			if(fNV > 1.0F / this.fogColorBlue) fNV = 1.0F / this.fogColorBlue;
			
			this.fogColorRed   = this.fogColorRed   * (1.0F - nVB) + this.fogColorRed   * fNV * nVB;
			this.fogColorGreen = this.fogColorGreen * (1.0F - nVB) + this.fogColorGreen * fNV * nVB;
			this.fogColorBlue  = this.fogColorBlue  * (1.0F - nVB) + this.fogColorBlue  * fNV * nVB;
		}
		*/

		GL11.glClearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
	}

	private void setupFog(int i1, float renderPartialTick) {
		EntityLiving viewerEntity = this.mc.renderViewEntity;
		boolean creative = false;
		
		if(viewerEntity instanceof EntityPlayer) {
			creative = ((EntityPlayer)viewerEntity).capabilities.isCreativeMode;
		}

		if(i1 == 999) {
			GL11.glFog(GL11.GL_FOG_COLOR, this.setFogColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
			GL11.glFogf(GL11.GL_FOG_START, 0.0F);
			GL11.glFogf(GL11.GL_FOG_END, 8.0F);
			if(GLContext.getCapabilities().GL_NV_fog_distance) {
				GL11.glFogi(34138, 34139);
			}

			GL11.glFogf(GL11.GL_FOG_START, 0.0F);
		} else {
			GL11.glFog(GL11.GL_FOG_COLOR, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
			GL11.glNormal3f(0.0F, -1.0F, 0.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			int i5 = ActiveRenderInfo.getBlockIdAtEntityViewpoint(this.mc.theWorld, viewerEntity, renderPartialTick);
			float dist;
			
			// TODO: Blindness
			/*
			if(viewerEntity.isPotionActive(Potion.blindness)) {
				dist = 5.0F;
				int i7 = viewerEntity.getActivePotionEffect(Potion.blindness).getDuration();
				if(i7 < 20) {
					dist = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - (float)i7 / 20.0F);
				}

				GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
				if(i1 < 0) {
					GL11.glFogf(GL11.GL_FOG_START, 0.0F);
					GL11.glFogf(GL11.GL_FOG_END, dist * 0.8F);
				} else {
					GL11.glFogf(GL11.GL_FOG_START, dist * 0.25F);
					GL11.glFogf(GL11.GL_FOG_END, dist);
				}

				if(GLContext.getCapabilities().GL_NV_fog_distance) {
					GL11.glFogi(34138, 34139);
				}
			} else*/ {
				float f9;
				if(this.cloudFog) {
					GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
					GL11.glFogf(GL11.GL_FOG_DENSITY, 0.1F);

				} else if(i5 > 0 && Block.blocksList[i5].blockMaterial == Material.water /* && !((EntityPlayer)viewerEntity).divingHelmetOn()*/) {
					GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
					GL11.glFogf(GL11.GL_FOG_DENSITY, 0.1F);

				} else if(i5 > 0 && Block.blocksList[i5].blockMaterial == Material.lava) {
					GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
					GL11.glFogf(GL11.GL_FOG_DENSITY, 2.0F);

				} else {
					dist = this.farPlaneDistance;

					float fogIntensity = 0.0F;
					
					if(this.mc.theWorld.worldProvider.getWorldHasNoSky() && !creative) {
						double d13 = (double)((viewerEntity.getBrightnessForRender(renderPartialTick) & 15728640) >> 20) / 16.0D + (viewerEntity.lastTickPosY + (viewerEntity.posY - viewerEntity.lastTickPosY) * (double)renderPartialTick + 4.0D) / 32.0D;
						
						if(d13 < 1.0D) {
							if(d13 < 0.0D) {
								d13 = 0.0D;
							}

							d13 *= d13;
							f9 = 100.0F * (float)d13;
							if(f9 < 5.0F) {
								f9 = 5.0F;
							}

							if(dist > f9) {
								dist = f9;
							}
						}
					} else {
						fogIntensity = (float)Math.max(0, this.mc.theWorld.getFogIntensity(renderPartialTick) - this.mc.theWorld.getRainStrength(renderPartialTick));
						int y = (int)viewerEntity.posY;
						if (y < 64 && y >= 56) {
							fogIntensity = MathHelper.lerp(fogIntensity, 0, (64 - y) / 8F);
						} else if (y < 56) {
							fogIntensity = 0;
						}
					}
					
					dist = (float) Math.min(dist, 256.0F - 192.0F * fogIntensity); 
					GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
					if(i1 < 0) {
						GL11.glFogf(GL11.GL_FOG_START, 0.0F);
						GL11.glFogf(GL11.GL_FOG_END, dist * 0.8F);
					} else {
						// This is the "normal" fog
						GL11.glFogf(GL11.GL_FOG_START, dist * 0.25F);						
						GL11.glFogf(GL11.GL_FOG_END, dist);
					}

					if(GLContext.getCapabilities().GL_NV_fog_distance) {
						GL11.glFogi(34138, 34139);
					}

				}
			}

			GL11.glEnable(GL11.GL_COLOR_MATERIAL);
			GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT);
		}
	}

	private FloatBuffer setFogColorBuffer(float f1, float f2, float f3, float f4) {
		this.fogColorBuffer.clear();
		this.fogColorBuffer.put(f1).put(f2).put(f3).put(f4);
		this.fogColorBuffer.flip();
		return this.fogColorBuffer;
	}
}
