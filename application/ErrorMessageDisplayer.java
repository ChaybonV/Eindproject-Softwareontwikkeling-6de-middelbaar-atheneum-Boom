package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import application.math.Coordinate;
import application.math.Number;
import application.objects.Drawable;
import application.config.ConfigHandler;
import application.javafx.Draw;

import java.util.LinkedList;
import java.util.Queue;

public class ErrorMessageDisplayer implements Drawable {
  private final Queue < ErrorMessage > messages;
  private ErrorMessage currrentMessage;
  private long startTimeShowingCurrentErrorMessage;

  public ErrorMessageDisplayer() {
    this.messages = new LinkedList < > ();
  }

  public void addMessage(String message) {
    getMessages().add(new ErrorMessage(message));
  }

  @Override
  public void draw(GraphicsContext gc) {
    refreshCurrentMessageIfNeeded();

    if (getCurrentMessage() == null) return;

    final double width = gc.getCanvas().getWidth();
    final double heigth = gc.getCanvas().getHeight();
    final double fontSize = Number.constrain(heigth * .05, 30, 80);

    gc.setFont(Font.font("Roboto", fontSize));
    gc.setTextAlign(TextAlignment.CENTER);
    gc.setFill(Color.DARKRED);
    Draw.text(gc, getCurrentMessage().message(), new Coordinate(width / 2, fontSize * 2));
  }

  private void refreshCurrentMessageIfNeeded() {
    if (getCurrentMessage() == null) {
      setCurrentMessage(getNewQueuedMessage());
    }

    if (getStartTimeShowingCurrentErrorMessage() < System.nanoTime() - ConfigHandler.getLong("ErrorMessageDisplayer.MESSAGE_TIME")) {
      if (getCurrentMessage() != null) {
        setCurrentMessage(null);
        refreshCurrentMessageIfNeeded();
      }
    }
  }

  private ErrorMessage getNewQueuedMessage() {
    final long currentNanoTime = System.nanoTime();

    while (getMessages().size() > 0) {
      final ErrorMessage queueMessage = getMessages().remove();

      if (queueMessage.canBeShown(currentNanoTime))
        return queueMessage;
    }

    return null;
  }

  public long getStartTimeShowingCurrentErrorMessage() {
    return startTimeShowingCurrentErrorMessage;
  }

  public void setStartTimeShowingCurrentErrorMessage(long startTimeShowingCurrentErrorMessage) {
    this.startTimeShowingCurrentErrorMessage = startTimeShowingCurrentErrorMessage;
  }

  public ErrorMessage getCurrentMessage() {
    return currrentMessage;
  }

  public void setCurrentMessage(ErrorMessage currentMessage) {
    setStartTimeShowingCurrentErrorMessage(currentMessage == null ? 0 : System.nanoTime());
    this.currrentMessage = currentMessage;
  }

  public Queue < ErrorMessage > getMessages() {
    return messages;
  }

  public void clear() {
    getMessages().clear();
    setCurrentMessage(null);
  }

  public static record ErrorMessage(String message, long time) {
    public ErrorMessage(String message) {
      this(message, System.nanoTime());
    }

    public boolean canBeShown(long currentNanoTime) {
      return time() > currentNanoTime - ConfigHandler.getLong("ErrorMessageDisplayer.QUEUE_TIME");
    }
  }
}