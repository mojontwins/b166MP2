package de.matthiasmann.twl.theme;

import de.matthiasmann.twl.Alignment;
import de.matthiasmann.twl.Border;
import de.matthiasmann.twl.DebugHook;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.Dimension;
import de.matthiasmann.twl.InputMap;
import de.matthiasmann.twl.PositionAnimatedPanel;
import de.matthiasmann.twl.ThemeInfo;
import de.matthiasmann.twl.renderer.CacheContext;
import de.matthiasmann.twl.renderer.Font;
import de.matthiasmann.twl.renderer.FontParameter;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.Renderer;
import de.matthiasmann.twl.utils.AbstractMathInterpreter;
import de.matthiasmann.twl.utils.StateExpression;
import de.matthiasmann.twl.utils.TextUtil;
import de.matthiasmann.twl.utils.XMLParser;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

public class ThemeManager {
	private static final HashMap enums = new HashMap();
	static final Object NULL;
	private final Renderer renderer;
	private final CacheContext cacheContext;
	private final ImageManager imageManager;
	private final HashMap fonts;
	private final HashMap themes;
	private final HashMap inputMaps;
	final HashMap constants;
	private final ThemeManager.MathInterpreter mathInterpreter;
	private Font defaultFont;
	private Font firstFont;
	final ParameterMapImpl emptyMap;
	final ParameterListImpl emptyList;

	static {
		registerEnumType("alignment", Alignment.class);
		registerEnumType("direction", PositionAnimatedPanel.Direction.class);
		NULL = new Object();
	}

	private ThemeManager(Renderer renderer, CacheContext cacheContext) throws XmlPullParserException, IOException {
		this.renderer = renderer;
		this.cacheContext = cacheContext;
		this.imageManager = new ImageManager(renderer);
		this.fonts = new HashMap();
		this.themes = new HashMap();
		this.inputMaps = new HashMap();
		this.constants = new HashMap();
		this.emptyMap = new ParameterMapImpl(this, (ThemeInfoImpl)null);
		this.emptyList = new ParameterListImpl(this, (ThemeInfoImpl)null);
		this.mathInterpreter = new ThemeManager.MathInterpreter();
	}

	public CacheContext getCacheContext() {
		return this.cacheContext;
	}

	public void destroy() {
		Iterator iterator2 = this.fonts.values().iterator();

		while(iterator2.hasNext()) {
			Font font = (Font)iterator2.next();
			font.destroy();
		}

		this.cacheContext.destroy();
	}

	public Font getDefaultFont() {
		return this.defaultFont;
	}

	public static ThemeManager createThemeManager(URL url, Renderer renderer) throws IOException {
		if(url == null) {
			throw new IllegalArgumentException("url is null");
		} else if(renderer == null) {
			throw new IllegalArgumentException("renderer is null");
		} else {
			return createThemeManager(url, renderer, renderer.createNewCacheContext());
		}
	}

	public static ThemeManager createThemeManager(URL url, Renderer renderer, CacheContext cacheContext) throws IOException {
		if(url == null) {
			throw new IllegalArgumentException("url is null");
		} else if(renderer == null) {
			throw new IllegalArgumentException("renderer is null");
		} else if(cacheContext == null) {
			throw new IllegalArgumentException("cacheContext is null");
		} else {
			try {
				renderer.setActiveCacheContext(cacheContext);
				ThemeManager ex = new ThemeManager(renderer, cacheContext);
				ex.insertDefaultConstants();
				ex.parseThemeFile(url);
				if(ex.defaultFont == null) {
					ex.defaultFont = ex.firstFont;
				}

				return ex;
			} catch (XmlPullParserException xmlPullParserException4) {
				throw (IOException)(new IOException()).initCause(xmlPullParserException4);
			}
		}
	}

	public static void registerEnumType(String name, Class enumClazz) {
		if(!enumClazz.isEnum()) {
			throw new IllegalArgumentException("not an enum class");
		} else {
			Class curClazz = (Class)enums.get(name);
			if(curClazz != null && curClazz != enumClazz) {
				throw new IllegalArgumentException("Enum type name \"" + name + "\" is already in use by " + curClazz);
			} else {
				enums.put(name, enumClazz);
			}
		}
	}

	public ThemeInfo findThemeInfo(String themePath) {
		return this.findThemeInfo(themePath, true);
	}

