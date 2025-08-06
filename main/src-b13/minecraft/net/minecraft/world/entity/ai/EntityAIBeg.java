package net.minecraft.world.entity.ai;

import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public class EntityAIBeg extends EntityAIBase {
	private EntityWolf theWolf;
	private EntityPlayer thePlayer;
	private World theWorld;
	private float field_48346_d;
	private int field_48347_e;

	public EntityAIBeg(EntityWolf entityWolf1, float f2) {
		this.theWolf = entityWolf1;
		this.theWorld = entityWolf1.worldObj;
		this.field_48346_d = f2;
		this.setMutexBits(2);
	}

	public boolean shouldExecute() {
		this.thePlayer = this.theWorld.getClosestPlayerToEntity(this.theWolf, (double)this.field_48346_d);
		return this.thePlayer == null ? false : this.func_48345_a(this.thePlayer);
	}

	public boolean continueExecuting() {
		return !this.thePlayer.isEntityAlive() ? false : (this.theWolf.getDistanceSqToEntity(this.thePlayer) > (double)(this.field_48346_d * this.field_48346_d) ? false : this.field_48347_e > 0 && this.func_48345_a(this.thePlayer));
	}

	public void startExecuting() {
		this.theWolf.func_48150_h(true);
		this.field_48347_e = 40 + this.theWolf.getRNG().nextInt(40);
	}

	public void resetTask() {
		this.theWolf.func_48150_h(false);
		this.thePlayer = null;
	}

	public void updateTask() {
		this.theWolf.getLookHelper().setLookPosition(this.thePlayer.posX, this.thePlayer.posY + (double)this.thePlayer.getEyeHeight(), this.thePlayer.posZ, 10.0F, (float)this.theWolf.getVerticalFaceSpeed());
		--this.field_48347_e;
	}

	private boolean func_48345_a(EntityPlayer entityPlayer1) {
		ItemStack itemStack2 = entityPlayer1.inventory.getCurrentItem();
		return itemStack2 == null ? false : (!this.theWolf.isTamed() && itemStack2.itemID == Item.bone.shiftedIndex ? true : this.theWolf.isWheat(itemStack2));
	}
}
