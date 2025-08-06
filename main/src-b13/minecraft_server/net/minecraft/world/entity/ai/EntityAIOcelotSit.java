package net.minecraft.world.entity.ai;

import net.minecraft.world.entity.animal.EntityOcelot;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockBed;
import net.minecraft.world.level.tile.entity.TileEntityChest;

public class EntityAIOcelotSit extends EntityAIBase {
	private final EntityOcelot theOcelot;
	private final float field_50083_b;
	private int timeExecuting = 0;
	private int sittingTime = 0;
	private int maxExecutingTime = 0;
	private int targetX = 0;
	private int targetY = 0;
	private int targetZ = 0;

	public EntityAIOcelotSit(EntityOcelot entityOcelot1, float f2) {
		this.theOcelot = entityOcelot1;
		this.field_50083_b = f2;
		this.setMutexBits(5);
	}

	public boolean shouldExecute() {
		return this.theOcelot.isTamed() && !this.theOcelot.isSitting() && this.theOcelot.getRNG().nextDouble() <= 0.006500000134110451D && this.preferedBlockNearby();
	}

	public boolean continueExecuting() {
		return this.timeExecuting <= this.maxExecutingTime && this.sittingTime <= 60 && this.likesSittingHere(this.theOcelot.worldObj, this.targetX, this.targetY, this.targetZ);
	}

	public void startExecuting() {
		this.theOcelot.getNavigator().tryMoveToXYZ((double)((float)this.targetX) + 0.5D, (double)(this.targetY + 1), (double)((float)this.targetZ) + 0.5D, this.field_50083_b);
		this.timeExecuting = 0;
		this.sittingTime = 0;
		this.maxExecutingTime = this.theOcelot.getRNG().nextInt(this.theOcelot.getRNG().nextInt(1200) + 1200) + 1200;
		this.theOcelot.getAiSit().func_48407_a(false);
	}

	public void resetTask() {
		this.theOcelot.setSitting(false);
	}

	public void updateTask() {
		++this.timeExecuting;
		this.theOcelot.getAiSit().func_48407_a(false);
		if(this.theOcelot.getDistanceSq((double)this.targetX, (double)(this.targetY + 1), (double)this.targetZ) > 1.0D) {
			this.theOcelot.setSitting(false);
			this.theOcelot.getNavigator().tryMoveToXYZ((double)((float)this.targetX) + 0.5D, (double)(this.targetY + 1), (double)((float)this.targetZ) + 0.5D, this.field_50083_b);
			++this.sittingTime;
		} else if(!this.theOcelot.isSitting()) {
			this.theOcelot.setSitting(true);
		} else {
			--this.sittingTime;
		}

	}

	private boolean preferedBlockNearby() {
		int i1 = (int)this.theOcelot.posY;
		double d2 = 2.147483647E9D;

		for(int i4 = (int)this.theOcelot.posX - 8; (double)i4 < this.theOcelot.posX + 8.0D; ++i4) {
			for(int i5 = (int)this.theOcelot.posZ - 8; (double)i5 < this.theOcelot.posZ + 8.0D; ++i5) {
				if(this.likesSittingHere(this.theOcelot.worldObj, i4, i1, i5) && this.theOcelot.worldObj.isAirBlock(i4, i1 + 1, i5)) {
					double d6 = this.theOcelot.getDistanceSq((double)i4, (double)i1, (double)i5);
					if(d6 < d2) {
						this.targetX = i4;
						this.targetY = i1;
						this.targetZ = i5;
						d2 = d6;
					}
				}
			}
		}

		return d2 < 2.147483647E9D;
	}

	private boolean likesSittingHere(World world1, int i2, int i3, int i4) {
		int i5 = world1.getBlockId(i2, i3, i4);
		int i6 = world1.getBlockMetadata(i2, i3, i4);
		
		if(i5 == Block.chest.blockID) {
			TileEntityChest tileEntityChest7 = (TileEntityChest)world1.getBlockTileEntity(i2, i3, i4);
			if(tileEntityChest7.numUsingPlayers < 1) {
				return true;
			}
		} else {
			if(i5 == Block.stoneOvenActive.blockID) {
				return true;
			}

			if(i5 == Block.bed.blockID && !BlockBed.isBlockFootOfBed(i6)) {
				return true;
			}
		}

		return false;
	}
}
