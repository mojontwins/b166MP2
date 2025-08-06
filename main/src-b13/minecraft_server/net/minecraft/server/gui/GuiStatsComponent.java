package net.minecraft.server.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DecimalFormat;
import javax.swing.JComponent;
import javax.swing.Timer;

import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;

public class GuiStatsComponent extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3812473911870978958L;
	private static final DecimalFormat s_field_40573_a = new DecimalFormat("########0.000");
	private int[] memoryUse = new int[256];
	private int updateCounter = 0;
	private String[] displayStrings = new String[10];
	private final MinecraftServer s_field_40572_e;

	public GuiStatsComponent(MinecraftServer minecraftServer1) {
		this.s_field_40572_e = minecraftServer1;
		this.setPreferredSize(new Dimension(356, 246));
		this.setMinimumSize(new Dimension(356, 246));
		this.setMaximumSize(new Dimension(356, 246));
		(new Timer(500, new GuiStatsListener(this))).start();
		this.setBackground(Color.BLACK);
	}

	private void updateStats() {
		long j1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.gc();
		this.displayStrings[0] = "Memory use: " + j1 / 1024L / 1024L + " mb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)";
		this.displayStrings[1] = "Threads: " + NetworkManager.numReadThreads + " + " + NetworkManager.numWriteThreads;
		this.displayStrings[2] = "Avg tick: " + s_field_40573_a.format(this.s_func_48551_a(this.s_field_40572_e.s_field_40027_f) * 1.0E-6D) + " ms";
		this.displayStrings[3] = "Avg sent: " + (int)this.s_func_48551_a(this.s_field_40572_e.s_field_48080_u) + ", Avg size: " + (int)this.s_func_48551_a(this.s_field_40572_e.s_field_48079_v);
		this.displayStrings[4] = "Avg rec: " + (int)this.s_func_48551_a(this.s_field_40572_e.s_field_48078_w) + ", Avg size: " + (int)this.s_func_48551_a(this.s_field_40572_e.s_field_48082_x);
		if(this.s_field_40572_e.worldMngr != null) {
			for(int i3 = 0; i3 < this.s_field_40572_e.worldMngr.length; ++i3) {
				this.displayStrings[5 + i3] = "Lvl " + i3 + " tick: " + s_field_40573_a.format(this.s_func_48551_a(this.s_field_40572_e.s_field_40028_g[i3]) * 1.0E-6D) + " ms";
				if(this.s_field_40572_e.worldMngr[i3] != null && this.s_field_40572_e.worldMngr[i3].chunkProviderServer != null) {
					this.displayStrings[5 + i3] = this.displayStrings[5 + i3] + ", " + this.s_field_40572_e.worldMngr[i3].chunkProviderServer.s_func_46040_d();
				}
			}
		}

		this.memoryUse[this.updateCounter++ & 255] = (int)(this.s_func_48551_a(this.s_field_40572_e.s_field_48079_v) * 100.0D / 12500.0D);
		this.repaint();
	}

	private double s_func_48551_a(long[] j1) {
		long j2 = 0L;

		for(int i4 = 0; i4 < j1.length; ++i4) {
			j2 += j1[i4];
		}

		return (double)j2 / (double)j1.length;
	}

	public void paint(Graphics graphics1) {
		graphics1.setColor(new Color(0xFFFFFF));
		graphics1.fillRect(0, 0, 356, 246);

		int i2;
		for(i2 = 0; i2 < 256; ++i2) {
			int i3 = this.memoryUse[i2 + this.updateCounter & 255];
			graphics1.setColor(new Color(i3 + 28 << 16));
			graphics1.fillRect(i2, 100 - i3, 1, i3);
		}

		graphics1.setColor(Color.BLACK);

		for(i2 = 0; i2 < this.displayStrings.length; ++i2) {
			String string4 = this.displayStrings[i2];
			if(string4 != null) {
				graphics1.drawString(string4, 32, 116 + i2 * 16);
			}
		}

	}

	static void update(GuiStatsComponent guiStatsComponent0) {
		guiStatsComponent0.updateStats();
	}
}
