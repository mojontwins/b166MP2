package de.matthiasmann.twl.textarea;

public class StyleSheetKey {
	final String element;
	final String className;
	final String id;

	public StyleSheetKey(String element, String className, String id) {
		this.element = element;
		this.className = className;
		this.id = id;
	}

	public String getClassName() {
		return this.className;
	}

	public String getElement() {
		return this.element;
	}

	public String getId() {
		return this.id;
	}

	public boolean equals(Object obj) {
		if(!(obj instanceof StyleSheetKey)) {
			return false;
		} else {
			StyleSheetKey other = (StyleSheetKey)obj;
			if(this.element == null) {
				if(other.element != null) {
					return false;
				}
			} else if(!this.element.equals(other.element)) {
				return false;
			}

			if(this.className == null) {
				if(other.className != null) {
					return false;
				}
			} else if(!this.className.equals(other.className)) {
				return false;
			}

			if(this.id == null) {
				if(other.id == null) {
					return true;
				}
			} else if(this.id.equals(other.id)) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		byte hash = 7;
		int hash1 = 53 * hash + (this.element != null ? this.element.hashCode() : 0);
		hash1 = 53 * hash1 + (this.className != null ? this.className.hashCode() : 0);
		hash1 = 53 * hash1 + (this.id != null ? this.id.hashCode() : 0);
		return hash1;
	}

	public boolean matches(StyleSheetKey what) {
		return this.element != null && !this.element.equals(what.element) ? false : (this.className != null && !this.className.equals(what.className) ? false : this.id == null || this.id.equals(what.id));
	}

	public String toString() {
		StringBuilder sb = (new StringBuilder()).append(this.element);
		if(this.className != null) {
			sb.append('.').append(this.className);
		}

		if(this.id != null) {
			sb.append('#').append(this.id);
		}

		return sb.toString();
	}
}
