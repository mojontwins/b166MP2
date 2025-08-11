package net.minecraft.world.entity.animal;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.World;

public class EntityClassicChicken extends EntityChicken {
	public boolean field_753_a = false;
	public float field_752_b = 0.0F;
	public float destPos = 0.0F;
	public float field_757_d;
	public float field_756_e;
	public float field_755_h = 1.0F;
	public int timeUntilNextEgg;
	
	public EntityClassicChicken(World world1) {
		super(world1);
		this.texture = "/mob/chicken.png";
		this.setSize(0.3F, 0.4F);
		this.health = 4;
		this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
	}
	
	@Override
	public boolean isAIEnabled() {
		return false;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.field_756_e = this.field_752_b;
		this.field_757_d = this.destPos;
		this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 4) * 0.3D);
		if(this.destPos < 0.0F) {
			this.destPos = 0.0F;
		}

		if(this.destPos > 1.0F) {
			this.destPos = 1.0F;
		}

		if(!this.onGround && this.field_755_h < 1.0F) {
			this.field_755_h = 1.0F;
		}

		this.field_755_h = (float)((double)this.field_755_h * 0.9D);
		if(!this.onGround && this.motionY < 0.0D) {
			this.motionY *= 0.6D;
		}

		this.field_752_b += this.field_755_h * 2.0F;
		if(!this.worldObj.isRemote && --this.timeUntilNextEgg <= 0) {
			this.worldObj.playSoundAtEntity(this, "mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			this.dropItem(Item.egg.shiftedIndex, 1);
			this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
		}

	}

}
