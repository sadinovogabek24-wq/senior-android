
package com.senior.assistant

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.content.Intent
import android.os.Bundle

class SeniorAccessibilityService : AccessibilityService() {

    companion object {
        var instance: SeniorAccessibilityService? = null
            private set

        fun back() {
            instance?.performGlobalAction(GLOBAL_ACTION_BACK)
        }

        fun home() {
            instance?.performGlobalAction(GLOBAL_ACTION_HOME)
        }

        fun clickText(text: String): Boolean {
            val service = instance ?: return false
            val root = service.rootInActiveWindow ?: return false
            return service.clickNode(root, text)
        }

        fun enterText(text: String): Boolean {
            val service = instance ?: return false
            val root = service.rootInActiveWindow ?: return false
            val node = service.findEditableNode(root) ?: return false

            val arguments = Bundle().apply {
                putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    text
                )
            }

            return node.performAction(
                AccessibilityNodeInfo.ACTION_SET_TEXT,
                arguments
            )
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Ekrandagi o'zgarishlarni kuzatish
    }

    override fun onInterrupt() {}

    private fun clickNode(
        node: AccessibilityNodeInfo,
        text: String
    ): Boolean {
        val label = (
            node.text?.toString() + " " +
            node.contentDescription?.toString()
        ).lowercase()

        if (label.contains(text.lowercase()) &&
            node.isClickable && node.isEnabled
        ) {
            return node.performAction(
                AccessibilityNodeInfo.ACTION_CLICK
            )
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            if (clickNode(child, text)) return true
        }

        return false
    }

    private fun findEditableNode(
        node: AccessibilityNodeInfo
    ): AccessibilityNodeInfo? {
        if (node.isEditable && node.isEnabled) return node

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val found = findEditableNode(child)
            if (found != null) return found
        }

        return null
    }

    override fun onDestroy() {
        if (instance === this) instance = null
        super.onDestroy()
    }
}
