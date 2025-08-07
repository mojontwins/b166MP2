package net.minecraft.world.entity.item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockPistonBase;
import net.minecraft.world.level.tile.BlockRail;
import net.minecraft.world.level.tile.BlockStairs;
import net.minecraft.world.level.tile.BlockStep;
import net.minecraft.world.level.tile.entity.TileEntity;
import net.minecraft.world.phys.AxisAlignedBB;

public class EntityMovingPiston extends Entity {
	public int xmove;
	public int ymove;
	public int zmove;
	public int xstart;
	public int ystart;
	public int zstart;
	public int data;
	private boolean extending;
	private int timeout;
	public int blockid;
	private TileEntity tileEntity;

	public int getXRender() {
		return this.extending ? this.xstart + this.xmove : this.xstart;
	}

	public int getYRender() {
		return this.extending ? this.ystart + this.ymove : this.ystart;
	}

	public int getZRender() {
		return this.extending ? this.zstart + this.zmove : this.zstart;
	}

	private static boolean isNormalBlock(Block block0) {
		return block0.blockID != Block.classicPiston.blockID && block0.blockID != Block.classicStickyPiston.blockID && block0.blockID != Block.classicPistonBase.blockID && block0.blockID != Block.classicStickyPistonBase.blockID ? block0.renderAsNormalBlock() : true;
	}

	public EntityMovingPiston(World world1) {
		super(world1);
		this.timeout = 0;
	}

	public EntityMovingPiston(World world1, int i2, int i3, int i4, boolean z5) {
		this(world1, i2, i3, i4, 0);
		if(z5) {
			this.blockid = Block.classicStickyPiston.blockID;
		} else {
			this.blockid = Block.classicPiston.blockID;
		}

	}

