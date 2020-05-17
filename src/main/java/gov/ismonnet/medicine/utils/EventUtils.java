package gov.ismonnet.medicine.utils;

import gov.ismonnet.medicine.jaxb.ws.Cadenza;
import gov.ismonnet.medicine.jaxb.ws.ImmutableEventBase;
import gov.ismonnet.medicine.jaxb.ws.Settimana;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EventUtils {

    public static final int LUNEDI_BITMASK = 0x1;
    public static final int MARTEDI_BITMASK = 0x2;
    public static final int MERCOLEDI_BITMASK = 0x4;
    public static final int GIOVEDI_BITMASK = 0x8;
    public static final int VENERDI_BITMASK = 0x10;
    public static final int SABATO_BITMASK = 0x20;
    public static final int DOMENICA_BITMASK = 0x40;

    private EventUtils() {}

    public static int weekToBitmask(Settimana week) {
        return daysToBitmask(weekToArray(week));
    }

    public static int daysToBitmask(boolean[] days) {
        return IntStream.range(0, days.length)
                .filter(i -> days[i])
                .reduce((bitmask, i) -> bitmask | (int) (Math.pow(2, i)))
                .orElse(0);
    }

    public static Settimana bitmaskToWeek(int bitmask) {
        return new Settimana()
                .withLunedi((bitmask & LUNEDI_BITMASK) != 0)
                .withMartedi((bitmask & MARTEDI_BITMASK) != 0)
                .withMercoledi((bitmask & MERCOLEDI_BITMASK) != 0)
                .withGiovedi((bitmask & GIOVEDI_BITMASK) != 0)
                .withVenerdi((bitmask & VENERDI_BITMASK) != 0)
                .withSabato((bitmask & SABATO_BITMASK) != 0)
                .withDomenica((bitmask & DOMENICA_BITMASK) != 0);
    }

    public static boolean[] weekToArray(Settimana week) {
        return new boolean[] {
                week.isLunedi(),
                week.isMartedi(),
                week.isMercoledi(),
                week.isGiovedi(),
                week.isVenerdi(),
                week.isSabato(),
                week.isDomenica()
        };
    }

    public static Set<LocalDate> getAllDates(final ImmutableEventBase event) {
        if(event.getCadenza() == null)
            return Collections.singleton(event.getData());
        return getAllDates(event, event.getData(), LocalDate.now());
    }

    public static Set<LocalDate> getAllDates(final ImmutableEventBase event,
                                             final LocalDate startDate,
                                             final LocalDate endDate) {
        if(startDate == null && endDate == null)
            return getAllDates(event);

        Objects.requireNonNull(startDate);
        Objects.requireNonNull(endDate);

        if(startDate.isAfter(endDate))
            return Collections.emptySet();

        if(event.getCadenza() == null) {
            if(event.getData().isBefore(startDate) || event.getData().isAfter(endDate))
                return Collections.emptySet();
            return Collections.singleton(event.getData());
        }

        final Cadenza cadence = event.getCadenza();
        final LocalDate clampedStartDate = clampEventStartDate(event, startDate);
        final LocalDate clampedEndDate = clampCadenceEndDate(cadence, endDate);

        final boolean usesOccurrences = cadence.getFine() != null && cadence.getFine().getOccorenze() != null;

        final LocalDate from;
        final BiFunction<LocalDate, Set<LocalDate>, Boolean> until;
        if(!usesOccurrences) {
            from = clampedStartDate;
            until = (currDate, res) -> currDate.isAfter(clampedEndDate);
        } else {
            final int occurrences = cadence.getFine().getOccorenze().intValue();
            from = event.getData();
            until = (currDate, res) -> res.size() >= occurrences;
        }

        final Set<LocalDate> res;
        if(cadence.getGiornaliera() != null)
            res = getAllDatesDailyCadence(from, until, cadence);
        else if(cadence.getSettimanale() != null)
            res = getAllDatesWeeklyCadence(from, until, cadence);
        else
            throw new AssertionError("Unimplemented cadence " + cadence);

        if(!usesOccurrences)
            return res;
        return res.stream()
                .filter(d -> !d.isBefore(clampedStartDate))
                .filter(d -> !d.isAfter(clampedEndDate))
                .collect(Collectors.toSet());
    }

    public static LocalDate clampEventStartDate(final ImmutableEventBase event, final LocalDate startDate) {

        if(event.getData().isAfter(startDate) || event.getData().isEqual(startDate)) {
            return event.getData();
        } else {
            // Days difference between the event date and the startDate
            final long daysDiff = event.getData().until(startDate, ChronoUnit.DAYS);
            final long interval = event.getCadenza().getIntervallo().longValue();
            return startDate.plus(daysDiff % interval, ChronoUnit.DAYS);
        }
    }

    public static LocalDate clampCadenceEndDate(final Cadenza cadence, final LocalDate endDate) {
        if(cadence.getFine() == null)
            return endDate;
        if(cadence.getFine().getOccorenze() != null)
            return endDate;
        if(cadence.getFine().getData() != null) {
            if(cadence.getFine().getData().isBefore(endDate))
                return cadence.getFine().getData();
            return endDate;
        }
        throw new AssertionError("Unexpected FineCadenza");
    }

    private static Set<LocalDate> getAllDatesDailyCadence(final LocalDate startDate,
                                                          final BiFunction<LocalDate, Set<LocalDate>, Boolean> until,
                                                          final Cadenza cadenza) {
        final Set<LocalDate> res = new HashSet<>();
        final long interval = cadenza.getIntervallo().longValue();

        LocalDate eventDate = startDate;
        while(!until.apply(eventDate, res)) {
            res.add(eventDate);
            eventDate = eventDate.plusDays(interval);
        }
        return res;
    }

    private static Set<LocalDate> getAllDatesWeeklyCadence(final LocalDate startDate,
                                                           final BiFunction<LocalDate, Set<LocalDate>, Boolean> until,
                                                           final Cadenza cadenza) {
        final Set<LocalDate> res = new HashSet<>();
        final boolean[] week = weekToArray(cadenza.getSettimanale());
        final long interval = cadenza.getIntervallo().longValue();

        // Get the start of the given week
        LocalDate weekDate = startDate.with(TemporalAdjusters.previousOrSame(WeekFields.ISO.getFirstDayOfWeek()));
        // Each week until we go over endDate
        outer: while(true) {

            LocalDate eventDate = weekDate;
            // For each day of the week
            for(int i = 0; i < 7; i++, eventDate = eventDate.plusDays(1)) {
                // As we are starting not from startDate, but from the first day of that week,
                // ensure the date is in the range
                if(eventDate.isBefore(startDate))
                    continue;
                // If we go over the endDate, we are done
                if(until.apply(eventDate, res))
                    break outer;
                // If it should happen on this day of week, add it
                if(week[i])
                    res.add(eventDate);
            }

            // Add n weeks, restarts from the first day of week for the next cycle
            weekDate = weekDate.plusWeeks(interval);
        }

        return res;
    }
}
