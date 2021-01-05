package com.hien.base.log;

import java.time.Instant;
import java.util.Map;

import com.hien.base.JsonSerializer;

public class LogObj {

    private Metric metric;
    private Debug debug;
    public String status;

    public LogObj() {
        this.status = "success";
        this.debug = new Debug();
        this.metric = new Metric();
        this.metric.start("process");

        this.putGlobalMetric("startedAt", Instant.now().toEpochMilli());
    }

    public LogObj(String name) {
        this();
        this.putGlobalMetric("logName", name);
    }

    public void logName(String logName) {
        this.putGlobalMetric("logName", logName);
    }

    public void debug(String desc) {
        this.debug.debug(desc);
    }

    public void debug(String key, String val) {
        if (key.contains("%s")) {
            this.debug(String.format(key, val));
        } else {
            this.debug.debug(key, val);
        }
    }

    public void error(Throwable tr) {
        this.debug.error(tr);
        this.status = "failure";
    }

    public void putGlobalMetric(String key, Object value) {
        if (value != null) {
            this.metric.putGlobalMetric(key, value);
        }
    }

    public void putRequestHeader(Object value) {
        if (value != null) {
            this.metric.putInputMetric("header", JsonSerializer.prettyPrintObject2Json(value));
        }
    }

    public void putRequestBody(Object value) {
        if (value != null) {
            this.metric.putInputMetric("body", JsonSerializer.prettyPrintObject2Json(value));
        }
    }

    public void putInputMetric(String key, Object value) {
        if (value != null) {
            this.metric.putInputMetric(key, value);
        }
    }

    public void putResponseBody(Object value) {
        if (value != null) {
            this.metric.putOutputMetric("body", JsonSerializer.prettyPrintObject2Json(value));
        }
    }

    public void putOutputMetric(String key, Object value) {
        if (value != null) {
            this.metric.putOutputMetric(key, value);
        }
    }

    public String getDebug() {
        return this.debug.toString();
    }

    public Map<String, Object> getMetric() {
        return this.metric.getRepresentedObject();
    }
}
