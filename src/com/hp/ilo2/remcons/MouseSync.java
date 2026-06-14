package com.hp.ilo2.remcons;


import java.awt.event.*;

@SuppressWarnings("deprecation")
public class MouseSync implements MouseListener, MouseMotionListener, MouseWheelListener, TimerListener {
    public static final int MOUSE_BUTTON_LEFT = 4;
    public static final int MOUSE_BUTTON_CENTER = 2;
    public static final int MOUSE_BUTTON_RIGHT = 1;
    private static final int CMD_START = 0;
    private static final int CMD_STOP = 1;
    private static final int CMD_SYNC = 2;
    private static final int CMD_SERVER_MOVE = 3;
    private static final int CMD_SERVER_SCREEN = 4;
    private static final int CMD_SERVER_DISABLE = 5;
    private static final int CMD_TIMEOUT = 6;
    private static final int CMD_CLICK = 7;
    private static final int CMD_ENTER = 8;
    private static final int CMD_EXIT = 9;
    private static final int CMD_PRESS = 10;
    private static final int CMD_RELEASE = 11;
    private static final int CMD_DRAG = 12;
    private static final int CMD_MOVE = 13;
    private static final int CMD_ALIGN = 14;
    private static final int STATE_INIT = 0;
    private static final int STATE_SYNC = 1;
    private static final int STATE_ENABLE = 2;
    private static final int STATE_DISABLE = 3;
    private static final int SYNC_SUCCESS_COUNT = 2;
    private static final int SYNC_FAIL_COUNT = 4;
    private static final int TIMEOUT_DELAY = 5;
    private static final int TIMEOUT_MOVE = 200;
    private static final int TIMEOUT_SYNC = 2000;
    private final Object mutex;
    private int state;
    private MouseSyncListener listener = null;
    private int server_w = 0;
    private int server_h = 0;
    private int server_x = 0;
    private int server_y = 0;
    private int client_x = 0;
    private int client_y = 0;
    private int client_dx = 0;
    private int client_dy = 0;
    private int[] send_dx = null;
    private int[] send_dy = null;
    private int[] recv_dx = null;
    private int[] recv_dy = null;
    private int send_dx_index = 0;
    private int send_dy_index = 0;
    private int send_dx_count = 0;
    private int send_dy_count = 0;
    private int send_dx_success = 0;
    private int send_dy_success = 0;
    private boolean sync_successful = false;
    private Timer timer = null;
    private int pressed_button = 0;
    private boolean dragging = false;
    private boolean debug_msg = false;

    public MouseSync(final Object var1) {
        super();
        this.mutex = var1;
        this.state = 0;
        this.state_machine(0, 0, 0);
    }

    public void setListener(final MouseSyncListener var1) {
        this.listener = var1;
    }

    public void enableDebug() {
        this.debug_msg = true;
    }

    public void disableDebug() {
        this.debug_msg = false;
    }

    public void restart() {
        this.go_state(0);
    }

    public void align() {
        this.state_machine(14, 0, 0);
    }

    public void sync() {
        this.state_machine(2, 0, 0);
    }

    public void serverMoved(final int var1, final int var2, final int var3, final int var4) {
        this.state_machine(3, var1, var2);
    }

    public void serverScreen(final int var1, final int var2) {
        this.state_machine(4, var1, var2);
    }

    public void serverDisabled() {
        this.state_machine(5, 0, 0);
    }

    public void timeout(final Object var1) {
        this.state_machine(6, 0, 0);
    }

    public void mouseClicked(final MouseEvent var1) {
        this.listener.requestScreenFocus(var1);
        this.listener.sendMouse(var1);
    }

    public void mouseEntered(final MouseEvent var1) {
        this.listener.installKeyboardHook();
    }

    public void mouseExited(final MouseEvent var1) {
        this.listener.unInstallKeyboardHook();
    }

    public void mousePressed(final MouseEvent var1) {
        this.listener.sendMouse(var1);
    }

    public void mouseReleased(final MouseEvent var1) {
        this.listener.sendMouse(var1);
    }

    public void mouseDragged(final MouseEvent var1) {
        this.listener.sendMouse(var1);
    }

    public void mouseMoved(final MouseEvent var1) {
        this.listener.sendMouse(var1);
    }

