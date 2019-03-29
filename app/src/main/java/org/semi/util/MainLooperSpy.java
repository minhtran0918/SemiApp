package org.semi.util;

import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.util.Log;

import java.lang.reflect.Field;

public class MainLooperSpy {
    private final Field messagesField;
    private final Field nextField;
    private final MessageQueue mainMessageQueue;
    public MainLooperSpy() {
        try {
            Field queueField = Looper.class.getDeclaredField("mQueue");
            queueField.setAccessible(true);
            messagesField = MessageQueue.class.getDeclaredField("mMessages");
            messagesField.setAccessible(true);
            nextField = Message.class.getDeclaredField("next");
            nextField.setAccessible(true);
            Looper mainLooper = Looper.getMainLooper();
            mainMessageQueue = (MessageQueue) queueField.get(mainLooper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void dumpQueue() {
        try {
            Message nextMessage = (Message) messagesField.get(mainMessageQueue);
            Log.w("MainLooperSpy", "Begin dumping queue");
            dumpMessages(nextMessage);
            Log.w("MainLooperSpy", "End dumping queue");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public void dumpMessages(Message message) throws IllegalAccessException {
        if (message != null) {
            Log.w("MainLooperSpy", message.toString());
            Message next = (Message) nextField.get(message);
            dumpMessages(next);
        }
    }
}
