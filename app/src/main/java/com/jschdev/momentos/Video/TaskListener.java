package com.jschdev.momentos.Video;

/**
 * Created by tcsadmin on 04/12/2015.
 */
public interface TaskListener {
    void onTaskStarted();

    void onTaskFinished(String result);
}