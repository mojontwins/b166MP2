package org.argo.jdom;

import java.util.List;
import java.util.Map;

public abstract class JsonNode {
	public abstract JsonNodeType getType();

	public abstract String getText();

	public abstract Map<JsonStringNode,JsonNode> getFields();

	public abstract List<JsonNode> getElements();

	public final String getStringValue(Object... var1) {
		return (String)this.wrapExceptionsFor(JsonNodeSelectors.func_27349_a(var1), this, var1);
	}

	@SuppressWarnings("unchecked")
	public final List<JsonNode> getArrayNode(Object... var1) {
		return (List<JsonNode>)this.wrapExceptionsFor(JsonNodeSelectors.func_27346_b(var1), this, var1);
	}

	private Object wrapExceptionsFor(JsonNodeSelector var1, JsonNode var2, Object[] var3) {
		try {
			return var1.getValue(var2);
		} catch (JsonNodeDoesNotMatchChainedJsonNodeSelectorException var5) {
			throw JsonNodeDoesNotMatchPathElementsException.jsonNodeDoesNotMatchPathElementsException(var5, var3, JsonNodeFactories.aJsonArray(new JsonNode[]{var2}));
		}
	}
}
