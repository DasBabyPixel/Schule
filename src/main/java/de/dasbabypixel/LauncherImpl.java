package de.dasbabypixel;

import de.dasbabypixel.Graph.Node;
import de.dasbabypixel.Graph.Node.Connection;
import de.dasbabypixel.api.property.NumberChangeListener;
import de.dasbabypixel.api.property.NumberInvalidationListener;
import de.dasbabypixel.api.property.NumberValue;
import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.game.Game;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.guis.TextGui;
import gamelauncher.engine.gui.launcher.ColorGui;
import gamelauncher.engine.gui.launcher.LineGui;
import gamelauncher.engine.gui.launcher.ScrollGui;
import gamelauncher.engine.plugin.Plugin;
import gamelauncher.engine.plugin.Plugin.GamePlugin;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry.Type;
import gamelauncher.engine.util.text.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
				launcher().guiManager().openGui(framebuffer, new GUI<>(launcher(), Start.graph));
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

	private static class GUI<T, V> extends ParentableAbstractGui {

		//		private GuiContainer container;

		public GUI(GameLauncher launcher, Graph<T, V> graph) throws GameException {
			super(launcher);
			Map<Node<T, V>, GUINode> guiMap = new HashMap<>();
			ParentableAbstractGui agui = new ParentableAbstractGui(launcher) {
			};
			agui.size(2000, 2000);
			ScrollGui sgui = launcher.guiManager().createGui(ScrollGui.class);
			sgui.gui().setValue(agui);
			sgui.xProperty().bind(xProperty());
			sgui.yProperty().bind(yProperty());
			sgui.widthProperty().bind(widthProperty());
			sgui.heightProperty().bind(heightProperty());
			for (Node<T, V> node : graph.nodes()) {
				GUINode guiNode = new GUINode(launcher, node, agui.xProperty(), agui.yProperty());
				guiNode.height(50);
				guiNode.width(250);
				agui.GUIs.add(guiNode);
				guiMap.put(node, guiNode);
			}
			for (Connection<T, V> connection : graph.connections()) {
				agui.GUIs.add(new GUIConnection(launcher(), guiMap.get(connection.from()),
						guiMap.get(connection.to()), connection));
			}
			GUIs.add(sgui);
		}

		public class GUIConnection extends ParentableAbstractGui {

			public GUIConnection(GameLauncher launcher, GUINode from, GUINode to,
					Connection<T, V> connection) throws GameException {
				super(launcher);
				xProperty().bind(GUI.this.xProperty());
				yProperty().bind(GUI.this.yProperty());
				widthProperty().bind(GUI.this.widthProperty());
				heightProperty().bind(GUI.this.heightProperty());
				LineGui lineGui = launcher.guiManager().createGui(LineGui.class);
				lineGui.fromX().bind(from.xProperty().add(from.widthProperty().divide(2)));
				lineGui.fromY().bind(from.yProperty().add(from.heightProperty().divide(2)));
				lineGui.toX().bind(to.xProperty().add(to.widthProperty().divide(2)));
				lineGui.toY().bind(to.yProperty().add(to.heightProperty().divide(2)));
				GUIs.add(lineGui);
			}
		}


		public class GUINode extends ParentableAbstractGui {
			private final NumberValue offsetX = NumberValue.zero();
			private final NumberValue offsetY = NumberValue.zero();
			private final Path path;

			public GUINode(GameLauncher launcher, Node<T, V> node, NumberValue bindX,
					NumberValue bindY) throws GameException {
				super(launcher);
				this.path = launcher().dataDirectory().resolve(node.data().toString() + ".bin");
				if (Files.exists(path)) {
					try {
						ByteBuffer buf = ByteBuffer.wrap(Files.readAllBytes(path));
						offsetX.setNumber(buf.getFloat());
						offsetY.setNumber(buf.getFloat());
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				NumberInvalidationListener listener = numberValue -> {
					ByteBuffer buf = ByteBuffer.wrap(new byte[8]);
					buf.putFloat(offsetX.floatValue());
					buf.putFloat(offsetY.floatValue());
					try {
						Files.write(path, buf.array(), StandardOpenOption.CREATE,
								StandardOpenOption.TRUNCATE_EXISTING);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				};
				offsetX.addListener(listener);
				offsetY.addListener(listener);
				xProperty().bind(bindX.add(offsetX));
				yProperty().bind(bindY.add(offsetY));
				ColorGui colorGui = launcher().guiManager().createGui(ColorGui.class);
				colorGui.xProperty().bind(xProperty());
				colorGui.yProperty().bind(yProperty());
				colorGui.widthProperty().bind(widthProperty());
				colorGui.heightProperty().bind(heightProperty());
				colorGui.color().set(0.1F, 0.1F, 0.1F, 0.8F);
				GUIs.add(colorGui);
				TextGui textGui =
						new TextGui(launcher, Component.text(Objects.toString(node.data())), 10);
				textGui.xProperty().bind(xProperty().add(
						widthProperty().subtract(textGui.widthProperty()).divide(2)));
				textGui.yProperty().bind(yProperty());
				textGui.heightProperty().bind(heightProperty());
				GUIs.add(textGui);

				hovering().addListener(Property::getValue);
				hovering().addListener((property, oldV, newV) -> {
					if (oldV == null || newV.booleanValue() != oldV.booleanValue()) {
						if (newV) {
							colorGui.color().set(0F, 0F, 0F, 0.9F);
						} else {
							colorGui.color().set(0.1F, 0.1F, 0.1F, 0.8F);
						}
					}
				});
			}

			private float pressInitialMouseX = 0;
			private float pressInitialMouseY = 0;
			private float pressInitialX = 0;
			private float pressInitialY = 0;

			@Override
			protected boolean doHandle(KeybindEntry entry) throws GameException {
				if (entry instanceof MouseButtonKeybindEntry mbe) {
					float pressDeltaX;
					float pressDeltaY;
					if (mbe.type() == Type.PRESS) {
						pressInitialMouseX = mbe.mouseX();
						pressInitialMouseY = mbe.mouseY();
						pressInitialX = offsetX.floatValue();
						pressInitialY = offsetY.floatValue();
					} else if (mbe.type() == Type.HOLD) {
						pressDeltaX = pressInitialMouseX - mbe.mouseX();
						pressDeltaY = pressInitialMouseY - mbe.mouseY();
						offsetX.setNumber(pressInitialX - pressDeltaX);
						offsetY.setNumber(pressInitialY - pressDeltaY);
						redraw();
					} else if (mbe.type() == Type.RELEASE) {
						pressInitialMouseX = pressInitialMouseY = 0;
					}
				}
				return super.doHandle(entry);
			}

		}
	}
}
