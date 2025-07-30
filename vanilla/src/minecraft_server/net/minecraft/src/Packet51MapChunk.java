package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Packet51MapChunk extends Packet {
	public int xPosition;
	public int yPosition;
	public int zPosition;
	public int xSize;
	public int ySize;
	public int zSize;
	public byte[] chunk;
	private int chunkSize;

	public Packet51MapChunk() {
		this.isChunkDataPacket = true;
	}

	public Packet51MapChunk(int i1, int i2, int i3, int i4, int i5, int i6, World world7) {
		this.isChunkDataPacket = true;
		this.xPosition = i1;
		this.yPosition = i2;
		this.zPosition = i3;
		this.xSize = i4;
		this.ySize = i5;
		this.zSize = i6;
		byte[] b8 = world7.getChunkData(i1, i2, i3, i4, i5, i6);
		Deflater deflater9 = new Deflater(-1);

		try {
			deflater9.setInput(b8);
			deflater9.finish();
			this.chunk = new byte[i4 * i5 * i6 * 5 / 2];
			this.chunkSize = deflater9.deflate(this.chunk);
		} finally {
			deflater9.end();
		}

	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.xPosition = dataInputStream1.readInt();
		this.yPosition = dataInputStream1.readShort();
		this.zPosition = dataInputStream1.readInt();
		this.xSize = dataInputStream1.read() + 1;
		this.ySize = dataInputStream1.read() + 1;
		this.zSize = dataInputStream1.read() + 1;
		this.chunkSize = dataInputStream1.readInt();
		byte[] b2 = new byte[this.chunkSize];
		dataInputStream1.readFully(b2);
		this.chunk = new byte[this.xSize * this.ySize * this.zSize * 5 / 2];
		Inflater inflater3 = new Inflater();
		inflater3.setInput(b2);

		try {
			inflater3.inflate(this.chunk);
		} catch (DataFormatException dataFormatException8) {
			throw new IOException("Bad compressed data format");
		} finally {
			inflater3.end();
		}

	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.xPosition);
		dataOutputStream1.writeShort(this.yPosition);
		dataOutputStream1.writeInt(this.zPosition);
		dataOutputStream1.write(this.xSize - 1);
		dataOutputStream1.write(this.ySize - 1);
		dataOutputStream1.write(this.zSize - 1);
		dataOutputStream1.writeInt(this.chunkSize);
		dataOutputStream1.write(this.chunk, 0, this.chunkSize);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleMapChunk(this);
	}

	public int getPacketSize() {
		return 17 + this.chunkSize;
	}
}
