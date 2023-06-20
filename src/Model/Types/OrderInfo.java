package Model.Types;

import java.util.UUID;

public record OrderInfo(UUID orderID, String orderAsString, boolean notSent, boolean arrived) {}
