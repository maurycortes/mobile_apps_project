package mx.itesm.proyectofinalmoviles.Doctor

import android.os.Bundle
import android.os.Environment
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_doctor.*
import mx.itesm.proyectofinalmoviles.MainActivity
import mx.itesm.proyectofinalmoviles.Paciente.Paciente
import mx.itesm.proyectofinalmoviles.Paciente.PacientesDoctores
import mx.itesm.proyectofinalmoviles.Paciente.Registro
import mx.itesm.proyectofinalmoviles.R
import mx.itesm.proyectofinalmoviles.SessionManager
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Arrays
import com.opencsv.CSVWriter
import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.StatefulBeanToCsv
import com.opencsv.bean.StatefulBeanToCsvBuilder
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
//import java.util.jar.Manifest
import android.Manifest
import javax.xml.xpath.XPathConstants.STRING


class DoctorActivity : AppCompatActivity() {

    companion object {
        var opcion = "Registro Paciente"
        var username_exists = false
        var username_pending = false
        const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0
    }
    lateinit var doctor_id: String
    lateinit var listRegistros: MutableList<Registro>
    lateinit var listPacientes: MutableList<Paciente>
    lateinit var listPendientes: MutableList<PacientesDoctores>
    lateinit var session: SessionManager
    private val CSV_HEADER = arrayOf<String>("id_username", "sistole", "diastole", "pulso", "tipo", "fecha")
    lateinit var downloadRegisters: ArrayList<RegistrosCSV>


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.doctor_registrar_paciente -> {
                supportActionBar!!.title = "Registrar Pacientes"
                tituloDoctor.setText("Registro de Paciente Nuevo")
                buttonDoctor.setText("Registrar")
                registrarPacienteNuevo.visibility = View.VISIBLE
                opcion = "Registro Paciente"

                buttonDoctor.setOnClickListener {
                    registrarPaciente()
                }

                logoutDoctor.setOnClickListener {
                    session.LogoutUser()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.doctor_descargar_informacion -> {
                supportActionBar!!.title = "Descargar Información"
                tituloDoctor.setText("Descarga de información de pacientes")
                buttonDoctor.setText("Descargar")
                registrarPacienteNuevo.visibility = View.INVISIBLE
                opcion = "Descarga Informacion"

                buttonDoctor.setOnClickListener {
                    descargarInformacion()
                }

                logoutDoctor.setOnClickListener {
                    session.LogoutUser()
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor)
        supportActionBar!!.title = "Registrar Pacientes"
        tituloDoctor.setText("Registro de Paciente Nuevo")
        registrarPacienteNuevo.visibility = View.VISIBLE

        session = SessionManager(applicationContext)
        session.checkLogin()
        logoutDoctor.setOnClickListener {
            session.LogoutUser()
        }
        var user: HashMap<String, String> = session.getUserDetails()
        doctor_id = user.get(SessionManager.KEY_NAME)!!


        //Set the doctor username
        val titleTextDoctor = "Doctor: " + doctor_id
        usernameDoctor.setText(titleTextDoctor)



        buttonDoctor.setOnClickListener {
            if (opcion == "Registro Paciente") {
                registrarPaciente()
            } else {
                descargarInformacion()
            }
        }

        //Reading patients information
        val mFirebaseDatabase_Registros: FirebaseDatabase
        val mDatabaseReference_Registros: DatabaseReference
        val mChildEventListener_Registros: ChildEventListener
        listRegistros = mutableListOf()
        mFirebaseDatabase_Registros = FirebaseDatabase.getInstance()
        mDatabaseReference_Registros = mFirebaseDatabase_Registros.getReference().child("registros")
        mChildEventListener_Registros = object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                val registro = p0!!.getValue(Registro::class.java)
                listRegistros.add(registro!!)
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                val registro = p0!!.getValue(Registro::class.java)
                listRegistros.add(registro!!)
            }

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                val registro = p0!!.getValue(Registro::class.java)
                listRegistros.add(registro!!)
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                val registro = p0!!.getValue(Registro::class.java)
                listRegistros.add(registro!!)
            }
        }
        mDatabaseReference_Registros.addChildEventListener(mChildEventListener_Registros)





