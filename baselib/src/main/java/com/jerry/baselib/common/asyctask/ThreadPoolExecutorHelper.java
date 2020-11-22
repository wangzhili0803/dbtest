package com.jerry.baselib.common.asyctask;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wzl on 2017/11/28.
 *
 * @Description 获取线程池单例
 */
public class ThreadPoolExecutorHelper {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    private ThreadPoolExecutorHelper() {
    }

    public static ExecutorService getExecutorService() {
        return ThreadPoolExecutorHelperInner.EXECUTOR_SERVICE;
    }

    private static final class ThreadPoolExecutorHelperInner {

        private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, 15L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("dzcj-thread-%d").build(),
            new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    /**
     * A ThreadFactory builder, providing any combination of these features:
     * <ul>
     * <li>whether threads should be marked as {@linkplain Thread#setDaemon daemon} threads
     * <li>a {@linkplain ThreadFactoryBuilder#setNameFormat naming format}
     * <li>a {@linkplain Thread#setPriority thread priority}
     * <li>an {@linkplain Thread#setUncaughtExceptionHandler uncaught exception handler}
     * <li>a {@linkplain ThreadFactory#newThread backing thread factory}
     * </ul>
     * <p>If no backing thread factory is provided, a default backing thread factory is used as if by
     * calling {@code setThreadFactory(}{@link Executors#defaultThreadFactory()}{@code )}.
     *
     * @author Kurt Alfred Kluever
     * @since 4.0
     */
    public static final class ThreadFactoryBuilder {

        private String nameFormat;
        private Boolean daemon;

        /**
         * Creates a new {@link ThreadFactory} builder.
         */
        ThreadFactoryBuilder() {
        }

        // Split out so that the anonymous ThreadFactory can't contain a reference back to the builder.
        // At least, I assume that's why. TODO(cpovirk): Check, and maybe add a test for this.
        private static ThreadFactory doBuild(ThreadFactoryBuilder builder) {
            final String nameFormat = builder.nameFormat;
            final Boolean daemon = builder.daemon;
            final ThreadFactory backingThreadFactory = Executors.defaultThreadFactory();
            final AtomicLong count = (nameFormat != null) ? new AtomicLong(0) : null;
            return runnable -> {
                Thread thread = backingThreadFactory.newThread(runnable);
                if (nameFormat != null) {
                    thread.setName(format(nameFormat, count.getAndIncrement()));
                }
                if (daemon != null) {
                    thread.setDaemon(daemon);
                }
                return thread;
            };
        }

        private static String format(String format, Object... args) {
            return String.format(Locale.ROOT, format, args);
        }

        /**
         * Sets the naming format to use when naming threads ({@link Thread#setName}) which are created
         * with this ThreadFactory.
         *
         * @param nameFormat a {@link String#format(String, Object...)}-compatible format String, to which a unique integer (0, 1, etc.) will be
         * supplied as the single parameter. This integer will be unique to the built instance of the ThreadFactory and will be assigned sequentially.
         * For example, {@code "rpc-pool-%d"} will generate thread names like {@code "rpc-pool-0"}, {@code "rpc-pool-1"}, {@code "rpc-pool-2"}, etc.
         * @return this for the builder pattern
         */
        public ThreadFactoryBuilder setNameFormat(String nameFormat) {
            this.nameFormat = nameFormat;
            return this;
        }

        /**
         * Sets daemon or not for new threads created with this ThreadFactory.
         *
         * @param daemon whether or not new Threads created with this ThreadFactory will be daemon threads
         * @return this for the builder pattern
         */
        public ThreadFactoryBuilder setDaemon(boolean daemon) {
            this.daemon = daemon;
            return this;
        }

        /**
         * Returns a new thread factory using the options supplied during the building process. After
         * building, it is still possible to change the options used to build the ThreadFactory and/or
         * build again. State is not shared amongst built instances.
         *
         * @return the fully constructed {@link ThreadFactory}
         */
        public ThreadFactory build() {
            return doBuild(this);
        }
    }
}
