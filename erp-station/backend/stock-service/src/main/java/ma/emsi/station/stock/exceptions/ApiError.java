package ma.emsi.station.stock.exceptions;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(LocalDateTime timestamp, int status, String error, String message, List<String> details) {
}
