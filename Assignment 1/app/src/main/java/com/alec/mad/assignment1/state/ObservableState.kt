package com.alec.mad.assignment1.state

/**
 * The observer pattern; subclasses have an observable state and can call [notifyObservers] to
 * interact with their observers. Observers subscribe by adding themselves to the [observers] set.
 */
abstract class ObservableState<O>(observers: Collection<O> = mutableSetOf()) {
    val observers: MutableSet<O> = observers.toMutableSet()

    protected inline fun notifyObservers(action: (O) -> Unit) =
        this.observers.toSet().forEach(action) // Operates on a copy of the set for thread safety
}