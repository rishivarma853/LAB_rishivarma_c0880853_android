package ca.lcit22f.rishivarma853.lab_rishivarma_c0880853_android;

public class IntentModel {

    public static class ListToMap {
        public static boolean addNewPlace = false;
        public static boolean updatePlace = false;
        public static class Place {
            public static int id;
            public static String address;
            public static String latitude;
            public static String longitude;
        }
        public static void clear() {
            addNewPlace = false;
            updatePlace = false;
            Place.address = "";
            Place.latitude = "";
            Place.longitude = "";
        }
    }

    public static class MapToList {
        public static boolean addedNewPlace = false;
        public static boolean updatedPlace = false;
        public static class Place {
            public static int id;

            public static String address;
            public static String latitude;
            public static String longitude;
        }
        public static void clear() {
            addedNewPlace = false;
            updatedPlace = false;
            Place.address = "";
            Place.latitude = "";
            Place.longitude = "";
        }
    }

}
