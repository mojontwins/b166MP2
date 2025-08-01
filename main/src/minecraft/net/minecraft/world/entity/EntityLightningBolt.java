package net.minecraft.world.entity;

import java.util.List;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class EntityLightningBolt extends EntityWeatherEffect {
	private int lightningState;
	public long boltVertex = 0L;
	private int boltLivingTime;

	public EntityLightningBolt(World world1, double d2, double d4, double d6) {
		super(world1);
		this.setLocationAndAngles(d2, d4, d6, 0.0F, 0.0F);
		this.lightningState = 2;
		this.boltVertex = this.rand.nextLong();
		this.boltLivingTime = this.rand.nextInt(3) + 1;
		if(world1.difficultySetting >= 2 && world1.doChunksNearChunkExist(MathHelper.floor_double(d2), MathHelper.floor_double(d4), MathHelper.floor_double(d6), 10)) {
			int i8 = MathHelper.floor_double(d2);
			int i9 = MathHelper.floor_double(d4);
			int i10 = MathHelper.floor_double(d6);
			if(world1.getBlockId(i8, i9, i10) == 0 && Block.fire.canPlaceBlockAt(world1, i8, i9, i10)) {
				world1.setBlockWithNotify(i8, i9, i10, Block.fire.blockID);
			}

			for(i8 = 0; i8 < 4; ++i8) {
				i9 = MathHelper.floor_double(d2) + this.rand.nextInt(3) - 1;
				i10 = MathHelper.floor_double(d4) + this.rand.nextInt(3) - 1;
				int i11 = MathHelper.floor_double(d6) + this.rand.nextInt(3) - 1;
				if(world1.getBlockId(i9, i10, i11) == 0 && Block.fire.canPlaceBlockAt(world1, i9, i10, i11)) {
					world1.setBlockWithNotify(i9, i10, i11, Block.fire.blockID);
				}
			}
		}

	}

	public void onUpdate() {
		super.onUpdate();
		if(this.lightningState == 2) {
			this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "ambient.weather.thunder", 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F);
			this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.explode", 2.0F, 0.5F + this.rand.nextFloat() * 0.2F);
		}

		--this.lightningState;
		if(this.lightningState < 0) {
			if(this.boltLivingTime == 0) {
				this.setDead();
			} else if(this.lightningState < -this.rand.nextInt(10)) {
				--this.boltLivingTime;
				this.lightningState = 1;
				this.boltVertex = this.rand.nextLong();
				if(this.worldObj.doChunksNearChunkExist(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 10)) {
					int i1 = MathHelper.floor_double(this.posX);
					int i2 = MathHelper.floor_double(this.posY);
					int i3 = MathHelper.floor_double(this.posZ);
					if(this.worldObj.getBlockId(i1, i2, i3) == 0 && Block.fire.canPlaceBlockAt(this.worldObj, i1, i2, i3)) {
						this.worldObj.setBlockWithNotify(i1, i2, i3, Block.fire.blockID);
					}
				}
			}
		}

		if(this.lightningState >= 0) {
			double d6 = 3.0D;
			List<Entity> list7 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBoxFromPool(this.posX - d6, this.posY - d6, this.posZ - d6, this.posX + d6, this.posY + 6.0D + d6, this.posZ + d6));

			for(int i4 = 0; i4 < list7.size(); ++i4) {
				Entity entity5 = (Entity)list7.get(i4);
				entity5.onStruckByLightning(this);
			}

			this.worldObj.lightningFlash = 2;
		}

	}

	protected void entityInit() {
	}

	protected void readEntityFromNBT(NBTTagCompound compoundTag) {
	}

	protected void writeEntityToNBT(NBTTagCompound compoundTag) {
	}

	public boolean isInRangeToRenderVec3D(Vec3D vec3D1) {
		return this.lightningState >= 0;
	}
}
