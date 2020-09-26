package com.alec.mad.assignment1.state

abstract class ObservableState<O>(observers: Collection<O> = mutableSetOf()) {
    val observers: MutableSet<O> = observers.toMutableSet()

    protected inline fun notifyObservers(action: (O) -> Unit) =
        this.observers.toSet().forEach(action)
}