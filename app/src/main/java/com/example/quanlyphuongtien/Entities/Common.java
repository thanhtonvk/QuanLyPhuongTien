package com.example.quanlyphuongtien.Entities;

public class Common {
    public static Teacher teacher;
    public static Protector protector;
    public static Student student;
    public static String contentQR;
    public static String idStudent;
    public static Location lStudent;

    //    21.04236977747165, 106.76520954232869
    public static int checkLocation() {
        double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
        //20.933357755309384, 106.00828157523416
        Location lSchool = new Location(21.04236977747165, 106.76520954232869);

        double latDistance = Math.toRadians(lStudent.getLat() - lSchool.getLat());
        double lngDistance = Math.toRadians(lStudent.getLng() - lSchool.getLng());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lStudent.getLat())) * Math.cos(Math.toRadians(lSchool.getLat()))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (int)(Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c))*1000;

    }
}