	private ThemeInfo findThemeInfo(String themePath, boolean warn) {
		int start = TextUtil.indexOf((String)themePath, '.', 0);

		ThemeInfo info;
		int next;
		for(info = (ThemeInfo)this.themes.get(themePath.substring(0, start)); info != null; start = next) {
			++start;
			if(start >= themePath.length()) {
				break;
			}

			next = TextUtil.indexOf(themePath, '.', start);
			info = info.getChildTheme(themePath.substring(start, next));
		}

		if(info == null && warn) {
			DebugHook.getDebugHook().missingTheme(themePath);
		}

		return info;
	}

	public Image getImageNoWarning(String name) {
		return this.imageManager.getImage(name);
	}

	public Image getImage(String name) {
		Image img = this.imageManager.getImage(name);
		if(img == null) {
			DebugHook.getDebugHook().missingImage(name);
		}

		return img;
	}

	public Object getCursor(String name) {
		return this.imageManager.getCursor(name);
	}

	public final void insertConstant(String name, Object value) {
		if(this.constants.containsKey(name)) {
			throw new IllegalArgumentException("Constant \'" + name + "\' already declared");
		} else {
			if(value == null) {
				value = NULL;
			}

			this.constants.put(name, value);
		}
	}

	protected void insertDefaultConstants() {
		this.insertConstant("SINGLE_COLUMN", -1);
	}

	private void parseThemeFile(URL url) throws XmlPullParserException, IOException {
		try {
			XMLParser ex = new XMLParser(url);

			try {
				ex.setLoggerName(ThemeManager.class.getName());
				ex.require(0, (String)null, (String)null);
				ex.nextTag();
				this.parseThemeFile(ex, url);
			} finally {
				ex.close();
			}

		} catch (Exception exception7) {
			throw (IOException)(new IOException("while parsing Theme XML: " + url)).initCause(exception7);
		}
	}

	private void parseThemeFile(XMLParser xmlp, URL baseUrl) throws XmlPullParserException, IOException {
		xmlp.require(2, (String)null, "themes");
		xmlp.nextTag();

		while(!xmlp.isEndTag()) {
			xmlp.require(2, (String)null, (String)null);
			String tagName = xmlp.getName();
			if(!"images".equals(tagName) && !"textures".equals(tagName)) {
				String name;
				if("include".equals(tagName)) {
					name = xmlp.getAttributeNotNull("filename");
					this.parseThemeFile(new URL(baseUrl, name));
					xmlp.nextTag();
				} else {
					name = xmlp.getAttributeNotNull("name");
					if("theme".equals(tagName)) {
						if(this.themes.containsKey(name)) {
							throw xmlp.error("theme \"" + name + "\" already defined");
						}

						this.themes.put(name, this.parseTheme(xmlp, name, (ThemeInfoImpl)null, baseUrl));
					} else if("inputMapDef".equals(tagName)) {
						if(this.inputMaps.containsKey(name)) {
							throw xmlp.error("inputMap \"" + name + "\" already defined");
						}

						this.inputMaps.put(name, this.parseInputMap(xmlp, name, (ThemeInfoImpl)null));
					} else if("fontDef".equals(tagName)) {
						if(this.fonts.containsKey(name)) {
							throw xmlp.error("font \"" + name + "\" already defined");
						}

						boolean value = xmlp.parseBoolFromAttribute("default", false);
						Font font = this.parseFont(xmlp, baseUrl);
						this.fonts.put(name, font);
						if(this.firstFont == null) {
							this.firstFont = font;
						}

						if(value) {
							if(this.defaultFont != null) {
								throw xmlp.error("default font already set");
							}

							this.defaultFont = font;
						}
					} else {
						if(!"constantDef".equals(tagName)) {
							throw xmlp.unexpected();
						}

						Map value1 = this.parseParam(xmlp, baseUrl, "constantDef", (ThemeInfoImpl)null);
						if(value1.size() != 1) {
							throw xmlp.error("constant definitions must define exactly 1 value");
						}

						this.insertConstant(name, value1.values().iterator().next());
					}
				}
			} else {
				this.imageManager.parseImages(xmlp, baseUrl);
			}

			xmlp.require(3, (String)null, tagName);
			xmlp.nextTag();
		}

		xmlp.require(3, (String)null, "themes");
	}

	private InputMap getInputMap(XMLParser xmlp, String name) throws XmlPullParserException {
		InputMap im = (InputMap)this.inputMaps.get(name);
		if(im == null) {
			throw xmlp.error("Undefined input map: " + name);
		} else {
			return im;
		}
	}

