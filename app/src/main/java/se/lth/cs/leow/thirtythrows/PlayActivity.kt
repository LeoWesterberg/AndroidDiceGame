package se.lth.cs.leow.thirtythrows

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class PlayActivity : AppCompatActivity() {
    //initialiserar modellobjektet game med spellogik
    private var game:ThirtyThrows = ThirtyThrows()
    private var listOfImageButtons = listOf<ImageButton>()
    private lateinit var spinner:Spinner
    private lateinit var throwButton:Button


    //Skapar en map mellan modellobjekt för tärningar och dess korresponderande vyobjekt(bildknapp)
    private var hashmap: HashMap<ImageButton, Dices> = HashMap()

    //Placerar alla bildresurser i två listor
    private var greyResources = listOf(R.drawable.grey1,R.drawable.grey2,R.drawable.grey3
                                    ,R.drawable.grey4,R.drawable.grey5,R.drawable.grey6)

    private var whiteResources = listOf(R.drawable.white1,R.drawable.white2,R.drawable.white3
                                    ,R.drawable.white4,R.drawable.white5,R.drawable.white6)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        spinner = findViewById(R.id.spinner)
        throwButton = findViewById(R.id.throw_button)
        updateSpinner(spinner)

        //binder referenser till vyobjekt med variabler och listor, samt skapar lyssnare
        listOfImageButtons = listOf<ImageButton>(
            findViewById(R.id.imageButton1), findViewById(R.id.imageButton2), findViewById(R.id.imageButton3),
            findViewById(R.id.imageButton4), findViewById(R.id.imageButton5), findViewById(R.id.imageButton6))

        //Lägger till kopplingarna i hashmap samt skapar lyssnare för bildknapparna
        listOfImageButtons.forEachIndexed {index, imageButton ->
            hashmap[imageButton] = game.dices[index]
            imageButton.setOnClickListener {
                //i det state där spelaren ska göra sitt val ska man inte kunna hålla fast tärningarna
                if(game.currentRoll <= 2){
                    toggleDice(imageButton)
                }
            }
        }
        //skapar lyssnare för throwbutton, som även hanterar den del som sätter igång den automatiska
        //beräkningen av poäng var tredje runda.
        throwButton.setOnClickListener{
            //Man man endast ska ändra tärningarna plus skiftet då man hanmar i calculating state
            if(game.currentRoll <= 2) {
                updateThrowState()
            }
            else{
                updateCalculateState(spinner.selectedItem.toString())
                if(game.round == 11){
                    switchToEndActivity()
                }
            }
        }
        //Gör så att tärningarna är slumpmässiga redan vid start
        rollDices(listOfImageButtons)
    }
    //Överföring av data och byte av aktivitet till end game
    private fun switchToEndActivity(){
        val endIntent = Intent(this, EndActivity::class.java)
        endIntent.putExtra("points",game.points.toIntArray())
        endIntent.putExtra("choices",game.choices.toTypedArray())

        //finish så att man inte ska kunna ta sig tillbaka till ett avslutat
        // spel med bak-knappen
        finish()
        startActivity(endIntent)
    }
    //uppdaterar state vid throwsteg och kallar på beräkning av score
    private fun updateThrowState(){
        rollDices(listOfImageButtons)
        if(game.currentRoll == 2){
            throwButton.setText(R.string.calculate)
        }
        game.currentRoll++
    }
    //uppdaterar state vid calculatingsteg och kallar på beräkning av score
    private fun updateCalculateState(selected:String){
        val value = if(selected == "Low") 3 else selected.toInt()
        game.remaindingChoices.remove(selected)
        updateSpinner(spinner)
        game.choices.add(selected)
        game.points.add(game.calculateScore(value))
        findViewById<TextView>(R.id.points_show).text = game.points.sum().toString()
        throwButton.setText(R.string.throw_button)
        hashmap.values.forEach { x -> x.locked = false }
        rollDices(listOfImageButtons)
        game.round++
        game.currentRoll = 1
    }

    //uppdaterar spinnervyn
    private fun updateSpinner(spinner:Spinner){
        var spinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, game.remaindingChoices)
        spinnerAdapter.notifyDataSetChanged()
        spinner.adapter = spinnerAdapter
    }
    //Hjälpmetod för att svartmarkera tärning och göra den otillgänglig nästa kast
    private fun toggleDice(view: ImageButton) {
            val dice = hashmap[view] as Dices
            val number: Int = dice.activeNumber
            setToggledImage(view, number, dice.locked)
            dice.switchLock()
    }

    //hjälpmetod för att använda modellobjekts rollDice() metod och uppdatera vyn
    private fun rollDices(list: List<ImageButton>) {
        list.forEach { x ->
                val dice = hashmap[x] as Dices
                setToggledImage(x, dice.rollDice(), true)
            }
    }

    //ändra bilden på tärningen utifrån vyobjekt, nummer och tidigare locked-state
    private fun setToggledImage(view: ImageButton, number: Int,locked:Boolean) {
        if (locked)
            view.setImageResource(whiteResources[number - 1])
        else
            view.setImageResource(greyResources[number - 1])
    }

    //Sparar undan state för spelprogrammet med hjälp av parcelable-objekten dices
    override fun onSaveInstanceState(outState: Bundle) {
        for(i in 1..6){
            outState.putParcelable(i.toString(),hashmap[listOfImageButtons[i-1]])
        }
        outState.putParcelable("game",game)
        super.onSaveInstanceState(outState)
    }

    //laddar tillbaka state i aktiviteten
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        for(i in 1..6){
            hashmap[listOfImageButtons[i-1]] = savedInstanceState.getParcelable<Dices>(i.toString()) as Dices
        }
        game = savedInstanceState.getParcelable("game")!!
        updateSpinner(spinner)
        findViewById<TextView>(R.id.points_show).text = game.points.sum().toString()
        hashmap.keys.forEach{x ->
            val dice:Dices = hashmap[x] as Dices
            setToggledImage(x,dice.activeNumber,!dice.locked)}
        if(game.currentRoll > 2){
            findViewById<Button>(R.id.throw_button).setText(R.string.calculate)
        }
        super.onRestoreInstanceState(savedInstanceState)
    }
}