    private static void move_delay() {
        try {
            Thread.sleep(5L);
        } catch (final InterruptedException var2) {
        }

    }

    private void sync_default() {
        final int[] var2 = {1, 4, 6, 8, 12, 16, 32, 64};
        this.send_dx = new int[var2.length];
        this.send_dy = new int[var2.length];
        this.recv_dx = new int[var2.length];
        this.recv_dy = new int[var2.length];

        for (int var1 = 0; var1 < var2.length; ++var1) {
            this.send_dx[var1] = var2[var1];
            this.send_dy[var1] = var2[var1];
            this.recv_dx[var1] = var2[var1];
            this.recv_dy[var1] = var2[var1];
        }

        this.send_dx_index = 0;
        this.send_dy_index = 0;
        this.send_dx_count = 0;
        this.send_dy_count = 0;
        this.send_dx_success = 0;
        this.send_dy_success = 0;
        this.sync_successful = false;
    }

    private void sync_continue() {
        byte var1 = (byte) 1;
        byte var2 = (byte) 1;
        int var3 = 0;
        int var4 = 0;
        if (this.server_x > this.server_w / 2) {
            var1 = (byte) -1;
        }

        if (this.server_y < this.server_h / 2) {
            var2 = (byte) -1;
        }

        if (0 <= this.send_dx_index) {
            var3 = (int) var1 * this.send_dx[this.send_dx_index];
        }

        if (0 <= this.send_dy_index) {
            var4 = (int) var2 * this.send_dy[this.send_dy_index];
        }

        this.listener.serverMove(var3, var4, this.client_x, this.client_y);
        this.timer.start();
    }

    private void sync_update(final int var1, final int var2) {
        this.timer.pause();
        int var3 = var1 - this.server_x;
        int var4 = this.server_y - var2;
        this.server_x = var1;
        this.server_y = var2;
        if (0 > var3) {
            var3 = -var3;
        }

        if (0 > var4) {
            var4 = -var4;
        }

        if (0 <= this.send_dx_index) {
            if (this.recv_dx[this.send_dx_index] == var3) {
                ++this.send_dx_success;
            }

            this.recv_dx[this.send_dx_index] = var3;
            ++this.send_dx_count;
            if (2 <= this.send_dx_success) {
                --this.send_dx_index;
                this.send_dx_success = 0;
                this.send_dx_count = 0;
            } else if (4 <= this.send_dx_count) {
                if (this.debug_msg) {
                    System.out.println("no x sync:" + this.send_dx[this.send_dx_index]);
                }

                this.go_state(2);
                return;
            }
        }

        if (0 <= this.send_dy_index) {
            if (this.recv_dy[this.send_dy_index] == var4) {
                ++this.send_dy_success;
            }

            this.recv_dy[this.send_dy_index] = var4;
            ++this.send_dy_count;
            if (2 <= this.send_dy_success) {
                --this.send_dy_index;
                this.send_dy_success = 0;
                this.send_dy_count = 0;
            } else if (4 <= this.send_dy_count) {
                if (this.debug_msg) {
                    System.out.println("no y sync:" + this.send_dy[this.send_dy_index]);
                }

                this.go_state(2);
                return;
            }
        }

        if (0 > this.send_dx_index && 0 > this.send_dy_index) {
            for (int var5 = this.send_dx.length - 1; 0 <= var5; --var5) {
                if (0 == this.recv_dx[var5] || 0 == this.recv_dy[var5]) {
                    if (this.debug_msg) {
                    }

                    this.go_state(2);
                    return;
                }

                if (0 != var5 && (this.recv_dx[var5] < this.recv_dx[var5 - 1] || this.recv_dy[var5] < this.recv_dy[var5 - 1])) {
                    if (this.debug_msg) {
                    }

                    this.go_state(2);
                    return;
                }
            }

            this.sync_successful = true;
            this.send_dx_index = 0;
            this.send_dy_index = 0;
            this.go_state(2);
        } else {
            this.sync_continue();
        }

    }

    private void init_vars() {
        this.server_w = 640;
        this.server_h = 480;
        this.server_x = 0;
        this.server_y = 0;
        this.client_x = 0;
        this.client_y = 0;
        this.client_dx = 0;
        this.client_dy = 0;
        this.pressed_button = 0;
        this.dragging = false;
        this.sync_default();
    }

