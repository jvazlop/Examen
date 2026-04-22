package org.iesra.procesaalumnos.interfaces

import org.iesra.procesaalumnos.model.Student

interface IEmailGenerator {
    fun generate(student: Student): String
}