package net.minecraft.src;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import javax.swing.JTextArea;

public class GuiLogOutputHandler extends Handler {
	private int[] field_998_b = new int[1024];
	private int field_1001_c = 0;
	Formatter field_999_a = new GuiLogFormatter(this);
	private JTextArea field_1000_d;

	public GuiLogOutputHandler(JTextArea jTextArea1) {
		this.setFormatter(this.field_999_a);
		this.field_1000_d = jTextArea1;
	}

	public void close() {
	}

	public void flush() {
	}

	public void publish(LogRecord logRecord1) {
		int i2 = this.field_1000_d.getDocument().getLength();
		this.field_1000_d.append(this.field_999_a.format(logRecord1));
		this.field_1000_d.setCaretPosition(this.field_1000_d.getDocument().getLength());
		int i3 = this.field_1000_d.getDocument().getLength() - i2;
		if(this.field_998_b[this.field_1001_c] != 0) {
			this.field_1000_d.replaceRange("", 0, this.field_998_b[this.field_1001_c]);
		}

		this.field_998_b[this.field_1001_c] = i3;
		this.field_1001_c = (this.field_1001_c + 1) % 1024;
	}
}
