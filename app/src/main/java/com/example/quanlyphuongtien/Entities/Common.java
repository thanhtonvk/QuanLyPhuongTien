package com.example.quanlyphuongtien.Entities;

public class Common {
    public static Teacher teacher;
    public static Protector protector;
    public static Student student;
    public static String contentQR;
    public static String idStudent;
    public static Location lStudent;
    public static boolean flagTeacher = false;
    public static boolean flagProtector = false;

    public static double checkLocation() {
        double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
        Location lSchool = new Location(21.04183657922609, 106.76522564722603);
        double latDistance = Math.toRadians(lStudent.getLat() - lSchool.getLat());
        double lngDistance = Math.toRadians(lStudent.getLng() - lSchool.getLng());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lStudent.getLat())) * Math.cos(Math.toRadians(lSchool.getLat()))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 1;
    }
}
