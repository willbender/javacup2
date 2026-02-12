package com.javacup.model.engine;

import com.javacup.model.Tactic;
import com.javacup.model.TacticDetail;
import com.javacup.model.command.Command;
import com.javacup.model.util.Position;
import lombok.Getter;

import java.util.List;

/**
 * Internal wrapper for Tactic implementations.
 * <p>
 * This class wraps user-provided tactics and creates an immutable
 * copy of the tactic detail for internal use by the match engine.
 * This prevents tactics from modifying their configuration during a match.
 * </p>
 * 
 * @author JavaCup Team
 * @since 2.0.0
 */
@Getter
final class TacticImpl implements Tactic {

    /**
     * The wrapped user tactic.
     */
    private final Tactic tactic;
    
    /**
     * Immutable copy of the tactic detail.
     */
    private final TacticDetailImpl detail;

    /**
     * Creates a tactic wrapper.
     *
     * @param tactic user tactic to wrap
     */
    public TacticImpl(Tactic tactic) {
        this.tactic = tactic;
        this.detail = new TacticDetailImpl(tactic.getDetail());
    }

    @Override
    public TacticDetail getDetail() {
        return detail;
    }

    @Override
    public List<Command> execute(GameSituations gameSituations) {
        return tactic.execute(gameSituations);
    }

    @Override
    public Position[] getStartPositions(GameSituations gameSituations) {
        return tactic.getStartPositions(gameSituations);
    }

    @Override
    public Position[] getNoStartPositions(GameSituations gameSituations) {
        return tactic.getNoStartPositions(gameSituations);
    }
}
