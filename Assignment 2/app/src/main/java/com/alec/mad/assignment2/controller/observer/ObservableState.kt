package com.alec.mad.assignment2.controller.observer

/**
 * The observer pattern; subclasses have an observable state and can call [notifyObservers] to
 * interact with their observers. And [notifyMe] to initially get notified with the current state.
 * Observers subscribe by adding themselves to the [observers] set.
 */
abstract class ObservableState<O>(val observers: MutableCollection<O> = mutableSetOf()) {

    /**
     * Call all functions in [O] on [them] with the current state of this class.
     */
    abstract fun notifyMe(them: O)

    /**
     * Perform [action] on all registered observers.
     */
    protected inline fun notifyObservers(action: (O) -> Unit) {
        // Operates on a copy of the set in case observers modify observer set
        this.observers.toSet().forEach(action)
    }
}