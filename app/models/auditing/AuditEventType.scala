package models.auditing

import models.WithName

sealed trait AuditEventType

object AuditEventType {
  object UpdateFlatRateExpenseSuccess extends WithName("updateFlatRateExpenseSuccess") with AuditEventType
  object UpdateFlatRateExpenseFailure extends WithName("updateFlatRateExpenseFailure") with AuditEventType
}
