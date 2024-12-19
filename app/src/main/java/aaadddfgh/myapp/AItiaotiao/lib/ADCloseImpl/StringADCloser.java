package aaadddfgh.myapp.AItiaotiao.lib.ADCloseImpl;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import aaadddfgh.myapp.AItiaotiao.lib.AdCloser;

public class StringADCloser implements AdCloser {

    public static List<AccessibilityNodeInfo> findNodes(AccessibilityNodeInfo node) {
        if (node == null) {
            return Collections.emptyList();
        }

        List<AccessibilityNodeInfo> result = new ArrayList<>();
        if (AdRule(node.getText())) {
            result.add(node);
        }

        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo childNode = node.getChild(i);
            result.addAll(findNodes(childNode));

        }

        return result;
    }

    private static boolean AdRule(CharSequence s){
        return s!=null && s.toString().contains("无障碍");
    }

    @Override
    public AccessibilityNodeInfo findAd(AccessibilityNodeInfo node) {
        if (node == null) {
            return null;
        }

        List<AccessibilityNodeInfo> l = findNodes(node);
        return l.size()>0?l.get(0):null;
    }


}
