package org.iesra.procesaalumnos.model

data class Student(
    val nombre: String,
    val apellidos: String,
    val emailOriginal: String,
    var grupoSolicitado: String? = null,
    var grupoAsignado: String? = null,
    var emailInstituto: String? = null
)