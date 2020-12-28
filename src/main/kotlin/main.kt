const val CARD_DAY_LIMIT = 15_000_000
const val CARD_MONTH_LIMIT = 60_000_000
const val MIN_WITHOUT_COMISSION_VALUE = 30_000
const val MAX_WITHOUT_COMISSION_VALUE = 7_500_000
const val MAESTRO_PERCENT = 0.006
const val MAESTRO_FIX_COMISSION = 2_000
const val VISA_FIX_COMISSION = 3_500
const val VISA_PERCENT = 0.0075
const val VK_DAY_LIMIT = 1_500_000
const val VK_MONTH_LIMIT = 4_000_000

fun main() {
    print("Введите тип вашей банковской карты: 1 - Mastercard/Maestro, 2 - Visa/Мир, 3 - VkPay: ")
    val cardType = readLine()!!.toInt()

    if (cardType < 1 || cardType > 3) {
        println("Тип карты должен быть числом 1...3")
        return
    }

    var totalTransfers = 0
    while(true) {
        print("Введите сумму перевода, коп. (пустая строка - выход): ")
        val transfer = readLine()
        if(transfer!!.isEmpty()) {
            return
        }

        val transferValue = transfer.toInt()
        if(!checkLimits(cardType, totalTransfers, transferValue)) {
            println("Превышен лимит по переводам. Попробуйте другую карту.")
            return
        }

        val comission = getComission(cardType, totalTransfers, transferValue)
        println("Комиссия за перевод составит: $comission коп.")

        totalTransfers += transferValue
    }

}

fun checkLimits(cardType: Int = 3, totalTransfers: Int, transferValue: Int): Boolean {
    val futureTotalTransfers = totalTransfers + transferValue

    return when {
        cardType < 3 && transferValue <= CARD_DAY_LIMIT && futureTotalTransfers <= CARD_MONTH_LIMIT -> true
        cardType == 3 && transferValue <= VK_DAY_LIMIT && futureTotalTransfers <= VK_MONTH_LIMIT -> true
        else -> false
    }
}

fun getComission(cardType: Int = 3, totalTransfers: Int, transferValue: Int): Int {
    val futureTotalTransfers = totalTransfers + transferValue

    return when(cardType) {
        1 -> getMaestroComission(futureTotalTransfers, transferValue)
        2 -> getVisaComission(transferValue)
        else -> 0
    }
}

fun getMaestroComission(totalTransfers: Int, transferValue: Int): Int {
    val withoutComission = totalTransfers in MIN_WITHOUT_COMISSION_VALUE..MAX_WITHOUT_COMISSION_VALUE

    return if (withoutComission) 0
    else (transferValue * MAESTRO_PERCENT + MAESTRO_FIX_COMISSION).toInt()
}

fun getVisaComission(transferValue: Int): Int {
    val moreWhenFixMinimalComission = (transferValue * VISA_PERCENT).toInt() > VISA_FIX_COMISSION

    return if (moreWhenFixMinimalComission) (transferValue * VISA_PERCENT).toInt()
    else VISA_FIX_COMISSION
}
