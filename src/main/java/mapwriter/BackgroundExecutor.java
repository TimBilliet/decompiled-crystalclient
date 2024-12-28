package mapwriter;

import mapwriter.tasks.Task;
import mapwriter.util.Logging;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class BackgroundExecutor {
  private final ExecutorService executor;
  
  private final LinkedList<Task> taskQueue;
  
  public boolean closed = false;
  
  private boolean doDiag = false;
  
  public BackgroundExecutor() {
    this.executor = Executors.newSingleThreadExecutor();
    this.taskQueue = new LinkedList<>();
  }
  
  public void addTask(Task task) {
    if (!this.closed) {
      if (!task.CheckForDuplicate()) {
        Future<?> future = this.executor.submit((Runnable)task);
        task.setFuture(future);
        this.taskQueue.add(task);
      } 
      this.doDiag = (tasksRemaining() <= 500 || !this.doDiag);
    } else {
      Logging.log("MwExecutor.addTask: error: cannot add task to closed executor");
    } 
  }
  
  public boolean processTaskQueue() {
    boolean processed = false;
    Task task = this.taskQueue.poll();
    if (task != null)
      if (task.isDone()) {
        task.printException();
        task.onComplete();
        processed = true;
      } else {
        this.taskQueue.push(task);
      }  
    return !processed;
  }
  
  public boolean processRemainingTasks(int attempts, int delay) {
    while (this.taskQueue.size() > 0 && attempts > 0) {
      if (processTaskQueue()) {
        try {
          Thread.sleep(delay);
        } catch (Exception exception) {}
        attempts--;
      } 
    } 
    return (attempts <= 0);
  }
  
  public int tasksRemaining() {
    return this.taskQueue.size();
  }
  
  public boolean close() {
    boolean error = true;
    try {
      taskLeftPerType();
      this.executor.shutdown();
      processRemainingTasks(50, 5);
      error = !this.executor.awaitTermination(10L, TimeUnit.SECONDS);
      error = false;
    } catch (InterruptedException e) {
      Logging.log("error: IO task was interrupted during shutdown");
      e.printStackTrace();
    } 
    this.closed = true;
    return error;
  }
  
  private void taskLeftPerType() {
    HashMap<String, Object> tasksLeft = new HashMap<>();
    for (Task t : this.taskQueue) {
      String className = t.getClass().toString();
      if (tasksLeft.containsKey(className)) {
        tasksLeft.put(className, (Integer) tasksLeft.get(className) + 1);
        continue;
      } 
      tasksLeft.put(className, 1);
    } 
    for (Map.Entry<String, Object> entry : tasksLeft.entrySet()) {
      String key = entry.getKey();
      Object object = entry.getValue();
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\BackgroundExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */