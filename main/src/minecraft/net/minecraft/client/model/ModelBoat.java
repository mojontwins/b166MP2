package net.minecraft.client.model;

import net.minecraft.world.entity.Entity;

public class ModelBoat extends ModelBase {
	public ModelRenderer[] boatSides = new ModelRenderer[5];

	public ModelBoat() {
		this.boatSides[0] = new ModelRenderer(this, 0, 8);
		this.boatSides[1] = new ModelRenderer(this, 0, 0);
		this.boatSides[2] = new ModelRenderer(this, 0, 0);
		this.boatSides[3] = new ModelRenderer(this, 0, 0);
		this.boatSides[4] = new ModelRenderer(this, 0, 0);
		byte b1 = 24;
		byte b2 = 6;
		byte b3 = 20;
		byte b4 = 4;
		this.boatSides[0].addBox((float)(-b1 / 2), (float)(-b3 / 2 + 2), -3.0F, b1, b3 - 4, 4, 0.0F);
		this.boatSides[0].setRotationPoint(0.0F, (float)b4, 0.0F);
		this.boatSides[1].addBox((float)(-b1 / 2 + 2), (float)(-b2 - 1), -1.0F, b1 - 4, b2, 2, 0.0F);
		this.boatSides[1].setRotationPoint((float)(-b1 / 2 + 1), (float)b4, 0.0F);
		this.boatSides[2].addBox((float)(-b1 / 2 + 2), (float)(-b2 - 1), -1.0F, b1 - 4, b2, 2, 0.0F);
		this.boatSides[2].setRotationPoint((float)(b1 / 2 - 1), (float)b4, 0.0F);
		this.boatSides[3].addBox((float)(-b1 / 2 + 2), (float)(-b2 - 1), -1.0F, b1 - 4, b2, 2, 0.0F);
		this.boatSides[3].setRotationPoint(0.0F, (float)b4, (float)(-b3 / 2 + 1));
		this.boatSides[4].addBox((float)(-b1 / 2 + 2), (float)(-b2 - 1), -1.0F, b1 - 4, b2, 2, 0.0F);
		this.boatSides[4].setRotationPoint(0.0F, (float)b4, (float)(b3 / 2 - 1));
		this.boatSides[0].rotateAngleX = (float)Math.PI / 2F;
		this.boatSides[1].rotateAngleY = 4.712389F;
		this.boatSides[2].rotateAngleY = (float)Math.PI / 2F;
		this.boatSides[3].rotateAngleY = (float)Math.PI;
	}

	public void render(Entity entity1, float f2, float f3, float f4, float f5, float f6, float f7) {
		for(int i8 = 0; i8 < 5; ++i8) {
			this.boatSides[i8].render(f7);
		}

	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
	}
}
