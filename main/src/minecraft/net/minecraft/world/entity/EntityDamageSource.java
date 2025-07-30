package net.minecraft.world.entity;

import net.minecraft.util.Translator;
import net.minecraft.world.entity.player.EntityPlayer;

public class EntityDamageSource extends DamageSource {
	protected Entity damageSourceEntity;

	public EntityDamageSource(String string1, Entity entity2) {
		super(string1);
		this.damageSourceEntity = entity2;
	}

	public Entity getEntity() {
		return this.damageSourceEntity;
	}

	public String getDeathMessage(EntityPlayer entityPlayer1) {
		return Translator.translateToLocalFormatted("death." + this.damageType, new Object[]{entityPlayer1.username, this.damageSourceEntity.getUsername()});
	}
}
