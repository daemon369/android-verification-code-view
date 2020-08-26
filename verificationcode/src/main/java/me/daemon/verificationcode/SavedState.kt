package me.daemon.verificationcode

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.annotation.RequiresApi

/**
 * @author daemon
 * @since 2019-06-27 15:55
 */
internal class SavedState : View.BaseSavedState {

    var str = ""
        private set

    constructor(source: Parcel?) : super(source) {
        str = source?.readString() ?: ""
    }

    @RequiresApi(Build.VERSION_CODES.N)
    constructor(source: Parcel?, loader: ClassLoader?) : super(source, loader) {
        str = source?.readString() ?: ""
    }

    constructor(superState: Parcelable?) : super(superState)

    fun str(str: String) = apply { this.str = str }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(str)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<SavedState> {
        override fun createFromParcel(parcel: Parcel): SavedState = SavedState(parcel)
        override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
    }

}