package org.argo.jdom;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.argo.saj.InvalidSyntaxException;
import org.argo.saj.SajParser;

public final class JdomParser {
	public JsonRootNode parse(Reader var1) throws InvalidSyntaxException, IOException {
		JsonListenerToJdomAdapter var2 = new JsonListenerToJdomAdapter();
		(new SajParser()).parse(var1, var2);
		return var2.getDocument();
	}

	public JsonRootNode parse(String var1) throws InvalidSyntaxException {
		try {
			JsonRootNode var2 = this.parse((Reader)(new StringReader(var1)));
			return var2;
		} catch (IOException var4) {
			throw new RuntimeException("Coding failure in Argo:  StringWriter gave an IOException", var4);
		}
	}
}
