package net.minecraft.client.gui;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.mojontwins.minecraft.commands.CommandProcessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.EntityClientPlayerMP;
import net.minecraft.world.ICommandSender;
import net.minecraft.world.level.BlockPos;
import net.minecraft.world.level.World;

public class GuiChat extends GuiScreen implements ICommandSender {
	private String field_50062_b = "";
	private int numMessages = -1;
	private boolean field_50060_d = false;
	private String field_50061_e = "";
	private String field_50059_f = "";
	private int field_50067_h = 0;
	private List<GuiPlayerInfo> field_50068_i = new ArrayList<GuiPlayerInfo>();
	private URI field_50065_j = null;
	protected GuiTextField textInput;
	private String messageText = "";
	
	public GuiChat(Minecraft mc) {
		this.mc = mc;
	}

	public GuiChat(String string1) {
		this.messageText = string1;
	}

	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.numMessages = this.mc.ingameGUI.getMessageList().size();
		this.textInput = new GuiTextField(this, this.fontRenderer, 4, this.height - 12, this.width - 4, 12);
		this.textInput.setMaxStringLength(256);
		this.textInput.setEnableBackgroundDrawing(false);
		this.textInput.setFocused(true);
		this.textInput.setText(this.messageText);
		this.textInput.setCanLoseFocus(false);
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		this.mc.ingameGUI.func_50014_d();
	}

	public void updateScreen() {
		this.textInput.updateCursorCounter();
	}

	protected void keyTyped(char keyAsc, int scanCode) {
		if(scanCode == 15) {
			if(this.mc.isMultiplayerWorld()) this.completePlayerName();
		} else {
			this.field_50060_d = false;
		}

		if(scanCode == 1) {
			this.mc.displayGuiScreen((GuiScreen)null);
		} else if(scanCode == 28) {
			String s = this.textInput.getText().trim();
			if(this.mc.isMultiplayerWorld()) {
				if(s.length() > 0 && !this.mc.lineIsCommand(s)) {					
					this.mc.thePlayer.sendChatMessage(s, this.getMouseOverCoordinates());
				}
			} else {
				CommandProcessor.withCommandSender(this);
				CommandProcessor.executeCommand(this.textInput.getText().trim(), this.mc.theWorld, null, this.mc.thePlayer, this.mc.thePlayer.getPlayerCoordinates());
			}

			this.mc.displayGuiScreen((GuiScreen)null);
		} else if(scanCode == 200) {
			this.func_50058_a(-1);
		} else if(scanCode == 208) {
			this.func_50058_a(1);
		} else if(scanCode == 201) {
			this.mc.ingameGUI.scrollMessages(19);
		} else if(scanCode == 209) {
			this.mc.ingameGUI.scrollMessages(-19);
		} else {
			this.textInput.textboxKeyTyped(keyAsc, scanCode);
		}

	}

	public void handleMouseInput() {
		super.handleMouseInput();
		int i1 = Mouse.getEventDWheel();
		if(i1 != 0) {
			if(i1 > 1) {
				i1 = 1;
			}

			if(i1 < -1) {
				i1 = -1;
			}

			if(!shiftPressed()) {
				i1 *= 7;
			}

			this.mc.ingameGUI.scrollMessages(i1);
		}

	}

	protected void mouseClicked(int i1, int i2, int i3) {
		if(i3 == 0) {
			ChatClickData chatClickData4 = this.mc.ingameGUI.func_50012_a(Mouse.getX(), Mouse.getY());
			if(chatClickData4 != null) {
				URI uRI5 = chatClickData4.func_50089_b();
				if(uRI5 != null) {
					this.field_50065_j = uRI5;
					this.mc.displayGuiScreen(new GuiChatConfirmLink(this, this, chatClickData4.func_50088_a(), 0, chatClickData4));
					return;
				}
			}
		}

		this.textInput.mouseClicked(i1, i2, i3);
		super.mouseClicked(i1, i2, i3);
	}

	public void confirmClicked(boolean z1, int i2) {
		if(i2 == 0) {
			if(z1) {
				try {
					Class<?> class3 = Class.forName("java.awt.Desktop");
					Object object4 = class3.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
					class3.getMethod("browse", new Class[]{URI.class}).invoke(object4, new Object[]{this.field_50065_j});
				} catch (Throwable throwable5) {
					throwable5.printStackTrace();
				}
			}

			this.field_50065_j = null;
			this.mc.displayGuiScreen(this);
		}

	}

	public void completePlayerName() {
		Iterator<GuiPlayerInfo> iterator2;
		GuiPlayerInfo guiPlayerInfo3;
		if(this.field_50060_d) {
			this.textInput.func_50021_a(-1);
			if(this.field_50067_h >= this.field_50068_i.size()) {
				this.field_50067_h = 0;
			}
		} else {
			int i1 = this.textInput.func_50028_c(-1);
			if(this.textInput.getCursorPosition() - i1 < 1) {
				return;
			}

			this.field_50068_i.clear();
			this.field_50061_e = this.textInput.getText().substring(i1);
			this.field_50059_f = this.field_50061_e.toLowerCase();
			iterator2 = ((EntityClientPlayerMP)this.mc.thePlayer).sendQueue.playerNames.iterator();

			while(iterator2.hasNext()) {
				guiPlayerInfo3 = (GuiPlayerInfo)iterator2.next();
				if(guiPlayerInfo3.nameStartsWith(this.field_50059_f)) {
					this.field_50068_i.add(guiPlayerInfo3);
				}
			}

			if(this.field_50068_i.size() == 0) {
				return;
			}

			this.field_50060_d = true;
			this.field_50067_h = 0;
			this.textInput.func_50020_b(i1 - this.textInput.getCursorPosition());
		}

		if(this.field_50068_i.size() > 1) {
			StringBuilder stringBuilder4 = new StringBuilder();

			for(iterator2 = this.field_50068_i.iterator(); iterator2.hasNext(); stringBuilder4.append(guiPlayerInfo3.name)) {
				guiPlayerInfo3 = (GuiPlayerInfo)iterator2.next();
				if(stringBuilder4.length() > 0) {
					stringBuilder4.append(", ");
				}
			}

			this.mc.ingameGUI.addChatMessage(stringBuilder4.toString());
		}

		this.textInput.func_50031_b(((GuiPlayerInfo)this.field_50068_i.get(this.field_50067_h++)).name);
	}

	public void func_50058_a(int i1) {
		int i2 = this.numMessages + i1;
		int i3 = this.mc.ingameGUI.getMessageList().size();
		if(i2 < 0) {
			i2 = 0;
		}

		if(i2 > i3) {
			i2 = i3;
		}

		if(i2 != this.numMessages) {
			if(i2 == i3) {
				this.numMessages = i3;
				this.textInput.setText(this.field_50062_b);
			} else {
				if(this.numMessages == i3) {
					this.field_50062_b = this.textInput.getText();
				}

				this.textInput.setText((String)this.mc.ingameGUI.getMessageList().get(i2));
				this.numMessages = i2;
			}
		}
	}

	public void drawScreen(int i1, int i2, float f3) {
		drawRect(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
		this.textInput.drawTextBox();
		super.drawScreen(i1, i2, f3);
	}

	@Override
	public void printMessage(World world, String message) {
		String[] subMessages = message.split("\n");
		
		for(String subMessage : subMessages) {
			this.mc.ingameGUI.addChatMessage(subMessage); 
		}
	}

	@Override
	public BlockPos getMouseOverCoordinates() {
		if(this.mc.objectMouseOver == null) return null;
		
		return new BlockPos().set(
				this.mc.objectMouseOver.blockX, 
				this.mc.objectMouseOver.blockY, 
				this.mc.objectMouseOver.blockZ
		);
	}
}
