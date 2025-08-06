package net.minecraft.client.renderer;

import java.awt.Component;
import java.nio.IntBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

public class MouseHelper {
	private Component windowComponent;
	public int deltaX;
	public int deltaY;
	
	public MouseHelper(Component component1) {
		this.windowComponent = component1;
		IntBuffer intBuffer2 = GLAllocation.createDirectIntBuffer(1);
		intBuffer2.put(0);
		intBuffer2.flip();
		IntBuffer intBuffer3 = GLAllocation.createDirectIntBuffer(1024);

		try {
			new Cursor(32, 32, 16, 16, 1, intBuffer3, intBuffer2);
		} catch (LWJGLException lWJGLException5) {
			lWJGLException5.printStackTrace();
		}

	}

	public void grabMouseCursor() {
		Mouse.setGrabbed(true);
		this.deltaX = 0;
		this.deltaY = 0;
	}

	public void ungrabMouseCursor() {
		Mouse.setCursorPosition(this.windowComponent.getWidth() / 2, this.windowComponent.getHeight() / 2);
		Mouse.setGrabbed(false);
	}

	public void mouseXYChange() {
		this.deltaX = Mouse.getDX();
		this.deltaY = Mouse.getDY();
	}
}
