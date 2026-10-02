package com.jwoglom.pumpx2.pump.bluetooth;

import static com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent.QualifyingEvent.ALERT;
import static com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent.QualifyingEvent.BOLUS_CHANGE;
import static com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent.QualifyingEvent.EXTENDED_BOLUS_CHANGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.response.qualifyingEvent.QualifyingEvent;
import com.welie.blessed.BondState;

import org.junit.Test;

import java.util.Set;

public class TandemBluetoothHandlerTest {
    @Test
    public void testNormalizeQualifyingEvents_prefersBolusChangeOverExtendedBolusChange() {
        assertEquals(
                Set.of(ALERT, BOLUS_CHANGE),
                TandemBluetoothHandler.normalizeQualifyingEvents(Set.of(ALERT, BOLUS_CHANGE, EXTENDED_BOLUS_CHANGE)));
    }

    @Test
    public void testNormalizeQualifyingEvents_preservesExtendedBolusChangeWhenBolusChangeMissing() {
        assertEquals(
                Set.of(ALERT, EXTENDED_BOLUS_CHANGE),
                TandemBluetoothHandler.normalizeQualifyingEvents(Set.of(ALERT, EXTENDED_BOLUS_CHANGE)));
    }

    @Test
    public void testInitialAuthNoReply_defaultPolicyPausesAtQuickRetryLimit() {
        assertFalse(TandemBluetoothHandler.shouldPauseInitialAuthNoReplyRetries(2, 3, false));
        assertTrue(TandemBluetoothHandler.shouldPauseInitialAuthNoReplyRetries(3, 3, false));
    }

    @Test
    public void testInitialAuthNoReply_backgroundRecoveryContinuesWithCooldown() {
        assertFalse(TandemBluetoothHandler.shouldPauseInitialAuthNoReplyRetries(3, 3, true));
        assertEquals(
                15_000L,
                TandemBluetoothHandler.initialAuthNoReplyReconnectDelayMs(
                        3, 3, true, 250L, 15_000L));
    }

    @Test
    public void testInitialAuthNoReply_quickRetriesRetainShortDelay() {
        assertEquals(
                250L,
                TandemBluetoothHandler.initialAuthNoReplyReconnectDelayMs(
                        2, 3, true, 250L, 15_000L));
    }

    @Test
    public void testReconnectBackoff_isBounded() {
        assertEquals(375L, TandemBluetoothHandler.nextReconnectDelayMs(250L));
        assertEquals(30_000L, TandemBluetoothHandler.nextReconnectDelayMs(30_000L));
        assertEquals(30_000L, TandemBluetoothHandler.nextReconnectDelayMs(Long.MAX_VALUE));
    }

    @Test
    public void testTerminalFailureRetry_requiresEstablishedMatchingPump() {
        assertTrue(TandemBluetoothHandler.isEstablishedPumpReconnectEligible(
                BondState.BONDED, "AA:BB:CC:DD:EE:FF", "aa:bb:cc:dd:ee:ff"));
        assertFalse(TandemBluetoothHandler.isEstablishedPumpReconnectEligible(
                BondState.NONE, "AA:BB:CC:DD:EE:FF", "AA:BB:CC:DD:EE:FF"));
        assertFalse(TandemBluetoothHandler.isEstablishedPumpReconnectEligible(
                BondState.BONDED, "AA:BB:CC:DD:EE:FF", "11:22:33:44:55:66"));
        assertFalse(TandemBluetoothHandler.isEstablishedPumpReconnectEligible(
                BondState.BONDED, null, "AA:BB:CC:DD:EE:FF"));
    }

    @Test
    public void testConnectionAttemptStaleness_usesElapsedThreshold() {
        assertFalse(TandemBluetoothHandler.isConnectionAttemptStale(1_000L, 20_999L, 20_000L));
        assertTrue(TandemBluetoothHandler.isConnectionAttemptStale(1_000L, 21_000L, 20_000L));
        assertFalse(TandemBluetoothHandler.isConnectionAttemptStale(0L, 99_000L, 20_000L));
        assertFalse(TandemBluetoothHandler.isConnectionAttemptStale(10_000L, 9_000L, 20_000L));
    }
}
