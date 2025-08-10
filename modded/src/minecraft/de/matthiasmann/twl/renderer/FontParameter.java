package de.matthiasmann.twl.renderer;

import de.matthiasmann.twl.utils.StateExpression;

import java.util.Map;

public class FontParameter {
	private final StateExpression condition;
	private final Map params;

	public FontParameter(StateExpression condition, Map params) {
		if(condition == null) {
			throw new NullPointerException("condition");
		} else if(params == null) {
			throw new NullPointerException("params");
		} else {
			this.condition = condition;
			this.params = params;
		}
	}

	public StateExpression getCondition() {
		return this.condition;
	}

	public Map getParams() {
		return this.params;
	}
}
