package net.minecraft.src;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.opengl.GL11;

public class RenderManager {
	private Map entityRenderMap = new HashMap();
	public static RenderManager instance = new RenderManager();
	private FontRenderer fontRenderer;
	public static double renderPosX;
	public static double renderPosY;
	public static double renderPosZ;
	public RenderEngine renderEngine;
	public ItemRenderer itemRenderer;
	public World worldObj;
	public EntityLiving livingPlayer;
	public float playerViewY;
	public float playerViewX;
	public GameSettings options;
	public double field_1222_l;
	public double field_1221_m;
	public double field_1220_n;

	private RenderManager() {
		this.entityRenderMap.put(EntitySpider.class, new RenderSpider());
		this.entityRenderMap.put(EntityPig.class, new RenderPig(new ModelPig(), new ModelPig(0.5F), 0.7F));
		this.entityRenderMap.put(EntitySheep.class, new RenderSheep(new ModelSheep2(), new ModelSheep1(), 0.7F));
		this.entityRenderMap.put(EntityCow.class, new RenderCow(new ModelCow(), 0.7F));
		this.entityRenderMap.put(EntityWolf.class, new RenderWolf(new ModelWolf(), 0.5F));
		this.entityRenderMap.put(EntityChicken.class, new RenderChicken(new ModelChicken(), 0.3F));
		this.entityRenderMap.put(EntityCreeper.class, new RenderCreeper());
		this.entityRenderMap.put(EntitySkeleton.class, new RenderBiped(new ModelSkeleton(), 0.5F));
		this.entityRenderMap.put(EntityZombie.class, new RenderBiped(new ModelZombie(), 0.5F));
		this.entityRenderMap.put(EntitySlime.class, new RenderSlime(new ModelSlime(16), new ModelSlime(0), 0.25F));
		this.entityRenderMap.put(EntityPlayer.class, new RenderPlayer());
		this.entityRenderMap.put(EntityGiantZombie.class, new RenderGiantZombie(new ModelZombie(), 0.5F, 6.0F));
		this.entityRenderMap.put(EntityGhast.class, new RenderGhast());
		this.entityRenderMap.put(EntitySquid.class, new RenderSquid(new ModelSquid(), 0.7F));
		this.entityRenderMap.put(EntityLiving.class, new RenderLiving(new ModelBiped(), 0.5F));
		this.entityRenderMap.put(Entity.class, new RenderEntity());
		this.entityRenderMap.put(EntityPainting.class, new RenderPainting());
		this.entityRenderMap.put(EntityArrow.class, new RenderArrow());
		this.entityRenderMap.put(EntitySnowball.class, new RenderSnowball(Item.snowball.getIconFromDamage(0)));
		this.entityRenderMap.put(EntityEgg.class, new RenderSnowball(Item.egg.getIconFromDamage(0)));
		this.entityRenderMap.put(EntityFireball.class, new RenderFireball());
		this.entityRenderMap.put(EntityItem.class, new RenderItem());
		this.entityRenderMap.put(EntityTNTPrimed.class, new RenderTNTPrimed());
		this.entityRenderMap.put(EntityFallingSand.class, new RenderFallingSand());
		this.entityRenderMap.put(EntityMinecart.class, new RenderMinecart());
		this.entityRenderMap.put(EntityBoat.class, new RenderBoat());
		this.entityRenderMap.put(EntityFish.class, new RenderFish());
		this.entityRenderMap.put(EntityLightningBolt.class, new RenderLightningBolt());
		ModLoader.AddAllRenderers(this.entityRenderMap);
		Iterator iterator2 = this.entityRenderMap.values().iterator();

		while(iterator2.hasNext()) {
			Render localbu = (Render)iterator2.next();
			localbu.setRenderManager(this);
		}

	}

