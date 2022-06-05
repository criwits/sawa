package top.criwits.sawa.multi;

public class RoomEntry {
    private int roomID;
    private int difficulty;

    public int getRoomID() {
        return roomID;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public RoomEntry(int roomID, int difficulty) {
        this.difficulty = difficulty;
        this.roomID = roomID;
    }

    public static String diffToString(int difficulty) {
        switch (difficulty) {
            case 0:
                return "Easy";
            case 1:
                return "Moderate";
            case 2:
                return "Hard";
            default:
                return "Unknown";
        }
    }
}
