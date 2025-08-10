package de.matthiasmann.twl;

import de.matthiasmann.twl.model.AbstractFloatModel;
import de.matthiasmann.twl.model.AbstractIntegerModel;
import de.matthiasmann.twl.model.ColorSpace;
import de.matthiasmann.twl.renderer.DynamicImage;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.utils.CallbackSupport;
import de.matthiasmann.twl.utils.TintAnimator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class ColorSelector extends DialogLayout {
	private static final String[] RGBA_NAMES = new String[]{"Red", "Green", "Blue", "Alpha"};
	private static final String[] RGBA_PREFIX = new String[]{"R: ", "G: ", "B: ", "A: "};
	final ByteBuffer imgData = ByteBuffer.allocateDirect(16384);
	final IntBuffer imgDataInt;
	ColorSpace colorSpace;
	float[] colorValues;
	ColorSelector.ColorValueModel[] colorValueModels;
	private boolean useColorArea2D = true;
	private boolean showPreview = false;
	private boolean useLabels = true;
	private boolean showHexEditField = false;
	private boolean showNativeAdjuster = true;
	private boolean showRGBAdjuster = true;
	private boolean showAlphaAdjuster = true;
	private Runnable[] callbacks;
	int currentColor;
	private ColorSelector.ARGBModel[] argbModels;
	EditField hexColorEditField;
	private TintAnimator previewTintAnimator;
	private boolean recreateLayout;
	private static final int IMAGE_SIZE = 64;

	public ColorSelector(ColorSpace colorSpace) {
		this.imgData.order(ByteOrder.BIG_ENDIAN);
		this.imgDataInt = this.imgData.asIntBuffer();
		this.currentColor = Color.WHITE.toARGB();
		this.setColorSpace(colorSpace);
	}

	public ColorSpace getColorSpace() {
		return this.colorSpace;
	}

	public void setColorSpace(ColorSpace colorModel) {
		if(colorModel == null) {
			throw new NullPointerException("colorModel");
		} else {
			if(this.colorSpace != colorModel) {
				boolean hasColor = this.colorSpace != null;
				this.colorSpace = colorModel;
				this.colorValues = new float[colorModel.getNumComponents()];
				if(hasColor) {
					this.setColor(this.currentColor);
				} else {
					this.setDefaultColor();
				}

				this.recreateLayout = true;
				this.invalidateLayout();
			}

		}
	}

	public Color getColor() {
		return new Color(this.currentColor);
	}

	public void setColor(Color color) {
		this.setColor(color.toARGB());
	}

	public void setDefaultColor() {
		this.currentColor = Color.WHITE.toARGB();

		for(int i = 0; i < this.colorSpace.getNumComponents(); ++i) {
			this.colorValues[i] = this.colorSpace.getDefaultValue(i);
		}

		this.updateAllColorAreas();
		this.colorChanged();
	}

	public boolean isUseColorArea2D() {
		return this.useColorArea2D;
	}

	public void setUseColorArea2D(boolean useColorArea2D) {
		if(this.useColorArea2D != useColorArea2D) {
			this.useColorArea2D = useColorArea2D;
			this.recreateLayout = true;
			this.invalidateLayout();
		}

	}

	public boolean isShowPreview() {
		return this.showPreview;
	}

	public void setShowPreview(boolean showPreview) {
		if(this.showPreview != showPreview) {
			this.showPreview = showPreview;
			this.recreateLayout = true;
			this.invalidateLayout();
		}

	}

	public boolean isShowHexEditField() {
		return this.showHexEditField;
	}

	public void setShowHexEditField(boolean showHexEditField) {
		if(this.showHexEditField != showHexEditField) {
			this.showHexEditField = showHexEditField;
			this.recreateLayout = true;
			this.invalidateLayout();
		}

	}

	public boolean isShowAlphaAdjuster() {
		return this.showAlphaAdjuster;
	}

	public void setShowAlphaAdjuster(boolean showAlphaAdjuster) {
		if(this.showAlphaAdjuster != showAlphaAdjuster) {
			this.showAlphaAdjuster = showAlphaAdjuster;
			this.recreateLayout = true;
			this.invalidateLayout();
		}

	}

	public boolean isShowNativeAdjuster() {
		return this.showNativeAdjuster;
	}

	public void setShowNativeAdjuster(boolean showNativeAdjuster) {
		if(this.showNativeAdjuster != showNativeAdjuster) {
			this.showNativeAdjuster = showNativeAdjuster;
			this.recreateLayout = true;
			this.invalidateLayout();
		}

	}

	public boolean isShowRGBAdjuster() {
		return this.showRGBAdjuster;
	}

	public void setShowRGBAdjuster(boolean showRGBAdjuster) {
		if(this.showRGBAdjuster != showRGBAdjuster) {
			this.showRGBAdjuster = showRGBAdjuster;
			this.recreateLayout = true;
			this.invalidateLayout();
		}

	}

	public boolean isUseLabels() {
		return this.useLabels;
	}

	public void setUseLabels(boolean useLabels) {
		if(this.useLabels != useLabels) {
			this.useLabels = useLabels;
			this.recreateLayout = true;
			this.invalidateLayout();
		}

	}

	public void addCallback(Runnable cb) {
		this.callbacks = (Runnable[])CallbackSupport.addCallbackToList(this.callbacks, cb, Runnable.class);
	}

	public void removeCallback(Runnable cb) {
		this.callbacks = (Runnable[])CallbackSupport.removeCallbackFromList(this.callbacks, cb);
	}

	protected void colorChanged() {
		this.currentColor = this.currentColor & 0xFF000000 | this.colorSpace.toRGB(this.colorValues);
		CallbackSupport.fireCallbacks(this.callbacks);
		if(this.argbModels != null) {
			ColorSelector.ARGBModel[] colorSelector$ARGBModel4 = this.argbModels;
			int i3 = this.argbModels.length;

			for(int i2 = 0; i2 < i3; ++i2) {
				ColorSelector.ARGBModel m = colorSelector$ARGBModel4[i2];
				m.fireCallback();
			}
		}

		if(this.previewTintAnimator != null) {
			this.previewTintAnimator.setColor(this.getColor());
		}

		this.updateHexEditField();
	}

	protected void setColor(int argb) {
		this.currentColor = argb;
		this.colorValues = this.colorSpace.fromRGB(argb & 0xFFFFFF);
		this.updateAllColorAreas();
	}

	protected int getNumComponents() {
		return this.colorSpace.getNumComponents();
	}

	public void layout() {
		if(this.recreateLayout) {
			this.createColorAreas();
		}

		super.layout();
	}

	public int getMinWidth() {
		if(this.recreateLayout) {
			this.createColorAreas();
		}

		return super.getMinWidth();
	}

	public int getMinHeight() {
		if(this.recreateLayout) {
			this.createColorAreas();
		}

		return super.getMinHeight();
	}

	public int getPreferredInnerWidth() {
		if(this.recreateLayout) {
			this.createColorAreas();
		}

		return super.getPreferredInnerWidth();
	}

	public int getPreferredInnerHeight() {
		if(this.recreateLayout) {
			this.createColorAreas();
		}

		return super.getPreferredInnerHeight();
	}

	protected void createColorAreas() {
		this.recreateLayout = false;
		this.setVerticalGroup((DialogLayout.Group)null);
		this.removeAllChildren();
		this.argbModels = new ColorSelector.ARGBModel[4];
		this.argbModels[0] = new ColorSelector.ARGBModel(16);
		this.argbModels[1] = new ColorSelector.ARGBModel(8);
		this.argbModels[2] = new ColorSelector.ARGBModel(0);
		this.argbModels[3] = new ColorSelector.ARGBModel(24);
		int numComponents = this.getNumComponents();
		DialogLayout.Group horzAreas = this.createSequentialGroup().addGap();
		DialogLayout.Group vertAreas = this.createParallelGroup();
		DialogLayout.Group horzLabels = null;
		DialogLayout.Group horzAdjuster = this.createParallelGroup();
		DialogLayout.Group horzControlls = this.createSequentialGroup();
		if(this.useLabels) {
			horzLabels = this.createParallelGroup();
			horzControlls.addGroup(horzLabels);
		}

		horzControlls.addGroup(horzAdjuster);
		DialogLayout.Group[] vertAdjuster = new DialogLayout.Group[4 + numComponents];
		int numAdjuters = 0;

		int component;
		for(component = 0; component < vertAdjuster.length; ++component) {
			vertAdjuster[component] = this.createParallelGroup();
		}

		this.colorValueModels = new ColorSelector.ColorValueModel[numComponents];

		Label vertMainGroup;
		for(component = 0; component < numComponents; ++component) {
			this.colorValueModels[component] = new ColorSelector.ColorValueModel(component);
			if(this.showNativeAdjuster) {
				ValueAdjusterFloat horzMainGroup = new ValueAdjusterFloat(this.colorValueModels[component]);
				if(this.useLabels) {
					vertMainGroup = new Label(this.colorSpace.getComponentName(component));
					vertMainGroup.setLabelFor(horzMainGroup);
					horzLabels.addWidget(vertMainGroup);
					vertAdjuster[numAdjuters].addWidget(vertMainGroup);
				} else {
					horzMainGroup.setDisplayPrefix(this.colorSpace.getComponentShortName(component).concat(": "));
					horzMainGroup.setTooltipContent(this.colorSpace.getComponentName(component));
				}

				horzAdjuster.addWidget(horzMainGroup);
				vertAdjuster[numAdjuters].addWidget(horzMainGroup);
				++numAdjuters;
			}
		}

		for(component = 0; component < this.argbModels.length; ++component) {
			if(component == 3 && this.showAlphaAdjuster || component < 3 && this.showRGBAdjuster) {
				ValueAdjusterInt valueAdjusterInt15 = new ValueAdjusterInt(this.argbModels[component]);
				if(this.useLabels) {
					vertMainGroup = new Label(RGBA_NAMES[component]);
					vertMainGroup.setLabelFor(valueAdjusterInt15);
					horzLabels.addWidget(vertMainGroup);
					vertAdjuster[numAdjuters].addWidget(vertMainGroup);
				} else {
					valueAdjusterInt15.setDisplayPrefix(RGBA_PREFIX[component]);
					valueAdjusterInt15.setTooltipContent(RGBA_NAMES[component]);
				}

				horzAdjuster.addWidget(valueAdjusterInt15);
				vertAdjuster[numAdjuters].addWidget(valueAdjusterInt15);
				++numAdjuters;
			}
		}

		component = 0;
		if(this.useColorArea2D) {
			while(component + 1 < numComponents) {
				ColorSelector.ColorArea2D colorSelector$ColorArea2D16 = new ColorSelector.ColorArea2D(component, component + 1);
				colorSelector$ColorArea2D16.setTooltipContent(this.colorSpace.getComponentName(component) + " / " + this.colorSpace.getComponentName(component + 1));
				horzAreas.addWidget(colorSelector$ColorArea2D16);
				vertAreas.addWidget(colorSelector$ColorArea2D16);
				component += 2;
			}
		}

		while(component < numComponents) {
			ColorSelector.ColorArea1D colorSelector$ColorArea1D17 = new ColorSelector.ColorArea1D(component);
			colorSelector$ColorArea1D17.setTooltipContent(this.colorSpace.getComponentName(component));
			horzAreas.addWidget(colorSelector$ColorArea1D17);
			vertAreas.addWidget(colorSelector$ColorArea1D17);
			++component;
		}

		if(this.showHexEditField && this.hexColorEditField == null) {
			this.createHexColorEditField();
		}

		if(this.showPreview) {
			if(this.previewTintAnimator == null) {
				this.previewTintAnimator = new TintAnimator(new TintAnimator.GUITimeSource(this), this.getColor());
			}

			Widget widget18 = new Widget();
			widget18.setTheme("colorarea");
			widget18.setTintAnimator(this.previewTintAnimator);
			Widget widget20 = new Widget() {
				protected void layout() {
					this.layoutChildrenFullInnerArea();
				}
			};
			widget20.setTheme("preview");
			widget20.add(widget18);
			Label i = new Label();
			i.setTheme("previewLabel");
			i.setLabelFor(widget20);
			DialogLayout.Group horz = this.createParallelGroup();
			DialogLayout.Group vert = this.createSequentialGroup();
			horzAreas.addGroup(horz.addWidget(i).addWidget(widget20));
			vertAreas.addGroup(vert.addGap().addWidget(i).addWidget(widget20));
			if(this.showHexEditField) {
				horz.addWidget(this.hexColorEditField);
				vert.addGap().addWidget(this.hexColorEditField);
			}
		}

		DialogLayout.Group dialogLayout$Group19 = this.createParallelGroup().addGroup(horzAreas.addGap()).addGroup(horzControlls);
		DialogLayout.Group dialogLayout$Group21 = this.createSequentialGroup().addGroup(vertAreas);

		for(int i22 = 0; i22 < numAdjuters; ++i22) {
			dialogLayout$Group21.addGroup(vertAdjuster[i22]);
		}

		if(this.showHexEditField) {
			if(this.hexColorEditField == null) {
				this.createHexColorEditField();
			}

			if(!this.showPreview) {
				dialogLayout$Group19.addWidget(this.hexColorEditField);
				dialogLayout$Group21.addWidget(this.hexColorEditField);
			}

			this.updateHexEditField();
		}

		this.setHorizontalGroup(dialogLayout$Group19);
		this.setVerticalGroup(dialogLayout$Group21.addGap());
	}

	protected void updateAllColorAreas() {
		if(this.colorValueModels != null) {
			ColorSelector.ColorValueModel[] colorSelector$ColorValueModel4 = this.colorValueModels;
			int i3 = this.colorValueModels.length;

			for(int i2 = 0; i2 < i3; ++i2) {
				ColorSelector.ColorValueModel cvm = colorSelector$ColorValueModel4[i2];
				cvm.fireCallback();
			}

			this.colorChanged();
		}

	}

	private void createHexColorEditField() {
		this.hexColorEditField = new EditField() {
			protected void insertChar(char ch) {
				if(this.isValid(ch)) {
					super.insertChar(ch);
				}

			}

			public void insertText(String str) {
				int i = 0;

				for(int n = str.length(); i < n; ++i) {
					if(!this.isValid(str.charAt(i))) {
						StringBuilder sb = new StringBuilder(str);
						int j = n;

						while(j-- >= i) {
							if(!this.isValid(sb.charAt(j))) {
								sb.deleteCharAt(j);
							}
						}

						str = sb.toString();
						break;
					}
				}

				super.insertText(str);
			}

			private boolean isValid(char ch) {
				int digit = Character.digit(ch, 16);
				return digit >= 0 && digit < 16;
			}
		};
		this.hexColorEditField.setTheme("hexColorEditField");
		this.hexColorEditField.setColumns(8);
		this.hexColorEditField.addCallback(new EditField.Callback() {
			public void callback(int key) {
				if(key == 1) {
					ColorSelector.this.updateHexEditField();
				} else {
					Color color = null;

					try {
						color = Color.parserColor("#".concat(ColorSelector.this.hexColorEditField.getText()));
						ColorSelector.this.hexColorEditField.setErrorMessage((Object)null);
					} catch (Exception exception4) {
						ColorSelector.this.hexColorEditField.setErrorMessage("Invalid color format");
					}

					if(key == 28 && color != null) {
						ColorSelector.this.setColor(color);
					}

				}
			}
		});
	}

	void updateHexEditField() {
		if(this.hexColorEditField != null) {
			this.hexColorEditField.setText(String.format("%08X", new Object[]{this.currentColor}));
		}

	}

	class ARGBModel extends AbstractIntegerModel {
		private final int startBit;

		public ARGBModel(int startBit) {
			this.startBit = startBit;
		}

		public int getMaxValue() {
			return 255;
		}

		public int getMinValue() {
			return 0;
		}

		public int getValue() {
			return ColorSelector.this.currentColor >> this.startBit & 255;
		}

		public void setValue(int value) {
			ColorSelector.this.setColor(ColorSelector.this.currentColor & ~(255 << this.startBit) | value << this.startBit);
		}

		void fireCallback() {
			this.doCallback();
		}
	}

	abstract class ColorArea extends Widget implements Runnable {
		DynamicImage img;
		Image cursorImage;
		boolean needsUpdate;
		private static int[] $SWITCH_TABLE$de$matthiasmann$twl$Event$Type;

		protected void applyTheme(ThemeInfo themeInfo) {
			super.applyTheme(themeInfo);
			this.cursorImage = themeInfo.getImage("cursor");
		}

		abstract void createImage(GUI gUI1);

		abstract void updateImage();

		abstract void handleMouse(int i1, int i2);

		protected void paintWidget(GUI gui) {
			if(this.img == null) {
				this.createImage(gui);
				this.needsUpdate = true;
			}

			if(this.img != null) {
				if(this.needsUpdate) {
					this.updateImage();
				}

				this.img.draw(this.getAnimationState(), this.getInnerX(), this.getInnerY(), this.getInnerWidth(), this.getInnerHeight());
			}

		}

		public void destroy() {
			super.destroy();
			if(this.img != null) {
				this.img.destroy();
				this.img = null;
			}

		}

		protected boolean handleEvent(Event evt) {
			switch($SWITCH_TABLE$de$matthiasmann$twl$Event$Type()[evt.getType().ordinal()]) {
			case 3:
			case 6:
				this.handleMouse(evt.getMouseX() - this.getInnerX(), evt.getMouseY() - this.getInnerY());
				return true;
			case 4:
			case 5:
			case 7:
			default:
				if(evt.isMouseEvent()) {
					return true;
				}

				return super.handleEvent(evt);
			case 8:
				return false;
			}
		}

		public void run() {
			this.needsUpdate = true;
		}

		static int[] $SWITCH_TABLE$de$matthiasmann$twl$Event$Type() {
			int[] i10000 = $SWITCH_TABLE$de$matthiasmann$twl$Event$Type;
			if($SWITCH_TABLE$de$matthiasmann$twl$Event$Type != null) {
				return i10000;
			} else {
				int[] i0 = new int[Event.Type.values().length];

				try {
					i0[Event.Type.KEY_PRESSED.ordinal()] = 9;
				} catch (NoSuchFieldError noSuchFieldError12) {
				}

				try {
					i0[Event.Type.KEY_RELEASED.ordinal()] = 10;
				} catch (NoSuchFieldError noSuchFieldError11) {
				}

				try {
					i0[Event.Type.MOUSE_BTNDOWN.ordinal()] = 3;
				} catch (NoSuchFieldError noSuchFieldError10) {
				}

				try {
					i0[Event.Type.MOUSE_BTNUP.ordinal()] = 4;
				} catch (NoSuchFieldError noSuchFieldError9) {
				}

				try {
					i0[Event.Type.MOUSE_CLICKED.ordinal()] = 5;
				} catch (NoSuchFieldError noSuchFieldError8) {
				}

				try {
					i0[Event.Type.MOUSE_DRAGGED.ordinal()] = 6;
				} catch (NoSuchFieldError noSuchFieldError7) {
				}

				try {
					i0[Event.Type.MOUSE_ENTERED.ordinal()] = 1;
				} catch (NoSuchFieldError noSuchFieldError6) {
				}

				try {
					i0[Event.Type.MOUSE_EXITED.ordinal()] = 7;
				} catch (NoSuchFieldError noSuchFieldError5) {
				}

				try {
					i0[Event.Type.MOUSE_MOVED.ordinal()] = 2;
				} catch (NoSuchFieldError noSuchFieldError4) {
				}

				try {
					i0[Event.Type.MOUSE_WHEEL.ordinal()] = 8;
				} catch (NoSuchFieldError noSuchFieldError3) {
				}

				try {
					i0[Event.Type.POPUP_CLOSED.ordinal()] = 12;
				} catch (NoSuchFieldError noSuchFieldError2) {
				}

				try {
					i0[Event.Type.POPUP_OPENED.ordinal()] = 11;
				} catch (NoSuchFieldError noSuchFieldError1) {
				}

				$SWITCH_TABLE$de$matthiasmann$twl$Event$Type = i0;
				return i0;
			}
		}
	}

	class ColorArea1D extends ColorSelector.ColorArea {
		final int component;

		public ColorArea1D(int component) {
			super();
			this.component = component;
			int i = 0;

			for(int n = ColorSelector.this.getNumComponents(); i < n; ++i) {
				if(i != component) {
					ColorSelector.this.colorValueModels[i].addCallback(this);
				}
			}

		}

		protected void paintWidget(GUI gui) {
			super.paintWidget(gui);
			if(this.cursorImage != null) {
				float minValue = ColorSelector.this.colorSpace.getMinValue(this.component);
				float maxValue = ColorSelector.this.colorSpace.getMaxValue(this.component);
				int pos = (int)((ColorSelector.this.colorValues[this.component] - maxValue) * (float)(this.getInnerHeight() - 1) / (minValue - maxValue) + 0.5F);
				this.cursorImage.draw(this.getAnimationState(), this.getInnerX(), this.getInnerY() + pos, this.getInnerWidth(), 1);
			}

		}

		protected void createImage(GUI gui) {
			this.img = gui.getRenderer().createDynamicImage(1, 64);
		}

		protected void updateImage() {
			float[] temp = (float[])ColorSelector.this.colorValues.clone();
			IntBuffer buf = ColorSelector.this.imgDataInt;
			ColorSpace cs = ColorSelector.this.colorSpace;
			float x = cs.getMaxValue(this.component);
			float dx = (cs.getMinValue(this.component) - x) / 63.0F;

			for(int i = 0; i < 64; ++i) {
				temp[this.component] = x;
				buf.put(i, cs.toRGB(temp) << 8 | 255);
				x += dx;
			}

			this.img.update(ColorSelector.this.imgData, DynamicImage.Format.RGBA);
			this.needsUpdate = false;
		}

		void handleMouse(int x, int y) {
			float minValue = ColorSelector.this.colorSpace.getMinValue(this.component);
			float maxValue = ColorSelector.this.colorSpace.getMaxValue(this.component);
			int innerHeight = this.getInnerHeight();
			int pos = Math.max(0, Math.min(innerHeight, y));
			float value = maxValue + (minValue - maxValue) * (float)pos / (float)innerHeight;
			ColorSelector.this.colorValueModels[this.component].setValue(value);
		}
	}

	class ColorArea2D extends ColorSelector.ColorArea {
		private final int componentX;
		private final int componentY;

		public ColorArea2D(int componentX, int componentY) {
			super();
			this.componentX = componentX;
			this.componentY = componentY;
			int i = 0;

			for(int n = ColorSelector.this.getNumComponents(); i < n; ++i) {
				if(i != componentX && i != componentY) {
					ColorSelector.this.colorValueModels[i].addCallback(this);
				}
			}

		}

		protected void paintWidget(GUI gui) {
			super.paintWidget(gui);
			if(this.cursorImage != null) {
				float minValueX = ColorSelector.this.colorSpace.getMinValue(this.componentX);
				float maxValueX = ColorSelector.this.colorSpace.getMaxValue(this.componentX);
				float minValueY = ColorSelector.this.colorSpace.getMinValue(this.componentY);
				float maxValueY = ColorSelector.this.colorSpace.getMaxValue(this.componentY);
				int posX = (int)((ColorSelector.this.colorValues[this.componentX] - maxValueX) * (float)(this.getInnerWidth() - 1) / (minValueX - maxValueX) + 0.5F);
				int posY = (int)((ColorSelector.this.colorValues[this.componentY] - maxValueY) * (float)(this.getInnerHeight() - 1) / (minValueY - maxValueY) + 0.5F);
				this.cursorImage.draw(this.getAnimationState(), this.getInnerX() + posX, this.getInnerY() + posY, 1, 1);
			}

		}

		protected void createImage(GUI gui) {
			this.img = gui.getRenderer().createDynamicImage(64, 64);
		}

		protected void updateImage() {
			float[] temp = (float[])ColorSelector.this.colorValues.clone();
			IntBuffer buf = ColorSelector.this.imgDataInt;
			ColorSpace cs = ColorSelector.this.colorSpace;
			float x0 = cs.getMaxValue(this.componentX);
			float dx = (cs.getMinValue(this.componentX) - x0) / 63.0F;
			float y = cs.getMaxValue(this.componentY);
			float dy = (cs.getMinValue(this.componentY) - y) / 63.0F;
			int i = 0;

			for(int idx = 0; i < 64; ++i) {
				temp[this.componentY] = y;
				float x = x0;

				for(int j = 0; j < 64; ++j) {
					temp[this.componentX] = x;
					buf.put(idx++, cs.toRGB(temp) << 8 | 255);
					x += dx;
				}

				y += dy;
			}

			this.img.update(ColorSelector.this.imgData, DynamicImage.Format.RGBA);
			this.needsUpdate = false;
		}

		void handleMouse(int x, int y) {
			float minValueX = ColorSelector.this.colorSpace.getMinValue(this.componentX);
			float maxValueX = ColorSelector.this.colorSpace.getMaxValue(this.componentX);
			float minValueY = ColorSelector.this.colorSpace.getMinValue(this.componentY);
			float maxValueY = ColorSelector.this.colorSpace.getMaxValue(this.componentY);
			int innerWidtht = this.getInnerWidth();
			int innerHeight = this.getInnerHeight();
			int posX = Math.max(0, Math.min(innerWidtht, x));
			int posY = Math.max(0, Math.min(innerHeight, y));
			float valueX = maxValueX + (minValueX - maxValueX) * (float)posX / (float)innerWidtht;
			float valueY = maxValueY + (minValueY - maxValueY) * (float)posY / (float)innerHeight;
			ColorSelector.this.colorValueModels[this.componentX].setValue(valueX);
			ColorSelector.this.colorValueModels[this.componentY].setValue(valueY);
		}
	}

	class ColorValueModel extends AbstractFloatModel {
		private final int component;

		public ColorValueModel(int component) {
			this.component = component;
		}

		public float getMaxValue() {
			return ColorSelector.this.colorSpace.getMaxValue(this.component);
		}

		public float getMinValue() {
			return ColorSelector.this.colorSpace.getMinValue(this.component);
		}

		public float getValue() {
			return ColorSelector.this.colorValues[this.component];
		}

		public void setValue(float value) {
			ColorSelector.this.colorValues[this.component] = value;
			this.doCallback();
			ColorSelector.this.colorChanged();
		}

		void fireCallback() {
			this.doCallback();
		}
	}
}
