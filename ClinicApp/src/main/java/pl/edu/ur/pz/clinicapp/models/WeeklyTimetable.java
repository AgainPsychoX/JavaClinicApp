package pl.edu.ur.pz.clinicapp.models;

import javax.persistence.*;

@Embeddable
public class WeeklyTimetable {
    @Column(name = "MondayStart", nullable = true)
    private Integer mondayStart;
    public int getMondayStart() {
        return mondayStart;
    }
    public void setMondayStart(int minuteOfDay) {
        this.mondayStart = minuteOfDay;
    }

    @Column(name = "MondayEnd", nullable = true)
    private Integer mondayEnd;
    public int getMondayEnd() {
        return mondayEnd;
    }
    public void setMondayEnd(int minuteOfDay) {
        this.mondayEnd = minuteOfDay;
    }

    @Column(name = "TuesdayStart", nullable = true)
    private Integer tuesdayStart;
    public int getTuesdayStart() {
        return tuesdayStart;
    }
    public void setTuesdayStart(int minuteOfDay) {
        this.tuesdayStart = minuteOfDay;
    }

    @Column(name = "TuesdayEnd", nullable = true)
    private Integer tuesdayEnd;
    public int getTuesdayEnd() {
        return tuesdayEnd;
    }
    public void setTuesdayEnd(int minuteOfDay) {
        this.tuesdayEnd = minuteOfDay;
    }

    @Column(name = "WednesdayStart", nullable = true)
    private Integer wednesdayStart;
    public int getWednesdayStart() {
        return wednesdayStart;
    }
    public void setWednesdayStart(int minuteOfDay) {
        this.wednesdayStart = minuteOfDay;
    }

    @Column(name = "WednesdayEnd", nullable = true)
    private Integer wednesdayEnd;
    public int getWednesdayEnd() {
        return wednesdayEnd;
    }
    public void setWednesdayEnd(int minuteOfDay) {
        this.wednesdayEnd = minuteOfDay;
    }

    @Column(name = "ThursdayStart", nullable = true)
    private Integer thursdayStart;
    public int getThursdayStart() {
        return thursdayStart;
    }
    public void setThursdayStart(int minuteOfDay) {
        this.thursdayStart = minuteOfDay;
    }

    @Column(name = "ThursdayEnd", nullable = true)
    private Integer thursdayEnd;
    public int getThursdayEnd() {
        return thursdayEnd;
    }
    public void setThursdayEnd(int minuteOfDay) {
        this.thursdayEnd = minuteOfDay;
    }

    @Column(name = "FridayStart", nullable = true)
    private Integer fridayStart;
    public int getFridayStart() {
        return fridayStart;
    }
    public void setFridayStart(int minuteOfDay) {
        this.fridayStart = minuteOfDay;
    }

    @Column(name = "FridayEnd", nullable = true)
    private Integer fridayEnd;
    public int getFridayEnd() {
        return fridayEnd;
    }
    public void setFridayEnd(int minuteOfDay) {
        this.fridayEnd = minuteOfDay;
    }

    @Column(name = "SaturdayStart", nullable = true)
    private Integer saturdayStart;
    public int getSaturdayStart() {
        return saturdayStart;
    }
    public void setSaturdayStart(int minuteOfDay) {
        this.saturdayStart = minuteOfDay;
    }

    @Column(name = "SaturdayEnd", nullable = true)
    private Integer saturdayEnd;
    public int getSaturdayEnd() {
        return saturdayEnd;
    }
    public void setSaturdayEnd(int minuteOfDay) {
        this.saturdayEnd = minuteOfDay;
    }

    @Column(name = "SundayStart", nullable = true)
    private Integer sundayStart;
    public int getSundayStart() {
        return sundayStart;
    }
    public void setSundayStart(int minuteOfDay) {
        this.sundayStart = minuteOfDay;
    }

    @Column(name = "SundayEnd", nullable = true)
    private Integer sundayEnd;
    public int getSundayEnd() {
        return sundayEnd;
    }
    public void setSundayEnd(int minuteOfDay) {
        this.sundayEnd = minuteOfDay;
    }

    // TODO: isAvailable(dateTime/now)
}
