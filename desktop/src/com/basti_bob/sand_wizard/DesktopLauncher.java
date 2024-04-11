package com.basti_bob.sand_wizard;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL31, 3, 1);

		Graphics.Monitor monitor = Lwjgl3ApplicationConfiguration.getMonitors()[2];

		config.useVsync(false);

		float scale = 1f;
		config.setWindowedMode((int) (1920 * scale), (int) (1080 * scale));
		config.setWindowPosition(monitor.virtualX, monitor.virtualY);
		config.setTitle("Sand Wizard");
		new Lwjgl3Application(new SandWizard(), config);
	}
}
