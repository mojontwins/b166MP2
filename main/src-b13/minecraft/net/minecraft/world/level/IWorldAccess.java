package net.minecraft.world.level;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.tile.entity.TileEntity;

public interface IWorldAccess {
	void markBlockNeedsUpdate(int i1, int i2, int i3);

	void markBlockRangeNeedsUpdate(int i1, int i2, int i3, int i4, int i5, int i6);

	void playSound(String string1, double d2, double d4, double d6, float f8, float f9);

	void spawnParticle(String string1, double d2, double d4, double d6, double d8, double d10, double d12);

	void obtainEntitySkin(Entity entity1);

	void releaseEntitySkin(Entity entity1);

	void playRecord(String string1, int i2, int i3, int i4);

	void doNothingWithTileEntity(int i1, int i2, int i3, TileEntity tileEntity4);

	void playAuxSFX(EntityPlayer entityPlayer1, int i2, int i3, int i4, int i5, int i6);

	void updateAllRenderers();

	void showString(String string);
}
