package net.minecraft.world.entity;

import net.minecraft.util.Translator;
import net.minecraft.world.entity.player.EntityPlayer;

public class EntityDamageSourceIndirect extends EntityDamageSource {
	private Entity indirectEntity;

	public EntityDamageSourceIndirect(String string1, Entity entity2, Entity entity3) {
		super(string1, entity2);
		this.indirectEntity = entity3;
	}

	public Entity getSourceOfDamage() {
		return this.damageSourceEntity;
	}

	public Entity getEntity() {
		return this.indirectEntity;
	}

	public String getDeathMessage(EntityPlayer entityPlayer1) {
		return Translator.translateToLocalFormatted("death." + this.damageType, new Object[]{entityPlayer1.username, this.indirectEntity.getUsername()});
	}
}
