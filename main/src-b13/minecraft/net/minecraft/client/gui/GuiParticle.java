package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;

import org.lwjgl.opengl.GL11;

public class GuiParticle extends Gui {
	private List<Particle> particles = new ArrayList<Particle>();
	private Minecraft mc;

	public GuiParticle(Minecraft mc) {
		this.mc = mc;
	}

	public void update() {
		for(int i1 = 0; i1 < this.particles.size(); ++i1) {
			Particle particle2 = (Particle)this.particles.get(i1);
			particle2.preUpdate();
			particle2.update(this);
			if(particle2.isDead) {
				this.particles.remove(i1--);
			}
		}

	}

	public void draw(float f1) {
		this.mc.renderEngine.bindTexture(this.mc.renderEngine.getTexture("/gui/particles.png"));

		for(int i2 = 0; i2 < this.particles.size(); ++i2) {
			Particle particle3 = (Particle)this.particles.get(i2);
			int i4 = (int)(particle3.prevPosX + (particle3.posX - particle3.prevPosX) * (double)f1 - 4.0D);
			int i5 = (int)(particle3.prevPosY + (particle3.posY - particle3.prevPosY) * (double)f1 - 4.0D);
			float f6 = (float)(particle3.prevTintAlpha + (particle3.tintAlpha - particle3.prevTintAlpha) * (double)f1);
			float f7 = (float)(particle3.prevTintRed + (particle3.tintRed - particle3.prevTintRed) * (double)f1);
			float f8 = (float)(particle3.prevTintGreen + (particle3.tintGreen - particle3.prevTintGreen) * (double)f1);
			float f9 = (float)(particle3.prevTintBlue + (particle3.tintBlue - particle3.prevTintBlue) * (double)f1);
			GL11.glColor4f(f7, f8, f9, f6);
			this.drawTexturedModalRect(i4, i5, 40, 0, 8, 8);
		}

	}
}
