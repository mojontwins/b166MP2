package net.minecraft.server.gui;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import javax.swing.JTextArea;

public class GuiLogOutputHandler extends Handler {
	private int[] s_field_998_b = new int[1024];
	private int s_field_1001_c = 0;
	Formatter s_field_999_a = new GuiLogFormatter(this);
	private JTextArea s_field_1000_d;

	public GuiLogOutputHandler(JTextArea jTextArea1) {
		this.setFormatter(this.s_field_999_a);
		this.s_field_1000_d = jTextArea1;
	}

	public void close() {
	}

	public void flush() {
	}

	public void publish(LogRecord logRecord1) {
		int i2 = this.s_field_1000_d.getDocument().getLength();
		this.s_field_1000_d.append(this.s_field_999_a.format(logRecord1));
		this.s_field_1000_d.setCaretPosition(this.s_field_1000_d.getDocument().getLength());
		int i3 = this.s_field_1000_d.getDocument().getLength() - i2;
		if(this.s_field_998_b[this.s_field_1001_c] != 0) {
			this.s_field_1000_d.replaceRange("", 0, this.s_field_998_b[this.s_field_1001_c]);
		}

		this.s_field_998_b[this.s_field_1001_c] = i3;
		this.s_field_1001_c = (this.s_field_1001_c + 1) % 1024;
	}
}
