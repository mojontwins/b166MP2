package net.minecraft.client.model;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;

public class ModelSilverfish extends ModelBase {
	private ModelRenderer[] silverfishBodyParts = new ModelRenderer[7];
	private ModelRenderer[] silverfishWings;
	private float[] field_35399_c = new float[7];
	private static final int[][] silverfishBoxLength = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
	private static final int[][] silverfishTexturePositions = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

	public ModelSilverfish() {
		float f1 = -3.5F;

		for(int i2 = 0; i2 < this.silverfishBodyParts.length; ++i2) {
			this.silverfishBodyParts[i2] = new ModelRenderer(this, silverfishTexturePositions[i2][0], silverfishTexturePositions[i2][1]);
			this.silverfishBodyParts[i2].addBox((float)silverfishBoxLength[i2][0] * -0.5F, 0.0F, (float)silverfishBoxLength[i2][2] * -0.5F, silverfishBoxLength[i2][0], silverfishBoxLength[i2][1], silverfishBoxLength[i2][2]);
			this.silverfishBodyParts[i2].setRotationPoint(0.0F, (float)(24 - silverfishBoxLength[i2][1]), f1);
			this.field_35399_c[i2] = f1;
			if(i2 < this.silverfishBodyParts.length - 1) {
				f1 += (float)(silverfishBoxLength[i2][2] + silverfishBoxLength[i2 + 1][2]) * 0.5F;
			}
		}

		this.silverfishWings = new ModelRenderer[3];
		this.silverfishWings[0] = new ModelRenderer(this, 20, 0);
		this.silverfishWings[0].addBox(-5.0F, 0.0F, (float)silverfishBoxLength[2][2] * -0.5F, 10, 8, silverfishBoxLength[2][2]);
		this.silverfishWings[0].setRotationPoint(0.0F, 16.0F, this.field_35399_c[2]);
		this.silverfishWings[1] = new ModelRenderer(this, 20, 11);
		this.silverfishWings[1].addBox(-3.0F, 0.0F, (float)silverfishBoxLength[4][2] * -0.5F, 6, 4, silverfishBoxLength[4][2]);
		this.silverfishWings[1].setRotationPoint(0.0F, 20.0F, this.field_35399_c[4]);
		this.silverfishWings[2] = new ModelRenderer(this, 20, 18);
		this.silverfishWings[2].addBox(-3.0F, 0.0F, (float)silverfishBoxLength[4][2] * -0.5F, 6, 5, silverfishBoxLength[1][2]);
		this.silverfishWings[2].setRotationPoint(0.0F, 19.0F, this.field_35399_c[1]);
	}

	public void render(Entity entity1, float f2, float f3, float f4, float f5, float f6, float f7) {
		this.setRotationAngles(f2, f3, f4, f5, f6, f7);

		int i8;
		for(i8 = 0; i8 < this.silverfishBodyParts.length; ++i8) {
			this.silverfishBodyParts[i8].render(f7);
		}

		for(i8 = 0; i8 < this.silverfishWings.length; ++i8) {
			this.silverfishWings[i8].render(f7);
		}

	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
		for(int i7 = 0; i7 < this.silverfishBodyParts.length; ++i7) {
			this.silverfishBodyParts[i7].rotateAngleY = MathHelper.cos(f3 * 0.9F + (float)i7 * 0.15F * (float)Math.PI) * (float)Math.PI * 0.05F * (float)(1 + Math.abs(i7 - 2));
			this.silverfishBodyParts[i7].rotationPointX = MathHelper.sin(f3 * 0.9F + (float)i7 * 0.15F * (float)Math.PI) * (float)Math.PI * 0.2F * (float)Math.abs(i7 - 2);
		}

		this.silverfishWings[0].rotateAngleY = this.silverfishBodyParts[2].rotateAngleY;
		this.silverfishWings[1].rotateAngleY = this.silverfishBodyParts[4].rotateAngleY;
		this.silverfishWings[1].rotationPointX = this.silverfishBodyParts[4].rotationPointX;
		this.silverfishWings[2].rotateAngleY = this.silverfishBodyParts[1].rotateAngleY;
		this.silverfishWings[2].rotationPointX = this.silverfishBodyParts[1].rotationPointX;
	}
}
