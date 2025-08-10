package de.matthiasmann.twl.textarea;

import de.matthiasmann.twl.model.HasCallback;
import de.matthiasmann.twl.utils.MultiStringReader;
import de.matthiasmann.twl.utils.TextUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class HTMLTextAreaModel extends HasCallback implements TextAreaModel {
	private final ArrayList elements;
	private final ArrayList styleSheetLinks;
	private final HashMap idMap;
	private String title;
	private final ArrayList styleStack;
	private final StringBuilder sb;
	private final int[] startLength;
	private TextAreaModel.ContainerElement curContainer;

	public HTMLTextAreaModel() {
		this.elements = new ArrayList();
		this.styleSheetLinks = new ArrayList();
		this.idMap = new HashMap();
		this.styleStack = new ArrayList();
		this.sb = new StringBuilder();
		this.startLength = new int[2];
	}

	public HTMLTextAreaModel(String html) {
		this();
		this.setHtml(html);
	}

	public HTMLTextAreaModel(Reader r) throws IOException {
		this();
		this.parseXHTML(r);
	}

	public void setHtml(String html) {
		Object r;
		if(isXHTML(html)) {
			r = new StringReader(html);
		} else {
			r = new MultiStringReader(new String[]{"<html><body>", html, "</body></html>"});
		}

		this.parseXHTML((Reader)r);
	}

	/** @deprecated */
	public void readHTMLFromStream(Reader r) throws IOException {
		this.parseXHTML(r);
	}

	public void readHTMLFromURL(URL url) throws IOException {
		InputStream in = url.openStream();

		try {
			this.parseXHTML(new InputStreamReader(in, "UTF8"));
		} finally {
			try {
				in.close();
			} catch (IOException iOException8) {
				Logger.getLogger(HTMLTextAreaModel.class.getName()).log(Level.SEVERE, "Exception while closing InputStream", iOException8);
			}

		}

	}

	public Iterator iterator() {
		return this.elements.iterator();
	}

	public Iterable getStyleSheetLinks() {
		return this.styleSheetLinks;
	}

	public String getTitle() {
		return this.title;
	}

	public TextAreaModel.Element getElementById(String id) {
		return (TextAreaModel.Element)this.idMap.get(id);
	}

	public void domModified() {
		this.doCallback();
	}

	public void parseXHTML(Reader reader) {
		this.elements.clear();
		this.styleSheetLinks.clear();
		this.idMap.clear();
		this.title = null;

		try {
			XmlPullParserFactory ex = XmlPullParserFactory.newInstance();
			ex.setNamespaceAware(false);
			ex.setValidating(false);
			XmlPullParser xpp = ex.newPullParser();
			xpp.setInput(reader);
			xpp.defineEntityReplacementText("nbsp", "\u00a0");
			xpp.require(0, (String)null, (String)null);
			xpp.nextTag();
			xpp.require(2, (String)null, "html");
			this.styleStack.clear();
			this.styleStack.add(new Style((Style)null, (StyleSheetKey)null));
			this.curContainer = null;
			this.sb.setLength(0);

			while(xpp.nextTag() != 3) {
				xpp.require(2, (String)null, (String)null);
				String name = xpp.getName();
				if("head".equals(name)) {
					this.parseHead(xpp);
				} else if("body".equals(name)) {
					this.pushStyle(xpp);
					TextAreaModel.BlockElement be = new TextAreaModel.BlockElement(this.getStyle());
					this.elements.add(be);
					this.parseContainer(xpp, be);
				}
			}

			this.parseMain(xpp);
			this.finishText();
		} catch (Throwable throwable9) {
			Logger.getLogger(HTMLTextAreaModel.class.getName()).log(Level.SEVERE, "Unable to parse XHTML document", throwable9);
		} finally {
			this.doCallback();
		}

	}

	private void parseContainer(XmlPullParser xpp, TextAreaModel.ContainerElement container) throws XmlPullParserException, IOException {
		TextAreaModel.ContainerElement prevContainer = this.curContainer;
		this.curContainer = container;
		this.pushStyle((XmlPullParser)null);
		this.parseMain(xpp);
		this.popStyle();
		this.curContainer = prevContainer;
	}

	private void parseMain(XmlPullParser xpp) throws XmlPullParserException, IOException {
		int level = 1;

		int type;
		while(level > 0 && (type = xpp.nextToken()) != 1) {
			String string9;
			switch(type) {
			case 2:
				string9 = xpp.getName();
				if("head".equals(string9)) {
					this.parseHead(xpp);
				} else {
					++level;
					if("br".equals(string9)) {
						this.sb.append("\n");
					} else {
						this.finishText();
						Style style10 = this.pushStyle(xpp);
						Object element;
						String href;
						String le;
						if("img".equals(string9)) {
							href = TextUtil.notNull(xpp.getAttributeValue((String)null, "src"));
							le = xpp.getAttributeValue((String)null, "alt");
							element = new TextAreaModel.ImageElement(style10, href, le);
						} else if("p".equals(string9)) {
							TextAreaModel.ParagraphElement textAreaModel$ParagraphElement11 = new TextAreaModel.ParagraphElement(style10);
							this.parseContainer(xpp, textAreaModel$ParagraphElement11);
							element = textAreaModel$ParagraphElement11;
							--level;
						} else if("button".equals(string9)) {
							href = TextUtil.notNull(xpp.getAttributeValue((String)null, "name"));
							le = TextUtil.notNull(xpp.getAttributeValue((String)null, "value"));
							element = new TextAreaModel.WidgetElement(style10, href, le);
						} else if(!"ul".equals(string9) && !"h1".equals(string9)) {
							if("ol".equals(string9)) {
								element = this.parseOL(xpp, style10);
								--level;
							} else if("li".equals(string9)) {
								TextAreaModel.ListElement textAreaModel$ListElement13 = new TextAreaModel.ListElement(style10);
								this.parseContainer(xpp, textAreaModel$ListElement13);
								element = textAreaModel$ListElement13;
								--level;
							} else if("div".equals(string9)) {
								TextAreaModel.BlockElement textAreaModel$BlockElement14 = new TextAreaModel.BlockElement(style10);
								this.parseContainer(xpp, textAreaModel$BlockElement14);
								element = textAreaModel$BlockElement14;
								--level;
							} else if("a".equals(string9)) {
								href = xpp.getAttributeValue((String)null, "href");
								if(href == null) {
									continue;
								}

								TextAreaModel.LinkElement textAreaModel$LinkElement15 = new TextAreaModel.LinkElement(style10, href);
								this.parseContainer(xpp, textAreaModel$LinkElement15);
								element = textAreaModel$LinkElement15;
								--level;
							} else {
								if(!"table".equals(string9)) {
									continue;
								}

								element = this.parseTable(xpp, style10);
								--level;
							}
						} else {
							TextAreaModel.ContainerElement textAreaModel$ContainerElement12 = new TextAreaModel.ContainerElement(style10);
							this.parseContainer(xpp, textAreaModel$ContainerElement12);
							element = textAreaModel$ContainerElement12;
							--level;
						}

						this.curContainer.add((TextAreaModel.Element)element);
						this.registerElement((TextAreaModel.Element)element);
					}
				}
				break;
			case 3:
				--level;
				string9 = xpp.getName();
				if(!"br".equals(string9)) {
					this.finishText();
					this.popStyle();
				}
				break;
			case 4:
				char[] buf = xpp.getTextCharacters(this.startLength);
				if(this.startLength[1] > 0) {
					int pos = this.sb.length();
					this.sb.append(buf, this.startLength[0], this.startLength[1]);
					if(!this.isPre()) {
						this.removeBreaks(pos);
					}
				}
			case 5:
			default:
				break;
			case 6:
				this.sb.append(xpp.getText());
			}
		}

	}

	private void parseHead(XmlPullParser xpp) throws XmlPullParserException, IOException {
		int level = 1;

		while(level > 0) {
			switch(xpp.nextTag()) {
			case 2:
				++level;
				String name = xpp.getName();
				if("link".equals(name)) {
					String linkhref = xpp.getAttributeValue((String)null, "href");
					if("stylesheet".equals(xpp.getAttributeValue((String)null, "rel")) && "text/css".equals(xpp.getAttributeValue((String)null, "type")) && linkhref != null) {
						this.styleSheetLinks.add(linkhref);
					}
				}

				if("title".equals(name)) {
					this.title = xpp.nextText();
					--level;
				}
				break;
			case 3:
				--level;
			}
		}

	}

	private TextAreaModel.TableElement parseTable(XmlPullParser xpp, Style tableStyle) throws XmlPullParserException, IOException {
		ArrayList cells = new ArrayList();
		ArrayList rowStyles = new ArrayList();
		int numColumns = 0;
		int cellSpacing = parseInt(xpp, "cellspacing", 0);
		int cellPadding = parseInt(xpp, "cellpadding", 0);

		String name;
		int idx;
		label63:
		do {
			while(true) {
				switch(xpp.nextTag()) {
				case 2:
					this.pushStyle(xpp);
					name = xpp.getName();
					if("td".equals(name) || "th".equals(name)) {
						int i14 = parseInt(xpp, "colspan", 1);
						TextAreaModel.TableCellElement textAreaModel$TableCellElement15 = new TextAreaModel.TableCellElement(this.getStyle(), i14);
						this.parseContainer(xpp, textAreaModel$TableCellElement15);
						this.registerElement(textAreaModel$TableCellElement15);
						cells.add(textAreaModel$TableCellElement15);

						for(idx = 1; idx < i14; ++idx) {
							cells.add((Object)null);
						}
					}

					if("tr".equals(name)) {
						rowStyles.add(this.getStyle());
					}
					break;
				case 3:
					this.popStyle();
					name = xpp.getName();
					if("tr".equals(name) && numColumns == 0) {
						numColumns = cells.size();
					}
					continue label63;
				}
			}
		} while(!"table".equals(name));

		TextAreaModel.TableElement tableElement = new TextAreaModel.TableElement(tableStyle, numColumns, rowStyles.size(), cellSpacing, cellPadding);
		int row = 0;

		for(idx = 0; row < rowStyles.size(); ++row) {
			tableElement.setRowStyle(row, (Style)rowStyles.get(row));

			for(int col = 0; col < numColumns && idx < cells.size(); ++idx) {
				TextAreaModel.TableCellElement cell = (TextAreaModel.TableCellElement)cells.get(idx);
				tableElement.setSell(row, col, cell);
				++col;
			}
		}

		return tableElement;
	}

	private TextAreaModel.OrderedListElement parseOL(XmlPullParser xpp, Style olStyle) throws XmlPullParserException, IOException {
		int start = parseInt(xpp, "start", 1);
		TextAreaModel.OrderedListElement ole = new TextAreaModel.OrderedListElement(olStyle, start);
		this.registerElement(ole);

		String name;
		label20:
		do {
			while(true) {
				switch(xpp.nextTag()) {
				case 2:
					this.pushStyle(xpp);
					name = xpp.getName();
					if("li".equals(name)) {
						TextAreaModel.ContainerElement ce = new TextAreaModel.ContainerElement(this.getStyle());
						this.parseContainer(xpp, ce);
						this.registerElement(ce);
						ole.add(ce);
					}
					break;
				case 3:
					this.popStyle();
					name = xpp.getName();
					continue label20;
				}
			}
		} while(!"ol".equals(name));

		return ole;
	}

	private void registerElement(TextAreaModel.Element element) {
		StyleSheetKey styleSheetKey = element.getStyle().getStyleSheetKey();
		if(styleSheetKey != null) {
			String id = styleSheetKey.getId();
			if(id != null) {
				this.idMap.put(id, element);
			}
		}

	}

	private static int parseInt(XmlPullParser xpp, String attribute, int defaultValue) {
		String value = xpp.getAttributeValue((String)null, attribute);
		if(value != null) {
			try {
				return Integer.parseInt(value);
			} catch (IllegalArgumentException illegalArgumentException5) {
			}
		}

		return defaultValue;
	}

	private static boolean isXHTML(String doc) {
		return doc.length() > 5 && doc.charAt(0) == 60 ? doc.startsWith("<?xml") || doc.startsWith("<!DOCTYPE") || doc.startsWith("<html>") : false;
	}

	private boolean isPre() {
		return ((Boolean)this.getStyle().get(StyleAttribute.PREFORMATTED, (StyleSheetResolver)null)).booleanValue();
	}

	private Style getStyle() {
		return (Style)this.styleStack.get(this.styleStack.size() - 1);
	}

	private Style pushStyle(XmlPullParser xpp) {
		Style parent = this.getStyle();
		StyleSheetKey key = null;
		String style = null;
		if(xpp != null) {
			String newStyle = xpp.getAttributeValue((String)null, "class");
			String element = xpp.getName();
			String id = xpp.getAttributeValue((String)null, "id");
			key = new StyleSheetKey(element, newStyle, id);
			style = xpp.getAttributeValue((String)null, "style");
		}

		Object newStyle1;
		if(style != null) {
			newStyle1 = new CSSStyle(parent, key, style);
		} else {
			newStyle1 = new Style(parent, key);
		}

		if(xpp != null && "pre".equals(xpp.getName())) {
			((Style)newStyle1).put(StyleAttribute.PREFORMATTED, Boolean.TRUE);
		}

		this.styleStack.add(newStyle1);
		return (Style)newStyle1;
	}

	private void popStyle() {
		int stackSize = this.styleStack.size();
		if(stackSize > 1) {
			this.styleStack.remove(stackSize - 1);
		}

	}

	private void finishText() {
		if(this.sb.length() > 0) {
			Style style = this.getStyle();
			TextAreaModel.TextElement e = new TextAreaModel.TextElement(style, this.sb.toString());
			this.registerElement(e);
			this.curContainer.add(e);
			this.sb.setLength(0);
		}

	}

	private void removeBreaks(int pos) {
		int wasSpace = this.sb.length();

		while(true) {
			char idx;
			do {
				if(wasSpace-- <= pos) {
					if(pos > 0) {
						--pos;
					}

					boolean z5 = false;

					boolean isSpace;
					for(int i6 = this.sb.length(); i6-- > pos; z5 = isSpace) {
						isSpace = this.sb.charAt(i6) == 32;
						if(isSpace && z5) {
							this.sb.deleteCharAt(i6);
						}
					}

					return;
				}

				idx = this.sb.charAt(wasSpace);
			} while(!Character.isWhitespace(idx) && !Character.isISOControl(idx));

			this.sb.setCharAt(wasSpace, ' ');
		}
	}
}
