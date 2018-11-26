package mx.itesm.proyectofinalmoviles.Paciente

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import mx.itesm.proyectofinalmoviles.R
import kotlinx.android.synthetic.main.activity_registro_paciente_nuevo.*
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import mx.itesm.proyectofinalmoviles.MainActivity
import mx.itesm.proyectofinalmoviles.SessionManager


class RegistroPacienteNuevo : AppCompatActivity() {

    lateinit var session: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_paciente_nuevo)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) //Hiding the keyboard
        supportActionBar!!.title = "Registro"

        //Recibe el id del paciente desde el login (solo una vez)
        val extras = intent.extras
        val paciente_id = extras.getString(MainActivity.PACIENTE_ID)!!
        val doctor_id = extras.getString(MainActivity.DOCTOR_ID)!!


        guardarPacienteInfo.setOnClickListener {
            val nombrePaciente = findViewById(R.id.nombre) as EditText
            val nombre = nombrePaciente.text.toString()
            val fechaPaciente = findViewById(R.id.fecha) as EditText
            val fecha = fechaPaciente.text.toString()
            val sexoPaciente = findViewById(R.id.sexo_spinner) as Spinner
            val sexo = sexoPaciente.selectedItem.toString()
            val pesoPaciente = findViewById(R.id.peso) as EditText
            val peso = pesoPaciente.text.toString()
            val alturaPaciente = findViewById(R.id.altura) as EditText
            val altura = alturaPaciente.text.toString()
            val imcPaciente = findViewById(R.id.indiceMasaCorporal) as EditText
            val indiceMasaCorporal = imcPaciente.text.toString()



            /*
            * METER LOS DATOS A LA TABLA DE USUARIOS ACTUALIZANDO LA INFORMACIÓN DE ESTE PACIENTE (paciente_id)
            * */
            //Writing new user information
            val mFirebaseDatabase: FirebaseDatabase
            val mDatabaseReference: DatabaseReference
            mFirebaseDatabase = FirebaseDatabase.getInstance()
            mDatabaseReference = mFirebaseDatabase.getReference().child("pacientes")
            val paciente = Paciente(paciente_id, nombre, fecha, sexo, peso, altura, indiceMasaCorporal, doctor_id)
            mDatabaseReference.push().setValue(paciente)
            Toast.makeText(applicationContext, "Información guardada.", Toast.LENGTH_LONG).show()


            //Ir a la pagina principal de los pacientes
            session = SessionManager(applicationContext)
            session.createLoginSession(paciente_id, "paciente")
            val i = Intent(applicationContext, PacienteActivity::class.java)
            startActivity(i)
            finish()
        }
    }




    override fun onBackPressed() {
        session = SessionManager(applicationContext)
        session.LogoutUser()
    }
}
