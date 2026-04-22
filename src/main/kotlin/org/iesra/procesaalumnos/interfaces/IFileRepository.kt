package org.iesra.procesaalumnos.service

import java.io.File
import java.nio.file.Path

interface IFileRepository {
    fun findInputFiles(path: Path): List<File>
    fun moveToProcessed(file: File, basePath: Path)
}