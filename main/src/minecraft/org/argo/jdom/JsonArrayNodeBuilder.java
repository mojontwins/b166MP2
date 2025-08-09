package org.argo.jdom;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class JsonArrayNodeBuilder implements JsonNodeBuilder {
	private final List<JsonNodeBuilder> elementBuilders = new LinkedList<JsonNodeBuilder>();

	public JsonArrayNodeBuilder withElement(JsonNodeBuilder var1) {
		this.elementBuilders.add(var1);
		return this;
	}

	public JsonRootNode build() {
		LinkedList<JsonNode> var1 = new LinkedList<JsonNode>();
		Iterator<JsonNodeBuilder> var2 = this.elementBuilders.iterator();

		while(var2.hasNext()) {
			JsonNodeBuilder var3 = (JsonNodeBuilder)var2.next();
			var1.add(var3.buildNode());
		}

		return JsonNodeFactories.aJsonArray((Iterable<JsonNode>)var1);
	}

	public JsonNode buildNode() {
		return this.build();
	}
}
