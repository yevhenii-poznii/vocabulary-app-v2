package com.kiskee.dictionarybuilder.service.report;

import com.kiskee.dictionarybuilder.exception.ResourceNotFoundException;
import com.kiskee.dictionarybuilder.model.dto.report.ReportDto;
import com.kiskee.dictionarybuilder.model.entity.report.Report;
import com.kiskee.dictionarybuilder.model.entity.report.ReportRow;
import com.kiskee.dictionarybuilder.service.time.CurrentDateTimeService;
import com.kiskee.dictionarybuilder.util.IdentityUtil;
import com.kiskee.dictionarybuilder.util.TimeZoneContextHolder;
import com.kiskee.dictionarybuilder.util.report.ReportPeriodUtil;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractReportService<R extends Report<RR>, RR extends ReportRow> {

    protected abstract CurrentDateTimeService getCurrentDateTimeService();

    protected abstract Optional<R> getReport(UUID userId);

    protected abstract ReportDto mapToDto(UUID userId, Set<RR> reportRows);

    protected ReportDto getReport() {
        UUID userId = IdentityUtil.getUserId();
        R report = getReport(userId).orElseThrow(() -> new ResourceNotFoundException("There is no report yet"));

        Set<RR> reportRows =
                report.getReportRows().stream().filter(this::rowInCurrentPeriod).collect(Collectors.toSet());

        return mapToDto(userId, reportRows);
    }

    private boolean rowInCurrentPeriod(RR row) {
        if (row.getRowPeriod().equals(ReportPeriodUtil.TOTAL)) {
            return true;
        }
        LocalDate currentDate = getCurrentDateTimeService().getCurrentDate(TimeZoneContextHolder.getTimeZone());
        return !currentDate.isAfter(ReportPeriodUtil.getLastDayOfPeriod(row.getEndPeriod(), row.getRowPeriod()));
    }
}
