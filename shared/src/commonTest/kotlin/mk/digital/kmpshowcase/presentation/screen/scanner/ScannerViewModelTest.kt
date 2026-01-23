package mk.digital.kmpshowcase.presentation.screen.scanner

import mk.digital.kmpshowcase.presentation.component.barcode.CodeFormat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ScannerViewModelTest {

    // === Default State Tests ===

    @Test
    fun `default state has mode index 0`() {
        val state = ScannerUiState()
        assertEquals(0, state.selectedModeIndex)
    }

    @Test
    fun `default state has format index 0`() {
        val state = ScannerUiState()
        assertEquals(0, state.selectedFormatIndex)
    }

    @Test
    fun `default state has QR_CODE format`() {
        val state = ScannerUiState()
        assertEquals(CodeFormat.QR_CODE, state.selectedFormat)
    }

    @Test
    fun `default state has empty input text`() {
        val state = ScannerUiState()
        assertEquals("", state.inputText)
    }

    @Test
    fun `default state has null generated bitmap`() {
        val state = ScannerUiState()
        assertNull(state.generatedBitmap)
    }

    @Test
    fun `default state has null scanned result`() {
        val state = ScannerUiState()
        assertNull(state.scannedResult)
    }

    // === ScannerUiState Tests ===

    @Test
    fun `ScannerUiState can hold mode index`() {
        val state = ScannerUiState(selectedModeIndex = 1)
        assertEquals(1, state.selectedModeIndex)
    }

    @Test
    fun `ScannerUiState can hold format index`() {
        val state = ScannerUiState(selectedFormatIndex = 1)
        assertEquals(1, state.selectedFormatIndex)
    }

    @Test
    fun `ScannerUiState can hold BARCODE format`() {
        val state = ScannerUiState(selectedFormat = CodeFormat.BARCODE)
        assertEquals(CodeFormat.BARCODE, state.selectedFormat)
    }

    @Test
    fun `ScannerUiState can hold input text`() {
        val state = ScannerUiState(inputText = "Test text")
        assertEquals("Test text", state.inputText)
    }

    @Test
    fun `ScannerUiState can hold scanned result`() {
        val state = ScannerUiState(scannedResult = "Scanned: ABC123")
        assertEquals("Scanned: ABC123", state.scannedResult)
    }

    // === CodeFormat Tests ===

    @Test
    fun `CodeFormat has QR_CODE value`() {
        assertEquals(CodeFormat.QR_CODE, CodeFormat.valueOf("QR_CODE"))
    }

    @Test
    fun `CodeFormat has BARCODE value`() {
        assertEquals(CodeFormat.BARCODE, CodeFormat.valueOf("BARCODE"))
    }

    @Test
    fun `CodeFormat BARCODE is at ordinal 0`() {
        assertEquals(0, CodeFormat.BARCODE.ordinal)
    }

    @Test
    fun `CodeFormat QR_CODE is at ordinal 1`() {
        assertEquals(1, CodeFormat.QR_CODE.ordinal)
    }

    // === State Transitions (data class copy tests) ===

    @Test
    fun `mode change updates state correctly`() {
        val initial = ScannerUiState()
        val updated = initial.copy(selectedModeIndex = 1, scannedResult = null)

        assertEquals(1, updated.selectedModeIndex)
        assertNull(updated.scannedResult)
        assertEquals(initial.selectedFormatIndex, updated.selectedFormatIndex)
    }

    @Test
    fun `format change updates state correctly`() {
        val initial = ScannerUiState()
        val updated = initial.copy(
            selectedFormatIndex = 1,
            selectedFormat = CodeFormat.BARCODE,
            generatedBitmap = null
        )

        assertEquals(1, updated.selectedFormatIndex)
        assertEquals(CodeFormat.BARCODE, updated.selectedFormat)
        assertNull(updated.generatedBitmap)
    }

    @Test
    fun `text change updates state correctly`() {
        val initial = ScannerUiState()
        val updated = initial.copy(inputText = "New text", generatedBitmap = null)

        assertEquals("New text", updated.inputText)
        assertNull(updated.generatedBitmap)
    }

    @Test
    fun `scan result updates state correctly`() {
        val initial = ScannerUiState()
        val updated = initial.copy(scannedResult = "Scanned value")

        assertEquals("Scanned value", updated.scannedResult)
    }

    @Test
    fun `clear scanned result updates state correctly`() {
        val initial = ScannerUiState(scannedResult = "Some result")
        val updated = initial.copy(scannedResult = null)

        assertNull(updated.scannedResult)
    }
}