        //Reading patients information
        val mFirebaseDatabase_Paciente: FirebaseDatabase
        val mDatabaseReference_Paciente: DatabaseReference
        val mChildEventListener_Paciente: ChildEventListener
        listPacientes = mutableListOf()
        mFirebaseDatabase_Paciente = FirebaseDatabase.getInstance()
        mDatabaseReference_Paciente = mFirebaseDatabase_Paciente.getReference().child("pacientes")
        mChildEventListener_Paciente = object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                val paciente = p0!!.getValue(Paciente::class.java)
                listPacientes.add(paciente!!)
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                val paciente = p0!!.getValue(Paciente::class.java)
                listPacientes.add(paciente!!)
            }

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                val paciente = p0!!.getValue(Paciente::class.java)
                listPacientes.add(paciente!!)
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                val paciente = p0!!.getValue(Paciente::class.java)
                listPacientes.add(paciente!!)
            }
        }
        mDatabaseReference_Paciente.addChildEventListener(mChildEventListener_Paciente)


        //Reading pending patients information
        val mFirebaseDatabase_Pendiente: FirebaseDatabase
        val mDatabaseReference_Pendiente: DatabaseReference
        val mChildEventListener_Pendiente: ChildEventListener
        listPendientes = mutableListOf()
        mFirebaseDatabase_Pendiente = FirebaseDatabase.getInstance()
        mDatabaseReference_Pendiente = mFirebaseDatabase_Pendiente.getReference().child("pacientes_doctores")
        mChildEventListener_Pendiente = object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                val paciente = p0!!.getValue(PacientesDoctores::class.java)
                listPendientes.add(paciente!!)
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                val paciente = p0!!.getValue(PacientesDoctores::class.java)
                listPendientes.add(paciente!!)
            }

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                val paciente = p0!!.getValue(PacientesDoctores::class.java)
                listPendientes.add(paciente!!)
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                val paciente = p0!!.getValue(PacientesDoctores::class.java)
                listPendientes.add(paciente!!)
            }
        }
        mDatabaseReference_Pendiente.addChildEventListener(mChildEventListener_Pendiente)




        navigation_doctor.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }






    fun registrarPaciente() {
        val nombrePacienteNuevo = findViewById(R.id.registrarPacienteNuevo) as EditText?
        val username = nombrePacienteNuevo!!.text.toString()
        username_exists = false
        username_pending = false

        if (username == "") {
            Toast.makeText(applicationContext, "Introduzca un nombre", Toast.LENGTH_LONG).show()
        } else {

            /*
            * VALIDAR QUE EL USUARIO NO EXISTA YA EN LA TABLA DE PACIENTES, sacar el user_status = "Existe" o "No existe"
            * */

            for (r in listPacientes) {
                if (r.id_username == username) {
                    username_exists = true
                }
            }

            for (r in listPendientes) {
                if (r.id_username == username) {
                    username_pending = true
                }
            }

            if (username_exists) {
                Toast.makeText(applicationContext, "El paciente ya existe.", Toast.LENGTH_LONG).show()
            }
            else if(username_pending) {
                Toast.makeText(applicationContext, "El paciente tiene pendiente registrarse.", Toast.LENGTH_LONG).show()
            }
            else if (!username_exists && !username_pending){

                /*
                * REGISTRAR EL NOMBRE DEL USUARIO A REGISTRAR EN LA TABLA DE PACIENTES (username)
                * */

                val registrarPaciente = findViewById(R.id.registrarPacienteNuevo) as EditText
                val paciente = registrarPaciente.text.toString()
                val mFirebaseDatabase: FirebaseDatabase
                val mDatabaseReference: DatabaseReference
                mFirebaseDatabase = FirebaseDatabase.getInstance()
                mDatabaseReference = mFirebaseDatabase.getReference().child("pacientes_doctores")
                val pacienteNuevo = PacientesDoctores(paciente, doctor_id)
                mDatabaseReference.push().setValue(pacienteNuevo)

                Toast.makeText(applicationContext, "Paciente registrado.", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(applicationContext, "Hubo un error en el registro de paciente.", Toast.LENGTH_LONG).show()
            }
        }



    }

    fun descargarInformacion() {

        /*
        * DESCARGAR TABLA DE REGISTROS A CSV O EXCEL
        * */

        if (ContextCompat.checkSelfPermission(this@DoctorActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            println("CON PERMISO")
            ActivityCompat.requestPermissions(this@DoctorActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_READ_CONTACTS)
        } else {
            println("COMOQUIERA")
            ActivityCompat.requestPermissions(this@DoctorActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSIONS_REQUEST_READ_CONTACTS)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    val baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                    val fileName = "MonitoreoUsuarios.csv"
                    val filePath = baseDir + File.separator + fileName
                    var fileWriter: FileWriter? = null
                    var csvWriter: CSVWriter? = null
                    var beanToCsv: StatefulBeanToCsv<RegistrosCSV>?

                    try {
                        fileWriter = FileWriter(filePath)

                        // write String Array
                        csvWriter = CSVWriter(fileWriter,
                                CSVWriter.DEFAULT_SEPARATOR,
                                CSVWriter.NO_QUOTE_CHARACTER,
                                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                CSVWriter.DEFAULT_LINE_END)

                        csvWriter.writeNext(CSV_HEADER)

                        //Initializing the list of registers
                        downloadRegisters = arrayListOf()

                        for (r in listRegistros) {
                            var newRegister = RegistrosCSV(r.id_username, r.sistole, r.diastole, r.pulso, r.tipo, r.fecha)
                            downloadRegisters.add(newRegister)
                        }

                        for (r in downloadRegisters) {
                            val data = arrayOf(
                                    r.id_username,
                                    r.sistole,
                                    r.diastole,
                                    r.pulso,
                                    r.tipo,
                                    r.fecha)

                            csvWriter.writeNext(data)
                        }

                        println("Write CSV using CSVWriter successfully!")

                        fileWriter = FileWriter(fileName)

                        // write List of Objects
                        val mappingStrategy = ColumnPositionMappingStrategy<RegistrosCSV>()
                        mappingStrategy.setType(RegistrosCSV::class.java)
                        mappingStrategy.setColumnMapping("id_username", "sistole", "diastole", "pulso", "tipo", "fecha")

                        beanToCsv = StatefulBeanToCsvBuilder<RegistrosCSV>(fileWriter)
                                .withMappingStrategy(mappingStrategy)
                                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                                .build()
                        beanToCsv.write(downloadRegisters)

                        println("Write CSV using BeanToCsv successfully!")

                    } catch (e: Exception) {
                        println("Writing CSV error!")
                        e.printStackTrace()
                    } finally {
                        try {
                            fileWriter?.close()
                            csvWriter!!.close()
                        } catch (e: IOException) {
                            println("Flushing/closing error!")
                            e.printStackTrace()
                        }
                    }
                    Toast.makeText(applicationContext, "Información descargada en Mis Archivos/Almacenamiento Interno/Download", Toast.LENGTH_LONG).show()

                } else {
                    // permission denied
                    Toast.makeText(applicationContext, "Permiso no autorizado", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }
}
