package net.minecraft.world;

public class GameRule {
	private String caption;
	private String description;
	protected boolean value;
	protected int intValue;
	private boolean intRule = false;
	private String category;
	
	private int id;
	
	private static int lastId;
	
	public GameRule() {
		this.setId(lastId ++);
	}

	public GameRule withCaption(String caption) {
		this.setCaption(caption);
		return this;
	}
	
	public GameRule withDescription(String description) {
		this.setDescription(description);
		return this;
	}
	
	public GameRule setIntRule() {
		this.intRule = true;
		return this;
	}
	
	public GameRule withValue(boolean value) {
		this.setValue(value);
		return this;
	}
	
	public GameRule withIntValue(int value) {
		this.setIntValue(value);
		return this;
	}
	
	public boolean isIntRule() {
		return this.intRule;
	}
	
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setValue(boolean value) {
		this.value = value;
	}
	
	public boolean getValue() {
		return this.value;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
	
	public GameRule category(String category) {
		this.category = category;
		return this;
	}
	
	public String getCategory() {
		return this.category;
	}
	
	public void toggle() {
		this.setValue(!this.getValue());
	}

	public void refresh() {
		this.setValue(this.getValue());
	}
}
