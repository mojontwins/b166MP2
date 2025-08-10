package net.minecraft.src;

import java.util.List;

public class EntityBird extends EntityAnimal {
	private boolean hasreproduced;
	public int typeint;
	public boolean typechosen;
	private boolean fleeing;
	public float wingb;
	public float wingc;
	public float wingd;
	public float winge;
	public float wingh;
	public boolean tamed;
	public boolean picked;

	public EntityBird(World world1) {
		super(world1);
		this.texture = "/mob/birdblue.png";
		this.setSize(0.4F, 0.3F);
		this.health = 2;
		this.isCollidedVertically = true;
		this.wingb = 0.0F;
		this.wingc = 0.0F;
		this.wingh = 1.0F;
		this.fleeing = false;
		this.tamed = false;
		this.typeint = 0;
		this.typechosen = false;
		this.hasreproduced = false;
	}

	protected void fall(float f1) {
	}

	public int getMaxSpawnedInChunk() {
		return 6;
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.winge = this.wingb;
		this.wingd = this.wingc;
		this.wingc = (float)((double)this.wingc + (double)(this.onGround ? -1 : 4) * 0.3D);
		if(this.wingc < 0.0F) {
			this.wingc = 0.0F;
		}

		if(this.wingc > 1.0F) {
			this.wingc = 1.0F;
		}

		if(!this.onGround && this.wingh < 1.0F) {
			this.wingh = 1.0F;
		}

		this.wingh = (float)((double)this.wingh * 0.9D);
		if(!this.onGround && this.motionY < 0.0D) {
			this.motionY *= 0.8D;
		}

		this.wingb += this.wingh * 2.0F;
		EntityLiving entityLiving1 = this.getClosestEntityLiving(this, 4.0D);
		if(entityLiving1 != null && !this.tamed && this.canEntityBeSeen(entityLiving1)) {
			this.fleeing = true;
		}

		if(this.rand.nextInt(300) == 0) {
			this.fleeing = true;
		}

		if(this.fleeing) {
			if(this.FlyToNextTree()) {
				this.fleeing = false;
			}

			int[] i2 = this.ReturnNearestMaterialCoord(this, Material.leaves, 16.0D);
			if(i2[0] == -1) {
				for(int i3 = 0; i3 < 2; ++i3) {
					this.WingFlap();
				}

				this.fleeing = false;
			}

			if(this.rand.nextInt(50) == 0) {
				this.fleeing = false;
			}
		}

		if(!this.fleeing) {
			EntityItem entityItem4 = this.getClosestSeeds(this, 12.0D);
			if(entityItem4 != null) {
				this.FlyToNextEntity(entityItem4);
				EntityItem entityItem5 = this.getClosestSeeds(this, 1.0D);
				if(this.rand.nextInt(50) == 0 && entityItem5 != null) {
					entityItem5.setEntityDead();
					this.tamed = true;
				}
			}
		}

	}

	protected void updatePlayerActionState() {
		if(this.onGround && this.rand.nextInt(10) == 0 && (this.motionX > 0.05D || this.motionZ > 0.05D || this.motionX < -0.05D || this.motionZ < -0.05D)) {
			this.motionY = 0.25D;
		}

		EntityPlayer entityPlayer1 = (EntityPlayer)this.ridingEntity;
		if(entityPlayer1 != null) {
			this.rotationYaw = entityPlayer1.rotationYaw;
			entityPlayer1.fallDistance = 0.0F;
			if(entityPlayer1.motionY < -0.1D) {
				entityPlayer1.motionY = -0.1D;
			}
		}

		if(this.fleeing && this.picked) {
			if(this.onGround) {
				this.picked = false;
			}
		} else {
			super.updatePlayerActionState();
		}

	}

	public void setEntityDead() {
		if(!this.tamed || this.health <= 0) {
			super.setEntityDead();
		}
	}