	public Render getEntityClassRenderObject(Class paramClass) {
		Render localbu = (Render)this.entityRenderMap.get(paramClass);
		if(localbu == null && paramClass != Entity.class) {
			localbu = this.getEntityClassRenderObject(paramClass.getSuperclass());
			this.entityRenderMap.put(paramClass, localbu);
		}

		return localbu;
	}

	public Render getEntityRenderObject(Entity paramsi) {
		return this.getEntityClassRenderObject(paramsi.getClass());
	}

	public void cacheActiveRenderInfo(World paramfb, RenderEngine paramjf, FontRenderer paramse, EntityLiving paramlo, GameSettings paramkr, float paramFloat) {
		this.worldObj = paramfb;
		this.renderEngine = paramjf;
		this.options = paramkr;
		this.livingPlayer = paramlo;
		this.fontRenderer = paramse;
		if(paramlo.isPlayerSleeping()) {
			int i1 = paramfb.getBlockId(MathHelper.floor_double(paramlo.posX), MathHelper.floor_double(paramlo.posY), MathHelper.floor_double(paramlo.posZ));
			if(i1 == Block.blockBed.blockID) {
				int i2 = paramfb.getBlockMetadata(MathHelper.floor_double(paramlo.posX), MathHelper.floor_double(paramlo.posY), MathHelper.floor_double(paramlo.posZ));
				int i3 = i2 & 3;
				this.playerViewY = (float)(i3 * 90 + 180);
				this.playerViewX = 0.0F;
			}
		} else {
			this.playerViewY = paramlo.prevRotationYaw + (paramlo.rotationYaw - paramlo.prevRotationYaw) * paramFloat;
			this.playerViewX = paramlo.prevRotationPitch + (paramlo.rotationPitch - paramlo.prevRotationPitch) * paramFloat;
		}

		this.field_1222_l = paramlo.lastTickPosX + (paramlo.posX - paramlo.lastTickPosX) * (double)paramFloat;
		this.field_1221_m = paramlo.lastTickPosY + (paramlo.posY - paramlo.lastTickPosY) * (double)paramFloat;
		this.field_1220_n = paramlo.lastTickPosZ + (paramlo.posZ - paramlo.lastTickPosZ) * (double)paramFloat;
	}

	public void renderEntity(Entity paramsi, float paramFloat) {
		double d1 = paramsi.lastTickPosX + (paramsi.posX - paramsi.lastTickPosX) * (double)paramFloat;
		double d2 = paramsi.lastTickPosY + (paramsi.posY - paramsi.lastTickPosY) * (double)paramFloat;
		double d3 = paramsi.lastTickPosZ + (paramsi.posZ - paramsi.lastTickPosZ) * (double)paramFloat;
		float f1 = paramsi.prevRotationYaw + (paramsi.rotationYaw - paramsi.prevRotationYaw) * paramFloat;
		float f2 = paramsi.getEntityBrightness(paramFloat);
		GL11.glColor3f(f2, f2, f2);
		this.renderEntityWithPosYaw(paramsi, d1 - renderPosX, d2 - renderPosY, d3 - renderPosZ, f1, paramFloat);
	}

	public void renderEntityWithPosYaw(Entity paramsi, double paramDouble1, double paramDouble2, double paramDouble3, float paramFloat1, float paramFloat2) {
		Render localbu = this.getEntityRenderObject(paramsi);
		if(localbu != null) {
			localbu.doRender(paramsi, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);
			localbu.doRenderShadowAndFire(paramsi, paramDouble1, paramDouble2, paramDouble3, paramFloat1, paramFloat2);
		}

	}

	public void func_852_a(World paramfb) {
		this.worldObj = paramfb;
	}

	public double func_851_a(double paramDouble1, double paramDouble2, double paramDouble3) {
		double d1 = paramDouble1 - this.field_1222_l;
		double d2 = paramDouble2 - this.field_1221_m;
		double d3 = paramDouble3 - this.field_1220_n;
		return d1 * d1 + d2 * d2 + d3 * d3;
	}

	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
	}
}
