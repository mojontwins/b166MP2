package net.minecraft.world.entity.monster;

import net.minecraft.src.MathHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.World;

public class EntityWraith extends EntityMocFlyerMob {
	public EntityWraith(World world) {
		super(world);
		this.texture = "/mob/wraith.png";
		this.setSize(1.5F, 1.5F);
		this.isImmuneToFire = false;
		this.c = 2;
		this.health = 10;
		this.moveSpeed = 1.3F;
	}

	public void entityInit() {
		if(this.worldObj.difficultySetting == 1) {
			this.c = 2;
		} else if(this.worldObj.difficultySetting > 1) {
			this.c = 3;
		}

		super.entityInit();
	}

	public boolean d2() {
		return super.getCanSpawnHere();
	}

	public boolean getCanSpawnHere() {
		return this.worldObj.canBlockSeeTheSky((int)(this.posX + .5), (int)(this.posY + 1), (int)(this.posZ + .5)) && super.getCanSpawnHere();
	}

	protected String getDeathSound() {
		return "mocreatures.wraithdying";
	}

	protected int getDropItemId() {
		return Item.gunpowder.shiftedIndex;
	}

	protected String getHurtSound() {
		return "mocreatures.wraithhurt";
	}

	protected String getLivingSound() {
		return "mocreatures.wraith";
	}

	public void onLivingUpdate() {
		if(!this.worldObj.isRemote && this.worldObj.isDaytime()) {
			float f = this.getBrightness(1.0F);
			if(f > 0.5F && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
				this.setFire(15);
			}
		}

		super.onLivingUpdate();
	}
}
