package com.arthur.marscalendar.util

import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper


fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)
}

fun RecyclerView.attachSnapHelperWithListener(
    snapHelper: SnapHelper,
    behavior: SnapOnScrollListener.Behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
    onSnapPositionChangeListener: OnSnapPositionChangeListener
) {
    snapHelper.attachToRecyclerView(this)
    val snapOnScrollListener = SnapOnScrollListener(snapHelper, behavior, onSnapPositionChangeListener)
    addOnScrollListener(snapOnScrollListener)
}

fun RecyclerView.scrollToLinearSnapPosition(selectedPosition: Int, snapHelper: LinearSnapHelper) {
    scrollToPosition(selectedPosition)
    post {
        val view = layoutManager?.findViewByPosition(selectedPosition)
        view.let {
            val snapDistance = snapHelper.calculateDistanceToFinalSnap(layoutManager!!, view!!)
            if (snapDistance?.get(0) ?: 0 != 0 || snapDistance?.get(1) ?: 0 != 0) {
                scrollBy(snapDistance!![0], snapDistance[1])
            }
        }
    }
}