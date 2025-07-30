package net.minecraft.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum EnumChatFormatting {
	BLACK('0'),
	DARK_BLUE('1'),
	DARK_GREEN('2'),
	DARK_AQUA('3'),
	DARK_RED('4'),
	DARK_PURPLE('5'),
	GOLD('6'),
	GRAY('7'),
	DARK_GRAY('8'),
	BLUE('9'),
	GREEN('a'),
	AQUA('b'),
	RED('c'),
	LIGHT_PURPLE('d'),
	YELLOW('e'),
	WHITE('f'),
	OBFUSCATED('k', true),
	BOLD('l', true),
	STRIKETHROUGH('m', true),
	UNDERLINE('n', true),
	ITALIC('o', true),
	RESET('r');

	private static final Map<Character, EnumChatFormatting> field_96321_w = new HashMap<Character, EnumChatFormatting>();
	private static final Map<String, EnumChatFormatting> field_96331_x = new HashMap<String, EnumChatFormatting>();
	private final char field_96329_z;
	private final boolean field_96303_A;
	private final String field_96304_B;

	private EnumChatFormatting(char var3) {
		this(var3, false);
	}

	private EnumChatFormatting(char var3, boolean var4) {
		this.field_96329_z = var3;
		this.field_96303_A = var4;
		this.field_96304_B = "\u00a7" + var3;
	}

	public char func_96298_a() {
		return this.field_96329_z;
	}

	public boolean func_96301_b() {
		return this.field_96303_A;
	}

	public boolean func_96302_c() {
		return !this.field_96303_A && this != RESET;
	}

	public String func_96297_d() {
		return this.name().toLowerCase();
	}

	public String toString() {
		return this.field_96304_B;
	}

	public static EnumChatFormatting func_96300_b(String var0) {
		return var0 == null ? null : (EnumChatFormatting)field_96331_x.get(var0.toLowerCase());
	}

	public static Collection<String> func_96296_a(boolean var0, boolean var1) {
		ArrayList<String> var2 = new ArrayList<String>();
		EnumChatFormatting[] var3 = values();
		int var4 = var3.length;

		for(int var5 = 0; var5 < var4; ++var5) {
			EnumChatFormatting var6 = var3[var5];
			if((!var6.func_96302_c() || var0) && (!var6.func_96301_b() || var1)) {
				var2.add(var6.func_96297_d());
			}
		}

		return var2;
	}

	static {
		EnumChatFormatting[] var0 = values();
		int var1 = var0.length;

		for(int var2 = 0; var2 < var1; ++var2) {
			EnumChatFormatting var3 = var0[var2];
			field_96321_w.put(Character.valueOf(var3.func_96298_a()), var3);
			field_96331_x.put(var3.func_96297_d(), var3);
		}

	}
}
