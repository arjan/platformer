package nl.hku.platformer;

import java.util.HashMap;
import java.util.Map;

import ddf.minim.AudioPlayer;
import ddf.minim.AudioSample;
import ddf.minim.Minim;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class ResourceManager {

	private static ResourceManager instance;
	
	Map<String, Object> resources;
	
	public static ResourceManager initialize(PApplet main) {
		ResourceManager.instance = new ResourceManager(main);
		return instance;
	}

	public static ResourceManager getInstance() {
		return instance;
	}
	
	private Minim minim;

	private PApplet applet;
	
	private ResourceManager(PApplet applet) {
		this.minim = new Minim(applet);
		this.applet = applet;
		this.resources = new HashMap<String, Object>();
	}

	public AudioSample loadSample(String rscId, String resource) {
		AudioSample sample = minim.loadSample(resource, 256);
		resources.put(rscId, sample);
		return sample;
	}
	
	public PImage loadImage(String rscId, String resource) {
		PImage image = this.applet.loadImage(resource);
		resources.put(rscId, image);
		return image;
	}

	public PFont loadFont(String rscId, String resource) {
		PFont font = this.applet.loadFont(resource);
		resources.put(rscId, font);
		return font;
	}
	
	public Object get(String rscId) {
		return resources.get(rscId);
	}
	
	public AudioSample getAudioSample(String rscId) {
		return (AudioSample) resources.get(rscId);
	}
	
	public PImage getImage(String rscId) {
		return (PImage) resources.get(rscId);
	}
	
	public PFont getFont(String rscId) {
		return (PFont) resources.get(rscId);
	}
}
