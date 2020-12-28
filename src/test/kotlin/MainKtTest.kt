import org.junit.Test

import org.junit.Assert.*

const val CARD_TYPE_MAESTRO = 1
const val CARD_TYPE_VISA = 2
const val CARD_TYPE_VK = 3

class MainKtTest {
    @Test
    fun checkLimits_maestro_in_limit() {
        val actual = checkLimits(CARD_TYPE_MAESTRO, 55_000_000, 1_000_000)

        assertTrue("Ожидалось, что платёж будет в пределах лимита.", actual)
    }

    @Test
    fun checkLimits_maestro_over_limit_1() {
        val actual = checkLimits(CARD_TYPE_MAESTRO, 55_000_000, 6_000_000)

        assertFalse("Ожидалось, что платёж превысит лимит по данной карте.", actual)
    }

    @Test
    fun checkLimits_maestro_over_limit_2() {
        val actual = checkLimits(CARD_TYPE_MAESTRO, 6_000_000, 55_000_000)

        assertFalse("Ожидалось, что платёж превысит лимит по данной карте.", actual)
    }

    @Test
    fun checkLimits_maestro_check_border_limit() {
        val actual = checkLimits(CARD_TYPE_MAESTRO, 55_000_000, 5_000_000)

        assertTrue("Ожидалось, что платёж размером с максимальный лимит не превысит лимит по карте.", actual)
    }

    @Test
    fun checkLimits_visa_in_limit() {
        val actual = checkLimits(CARD_TYPE_VISA, 55_000_000, 1_000_000)

        assertTrue("Ожидалось, что платёж будет в пределах лимита.", actual)
    }

    @Test
    fun checkLimits_visa_over_limit_1() {
        val actual = checkLimits(CARD_TYPE_VISA, 55_000_000, 6_000_000)

        assertFalse("Ожидалось, что платёж превысит лимит по данной карте.", actual)
    }

    @Test
    fun checkLimits_visa_over_limit_2() {
        val actual = checkLimits(CARD_TYPE_VISA, 6_000_000, 55_000_000)

        assertFalse("Ожидалось, что платёж превысит лимит по данной карте.", actual)
    }

    @Test
    fun checkLimits_visa_check_border_limit() {
        val actual = checkLimits(CARD_TYPE_VISA, 55_000_000, 5_000_000)

        assertTrue("Ожидалось, что платёж размером с максимальный лимит не превысит лимит по карте.", actual)
    }

    @Test
    fun checkLimits_vk_in_limit() {
        val actual = checkLimits(CARD_TYPE_VK, 1_000_000, 100_000)

        assertTrue("Ожидалось, что платёж будет в пределах лимита.", actual)
    }

    @Test
    fun checkLimits_vk_over_limit_1() {
        val actual = checkLimits(CARD_TYPE_VK, 3_900_000, 200_000)

        assertFalse("Ожидалось, что платёж превысит лимит по данной карте.", actual)
    }

    @Test
    fun checkLimits_vk_over_limit_2() {
        val actual = checkLimits(CARD_TYPE_VK, 200_000, 3_900_000)

        assertFalse("Ожидалось, что платёж превысит лимит по данной карте.", actual)
    }

    @Test
    fun checkLimits_vk_check_border_limit() {
        val actual = checkLimits(CARD_TYPE_VK, 1_000_000, 500_000)

        assertTrue("Ожидалось, что платёж размером с максимальный лимит не превысит лимит по карте.", actual)
    }

    @Test
    fun checkLimits_default_values() {
        val totalTransfers = 1_000_000
        val transferValue = 500_000

        val expected = checkLimits(CARD_TYPE_VK, totalTransfers, transferValue)
        val actual = checkLimits(totalTransfers = totalTransfers, transferValue = 500_000)

        assertEquals("Ожидалось, что значением по умолчанию будет ВК-карты.", expected, actual)
    }

    @Test
    fun getComission_maestro_with_comission_less_when_min() {
        val transferValue = 10_000

        val expected = (transferValue * 0.006 + 2_000).toInt()
        val actual = getComission(CARD_TYPE_MAESTRO, 0, transferValue)

        assertEquals("Ожидалось, что при платеже, меньшем, чем нижняя граница, " +
                "комиссия составит процент + фиксированная сумму.", expected, actual)
    }

