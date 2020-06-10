package it.polimi.ingsw.psp40.controller.strategies.strategyMove;

import it.polimi.ingsw.psp40.exceptions.CellOutOfBoundsException;
import it.polimi.ingsw.psp40.exceptions.WorkerAlreadyPresentException;
import it.polimi.ingsw.psp40.exceptions.WrongCellSelectedMoveException;
import it.polimi.ingsw.psp40.exceptions.ZeroCellsAvailableMoveException;
import it.polimi.ingsw.psp40.model.Cell;
import it.polimi.ingsw.psp40.model.Match;
import it.polimi.ingsw.psp40.model.Worker;

import java.util.List;

/**
 * This strategy check f one of your Workers moved up on your last turn, opponent Workers cannot move up this turn.
 * @author sup3rgiu
 */
public class OthersCantLevelUp extends DefaultMove {

    /**
     * Constructor
     * @param match
     */
    public OthersCantLevelUp(Match match) {
        super(match);
    }

    /**
     * This methods activates when a worker has moved up, setting the parameter which prevents the enemy's Workers to level up
     * @param worker it's the {@link Worker} to move
     * @param cell it's the new {@link Cell} where is moved
     * @throws ZeroCellsAvailableMoveException
     * @throws WrongCellSelectedMoveException
     * @throws WorkerAlreadyPresentException
     * @throws CellOutOfBoundsException
     */
    @Override
    public void move(Worker worker, Cell cell) throws ZeroCellsAvailableMoveException, WrongCellSelectedMoveException, WorkerAlreadyPresentException, CellOutOfBoundsException {
        try {
            Cell origWorkerCell = match.getLocation().getLocation(worker);
            super.move(worker, cell);
            if(cell.getTower().getTopComponent().getComponentCode() > origWorkerCell.getTower().getTopComponent().getComponentCode()) { // I'm going up
                match.getMatchProperties().setOthersCantLevelUp(true);
            }
        } catch (ZeroCellsAvailableMoveException e) {
            throw new ZeroCellsAvailableMoveException();
        } catch (WrongCellSelectedMoveException e) {
            throw new WrongCellSelectedMoveException();
        } catch (WorkerAlreadyPresentException e) {
            throw new WorkerAlreadyPresentException();
        } catch (CellOutOfBoundsException e) {
            throw new CellOutOfBoundsException();
        }
    }

    /**
     * This method is like the default {@link DefaultMove} but disable the flag "Others can't level up"
     * @param worker it's the {@link Worker} you want to know about
     * @return the list of available {@link Cell} for move
     */
    @Override
    public List<Cell> getAvailableCells(Worker worker) {
        match.getMatchProperties().setOthersCantLevelUp(false);
        return super.getAvailableCells(worker);
    }
}
