package org.iesra.procesaalumnos.interfaces

import java.io.File
import org.iesra.procesaalumnos.model.Student

interface IStudentParser {
    fun parse(file: File): Student
}