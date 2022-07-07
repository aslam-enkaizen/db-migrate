package com.exrade.runtime.timer;

import com.exrade.core.ExLogger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class TimeEventExecutor {

	public static void main(String[] args) {
        ExLogger.get().info("Scheduler starting...");

        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown(scheduler)));
        } catch (Exception e) {
        	ExLogger.get().error("Error starting scheduler");
        }
    }
	
}

class Shutdown implements Runnable {

    private Scheduler scheduler;
    
    public Shutdown(Scheduler s) {
        this.scheduler = s;
    }
    
    public void run() {
        try {
        	ExLogger.get().info("Scheduler shutting down...");
            scheduler.shutdown();
        } catch (SchedulerException e) {
        	ExLogger.get().error("Error shutting down scheduler");
        }
    }
}