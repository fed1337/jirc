package org.remcons;


import java.util.Date;

class Timer implements Runnable {
    private static final int STATE_INIT = 0;
    private static final int STATE_RUNNING = 1;
    private static final int STATE_PAUSED = 2;
    private static final int STATE_STOPPED = 3;
    private static final int POLL_PERIOD = 50;
    private final int timeout_max;
    private final boolean one_shot;
    private final Object mutex;
    private int state = 0;
    private int timeout_count;
    private long start_time_millis;
    private long stop_time_millis;
    private Date date = new Date();
    private TimerListener callback;
    private Object callback_info;

    public Timer(int var1, boolean var2, Object var3) {
        this.timeout_max = var1;
        this.one_shot = var2;
        this.mutex = var3;
    }

    public void setListener(TimerListener var1, Object var2) {
        synchronized (this.mutex) {
            this.callback = var1;
            this.callback_info = var2;
        }
    }

    public void start() {
        synchronized (this.mutex) {
            switch (this.state) {
                case 0:
                    this.state = 1;
                    this.timeout_count = 0;
                    (new Thread(this)).start();
                    break;
                case 1:
                    this.timeout_count = 0;
                    break;
                case 2:
                    this.timeout_count = 0;
                    this.state = 1;
                    break;
                case 3:
                    this.timeout_count = 0;
                    this.state = 1;
            }

        }
    }

    public void stop() {
        synchronized (this.mutex) {
            if (this.state != 0) {
                this.state = 3;
            }

        }
    }

    public void pause() {
        synchronized (this.mutex) {
            if (this.state == 1) {
                this.state = 2;
            }

        }
    }

    public void cont() {
        synchronized (this.mutex) {
            if (this.state == 2) {
                this.state = 1;
            }

        }
    }

    public void run() {
        do {
            this.date = new Date();
            this.start_time_millis = this.date.getTime();

            try {
                Thread.sleep(50L);
            } catch (InterruptedException var2) {
            }

            this.date = new Date();
            this.stop_time_millis = this.date.getTime();
        } while (this.process_state());

    }

    private boolean process_state() {
        boolean var1 = true;
        synchronized (this.mutex) {
            switch (this.state) {
                case 0:
                case 2:
                default:
                    break;
                case 1:
                    if (this.stop_time_millis > this.start_time_millis) {
                        this.timeout_count = (int) ((long) this.timeout_count + (this.stop_time_millis - this.start_time_millis));
                    } else {
                        this.timeout_count += 50;
                    }

                    if (this.timeout_count >= this.timeout_max) {
                        if (this.callback != null) {
                            this.callback.timeout(this.callback_info);
                        }

                        if (this.one_shot) {
                            this.state = 0;
                            var1 = false;
                        } else {
                            this.timeout_count = 0;
                        }
                    }
                    break;
                case 3:
                    this.state = 0;
                    var1 = false;
            }

            return var1;
        }
    }
}
