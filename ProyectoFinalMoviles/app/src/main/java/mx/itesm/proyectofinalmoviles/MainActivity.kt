package mx.itesm.proyectofinalmoviles

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.database.*
import mx.itesm.proyectofinalmoviles.Doctor.DoctorActivity
import kotlinx.android.synthetic.main.activity_main.*
import mx.itesm.proyectofinalmoviles.Doctor.Doctor
import mx.itesm.proyectofinalmoviles.Paciente.*
import android.net.ConnectivityManager


class MainActivity : AppCompatActivity() {

    companion object {
        const val PACIENTE_ID: String = "PACIENTE_ID"
        const val DOCTOR_ID: String = "DOCTOR_ID"
        var USER_CODE: Int = 1
        var doctor_status = "nuevo"
        var paciente = "nuevo"
        var match = false
    }
    lateinit var listPacientes: MutableList<Paciente>
    lateinit var listDoctores: MutableList<Doctor>
    lateinit var listPendientes: MutableList<PacientesDoctores>
    lateinit var session: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)







        //Reading doctors information
        val mFirebaseDatabase_Doctor: FirebaseDatabase
        val mDatabaseReference_Doctor: DatabaseReference
        val mChildEventListener_Doctor: ChildEventListener
        listDoctores = mutableListOf()
        mFirebaseDatabase_Doctor = FirebaseDatabase.getInstance()
        mDatabaseReference_Doctor = mFirebaseDatabase_Doctor.getReference().child("doctor")
        mChildEventListener_Doctor = object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                val doctor = p0!!.getValue(Doctor::class.java)
                listDoctores.add(doctor!!)
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                val doctor = p0!!.getValue(Doctor::class.java)
                listDoctores.add(doctor!!)
            }

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                val doctor = p0!!.getValue(Doctor::class.java)
                listDoctores.add(doctor!!)
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                val doctor = p0!!.getValue(Doctor::class.java)
                listDoctores.add(doctor!!)
            }
        }
        mDatabaseReference_Doctor.addChildEventListener(mChildEventListener_Doctor)







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






        //Reading patients pending information
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
                val pacientePendiente = p0!!.getValue(PacientesDoctores::class.java)
                listPendientes.add(pacientePendiente!!)
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                val pacientePendiente = p0!!.getValue(PacientesDoctores::class.java)
                listPendientes.add(pacientePendiente!!)
            }

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                val pacientePendiente = p0!!.getValue(PacientesDoctores::class.java)
                listPendientes.add(pacientePendiente!!)
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                val pacientePendiente = p0!!.getValue(PacientesDoctores::class.java)
                listPendientes.add(pacientePendiente!!)
            }
        }
        mDatabaseReference_Pendiente.addChildEventListener(mChildEventListener_Pendiente)






        /*
        * VALIDAR LA SESION
        * */
        session = SessionManager(applicationContext)
        if(session.isLoggedIn()) {
            val user: HashMap<String, String> = session.getUserDetails()
            val userType: String = user.get(SessionManager.USER_TYPE)!!
            var i = Intent(applicationContext, PacienteActivity::class.java)
            if (userType == "paciente") {
                i = Intent(applicationContext, PacienteActivity::class.java)
            } else {
                i = Intent(applicationContext, DoctorActivity::class.java)
            }
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            finish()
        }








        /*
        *   VALIDAR LOS LOGINS (QUE EXISTAN LOS USUARIOS) Y QUÉ TIPO DE USUARIO ES (PACIENTE O DOCTOR)
        * */

        entrar.setOnClickListener {
            doctor_status = ""
            paciente = "nuevo"
            match = false
            val userType = findViewById(R.id.tipoUsuario) as Spinner
            val user = userType.selectedItem.toString()
            val userText = findViewById(R.id.usuario) as EditText
            val user_id = userText.text.toString()
            val doctorText = findViewById(R.id.doctor) as EditText
            val doctor_id = doctorText.text.toString()

            /*
            * Checking Connection
            * */
            if(haveNetworkConnection()) {

                if (user == "Paciente") {

                    for (r in listPendientes) {
                        if (r.id_username == user_id && r.id_doctor == doctor_id) {
                            paciente = "pendiente"
                            match = true
                        }
                    }

                    for (r in listPacientes) {
                        if (r.id_username == user_id) {
                            paciente = "existe"
                        }
                    }

                    for (r in listDoctores) {
                        val doctorLookup = "doctor_" + doctor_id
                        if (r.id_doctor == doctorLookup) {
                            doctor_status = "existe"
                        }
                    }

                    /*
                    * VALIDACION DE SI EL PACIENTE ES NUEVO O YA TIENE DATOS
                    * */
                    if (paciente == "existe" && doctor_status == "existe" && match) {
                        session.createLoginSession(user_id, "paciente") //Create new session
                        val i = Intent(applicationContext, PacienteActivity::class.java)
                        startActivity(i)
                        finish()
                    } else if (paciente == "pendiente" && match) {
                        val intent = Intent(this, RegistroPacienteNuevo::class.java)
                        intent.putExtra(PACIENTE_ID, user_id)
                        intent.putExtra(DOCTOR_ID, doctor_id)
                        startActivityForResult(intent, USER_CODE)
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "Credenciales incorrectas. Valide con su doctor.", Toast.LENGTH_LONG).show()
                    }
                } else {

                    for (r in listDoctores) {
                        if (r.id_doctor == doctor_id) {
                            doctor_status = "existe"
                        }
                    }

                    /*
                    * VALIDAR QUE EL DOCTOR EXISTA Y MANDAR ESE ID AL SIGUIENTE INTENT
                    * */
                    if (doctor_status == "existe") {
                        val doctor_id_clean = doctor_id.substring(7, doctor_id.length)
                        session.createLoginSession(doctor_id_clean, "doctor")
                        val i = Intent(applicationContext, DoctorActivity::class.java)
                        startActivity(i)
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "El doctor no está dado de alta.", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(applicationContext, "No tiene conexión a internet.", Toast.LENGTH_LONG).show()
            }
        }






        //Hiding the user_id edit text when "doctor" is selected in the spinner
        val spinner = findViewById<Spinner>(R.id.tipoUsuario)
        val adapter = ArrayAdapter.createFromResource(
                this,
                R.array.tipo_usuario, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0) {
                    usuario.visibility = View.INVISIBLE
                } else {
                    usuario.visibility = View.VISIBLE
                }
            }
        }




    }





    override fun onBackPressed() {
        // Do Here what ever you want do on back press;
    }







    private fun haveNetworkConnection(): Boolean {
        var haveConnectedWifi = false
        var haveConnectedMobile = false

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val netInfo = cm.allNetworkInfo
        for (ni in netInfo) {
            if (ni.typeName.equals("WIFI", ignoreCase = true))
                if (ni.isConnected)
                    haveConnectedWifi = true
            if (ni.typeName.equals("MOBILE", ignoreCase = true))
                if (ni.isConnected)
                    haveConnectedMobile = true
        }
        return haveConnectedWifi || haveConnectedMobile
    }
}
