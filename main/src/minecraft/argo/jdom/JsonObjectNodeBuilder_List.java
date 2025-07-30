package argo.jdom;

import java.util.HashMap;
import java.util.Iterator;

class JsonObjectNodeBuilder_List extends HashMap<JsonStringNode, JsonNode> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8787633618813945764L;
	final JsonObjectNodeBuilder nodeBuilder;

	JsonObjectNodeBuilder_List(JsonObjectNodeBuilder var1) {
		this.nodeBuilder = var1;
		Iterator<JsonFieldBuilder> var2 = JsonObjectNodeBuilder.func_27236_a(this.nodeBuilder).iterator();

		while(var2.hasNext()) {
			JsonFieldBuilder var3 = (JsonFieldBuilder)var2.next();
			this.put(var3.func_27303_b(), var3.buildValue());
		}

	}
}
