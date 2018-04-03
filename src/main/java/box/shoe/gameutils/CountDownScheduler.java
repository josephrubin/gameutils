package box.shoe.gameutils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Schedules tasks to occur at some point in the future.
 * A Runnable will be fired after the given number of calls to tick().
 */

public class CountDownScheduler implements Updatable
{ //TODO: just have tasks be scheduled based on number of updates, and not ms. Timing based on ms was unreliable, and not super helpful.
    // The scheduled tasks.
    private Set<Task> tasks;

    // Temporary buffer of tasks that are to be scheduled.
    // We do not always schedule tasks right away, in case we are currently using the tasks,
    // So we avoid a Concurrent modification Exception by waiting until the appropriate time.
    // Contract: tasks are scheduled before they are due to be fired.
    private Set<Task> tasksBuffer;

    /**
     * Create a CountDownScheduler.
     */
    public CountDownScheduler()
    {
        tasks = new HashSet<>();
        tasksBuffer = new HashSet<>();
    }

    /**
     * Register a Runnable to be fired after the supplied number of ticks have been counted.
     * @param ticks the number of times tick() will be called before the Runnable fires each time.
     * @param repetitions the number of firings of the runnable before being removed. 0 means eternal.
     * @param runnable the Runnable to fire.
     */
    public void schedule(int ticks, int repetitions, Runnable runnable)
    {
        // We add new tasks to a buffer, because we won't schedule them for real until
        // we know it is safe to do so (the scheduled tasks are not being accessed some other way).
        // If we scheduled them to the real list now, then, e.g., if the firing and removal of
        // one task scheduled another while the tasks were still ticking, a CoMoEx would be thrown.
        tasksBuffer.add(new Task(ticks, repetitions, runnable));
    }

    /**
     * Remove all scheduled Runnables.
     */
    public synchronized void cancelAll()
    {
        tasks.clear();
        tasksBuffer.clear();
    }

    @Override
    public synchronized void update()
    {
        // Now we can safely schedule the tasks from the buffer.
        tasks.addAll(tasksBuffer);
        tasksBuffer.clear();

        // Iterate over all of the tasks and tock them. Then remove the ones which have
        // exhausted their number of firings.
        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext())
        {
            boolean taskExhausted = iterator.next().tock();
            if (taskExhausted)
            {
                iterator.remove();
            }
        }
    }

    private static class Task
    {
        private int maxFrames;
        private int currentFrame;
        private int maxRepetitions;
        private int currentRepetition;
        private Runnable runnable;

        private Task(int maxFrames, int repetitions, Runnable runnable)
        {
            currentFrame = 0;
            this.maxFrames = maxFrames;
            currentRepetition = 0;
            this.maxRepetitions = repetitions;
            if (this.maxFrames == 0)
            {
                throw new IllegalArgumentException("This task is scheduled in 0 ticks. Events must be scheduled for the future.");
            }
            this.runnable = runnable;
        }

        private boolean tock() //Returns true if this event should be removed from the set (it should not repeat)
        {
            currentFrame++;
            if (currentFrame >= maxFrames) //If we have exhausted the delay
            {
                currentFrame = 0;
                runnable.run();

                if (maxRepetitions == 0)
                    return false; //Eternally repeat

                currentRepetition++;
                if (currentRepetition >= maxRepetitions)
                {
                    runnable = null;
                    return true; //If we have reached the desired repetitions, remove
                }
            }

            return false;
        }

        @Override
        public boolean equals(Object otherObject)
        {
            if (otherObject == null)
                return false;
            if (otherObject.getClass() != getClass())
                return false;
            Task otherTask = (Task) otherObject;
            return maxFrames == otherTask.maxFrames && runnable.equals(otherTask.runnable);
        }
    }
}
