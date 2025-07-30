package net.minecraft.client.model;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.TextureOffset;

public class ModelRenderer {
	public float textureWidth;
	public float textureHeight;
	private int textureOffsetX;
	private int textureOffsetY;
	public float rotationPointX;
	public float rotationPointY;
	public float rotationPointZ;
	public float rotateAngleX;
	public float rotateAngleY;
	public float rotateAngleZ;
	private boolean compiled;
	private int displayList;
	public boolean mirror;
	public boolean showModel;
	public boolean isHidden;
	public List<ModelBox> cubeList;
	public List<ModelRenderer> childModels;
	public final String boxName;
	private ModelBase baseModel;

	public ModelRenderer(ModelBase modelBase1, String string2) {
		this.textureWidth = 64.0F;
		this.textureHeight = 32.0F;
		this.compiled = false;
		this.displayList = 0;
		this.mirror = false;
		this.showModel = true;
		this.isHidden = false;
		this.cubeList = new ArrayList<ModelBox>();
		this.baseModel = modelBase1;
		modelBase1.boxList.add(this);
		this.boxName = string2;
		this.setTextureSize(modelBase1.textureWidth, modelBase1.textureHeight);
	}

	public ModelRenderer(ModelBase modelBase1) {
		this(modelBase1, (String)null);
	}

	public ModelRenderer(ModelBase modelBase1, int i2, int i3) {
		this(modelBase1);
		this.setTextureOffset(i2, i3);
	}

	public void addChild(ModelRenderer modelRenderer1) {
		if(this.childModels == null) {
			this.childModels = new ArrayList<ModelRenderer>();
		}

		this.childModels.add(modelRenderer1);
	}

	public ModelRenderer setTextureOffset(int i1, int i2) {
		this.textureOffsetX = i1;
		this.textureOffsetY = i2;
		return this;
	}

	public ModelRenderer addBox(String string1, float f2, float f3, float f4, int i5, int i6, int i7) {
		string1 = this.boxName + "." + string1;
		TextureOffset textureOffset8 = this.baseModel.getTextureOffset(string1);
		this.setTextureOffset(textureOffset8.u, textureOffset8.v);
		this.cubeList.add((new ModelBox(this, this.textureOffsetX, this.textureOffsetY, f2, f3, f4, i5, i6, i7, 0.0F)).func_40671_a(string1));
		return this;
	}

	public ModelRenderer addBox(float f1, float f2, float f3, int i4, int i5, int i6) {
		this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, f1, f2, f3, i4, i5, i6, 0.0F));
		return this;
	}

	public void addBox(float f1, float f2, float f3, int i4, int i5, int i6, float f7) {
		this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, f1, f2, f3, i4, i5, i6, f7));
	}

	public void setRotationPoint(float f1, float f2, float f3) {
		this.rotationPointX = f1;
		this.rotationPointY = f2;
		this.rotationPointZ = f3;
	}

	public void render(float f1) {
		if(!this.isHidden) {
			if(this.showModel) {
				if(!this.compiled) {
					this.compileDisplayList(f1);
				}

				int i2;
				if(this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
					if(this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F) {
						GL11.glCallList(this.displayList);
						if(this.childModels != null) {
							for(i2 = 0; i2 < this.childModels.size(); ++i2) {
								((ModelRenderer)this.childModels.get(i2)).render(f1);
							}
						}
					} else {
						GL11.glTranslatef(this.rotationPointX * f1, this.rotationPointY * f1, this.rotationPointZ * f1);
						GL11.glCallList(this.displayList);
						if(this.childModels != null) {
							for(i2 = 0; i2 < this.childModels.size(); ++i2) {
								((ModelRenderer)this.childModels.get(i2)).render(f1);
							}
						}

						GL11.glTranslatef(-this.rotationPointX * f1, -this.rotationPointY * f1, -this.rotationPointZ * f1);
					}
				} else {
					GL11.glPushMatrix();
					GL11.glTranslatef(this.rotationPointX * f1, this.rotationPointY * f1, this.rotationPointZ * f1);
					if(this.rotateAngleZ != 0.0F) {
						GL11.glRotatef(this.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
					}

					if(this.rotateAngleY != 0.0F) {
						GL11.glRotatef(this.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
					}

					if(this.rotateAngleX != 0.0F) {
						GL11.glRotatef(this.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);
					}

					GL11.glCallList(this.displayList);
					if(this.childModels != null) {
						for(i2 = 0; i2 < this.childModels.size(); ++i2) {
							((ModelRenderer)this.childModels.get(i2)).render(f1);
						}
					}

					GL11.glPopMatrix();
				}

			}
		}
	}

	public void renderWithRotation(float f1) {
		if(!this.isHidden) {
			if(this.showModel) {
				if(!this.compiled) {
					this.compileDisplayList(f1);
				}

				GL11.glPushMatrix();
				GL11.glTranslatef(this.rotationPointX * f1, this.rotationPointY * f1, this.rotationPointZ * f1);
				if(this.rotateAngleY != 0.0F) {
					GL11.glRotatef(this.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
				}

				if(this.rotateAngleX != 0.0F) {
					GL11.glRotatef(this.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);
				}

				if(this.rotateAngleZ != 0.0F) {
					GL11.glRotatef(this.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
				}

				GL11.glCallList(this.displayList);
				GL11.glPopMatrix();
			}
		}
	}

	public void postRender(float f1) {
		if(!this.isHidden) {
			if(this.showModel) {
				if(!this.compiled) {
					this.compileDisplayList(f1);
				}

				if(this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
					if(this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
						GL11.glTranslatef(this.rotationPointX * f1, this.rotationPointY * f1, this.rotationPointZ * f1);
					}
				} else {
					GL11.glTranslatef(this.rotationPointX * f1, this.rotationPointY * f1, this.rotationPointZ * f1);
					if(this.rotateAngleZ != 0.0F) {
						GL11.glRotatef(this.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
					}

					if(this.rotateAngleY != 0.0F) {
						GL11.glRotatef(this.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
					}

					if(this.rotateAngleX != 0.0F) {
						GL11.glRotatef(this.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);
					}
				}

			}
		}
	}

	private void compileDisplayList(float f1) {
		this.displayList = GLAllocation.generateDisplayLists(1);
		GL11.glNewList(this.displayList, GL11.GL_COMPILE);
		Tessellator tessellator2 = Tessellator.instance;

		for(int i3 = 0; i3 < this.cubeList.size(); ++i3) {
			((ModelBox)this.cubeList.get(i3)).render(tessellator2, f1);
		}

		GL11.glEndList();
		this.compiled = true;
	}

	public ModelRenderer setTextureSize(int i1, int i2) {
		this.textureWidth = (float)i1;
		this.textureHeight = (float)i2;
		return this;
	}
}
