package de.matthiasmann.twl.textarea;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;

public class StyleSheet implements StyleSheetResolver {
	private final ArrayList rules = new ArrayList();
	private final IdentityHashMap cache = new IdentityHashMap();

	public void parse(URL url) throws IOException {
		InputStream is = url.openStream();

		try {
			this.parse((Reader)(new InputStreamReader(is, "UTF8")));
		} finally {
			is.close();
		}

	}

	public void parse(String style) throws IOException {
		this.parse((Reader)(new StringReader(style)));
	}

	public void parse(Reader r) throws IOException {
		Parser parser = new Parser(r);
		ArrayList selectors = new ArrayList();

		while(true) {
			int what;
			StyleSheet.Selector selector;
			String i;
			String n;
			label99:
			while(true) {
				if((what = parser.yylex()) == 0) {
					return;
				}

				selector = null;

				while(true) {
					String style = null;
					i = null;
					n = null;
					parser.sawWhitespace = false;
					switch(what) {
					case 1:
						style = parser.yytext();
					case 2:
						what = parser.yylex();
					case 3:
					case 4:
						break;
					default:
						parser.unexpected();
					}

					for(; (what == 3 || what == 4) && !parser.sawWhitespace; what = parser.yylex()) {
						parser.expect(1);
						String score = parser.yytext();
						if(what == 3) {
							i = score;
						} else {
							n = score;
						}
					}

					selector = new StyleSheet.Selector(style, i, n, selector);
					switch(what) {
					case 5:
						selector.directChild = true;
						what = parser.yylex();
						break;
					case 6:
					case 7:
						selector.directChild = true;
						selectors.add(selector);
						switch(what) {
						case 6:
							continue label99;
						case 7:
							break label99;
						default:
							parser.unexpected();
							break label99;
						}
					}
				}
			}

			CSSStyle cSSStyle12 = new CSSStyle();

			while((what = parser.yylex()) != 8) {
				if(what != 1) {
					parser.unexpected();
				}

				i = parser.yytext();
				parser.expect(9);
				what = parser.yylex();
				if(what != 10 && what != 8) {
					parser.unexpected();
				}

				n = parser.sb.toString().trim();

				try {
					cSSStyle12.parseCSSAttribute(i, n);
				} catch (IllegalArgumentException illegalArgumentException11) {
				}

				if(what == 8) {
					break;
				}
			}

			int i13 = 0;

			for(int i14 = selectors.size(); i13 < i14; ++i13) {
				selector = (StyleSheet.Selector)selectors.get(i13);
				this.rules.add(selector);
				int i15 = 0;

				for(StyleSheet.Selector s = selector; s != null; s = s.tail) {
					if(s.directChild) {
						++i15;
					}

					if(s.element != null) {
						i15 += 256;
					}

					if(s.className != null) {
						i15 += 65536;
					}

					if(s.id != null) {
						i15 += 16777216;
					}
				}

				selector.score = i15;
				selector.style = cSSStyle12;
			}

			selectors.clear();
		}
	}

	public void layoutFinished() {
		this.cache.clear();
	}

	public void startLayout() {
		this.cache.clear();
	}

	public Style resolve(Style style) {
		do {
			if(style.getStyleSheetKey() != null) {
				Object cacheData = this.cache.get(style);
				if(cacheData != null) {
					return cacheData == this ? null : (Style)cacheData;
				}

				StyleSheet.Selector[] candidates = new StyleSheet.Selector[this.rules.size()];
				int numCandidates = 0;
				int result = 0;

				for(int copy = this.rules.size(); result < copy; ++result) {
					StyleSheet.Selector i = (StyleSheet.Selector)this.rules.get(result);
					if(this.matches(i, style)) {
						candidates[numCandidates++] = i;
					}
				}

				if(numCandidates > 1) {
					Arrays.sort(candidates, 0, numCandidates);
				}

				Object object10 = null;
				boolean z11 = true;
				int i12 = 0;

				for(int n = numCandidates; i12 < n; ++i12) {
					CSSStyle ruleStyle = candidates[i12].style;
					if(object10 == null) {
						object10 = ruleStyle;
					} else {
						if(z11) {
							object10 = new Style((Style)object10);
							z11 = false;
						}

						((Style)object10).putAll((Style)ruleStyle);
					}
				}

				this.cache.put(style, object10 == null ? this : object10);
				return (Style)object10;
			}

			style = style.getParent();
		} while(style != null);

		return null;
	}

	private boolean matches(StyleSheet.Selector selector, Style style) {
		do {
			StyleSheetKey styleSheetKey = style.getStyleSheetKey();
			if(styleSheetKey != null) {
				if(selector.matches(styleSheetKey)) {
					selector = selector.tail;
					if(selector == null) {
						return true;
					}
				} else if(selector.directChild) {
					return false;
				}
			}

			style = style.getParent();
		} while(style != null);

		return false;
	}

	static class Selector extends StyleSheetKey implements Comparable {
		final StyleSheet.Selector tail;
		boolean directChild;
		CSSStyle style;
		int score;

		public Selector(String element, String className, String id, StyleSheet.Selector tail) {
			super(element, className, id);
			this.tail = tail;
		}

		public int compareTo(StyleSheet.Selector other) {
			return this.score - other.score;
		}

		public int compareTo(Object object1) {
			return this.compareTo((StyleSheet.Selector)object1);
		}
	}
}
