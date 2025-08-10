package net.minecraft.src;

public class RenderCaveOgre extends RenderOgre {
	private ModelOgre2 tempOgre;

	public RenderCaveOgre(ModelOgre2 modelOgre21, ModelBase modelBase2, float f3) {
		super(modelOgre21, modelBase2, f3);
		this.setRenderPassModel(modelBase2);
		this.tempOgre = modelOgre21;
	}

	protected boolean a(EntityOgre entityOgre1, int i2) {
		this.loadTexture("/mob/caveogreb.png");
		return i2 == 0 && !entityOgre1.ogreboolean;
	}
}
