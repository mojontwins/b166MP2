package net.minecraft.src;

public class TileEntityRecordPlayer extends TileEntity {
	public int field_28009_a;

	public void readFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readFromNBT(nBTTagCompound1);
		this.field_28009_a = nBTTagCompound1.getInteger("Record");
	}

	public void writeToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeToNBT(nBTTagCompound1);
		if(this.field_28009_a > 0) {
			nBTTagCompound1.setInteger("Record", this.field_28009_a);
		}

	}
}
