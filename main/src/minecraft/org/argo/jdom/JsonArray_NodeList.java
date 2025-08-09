package org.argo.jdom;

import java.util.ArrayList;
import java.util.Iterator;

final class JsonArray_NodeList extends ArrayList<JsonNode> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3380442505794561285L;
	final Iterable<JsonNode> field_27405_a;

	JsonArray_NodeList(Iterable<JsonNode> var1) {
		this.field_27405_a = var1;
		Iterator<JsonNode> var2 = this.field_27405_a.iterator();

		while(var2.hasNext()) {
			JsonNode var3 = (JsonNode)var2.next();
			this.add(var3);
		}

	}
}
