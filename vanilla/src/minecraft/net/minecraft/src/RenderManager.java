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
		Iterator iterator1 = this.entityRenderMap.values().iterator();

		while(iterator1.hasNext()) {
			Render render2 = (Render)iterator1.next();
			render2.setRenderManager(this);
		}

	}

	public Render getEntityClassRenderObject(Class class1) {
		Render render2 = (Render)this.entityRenderMap.get(class1);
		if(render2 == null && class1 != Entity.class) {
			render2 = this.getEntityClassRenderObject(class1.getSuperclass());
			this.entityRenderMap.put(class1, render2);
		}

		return render2;
	}

	public Render getEntityRenderObject(Entity entity1) {
		return this.getEntityClassRenderObject(entity1.getClass());
	}

	public void cacheActiveRenderInfo(World world1, RenderEngine renderEngine2, FontRenderer fontRenderer3, EntityLiving entityLiving4, GameSettings gameSettings5, float f6) {
		this.worldObj = world1;
		this.renderEngine = renderEngine2;
		this.options = gameSettings5;
		this.livingPlayer = entityLiving4;
		this.fontRenderer = fontRenderer3;
		if(entityLiving4.isPlayerSleeping()) {
			int i7 = world1.getBlockId(MathHelper.floor_double(entityLiving4.posX), MathHelper.floor_double(entityLiving4.posY), MathHelper.floor_double(entityLiving4.posZ));
			if(i7 == Block.blockBed.blockID) {
				int i8 = world1.getBlockMetadata(MathHelper.floor_double(entityLiving4.posX), MathHelper.floor_double(entityLiving4.posY), MathHelper.floor_double(entityLiving4.posZ));
				int i9 = i8 & 3;
				this.playerViewY = (float)(i9 * 90 + 180);
				this.playerViewX = 0.0F;
			}
		} else {
			this.playerViewY = entityLiving4.prevRotationYaw + (entityLiving4.rotationYaw - entityLiving4.prevRotationYaw) * f6;
			this.playerViewX = entityLiving4.prevRotationPitch + (entityLiving4.rotationPitch - entityLiving4.prevRotationPitch) * f6;
		}

		this.field_1222_l = entityLiving4.lastTickPosX + (entityLiving4.posX - entityLiving4.lastTickPosX) * (double)f6;
		this.field_1221_m = entityLiving4.lastTickPosY + (entityLiving4.posY - entityLiving4.lastTickPosY) * (double)f6;
		this.field_1220_n = entityLiving4.lastTickPosZ + (entityLiving4.posZ - entityLiving4.lastTickPosZ) * (double)f6;
	}

	public void renderEntity(Entity entity1, float f2) {
		double d3 = entity1.lastTickPosX + (entity1.posX - entity1.lastTickPosX) * (double)f2;
		double d5 = entity1.lastTickPosY + (entity1.posY - entity1.lastTickPosY) * (double)f2;
		double d7 = entity1.lastTickPosZ + (entity1.posZ - entity1.lastTickPosZ) * (double)f2;
		float f9 = entity1.prevRotationYaw + (entity1.rotationYaw - entity1.prevRotationYaw) * f2;
		float f10 = entity1.getEntityBrightness(f2);
		GL11.glColor3f(f10, f10, f10);
		this.renderEntityWithPosYaw(entity1, d3 - renderPosX, d5 - renderPosY, d7 - renderPosZ, f9, f2);
	}

	public void renderEntityWithPosYaw(Entity entity1, double d2, double d4, double d6, float f8, float f9) {
		Render render10 = this.getEntityRenderObject(entity1);
		if(render10 != null) {
			render10.doRender(entity1, d2, d4, d6, f8, f9);
			render10.doRenderShadowAndFire(entity1, d2, d4, d6, f8, f9);
		}

	}

	public void func_852_a(World world1) {
		this.worldObj = world1;
	}

	public double func_851_a(double d1, double d3, double d5) {
		double d7 = d1 - this.field_1222_l;
		double d9 = d3 - this.field_1221_m;
		double d11 = d5 - this.field_1220_n;
		return d7 * d7 + d9 * d9 + d11 * d11;
	}

	public FontRenderer getFontRenderer() {
		return this.fontRenderer;
	}
}
