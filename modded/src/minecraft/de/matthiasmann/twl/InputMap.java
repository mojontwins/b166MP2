package de.matthiasmann.twl;

import de.matthiasmann.twl.utils.XMLParser;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public final class InputMap {
	private static final InputMap EMPTY_MAP = new InputMap(new KeyStroke[0]);
	private final KeyStroke[] keyStrokes;

	private InputMap(KeyStroke[] keyStrokes) {
		this.keyStrokes = keyStrokes;
	}

	public String mapEvent(Event event) {
		if(event.isKeyEvent()) {
			int mappedEventModifiers = KeyStroke.convertModifier(event);
			KeyStroke[] keyStroke6 = this.keyStrokes;
			int i5 = this.keyStrokes.length;

			for(int i4 = 0; i4 < i5; ++i4) {
				KeyStroke ks = keyStroke6[i4];
				if(ks.match(event, mappedEventModifiers)) {
					return ks.getAction();
				}
			}
		}

		return null;
	}

	public InputMap addKeyStrokes(LinkedHashSet newKeyStrokes) {
		int size = newKeyStrokes.size();
		if(size == 0) {
			return this;
		} else {
			KeyStroke[] combined = new KeyStroke[this.keyStrokes.length + size];
			newKeyStrokes.toArray(combined);
			KeyStroke[] keyStroke7 = this.keyStrokes;
			int i6 = this.keyStrokes.length;

			for(int i5 = 0; i5 < i6; ++i5) {
				KeyStroke ks = keyStroke7[i5];
				if(!newKeyStrokes.contains(ks)) {
					combined[size++] = ks;
				}
			}

			return new InputMap(shrink(combined, size));
		}
	}

	public InputMap addKeyStrokes(InputMap map) {
		return map != this && map.keyStrokes.length != 0 ? (this.keyStrokes.length == 0 ? map : this.addKeyStrokes(new LinkedHashSet(Arrays.asList(map.keyStrokes)))) : this;
	}

	public InputMap addKeyStroke(KeyStroke keyStroke) {
		LinkedHashSet newKeyStrokes = new LinkedHashSet(1, 1.0F);
		newKeyStrokes.add(keyStroke);
		return this.addKeyStrokes(newKeyStrokes);
	}

	public InputMap removeKeyStrokes(Set keyStrokes) {
		if(keyStrokes.isEmpty()) {
			return this;
		} else {
			int size = 0;
			KeyStroke[] result = new KeyStroke[this.keyStrokes.length];
			KeyStroke[] keyStroke7 = this.keyStrokes;
			int i6 = this.keyStrokes.length;

			for(int i5 = 0; i5 < i6; ++i5) {
				KeyStroke ks = keyStroke7[i5];
				if(!keyStrokes.contains(ks)) {
					result[size++] = ks;
				}
			}

			return new InputMap(shrink(result, size));
		}
	}

	public KeyStroke[] getKeyStrokes() {
		return (KeyStroke[])this.keyStrokes.clone();
	}

	public static InputMap empty() {
		return EMPTY_MAP;
	}

	public static InputMap parse(URL url) throws IOException {
		try {
			XMLParser ex = new XMLParser(url);

			InputMap inputMap4;
			try {
				ex.require(0, (String)null, (String)null);
				ex.nextTag();
				ex.require(2, (String)null, "inputMapDef");
				ex.nextTag();
				LinkedHashSet keyStrokes = parseBody(ex);
				ex.require(3, (String)null, "inputMapDef");
				inputMap4 = new InputMap((KeyStroke[])keyStrokes.toArray(new KeyStroke[keyStrokes.size()]));
			} finally {
				ex.close();
			}

			return inputMap4;
		} catch (XmlPullParserException xmlPullParserException8) {
			throw (IOException)(new IOException("Can\'t parse XML")).initCause(xmlPullParserException8);
		}
	}

	public void writeXML(OutputStream os) throws IOException {
		try {
			XmlPullParserFactory ex = XmlPullParserFactory.newInstance();
			XmlSerializer serializer = ex.newSerializer();
			serializer.setOutput(os, "UTF8");
			serializer.startDocument("UTF8", Boolean.TRUE);
			serializer.text("\n");
			serializer.startTag((String)null, "inputMapDef");
			KeyStroke[] keyStroke7 = this.keyStrokes;
			int i6 = this.keyStrokes.length;

			for(int i5 = 0; i5 < i6; ++i5) {
				KeyStroke ks = keyStroke7[i5];
				serializer.text("\n    ");
				serializer.startTag((String)null, "action");
				serializer.attribute((String)null, "name", ks.getAction());
				serializer.text(ks.getStroke());
				serializer.endTag((String)null, "action");
			}

			serializer.text("\n");
			serializer.endTag((String)null, "inputMapDef");
			serializer.endDocument();
		} catch (XmlPullParserException xmlPullParserException8) {
			throw (IOException)(new IOException("Can\'t generate XML")).initCause(xmlPullParserException8);
		}
	}

	public static LinkedHashSet parseBody(XMLParser xmlp) throws XmlPullParserException, IOException {
		LinkedHashSet newStrokes = new LinkedHashSet();

		while(!xmlp.isEndTag()) {
			xmlp.require(2, (String)null, "action");
			String name = xmlp.getAttributeNotNull("name");
			String key = xmlp.nextText();

			try {
				KeyStroke ex = KeyStroke.parse(key, name);
				if(!newStrokes.add(ex)) {
					Logger.getLogger(InputMap.class.getName()).log(Level.WARNING, "Duplicate key stroke: {0}", ex.getStroke());
				}
			} catch (IllegalArgumentException illegalArgumentException5) {
				throw xmlp.error("can\'t parse Keystroke", illegalArgumentException5);
			}

			xmlp.require(3, (String)null, "action");
			xmlp.nextTag();
		}

		return newStrokes;
	}

	private static KeyStroke[] shrink(KeyStroke[] keyStrokes, int size) {
		if(size != keyStrokes.length) {
			KeyStroke[] tmp = new KeyStroke[size];
			System.arraycopy(keyStrokes, 0, tmp, 0, size);
			keyStrokes = tmp;
		}

		return keyStrokes;
	}
}
