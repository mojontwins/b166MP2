package de.matthiasmann.twl.renderer.lwjgl;

import java.nio.ByteBuffer;

public interface TexturePostProcessing {
	void process(ByteBuffer byteBuffer1, int i2, int i3, int i4, LWJGLTexture.Format lWJGLTexture$Format5);
}
