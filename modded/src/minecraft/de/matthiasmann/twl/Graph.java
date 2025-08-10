package de.matthiasmann.twl;

import de.matthiasmann.twl.model.GraphLineModel;
import de.matthiasmann.twl.model.GraphModel;
import de.matthiasmann.twl.renderer.LineRenderer;
import de.matthiasmann.twl.utils.TextUtil;

import java.util.Arrays;

public class Graph extends Widget {
	private final Graph.GraphArea area;
	GraphModel model;
	private ParameterMap themeLineStyles;
	private int sizeMultipleX;
	private int sizeMultipleY;
	Graph.LineStyle[] lineStyles;
	private float[] renderXYBuffer;
	private static final float EPSILON = 1.0E-4F;

	public Graph() {
		this.sizeMultipleX = 1;
		this.sizeMultipleY = 1;
		this.lineStyles = new Graph.LineStyle[8];
		this.renderXYBuffer = new float[128];
		this.area = new Graph.GraphArea();
		this.area.setClip(true);
		this.add(this.area);
	}

	public Graph(GraphModel model) {
		this();
		this.setModel(model);
	}

	public GraphModel getModel() {
		return this.model;
	}

	public void setModel(GraphModel model) {
		this.model = model;
		this.invalidateLineStyles();
	}

	public int getSizeMultipleX() {
		return this.sizeMultipleX;
	}

	public void setSizeMultipleX(int sizeMultipleX) {
		if(sizeMultipleX < 1) {
			throw new IllegalArgumentException("sizeMultipleX must be >= 1");
		} else {
			this.sizeMultipleX = sizeMultipleX;
		}
	}

	public int getSizeMultipleY() {
		return this.sizeMultipleY;
	}

	public void setSizeMultipleY(int sizeMultipleY) {
		if(sizeMultipleY < 1) {
			throw new IllegalArgumentException("sizeMultipleX must be >= 1");
		} else {
			this.sizeMultipleY = sizeMultipleY;
		}
	}

	protected void applyTheme(ThemeInfo themeInfo) {
		super.applyTheme(themeInfo);
		this.applyThemeGraph(themeInfo);
	}

	protected void applyThemeGraph(ThemeInfo themeInfo) {
		this.themeLineStyles = themeInfo.getParameterMap("lineStyles");
		this.setSizeMultipleX(themeInfo.getParameter("sizeMultipleX", 1));
		this.setSizeMultipleY(themeInfo.getParameter("sizeMultipleY", 1));
		this.invalidateLineStyles();
	}

	protected void invalidateLineStyles() {
		Arrays.fill(this.lineStyles, (Object)null);
	}

	void syncLineStyles() {
		int numLines = this.model.getNumLines();
		if(this.lineStyles.length < numLines) {
			Graph.LineStyle[] i = new Graph.LineStyle[numLines];
			System.arraycopy(this.lineStyles, 0, i, 0, this.lineStyles.length);
			this.lineStyles = i;
		}

		for(int i7 = 0; i7 < numLines; ++i7) {
			GraphLineModel line = this.model.getLine(i7);
			Graph.LineStyle style = this.lineStyles[i7];
			if(style == null) {
				style = new Graph.LineStyle();
				this.lineStyles[i7] = style;
			}

			String visualStyle = TextUtil.notNull(line.getVisualStyleName());
			if(!style.name.equals(visualStyle)) {
				ParameterMap lineStyle = null;
				if(this.themeLineStyles != null) {
					lineStyle = this.themeLineStyles.getParameterMap(visualStyle);
				}

				style.setStyleName(visualStyle, lineStyle);
			}
		}

	}

	void renderLine(LineRenderer lineRenderer, GraphLineModel line, float minValue, float maxValue, Graph.LineStyle style) {
		int numPoints = line.getNumPoints();
		if(numPoints > 0) {
			if(this.renderXYBuffer.length < numPoints * 2) {
				this.renderXYBuffer = new float[numPoints * 2];
			}

			float[] xy = this.renderXYBuffer;
			float delta = maxValue - minValue;
			if(Math.abs(delta) < 1.0E-4F) {
				delta = copySign(1.0E-4F, delta);
			}

			float yscale = (float)(-this.getInnerHeight()) / delta;
			float yoff = (float)this.getInnerBottom();
			float xscale = (float)this.getInnerWidth() / (float)Math.max(1, numPoints - 1);
			float xoff = (float)this.getInnerX();

			for(int i = 0; i < numPoints; ++i) {
				float value = line.getPoint(i);
				xy[i * 2 + 0] = (float)i * xscale + xoff;
				xy[i * 2 + 1] = (value - minValue) * yscale + yoff;
			}

			if(numPoints == 1) {
				xy[2] = xoff + xscale;
				xy[3] = xy[1];
				numPoints = 2;
			}

			lineRenderer.drawLine(xy, numPoints, style.lineWidth, style.color, false);
		}
	}

	private static float copySign(float magnitude, float sign) {
		int rawMagnitude = Float.floatToRawIntBits(magnitude);
		int rawSign = Float.floatToRawIntBits(sign);
		int rawResult = rawMagnitude | rawSign & Integer.MIN_VALUE;
		return Float.intBitsToFloat(rawResult);
	}

	public boolean setSize(int width, int height) {
		return super.setSize(round(width, this.sizeMultipleX), round(height, this.sizeMultipleY));
	}

	private static int round(int value, int grid) {
		return value - value % grid;
	}

	protected void layout() {
		this.layoutChildFullInnerArea(this.area);
	}

	class GraphArea extends Widget {
		protected void paintWidget(GUI gui) {
			if(Graph.this.model != null) {
				Graph.this.syncLineStyles();
				LineRenderer lineRenderer = gui.getRenderer().getLineRenderer();
				int numLines = Graph.this.model.getNumLines();
				boolean independantScale = Graph.this.model.getScaleLinesIndependant();
				float minValue = Float.MAX_VALUE;
				float maxValue = -3.4028235E38F;
				int i;
				GraphLineModel line;
				if(independantScale) {
					for(i = 0; i < numLines; ++i) {
						line = Graph.this.model.getLine(i);
						minValue = Math.min(minValue, line.getMinValue());
						maxValue = Math.max(maxValue, line.getMaxValue());
					}
				}

				for(i = 0; i < numLines; ++i) {
					line = Graph.this.model.getLine(i);
					Graph.LineStyle style = Graph.this.lineStyles[i];
					if(independantScale) {
						Graph.this.renderLine(lineRenderer, line, minValue, maxValue, style);
					} else {
						Graph.this.renderLine(lineRenderer, line, line.getMinValue(), line.getMaxValue(), style);
					}
				}
			}

		}
	}

	static class LineStyle {
		String name = "";
		Color color = Color.WHITE;
		float lineWidth = 1.0F;

		void setStyleName(String name, ParameterMap lineStyle) {
			this.name = name;
			if(lineStyle != null) {
				this.color = lineStyle.getParameter("color", Color.WHITE);
				this.lineWidth = Math.max(1.0E-4F, lineStyle.getParameter("width", 1.0F));
			}

		}
	}
}
