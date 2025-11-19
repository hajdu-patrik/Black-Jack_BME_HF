package blackjack.io;

import blackjack.logic.BlackjackGame;
import java.io.*;

public class SaveManager {

    // Relative path to the project root
    private static final String SAVE_FILE = "saves/gamestate.dat";

    public static void saveGame(BlackjackGame game) throws IOException {
        // Ensure the parent directory exists
        File file = new File(SAVE_FILE);
        file.getParentFile().mkdirs();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(game);
        }
    }

    public static BlackjackGame loadGame() throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            return (BlackjackGame) ois.readObject();
        }
    }
}