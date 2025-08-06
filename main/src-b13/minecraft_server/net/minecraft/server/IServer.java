package net.minecraft.server;

public interface IServer {
	int getIntProperty(String string1, int i2);

	String getStringProperty(String string1, String string2);

	void setProperty(String string1, Object object2);

	void saveProperties();

	String getSettingsFilename();

	String getHostname();

	int getPort();

	String getMotd();

	String getVersionString();

	int playersOnline();

	int getMaxPlayers();

	String[] getPlayerNamesAsList();

	String getWorldName();

	String getPlugin();

	void s_func_40010_o();

	String handleRConCommand(String string1);

	boolean isDebuggingEnabled();

	void log(String string1);

	void logWarning(String string1);

	void logSevere(String string1);

	void logIn(String string1);
}
