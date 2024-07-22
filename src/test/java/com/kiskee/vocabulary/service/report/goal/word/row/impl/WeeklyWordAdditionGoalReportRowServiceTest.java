package com.kiskee.vocabulary.service.report.goal.word.row.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.kiskee.vocabulary.model.dto.report.goal.WordAdditionData;
import com.kiskee.vocabulary.model.entity.report.word.DictionaryWordAdditionGoalReport;
import com.kiskee.vocabulary.model.entity.report.word.WordAdditionGoalReportRow;
import com.kiskee.vocabulary.model.entity.report.word.period.WeeklyWordAdditionGoalReportRow;
import com.kiskee.vocabulary.util.report.ReportPeriodUtil;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WeeklyWordAdditionGoalReportRowServiceTest {

    @InjectMocks
    private WeeklyWordAdditionGoalReportRowService weeklyWordAdditionGoalReportRowService;

    private static final UUID USER_ID = UUID.fromString("78c87bb3-01b6-41ca-8329-247a72162868");

    @ParameterizedTest
    @MethodSource("buildRowFromScratchTestData")
    void
            testBuildRowFromScratch_WhenWeeklyRowDoesNotExistAndUserWasCreatedBeforeReportStartPeriod_ThenBuildRowFromScratchWithStartPeriod(
                    TestData testData) {
        WordAdditionGoalReportRow weeklyRow =
                weeklyWordAdditionGoalReportRowService.buildRowFromScratch(testData.data());

        assertThat(weeklyRow.getWorkingDays()).isEqualTo(1);
        assertThat(weeklyRow.getRowPeriod()).isEqualTo(ReportPeriodUtil.WEEK);
        assertThat(weeklyRow.getStartPeriod()).isEqualTo(testData.data().currentDate());
        assertThat(weeklyRow.getEndPeriod()).isEqualTo(testData.data().currentDate());
        assertThat(weeklyRow.getDictionaryReports()).containsExactly(testData.expectedDictionaryGoalReport());
    }

    @ParameterizedTest
    @MethodSource("buildRowFromScratchTestDataWhenUserCreatedAfterStartPeriod")
    void
            testBuildRowFromScratch_WhenWeeklyRowDoesNotExistAndUserWasCreatedAfterReportStartPeriod_ThenBuildRowFromScratchWithUserCreatedDateAsStartPeriod(
                    TestData testData) {
        WordAdditionGoalReportRow weeklyRow =
                weeklyWordAdditionGoalReportRowService.buildRowFromScratch(testData.data());

        assertThat(weeklyRow.getWorkingDays()).isEqualTo(1);
        assertThat(weeklyRow.getRowPeriod()).isEqualTo(ReportPeriodUtil.WEEK);
        assertThat(weeklyRow.getStartPeriod()).isEqualTo(testData.data().currentDate());
        assertThat(weeklyRow.getEndPeriod()).isEqualTo(testData.data().currentDate());
        assertThat(weeklyRow.getDictionaryReports()).containsExactly(testData.expectedDictionaryGoalReport());
    }

    @ParameterizedTest
    @MethodSource("buildRowFromScratchTestDataWhenWasSkippedFewDaysFromStartPeriod")
    void
            testBuildRowFromScratch_WhenWeeklyRowDoesNotExistAndWasSkippedFewDaysFromStartPeriod_ThenBuildRowFromScratchWithSkippedDays(
                    TestData testData) {
        WordAdditionGoalReportRow weeklyRow =
                weeklyWordAdditionGoalReportRowService.buildRowFromScratch(testData.data());

        assertThat(weeklyRow.getWorkingDays()).isEqualTo(3);
        assertThat(weeklyRow.getRowPeriod()).isEqualTo(ReportPeriodUtil.WEEK);
        assertThat(weeklyRow.getStartPeriod())
                .isEqualTo(testData.data().currentDate().minusDays(2));
        assertThat(weeklyRow.getEndPeriod()).isEqualTo(testData.data().currentDate());
        assertThat(weeklyRow.getDictionaryReports()).containsExactly(testData.expectedDictionaryGoalReport());
    }

    @ParameterizedTest
    @MethodSource("updateRowForTodayAndExistingDictionaryReport")
    void testUpdateRow_WhenRowExistsForTodayAndGivenDictionaryReportExists_ThenRecalculateRow(TestData testData) {
        DictionaryWordAdditionGoalReport dictionaryGoalReport =
                new DictionaryWordAdditionGoalReport(1L, 10L, 70.0, 10, 7);
        WeeklyWordAdditionGoalReportRow weeklyRowForToday = WeeklyWordAdditionGoalReportRow.builder()
                .id(1L)
                .startPeriod(testData.data().currentDate())
                .endPeriod(testData.data().currentDate())
                .workingDays(1)
                .dictionaryReports(Set.of(dictionaryGoalReport))
                .build();

        WordAdditionGoalReportRow weeklyRow =
                weeklyWordAdditionGoalReportRowService.updateRow(weeklyRowForToday, testData.data());

        assertThat(weeklyRow.getWorkingDays()).isEqualTo(1);
        assertThat(weeklyRow.getRowPeriod()).isEqualTo(ReportPeriodUtil.WEEK);
        assertThat(weeklyRow.getStartPeriod()).isEqualTo(testData.data().currentDate());
        assertThat(weeklyRow.getEndPeriod()).isEqualTo(testData.data().currentDate());
        assertThat(weeklyRow.getDictionaryReports()).containsExactly(testData.expectedDictionaryGoalReport());
    }

    @ParameterizedTest
    @MethodSource("buildRowFromScratchTestData")
    void
            testUpdateRow_WhenRowExistsForTodayAndGivenDictionaryReportDoesNotExist_ThenRecalculateRowWithNewDictionaryReport(
                    TestData testData) {
        DictionaryWordAdditionGoalReport dictionaryGoalReport =
                new DictionaryWordAdditionGoalReport(1L, 5L, 70.0, 10, 7);
        WeeklyWordAdditionGoalReportRow weeklyRowForToday = WeeklyWordAdditionGoalReportRow.builder()
                .id(1L)
                .startPeriod(testData.data().currentDate())
                .endPeriod(testData.data().currentDate())
                .workingDays(1)
                .dictionaryReports(Set.of(dictionaryGoalReport))
                .build();

        WordAdditionGoalReportRow weeklyRow =
                weeklyWordAdditionGoalReportRowService.updateRow(weeklyRowForToday, testData.data());

        assertThat(weeklyRow.getWorkingDays()).isEqualTo(1);
        assertThat(weeklyRow.getRowPeriod()).isEqualTo(ReportPeriodUtil.WEEK);
        assertThat(weeklyRow.getStartPeriod()).isEqualTo(testData.data().currentDate());
        assertThat(weeklyRow.getEndPeriod()).isEqualTo(testData.data().currentDate());
        assertThat(weeklyRow.getDictionaryReports())
                .containsExactlyInAnyOrder(dictionaryGoalReport, testData.expectedDictionaryGoalReport());
    }

    @ParameterizedTest
    @MethodSource("updateRowForNextDayInTheSamePeriod")
    void testUpdateRow_WhenRowExistsForPreviousDayAndCurrentDateInTheSamePeriod_ThenRecalculateRowForNewDay(
            TestData testData) {
        DictionaryWordAdditionGoalReport dictionaryGoalReport =
                new DictionaryWordAdditionGoalReport(1L, 10L, 70.0, 10, 7);
        WeeklyWordAdditionGoalReportRow weeklyRowForToday = WeeklyWordAdditionGoalReportRow.builder()
                .id(1L)
                .startPeriod(testData.data().currentDate())
                .endPeriod(testData.data().currentDate())
                .workingDays(1)
                .dictionaryReports(Set.of(dictionaryGoalReport))
                .build();

        WordAdditionGoalReportRow weeklyRow =
                weeklyWordAdditionGoalReportRowService.updateRow(weeklyRowForToday, testData.data());

        assertThat(weeklyRow.getWorkingDays()).isEqualTo(2);
        assertThat(weeklyRow.getRowPeriod()).isEqualTo(ReportPeriodUtil.WEEK);
        assertThat(weeklyRow.getStartPeriod())
                .isEqualTo(testData.data().currentDate().minusDays(1));
        assertThat(weeklyRow.getEndPeriod()).isEqualTo(testData.data().currentDate());
        assertThat(weeklyRow.getDictionaryReports()).containsExactlyInAnyOrder(testData.expectedDictionaryGoalReport());
    }

    @ParameterizedTest
    @MethodSource("updateRowForNextDayInTheSamePeriod")
    void
            testUpdateRow_WhenRowExistsForPreviousDayAndCurrentDateInTheSamePeriodButGivenNewDictionaryId_ThenRecalculateRowForNewDay(
                    TestData testData) {
        DictionaryWordAdditionGoalReport dictionaryGoalReport =
                new DictionaryWordAdditionGoalReport(1L, 5L, 70.0, 10, 7);
        WeeklyWordAdditionGoalReportRow weeklyRowForToday = WeeklyWordAdditionGoalReportRow.builder()
                .id(1L)
                .startPeriod(testData.data().currentDate().minusDays(1))
                .endPeriod(testData.data().currentDate().minusDays(1))
                .workingDays(1)
                .dictionaryReports(Set.of(dictionaryGoalReport))
                .build();

        WordAdditionGoalReportRow weeklyRow =
                weeklyWordAdditionGoalReportRowService.updateRow(weeklyRowForToday, testData.data());

        System.out.println(weeklyRow.getDictionaryReports());
        assertThat(weeklyRow.getWorkingDays()).isEqualTo(2);
        assertThat(weeklyRow.getRowPeriod()).isEqualTo(ReportPeriodUtil.WEEK);
        assertThat(weeklyRow.getStartPeriod())
                .isEqualTo(testData.data().currentDate().minusDays(1));
        assertThat(weeklyRow.getEndPeriod()).isEqualTo(testData.data().currentDate());
        assertThat(weeklyRow.getDictionaryReports())
                .containsExactlyInAnyOrder(dictionaryGoalReport, testData.expectedDictionaryGoalReport());
    }

    private static Stream<TestData> buildRowFromScratchTestData() {
        Long dictionaryId = 10L;
        int newWordsGoal = 10;
        LocalDate userCreatedAt = LocalDate.of(2024, 7, 9);
        LocalDate currentDate = LocalDate.of(2024, 7, 15);
        List<TestData> testDataList = IntStream.range(1, 12)
                .mapToObj(i -> new TestData(
                        new WordAdditionData(USER_ID, dictionaryId, i, newWordsGoal, userCreatedAt, currentDate),
                        new DictionaryWordAdditionGoalReport(
                                null, dictionaryId, (double) i * newWordsGoal, newWordsGoal, i)))
                .toList();

        return testDataList.stream();
    }

    private static Stream<TestData> buildRowFromScratchTestDataWhenUserCreatedAfterStartPeriod() {
        Long dictionaryId = 10L;
        int newWordsGoal = 10;
        LocalDate userCreatedAt = LocalDate.of(2024, 7, 16);
        LocalDate currentDate = LocalDate.of(2024, 7, 16);
        List<TestData> testDataList = IntStream.range(1, 12)
                .mapToObj(i -> new TestData(
                        new WordAdditionData(USER_ID, dictionaryId, i, newWordsGoal, userCreatedAt, currentDate),
                        new DictionaryWordAdditionGoalReport(
                                null, dictionaryId, (double) i * newWordsGoal, newWordsGoal, i)))
                .toList();

        return testDataList.stream();
    }

    private static Stream<TestData> buildRowFromScratchTestDataWhenWasSkippedFewDaysFromStartPeriod() {
        Long dictionaryId = 10L;
        int newWordsGoal = 10;
        LocalDate userCreatedAt = LocalDate.of(2024, 7, 9);
        LocalDate currentDate = LocalDate.of(2024, 7, 17);
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        List<TestData> testDataList = IntStream.range(1, 12)
                .mapToObj(i -> new TestData(
                        new WordAdditionData(USER_ID, dictionaryId, i, newWordsGoal, userCreatedAt, currentDate),
                        new DictionaryWordAdditionGoalReport(
                                null,
                                dictionaryId,
                                Double.parseDouble(decimalFormat.format(((double) i / (newWordsGoal * 3)) * 100)),
                                newWordsGoal * 3,
                                i)))
                .toList();

        return testDataList.stream();
    }

    private static Stream<TestData> updateRowForTodayAndExistingDictionaryReport() {
        Long dictionaryId = 10L;
        int newWordsGoal = 10;
        LocalDate userCreatedAt = LocalDate.of(2024, 7, 9);
        LocalDate currentDate = LocalDate.of(2024, 7, 15);
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        List<TestData> testDataList = IntStream.range(1, 12)
                .mapToObj(i -> new TestData(
                        new WordAdditionData(USER_ID, dictionaryId, i, newWordsGoal, userCreatedAt, currentDate),
                        new DictionaryWordAdditionGoalReport(
                                1L,
                                dictionaryId,
                                Double.parseDouble(decimalFormat.format(((double) (i + 7) / newWordsGoal) * 100)),
                                newWordsGoal,
                                i + 7)))
                .toList();

        return testDataList.stream();
    }

    private static Stream<TestData> updateRowForNextDayInTheSamePeriod() {
        Long dictionaryId = 10L;
        int newWordsGoal = 10;
        LocalDate userCreatedAt = LocalDate.of(2024, 7, 9);
        LocalDate currentDate = LocalDate.of(2024, 7, 16);
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        List<TestData> testDataList = IntStream.range(1, 12)
                .mapToObj(i -> new TestData(
                        new WordAdditionData(USER_ID, dictionaryId, i, newWordsGoal, userCreatedAt, currentDate),
                        new DictionaryWordAdditionGoalReport(
                                1L,
                                dictionaryId,
                                Double.parseDouble(decimalFormat.format(((double) (i + 7) / (newWordsGoal * 2)) * 100)),
                                newWordsGoal * 2,
                                i + 7)))
                .toList();

        return testDataList.stream();
    }

    //    private static Stream<TestData> updateRowForNextDayInTheSamePeriodAndForNewDictionaryId() {
    //        Long dictionaryId = 10L;
    //        int newWordsGoal = 10;
    //        LocalDate userCreatedAt = LocalDate.of(2024, 7, 9);
    //        LocalDate currentDate = LocalDate.of(2024, 7, 16);
    //        DecimalFormat decimalFormat = new DecimalFormat("#.###");
    //        List<TestData> testDataList = IntStream.range(1, 12)
    //                .mapToObj(i -> new TestData(
    //                        new WordAdditionData(USER_ID, dictionaryId, i, newWordsGoal, userCreatedAt, currentDate),
    //                        new DictionaryWordAdditionGoalReport(
    //                                1L, dictionaryId, Double.parseDouble(decimalFormat.format(((double) (i + 7) /
    // (newWordsGoal * 2)) * 100)), newWordsGoal * 2, i + 7)))
    //                .toList();
    //
    //        return testDataList.stream();
    //    }

    private record TestData(WordAdditionData data, DictionaryWordAdditionGoalReport expectedDictionaryGoalReport) {}
}