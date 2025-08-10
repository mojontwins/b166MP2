package ibxm;

public class Sample {
	public String name = "";
	public boolean set_panning;
	public int volume;
	public int panning;
	public int transpose;
	private int loop_start;
	private int loop_length;
	private short[] sample_data;
	private static final int POINT_SHIFT = 4;
	private static final int POINTS = 16;
	private static final int OVERLAP = 8;
	private static final int INTERP_SHIFT = 11;
	private static final int INTERP_BITMASK = 2047;
	private static final short[] sinc_table = new short[]{(short)0, (short)-7, (short)27, (short)-71, (short)142, (short)-227, (short)299, (short)32439, (short)299, (short)-227, (short)142, (short)-71, (short)27, (short)-7, (short)0, (short)0, (short)0, (short)0, (short)-5, (short)36, (short)-142, (short)450, (short)-1439, (short)32224, (short)2302, (short)-974, (short)455, (short)-190, (short)64, (short)-15, (short)2, (short)0, (short)0, (short)6, (short)-33, (short)128, (short)-391, (short)1042, (short)-2894, (short)31584, (short)4540, (short)-1765, (short)786, (short)-318, (short)105, (short)-25, (short)3, (short)0, (short)0, (short)10, (short)-55, (short)204, (short)-597, (short)1533, (short)-4056, (short)30535, (short)6977, (short)-2573, (short)1121, (short)-449, (short)148, (short)-36, (short)5, (short)0, (short)-1, (short)13, (short)-71, (short)261, (short)-757, (short)1916, (short)-4922, (short)29105, (short)9568, (short)-3366, (short)1448, (short)-578, (short)191, (short)-47, (short)7, (short)0, (short)-1, (short)15, (short)-81, (short)300, (short)-870, (short)2185, (short)-5498, (short)27328, (short)12263, (short)-4109, (short)1749, (short)-698, (short)232, (short)-58, (short)9, (short)0, (short)-1, (short)15, (short)-86, (short)322, (short)-936, (short)2343, (short)-5800, (short)25249, (short)15006, (short)-4765, (short)2011, (short)-802, (short)269, (short)-68, (short)10, (short)0, (short)-1, (short)15, (short)-87, (short)328, (short)-957, (short)2394, (short)-5849, (short)22920, (short)17738, (short)-5298, (short)2215, (short)-885, (short)299, (short)-77, (short)12, (short)0, (short)0, (short)14, (short)-83, (short)319, (short)-938, (short)2347, (short)-5671, (short)20396, (short)20396, (short)-5671, (short)2347, (short)-938, (short)319, (short)-83, (short)14, (short)0, (short)0, (short)12, (short)-77, (short)299, (short)-885, (short)2215, (short)-5298, (short)17738, (short)22920, (short)-5849, (short)2394, (short)-957, (short)328, (short)-87, (short)15, (short)-1, (short)0, (short)10, (short)-68, (short)269, (short)-802, (short)2011, (short)-4765, (short)15006, (short)25249, (short)-5800, (short)2343, (short)-936, (short)322, (short)-86, (short)15, (short)-1, (short)0, (short)9, (short)-58, (short)232, (short)-698, (short)1749, (short)-4109, (short)12263, (short)27328, (short)-5498, (short)2185, (short)-870, (short)300, (short)-81, (short)15, (short)-1, (short)0, (short)7, (short)-47, (short)191, (short)-578, (short)1448, (short)-3366, (short)9568, (short)29105, (short)-4922, (short)1916, (short)-757, (short)261, (short)-71, (short)13, (short)-1, (short)0, (short)5, (short)-36, (short)148, (short)-449, (short)1121, (short)-2573, (short)6977, (short)30535, (short)-4056, (short)1533, (short)-597, (short)204, (short)-55, (short)10, (short)0, (short)0, (short)3, (short)-25, (short)105, (short)-318, (short)786, (short)-1765, (short)4540, (short)31584, (short)-2894, (short)1042, (short)-391, (short)128, (short)-33, (short)6, (short)0, (short)0, (short)2, (short)-15, (short)64, (short)-190, (short)455, (short)-974, (short)2302, (short)32224, (short)-1439, (short)450, (short)-142, (short)36, (short)-5, (short)0, (short)0, (short)0, (short)0, (short)-7, (short)27, (short)-71, (short)142, (short)-227, (short)299, (short)32439, (short)299, (short)-227, (short)142, (short)-71, (short)27, (short)-7, (short)0};

	public Sample() {
		this.set_sample_data(new short[0], 0, 0, false);
	}

