package net.minecraft.world.entity.monster;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.Chunk;

public class EntitySlime extends EntityLiving implements IMob {
	public float field_40139_a;
	public float field_768_a;
	public float field_767_b;
	private int slimeJumpDelay = 0;

	public EntitySlime(World world1) {
		super(world1);
		this.texture = "/mob/slime.png";
		int i2 = 1 << this.rand.nextInt(3);
		this.yOffset = 0.0F;
		this.slimeJumpDelay = this.rand.nextInt(20) + 10;
		this.setSlimeSize(i2);
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, new Byte((byte)1));
	}

	public void setSlimeSize(int i1) {
		this.dataWatcher.updateObject(16, new Byte((byte)i1));
		this.setSize(0.6F * (float)i1, 0.6F * (float)i1);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.setEntityHealth(this.getMaxHealth());
		this.experienceValue = i1;
	}

	public int getMaxHealth() {
		int i1 = this.getSlimeSize();
		return i1 * i1;
	}

	public int getSlimeSize() {
		return this.dataWatcher.getWatchableObjectByte(16);
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
		compoundTag.setInteger("Size", this.getSlimeSize() - 1);
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
		this.setSlimeSize(compoundTag.getInteger("Size") + 1);
	}

	protected String getSlimeParticle() {
		return "slime";
	}

	protected String func_40138_aj() {
		return "mob.slime";
	}

	public void onUpdate() {
		if(!this.worldObj.isRemote && this.worldObj.difficultySetting == 0 && this.getSlimeSize() > 0) {
			this.isDead = true;
		}

		this.field_768_a += (this.field_40139_a - this.field_768_a) * 0.5F;
		this.field_767_b = this.field_768_a;
		boolean z1 = this.onGround;
		super.onUpdate();
		if(this.onGround && !z1) {
			int i2 = this.getSlimeSize();

			for(int i3 = 0; i3 < i2 * 8; ++i3) {
				float f4 = this.rand.nextFloat() * (float)Math.PI * 2.0F;
				float f5 = this.rand.nextFloat() * 0.5F + 0.5F;
				float f6 = MathHelper.sin(f4) * (float)i2 * 0.5F * f5;
				float f7 = MathHelper.cos(f4) * (float)i2 * 0.5F * f5;
				this.worldObj.spawnParticle(this.getSlimeParticle(), this.posX + (double)f6, this.boundingBox.minY, this.posZ + (double)f7, 0.0D, 0.0D, 0.0D);
			}

			if(this.func_40134_ak()) {
				this.worldObj.playSoundAtEntity(this, this.func_40138_aj(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
			}

			this.field_40139_a = -0.5F;
		}

		this.func_40136_ag();
	}

	protected void updateEntityActionState() {
		this.despawnEntity();
		EntityPlayer entityPlayer1 = this.worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);
		if(entityPlayer1 != null) {
			this.faceEntity(entityPlayer1, 10.0F, 20.0F);
		}

		if(this.onGround && this.slimeJumpDelay-- <= 0) {
			this.slimeJumpDelay = this.func_40131_af();
			if(entityPlayer1 != null) {
				this.slimeJumpDelay /= 3;
			}

			this.isJumping = true;
			if(this.func_40133_ao()) {
				this.worldObj.playSoundAtEntity(this, this.func_40138_aj(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
			}

			this.field_40139_a = 1.0F;
			this.moveStrafing = 1.0F - this.rand.nextFloat() * 2.0F;
			this.moveForward = (float)(1 * this.getSlimeSize());
		} else {
			this.isJumping = false;
			if(this.onGround) {
				this.moveStrafing = this.moveForward = 0.0F;
			}
		}

	}

	protected void func_40136_ag() {
		this.field_40139_a *= 0.6F;
	}

	protected int func_40131_af() {
		return this.rand.nextInt(20) + 10;
	}

	protected EntitySlime createInstance() {
		return new EntitySlime(this.worldObj);
	}

	public void setDead() {
		int i1 = this.getSlimeSize();
		if(!this.worldObj.isRemote && i1 > 1 && this.getHealth() <= 0) {
			int i2 = 2 + this.rand.nextInt(3);

			for(int i3 = 0; i3 < i2; ++i3) {
				float f4 = ((float)(i3 % 2) - 0.5F) * (float)i1 / 4.0F;
				float f5 = ((float)(i3 / 2) - 0.5F) * (float)i1 / 4.0F;
				EntitySlime entitySlime6 = this.createInstance();
				entitySlime6.setSlimeSize(i1 / 2);
				entitySlime6.setLocationAndAngles(this.posX + (double)f4, this.posY + 0.5D, this.posZ + (double)f5, this.rand.nextFloat() * 360.0F, 0.0F);
				this.worldObj.spawnEntityInWorld(entitySlime6);
			}
		}

		super.setDead();
	}

	public void onCollideWithPlayer(EntityPlayer entityPlayer1) {
		if(this.func_40137_ah()) {
			int i2 = this.getSlimeSize();
			if(this.canEntityBeSeen(entityPlayer1) && (double)this.getDistanceToEntity(entityPlayer1) < 0.6D * (double)i2 && entityPlayer1.attackEntityFrom(DamageSource.causeMobDamage(this), this.func_40130_ai())) {
				this.worldObj.playSoundAtEntity(this, "mob.slimeattack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			}
		}

	}

	protected boolean func_40137_ah() {
		return this.getSlimeSize() > 1;
	}

	protected int func_40130_ai() {
		return this.getSlimeSize();
	}

	protected String getHurtSound() {
		return "mob.slime";
	}

	protected String getDeathSound() {
		return "mob.slime";
	}

	protected int getDropItemId() {
		return this.getSlimeSize() == 1 ? Item.slimeBall.shiftedIndex : 0;
	}

	public boolean getCanSpawnHere() {
		Chunk chunk1 = this.worldObj.getChunkFromBlockCoords(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posZ));
		return (this.getSlimeSize() == 1 || this.worldObj.difficultySetting > 0) && this.rand.nextInt(10) == 0 && chunk1.getRandomWithSeed(987234911L).nextInt(10) == 0 && this.posY < 40.0D ? super.getCanSpawnHere() : false;
	}

	protected float getSoundVolume() {
		return 0.4F * (float)this.getSlimeSize();
	}

	public int getVerticalFaceSpeed() {
		return 0;
	}

	protected boolean func_40133_ao() {
		return this.getSlimeSize() > 1;
	}

	protected boolean func_40134_ak() {
		return this.getSlimeSize() > 2;
	}
}
