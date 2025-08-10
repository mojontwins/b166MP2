package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderHorse extends RenderLiving {
	public RenderHorse(ModelBase modelBase1, ModelBase modelBase2) {
		super(modelBase2, 0.5F);
		this.setRenderPassModel(modelBase1);
	}

	protected boolean shouldRenderPass(EntityLiving entityLiving1, int i2, float f3) {
		return this.func_176_a((EntityHorse)entityLiving1, i2);
	}

	protected float func_182_a(EntityHorse entityHorse1, float f2) {
		float f3 = entityHorse1.fwinge + (entityHorse1.fwingb - entityHorse1.fwinge) * f2;
		float f4 = entityHorse1.fwingd + (entityHorse1.fwingc - entityHorse1.fwingd) * f2;
		if(!entityHorse1.adult) {
			this.stretch(entityHorse1);
		}

		return (MathHelper.sin(f3) + 1.0F) * f4;
	}

	protected void stretch(EntityHorse entityHorse1) {
		GL11.glScalef(entityHorse1.b, entityHorse1.b, entityHorse1.b);
	}

	protected float func_170_d(EntityLiving entityLiving1, float f2) {
		return this.func_182_a((EntityHorse)entityLiving1, f2);
	}

	protected boolean func_176_a(EntityHorse entityHorse1, int i2) {
		if(!entityHorse1.typechosen) {
			entityHorse1.chooseType();
		}

		if(entityHorse1.texture == "/mob/horseb.png") {
			if(!entityHorse1.rideable) {
				this.loadTexture("/mob/horsea.png");
			} else {
				this.loadTexture("/mob/horseasaddle.png");
			}
		} else if(entityHorse1.texture == "/mob/horsebrownb.png") {
			if(!entityHorse1.rideable) {
				this.loadTexture("/mob/horsebrowna.png");
			} else {
				this.loadTexture("/mob/horsebrownsaddle.png");
			}
		} else if(entityHorse1.texture == "/mob/horseblackb.png") {
			if(!entityHorse1.rideable) {
				this.loadTexture("/mob/horseblacka.png");
			} else {
				this.loadTexture("/mob/horseblacksaddle.png");
			}
		} else if(entityHorse1.texture == "/mob/horsegoldb.png") {
			if(!entityHorse1.rideable) {
				this.loadTexture("/mob/horsegolda.png");
			} else {
				this.loadTexture("/mob/horsegoldsaddle.png");
			}
		} else if(entityHorse1.texture == "/mob/horsewhiteb.png") {
			if(!entityHorse1.rideable) {
				this.loadTexture("/mob/horsewhitea.png");
			} else {
				this.loadTexture("/mob/horsewhitesaddle.png");
			}
		} else if(entityHorse1.texture == "/mob/horsepackb.png") {
			if(!entityHorse1.rideable) {
				this.loadTexture("/mob/horsepacka.png");
			} else {
				this.loadTexture("/mob/horsepacksaddle.png");
			}
		} else if(entityHorse1.texture == "/mob/horsenightb.png") {
			if(!entityHorse1.rideable) {
				this.loadTexture("/mob/horsenighta.png");
			} else {
				this.loadTexture("/mob/horsenightsaddle.png");
			}
		} else if(entityHorse1.texture == "/mob/horsebpb.png") {
			if(!entityHorse1.rideable) {
				this.loadTexture("/mob/horsebpa.png");
			} else {
				this.loadTexture("/mob/horsebpsaddle.png");
			}
		}

		return i2 == 0 && !entityHorse1.horseboolean;
	}
}
