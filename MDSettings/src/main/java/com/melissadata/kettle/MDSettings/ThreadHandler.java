package com.melissadata.kettle.MDSettings;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.pentaho.di.core.logging.LogChannelInterface;

public class ThreadHandler implements Callable<Integer> {

	// get License values and sets default values.
	public static class SettingCallable implements Callable<Integer> {

		private AdvancedConfigInterface acInterface;

		public SettingCallable(AdvancedConfigInterface acInterface) {

			this.acInterface = acInterface;
		}

		public Integer call() {

			acInterface.setDataValues();
			return 1;
		}
	}

	private static                      ExecutorService     settingThread;
	private static                      FutureTask<Integer> taskSet;
	@SuppressWarnings("unused") private LogChannelInterface log;

	public ThreadHandler(AdvancedConfigInterface acInterface, LogChannelInterface log) {

		this.log = log;
		settingThread = Executors.newSingleThreadExecutor();
		taskSet = new FutureTask<Integer>(new SettingCallable(acInterface));
	}

	@Override
	public Integer call() throws Exception {

		settingThread.submit(taskSet);
		try {
			taskSet.get();
		} catch (InterruptedException e) {
			// ignore
		} catch (ExecutionException e) {
			// ignore
		}
		return 1;
	}
}
