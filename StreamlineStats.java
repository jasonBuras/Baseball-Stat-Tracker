package Baseball

public class StreamlineStats {

    public static void main(String[] args) {
        String programName = "Streamline Stats";
        int major = 1;
        int minor = 0;
        int patch = 0;

        //v[MAJOR.MINOR.PATCH]
        System.out.println(programName + " v" + major + "." + minor + "." + patch);
        UI ui = new UI();
        ui.mainMenu();
    }

}