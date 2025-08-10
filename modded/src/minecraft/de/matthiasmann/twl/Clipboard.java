package de.matthiasmann.twl;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public final class Clipboard {
	public static String getClipboard() {
		try {
			java.awt.datatransfer.Clipboard ex = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable transferable = ex.getContents((Object)null);
			if(transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				return (String)transferable.getTransferData(DataFlavor.stringFlavor);
			}
		} catch (Exception exception2) {
			exception2.printStackTrace();
		}

		return "";
	}

	public static void setClipboard(String str) {
		try {
			java.awt.datatransfer.Clipboard ex = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection transferable = new StringSelection(str);
			ex.setContents(transferable, transferable);
		} catch (Exception exception3) {
			exception3.printStackTrace();
		}

	}
}