	public boolean interact(EntityPlayer entityPlayer1) {
		if(!this.tamed) {
			return false;
		} else {
			this.rotationYaw = entityPlayer1.rotationYaw;
			this.mountEntity(entityPlayer1);
			if(this.ridingEntity != null) {
				this.picked = true;
			} else {
				this.worldObj.playSoundAtEntity(this, "mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			}

			this.motionX = entityPlayer1.motionX * 5.0D;
			this.motionY = entityPlayer1.motionY / 2.0D + 0.5D;
			this.motionZ = entityPlayer1.motionZ * 5.0D;
			return true;
		}
	}

	public double getYOffset() {
		return this.ridingEntity instanceof EntityPlayer ? (double)(this.yOffset - 1.15F) : (double)this.yOffset;
	}

	private EntityItem getClosestSeeds(Entity entity1, double d2) {
		double d4 = -1.0D;
		EntityItem entityItem6 = null;
		List list7 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(d2, d2, d2));

		for(int i8 = 0; i8 < list7.size(); ++i8) {
			Entity entity9 = (Entity)list7.get(i8);
			if(entity9 instanceof EntityItem) {
				EntityItem entityItem10 = (EntityItem)entity9;
				if(entityItem10.item.itemID == Item.seeds.shiftedIndex) {
					double d11 = entityItem10.getDistanceSq(entity1.posX, entity1.posY, entity1.posZ);
					if((d2 < 0.0D || d11 < d2 * d2) && (d4 == -1.0D || d11 < d4)) {
						d4 = d11;
						entityItem6 = entityItem10;
					}
				}
			}
		}

		return entityItem6;
	}

	private EntityLiving getClosestEntityLiving(Entity entity1, double d2) {
		double d4 = -1.0D;
		EntityLiving entityLiving6 = null;
		List list7 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(d2, d2, d2));

		for(int i8 = 0; i8 < list7.size(); ++i8) {
			Entity entity9 = (Entity)list7.get(i8);
			if(entity9 instanceof EntityLiving && !(entity9 instanceof EntityBird)) {
				double d10 = entity9.getDistanceSq(entity1.posX, entity1.posY, entity1.posZ);
				if((d2 < 0.0D || d10 < d2 * d2) && (d4 == -1.0D || d10 < d4) && ((EntityLiving)entity9).canEntityBeSeen(entity1)) {
					d4 = d10;
					entityLiving6 = (EntityLiving)entity9;
				}
			}
		}

		return entityLiving6;
	}

