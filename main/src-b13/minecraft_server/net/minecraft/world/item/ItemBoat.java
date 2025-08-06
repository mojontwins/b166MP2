package net.minecraft.world.item;

import java.util.List;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityBoat;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.EnumMovingObjectType;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

public class ItemBoat extends Item {
	public ItemBoat(int i1) {
		super(i1);
		this.maxStackSize = 1;
		
		this.displayOnCreativeTab = CreativeTabs.tabTransport;
	}

	public ItemStack onItemRightClick(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
		float f4 = 1.0F;
		float f5 = entityPlayer3.prevRotationPitch + (entityPlayer3.rotationPitch - entityPlayer3.prevRotationPitch) * f4;
		float f6 = entityPlayer3.prevRotationYaw + (entityPlayer3.rotationYaw - entityPlayer3.prevRotationYaw) * f4;
		double d7 = entityPlayer3.prevPosX + (entityPlayer3.posX - entityPlayer3.prevPosX) * (double)f4;
		double d9 = entityPlayer3.prevPosY + (entityPlayer3.posY - entityPlayer3.prevPosY) * (double)f4 + 1.62D - (double)entityPlayer3.yOffset;
		double d11 = entityPlayer3.prevPosZ + (entityPlayer3.posZ - entityPlayer3.prevPosZ) * (double)f4;
		Vec3D vec3D13 = Vec3D.createVector(d7, d9, d11);
		float f14 = MathHelper.cos(-f6 * ((float)Math.PI / 180.0F) - (float)Math.PI);
		float f15 = MathHelper.sin(-f6 * ((float)Math.PI / 180.0F) - (float)Math.PI);
		float f16 = -MathHelper.cos(-f5 * ((float)Math.PI / 180.0F));
		float f17 = MathHelper.sin(-f5 * ((float)Math.PI / 180.0F));
		float f18 = f15 * f16;
		float f20 = f14 * f16;
		double d21 = 5.0D;
		Vec3D vec3D23 = vec3D13.addVector((double)f18 * d21, (double)f17 * d21, (double)f20 * d21);
		MovingObjectPosition movingObjectPosition24 = world2.rayTraceBlocks_do(vec3D13, vec3D23, true);
		if(movingObjectPosition24 == null) {
			return itemStack1;
		} else {
			Vec3D vec3D25 = entityPlayer3.getLook(f4);
			boolean z26 = false;
			float f27 = 1.0F;
			List<Entity> list28 = world2.getEntitiesWithinAABBExcludingEntity(entityPlayer3, entityPlayer3.boundingBox.addCoord(vec3D25.xCoord * d21, vec3D25.yCoord * d21, vec3D25.zCoord * d21).expand((double)f27, (double)f27, (double)f27));

			for(int i29 = 0; i29 < list28.size(); ++i29) {
				Entity entity30 = (Entity)list28.get(i29);
				if(entity30.canBeCollidedWith()) {
					float f31 = entity30.getCollisionBorderSize();
					AxisAlignedBB axisAlignedBB32 = entity30.boundingBox.expand((double)f31, (double)f31, (double)f31);
					if(axisAlignedBB32.isVecInside(vec3D13)) {
						z26 = true;
					}
				}
			}

			if(z26) {
				return itemStack1;
			} else {
				if(movingObjectPosition24.typeOfHit == EnumMovingObjectType.TILE) {
					int i33 = movingObjectPosition24.blockX;
					int i34 = movingObjectPosition24.blockY;
					int i35 = movingObjectPosition24.blockZ;
					if(!world2.isRemote) {
						if(world2.getBlockId(i33, i34, i35) == Block.snow.blockID) {
							--i34;
						}

						world2.spawnEntityInWorld(new EntityBoat(world2, (double)((float)i33 + 0.5F), (double)((float)i34 + 1.0F), (double)((float)i35 + 0.5F)));
					}

					if(!entityPlayer3.capabilities.isCreativeMode) {
						--itemStack1.stackSize;
					}
				}

				return itemStack1;
			}
		}
	}
}
