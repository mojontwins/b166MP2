package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import net.minecraft.network.NetHandler;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ExtendedBlockStorage;
import net.minecraft.world.level.chunk.NibbleArray;

public class Packet51MapChunk extends Packet {
	public int xCh;
	public int zCh;
	public int yChMin;
	public int yChMax;
	public byte[] chunkData;
	public boolean includeInitialize;
	private int tempLength;
	private int field_48178_h;
	private static byte[] temp = new byte[0];

	public Packet51MapChunk() {
		this.isChunkDataPacket = true;
	}

	public Packet51MapChunk(Chunk chunk1, boolean z2, int i3) {
		this.isChunkDataPacket = true;
		this.xCh = chunk1.xPosition;
		this.zCh = chunk1.zPosition;
		this.includeInitialize = z2;
		if(z2) {
			i3 = 65535;
			chunk1.field_50120_o = true;
		}

		ExtendedBlockStorage[] extendedBlockStorage4 = chunk1.getBlockStorageArray();
		int i5 = 0;
		int i6 = 0;

		int i7;
		for(i7 = 0; i7 < extendedBlockStorage4.length; ++i7) {
			if(extendedBlockStorage4[i7] != null && (!z2 || !extendedBlockStorage4[i7].getIsEmpty()) && (i3 & 1 << i7) != 0) {
				this.yChMin |= 1 << i7;
				++i5;
				if(extendedBlockStorage4[i7].getBlockMSBArray() != null) {
					this.yChMax |= 1 << i7;
					++i6;
				}
			}
		}

		i7 = 2048 * (5 * i5 + i6);
		if(z2) {
			i7 += 256;
		}

		if(temp.length < i7) {
			temp = new byte[i7];
		}

		byte[] b8 = temp;
		int i9 = 0;

		int i10;
		for(i10 = 0; i10 < extendedBlockStorage4.length; ++i10) {
			if(extendedBlockStorage4[i10] != null && (!z2 || !extendedBlockStorage4[i10].getIsEmpty()) && (i3 & 1 << i10) != 0) {
				byte[] b11 = extendedBlockStorage4[i10].getBlockLSBArray();
				System.arraycopy(b11, 0, b8, i9, b11.length);
				i9 += b11.length;
			}
		}

		NibbleArray nibbleArray15;
		for(i10 = 0; i10 < extendedBlockStorage4.length; ++i10) {
			if(extendedBlockStorage4[i10] != null && (!z2 || !extendedBlockStorage4[i10].getIsEmpty()) && (i3 & 1 << i10) != 0) {
				nibbleArray15 = extendedBlockStorage4[i10].getMetadataArray();
				System.arraycopy(nibbleArray15.data, 0, b8, i9, nibbleArray15.data.length);
				i9 += nibbleArray15.data.length;
			}
		}

		for(i10 = 0; i10 < extendedBlockStorage4.length; ++i10) {
			if(extendedBlockStorage4[i10] != null && (!z2 || !extendedBlockStorage4[i10].getIsEmpty()) && (i3 & 1 << i10) != 0) {
				nibbleArray15 = extendedBlockStorage4[i10].getBlocklightArray();
				System.arraycopy(nibbleArray15.data, 0, b8, i9, nibbleArray15.data.length);
				i9 += nibbleArray15.data.length;
			}
		}

		for(i10 = 0; i10 < extendedBlockStorage4.length; ++i10) {
			if(extendedBlockStorage4[i10] != null && (!z2 || !extendedBlockStorage4[i10].getIsEmpty()) && (i3 & 1 << i10) != 0) {
				nibbleArray15 = extendedBlockStorage4[i10].getSkylightArray();
				System.arraycopy(nibbleArray15.data, 0, b8, i9, nibbleArray15.data.length);
				i9 += nibbleArray15.data.length;
			}
		}

		if(i6 > 0) {
			for(i10 = 0; i10 < extendedBlockStorage4.length; ++i10) {
				if(extendedBlockStorage4[i10] != null && (!z2 || !extendedBlockStorage4[i10].getIsEmpty()) && extendedBlockStorage4[i10].getBlockMSBArray() != null && (i3 & 1 << i10) != 0) {
					nibbleArray15 = extendedBlockStorage4[i10].getBlockMSBArray();
					System.arraycopy(nibbleArray15.data, 0, b8, i9, nibbleArray15.data.length);
					i9 += nibbleArray15.data.length;
				}
			}
		}

		if(z2) {
			byte[] b16 = chunk1.getBiomeArray();
			System.arraycopy(b16, 0, b8, i9, b16.length);
			i9 += b16.length;
		}

		Deflater deflater17 = new Deflater(-1);

		try {
			deflater17.setInput(b8, 0, i9);
			deflater17.finish();
			this.chunkData = new byte[i9];
			this.tempLength = deflater17.deflate(this.chunkData);
		} finally {
			deflater17.end();
		}

	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.xCh = dataInputStream1.readInt();
		this.zCh = dataInputStream1.readInt();
		this.includeInitialize = dataInputStream1.readBoolean();
		this.yChMin = dataInputStream1.readShort();
		this.yChMax = dataInputStream1.readShort();
		this.tempLength = dataInputStream1.readInt();
		this.field_48178_h = dataInputStream1.readInt();
		if(temp.length < this.tempLength) {
			temp = new byte[this.tempLength];
		}

		dataInputStream1.readFully(temp, 0, this.tempLength);
		int i2 = 0;

		int i3;
		for(i3 = 0; i3 < 16; ++i3) {
			i2 += this.yChMin >> i3 & 1;
		}

		i3 = 12288 * i2;
		if(this.includeInitialize) {
			i3 += 256;
		}

		this.chunkData = new byte[i3];
		Inflater inflater4 = new Inflater();
		inflater4.setInput(temp, 0, this.tempLength);

		try {
			inflater4.inflate(this.chunkData);
		} catch (DataFormatException dataFormatException9) {
			throw new IOException("Bad compressed data format");
		} finally {
			inflater4.end();
		}

	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.xCh);
		dataOutputStream1.writeInt(this.zCh);
		dataOutputStream1.writeBoolean(this.includeInitialize);
		dataOutputStream1.writeShort((short)(this.yChMin & 65535));
		dataOutputStream1.writeShort((short)(this.yChMax & 65535));
		dataOutputStream1.writeInt(this.tempLength);
		dataOutputStream1.writeInt(this.field_48178_h);
		dataOutputStream1.write(this.chunkData, 0, this.tempLength);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleMapChunk(this);
	}

	public int getPacketSize() {
		return 17 + this.tempLength;
	}
}
