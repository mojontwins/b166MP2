package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.FontRenderer;
import net.minecraft.util.ChatAllowedCharacters;

public class GuiTextArea extends Gui {
	private final FontRenderer fontRenderer;
	private final int xPos;
	private final int yPos;
	private final int width;
	private final int height;
	private String text;
	private int maxStringLength;
	private int cursorCounter;
	private int curChar;
	public boolean isFocused = false;
	public boolean isEnabled = true;
	private GuiScreen parentGuiScreen;
	private boolean canLoseFocus = true;
	private boolean enableBackgroundDrawing = true;
	private int curRow = -1;
	private int curColX = -1;
	
	private List<String> brokenLines = new ArrayList<String>();
	
	public GuiTextArea(GuiScreen guiScreen, FontRenderer fontRenderer, int x, int y, int w, int h, String text) {
		this.parentGuiScreen = guiScreen;
		this.fontRenderer = fontRenderer;
		this.xPos = x;
		this.yPos = y;
		this.width = w;
		this.height = h;
		this.setText(text);
		this.curChar = text.length();

	}
	
	public void setText(String string1) {
		this.text = string1;
	}

	public String getText() {
		return this.text;
	}
	
	public void updateCursorCounter() {
		++this.cursorCounter;
	}
	
	public int getPosOffsetForCurRow() {
		int offset = 0;
		
		for(int i = 0; i < this.curRow; i ++) {
			offset += this.brokenLines.get(i).length();
		}
		
		return offset;
	}
	
	public void updateCurChar() {
		String tempLine = "";
		char[] curLineChars = this.brokenLines.get(this.curRow).toCharArray();
		int i;
		for(i = 0; i < curLineChars.length; i ++) {
			tempLine += curLineChars[i];
			if(this.fontRenderer.getStringWidth(tempLine) > this.curColX) {
				break;
			}
		}
		
		this.curChar = this.getPosOffsetForCurRow() + i;
	}
	
	public boolean textAreaKeyTyped(char c, int code) {
		if(this.isEnabled && this.isFocused) {
			if(c == 9) {
				// Tab
				this.parentGuiScreen.selectNextField(); 
				return true;
			}
			
			if(c == 22) {
				// Paste
				String s = GuiScreen.getClipboardString();
				if(s == null) s = "";

				this.setText(this.getText() + s);
				return true;
			}
			
			if(code == 200) {
				// UP
				if(this.curRow > 0) {
					this.curRow --;
					this.updateCurChar();
				}
				
				// Invalidate cache
				this.curRow = -1;
				return true;
			}
			
			if(code == 208) {
				// DOWN
				if(this.curRow >= 0 && this.curRow < this.brokenLines.size() - 1) {
					this.curRow ++;
					this.updateCurChar();
				}
				
				// Invalidate cache
				this.curRow = -1;
				return true;
			}
			
			if(code == 203) {
				// LEFT
				if(this.curChar > 0) this.curChar --;
				
				// Invalidate cache
				this.curRow = -1;
				return true;
			}
			
			if(code == 205) {
				// RIGHT
				if(this.curChar < this.getText().length()) this.curChar ++;
				
				// Invalidate cache
				this.curRow = -1;
				return true;
			}
			
			if(code == 14 && this.getText().length() > 0 && this.curChar > 0) {
				// Delete
				this.setText(this.getText().substring(0, this.curChar - 1) + this.getText().substring(this.curChar));
				this.curChar --;
				return true;
			}
			
			/// REFINE THIS TO ADD INSIDE THE STRING
			if(ChatAllowedCharacters.allowedCharacters.indexOf(c) >= 0 && (this.getText().length() < this.maxStringLength || this.maxStringLength == 0)) {
				this.setText(this.getText().substring(0, this.curChar) + c + this.getText().substring(this.curChar));
				this.curChar ++;
				return true;
			}
		}
		
		return false;
	}
	
	public void mouseClicked(int x, int y, int i3) {
		boolean inside = this.isEnabled && x >= this.xPos && x < this.xPos + this.width && y >= this.yPos && y < this.yPos + this.height;
		if (this.canLoseFocus) {
			this.setFocused(inside);
		}
	}
	
	public void setFocused(boolean focused) {
		if(focused && !this.isFocused) {
			this.cursorCounter = 0;
		}

		this.isFocused = focused;
	}
	
	public void drawTextArea() {
		// This is a bit more complex than text field. It should print the text multi-line
		// This should also account for the fact that the cursor may not be at the end
		
		if (this.isEnableBackgroundDrawing()) {
			Gui.drawRect(this.xPos - 1, this.yPos - 1, this.xPos + this.width + 1, this.yPos + this.height + 1, -6250336);
			Gui.drawRect(this.xPos, this.yPos, this.xPos + this.width, this.yPos + this.height, 0xFF000000);
		}

		int x0 = this.enableBackgroundDrawing ? this.xPos + 4 : this.xPos;
		int y0 = this.enableBackgroundDrawing ? this.yPos + 4 : this.yPos;
		
		int w = this.enableBackgroundDrawing ? this.width - 8 : this.width;
		int h = this.enableBackgroundDrawing ? this.height - 8 : this.height;
		
		int c1 = this.enableBackgroundDrawing ? 0xE0E0E0 : 0x222222;
		int c2 = this.enableBackgroundDrawing ? 0x707070 : 0x222222;
	
		String curText = this.getText() + "\n";
		String curStr = "";
		this.brokenLines.clear();
		char[] textChars = curText.toCharArray();
		
		int y = y0;
		int color = this.isEnabled ? c1 : c2;
		int row = 0;
		
		for(int i = 0; i < textChars.length; i ++) {
			char c = textChars [i];
			
			if(this.isFocused && this.cursorCounter / 6 % 2 == 0 && i == this.curChar) {
				this.curRow = row;
				this.curColX = this.fontRenderer.getStringWidth(curStr);
				
				this.fontRenderer.drawString("_", x0 + this.curColX, y, color);				
			}
			
			// Does current plus the new char fit?
			if(this.fontRenderer.getStringWidth(curStr + c) > w || c == '\n') {
				this.fontRenderer.drawString(curStr, x0, y, color);				
				this.brokenLines.add(curStr);
				if(c != '\n') curStr = "" + c;
				y += 10;
				if (y + 8 > y0 + h) break;
				row ++;
			} else {
				curStr += c;
			}
		}		
		
	}

	public void setMaxStringLength(int i1) {
		this.maxStringLength = i1;
	}

	public void setCanLoseFocus(boolean b) {
		this.canLoseFocus = b;
	}
	
	public boolean isEnableBackgroundDrawing() {
		return this.enableBackgroundDrawing;
	}

	public void setEnableBackgroundDrawing(boolean z1) {
		this.enableBackgroundDrawing = z1;
	}
}
