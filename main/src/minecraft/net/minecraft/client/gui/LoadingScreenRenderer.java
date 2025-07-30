package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftError;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.level.chunk.storage.IProgressUpdate;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class LoadingScreenRenderer implements IProgressUpdate {
	private String text = "";
	private Minecraft mc;
	private String title = "";
	private long millis = System.currentTimeMillis();
	private boolean isSaving = false;
	private String hint = "";
	
	public LoadingScreenRenderer(Minecraft mc) {
		this.mc = mc;
	}
	
	public void setHint(String hint) {
		this.hint = hint;
	}

	public void printText(String string1) {
		this.isSaving = false;
		this.updateTitle(string1);
	}

	public void displaySavingString(String string1) {
		this.isSaving = true;
		this.updateTitle(this.title);
	}

	public void updateTitle(String string1) {
		if(!this.mc.running) {
			if(!this.isSaving) {
				throw new MinecraftError();
			}
		} else {
			this.title = string1;
			ScaledResolution scaledResolution2 = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glOrtho(0.0D, scaledResolution2.scaledWidthD, scaledResolution2.scaledHeightD, 0.0D, 100.0D, 300.0D);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, 0.0F, -200.0F);
		}
	}

	public void displayLoadingString(String text) {
		if(!this.mc.running) {
			if(!this.isSaving) {
				throw new MinecraftError();
			}
		} else {
			this.millis = 0L;
			this.text = text;
			this.setLoadingProgress(-1);
			this.millis = 0L;
			
		}
	}

	public void setLoadingProgress(int progress) {
		if(!this.mc.running) {
			if(!this.isSaving) {
				throw new MinecraftError();
			}
		} else {
			long currentMillis = System.currentTimeMillis();
			if(currentMillis - this.millis >= 100L) {
				this.millis = currentMillis;

				ScaledResolution res = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);
				int w = res.getScaledWidth();
				int h = res.getScaledHeight();

				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glLoadIdentity();
				GL11.glOrtho(0.0D, res.scaledWidthD, res.scaledHeightD, 0.0D, 100.0D, 300.0D);
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glLoadIdentity();
				GL11.glTranslatef(0.0F, 0.0F, -200.0F);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
				
				Tessellator tes = Tessellator.instance;
				int texId = this.mc.renderEngine.getTexture("/gui/background.png");
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

				float tileSize = 32.0F;
				tes.startDrawingQuads();
				tes.setColorOpaque_I(4210752);
				tes.addVertexWithUV(0.0D, (double)h, 0.0D, 0.0D, (double)((float)h / tileSize));
				tes.addVertexWithUV((double)w, (double)h, 0.0D, (double)((float)w / tileSize), (double)((float)h / tileSize));
				tes.addVertexWithUV((double)w, 0.0D, 0.0D, (double)((float)w / tileSize), 0.0D);
				tes.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
				tes.draw();

				// Draw progress bar.
				if(progress >= 0) {
					int x = w / 2 - 100 / 2;
					int y = h / 2 + 16;
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					tes.startDrawingQuads();
					tes.setColorOpaque_I(8421504);
					tes.addVertex((double)x, (double)y, 0.0D);
					tes.addVertex((double)x, (double)(y + 2), 0.0D);
					tes.addVertex((double)(x + 100), (double)(y + 2), 0.0D);
					tes.addVertex((double)(x + 100), (double)y, 0.0D);
					tes.setColorOpaque_I(8454016);
					tes.addVertex((double)x, (double)y, 0.0D);
					tes.addVertex((double)x, (double)(y + 2), 0.0D);
					tes.addVertex((double)(x + progress), (double)(y + 2), 0.0D);
					tes.addVertex((double)(x + progress), (double)y, 0.0D);
					tes.draw();
					GL11.glEnable(GL11.GL_TEXTURE_2D);
				}

				// Draw text
				this.mc.fontRenderer.drawStringWithShadow(this.title, (w - this.mc.fontRenderer.getStringWidth(this.title)) / 2, h / 2 - 4 - 16, 0xFFFFFF);
				this.mc.fontRenderer.drawStringWithShadow(this.text, (w - this.mc.fontRenderer.getStringWidth(this.text)) / 2, h / 2 - 4 + 8, 0xFFFFFF);
				this.mc.fontRenderer.drawStringWithShadow(this.hint, (w - this.mc.fontRenderer.getStringWidth(this.hint)) / 2, h - 24, 0x00FFFF);
				Display.update();

				try {
					Thread.yield();
				} catch (Exception e) {
				}

			}
		}
	}
}
