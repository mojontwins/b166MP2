package de.matthiasmann.twl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class MenuElement {
	private String name;
	private String theme;
	private boolean enabled = true;
	private Object tooltipContent;
	private PropertyChangeSupport pcs;

	public MenuElement() {
	}

	public MenuElement(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public MenuElement setName(String name) {
		String oldName = this.name;
		this.name = name;
		this.firePropertyChange("name", oldName, name);
		return this;
	}

	public String getTheme() {
		return this.theme;
	}

	public MenuElement setTheme(String theme) {
		String oldTheme = this.theme;
		this.theme = theme;
		this.firePropertyChange("theme", oldTheme, theme);
		return this;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public MenuElement setEnabled(boolean enabled) {
		boolean oldEnabled = this.enabled;
		this.enabled = enabled;
		this.firePropertyChange("enabled", oldEnabled, enabled);
		return this;
	}

	public Object getTooltipContent() {
		return this.tooltipContent;
	}

	public MenuElement setTooltipContent(Object tooltip) {
		Object oldTooltip = this.tooltipContent;
		this.tooltipContent = tooltip;
		this.firePropertyChange("tooltipContent", oldTooltip, tooltip);
		return this;
	}

	protected abstract Widget createMenuWidget(MenuManager menuManager1, int i2);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if(this.pcs == null) {
			this.pcs = new PropertyChangeSupport(this);
		}

		this.pcs.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if(this.pcs == null) {
			this.pcs = new PropertyChangeSupport(this);
		}

		this.pcs.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if(this.pcs != null) {
			this.pcs.removePropertyChangeListener(propertyName, listener);
		}

	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if(this.pcs != null) {
			this.pcs.removePropertyChangeListener(listener);
		}

	}

	protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
		if(this.pcs != null) {
			this.pcs.firePropertyChange(propertyName, oldValue, newValue);
		}

	}

	protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
		if(this.pcs != null) {
			this.pcs.firePropertyChange(propertyName, oldValue, newValue);
		}

	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if(this.pcs != null) {
			this.pcs.firePropertyChange(propertyName, oldValue, newValue);
		}

	}

	void setWidgetTheme(Widget w, String defaultTheme) {
		if(this.theme != null) {
			w.setTheme(this.theme);
		} else {
			w.setTheme(defaultTheme);
		}

	}

	class MenuBtn extends Button implements PropertyChangeListener {
		public MenuBtn() {
			this.sync();
		}

		protected void afterAddToGUI(GUI gui) {
			super.afterAddToGUI(gui);
			MenuElement.this.addPropertyChangeListener(this);
		}

		protected void beforeRemoveFromGUI(GUI gui) {
			MenuElement.this.removePropertyChangeListener(this);
			super.beforeRemoveFromGUI(gui);
		}

		public void propertyChange(PropertyChangeEvent evt) {
			this.sync();
		}

		protected void sync() {
			this.setEnabled(MenuElement.this.isEnabled());
			this.setTooltipContent(MenuElement.this.getTooltipContent());
			this.setText(MenuElement.this.getName());
		}
	}
}
