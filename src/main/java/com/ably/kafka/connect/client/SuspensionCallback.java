package com.ably.kafka.connect.client;

/*
Interface to be used to communicate connection suspensions
* */
public interface SuspensionCallback {
    void onSuspendedStateChange(boolean suspended);
}
