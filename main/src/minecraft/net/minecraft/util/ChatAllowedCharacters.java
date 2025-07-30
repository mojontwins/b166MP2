package net.minecraft.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ChatAllowedCharacters {
	public static final String allowedCharacters = getAllowedCharacters();
	public static final char[] allowedCharactersArray = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':', ' '};

	private static String getAllowedCharacters() {
		String string0 = "";

		try {
			BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(ChatAllowedCharacters.class.getResourceAsStream("/font.txt"), "UTF-8"));
			String string2 = "";

			while((string2 = bufferedReader1.readLine()) != null) {
				if(!string2.startsWith("#")) {
					string0 = string0 + string2;
				}
			}

			bufferedReader1.close();
		} catch (Exception exception3) {
		}

		return string0;
	}

	public static final boolean isAllowedCharacter(char c0) {
		return c0 != 167 && (allowedCharacters.indexOf(c0) >= 0 || c0 >= 32);
	}

	public static String func_52019_a(String string0) {
		StringBuilder stringBuilder1 = new StringBuilder();
		char[] c2 = string0.toCharArray();
		int i3 = c2.length;

		for(int i4 = 0; i4 < i3; ++i4) {
			char c5 = c2[i4];
			if(isAllowedCharacter(c5)) {
				stringBuilder1.append(c5);
			}
		}

		return stringBuilder1.toString();
	}
}
