package mk.digital.kmpshowcase.data.dto

import mk.digital.kmpshowcase.data.repository.dto.DTOTestRunner
import kotlin.test.Test
import kotlin.test.assertEquals

class AddressDTOTest : DTOTestRunner() {

    @Test
    fun testSuccess() = runTest<AddressDTO>(
        """
    {
      "city": "city",
      "street": "street",
      "suite": "suite",
      "zipcode": "zipcode"
    }
    """
    ) {
        assertEquals(
            AddressDTO("city", "street", "suite", "zipcode"),
            it
        )
    }
}