	private InputMap parseInputMap(XMLParser xmlp, String name, ThemeInfoImpl parent) throws XmlPullParserException, IOException {
		InputMap base = InputMap.empty();
		if(xmlp.parseBoolFromAttribute("merge", false)) {
			if(parent == null) {
				throw xmlp.error("Can\'t merge on top level");
			}

			Object baseName = parent.params.get(name);
			if(baseName instanceof InputMap) {
				base = (InputMap)baseName;
			} else if(baseName != null) {
				throw xmlp.error("Can only merge with inputMap - found a " + baseName.getClass().getSimpleName());
			}
		}

		String baseName1 = xmlp.getAttributeValue((String)null, "ref");
		if(baseName1 != null) {
			base = base.addKeyStrokes(this.getInputMap(xmlp, baseName1));
		}

		xmlp.nextTag();
		LinkedHashSet keyStrokes = InputMap.parseBody(xmlp);
		InputMap im = base.addKeyStrokes(keyStrokes);
		return im;
	}

	private Font parseFont(XMLParser xmlp, URL baseUrl) throws XmlPullParserException, IOException {
		Map params = xmlp.getUnusedAttributes();
		ArrayList fontParams = new ArrayList();
		params.remove("name");
		params.remove("default");
		xmlp.nextTag();

		while(!xmlp.isEndTag()) {
			xmlp.require(2, (String)null, "fontParam");
			StateExpression cond = ParserUtil.parseCondition(xmlp);
			if(cond == null) {
				throw xmlp.error("Condition required");
			}

			Map condParams = xmlp.getUnusedAttributes();
			condParams.remove("if");
			condParams.remove("unless");
			fontParams.add(new FontParameter(cond, condParams));
			xmlp.nextTag();
			xmlp.require(3, (String)null, "fontParam");
			xmlp.nextTag();
		}

		return this.renderer.loadFont(baseUrl, params, fontParams);
	}

	private void parseThemeWildcardRef(XMLParser xmlp, ThemeInfoImpl parent) throws IOException, XmlPullParserException {
		String ref = xmlp.getAttributeValue((String)null, "ref");
		if(parent == null) {
			throw xmlp.error("Can\'t declare wildcard themes on top level");
		} else if(ref == null) {
			throw xmlp.error("Reference required for wildcard theme");
		} else if(!ref.endsWith("*")) {
			throw xmlp.error("Wildcard reference must end with \'*\'");
		} else {
			String refPath = ref.substring(0, ref.length() - 1);
			if(refPath.length() > 0 && !refPath.endsWith(".")) {
				throw xmlp.error("Wildcard must end with \".*\" or be \"*\"");
			} else {
				parent.wildcardImportPath = refPath;
				xmlp.nextTag();
			}
		}
	}

	private ThemeInfoImpl parseTheme(XMLParser xmlp, String themeName, ThemeInfoImpl parent, URL baseUrl) throws IOException, XmlPullParserException {
		ParserUtil.checkNameNotEmpty(themeName, xmlp);
		if(themeName.indexOf(46) < 0 && themeName.indexOf(42) < 0) {
			ThemeInfoImpl ti = new ThemeInfoImpl(this, themeName, parent);
			ThemeInfoImpl oldEnv = this.mathInterpreter.setEnv(ti);

			try {
				if(xmlp.parseBoolFromAttribute("merge", false)) {
					if(parent == null) {
						throw xmlp.error("Can\'t merge on top level");
					}

					ThemeInfoImpl ref = (ThemeInfoImpl)parent.children.get(themeName);
					if(ref != null) {
						ti.copy(ref);
					}
				}

				String ref1 = xmlp.getAttributeValue((String)null, "ref");
				if(ref1 != null) {
					ThemeInfoImpl tagName = (ThemeInfoImpl)this.findThemeInfo(ref1);
					if(tagName == null) {
						throw xmlp.error("referenced theme info not found: " + ref1);
					}

					ti.copy(tagName);
				}

				ti.maybeUsedFromWildcard = xmlp.parseBoolFromAttribute("allowWildcard", false);
				xmlp.nextTag();

				while(!xmlp.isEndTag()) {
					xmlp.require(2, (String)null, (String)null);
					String tagName1 = xmlp.getName();
					String name = xmlp.getAttributeNotNull("name");
					if("param".equals(tagName1)) {
						Map tiChild = this.parseParam(xmlp, baseUrl, "param", ti);
						ti.params.putAll(tiChild);
					} else {
						if(!"theme".equals(tagName1)) {
							throw xmlp.unexpected();
						}

						if(name.length() == 0) {
							this.parseThemeWildcardRef(xmlp, ti);
						} else {
							ThemeInfoImpl tiChild1 = this.parseTheme(xmlp, name, ti, baseUrl);
							ti.children.put(name, tiChild1);
						}
					}

					xmlp.require(3, (String)null, tagName1);
					xmlp.nextTag();
				}
			} finally {
				this.mathInterpreter.setEnv(oldEnv);
			}

			return ti;
		} else {
			throw xmlp.error("name must not contain \'.\' or \'*\'");
		}
	}

