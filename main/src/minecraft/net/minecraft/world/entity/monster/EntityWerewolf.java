package net.minecraft.world.entity.monster;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public class EntityWerewolf extends EntityMocMob {
	private boolean transforming;
	private boolean hunched;
	private int tcounter;

	public EntityWerewolf(World world) {
		super(world);
		this.texture = "/mocreatures/werehuman.png";
		this.setSize(0.9F, 1.3F);
		this.health = 15;
		this.transforming = false;
		this.tcounter = 0;
		this.setHumanForm(true);
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(22, (byte)0);
	}

	public boolean getIsHumanForm() {
		return this.dataWatcher.getWatchableObjectByte(22) == 1;
	}

	public void setHumanForm(boolean flag) {
		if(!this.worldObj.isRemote) {
			byte input = (byte)(flag ? 1 : 0);
			this.dataWatcher.updateObject(22, input);
		}
	}

	public boolean getIsHunched() {
		return this.hunched;
	}

	public void setHunched(boolean var1) {
		this.hunched = var1;
	}

	protected void attackEntity(Entity entity, float f) {
		if(this.getIsHumanForm()) {
			this.entityToAttack = null;
		} else {
			if(f > 2.0F && f < 6.0F && this.rand.nextInt(15) == 0) {
				if(this.onGround) {
					this.setHunched(true);
					double d = entity.posX - this.posX;
					double d1 = entity.posZ - this.posZ;
					float f1 = MathHelper.sqrt_double(d * d + d1 * d1);
					this.motionX = d / (double)f1 * 0.5D * (double)0.8F + this.motionX * 0.2000000029802322D;
					this.motionZ = d1 / (double)f1 * 0.5D * (double)0.8F + this.motionZ * 0.2000000029802322D;
					this.motionY = (double)0.4F;
				}
			} else {
				super.attackEntity(entity, f);
			}

		}
	}

	public boolean attackEntityFrom(DamageSource damagesource, int i) {
		Entity entity = damagesource.getEntity();
		if(!this.getIsHumanForm() && entity != null && entity instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer)entity;
			ItemStack itemstack = entityplayer.getCurrentEquippedItem();
			if(itemstack != null) {
				i = 1;
				if(itemstack.itemID == Item.hoeGold.shiftedIndex) {
					i = 6;
				}

				if(itemstack.itemID == Item.shovelGold.shiftedIndex) {
					i = 7;
				}

				if(itemstack.itemID == Item.pickaxeGold.shiftedIndex) {
					i = 8;
				}

				if(itemstack.itemID == Item.axeGold.shiftedIndex) {
					i = 9;
				}

				if(itemstack.itemID == Item.swordGold.shiftedIndex) {
					i = 10;
				}
			}
		}

		return super.attackEntityFrom(damagesource, i);
	}

	protected Entity findPlayerToAttack() {
		if(this.getIsHumanForm()) {
			return null;
		} else {
			EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, 16.0D);
			return entityplayer != null && this.canEntityBeSeen(entityplayer) ? entityplayer : null;
		}
	}

	public boolean getCanSpawnHere() {
		return this.worldObj.canBlockSeeTheSky((int)(this.posX + .5), (int)(this.posY + 1), (int)(this.posZ + .5)) && super.getCanSpawnHere();
	}

	protected String getDeathSound() {
		return "mocreatures." + (this.getIsHumanForm() ? "werehumandying" : "werewolfdying");
	}

	protected int getDropItemId() {
		int i = this.rand.nextInt(12);
		if(this.getIsHumanForm()) {
			switch(i) {
			case 0:
				return Item.shovelWood.shiftedIndex;
			case 1:
				return Item.axeWood.shiftedIndex;
			case 2:
				return Item.swordWood.shiftedIndex;
			case 3:
				return Item.hoeWood.shiftedIndex;
			case 4:
				return Item.pickaxeWood.shiftedIndex;
			default:
				return Item.stick.shiftedIndex;
			}
		} else {
			switch(i) {
			case 0:
				return Item.hoeSteel.shiftedIndex;
			case 1:
				return Item.shovelSteel.shiftedIndex;
			case 2:
				return Item.axeSteel.shiftedIndex;
			case 3:
				return Item.pickaxeSteel.shiftedIndex;
			case 4:
				return Item.swordSteel.shiftedIndex;
			case 5:
				return Item.hoeStone.shiftedIndex;
			case 6:
				return Item.shovelStone.shiftedIndex;
			case 7:
				return Item.axeStone.shiftedIndex;
			case 8:
				return Item.pickaxeStone.shiftedIndex;
			case 9:
				return Item.swordStone.shiftedIndex;
			default:
				return Item.appleGold.shiftedIndex;
			}
		}
	}

	protected String getHurtSound() {
		return "mocreatures." + (this.getIsHumanForm() ? "werehumanhurt" : "werewolfhurt");
	}

	public boolean getIsUndead() {
		return true;
	}

	protected String getLivingSound() {
		return "mocreatures." + (this.getIsHumanForm() ? "werehumangrunt" : "werewolfgrunt");
	}

	public int getMaxSpawnedInChunk() {
		return 1;
	}

	public boolean IsNight() {
		return !this.worldObj.isDaytime();
	}

	public void moveEntityWithHeading(float f, float f1) {
		if(!this.getIsHumanForm() && this.onGround) {
			this.motionX *= 1.2D;
			this.motionZ *= 1.2D;
		}

		super.moveEntityWithHeading(f, f1);
	}

	public void onDeath(DamageSource damagesource) {
		Entity entity = damagesource.getEntity();
		if(this.scoreValue > 0 && entity != null) {
			entity.addToPlayerScore(this, this.scoreValue);
		}

		if(entity != null) {
			entity.onKillEntity(this);
		}

		if(!this.worldObj.isRemote) {
			for(int i = 0; i < 2; ++i) {
				int j = this.getDropItemId();
				if(j > 0) {
					this.dropItem(j, 1);
				}
			}
		}

	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		if(!this.worldObj.isRemote) {
			if((this.IsNight() && this.getIsHumanForm() || !this.IsNight() && !this.getIsHumanForm()) && this.rand.nextInt(250) == 0) {
				this.transforming = true;
			}

			if(this.getIsHumanForm() && this.entityToAttack != null) {
				this.entityToAttack = null;
			}

			if(this.entityToAttack != null && !this.getIsHumanForm() && this.entityToAttack.posX - this.posX > 3.0D && this.entityToAttack.posZ - this.posZ > 3.0D) {
				this.setHunched(true);
			}

			if(this.getIsHunched() && this.rand.nextInt(50) == 0) {
				this.setHunched(false);
			}

			if(this.transforming && this.rand.nextInt(3) == 0) {
				++this.tcounter;
				if(this.tcounter % 2 == 0) {
					this.posX += 0.3D;
					this.posY += (double)(this.tcounter / 30);
					this.attackEntityFrom(DamageSource.causeMobDamage(this), 1);
				}

				if(this.tcounter % 2 != 0) {
					this.posX -= 0.3D;
				}

				if(this.tcounter == 10) {
					this.worldObj.playSoundAtEntity(this, "weretransform", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
				}

				if(this.tcounter > 30) {
					this.Transform();
					this.tcounter = 0;
					this.transforming = false;
				}
			}

			if(this.rand.nextInt(300) == 0) {
				this.entityAge -= 100 * this.worldObj.difficultySetting;
				if(this.entityAge < 0) {
					this.entityAge = 0;
				}
			}
		}

	}

	private void Transform() {
		if(this.deathTime <= 0) {
			int i = MathHelper.floor_double(this.posX);
			int j = MathHelper.floor_double(this.boundingBox.minY) + 1;
			int k = MathHelper.floor_double(this.posZ);
			float f = 0.1F;

			for(int l = 0; l < 30; ++l) {
				double d = (double)((float)i + this.worldObj.rand.nextFloat());
				double d1 = (double)((float)j + this.worldObj.rand.nextFloat());
				double d2 = (double)((float)k + this.worldObj.rand.nextFloat());
				double d3 = d - (double)i;
				double d4 = d1 - (double)j;
				double d5 = d2 - (double)k;
				double d6 = (double)MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
				d3 /= d6;
				d4 /= d6;
				d5 /= d6;
				double d7 = 0.5D / (d6 / (double)f + 0.1D);
				d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
				d3 *= d7;
				d4 *= d7;
				d5 *= d7;
				this.worldObj.spawnParticle("explode", (d + (double)i * 1.0D) / 2.0D, (d1 + (double)j * 1.0D) / 2.0D, (d2 + (double)k * 1.0D) / 2.0D, d3, d4, d5);
			}

			if(this.getIsHumanForm()) {
				this.setHumanForm(false);
				this.health = 40;
				this.transforming = false;
			} else {
				this.setHumanForm(true);
				this.health = 15;
				this.transforming = false;
			}

		}
	}

	protected void updateEntityActionState() {
		if(!this.transforming) {
			super.updateEntityActionState();
		}

	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		this.setHumanForm(nbttagcompound.getBoolean("HumanForm"));
	}

	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setBoolean("HumanForm", this.getIsHumanForm());
	}
}
