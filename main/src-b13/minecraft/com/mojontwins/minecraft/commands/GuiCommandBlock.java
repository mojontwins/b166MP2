package com.mojontwins.minecraft.commands;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextArea;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.packet.Packet91UpdateCommandBlock;
import net.minecraft.util.StringTranslate;

public class GuiCommandBlock extends GuiScreen {
	public GuiTextArea commandTextArea;
	public GuiTextField resultTextArea;
	public final TileEntityCommandBlock theCommandBlock;
	public GuiButton doneBtn;
	public GuiButton cancelBtn;
	public GuiButton loopBtn;
	
	public GuiCommandBlock(TileEntityCommandBlock commandBlock) {
		this.theCommandBlock = commandBlock;
	}

	@Override
	public void initGui() {
		StringTranslate st = StringTranslate.getInstance();
		Keyboard.enableRepeatEvents(true);
		this.controlList.clear();
		this.controlList.add(this.doneBtn = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 152, 95, 20, st.translateKey("gui.done")));
		this.controlList.add(this.cancelBtn = new GuiButton(1, this.width / 2 + 5, this.height / 4 + 152, 95, 20, st.translateKey("gui.cancel")));
		
		this.controlList.add(this.loopBtn = new GuiButton(2, this.width / 2 - 100, this.height / 4 + 128, "Loop: " + (this.theCommandBlock.looper ? "Run while powered" : "Run only once")));
		
		this.commandTextArea = new GuiTextArea(this, this.fontRenderer, this.width / 2 - 200, 30, 400, 110, this.theCommandBlock.command);
		this.commandTextArea.setFocused(true);
		
		this.resultTextArea = new GuiTextField(this, this.fontRenderer, this.width / 2 - 200, 160, 400, 16, this.theCommandBlock.commandResults);
		this.resultTextArea.isEnabled = false;
		
		this.doneBtn.enabled = this.commandTextArea.getText().trim().length() > 0;
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		if(button.enabled) {
			if(button.id == 2) {
				this.theCommandBlock.looper = !this.theCommandBlock.looper;
				this.loopBtn.displayString =  "Loop: " + (this.theCommandBlock.looper ? "Run while powered" : "Run only once");
			} else if(button.id == 1) {
				this.mc.displayGuiScreen((GuiScreen)null);
			} else if(button.id == 0) {
				this.theCommandBlock.command = this.commandTextArea.getText().trim();
				
				if(this.mc.theWorld.isRemote) {
					Packet91UpdateCommandBlock packet = new Packet91UpdateCommandBlock(
							this.theCommandBlock.xCoord,
							this.theCommandBlock.yCoord,
							this.theCommandBlock.zCoord,
							this.theCommandBlock.command
							);
					this.mc.getSendQueue().addToSendQueue(packet);
				}

				this.mc.displayGuiScreen((GuiScreen)null);
			}

		}
	}
	
	@Override
	protected void keyTyped(char c, int code) {
		this.commandTextArea.textAreaKeyTyped(c, code);
		this.doneBtn.enabled = this.commandTextArea.getText().trim().length() > 0;
		if(code != 28 && c != 13) {
			if(code == 1) {
				this.actionPerformed(this.cancelBtn);
			}
		} else {
			//this.actionPerformed(this.doneBtn);
		}

	}
	
	@Override
	protected void mouseClicked(int x, int y, int b) {
		super.mouseClicked(x, y, b);
		this.commandTextArea.mouseClicked(x, y, b);
	}

	@Override
	public void drawScreen(int x, int y, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, "Set Command for Block", this.width / 2, 20, 16777215);
		
		this.drawCenteredString(this.fontRenderer, "Last execution message", this.width / 2, 150, 16777215);
		
		this.commandTextArea.drawTextArea();
		this.resultTextArea.drawTextBox();
		
		super.drawScreen(x, y, partialTicks);
	}
}
