package mx.itesm.proyectofinalmoviles.Paciente

class Registro(val id_username: String, val sistole: String, val diastole: String, val pulso: String, val tipo: String, val fecha: String) {
    constructor() : this("", "", "", "", "", "") {

    }
}