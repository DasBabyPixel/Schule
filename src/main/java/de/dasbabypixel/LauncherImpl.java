package de.dasbabypixel;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.game.Game;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.plugin.Plugin;
import gamelauncher.engine.plugin.Plugin.GamePlugin;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;

@GamePlugin
public class LauncherImpl extends Plugin {
	private final Key GAME_KEY = new Key(this, "graphen");

	public LauncherImpl() {
		super("schule");
	}

	@Override
	public void onEnable() throws GameException {
		launcher().gameRegistry().register(new Game(GAME_KEY) {
			@Override
			protected void launch0(Framebuffer framebuffer) throws GameException {
				launcher().guiManager().openGui(framebuffer, new GUI(launcher()));
			}

			@Override
			protected void close0() throws GameException {
			}
		});
	}

	@Override
	public void onDisable() throws GameException {
		launcher().gameRegistry().unregister(GAME_KEY);
	}

	private static class GUI extends ParentableAbstractGui {

		public GUI(GameLauncher launcher) {
			super(launcher);
		}

		public static class GUINode extends ParentableAbstractGui {

			public GUINode(GameLauncher launcher) {
				super(launcher);
			}
		}
	}
}
