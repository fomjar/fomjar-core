package com.fomjar.dist;

import com.fomjar.lang.Async;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public abstract class AbstractDist implements Dist {

    private static final long ELECT_KEEP    = 1500L;
    private static final long ELECT_PERIOD  = 1000L;
    private Map<String, Election>   electionAll     = new ConcurrentHashMap<>();
    private Map<String, Election>   electionMy      = new ConcurrentHashMap<>();
    private Future<?>               electionTask    = null;

    @Override
    public void elect(String topic, Election election) {
        this.electionAll.put(topic, election);

        if (null == this.electionTask)
            this.startElectTask();
    }

    private void startElectTask() {
        this.electionTask = Async.loop(() -> {
            // 无选举项，直接清理退出
            if (this.electionAll.isEmpty()) {
                this.electionMy.clear();
                this.electionTask.cancel(true);
                this.electionTask = null;
                return;
            }

            this.electionAll.forEach((topic, election) -> {
                try {
                    // try elect
                    if (this.lock("elect-" + topic, 0, AbstractDist.ELECT_KEEP)) {
                        if (this.electionMy.containsKey(topic)) {
                            // 早已当选
                        } else {
                            // 新当选
                            this.electionMy.put(topic, election);
                            election.elected(topic);
                        }
                    } else {
                        if (this.electionMy.containsKey(topic)) {
                            // 落选
                            this.electionMy.remove(topic).lost(topic);
                        } else {
                            // 早已落选
                        }
                    }
                } catch (Exception e) { e.printStackTrace(); }
            });
        }, AbstractDist.ELECT_PERIOD);
    }

    @Override
    public boolean isElected(String topic) {
        return this.electionMy.containsKey(topic);
    }

    @Override
    public void abstain(String topic) {
        this.electionMy.remove(topic);
        this.electionAll.remove(topic);
    }
}
