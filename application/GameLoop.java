package application;

import javafx.animation.AnimationTimer;
import application.javafx.Controller;

public class GameLoop extends AnimationTimer {
  public static final int ONE_SECOND_NANO = 1_000_000_000;
  private long prevNanoTime = 0;
  private long nowNanoTime = 0;
  private long startNanoTime;

  @Override
  public void handle(long now) {
    if (now - getNowNanoTime() < 2_777_777.777777777777777777777) {
      return;
    }
    setPrevNanoTime(getNowNanoTime());
    setNowNanoTime(now);
    if (!Game.getInstance().isPause()) {
      Controller.getInstance().update();
      Controller.getInstance().draw();
    }
  }

  @Override
  public void start() {
    super.start();
    setStartNanoTime(System.nanoTime());
    setPrevNanoTime(System.nanoTime());
  }

  public int getFramesPerSecond() {
    return (int)(1 / getFrameTimeSeconds());
  }

  public int getFrameTime() {
    return (int)(getNowNanoTime() - getPrevNanoTime());
  }

  public double getFrameTimeSeconds() {
    return (double) getFrameTime() / (double) ONE_SECOND_NANO;
  }

  public long getNowNanoTime() {
    return nowNanoTime;
  }

  private void setNowNanoTime(long nowNanoTime) {
    this.nowNanoTime = nowNanoTime;
  }

  public long getPrevNanoTime() {
    return prevNanoTime;
  }

  private void setPrevNanoTime(long prevNanoTime) {
    this.prevNanoTime = prevNanoTime;
  }

  public long getStartNanoTime() {
    return startNanoTime;
  }

  private void setStartNanoTime(long startNanoTime) {
    this.startNanoTime = startNanoTime;
  }
}