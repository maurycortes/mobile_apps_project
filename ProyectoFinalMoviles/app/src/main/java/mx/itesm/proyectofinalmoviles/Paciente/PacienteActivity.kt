package mx.itesm.proyectofinalmoviles.Paciente

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_paciente.*
import mx.itesm.proyectofinalmoviles.R
import java.text.SimpleDateFormat
import java.util.*
import android.os.CountDownTimer
import android.support.v7.app.AlertDialog
import mx.itesm.proyectofinalmoviles.SessionManager
import kotlin.collections.HashMap


class PacienteActivity : AppCompatActivity() {

    lateinit var listRegistros: MutableList<Registro>
    lateinit var paciente_id: String
    lateinit var session: SessionManager
    lateinit var paciente_title: String

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.toma_datos_aleatorio -> {
                supportActionBar!!.title = paciente_title
                tituloPacienteHistoricos.visibility = View.INVISIBLE
                list_historicos_protocolo.visibility = View.INVISIBLE
                list_historicos_aleatorio.visibility = View.INVISIBLE
                tituloPaciente.setText(R.string.analisis_aleatorio)
                tituloPaciente.visibility = View.VISIBLE
                timer.visibility = View.INVISIBLE
                iniciarTimer.visibility = View.INVISIBLE
                sistole.visibility = View.VISIBLE
                diastole.visibility = View.VISIBLE
                pulso.visibility = View.VISIBLE
                guardar.visibility = View.VISIBLE
                sistole.isEnabled = true
                diastole.isEnabled = true
                pulso.isEnabled = true
                guardar.isEnabled = true

                guardar.setOnClickListener {
                    setDatosPacienteValidation("Aleatorio")
                    sistole.text.clear()
                    diastole.text.clear()
                    pulso.text.clear()
                }

                logoutPaciente.setOnClickListener {
                    session.LogoutUser()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.toma_datos_protocolo -> {
                supportActionBar!!.title = paciente_title
                tituloPacienteHistoricos.visibility = View.INVISIBLE
                list_historicos_protocolo.visibility = View.INVISIBLE
                list_historicos_aleatorio.visibility = View.INVISIBLE
                tituloPaciente.setText(R.string.analisis_protocolo)
                tituloPaciente.visibility = View.VISIBLE
                timer.visibility = View.VISIBLE
                iniciarTimer.visibility = View.VISIBLE
                sistole.visibility = View.VISIBLE
                diastole.visibility = View.VISIBLE
                pulso.visibility = View.VISIBLE
                guardar.visibility = View.VISIBLE
                sistole.isEnabled = false
                diastole.isEnabled = false
                pulso.isEnabled = false
                guardar.isEnabled = false
                iniciarTimer.isEnabled = true

                iniciarTimer.setOnClickListener {
                    object : CountDownTimer(300000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            val mins_str = ((millisUntilFinished / 1000) / 60).toInt().toString()
                            val secs = ((millisUntilFinished / 1000) % 60).toInt()
                            var secs_str: String = secs.toString()
                            if(secs < 10) {
                                secs_str = "0" + secs.toString()
                            }
                            var time = "0" + mins_str + ":" + secs_str
                            timer.setText(time)
                        }
                        override fun onFinish() {
                            timer.setText(R.string.timer_terminado)

                            val timerAlert = AlertDialog.Builder(this@PacienteActivity)
                            timerAlert.setTitle("Aviso Importante")
                            timerAlert.setMessage("El tiempo de espera ha terminado, continua introduciendo la información por protocolo.")
                            timerAlert.setPositiveButton("OK") { dialog, which ->
                            }
                            val dialog: AlertDialog = timerAlert.create()
                            dialog.show()

                            sistole.isEnabled = true
                            diastole.isEnabled = true
                            pulso.isEnabled = true
                            guardar.isEnabled = true
                        }
                    }.start()
                    iniciarTimer.isEnabled = false
                }


                guardar.setOnClickListener {
                    setDatosPacienteValidation("Protocolo")
                    sistole.text.clear()
                    diastole.text.clear()
                    pulso.text.clear()
                }

                logoutPaciente.setOnClickListener {
                    session.LogoutUser()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.historicos_aleatorio -> {
                supportActionBar!!.title = paciente_title
                tituloPacienteHistoricos.visibility = View.VISIBLE
                tituloPaciente.visibility = View.INVISIBLE
                timer.visibility = View.INVISIBLE
                iniciarTimer.visibility = View.INVISIBLE
                sistole.visibility = View.INVISIBLE
                diastole.visibility = View.INVISIBLE
                pulso.visibility = View.INVISIBLE
                guardar.visibility = View.INVISIBLE
                list_historicos_protocolo.visibility = View.INVISIBLE
                list_historicos_aleatorio.visibility = View.VISIBLE


                val registrosAleatorios = filtrarHistoricos("Aleatorio")
                val adapterAleatorios = ArrayAdapter(this, android.R.layout.simple_list_item_1, registrosAleatorios)
                list_historicos_aleatorio.adapter = adapterAleatorios

                logoutPaciente.setOnClickListener {
                    session.LogoutUser()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.historicos_protocolo -> {
                supportActionBar!!.title = paciente_title
                tituloPacienteHistoricos.visibility = View.VISIBLE
                tituloPaciente.visibility = View.INVISIBLE
                timer.visibility = View.INVISIBLE
                iniciarTimer.visibility = View.INVISIBLE
                sistole.visibility = View.INVISIBLE
                diastole.visibility = View.INVISIBLE
                pulso.visibility = View.INVISIBLE
                guardar.visibility = View.INVISIBLE
                list_historicos_aleatorio.visibility = View.INVISIBLE
                list_historicos_protocolo.visibility = View.VISIBLE

                val registrosProtocolo = filtrarHistoricos("Protocolo")
                val adapterProtocolo = ArrayAdapter(this, android.R.layout.simple_list_item_1, registrosProtocolo)
                list_historicos_protocolo.adapter = adapterProtocolo

                logoutPaciente.setOnClickListener {
                    session.LogoutUser()
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente)

        //Checar Session
        session = SessionManager(applicationContext)
        session.checkLogin()
        logoutPaciente.setOnClickListener {
            session.LogoutUser()
        }
        val user: HashMap<String, String> = session.getUserDetails()
        paciente_id = user.get(SessionManager.KEY_NAME)!!
        //Set the patient username in the title of the activity
        paciente_title = "Paciente: " + paciente_id


        supportActionBar!!.title = paciente_title
        tituloPaciente.setText(R.string.analisis_aleatorio)
        tituloPacienteHistoricos.visibility = View.INVISIBLE
        tituloPaciente.visibility = View.VISIBLE
        timer.visibility = View.INVISIBLE
        iniciarTimer.visibility = View.INVISIBLE
        sistole.visibility = View.VISIBLE
        diastole.visibility = View.VISIBLE
        pulso.visibility = View.VISIBLE
        guardar.visibility = View.VISIBLE
        list_historicos_aleatorio.visibility = View.INVISIBLE
        list_historicos_protocolo.visibility = View.INVISIBLE
        guardar.setOnClickListener {
            setDatosPacienteValidation("Aleatorio")
            sistole.text.clear()
            diastole.text.clear()
            pulso.text.clear()
        }





        //Reading patient information from Firebase
        val mFirebaseDatabase: FirebaseDatabase
        val mDatabaseReference: DatabaseReference
        val mChildEventListener: ChildEventListener
        listRegistros = mutableListOf()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mFirebaseDatabase.getReference().child("registros")
        mChildEventListener = object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                val registroNuevo = p0!!.getValue(Registro::class.java)
                listRegistros.add(registroNuevo!!)
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                val registroNuevo = p0!!.getValue(Registro::class.java)
                listRegistros.add(registroNuevo!!)
            }

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                val registroNuevo = p0!!.getValue(Registro::class.java)
                listRegistros.add(registroNuevo!!)
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                val registroNuevo = p0!!.getValue(Registro::class.java)
                listRegistros.add(registroNuevo!!)
            }
        }
        mDatabaseReference.addChildEventListener(mChildEventListener)






        navigation_paciente.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }







    fun setDatosPacienteValidation(tipo: String) {
        val sistoleItem = findViewById(R.id.sistole) as EditText
        val sistoleText = sistoleItem.text.toString()
        val diastoleItem = findViewById(R.id.diastole) as EditText
        val diastoleText = diastoleItem.text.toString()
        val pulsoItem = findViewById(R.id.pulso) as EditText
        val pulsoText = pulsoItem.text.toString()

        if (sistoleText != "" && diastoleText != "" && pulsoText != "") {
            setDatosPaciente(tipo)
        } else {
            Toast.makeText(this, "Escribe todos los datos.", Toast.LENGTH_SHORT).show()
        }
    }






    fun setDatosPaciente(tipo: String) {
        val sistolePaciente = findViewById(R.id.sistole) as EditText
        val sistole = sistolePaciente.text.toString()
        val diastolePaciente = findViewById(R.id.diastole) as EditText
        val diastole = diastolePaciente.text.toString()
        val pulsoPaciente = findViewById(R.id.pulso) as EditText
        val pulso = pulsoPaciente.text.toString()
        val df = SimpleDateFormat("dd/M/yyyy")
        val fecha = df.format(Calendar.getInstance().time).toString()

        //Writing new user information
        val mFirebaseDatabase: FirebaseDatabase
        val mDatabaseReference: DatabaseReference
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mFirebaseDatabase.getReference().child("registros")
        val paciente= Registro(paciente_id, sistole, diastole, pulso, tipo, fecha)
        mDatabaseReference.push().setValue(paciente)
        Toast.makeText(applicationContext, "Registro guardado.", Toast.LENGTH_LONG).show()
    }












    //Filtrar los datos historicos basados en su tipo (Aleatorio o por protocolo)
    fun filtrarHistoricos(tipo: String): ArrayList<String> {
        var data: String
        val tempData: ArrayList<String>
        tempData = arrayListOf()

        for (r in listRegistros) {
            if (r.tipo == tipo && r.id_username == paciente_id) {
                data = "Sistole: " + r.sistole + " Diastole: " + r.diastole + " Pulso: " + r.pulso + "\n\nFecha: " + r.fecha + "\n"
                tempData.add(data)
            }
        }

        tempData.reverse() //Para obtener los datos de más nuevos a más viejos
        return tempData
    }
}
