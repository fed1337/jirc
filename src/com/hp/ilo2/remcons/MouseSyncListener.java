package com.hp.ilo2.remcons;


import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

interface MouseSyncListener {
    void serverMove(int var1, int var2, int var3, int var4);

    void serverPress(int var1);

    void serverRelease(int var1);

    void serverClick(int var1, int var2);

    void sendMouse(MouseEvent var1);

    void sendMouseScroll(MouseWheelEvent var1);

    void requestScreenFocus(MouseEvent var1);

    void installKeyboardHook();

    void unInstallKeyboardHook();
}
