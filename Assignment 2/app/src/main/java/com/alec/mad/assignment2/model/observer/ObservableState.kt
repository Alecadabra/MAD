package com.alec.mad.assignment2.model.observer

import java.util.*

/**
 * The observer pattern; subclasses have an observable state and can call [notifyObservers] to
 * interact with their observers. Observers subscribe by adding themselves to the [observers] set.
 */
abstract class ObservableState<O>(val observers: MutableCollection<O> = mutableSetOf()) {

    abstract fun notifyMe(them: O)

    protected inline fun notifyObservers(action: (O) -> Unit) {
        // Operates on a copy of the set in case observers modify observer set
        this.observers.toSet().forEach(action)
    }
}