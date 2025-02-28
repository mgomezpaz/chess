package result;

import model.GameData;
import java.util.Collection;

/**
 * Result object for listing games
 */
public record ListGamesResult(Collection<GameData> games) {
} 