package se.lth.cs.leow.thirtythrows

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import org.w3c.dom.Text

class EndActivity : AppCompatActivity() {
    //En statisk aktivitet med slutpoäng,
    // behövs därför ingen onSaveInstanceState/onRestoreInstanceState
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end)

        //Hämta data från Intentet skickat från Playaktiviteten
        val roundPoints = intent.getIntArrayExtra("points")
        val roundChoices = intent.getStringArrayExtra("choices")

        val totalPoints =  roundPoints?.sum()

        //Skriver ut texten på skärmen
        findViewById<TextView>(R.id.round_points_show).text = roundPoints.contentToString()
        findViewById<TextView>(R.id.total_points_text).text = totalPoints.toString()
        findViewById<TextView>(R.id.round_choices_show).text = roundChoices.contentToString()

        //En spela igen knapp som tar dig tillbaka till spelaktiviteten
        findViewById<Button>(R.id.playAgainButton).setOnClickListener {
            val playIntent = Intent(this, PlayActivity::class.java)
            //finish så att man inte ska kunna ta sig tillbaka till statistiken med bak-knappen
            finish()
            startActivity(playIntent)
        }
    }
}