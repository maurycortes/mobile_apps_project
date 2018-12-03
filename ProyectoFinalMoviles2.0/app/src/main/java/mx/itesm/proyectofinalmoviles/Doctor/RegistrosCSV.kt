package mx.itesm.proyectofinalmoviles.Doctor

class RegistrosCSV {
    var id_username: String? = null
    var sistole: String? = null
    var diastole: String? = null
    var pulso: String? = null
    var tipo: String? = null
    var fecha: String? = null

    constructor() {}
    constructor(id_username: String?, sistole: String?, diastole: String?, pulso: String?, tipo: String?, fecha: String?) {
        this.id_username = id_username
        this.sistole = sistole
        this.diastole = diastole
        this.pulso = pulso
        this.tipo = tipo
        this.fecha = fecha
    }
}