	private EntityMovingPiston(World world1, int i2, int i3, int i4, int i5) {
		super(world1);
		this.timeout = 0;
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.98F);
		this.yOffset = this.height / 2.0F;
		this.setPosition((double)((float)i2 + 0.5F), (double)((float)i3 + 0.5F), (double)((float)i4 + 0.5F));
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = (double)((float)i2 + 0.5F);
		this.prevPosY = (double)((float)i3 + 0.5F);
		this.prevPosZ = (double)((float)i4 + 0.5F);
		this.xstart = i2;
		this.ystart = i3;
		this.zstart = i4;
		this.blockid = i5;
	}

	private void start() {
		if(this.blockid != Block.classicPiston.blockID && this.blockid != Block.classicStickyPiston.blockID) {
			this.data = this.worldObj.getBlockMetadata(this.xstart, this.ystart, this.zstart);
			this.worldObj.setBlockWithNotify(this.xstart, this.ystart, this.zstart, 0);
		}

		this.worldObj.spawnEntityInWorld(this);
	}

	private static TileEntity getTileEntity(World world0, int i1, int i2, int i3) {
		TileEntity tileEntity4 = world0.getBlockTileEntity(i1, i2, i3);

		try {
			if(tileEntity4 != null && tileEntity4 instanceof IInventory) {
				IInventory iInventory5 = (IInventory)tileEntity4;
				IInventory iInventory6 = (IInventory)tileEntity4.getClass().newInstance();

				for(int i7 = 0; i7 < iInventory5.getSizeInventory(); ++i7) {
					iInventory6.setInventorySlotContents(i7, iInventory5.getStackInSlot(i7));
					iInventory5.setInventorySlotContents(i7, (ItemStack)null);
				}

				return (TileEntity)iInventory6;
			}
		} catch (InstantiationException instantiationException8) {
		} catch (IllegalAccessException illegalAccessException9) {
		}

		return tileEntity4;
	}

	private static boolean tryBreak(World world0, int i1, int i2, int i3, Block block4) {
		if(!isNormalBlock(block4) && block4.blockID != Block.fence.blockID && !(block4 instanceof BlockStep) && block4.blockID != Block.cake.blockID && !(block4 instanceof BlockStairs) && !(block4 instanceof BlockRail)) {
			if(block4.blockID != Block.bed.blockID) {
				block4.dropBlockAsItem(world0, i1, i2, i3, 0, 0);
				world0.setBlockWithNotify(i1, i2, i3, 0);
			}

			return true;
		} else {
			return false;
		}
	}

	private static double getEntityMovement(double d0, double d2) {
		return d2 + d0;
	}

	public static boolean buildPistons(EntityMovingPiston movingPiston0, int i1, int i2, int i3, int i4) {
		ArrayList<EntityMovingPiston> arrayList5 = new ArrayList<EntityMovingPiston>();
		movingPiston0.data = i4;
		arrayList5.add(movingPiston0);
		int i6 = movingPiston0.xstart;
		int i7 = movingPiston0.ystart;
		int i8 = movingPiston0.zstart;
		int i9 = i1 - movingPiston0.xstart;
		int i10 = i2 - movingPiston0.ystart;
		int i11 = i3 - movingPiston0.zstart;
		int i13 = 0;

		while(true) {
			if(i13 < 17) {
				i6 += i9;
				i7 += i10;
				i8 += i11;
				int i12 = movingPiston0.worldObj.getBlockId(i6, i7, i8);
				if(i12 != 0 && !tryBreak(movingPiston0.worldObj, i6, i7, i8, Block.blocksList[i12])) {
					if(i12 != Block.bedrock.blockID && i12 != Block.obsidian.blockID && i12 != Block.classicPiston.blockID && i12 != Block.classicStickyPiston.blockID && i13 < 16) {
						if(i12 == Block.classicPistonBase.blockID || i12 == Block.classicStickyPistonBase.blockID) {
							int i23 = movingPiston0.worldObj.getBlockMetadata(i6, i7, i8);
							if((i23 & 8) != 0) {
								return false;
							}
						}

						EntityMovingPiston movingPiston24 = new EntityMovingPiston(movingPiston0.worldObj, i6, i7, i8, i12);
						movingPiston24.tileEntity = getTileEntity(movingPiston0.worldObj, i6, i7, i8);
						arrayList5.add(movingPiston24);
						++i13;
						continue;
					}

					return false;
				}
			}

			EntityMovingPiston movingPiston22 = (EntityMovingPiston)arrayList5.get(arrayList5.size() - 1);
			List<Entity> list14 = movingPiston22.worldObj.getEntitiesWithinAABBExcludingEntity(movingPiston22, AxisAlignedBB.getBoundingBoxFromPool((double)i6, (double)i7, (double)i8, (double)(i6 + 1), (double)(i7 + 1), (double)(i8 + 1)));
			double d15 = 1.1999999731779099D;
			if(movingPiston0.blockid == Block.classicStickyPiston.blockID) {
				d15 = 0.7199999839067459D;
			}

			Iterator<Entity> iterator17;
			Entity entity18;
			for(iterator17 = list14.iterator(); iterator17.hasNext(); entity18.motionZ = getEntityMovement((double)i11 * d15, entity18.motionZ)) {
				entity18 = (Entity)iterator17.next();
				entity18.motionX = getEntityMovement((double)i9 * d15, entity18.motionX);
				entity18.motionY = getEntityMovement((double)i10 * d15, entity18.motionY);
			}

			list14 = movingPiston22.worldObj.getEntitiesWithinAABBExcludingEntity(movingPiston22, AxisAlignedBB.getBoundingBoxFromPool((double)i6, (double)(i7 - 1), (double)i8, (double)(i6 + 1), (double)(i7 + 1), (double)(i8 + 1)));
			iterator17 = list14.iterator();

			while(iterator17.hasNext()) {
				entity18 = (Entity)iterator17.next();
				if(entity18 instanceof EntityMinecart) {
					entity18.motionX = getEntityMovement((double)i9 * d15, entity18.motionX);
					entity18.motionY = getEntityMovement((double)i10 * d15, entity18.motionY);
					entity18.motionZ = getEntityMovement((double)i11 * d15, entity18.motionZ);
				}
			}

			if(i10 > 0 && i9 == 0 && i11 == 0 && movingPiston0.blockid != Block.classicStickyPiston.blockID) {
				double d25 = (double)i10 * (double)0.24F * 5.0D;

				while(arrayList5.size() > 0) {
					int i19 = arrayList5.size() - 1;
					EntityMovingPiston movingPiston20 = (EntityMovingPiston)arrayList5.get(i19);
					if(movingPiston20.blockid != Block.sand.blockID && movingPiston20.blockid != Block.gravel.blockID) {
						break;
					}

					movingPiston20.worldObj.setBlockWithNotify(movingPiston20.xstart, movingPiston20.ystart, movingPiston20.zstart, 0);
					EntityFallingSand entityFallingSand21 = new EntityFallingSand(movingPiston20.worldObj, (double)((float)movingPiston20.xstart + 0.5F), (double)((float)movingPiston20.ystart + 0.5F), (double)((float)movingPiston20.zstart + 0.5F), movingPiston20.blockid);
					entityFallingSand21.motionY += d25;
					movingPiston20.worldObj.spawnEntityInWorld(entityFallingSand21);
					arrayList5.remove(i19);
					d25 -= 0.2D;
					if(d25 < 0.4D) {
						break;
					}
				}
			}

			Iterator<EntityMovingPiston> iterator18 = arrayList5.iterator();

			while(iterator18.hasNext()) {
				EntityMovingPiston movingPiston26 = (EntityMovingPiston)iterator18.next();
				movingPiston26.extending = true;
				movingPiston26.xmove = i9;
				movingPiston26.ymove = i10;
				movingPiston26.zmove = i11;
				movingPiston26.start();
			}

			return true;
		}
	}

	public static void buildRetractingPistons(World world0, int i1, int i2, int i3, int i4, int i5, int i6, int i7, boolean z8) {
		int i9 = i4 - i1;
		int i10 = i5 - i2;
		int i11 = i6 - i3;
		EntityMovingPiston movingPiston12 = new EntityMovingPiston(world0, i1, i2, i3, z8);
		movingPiston12.xmove = i9;
		movingPiston12.ymove = i10;
		movingPiston12.zmove = i11;
		movingPiston12.extending = false;
		movingPiston12.data = i7;
		world0.spawnEntityInWorld(movingPiston12);

		while(z8) {
			i1 -= i9;
			i2 -= i10;
			i3 -= i11;
			z8 = false;
			int i13 = world0.getBlockId(i1, i2, i3);
			if(i13 == 0) {
				return;
			}

			Block block14 = Block.blocksList[i13];
			if(!isNormalBlock(block14) && i13 != Block.fence.blockID && !(block14 instanceof BlockStep) && i13 != Block.cake.blockID && !(block14 instanceof BlockStairs) && !(block14 instanceof BlockRail)) {
				return;
			}

			if(i13 == Block.bedrock.blockID || i13 == Block.obsidian.blockID || i13 == Block.classicPiston.blockID || i13 == Block.classicStickyPiston.blockID) {
				return;
			}

			int i15 = world0.getBlockMetadata(i1, i2, i3);
			if(i13 == Block.classicPistonBase.blockID || i13 == Block.classicStickyPistonBase.blockID) {
				if((i15 & 8) != 0) {
					return;
				}

				if(i13 == Block.classicStickyPistonBase.blockID && i15 == (i7 & 7)) {
					z8 = true;
				}
			}

			movingPiston12 = new EntityMovingPiston(world0, i1, i2, i3, i13);
			movingPiston12.xmove = i9;
			movingPiston12.ymove = i10;
			movingPiston12.zmove = i11;
			movingPiston12.extending = false;
			movingPiston12.data = i15;
			movingPiston12.tileEntity = getTileEntity(world0, i1, i2, i3);
			world0.spawnEntityInWorld(movingPiston12);
			world0.setBlockWithNotify(i1, i2, i3, 0);
		}

	}

	protected boolean canTriggerWalking() {
		return true;
	}

	protected void entityInit() {
	}

	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}

	public void onUpdate() {
		if(this.blockid == 0) {
			this.setDead();
		} else {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;
			++this.timeout;
			this.boundingBox.offset((double)this.xmove * (double)0.24F, (double)this.ymove * (double)0.24F, (double)this.zmove * (double)0.24F);
			this.posX += (double)this.xmove * (double)0.24F;
			this.posY += (double)this.ymove * (double)0.24F;
			this.posZ += (double)this.zmove * (double)0.24F;
			int i1 = MathHelper.floor_double(this.posX + (double)(this.xmove >= 0 ? -0.5F : 0.5F));
			int i2 = MathHelper.floor_double(this.posY + (double)(this.ymove >= 0 ? -0.5F : 0.5F));
			int i3 = MathHelper.floor_double(this.posZ + (double)(this.zmove >= 0 ? -0.5F : 0.5F));
			if(i1 == this.xstart + this.xmove && i2 == this.ystart + this.ymove && i3 == this.zstart + this.zmove) {
				boolean z4 = this.blockid == Block.classicPiston.blockID || this.blockid == Block.classicStickyPiston.blockID;
				if(!this.extending && z4) {
					int i8 = this.worldObj.getBlockMetadata(i1, i2, i3);
					this.worldObj.setBlockMetadataWithNotify(i1, i2, i3, i8 & 7);
					this.worldObj.markBlockAsNeedsUpdate(i1, i2, i3);
				} else {
					this.end(i1, i2, i3, !z4);
					if(this.blockid == Block.classicPistonBase.blockID || this.blockid == Block.classicStickyPistonBase.blockID) {
						((BlockPistonBase)Block.blocksList[this.blockid]).onPistonPushed(this.worldObj, i1, i2, i3);
					}

					List<Entity> list5 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBoxFromPool((double)i1, (double)(i2 + 1), (double)i3, (double)(i1 + 1), (double)i2 + 1.1D, (double)(i3 + 1)));

					Entity entity7;
					for(Iterator<Entity> iterator6 = list5.iterator(); iterator6.hasNext(); entity7.motionY += 0.05D) {
						entity7 = (Entity)iterator6.next();
					}
				}

				this.setDead();
			} else if(this.timeout > 100 && !this.worldObj.isRemote) {
				this.setDead();
			}

		}
	}

	private void end(int i1, int i2, int i3, boolean z4) {
		int i5 = this.worldObj.getBlockId(i1, i2, i3);
		boolean z6 = Block.blocksList[i5] == null ? false : isNormalBlock(Block.blocksList[i5]);
		if(z6) {
			if(z4) {
				Block.blocksList[this.blockid].dropBlockAsItem(this.worldObj, i1, i2, i3, 0, 0);
				this.worldObj.setBlockWithNotify(i1, i2, i3, 0);
			} else {
				boolean z7 = this.blockid == Block.classicStickyPiston.blockID;
				buildRetractingPistons(this.worldObj, i1, i2, i3, this.xstart, this.ystart, this.zstart, this.worldObj.getBlockMetadata(i1, i2, i3), z7);
			}

		} else {
			this.worldObj.setBlockWithNotify(i1, i2, i3, this.blockid);
			if(this.data != 0) {
				this.worldObj.setBlockMetadataWithNotify(i1, i2, i3, this.data);
			}

			if(this.tileEntity != null) {
				this.worldObj.setBlockTileEntity(i1, i2, i3, this.tileEntity);
			}

		}
	}

	protected void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		nBTTagCompound1.setByte("xmove", (byte)this.xmove);
		nBTTagCompound1.setByte("ymove", (byte)this.ymove);
		nBTTagCompound1.setByte("zmove", (byte)this.zmove);
		nBTTagCompound1.setInteger("xstart", this.xstart);
		nBTTagCompound1.setInteger("ystart", this.ystart);
		nBTTagCompound1.setInteger("zstart", this.zstart);
		nBTTagCompound1.setByte("Block", (byte)this.blockid);
		nBTTagCompound1.setByte("Data", (byte)this.data);
		nBTTagCompound1.setBoolean("Extending", this.extending);
	}

	protected void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		this.xmove = nBTTagCompound1.getByte("xmove") & 255;
		this.ymove = nBTTagCompound1.getByte("ymove") & 255;
		this.zmove = nBTTagCompound1.getByte("zmove") & 255;
		this.xstart = nBTTagCompound1.getInteger("xstart");
		this.ystart = nBTTagCompound1.getInteger("ystart");
		this.zstart = nBTTagCompound1.getInteger("zstart");
		this.blockid = nBTTagCompound1.getByte("Block") & 255;
		this.data = nBTTagCompound1.getByte("Data") & 255;
		this.extending = nBTTagCompound1.getBoolean("Extending");
		this.timeout = 0;
	}

	public float getShadowSize() {
		return 0.0F;
	}

	public World k() {
		return this.worldObj;
	}
}
