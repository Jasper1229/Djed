package jjs.djed.web.patch;

import java.time.OffsetDateTime;

public record UpdateSessionTimesRequest (OffsetDateTime startTime, OffsetDateTime endTime) {
}
