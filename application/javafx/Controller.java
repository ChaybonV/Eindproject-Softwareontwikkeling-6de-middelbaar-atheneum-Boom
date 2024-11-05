package application.javafx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import application.math.Coordinate;
import application.objects.Updatable;
import application.ErrorMessageDisplayer;
import application.Game;
import application.GameLoop;
import application.entities.Entity;
import application.entities.Hero;
import application.items.Armor;
import application.items.Backpack;
import application.items.Item;
import application.items.weapons.Weapon;
import application.items.weapons.shooters.Shooter;
import application.items.weapons.shooters.magazines.HeavyMagazine;
import application.items.weapons.shooters.magazines.LightMagazine;
import application.items.weapons.shooters.magazines.MediumMagazine;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Controller implements Updatable {
  public static final int FPS_UPDATE_TIME = GameLoop.ONE_SECOND_NANO / 4;
  private static Controller container;
  private final List < KeyCode > keys = new LinkedList < > ();
  private final GameLoop gameLoop = new GameLoop();
  private final Coordinate cursor = new Coordinate(0, 0);
  private final ErrorMessageDisplayer errorMessageDisplayer = new ErrorMessageDisplayer();
  private Scene scene;

  @FXML
  private SplitPane sidePanel;
  @FXML
  private Pane canvasAnchorPane;
  @FXML
  private Label backpackCurrentMass;
  @FXML
  private Label backpackMaxMass;
  @FXML
  private Label magazineTotalLight;
  @FXML
  private Label magazineTotalMedium;
  @FXML
  private Label magazineTotalHeavy;
  @FXML
  private ChoiceBox < Armor > bodyChoice;
  @FXML
  private ChoiceBox < Weapon > handsChoice;
  @FXML
  private ChoiceBox < Backpack > backChoice;
  @FXML
  private TableView < TableItem > backpackTable;
  @FXML
  private TableView < TableItem > pickUpTable;
  @FXML
  private Label magazineTotal;
  @FXML
  private ProgressBar magazineBar;
  @FXML
  private Label magazineMax;
  @FXML
  private ProgressBar boostBar;
  @FXML
  private Canvas canvas;
  @FXML
  private MenuBar menuBar;
  @FXML
  private ProgressBar healthBar;
  @FXML
  private BorderPane borderPane;
  @FXML
  private ProgressBar armorBar;
  private long prevFpsUpdateTime;
  private int fps = 0;

  public static Controller getInstance() {
    return container;
  }

  public static void applyColumnCellValueFactories(TableView < TableItem > view) {
    for (TableColumn < TableItem, ? > column : view.getColumns()) {
      switch (column.getText().toLowerCase()) {
      case "name" -> column.setCellValueFactory(new PropertyValueFactory < > ("name"));
      case "mass" -> column.setCellValueFactory(new PropertyValueFactory < > ("mass"));
      case "extra" -> column.setCellValueFactory(new PropertyValueFactory < > ("extra"));
      }
    }
  }

  @SuppressWarnings({"rawtypes","unchecked"})
  public static void updateListToOtherList(List oldList, List newListItems) {
    if (newListItems.size() == 0) {
    	oldList.clear();
    }
    oldList.removeIf(o -> !newListItems.contains(o));
    newListItems.forEach(o -> {
      if (!oldList.contains(o)) {
        oldList.add(o);
      }
    });
  }

  public void initialize(Scene scene) {
    Controller.container = this;
    setScene(scene);
    resize();

    scene.setOnKeyPressed(event -> {
      if (!getKeys().contains(event.getCode())) {
        getKeys().add(event.getCode());
      }
    });
    scene.setOnKeyReleased(event -> {
      getKeys().remove(event.getCode());
    });
    scene.setOnMouseMoved(event -> {
      final Coordinate cursor = getCursor();
      cursor.setX(event.getSceneX());
      cursor.setY(event.getSceneY() - Controller.getInstance().getMenuBar().getHeight());
    });

    Game.getInstance().initialize();

    applyColumnCellValueFactories(backpackTable);
    applyColumnCellValueFactories(pickUpTable);

    pickUpTable.setOnMouseClicked(this::pickUpTableMouseClickHandler);

    backpackTable.setOnMouseClicked(this::backpackTableMouseClickHandler);

    final Hero player = Game.getInstance().getPlayer();
    handsChoice.setOnAction((ActionEvent event) -> {
      Weapon item = handsChoice.getValue();
      if (item == null) {
    	  return;
      }

      player.getBackpack().removeItem(item);

      Item oldItem = player.setHand(item);
      if (oldItem == null) {
    	  return;
      }
      player.getBackpack().addItemToBackpack(oldItem);
    });

    bodyChoice.setOnAction(event -> {
      Armor armor = bodyChoice.getValue();
      if (armor == null) return;

      player.getBackpack().removeItem(armor);

      Item oldItem = player.setArmor(armor);
      if (oldItem == null) return;

      player.getBackpack().addItemToBackpack(oldItem);
    });

    backChoice.setOnAction(event -> {
      Backpack backpack = backChoice.getValue();

      if (backpack == null) return;

      player.changeBackpack(backpack);
    });

    final Item handItem = player.getHand();
    if (handItem instanceof Weapon) {
      handsChoice.getItems().add((Weapon) handItem);
      handsChoice.getSelectionModel().select((Weapon) handItem);
    }
    final Armor bodyItem = player.getArmor();
    if (bodyItem != null) {
      bodyChoice.getItems().add(bodyItem);
      bodyChoice.getSelectionModel().select(bodyItem);
    }
    final Backpack backItem = player.getBackpack();
    if (backItem != null) {
      backChoice.getItems().add(backItem);
      backChoice.getSelectionModel().select(backItem);
    }
    gameLoop.start();
  }

  private void resize() {
    canvas.setWidth(canvasAnchorPane.getWidth());
    canvas.setHeight(canvasAnchorPane.getHeight());
  }

  public void showError(String message) {
    if (message == null || message.isBlank()) {
    	return;
    }
    getErrorMessageDisplayer().addMessage(message);
  }

  @Override
  public void update() {
    Game.getInstance().update();
    resize();
  }

  public void draw() {
    final Hero player = Game.getInstance().getPlayer();
    healthBar.setProgress(player.getHpPercentage());

    final Armor armor = player.getArmor();
    if (armor == null) {
        armorBar.setProgress(0);
    } else {
        armorBar.setProgress(armor.getProtectionPercentage());
    }

    Game.getInstance().draw(canvas.getGraphicsContext2D());

    updateListToOtherList(pickUpTable.getItems(), player.getAllSurroundingItems());

    final Backpack backpack = player.getBackpack();
    final List < Item > handItems = backpack.getItems().stream().filter(item -> item instanceof Weapon).collect(Collectors.toList());
    if (player.getHand() instanceof Weapon) {
      handItems.add(player.getHand());
      if (handsChoice.getSelectionModel().getSelectedItem() != player.getHand()) {
    	  handsChoice.getSelectionModel().select((Weapon) player.getHand());
      }
    }
    updateListToOtherList(handsChoice.getItems(), handItems);

    final List < Item > bodyItems = backpack.getItems().stream().filter(item -> item instanceof Armor).collect(Collectors.toList());
    if (player.getArmor() != null) {
      bodyItems.add(player.getArmor());
      if (bodyChoice.getSelectionModel().getSelectedItem() != player.getArmor()) {
        bodyChoice.getSelectionModel().select(player.getArmor());
      }
    }
    updateListToOtherList(bodyChoice.getItems(), bodyItems);

    final List < Item > backItems = backpack.getItems().stream().filter(item -> item instanceof Backpack).collect(Collectors.toList());
    backItems.add(backpack);
    if (backChoice.getSelectionModel().getSelectedItem() != backpack) {
    	backChoice.getSelectionModel().select(backpack);
    }
    updateListToOtherList(backChoice.getItems(), backItems);

    updateListToOtherList(backpackTable.getItems(), backpack.getItems());

    backpackCurrentMass.setText(String.valueOf(backpack.getMassInBackpack()));
    backpackMaxMass.setText(String.valueOf(backpack.getMaxMass()));

    int amountOfLightMz = 0;
    int amountOfMediumMz = 0;
    int amountOfHeavyMz = 0;
    for (Item item: backpack.getItems()) {
      if (item instanceof LightMagazine) {
    	  amountOfLightMz += ((LightMagazine) item).getAmount();
      }
      else if (item instanceof MediumMagazine) {
    	  amountOfMediumMz += ((MediumMagazine) item).getAmount();
      }
      else if (item instanceof HeavyMagazine) {
    	  amountOfHeavyMz += ((HeavyMagazine) item).getAmount();
      }
    }

    magazineTotalLight.setText(String.valueOf(amountOfLightMz));
    magazineTotalMedium.setText(String.valueOf(amountOfMediumMz));
    magazineTotalHeavy.setText(String.valueOf(amountOfHeavyMz));

    magazineBar.setProgress(0);
    magazineTotal.setText(String.valueOf(0));
    magazineMax.setText(String.valueOf(0));
    final Item handItem = player.getHand();
    if (handItem instanceof Weapon) {
      if (handItem instanceof Shooter) {
        if (((Shooter < ? > ) handItem).getMagazine() != null) {
          magazineBar.setProgress(((Shooter < ? > ) handItem).getMagazine().getPercentageLoaded());
          magazineTotal.setText(String.valueOf(((Shooter < ? > ) handItem).getMagazine().getAmount()));
          magazineMax.setText(String.valueOf(((Shooter < ? > ) handItem).getMagazine().getMax()));
        }
      }
    }

    boostBar.setProgress(player.getBoost().getPercentage());

    if (getGameLoop().getNowNanoTime() - getPrevFpsUpdateTime() > FPS_UPDATE_TIME) {
      setPrevFpsUpdateTime(getGameLoop().getNowNanoTime());
      setFps(getGameLoop().getFramesPerSecond());
    }
    getErrorMessageDisplayer().draw(canvas.getGraphicsContext2D());
  }

  public int getFps() {
    return fps;
  }

  public void setFps(int fps) {
    this.fps = fps;
  }

  public long getPrevFpsUpdateTime() {
    return prevFpsUpdateTime;
  }

  public void setPrevFpsUpdateTime(long prevFpsUpdateTime) {
    this.prevFpsUpdateTime = prevFpsUpdateTime;
  }

  public List < KeyCode > getKeys() {
    return keys;
  }

  public GameLoop getGameLoop() {
    return gameLoop;
  }

  public Coordinate getCursor() {
    return cursor;
  }

  public MenuBar getMenuBar() {
    return menuBar;
  }

  public void canvasClick(MouseEvent mouseEvent) {
    canvas.requestFocus();
  }

  public Canvas getCanvas() {
    return canvas;
  }

  public Scene getScene() {
    return scene;
  }

  public void setScene(Scene scene) {
    this.scene = scene;
  }

  public ErrorMessageDisplayer getErrorMessageDisplayer() {
    return errorMessageDisplayer;
  }
  
  public void pauseGame(boolean pause) {
    Game.getInstance().setPause(pause);
    sidePanel.setDisable(pause);
  }

  public void pauseGame() {
    pauseGame(!Game.getInstance().isPause());
  }

  public void startNewGame() {
    Game.startNewGame();
    getErrorMessageDisplayer().clear();
    sidePanel.setDisable(Game.getInstance().isPause());
    initialize(getScene());
  }

  public void close() {
    Platform.exit();
  }

  private void backpackTableMouseClickHandler(MouseEvent event) {
    if (backpackTable.getSelectionModel().getSelectedIndex() < 0)
      return;

    final Item item = (Item) backpackTable.getItems().remove(backpackTable.getSelectionModel().getSelectedIndex());

    final Backpack backpack = Game.getInstance().getPlayer().getBackpack();
    if (backpack == null)
      return;

    backpack.dropItemFromBackpack(item);

    backpackTable.getSelectionModel().clearSelection();
  }

  private void pickUpTableMouseClickHandler(MouseEvent event) {
    if (pickUpTable.getSelectionModel().getSelectedIndex() < 0)
      return;

    final Item item = (Item) pickUpTable.getItems().get(pickUpTable.getSelectionModel().getSelectedIndex());

    try {
      Game.getInstance().getPlayer().addItemToBackpack(item);
      pickUpTable.getItems().remove(pickUpTable.getSelectionModel().getSelectedIndex());
    } catch (Entity.NoBackpack ignored) {
      showError("U heeft geen rugzak om dit item aan toe te voegen!");
    } catch (Backpack.MaxMassExceeded ignored) {
      showError("De rugzak zit vol!");
    }

    pickUpTable.getSelectionModel().clearSelection();
  }
}