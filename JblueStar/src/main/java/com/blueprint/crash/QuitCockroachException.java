package com.blueprint.crash;

/**
 * Created by wanjian on 2017/2/15.
 * https://github.com/android-notes/Cockroach
 */

final class QuitCockroachException extends RuntimeException {
    public QuitCockroachException(String message) {
        super(message);
    }
}
