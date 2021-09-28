package se.lth.cs.leow.thirtythrows

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Skapar Intent som används för att skicka vidare spelaren till speldelen
        findViewById<Button>(R.id.play_button).setOnClickListener {
            val playIntent = Intent(this, PlayActivity::class.java)
            startActivity(playIntent)
        }
    }
}