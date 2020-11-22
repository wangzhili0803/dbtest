package com.jerry.baselib.common.asyctask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Looper;
import android.os.Message;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.jerry.baselib.common.util.WeakHandler;

/**
 * Created by wzl on 2017/11/21.
 *
 * @Description 异步任务库
 */
public class AppTask implements LifecycleObserver {
    private static final int MESSAGE_FINISH = 0x11;
    private static final int MESSAGE_BROKEN = 0x22;

    private static final Integer ID_NONE_CONTEXT = 0x66;

    private static AtomicInteger count = new AtomicInteger(0);
    private Holder mHolder = null;
    private Map<Integer, BackgroundTask> mTaskMap = new ConcurrentHashMap<>();
    private Map<Integer, WhenTaskDone> mTaskDoneMap = new ConcurrentHashMap<>();
    private Map<Integer, WhenTaskBroken> mTaskBrokenMap = new ConcurrentHashMap<>();
    private Map<Integer, WhenTaskEnd> mTaskEndMap = new ConcurrentHashMap<>();
    private WeakHandler handler = new WeakHandler(Looper.getMainLooper(), message -> {
        if (!(message.obj instanceof Holder)) {
            return false;
        }
        if (message.what == MESSAGE_FINISH) {
            Holder result = (Holder) message.obj;

            mTaskMap.remove(result.id);
            mTaskBrokenMap.remove(result.id);

            WhenTaskDone listener = mTaskDoneMap.remove(result.id);
            if (listener != null) {
                listener.whenDone(result.object);
            }
            WhenTaskEnd end = mTaskEndMap.remove(result.id);
            if (end != null) {
                end.whenEnd();
            }
        } else if (message.what == MESSAGE_BROKEN) {
            Holder result = (Holder) message.obj;

            mTaskMap.remove(result.id);
            mTaskDoneMap.remove(result.id);

            WhenTaskBroken listener = mTaskBrokenMap.remove(result.id);
            if (listener != null && result.object instanceof Throwable) {
                listener.whenBroken(((Throwable) result.object));
            }
            WhenTaskEnd end = mTaskEndMap.remove(result.id);
            if (end != null) {
                end.whenEnd();
            }
        }
        resetHolder();
        return true;
    });

    @OnLifecycleEvent(Event.ON_DESTROY)
    public void onDestroy(){
        resetHolder();

        if (mTaskMap != null) {
            mTaskMap.clear();
        }

        if (mTaskDoneMap != null) {
            mTaskDoneMap.clear();
        }

        if (mTaskBrokenMap != null) {
            mTaskBrokenMap.clear();
        }

        if (mTaskEndMap != null) {
            mTaskEndMap.clear();
        }
    }

    private AppTask() {
    }

    private static AppTask getInstance() {
        return DzcjTaskHolder.INSTANCE;
    }

    @MainThread
    public static TaskRegister with(@NonNull FragmentActivity activity) {
        activity.getLifecycle().addObserver(getInstance());
        return withoutContext();
    }

    @MainThread
    public static TaskRegister with(@NonNull Fragment fragment) {
        fragment.getLifecycle().addObserver(getInstance());
        return withoutContext();
    }

    @MainThread
    public static TaskRegister withoutContext() {
        return getInstance().buildRegister();
    }

    @WorkerThread
    public static void post(@NonNull Message message) {
        getInstance().handler.sendMessage(message);
    }

    private TaskRegister buildRegister() {
        mHolder = new Holder(ID_NONE_CONTEXT, new Object());
        return new TaskRegister(count.getAndIncrement());
    }

    private void resetHolder() {
        if (mHolder == null) {
            return;
        }

        mHolder.id = 0;
        mHolder.object = null;
        mHolder = null;
    }

    private Runnable buildRunnable(@NonNull final Integer id) {
        return () -> {
            if (mTaskMap.containsKey(id)) {
                Message message = Message.obtain();

                try {
                    message.what = MESSAGE_FINISH;
                    message.obj = new Holder(id, mTaskMap.get(id).onBackground());
                } catch (Throwable t) {
                    message.what = MESSAGE_BROKEN;
                    message.obj = new Holder(id, t);
                }

                post(message);
            }
        };
    }

    private static class DzcjTaskHolder {

        static final AppTask INSTANCE = new AppTask();
    }

    public class TaskRegister {

        private Integer id;

        private TaskRegister(Integer id) {
            this.id = id;
        }

        /**
         * 必须
         */
        public Builder assign(@NonNull BackgroundTask task) {
            mTaskMap.put(id, task);
            return new Builder(id);
        }

    }

    /**
     * 用于承载当前上下文环境 (FragmentActivity(v4)/Fragment(v4) ，
     * 当当前上下文环境的生命周期停止时，记住使用 resetHolder() 将 mHolder 重置。
     */
    private class Holder {

        private Integer id;

        private Object object;

        private Holder(@NonNull Integer id, @Nullable Object object) {
            this.id = id;
            this.object = object;
        }
    }

    public class Builder {

        private Integer id;

        private Builder(Integer id) {
            this.id = id;
        }

        /**
         * 可选。
         */
        public Builder whenDone(@NonNull WhenTaskDone<?> listener) {
            mTaskDoneMap.put(id, listener);
            return this;
        }

        /**
         * 可选。
         */
        public Builder whenBroken(@NonNull WhenTaskBroken listener) {
            mTaskBrokenMap.put(id, listener);
            return this;
        }

        /**
         * 可选。
         */
        public Builder whenTaskEnd(@NonNull WhenTaskEnd listener) {
            mTaskEndMap.put(id, listener);
            return this;
        }

        /**
         * 必须。
         */
        public void execute() {
            ThreadPoolExecutorHelper.getExecutorService().execute(buildRunnable(id));
        }
    }

}
