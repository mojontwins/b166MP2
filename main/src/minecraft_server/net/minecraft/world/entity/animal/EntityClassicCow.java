package net.minecraft.world.entity.animal;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public class EntityClassicCow extends EntityCow {
	protected boolean looksWithInterest;
	private float prevHeadRoll;
	private float headRoll;

	public EntityClassicCow(World world1) {
		super(world1);
		this.texture = "/mob/cow.png";
		this.setSize(0.9F, 1.3F);
	}
	
	@Override
	public boolean isAIEnabled() {
		return false;
	}
	
	@Override
	protected void updateEntityActionState() {
		super.updateEntityActionState();
		
		Entity target = null;
		
		EntityPlayer closestPlayer = this.worldObj.getClosestPlayerToEntity(this, 8.0D); 
		if (closestPlayer != null) {
			ItemStack heldItem = closestPlayer.inventory.getCurrentItem();
			
			// Start following player?
			if (heldItem != null && heldItem.itemID == Item.wheat.shiftedIndex) {
				target = closestPlayer;
			}
		}
		
		this.setTarget(target);
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		
		this.looksWithInterest = false;
		
		if (this.hasCurrentTarget() && !this.hasPath()) {
			Entity currentTargetEntity = this.getCurrentTarget();
			if (currentTargetEntity instanceof EntityPlayer) {
				EntityPlayer entityPlayer = (EntityPlayer) currentTargetEntity;
				ItemStack heldItem = entityPlayer.inventory.getCurrentItem();
				if (heldItem != null && heldItem.itemID == Item.wheat.shiftedIndex) {
					this.looksWithInterest = true;
				}
			}
		}
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		this.prevHeadRoll = this.headRoll;
		
		if(this.looksWithInterest) {
			this.headRoll += (1.0F - this.headRoll) * 0.4F;
		} else {
			this.headRoll += (0.0F - this.headRoll) * 0.4F;
		}
		
		if(this.looksWithInterest) {
			this.numTicksToChaseTarget = 10;
		}
	}
	
	public float getInterestedAngle(float f1) {
		return (this.prevHeadRoll + (this.headRoll - this.prevHeadRoll) * f1) * 0.15F * (float)Math.PI;
	}
}
