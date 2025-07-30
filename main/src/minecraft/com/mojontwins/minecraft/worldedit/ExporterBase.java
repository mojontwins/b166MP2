package com.mojontwins.minecraft.worldedit;

import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.world.phys.Vec3i;

public abstract class ExporterBase {
	public static HashMap<String,ExporterBase> exporterList = new HashMap<String,ExporterBase> ();
	public static int lastId = 0;
	
	public static ExporterBase raw = new ExporterRaw().register();
	public static ExporterBase columnsArray = new ExporterColumnsArray().register();
	public static ExporterBase schematic = new ExporterSchematic().register();
	
	public int id;
	
	public ExporterBase() {
		this.id = lastId++;
	}
	
	public ExporterBase register() {
		exporterList.put(this.getName(), this);
		return this;
	}
	
	public static ExporterBase getByName(String name) {
		return exporterList.get(name);
	}
	
	public static String getList() {
		String res = "Known exporters: ";
		
		Iterator<String> it = exporterList.keySet().iterator();
		while(it.hasNext()) {
			res += exporterList.get(it.next()).getName();
			if(it.hasNext()) res += ", ";
		}
		
		return res;
	}
	
	public abstract String getName();
	
	public abstract boolean export(int [][][] buffer, Vec3i dims, String fileName, String arg);
	
	public abstract String getHelp();
}
