package mx.itesm.proyectofinalmoviles.Paciente

class Paciente(val id_username:String, val nombre: String, val fecha: String, val sexo:String,
               val peso:String, val altura:String, val imc:String, val id_doctor:String) {
    constructor() : this("", "", "", "", "", "", "", "") {

    }
}