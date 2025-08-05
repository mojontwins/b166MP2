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
	public int chunkX;
	public int chunkZ;
	public int usedSubchunks;
	public int blockMSBSubchunks;
	public byte[] chunkData;
	public boolean includeInitialize;
	private int compressedDataLength;
	private int unusedField;
	private static byte[] temp = new byte[0];

	public Packet51MapChunk() {
		this.isChunkDataPacket = true;
	}

	public Packet51MapChunk(Chunk chunk, boolean isNewChunk, int updateHash) {
		// updateHash is a bitfield, with 1 bit per chunk subsection which must be updated.

		// A whole chunk, in vanilla, took:
		// 32K low data
		// 16K high data
		// 16K meta
		// 32K lights
		
		this.isChunkDataPacket = true;
		this.chunkX = chunk.xPosition;
		this.chunkZ = chunk.zPosition;
		this.includeInitialize = isNewChunk;
		if(isNewChunk) {
			updateHash = 65535;
			chunk.field_50120_o = true;
		}

		ExtendedBlockStorage[] subChunks = chunk.getBlockStorageArray();
		int blockLSBsubChunks = 0;
		int blockMSBsubChunks = 0;

		for(int i = 0; i < subChunks.length; ++i) {
			if(subChunks[i] != null && (!isNewChunk || !subChunks[i].getIsEmpty()) && (updateHash & 1 << i) != 0) {
				this.usedSubchunks |= 1 << i;
				++blockLSBsubChunks;
				if(subChunks[i].getBlockMSBArray() != null) {
					this.blockMSBSubchunks |= 1 << i;
					++blockMSBsubChunks;
				}
			}
		}

		// blockLSBsubChunks is the amount of subchunks with normal data (low id, meta, light)
		// each subchunk use 6 nibbles per block: 2 LSB, 1 meta, 2 light, 1 MSB
		int byteLength = 2048 * (5 * blockLSBsubChunks + blockMSBsubChunks);
		if(isNewChunk) {
			byteLength += 256;
		}

		if(temp.length < byteLength) {
			temp = new byte[byteLength];
		}

		byte[] rawBytes = temp;
		int totalSize = 0;

		int i;
		for(i = 0; i < subChunks.length; ++i) {
			if(subChunks[i] != null && (!isNewChunk || !subChunks[i].getIsEmpty()) && (updateHash & 1 << i) != 0) {
				byte[] data = subChunks[i].getBlockLSBArray();
				System.arraycopy(data, 0, rawBytes, totalSize, data.length);
				totalSize += data.length;
			}
		}
		
		NibbleArray nibbles;
		
		for(i = 0; i < subChunks.length; ++i) {
			if(subChunks[i] != null && (!isNewChunk || !subChunks[i].getIsEmpty()) && (updateHash & 1 << i) != 0) {
				nibbles = subChunks[i].getMetadataArray();
				System.arraycopy(nibbles.data, 0, rawBytes, totalSize, nibbles.data.length);
				totalSize += nibbles.data.length;
			}
		}
		
		for(i = 0; i < subChunks.length; ++i) {
			if(subChunks[i] != null && (!isNewChunk || !subChunks[i].getIsEmpty()) && (updateHash & 1 << i) != 0) {
				nibbles = subChunks[i].getBlocklightArray();
				System.arraycopy(nibbles.data, 0, rawBytes, totalSize, nibbles.data.length);
				totalSize += nibbles.data.length;
			}
		}

		for(i = 0; i < subChunks.length; ++i) {
			if(subChunks[i] != null && (!isNewChunk || !subChunks[i].getIsEmpty()) && (updateHash & 1 << i) != 0) {
				nibbles = subChunks[i].getSkylightArray();
				System.arraycopy(nibbles.data, 0, rawBytes, totalSize, nibbles.data.length);
				totalSize += nibbles.data.length;
			}
		}

		if(blockMSBsubChunks > 0) {
			for(i = 0; i < subChunks.length; ++i) {
				if(subChunks[i] != null && (!isNewChunk || !subChunks[i].getIsEmpty()) && subChunks[i].getBlockMSBArray() != null && (updateHash & 1 << i) != 0) {
					nibbles = subChunks[i].getBlockMSBArray();
					System.arraycopy(nibbles.data, 0, rawBytes, totalSize, nibbles.data.length);
					totalSize += nibbles.data.length;
				}
			}
		}

		if(isNewChunk) {
			byte[] biomes = chunk.getBiomeArray();
			System.arraycopy(biomes, 0, rawBytes, totalSize, biomes.length);
			totalSize += biomes.length;
		}

		Deflater deflater = new Deflater(-1);

		try {
			deflater.setInput(rawBytes, 0, totalSize);
			deflater.finish();
			this.chunkData = new byte[totalSize];
			this.compressedDataLength = deflater.deflate(this.chunkData);
		} finally {
			deflater.end();
		}

	}

	public void readPacketData(DataInputStream dis) throws IOException {
		this.chunkX = dis.readInt();
		this.chunkZ = dis.readInt();
		this.includeInitialize = dis.readBoolean();
		this.usedSubchunks = dis.readShort();
		this.blockMSBSubchunks = dis.readShort();
		this.compressedDataLength = dis.readInt();
		this.unusedField = dis.readInt();
		if(temp.length < this.compressedDataLength) {
			temp = new byte[this.compressedDataLength];
		}

		dis.readFully(temp, 0, this.compressedDataLength);
		int blockLSBsubChunks = 0;

		for(int i = 0; i < 16; ++i) {
			blockLSBsubChunks += this.usedSubchunks >> i & 1;
		}

		// Is 12288 = 4096 (block lsb) + 4096 (light) + 4096 (meta/block msb)
		int size = 12288 * blockLSBsubChunks;
		if(this.includeInitialize) {
			size += 256;
		}

		this.chunkData = new byte[size];
		Inflater inflater = new Inflater();
		inflater.setInput(temp, 0, this.compressedDataLength);

		try {
			inflater.inflate(this.chunkData);
		} catch (DataFormatException e) {
			throw new IOException("Bad compressed data format");
		} finally {
			inflater.end();
		}

	}

	public void writePacketData(DataOutputStream dos) throws IOException {
		dos.writeInt(this.chunkX);
		dos.writeInt(this.chunkZ);
		dos.writeBoolean(this.includeInitialize);
		dos.writeShort((short)(this.usedSubchunks & 65535));
		dos.writeShort((short)(this.blockMSBSubchunks & 65535));
		dos.writeInt(this.compressedDataLength);
		dos.writeInt(this.unusedField);
		dos.write(this.chunkData, 0, this.compressedDataLength);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleMapChunk(this);
	}

	public int getPacketSize() {
		return 17 + this.compressedDataLength;
	}
}
