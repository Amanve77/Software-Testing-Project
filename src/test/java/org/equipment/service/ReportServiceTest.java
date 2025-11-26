package org.equipment.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.equipment.data.DataStore;
import org.equipment.domain.Rental;
import org.equipment.support.TestDataSupport;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReportServiceTest {
    private final RentalService rentalService = new RentalService();
    private final ReportService reportService = new ReportService();

    @BeforeClass
    public static void setupStore() {
        TestDataSupport.initStore();
    }

    @Before
    public void resetStore() {
        TestDataSupport.resetStore();
    }

    @Test
    public void totalsReflectActiveRentals() {
        rentalService.rent(1000, 5000, 2);
        assertTrue(reportService.totalHeldDeposits() > 0);
        assertEquals(0.0, reportService.totalLateFeesCollected(), 0.0001);
    }

    @Test
    public void overdueByCategorySummarizesCounts() {
        Rental rental = rentalService.rent(1000, 5000, 1);
        rental.setDueDate(LocalDate.now().minusDays(1));
        DataStore.persistRentals();
        Map<String, Long> overdue = reportService.overdueByCategory();
        assertTrue(overdue.containsKey("Camera"));
        assertEquals(Long.valueOf(1), overdue.get("Camera"));
    }

    @Test
    public void topCustomersReturnsSummaries() {
        rentalService.rent(1000, 5000, 1);
        rentalService.rent(1001, 5000, 1);

        List<ReportService.CustomerSummary> summaries = reportService.topCustomersByRentals(5);
        assertFalse(summaries.isEmpty());
        assertEquals(5000, summaries.get(0).getCustomerId());
        assertTrue(summaries.get(0).getRentals() >= 2);
    }
}



