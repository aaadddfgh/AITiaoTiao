package aaadddfgh.myapp.AItiaotiao.service;

import android.accessibilityservice.AccessibilityButtonController;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import aaadddfgh.myapp.AItiaotiao.lib.AccessibilityNodeInfoTextConverter;
import aaadddfgh.myapp.AItiaotiao.lib.AdCloser;
import aaadddfgh.myapp.AItiaotiao.lib.ClassScanner;


public class AccessibilityServiceManager extends AccessibilityService {
    private ArrayList<AdCloser> adClosers=new ArrayList<>();
    private static final String TAG = "AccessibilityServiceManager";
    private static AccessibilityServiceManager service;
    private List<EventListener> eventListeners; // 一般事件监听器列表
    private List<TextChangeListener> textChangeListeners; // 文本变化事件监听器列表

    private AccessibilityButtonController accessibilityButtonController;
    private AccessibilityButtonController
            .AccessibilityButtonCallback accessibilityButtonCallback;
    private boolean mIsAccessibilityButtonAvailable;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();

        info.eventTypes =
                AccessibilityEvent.TYPE_VIEW_FOCUSED |
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED |
                AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY |
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED |
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED |
                AccessibilityEvent.TYPE_WINDOWS_CHANGED|
                AccessibilityEvent.TYPE_VIEW_SCROLLED ;

        info.notificationTimeout = 100;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        this.setServiceInfo(info);

        accessibilityButtonController = getAccessibilityButtonController();
        mIsAccessibilityButtonAvailable =
                accessibilityButtonController.isAccessibilityButtonAvailable();

        if (!mIsAccessibilityButtonAvailable) {
            return;
        }

        AccessibilityServiceInfo serviceInfo = getServiceInfo();
        serviceInfo.flags
                |= AccessibilityServiceInfo.FLAG_REQUEST_ACCESSIBILITY_BUTTON;
        setServiceInfo(serviceInfo);

        accessibilityButtonCallback =
                new AccessibilityButtonController.AccessibilityButtonCallback() {
                    @Override
                    public void onClicked(AccessibilityButtonController controller) {
                        Log.d("MY_APP_TAG", "Accessibility button pressed!");

                        // Add custom logic for a service to react to the
                        // accessibility button being pressed.

                    }

                    @Override
                    public void onAvailabilityChanged(
                            AccessibilityButtonController controller, boolean available) {
                        if (controller.equals(accessibilityButtonController)) {
                            mIsAccessibilityButtonAvailable = available;
                        }
                    }
                };

        if (accessibilityButtonCallback != null && accessibilityButtonController!=null) {
            accessibilityButtonController.registerAccessibilityButtonCallback(
                    accessibilityButtonCallback);
        }
    }

    private void logText(String s){
        new aaadddfgh.myapp.AItiaotiao.storage.LogFile(getApplicationContext()).writeLog(s);
    }

    public static AccessibilityServiceManager getInstance(){
        return service;
    }

    public AccessibilityServiceManager() {
        this.eventListeners = new ArrayList<>();
        this.textChangeListeners = new ArrayList<>();
    }

    @Override
    public void onCreate() {
        super.onCreate();


            //List<Class<?>> clazzes = ClassScanner.scanClasses("aaadddfgh.myapp.AItiaotiao.lib");
            List<String> names = ClassScanner.getClassesInPackage(getBaseContext(), "aaadddfgh.myapp.AItiaotiao.lib.ADCloseImpl");
            for (String className : names) {
                try {
                    // 加载类并实例化
                    Class<?> clazz = Class.forName(className);
                    AdCloser adCloser = (AdCloser) clazz.getDeclaredConstructor().newInstance();
                    adClosers.add(adCloser);
                } catch (Exception e) {
                    e.printStackTrace(); // 处理异常，例如类加载失败或实例化失败
                }
            }




        service = this;
        Log.d(TAG, "created!");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.d(TAG, "EVENT !!! " + eventTypeToString(accessibilityEvent.getEventType()));
        for (EventListener listener : eventListeners) {
            if (listener != null) {
                listener.onEvent(accessibilityEvent);
            }
        }

        logText(
                accessibilityEvent.getPackageName()+"\n"+AccessibilityNodeInfoTextConverter.convert(getRootInActiveWindow())
        );

        for (AdCloser closer:adClosers){


            AccessibilityNodeInfo root = getRootInActiveWindow();
            AccessibilityNodeInfo node = closer.findAd(root);
            if (node!=null){
                Log.d(TAG, String.valueOf(node.getText()));
            }
            root.recycle();
        }

        // 检查事件类型是否为文本变化
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            if (rootNode != null) {
                CharSequence text = rootNode.getText();
                for (TextChangeListener listener : textChangeListeners) {
                    if (listener != null) {
                        listener.onTextChangeEvent(text);
                    }
                }
                rootNode.recycle();
                 // 释放资源
            }
        }

    }