	public void set_sample_data(short[] data, int loop_start, int loop_length, boolean ping_pong) {
		if(loop_start < 0) {
			loop_start = 0;
		}

		if(loop_start >= data.length) {
			loop_start = data.length - 1;
		}

		if(loop_start + loop_length > data.length) {
			loop_length = data.length - loop_start;
		}

		int offset;
		short sample;
		if(loop_length <= 1) {
			this.sample_data = new short[8 + data.length + 24];
			System.arraycopy(data, 0, this.sample_data, 8, data.length);

			for(offset = 0; offset < 8; ++offset) {
				sample = this.sample_data[8 + data.length - 1];
				sample = (short)(sample * (8 - offset) / 8);
				this.sample_data[8 + data.length + offset] = sample;
			}

			loop_start = 8 + data.length + 8;
			loop_length = 1;
		} else {
			if(!ping_pong) {
				this.sample_data = new short[8 + loop_start + loop_length + 16];
				System.arraycopy(data, 0, this.sample_data, 8, loop_start + loop_length);
				loop_start += 8;
			} else {
				this.sample_data = new short[8 + loop_start + loop_length * 2 + 16];
				System.arraycopy(data, 0, this.sample_data, 8, loop_start + loop_length);

				for(offset = 0; offset < loop_length; ++offset) {
					sample = data[loop_start + loop_length - offset - 1];
					this.sample_data[8 + loop_start + loop_length + offset] = sample;
				}

				loop_start += 8;
				loop_length *= 2;
			}

			for(offset = 0; offset < 16; ++offset) {
				sample = this.sample_data[loop_start + offset];
				this.sample_data[loop_start + loop_length + offset] = sample;
			}
		}

		this.loop_start = loop_start;
		this.loop_length = loop_length;
	}

	public void resample_nearest(int sample_idx, int sample_frac, int step, int left_gain, int right_gain, int[] mix_buffer, int frame_offset, int frames) {
		sample_idx += 8;
		int loop_end = this.loop_start + this.loop_length - 1;
		int offset = frame_offset << 1;

		for(int end = frame_offset + frames - 1 << 1; frames > 0; frames = end - offset + 2 >> 1) {
			if(sample_idx > loop_end) {
				if(this.loop_length <= 1) {
					break;
				}

				sample_idx = this.loop_start + (sample_idx - this.loop_start) % this.loop_length;
			}

			int max_sample_idx = sample_idx + (sample_frac + (frames - 1) * step >> 15);
			int i10001;
			if(max_sample_idx > loop_end) {
				while(sample_idx <= loop_end) {
					i10001 = offset++;
					mix_buffer[i10001] += this.sample_data[sample_idx] * left_gain >> 15;
					i10001 = offset++;
					mix_buffer[i10001] += this.sample_data[sample_idx] * right_gain >> 15;
					sample_frac += step;
					sample_idx += sample_frac >> 15;
					sample_frac &= 32767;
				}
			} else {
				while(offset <= end) {
					i10001 = offset++;
					mix_buffer[i10001] += this.sample_data[sample_idx] * left_gain >> 15;
					i10001 = offset++;
					mix_buffer[i10001] += this.sample_data[sample_idx] * right_gain >> 15;
					sample_frac += step;
					sample_idx += sample_frac >> 15;
					sample_frac &= 32767;
				}
			}
		}

	}

	public void resample_linear(int sample_idx, int sample_frac, int step, int left_gain, int right_gain, int[] mix_buffer, int frame_offset, int frames) {
		sample_idx += 8;
		int loop_end = this.loop_start + this.loop_length - 1;
		int offset = frame_offset << 1;

		for(int end = frame_offset + frames - 1 << 1; frames > 0; frames = end - offset + 2 >> 1) {
			if(sample_idx > loop_end) {
				if(this.loop_length <= 1) {
					break;
				}

				sample_idx = this.loop_start + (sample_idx - this.loop_start) % this.loop_length;
			}

			int max_sample_idx = sample_idx + (sample_frac + (frames - 1) * step >> 15);
			short amplitude;
			int i14;
			int i10001;
			if(max_sample_idx > loop_end) {
				while(sample_idx <= loop_end) {
					amplitude = this.sample_data[sample_idx];
					i14 = amplitude + ((this.sample_data[sample_idx + 1] - amplitude) * sample_frac >> 15);
					i10001 = offset++;
					mix_buffer[i10001] += i14 * left_gain >> 15;
					i10001 = offset++;
					mix_buffer[i10001] += i14 * right_gain >> 15;
					sample_frac += step;
					sample_idx += sample_frac >> 15;
					sample_frac &= 32767;
				}
			} else {
				while(offset <= end) {
					amplitude = this.sample_data[sample_idx];
					i14 = amplitude + ((this.sample_data[sample_idx + 1] - amplitude) * sample_frac >> 15);
					i10001 = offset++;
					mix_buffer[i10001] += i14 * left_gain >> 15;
					i10001 = offset++;
					mix_buffer[i10001] += i14 * right_gain >> 15;
					sample_frac += step;
					sample_idx += sample_frac >> 15;
					sample_frac &= 32767;
				}
			}
		}

	}

