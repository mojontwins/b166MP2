package net.minecraft.server.gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

final class ConsoleLogFormatter extends Formatter {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public String format(LogRecord logRecord1) {
		StringBuilder stringBuilder2 = new StringBuilder();
		stringBuilder2.append(this.dateFormat.format(logRecord1.getMillis()));
		Level level3 = logRecord1.getLevel();
		if(level3 == Level.FINEST) {
			stringBuilder2.append(" [FINEST] ");
		} else if(level3 == Level.FINER) {
			stringBuilder2.append(" [FINER] ");
		} else if(level3 == Level.FINE) {
			stringBuilder2.append(" [FINE] ");
		} else if(level3 == Level.INFO) {
			stringBuilder2.append(" [INFO] ");
		} else if(level3 == Level.WARNING) {
			stringBuilder2.append(" [WARNING] ");
		} else if(level3 == Level.SEVERE) {
			stringBuilder2.append(" [SEVERE] ");
		} else if(level3 == Level.SEVERE) {
			stringBuilder2.append(" [" + level3.getLocalizedName() + "] ");
		}

		stringBuilder2.append(logRecord1.getMessage());
		stringBuilder2.append('\n');
		Throwable throwable4 = logRecord1.getThrown();
		if(throwable4 != null) {
			StringWriter stringWriter5 = new StringWriter();
			throwable4.printStackTrace(new PrintWriter(stringWriter5));
			stringBuilder2.append(stringWriter5.toString());
		}

		return stringBuilder2.toString();
	}
}