//    @Override
//    public void onAccessibilityEvent(AccessibilityEvent event) {
//        // 处理系统事件，例如点击、滑动等
//        for (EventListener listener : eventListeners) {
//            if (listener != null) {
//                listener.onEvent(event);
//            }
//        }
//
//        // 检查事件类型是否为文本变化
//        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
//            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
//            if (rootNode != null) {
//                CharSequence text = rootNode.getText();
//                for (TextChangeListener listener : textChangeListeners) {
//                    if (listener != null) {
//                        listener.onTextChangeEvent(text);
//                    }
//                }
//                rootNode.recycle(); // 释放资源
//            }
//        }
//    }

    @Override
    public void onInterrupt() {
        // 处理服务中断的逻辑
    }

    // 定义一般事件监听器接口
    public interface EventListener {
        void onEvent(AccessibilityEvent event);
    }

    // 定义文本变化事件监听器接口
    public interface TextChangeListener extends EventListener {
        void onTextChangeEvent(CharSequence text);
    }

    // 添加一般事件监听器方法
    public void addEventListener(EventListener listener) {
        if (listener != null) {
            eventListeners.add(listener);
        }
    }

    // 移除一般事件监听器方法
    public void removeEventListener(EventListener listener) {
        if (listener != null) {
            eventListeners.remove(listener);
        }
    }

    // 添加文本变化事件监听器方法
    public void addTextChangeListener(TextChangeListener listener) {
        if (listener != null) {
            textChangeListeners.add(listener);
        }
    }

    // 移除文本变化事件监听器方法
    public void removeTextChangeListener(TextChangeListener listener) {
        if (listener != null) {
            textChangeListeners.remove(listener);
        }
    }

    // 其他操作控制方法，例如按键、滚动等
    public void performCustomAction(int actionId) {
        switch (actionId) {
            case 1:
                // 自定义动作1
                break;
            case 2:
                // 自定义动作2
                break;
            // 其他自定义动作
        }
    }

    // 获取当前窗口的根节点信息

    public static String eventTypeToString(int eventType) {
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                return "TYPE_VIEW_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                return "TYPE_VIEW_FOCUSED";
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                return "TYPE_VIEW_HOVER_ENTER";
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                return "TYPE_VIEW_HOVER_EXIT";
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                return "TYPE_VIEW_LONG_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                return "TYPE_VIEW_SCROLLED";
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                return "TYPE_VIEW_SELECTED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                return "TYPE_VIEW_TEXT_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                return "TYPE_VIEW_TEXT_SELECTION_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY:
                return "TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY";
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                return "TYPE_WINDOW_CONTENT_CHANGED";
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                return "TYPE_WINDOW_STATE_CHANGED";
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                return "TYPE_WINDOWS_CHANGED";
            default:
                return "UNKNOWN_EVENT_TYPE";
        }
    }

}