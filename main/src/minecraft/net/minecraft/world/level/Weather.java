package net.minecraft.world.level;

import java.util.Random;

import net.minecraft.world.level.biome.BiomeGenBase;

public class Weather {
	public static final int RAIN = 1;
	public static final int SNOW = 2;
	
	public static Weather cold = new Weather().setName("Cold");
	public static Weather normal = new Weather().setName("Normal");
	public static Weather hot = new Weather().setName("Hot");
	public static Weather desert = new Weather().setName("Desertic");
	
	public String name = "Weather";
	
	public static int[] snowingMinTimeToStop = new int[] {12000, 6000, 6000, 8000};
	public static int[] snowingMaxTimeToStop = new int[] {28000, 16000, 12000, 12000};
	
	public static int[] snowingMinTimeToStart = new int[] {6000, 9000, 12000, 9000};
	public static int[] snowingMaxTimeToStart = new int[] {18000, 50000, 72000, 24000};
	
	public static int[] rainingMinTimeToStop = new int[] {6000, 6000, 3000, 8000};
	public static int[] rainingMaxTimeToStop = new int[] {12000, 12000, 6000, 24000};
	
	public static int[] rainingMinTimeToStart = new int[] {2000, 12000, 60000, 1000};
	public static int[] rainingMaxTimeToStart = new int[] {6000, 24000, 168000, 3000};
	
	public static int[] thunderingMinTimeToStop = new int[] {3000, 3000, 6000, 3000};
	public static int[] thunderingMaxTimeToStop = new int[] {9000, 6000, 12000, 9000};
	
	public static int[] thunderingMinTimeToStart = new int[] {12000, 12000, 12000, 12000};
	public static int[] thunderingMaxTimeToStart = new int[] {168000, 168000, 72000, 168000};

	public static int getTimeForSnowingEnd(Random rand) {
		return rand.nextInt(snowingMaxTimeToStop[Seasons.currentSeason] - snowingMinTimeToStop[Seasons.currentSeason]) + snowingMinTimeToStop[Seasons.currentSeason];
	}
	
	public static int getTimeForNextSnow(Random rand) {
		return rand.nextInt(snowingMaxTimeToStart[Seasons.currentSeason] - snowingMinTimeToStart[Seasons.currentSeason]) + snowingMinTimeToStart[Seasons.currentSeason];
	}

	public static int getTimeForRainingEnd(Random rand) {
		return rand.nextInt(rainingMaxTimeToStop[Seasons.currentSeason] - rainingMinTimeToStop[Seasons.currentSeason]) + rainingMinTimeToStop[Seasons.currentSeason];
	}
	
	public static int getTimeForNextRain(Random rand) {
		return rand.nextInt(rainingMaxTimeToStart[Seasons.currentSeason] - rainingMinTimeToStart[Seasons.currentSeason]) + rainingMinTimeToStart[Seasons.currentSeason];
	}
	
	public static int getTimeForThunderingEnd(Random rand) {
		return rand.nextInt(thunderingMaxTimeToStop[Seasons.currentSeason] - thunderingMinTimeToStop[Seasons.currentSeason]) + thunderingMinTimeToStop[Seasons.currentSeason];
	}
	
	public static int getTimeForNextThunder(Random rand) {
		return rand.nextInt(thunderingMaxTimeToStart[Seasons.currentSeason] - thunderingMinTimeToStart[Seasons.currentSeason]) + thunderingMinTimeToStart[Seasons.currentSeason];
	}
	
	public static int particleDecide(BiomeGenBase biomeGen, World world) {
		if(biomeGen != null) {	
			if(biomeGen.weather == Weather.cold) {
				
				if(world.snowingStrength > 0.0F) {
					if(Seasons.currentSeason == Seasons.SUMMER) return RAIN;
					return SNOW;
				}
				if(world.rainingStrength > 0.0F) {
					if(Seasons.currentSeason == Seasons.WINTER) return SNOW;
					return RAIN;
				}
			} else if(biomeGen.weather == Weather.normal) {
				if(world.rainingStrength > 0.0F) return RAIN;
				if(world.snowingStrength > 0.0F && Seasons.currentSeason == Seasons.WINTER) return SNOW;
			} else if(biomeGen.weather == Weather.hot) {
				if(world.rainingStrength > 0.0F) return RAIN;
			} else if(biomeGen.weather == Weather.desert) {
				;
			}
		}
		return 0;
	}
	
	public Weather setName(String name) {
		this.name = name;
		return this;
	}
}
