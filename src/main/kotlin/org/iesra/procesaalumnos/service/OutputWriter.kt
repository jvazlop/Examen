package org.iesra.procesaalumnos.service

import org.iesra.procesaalumnos.model.Student
import java.io.File
import java.nio.file.Path

class OutputWriter {

    fun writeFiles(groupName: String, students: List<Student>, groups: Map<String, List<Student>>, baseDir: Path) {
        writeCsv(groupName, students, baseDir)
        writeGroupsTxt(groupName, groups, baseDir)
    }

    private fun writeCsv(groupName: String, students: List<Student>, baseDir: Path) {
        val file = baseDir.resolve("${groupName}-correos.csv").toFile()
        file.bufferedWriter().use { out ->
            out.write("nombre|apellidos|email|email2\n") // Cabecera según enunciado [cite: 77, 82]
            students.forEach { s ->
                out.write("${s.nombre}|${s.apellidos}|${s.emailOriginal}|${s.emailInstituto}\n")
            }
        }
    }

    private fun writeGroupsTxt(groupName: String, groups: Map<String, List<Student>>, baseDir: Path) {
        val file = baseDir.resolve("${groupName}-grupos.txt").toFile()
        file.bufferedWriter().use { out ->
            groups.toSortedMap().forEach { (name, members) ->
                out.write("[Grupo-$name]\n")
                members.forEach { m ->
                    out.write("    -${m.nombre} ${m.apellidos}\n")
                }
                out.write("\n")
            }
        }
    }
}