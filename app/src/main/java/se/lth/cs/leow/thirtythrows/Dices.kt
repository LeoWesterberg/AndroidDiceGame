package se.lth.cs.leow.thirtythrows
import android.os.Parcel
import android.os.Parcelable
import kotlin.random.Random

class Dices():Parcelable {
    //En simpel modell för en tärning som även implementerar Parcelable
    //så att man kan spara undan ett objekt i onSaveInstanceState.
    //Har variabler för tärningsnummer och om den där i låst tillstånd eller ej.
    var activeNumber:Int = 1
    var locked: Boolean = false

    constructor(parcel: Parcel) : this() {
        activeNumber = parcel.readInt()
        locked = parcel.readByte() != 0.toByte()
    }
    //metod för att slå tärningen
    fun rollDice(): Int{
        if(!locked){
            activeNumber = Random.nextInt(1, 7)
        }
        else locked = false
        return activeNumber
    }

    fun switchLock(){
        locked = !locked
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(activeNumber)
        parcel.writeByte(if (locked) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Dices> {
        override fun createFromParcel(parcel: Parcel): Dices {
            return Dices(parcel)
        }

        override fun newArray(size: Int): Array<Dices?> {
            return arrayOfNulls(size)
        }
    }

}