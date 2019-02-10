/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wholesalemarket_LMP;

/**
 *
 * @author admin
 */
public class DataInput_Producer_Supplier {

    public enum Type {

        MIN_CAP_TITLE, MAX_CAP_TITLE, START_PRICE_TITLE, SLOPE_PRICE_TITLE
    }

    public static String[] createColumnTitlesMinCap(int _startHour, int _endHour, Type _titleType) {
        int initPos = _startHour + 2;
        int hourSize = _endHour - _startHour + 1;
        String[] auxData;

        switch (_titleType) {
            case MIN_CAP_TITLE:
                auxData = ColumnTitlesMin;
                break;
            case MAX_CAP_TITLE:
                auxData = ColumnTitlesMax;
                break;
            case START_PRICE_TITLE:
                auxData = ColumnTitlesStart;
                break;
            case SLOPE_PRICE_TITLE:
                auxData = ColumnTitlesSlope;
                break;
            default:
                auxData = ColumnTitlesMin;
                break;
        }

        String[] _columnTitle = new String[hourSize + 2];
        _columnTitle[0] = auxData[0];
        _columnTitle[1] = auxData[1];
        for (int h = 2; h < hourSize + 2; h++) {
            _columnTitle[h] = auxData[initPos++];
        }
        return _columnTitle;
    }

    public static String[] ColumnTitlesMin = {
        "Agent Min MW", "Start BUS", "00h-01h", "01h-02h", "02h-03h", "03h-04h", "04h-05h",
        "05h-06h", "06h-07h", "07h-08h", "08h-09h", "09h-10h", "10h-11h", "11h-12h",
        "12h-13h", "13h-14h", "14h-15h", "15h-16h", "16h-17h", "17h-18h", "18h-19h", "19h-20h",
        "20h-21h", "21h-22h", "22h-23h", "23h-24h"
    };

    public static String[] ColumnTitlesMax = {
        "Agent Max Mw", "Start BUS", "00h-01h", "01h-02h", "02h-03h", "03h-04h", "04h-05h",
        "05h-06h", "06h-07h", "07h-08h", "08h-09h", "09h-10h", "10h-11h", "11h-12h",
        "12h-13h", "13h-14h", "14h-15h", "15h-16h", "16h-17h", "17h-18h", "18h-19h", "19h-20h",
        "20h-21h", "21h-22h", "22h-23h", "23h-24h"
    };

    public static String[] ColumnTitlesStart = {
        "Agent Start €/MWh", "Start BUS", "00h-01h", "01h-02h", "02h-03h", "03h-04h", "04h-05h",
        "05h-06h", "06h-07h", "07h-08h", "08h-09h", "09h-10h", "10h-11h", "11h-12h",
        "12h-13h", "13h-14h", "14h-15h", "15h-16h", "16h-17h", "17h-18h", "18h-19h", "19h-20h",
        "20h-21h", "21h-22h", "22h-23h", "23h-24h"
    };

    public static String[] ColumnTitlesSlope = {
        "Agent Slope €/MWh", "Start BUS", "00h-01h", "01h-02h", "02h-03h", "03h-04h", "04h-05h",
        "05h-06h", "06h-07h", "07h-08h", "08h-09h", "09h-10h", "10h-11h", "11h-12h",
        "12h-13h", "13h-14h", "14h-15h", "15h-16h", "16h-17h", "17h-18h", "18h-19h", "19h-20h",
        "20h-21h", "21h-22h", "22h-23h", "23h-24h"
    };

    String minPot_defaultValues = "10";

    String maxPot_defaultValues = "100";

    String Start_defaultValues = "10";

    String Slope_defaultValues = "0.5";

}
