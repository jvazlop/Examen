package org.iesra.procesaalumnos

import org.iesra.procesaalumnos.cli.CliOptions
import org.iesra.procesaalumnos.model.FileIssue
import org.iesra.procesaalumnos.model.Student
import org.iesra.procesaalumnos.service.* // Importamos todas las interfaces y clases
import java.nio.file.Path
import org.iesra.procesaalumnos.interfaces.*

class StudentProcessingApplication {

    fun run(options: CliOptions) {
        // 1. Configuración de la ruta base
        val inputDir = options.path ?: Path.of(".")

        // 2. Inicialización de servicios mediante sus interfaces
        // Esto permite que el código sea flexible y profesional
        val fileRepository: IFileRepository = FileRepository()
        val parser: IStudentParser = StudentParser()
        val emailGenerator: IEmailGenerator = EmailGenerator()
        val groupAssigner: IGroupAssigner = GroupAssigner()
        val outputWriter = OutputWriter() // OutputWriter suele ser directo, pero podrías darle interfaz también

        // 3. Localización de los archivos .txt
        val files = fileRepository.findInputFiles(inputDir)
        val students = mutableListOf<Student>()
        val issues = mutableListOf<FileIssue>()

        println("Iniciando procesamiento de ${files.size} archivos...")

        for (file in files) {
            try {
                // 4. Transformar el archivo TXT en un objeto Student (IStudentParser)
                val student = parser.parse(file)

                // 5. Generar el correo corporativo (IEmailGenerator)
                student.emailInstituto = emailGenerator.generate(student)

                // 6. Asignar grupo y gestionar plazas (IGroupAssigner)
                groupAssigner.assignGroup(student)

                // 7. Si todo ha ido bien, lo añadimos a la lista de éxito
                students.add(student)
                println("Procesado correctamente: ${file.name}")

            } catch (e: Exception) {
                // 8. Si falla el parseo o faltan datos, registramos la incidencia
                issues.add(FileIssue(file.name, e.message ?: "Error de formato"))
                println("Error en archivo: ${file.name} -> ${e.message}")
            } finally {
                // 9. Independientemente de si hubo error o no, movemos el archivo a 'procesados'
                fileRepository.moveToProcessed(file, inputDir)
            }
        }

        // --- GENERACIÓN DE RESULTADOS FINALES ---

        if (students.isNotEmpty()) {
            outputWriter.writeFiles(
                options.group,
                students,
                groupAssigner.getAllGroups(),
                inputDir
            )
            println("\nArchivos de salida generados correctamente en: $inputDir")
        }

        // --- RESUMEN POR PANTALLA ---
        mostrarResumen(students, issues, groupAssigner)
    }

    /**
     * Imprime el resumen final que exige el enunciado
     */
    private fun mostrarResumen(students: List<Student>, issues: List<FileIssue>, assigner: IGroupAssigner) {
        println("\n" + "=".repeat(40))
        println("        RESUMEN FINAL DEL EXAMEN")
        println("=".repeat(40))
        println("Total archivos analizados: ${students.size + issues.size}")
        println("Alumnos procesados con éxito: ${students.size}")
        println("Archivos con errores de formato: ${issues.size}")

        println("\nDistribución de Grupos:")
        assigner.getAllGroups().forEach { (letra, lista) ->
            println("  - Grupo-$letra: ${lista.size} alumnos")
        }

        if (issues.isNotEmpty() || assigner.issues.isNotEmpty()) {
            println("\nIncidencias y Errores:")
            // Errores de lectura/formato
            issues.forEach { println("  [!] Fichero ${it.fileName}: ${it.message}") }
            // Avisos de reasignación (grupos llenos o no indicados)
            assigner.issues.forEach { println("  [i] $it") }
        }
        println("=".repeat(40))
    }
}