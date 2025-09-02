package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelWereHuman;
import net.minecraft.client.model.ModelWerewolf;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.EntityWerewolf;

public class RenderWerewolf extends RenderLiving {
	private ModelWerewolf tempWerewolf;

	public RenderWerewolf(ModelWereHuman modelwerehuman, ModelBase modelbase, float f) {
		super(modelbase, f);
		this.setRenderPassModel(modelwerehuman);
		this.tempWerewolf = (ModelWerewolf)modelbase;
	}

	public void doRenderLiving(EntityLiving entityliving, double d, double d1, double d2, float f, float f1) {
		EntityWerewolf entitywerewolf = (EntityWerewolf)entityliving;
		this.tempWerewolf.hunched = entitywerewolf.getIsHunched();
		super.doRenderLiving(entityliving, d, d1, d2, f, f1);
	}

	protected int setWoolColorAndRender(EntityWerewolf entitywerewolf, int i) {
		if(!entitywerewolf.getIsHumanForm()) {
			entitywerewolf.texture = "/mob/werewolf.png";
			this.loadTexture("/mob/wereblank.png");
		} else {
			entitywerewolf.texture = "/mob/wereblank.png";
			this.loadTexture("/mob/werehuman.png");
		}

		return 1;
	}

	protected int shouldRenderPass(EntityLiving entityliving, int i, float f) {
		return this.setWoolColorAndRender((EntityWerewolf)entityliving, i);
	}
}
