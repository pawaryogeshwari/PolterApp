package com.polter.mobipolter.activities.model

import java.io.Serializable

data class BankEntity(
        var bankAccountName : String,
        var bankAccountNo : String,
        var ifscBankCode : String,
        var bankOtherInfo : String
):Serializable