	private boolean FlyToNextEntity(Entity entity1) {
		if(entity1 != null) {
			int i2 = MathHelper.floor_double(entity1.posX);
			int i3 = MathHelper.floor_double(entity1.posY);
			int i4 = MathHelper.floor_double(entity1.posZ);
			this.faceTreeTop(i2, i3, i4, 30.0F);
			if(MathHelper.floor_double(this.posY) < i3) {
				this.motionY += 0.15D;
			}

			double d5;
			if(this.posX < entity1.posX) {
				d5 = entity1.posX - this.posX;
				if(d5 > 0.5D) {
					this.motionX += 0.05D;
				}
			} else {
				d5 = this.posX - entity1.posX;
				if(d5 > 0.5D) {
					this.motionX -= 0.05D;
				}
			}

			if(this.posZ < entity1.posZ) {
				d5 = entity1.posZ - this.posZ;
				if(d5 > 0.5D) {
					this.motionZ += 0.05D;
				}
			} else {
				d5 = this.posZ - entity1.posZ;
				if(d5 > 0.5D) {
					this.motionZ -= 0.05D;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private void WingFlap() {
		this.motionY += 0.05D;
		if(this.rand.nextInt(30) == 0) {
			this.motionX += 0.2D;
		}

		if(this.rand.nextInt(30) == 0) {
			this.motionX -= 0.2D;
		}

		if(this.rand.nextInt(30) == 0) {
			this.motionZ += 0.2D;
		}

		if(this.rand.nextInt(30) == 0) {
			this.motionZ -= 0.2D;
		}

	}

	private boolean FlyToNextTree() {
		int[] i1 = this.ReturnNearestMaterialCoord(this, Material.leaves, 20.0D);
		int[] i2 = this.FindTreeTop(i1[0], i1[1], i1[2]);
		if(i2[1] != 0) {
			int i3 = i2[0];
			int i4 = i2[1];
			int i5 = i2[2];
			this.faceTreeTop(i3, i4, i5, 30.0F);
			if(i4 - MathHelper.floor_double(this.posY) > 2) {
				this.motionY += 0.15D;
			}

			boolean z6 = false;
			boolean z7 = false;
			int i10;
			if(this.posX < (double)i3) {
				i10 = i3 - MathHelper.floor_double(this.posX);
				this.motionX += 0.05D;
			} else {
				i10 = MathHelper.floor_double(this.posX) - i3;
				this.motionX -= 0.05D;
			}

			int i11;
			if(this.posZ < (double)i5) {
				i11 = i5 - MathHelper.floor_double(this.posZ);
				this.motionZ += 0.05D;
			} else {
				i11 = MathHelper.floor_double(this.posX) - i5;
				this.motionZ -= 0.05D;
			}

			double d8 = (double)(i10 + i11);
			if(d8 < 3.0D) {
				return true;
			}
		}

		return false;
	}

	public void setType(int i1) {
		this.typeint = i1;
		this.typechosen = false;
		this.chooseType();
	}

	public void chooseType() {
		if(this.typeint == 0) {
			int i1 = this.rand.nextInt(100);
			if(i1 <= 15) {
				this.typeint = 1;
			} else if(i1 <= 30) {
				this.typeint = 2;
			} else if(i1 <= 45) {
				this.typeint = 3;
			} else if(i1 <= 60) {
				this.typeint = 4;
			} else if(i1 <= 75) {
				this.typeint = 5;
			} else if(i1 <= 90) {
				this.typeint = 6;
			} else {
				this.typeint = 2;
			}
		}

		if(!this.typechosen) {
			if(this.typeint == 1) {
				this.texture = "/mob/birdwhite.png";
			} else if(this.typeint == 2) {
				this.texture = "/mob/birdblack.png";
			} else if(this.typeint == 3) {
				this.texture = "/mob/birdgreen.png";
			} else if(this.typeint == 4) {
				this.texture = "/mob/birdblue.png";
			} else if(this.typeint == 5) {
				this.texture = "/mob/birdyellow.png";
			} else if(this.typeint == 6) {
				this.texture = "/mob/birdred.png";
			}
		}

		this.typechosen = true;
	}

	public void faceTreeTop(int i1, int i2, int i3, float f4) {
		double d5 = (double)i1 - this.posX;
		double d7 = (double)i3 - this.posZ;
		double d9 = (double)i2 - this.posY;
		double d11 = (double)MathHelper.sqrt_double(d5 * d5 + d7 * d7);
		float f13 = (float)(Math.atan2(d7, d5) * 180.0D / 3.141592741012573D) - 90.0F;
		float f14 = (float)(Math.atan2(d9, d11) * 180.0D / 3.141592741012573D);
		this.rotationPitch = -this.b(this.rotationPitch, f14, f4);
		this.rotationYaw = this.b(this.rotationYaw, f13, f4);
	}

	private float b(float f1, float f2, float f3) {
		float f4;
		for(f4 = f2 - f1; f4 < -180.0F; f4 += 360.0F) {
		}

		while(f4 >= 180.0F) {
			f4 -= 360.0F;
		}

		if(f4 > f3) {
			f4 = f3;
		}

		if(f4 < -f3) {
			f4 = -f3;
		}

		return f1 + f4;
	}

	private int[] FindTreeTop(int i1, int i2, int i3) {
		int i4 = i1 - 5;
		int i5 = i3 - 5;
		int i6 = i1 + 5;
		int i7 = i2 + 7;
		int i8 = i3 + 5;

		for(int i9 = i4; i9 < i6; ++i9) {
			for(int i10 = i5; i10 < i8; ++i10) {
				int i11 = this.worldObj.getBlockId(i9, i2, i10);
				if(i11 != 0 && Block.blocksList[i11].blockMaterial == Material.wood) {
					for(int i12 = i2; i12 < i7; ++i12) {
						int i13 = this.worldObj.getBlockId(i9, i12, i10);
						if(i13 == 0) {
							return new int[]{i9, i12 + 2, i10};
						}
					}
				}
			}
		}

		return new int[]{0, 0, 0};
	}

	public int[] ReturnNearestMaterialCoord(Entity entity1, Material material2, Double double3) {
		AxisAlignedBB axisAlignedBB4 = entity1.boundingBox.expand(double3.doubleValue(), double3.doubleValue(), double3.doubleValue());
		int i5 = MathHelper.floor_double(axisAlignedBB4.minX);
		int i6 = MathHelper.floor_double(axisAlignedBB4.maxX + 1.0D);
		int i7 = MathHelper.floor_double(axisAlignedBB4.minY);
		int i8 = MathHelper.floor_double(axisAlignedBB4.maxY + 1.0D);
		int i9 = MathHelper.floor_double(axisAlignedBB4.minZ);
		int i10 = MathHelper.floor_double(axisAlignedBB4.maxZ + 1.0D);

		for(int i11 = i5; i11 < i6; ++i11) {
			for(int i12 = i7; i12 < i8; ++i12) {
				for(int i13 = i9; i13 < i10; ++i13) {
					int i14 = this.worldObj.getBlockId(i11, i12, i13);
					if(i14 != 0 && Block.blocksList[i14].blockMaterial == material2) {
						return new int[]{i11, i12, i13};
					}
				}
			}
		}

		return new int[]{-1, 0, 0};
	}

	protected int getDropItemId() {
		return this.rand.nextInt(2) == 0 ? Item.feather.shiftedIndex : Item.seeds.shiftedIndex;
	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
		nBTTagCompound1.setInteger("TypeInt", this.typeint);
		nBTTagCompound1.setBoolean("HasReproduced", this.hasreproduced);
		nBTTagCompound1.setBoolean("Tamed", this.tamed);
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
		this.hasreproduced = nBTTagCompound1.getBoolean("HasReproduced");
		this.tamed = nBTTagCompound1.getBoolean("Tamed");
		this.typeint = nBTTagCompound1.getInteger("TypeInt");
	}

	protected String getLivingSound() {
		return this.typeint == 1 ? "birdwhite" : (this.typeint == 2 ? "birdblack" : (this.typeint == 3 ? "birdgreen" : (this.typeint == 4 ? "birdblue" : (this.typeint == 5 ? "birdyellow" : "birdred"))));
	}

	protected String getHurtSound() {
		return "birdhurt";
	}

	protected String getDeathSound() {
		return "birddying";
	}

	public boolean getCanSpawnHere() {
		return ((Integer)mod_mocreatures.birdfreq.get()).intValue() > 0 && super.getCanSpawnHere();
	}
}
