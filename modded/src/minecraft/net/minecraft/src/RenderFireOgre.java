package net.minecraft.src;

public class RenderFireOgre extends RenderOgre {
	private ModelOgre2 tempOgre;

	public RenderFireOgre(ModelOgre2 modelOgre21, ModelBase modelBase2, float f3) {
		super(modelOgre21, modelBase2, f3);
		this.setRenderPassModel(modelBase2);
		this.tempOgre = modelOgre21;
	}

	protected boolean a(EntityOgre entityOgre1, int i2) {
		this.loadTexture("/mob/fireogreb.png");
		return i2 == 0 && !entityOgre1.ogreboolean;
	}
}
