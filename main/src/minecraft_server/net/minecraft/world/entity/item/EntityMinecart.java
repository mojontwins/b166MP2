package net.minecraft.world.entity.item;

import java.util.List;

import com.mojang.nbt.NBTTagCompound;
import com.mojang.nbt.NBTTagList;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.EntityIronGolem;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.inventory.IInventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockRail;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class EntityMinecart extends Entity implements IInventory {
	private ItemStack[] cargoItems;
	private int fuel;
	private boolean field_856_i;
	public int minecartType;
	public double pushX;
	public double pushZ;
	private static final int[][][] field_855_j = new int[][][]{{{0, 0, -1}, {0, 0, 1}}, {{-1, 0, 0}, {1, 0, 0}}, {{-1, -1, 0}, {1, 0, 0}}, {{-1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}}, {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, {-1, 0, 0}}, {{0, 0, -1}, {-1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};
	private int turnProgress;
	private double minecartX;
	private double minecartY;
	private double minecartZ;
	private double minecartYaw;
	private double minecartPitch;
	private double velocityX;
	private double velocityY;
	private double velocityZ;

	public EntityMinecart(World world1) {
		super(world1);
		this.cargoItems = new ItemStack[36];
		this.fuel = 0;
		this.field_856_i = false;
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.7F);
		this.yOffset = this.height / 2.0F;
	}

	protected boolean canTriggerWalking() {
		return false;
	}

	protected void entityInit() {
		this.dataWatcher.addObject(16, new Byte((byte)0));
		this.dataWatcher.addObject(17, new Integer(0));
		this.dataWatcher.addObject(18, new Integer(1));
		this.dataWatcher.addObject(19, new Integer(0));
	}

	public AxisAlignedBB getCollisionBox(Entity entity1) {
		return entity1.boundingBox;
	}

	public AxisAlignedBB getBoundingBox() {
		return null;
	}

	public boolean canBePushed() {
		return true;
	}

	public EntityMinecart(World world1, double d2, double d4, double d6, int i8) {
		this(world1);
		this.setPosition(d2, d4 + (double)this.yOffset, d6);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = d2;
		this.prevPosY = d4;
		this.prevPosZ = d6;
		this.minecartType = i8;
	}

	public double getMountedYOffset() {
		return (double)this.height * 0.0D - (double)0.3F;
	}

	public boolean attackEntityFrom(DamageSource damageSource1, int i2) {
		if(!this.worldObj.isRemote && !this.isDead) {
			this.func_41029_h(-this.func_41030_m());
			this.func_41028_c(10);
			this.setBeenAttacked();
			this.func_41024_b(this.func_41025_i() + i2 * 10);
			if(this.func_41025_i() > 40) {
				if(this.riddenByEntity != null) {
					this.riddenByEntity.mountEntity(this);
				}

				this.setDead();
				this.dropItemWithOffset(Item.minecartEmpty.shiftedIndex, 1, 0.0F);
				if(this.minecartType == 1) {
					EntityMinecart entityMinecart3 = this;

					for(int i4 = 0; i4 < entityMinecart3.getSizeInventory(); ++i4) {
						ItemStack itemStack5 = entityMinecart3.getStackInSlot(i4);
						if(itemStack5 != null) {
							float f6 = this.rand.nextFloat() * 0.8F + 0.1F;
							float f7 = this.rand.nextFloat() * 0.8F + 0.1F;
							float f8 = this.rand.nextFloat() * 0.8F + 0.1F;

							while(itemStack5.stackSize > 0) {
								int i9 = this.rand.nextInt(21) + 10;
								if(i9 > itemStack5.stackSize) {
									i9 = itemStack5.stackSize;
								}

								itemStack5.stackSize -= i9;
								EntityItem entityItem10 = new EntityItem(this.worldObj, this.posX + (double)f6, this.posY + (double)f7, this.posZ + (double)f8, new ItemStack(itemStack5.itemID, i9, itemStack5.getItemDamage()));
								float f11 = 0.05F;
								entityItem10.motionX = (double)((float)this.rand.nextGaussian() * f11);
								entityItem10.motionY = (double)((float)this.rand.nextGaussian() * f11 + 0.2F);
								entityItem10.motionZ = (double)((float)this.rand.nextGaussian() * f11);
								this.worldObj.spawnEntityInWorld(entityItem10);
							}
						}
					}

					this.dropItemWithOffset(Block.chest.blockID, 1, 0.0F);
				} else if(this.minecartType == 2) {
					this.dropItemWithOffset(Block.stoneOvenIdle.blockID, 1, 0.0F);
				}
			}

			return true;
		} else {
			return true;
		}
	}

	public void performHurtAnimation() {
		this.func_41029_h(-this.func_41030_m());
		this.func_41028_c(10);
		this.func_41024_b(this.func_41025_i() + this.func_41025_i() * 10);
	}

	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public void setDead() {
		for(int i1 = 0; i1 < this.getSizeInventory(); ++i1) {
			ItemStack itemStack2 = this.getStackInSlot(i1);
			if(itemStack2 != null) {
				float f3 = this.rand.nextFloat() * 0.8F + 0.1F;
				float f4 = this.rand.nextFloat() * 0.8F + 0.1F;
				float f5 = this.rand.nextFloat() * 0.8F + 0.1F;

				while(itemStack2.stackSize > 0) {
					int i6 = this.rand.nextInt(21) + 10;
					if(i6 > itemStack2.stackSize) {
						i6 = itemStack2.stackSize;
					}

					itemStack2.stackSize -= i6;
					EntityItem entityItem7 = new EntityItem(this.worldObj, this.posX + (double)f3, this.posY + (double)f4, this.posZ + (double)f5, new ItemStack(itemStack2.itemID, i6, itemStack2.getItemDamage()));
					if(itemStack2.hasTagCompound()) {
						entityItem7.item.setTagCompound((NBTTagCompound)itemStack2.getTagCompound().copy());
					}

					float f8 = 0.05F;
					entityItem7.motionX = (double)((float)this.rand.nextGaussian() * f8);
					entityItem7.motionY = (double)((float)this.rand.nextGaussian() * f8 + 0.2F);
					entityItem7.motionZ = (double)((float)this.rand.nextGaussian() * f8);
					this.worldObj.spawnEntityInWorld(entityItem7);
				}
			}
		}

		super.setDead();
	}

	public void onUpdate() {
		if(this.func_41023_l() > 0) {
			this.func_41028_c(this.func_41023_l() - 1);
		}

		if(this.func_41025_i() > 0) {
			this.func_41024_b(this.func_41025_i() - 1);
		}

		if(this.posY < -64.0D) {
			this.kill();
		}

		if(this.isMinecartPowered() && this.rand.nextInt(4) == 0) {
			this.worldObj.spawnParticle("largesmoke", this.posX, this.posY + 0.8D, this.posZ, 0.0D, 0.0D, 0.0D);
		}

		if(this.worldObj.isRemote) {
			if(this.turnProgress > 0) {
				double d45 = this.posX + (this.minecartX - this.posX) / (double)this.turnProgress;
				double d46 = this.posY + (this.minecartY - this.posY) / (double)this.turnProgress;
				double d5 = this.posZ + (this.minecartZ - this.posZ) / (double)this.turnProgress;

				double d7;
				for(d7 = this.minecartYaw - (double)this.rotationYaw; d7 < -180.0D; d7 += 360.0D) {
				}

				while(d7 >= 180.0D) {
					d7 -= 360.0D;
				}

				this.rotationYaw = (float)((double)this.rotationYaw + d7 / (double)this.turnProgress);
				this.rotationPitch = (float)((double)this.rotationPitch + (this.minecartPitch - (double)this.rotationPitch) / (double)this.turnProgress);
				--this.turnProgress;
				this.setPosition(d45, d46, d5);
				this.setRotation(this.rotationYaw, this.rotationPitch);
			} else {
				this.setPosition(this.posX, this.posY, this.posZ);
				this.setRotation(this.rotationYaw, this.rotationPitch);
			}

		} else {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			this.motionY -= (double)0.04F;
			int i1 = MathHelper.floor_double(this.posX);
			int i2 = MathHelper.floor_double(this.posY);
			int i3 = MathHelper.floor_double(this.posZ);
			if(BlockRail.isRailBlockAt(this.worldObj, i1, i2 - 1, i3)) {
				--i2;
			}

			double d4 = 0.4D;
			double d6 = 2.0D / 256D;
			int i8 = this.worldObj.getBlockId(i1, i2, i3);
			if(BlockRail.isRailBlock(i8)) {
				Vec3D vec3D9 = this.func_514_g(this.posX, this.posY, this.posZ);
				int i10 = this.worldObj.getBlockMetadata(i1, i2, i3);
				this.posY = (double)i2;
				boolean z11 = false;
				boolean z12 = false;
				if(i8 == Block.railPowered.blockID) {
					z11 = (i10 & 8) != 0;
					z12 = !z11;
				}

				if(((BlockRail)Block.blocksList[i8]).isPowered()) {
					i10 &= 7;
				}

				if(i10 >= 2 && i10 <= 5) {
					this.posY = (double)(i2 + 1);
				}

				if(i10 == 2) {
					this.motionX -= d6;
				}

				if(i10 == 3) {
					this.motionX += d6;
				}

				if(i10 == 4) {
					this.motionZ += d6;
				}

				if(i10 == 5) {
					this.motionZ -= d6;
				}

				int[][] i13 = field_855_j[i10];
				double d14 = (double)(i13[1][0] - i13[0][0]);
				double d16 = (double)(i13[1][2] - i13[0][2]);
				double d18 = Math.sqrt(d14 * d14 + d16 * d16);
				double d20 = this.motionX * d14 + this.motionZ * d16;
				if(d20 < 0.0D) {
					d14 = -d14;
					d16 = -d16;
				}

				double d22 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
				this.motionX = d22 * d14 / d18;
				this.motionZ = d22 * d16 / d18;
				double d24;
				if(z12) {
					d24 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
					if(d24 < 0.03D) {
						this.motionX *= 0.0D;
						this.motionY *= 0.0D;
						this.motionZ *= 0.0D;
					} else {
						this.motionX *= 0.5D;
						this.motionY *= 0.0D;
						this.motionZ *= 0.5D;
					}
				}

				d24 = 0.0D;
				double d26 = (double)i1 + 0.5D + (double)i13[0][0] * 0.5D;
				double d28 = (double)i3 + 0.5D + (double)i13[0][2] * 0.5D;
				double d30 = (double)i1 + 0.5D + (double)i13[1][0] * 0.5D;
				double d32 = (double)i3 + 0.5D + (double)i13[1][2] * 0.5D;
				d14 = d30 - d26;
				d16 = d32 - d28;
				double d34;
				double d36;
				double d38;
				if(d14 == 0.0D) {
					this.posX = (double)i1 + 0.5D;
					d24 = this.posZ - (double)i3;
				} else if(d16 == 0.0D) {
					this.posZ = (double)i3 + 0.5D;
					d24 = this.posX - (double)i1;
				} else {
					d34 = this.posX - d26;
					d36 = this.posZ - d28;
					d38 = (d34 * d14 + d36 * d16) * 2.0D;
					d24 = d38;
				}

				this.posX = d26 + d14 * d24;
				this.posZ = d28 + d16 * d24;
				this.setPosition(this.posX, this.posY + (double)this.yOffset, this.posZ);
				d34 = this.motionX;
				d36 = this.motionZ;
				if(this.riddenByEntity != null) {
					d34 *= 0.75D;
					d36 *= 0.75D;
				}

				if(d34 < -d4) {
					d34 = -d4;
				}

				if(d34 > d4) {
					d34 = d4;
				}

				if(d36 < -d4) {
					d36 = -d4;
				}

				if(d36 > d4) {
					d36 = d4;
				}

				this.moveEntity(d34, 0.0D, d36);
				if(i13[0][1] != 0 && MathHelper.floor_double(this.posX) - i1 == i13[0][0] && MathHelper.floor_double(this.posZ) - i3 == i13[0][2]) {
					this.setPosition(this.posX, this.posY + (double)i13[0][1], this.posZ);
				} else if(i13[1][1] != 0 && MathHelper.floor_double(this.posX) - i1 == i13[1][0] && MathHelper.floor_double(this.posZ) - i3 == i13[1][2]) {
					this.setPosition(this.posX, this.posY + (double)i13[1][1], this.posZ);
				}

				if(this.riddenByEntity != null) {
					this.motionX *= (double)0.997F;
					this.motionY *= 0.0D;
					this.motionZ *= (double)0.997F;
				} else {
					if(this.minecartType == 2) {
						d38 = (double)MathHelper.sqrt_double(this.pushX * this.pushX + this.pushZ * this.pushZ);
						if(d38 > 0.01D) {
							this.pushX /= d38;
							this.pushZ /= d38;
							double d40 = 0.04D;
							this.motionX *= (double)0.8F;
							this.motionY *= 0.0D;
							this.motionZ *= (double)0.8F;
							this.motionX += this.pushX * d40;
							this.motionZ += this.pushZ * d40;
						} else {
							this.motionX *= (double)0.9F;
							this.motionY *= 0.0D;
							this.motionZ *= (double)0.9F;
						}
					}

					this.motionX *= (double)0.96F;
					this.motionY *= 0.0D;
					this.motionZ *= (double)0.96F;
				}

				Vec3D vec3D51 = this.func_514_g(this.posX, this.posY, this.posZ);
				if(vec3D51 != null && vec3D9 != null) {
					double d39 = (vec3D9.yCoord - vec3D51.yCoord) * 0.05D;
					d22 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
					if(d22 > 0.0D) {
						this.motionX = this.motionX / d22 * (d22 + d39);
						this.motionZ = this.motionZ / d22 * (d22 + d39);
					}

					this.setPosition(this.posX, vec3D51.yCoord, this.posZ);
				}

				int i52 = MathHelper.floor_double(this.posX);
				int i53 = MathHelper.floor_double(this.posZ);
				if(i52 != i1 || i53 != i3) {
					d22 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
					this.motionX = d22 * (double)(i52 - i1);
					this.motionZ = d22 * (double)(i53 - i3);
				}

				double d41;
				if(this.minecartType == 2) {
					d41 = (double)MathHelper.sqrt_double(this.pushX * this.pushX + this.pushZ * this.pushZ);
					if(d41 > 0.01D && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.001D) {
						this.pushX /= d41;
						this.pushZ /= d41;
						if(this.pushX * this.motionX + this.pushZ * this.motionZ < 0.0D) {
							this.pushX = 0.0D;
							this.pushZ = 0.0D;
						} else {
							this.pushX = this.motionX;
							this.pushZ = this.motionZ;
						}
					}
				}

				if(z11) {
					d41 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
					if(d41 > 0.01D) {
						double d43 = 0.06D;
						this.motionX += this.motionX / d41 * d43;
						this.motionZ += this.motionZ / d41 * d43;
					} else if(i10 == 1) {
						if(this.worldObj.isBlockNormalCube(i1 - 1, i2, i3)) {
							this.motionX = 0.02D;
						} else if(this.worldObj.isBlockNormalCube(i1 + 1, i2, i3)) {
							this.motionX = -0.02D;
						}
					} else if(i10 == 0) {
						if(this.worldObj.isBlockNormalCube(i1, i2, i3 - 1)) {
							this.motionZ = 0.02D;
						} else if(this.worldObj.isBlockNormalCube(i1, i2, i3 + 1)) {
							this.motionZ = -0.02D;
						}
					}
				}
			} else {
				if(this.motionX < -d4) {
					this.motionX = -d4;
				}

				if(this.motionX > d4) {
					this.motionX = d4;
				}

				if(this.motionZ < -d4) {
					this.motionZ = -d4;
				}

				if(this.motionZ > d4) {
					this.motionZ = d4;
				}

				if(this.onGround) {
					this.motionX *= 0.5D;
					this.motionY *= 0.5D;
					this.motionZ *= 0.5D;
				}

				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				if(!this.onGround) {
					this.motionX *= (double)0.95F;
					this.motionY *= (double)0.95F;
					this.motionZ *= (double)0.95F;
				}
			}

			this.rotationPitch = 0.0F;
			double d47 = this.prevPosX - this.posX;
			double d48 = this.prevPosZ - this.posZ;
			if(d47 * d47 + d48 * d48 > 0.001D) {
				this.rotationYaw = (float)(Math.atan2(d48, d47) * 180.0D / Math.PI);
				if(this.field_856_i) {
					this.rotationYaw += 180.0F;
				}
			}

			double d49;
			for(d49 = (double)(this.rotationYaw - this.prevRotationYaw); d49 >= 180.0D; d49 -= 360.0D) {
			}

			while(d49 < -180.0D) {
				d49 += 360.0D;
			}

			if(d49 < -170.0D || d49 >= 170.0D) {
				this.rotationYaw += 180.0F;
				this.field_856_i = !this.field_856_i;
			}

			this.setRotation(this.rotationYaw, this.rotationPitch);
			List<Entity> list15 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand((double)0.2F, 0.0D, (double)0.2F));
			if(list15 != null && list15.size() > 0) {
				for(int i50 = 0; i50 < list15.size(); ++i50) {
					Entity entity17 = (Entity)list15.get(i50);
					if(entity17 != this.riddenByEntity && entity17.canBePushed() && entity17 instanceof EntityMinecart) {
						entity17.applyEntityCollision(this);
					}
				}
			}

			if(this.riddenByEntity != null && this.riddenByEntity.isDead) {
				if(this.riddenByEntity.ridingEntity == this) {
					this.riddenByEntity.ridingEntity = null;
				}

				this.riddenByEntity = null;
			}

			if(this.fuel > 0) {
				--this.fuel;
			}

			if(this.fuel <= 0) {
				this.pushX = this.pushZ = 0.0D;
			}

			this.setMinecartPowered(this.fuel > 0);
		}
	}

	public Vec3D func_515_a(double d1, double d3, double d5, double d7) {
		int i9 = MathHelper.floor_double(d1);
		int i10 = MathHelper.floor_double(d3);
		int i11 = MathHelper.floor_double(d5);
		if(BlockRail.isRailBlockAt(this.worldObj, i9, i10 - 1, i11)) {
			--i10;
		}

		int i12 = this.worldObj.getBlockId(i9, i10, i11);
		if(!BlockRail.isRailBlock(i12)) {
			return null;
		} else {
			int i13 = this.worldObj.getBlockMetadata(i9, i10, i11);
			if(((BlockRail)Block.blocksList[i12]).isPowered()) {
				i13 &= 7;
			}

			d3 = (double)i10;
			if(i13 >= 2 && i13 <= 5) {
				d3 = (double)(i10 + 1);
			}

			int[][] i14 = field_855_j[i13];
			double d15 = (double)(i14[1][0] - i14[0][0]);
			double d17 = (double)(i14[1][2] - i14[0][2]);
			double d19 = Math.sqrt(d15 * d15 + d17 * d17);
			d15 /= d19;
			d17 /= d19;
			d1 += d15 * d7;
			d5 += d17 * d7;
			if(i14[0][1] != 0 && MathHelper.floor_double(d1) - i9 == i14[0][0] && MathHelper.floor_double(d5) - i11 == i14[0][2]) {
				d3 += (double)i14[0][1];
			} else if(i14[1][1] != 0 && MathHelper.floor_double(d1) - i9 == i14[1][0] && MathHelper.floor_double(d5) - i11 == i14[1][2]) {
				d3 += (double)i14[1][1];
			}

			return this.func_514_g(d1, d3, d5);
		}
	}

	public Vec3D func_514_g(double d1, double d3, double d5) {
		int i7 = MathHelper.floor_double(d1);
		int i8 = MathHelper.floor_double(d3);
		int i9 = MathHelper.floor_double(d5);
		if(BlockRail.isRailBlockAt(this.worldObj, i7, i8 - 1, i9)) {
			--i8;
		}

		int i10 = this.worldObj.getBlockId(i7, i8, i9);
		if(BlockRail.isRailBlock(i10)) {
			int i11 = this.worldObj.getBlockMetadata(i7, i8, i9);
			d3 = (double)i8;
			if(((BlockRail)Block.blocksList[i10]).isPowered()) {
				i11 &= 7;
			}

			if(i11 >= 2 && i11 <= 5) {
				d3 = (double)(i8 + 1);
			}

			int[][] i12 = field_855_j[i11];
			double d13 = 0.0D;
			double d15 = (double)i7 + 0.5D + (double)i12[0][0] * 0.5D;
			double d17 = (double)i8 + 0.5D + (double)i12[0][1] * 0.5D;
			double d19 = (double)i9 + 0.5D + (double)i12[0][2] * 0.5D;
			double d21 = (double)i7 + 0.5D + (double)i12[1][0] * 0.5D;
			double d23 = (double)i8 + 0.5D + (double)i12[1][1] * 0.5D;
			double d25 = (double)i9 + 0.5D + (double)i12[1][2] * 0.5D;
			double d27 = d21 - d15;
			double d29 = (d23 - d17) * 2.0D;
			double d31 = d25 - d19;
			if(d27 == 0.0D) {
				d1 = (double)i7 + 0.5D;
				d13 = d5 - (double)i9;
			} else if(d31 == 0.0D) {
				d5 = (double)i9 + 0.5D;
				d13 = d1 - (double)i7;
			} else {
				double d33 = d1 - d15;
				double d35 = d5 - d19;
				double d37 = (d33 * d27 + d35 * d31) * 2.0D;
				d13 = d37;
			}

			d1 = d15 + d27 * d13;
			d3 = d17 + d29 * d13;
			d5 = d19 + d31 * d13;
			if(d29 < 0.0D) {
				++d3;
			}

			if(d29 > 0.0D) {
				d3 += 0.5D;
			}

			return Vec3D.createVector(d1, d3, d5);
		} else {
			return null;
		}
	}

	protected void writeEntityToNBT(NBTTagCompound compoundTag) {
		compoundTag.setInteger("Type", this.minecartType);
		if(this.minecartType == 2) {
			compoundTag.setDouble("PushX", this.pushX);
			compoundTag.setDouble("PushZ", this.pushZ);
			compoundTag.setShort("Fuel", (short)this.fuel);
		} else if(this.minecartType == 1) {
			NBTTagList nBTTagList2 = new NBTTagList();

			for(int i3 = 0; i3 < this.cargoItems.length; ++i3) {
				if(this.cargoItems[i3] != null) {
					NBTTagCompound nBTTagCompound4 = new NBTTagCompound();
					nBTTagCompound4.setByte("Slot", (byte)i3);
					this.cargoItems[i3].writeToNBT(nBTTagCompound4);
					nBTTagList2.appendTag(nBTTagCompound4);
				}
			}

			compoundTag.setTag("Items", nBTTagList2);
		}

	}

	protected void readEntityFromNBT(NBTTagCompound compoundTag) {
		this.minecartType = compoundTag.getInteger("Type");
		if(this.minecartType == 2) {
			this.pushX = compoundTag.getDouble("PushX");
			this.pushZ = compoundTag.getDouble("PushZ");
			this.fuel = compoundTag.getShort("Fuel");
		} else if(this.minecartType == 1) {
			NBTTagList nBTTagList2 = compoundTag.getTagList("Items");
			this.cargoItems = new ItemStack[this.getSizeInventory()];

			for(int i3 = 0; i3 < nBTTagList2.tagCount(); ++i3) {
				NBTTagCompound nBTTagCompound4 = (NBTTagCompound)nBTTagList2.tagAt(i3);
				int i5 = nBTTagCompound4.getByte("Slot") & 255;
				if(i5 >= 0 && i5 < this.cargoItems.length) {
					this.cargoItems[i5] = ItemStack.loadItemStackFromNBT(nBTTagCompound4);
				}
			}
		}

	}

	public float getShadowSize() {
		return 0.0F;
	}

	public void applyEntityCollision(Entity entity1) {
		if(!this.worldObj.isRemote) {
			if(entity1 != this.riddenByEntity) {
				if(entity1 instanceof EntityLiving && !(entity1 instanceof EntityPlayer) && !(entity1 instanceof EntityIronGolem) && this.minecartType == 0 && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01D && this.riddenByEntity == null && entity1.ridingEntity == null) {
					entity1.mountEntity(this);
				}

				double d2 = entity1.posX - this.posX;
				double d4 = entity1.posZ - this.posZ;
				double d6 = d2 * d2 + d4 * d4;
				if(d6 >= 9.999999747378752E-5D) {
					d6 = (double)MathHelper.sqrt_double(d6);
					d2 /= d6;
					d4 /= d6;
					double d8 = 1.0D / d6;
					if(d8 > 1.0D) {
						d8 = 1.0D;
					}

					d2 *= d8;
					d4 *= d8;
					d2 *= (double)0.1F;
					d4 *= (double)0.1F;
					d2 *= (double)(1.0F - this.entityCollisionReduction);
					d4 *= (double)(1.0F - this.entityCollisionReduction);
					d2 *= 0.5D;
					d4 *= 0.5D;
					if(entity1 instanceof EntityMinecart) {
						double d10 = entity1.posX - this.posX;
						double d12 = entity1.posZ - this.posZ;
						Vec3D vec3D14 = Vec3D.createVector(d10, 0.0D, d12).normalize();
						Vec3D vec3D15 = Vec3D.createVector((double)MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F), 0.0D, (double)MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F)).normalize();
						double d16 = Math.abs(vec3D14.dotProduct(vec3D15));
						if(d16 < (double)0.8F) {
							return;
						}

						double d18 = entity1.motionX + this.motionX;
						double d20 = entity1.motionZ + this.motionZ;
						if(((EntityMinecart)entity1).minecartType == 2 && this.minecartType != 2) {
							this.motionX *= (double)0.2F;
							this.motionZ *= (double)0.2F;
							this.addVelocity(entity1.motionX - d2, 0.0D, entity1.motionZ - d4);
							entity1.motionX *= (double)0.95F;
							entity1.motionZ *= (double)0.95F;
						} else if(((EntityMinecart)entity1).minecartType != 2 && this.minecartType == 2) {
							entity1.motionX *= (double)0.2F;
							entity1.motionZ *= (double)0.2F;
							entity1.addVelocity(this.motionX + d2, 0.0D, this.motionZ + d4);
							this.motionX *= (double)0.95F;
							this.motionZ *= (double)0.95F;
						} else {
							d18 /= 2.0D;
							d20 /= 2.0D;
							this.motionX *= (double)0.2F;
							this.motionZ *= (double)0.2F;
							this.addVelocity(d18 - d2, 0.0D, d20 - d4);
							entity1.motionX *= (double)0.2F;
							entity1.motionZ *= (double)0.2F;
							entity1.addVelocity(d18 + d2, 0.0D, d20 + d4);
						}
					} else {
						this.addVelocity(-d2, 0.0D, -d4);
						entity1.addVelocity(d2 / 4.0D, 0.0D, d4 / 4.0D);
					}
				}

			}
		}
	}

	public int getSizeInventory() {
		return 27;
	}

	public ItemStack getStackInSlot(int i1) {
		return this.cargoItems[i1];
	}

	public ItemStack decrStackSize(int i1, int i2) {
		if(this.cargoItems[i1] != null) {
			ItemStack itemStack3;
			if(this.cargoItems[i1].stackSize <= i2) {
				itemStack3 = this.cargoItems[i1];
				this.cargoItems[i1] = null;
				return itemStack3;
			} else {
				itemStack3 = this.cargoItems[i1].splitStack(i2);
				if(this.cargoItems[i1].stackSize == 0) {
					this.cargoItems[i1] = null;
				}

				return itemStack3;
			}
		} else {
			return null;
		}
	}

	public ItemStack getStackInSlotOnClosing(int i1) {
		if(this.cargoItems[i1] != null) {
			ItemStack itemStack2 = this.cargoItems[i1];
			this.cargoItems[i1] = null;
			return itemStack2;
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int i1, ItemStack itemStack2) {
		this.cargoItems[i1] = itemStack2;
		if(itemStack2 != null && itemStack2.stackSize > this.getInventoryStackLimit()) {
			itemStack2.stackSize = this.getInventoryStackLimit();
		}

	}

	public String getInvName() {
		return "container.minecart";
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public void onInventoryChanged() {
	}

	public boolean interact(EntityPlayer entityPlayer1) {
		if(this.minecartType == 0) {
			if(this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != entityPlayer1) {
				return true;
			}

			if(!this.worldObj.isRemote) {
				entityPlayer1.mountEntity(this);
			}
		} else if(this.minecartType == 1) {
			if(!this.worldObj.isRemote) {
				entityPlayer1.displayGUIChest(this);
			}
		} else if(this.minecartType == 2) {
			ItemStack itemStack2 = entityPlayer1.inventory.getCurrentItem();
			if(itemStack2 != null && itemStack2.itemID == Item.coal.shiftedIndex) {
				if(--itemStack2.stackSize == 0) {
					entityPlayer1.inventory.setInventorySlotContents(entityPlayer1.inventory.currentItem, (ItemStack)null);
				}

				this.fuel += 3600;
			}

			this.pushX = this.posX - entityPlayer1.posX;
			this.pushZ = this.posZ - entityPlayer1.posZ;
		}

		return true;
	}

	public void setPositionAndRotation2(double d1, double d3, double d5, float f7, float f8, int i9) {
		this.minecartX = d1;
		this.minecartY = d3;
		this.minecartZ = d5;
		this.minecartYaw = (double)f7;
		this.minecartPitch = (double)f8;
		this.turnProgress = i9 + 2;
		this.motionX = this.velocityX;
		this.motionY = this.velocityY;
		this.motionZ = this.velocityZ;
	}

	public void setVelocity(double d1, double d3, double d5) {
		this.velocityX = this.motionX = d1;
		this.velocityY = this.motionY = d3;
		this.velocityZ = this.motionZ = d5;
	}

	public boolean isUseableByPlayer(EntityPlayer entityPlayer1) {
		return this.isDead ? false : entityPlayer1.getDistanceSqToEntity(this) <= 64.0D;
	}

	protected boolean isMinecartPowered() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
	}

	protected void setMinecartPowered(boolean var1) {
		if(var1) {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)(this.dataWatcher.getWatchableObjectByte(16) | 1)));
		} else {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte)(this.dataWatcher.getWatchableObjectByte(16) & -2)));
		}

	}

	public void openChest() {
	}

	public void closeChest() {
	}

	public void func_41024_b(int i1) {
		this.dataWatcher.updateObject(19, i1);
	}

	public int func_41025_i() {
		return this.dataWatcher.getWatchableObjectInt(19);
	}

	public void func_41028_c(int i1) {
		this.dataWatcher.updateObject(17, i1);
	}

	public int func_41023_l() {
		return this.dataWatcher.getWatchableObjectInt(17);
	}

	public void func_41029_h(int i1) {
		this.dataWatcher.updateObject(18, i1);
	}

	public int func_41030_m() {
		return this.dataWatcher.getWatchableObjectInt(18);
	}
}
