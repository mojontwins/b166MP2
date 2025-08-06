package net.minecraft.client.renderer;

import java.util.Comparator;

public class GraphicsModeSorter implements Comparator<Object> {
	public int compareGraphicModes(GraphicsMode graphicsMode1, GraphicsMode graphicsMode2) {
		if(graphicsMode1.w > graphicsMode2.w) {
			return -1;
		} else if(graphicsMode1.w < graphicsMode2.w) {
			return 1;
		} else if(graphicsMode1.h > graphicsMode2.h) {
			return -1;
		} else if(graphicsMode1.h < graphicsMode2.h) {
			return 1;
		} else if(graphicsMode1.d > graphicsMode2.d) {
			return -1;
		} else if(graphicsMode1.d < graphicsMode2.d) {
			return 1;
		} else if(graphicsMode1.f > graphicsMode2.f) {
			return -1;
		} else if(graphicsMode1.f < graphicsMode2.f) {
			return 1;
		} else {
			return 0;
		}
	}
	
	@Override
	public int compare(Object arg0, Object arg1) {
		return this.compareGraphicModes(new GraphicsMode((String)arg0), new GraphicsMode((String)arg1));
	}

}