    private void move_server(final boolean var1, final boolean var2) {
        int var8 = 0;
        int var9 = 0;
        int var10 = 0;
        int var11 = 0;
        this.timer.pause();
        int var4 = this.client_dx;
        int var5 = this.client_dy;
        final byte var6;
        if (0 <= var4) {
            var6 = (byte) 1;
        } else {
            var6 = (byte) -1;
            var4 = -var4;
        }

        final byte var7;
        if (0 <= var5) {
            var7 = (byte) 1;
        } else {
            var7 = (byte) -1;
            var5 = -var5;
        }

        do {
            int var3;
            if (0 == var4) {
                var8 = 0;
            } else {
                for (var3 = this.send_dx.length - 1; var3 >= this.send_dx_index; --var3) {
                    if (this.recv_dx[var3] <= var4) {
                        var8 = (int) var6 * this.send_dx[var3];
                        var10 += this.recv_dx[var3];
                        var4 -= this.recv_dx[var3];
                        break;
                    }
                }

                if (var3 < this.send_dx_index) {
                    var8 = 0;
                    var10 += var4;
                    var4 = 0;
                }
            }

            if (0 == var5) {
                var9 = 0;
            } else {
                for (var3 = this.send_dy.length - 1; var3 >= this.send_dy_index; --var3) {
                    if (this.recv_dy[var3] <= var5) {
                        var9 = (int) var7 * this.send_dy[var3];
                        var11 += this.recv_dy[var3];
                        var5 -= this.recv_dy[var3];
                        break;
                    }
                }

                if (var3 < this.send_dy_index) {
                    var9 = 0;
                    var11 += var5;
                    var5 = 0;
                }
            }

            if (0 != var8 || 0 != var9) {
                this.listener.serverMove(var8, var9, this.client_x, this.client_y);
            }
        } while (var1 && (0 != var4 || 0 != var5));

        this.client_dx -= (int) var6 * var10;
        this.client_dy -= (int) var7 * var11;
        if (!var2) {
            this.server_x += (int) var6 * var10;
            this.server_y -= (int) var7 * var11;
            if (this.debug_msg) {
            }
        }

        if (0 != this.client_dx || 0 != this.client_dy) {
            this.timer.start();
        }

    }

    private void go_state(final int var1) {
        synchronized (this.mutex) {
            this.state_machine(1, 0, 0);
            this.state = var1;
            this.state_machine(0, 0, 0);
        }
    }

    private void state_machine(final int var1, final int var3, final int var4) {
        synchronized (this.mutex) {
            switch (this.state) {
                case 0:
                    this.state_init(var1, var3, var4);
                    break;
                case 1:
                    this.state_sync(var1, var3, var4);
                    break;
                case 2:
                    this.state_enable(var1, var3, var4);
                    break;
                case 3:
                    this.state_disable(var1, var3, var4);
            }

        }
    }

    private void state_init(final int var1, final int var3, final int var4) {
        switch (var1) {
            case 0:
                this.init_vars();
                this.go_state(3);
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            default:
        }
    }

    private void state_sync(final int var1, final int var3, final int var4) {
        switch (var1) {
            case 0:
                this.timer = new Timer(2000, false, this.mutex);
                this.timer.setListener(this, null);
                this.sync_default();
                this.send_dx_index = this.send_dx.length - 1;
                this.send_dy_index = this.send_dy.length - 1;
                this.sync_continue();
                break;
            case 1:
                this.timer.stop();
                this.timer = null;
                if (!this.sync_successful) {
                    if (this.debug_msg) {
                        System.out.println("fail");
                    }

                    this.sync_default();
                } else if (this.debug_msg) {
                }

                if (this.debug_msg) {
                    int var5;
                    for (var5 = 0; var5 < this.send_dx.length; ++var5) {
                    }

                    for (var5 = 0; var5 < this.send_dx.length; ++var5) {
                    }
                }
                break;
            case 2:
                this.go_state(1);
                break;
            case 3:
                if (2000 >= var3 && 2000 >= var4) {
                    this.sync_update(var3, var4);
                } else {
                    this.go_state(3);
                }
                break;
            case 4:
                this.server_w = var3;
                this.server_h = var4;
                break;
            case 5:
                this.go_state(3);
                break;
            case 6:
                this.go_state(2);
            case 7:
            case 10:
            case 11:
            case 14:
            default:
                break;
            case 8:
            case 9:
            case 12:
            case 13:
                this.client_x = ((MouseEvent) null).getX();
                this.client_y = ((MouseEvent) null).getY();
        }

    }

