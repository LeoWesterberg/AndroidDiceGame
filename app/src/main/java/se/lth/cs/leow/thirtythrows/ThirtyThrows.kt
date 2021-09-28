package se.lth.cs.leow.thirtythrows

import android.os.Parcel
import android.os.Parcelable

class ThirtyThrows() :Parcelable{
    //Attribut som behövs för att modellera spelet
    var dices:List<Dices> = List(6) {Dices()}
    var round = 1
    var remaindingChoices:MutableList<String> = mutableListOf("Low","4","5","6","7","8","9","10","11","12");
    var currentRoll = 1
    var points = mutableListOf<Int>()
    var choices = mutableListOf<String>()

    constructor(parcel: Parcel) : this() {
        dices = parcel.createTypedArrayList(Dices)!!
        round = parcel.readInt()
        currentRoll = parcel.readInt()
    }

    //Algorithm som beräknar score genom att den rekursivt slår ihop de tärningar som ger score
    fun calculateScore(choice:Int):Int{
        val values:MutableList<Int> = dices.map { x -> x.activeNumber }.sortedDescending().toMutableList()
        return initCalculateScore(choice,values,mutableListOf<Int>(),0)
    }
    //hjälpmetod till calculate score
    private fun initCalculateScore(choice:Int,values:MutableList<Int>,picked:MutableList<Int>, index:Int):Int{
        if(choice == 3){
            return values.filter { x -> x <= 3 }.sum()
        }
        val sum:Int = picked.sum()
        if(sum == choice) {
            for (i in picked)
                values.remove(i)
            return choice + initCalculateScore(choice, values, mutableListOf<Int>(), 0)
        }
        return if(sum > choice || index == values.size) {
            0
        } else {
            picked += values[index]
            //Fallet då vi tar med den största tärningen som ej tidigare har inkluderats i beräkningarna
            val nonSkipped = initCalculateScore(choice, values, picked, index + 1)
            //If-sats för mindre beräkningar om vi inte hittar ett tal som matchar choice
            // om vi inkluderade talet ovan.

            var skipped: Int = 0
            if(nonSkipped == 0){
                picked.remove(values[index])
                skipped = initCalculateScore(choice, values, picked, index + 1)
            }
            if(skipped < nonSkipped) nonSkipped else skipped

        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(dices)
        parcel.writeInt(round)
        parcel.writeInt(currentRoll)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ThirtyThrows> {
        override fun createFromParcel(parcel: Parcel): ThirtyThrows {
            return ThirtyThrows(parcel)
        }

        override fun newArray(size: Int): Array<ThirtyThrows?> {
            return arrayOfNulls(size)
        }
    }
}