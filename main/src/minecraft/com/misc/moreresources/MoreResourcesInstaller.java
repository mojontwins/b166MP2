package com.misc.moreresources;

import java.io.File;
import java.net.URISyntaxException;

import net.minecraft.client.Minecraft;

public class MoreResourcesInstaller {
	Minecraft mc;
	private final String resourceDirectory = "/resources/sounds";
	
	public MoreResourcesInstaller(Minecraft mc) {
		this.mc = mc;
	}
	
	public void installResources() {
		try {
			// Load custom resources
			
			this.mc.installResourceURL("sound/mob/ogre/ogre0.ogg", this.getURLfromResource(this.resourceDirectory +"/ogre1.ogg"));
			this.mc.installResourceURL("sound/mob/ogre/ogre1.ogg", this.getURLfromResource(this.resourceDirectory +"/ogre2.ogg"));
			this.mc.installResourceURL("sound/mob/ogre/ogre2.ogg", this.getURLfromResource(this.resourceDirectory +"/ogre3.ogg"));
			this.mc.installResourceURL("sound/mob/ogre/death.ogg", this.getURLfromResource(this.resourceDirectory +"/ogredying.ogg"));
			this.mc.installResourceURL("sound/mob/ogre/hurt.ogg", this.getURLfromResource(this.resourceDirectory +"/ogrehurt1.ogg"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public File getFileFromResource(String fileName) throws URISyntaxException{

		Class<?> classLoader = this.getClass();
		java.net.URL resource = classLoader.getResource(fileName);
		
		if (resource == null) {
			throw new IllegalArgumentException("file not found! " + fileName);
		} else {

			// failed if files have whitespace or special characters
			//return new File(resource.getFile());

			return new File(resource.toURI());
		}

	}
	
	public java.net.URL getURLfromResource(String fileName) throws URISyntaxException{

		Class<?> classLoader = this.getClass();
		java.net.URL resource = classLoader.getResource(fileName);
		
		if (resource == null) {
			throw new IllegalArgumentException("file not found! " + fileName);
		} else {
			return resource;
		}
	}
}
