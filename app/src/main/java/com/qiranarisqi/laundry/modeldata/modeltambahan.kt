package com.qiranarisqi.laundry.modeldata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class modeltambahan(
    val idtambahan: String? = null,
    val namatambahan: String? = null,
    val hargatambahan: Int? = null, // ubah dari String? ke Int?
    val cabangtambahan: String? = null,
    val statustambahan: String? = null
) : Parcelable