	private Map parseParam(XMLParser xmlp, URL baseUrl, String tagName, ThemeInfoImpl parent) throws XmlPullParserException, IOException {
		try {
			xmlp.require(2, (String)null, tagName);
			String ex = xmlp.getAttributeNotNull("name");
			xmlp.nextTag();
			String valueTagName = xmlp.getName();
			Object value = this.parseValue(xmlp, valueTagName, ex, baseUrl, parent);
			xmlp.require(3, (String)null, valueTagName);
			xmlp.nextTag();
			xmlp.require(3, (String)null, tagName);
			if(value instanceof Map) {
				return (Map)value;
			} else {
				ParserUtil.checkNameNotEmpty(ex, xmlp);
				return Collections.singletonMap(ex, value);
			}
		} catch (NumberFormatException numberFormatException8) {
			throw xmlp.error("unable to parse value", numberFormatException8);
		}
	}

	private ParameterListImpl parseList(XMLParser xmlp, URL baseUrl, ThemeInfoImpl parent) throws XmlPullParserException, IOException {
		ParameterListImpl result = new ParameterListImpl(this, parent);
		xmlp.nextTag();

		while(xmlp.isStartTag()) {
			String tagName = xmlp.getName();
			Object obj = this.parseValue(xmlp, tagName, (String)null, baseUrl, parent);
			xmlp.require(3, (String)null, tagName);
			result.params.add(obj);
			xmlp.nextTag();
		}

		return result;
	}

	private ParameterMapImpl parseMap(XMLParser xmlp, URL baseUrl, String name, ThemeInfoImpl parent) throws XmlPullParserException, IOException, NumberFormatException {
		ParameterMapImpl result = new ParameterMapImpl(this, parent);
		if(xmlp.parseBoolFromAttribute("merge", false)) {
			if(parent == null) {
				throw xmlp.error("Can\'t merge on top level");
			}

			Object ref = parent.params.get(name);
			if(ref instanceof ParameterMapImpl) {
				ParameterMapImpl tagName = (ParameterMapImpl)ref;
				result.params.putAll(tagName.params);
			} else if(ref != null) {
				throw xmlp.error("Can only merge with map - found a " + ref.getClass().getSimpleName());
			}
		}

		String ref1 = xmlp.getAttributeValue((String)null, "ref");
		if(ref1 != null) {
			Object tagName1 = parent.params.get(ref1);
			if(tagName1 == null) {
				tagName1 = this.constants.get(ref1);
				if(tagName1 == null) {
					throw new IOException("Referenced map not found: " + ref1);
				}
			}

			if(!(tagName1 instanceof ParameterMapImpl)) {
				throw new IOException("Expected a map got a " + tagName1.getClass().getSimpleName());
			}

			ParameterMapImpl params = (ParameterMapImpl)tagName1;
			result.params.putAll(params.params);
		}

		xmlp.nextTag();

		while(xmlp.isStartTag()) {
			String tagName2 = xmlp.getName();
			Map params1 = this.parseParam(xmlp, baseUrl, "param", parent);
			xmlp.require(3, (String)null, tagName2);
			result.addParameters(params1);
			xmlp.nextTag();
		}

		return result;
	}

