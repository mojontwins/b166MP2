package net.minecraft.client.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.packet.Packet93UpdateAnimalName;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.world.entity.EntityCreature;

public class GuiGiveName extends GuiScreen {
	private static final String allowedCharacters = ChatAllowedCharacters.allowedCharacters;
	private String givenName;
	private EntityCreature theEntity;
	
	protected String screenTitle = "Name this mob:";
	
	public GuiGiveName(EntityCreature entity) {
		this.theEntity = entity;	
	}

	public void initGui() {
		this.givenName = "";
		this.controlList.clear();
		Keyboard.enableRepeatEvents(true);
		this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, "Done"));
	}
	
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		if(this.mc.theWorld.isRemote) {
			this.mc.getSendQueue().addToSendQueue(new Packet93UpdateAnimalName(this.theEntity.entityId, this.givenName));
		}

	}
	
	protected void actionPerformed(GuiButton guiButton1) {
		if(guiButton1.enabled) {
			if(guiButton1.id == 0) {
				this.theEntity.setName(this.givenName);
				this.mc.displayGuiScreen((GuiScreen)null);
			}

		}
	}
	
	protected void keyTyped(char c1, int i2) {
		if(i2 == 14 && this.givenName.length() > 0) {
			this.givenName = this.givenName.substring(0, this.givenName.length() - 1);
		}

		if(allowedCharacters.indexOf(c1) >= 0 && this.givenName.length() < 32) {
			this.givenName = this.givenName + c1;
		}

	}
	
	public void drawScreen(int i1, int i2, float f3) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 40, 0xFFFFFF);
		this.drawCenteredString(this.fontRenderer, this.givenName + "_", this.width / 2, 80, 0xFFFFFF);
		
		super.drawScreen(i1, i2, f3);
	}	
}
