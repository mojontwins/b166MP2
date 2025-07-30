package net.minecraft.src;

public class ItemInWorldManager {
	private WorldServer thisWorld;
	public EntityPlayer thisPlayer;
	private float field_672_d = 0.0F;
	private int field_22055_d;
	private int field_22054_g;
	private int field_22053_h;
	private int field_22052_i;
	private int field_22051_j;
	private boolean field_22050_k;
	private int field_22049_l;
	private int field_22048_m;
	private int field_22047_n;
	private int field_22046_o;

	public ItemInWorldManager(WorldServer worldServer1) {
		this.thisWorld = worldServer1;
	}

	public void func_328_a() {
		++this.field_22051_j;
		if(this.field_22050_k) {
			int i1 = this.field_22051_j - this.field_22046_o;
			int i2 = this.thisWorld.getBlockId(this.field_22049_l, this.field_22048_m, this.field_22047_n);
			if(i2 != 0) {
				Block block3 = Block.blocksList[i2];
				float f4 = block3.blockStrength(this.thisPlayer) * (float)(i1 + 1);
				if(f4 >= 1.0F) {
					this.field_22050_k = false;
					this.func_325_c(this.field_22049_l, this.field_22048_m, this.field_22047_n);
				}
			} else {
				this.field_22050_k = false;
			}
		}

	}

	public void func_324_a(int i1, int i2, int i3, int i4) {
		this.thisWorld.func_28096_a((EntityPlayer)null, i1, i2, i3, i4);
		this.field_22055_d = this.field_22051_j;
		int i5 = this.thisWorld.getBlockId(i1, i2, i3);
		if(i5 > 0) {
			Block.blocksList[i5].onBlockClicked(this.thisWorld, i1, i2, i3, this.thisPlayer);
		}

		if(i5 > 0 && Block.blocksList[i5].blockStrength(this.thisPlayer) >= 1.0F) {
			this.func_325_c(i1, i2, i3);
		} else {
			this.field_22054_g = i1;
			this.field_22053_h = i2;
			this.field_22052_i = i3;
		}

	}

	public void func_22045_b(int i1, int i2, int i3) {
		if(i1 == this.field_22054_g && i2 == this.field_22053_h && i3 == this.field_22052_i) {
			int i4 = this.field_22051_j - this.field_22055_d;
			int i5 = this.thisWorld.getBlockId(i1, i2, i3);
			if(i5 != 0) {
				Block block6 = Block.blocksList[i5];
				float f7 = block6.blockStrength(this.thisPlayer) * (float)(i4 + 1);
				if(f7 >= 0.7F) {
					this.func_325_c(i1, i2, i3);
				} else if(!this.field_22050_k) {
					this.field_22050_k = true;
					this.field_22049_l = i1;
					this.field_22048_m = i2;
					this.field_22047_n = i3;
					this.field_22046_o = this.field_22055_d;
				}
			}
		}

		this.field_672_d = 0.0F;
	}

	public boolean removeBlock(int i1, int i2, int i3) {
		Block block4 = Block.blocksList[this.thisWorld.getBlockId(i1, i2, i3)];
		int i5 = this.thisWorld.getBlockMetadata(i1, i2, i3);
		boolean z6 = this.thisWorld.setBlockWithNotify(i1, i2, i3, 0);
		if(block4 != null && z6) {
			block4.onBlockDestroyedByPlayer(this.thisWorld, i1, i2, i3, i5);
		}

		return z6;
	}

	public boolean func_325_c(int i1, int i2, int i3) {
		int i4 = this.thisWorld.getBlockId(i1, i2, i3);
		int i5 = this.thisWorld.getBlockMetadata(i1, i2, i3);
		this.thisWorld.func_28101_a(this.thisPlayer, 2001, i1, i2, i3, i4 + this.thisWorld.getBlockMetadata(i1, i2, i3) * 256);
		boolean z6 = this.removeBlock(i1, i2, i3);
		ItemStack itemStack7 = this.thisPlayer.getCurrentEquippedItem();
		if(itemStack7 != null) {
			itemStack7.func_25124_a(i4, i1, i2, i3, this.thisPlayer);
			if(itemStack7.stackSize == 0) {
				itemStack7.func_577_a(this.thisPlayer);
				this.thisPlayer.destroyCurrentEquippedItem();
			}
		}

		if(z6 && this.thisPlayer.canHarvestBlock(Block.blocksList[i4])) {
			Block.blocksList[i4].harvestBlock(this.thisWorld, this.thisPlayer, i1, i2, i3, i5);
			((EntityPlayerMP)this.thisPlayer).playerNetServerHandler.sendPacket(new Packet53BlockChange(i1, i2, i3, this.thisWorld));
		}

		return z6;
	}

	public boolean func_6154_a(EntityPlayer entityPlayer1, World world2, ItemStack itemStack3) {
		int i4 = itemStack3.stackSize;
		ItemStack itemStack5 = itemStack3.useItemRightClick(world2, entityPlayer1);
		if(itemStack5 != itemStack3 || itemStack5 != null && itemStack5.stackSize != i4) {
			entityPlayer1.inventory.mainInventory[entityPlayer1.inventory.currentItem] = itemStack5;
			if(itemStack5.stackSize == 0) {
				entityPlayer1.inventory.mainInventory[entityPlayer1.inventory.currentItem] = null;
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean activeBlockOrUseItem(EntityPlayer entityPlayer1, World world2, ItemStack itemStack3, int i4, int i5, int i6, int i7) {
		int i8 = world2.getBlockId(i4, i5, i6);
		return i8 > 0 && Block.blocksList[i8].blockActivated(world2, i4, i5, i6, entityPlayer1) ? true : (itemStack3 == null ? false : itemStack3.useItem(entityPlayer1, world2, i4, i5, i6, i7));
	}
}
