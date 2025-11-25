package blackjack.io;

import blackjack.logic.BlackjackGame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the functionality of the SaveManager class, ensuring persistence works correctly.
 */
class SaveManagerTest {
    private static final String TEST_FILE_PATH = "saves/gamestate.dat";
    private BlackjackGame originalGame;

    /**
     * Initializes a game instance and ensures a clean state before each test.
     */
    @BeforeEach
    void setUp() {
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }

        originalGame = new BlackjackGame("SaveTestPlayer", 2); 
        originalGame.playerHit(); 
    }

    /**
     * Cleans up the test environment by deleting the created save file.
     */
    @AfterEach
    void tearDown() {
        System.gc(); // Help release file locks on Windows
        File file = new File(TEST_FILE_PATH);
        if (file.exists())
            file.delete();
    }

    /**
     * Verifies that the game state is successfully saved to a file.
     */
    @Test
    void testSaveGameSuccess() {
        assertDoesNotThrow(() -> SaveManager.saveGame(originalGame), "Saving should not throw an exception.");
        
        File file = new File(TEST_FILE_PATH);
        assertTrue(file.exists(), "The saved file must exist after calling saveGame.");
        assertTrue(file.length() > 0, "The saved file must not be empty.");
    }

    /**
     * Verifies that a saved game can be loaded and retains its state integrity.
     */
    @Test
    void testLoadGameSuccessAndStateIntegrity() {
        try {
            SaveManager.saveGame(originalGame);
            
            BlackjackGame loadedGame = SaveManager.loadGame();
            assertNotNull(loadedGame, "The loaded game object should not be null.");
            
            assertEquals(originalGame.getPlayer().getName(), loadedGame.getPlayer().getName());
            assertEquals(originalGame.getNumberOfDecks(), loadedGame.getNumberOfDecks());
            
        } catch (Exception e) {
            fail("Exception during save/load test: " + e.getMessage());
        }
    }
}