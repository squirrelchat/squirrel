/*
 * Copyright (c) 2020-present Bowser65 & vinceh121, All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package chat.squirrel.scheduling;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerManager implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(SchedulerManager.class);
    private LinkedList<ITask<?>> fifo = new LinkedList<ITask<?>>();
    private Thread executorThread;
    private long normalTime = 16;

    public SchedulerManager() {
        executorThread = new Thread(this, "squirrel-scheduler");
    }

    public void start() {
        executorThread.start();
    }

    public void shutdown() {
        fifo.clear();
        executorThread.interrupt();
    }

    public void addTask(ITask<?> task) {
        this.fifo.offer(task);
    }

    public void setTps(long tps) {
        this.normalTime = (long) (1 / (double) tps) * 1000L;
    }

    @Override
    public void run() {
        try {
            for (;;) {
                if (fifo.size() == 0) {
                    Thread.sleep(normalTime);
                    continue;
                }
                final long start = System.currentTimeMillis();
                scheduleIncremental();
                if (normalTime == -1)
                    continue;
                final long end = System.currentTimeMillis();
                final long duration = end - start;
                final long wait = duration - normalTime;

                if (duration > normalTime) {
                    LOG.warn("Task took (" + duration + "ms) longer than allowed time (" + normalTime + "ms)");
                }

                if (wait >= 0) {
                    Thread.sleep(wait);
                }

            }
        } catch (InterruptedException e) {
            return;
        }
    }

    private void scheduleIncremental() {
        final ITask<?> task = fifo.removeFirst();

        task.execute();
    }

}
