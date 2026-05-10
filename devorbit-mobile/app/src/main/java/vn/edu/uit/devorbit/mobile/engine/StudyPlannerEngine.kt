package vn.edu.uit.devorbit.mobile.engine

import vn.edu.uit.devorbit.mobile.model.domain.LearningTask
import vn.edu.uit.devorbit.mobile.model.domain.StudyItem
import vn.edu.uit.devorbit.mobile.model.domain.StudyPhase
import vn.edu.uit.devorbit.mobile.model.domain.StudyPlan

object StudyPlannerEngine {

    /**
     * Creates a phased (weekly) study plan from tasks.
     *
     * @param tasks                 Learning tasks with deadlines and estimates
     * @param deadlines             Override map: taskId -> deadline epoch millis
     * @param availableHoursPerDay  How many hours the user can study each day
     * @param existingTasks         Pre-existing tasks to merge (avoids duplicates by id)
     */
    fun generatePlan(
        tasks: List<LearningTask>,
        deadlines: Map<Long, Long> = emptyMap(),
        availableHoursPerDay: Double,
        existingTasks: List<LearningTask> = emptyList()
    ): StudyPlan {
        val merged = (tasks + existingTasks)
            .distinctBy { it.id }
            .filter { !it.completed }
            .sortedWith(
                compareBy<LearningTask> {
                    deadlines[it.id] ?: it.deadline ?: Long.MAX_VALUE
                }.thenByDescending { it.priority }
            )

        val hoursPerWeek = availableHoursPerDay * 7.0
        val phases = mutableListOf<StudyPhase>()
        var weekIndex = 0

        val remaining = merged.toMutableList()
        while (remaining.isNotEmpty()) {
            val phaseItems = mutableListOf<LearningTask>()
            var weekHours = 0.0

            val iter = remaining.iterator()
            while (iter.hasNext()) {
                val task = iter.next()
                val taskHours = task.estimatedMinutes / 60.0
                if (weekHours + taskHours <= hoursPerWeek) {
                    phaseItems.add(task)
                    weekHours += taskHours
                    iter.remove()
                }
            }

            // If nothing fit (single task > weekly limit), force-add one
            if (phaseItems.isEmpty() && remaining.isNotEmpty()) {
                phaseItems.add(remaining.removeAt(0))
            }

            if (phaseItems.isNotEmpty()) {
                phases.add(
                    StudyPhase(
                        title = "Tuần ${weekIndex + 1}",
                        items = phaseItems.map { task ->
                            StudyItem(
                                id = task.id.toString(),
                                title = task.title,
                                subjectId = task.subjectId ?: 0L,
                                estimatedHours = task.estimatedMinutes / 60.0,
                                difficulty = when {
                                    task.priority >= 8 -> "hard"
                                    task.priority >= 4 -> "medium"
                                    else -> "easy"
                                },
                                completed = false,
                                prerequisiteIds = emptyList()
                            )
                        },
                        startDay = weekIndex * 7 + 1,
                        endDay = (weekIndex + 1) * 7
                    )
                )
            }
            weekIndex++
        }

        val totalHours = phases.sumOf { p -> p.items.sumOf { it.estimatedHours } }

        return StudyPlan(
            id = System.currentTimeMillis().toString(),
            title = "Kế hoạch học tập",
            phases = phases,
            totalHours = totalHours,
            generatedAt = System.currentTimeMillis()
        )
    }

    /**
     * Redistributes items when any phase exceeds [maxHoursPerWeek].
     * Overflow spills into subsequent phases; extra phases are appended if needed.
     */
    fun balanceWorkload(plan: StudyPlan, maxHoursPerWeek: Double): StudyPlan {
        val balanced = mutableListOf<StudyPhase>()
        val overflow = mutableListOf<StudyItem>()

        plan.phases.forEach { phase ->
            val pool = (phase.items + overflow).toMutableList()
            overflow.clear()

            var weekHours = 0.0
            val keep = mutableListOf<StudyItem>()

            // Sort easy-first so hardest items spill last
            pool.sortedBy { it.estimatedHours }.forEach { item ->
                if (weekHours + item.estimatedHours <= maxHoursPerWeek) {
                    keep.add(item)
                    weekHours += item.estimatedHours
                } else {
                    overflow.add(item)
                }
            }

            balanced.add(phase.copy(items = keep))
        }

        // Remaining overflow -> new trailing phases
        var extraWeek = plan.phases.size
        var pending = overflow.toMutableList()
        while (pending.isNotEmpty()) {
            var weekHours = 0.0
            val batch = mutableListOf<StudyItem>()
            val iter = pending.iterator()
            while (iter.hasNext()) {
                val item = iter.next()
                if (weekHours + item.estimatedHours <= maxHoursPerWeek) {
                    batch.add(item)
                    weekHours += item.estimatedHours
                    iter.remove()
                }
            }
            if (batch.isEmpty() && pending.isNotEmpty()) {
                batch.add(pending.removeAt(0))
            }
            balanced.add(
                StudyPhase(
                    title = "Tuần ${extraWeek + 1}",
                    items = batch,
                    startDay = extraWeek * 7 + 1,
                    endDay = (extraWeek + 1) * 7
                )
            )
            extraWeek++
        }

        val totalHours = balanced.sumOf { p -> p.items.sumOf { it.estimatedHours } }
        return plan.copy(phases = balanced, totalHours = totalHours)
    }

    /**
     * Rebuilds a plan after some items are completed.
     * Completed items removed; remaining items redistributed across same number of phases.
     */
    fun autoReprioritize(plan: StudyPlan, completedItems: Set<String>): StudyPlan {
        val allItems = plan.phases.flatMap { it.items }
        val remaining = allItems.filter { it.id !in completedItems }

        if (remaining.isEmpty()) {
            return plan.copy(phases = emptyList(), totalHours = 0.0)
        }

        val numPhases = maxOf(1, plan.phases.size)
        val perPhase = maxOf(1, remaining.size / numPhases)
        val extra = remaining.size % numPhases

        val newPhases = mutableListOf<StudyPhase>()
        var idx = 0
        for (i in 0 until numPhases) {
            if (idx >= remaining.size) break
            val count = perPhase + if (i < extra) 1 else 0
            val chunk = remaining.subList(idx, minOf(idx + count, remaining.size))
            newPhases.add(
                StudyPhase(
                    title = "Tuần ${i + 1}",
                    items = chunk,
                    startDay = i * 7 + 1,
                    endDay = (i + 1) * 7
                )
            )
            idx += count
        }

        val totalHours = newPhases.sumOf { p -> p.items.sumOf { it.estimatedHours } }
        return plan.copy(phases = newPhases, totalHours = totalHours)
    }
}
