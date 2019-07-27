package com.viaviapp.allinonevideosapp;

import android.transition.Transition;
import android.transition.Transition.TransitionListener;
import android.view.ViewGroup;

public class SimpleTransitionListener implements TransitionListener {
    /**
     * Notification about the start of the transition.
     *
     * @param transition The started transition.
     */
    @Override
    public void onTransitionStart(Transition transition) {

    }

    /**
     * Notification about the end of the transition. Canceled transitions
     * will always notify listeners of both the cancellation and end
     * events. That is, {@link #onTransitionEnd(Transition)} is always called,
     * regardless of whether the transition was canceled or played
     * through to completion.
     *
     * @param transition The transition which reached its end.
     */
    @Override
    public void onTransitionEnd(Transition transition) {

    }

    /**
     * Notification about the cancellation of the transition.
     * Note that cancel may be called by a parent {@link TransitionSet} on
     * a child transition which has not yet started. This allows the child
     * transition to restore state on target objects which was set at
     * {@link #createAnimator(ViewGroup, TransitionValues, TransitionValues)
     * createAnimator()} time.
     *
     * @param transition The transition which was canceled.
     */
    @Override
    public void onTransitionCancel(Transition transition) {

    }

    /**
     * Notification when a transition is paused.
     * Note that createAnimator() may be called by a parent {@link TransitionSet} on
     * a child transition which has not yet started. This allows the child
     * transition to restore state on target objects which was set at
     * {@link #createAnimator(ViewGroup, TransitionValues, TransitionValues)
     * createAnimator()} time.
     *
     * @param transition The transition which was paused.
     */
    @Override
    public void onTransitionPause(Transition transition) {

    }

    /**
     * Notification when a transition is resumed.
     * Note that resume() may be called by a parent {@link TransitionSet} on
     * a child transition which has not yet started. This allows the child
     * transition to restore state which may have changed in an earlier call
     * to {@link #onTransitionPause(Transition)}.
     *
     * @param transition The transition which was resumed.
     */
    @Override
    public void onTransitionResume(Transition transition) {

    }
}