	public void resample_sinc(int sample_idx, int sample_frac, int step, int left_gain, int right_gain, int[] mix_buffer, int frame_offset, int frames) {
		int loop_end = this.loop_start + this.loop_length - 1;
		int offset = frame_offset << 1;

		for(int end = frame_offset + frames - 1 << 1; offset <= end; sample_frac &= 32767) {
			if(sample_idx > loop_end) {
				if(this.loop_length <= 1) {
					break;
				}

				sample_idx = this.loop_start + (sample_idx - this.loop_start) % this.loop_length;
			}

			int table_idx = sample_frac >> 11 << 4;
			int a1 = sinc_table[table_idx + 0] * this.sample_data[sample_idx + 0] >> 15;
			a1 += sinc_table[table_idx + 1] * this.sample_data[sample_idx + 1] >> 15;
			a1 += sinc_table[table_idx + 2] * this.sample_data[sample_idx + 2] >> 15;
			a1 += sinc_table[table_idx + 3] * this.sample_data[sample_idx + 3] >> 15;
			a1 += sinc_table[table_idx + 4] * this.sample_data[sample_idx + 4] >> 15;
			a1 += sinc_table[table_idx + 5] * this.sample_data[sample_idx + 5] >> 15;
			a1 += sinc_table[table_idx + 6] * this.sample_data[sample_idx + 6] >> 15;
			a1 += sinc_table[table_idx + 7] * this.sample_data[sample_idx + 7] >> 15;
			a1 += sinc_table[table_idx + 8] * this.sample_data[sample_idx + 8] >> 15;
			a1 += sinc_table[table_idx + 9] * this.sample_data[sample_idx + 9] >> 15;
			a1 += sinc_table[table_idx + 10] * this.sample_data[sample_idx + 10] >> 15;
			a1 += sinc_table[table_idx + 11] * this.sample_data[sample_idx + 11] >> 15;
			a1 += sinc_table[table_idx + 12] * this.sample_data[sample_idx + 12] >> 15;
			a1 += sinc_table[table_idx + 13] * this.sample_data[sample_idx + 13] >> 15;
			a1 += sinc_table[table_idx + 14] * this.sample_data[sample_idx + 14] >> 15;
			a1 += sinc_table[table_idx + 15] * this.sample_data[sample_idx + 15] >> 15;
			int a2 = sinc_table[table_idx + 16] * this.sample_data[sample_idx + 0] >> 15;
			a2 += sinc_table[table_idx + 17] * this.sample_data[sample_idx + 1] >> 15;
			a2 += sinc_table[table_idx + 18] * this.sample_data[sample_idx + 2] >> 15;
			a2 += sinc_table[table_idx + 19] * this.sample_data[sample_idx + 3] >> 15;
			a2 += sinc_table[table_idx + 20] * this.sample_data[sample_idx + 4] >> 15;
			a2 += sinc_table[table_idx + 21] * this.sample_data[sample_idx + 5] >> 15;
			a2 += sinc_table[table_idx + 22] * this.sample_data[sample_idx + 6] >> 15;
			a2 += sinc_table[table_idx + 23] * this.sample_data[sample_idx + 7] >> 15;
			a2 += sinc_table[table_idx + 24] * this.sample_data[sample_idx + 8] >> 15;
			a2 += sinc_table[table_idx + 25] * this.sample_data[sample_idx + 9] >> 15;
			a2 += sinc_table[table_idx + 26] * this.sample_data[sample_idx + 10] >> 15;
			a2 += sinc_table[table_idx + 27] * this.sample_data[sample_idx + 11] >> 15;
			a2 += sinc_table[table_idx + 28] * this.sample_data[sample_idx + 12] >> 15;
			a2 += sinc_table[table_idx + 29] * this.sample_data[sample_idx + 13] >> 15;
			a2 += sinc_table[table_idx + 30] * this.sample_data[sample_idx + 14] >> 15;
			a2 += sinc_table[table_idx + 31] * this.sample_data[sample_idx + 15] >> 15;
			int amplitude = a1 + ((a2 - a1) * (sample_frac & 2047) >> 11);
			mix_buffer[offset] += amplitude * left_gain >> 15;
			mix_buffer[offset + 1] += amplitude * right_gain >> 15;
			offset += 2;
			sample_frac += step;
			sample_idx += sample_frac >> 15;
		}

	}

	public boolean has_finished(int sample_idx) {
		boolean finished = false;
		if(this.loop_length <= 1 && sample_idx > this.loop_start) {
			finished = true;
		}

		return finished;
	}
}
