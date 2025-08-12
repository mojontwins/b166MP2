package net.minecraft.world.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.entity.animal.EntityBoar;
import net.minecraft.world.entity.animal.EntityChicken;
import net.minecraft.world.entity.animal.EntityChickenBlack;
import net.minecraft.world.entity.animal.EntityColdCow;
import net.minecraft.world.entity.animal.EntityCow;
import net.minecraft.world.entity.animal.EntityGoat;
import net.minecraft.world.entity.animal.EntityMooshroom;
import net.minecraft.world.entity.animal.EntityOcelot;
import net.minecraft.world.entity.animal.EntityPig;
import net.minecraft.world.entity.animal.EntitySheep;
import net.minecraft.world.entity.animal.EntitySquid;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.item.EntityBoat;
import net.minecraft.world.entity.item.EntityFallingSand;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.item.EntityMinecart;
import net.minecraft.world.entity.item.EntityMovingPiston;
import net.minecraft.world.entity.item.EntityTNTPrimed;
import net.minecraft.world.entity.monster.EntityClassicSkeleton;
import net.minecraft.world.entity.monster.EntityClassicZombie;
import net.minecraft.world.entity.monster.EntityCreeper;
import net.minecraft.world.entity.monster.EntityGhast;
import net.minecraft.world.entity.monster.EntityGiantZombie;
import net.minecraft.world.entity.monster.EntityIronGolem;
import net.minecraft.world.entity.monster.EntityMob;
import net.minecraft.world.entity.monster.EntityOgre;
import net.minecraft.world.entity.monster.EntityPigZombie;
import net.minecraft.world.entity.monster.EntitySkeleton;
import net.minecraft.world.entity.monster.EntitySlime;
import net.minecraft.world.entity.monster.EntitySnowman;
import net.minecraft.world.entity.monster.EntitySpider;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntityEggInfo;
import net.minecraft.world.entity.projectile.EntityFireball;
import net.minecraft.world.entity.projectile.EntitySmallFireball;
import net.minecraft.world.entity.projectile.EntitySnowball;
import net.minecraft.world.level.World;

public class EntityList {
	private static Map<String, Class<?>> stringToClassMapping = new HashMap<String, Class<?>>();
	private static Map<Class<?>, String> classToStringMapping = new HashMap<Class<?>, String>();
	private static Map<Integer, Class<?>> IDtoClassMapping = new HashMap<Integer, Class<?>>();
	private static Map<Class<?>, Integer> classToIDMapping = new HashMap<Class<?>, Integer>();
	private static Map<String, Integer> stringToIDMapping = new HashMap<String, Integer>();
	public static HashMap<Integer, EntityEggInfo> entityEggs = new HashMap<Integer, EntityEggInfo>();

	private static void addMapping(Class<?> class0, String string1, int i2) {
		stringToClassMapping.put(string1, class0);
		classToStringMapping.put(class0, string1);
		IDtoClassMapping.put(i2, class0);
		classToIDMapping.put(class0, i2);
		stringToIDMapping.put(string1, i2);
	}

	private static void addMapping(Class<?> class0, String name, int id, int c1, int c2) {
		addMapping(class0, name, id);
		entityEggs.put(id, new EntityEggInfo(id, c1, c2, name));
	}

	public static Entity createEntityByName(String string0, World world1) {
		Entity entity2 = null;

		try {
			Class<?> class3 = (Class<?>)stringToClassMapping.get(string0);
			if(class3 != null) {
				entity2 = (Entity)class3.getConstructor(new Class[]{World.class}).newInstance(new Object[]{world1});
			}
		} catch (Exception exception4) {
			exception4.printStackTrace();
		}

		return entity2;
	}

	public static Entity createEntityFromNBT(NBTTagCompound nBTTagCompound0, World world1) {
		Entity entity2 = null;

		try {
			Class<?> class3 = (Class<?>)stringToClassMapping.get(nBTTagCompound0.getString("id"));
			if(class3 != null) {
				entity2 = (Entity)class3.getConstructor(new Class[]{World.class}).newInstance(new Object[]{world1});
			}
		} catch (Exception exception4) {
			exception4.printStackTrace();
		}

		if(entity2 != null) {
			entity2.readFromNBT(nBTTagCompound0);
		} else {
			System.out.println("Skipping Entity with id " + nBTTagCompound0.getString("id"));
		}

		return entity2;
	}

