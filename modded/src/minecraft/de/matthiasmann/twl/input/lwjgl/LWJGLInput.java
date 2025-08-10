package de.matthiasmann.twl.input.lwjgl;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.input.Input;
import de.matthiasmann.twl.renderer.lwjgl.RenderScale;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class LWJGLInput implements Input {
	private boolean wasActive;

	public boolean pollInput(GUI gui) {
		boolean active = Display.isActive();
		if(this.wasActive && !active) {
			this.wasActive = false;
			return false;
		} else {
			this.wasActive = active;
			if(Keyboard.isCreated()) {
				while(Keyboard.next()) {
					gui.handleKey(Keyboard.getEventKey(), Keyboard.getEventCharacter(), Keyboard.getEventKeyState());
				}
			}

			if(Mouse.isCreated()) {
				while(Mouse.next()) {
					gui.handleMouse(Mouse.getEventX() / RenderScale.scale, (gui.getHeight() - Mouse.getEventY() - 1) / RenderScale.scale, Mouse.getEventButton(), Mouse.getEventButtonState());
					int wheelDelta = Mouse.getEventDWheel();
					if(wheelDelta != 0) {
						gui.handleMouseWheel(wheelDelta / 120);
					}
				}
			}

			return true;
		}
	}
}
