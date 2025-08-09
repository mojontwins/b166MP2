package org.argo.jdom;

import java.util.LinkedList;
import java.util.List;

public final class JsonObjectNodeBuilder implements JsonNodeBuilder {
	private final List<JsonFieldBuilder> fieldBuilders = new LinkedList<JsonFieldBuilder>();

	public JsonObjectNodeBuilder withFieldBuilder(JsonFieldBuilder var1) {
		this.fieldBuilders.add(var1);
		return this;
	}

	public JsonRootNode func_27235_a() {
		return JsonNodeFactories.aJsonObject(new JsonObjectNodeBuilder_List(this));
	}

	public JsonNode buildNode() {
		return this.func_27235_a();
	}

	static List<JsonFieldBuilder> func_27236_a(JsonObjectNodeBuilder var0) {
		return var0.fieldBuilders;
	}
}
