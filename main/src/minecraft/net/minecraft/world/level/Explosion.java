package net.minecraft.world.level;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class Explosion {
	public boolean isFlaming = false;
	private Random explosionRNG = new Random();
	private World worldObj;
	public double explosionX;
	public double explosionY;
	public double explosionZ;
	public Entity exploder;
	public float explosionSize;
	public Set<ChunkPosition> destroyedBlockPositions = new HashSet<ChunkPosition>();

	public Explosion(World world1, Entity entity2, double d3, double d5, double d7, float f9) {
		this.worldObj = world1;
		this.exploder = entity2;
		this.explosionSize = f9;
		this.explosionX = d3;
		this.explosionY = d5;
		this.explosionZ = d7;
	}

	public void doExplosionA() {
		float f1 = this.explosionSize;
		byte b2 = 16;

		int i3;
		int i4;
		int i5;
		double d15;
		double d17;
		double d19;
		for(i3 = 0; i3 < b2; ++i3) {
			for(i4 = 0; i4 < b2; ++i4) {
				for(i5 = 0; i5 < b2; ++i5) {
					if(i3 == 0 || i3 == b2 - 1 || i4 == 0 || i4 == b2 - 1 || i5 == 0 || i5 == b2 - 1) {
						double d6 = (double)((float)i3 / ((float)b2 - 1.0F) * 2.0F - 1.0F);
						double d8 = (double)((float)i4 / ((float)b2 - 1.0F) * 2.0F - 1.0F);
						double d10 = (double)((float)i5 / ((float)b2 - 1.0F) * 2.0F - 1.0F);
						double d12 = Math.sqrt(d6 * d6 + d8 * d8 + d10 * d10);
						d6 /= d12;
						d8 /= d12;
						d10 /= d12;
						float f14 = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
						d15 = this.explosionX;
						d17 = this.explosionY;
						d19 = this.explosionZ;

						for(float f21 = 0.3F; f14 > 0.0F; f14 -= f21 * 0.75F) {
							int i22 = MathHelper.floor_double(d15);
							int armorValue = MathHelper.floor_double(d17);
							int i24 = MathHelper.floor_double(d19);
							int i25 = this.worldObj.getBlockId(i22, armorValue, i24);
							if(i25 > 0) {
								f14 -= (Block.blocksList[i25].getExplosionResistance(this.exploder) + 0.3F) * f21;
							}

							if(f14 > 0.0F) {
								this.destroyedBlockPositions.add(new ChunkPosition(i22, armorValue, i24));
							}

							d15 += d6 * (double)f21;
							d17 += d8 * (double)f21;
							d19 += d10 * (double)f21;
						}
					}
				}
			}
		}

		this.explosionSize *= 2.0F;
		i3 = MathHelper.floor_double(this.explosionX - (double)this.explosionSize - 1.0D);
		i4 = MathHelper.floor_double(this.explosionX + (double)this.explosionSize + 1.0D);
		i5 = MathHelper.floor_double(this.explosionY - (double)this.explosionSize - 1.0D);
		int i29 = MathHelper.floor_double(this.explosionY + (double)this.explosionSize + 1.0D);
		int i7 = MathHelper.floor_double(this.explosionZ - (double)this.explosionSize - 1.0D);
		int i30 = MathHelper.floor_double(this.explosionZ + (double)this.explosionSize + 1.0D);
		List<?> list9 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, AxisAlignedBB.getBoundingBoxFromPool((double)i3, (double)i5, (double)i7, (double)i4, (double)i29, (double)i30));
		Vec3D vec3D31 = Vec3D.createVector(this.explosionX, this.explosionY, this.explosionZ);

		for(int i11 = 0; i11 < list9.size(); ++i11) {
			Entity entity33 = (Entity)list9.get(i11);
			double d13 = entity33.getDistance(this.explosionX, this.explosionY, this.explosionZ) / (double)this.explosionSize;
			if(d13 <= 1.0D) {
				d15 = entity33.posX - this.explosionX;
				d17 = entity33.posY - this.explosionY;
				d19 = entity33.posZ - this.explosionZ;
				double d34 = (double)MathHelper.sqrt_double(d15 * d15 + d17 * d17 + d19 * d19);
				d15 /= d34;
				d17 /= d34;
				d19 /= d34;
				double d35 = (double)this.worldObj.getBlockDensity(vec3D31, entity33.boundingBox);
				double d36 = (1.0D - d13) * d35;
				entity33.attackEntityFrom(DamageSource.explosion, (int)((d36 * d36 + d36) / 2.0D * 8.0D * (double)this.explosionSize + 1.0D));
				entity33.motionX += d15 * d36;
				entity33.motionY += d17 * d36;
				entity33.motionZ += d19 * d36;
			}
		}

		this.explosionSize = f1;
		ArrayList<ChunkPosition> arrayList32 = new ArrayList<ChunkPosition>();
		arrayList32.addAll(this.destroyedBlockPositions);
	}

	public void doExplosionB(boolean z1) {
		this.worldObj.playSoundEffect(this.explosionX, this.explosionY, this.explosionZ, "random.explode", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);
		this.worldObj.spawnParticle("hugeexplosion", this.explosionX, this.explosionY, this.explosionZ, 0.0D, 0.0D, 0.0D);
		ArrayList<ChunkPosition> arrayList2 = new ArrayList<ChunkPosition>();
		arrayList2.addAll(this.destroyedBlockPositions);

		int i3;
		ChunkPosition chunkPosition4;
		int i5;
		int i6;
		int i7;
		int i8;
		for(i3 = arrayList2.size() - 1; i3 >= 0; --i3) {
			chunkPosition4 = (ChunkPosition)arrayList2.get(i3);
			i5 = chunkPosition4.x;
			i6 = chunkPosition4.y;
			i7 = chunkPosition4.z;
			i8 = this.worldObj.getBlockId(i5, i6, i7);
			if(z1) {
				double d9 = (double)((float)i5 + this.worldObj.rand.nextFloat());
				double d11 = (double)((float)i6 + this.worldObj.rand.nextFloat());
				double d13 = (double)((float)i7 + this.worldObj.rand.nextFloat());
				double d15 = d9 - this.explosionX;
				double d17 = d11 - this.explosionY;
				double d19 = d13 - this.explosionZ;
				double d21 = (double)MathHelper.sqrt_double(d15 * d15 + d17 * d17 + d19 * d19);
				d15 /= d21;
				d17 /= d21;
				d19 /= d21;
				double d23 = 0.5D / (d21 / (double)this.explosionSize + 0.1D);
				d23 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
				d15 *= d23;
				d17 *= d23;
				d19 *= d23;
				this.worldObj.spawnParticle("explode", (d9 + this.explosionX * 1.0D) / 2.0D, (d11 + this.explosionY * 1.0D) / 2.0D, (d13 + this.explosionZ * 1.0D) / 2.0D, d15, d17, d19);
				this.worldObj.spawnParticle("smoke", d9, d11, d13, d15, d17, d19);
			}

			if(i8 > 0) {
				Block.blocksList[i8].dropBlockAsItemWithChance(this.worldObj, i5, i6, i7, this.worldObj.getBlockMetadata(i5, i6, i7), 0.3F, 0);
				this.worldObj.setBlockWithNotify(i5, i6, i7, 0);
				Block.blocksList[i8].onBlockDestroyedByExplosion(this.worldObj, i5, i6, i7);
			}
		}

		if(this.isFlaming) {
			for(i3 = arrayList2.size() - 1; i3 >= 0; --i3) {
				chunkPosition4 = (ChunkPosition)arrayList2.get(i3);
				i5 = chunkPosition4.x;
				i6 = chunkPosition4.y;
				i7 = chunkPosition4.z;
				i8 = this.worldObj.getBlockId(i5, i6, i7);
				int i25 = this.worldObj.getBlockId(i5, i6 - 1, i7);
				if(i8 == 0 && Block.opaqueCubeLookup[i25] && this.explosionRNG.nextInt(3) == 0) {
					this.worldObj.setBlockWithNotify(i5, i6, i7, Block.fire.blockID);
				}
			}
		}

	}
}
