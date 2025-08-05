package net.minecraft.world.item.map;

import net.minecraft.network.packet.Packet;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public class ItemMapBase extends Item {
	protected ItemMapBase(int id) {
		super(id);
	}

	public boolean s_func_28019_b() {
		return true;
	}

	public Packet getUpdatePacket(ItemStack theStack, World theWorld, EntityPlayer thePlayer) {
		return null;
	}
}
