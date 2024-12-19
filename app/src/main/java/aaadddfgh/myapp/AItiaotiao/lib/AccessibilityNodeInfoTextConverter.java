package aaadddfgh.myapp.AItiaotiao.lib;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class AccessibilityNodeInfoTextConverter {

    public static String convert(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return "Invalid Node Info";
        }

        List<String> nodeList = new ArrayList<>();
        traverseNode(nodeInfo, nodeList, 0);

        StringBuilder textBuilder = new StringBuilder();
        for (String node : nodeList) {
            textBuilder.append(node).append("\n");
        }

        return textBuilder.toString();
    }

    public static String prependSpacesToLines(String input, int n) {
        StringBuilder sb = new StringBuilder();
        String[] lines = input.split("\n");

        for (String line : lines) {
            if (!line.trim().isEmpty()) { // 忽略空行
                for (int i = 0; i < n; i++) {
                    sb.append("  ");
                }
            }
            sb.append(line).append("\n");
        }

        return sb.toString();
    }

    private static void traverseNode(AccessibilityNodeInfo node, List<String> nodeList, int depth) {
        if (node == null) {
            return;
        }

        // Add the current node's information to the list with indentation
        StringBuilder builder = new StringBuilder();

        builder.append("Description: ").append(node.getText() != null ? node.getText().toString() : "None");
        builder.append("\n");
        builder.append("Type: ").append(node.getClassName());
        builder.append("\n");

        if (node.getContentDescription() != null) {
            builder.append("Additional Info: ").append(node.getContentDescription()).append("\n");
        }
        if (node.isClickable()) {
            builder.append("Can Click: Yes\n");
        } else {
            builder.append("Can Click: No\n");
        }
        if (node.isCheckable()) {
            builder.append("Can Check: ").append(node.isChecked() ? "Yes" : "No").append("\n");
        }
        if (node.isFocusable()) {
            builder.append("Can Focus: Yes\n");
        } else {
            builder.append("Can Focus: No\n");
        }



        nodeList.add(
                prependSpacesToLines(
                        builder.toString(),
                        depth
                )
        );

        // Recursively traverse child nodes
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo childNode = node.getChild(i);
            traverseNode(childNode, nodeList, depth + 1);

        }
    }

}