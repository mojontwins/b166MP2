package net.minecraft.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeMap;

public class StringTranslate {
	private static StringTranslate instance = new StringTranslate();
	private Properties translateTable = new Properties();
	private TreeMap<String, String> languageList;
	private String currentLanguage;
	private boolean isUnicode;

	private StringTranslate() {
		this.loadLanguageList();
		this.setLanguage("en_US");
	}

	public static StringTranslate getInstance() {
		return instance;
	}

	private void loadLanguageList() {
		TreeMap<String, String> treeMap1 = new TreeMap<String, String>();

		try {
			BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(StringTranslate.class.getResourceAsStream("/lang/languages.txt"), "UTF-8"));

			for(String string3 = bufferedReader2.readLine(); string3 != null; string3 = bufferedReader2.readLine()) {
				String[] string4 = string3.split("=");
				if(string4 != null && string4.length == 2) {
					treeMap1.put(string4[0], string4[1]);
				}
			}
		} catch (Exception e) {
			treeMap1.put("en_US", "English (US)");
			return;
		}

		this.languageList = treeMap1;
	}

	public TreeMap<String, String> getLanguageList() {
		return this.languageList;
	}

	private void loadLanguage(Properties properties1, String string2) throws IOException {
		BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(StringTranslate.class.getResourceAsStream("/lang/" + string2 + ".lang"), "UTF-8"));

		for(String string4 = bufferedReader3.readLine(); string4 != null; string4 = bufferedReader3.readLine()) {
			string4 = string4.trim();
			if(!string4.startsWith("#")) {
				String[] string5 = string4.split("=");
				if(string5 != null && string5.length == 2) {
					properties1.setProperty(string5[0], string5[1]);
				}
			}
		}

	}

	public void setLanguage(String string1) {
		if(!string1.equals(this.currentLanguage)) {
			Properties properties2 = new Properties();

			try {
				this.loadLanguage(properties2, "en_US");
			} catch (Exception e) {
			}

			this.isUnicode = false;
			if(!"en_US".equals(string1)) {
				try {
					this.loadLanguage(properties2, string1);
					Enumeration<?> enumeration3 = properties2.propertyNames();

					label47:
					while(true) {
						while(true) {
							Object object5;
							do {
								if(!enumeration3.hasMoreElements() || this.isUnicode) {
									break label47;
								}

								Object object4 = enumeration3.nextElement();
								object5 = properties2.get(object4);
							} while(object5 == null);

							String string6 = object5.toString();

							for(int i7 = 0; i7 < string6.length(); ++i7) {
								if(string6.charAt(i7) >= 256) {
									this.isUnicode = true;
									break;
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}

			this.currentLanguage = string1;
			this.translateTable = properties2;
		}
	}

	public String getCurrentLanguage() {
		return this.currentLanguage;
	}

	public boolean isUnicode() {
		return this.isUnicode;
	}

	public String translateKey(String string1) {
		return this.translateTable.getProperty(string1, string1);
	}

	public String translateKeyFormat(String string1, Object... object2) {
		String string3 = this.translateTable.getProperty(string1, string1);
		return String.format(string3, object2);
	}

	public String translateNamedKey(String string1) {
		return this.translateTable.getProperty(string1 + ".name", string1 + ".name");
	}

	public static boolean isBidrectional(String string0) {
		return "ar_SA".equals(string0) || "he_IL".equals(string0);
	}
}
