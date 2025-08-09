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
	public int blockID;
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
		return 
				block0.blockID != Block.classicPiston.blockID && 
				block0.blockID != Block.classicStickyPiston.blockID && 
				block0.blockID != Block.classicPistonBase.blockID && 
				block0.blockID != Block.classicStickyPistonBase.blockID ? 
						block0.renderAsNormalBlock() 
					: 
						true;
	}

	public EntityMovingPiston(World world) {
		super(world);
		this.timeout = 0;
	}

	public EntityMovingPiston(World world, int x, int y, int z, boolean sticky) {
		this(world, x, y, z, 0);
		if(sticky) {
			this.blockID = Block.classicStickyPiston.blockID;
		} else {
			this.blockID = Block.classicPiston.blockID;
		}

	}

	private EntityMovingPiston(World world, int x, int y, int z, int blockID) {
		super(world);
		this.timeout = 0;
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.98F);
		this.yOffset = this.height / 2.0F;
		this.setPosition((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F));
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = (double)((float)x + 0.5F);
		this.prevPosY = (double)((float)y + 0.5F);
		this.prevPosZ = (double)((float)z + 0.5F);
		this.xstart = x;
		this.ystart = y;
		this.zstart = z;
		this.blockID = blockID;
	}

	private void start() {
		if(this.blockID != Block.classicPiston.blockID && this.blockID != Block.classicStickyPiston.blockID) {
			this.data = this.worldObj.getBlockMetadata(this.xstart, this.ystart, this.zstart);
			if(!this.worldObj.isRemote)	this.worldObj.setBlockWithNotify(this.xstart, this.ystart, this.zstart, 0);
		}

		this.worldObj.spawnEntityInWorld(this);
	}

	private static TileEntity getTileEntity(World world, int x, int y, int z) {
		TileEntity te = world.getBlockTileEntity(x, y, z);

		try {
			if(te != null && te instanceof IInventory) {
				IInventory inventorySrc = (IInventory)te;
				IInventory inventoryDst = (IInventory)te.getClass().newInstance();

				for(int i = 0; i < inventorySrc.getSizeInventory(); ++i) {
					inventoryDst.setInventorySlotContents(i, inventorySrc.getStackInSlot(i));
					inventorySrc.setInventorySlotContents(i, (ItemStack)null);
				}

				return (TileEntity)inventoryDst;
			}
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}

		return te;
	}

	private static boolean tryBreak(World world, int x, int y, int z, Block block) {
		if(!isNormalBlock(block) && block.blockID != Block.fence.blockID && !(block instanceof BlockStep) && block.blockID != Block.cake.blockID && !(block instanceof BlockStairs) && !(block instanceof BlockRail)) {
			if(block.blockID != Block.bed.blockID) {
				if(!world.isRemote)	{
					block.dropBlockAsItem(world, x, y, z, 0, 0);
					world.setBlockWithNotify(x, y, z, 0);
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private static double getEntityMovement(double dx, double dz) {
		return dx + dz;
	}

	public static boolean buildPistons(EntityMovingPiston thePiston, int x, int y, int z, int data) {
		ArrayList<EntityMovingPiston> pistonsList = new ArrayList<EntityMovingPiston>();
		thePiston.data = data;
		pistonsList.add(thePiston);
		int x0 = thePiston.xstart;
		int y0 = thePiston.ystart;
		int z0 = thePiston.zstart;
		int dx = x - thePiston.xstart;
		int dy = y - thePiston.ystart;
		int dz = z - thePiston.zstart;
		int progress = 0;

		while(true) {
			if(progress < 17) {
				x0 += dx;
				y0 += dy;
				z0 += dz;
				int blockID = thePiston.worldObj.getBlockId(x0, y0, z0);
				if(blockID != 0 && !tryBreak(thePiston.worldObj, x0, y0, z0, Block.blocksList[blockID])) {
					if(blockID != Block.bedrock.blockID && blockID != Block.obsidian.blockID && blockID != Block.classicPiston.blockID && blockID != Block.classicStickyPiston.blockID && progress < 16) {
						if(blockID == Block.classicPistonBase.blockID || blockID == Block.classicStickyPistonBase.blockID) {
							int meta = thePiston.worldObj.getBlockMetadata(x0, y0, z0);
							if((meta & 8) != 0) {
								return false;
							}
						}

						EntityMovingPiston newPiston = new EntityMovingPiston(thePiston.worldObj, x0, y0, z0, blockID);
						newPiston.tileEntity = getTileEntity(thePiston.worldObj, x0, y0, z0);
						pistonsList.add(newPiston);
						++progress;
						continue;
					}

					return false;
				}
			}

			EntityMovingPiston lastPiston = (EntityMovingPiston)pistonsList.get(pistonsList.size() - 1);
			List<Entity> collidingEntities = lastPiston.worldObj.getEntitiesWithinAABBExcludingEntity(lastPiston, AxisAlignedBB.getBoundingBoxFromPool((double)x0, (double)y0, (double)z0, (double)(x0 + 1), (double)(y0 + 1), (double)(z0 + 1)));

			double vMod = 1.1999999731779099D;
			if(thePiston.blockID == Block.classicStickyPiston.blockID) {
				vMod = 0.7199999839067459D;
			}

			Iterator<Entity> itEntities;
			Entity theEntity;

			itEntities = collidingEntities.iterator(); while(itEntities.hasNext()) {
				theEntity = (Entity)itEntities.next();
				theEntity.motionX = getEntityMovement((double)dx * vMod, theEntity.motionX);
				theEntity.motionY = getEntityMovement((double)dy * vMod, theEntity.motionY);
				theEntity.motionZ = getEntityMovement((double)dz * vMod, theEntity.motionZ);
			}

			collidingEntities = lastPiston.worldObj.getEntitiesWithinAABBExcludingEntity(lastPiston, AxisAlignedBB.getBoundingBoxFromPool((double)x0, (double)(y0 - 1), (double)z0, (double)(x0 + 1), (double)(y0 + 1), (double)(z0 + 1)));

			itEntities = collidingEntities.iterator(); while(itEntities.hasNext()) {
				theEntity = (Entity)itEntities.next();
				if(theEntity instanceof EntityMinecart) {
					theEntity.motionX = getEntityMovement((double)dx * vMod, theEntity.motionX);
					theEntity.motionY = getEntityMovement((double)dy * vMod, theEntity.motionY);
					theEntity.motionZ = getEntityMovement((double)dz * vMod, theEntity.motionZ);
				}
			}

			if(dy > 0 && dx == 0 && dz == 0 && thePiston.blockID != Block.classicStickyPiston.blockID) {
				double pistonThustY = (double)dy * (double)0.24F * 5.0D;

				while(pistonsList.size() > 0) {
					int numPistons = pistonsList.size() - 1;
					EntityMovingPiston aPiston = (EntityMovingPiston)pistonsList.get(numPistons);
					if(aPiston.blockID != Block.sand.blockID && aPiston.blockID != Block.gravel.blockID) {
						break;
					}

					if(!aPiston.worldObj.isRemote) {
						aPiston.worldObj.setBlockWithNotify(aPiston.xstart, aPiston.ystart, aPiston.zstart, 0);
						EntityFallingSand fallingSand = new EntityFallingSand(aPiston.worldObj, (double)((float)aPiston.xstart + 0.5F), (double)((float)aPiston.ystart + 0.5F), (double)((float)aPiston.zstart + 0.5F), aPiston.blockID);
						fallingSand.motionY += pistonThustY;
						aPiston.worldObj.spawnEntityInWorld(fallingSand);
					}
					pistonsList.remove(numPistons);
					pistonThustY -= 0.2D;
					if(pistonThustY < 0.4D) {
						break;
					}
				}
			}

			Iterator<EntityMovingPiston> itPistons = pistonsList.iterator();

			while(itPistons.hasNext()) {
				EntityMovingPiston curPiston = (EntityMovingPiston)itPistons.next();
				curPiston.extending = true;
				curPiston.xmove = dx;
				curPiston.ymove = dy;
				curPiston.zmove = dz;
				curPiston.start();
			}

			return true;
		}
	}

	public static void buildRetractingPistons(World world, int xH, int yH, int zH, int x, int y, int z, int data, boolean sticky) {
		int dx = x - xH;
		int dy = y - yH;
		int dz = z - zH;
		EntityMovingPiston piston = new EntityMovingPiston(world, xH, yH, zH, sticky);
		piston.xmove = dx;
		piston.ymove = dy;
		piston.zmove = dz;
		piston.extending = false;
		piston.data = data;
		world.spawnEntityInWorld(piston);

		while(sticky) {
			xH -= dx;
			yH -= dy;
			zH -= dz;
			sticky = false;
			int blockHeadID = world.getBlockId(xH, yH, zH);
			if(blockHeadID == 0) {
				return;
			}

			Block blockHead = Block.blocksList[blockHeadID];
			if(!isNormalBlock(blockHead) && blockHeadID != Block.fence.blockID && !(blockHead instanceof BlockStep) && blockHeadID != Block.cake.blockID && !(blockHead instanceof BlockStairs) && !(blockHead instanceof BlockRail)) {
				return;
			}

			if(blockHeadID == Block.bedrock.blockID || blockHeadID == Block.obsidian.blockID || blockHeadID == Block.classicPiston.blockID || blockHeadID == Block.classicStickyPiston.blockID) {
				return;
			}

			int metaHead = world.getBlockMetadata(xH, yH, zH);
			if(blockHeadID == Block.classicPistonBase.blockID || blockHeadID == Block.classicStickyPistonBase.blockID) {
				if((metaHead & 8) != 0) {
					return;
				}

				if(blockHeadID == Block.classicStickyPistonBase.blockID && metaHead == (data & 7)) {
					sticky = true;
				}
			}

			piston = new EntityMovingPiston(world, xH, yH, zH, blockHeadID);
			piston.xmove = dx;
			piston.ymove = dy;
			piston.zmove = dz;
			piston.extending = false;
			piston.data = metaHead;
			piston.tileEntity = getTileEntity(world, xH, yH, zH);
			world.spawnEntityInWorld(piston);
			if(!world.isRemote) world.setBlockWithNotify(xH, yH, zH, 0);
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
		if(this.blockID == 0) {
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
			int x = MathHelper.floor_double(this.posX + (double)(this.xmove >= 0 ? -0.5F : 0.5F));
			int y = MathHelper.floor_double(this.posY + (double)(this.ymove >= 0 ? -0.5F : 0.5F));
			int z = MathHelper.floor_double(this.posZ + (double)(this.zmove >= 0 ? -0.5F : 0.5F));
			
			if(x == this.xstart + this.xmove && y == this.ystart + this.ymove && z == this.zstart + this.zmove) {
				boolean isPiston = this.blockID == Block.classicPiston.blockID || this.blockID == Block.classicStickyPiston.blockID;
				if(!this.extending && isPiston) {
					if(!this.worldObj.isRemote)	{
						int meta = this.worldObj.getBlockMetadata(x, y, z);
						this.worldObj.setBlockMetadataWithNotify(x, y, z, meta & 7);
						this.worldObj.markBlockAsNeedsUpdate(x, y, z);
					}
				} else {
					this.end(x, y, z, !isPiston);
					if(this.blockID == Block.classicPistonBase.blockID || this.blockID == Block.classicStickyPistonBase.blockID) {
						((BlockPistonBase)Block.blocksList[this.blockID]).onPistonPushed(this.worldObj, x, y, z);
					}

					List<Entity> entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBoxFromPool((double)x, (double)(y + 1), (double)z, (double)(x + 1), (double)y + 1.1D, (double)(z + 1)));

					Entity entity;
					for(Iterator<Entity> itEntities = entities.iterator(); itEntities.hasNext(); entity.motionY += 0.05D) {
						entity = (Entity)itEntities.next();
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
				if(!this.worldObj.isRemote) {
					Block.blocksList[this.blockID].dropBlockAsItem(this.worldObj, i1, i2, i3, 0, 0);
					this.worldObj.setBlockWithNotify(i1, i2, i3, 0);
				}
			} else {
				boolean z7 = this.blockID == Block.classicStickyPiston.blockID;
				buildRetractingPistons(this.worldObj, i1, i2, i3, this.xstart, this.ystart, this.zstart, this.worldObj.getBlockMetadata(i1, i2, i3), z7);
			}

		} else {
			if (!this.worldObj.isRemote) {
				this.worldObj.setBlockWithNotify(i1, i2, i3, this.blockID);
				if(this.data != 0) {
					this.worldObj.setBlockMetadataWithNotify(i1, i2, i3, this.data);
				}
	
				if(this.tileEntity != null) {
					this.worldObj.setBlockTileEntity(i1, i2, i3, this.tileEntity);
				}
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
		nBTTagCompound1.setByte("Block", (byte)this.blockID);
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
		this.blockID = nBTTagCompound1.getByte("Block") & 255;
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
