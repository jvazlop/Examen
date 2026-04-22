package org.iesra.procesaalumnos.interfaces

import org.iesra.procesaalumnos.model.Student

interface IGroupAssigner {
    fun assignGroup(student: Student)
    fun getAllGroups(): Map<String, List<Student>>
    val issues: List<String>
}