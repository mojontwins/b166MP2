package de.matthiasmann.twl.renderer.lwjgl;

import de.matthiasmann.twl.renderer.CacheContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GLContext;

public class LWJGLCacheContext implements CacheContext {
	final LWJGLRenderer renderer;
	final HashMap textures;
	final HashMap fontCache;
	final ArrayList allTextures;
	boolean valid;

	protected LWJGLCacheContext(LWJGLRenderer renderer) {
		this.renderer = renderer;
		this.textures = new HashMap();
		this.fontCache = new HashMap();
		this.allTextures = new ArrayList();
		this.valid = true;
	}

	LWJGLTexture loadTexture(URL url, LWJGLTexture.Format fmt, LWJGLTexture.Filter filter) throws IOException {
		String urlString = url.toString();
		LWJGLTexture texture = (LWJGLTexture)this.textures.get(urlString);
		if(texture == null) {
			texture = this.createTexture(url, fmt, filter, (TexturePostProcessing)null);
			this.textures.put(urlString, texture);
		}

		return texture;
	}

	LWJGLTexture createTexture(URL textureUrl, LWJGLTexture.Format fmt, LWJGLTexture.Filter filter, TexturePostProcessing tpp) throws IOException {
		if(!this.valid) {
			throw new IllegalStateException("CacheContext already destroyed");
		} else {
			InputStream is = textureUrl.openStream();

			LWJGLTexture lWJGLTexture14;
			try {
				PNGDecoder ex = new PNGDecoder(is);
				fmt = ex.decideTextureFormat(fmt);
				int width = ex.getWidth();
				int height = ex.getHeight();
				int maxTextureSize = this.renderer.maxTextureSize;
				if(width > maxTextureSize || height > maxTextureSize) {
					throw new IOException("Texture size too large. Maximum supported texture by this system is " + maxTextureSize);
				}

				if(GLContext.getCapabilities().GL_EXT_abgr) {
					if(fmt == LWJGLTexture.Format.RGBA) {
						fmt = LWJGLTexture.Format.ABGR;
					}
				} else if(fmt == LWJGLTexture.Format.ABGR) {
					fmt = LWJGLTexture.Format.RGBA;
				}

				int stride = width * fmt.getPixelSize();
				ByteBuffer buf = BufferUtils.createByteBuffer(stride * height);
				ex.decode(buf, stride, fmt);
				buf.flip();
				if(tpp != null) {
					tpp.process(buf, stride, width, height, fmt);
				}

				LWJGLTexture texture = new LWJGLTexture(this.renderer, width, height, buf, fmt, filter);
				this.allTextures.add(texture);
				lWJGLTexture14 = texture;
			} catch (IOException iOException21) {
				throw (IOException)(new IOException("Unable to load PNG file: " + textureUrl)).initCause(iOException21);
			} finally {
				try {
					is.close();
				} catch (IOException iOException20) {
				}

			}

			return lWJGLTexture14;
		}
	}

	BitmapFont loadBitmapFont(URL url) throws IOException {
		String urlString = url.toString();
		BitmapFont bmFont = (BitmapFont)this.fontCache.get(urlString);
		if(bmFont == null) {
			bmFont = BitmapFont.loadFont(this.renderer, url);
			this.fontCache.put(urlString, bmFont);
		}

		return bmFont;
	}

	public boolean isValid() {
		return this.valid;
	}

	public void destroy() {
		try {
			Iterator iterator2 = this.allTextures.iterator();

			while(iterator2.hasNext()) {
				LWJGLTexture f = (LWJGLTexture)iterator2.next();
				f.destroy();
			}

			iterator2 = this.fontCache.values().iterator();

			while(iterator2.hasNext()) {
				BitmapFont f1 = (BitmapFont)iterator2.next();
				f1.destroy();
			}

		} finally {
			this.textures.clear();
			this.fontCache.clear();
			this.allTextures.clear();
			this.valid = false;
		}
	}
}
