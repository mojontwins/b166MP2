package net.minecraft.world.item;

import net.minecraft.world.GameRules;
import net.minecraft.world.entity.EnumAction;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;

public class ItemFood extends Item {
	private final int healAmount;
	private final float saturationModifier;
	private final boolean isWolfsFavoriteMeat;
	private boolean alwaysEdible;
	
	public ItemFood(int i1, int i2, float f3, boolean z4) {
		super(i1);
		this.healAmount = i2;
		this.isWolfsFavoriteMeat = z4;
		this.saturationModifier = f3;
		
		this.displayOnCreativeTab = CreativeTabs.tabFood;
		
		if (!GameRules.boolRule("stackableFood")) this.setMaxStackSize(1);
	}

	public ItemFood(int i1, int i2, boolean z3) {
		this(i1, i2, 0.6F, z3);
	}

	public ItemStack onFoodEaten(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		--itemStack.stackSize;
		
		if (GameRules.boolRule("enableHunger")) {
			entityPlayer.getFoodStats().addStats(this);
		} else {
			int heal = this.healAmount;
			entityPlayer.heal(heal);
		}
		
		world.playSoundAtEntity(entityPlayer, "random.burp", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		
		// TODO: A means of adding potion effects to food
		/*
		if(!world.isRemote && this.potionId > 0 && world.rand.nextFloat() < this.potionEffectProbability) {
			entityPlayer.addPotionEffect(new PotionEffect(this.potionId, this.potionDuration * 20, this.potionAmplifier));
		}
		*/

		return itemStack;
	}

	public int getMaxItemUseDuration(ItemStack itemStack1) {
		return 32;
	}

	public EnumAction getItemUseAction(ItemStack itemStack1) {
		return EnumAction.eat;
	}

	public ItemStack onItemRightClick(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
		if(entityPlayer3.canEat(this.alwaysEdible)) {
			entityPlayer3.setItemInUse(itemStack1, this.getMaxItemUseDuration(itemStack1));
		}

		return itemStack1;
	}

	public int getHealAmount() {
		return this.healAmount;
	}

	public float getSaturationModifier() {
		return this.saturationModifier;
	}

	public boolean isWolfsFavoriteMeat() {
		return this.isWolfsFavoriteMeat;
	}

	public ItemFood setPotionEffect(int i1, int i2, int i3, float f4) {
		return this;
	}

	public ItemFood setAlwaysEdible() {
		this.alwaysEdible = true;
		return this;
	}

	public Item setItemName(String string1) {
		return super.setItemName(string1);
	}
}
