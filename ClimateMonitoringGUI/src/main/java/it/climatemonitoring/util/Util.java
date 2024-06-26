package it.climatemonitoring.util;

import java.time.LocalDate;

public class Util {

    /**
     * Metodo utilizzato per controllare se il parametro inputDate e compreso in un intervallo di tempo specificato
     * @param startDate
     * @param endDate
     * @param inputDate
     */
    public static boolean isBetweenDates(LocalDate startDate, LocalDate endDate, LocalDate inputDate){
        return inputDate.isAfter(startDate) && inputDate.isBefore(endDate);
    }

    private static Float toRad(Float value){
        return (float) (value * Math.PI / 180);
    }

    /**
     * Metodo utilizzato per calcolare la distanza tra due punti
     * @param latFirstPoint
     * @param longFirstPoint
     * @param latSecondPoint
     * @param longSecondPoint
     * @return
     */
    public static Float haversineDistance(Float latFirstPoint, Float longFirstPoint, Float latSecondPoint, Float longSecondPoint){
        final int earthRadius = 6731; // in kms
        float latDistance = toRad(latSecondPoint - latFirstPoint);
        float longDistance = toRad(longSecondPoint - longFirstPoint);

        float a = (float) (Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(toRad(latFirstPoint)) * Math.cos(toRad(latSecondPoint)) *
                        Math.sin(toRad(longDistance / 2))  * Math.sin(longDistance / 2));
        float c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));

        return earthRadius * c;
    }
}
