package com.misc.moreresources;

import java.io.File;
import java.net.URISyntaxException;

import net.minecraft.client.Minecraft;

public class MoreResourcesInstaller {
	Minecraft mc;
	
	public MoreResourcesInstaller(Minecraft mc) {
		this.mc = mc;
	}
	
	public void installResources() {
		try {
			// Load custom resources
			
			// this.mc.installResourceURL("sound/mob/betterdungeons/pirate.ogg", this.getURLfromResource("com/misc/moreresources/sounds/pirate_speak.ogg"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public File getFileFromResource(String fileName) throws URISyntaxException{

        ClassLoader classLoader = this.getClass().getClassLoader();
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

        ClassLoader classLoader = this.getClass().getClassLoader();
        java.net.URL resource = classLoader.getResource(fileName);
        
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
        	return resource;
        }
	}
}
