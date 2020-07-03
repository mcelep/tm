package com.example.tm
import java.util.*
import kotlin.collections.ArrayList

class PriorityTaskManager : AbstractTaskManager() {
    override fun add(process: Process): Boolean {
        synchronized(tasks) {
            if (tasks.size >= PROCESS_LIMIT) {
                val ordered: List<Process> = createOrderedList()
                for (p: Process in ordered) {
                    if (process.priority.ordinal > p.priority.ordinal) {
                        tasks.remove(p)
                        saveProcess(process)
                        return true
                    }
                }
                return false
            } else {
                saveProcess(process)
                return true
            }
        }
    }

    private fun createOrderedList(): List<Process> {
        return ArrayList<Process>(tasks).sortedWith(Comparator { t, t2 ->
            val prioComparison = t.priority.ordinal - t2.priority.ordinal
            if (prioComparison != 0) {
                return@Comparator prioComparison
            } else {
                return@Comparator t.creationDate.compareTo(t2.creationDate)
            }
        })
    }

}