	public static Entity createEntityByID(int i0, World world1) {
		Entity entity2 = null;

		try {
			Class<?> class3 = (Class<?>)IDtoClassMapping.get(i0);
			if(class3 != null) {
				entity2 = (Entity)class3.getConstructor(new Class[]{World.class}).newInstance(new Object[]{world1});
			}
		} catch (Exception exception4) {
			exception4.printStackTrace();
		}

		if(entity2 == null) {
			System.out.println("Skipping Entity with id " + i0);
		}

		return entity2;
	}

	public static int getEntityID(Entity entity0) {
		return ((Integer)classToIDMapping.get(entity0.getClass())).intValue();
	}

	public static String getEntityString(Entity entity0) {
		return (String)classToStringMapping.get(entity0.getClass());
	}

	public static String getStringFromID(int i0) {
		Class<?> class1 = (Class<?>)IDtoClassMapping.get(i0);
		return class1 != null ? (String)classToStringMapping.get(class1) : null;
	}

	public static int getIDFromString(String string0) {
		Integer integer1 = (Integer)stringToIDMapping.get(string0);
		return integer1 == null ? 90 : integer1.intValue();
	}
	
	public static List<String> getAllEntityStrings() {
		List<String> result = new ArrayList<String>();
		Iterator<String> it = stringToClassMapping.keySet().iterator();
		while(it.hasNext()) result.add(it.next());
		return result;
	}
	
	public static String getNameByClass(Class<?> clazz) {
		return classToStringMapping.get(clazz);
	}


	static {
		addMapping(EntityItem.class, "Item", 1);
		addMapping(EntityPainting.class, "Painting", 9);
		addMapping(EntityArrow.class, "Arrow", 10);
		addMapping(EntitySnowball.class, "Snowball", 11);
		addMapping(EntityFireball.class, "Fireball", 12);
		addMapping(EntitySmallFireball.class, "SmallFireball", 13);
		addMapping(EntityTNTPrimed.class, "PrimedTnt", 20);
		addMapping(EntityFallingSand.class, "FallingSand", 21);
		addMapping(EntityMinecart.class, "Minecart", 40);
		addMapping(EntityBoat.class, "Boat", 41);
		addMapping(EntityLiving.class, "Mob", 48);
		addMapping(EntityMob.class, "Monster", 49);
		addMapping(EntityCreeper.class, "Creeper", 50, 894731, 0);
		addMapping(EntitySkeleton.class, "Skeleton", 51, 12698049, 4802889);
		addMapping(EntitySpider.class, "Spider", 52, 3419431, 11013646);
		addMapping(EntityGiantZombie.class, "Giant", 53);
		addMapping(EntityZombie.class, "Zombie", 54, 44975, 7969893);
		addMapping(EntitySlime.class, "Slime", 55, 5349438, 8306542);
		addMapping(EntityGhast.class, "Ghast", 56, 16382457, 12369084);
		addMapping(EntityPigZombie.class, "PigZombie", 57, 15373203, 5009705);
		addMapping(EntityPig.class, "Pig", 90, 15771042, 14377823);
		addMapping(EntitySheep.class, "Sheep", 91, 15198183, 16758197);
		addMapping(EntityCow.class, "Cow", 92, 4470310, 10592673);
		addMapping(EntityChicken.class, "Chicken", 93, 10592673, 16711680);
		addMapping(EntitySquid.class, "Squid", 94, 2243405, 7375001);
		addMapping(EntityWolf.class, "Wolf", 95, 14144467, 13545366);
		addMapping(EntityMooshroom.class, "MushroomCow", 96, 10489616, 12040119);
		addMapping(EntitySnowman.class, "SnowMan", 97);
		addMapping(EntityOcelot.class, "Ozelot", 98, 15720061, 5653556);
		addMapping(EntityIronGolem.class, "VillagerGolem", 99);
		addMapping(EntityVillager.class, "Villager", 120, 5651507, 12422002);
		
		addMapping(EntityMovingPiston.class, "MovingPiston", 119); 
		
		addMapping(EntityBoar.class, "Boar", 120, 0x362119, 0x57453D);
		addMapping(EntityOgre.class, "Ogre", 121, 0x209855, 0xB6A055);
		addMapping(EntityColdCow.class, "ColdCow", 122, 0xA46A43, 0x39241B);
		addMapping(EntityChickenBlack.class, "ChickenBlack", 123, 0x1D1D1D, 0x393939);
		addMapping(EntityGoat.class, "Goat", 124, 0xDDD6CE, 0x4B4B4B);
		
		addMapping(EntityClassicZombie.class, "ZombieClassic", 150);
		addMapping(EntityClassicSkeleton.class, "SkeletonClassic", 151);
	}
}
