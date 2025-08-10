package net.minecraft.world.entity.monster;

import java.util.Collections;
import java.util.List;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ISentient;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.Destroyer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class EntityOgre extends EntityMob implements IMob {
	public int frequencyA;
	public float destroyForce;
	public boolean ogrehasenemy;
	public boolean ogreattack;
	public boolean bogrefire;
	protected double attackRange;

	public EntityOgre(World world1) {
		super(world1);
		this.attackStrength = 3;
		this.attackRange = 16.0D;
		this.texture = "/mob/ogre.png";
		this.setSize(1.5F, 4.0F);
		this.health = this.getMaxHealth();
		this.bogrefire = false;
		this.ogreattack = false;
		this.ogrehasenemy = false;
		this.destroyForce = 3.0F;
		this.isImmuneToFire = false;
		this.frequencyA = 30;
	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
		nBTTagCompound1.setBoolean("OgreAttack", this.ogreattack);
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
		this.ogreattack = nBTTagCompound1.getBoolean("OgreAttack");
	}

	protected String getLivingSound() {
		return "mob.ogre.ogre";
	}

	protected String getHurtSound() {
		return "mov.ogre.hurt";
	}

	protected String getDeathSound() {
		return "mov.ogre.death";
	}

	protected int getDropItemId() {
		return Block.obsidian.blockID;
	}

	protected Entity findPlayerToAttack() {
		float f1 = this.getBrightness(1.0F);
		if(f1 < 0.5F) {
			EntityPlayer entityPlayer2 = this.worldObj.getClosestPlayerToEntity(this, this.attackRange);
			if(entityPlayer2 != null && this.worldObj.difficultySetting > 0) {
				this.ogrehasenemy = true;
				return entityPlayer2;
			}
			
			// Find close entities of type EntityAmazon, EntityPigman/EntityCowman, etc
			List<Entity> list = this.worldObj.getEntitiesWithinAABB(ISentient.class, this.boundingBox.expand(16.0D, 4.0D, 16.0D));
			Collections.sort(list, new AttackableTargetSorter(this));
			
			if(list.size() > 0) return list.get(0);
		} else {
			List<Entity> list = this.worldObj.getEntitiesWithinAABBbutNotMe(this, IMob.class, this.boundingBox.expand(16.0D, 4.0D, 16.0D));
			Collections.sort(list, new AttackableTargetSorter(this));
			
			if(list.size() > 0) return list.get(0);
		}

		this.ogrehasenemy = false;
		return null;
	}

	public boolean attackEntityFrom(Entity attacker, int damage) {
		if(super.attackEntityFrom(DamageSource.causeMobDamage(this), damage)) {
			if(this.riddenByEntity != attacker && this.ridingEntity != attacker) {
				if(attacker != this && this.worldObj.difficultySetting > 0) {
					this.entityToAttack = attacker;
					this.ogrehasenemy = true;
				}

				return true;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public void onLivingUpdate() {
		this.findPlayerToAttack();
		if(this.ogrehasenemy && this.rand.nextInt(this.frequencyA) == 0) {
			this.ogreattack = true;
			this.attackTime = 15;
		}

		super.onLivingUpdate();
	}

	@Override
	protected void attackEntity(Entity entity1, float f2) {
		if((double)f2 < 2.2D && entity1.boundingBox.maxY > this.boundingBox.minY && entity1.boundingBox.minY < this.boundingBox.maxY && this.worldObj.difficultySetting > 0) {
			entity1.attackEntityFrom(DamageSource.causeMobDamage(this), this.attackStrength);
		}

	}

	public void DestroyingOgre() {
		Destroyer.DestroyBlast(this.worldObj, this, this.posX, this.posY + 1.0D, this.posZ, this.destroyForce, this.bogrefire);
	}

	@Override
	public int getMaxHealth() {
		return 40;
	}
	
	@Override
	public boolean getCanSpawnHere() {
		return this.worldObj.difficultySetting > 0 && 
				this.worldObj.checkIfAABBIsClear(this.boundingBox) && 
				this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).size() == 0 &&
				!this.worldObj.isAnyLiquid(this.boundingBox) && 
				!this.worldObj.canBlockSeeTheSky((int)(this.posX + .5), (int)(this.posY + 1), (int)(this.posZ + .5));
	}
}