    private void state_enable(final int var1, final int var3, final int var4) {
        switch (var1) {
            case 0:
                if (this.debug_msg) {
                }

                this.timer = new Timer(200, false, this.mutex);
                this.timer.setListener(this, null);
                break;
            case 1:
                this.timer.stop();
                this.timer = null;
                break;
            case 2:
                this.go_state(1);
                break;
            case 3:
                if (this.debug_msg) {
                }

                if (2000 >= var3 && 2000 >= var4) {
                    this.server_x = var3;
                    this.server_y = var4;
                } else {
                    this.go_state(3);
                }
                break;
            case 4:
                this.server_w = var3;
                this.server_h = var4;
                break;
            case 5:
                this.go_state(3);
                break;
            case 6:
                this.move_server(true, true);
                break;
            case 7:
                if (!this.dragging) {
                    if (0 != (((MouseEvent) null).getModifiers() & 16)) {
                        this.listener.serverClick(4, 1);
                    } else if (0 != (((MouseEvent) null).getModifiers() & 8)) {
                        this.listener.serverClick(2, 1);
                    } else if (0 != (((MouseEvent) null).getModifiers() & 4)) {
                        this.listener.serverClick(1, 1);
                    }
                }
                break;
            case 8:
            case 9:
                this.client_x = ((MouseEvent) null).getX();
                this.client_y = ((MouseEvent) null).getY();
                if (0 > this.client_x) {
                    this.client_x = 0;
                }

                if (this.client_x > this.server_w) {
                    this.client_x = this.server_w;
                }

                if (0 > this.client_y) {
                    this.client_y = 0;
                }

                if (this.client_y > this.server_h) {
                    this.client_y = this.server_h;
                }

                if (this.debug_msg) {
                }

                if (1 != this.pressed_button && 0 == (((MouseEvent) null).getModifiers() & 2)) {
                    this.align();
                }
                break;
            case 10:
                if (0 == this.pressed_button) {
                    if (0 != (((MouseEvent) null).getModifiers() & 4)) {
                        this.pressed_button = 1;
                    } else if (0 != (((MouseEvent) null).getModifiers() & 8)) {
                        this.pressed_button = 2;
                    } else {
                        this.pressed_button = 4;
                    }

                    this.dragging = false;
                }
                break;
            case 11:
                if (-4 == this.pressed_button) {
                    this.listener.serverRelease(4);
                } else if (-2 == this.pressed_button) {
                    this.listener.serverRelease(2);
                } else if (-1 == this.pressed_button) {
                    this.listener.serverRelease(1);
                }

                this.pressed_button = 0;
                break;
            case 12:
                if (1 != this.pressed_button) {
                    if (0 < this.pressed_button) {
                        this.pressed_button = -this.pressed_button;
                        this.listener.serverPress(this.pressed_button);
                    }

                    this.client_dx += ((MouseEvent) null).getX() - this.client_x;
                    this.client_dy += this.client_y - ((MouseEvent) null).getY();
                    this.move_server(false, true);
                }

                this.client_x = ((MouseEvent) null).getX();
                this.client_y = ((MouseEvent) null).getY();
                if (this.debug_msg) {
                }

                this.dragging = true;
                break;
            case 13:
                if (0 == (((MouseEvent) null).getModifiers() & 2)) {
                    this.client_dx += ((MouseEvent) null).getX() - this.client_x;
                    this.client_dy += this.client_y - ((MouseEvent) null).getY();
                    this.move_server(false, true);
                }

                this.client_x = ((MouseEvent) null).getX();
                this.client_y = ((MouseEvent) null).getY();
                if (this.debug_msg) {
                }
                break;
            case 14:
                this.client_dx = this.client_x - this.server_x;
                this.client_dy = this.server_y - this.client_y;
                this.move_server(true, true);
        }

    }

