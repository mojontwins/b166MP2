package net.minecraft.client.player;

import com.mojang.nbt.NBTTagCompound;
import com.risugami.recipebook.GuiRecipeBook;
import com.risugami.recipebook.InventoryRecipeBook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.GuiChest;
import net.minecraft.client.gui.GuiDispenser;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.src.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.inventory.IInventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockPos;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.entity.TileEntityDispenser;
import net.minecraft.world.level.tile.entity.TileEntityFurnace;
import net.minecraft.world.level.tile.entity.TileEntitySign;

public class EntityPlayerSP extends EntityPlayer {
	public MovementInput movementInput;
	public Minecraft mc;
	protected int sprintToggleTimer = 0;
	public int sprintingTicksLeft = 0;
	public float renderArmYaw;
	public float renderArmPitch;
	public float prevRenderArmYaw;
	public float prevRenderArmPitch;

	public EntityPlayerSP(Minecraft mc, World world2, User session3, int i4) {
		super(world2);
		this.mc = mc;
		this.dimension = i4;
		if(session3 != null && session3.username != null && session3.username.length() > 0) {
			this.skinUrl = "http://s3.amazonaws.com/MinecraftSkins/" + session3.username + ".png";
		}

		this.username = session3.username;
	}

	public void moveEntity(double d1, double d3, double d5) {
		super.moveEntity(d1, d3, d5);
	}

	public void updateEntityActionState() {
		super.updateEntityActionState();
		this.moveStrafing = this.movementInput.moveStrafe;
		this.moveForward = this.movementInput.moveForward;
		this.isJumping = this.movementInput.jump;
		this.prevRenderArmYaw = this.renderArmYaw;
		this.prevRenderArmPitch = this.renderArmPitch;
		this.renderArmPitch = (float)((double)this.renderArmPitch + (double)(this.rotationPitch - this.renderArmPitch) * 0.5D);
		this.renderArmYaw = (float)((double)this.renderArmYaw + (double)(this.rotationYaw - this.renderArmYaw) * 0.5D);
	}

	protected boolean isClientWorld() {
		return true;
	}

	public void onLivingUpdate() {
		if(this.sprintingTicksLeft > 0) {
			--this.sprintingTicksLeft;
			if(this.sprintingTicksLeft == 0) {
				this.setSprinting(false);
			}
		}

		if(this.sprintToggleTimer > 0) {
			--this.sprintToggleTimer;
		}

		if(this.mc.playerController.func_35643_e()) {
			this.posX = this.posZ = 0.5D;
			this.posX = 0.0D;
			this.posZ = 0.0D;
			this.rotationYaw = (float)this.ticksExisted / 12.0F;
			this.rotationPitch = 10.0F;
			this.posY = 68.5D;
		} else {

			this.prevTimeInPortal = this.timeInPortal;
			boolean z1;
			if(this.inPortal) {
				if(!this.worldObj.isRemote && this.ridingEntity != null) {
					this.mountEntity((Entity)null);
				}

				if(this.mc.currentScreen != null) {
					this.mc.displayGuiScreen((GuiScreen)null);
				}

				if(this.timeInPortal == 0.0F) {
					this.mc.sndManager.playSoundFX("portal.trigger", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
				}

				this.timeInPortal += 0.0125F;
				if(this.timeInPortal >= 1.0F) {
					this.timeInPortal = 1.0F;
					if(!this.worldObj.isRemote) {
						this.timeUntilPortal = 10;
						this.mc.sndManager.playSoundFX("portal.travel", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
						z1 = false;
						byte b5;
						if(this.dimension == -1) {
							b5 = 0;
						} else {
							b5 = -1;
						}

						this.mc.usePortal(b5);
					}
				}

				this.inPortal = false;
			} else {
				if(this.timeInPortal > 0.0F) {
					this.timeInPortal -= 0.05F;
				}

				if(this.timeInPortal < 0.0F) {
					this.timeInPortal = 0.0F;
				}
			}

			if(this.timeUntilPortal > 0) {
				--this.timeUntilPortal;
			}

			z1 = this.movementInput.jump;
			float f2 = 0.8F;
			boolean z3 = this.movementInput.moveForward >= f2;
			
			this.movementInput.readMovementInput();
			this.pressingJump = this.movementInput.jump;
			
			if(this.isUsingItem()) {
				this.movementInput.moveStrafe *= 0.2F;
				this.movementInput.moveForward *= 0.2F;
				this.sprintToggleTimer = 0;
			}

			if(this.movementInput.sneak && this.ySize < 0.2F) {
				this.ySize = 0.2F;
			}

			this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ + (double)this.width * 0.35D);
			this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ - (double)this.width * 0.35D);
			this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ - (double)this.width * 0.35D);
			this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, this.boundingBox.minY + 0.5D, this.posZ + (double)this.width * 0.35D);
			boolean z4 = (float)this.getFoodStats().getFoodLevel() > 6.0F;
			if(this.onGround && !z3 && this.movementInput.moveForward >= f2 && !this.isSprinting() && z4 && !this.isUsingItem()) {
				if(this.sprintToggleTimer == 0) {
					this.sprintToggleTimer = 7;
				} else {
					if(GameRules.boolRule("enableSprinting"))  { this.setSprinting(true); }
					this.sprintToggleTimer = 0;
				}
			}

