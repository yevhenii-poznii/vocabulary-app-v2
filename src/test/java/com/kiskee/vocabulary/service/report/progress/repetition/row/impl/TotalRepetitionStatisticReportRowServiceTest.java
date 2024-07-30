package com.kiskee.vocabulary.service.report.progress.repetition.row.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.kiskee.vocabulary.model.dto.report.update.progress.repetition.RepetitionStatisticData;
import com.kiskee.vocabulary.model.entity.report.progress.repetition.DictionaryRepetitionStatisticReport;
import com.kiskee.vocabulary.model.entity.report.progress.repetition.RepetitionStatisticReportRow;
import com.kiskee.vocabulary.model.entity.report.progress.repetition.period.TotalRepetitionStatisticReportRow;
import com.kiskee.vocabulary.util.report.ReportPeriodUtil;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TotalRepetitionStatisticReportRowServiceTest {

    @InjectMocks
    private TotalRepetitionStatisticReportRowService totalRepetitionStatisticReportRowService;

    private static final UUID USER_ID = UUID.fromString("78c87bb3-01b6-41ca-8329-247a72162868");
    private static final String DICTIONARY_NAME = "SomeDictionaryName";

    @ParameterizedTest
    @MethodSource("buildRowFromScratchTestData")
    void
            testBuildRowFromScratch_WhenTotalRowDoesNotExistAndUserWasCreatedBeforeReportStartPeriod_ThenBuildRowFromScratchWithStartPeriod(
                    TestData testData) {
        RepetitionStatisticReportRow totalRow =
                totalRepetitionStatisticReportRowService.buildRowFromScratch(testData.data());

        assertThat(totalRow.getWorkingDays()).isEqualTo(1);
        assertThat(totalRow.getRowPeriod()).isEqualTo(ReportPeriodUtil.TOTAL);
        assertThat(totalRow.getStartPeriod()).isEqualTo(testData.data().getCurrentDate());
        assertThat(totalRow.getEndPeriod()).isEqualTo(testData.data().getCurrentDate());
        assertThat(totalRow.getDictionaryReports()).containsExactly(testData.expectedDictionaryReport());
    }

    @ParameterizedTest
    @MethodSource("buildRowFromScratchTestDataWhenUserCreatedAfterStartPeriod")
    void
            testBuildRowFromScratch_WhenTotalRowDoesNotExistAndUserWasCreatedAfterReportStartPeriod_ThenBuildRowFromScratchWithUserCreatedDateAsStartPeriod(
                    TestData testData) {
        RepetitionStatisticReportRow yearlyRow =
                totalRepetitionStatisticReportRowService.buildRowFromScratch(testData.data());

        assertThat(yearlyRow.getWorkingDays()).isEqualTo(4);
        assertThat(yearlyRow.getRowPeriod()).isEqualTo(ReportPeriodUtil.TOTAL);
        assertThat(yearlyRow.getStartPeriod())
                .isEqualTo(testData.data().getCurrentDate().minusDays(5));
        assertThat(yearlyRow.getEndPeriod()).isEqualTo(testData.data().getCurrentDate());
        assertThat(yearlyRow.getDictionaryReports()).containsExactly(testData.expectedDictionaryReport());
    }

    @ParameterizedTest
    @MethodSource("updateRowForTodayAndExistingDictionaryReport")
    void testUpdateRow_WhenRowExistsForTodayAndGivenDictionaryReportExists_ThenRecalculateRow(TestData testData) {
        DictionaryRepetitionStatisticReport dictionaryReport = new DictionaryRepetitionStatisticReport(
                1L, 10L, DICTIONARY_NAME, 96.429, 56, 79.63, 43, 20.37, 11, 3.571, 2, 54, 1);
        TotalRepetitionStatisticReportRow totalRowForToday = TotalRepetitionStatisticReportRow.builder()
                .id(1L)
                .startPeriod(testData.data().getCurrentDate())
                .endPeriod(testData.data().getCurrentDate())
                .workingDays(4)
                .dictionaryReports(Set.of(dictionaryReport))
                .build();

        RepetitionStatisticReportRow totalRow =
                totalRepetitionStatisticReportRowService.updateRow(totalRowForToday, testData.data());

        assertThat(totalRow.getWorkingDays()).isEqualTo(4);
        assertThat(totalRow.getRowPeriod()).isEqualTo(ReportPeriodUtil.TOTAL);
        assertThat(totalRow.getStartPeriod())
                .isEqualTo(testData.data().getCurrentDate().minusDays(5));
        assertThat(totalRow.getEndPeriod()).isEqualTo(testData.data().getCurrentDate());
        assertThat(totalRow.getDictionaryReports()).containsExactly(testData.expectedDictionaryReport());
    }

    @ParameterizedTest
    @MethodSource("buildRowFromScratchTestData")
    void
            testUpdateRow_WhenRowExistsForTodayAndGivenDictionaryReportDoesNotExist_ThenRecalculateRowWithNewDictionaryReport(
                    TestData testData) {
        DictionaryRepetitionStatisticReport dictionaryReport = new DictionaryRepetitionStatisticReport(
                1L, 5L, DICTIONARY_NAME, 96.429, 56, 79.63, 43, 20.37, 11, 3.571, 2, 54, 1);
        TotalRepetitionStatisticReportRow totalRowForToday = TotalRepetitionStatisticReportRow.builder()
                .id(1L)
                .startPeriod(testData.data().getCurrentDate())
                .endPeriod(testData.data().getCurrentDate())
                .workingDays(1)
                .dictionaryReports(Set.of(dictionaryReport))
                .build();

        RepetitionStatisticReportRow totalRow =
                totalRepetitionStatisticReportRowService.updateRow(totalRowForToday, testData.data());

        assertThat(totalRow.getWorkingDays()).isEqualTo(1);
        assertThat(totalRow.getRowPeriod()).isEqualTo(ReportPeriodUtil.TOTAL);
        assertThat(totalRow.getStartPeriod()).isEqualTo(testData.data().getCurrentDate());
        assertThat(totalRow.getEndPeriod()).isEqualTo(testData.data().getCurrentDate());
        assertThat(totalRow.getDictionaryReports())
                .containsExactlyInAnyOrder(dictionaryReport, testData.expectedDictionaryReport());
    }

    @ParameterizedTest
    @MethodSource("updateRowForNextDayInTheSamePeriod")
    void testUpdateRow_WhenRowExistsForPreviousDayAndCurrentDateInTheSamePeriod_ThenRecalculateRowForNewDay(
            TestData testData) {
        DictionaryRepetitionStatisticReport dictionaryReport = new DictionaryRepetitionStatisticReport(
                1L, 10L, DICTIONARY_NAME, 96.429, 56, 79.63, 43, 20.37, 11, 3.571, 2, 54, 1);
        TotalRepetitionStatisticReportRow totalRowForToday = TotalRepetitionStatisticReportRow.builder()
                .id(1L)
                .startPeriod(testData.data().getCurrentDate().minusDays(5))
                .endPeriod(testData.data().getCurrentDate().minusDays(1))
                .workingDays(1)
                .dictionaryReports(Set.of(dictionaryReport))
                .build();

        RepetitionStatisticReportRow totalRow =
                totalRepetitionStatisticReportRowService.updateRow(totalRowForToday, testData.data());

        assertThat(totalRow.getWorkingDays()).isEqualTo(5);
        assertThat(totalRow.getRowPeriod()).isEqualTo(ReportPeriodUtil.TOTAL);
        assertThat(totalRow.getStartPeriod())
                .isEqualTo(testData.data().getCurrentDate().minusDays(6));
        assertThat(totalRow.getEndPeriod()).isEqualTo(testData.data().getCurrentDate());
        assertThat(totalRow.getDictionaryReports()).containsExactlyInAnyOrder(testData.expectedDictionaryReport());
    }

    @ParameterizedTest
    @MethodSource("updateRowForNextDayInTheSamePeriodAndForNewDictionaryId")
    void
            testUpdateRow_WhenRowExistsForPreviousDayAndCurrentDateInTheSamePeriodButGivenNewDictionaryId_ThenRecalculateRowForNewDay(
                    TestData testData) {
        DictionaryRepetitionStatisticReport dictionaryReport = new DictionaryRepetitionStatisticReport(
                1L, 5L, DICTIONARY_NAME, 96.429, 56, 79.63, 43, 20.37, 11, 3.571, 2, 54, 1);
        TotalRepetitionStatisticReportRow totalRowForToday = TotalRepetitionStatisticReportRow.builder()
                .id(1L)
                .startPeriod(testData.data().getCurrentDate().minusDays(5))
                .endPeriod(testData.data().getCurrentDate().minusDays(1))
                .workingDays(4)
                .dictionaryReports(Set.of(dictionaryReport))
                .build();

        RepetitionStatisticReportRow totalRow =
                totalRepetitionStatisticReportRowService.updateRow(totalRowForToday, testData.data());

        assertThat(totalRow.getWorkingDays()).isEqualTo(5);
        assertThat(totalRow.getRowPeriod()).isEqualTo(ReportPeriodUtil.TOTAL);
        assertThat(totalRow.getStartPeriod())
                .isEqualTo(testData.data().getCurrentDate().minusDays(6));
        assertThat(totalRow.getEndPeriod()).isEqualTo(testData.data().getCurrentDate());
        assertThat(totalRow.getDictionaryReports())
                .containsExactlyInAnyOrder(dictionaryReport, testData.expectedDictionaryReport());
    }

    private static Stream<TestData> buildRowFromScratchTestData() {
        LocalDate userCreatedAt = LocalDate.of(2024, 5, 9);
        LocalDate currentDate = LocalDate.of(2024, 5, 9);
        return buildTestDataFromScratch(userCreatedAt, currentDate);
    }

    private static Stream<TestData> buildRowFromScratchTestDataWhenUserCreatedAfterStartPeriod() {
        LocalDate userCreatedAt = LocalDate.of(2024, 5, 9);
        LocalDate currentDate = LocalDate.of(2024, 5, 14);
        return buildTestDataFromScratch(userCreatedAt, currentDate);
    }

    private static Stream<TestData> updateRowForTodayAndExistingDictionaryReport() {
        LocalDate userCreatedAt = LocalDate.of(2024, 5, 9);
        LocalDate currentDate = LocalDate.of(2024, 5, 14);
        return buildTestDataForUpdate(userCreatedAt, currentDate);
    }

    private static Stream<TestData> updateRowForNextDayInTheSamePeriod() {
        LocalDate userCreatedAt = LocalDate.of(2024, 5, 9);
        LocalDate currentDate = LocalDate.of(2024, 5, 15);
        return buildTestDataForUpdate(userCreatedAt, currentDate);
    }

    private static Stream<TestData> updateRowForNextDayInTheSamePeriodAndForNewDictionaryId() {
        LocalDate userCreatedAt = LocalDate.of(2024, 5, 9);
        LocalDate currentDate = LocalDate.of(2024, 5, 15);
        return buildTestDataFromScratch(userCreatedAt, currentDate);
    }

    private static Stream<TestData> buildTestDataFromScratch(LocalDate userCreatedAt, LocalDate currentDate) {
        return Stream.of(
                new TestData(
                        new RepetitionStatisticData(
                                USER_ID, 10L, DICTIONARY_NAME, userCreatedAt, currentDate, 43, 11, 2, 56, 54),
                        new DictionaryRepetitionStatisticReport(
                                null, 10L, DICTIONARY_NAME, 96.429, 56, 79.63, 43, 20.37, 11, 3.571, 2, 54, 1)),
                new TestData(
                        new RepetitionStatisticData(
                                USER_ID, 10L, DICTIONARY_NAME, userCreatedAt, currentDate, 13, 15, 6, 34, 28),
                        new DictionaryRepetitionStatisticReport(
                                null, 10L, DICTIONARY_NAME, 82.353, 34, 46.429, 13, 53.571, 15, 17.647, 6, 28, 1)));
    }

    private static Stream<TestData> buildTestDataForUpdate(LocalDate userCreatedAt, LocalDate currentDate) {
        return Stream.of(
                new TestData(
                        new RepetitionStatisticData(
                                USER_ID, 10L, DICTIONARY_NAME, userCreatedAt, currentDate, 67, 21, 9, 97, 88),
                        new DictionaryRepetitionStatisticReport(
                                1L, 10L, DICTIONARY_NAME, 92.81, 153, 77.465, 110, 22.535, 32, 7.19, 11, 142, 2)),
                new TestData(
                        new RepetitionStatisticData(
                                USER_ID, 10L, DICTIONARY_NAME, userCreatedAt, currentDate, 10, 23, 5, 38, 33),
                        new DictionaryRepetitionStatisticReport(
                                1L, 10L, DICTIONARY_NAME, 92.553, 94, 60.92, 53, 39.08, 34, 7.447, 7, 87, 2)));
    }

    private record TestData(
            RepetitionStatisticData data, DictionaryRepetitionStatisticReport expectedDictionaryReport) {}
}
