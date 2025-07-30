package net.minecraft.src;

import java.util.Vector;
import javax.swing.JList;

import net.minecraft.server.MinecraftServer;

public class PlayerListBox extends JList implements IUpdatePlayerListBox {
	private MinecraftServer mcServer;
	private int updateCounter = 0;

	public PlayerListBox(MinecraftServer minecraftServer1) {
		this.mcServer = minecraftServer1;
		minecraftServer1.func_6022_a(this);
	}

	public void update() {
		if(this.updateCounter++ % 20 == 0) {
			Vector vector1 = new Vector();

			for(int i2 = 0; i2 < this.mcServer.configManager.playerEntities.size(); ++i2) {
				vector1.add(((EntityPlayerMP)this.mcServer.configManager.playerEntities.get(i2)).username);
			}

			this.setListData(vector1);
		}

	}
}
