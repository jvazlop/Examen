package org.iesra.procesaalumnos.service

import org.iesra.procesaalumnos.model.Student
import org.iesra.procesaalumnos.model.FileIssue

class GroupAssigner {
    // Almacena los grupos: "A" -> lista de alumnos
    private val groups = mutableMapOf<String, MutableList<Student>>()
    private val maxCapacity = 5
    val issues = mutableListOf<String>()

    fun assignGroup(student: Student) {
        val requested = student.grupoSolicitado?.uppercase()

        // 1. Intentar asignar el grupo solicitado si existe y tiene hueco
        if (!requested.isNullOrBlank() && canFitIn(requested)) {
            addToGroup(requested, student)
            student.grupoAsignado = requested
        } else {
            // 2. Si no es válido o está lleno, buscar uno disponible o crear uno
            val assigned = findAvailableOrCreate()

            // Registrar incidencia si fue por falta de hueco o grupo no informado
            val reason = if (requested.isNullOrBlank()) "no informado" else "grupo $requested lleno"
            issues.add("archivo ${student.nombre}.txt: grupo $reason, asignado a $assigned")

            addToGroup(assigned, student)
            student.grupoAsignado = assigned
        }
    }

    private fun canFitIn(groupName: String): Boolean {
        return (groups[groupName]?.size ?: 0) < maxCapacity
    }

    private fun addToGroup(groupName: String, student: Student) {
        groups.getOrPut(groupName) { mutableListOf() }.add(student)
    }

    private fun findAvailableOrCreate(): String {
        // Buscar en el abecedario A, B, C...
        for (char in 'A'..'Z') {
            val letter = char.toString()
            if (canFitIn(letter)) return letter
        }
        return "Z" // Fallback extremo
    }

    fun getAllGroups(): Map<String, List<Student>> = groups
}