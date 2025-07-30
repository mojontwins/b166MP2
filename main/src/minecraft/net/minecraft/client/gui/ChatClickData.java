package net.minecraft.client.gui;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.renderer.FontRenderer;

public class ChatClickData {
	public static final Pattern field_50097_a = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,3})(/\\S*)?$");
	private final ChatLine field_50096_c;
	private final String field_50091_f;
	private final String field_50092_g;

	public ChatClickData(FontRenderer fontRenderer1, ChatLine chatLine2, int i3, int i4) {
		this.field_50096_c = chatLine2;
		this.field_50091_f = fontRenderer1.func_50107_a(chatLine2.message, i3);
		this.field_50092_g = this.func_50090_c();
	}

	public String func_50088_a() {
		return this.field_50092_g;
	}

	public URI func_50089_b() {
		String string1 = this.func_50088_a();
		if(string1 == null) {
			return null;
		} else {
			Matcher matcher2 = field_50097_a.matcher(string1);
			if(matcher2.matches()) {
				try {
					String string3 = matcher2.group(0);
					if(matcher2.group(1) == null) {
						string3 = "http://" + string3;
					}

					return new URI(string3);
				} catch (URISyntaxException uRISyntaxException4) {
					Logger.getLogger("Minecraft").log(Level.SEVERE, "Couldn\'t create URI from chat", uRISyntaxException4);
				}
			}

			return null;
		}
	}

	private String func_50090_c() {
		int i1 = this.field_50091_f.lastIndexOf(" ", this.field_50091_f.length()) + 1;
		if(i1 < 0) {
			i1 = 0;
		}

		int i2 = this.field_50096_c.message.indexOf(" ", i1);
		if(i2 < 0) {
			i2 = this.field_50096_c.message.length();
		}

		return FontRenderer.removeColorCodes(this.field_50096_c.message.substring(i1, i2));
	}
}
