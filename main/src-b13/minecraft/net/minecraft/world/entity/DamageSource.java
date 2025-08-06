package net.minecraft.world.entity;

import net.minecraft.util.Translator;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntityFireball;

public class DamageSource {
	public static DamageSource inFire = (new DamageSource("inFire")).setFireDamage();
	public static DamageSource onFire = (new DamageSource("onFire")).setDamageBypassesArmor().setFireDamage();
	public static DamageSource lava = (new DamageSource("lava")).setFireDamage();
	public static DamageSource inWall = (new DamageSource("inWall")).setDamageBypassesArmor();
	public static DamageSource drown = (new DamageSource("drown")).setDamageBypassesArmor();
	public static DamageSource starve = (new DamageSource("starve")).setDamageBypassesArmor();
	public static DamageSource cactus = new DamageSource("cactus");
	public static DamageSource fall = (new DamageSource("fall")).setDamageBypassesArmor();
	public static DamageSource outOfWorld = (new DamageSource("outOfWorld")).setDamageBypassesArmor().setDamageAllowedInCreativeMode();
	public static DamageSource generic = (new DamageSource("generic")).setDamageBypassesArmor();
	public static DamageSource explosion = new DamageSource("explosion");
	public static DamageSource magic = (new DamageSource("magic")).setDamageBypassesArmor();
	private boolean isUnblockable = false;
	private boolean isDamageAllowedInCreativeMode = false;
	private float hungerDamage = 0.3F;
	private boolean fireDamage;
	private boolean projectile;
	public String damageType;

	public static DamageSource causeMobDamage(EntityLiving entityLiving0) {
		return new EntityDamageSource("mob", entityLiving0);
	}

	public static DamageSource causePlayerDamage(EntityPlayer entityPlayer0) {
		return new EntityDamageSource("player", entityPlayer0);
	}

	public static DamageSource causeArrowDamage(EntityArrow entityArrow0, Entity entity1) {
		return (new EntityDamageSourceIndirect("arrow", entityArrow0, entity1)).setProjectile();
	}

	public static DamageSource causeFireballDamage(EntityFireball entityFireball0, Entity entity1) {
		return (new EntityDamageSourceIndirect("fireball", entityFireball0, entity1)).setFireDamage().setProjectile();
	}

	public static DamageSource causeThrownDamage(Entity entity0, Entity entity1) {
		return (new EntityDamageSourceIndirect("thrown", entity0, entity1)).setProjectile();
	}

	public static DamageSource causeIndirectMagicDamage(Entity entity0, Entity entity1) {
		return (new EntityDamageSourceIndirect("indirectMagic", entity0, entity1)).setDamageBypassesArmor();
	}

	public boolean isProjectile() {
		return this.projectile;
	}

	public DamageSource setProjectile() {
		this.projectile = true;
		return this;
	}

	public boolean isUnblockable() {
		return this.isUnblockable;
	}

	public float getHungerDamage() {
		return this.hungerDamage;
	}

	public boolean canHarmInCreative() {
		return this.isDamageAllowedInCreativeMode;
	}

	protected DamageSource(String string1) {
		this.damageType = string1;
	}

	public Entity getSourceOfDamage() {
		return this.getEntity();
	}

	public Entity getEntity() {
		return null;
	}

	protected DamageSource setDamageBypassesArmor() {
		this.isUnblockable = true;
		this.hungerDamage = 0.0F;
		return this;
	}

	protected DamageSource setDamageAllowedInCreativeMode() {
		this.isDamageAllowedInCreativeMode = true;
		return this;
	}

	protected DamageSource setFireDamage() {
		this.fireDamage = true;
		return this;
	}

	public String getDeathMessage(EntityPlayer entityPlayer1) {
		return Translator.translateToLocalFormatted("death." + this.damageType, new Object[]{entityPlayer1.username});
	}

	public boolean fireDamage() {
		return this.fireDamage;
	}

	public String getDamageType() {
		return this.damageType;
	}
}
