package com.basti_bob.sand_wizard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.basti_bob.sand_wizard.SandWizard;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		//config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());

		//config.setForegroundFPS(60);
		config.useVsync(false);
		float scale = 1f;
		config.setWindowedMode((int) (1920 * scale), (int) (1080 * scale));
		config.setMaximizedMonitor(Lwjgl3ApplicationConfiguration.getMonitors()[2]);

		config.setTitle("Sand Wizard");
		new Lwjgl3Application(new SandWizard(), config);
	}
}
