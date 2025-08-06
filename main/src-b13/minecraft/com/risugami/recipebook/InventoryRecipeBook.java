package com.risugami.recipebook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.inventory.IInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingManager;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.crafting.ShapedRecipes;
import net.minecraft.world.item.crafting.ShapelessRecipes;

public class InventoryRecipeBook implements IInventory {
	private static List<IRecipe> recipes = null;
	private List<IRecipe> filterRecipes = null;
	private final ItemStack book;
	public ItemStack filter = null;
	private int index = -1;
	private int totalPages;
	public ItemStack[][] items = new ItemStack[GuiRecipeBook.ENTRIES][];

	public InventoryRecipeBook(ItemStack item) {
		recipes = Collections.unmodifiableList(CraftingManager.getInstance().getRecipeList());
		this.book = item;
		int i = 0;
		if(this.book != null) {
			i = this.book.getItemDamage();
		}

		if(this.book != null && this.book.stackTagCompound != null && this.book.stackTagCompound.hasKey("filter")) {
			NBTTagCompound nbtTags = this.book.stackTagCompound.getCompoundTag("filter");
			this.filter = ItemStack.loadItemStackFromNBT(nbtTags);
		}

		this.update();
		this.index = this.setIndex(i);
	}

	private void update() {
		try {
			if(this.filter == null) {
				this.filterRecipes = recipes;
			} else {
				this.filterRecipes = new ArrayList<IRecipe>(recipes.size());
				Iterator<IRecipe> iterator2 = recipes.iterator();

				label42:
				while(true) {
					while(true) {
						IRecipe e;
						ItemStack[] itemArray;
						do {
							if(!iterator2.hasNext()) {
								break label42;
							}

							e = (IRecipe)iterator2.next();
							itemArray = this.getRecipeArray(e);
						} while(itemArray == null);

						for(int p = 0; p < itemArray.length; ++p) {
							if(itemArray[p] != null && this.filter.isItemEqual(itemArray[p])) {
								this.filterRecipes.add(e);
								break;
							}
						}
					}
				}
			}

			this.index = this.setIndex(0);
			this.totalPages = (this.filterRecipes.size() - 1) / GuiRecipeBook.ENTRIES + 1;
		} catch (Throwable throwable5) {
			throwable5.printStackTrace();
		}

	}

	public void decIndex() {
		this.index = this.setIndex(this.index - GuiRecipeBook.ENTRIES);
	}

	public void incIndex() {
		this.index = this.setIndex(this.index + GuiRecipeBook.ENTRIES);
	}

	public int setIndex(int i) {
		try {
			if(this.totalPages == 1) {
				i = 0;
			} else if(i < 0) {
				i = this.filterRecipes.size() - this.filterRecipes.size() % GuiRecipeBook.ENTRIES;
			} else if(i >= this.filterRecipes.size()) {
				i = 0;
			}

			this.items = new ItemStack[GuiRecipeBook.ENTRIES][];

			for(int e = 0; e < GuiRecipeBook.ENTRIES; ++e) {
				this.items[e] = new ItemStack[10];
				int ip = i + e;
				if(ip < this.filterRecipes.size()) {
					IRecipe recipe = (IRecipe)this.filterRecipes.get(ip);
					ItemStack[] itemArray = this.getRecipeArray(recipe);
					if(itemArray == null) {
						return this.setIndex(i + 1);
					}

					this.items[e] = itemArray;
				}
			}

			if(this.book != null) {
				this.book.setItemDamage(i);
			}

			return i;
		} catch (Throwable throwable6) {
			throwable6.printStackTrace();
			return 0;
		}
	}

	public int getSizeInventory() {
		return 10 * GuiRecipeBook.ENTRIES;
	}

	private ItemStack[] getRecipeArray(IRecipe recipe) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		ItemStack[] ret = new ItemStack[10];
		int o;
		if(recipe instanceof ShapedRecipes) {
			int temp = ((ShapedRecipes) recipe).getRecipeWidth();
			o = ((ShapedRecipes) recipe).getRecipeHeight();
			if(temp * o > 9) {
				return null;
			}

			ItemStack[] temp1 = ((ShapedRecipes) recipe).getRecipeItems();
			ret[0] = recipe.getRecipeOutput();
			
			for(int o1 = 0; o1 < temp1.length; ++o1) {
				int x = o1 % temp;
				int y = o1 / temp;
				ret[x + y * 3 + 1] = temp1[o1];
			}
			
		} else if(recipe instanceof ShapelessRecipes) {
			List<?> list9 = ((ShapelessRecipes) recipe).getRecipeItems();
			if(list9.size() > 9) {
				return null;
			}

			ret[0] = ((ShapelessRecipes) recipe).getRecipeOutput();

			for(o = 0; o < list9.size(); ++o) {
				ret[o + 1] = (ItemStack)list9.get(o);
			}
		}

		return ret;
	}

	public ItemStack decrStackSize(int paramInt1, int paramInt2) {
		if(paramInt1 == -1) {
			this.filter = null;
		} else {
			this.filter = this.getStackInSlot(paramInt1);
		}

		if(this.book != null) {
			if(this.filter != null) {
				NBTTagCompound nbtTags = new NBTTagCompound();
				this.filter.writeToNBT(nbtTags);
				this.book.stackTagCompound = new NBTTagCompound();
				this.book.stackTagCompound.setCompoundTag("filter", nbtTags);
			} else {
				this.book.stackTagCompound = null;
			}
		}

		this.update();
		return null;
	}

	public void setInventorySlotContents(int paramInt, ItemStack item) {
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	public String getInvName() {
		StringBuilder build = new StringBuilder();
		build.append(this.index / GuiRecipeBook.ENTRIES + 1);
		build.append('/');
		build.append(this.totalPages);
		return build.toString();
	}

	public ItemStack getStackInSlot(int paramInt) {
		return paramInt == -1 ? this.filter : this.items[paramInt / 10][paramInt % 10];
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public void onInventoryChanged() {
	}

	public ItemStack getStackInSlotOnClosing(int arg0) {
		return null;
	}

	public void closeChest() {
	}

	public void openChest() {
	}
}
