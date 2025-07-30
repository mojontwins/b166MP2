package net.minecraft.world.entity;

import java.util.ArrayList;
import java.util.List;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;

public class EntityPainting extends Entity {
	private int tickCounter1;
	public int direction;
	public int xPosition;
	public int yPosition;
	public int zPosition;
	public EnumArt art;

	public EntityPainting(World world1) {
		super(world1);
		this.tickCounter1 = 0;
		this.direction = 0;
		this.yOffset = 0.0F;
		this.setSize(0.5F, 0.5F);
	}

	public EntityPainting(World world1, int i2, int i3, int i4, int i5) {
		this(world1);
		this.xPosition = i2;
		this.yPosition = i3;
		this.zPosition = i4;
		ArrayList<EnumArt> arrayList6 = new ArrayList<EnumArt>();
		EnumArt[] enumArt7 = EnumArt.values();
		int i8 = enumArt7.length;

		for(int i9 = 0; i9 < i8; ++i9) {
			EnumArt enumArt10 = enumArt7[i9];
			this.art = enumArt10;
			this.func_412_b(i5);
			if(this.onValidSurface()) {
				arrayList6.add(enumArt10);
			}
		}

		if(arrayList6.size() > 0) {
			this.art = (EnumArt)arrayList6.get(this.rand.nextInt(arrayList6.size()));
		}

		this.func_412_b(i5);
	}

	public EntityPainting(World world1, int i2, int i3, int i4, int i5, String string6) {
		this(world1);
		this.xPosition = i2;
		this.yPosition = i3;
		this.zPosition = i4;
		EnumArt[] enumArt7 = EnumArt.values();
		int i8 = enumArt7.length;

		for(int i9 = 0; i9 < i8; ++i9) {
			EnumArt enumArt10 = enumArt7[i9];
			if(enumArt10.title.equals(string6)) {
				this.art = enumArt10;
				break;
			}
		}

		this.func_412_b(i5);
	}

	protected void entityInit() {
	}

	public void func_412_b(int i1) {
		this.direction = i1;
		this.prevRotationYaw = this.rotationYaw = (float)(i1 * 90);
		float f2 = (float)this.art.sizeX;
		float f3 = (float)this.art.sizeY;
		float f4 = (float)this.art.sizeX;
		if(i1 != 0 && i1 != 2) {
			f2 = 0.5F;
		} else {
			f4 = 0.5F;
		}

		f2 /= 32.0F;
		f3 /= 32.0F;
		f4 /= 32.0F;
		float f5 = (float)this.xPosition + 0.5F;
		float f6 = (float)this.yPosition + 0.5F;
		float f7 = (float)this.zPosition + 0.5F;
		float f8 = 0.5625F;
		if(i1 == 0) {
			f7 -= f8;
		}

		if(i1 == 1) {
			f5 -= f8;
		}

		if(i1 == 2) {
			f7 += f8;
		}

		if(i1 == 3) {
			f5 += f8;
		}

		if(i1 == 0) {
			f5 -= this.func_411_c(this.art.sizeX);
		}

		if(i1 == 1) {
			f7 += this.func_411_c(this.art.sizeX);
		}

		if(i1 == 2) {
			f5 += this.func_411_c(this.art.sizeX);
		}

		if(i1 == 3) {
			f7 -= this.func_411_c(this.art.sizeX);
		}

		f6 += this.func_411_c(this.art.sizeY);
		this.setPosition((double)f5, (double)f6, (double)f7);
		float f9 = -0.00625F;
		this.boundingBox.setBounds((double)(f5 - f2 - f9), (double)(f6 - f3 - f9), (double)(f7 - f4 - f9), (double)(f5 + f2 + f9), (double)(f6 + f3 + f9), (double)(f7 + f4 + f9));
	}

	private float func_411_c(int i1) {
		return i1 == 32 ? 0.5F : (i1 == 64 ? 0.5F : 0.0F);
	}

	public void onUpdate() {
		if(this.tickCounter1++ == 100 && !this.worldObj.isRemote) {
			this.tickCounter1 = 0;
			if(!this.isDead && !this.onValidSurface()) {
				this.setDead();
				this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Item.painting)));
			}
		}

	}

	public boolean onValidSurface() {
		if(this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).size() > 0) {
			return false;
		} else {
			int i1 = this.art.sizeX / 16;
			int i2 = this.art.sizeY / 16;
			int i3 = this.xPosition;
			int i4 = this.yPosition;
			int i5 = this.zPosition;
			if(this.direction == 0) {
				i3 = MathHelper.floor_double(this.posX - (double)((float)this.art.sizeX / 32.0F));
			}

			if(this.direction == 1) {
				i5 = MathHelper.floor_double(this.posZ - (double)((float)this.art.sizeX / 32.0F));
			}

			if(this.direction == 2) {
				i3 = MathHelper.floor_double(this.posX - (double)((float)this.art.sizeX / 32.0F));
			}

			if(this.direction == 3) {
				i5 = MathHelper.floor_double(this.posZ - (double)((float)this.art.sizeX / 32.0F));
			}

			i4 = MathHelper.floor_double(this.posY - (double)((float)this.art.sizeY / 32.0F));

			int i7;
			for(int i6 = 0; i6 < i1; ++i6) {
				for(i7 = 0; i7 < i2; ++i7) {
					Material material8;
					if(this.direction != 0 && this.direction != 2) {
						material8 = this.worldObj.getBlockMaterial(this.xPosition, i4 + i7, i5 + i6);
					} else {
						material8 = this.worldObj.getBlockMaterial(i3 + i6, i4 + i7, this.zPosition);
					}

					if(!material8.isSolid()) {
						return false;
					}
				}
			}

			List<Entity> list9 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox);

			for(i7 = 0; i7 < list9.size(); ++i7) {
				if(list9.get(i7) instanceof EntityPainting) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean canBeCollidedWith() {
		return true;
	}

	public boolean attackEntityFrom(DamageSource damageSource1, int i2) {
		if(!this.isDead && !this.worldObj.isRemote) {
			this.setDead();
			this.setBeenAttacked();
			this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Item.painting)));
		}

		return true;
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		compoundTag.setByte("Dir", (byte)this.direction);
		compoundTag.setString("Motive", this.art.title);
		compoundTag.setInteger("TileX", this.xPosition);
		compoundTag.setInteger("TileY", this.yPosition);
		compoundTag.setInteger("TileZ", this.zPosition);
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		this.direction = compoundTag.getByte("Dir");
		this.xPosition = compoundTag.getInteger("TileX");
		this.yPosition = compoundTag.getInteger("TileY");
		this.zPosition = compoundTag.getInteger("TileZ");
		String string2 = compoundTag.getString("Motive");
		EnumArt[] enumArt3 = EnumArt.values();
		int i4 = enumArt3.length;

		for(int i5 = 0; i5 < i4; ++i5) {
			EnumArt enumArt6 = enumArt3[i5];
			if(enumArt6.title.equals(string2)) {
				this.art = enumArt6;
			}
		}

		if(this.art == null) {
			this.art = EnumArt.Kebab;
		}

		this.func_412_b(this.direction);
	}

	public void moveEntity(double d1, double d3, double d5) {
		if(!this.worldObj.isRemote && !this.isDead && d1 * d1 + d3 * d3 + d5 * d5 > 0.0D) {
			this.setDead();
			this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Item.painting)));
		}

	}

	public void addVelocity(double d1, double d3, double d5) {
		if(!this.worldObj.isRemote && !this.isDead && d1 * d1 + d3 * d3 + d5 * d5 > 0.0D) {
			this.setDead();
			this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(Item.painting)));
		}

	}
}