			if(this.isSneaking()) {
				this.sprintToggleTimer = 0;
			}

			if(this.isSprinting() && (this.movementInput.moveForward < f2 || this.isCollidedHorizontally || !z4)) {
				this.setSprinting(false);
			}

			if(this.capabilities.allowFlying && !z1 && this.movementInput.jump) {
				if(this.flyToggleTimer == 0) {
					this.flyToggleTimer = 7;
				} else {
					this.capabilities.isFlying = !this.capabilities.isFlying;
					this.func_50009_aI();
					this.flyToggleTimer = 0;
				}
			}

			if(this.capabilities.isFlying) {
				if(this.movementInput.sneak) {
					this.motionY -= 0.15D;
				}

				if(this.movementInput.jump) {
					this.motionY += 0.15D;
				}
			}

			super.onLivingUpdate();
			if(this.onGround && this.capabilities.isFlying) {
				this.capabilities.isFlying = false;
				this.func_50009_aI();
			}

		}
	}

	public float getFOVMultiplier() {
		float f1 = 1.0F;
		if(this.capabilities.isFlying) {
			f1 *= 1.1F;
		}

		f1 *= (this.landMovementFactor * this.getSpeedModifier() / this.speedOnGround + 1.0F) / 2.0F;
		if(this.isUsingItem() && this.getItemInUse().itemID == Item.bow.shiftedIndex) {
			int i2 = this.getItemInUseDuration();
			float f3 = (float)i2 / 20.0F;
			if(f3 > 1.0F) {
				f3 = 1.0F;
			} else {
				f3 *= f3;
			}

			f1 *= 1.0F - f3 * 0.15F;
		}

		return f1;
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
		compoundTag.setInteger("Score", this.score);
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
		this.score = compoundTag.getInteger("Score");
	}

	public void closeScreen() {
		super.closeScreen();
		this.mc.displayGuiScreen((GuiScreen)null);
	}

	public void displayGUIEditSign(TileEntitySign tileEntitySign1) {
		this.mc.displayGuiScreen(new GuiEditSign(tileEntitySign1));
	}
	
	public void displayGUIChest(IInventory iInventory1) {
		this.mc.displayGuiScreen(new GuiChest(this.inventory, iInventory1));
	}

	public void displayWorkbenchGUI(int i1, int i2, int i3) {
		this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj, i1, i2, i3));
	}

	public void displayGUIFurnace(TileEntityFurnace tileEntityFurnace1) {
		this.mc.displayGuiScreen(new GuiFurnace(this.inventory, tileEntityFurnace1));
	}
	
	public void displayGUIDispenser(TileEntityDispenser tileEntityDispenser1) {
		this.mc.displayGuiScreen(new GuiDispenser(this.inventory, tileEntityDispenser1));
	}
	
	public void displayGUIRecipeBook(ItemStack itemStack) {
		this.mc.displayGuiScreen(new GuiRecipeBook(new InventoryRecipeBook(itemStack)));
	}

	public void onCriticalHit(Entity entity1) {
		this.mc.effectRenderer.addEffect(new EntityCrit2FX(this.mc.theWorld, entity1));
	}

	public void onEnchantmentCritical(Entity entity1) {
		EntityCrit2FX entityCrit2FX2 = new EntityCrit2FX(this.mc.theWorld, entity1, "magicCrit");
		this.mc.effectRenderer.addEffect(entityCrit2FX2);
	}

	public void onItemPickup(Entity entity1, int i2) {
		this.mc.effectRenderer.addEffect(new EntityPickupFX(this.mc.theWorld, entity1, this, -0.5F));
	}

	public void sendChatMessage(String string1, BlockPos mousePos) {
	}

	public boolean isSneaking() {
		return this.movementInput.sneak && !this.sleeping;
	}

	public void setHealth(int i1) {
		int i2 = this.getHealth() - i1;
		if(i2 <= 0) {
			this.setEntityHealth(i1);
			if(i2 < 0) {
				this.heartsLife = this.heartsHalvesLife / 2;
			}
		} else {
			this.naturalArmorRating = i2;
			this.setEntityHealth(this.getHealth());
			this.heartsLife = this.heartsHalvesLife;
			this.damageEntity(DamageSource.generic, i2);
			this.hurtTime = this.maxHurtTime = 10;
		}

	}

	public void respawnPlayer() {
		// This is called upon game over on SP. This means that the player has died and must be respawned.
		// So we sent the right dimension to respawn the player in.
		this.mc.respawn(false, this.getSpawnDimension(), false);
	}

	public void func_6420_o() {
	}

	public void addChatMessage(String string1) {
		this.mc.ingameGUI.addChatMessageTranslate(string1);
	}

	private boolean isBlockTranslucent(int i1, int i2, int i3) {
		return this.worldObj.isBlockNormalCube(i1, i2, i3);
	}

	protected boolean pushOutOfBlocks(double d1, double d3, double d5) {
		int i7 = MathHelper.floor_double(d1);
		int i8 = MathHelper.floor_double(d3);
		int i9 = MathHelper.floor_double(d5);
		double d10 = d1 - (double)i7;
		double d12 = d5 - (double)i9;
		if(this.isBlockTranslucent(i7, i8, i9) || this.isBlockTranslucent(i7, i8 + 1, i9)) {
			boolean z14 = !this.isBlockTranslucent(i7 - 1, i8, i9) && !this.isBlockTranslucent(i7 - 1, i8 + 1, i9);
			boolean z15 = !this.isBlockTranslucent(i7 + 1, i8, i9) && !this.isBlockTranslucent(i7 + 1, i8 + 1, i9);
			boolean z16 = !this.isBlockTranslucent(i7, i8, i9 - 1) && !this.isBlockTranslucent(i7, i8 + 1, i9 - 1);
			boolean z17 = !this.isBlockTranslucent(i7, i8, i9 + 1) && !this.isBlockTranslucent(i7, i8 + 1, i9 + 1);
			byte b18 = -1;
			double d19 = 9999.0D;
			if(z14 && d10 < d19) {
				d19 = d10;
				b18 = 0;
			}

			if(z15 && 1.0D - d10 < d19) {
				d19 = 1.0D - d10;
				b18 = 1;
			}

			if(z16 && d12 < d19) {
				d19 = d12;
				b18 = 4;
			}

			if(z17 && 1.0D - d12 < d19) {
				d19 = 1.0D - d12;
				b18 = 5;
			}

			float f21 = 0.1F;
			if(b18 == 0) {
				this.motionX = (double)(-f21);
			}

			if(b18 == 1) {
				this.motionX = (double)f21;
			}

			if(b18 == 4) {
				this.motionZ = (double)(-f21);
			}

			if(b18 == 5) {
				this.motionZ = (double)f21;
			}
		}

		return false;
	}

	public void setSprinting(boolean z1) {
		super.setSprinting(z1);
		this.sprintingTicksLeft = z1 ? 600 : 0;
	}

	public void setXPStats(float f1, int i2, int i3) {
		this.experience = f1;
		this.experienceTotal = i2;
		this.experienceLevel = i3;
	}
}
