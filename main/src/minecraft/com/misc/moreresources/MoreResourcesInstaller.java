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
			
			// Dr. Zhark's Ogre
			this.mc.installResourceURL("sound/mob/ogre/ogre0.ogg", this.getURLfromResource(this.resourceDirectory + "/ogre1.ogg"));
			this.mc.installResourceURL("sound/mob/ogre/ogre1.ogg", this.getURLfromResource(this.resourceDirectory + "/ogre2.ogg"));
			this.mc.installResourceURL("sound/mob/ogre/ogre2.ogg", this.getURLfromResource(this.resourceDirectory + "/ogre3.ogg"));
			this.mc.installResourceURL("sound/mob/ogre/death.ogg", this.getURLfromResource(this.resourceDirectory + "/ogredying.ogg"));
			this.mc.installResourceURL("sound/mob/ogre/hurt.ogg", this.getURLfromResource(this.resourceDirectory + "/ogrehurt1.ogg"));

			// Dr. Zhark's Goat			
			this.mc.installResourceURL("sound/mocreatures/goateating.ogg", this.getURLfromResource(this.resourceDirectory + "/goateating.ogg"));
			this.mc.installResourceURL("sound/mocreatures/goatsmack.ogg", this.getURLfromResource(this.resourceDirectory + "/goatsmack.ogg"));
			this.mc.installResourceURL("sound/mocreatures/goatdigg.ogg", this.getURLfromResource(this.resourceDirectory + "/goatdigg.ogg"));
			this.mc.installResourceURL("sound/mocreatures/goathurt.ogg", this.getURLfromResource(this.resourceDirectory + "/goathurt.ogg"));
			this.mc.installResourceURL("sound/mocreatures/goatfemale.ogg", this.getURLfromResource(this.resourceDirectory + "/goatfemale.ogg"));
			this.mc.installResourceURL("sound/mocreatures/goatkid.ogg", this.getURLfromResource(this.resourceDirectory + "/goatkid.ogg"));
			this.mc.installResourceURL("sound/mocreatures/goatgrunt.ogg", this.getURLfromResource(this.resourceDirectory + "/goatgrunt.ogg"));			
			this.mc.installResourceURL("sound/mocreatures/goatdying.ogg", this.getURLfromResource(this.resourceDirectory + "/goatdying.ogg"));
			
			// Dr. Zhark's Werewolf
			this.mc.installResourceURL("sound/mocreatures/werehumandying0.ogg", this.getURLfromResource(this.resourceDirectory + "/werehumandying1.ogg"));
			this.mc.installResourceURL("sound/mocreatures/werehumandying1.ogg", this.getURLfromResource(this.resourceDirectory + "/werehumandying2.ogg"));
			this.mc.installResourceURL("sound/mocreatures/werehumanhurt0.ogg", this.getURLfromResource(this.resourceDirectory + "/werehumanhurt1.ogg"));
			this.mc.installResourceURL("sound/mocreatures/werehumanhurt1.ogg", this.getURLfromResource(this.resourceDirectory + "/werehumanhurt2.ogg"));
			this.mc.installResourceURL("sound/mocreatures/weretransform0.ogg", this.getURLfromResource(this.resourceDirectory + "/weretransform1.ogg"));
			this.mc.installResourceURL("sound/mocreatures/weretransform1.ogg", this.getURLfromResource(this.resourceDirectory + "/weretransform2.ogg"));
			this.mc.installResourceURL("sound/mocreatures/weretransform2.ogg", this.getURLfromResource(this.resourceDirectory + "/weretransform3.ogg"));
			this.mc.installResourceURL("sound/mocreatures/werewolfdying0.ogg", this.getURLfromResource(this.resourceDirectory + "/werewolfdying1.ogg"));
			this.mc.installResourceURL("sound/mocreatures/werewolfdying1.ogg", this.getURLfromResource(this.resourceDirectory + "/werewolfdying2.ogg"));
			this.mc.installResourceURL("sound/mocreatures/werewolfgrunt0.ogg", this.getURLfromResource(this.resourceDirectory + "/werewolfgrunt1.ogg"));
			this.mc.installResourceURL("sound/mocreatures/werewolfgrunt1.ogg", this.getURLfromResource(this.resourceDirectory + "/werewolfgrunt2.ogg"));
			this.mc.installResourceURL("sound/mocreatures/werewolfgrunt2.ogg", this.getURLfromResource(this.resourceDirectory + "/werewolfgrunt3.ogg"));
			this.mc.installResourceURL("sound/mocreatures/werewolfgrunt3.ogg", this.getURLfromResource(this.resourceDirectory + "/werewolfgrunt4.ogg"));
			this.mc.installResourceURL("sound/mocreatures/werewolfhurt0.ogg", this.getURLfromResource(this.resourceDirectory + "/werewolfhurt1.ogg"));
			this.mc.installResourceURL("sound/mocreatures/werewolfhurt1.ogg", this.getURLfromResource(this.resourceDirectory + "/werewolfhurt2.ogg"));
			
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
