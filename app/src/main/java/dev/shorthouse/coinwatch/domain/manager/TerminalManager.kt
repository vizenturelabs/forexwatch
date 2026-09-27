package com.vizenture.forexwatch.domain.manager

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalManager @Inject constructor() {
    @Volatile
    private var activeTerminalId: Int = 1 // Default to Terminal 1

    fun getActiveTerminalId(): String = activeTerminalId.toString()

    fun setActiveTerminalId(terminalId: Int) {
        this.activeTerminalId = terminalId
    }
}
