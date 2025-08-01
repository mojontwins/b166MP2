package net.minecraft.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertyManager {
	public static Logger logger = Logger.getLogger("Minecraft");
	private Properties serverProperties = new Properties();
	private File serverPropertiesFile;

	public PropertyManager(File file1) {
		this.serverPropertiesFile = file1;
		if(file1.exists()) {
			try {
				this.serverProperties.load(new FileInputStream(file1));
			} catch (Exception exception3) {
				logger.log(Level.WARNING, "Failed to load " + file1, exception3);
				this.generateNewProperties();
			}
		} else {
			logger.log(Level.WARNING, file1 + " does not exist");
			this.generateNewProperties();
		}

	}

	public void generateNewProperties() {
		logger.log(Level.INFO, "Generating new properties file");
		this.saveProperties();
	}

	public void saveProperties() {
		try {
			this.serverProperties.store(new FileOutputStream(this.serverPropertiesFile), "Minecraft server properties");
		} catch (Exception exception2) {
			logger.log(Level.WARNING, "Failed to save " + this.serverPropertiesFile, exception2);
			this.generateNewProperties();
		}

	}

	public File getPropertiesFile() {
		return this.serverPropertiesFile;
	}

	public String getStringProperty(String string1, String string2) {
		if(!this.serverProperties.containsKey(string1)) {
			this.serverProperties.setProperty(string1, string2);
			this.saveProperties();
		}

		return this.serverProperties.getProperty(string1, string2);
	}

	public int getIntProperty(String string1, int i2) {
		try {
			return Integer.parseInt(this.getStringProperty(string1, "" + i2));
		} catch (Exception exception4) {
			this.serverProperties.setProperty(string1, "" + i2);
			return i2;
		}
	}

	public boolean getBooleanProperty(String string1, boolean z2) {
		try {
			return Boolean.parseBoolean(this.getStringProperty(string1, "" + z2));
		} catch (Exception exception4) {
			this.serverProperties.setProperty(string1, "" + z2);
			return z2;
		}
	}

	public void setProperty(String string1, Object object2) {
		this.serverProperties.setProperty(string1, "" + object2);
	}

	public void setProperty(String string1, boolean z2) {
		this.serverProperties.setProperty(string1, "" + z2);
		this.saveProperties();
	}
}