	private Object parseValue(XMLParser xmlp, String tagName, String wildcardName, URL baseUrl, ThemeInfoImpl parent) throws XmlPullParserException, IOException, NumberFormatException {
		try {
			if("list".equals(tagName)) {
				return this.parseList(xmlp, baseUrl, parent);
			} else if("map".equals(tagName)) {
				return this.parseMap(xmlp, baseUrl, wildcardName, parent);
			} else if("inputMapDef".equals(tagName)) {
				return this.parseInputMap(xmlp, wildcardName, parent);
			} else if("fontDef".equals(tagName)) {
				return this.parseFont(xmlp, baseUrl);
			} else {
				String ex;
				if("enum".equals(tagName)) {
					ex = xmlp.getAttributeNotNull("type");
					Class result2 = (Class)enums.get(ex);
					if(result2 == null) {
						throw xmlp.error("enum type \"" + ex + "\" not registered");
					} else {
						return xmlp.parseEnumFromText(result2);
					}
				} else if("bool".equals(tagName)) {
					return xmlp.parseBoolFromText();
				} else {
					ex = xmlp.nextText();
					if("color".equals(tagName)) {
						return ParserUtil.parseColor(xmlp, ex);
					} else if("float".equals(tagName)) {
						return this.parseMath(xmlp, ex).floatValue();
					} else if("int".equals(tagName)) {
						return this.parseMath(xmlp, ex).intValue();
					} else if("string".equals(tagName)) {
						return ex;
					} else if("font".equals(tagName)) {
						Font result1 = (Font)this.fonts.get(ex);
						if(result1 == null) {
							throw xmlp.error("Font \"" + ex + "\" not found");
						} else {
							return result1;
						}
					} else if("border".equals(tagName)) {
						return this.parseObject(xmlp, ex, Border.class);
					} else if("dimension".equals(tagName)) {
						return this.parseObject(xmlp, ex, Dimension.class);
					} else if(!"gap".equals(tagName) && !"size".equals(tagName)) {
						if("constant".equals(tagName)) {
							Object result = this.constants.get(ex);
							if(result == null) {
								throw xmlp.error("Unknown constant: " + ex);
							} else {
								if(result == NULL) {
									result = null;
								}

								return result;
							}
						} else if("image".equals(tagName)) {
							if(ex.endsWith(".*")) {
								if(wildcardName == null) {
									throw new IllegalArgumentException("Wildcard\'s not allowed");
								} else {
									return this.imageManager.getImages(ex, wildcardName);
								}
							} else {
								return this.imageManager.getReferencedImage(xmlp, ex);
							}
						} else if("cursor".equals(tagName)) {
							if(ex.endsWith(".*")) {
								if(wildcardName == null) {
									throw new IllegalArgumentException("Wildcard\'s not allowed");
								} else {
									return this.imageManager.getCursors(ex, wildcardName);
								}
							} else {
								return this.imageManager.getReferencedCursor(xmlp, ex);
							}
						} else if("inputMap".equals(tagName)) {
							return this.getInputMap(xmlp, ex);
						} else {
							throw xmlp.error("Unknown type \"" + tagName + "\" specified");
						}
					} else {
						return this.parseObject(xmlp, ex, DialogLayout.Gap.class);
					}
				}
			}
		} catch (NumberFormatException numberFormatException8) {
			throw xmlp.error("unable to parse value", numberFormatException8);
		}
	}

	private Number parseMath(XMLParser xmlp, String str) throws XmlPullParserException {
		try {
			return this.mathInterpreter.execute(str);
		} catch (ParseException parseException4) {
			throw xmlp.error("unable to evaluate", parseException4);
		}
	}

	private Object parseObject(XMLParser xmlp, String str, Class type) throws XmlPullParserException {
		try {
			return this.mathInterpreter.executeCreateObject(str, type);
		} catch (ParseException parseException5) {
			throw xmlp.error("unable to evaluate", parseException5);
		}
	}

	ThemeInfo resolveWildcard(String base, String name) {
		assert base.length() == 0 || base.endsWith(".");

		String fullPath = base.concat(name);
		ThemeInfo info = this.findThemeInfo(fullPath, false);
		return info != null && ((ThemeInfoImpl)info).maybeUsedFromWildcard ? info : null;
	}

	class MathInterpreter extends AbstractMathInterpreter {
		private ThemeInfoImpl env;

		public ThemeInfoImpl setEnv(ThemeInfoImpl env) {
			ThemeInfoImpl oldEnv = this.env;
			this.env = env;
			return oldEnv;
		}

		public void accessVariable(String name) {
			Object obj;
			if(this.env != null) {
				obj = this.env.getParameterValue(name, false);
				if(obj != null) {
					this.push(obj);
					return;
				}

				ThemeInfo obj1 = this.env.getChildTheme(name);
				if(obj1 != null) {
					this.push(obj1);
					return;
				}
			}

			obj = ThemeManager.this.constants.get(name);
			if(obj != null) {
				this.push(obj);
			} else {
				throw new IllegalArgumentException("variable not found: " + name);
			}
		}

		protected Object accessField(Object obj, String field) {
			if(obj instanceof ParameterMapImpl) {
				Object border1 = ((ParameterMapImpl)obj).getParameterValue(field, false);
				if(border1 == null) {
					throw new IllegalArgumentException("field not found: " + field);
				} else {
					return border1;
				}
			} else if(obj instanceof Image && "border".equals(field)) {
				Border border = null;
				if(obj instanceof HasBorder) {
					border = ((HasBorder)obj).getBorder();
				}

				return border != null ? border : Border.ZERO;
			} else {
				return super.accessField(obj, field);
			}
		}
	}
}
