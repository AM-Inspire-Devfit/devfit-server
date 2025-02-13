package com.amcamp.global.util;

import com.amcamp.global.exception.CommonException;
import com.amcamp.global.exception.errorcode.DateErrorCode;
import java.time.LocalDateTime;

public class DateCheckUtil {

    public static void checkStartDtAndDueDt(LocalDateTime StartDt, LocalDateTime DueDt) {
        if (StartDt.isAfter(DueDt) || StartDt.isBefore(LocalDateTime.now())) {
            throw new CommonException(DateErrorCode.INVALID_DATE_PERIOD_ERROR);
        }
    }
}
