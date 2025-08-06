package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.network.packet.Packet19EntityAction;
import net.minecraft.util.StringTranslate;

public class GuiSleepMP extends GuiChat {
	public GuiSleepMP(Minecraft mc) {
		super(mc);
		// TODO Auto-generated constructor stub
	}

	public void initGui() {
		super.initGui();
		StringTranslate stringTranslate1 = StringTranslate.getInstance();
		this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height - 40, stringTranslate1.translateKey("multiplayer.stopSleeping")));
	}

	protected void keyTyped(char c1, int i2) {
		if(i2 == 1) {
			this.wakeEntity();
		} else if(i2 == 28) {
			String string3 = this.textInput.getText().trim();
			if(string3.length() > 0) {
				this.mc.thePlayer.sendChatMessage(string3, null);
			}

			this.textInput.setText("");
			this.mc.ingameGUI.func_50014_d();
		} else {
			super.keyTyped(c1, i2);
		}

	}

	protected void actionPerformed(GuiButton guiButton1) {
		if(guiButton1.id == 1) {
			this.wakeEntity();
		} else {
			super.actionPerformed(guiButton1);
		}

	}

	private void wakeEntity() {
		if(this.mc.thePlayer instanceof EntityClientPlayerMP) {
			NetClientHandler netClientHandler1 = ((EntityClientPlayerMP)this.mc.thePlayer).sendQueue;
			netClientHandler1.addToSendQueue(new Packet19EntityAction(this.mc.thePlayer, 3));
		}

	}
}
