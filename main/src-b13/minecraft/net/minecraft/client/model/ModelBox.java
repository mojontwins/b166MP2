package net.minecraft.client.model;

import net.minecraft.client.renderer.PositionTextureVertex;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.TexturedQuad;

public class ModelBox {
	private PositionTextureVertex[] vertexPositions;
	private TexturedQuad[] quadList;
	public final float posX1;
	public final float posY1;
	public final float posZ1;
	public final float posX2;
	public final float posY2;
	public final float posZ2;
	public String field_40673_g;

	public ModelBox(ModelRenderer modelRenderer1, int i2, int i3, float f4, float f5, float f6, int i7, int i8, int i9, float f10) {
		this.posX1 = f4;
		this.posY1 = f5;
		this.posZ1 = f6;
		this.posX2 = f4 + (float)i7;
		this.posY2 = f5 + (float)i8;
		this.posZ2 = f6 + (float)i9;
		this.vertexPositions = new PositionTextureVertex[8];
		this.quadList = new TexturedQuad[6];
		float f11 = f4 + (float)i7;
		float f12 = f5 + (float)i8;
		float f13 = f6 + (float)i9;
		f4 -= f10;
		f5 -= f10;
		f6 -= f10;
		f11 += f10;
		f12 += f10;
		f13 += f10;
		if(modelRenderer1.mirror) {
			float f14 = f11;
			f11 = f4;
			f4 = f14;
		}

		PositionTextureVertex positionTextureVertex23 = new PositionTextureVertex(f4, f5, f6, 0.0F, 0.0F);
		PositionTextureVertex positionTextureVertex15 = new PositionTextureVertex(f11, f5, f6, 0.0F, 8.0F);
		PositionTextureVertex positionTextureVertex16 = new PositionTextureVertex(f11, f12, f6, 8.0F, 8.0F);
		PositionTextureVertex positionTextureVertex17 = new PositionTextureVertex(f4, f12, f6, 8.0F, 0.0F);
		PositionTextureVertex positionTextureVertex18 = new PositionTextureVertex(f4, f5, f13, 0.0F, 0.0F);
		PositionTextureVertex positionTextureVertex19 = new PositionTextureVertex(f11, f5, f13, 0.0F, 8.0F);
		PositionTextureVertex positionTextureVertex20 = new PositionTextureVertex(f11, f12, f13, 8.0F, 8.0F);
		PositionTextureVertex positionTextureVertex21 = new PositionTextureVertex(f4, f12, f13, 8.0F, 0.0F);
		this.vertexPositions[0] = positionTextureVertex23;
		this.vertexPositions[1] = positionTextureVertex15;
		this.vertexPositions[2] = positionTextureVertex16;
		this.vertexPositions[3] = positionTextureVertex17;
		this.vertexPositions[4] = positionTextureVertex18;
		this.vertexPositions[5] = positionTextureVertex19;
		this.vertexPositions[6] = positionTextureVertex20;
		this.vertexPositions[7] = positionTextureVertex21;
		this.quadList[0] = new TexturedQuad(new PositionTextureVertex[]{positionTextureVertex19, positionTextureVertex15, positionTextureVertex16, positionTextureVertex20}, i2 + i9 + i7, i3 + i9, i2 + i9 + i7 + i9, i3 + i9 + i8, modelRenderer1.textureWidth, modelRenderer1.textureHeight);
		this.quadList[1] = new TexturedQuad(new PositionTextureVertex[]{positionTextureVertex23, positionTextureVertex18, positionTextureVertex21, positionTextureVertex17}, i2, i3 + i9, i2 + i9, i3 + i9 + i8, modelRenderer1.textureWidth, modelRenderer1.textureHeight);
		this.quadList[2] = new TexturedQuad(new PositionTextureVertex[]{positionTextureVertex19, positionTextureVertex18, positionTextureVertex23, positionTextureVertex15}, i2 + i9, i3, i2 + i9 + i7, i3 + i9, modelRenderer1.textureWidth, modelRenderer1.textureHeight);
		this.quadList[3] = new TexturedQuad(new PositionTextureVertex[]{positionTextureVertex16, positionTextureVertex17, positionTextureVertex21, positionTextureVertex20}, i2 + i9 + i7, i3 + i9, i2 + i9 + i7 + i7, i3, modelRenderer1.textureWidth, modelRenderer1.textureHeight);
		this.quadList[4] = new TexturedQuad(new PositionTextureVertex[]{positionTextureVertex15, positionTextureVertex23, positionTextureVertex17, positionTextureVertex16}, i2 + i9, i3 + i9, i2 + i9 + i7, i3 + i9 + i8, modelRenderer1.textureWidth, modelRenderer1.textureHeight);
		this.quadList[5] = new TexturedQuad(new PositionTextureVertex[]{positionTextureVertex18, positionTextureVertex19, positionTextureVertex20, positionTextureVertex21}, i2 + i9 + i7 + i9, i3 + i9, i2 + i9 + i7 + i9 + i7, i3 + i9 + i8, modelRenderer1.textureWidth, modelRenderer1.textureHeight);
		if(modelRenderer1.mirror) {
			for(int i22 = 0; i22 < this.quadList.length; ++i22) {
				this.quadList[i22].flipFace();
			}
		}

	}

	public void render(Tessellator tessellator1, float f2) {
		for(int i3 = 0; i3 < this.quadList.length; ++i3) {
			this.quadList[i3].draw(tessellator1, f2);
		}

	}

	public ModelBox func_40671_a(String string1) {
		this.field_40673_g = string1;
		return this;
	}
}
