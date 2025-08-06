package net.minecraft.world.entity.monster;

import java.util.Iterator;
import java.util.List;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemGolden;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class EntityPigZombie extends EntityZombie {
	private int angerLevel = 0;
	private int randomSoundDelay = 0;
	private ItemStack heldItem;
	
	private int spawnObjectCounter;
	private boolean preparingToDrop = false;
	private boolean dropAGoodOne = false;

	public EntityPigZombie(World world1) {
		super(world1);
		this.texture = "/mob/pigzombie.png";
		this.moveSpeed = 0.5F;
		this.attackStrength = 5;
		this.isImmuneToFire = true;
		
		switch(world1.rand.nextInt(8)) {
		case 0: this.heldItem = new ItemStack(Item.maceGold, 1);
		case 1: this.heldItem = new ItemStack(Item.battleGold, 1);
		default: this.heldItem = new ItemStack(Item.swordGold, 1);
		}
	}

	protected boolean isAIEnabled() {
		return false;
	}

	public void onUpdate() {
		this.moveSpeed = this.entityToAttack != null ? 0.95F : 0.5F;
		if(this.randomSoundDelay > 0 && --this.randomSoundDelay == 0) {
			this.worldObj.playSoundAtEntity(this, "mob.zombiepig.zpigangry", this.getSoundVolume() * 2.0F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 1.8F);
		}

		super.onUpdate();
	}

	public boolean getCanSpawnHere() {
		return this.worldObj.difficultySetting > 0 && this.worldObj.checkIfAABBIsClear(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).size() == 0 && !this.worldObj.isAnyLiquid(this.boundingBox);
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
		compoundTag.setShort("Anger", (short)this.angerLevel);
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
		this.angerLevel = compoundTag.getShort("Anger");
	}

	protected Entity findPlayerToAttack() {
		// return this.angerLevel == 0 ? null : super.findPlayerToAttack();
		
		// new behaviour:
		
		// First look for golden items around, if not angry
		if(this.angerLevel == 0) {
			List<Entity> list = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(16.0D, 4.0D, 16.0D));
			Iterator<Entity> iterator = list.iterator();
			while(iterator.hasNext()) {
				EntityItem entityItem = (EntityItem)iterator.next();
				if(entityItem != null && entityItem.item != null && entityItem.item.getItem() instanceof ItemGolden) {
					return entityItem;
				}
			}
		}
		
		// Then look for players. nearest player found not wearing gold becomes the target.
		Entity entityToAttack = super.findPlayerToAttack();
		if(entityToAttack instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer) entityToAttack;
			if(!entityPlayer.wearingGold()) return entityToAttack;
		}
		
		return this.angerLevel == 0 ? null : entityToAttack;
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		
		// Pick up golden stuff & throw shit at random
		if(!this.worldObj.isRemote) {
			if(!this.preparingToDrop && this.angerLevel == 0 && this.rand.nextInt(32) == 0) {
				List<Entity> list = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox);
				Iterator<Entity> iterator = list.iterator();
				while(iterator.hasNext()) {
					EntityItem entityItem = (EntityItem)iterator.next();
					if(entityItem != null && entityItem.item != null && entityItem.item.getItem() instanceof ItemGolden) {
						entityItem.setDead();
						this.spawnObjectCounter = 50;
						this.dropAGoodOne = entityItem.item.itemID == Item.ingotGold.shiftedIndex;
						this.preparingToDrop = true;
						System.out.println ("BLEDO");
					}
				}
			} 
			
			if(this.angerLevel > 0) {
				this.preparingToDrop = false;
			}
			
			if(this.preparingToDrop) {
				
				this.spawnObjectCounter --; if (this.spawnObjectCounter <= 0) {
					this.preparingToDrop = false;
					
					// drop object
					int itemId = 0;
					
					if(this.dropAGoodOne) {
						int chance = this.rand.nextInt(500);
						if(chance == 0) {
							itemId = Item.hammerDiamond.shiftedIndex;
						} else if(chance == 1) {
							itemId = Item.battleDiamond.shiftedIndex;				
						} else if(chance < 7) {
							itemId = Item.hammerSteel.shiftedIndex;
						} else if(chance < 12) {
							itemId = Item.battleSteel.shiftedIndex;				
						} 
					}

					if(itemId == 0) {
						switch(this.rand.nextInt(8)) {
							case 0:
								itemId = Item.ingotIron.shiftedIndex;
								break;
							case 1:
								itemId = Item.silk.shiftedIndex;
								break;
							case 2:
								itemId = Block.obsidian.blockID;
								break;
							case 4:
								itemId = Item.arrow.shiftedIndex;
								break;
							case 5:
								itemId = Item.brick.shiftedIndex;
								break;
							case 6:
								itemId = this.rand.nextBoolean() ? Block.mushroomRed.blockID : Block.mushroomBrown.blockID;
								break;
							default:
								itemId = Item.ingotIron.shiftedIndex;
						}
					}
					
					this.dropItem(itemId, 1);
				}
			}
		}		
	}

	public boolean attackEntityFrom(DamageSource damageSource1, int i2) {
		Entity entity3 = damageSource1.getEntity();
		if(entity3 instanceof EntityPlayer) {
			List<Entity> list4 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(32.0D, 32.0D, 32.0D));

			for(int i5 = 0; i5 < list4.size(); ++i5) {
				Entity entity6 = (Entity)list4.get(i5);
				if(entity6 instanceof EntityPigZombie) {
					EntityPigZombie entityPigZombie7 = (EntityPigZombie)entity6;
					entityPigZombie7.becomeAngryAt(entity3);
				}
			}

			this.becomeAngryAt(entity3);
		}

		return super.attackEntityFrom(damageSource1, i2);
	}

	public void becomeAngryAt(Entity entity1) {
		this.entityToAttack = entity1;
		this.angerLevel = 400 + this.rand.nextInt(400);
		this.randomSoundDelay = this.rand.nextInt(40);
	}

	protected String getLivingSound() {
		return "mob.zombiepig.zpig";
	}

	protected String getHurtSound() {
		return "mob.zombiepig.zpighurt";
	}

	protected String getDeathSound() {
		return "mob.zombiepig.zpigdeath";
	}

	protected void dropFewItems(boolean z1, int i2) {
		int i3 = this.rand.nextInt(2 + i2);

		int i4;
		for(i4 = 0; i4 < i3; ++i4) {
			this.dropItem(Item.rottenFlesh.shiftedIndex, 1);
		}

		i3 = this.rand.nextInt(2 + i2);

		for(i4 = 0; i4 < i3; ++i4) {
			this.dropItem(Item.goldNugget.shiftedIndex, 1);
		}

	}

	protected void dropRareDrop(int i1) {
		if(i1 > 0) {
			ItemStack itemStack2 = new ItemStack(Item.swordGold);
			this.entityDropItem(itemStack2, 0.0F);
		} else {
			int i3 = this.rand.nextInt(3);
			if(i3 == 0) {
				this.dropItem(Item.ingotGold.shiftedIndex, 1);
			} else if(i3 == 1) {
				this.dropItem(Item.swordGold.shiftedIndex, 1);
			} else if(i3 == 2) {
				this.dropItem(Item.helmetGold.shiftedIndex, 1);
			}
		}

	}

	protected int getDropItemId() {
		return Item.rottenFlesh.shiftedIndex;
	}

	public ItemStack getHeldItem() {
		return heldItem;
	}
	
	@Override
	public boolean setHeldItem(ItemStack itemStack) {
		this.heldItem = itemStack;
		return true;
	}
}
