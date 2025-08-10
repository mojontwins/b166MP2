package de.matthiasmann.twl.theme;

import de.matthiasmann.twl.Border;
import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.utils.StateExpression;
import de.matthiasmann.twl.utils.TextUtil;
import de.matthiasmann.twl.utils.XMLParser;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;

import org.xmlpull.v1.XmlPullParserException;

final class ParserUtil {
	static void checkNameNotEmpty(String name, XMLParser xmlp) throws XmlPullParserException {
		if(name == null) {
			throw xmlp.error("missing \'name\' on \'" + xmlp.getName() + "\'");
		} else if(name.length() == 0) {
			throw xmlp.error("empty name not allowed");
		} else if("none".equals(name)) {
			throw xmlp.error("can\'t use reserved name \"none\"");
		} else if(name.indexOf(42) >= 0) {
			throw xmlp.error("\'*\' is not allowed in names");
		} else if(name.indexOf(47) >= 0) {
			throw xmlp.error("\'/\' is not allowed in names");
		}
	}

	static Border parseBorderFromAttribute(XMLParser xmlp, String attribute) throws XmlPullParserException {
		String value = xmlp.getAttributeValue((String)null, attribute);
		return value == null ? null : parseBorder(xmlp, value);
	}

	static Border parseBorder(XMLParser xmlp, String value) throws XmlPullParserException {
		try {
			int[] ex = TextUtil.parseIntArray(value);
			switch(ex.length) {
			case 1:
				return new Border(ex[0]);
			case 2:
				return new Border(ex[0], ex[1]);
			case 3:
			default:
				throw xmlp.error("Unsupported border format");
			case 4:
				return new Border(ex[0], ex[1], ex[2], ex[3]);
			}
		} catch (NumberFormatException numberFormatException3) {
			throw xmlp.error("Unable to parse border size", numberFormatException3);
		}
	}

	static Color parseColorFromAttribute(XMLParser xmlp, String attribute, Color defaultColor) throws XmlPullParserException {
		String value = xmlp.getAttributeValue((String)null, attribute);
		return value == null ? defaultColor : parseColor(xmlp, value);
	}

	static Color parseColor(XMLParser xmlp, String value) throws XmlPullParserException {
		try {
			Color ex = Color.parserColor(value);
			if(ex == null) {
				throw xmlp.error("Unknown color name: " + value);
			} else {
				return ex;
			}
		} catch (NumberFormatException numberFormatException3) {
			throw xmlp.error("unable to parse color code", numberFormatException3);
		}
	}

	static String appendDot(String name) {
		int len = name.length();
		if(len > 0 && name.charAt(len - 1) != 46) {
			name = name.concat(".");
		}

		return name;
	}

	static int[] parseIntArrayFromAttribute(XMLParser xmlp, String attribute) throws XmlPullParserException {
		try {
			String ex = xmlp.getAttributeNotNull(attribute);
			return TextUtil.parseIntArray(ex);
		} catch (NumberFormatException numberFormatException3) {
			throw xmlp.error("Unable to parse", numberFormatException3);
		}
	}

	static SortedMap find(SortedMap map, String baseName) {
		return map.subMap(baseName, baseName.concat("\uffff"));
	}

	static Map resolve(SortedMap map, String ref, String name) {
		name = appendDot(name);
		int refLen = ref.length() - 1;
		ref = ref.substring(0, refLen);
		SortedMap matched = find(map, ref);
		if(matched.isEmpty()) {
			return matched;
		} else {
			HashMap result = new HashMap();
			Iterator iterator7 = matched.entrySet().iterator();

			while(iterator7.hasNext()) {
				Entry texEntry = (Entry)iterator7.next();
				String entryName = (String)texEntry.getKey();

				assert entryName.startsWith(ref);

				result.put(name.concat(entryName.substring(refLen)), texEntry.getValue());
			}

			return result;
		}
	}

	static StateExpression parseCondition(XMLParser xmlp) throws XmlPullParserException {
		String expression = xmlp.getAttributeValue((String)null, "if");
		boolean negate = expression == null;
		if(expression == null) {
			expression = xmlp.getAttributeValue((String)null, "unless");
		}

		if(expression != null) {
			try {
				return StateExpression.parse(expression, negate);
			} catch (ParseException parseException4) {
				throw xmlp.error("Unable to parse condition", parseException4);
			}
		} else {
			return null;
		}
	}
}
