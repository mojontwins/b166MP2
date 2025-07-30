package net.minecraft.src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class GuiStatsListener implements ActionListener {
	final GuiStatsComponent statsComponent;

	GuiStatsListener(GuiStatsComponent guiStatsComponent1) {
		this.statsComponent = guiStatsComponent1;
	}

	public void actionPerformed(ActionEvent actionEvent1) {
		GuiStatsComponent.update(this.statsComponent);
	}
}
