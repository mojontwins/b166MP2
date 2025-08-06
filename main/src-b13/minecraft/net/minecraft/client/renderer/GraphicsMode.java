package net.minecraft.client.renderer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphicsMode {
	public int w, h, d, f;
	
	public GraphicsMode(String mode) {
		try {
			String pattern = "(\\d+)x(\\d+)x(\\d+) (\\d+)Hz";
			
			Pattern p = Pattern.compile(pattern);
			Matcher matcher = p.matcher(mode);
			
			if(matcher.find()) {
				this.w = Integer.parseInt(matcher.group(1));
				this.h = Integer.parseInt(matcher.group(2));
				this.d = Integer.parseInt(matcher.group(3));
				this.f = Integer.parseInt(matcher.group(4));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
