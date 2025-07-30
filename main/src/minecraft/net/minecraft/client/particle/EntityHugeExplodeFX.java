package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.level.World;

public class EntityHugeExplodeFX extends EntityFX {
	private int timeSinceStart = 0;
	private int maximumTime = 0;

	public EntityHugeExplodeFX(World world1, double d2, double d4, double d6, double d8, double d10, double d12) {
		super(world1, d2, d4, d6, 0.0D, 0.0D, 0.0D);
		this.maximumTime = 8;
	}

	public void renderParticle(Tessellator tessellator1, float f2, float f3, float f4, float f5, float f6, float f7) {
	}

	public void onUpdate() {
		for(int i1 = 0; i1 < 6; ++i1) {
			double d2 = this.posX + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
			double d4 = this.posY + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
			double d6 = this.posZ + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0D;
			this.worldObj.spawnParticle("largeexplode", d2, d4, d6, (double)((float)this.timeSinceStart / (float)this.maximumTime), 0.0D, 0.0D);
		}

		++this.timeSinceStart;
		if(this.timeSinceStart == this.maximumTime) {
			this.setDead();
		}

	}

	public int getFXLayer() {
		return 1;
	}
}
