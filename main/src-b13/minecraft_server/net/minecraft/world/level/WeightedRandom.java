package net.minecraft.world.level;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class WeightedRandom {
	public static int getTotalWeight(Collection<?> list7) {
		int i1 = 0;

		WeightedRandomChoice weightedRandomChoice3;
		for(Iterator<?> iterator2 = list7.iterator(); iterator2.hasNext(); i1 += weightedRandomChoice3.itemWeight) {
			weightedRandomChoice3 = (WeightedRandomChoice)iterator2.next();
		}

		return i1;
	}

	public static WeightedRandomChoice getRandomItem(Random random0, Collection<?> collection1, int i2) {
		if(i2 <= 0) {
			throw new IllegalArgumentException();
		} else {
			int i3 = random0.nextInt(i2);
			Iterator<?> iterator4 = collection1.iterator();

			WeightedRandomChoice weightedRandomChoice5;
			do {
				if(!iterator4.hasNext()) {
					return null;
				}

				weightedRandomChoice5 = (WeightedRandomChoice)iterator4.next();
				i3 -= weightedRandomChoice5.itemWeight;
			} while(i3 >= 0);

			return weightedRandomChoice5;
		}
	}

	public static WeightedRandomChoice getRandomItem(Random random0, Collection<?> list7) {
		return getRandomItem(random0, list7, getTotalWeight(list7));
	}

	public static int getTotalWeight(WeightedRandomChoice[] weightedRandomChoice0) {
		int i1 = 0;
		WeightedRandomChoice[] weightedRandomChoice2 = weightedRandomChoice0;
		int i3 = weightedRandomChoice0.length;

		for(int i4 = 0; i4 < i3; ++i4) {
			WeightedRandomChoice weightedRandomChoice5 = weightedRandomChoice2[i4];
			i1 += weightedRandomChoice5.itemWeight;
		}

		return i1;
	}

	public static WeightedRandomChoice getRandomItem(Random random0, WeightedRandomChoice[] weightedRandomChoice1, int i2) {
		if(i2 <= 0) {
			throw new IllegalArgumentException();
		} else {
			int i3 = random0.nextInt(i2);
			WeightedRandomChoice[] weightedRandomChoice4 = weightedRandomChoice1;
			int i5 = weightedRandomChoice1.length;

			for(int i6 = 0; i6 < i5; ++i6) {
				WeightedRandomChoice weightedRandomChoice7 = weightedRandomChoice4[i6];
				i3 -= weightedRandomChoice7.itemWeight;
				if(i3 < 0) {
					return weightedRandomChoice7;
				}
			}

			return null;
		}
	}

	public static WeightedRandomChoice getRandomItem(Random random0, WeightedRandomChoice[] weightedRandomChoice1) {
		return getRandomItem(random0, weightedRandomChoice1, getTotalWeight(weightedRandomChoice1));
	}
}
