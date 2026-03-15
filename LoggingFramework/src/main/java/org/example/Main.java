package org.example;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
	public static void main(String[] args) {
	}

	interface LogCallBack {
		void ack();
	}

	interface Module extends LogCallBack {
		void doSomeTask();

		class ModuleImpl implements Module {
			private final Logger logger;
			private final String id;

			public ModuleImpl(Logger logger, String id) {
				this.logger = logger;
				this.id = id;
			}

			@Override
			public void doSomeTask() {
				logger.log(new Logger.LogTask(id, "some task"), this);
			}

			@Override
			public void ack() {
				System.out.println("Logging is complete");
			}
		}
	}


	interface LoggingStrategy {
		void write(String message);

		enum OutputType {
			TOOL, FILE
		}

		static LoggingStrategy getStrategy(OutputType output) {
			return switch (output) {
				case FILE -> new FileLoggingStrategy();
				case TOOL -> new ToolLoggingStrategy();
				case null, default -> throw new RuntimeException();
			};
		}
		final class FileLoggingStrategy implements LoggingStrategy {
			@Override
			public void write(String message) {
				// write to a file
			}
		}

		final class ToolLoggingStrategy implements LoggingStrategy {
			@Override
			public void write(String message) {
				// write to a tool
			}
		}
	}

	interface Logger {
		void log(LogTask logTask, LogCallBack cb);

		record LogTask(String moduleId, String action){}

		final class OneThreadLogger implements Logger {

			private final Thread writeWorker;
			private final BlockingQueue<LogTask> taskQueue;
			private final Map<String, LogCallBack> moduleVsCallBack;
			private LoggingStrategy loggingStrategy;

			public OneThreadLogger() {
				taskQueue = new ArrayBlockingQueue<>(10);
				moduleVsCallBack = new ConcurrentHashMap<>();
				writeWorker = new Thread(() -> {
					// keep thread alive
					while(!Thread.currentThread().isAlive()) {
						try {
							LogTask task = taskQueue.take();
							performLogging(task.action());
							moduleVsCallBack.get(task.moduleId()).ack();
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
					}
				});
			}

			@Override
			public void log(LogTask logTask, LogCallBack cb) {
				taskQueue.add(logTask);
				moduleVsCallBack.putIfAbsent(logTask.moduleId(), cb);
			}

			private void performLogging(String message) {
				loggingStrategy.write(message); // IO
			}
		}
	}

	// logger.log() // Will run a task to log to a file
	// after completion ack -> Module gives some callback that logger can use
	//
}

/*
* Design Multi-Module Logging Framework There are controllers which has module 1, module2, module 3, need to design logging capability framework for the complete system.
* When module1/2/3 has done some activity it will send log data to your framework.
* and framework should log to a file/any tool as a stream.
* Three threads one writing to the buffer,your framework thread is reading it. and ack when it is written to the file/tool.
* */