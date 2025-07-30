package net.minecraft.src;

import java.io.File;
import java.util.List;

public interface ISaveHandler {
	WorldInfo func_22096_c();

	void func_22091_b();

	IChunkLoader func_22092_a(WorldProvider worldProvider1);

	void func_22095_a(WorldInfo worldInfo1, List list2);

	void func_22094_a(WorldInfo worldInfo1);

	IPlayerFileData func_22090_d();

	void func_22093_e();

	File func_28111_b(String string1);
}
