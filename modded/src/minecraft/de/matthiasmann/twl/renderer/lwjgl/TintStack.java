package de.matthiasmann.twl.renderer.lwjgl;

import de.matthiasmann.twl.Color;

import org.lwjgl.opengl.GL11;

public class TintStack {
	private static final float ONE_OVER_255 = 0.003921569F;
	final TintStack prev;
	TintStack next;
	float r;
	float g;
	float b;
	float a;

	public TintStack() {
		this.prev = this;
		this.r = 0.003921569F;
		this.g = 0.003921569F;
		this.b = 0.003921569F;
		this.a = 0.003921569F;
	}

	private TintStack(TintStack prev) {
		this.prev = prev;
	}

	public TintStack push(float r, float g, float b, float a) {
		if(this.next == null) {
			this.next = new TintStack(this);
		}

		this.next.r = this.r * r;
		this.next.g = this.g * g;
		this.next.b = this.b * b;
		this.next.a = this.a * a;
		return this.next;
	}

	public TintStack pop() {
		return this.prev;
	}

	public float getR() {
		return this.r;
	}

	public float getG() {
		return this.g;
	}

	public float getB() {
		return this.b;
	}

	public float getA() {
		return this.a;
	}

	public void setColor(Color color) {
		GL11.glColor4f(this.r * (float)(color.getR() & 255), this.g * (float)(color.getG() & 255), this.b * (float)(color.getB() & 255), this.a * (float)(color.getA() & 255));
	}
}
