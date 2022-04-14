package com.tictactoe

import java.util.TreeMap

class PriorityMap<K, V>(override val size:Int): TreeMap<K, V>() {
    private val priority = TreeMap<K, Int>()

    override operator fun get(key: K): V? = super.get(key)

    operator fun set(key: K, value: V): V {
        if(super.containsKey(key)){
            super.put(key, value)
            priority[key] = priority[key]!! + 1
        } else if(super.size >= this.size) {
            val min = priority.keys.minOf { priority[it]!! }
            priority.keys.filter { priority[it]==min }.forEach {
                remove(it)
                priority.remove(it)
            }
            super.put(key, value)
            priority[key] =  1
        } else {
            super.put(key, value)
            priority[key] =  1
        }
        return value
    }
}