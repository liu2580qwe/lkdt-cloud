package org.lkdt.modules.radar.supports.radarDataService.dataCache.DO;

@Deprecated
public enum Lane {

    ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, ELEVEN, TWELVE,
    THIRTEEN, FOURTEEN, FIFTEEN, SIXTEEN, SEVENTEEN;

    public static String getKey(Lane lane){
        switch (lane){
            case ONE:
                return "1";
            case TWO:
                return "2";
            case THREE:
                return "3";
            case FOUR:
                return "4";
            case FIVE:
                return "5";
            case SIX:
                return "6";
            case SEVEN:
                return "7";
            case EIGHT:
                return "8";
            case NINE:
                return "9";
            case TEN:
                return "10";
            case ELEVEN:
                return "11";
            case TWELVE:
                return "12";
            case THIRTEEN:
                return "13";
            case FOURTEEN:
                return "14";
            case FIFTEEN:
                return "15";
            case SIXTEEN:
                return "16";
            case SEVENTEEN:
                return "17";
            default:
                return null;
        }
    }
}