    private void state_disable(final int var1, final int var3, final int var4) {
        switch (var1) {
            case 0:
                if (this.debug_msg) {
                }

                this.timer = new Timer(200, false, this.mutex);
                this.timer.setListener(this, null);
                break;
            case 1:
                this.timer.stop();
                this.timer = null;
                break;
            case 2:
                this.sync_default();
                break;
            case 3:
                if (this.debug_msg) {
                }

                if (2000 > var3 && 2000 > var4) {
                    this.server_x = var3;
                    this.server_y = var4;
                    this.go_state(2);
                }
                break;
            case 4:
                this.server_w = var3;
                this.server_h = var4;
            case 5:
            default:
                break;
            case 6:
                this.move_server(true, false);
                break;
            case 7:
                if (!this.dragging) {
                    if (0 != (((MouseEvent) null).getModifiers() & 16)) {
                        this.listener.serverClick(4, 1);
                    } else if (0 != (((MouseEvent) null).getModifiers() & 8)) {
                        this.listener.serverClick(2, 1);
                    } else if (0 != (((MouseEvent) null).getModifiers() & 4)) {
                        this.listener.serverClick(1, 1);
                    }
                }
                break;
            case 8:
            case 9:
                this.client_x = ((MouseEvent) null).getX();
                this.client_y = ((MouseEvent) null).getY();
                if (0 > this.client_x) {
                    this.client_x = 0;
                }

                if (this.client_x > this.server_w) {
                    this.client_x = this.server_w;
                }

                if (0 > this.client_y) {
                    this.client_y = 0;
                }

                if (this.client_y > this.server_h) {
                    this.client_y = this.server_h;
                }

                if (this.debug_msg) {
                }

                if (1 != this.pressed_button && 0 == (((MouseEvent) null).getModifiers() & 2)) {
                    this.align();
                }
                break;
            case 10:
                if (0 == this.pressed_button) {
                    if (0 != (((MouseEvent) null).getModifiers() & 4)) {
                        this.pressed_button = 1;
                    } else if (0 != (((MouseEvent) null).getModifiers() & 8)) {
                        this.pressed_button = 2;
                    } else {
                        this.pressed_button = 4;
                    }

                    this.dragging = false;
                }
                break;
            case 11:
                if (-4 == this.pressed_button) {
                    this.listener.serverRelease(4);
                } else if (-2 == this.pressed_button) {
                    this.listener.serverRelease(2);
                } else if (-1 == this.pressed_button) {
                    this.listener.serverRelease(1);
                }

                this.pressed_button = 0;
                break;
            case 12:
                if (1 == this.pressed_button) {
                    this.server_x = ((MouseEvent) null).getX();
                    this.server_y = ((MouseEvent) null).getY();
                } else {
                    if (0 < this.pressed_button) {
                        this.pressed_button = -this.pressed_button;
                        this.listener.serverPress(this.pressed_button);
                    }

                    this.client_dx += ((MouseEvent) null).getX() - this.client_x;
                    this.client_dy += this.client_y - ((MouseEvent) null).getY();
                    this.move_server(false, false);
                }

                this.client_x = ((MouseEvent) null).getX();
                this.client_y = ((MouseEvent) null).getY();
                if (this.debug_msg) {
                }

                this.dragging = true;
                break;
            case 13:
                if (0 == (((MouseEvent) null).getModifiers() & 2)) {
                    this.client_dx += ((MouseEvent) null).getX() - this.client_x;
                    this.client_dy += this.client_y - ((MouseEvent) null).getY();
                    this.move_server(false, false);
                } else {
                    this.server_x = ((MouseEvent) null).getX();
                    this.server_y = ((MouseEvent) null).getY();
                }

                this.client_x = ((MouseEvent) null).getX();
                this.client_y = ((MouseEvent) null).getY();
                if (this.debug_msg) {
                }
                break;
            case 14:
                this.client_dx = this.client_x - this.server_x;
                this.client_dy = this.server_y - this.client_y;
                this.move_server(true, false);
        }

    }

    public void mouseWheelMoved(final MouseWheelEvent var1) {
    }
}
