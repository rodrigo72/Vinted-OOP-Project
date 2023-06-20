package Model;

import java.io.Serializable;
import java.time.*;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class AppClock extends Clock implements Serializable {

    private final AtomicReference<Instant> instantHolder;
    private final ZoneId zone;

    public static AppClock epochUTC() {
        return AppClock.of(Instant.EPOCH, ZoneOffset.UTC);
    }

    public static AppClock of(Instant instant, ZoneId zone) {
        Objects.requireNonNull(instant, "instant is null");
        Objects.requireNonNull(zone, "zone is null");
        return new AppClock(new AtomicReference<>(instant), zone);
    }

    private AppClock(AtomicReference<Instant> instantHolder, ZoneId zone) {
        this.instantHolder = instantHolder;
        this.zone = zone;
    }

    public void setInstant(Instant instant) {
        Objects.requireNonNull(instant, "instant is null");
        instantHolder.set(instant);
    }

    public void add(TemporalAmount amountToAdd) {
        Objects.requireNonNull(amountToAdd, "amountToAdd is null");
        instantHolder.updateAndGet(current -> {
            ZonedDateTime currentZoned = current.atZone(zone);
            ZonedDateTime result = currentZoned.plus(amountToAdd);
            return result.toInstant();
        });
    }

    public void set (TemporalAdjuster adjuster) {
        Objects.requireNonNull(adjuster, "adjuster is null");
        instantHolder.updateAndGet(current -> {
            ZonedDateTime currentZoned = current.atZone(zone);
            ZonedDateTime result = currentZoned.with(adjuster);
            return result.toInstant();
        });
    }

    public void setDate (Date date) {
        Objects.requireNonNull(date, "date is null");
        instantHolder.set(date.toInstant());
    }

    public int getYear() {
        return this.instantHolder.get().atZone(zone).getYear();
    }

    public LocalDateTime getLocalDateTime() {
        return instantHolder.get().atZone(zone).toLocalDateTime();
    }

    public LocalDate getLocalDate() {
        return instantHolder.get().atZone(zone).toLocalDate();
    }

    public boolean isDateAfter (LocalDate date) {
        LocalDate clockDate = this.getLocalDate();
        return date.isAfter(clockDate);
    }

    public boolean isDateEqual (LocalDate date) {
        LocalDate clockDate = this.getLocalDate();
        return date.isEqual(clockDate);
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public AppClock withZone(ZoneId zone) {
        Objects.requireNonNull(zone, "zone is null");
        if (zone.equals(this.zone)) {
            return this;
        }
        return new AppClock(new AtomicReference<>(instantHolder.get()), zone);
    }

    @Override
    public Instant instant() {
        return instantHolder.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppClock that = (AppClock) o;
        return instantHolder.equals(that.instantHolder) && zone.equals(that.zone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instantHolder, zone);
    }

    @Override
    public String toString() {
        return "AppClock {" +
                "instantHolder=" + instantHolder +
                ", zone=" + zone +
                '}';
    }
}

