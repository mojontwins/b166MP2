package net.minecraft.server.gui;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

class ServerGuiFocusAdapter extends FocusAdapter {
	final ServerGUI mcServerGui;

	ServerGuiFocusAdapter(ServerGUI serverGUI1) {
		this.mcServerGui = serverGUI1;
	}

	public void focusGained(FocusEvent focusEvent1) {
	}
}
