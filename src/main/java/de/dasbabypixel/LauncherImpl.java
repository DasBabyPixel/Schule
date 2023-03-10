package de.dasbabypixel;

import de.dasbabypixel.Graph.Node;
import de.dasbabypixel.Graph.Node.Connection;
import de.dasbabypixel.api.property.NumberValue;
import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.game.Game;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.guis.TextGui;
import gamelauncher.engine.gui.launcher.ColorGui;
import gamelauncher.engine.gui.launcher.LineGui;
import gamelauncher.engine.plugin.Plugin;
import gamelauncher.engine.plugin.Plugin.GamePlugin;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEntry.Type;
import gamelauncher.engine.util.text.Component;

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
			//			ScrollGui scrollGui = launcher().guiManager().createGui(ScrollGui.class);
			//			scrollGui.xProperty().bind(xProperty());
			//			scrollGui.yProperty().bind(yProperty());
			//			scrollGui.heightProperty().bind(heightProperty());
			//			scrollGui.widthProperty().bind(widthProperty());
			//			container = new GuiContainer(launcher);
			int x = 0;
			int y = 0;
			Map<Node<T, V>, GUINode> guiMap = new HashMap<>();
			for (Node<T, V> node : graph.nodes()) {
				GUINode guiNode = new GUINode(launcher, node, xProperty().add(x * 255),
						yProperty().add(y * 55));
				guiNode.height(50);
				guiNode.width(250);
				if (guiNode.x() + guiNode.width() < guiNode.y() + guiNode.height()) {
					x++;
				} else {
					y++;
					x = 0;
				}
				GUIs.add(guiNode);
				guiMap.put(node, guiNode);
			}
			for (Connection<T, V> connection : graph.connections()) {
				GUIs.add(new GUIConnection(launcher(), guiMap.get(connection.from()),
						guiMap.get(connection.to()), connection));
			}
			//			scrollGui.gui().setValue(container);
			//			GUIs.add(scrollGui);
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

			public GUINode(GameLauncher launcher, Node<T, V> node, NumberValue bindX,
					NumberValue bindY) throws GameException {
				super(launcher);
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
			private float pressDeltaX = 0;
			private float pressDeltaY = 0;

			@Override
			protected boolean doHandle(KeybindEntry entry) throws GameException {
				if (entry instanceof MouseButtonKeybindEntry mbe) {
					if (mbe.type() == Type.PRESS) {
						pressInitialMouseX = mbe.mouseX();
						pressInitialMouseY = mbe.mouseY();
						pressInitialX = offsetX.floatValue();
						pressInitialY = offsetY.floatValue();
						pressDeltaX = 0;
						pressDeltaY = 0;
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
