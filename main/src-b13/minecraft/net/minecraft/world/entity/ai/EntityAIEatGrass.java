package net.minecraft.world.entity.ai;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class EntityAIEatGrass extends EntityAIBase {
	private EntityLiving theEntity;
	private World theWorld;
	int eatGrassTick = 0;

	public EntityAIEatGrass(EntityLiving entityLiving1) {
		this.theEntity = entityLiving1;
		this.theWorld = entityLiving1.worldObj;
		this.setMutexBits(7);
	}

	public boolean shouldExecute() {
		if(this.theEntity.getRNG().nextInt(this.theEntity.isChild() ? 50 : 1000) != 0) {
			return false;
		} else {
			int i1 = MathHelper.floor_double(this.theEntity.posX);
			int i2 = MathHelper.floor_double(this.theEntity.posY);
			int i3 = MathHelper.floor_double(this.theEntity.posZ);
			return /*this.theWorld.getBlockId(i1, i2, i3) == Block.tallGrass.blockID && this.theWorld.getBlockMetadata(i1, i2, i3) == 1 ? true : */this.theWorld.getBlockId(i1, i2 - 1, i3) == Block.grass.blockID;
		}
	}

	public void startExecuting() {
		this.eatGrassTick = 40;
		this.theWorld.setEntityState(this.theEntity, (byte)10);
		this.theEntity.getNavigator().clearPathEntity();
	}

	public void resetTask() {
		this.eatGrassTick = 0;
	}

	public boolean continueExecuting() {
		return this.eatGrassTick > 0;
	}

	public int getTimerValue() {
		return this.eatGrassTick;
	}

	public void updateTask() {
		this.eatGrassTick = Math.max(0, this.eatGrassTick - 1);
		if(this.eatGrassTick == 4) {
			int i1 = MathHelper.floor_double(this.theEntity.posX);
			int i2 = MathHelper.floor_double(this.theEntity.posY);
			int i3 = MathHelper.floor_double(this.theEntity.posZ);
			/*if(this.theWorld.getBlockId(i1, i2, i3) == Block.tallGrass.blockID) {
				this.theWorld.playAuxSFX(2001, i1, i2, i3, Block.tallGrass.blockID + 4096);
				this.theWorld.setBlockWithNotify(i1, i2, i3, 0);
				this.theEntity.eatGrassBonus();
			} else */if(this.theWorld.getBlockId(i1, i2 - 1, i3) == Block.grass.blockID) {
				this.theWorld.playAuxSFX(2001, i1, i2 - 1, i3, Block.grass.blockID);
				this.theWorld.setBlockWithNotify(i1, i2 - 1, i3, Block.dirt.blockID);
				this.theEntity.eatGrassBonus();
			}

		}
	}
}