    @Test
    fun getComission_maestro_with_comission_more_when_max() {
        val transferValue = 8_000_000

        val expected = (transferValue * 0.006 + 2_000).toInt()
        val actual = getComission(CARD_TYPE_MAESTRO, 0, transferValue)

        assertEquals("Ожидалось, что при платеже, большем, чем верхняя граница, " +
                "комиссия составит процент + фиксированная сумму.", expected, actual)
    }

    @Test
    fun getComission_maestro_without_comission() {
        val transferValue = 6_000_000

        val expected = 0
        val actual = getComission(CARD_TYPE_MAESTRO, 0, transferValue)

        assertEquals("Ожидалось, что при платеже в определённых границах комиссии не будет.", expected, actual)
    }

    @Test
    fun getComission_visa_less_then_minimal_comission() {
        val transferValue = 1_000

        val expected = 3_500
        val actual = getComission(CARD_TYPE_VISA, 0, transferValue)

        assertEquals("Ожидалось, что комиссия составит минимальное значение.", expected, actual)
    }

    @Test
    fun getComission_visa_equals_minimal_comission() {
        val transferValue = 3_500

        val expected = 3_500
        val actual = getComission(CARD_TYPE_VISA, 0, transferValue)

        assertEquals("Ожидалось, что комиссия составит минимальное значение.", expected, actual)
    }

    @Test
    fun getComission_visa_more_minimal_comission() {
        val transferValue = 5_000_000

        val expected = (transferValue * 0.0075).toInt()
        val actual = getComission(CARD_TYPE_VISA, 0, transferValue)

        assertEquals("Ожидалось, что комиссия составит процент (0.75%) от платежа.", expected, actual)
    }

    @Test
    fun getComission_vk() {
        val transferValue = 5_000_000

        val expected = 0
        val actual = getComission(CARD_TYPE_VK, transferValue, transferValue)

        assertEquals("Ожидалось, что для ВК-карты комиссии не будет.", expected, actual)
    }

    @Test
    fun getComission_default_values() {
        val totalTransfers = 5_000_000
        val transferValue = 5_000_000

        val expected = getComission(CARD_TYPE_VK, totalTransfers, transferValue)
        val actual = getComission(totalTransfers = totalTransfers, transferValue = transferValue)

        assertEquals("Ожидалось, что начением по умолчанию будет ВК-карта.", expected, actual)
    }

    @Test
    fun getMaestroComission_with_comission_less_when_min() {
        val transferValue = 10_000

        val expected = (transferValue * 0.006 + 2_000).toInt()
        val actual = getMaestroComission(0, transferValue)

        assertEquals("Ожидалось, что при платеже, меньшем, чем нижняя граница, " +
                "комиссия составит процент + фиксированная сумму.", expected, actual)
    }

    @Test
    fun getMaestroComission_with_comission_more_when_max() {
        val transferValue = 8_000_000

        val expected = (transferValue * 0.006 + 2_000).toInt()
        val actual = getMaestroComission(0, transferValue)

        assertEquals("Ожидалось, что при платеже, большем, чем верхняя граница, " +
                "комиссия составит процент + фиксированная сумму.", expected, actual)
    }

    @Test
    fun getMaestroComission_without_comission() {
        val totalTransfers = 6_000_000

        val expected = 0
        val actual = getMaestroComission(totalTransfers, 1_000)

        assertEquals("Ожидалось, что при платеже в определённых границах комиссии не будет.", expected, actual)
    }

    @Test
    fun getVisaComission_less_then_minimal_comission() {
        val transferValue = 1_000

        val expected = 3_500
        val actual = getVisaComission(transferValue)

        assertEquals("Ожидалось, что комиссия составит минимальное значение.", expected, actual)
    }

    @Test
    fun getVisaComission_equals_minimal_comission() {
        val transferValue = 3_500

        val expected = 3_500
        val actual = getVisaComission(transferValue)

        assertEquals("Ожидалось, что комиссия составит минимальное значение.", expected, actual)
    }

    @Test
    fun getVisaComission_more_minimal_comission() {
        val transferValue = 5_000_000

        val expected = (transferValue * 0.0075).toInt()
        val actual = getVisaComission(transferValue)

        assertEquals("Ожидалось, что комиссия составит процент (0.75%) от платежа.", expected, actual)
    }
}